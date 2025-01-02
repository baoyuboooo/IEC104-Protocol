package com.baoyubo.iec104.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * IEC104协议 U帧类型 固定只有下面6种数据（占4个字节，低位在前）
 *
 * @author yubo.bao
 * @date 2023/7/3 14:29
 */
@Getter
public enum UControlEnum {

    /**
     * 启动命令
     */
    START(new byte[]{0b00000111, 0b00000000, 0b00000000, 0b00000000}),

    /**
     * 启动确认命令
     */
    START_REPLY(new byte[]{0b00001011, 0b00000000, 0b00000000, 0b00000000}),

    /**
     * 停止指令
     */
    STOP(new byte[]{0b00010011, 0b00000000, 0b00000000, 0b00000000}),

    /**
     * 停止确认
     */
    STOP_REPLY(new byte[]{0b00100011, 0b00000000, 0b00000000, 0b00000000}),

    /**
     * 测试命令
     */
    TEST(new byte[]{0b01000011, 0b00000000, 0b00000000, 0b00000000}),

    /**
     * 测试确认指令
     */
    TEST_REPLY(new byte[]{(byte) 0b10000011, 0b00000000, 0b00000000, 0b00000000}),
    ;


    private final byte[] controlBytes;

    UControlEnum(byte[] controlBytes) {
        this.controlBytes = controlBytes;
    }


    private static final Map<Byte, UControlEnum> VALUE_MAP = new HashMap<>();

    static {
        for (UControlEnum uControlEnum : values()) {
            VALUE_MAP.put(uControlEnum.getControlBytes()[0], uControlEnum);
        }
    }


    /**
     * 通过值获取对应的类型标识符TI枚举
     *
     * @param controlBytes 值
     * @return 类型标识符TI枚举
     */
    public static UControlEnum ofBytes(byte[] controlBytes) {
        return VALUE_MAP.get(controlBytes[0]);
    }

}
