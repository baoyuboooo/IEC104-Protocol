package com.baoyubo.iec104.config;

import lombok.Data;

/**
 * 服务端配置
 *
 * @author yubo.bao
 * @date 2023/7/20 15:33
 */
@Data
public class ServerConfig {

    /**
     * 端口
     */
    private int port;

    /**
     * 客户端连接 测试帧 定时任务时间间隔t3 (单位秒，默认30s)
     */
    private int channelTestDuration = 30;

}
