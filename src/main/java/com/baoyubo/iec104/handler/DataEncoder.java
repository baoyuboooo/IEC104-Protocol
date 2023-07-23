package com.baoyubo.iec104.handler;

import com.baoyubo.iec104.enums.FrameTypeEnum;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.model.MessageASDU;
import com.baoyubo.iec104.model.MessageInfo;
import com.baoyubo.iec104.util.ByteUtil;
import com.baoyubo.iec104.util.Iec104ByteUtil;
import com.baoyubo.iec104.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 编码器 (协议模型 -> 字节报文)
 *
 * @author yubo.bao
 * @date 2023/7/3 17:46
 */
public class DataEncoder extends MessageToByteEncoder<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataEncoder.class);

    private final String name;


    public DataEncoder(String name) {
        this.name = name;
    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        LOGGER.debug("[{}-编码器] 准备执行消息编码, Message : {}", name, JsonUtil.toJsonString(message));

        byte[] bytes = encode(message);
        byteBuf.writeBytes(bytes);

        LOGGER.debug("[{}-编码器] 消息编码完成并发送 HexString : {}", name, ByteUtil.toHexString(bytes));
    }


    /**
     * 执行编码
     *
     * @param message message
     * @return byte[]
     * @throws IOException exp
     */
    private byte[] encode(Message message) throws IOException {

        // 注意：启动字符、APDU长度 这两个字段会由 LengthAndHeaderPrepender 自动填充
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        FrameTypeEnum frameTypeEnum = message.getFrameType();

        // U帧 (控制域)
        if (FrameTypeEnum.U_FRAME == frameTypeEnum) {
            bytes.write(message.getControl().getUControl().getControlBytes());
            return bytes.toByteArray();
        }

        // S帧 (控制域)
        if (FrameTypeEnum.S_FRAME == frameTypeEnum) {
            bytes.write(Iec104ByteUtil.sControlToByteArray(message.getControl().getReceiveSequenceNum()));
            return bytes.toByteArray();
        }

        // I帧 (控制域 + ASDU)
        if (FrameTypeEnum.I_FRAME == frameTypeEnum) {
            bytes.write(Iec104ByteUtil.iControlToByteArray(message.getControl().getSendSequenceNum(), message.getControl().getReceiveSequenceNum()));
            bytes.write(encodeASDU(message.getAsdu()));
            return bytes.toByteArray();
        }

        throw new RuntimeException("异常数据");
    }


    /**
     * 执行 ASDU 编码
     *
     * @param asdu asdu
     * @return byte[]
     */
    private byte[] encodeASDU(MessageASDU asdu) throws IOException {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        // 类型标识符TI
        bytes.write(asdu.getTypeIdentifier().getValue());

        // 可变结构限定词VSQ
        bytes.write(Iec104ByteUtil.vsqToByteArray(asdu.getVsq()));

        // 传输原因
        bytes.write(Iec104ByteUtil.reasonToByteArray(asdu.getTransferReason()));

        // 公共地址
        bytes.write(Iec104ByteUtil.commonAddressToByteArrays(asdu.getCommonAddress()));

        // 信息数据列表(连续)
        if (asdu.getVsq().getIsContinuous()) {

            // 第一个信息对象地址
            Integer firstInfoAddress = asdu.getMessageInfoList().get(0).getInfoAddress();
            bytes.write(Iec104ByteUtil.infoAddressToByteArray(firstInfoAddress));

            for (MessageInfo info : asdu.getMessageInfoList()) {

                // 信息对象值
                if (info.getInfoValue() != null) {
                    bytes.write(info.getInfoValue());
                }

                // 品质描述符
                if (info.getQualifier() != null) {
                    bytes.write(info.getQualifier().getValue());
                }

                // 时标
                if (info.getTimeScale() != null) {
                    bytes.write(ByteUtil.dateToCP56TimeByteArray(info.getTimeScale()));
                }
            }
        }
        // 信息数据列表(不连续)
        else {
            for (MessageInfo info : asdu.getMessageInfoList()) {

                // 信息对象地址
                bytes.write(Iec104ByteUtil.infoAddressToByteArray(info.getInfoAddress()));

                // 信息对象值
                if (info.getInfoValue() != null) {
                    bytes.write(info.getInfoValue());
                }

                // 品质描述符
                if (info.getQualifier() != null) {
                    bytes.write(info.getQualifier().getValue());
                }

                // 时标
                if (info.getTimeScale() != null) {
                    bytes.write(ByteUtil.dateToCP56TimeByteArray(info.getTimeScale()));
                }
            }
        }

        return bytes.toByteArray();
    }

}
