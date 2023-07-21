package com.baoyubo.iec104.factory;

import com.baoyubo.iec104.enums.FrameTypeEnum;
import com.baoyubo.iec104.handler.DataDecoder;
import com.baoyubo.iec104.handler.DataEncoder;
import com.baoyubo.iec104.manager.ControlManager;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.model.MessageControl;
import com.baoyubo.iec104.util.ByteUtil;
import com.baoyubo.iec104.util.JsonUtil;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yubo.bao
 * @date 2023/7/21 18:08
 */
class MessageFactoryTest {

    private final DataEncoder mockDataEncoder = new DataEncoder("Mock");
    private final DataDecoder mockDataDecoder = new DataDecoder("Mock");

    private final ControlManager mockControlManager = new ControlManager("Mock");

    private byte[] encode(Message message) {

        // 模拟 CommonDataHandler 自动更新控制域
        if (FrameTypeEnum.I_FRAME == message.getFrameType()) {
            MessageControl iControl = MessageFactory.commonBuildIControl(mockControlManager);
            message.setControl(iControl);
        }

        // 编码
        return ReflectionTestUtils.invokeMethod(mockDataEncoder, "encode", message);
    }


    private Message decode(byte[] bytes) {
        // 解码
        return ReflectionTestUtils.invokeMethod(mockDataDecoder, "decode", Unpooled.wrappedBuffer(bytes));
    }


    @Test
    void buildClientInitStartMessage() {
        Message message = MessageFactory.buildClientInitStartMessage();
        byte[] bytes = encode(message);
        Assertions.assertEquals("00000111 00000000 00000000 00000000", ByteUtil.toBinaryString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildServerInitStartReplyMessage() {
        Message message = MessageFactory.buildServerInitStartReplyMessage();
        byte[] bytes = encode(message);
        Assertions.assertEquals("00001011 00000000 00000000 00000000", ByteUtil.toBinaryString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildServerInitEndMessage() {
        Message message = MessageFactory.buildServerInitEndMessage();
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 46 01 04 00 00 00 00 00 00 00", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildClientGeneralCallMessage() {
        Message message = MessageFactory.buildClientGeneralCallMessage();
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 64 01 06 00 00 00 00 00 00 14", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildServerGeneralCallReplyMessage() {
        Message message = MessageFactory.buildServerGeneralCallReplyMessage();
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 64 01 07 00 00 00 00 00 00 14", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildServerCallHarunobuMessage() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(100, (int) 1);
        params.put(200, (int) 0);

        Message message = MessageFactory.buildServerCallHarunobuMessage(true, params);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 01 02 14 00 00 00 64 00 00 01 C8 00 00 00", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildServerTelemetryMessage() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(100, (float) 3.14);
        params.put(200, (float) -3.14);

        Message message = MessageFactory.buildServerTelemetryMessage(true, params);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 0D 02 14 00 00 00 64 00 00 C3 F5 48 40 00 C8 00 00 C3 F5 48 C0 00", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildServerGeneralCallEndMessage() {
        Message message = MessageFactory.buildServerGeneralCallEndMessage();
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 64 01 0A 00 00 00 00 00 00 14", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildClientTimeSyncMessage() {
        // 2023-06-01 12:12:12
        Date date = new Date(1685592732000L);
        Message message = MessageFactory.buildClientTimeSyncMessage(date);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 67 01 06 00 00 00 00 00 00 E0 2E 0C 0C 81 06 17", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildServerTimeSyncReplyMessage() {
        // 2023-06-01 12:12:12
        Date date = new Date(1685592732000L);
        Message message = MessageFactory.buildServerTimeSyncReplyMessage(date);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 67 01 07 00 00 00 00 00 00 E0 2E 0C 0C 81 06 17", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildClientTimeReadMessage() {
        // 2023-06-01 12:12:12
        Date date = new Date(1685592732000L);
        Message message = MessageFactory.buildClientTimeReadMessage(date);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 67 01 05 00 00 00 00 00 00 E0 2E 0C 0C 81 06 17", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildServerTimeReadReplyMessage() {
        // 2023-06-01 12:12:12
        Date date = new Date(1685592732000L);
        Message message = MessageFactory.buildServerTimeReadReplyMessage(date);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 67 01 05 00 00 00 00 00 00 E0 2E 0C 0C 81 06 17", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    public void buildClientRemoteControlSelectMessage() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(100, 1);
        params.put(200, 0);

        Message message = MessageFactory.buildClientRemoteControlSelectMessage(params);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 2E 02 06 00 00 00 64 00 00 82 C8 00 00 80", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    public void buildServerRemoteControlSelectReplyMessage() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(100, 1);
        params.put(200, 0);
        Message receivedMessage = MessageFactory.buildClientRemoteControlSelectMessage(params);

        Message message = MessageFactory.buildServerRemoteControlSelectReplyMessage(receivedMessage);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 2E 02 07 00 00 00 64 00 00 82 C8 00 00 80", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    public void buildClientRemoteControlExecuteMessage() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(100, 1);
        params.put(200, 0);
        Message receivedMessage = MessageFactory.buildClientRemoteControlSelectMessage(params);
        receivedMessage = MessageFactory.buildServerRemoteControlSelectReplyMessage(receivedMessage);

        Message message = MessageFactory.buildClientRemoteControlExecuteMessage(receivedMessage);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 2E 02 06 00 00 00 64 00 00 02 C8 00 00 00", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    public void buildServerRemoteControlExecuteReplyMessage() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(100, 1);
        params.put(200, 0);
        Message receivedMessage = MessageFactory.buildClientRemoteControlSelectMessage(params);
        receivedMessage = MessageFactory.buildServerRemoteControlSelectReplyMessage(receivedMessage);
        receivedMessage = MessageFactory.buildClientRemoteControlExecuteMessage(receivedMessage);

        Message message = MessageFactory.buildServerRemoteControlExecuteReplyMessage(receivedMessage);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 2E 02 07 00 00 00 64 00 00 02 C8 00 00 00", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    public void buildServerRemoteControlExecuteEndMessage() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(100, 1);
        params.put(200, 0);
        Message receivedMessage = MessageFactory.buildClientRemoteControlSelectMessage(params);
        receivedMessage = MessageFactory.buildServerRemoteControlSelectReplyMessage(receivedMessage);
        receivedMessage = MessageFactory.buildClientRemoteControlExecuteMessage(receivedMessage);
        receivedMessage = MessageFactory.buildServerRemoteControlExecuteReplyMessage(receivedMessage);

        Message message = MessageFactory.buildServerRemoteControlExecuteEndMessage(receivedMessage);
        byte[] bytes = encode(message);
        Assertions.assertEquals("00 00 00 00 2E 02 0A 00 00 00 64 00 00 02 C8 00 00 00", ByteUtil.toHexString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildTestMessage() {
        Message message = MessageFactory.buildTestMessage();
        byte[] bytes = encode(message);
        Assertions.assertEquals("01000011 00000000 00000000 00000000", ByteUtil.toBinaryString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }

    @Test
    void buildTestReplyMessage() {
        Message message = MessageFactory.buildTestReplyMessage();
        byte[] bytes = encode(message);
        Assertions.assertEquals("10000011 00000000 00000000 00000000", ByteUtil.toBinaryString(bytes));

        Message decodeMessage = decode(bytes);
        Assertions.assertEquals(JsonUtil.toJsonString(message), JsonUtil.toJsonString(decodeMessage));
    }
}