package com.baoyubo.iec104.handler;


import com.baoyubo.iec104.constant.Constants;
import com.baoyubo.iec104.enums.FrameTypeEnum;
import com.baoyubo.iec104.factory.MessageFactory;
import com.baoyubo.iec104.manager.CacheManager;
import com.baoyubo.iec104.manager.ControlManager;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.model.MessageControl;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用数据处理 (读、写)
 *
 * @author yubo.bao
 * @date 2023/7/3 17:57
 */
public class CommonDataHandler extends ChannelDuplexHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonDataHandler.class);

    /**
     * 名称
     */
    private final String name;


    /**
     * 构造函数
     *
     * @param name 名称
     */
    public CommonDataHandler(String name) {
        this.name = name;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // channel 设置属性变量
        ctx.channel().attr(Constants.CONTROL_MANAGER).set(new ControlManager(this.name));
        ctx.channel().attr(Constants.CACHE_MANAGER).set(new CacheManager());


        LOGGER.info("[{}-通用数据处理-建立连接]", this.name);
        ctx.fireChannelActive();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[{}-通用数据处理-关闭连接]", this.name);
        ctx.fireChannelInactive();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                LOGGER.info("[{}-通用数据处理-超时] 自动发送测试帧", this.name);
                ctx.writeAndFlush(MessageFactory.buildTestMessage()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }


    /**
     * 处理输出数据（写出的数据）
     * <p>
     * I帧: 处理消息, 自动填充控制域
     * S帧: 不做处理
     * U帧: 不做处理
     *
     * @param ctx     ctx
     * @param msg     msg
     * @param promise promise
     * @throws Exception Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        Message message = (Message) msg;

        // 只处理I帧消息
        if (FrameTypeEnum.I_FRAME == message.getFrameType()) {

            // 协议控制管理
            ControlManager controlMgr = ctx.channel().attr(Constants.CONTROL_MANAGER).get();
            // 消息缓存管理
            CacheManager cacheMgr = ctx.channel().attr(Constants.CACHE_MANAGER).get();

            // 更新控制域序列号
            MessageControl iControl = MessageFactory.commonBuildIControl(controlMgr);
            message.setControl(iControl);
            LOGGER.debug("[{}-通用数据处理-I帧数据输出] 发送I帧: 发送序列号 = {}, 接受序列号 = {}", this.name, iControl.getSendSequenceNum(), iControl.getReceiveSequenceNum());

            // 缓存消息
            cacheMgr.add(message);
        }

        // 将消息继续传递给下一个 Handler 继续处理
        ctx.write(message, promise);
    }


    /**
     * 处理输入数据（读到的数据）
     * <p>
     * I帧: 校验消息，如果校验成功则传递给后续Handler继续处理，否则直接返回S帧
     * S帧: 处理消息，不会传递给后续Handler继续处理
     * U帧: 不做处理，直接传递给后续Handler继续处理
     *
     * @param ctx ctx
     * @param msg msg
     * @throws Exception Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        FrameTypeEnum frameTypeEnum = message.getFrameType();
        switch (frameTypeEnum) {
            case I_FRAME:
                readHandleIFrameMessage(ctx, message);
                return;
            case S_FRAME:
                readHandleSFrameMessage(ctx, message);
                return;
            case U_FRAME:
                readHandleUFrameMessage(ctx, message);
                return;
            default:
        }
    }


    /**
     * [读处理] 处理I帧消息 (校验消息，如果校验成功则传递给后续Handler继续处理，否则直接返回S帧)
     */
    private void readHandleIFrameMessage(ChannelHandlerContext ctx, Message message) {

        // 协议控制管理
        ControlManager controlMgr = ctx.channel().attr(Constants.CONTROL_MANAGER).get();
        // 消息缓存管理
        CacheManager cacheMgr = ctx.channel().attr(Constants.CACHE_MANAGER).get();

        // 消息的发送序列号
        short messageSendSequenceNum = message.getControl().getSendSequenceNum();
        // 当前的接收序列号
        short currReceiveSequenceNum = controlMgr.getReceiveSequenceNum();

        // 校验消息顺序成功：表示这个收到的消息是符合预期的, 表示没有丢失报文
        boolean checkSuccess = currReceiveSequenceNum == messageSendSequenceNum;
        if (checkSuccess) {

            // 此时接收序列号自增
            controlMgr.incrementReceiveSequenceNum();

            // 清理缓存中 小于 当前最新序列号的消息
            cacheMgr.removeLess(controlMgr.getReceiveSequenceNum());

        }
        // 校验消息顺序失败：表示这个收到的消息是不符合预期的，可能为丢失报文或者重发场景，丢弃此消息
        else {
            LOGGER.error("[{}-通用数据处理-I帧数据接收] 校验序列号失败, 丢弃掉此消息. 当前的接收序列号 = {}, 收到消息中的发送序列号 = {}", this.name, currReceiveSequenceNum, messageSendSequenceNum);
        }

        // 通用处理：收到一个I帧，系统自动回复一个S帧
        Message sFrameMessage = MessageFactory.commonBuildSFrameMessage(controlMgr);
        ctx.writeAndFlush(sFrameMessage);
        LOGGER.debug("[{}-通用数据处理-I帧数据接收] 系统自动回复S帧 = {}", this.name, sFrameMessage.getControl().getReceiveSequenceNum());

        // 如果校验成功，将消息继续传递给下一个 Handler 继续处理
        if (checkSuccess) {
            ctx.fireChannelRead(message);
        }

    }


    /**
     * [读处理] 处理S帧消息 (不会继续传递给下一个 Handler 继续处理)
     */
    private void readHandleSFrameMessage(ChannelHandlerContext ctx, Message message) {

        // 消息缓存管理
        CacheManager cacheMgr = ctx.channel().attr(Constants.CACHE_MANAGER).get();

        // S帧消息的接受序列号（表示期望接收到的发送序列号），所以使用此值作为key去查找缓存
        Short exceptReceiveSequenceNum = message.getControl().getReceiveSequenceNum();

        // 清理缓存中 小于 序列号的消息
        cacheMgr.removeLess(exceptReceiveSequenceNum);
        LOGGER.debug("[{}-通用数据处理-S帧数据接收] 收到S帧 = {}, 清理后缓存 = {}", this.name, exceptReceiveSequenceNum, cacheMgr.getAllSendSequenceNum());


//        // todo: 此处逻辑待定，先注释掉
//        //获取缓存中 大于等于 序列号的消息重新发送
//        List<Message> messagesToSend = cacheMgr.getGreaterEqual(exceptReceiveSequenceNum);
//        for (Message m : messagesToSend) {
//            LOGGER.debug("[{}-通用数据处理-S帧数据接收] 收到S帧 = {}, 缓存中存在大于等于序列号的消息，重新发送I帧: 发送序列号 = {}, 接受序列号 = {}", this.name, exceptReceiveSequenceNum, m.getControl().getSendSequenceNum(), m.getControl().getReceiveSequenceNum());
//            ctx.writeAndFlush(m);
//        }

    }


    /**
     * [读处理] 处理U帧消息
     */
    private void readHandleUFrameMessage(ChannelHandlerContext ctx, Message message) {
        // 将消息继续传递给下一个 Handler 继续处理
        ctx.fireChannelRead(message);
    }

}
