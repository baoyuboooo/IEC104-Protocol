package com.baoyubo.iec104.handler;


import com.baoyubo.business.enums.RemoteOperateTypeEnum;
import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.constant.Constants;
import com.baoyubo.iec104.enums.FrameTypeEnum;
import com.baoyubo.iec104.enums.QualifiersEnum;
import com.baoyubo.iec104.enums.TypeIdentifierEnum;
import com.baoyubo.iec104.enums.UControlEnum;
import com.baoyubo.iec104.factory.MessageFactory;
import com.baoyubo.iec104.factory.RemoteOperationFactory;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.model.MessageASDU;
import com.baoyubo.iec104.util.Iec104ByteUtil;
import com.baoyubo.iec104.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.function.Consumer;

/**
 * 客户端 数据处理
 *
 * @author yubo.bao
 * @date 2023/7/3 17:57
 */
public class ClientDataHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataHandler.class);

    /**
     * ChannelHandlerContext
     */
    @Getter
    private ChannelHandlerContext ctx;

    /**
     * 业务数据消费者
     */
    private final Consumer<RemoteOperation> bizDataConsumer;


    /**
     * 构造函数
     *
     * @param bizDataConsumer 业务数据消费者
     */
    public ClientDataHandler(Consumer<RemoteOperation> bizDataConsumer) {
        this.bizDataConsumer = bizDataConsumer;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[客户端-建立连接] 开始自动下发 初始化-启动链路");
        Message message = MessageFactory.buildClientInitStartMessage();
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[客户端-关闭连接]");
        // 通知客户端业务：连接关闭
        RemoteOperation remoteOperate = RemoteOperationFactory.buildClientRemoteOperationByMessage(RemoteOperateTypeEnum.CLOSE, null);
        bizDataConsumer.accept(remoteOperate);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        LOGGER.debug("[客户端-收到消息-处理开始] **********  Message : {}", JsonUtil.toJsonString(message));

        FrameTypeEnum frameTypeEnum = message.getFrameType();
        switch (frameTypeEnum) {
            case I_FRAME:
                handleIFrameMessage(ctx, message);
                break;
            case U_FRAME:
                handleUFrameMessage(ctx, message);
                break;
            case S_FRAME:
                handleSFrameMessage(ctx, message);
                break;
            default:
        }

        LOGGER.debug("[客户端-收到消息-处理完毕] **********");
    }


    /**
     * 处理 I帧消息
     *
     * @param ctx     ctx
     * @param message message
     */
    private void handleIFrameMessage(ChannelHandlerContext ctx, Message message) {
        TypeIdentifierEnum typeIdentifierEnum = message.getAsdu().getTypeIdentifier();

        // 初始化结束
        if (TypeIdentifierEnum.INIT_END == typeIdentifierEnum) {
            handleIFrameInitEndMessage(ctx, message);
            return;
        }

        // 总召唤
        if (TypeIdentifierEnum.GENERAL_CALL == typeIdentifierEnum) {
            handleIFrameGeneralCallMessage(ctx, message);
            return;
        }

        // 时钟同步
        if (TypeIdentifierEnum.TIME_SYNCHRONIZATION == typeIdentifierEnum) {
            handleIFrameTimeSyncMessage(ctx, message);
            return;
        }

        // 遥信
        if (TypeIdentifierEnum.isHarunobu(typeIdentifierEnum)) {
            handleIFrameHarunobuMessage(ctx, message);
            return;
        }

        // 遥测
        if (TypeIdentifierEnum.isTelemetry(typeIdentifierEnum)) {
            handleIFrameTelemetryMessage(ctx, message);
            return;
        }

        // 遥控
        if (TypeIdentifierEnum.isRemoteControl(typeIdentifierEnum)) {
            handleIFrameRemoteControlMessage(ctx, message);
            return;
        }

        LOGGER.warn("[客户端-收到I帧消息] 不支持处理的消息");
    }


    /**
     * 处理 I帧消息 初始化结束
     *
     * @param ctx     ctx
     * @param message message
     */
    private void handleIFrameInitEndMessage(ChannelHandlerContext ctx, Message message) {

        MessageASDU asdu = message.getAsdu();
        TypeIdentifierEnum typeIdentifierEnum = asdu.getTypeIdentifier();

        // 初始化-结束
        if (TypeIdentifierEnum.INIT_END == typeIdentifierEnum && Constants.COT_4 == asdu.getTransferReason()) {
            LOGGER.info("[客户端-收到I帧消息-初始化结束] 初始化-结束");
            return;
        }


        LOGGER.warn("[客户端-收到I帧消息-初始化结束] 不支持处理的消息");
    }


    /**
     * 处理 I帧消息 总召唤
     *
     * @param ctx     ctx
     * @param message message
     */
    private void handleIFrameGeneralCallMessage(ChannelHandlerContext ctx, Message message) {

        MessageASDU asdu = message.getAsdu();
        TypeIdentifierEnum typeIdentifierEnum = asdu.getTypeIdentifier();
        QualifiersEnum qualifiersEnum = asdu.getMessageInfoList().get(0).getQualifier();

        // 总召唤-确认
        if (TypeIdentifierEnum.GENERAL_CALL == typeIdentifierEnum && Constants.COT_7 == asdu.getTransferReason() && QualifiersEnum.GENERAL_CALL_QUALIFIER == qualifiersEnum) {
            LOGGER.info("[客户端-收到I帧消息-总召唤] 总召唤-确认");
            return;
        }

        // 总召唤-结束
        if (TypeIdentifierEnum.GENERAL_CALL == typeIdentifierEnum && Constants.COT_10 == asdu.getTransferReason()) {
            // 通知客户端业务：总召唤结束
            RemoteOperation remoteOperation = RemoteOperationFactory.buildClientRemoteOperationByMessage(RemoteOperateTypeEnum.GENERAL_CALL_END, message);
            bizDataConsumer.accept(remoteOperation);

            LOGGER.info("[客户端-收到I帧消息-总召唤] 总召唤-结束, 开始下发 时钟同步-命令");
            Message newMessage = MessageFactory.buildClientTimeSyncMessage(new Date());
            ctx.writeAndFlush(newMessage);
            return;
        }

        LOGGER.warn("[客户端-收到I帧消息-总召唤] 不支持处理的消息");
    }


    /**
     * 处理 I帧消息 时钟同步
     *
     * @param ctx     ctx
     * @param message message
     */
    private void handleIFrameTimeSyncMessage(ChannelHandlerContext ctx, Message message) {

        MessageASDU asdu = message.getAsdu();
        TypeIdentifierEnum typeIdentifierEnum = asdu.getTypeIdentifier();

        // 时钟同步-确认
        if (TypeIdentifierEnum.TIME_SYNCHRONIZATION == typeIdentifierEnum && Constants.COT_7 == asdu.getTransferReason()) {
            LOGGER.info("[客户端-收到I帧消息-时钟同步] 时钟同步-确认, 开始下发 时钟读取-命令");
            Message newMessage = MessageFactory.buildClientTimeReadMessage(new Date());
            ctx.writeAndFlush(newMessage);
            return;
        }

        // 时钟读取-确认
        if (TypeIdentifierEnum.TIME_SYNCHRONIZATION == typeIdentifierEnum && Constants.COT_5 == asdu.getTransferReason()) {
            LOGGER.info("[客户端-收到I帧消息-时钟同步] 时钟读取-确认");
            return;
        }

        LOGGER.warn("[客户端-收到I帧消息-时钟同步] 不支持处理的消息");
    }


    /**
     * 处理 I帧消息 遥信
     *
     * @param ctx     ctx
     * @param message message
     */
    private void handleIFrameHarunobuMessage(ChannelHandlerContext ctx, Message message) {

        MessageASDU asdu = message.getAsdu();
        TypeIdentifierEnum typeIdentifierEnum = asdu.getTypeIdentifier();

        // 总召唤-遥信数据
        if (TypeIdentifierEnum.isHarunobu(typeIdentifierEnum) && Constants.COT_20 == asdu.getTransferReason()) {
            LOGGER.info("[客户端-收到I帧消息-遥信数据] 总召唤-遥信数据");
            // 通知客户端业务：总召唤-遥信数据
            RemoteOperation remoteOperation = RemoteOperationFactory.buildClientRemoteOperationByMessage(RemoteOperateTypeEnum.GENERAL_CALL_HARUNOBU, message);
            bizDataConsumer.accept(remoteOperation);
            return;
        }

        // 遥信数据
        if (TypeIdentifierEnum.isHarunobu(typeIdentifierEnum) && Constants.COT_3 == asdu.getTransferReason()) {
            LOGGER.info("[客户端-收到I帧消息-遥信数据] 遥信数据");
            // 通知客户端业务：总召唤-遥信数据
            RemoteOperation remoteOperation = RemoteOperationFactory.buildClientRemoteOperationByMessage(RemoteOperateTypeEnum.HARUNOBU, message);
            bizDataConsumer.accept(remoteOperation);
            return;
        }

        LOGGER.warn("[客户端-收到I帧消息-遥信数据] 不支持处理类型的消息");
    }


    /**
     * 处理 I帧消息 遥测
     *
     * @param ctx     ctx
     * @param message message
     */
    private void handleIFrameTelemetryMessage(ChannelHandlerContext ctx, Message message) {

        MessageASDU asdu = message.getAsdu();
        TypeIdentifierEnum typeIdentifierEnum = asdu.getTypeIdentifier();

        // 总召唤-遥测数据
        if (TypeIdentifierEnum.isTelemetry(typeIdentifierEnum) && Constants.COT_20 == asdu.getTransferReason()) {
            LOGGER.info("[客户端-收到I帧消息-遥测数据] 总召唤-遥测数据");
            // 通知客户端业务：总召唤-遥测数据
            RemoteOperation remoteOperation = RemoteOperationFactory.buildClientRemoteOperationByMessage(RemoteOperateTypeEnum.GENERAL_CALL_TELEMETRY, message);
            bizDataConsumer.accept(remoteOperation);
            return;
        }

        // 遥测数据
        if (TypeIdentifierEnum.isTelemetry(typeIdentifierEnum) && Constants.COT_3 == asdu.getTransferReason()) {
            LOGGER.info("[客户端-收到I帧消息-遥测数据] 遥测数据");
            // 通知客户端业务：遥测数据
            RemoteOperation remoteOperation = RemoteOperationFactory.buildClientRemoteOperationByMessage(RemoteOperateTypeEnum.TELEMETRY, message);
            bizDataConsumer.accept(remoteOperation);
            return;
        }

        LOGGER.warn("[客户端-收到I帧消息-遥测数据] 不支持处理的消息");
    }


    /**
     * 处理 I帧消息 遥控
     *
     * @param ctx     ctx
     * @param message message
     */
    private void handleIFrameRemoteControlMessage(ChannelHandlerContext ctx, Message message) {

        MessageASDU asdu = message.getAsdu();
        TypeIdentifierEnum typeIdentifierEnum = asdu.getTypeIdentifier();
        byte remoteControlValue = asdu.getMessageInfoList().get(0).getInfoValue()[0];
        int se = Iec104ByteUtil.getRemoteControlValueSE(remoteControlValue);

        // 遥控选择-确认
        if (TypeIdentifierEnum.isRemoteControl(typeIdentifierEnum) && Constants.COT_7 == asdu.getTransferReason() && Constants.REMOTE_CONTROL_SE_SELECT == se) {
            LOGGER.info("[客户端-收到I帧消息-遥控数据] 遥控选择-确认, 开始下发 遥控执行-命令");
            Message newMessage = MessageFactory.buildClientRemoteControlExecuteMessage(message);
            ctx.writeAndFlush(newMessage);
            return;
        }

        // 遥控执行-确认
        if (TypeIdentifierEnum.isRemoteControl(typeIdentifierEnum) && Constants.COT_7 == asdu.getTransferReason() && Constants.REMOTE_CONTROL_SE_EXECUTE == se) {
            LOGGER.info("[客户端-收到I帧消息-遥控数据] 遥控执行-确认");
            return;
        }

        // 遥控执行-结束
        if (TypeIdentifierEnum.isRemoteControl(typeIdentifierEnum) && Constants.COT_10 == asdu.getTransferReason() && Constants.REMOTE_CONTROL_SE_EXECUTE == se) {
            LOGGER.info("[客户端-收到I帧消息-遥控数据] 遥控执行-结束");
            return;
        }

        LOGGER.warn("[客户端-收到I帧消息-遥控数据] 不支持处理的消息");
    }


    /**
     * 处理 U帧消息
     *
     * @param ctx     ctx
     * @param message message
     */
    private void handleUFrameMessage(ChannelHandlerContext ctx, Message message) {

        if (UControlEnum.START_REPLY == message.getControl().getUControl()) {
            LOGGER.info("[客户端-收到U帧消息] 初始化-启动链路确认");
            return;
        }

        if (UControlEnum.TEST == message.getControl().getUControl()) {
            LOGGER.info("[客户端-收到U帧消息] 测试命令, 开始自动回复 测试确认");
            Message newMessage = MessageFactory.buildTestReplyMessage();
            ctx.writeAndFlush(newMessage);
            return;
        }

        if (UControlEnum.TEST_REPLY == message.getControl().getUControl()) {
            LOGGER.info("[客户端-收到U帧消息] 测试确认");
            return;
        }

        if (UControlEnum.STOP == message.getControl().getUControl()) {
            LOGGER.info("[客户端-收到U帧消息] 停止命令");
            return;
        }

        if (UControlEnum.STOP_REPLY == message.getControl().getUControl()) {
            LOGGER.info("[客户端-收到U帧消息] 停止确认");
            return;
        }

        LOGGER.warn("[客户端-收到U帧消息] 不支持处理的消息");
    }

    /**
     * 处理 S帧消息
     *
     * @param ctx     ctx
     * @param message message
     */
    private void handleSFrameMessage(ChannelHandlerContext ctx, Message message) {

        // CommonDataHandler 统一处理
    }

}
