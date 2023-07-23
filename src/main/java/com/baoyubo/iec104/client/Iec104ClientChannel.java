package com.baoyubo.iec104.client;

import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.config.ClientConfig;
import com.baoyubo.iec104.factory.MessageFactory;
import com.baoyubo.iec104.handler.ClientDataHandler;
import com.baoyubo.iec104.handler.CommonDataHandler;
import com.baoyubo.iec104.handler.DataDecoder;
import com.baoyubo.iec104.handler.DataEncoder;
import com.baoyubo.iec104.handler.LengthAndHeaderPrepender;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.util.JsonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * IEC104协议 客户端 Channel
 *
 * @author yubo.bao
 * @date 2023/7/20 15:06
 */
public class Iec104ClientChannel implements ClientChannel {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec104ClientChannel.class);

    /**
     * 客户端数据处理
     */
    private ClientDataHandler clientDataHandler;


    /**
     * 客户端构造函数
     *
     * @param config       客户端配置
     * @param dataConsumer 客户端数据消费者
     */
    public Iec104ClientChannel(ClientConfig config, Consumer<RemoteOperation> dataConsumer) {
        this.clientDataHandler = initIEC104Client(config, dataConsumer);
    }


    /**
     * 初始化客户端
     *
     * @param config          客户端配置
     * @param bizDataConsumer 客户端业务数据消费者
     * @return 客户端连接句柄
     */
    private ClientDataHandler initIEC104Client(ClientConfig config, Consumer<RemoteOperation> bizDataConsumer) {

        // 客户端数据处理
        ClientDataHandler clientDataHandler = new ClientDataHandler(bizDataConsumer);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(255, 1, 1, 0, 0));
                        ch.pipeline().addLast(new LengthAndHeaderPrepender());
                        ch.pipeline().addLast(new IdleStateHandler(config.getChannelTestDuration(), 0, 0));
                        ch.pipeline().addLast(new DataEncoder("客户端"));
                        ch.pipeline().addLast(new DataDecoder("客户端"));
                        ch.pipeline().addLast(new CommonDataHandler("客户端"));
                        ch.pipeline().addLast(clientDataHandler);
                    }
                });
        bootstrap.connect(config.getRemoteHost(), config.getRemotePort());

        return clientDataHandler;
    }


    @Override
    public void push(RemoteOperation remoteOperation) {
        LOGGER.info("[客户端-推送远程操控] {} , RemoteOperation : {}", remoteOperation.getOperateType().getDescription(), JsonUtil.toJsonString(remoteOperation));

        Message message = MessageFactory.buildClientMessageByRemoteOperation(remoteOperation);
        LOGGER.debug("[客户端-推送远程操控] {} , Message : {}", remoteOperation.getOperateType().getDescription(), JsonUtil.toJsonString(message));

        this.clientDataHandler.getCtx().writeAndFlush(message);
    }


    @Override
    public void closeClient() {
        this.clientDataHandler.getCtx().close();
    }
}
