package com.baoyubo.iec104.model;

import com.baoyubo.iec104.enums.QualifiersEnum;
import lombok.Data;

import java.util.Date;

/**
 * IEC104协议消息 信息数据 (所占字节个数不固定)
 *
 * @author yubo.bao
 * @date 2023/7/3 13:34
 */
@Data
public class MessageInfo {

    /**
     * 信息对象地址 (占3个字节)
     */
    private Integer infoAddress;

    /**
     * 信息对象值 (所占字节个数不固定 1、2、4)
     */
    private byte[] infoValue;

    /**
     * 限定词/描述符 (占1个字节)
     */
    private QualifiersEnum qualifier;

    /**
     * 时标 (占7个字节)
     */
    private Date timeScale;

}
