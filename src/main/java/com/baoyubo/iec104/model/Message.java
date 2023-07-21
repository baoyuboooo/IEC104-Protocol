package com.baoyubo.iec104.model;

import com.baoyubo.iec104.enums.FrameTypeEnum;
import lombok.Data;

/**
 * IEC104协议消息
 *
 * @author yubo.bao
 * @date 2023/7/3 13:34
 */
@Data
public class Message {

    /**
     * 帧类型（I帧、U帧、S帧）
     */
    private FrameTypeEnum frameType;

    /**
     * 控制域 (占4个字节) (CommonDataHandler自动填充)
     */
    private MessageControl control;

    /**
     * ASDU
     */
    private MessageASDU asdu;

}
