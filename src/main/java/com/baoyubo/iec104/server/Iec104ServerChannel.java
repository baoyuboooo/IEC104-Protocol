package com.baoyubo.iec104.server;

import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.config.ServerConfig;
import com.baoyubo.iec104.factory.MessageFactory;
import com.baoyubo.iec104.handler.CommonDataHandler;
import com.baoyubo.iec104.handler.DataDecoder;
import com.baoyubo.iec104.handler.DataEncoder;
import com.baoyubo.iec104.handler.LengthAndHeaderPrepender;
import com.baoyubo.iec104.handler.ServerDataHandler;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.util.JsonUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * IEC104协议 服务端 Channel
 *
 * @author yubo.bao
 * @date 2023/7/20 15:06
 */
public class Iec104ServerChannel implements ServerChannel {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec104ServerChannel.class);

    /**
     * 服务端数据处理
     */
    private final ServerDataHandler serverDataHandler;


    /**
     * 服务端构造函数
     *
     * @param config       服务端配置
     * @param dataConsumer 服务端数据消费者
     */
    public Iec104ServerChannel(ServerConfig config, Consumer<RemoteOperation> dataConsumer) {
        this.serverDataHandler = initIEC104Server(config, dataConsumer);
    }


    /**
     * 初始化服务端
     *
     * @param config          服务端配置
     * @param bizDataConsumer 服务端业务数据消费者
     * @return 服务端连接句柄
     */
    private ServerDataHandler initIEC104Server(ServerConfig config, Consumer<RemoteOperation> bizDataConsumer) {

        // 服务端数据处理
        ServerDataHandler serverDataHandler = new ServerDataHandler(bizDataConsumer);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(255, 1, 1, 0, 0));
                        ch.pipeline().addLast(new LengthAndHeaderPrepender());
                        ch.pipeline().addLast(new IdleStateHandler(config.getChannelTestDuration(), 0, 0));
                        ch.pipeline().addLast(new DataEncoder("服务端"));
                        ch.pipeline().addLast(new DataDecoder("服务端"));
                        ch.pipeline().addLast(new CommonDataHandler("服务端"));
                        ch.pipeline().addLast(serverDataHandler);
                    }
                });
        serverBootstrap.bind(config.getPort());

        return serverDataHandler;
    }


    @Override
    public void push(RemoteOperation remoteOperation) {
        LOGGER.info("[服务端-推送远程操控] {} , RemoteOperation : {}", remoteOperation.getOperateType().getDescription(), JsonUtil.toJsonString(remoteOperation));

        Message message = MessageFactory.buildServerMessageByRemoteOperation(remoteOperation);
        LOGGER.debug("[服务端-推送远程操控] {} , Message : {}", remoteOperation.getOperateType().getDescription(), JsonUtil.toJsonString(message));

        this.serverDataHandler.getCtx().writeAndFlush(message);
    }


    @Override
    public void closeServer() {
        this.serverDataHandler.getCtx().close();
    }
}
