package com.baoyubo.iec104.config;

import lombok.Data;

/**
 * 客户端配置
 *
 * @author yubo.bao
 * @date 2023/7/20 15:33
 */
@Data
public class ClientConfig {

    /**
     * 远程主机地址
     */
    private String remoteHost;

    /**
     * 远程端口
     */
    private int remotePort;

    /**
     * 客户端连接 测试帧 定时任务时间间隔t3 (单位秒，默认30s)
     */
    private int channelTestDuration = 30;

}
