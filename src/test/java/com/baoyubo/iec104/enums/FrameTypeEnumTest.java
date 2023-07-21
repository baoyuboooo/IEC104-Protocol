package com.baoyubo.iec104.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author yubo.bao
 * @date 2023/7/21 15:20
 */
class FrameTypeEnumTest {

    @Test
    void ofBytes() {

        byte[] uFrameBytes = new byte[]{0b00000111, 0b00000000, 0b00000000, 0b00000000};
        FrameTypeEnum uFrameType = FrameTypeEnum.ofBytes(uFrameBytes);
        Assertions.assertEquals(FrameTypeEnum.U_FRAME, uFrameType);

        byte[] sFrameBytes = new byte[]{0b00000001, 0b00000000, 0b00000000, 0b00000000};
        FrameTypeEnum sFrameType = FrameTypeEnum.ofBytes(sFrameBytes);
        Assertions.assertEquals(FrameTypeEnum.S_FRAME, sFrameType);

        byte[] iFrameBytes = new byte[]{0b00000000, 0b00000000, 0b00000000, 0b00000000};
        FrameTypeEnum iFrameType = FrameTypeEnum.ofBytes(iFrameBytes);
        Assertions.assertEquals(FrameTypeEnum.I_FRAME, iFrameType);
    }
}