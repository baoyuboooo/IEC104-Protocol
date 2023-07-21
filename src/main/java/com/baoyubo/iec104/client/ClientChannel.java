package com.baoyubo.iec104.client;

import com.baoyubo.business.model.RemoteOperation;

/**
 * 客户端 Channel
 *
 * @author yubo.bao
 * @date 2023/7/20 15:02
 */
public interface ClientChannel {

    /**
     * 下发远程操控 (支持类型: 总召唤、遥控)
     *
     * @param remoteOperation 远程操控
     */
    void sendRemoteOperation(RemoteOperation remoteOperation);

    /**
     * 主动关闭连接
     */
    void close();

}
