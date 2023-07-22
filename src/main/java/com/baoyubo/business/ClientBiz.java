package com.baoyubo.business;

import com.baoyubo.business.model.RemoteOperation;

/**
 * 客户端 自定义业务处理
 *
 * @author yubo.bao
 * @date 2023/7/22 20:31
 */
public interface ClientBiz {

    /**
     * 启动客户端
     *
     * @param remoteHost 远程主机地址
     * @param remotePort 远程端口
     */
    void startClient(String remoteHost, int remotePort);

    /**
     * 关闭客户端
     */
    void closeClient();

    /**
     * 推送远程操控数据
     *
     * @param remoteOperation 远程操控
     */
    void pushData(RemoteOperation remoteOperation);

    /**
     * 处理远程操控数据
     *
     * @param remoteOperation 远程操控
     */
    void handleData(RemoteOperation remoteOperation);
}
