package com.baoyubo.iec104.model;

import lombok.Data;

/**
 * IEC104协议消息 可变结构限定词VSQ (占1个字节)
 *
 * @author yubo.bao
 * @date 2023/7/3 13:34
 */
@Data
public class MessageVSQ {

    /**
     * 信息数据列表中地址是否为连续的
     */
    private Boolean isContinuous;

    /**
     * 信息数据列表个数
     */
    private Short messageInfoListSize;


    public MessageVSQ() {
    }

    public MessageVSQ(boolean isContinuous, int messageInfoListSize) {
        this.isContinuous = isContinuous;
        this.messageInfoListSize = (short) messageInfoListSize;
    }

}
