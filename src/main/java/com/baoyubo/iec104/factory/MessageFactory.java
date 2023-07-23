package com.baoyubo.iec104.factory;

import com.baoyubo.business.enums.RemoteOperateTypeEnum;
import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.constant.Constants;
import com.baoyubo.iec104.enums.FrameTypeEnum;
import com.baoyubo.iec104.enums.QualifiersEnum;
import com.baoyubo.iec104.enums.TypeIdentifierEnum;
import com.baoyubo.iec104.enums.UControlEnum;
import com.baoyubo.iec104.manager.ControlManager;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.model.MessageASDU;
import com.baoyubo.iec104.model.MessageControl;
import com.baoyubo.iec104.model.MessageInfo;
import com.baoyubo.iec104.model.MessageVSQ;
import com.baoyubo.iec104.util.ByteUtil;
import com.baoyubo.iec104.util.Iec104ByteUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * IEC104协议消息 构建工厂
 *
 * @author yubo.bao
 * @date 2023/7/20 15:15
 */
public final class MessageFactory {

    /**
     * 根据 客户端业务推送的远程操控 构建 IEC104协议消息 (业务模型 -> 协议模型)
     * <p>
     * * 总召唤
     * * 遥控
     *
     * @param remoteOperation 远程操控
     * @return IEC104协议消息
     */
    public static Message buildClientMessageByRemoteOperation(RemoteOperation remoteOperation) {
        RemoteOperateTypeEnum operateType = remoteOperation.getOperateType();
        switch (operateType) {
            case GENERAL_CALL:
                return buildClientGeneralCallMessage();
            case REMOTE_CONTROL:
                return buildClientRemoteControlSelectMessage(remoteOperation.getParams());
            default:
                throw new RuntimeException("客户端不支持");
        }
    }


    /**
     * 根据 服务端业务推送的远程操控 构建 IEC104协议消息 (业务模型 -> 协议模型)
     * <p>
     * * 遥信
     * * 总召唤遥信
     * * 遥测
     * * 总召唤遥测
     * * 总召唤结束
     *
     * @param remoteOperation 远程操控
     * @return IEC104协议消息
     */
    public static Message buildServerMessageByRemoteOperation(RemoteOperation remoteOperation) {
        RemoteOperateTypeEnum operateType = remoteOperation.getOperateType();
        switch (operateType) {
            case GENERAL_CALL_HARUNOBU:
                return buildServerHarunobuMessage(true, remoteOperation.getParams());
            case HARUNOBU:
                return buildServerHarunobuMessage(false, remoteOperation.getParams());
            case GENERAL_CALL_TELEMETRY:
                return buildServerTelemetryMessage(true, remoteOperation.getParams());
            case TELEMETRY:
                return buildServerTelemetryMessage(false, remoteOperation.getParams());
            case GENERAL_CALL_END:
                return buildServerGeneralCallEndMessage();
            default:
                throw new RuntimeException("服务端不支持");
        }
    }


    /**
     * 客户端 初始化-启动链路 (U帧)
     *
     * @return IEC104协议消息
     */
    public static Message buildClientInitStartMessage() {
        MessageControl uControl = new MessageControl();
        uControl.setUControl(UControlEnum.START);

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.U_FRAME);
        message.setControl(uControl);

