package com.baoyubo.business.impl;

import com.baoyubo.business.ServerBiz;
import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.config.ServerConfig;
import com.baoyubo.iec104.server.Iec104ServerChannel;
import com.baoyubo.iec104.server.ServerChannel;
import com.baoyubo.iec104.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * IEC104协议消息 服务端 自定义业务处理
 *
 * @author yubo.bao
 * @date 2023/7/22 20:37
 */
@Service
public class Iec104ServerBiz implements ServerBiz {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec104ServerBiz.class);

    /**
     * 服务端 Channel
     */
    private ServerChannel serverChannel;

    /**
     * 启动服务端
     *
     * @param port 端口
     */
    @Override
    public void startServer(int port) {
        ServerConfig config = new ServerConfig();
        config.setPort(port);

        this.serverChannel = new Iec104ServerChannel(config, this::handleData);
    }

    /**
     * 关闭服务端
     */
    @Override
    public void closeServer() {
        if (this.serverChannel != null) {
            this.serverChannel.closeServer();
        }
    }

    /**
     * 推送远程操控数据
     *
     * @param remoteOperation 远程操控
     */
    @Override
    public void pushData(RemoteOperation remoteOperation) {
        this.serverChannel.push(remoteOperation);
    }

    /**
     * 处理远程操控数据
     *
     * @param remoteOperation 远程操控
     */
    @Override
    public void handleData(RemoteOperation remoteOperation) {
        LOGGER.info("[服务端-业务处理] 收到服务端数据，开始处理...  数据 = {}", JsonUtil.toJsonString(remoteOperation));
        // todo 业务自定义数据处理逻辑

    }
}
