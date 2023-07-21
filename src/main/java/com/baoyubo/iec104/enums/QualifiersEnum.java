package com.baoyubo.iec104.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * IEC104协议 限定词/描述符 (占1个字节)
 *
 * @author yubo.bao
 * @date 2023/7/3 15:08
 */
public enum QualifiersEnum {

    /**
     * 总召唤限定词
     */
    GENERAL_CALL_QUALIFIER(20, 0x14),

    /**
     * 遥测限定词
     */
    TELEMETRY_QUALIFIER(0, 0x00),
    ;


    @Getter
    private final byte value;

    @Getter
    private final byte valueHex;


    /**
     * QualifiersEnum
     *
     * @param value    十进制数值
     * @param valueHex 十六进制数值
     */
    QualifiersEnum(int value, int valueHex) {
        this.value = (byte) value;
        this.valueHex = (byte) valueHex;
    }


    private static final Map<Byte, QualifiersEnum> VALUE_MAP = new HashMap<>();

    static {
        for (QualifiersEnum qualifiersEnum : values()) {
            VALUE_MAP.put(qualifiersEnum.getValue(), qualifiersEnum);
        }
    }


    /**
     * 通过值获取对应的 限定词/描述符
     *
     * @param value 值
     * @return 限定词/描述符
     */
    public static QualifiersEnum ofValue(byte value) {
        return VALUE_MAP.get(value);
    }

}
