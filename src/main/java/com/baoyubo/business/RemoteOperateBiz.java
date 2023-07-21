package com.baoyubo.business;

import com.baoyubo.business.model.RemoteOperation;

/**
 * 远程操控 业务逻辑
 *
 * @author yubo.bao
 * @date 2023/7/20 13:43
 */
public interface RemoteOperateBiz {

    /**
     * 向客户端下发远程操控
     *
     * @param remoteOperation 远程操控
     */
    void sendToClient(RemoteOperation remoteOperation);

    /**
     * 处理客户端收到的远程操控数据
     *
     * @param remoteOperation 远程操控
     */
    void handleClientData(RemoteOperation remoteOperation);

    /**
     * 向服务端下发远程操控
     *
     * @param remoteOperation 远程操控
     */
    void sendToServer(RemoteOperation remoteOperation);

    /**
     * 处理服务端收到的远程操控数据
     *
     * @param remoteOperation 远程操控
     */
    void handleServerData(RemoteOperation remoteOperation);

}
