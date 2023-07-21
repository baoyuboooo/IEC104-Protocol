package com.baoyubo.business.enums;

import lombok.Getter;

/**
 * 远程操控类型 (业务定义)
 *
 * @author yubo.bao
 * @date 2023/7/3 14:09
 */
public enum RemoteOperateTypeEnum {

    /**
     * 总召唤命令
     */
    GENERAL_CALL("general_call", "总召唤命令"),

    /**
     * 总召唤遥信数据
     */
    GENERAL_CALL_HARUNOBU_REPLY("general_call_harunobu", "总召唤遥信数据"),

    /**
     * 总召唤遥测数据
     */
    GENERAL_CALL_REPLY_TELEMETRY_REPLY("general_call_telemetry", "总召唤遥测数据"),

    /**
     * 总召唤结束
     */
    GENERAL_CALL_END("general_call_end", "总召唤结束"),

    /**
     * 遥控命令
     */
    REMOTE_CONTROL("remote_control", "遥控命令"),

    /**
     * 遥信数据
     */
    HARUNOBU("harunobu", "遥信数据"),

    /**
     * 遥测数据
     */
    TELEMETRY("telemetry", "遥测数据"),

    /**
     * 连接关闭
     */
    CLOSE("close", "连接关闭"),
    ;

    @Getter
    private final String type;

    @Getter
    private final String description;


    RemoteOperateTypeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }
}
