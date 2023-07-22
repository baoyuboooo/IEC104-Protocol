package com.baoyubo.iec104.server;

import com.baoyubo.business.model.RemoteOperation;

/**
 * 服务端 Channel
 *
 * @author yubo.bao
 * @date 2023/7/20 15:02
 */
public interface ServerChannel {

    /**
     * 推送远程操控
     * <p>
     * * 遥信
     * * 总召唤遥信
     * * 遥测
     * * 总召唤遥测
     *
     * @param remoteOperation 远程操控
     */
    void push(RemoteOperation remoteOperation);

    /**
     * 主动关闭服务端
     */
    void closeServer();

}
