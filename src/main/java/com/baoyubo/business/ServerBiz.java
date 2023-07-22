package com.baoyubo.business;

import com.baoyubo.business.model.RemoteOperation;

/**
 * 服务端 自定义业务处理
 *
 * @author yubo.bao
 * @date 2023/7/22 20:31
 */
public interface ServerBiz {

    /**
     * 启动服务端
     *
     * @param port 端口
     */
    void startServer(int port);

    /**
     * 关闭服务端
     */
    void closeServer();

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
