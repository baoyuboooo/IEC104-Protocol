package com.baoyubo.iec104.factory;

import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.enums.FrameTypeEnum;
import com.baoyubo.iec104.enums.UControlEnum;
import com.baoyubo.iec104.manager.ControlManager;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.model.MessageControl;

/**
 * IEC104协议消息 构建工厂
 *
 * @author yubo.bao
 * @date 2023/7/20 15:15
 */
public final class MessageFactory {

    /**
     * 根据 客户端下发的远程操控 构建 IEC104协议消息 (业务模型 -> 协议模型)
     *
     * @param remoteOperation 远程操控
     * @return IEC104协议消息
     */
    public static Message buildClientMessageByRemoteOperation(RemoteOperation remoteOperation) {
        return new Message();
    }


    /**
     * 客户端 初始化（启动链路）
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
