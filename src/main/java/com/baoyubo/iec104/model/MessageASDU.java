package com.baoyubo.iec104.model;


import com.baoyubo.iec104.enums.TypeIdentifierEnum;
import lombok.Data;

import java.util.List;

/**
 * IEC104协议消息 ASDU
 *
 * @author yubo.bao
 * @date 2023/7/3 13:34
 */
@Data
public class MessageASDU {

    /**
     * 类型标识符TI (占1个字节)
     */
    private TypeIdentifierEnum typeIdentifier;

    /**
     * 可变结构限定词VSQ (占1个字节)
     */
    private MessageVSQ vsq;

    /**
     * 传输原因COT (占2个字节)
     */
    private Short transferReason;

    /**
     * 应用服务数据单元公共地址 (占2个字节)
     */
    private Short commonAddress;

    /**
     * 信息数据列表 (所占字节个数不固定)
     */
    private List<MessageInfo> messageInfoList;

}
