package com.baoyubo.iec104.enums;

/**
 * IEC104协议 帧类型
 *
 * @author yubo.bao
 * @date 2023/7/3 14:09
 */
public enum FrameTypeEnum {

    /**
     * I 帧
     */
    I_FRAME,

    /**
     * U 帧
     */
    U_FRAME,

    /**
     * S 帧
     */
    S_FRAME;


    /**
     * 获取 帧类型
     *
     * @param controlBytes 控制域字节数组（占4个字节，低位在前）
     * @return FrameTypeEnum
     */
    public static FrameTypeEnum ofBytes(byte[] controlBytes) {

        if ((controlBytes[0] & 0b00000001) != 0 && (controlBytes[0] & 0b00000010) != 0 && (controlBytes[2] & 0b00000001) == 0) {
            return FrameTypeEnum.U_FRAME;
        }
        if ((controlBytes[0] & 0b00000001) != 0 && (controlBytes[2] & 0b00000001) == 0) {
            return FrameTypeEnum.S_FRAME;
        }
        if ((controlBytes[0] & 0b00000001) == 0 && (controlBytes[2] & 0b00000001) == 0) {
            return FrameTypeEnum.I_FRAME;
        }
        return null;
    }
}
