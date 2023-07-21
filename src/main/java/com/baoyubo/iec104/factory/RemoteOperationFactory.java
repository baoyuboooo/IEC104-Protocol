package com.baoyubo.iec104.factory;

import com.baoyubo.business.enums.RemoteOperateTypeEnum;
import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.model.Message;


/**
 * 远程操控 构建工厂
 *
 * @author yubo.bao
 * @date 2023/7/5 18:44
 */
public class RemoteOperationFactory {

    /**
     * 根据 IEC104协议消息 构建 远程操控  (协议模型 -> 业务模型)
     *
     * @param operateTypeEnum 远程操控类型
     * @param receivedMessage 客户端收到IEC104协议消息
     * @return 远程操控
     */
    public static RemoteOperation buildClientRemoteOperationByMessage(RemoteOperateTypeEnum operateTypeEnum, Message receivedMessage) {
        return new RemoteOperation();
    }

}
