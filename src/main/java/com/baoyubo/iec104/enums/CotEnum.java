package com.baoyubo.iec104.enums;

import lombok.Getter;

/**
 * IEC104协议 传送原因枚举 (Cause Of Transmission, COT, 占2个字节) 只展示常用的
 *
 * @author haowei.chu
 * @date 2025年01月03日 14:27
 */
@Getter
public enum CotEnum {
    NO_USE(0x00, "未用"),
    /**
     * periodic, cyclic 周期、循环
     */
    PERIODIC(0x01, "周期、循环"),
    /**
     * background interrogation
     */
    BACK(0x02, "背景扫描"),
    /**
     * spontaneous 自发
     */
    SPONT(0x03, "突发(自发)"),
    /**
     * initialized 初始化完成
     */
    INIT(0x04, "初始化完成"),
    /**
     * interrogation or interrogated 请求或者被请求
     */
    REQ(0x05, "请求或者被请求"),
    /**
     * activation 激活，用于下发控制方向的遥控、参数设置
     */
    ACT(0x06, "激活"),
    /**
     * confirmation activation 激活确认，响应控制激活命令
     */
    ACTCON(0x07, "激活确认"),
    /**
     * deactivation 停止激活
     */
    DEACT(0x08, "停止激活"),
    /**
     * confirmation deactivation 停止激活确认
     */
    DEACTCON(0x09, "停止激活确认"),
    /**
     * termination activation 激活终止，用于结束控制流程，终止激活状态
     */
    ACTTERM(0x0a, "激活终止"),
    FILE_TRANS(0x0d, "文件传输"),
    /**
     * interrogated by general interrogation 响应召唤命令
     */
    INTROGEN(0x14, "响应站召唤");

    private final short code;
    private final String name;

    CotEnum(int code, String name) {
        this.code = (short) code;
        this.name = name;
    }
}