        return message;
    }


    /**
     * 服务端 初始化-启动链路确认 (U帧)
     *
     * @return IEC104协议消息
     */
    public static Message buildServerInitStartReplyMessage() {
        MessageControl uControl = new MessageControl();
        uControl.setUControl(UControlEnum.START_REPLY);

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.U_FRAME);
        message.setControl(uControl);

        return message;
    }


    /**
     * 服务端 初始化-结束 (I帧)
     *
     * @return IEC104协议消息
     */
    public static Message buildServerInitEndMessage() {

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setInfoAddress(Constants.INFO_ADDRESS_0);
        messageInfo.setInfoValue(new byte[]{0x0});

        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.INIT_END);
        asdu.setVsq(new MessageVSQ(false, 1));
        asdu.setTransferReason(Constants.COT_4);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(Collections.singletonList(messageInfo));

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 客户端 总召唤-命令 (I帧)
     *
     * @return IEC104协议消息
     */
    public static Message buildClientGeneralCallMessage() {

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setInfoAddress(Constants.INFO_ADDRESS_0);
        messageInfo.setQualifier(QualifiersEnum.GENERAL_CALL_QUALIFIER);

        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.GENERAL_CALL);
        asdu.setVsq(new MessageVSQ(false, 1));
        asdu.setTransferReason(Constants.COT_6);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(Collections.singletonList(messageInfo));

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 服务端 总召唤-确认 (I帧)
     *
     * @return IEC104协议消息
     */
    public static Message buildServerGeneralCallReplyMessage() {

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setInfoAddress(Constants.INFO_ADDRESS_0);
        messageInfo.setQualifier(QualifiersEnum.GENERAL_CALL_QUALIFIER);

        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.GENERAL_CALL);
        asdu.setVsq(new MessageVSQ(false, 1));
        asdu.setTransferReason(Constants.COT_7);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(Collections.singletonList(messageInfo));

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 服务端 遥信 (I帧)
     *
     * @param isGeneralCall 是否为总召唤,
     * @param params        参数信息，值为整数类型, 占用1字节 (0-关， 1-开)
     * @return IEC104协议消息
     */
    public static Message buildServerHarunobuMessage(boolean isGeneralCall, Map<Integer, Object> params) {

        List<MessageInfo> messageInfoList = new ArrayList<>(params.size());
        params.forEach((infoAddr, value) -> {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setInfoAddress(infoAddr);
            messageInfo.setInfoValue(new byte[]{Integer.valueOf((int) value).byteValue()});
            messageInfoList.add(messageInfo);
        });

        // 单点遥信
        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.ONE_POINT_HARUNOBU);
        asdu.setVsq(new MessageVSQ(false, messageInfoList.size()));
        asdu.setTransferReason(isGeneralCall ? Constants.COT_20 : Constants.COT_3);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(messageInfoList);

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 服务端 遥测 (I帧)
     *
     * @param isGeneralCall 是否为总召唤,
     * @param params        参数信息，值为浮点类型, 占用4字节
     * @return IEC104协议消息
     */
    public static Message buildServerTelemetryMessage(boolean isGeneralCall, Map<Integer, Object> params) {

        List<MessageInfo> messageInfoList = new ArrayList<>(params.size());
        params.forEach((infoAddr, value) -> {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setInfoAddress(infoAddr);
            messageInfo.setInfoValue(ByteUtil.floatToByteArray((float) value));
            messageInfo.setQualifier(QualifiersEnum.TELEMETRY_QUALIFIER);
            messageInfoList.add(messageInfo);
        });

        // 测量值 短浮点数 遥测
        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.SHORT_FLOAT_POINT_TELEMETRY);
        asdu.setVsq(new MessageVSQ(false, messageInfoList.size()));
        asdu.setTransferReason(isGeneralCall ? Constants.COT_20 : Constants.COT_3);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(messageInfoList);

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 服务端 总召唤-结束 (I帧)
     *
     * @return IEC104协议消息
     */
    public static Message buildServerGeneralCallEndMessage() {

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setInfoAddress(Constants.INFO_ADDRESS_0);
        messageInfo.setQualifier(QualifiersEnum.GENERAL_CALL_QUALIFIER);

        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.GENERAL_CALL);
        asdu.setVsq(new MessageVSQ(false, 1));
        asdu.setTransferReason(Constants.COT_10);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(Collections.singletonList(messageInfo));

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 客户端 时钟同步-命令 (I帧)
     *
     * @return IEC104协议消息
     */
    public static Message buildClientTimeSyncMessage(Date date) {

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setInfoAddress(Constants.INFO_ADDRESS_0);
        messageInfo.setTimeScale(date);

        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.TIME_SYNCHRONIZATION);
        asdu.setVsq(new MessageVSQ(false, 1));
        asdu.setTransferReason(Constants.COT_6);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(Collections.singletonList(messageInfo));

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 服务端 时钟同步-确认 (I帧)
     *
     * @return IEC104协议消息
     */
    public static Message buildServerTimeSyncReplyMessage(Date date) {

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setInfoAddress(Constants.INFO_ADDRESS_0);
        messageInfo.setTimeScale(date);

        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.TIME_SYNCHRONIZATION);
        asdu.setVsq(new MessageVSQ(false, 1));
        asdu.setTransferReason(Constants.COT_7);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(Collections.singletonList(messageInfo));

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 客户端 时钟读取-命令 (I帧)
     *
     * @return IEC104协议消息
     */
    public static Message buildClientTimeReadMessage(Date date) {

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setInfoAddress(Constants.INFO_ADDRESS_0);
        messageInfo.setTimeScale(date);

        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.TIME_SYNCHRONIZATION);
        asdu.setVsq(new MessageVSQ(false, 1));
        asdu.setTransferReason(Constants.COT_5);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(Collections.singletonList(messageInfo));

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 服务端 时钟读取-确认 (I帧)
     *
     * @return IEC104协议消息
     */
    public static Message buildServerTimeReadReplyMessage(Date date) {

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setInfoAddress(Constants.INFO_ADDRESS_0);
        messageInfo.setTimeScale(date);

        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.TIME_SYNCHRONIZATION);
        asdu.setVsq(new MessageVSQ(false, 1));
        asdu.setTransferReason(Constants.COT_5);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(Collections.singletonList(messageInfo));

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 客户端 遥控选择-命令 (I帧)
     *
     * @param params 参数信息，值为整数类型, 占用1字节 (0-关， 1-开)
     * @return IEC104协议消息
     */
    public static Message buildClientRemoteControlSelectMessage(Map<Integer, Object> params) {

        List<MessageInfo> messageInfoList = new ArrayList<>(params.size());
        params.forEach((infoAddr, value) -> {

            // 双命令遥控信息 DCO
            int se = Constants.REMOTE_CONTROL_SE_SELECT;
            int qu = 0;
            int scs = ((int) value == 1) ? 2 : 0;
            byte dco = Iec104ByteUtil.buildRemoteControlValueDCO(se, qu, scs);

            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setInfoAddress(infoAddr);
            messageInfo.setInfoValue(new byte[]{dco});
            messageInfoList.add(messageInfo);
        });


        // 双命令 遥控
        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(TypeIdentifierEnum.TWO_POINT_REMOTE_CONTROL);
        asdu.setVsq(new MessageVSQ(false, messageInfoList.size()));
        asdu.setTransferReason(Constants.COT_6);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(messageInfoList);

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 服务端 遥控选择-确认 (I帧)
     *
     * @param receivedMessage 接收到的消息 (遥控选择-命令)
     * @return IEC104协议消息
     */
    public static Message buildServerRemoteControlSelectReplyMessage(Message receivedMessage) {

        // 更新 接收到的消息-传输原因字段
        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(receivedMessage.getAsdu().getTypeIdentifier());
        asdu.setVsq(receivedMessage.getAsdu().getVsq());
        asdu.setTransferReason(Constants.COT_7);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(receivedMessage.getAsdu().getMessageInfoList());

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 客户端 遥控执行-命令 (I帧)
     *
     * @param receivedMessage 接收到的消息 (遥控选择-确认)
     * @return IEC104协议消息
     */
    public static Message buildClientRemoteControlExecuteMessage(Message receivedMessage) {

        // 更新 接收到的消息-信息数据值字段：将 遥控(SE选择) 更新为 遥控(SE执行)
        List<MessageInfo> messageInfoList = new ArrayList<>(receivedMessage.getAsdu().getMessageInfoList().size());
        receivedMessage.getAsdu().getMessageInfoList().forEach(receivedMessageInfo -> {
            byte receivedDCO = receivedMessageInfo.getInfoValue()[0];
            byte newDCO = Iec104ByteUtil.updateRemoteControlValueSE(receivedDCO, Constants.REMOTE_CONTROL_SE_EXECUTE);

            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setInfoAddress(receivedMessageInfo.getInfoAddress());
            messageInfo.setInfoValue(new byte[]{newDCO});
            messageInfoList.add(messageInfo);
        });

        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(receivedMessage.getAsdu().getTypeIdentifier());
        asdu.setVsq(receivedMessage.getAsdu().getVsq());
        asdu.setTransferReason(Constants.COT_6);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(messageInfoList);

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 服务端 遥控执行-确认 (I帧)
     *
     * @param receivedMessage 接收到的消息 (遥控执行-命令)
     * @return IEC104协议消息
     */
    public static Message buildServerRemoteControlExecuteReplyMessage(Message receivedMessage) {

        // 更新 接收到的消息-传输原因字段
        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(receivedMessage.getAsdu().getTypeIdentifier());
        asdu.setVsq(receivedMessage.getAsdu().getVsq());
        asdu.setTransferReason(Constants.COT_7);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(receivedMessage.getAsdu().getMessageInfoList());

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 服务端 遥控执行-结束 (I帧)
     *
     * @param receivedMessage 接收到的消息 (遥控执行-命令)
     * @return IEC104协议消息
     */
    public static Message buildServerRemoteControlExecuteEndMessage(Message receivedMessage) {

        // 更新 接收到的消息-传输原因字段
        MessageASDU asdu = new MessageASDU();
        asdu.setTypeIdentifier(receivedMessage.getAsdu().getTypeIdentifier());
        asdu.setVsq(receivedMessage.getAsdu().getVsq());
        asdu.setTransferReason(Constants.COT_10);
        asdu.setCommonAddress(Constants.DEFAULT_COMMON_ADDRESS);
        asdu.setMessageInfoList(receivedMessage.getAsdu().getMessageInfoList());

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(null);
        message.setAsdu(asdu);

        return message;
    }


    /**
     * 测试帧 (U帧)
     */
    public static Message buildTestMessage() {
        MessageControl uControl = new MessageControl();
        uControl.setUControl(UControlEnum.TEST);

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.U_FRAME);
        message.setControl(uControl);

        return message;
    }


    /**
     * 测试帧回复 (U帧)
     */
    public static Message buildTestReplyMessage() {
        MessageControl uControl = new MessageControl();
        uControl.setUControl(UControlEnum.TEST_REPLY);

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.U_FRAME);
        message.setControl(uControl);

        return message;
    }


    /**
     * I帧控制域  (CommonDataHandler自动处理)
     */
    public static MessageControl commonBuildIControl(ControlManager controlMgr) {
        MessageControl iControl = new MessageControl();
        iControl.setSendSequenceNum(controlMgr.getAndIncrementSendSequenceNum());
        iControl.setReceiveSequenceNum(controlMgr.getReceiveSequenceNum());
        return iControl;
    }


    /**
     * S帧消息 (CommonDataHandler自动处理)
     */
    public static Message commonBuildSFrameMessage(ControlManager controlMgr) {
        MessageControl sControl = new MessageControl();
        sControl.setReceiveSequenceNum(controlMgr.getReceiveSequenceNum());

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.S_FRAME);
        message.setControl(sControl);

        return message;
    }

}
