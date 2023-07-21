package com.baoyubo.iec104.handler;

import com.baoyubo.iec104.constant.Constants;
import com.baoyubo.iec104.enums.FrameTypeEnum;
import com.baoyubo.iec104.enums.QualifiersEnum;
import com.baoyubo.iec104.enums.TypeIdentifierEnum;
import com.baoyubo.iec104.enums.UControlEnum;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.model.MessageASDU;
import com.baoyubo.iec104.model.MessageControl;
import com.baoyubo.iec104.model.MessageInfo;
import com.baoyubo.iec104.model.MessageVSQ;
import com.baoyubo.iec104.util.ByteUtil;
import com.baoyubo.iec104.util.Iec104ByteUtil;
import com.baoyubo.iec104.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 解码器 (字节报文 -> 协议模型)
 *
 * @author yubo.bao
 * @date 2023/7/3 17:46
 */
public class DataDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDecoder.class);

    private final String name;


    public DataDecoder(String name) {
        this.name = name;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {

        // 校验字节报文
        if (!check(byteBuf)) {
            return;
        }

        LOGGER.info("[{}-解码器] 准备执行消息解码, HexString : {}", name, ByteUtil.toHexString(byteBuf.array()));

        // 跳过 固定头字段 和 APDU长度字段
        byteBuf.skipBytes(Constants.HEADER_FIELD_LEN + Constants.APDU_LENGTH_FIELD_LEN);

        // 解析字节报文
        Message message = decode(byteBuf);
        list.add(message);

        LOGGER.info("[{}-解码器] 消息解码完成 Message : {}", name, JsonUtil.toJsonString(message));
    }


    /**
     * 校验数据
     *
     * @param byteBuf byteBuf
     * @return boolean
     */
    private boolean check(ByteBuf byteBuf) {
        return byteBuf.getByte(0) == Constants.HEADER
                && byteBuf.readableBytes() >= Constants.APCI_FIELD_LEN;
    }


    /**
     * 执行解码
     *
     * @param byteBuf byteBuf
     * @return Message
     */
    private Message decode(ByteBuf byteBuf) {

        Message message = new Message();

        // 控制域 (占4个字节)
        byte[] controlBytes = new byte[4];
        byteBuf.readBytes(controlBytes);
        FrameTypeEnum frameTypeEnum = FrameTypeEnum.ofBytes(controlBytes);
        message.setFrameType(frameTypeEnum);

        // U帧 (控制域)
        if (FrameTypeEnum.U_FRAME == frameTypeEnum) {
            MessageControl control = new MessageControl();
            control.setUControl(UControlEnum.ofBytes(controlBytes));
            message.setControl(control);
            return message;
        }

        // S帧 (控制域)
        if (FrameTypeEnum.S_FRAME == frameTypeEnum) {
            MessageControl control = new MessageControl();
            control.setReceiveSequenceNum(Iec104ByteUtil.controlByteArrayToReceiveSequenceNum(controlBytes));
            message.setControl(control);
            return message;
        }

        // I帧 (控制域 + ASDU)
        if (FrameTypeEnum.I_FRAME == frameTypeEnum) {
            MessageControl control = new MessageControl();
            control.setSendSequenceNum(Iec104ByteUtil.controlByteArrayToSendSequenceNum(controlBytes));
            control.setReceiveSequenceNum(Iec104ByteUtil.controlByteArrayToReceiveSequenceNum(controlBytes));
            message.setControl(control);
            message.setAsdu(decodeASDU(byteBuf));
            return message;
        }

        throw new RuntimeException("异常数据");
    }


    /**
     * 执行 ASDU 解码
     *
     * @param byteBuf byteBuf
     * @return Message
     */
    private MessageASDU decodeASDU(ByteBuf byteBuf) {

        MessageASDU asdu = new MessageASDU();

        // 类型标识符TI (占1个字节)
        TypeIdentifierEnum typeIdentifier = TypeIdentifierEnum.ofValue(byteBuf.readByte());
        asdu.setTypeIdentifier(typeIdentifier);

        // 可变结构限定词VSQ (占1个字节)
        MessageVSQ vsq = Iec104ByteUtil.byteArrayToVsq(byteBuf.readByte());
        asdu.setVsq(vsq);

        // 传输原因COT (占2个字节)
        byte[] reasonBytes = new byte[2];
        byteBuf.readBytes(reasonBytes);
        asdu.setTransferReason(Iec104ByteUtil.byteArrayToReason(reasonBytes));

        // 应用服务数据单元公共地址 (占2个字节)
        byte[] commonAddressBytes = new byte[2];
        byteBuf.readBytes(commonAddressBytes);
        asdu.setCommonAddress(Iec104ByteUtil.byteArrayToCommonAddress(commonAddressBytes));

        // 信息数据列表 连续
        if (vsq.getIsContinuous()) {

            // 首个信息对象地址 (占3个字节), 后续信息对象地址连续自增
            byte[] infoAddressBytes = new byte[3];
            byteBuf.readBytes(infoAddressBytes);
            int infoAddress = Iec104ByteUtil.byteArrayToInfoAddress(infoAddressBytes);

            int messageInfoListSize = vsq.getMessageInfoListSize();
            List<MessageInfo> messageInfos = new ArrayList<>(messageInfoListSize);

            for (int i = 0; i < messageInfoListSize; i++) {
                MessageInfo messageInfo = decodeMessageInfo(byteBuf, typeIdentifier, infoAddress++);
                messageInfos.add(messageInfo);
            }
            asdu.setMessageInfoList(messageInfos);
        }
        // 信息数据列表 不连续
        else {

            int messageInfoListSize = vsq.getMessageInfoListSize();
            List<MessageInfo> messageInfos = new ArrayList<>(messageInfoListSize);

            for (int i = 0; i < messageInfoListSize; i++) {

                // 信息对象地址 (占3个字节)
                byte[] infoAddressBytes = new byte[3];
                byteBuf.readBytes(infoAddressBytes);
                int infoAddress = Iec104ByteUtil.byteArrayToInfoAddress(infoAddressBytes);

                MessageInfo messageInfo = decodeMessageInfo(byteBuf, typeIdentifier, infoAddress);
                messageInfos.add(messageInfo);
            }
            asdu.setMessageInfoList(messageInfos);
        }

        return asdu;
    }


    /**
     * 执行 信息数据 解码
     *
     * @param byteBuf        byteBuf
     * @param typeIdentifier typeIdentifier
     * @param infoAddress    infoAddress
     * @return MessageInfo
     */
    private MessageInfo decodeMessageInfo(ByteBuf byteBuf, TypeIdentifierEnum typeIdentifier, int infoAddress) {

        MessageInfo messageInfo = new MessageInfo();

        //  信息对象地址 (占3个字节)
        messageInfo.setInfoAddress(infoAddress);

        // 信息对象值 (所占字节个数不固定 1、2、4)
        if (TypeIdentifierEnum.hasMessageInfoValue(typeIdentifier)) {
            byte[] infoValue = new byte[typeIdentifier.getMessageInfoValueLength()];
            byteBuf.readBytes(infoValue);
            messageInfo.setInfoValue(infoValue);
        }

        //  限定词/描述符 (占1个字节)
        if (TypeIdentifierEnum.hasMessageInfoQualifier(typeIdentifier)) {
            messageInfo.setQualifier(QualifiersEnum.ofValue(byteBuf.readByte()));
        }

        // 时标 (占7个字节)
        if (TypeIdentifierEnum.hasMessageInfoTimeScale(typeIdentifier)) {
            byte[] timeScaleBytes = new byte[Constants.MESSAGE_INFO_TIME_SCALE_FIELD_LEN];
            byteBuf.readBytes(timeScaleBytes);
            messageInfo.setTimeScale(ByteUtil.cp56TimeByteArrayToDate(timeScaleBytes));
        }

        return messageInfo;
    }

}
