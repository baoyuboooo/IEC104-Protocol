package com.baoyubo.iec104.model;

import com.baoyubo.iec104.enums.UControlEnum;
import lombok.Data;

/**
 * IEC104协议消息 控制域 占4个字节
 *
 * @author yubo.bao
 * @date 2023/7/3 13:34
 */
@Data
public class MessageControl {

    /**
     * U帧 (占4个字节)
     */
    private UControlEnum uControl;

    /**
     * I帧 发送序列号 (占2个字节)
     */
    private Short sendSequenceNum;

    /**
     * I帧\S帧 接收序列号 (占2个字节)
     */
    private Short receiveSequenceNum;

}
