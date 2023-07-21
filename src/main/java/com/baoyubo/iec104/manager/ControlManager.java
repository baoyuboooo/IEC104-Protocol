package com.baoyubo.iec104.manager;

import com.baoyubo.iec104.constant.Constants;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * IEC104协议消息 消息控制管理
 *
 * @author yubo.bao
 * @date 2023/7/11 10:19
 */
public class ControlManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlManager.class);

    /**
     * 发送序列号
     */
    private final AtomicInteger sendSequenceNum = new AtomicInteger(Constants.SEQUENCE_NUM_MIN);

    /**
     * 接收序列号
     */
    private final AtomicInteger receiveSequenceNum = new AtomicInteger(Constants.SEQUENCE_NUM_MIN);

    /**
     * 名称
     */
    private String name;

    /**
     * 客户端链接句柄
     */
    @Setter
    @Getter
    private ChannelHandlerContext channelHandlerContext;


    /**
     * 构造函数
     *
     * @param name 名称
     */
    public ControlManager(String name) {
        this.name = name;
    }


    /**
     * 获取并自增 发送序列号 (达到最大值后重置)
     */
    public short getAndIncrementSendSequenceNum() {
        int currSendSequenceNum = this.sendSequenceNum.getAndIncrement();
        if (currSendSequenceNum <= Constants.SEQUENCE_NUM_MAX) {
            return (short) currSendSequenceNum;
        } else {
            this.sendSequenceNum.set(Constants.SEQUENCE_NUM_MIN);
            return (short) this.sendSequenceNum.getAndIncrement();
        }
    }

    /**
     * 获取 接收序列号 (达到最大值后重置)
     */
    public short getReceiveSequenceNum() {
        int currReceiveSequenceNum = this.receiveSequenceNum.get();
        if (currReceiveSequenceNum <= Constants.SEQUENCE_NUM_MAX) {
            return (short) currReceiveSequenceNum;
        } else {
            this.receiveSequenceNum.set(Constants.SEQUENCE_NUM_MIN);
            return (short) this.receiveSequenceNum.get();
        }
    }

    /**
     * 自增 接收序列号 (达到最大值后重置)
     */
    public void incrementReceiveSequenceNum() {
        int currReceiveSequenceNum = this.receiveSequenceNum.incrementAndGet();
        if (currReceiveSequenceNum > Constants.SEQUENCE_NUM_MAX) {
            this.receiveSequenceNum.set(Constants.SEQUENCE_NUM_MIN);
        }
    }

}
