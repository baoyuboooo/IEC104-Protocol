package com.baoyubo.iec104.constant;

import com.baoyubo.iec104.manager.ControlManager;
import com.baoyubo.iec104.manager.CacheManager;
import io.netty.util.AttributeKey;


/**
 * @author yubo.bao
 * @date 2023/7/20 15:28
 */
public class Constants {

    // 启动字符 固定头 占1个字节
    public static final byte HEADER = 0x68;


    // APDI 字段长度
    public static final int APCI_FIELD_LEN = Constants.HEADER_FIELD_LEN + Constants.APDU_LENGTH_FIELD_LEN + Constants.CONTROL_FIELD_LEN;
    // 固定头 字段长度
    public static final int HEADER_FIELD_LEN = 1;
    // APDU长度 字段长度
    public static final int APDU_LENGTH_FIELD_LEN = 1;
    // 控制域 字段长度
    public static final int CONTROL_FIELD_LEN = 4;
    // 信息数据-时标 字段长度
    public static final byte MESSAGE_INFO_TIME_SCALE_FIELD_LEN = 7;

    // 信息地址 0
    public static final int INFO_ADDRESS_0 = 0;
    // IEC104协议 默认的 公共地址
    public static final short DEFAULT_COMMON_ADDRESS = 0;


    // SE 遥控选择标志: 1-选择
    public static final int REMOTE_CONTROL_SE_SELECT = 1;
    // SE 遥控选择标志: 0-执行
    public static final int REMOTE_CONTROL_SE_EXECUTE = 0;


    // 控制域 最大发送序号、接收序列号
    public static final int SEQUENCE_NUM_MAX = 32767;
    // 控制域 最大发送序号、接收序列号
    public static final int SEQUENCE_NUM_MIN = 0;


    // 缓存容量
    public static final int CACHE_CAPACITY = 256;


    // Netty变量: 协议控制管理
    public static final AttributeKey<ControlManager> CONTROL_MANAGER = AttributeKey.newInstance("ControlManager");
    // Netty变量: 消息缓存管理
    public static final AttributeKey<CacheManager> CACHE_MANAGER = AttributeKey.newInstance("CacheManager");

}
