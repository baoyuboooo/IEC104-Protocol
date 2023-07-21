package com.baoyubo.iec104.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author yubo.bao
 * @date 2023/7/19 18:25
 */
class ByteUtilTest {

    @Test
    void intToByteArray() {
        int value = 9;
        byte[] bytes = ByteUtil.intToByteArray(value);
        Assertions.assertEquals("00001001 00000000 00000000 00000000", ByteUtil.toBinaryString(bytes));
    }

    @Test
    void byteArrayToInt() {
        int value = 9;
        byte[] bytes = ByteUtil.intToByteArray(value);
        int res = ByteUtil.byteArrayToInt(bytes);
        Assertions.assertEquals(value, res);
    }

    @Test
    void shortToByteArray() {
        short value = 9;
        byte[] bytes = ByteUtil.shortToByteArray(value);
        Assertions.assertEquals("00001001 00000000", ByteUtil.toBinaryString(bytes));
    }

    @Test
    void byteArrayToShort() {
        short value = 9;
        byte[] bytes = ByteUtil.shortToByteArray(value);
        int res = ByteUtil.byteArrayToShort(bytes);
        Assertions.assertEquals(value, res);
    }

    @Test
    void floatToByteArray() {
        float value1 = 3.14f;
        byte[] bytes1 = ByteUtil.floatToByteArray(value1);
        Assertions.assertEquals("11000011 11110101 01001000 01000000", ByteUtil.toBinaryString(bytes1));

        float value2 = -3.14f;
        byte[] bytes2 = ByteUtil.floatToByteArray(value2);
        Assertions.assertEquals("11000011 11110101 01001000 11000000", ByteUtil.toBinaryString(bytes2));
    }

    @Test
    void byteArrayToFloat() {
        float value1 = 3.14f;
        byte[] bytes1 = ByteUtil.floatToByteArray(value1);
        float res1 = ByteUtil.byteArrayToFloat(bytes1);
        Assertions.assertEquals(String.valueOf(value1), String.valueOf(res1));

        float value2 = -3.14f;
        byte[] bytes2 = ByteUtil.floatToByteArray(value2);
        float res2 = ByteUtil.byteArrayToFloat(bytes2);
        Assertions.assertEquals(String.valueOf(value2), String.valueOf(res2));
    }

    @Test
    void dateToCP56TimeByteArray() {

        // 2023-06-01 12:12:12
        Date date = new Date(1685592732000L);
        byte[] bytes = ByteUtil.dateToCP56TimeByteArray(date);

        Assertions.assertEquals("E0 2E 0C 0C 81 06 17", ByteUtil.toHexString(bytes));
        Assertions.assertEquals("11100000 00101110 00001100 00001100 10000001 00000110 00010111", ByteUtil.toBinaryString(bytes));
    }

    @Test
    void cp56TimeByteArrayToDate() {

        // 2023-06-01 12:12:12
        Date date = new Date(1685592732000L);
        byte[] bytes = ByteUtil.dateToCP56TimeByteArray(date);
        Date date2 = ByteUtil.cp56TimeByteArrayToDate(bytes);
        Assertions.assertEquals(date.getTime(), date2.getTime());
    }

    @Test
    void toHexString() {
        Assertions.assertEquals("FF", ByteUtil.toHexString((byte) 255));
        Assertions.assertEquals("FF 07", ByteUtil.toHexString(new byte[]{(byte) 255, (byte) 7}));
    }

    @Test
    void toBinaryString() {
        Assertions.assertEquals("11111111", ByteUtil.toBinaryString((byte) 255));
        Assertions.assertEquals("11111111 00000111", ByteUtil.toBinaryString(new byte[]{(byte) 255, (byte) 7}));
    }

}