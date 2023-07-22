package com.baoyubo.iec104.handler;


import com.baoyubo.business.enums.RemoteOperateTypeEnum;
import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.factory.RemoteOperationFactory;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * 服务端 数据处理
 *
 * @author yubo.bao
 * @date 2023/7/3 17:57
 */
public class ServerDataHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerDataHandler.class);

    /**
     * 业务数据消费者
     */
    private final Consumer<RemoteOperation> bizDataConsumer;


    /**
     * 构造函数
     *
     * @param bizDataConsumer 业务数据消费者
     */
    public ServerDataHandler(Consumer<RemoteOperation> bizDataConsumer) {
        this.bizDataConsumer = bizDataConsumer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[服务端-建立连接]");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[服务端-关闭连接]");
        // 通知业务处理服务端连接关闭
        RemoteOperation remoteOperate = RemoteOperationFactory.buildClientRemoteOperationByMessage(RemoteOperateTypeEnum.CLOSE, null);
        bizDataConsumer.accept(remoteOperate);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        LOGGER.info("[服务端-收到消息-处理开始] =============  Message : {}", JsonUtil.toJsonString(message));

        LOGGER.info("[服务端-收到消息-处理完毕] =============");
    }

}
