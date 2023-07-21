package com.baoyubo.iec104.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author yubo.bao
 * @date 2023/7/21 15:20
 */
class UControlEnumTest {

    @Test
    void ofBytes() {
        Assertions.assertEquals(UControlEnum.START, UControlEnum.ofBytes(UControlEnum.START.getControlBytes()));
        Assertions.assertEquals(UControlEnum.START_REPLY, UControlEnum.ofBytes(UControlEnum.START_REPLY.getControlBytes()));
        Assertions.assertEquals(UControlEnum.STOP, UControlEnum.ofBytes(UControlEnum.STOP.getControlBytes()));
        Assertions.assertEquals(UControlEnum.STOP_REPLY, UControlEnum.ofBytes(UControlEnum.STOP_REPLY.getControlBytes()));
        Assertions.assertEquals(UControlEnum.TEST, UControlEnum.ofBytes(UControlEnum.TEST.getControlBytes()));
        Assertions.assertEquals(UControlEnum.TEST_REPLY, UControlEnum.ofBytes(UControlEnum.TEST_REPLY.getControlBytes()));
    }

}