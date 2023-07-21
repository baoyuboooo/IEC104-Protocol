package com.baoyubo.iec104.util;

import com.baoyubo.iec104.model.MessageVSQ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author yubo.bao
 * @date 2023/7/19 19:07
 */
class Iec104ByteUtilTest {

    @Test
    void iControlToByteArray() {
        int sendSequenceNum = 128;
        int receiveSequenceNum = 3;
        byte[] bytes = Iec104ByteUtil.iControlToByteArray(sendSequenceNum, receiveSequenceNum);
        Assertions.assertEquals("00 01 06 00", ByteUtil.toHexString(bytes));
        Assertions.assertEquals("00000000 00000001 00000110 00000000", ByteUtil.toBinaryString(bytes));
    }

    @Test
    void sControlToByteArray() {
        int receiveSequenceNum = 128;
        byte[] bytes = Iec104ByteUtil.sControlToByteArray(receiveSequenceNum);
        Assertions.assertEquals("01 00 00 01", ByteUtil.toHexString(bytes));
        Assertions.assertEquals("00000001 00000000 00000000 00000001", ByteUtil.toBinaryString(bytes));
    }

    @Test
    void controlByteArrayToSendSequenceNum() {
        int sendSequenceNum = 128;
        int receiveSequenceNum = 3;
        byte[] bytes = Iec104ByteUtil.iControlToByteArray(sendSequenceNum, receiveSequenceNum);
        int res = Iec104ByteUtil.controlByteArrayToSendSequenceNum(bytes);
        Assertions.assertEquals(sendSequenceNum, res);
    }

    @Test
    void controlByteArrayToRecrudescenceNum() {
        int sendSequenceNum = 128;
        int receiveSequenceNum = 3;
        byte[] bytes = Iec104ByteUtil.iControlToByteArray(sendSequenceNum, receiveSequenceNum);
        int res = Iec104ByteUtil.controlByteArrayToReceiveSequenceNum(bytes);
        Assertions.assertEquals(receiveSequenceNum, res);
    }

    @Test
    void vsqToByteArray() {
        MessageVSQ vsq1 = new MessageVSQ(true, (short) 3);
        byte b1 = Iec104ByteUtil.vsqToByteArray(vsq1);
        Assertions.assertEquals("10000011", ByteUtil.toBinaryString(b1));

        MessageVSQ vsq2 = new MessageVSQ(false, (short) 3);
        byte b2 = Iec104ByteUtil.vsqToByteArray(vsq2);
        Assertions.assertEquals("00000011", ByteUtil.toBinaryString(b2));
    }

    @Test
    void byteArrayToVsq() {
        MessageVSQ vsq1 = new MessageVSQ(true, (short) 3);
        byte b1 = Iec104ByteUtil.vsqToByteArray(vsq1);
        MessageVSQ res1 = Iec104ByteUtil.byteArrayToVsq(b1);
        Assertions.assertEquals(true, res1.getIsContinuous());
        Assertions.assertEquals((short) 3, res1.getMessageInfoListSize());

        MessageVSQ vsq2 = new MessageVSQ(false, (short) 3);
        byte b2 = Iec104ByteUtil.vsqToByteArray(vsq2);
        MessageVSQ res2 = Iec104ByteUtil.byteArrayToVsq(b2);
        Assertions.assertEquals(false, res2.getIsContinuous());
        Assertions.assertEquals((short) 3, res2.getMessageInfoListSize());
    }

    @Test
    public void reasonToByteArray() {
        short reason = (short) 9;
        byte[] bytes = Iec104ByteUtil.reasonToByteArray(reason);
        Assertions.assertEquals("00001001 00000000", ByteUtil.toBinaryString(bytes));
    }

    @Test
    public void byteArrayToReason() {
        short reason = (short) 9;
        byte[] bytes = Iec104ByteUtil.reasonToByteArray(reason);
        short res = Iec104ByteUtil.byteArrayToReason(bytes);
        Assertions.assertEquals(reason, res);
    }

    @Test
    public void commonAddressToByteArrays() {
        short commonAddress = (short) 9;
        byte[] bytes = Iec104ByteUtil.commonAddressToByteArrays(commonAddress);
        Assertions.assertEquals("00001001 00000000", ByteUtil.toBinaryString(bytes));
    }

    @Test
    public void byteArrayToCommonAddress() {
        short commonAddress = (short) 9;
        byte[] bytes = Iec104ByteUtil.commonAddressToByteArrays(commonAddress);
        short res = Iec104ByteUtil.byteArrayToCommonAddress(bytes);
        Assertions.assertEquals(commonAddress, res);
    }

    @Test
    public void infoAddressToByteArray() {
        int infoAddress = 100;
        byte[] bytes = Iec104ByteUtil.infoAddressToByteArray(infoAddress);
        Assertions.assertEquals("01100100 00000000 00000000", ByteUtil.toBinaryString(bytes));
    }

    @Test
    public void byteArrayToInfoAddress() {
        int infoAddress = 100;
        byte[] bytes = Iec104ByteUtil.infoAddressToByteArray(infoAddress);
        int res = Iec104ByteUtil.byteArrayToInfoAddress(bytes);
        Assertions.assertEquals(infoAddress, res);
    }

    @Test
    public void buildRemoteControlValueSCO() {
        byte sco = Iec104ByteUtil.buildRemoteControlValueSCO(1, 0, 1);
        Assertions.assertEquals("10000001", ByteUtil.toBinaryString(sco));
    }

    @Test
    public void buildRemoteControlValueDCO() {
        byte dco = Iec104ByteUtil.buildRemoteControlValueDCO(1, 0, 1);
        Assertions.assertEquals("10000001", ByteUtil.toBinaryString(dco));
    }

    @Test
    public void getRemoteControlValueSE() {
        byte sco = Iec104ByteUtil.buildRemoteControlValueSCO(1, 0, 1);
        Assertions.assertEquals(1, Iec104ByteUtil.getRemoteControlValueSE(sco));

        byte dco = Iec104ByteUtil.buildRemoteControlValueDCO(1, 0, 1);
        Assertions.assertEquals(1, Iec104ByteUtil.getRemoteControlValueSE(dco));
    }

    @Test
    public void updateRemoteControlValueSE() {
        byte sco = Iec104ByteUtil.buildRemoteControlValueSCO(1, 0, 1);
        Assertions.assertEquals("10000001", ByteUtil.toBinaryString(sco));
        byte scoRes = Iec104ByteUtil.updateRemoteControlValueSE(sco, 0);
        Assertions.assertEquals("00000001", ByteUtil.toBinaryString(scoRes));

        byte dco = Iec104ByteUtil.buildRemoteControlValueDCO(1, 0, 1);
        Assertions.assertEquals("10000001", ByteUtil.toBinaryString(dco));
        byte dcoRes = Iec104ByteUtil.updateRemoteControlValueSE(dco, 0);
        Assertions.assertEquals("00000001", ByteUtil.toBinaryString(dcoRes));
    }


}