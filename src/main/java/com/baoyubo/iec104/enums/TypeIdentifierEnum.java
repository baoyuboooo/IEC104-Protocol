package com.baoyubo.iec104.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * IEC104协议 类型标识符TI (占1个字节)
 *
 * @author yubo.bao
 * @date 2023/7/3 14:09
 */
public enum TypeIdentifierEnum {

    /**
     * 初始化结束
     */
    INIT_END(70, 0x46, 1),

    /**
     * 召唤命令
     */
    GENERAL_CALL(100, 0x64, 0),

    /**
     * 时钟同步
     */
    TIME_SYNCHRONIZATION(103, 0x67, 0),

    /**
     * 单点遥信
     */
    ONE_POINT_HARUNOBU(1, 0x01, 1),

    /**
     * 单点遥信带时标
     */
    ONE_POINT_TIME_HARUNOBU(30, 0x1E, 1),

    /**
     * 双点遥信
     */
    TWO_POINT_HARUNOBU(3, 0x03, 1),

    /**
     * 双点遥信带时标
     */
    TWO_POINT_TIME_HARUNOBU(31, 0x1F, 1),

    /**
     * 测量值 归一化值 遥测
     */
    NORMALIZED_TELEMETRY(9, 0x09, 2),

    /**
     * 测量值 标度化值 遥测
     */
    SCALED_TELEMETRY(11, 0x0B, 2),

    /**
     * 测量值 短浮点数 遥测
     */
    SHORT_FLOAT_POINT_TELEMETRY(13, 0x0D, 4),

    /**
     * 单命令 遥控
     */
    ONE_POINT_REMOTE_CONTROL(45, 0x2D, 1),

    /**
     * 双命令 遥控
     */
    TWO_POINT_REMOTE_CONTROL(46, 0x2E, 1),
    ;

    @Getter
    private final byte value;

    @Getter
    private final byte valueHex;

    @Getter
    private final int messageInfoValueLength;

    /**
     * TypeIdentifierEnum
     *
     * @param value                  十进制数值
     * @param valueHex               十六进制数值
     * @param messageInfoValueLength 信息数据值所占用字节长度
     */
    TypeIdentifierEnum(int value, int valueHex, int messageInfoValueLength) {
        this.value = (byte) value;
        this.valueHex = (byte) valueHex;
        this.messageInfoValueLength = messageInfoValueLength;
    }


    private static final Map<Byte, TypeIdentifierEnum> VALUE_MAP = new HashMap<>();

    static {
        for (TypeIdentifierEnum qualifiersEnum : values()) {
            VALUE_MAP.put(qualifiersEnum.getValue(), qualifiersEnum);
        }
    }


    /**
     * 通过值获取对应的 类型标识符TI枚举
     *
     * @param value 值
     * @return 类型标识符TI枚举
     */
    public static TypeIdentifierEnum ofValue(byte value) {
        return VALUE_MAP.get(value);
    }


    /**
     * 判断是否为遥测
     *
     * @param typeIdentifier 类型标识
     * @return boolean
     */
    public static boolean isTelemetry(TypeIdentifierEnum typeIdentifier) {
        return TypeIdentifierEnum.NORMALIZED_TELEMETRY == typeIdentifier
                || TypeIdentifierEnum.SCALED_TELEMETRY == typeIdentifier
                || TypeIdentifierEnum.SHORT_FLOAT_POINT_TELEMETRY == typeIdentifier;
    }


    /**
     * 判断是否为遥信
     *
     * @param typeIdentifier 类型标识
     * @return boolean
     */
    public static boolean isHarunobu(TypeIdentifierEnum typeIdentifier) {
        return TypeIdentifierEnum.ONE_POINT_HARUNOBU == typeIdentifier
                || TypeIdentifierEnum.ONE_POINT_TIME_HARUNOBU == typeIdentifier
                || TypeIdentifierEnum.TWO_POINT_HARUNOBU == typeIdentifier
                || TypeIdentifierEnum.TWO_POINT_TIME_HARUNOBU == typeIdentifier;
    }


    /**
     * 判断是否为遥控
     *
     * @param typeIdentifier 类型标识符TI枚举
     * @return boolean
     */
    public static boolean isRemoteControl(TypeIdentifierEnum typeIdentifier) {
        return TypeIdentifierEnum.ONE_POINT_REMOTE_CONTROL == typeIdentifier
                || TypeIdentifierEnum.TWO_POINT_REMOTE_CONTROL == typeIdentifier;
    }


    /**
     * MessageInfo 是否包含 信息对象值 字段
     *
     * @param typeIdentifier 类型标识符TI枚举
     * @return boolean
     */
    public static boolean hasMessageInfoValue(TypeIdentifierEnum typeIdentifier) {
        return typeIdentifier.messageInfoValueLength > 0;
    }


    /**
     * MessageInfo 是否包含 品质描述符 字段
     *
     * @param typeIdentifier 类型标识符TI枚举
     * @return boolean
     */
    public static boolean hasMessageInfoQualifier(TypeIdentifierEnum typeIdentifier) {
        return TypeIdentifierEnum.GENERAL_CALL == typeIdentifier
                || isTelemetry(typeIdentifier);
    }


    /**
     * MessageInfo 是否包含 时标 字段
     *
     * @param typeIdentifier 类型标识符TI枚举
     * @return boolean
     */
    public static boolean hasMessageInfoTimeScale(TypeIdentifierEnum typeIdentifier) {
        return TypeIdentifierEnum.TIME_SYNCHRONIZATION == typeIdentifier
                || TypeIdentifierEnum.ONE_POINT_TIME_HARUNOBU == typeIdentifier
                || TypeIdentifierEnum.TWO_POINT_TIME_HARUNOBU == typeIdentifier;
    }

}
