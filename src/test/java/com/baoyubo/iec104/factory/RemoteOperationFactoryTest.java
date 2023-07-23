package com.baoyubo.iec104.factory;

import com.baoyubo.business.enums.RemoteOperateTypeEnum;
import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.model.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yubo.bao
 * @date 2023/7/24 11:18
 */
class RemoteOperationFactoryTest {

    @Test
    public void buildRemoteOperation() {
        Message receivedMessage = null;
        RemoteOperateTypeEnum operateTypeEnum = RemoteOperateTypeEnum.GENERAL_CALL;
        RemoteOperation res = RemoteOperationFactory.buildRemoteOperationByMessage(operateTypeEnum, receivedMessage);

        Assertions.assertEquals(operateTypeEnum, res.getOperateType());
    }

    @Test
    public void buildRemoteOperationHarunobu() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(100, (int) 1);
        Message receivedMessage = MessageFactory.buildServerHarunobuMessage(false, params);

        RemoteOperateTypeEnum operateTypeEnum = RemoteOperateTypeEnum.HARUNOBU;
        RemoteOperation res = RemoteOperationFactory.buildRemoteOperationByMessage(operateTypeEnum, receivedMessage);

        Assertions.assertEquals(operateTypeEnum, res.getOperateType());
        Assertions.assertEquals(params.toString(), res.getParams().toString());
    }

    @Test
    public void buildRemoteOperationTelemetry() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(100, (float) -3.14f);
        Message receivedMessage = MessageFactory.buildServerTelemetryMessage(false, params);

        RemoteOperateTypeEnum operateTypeEnum = RemoteOperateTypeEnum.TELEMETRY;
        RemoteOperation res = RemoteOperationFactory.buildRemoteOperationByMessage(operateTypeEnum, receivedMessage);

        Assertions.assertEquals(operateTypeEnum, res.getOperateType());
        Assertions.assertEquals(params.toString(), res.getParams().toString());
    }

    @Test
    public void buildRemoteOperationRemoteControl() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(100, (int) 1);
        Message receivedMessage = MessageFactory.buildClientRemoteControlSelectMessage(params);

        RemoteOperateTypeEnum operateTypeEnum = RemoteOperateTypeEnum.REMOTE_CONTROL;
        RemoteOperation res = RemoteOperationFactory.buildRemoteOperationByMessage(operateTypeEnum, receivedMessage);

        Assertions.assertEquals(operateTypeEnum, res.getOperateType());
        Assertions.assertEquals(params.toString(), res.getParams().toString());
    }

}