package com.baoyubo.iec104.util;

import com.baoyubo.iec104.model.MessageVSQ;

/**
 * IEC104协议 字节工具类
 *
 * @author yubo.bao
 * @date 2023/7/19 17:56
 */
public final class Iec104ByteUtil {


    /**
     * 将 I帧控制域 转换为 字节数组（占4个字节，低位在前）
     *
     * @param sendSequenceNum    发送序号
     * @param receiveSequenceNum 接收序号
     * @return byte[]
     */
    public static byte[] iControlToByteArray(int sendSequenceNum, int receiveSequenceNum) {
        byte[] control = new byte[4];
        sendSequenceNum = sendSequenceNum << 1;
        control[0] = (byte) (sendSequenceNum & 0xFF);
        control[1] = (byte) ((sendSequenceNum >> 8) & 0xFF);
        receiveSequenceNum = receiveSequenceNum << 1;
        control[2] = (byte) (receiveSequenceNum & 0xFF);
        control[3] = (byte) ((receiveSequenceNum >> 8) & 0xFF);
        return control;
    }


    /**
     * 将 S帧控制域 转换为 字节数组 （占4个字节，低位在前）
     *
     * @param receiveSequenceNum 接收序号
     * @return byte[]
     */
    public static byte[] sControlToByteArray(int receiveSequenceNum) {
        byte[] control = new byte[4];
        control[0] = (byte) (1);
        control[1] = (byte) (0);
        receiveSequenceNum = receiveSequenceNum << 1;
        control[2] = (byte) (receiveSequenceNum & 0xFF);
        control[3] = (byte) ((receiveSequenceNum >> 8) & 0xFF);
        return control;
    }


    /**
     * 将 控制域字节数组 转换为 控制域发送序列号 （占4个字节中的前2个字节，低位在前）
     *
     * @param bytes byte[]
     * @return 发送序号
     */
    public static short controlByteArrayToSendSequenceNum(byte[] bytes) {
        int sendSequenceNum = ((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF);
        return (short) (sendSequenceNum >> 1);
    }


    /**
     * 将 控制域字节数组 转换为 控制域接收序列号 （占4个字节中的后2个字节，低位在前）
     *
     * @param bytes byte[]
     * @return 接收序号
     */
    public static short controlByteArrayToReceiveSequenceNum(byte[] bytes) {
        int receiveSequenceNum = ((bytes[3] & 0xFF) << 8) | (bytes[2] & 0xFF);
        return (short) (receiveSequenceNum >> 1);
    }


    /**
     * 将 可变结构限定词VSQ 转换为 字节
     *
     * @param vsq vsq
     * @return byte
     */
    public static byte vsqToByteArray(MessageVSQ vsq) {

        // SQ 在比特数组D7位置
        // SQ = 0 表示不连续； SQ = 1 表示连续
        int sq = vsq.getIsContinuous() ? 0x80 : 0;
        short listSize = vsq.getMessageInfoListSize();
        return (byte) (sq | listSize);
    }


    /**
     * 将 字节 转换为 可变结构限定词VSQ
     *
     * @param b 字节
     * @return MessageVSQ
     */
    public static MessageVSQ byteArrayToVsq(byte b) {
        boolean isContinuous = (b & 0x80) != 0;
        short listSize = (short) (b & 0x7F);
        return new MessageVSQ(isContinuous, listSize);
    }


    /**
     * 将 传输原因 转换为 字节数组 （占2个字节，低位在前）
     *
     * @param reason 传输原因
     * @return byte[]
     */
    public static byte[] reasonToByteArray(short reason) {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) ((reason >> 8) & 0xff);
        bytes[0] = (byte) (reason & 0xff);
        return bytes;
    }


    /**
     * 将 字节数组 转换为 传输原因 （占2个字节，低位在前）
     *
     * @param bytes byte[]
     * @return 传输原因
     */
    public static short byteArrayToReason(byte[] bytes) {
        int result = 0;
        result = (result | bytes[1]) << 8;
        result = result | bytes[0];
        return (short) result;
    }


    /**
     * 将 公共地址 转换为 字节数组 （占2个字节，低位在前）
     *
     * @param commonAddress 公共地址
     * @return byte[]
     */
    public static byte[] commonAddressToByteArrays(short commonAddress) {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) ((commonAddress >> 8) & 0xff);
        bytes[0] = (byte) (commonAddress & 0xff);
        return bytes;
    }


    /**
     * 将 字节数组 转换为 公共地址 （占2个字节，低位在前）
     *
     * @param bytes byte[]
     * @return 公共地址
     */
    public static short byteArrayToCommonAddress(byte[] bytes) {
        return (short) (((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF));
    }


    /**
     * 将 信息对象地址 转换为 字节数组 （占3个字节，低位在前）
     *
     * @param infoAddress 信息对象地址
     * @return byte[]
     */
    public static byte[] infoAddressToByteArray(int infoAddress) {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) (infoAddress & 0xFF);
        bytes[1] = (byte) ((infoAddress >> 8) & 0xFF);
        bytes[2] = (byte) ((infoAddress >> 16) & 0xFF);
        return bytes;
    }


    /**
     * 将 字节数组 转换为 信息对象地址 （占3个字节，低位在前）
     *
     * @param bytes byte[]
     * @return 信息对象地址
     */
    public static int byteArrayToInfoAddress(byte[] bytes) {
        return (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16);
    }


    /**
     * 构建 单命令遥控信息 SCO
     *
     * @param se  遥控选择标志: 0-执行, 1-选择
     * @param qu  遥控输出方式: 0-无定义, 1-短脉冲, 2-长脉冲, 3-持续脉冲
     * @param scs 遥控状态说明: 0-控分, 1-控合
     * @return SCO
     */
    public static byte buildRemoteControlValueSCO(int se, int qu, int scs) {
        byte sco = 0;
        sco |= ((byte) se & 0x01) << 7;
        sco |= ((byte) qu & 0x1F) << 2;
        sco |= ((byte) scs & 0x01);
        return sco;
    }


    /**
     * 解析 单命令遥控信息 SCO
     *
     * @param sco SCO
     * @return [se, qu, scs]
     */
    public static int[] parseRemoteControlValueSCO(byte sco) {
        int se = (sco >> 7) & 0x01;
        int qu = (sco >> 2) & 0x1F;
        int scs = sco & 0x01;
        return new int[]{se, qu, scs};
    }


    /**
     * 构建 双命令遥控信息 DCO
     *
     * @param se  遥控选择标志: 0-执行, 1-选择
     * @param qu  遥控输出方式: 0-无定义, 1-短脉冲, 2-长脉冲, 3-持续脉冲
     * @param scs 遥控状态说明: 0-非法, 1-控分, 2-控合, 3-非法
     * @return DCO
     */
    public static byte buildRemoteControlValueDCO(int se, int qu, int scs) {
        byte dco = 0;
        dco |= ((byte) se & 0x01) << 7;
        dco |= ((byte) qu & 0x1F) << 2;
        dco |= ((byte) scs & 0x03);
        return dco;
    }


    /**
     * 解析 双命令遥控信息 DCO
     *
     * @param dco DCO
     * @return [se, qu, scs]
     */
    public static int[] parseRemoteControlValueDCO(byte dco) {
        int se = (dco >> 7) & 0x01;
        int qu = (dco >> 2) & 0x1F;
        int scs = dco & 0x03;
        return new int[]{se, qu, scs};
    }


    private Iec104ByteUtil() {
    }
}
