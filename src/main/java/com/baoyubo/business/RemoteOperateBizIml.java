package com.baoyubo.business;

import com.baoyubo.business.model.RemoteOperation;
import com.baoyubo.iec104.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 远程操控 业务逻辑
 *
 * @author yubo.bao
 * @date 2023/7/20 12:59
 */
@Service
public class RemoteOperateBizIml implements RemoteOperateBiz {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteOperateBizIml.class);


    @Override
    public void sendToClient(RemoteOperation remoteOperation) {

    }

    @Override
    public void handleClientData(RemoteOperation remoteOperation) {
        LOGGER.info("[业务处理] 收到客户端上报数据，开始处理...  数据 = {}", JsonUtil.toJsonString(remoteOperation));
        // todo 业务自定义数据处理逻辑
    }

    @Override
    public void sendToServer(RemoteOperation remoteOperation) {

    }

    @Override
    public void handleServerData(RemoteOperation remoteOperation) {
        LOGGER.info("[业务处理] 收到服务端上报数据，开始处理...  数据 = {}", JsonUtil.toJsonString(remoteOperation));
        // todo 业务自定义数据处理逻辑
    }
}
