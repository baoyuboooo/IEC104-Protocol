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
     * 推送远程操控
     * <p>
     * * 总召唤
     * * 遥控
     *
     * @param remoteOperation 远程操控
     */
    void push(RemoteOperation remoteOperation);

    /**
     * 主动关闭客户端
     */
    void closeClient();

}
