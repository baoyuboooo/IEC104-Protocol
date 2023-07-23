package com.baoyubo.iec104.factory;

import com.baoyubo.business.enums.RemoteOperateTypeEnum;
import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.util.ByteUtil;
import com.baoyubo.iec104.util.Iec104ByteUtil;

import java.util.HashMap;
import java.util.Map;


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
     * @param receivedMessage 收到IEC104协议消息
     * @return 远程操控
     */
    public static RemoteOperation buildRemoteOperationByMessage(RemoteOperateTypeEnum operateTypeEnum, Message receivedMessage) {
        switch (operateTypeEnum) {
            case GENERAL_CALL:
            case GENERAL_CALL_END:
            case CLOSE:
                return buildRemoteOperation(operateTypeEnum);
            case HARUNOBU:
            case GENERAL_CALL_HARUNOBU:
                return buildRemoteOperationHarunobu(operateTypeEnum, receivedMessage);
            case TELEMETRY:
            case GENERAL_CALL_TELEMETRY:
                return buildRemoteOperationTelemetry(operateTypeEnum, receivedMessage);
            case REMOTE_CONTROL:
                return buildRemoteOperationRemoteControl(operateTypeEnum, receivedMessage);
            default:
                throw new RuntimeException("不支持");
        }
    }


    /**
     * 远程操控 业务模型
     *
     * @param operateTypeEnum operateTypeEnum
     * @return RemoteOperation
     */
    private static RemoteOperation buildRemoteOperation(RemoteOperateTypeEnum operateTypeEnum) {
        RemoteOperation remoteOperation = new RemoteOperation();
        remoteOperation.setOperateType(operateTypeEnum);
        return remoteOperation;
    }


    /**
     * 远程操控-遥信 业务模型
     *
     * @param operateTypeEnum operateTypeEnum
     * @param receivedMessage receivedMessage
     * @return RemoteOperation
     */
    private static RemoteOperation buildRemoteOperationHarunobu(RemoteOperateTypeEnum operateTypeEnum, Message receivedMessage) {

        // 遥信：Object 为整型
        Map<Integer, Object> params = new HashMap<>();
        receivedMessage.getAsdu().getMessageInfoList().forEach(messageInfo -> {
            // 遥信的值为1字节数字
            params.put(messageInfo.getInfoAddress(), (int) messageInfo.getInfoValue()[0]);
        });

        RemoteOperation remoteOperation = new RemoteOperation();
        remoteOperation.setOperateType(operateTypeEnum);
        remoteOperation.setParams(params);
        return remoteOperation;
    }


    /**
     * 远程操控-遥测 业务模型
     *
     * @param operateTypeEnum operateTypeEnum
     * @param receivedMessage receivedMessage
     * @return RemoteOperation
     */
    private static RemoteOperation buildRemoteOperationTelemetry(RemoteOperateTypeEnum operateTypeEnum, Message receivedMessage) {

        // 遥信：Object 为短浮点型
        Map<Integer, Object> params = new HashMap<>();
        receivedMessage.getAsdu().getMessageInfoList().forEach(messageInfo -> {
            // 遥信的值为4字节浮点型
            params.put(messageInfo.getInfoAddress(), (float) ByteUtil.byteArrayToFloat(messageInfo.getInfoValue()));
        });

        RemoteOperation remoteOperation = new RemoteOperation();
        remoteOperation.setOperateType(operateTypeEnum);
        remoteOperation.setParams(params);
        return remoteOperation;
    }


    /**
     * 远程操控-遥控 业务模型
     *
     * @param operateTypeEnum operateTypeEnum
     * @param receivedMessage receivedMessage
     * @return RemoteOperation
     */
    private static RemoteOperation buildRemoteOperationRemoteControl(RemoteOperateTypeEnum operateTypeEnum, Message receivedMessage) {

        // 遥信：Object 为整型
        Map<Integer, Object> params = new HashMap<>();
        receivedMessage.getAsdu().getMessageInfoList().forEach(messageInfo -> {
            // 双命令遥控信息 DCO
            byte dco = messageInfo.getInfoValue()[0];
            int[] res = Iec104ByteUtil.parseRemoteControlValueDCO(dco);
            int se = res[0];
            int qu = res[1];
            int scs = res[2];

            int value = (scs == 2) ? 1 : 0;
            params.put(messageInfo.getInfoAddress(), (int) value);
        });

        RemoteOperation remoteOperation = new RemoteOperation();
        remoteOperation.setOperateType(operateTypeEnum);
        remoteOperation.setParams(params);
        return remoteOperation;
    }

}
