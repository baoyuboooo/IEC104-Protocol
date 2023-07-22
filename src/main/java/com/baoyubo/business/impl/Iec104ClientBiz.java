package com.baoyubo.business.impl;

import com.baoyubo.business.ClientBiz;
import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.client.ClientChannel;
import com.baoyubo.iec104.client.Iec104ClientChannel;
import com.baoyubo.iec104.config.ClientConfig;
import com.baoyubo.iec104.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * IEC104协议消息 客户端 自定义业务处理
 *
 * @author yubo.bao
 * @date 2023/7/22 20:37
 */
@Service
public class Iec104ClientBiz implements ClientBiz {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec104ClientBiz.class);

    /**
     * 客户端 Channel
     */
    private ClientChannel clientChannel;

    /**
     * 启动客户端
     *
     * @param remoteHost 远程主机地址
     * @param remotePort 远程端口
     */
    @Override
    public void startClient(String remoteHost, int remotePort) {
        ClientConfig config = new ClientConfig();
        config.setRemoteHost(remoteHost);
        config.setRemotePort(remotePort);

        this.clientChannel = new Iec104ClientChannel(config, this::handleData);
    }

    /**
     * 关闭客户端
     */
    @Override
    public void closeClient() {
        if (this.clientChannel != null) {
            this.clientChannel.closeClient();
        }
    }

    /**
     * 推送远程操控数据
     *
     * @param remoteOperation 远程操控
     */
    @Override
    public void pushData(RemoteOperation remoteOperation) {
        this.clientChannel.push(remoteOperation);
    }

    /**
     * 处理远程操控数据
     *
     * @param remoteOperation 远程操控
     */
    @Override
    public void handleData(RemoteOperation remoteOperation) {
        LOGGER.info("[客户端-业务处理] 收到客户端数据，开始处理...  数据 = {}", JsonUtil.toJsonString(remoteOperation));
        // todo 业务自定义数据处理逻辑
    }
}
