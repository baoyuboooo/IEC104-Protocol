package com.baoyubo.controller;

import com.baoyubo.business.ServerBiz;
import com.baoyubo.business.enums.RemoteOperateTypeEnum;
import com.baoyubo.business.model.RemoteOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务端 业务API
 *
 * @author yubo.bao
 * @date 2023/7/22 21:24
 */
@RestController
public class ServerController {

    @Autowired
    private ServerBiz serverBiz;

    /**
     * 启动服务端
     *
     * @param port 端口
     */
    @RequestMapping(value = "/iec104/server/start", method = RequestMethod.POST)
    public ResponseEntity<Object> start(
            @RequestParam(value = "port") Integer port
    ) {
        serverBiz.startServer(port);
        return ResponseEntity.ok().build();
    }


    /**
     * 关闭服务端
     */
    @RequestMapping(value = "/iec104/server/close", method = RequestMethod.POST)
    public ResponseEntity<Object> close() {
        serverBiz.closeServer();
        return ResponseEntity.ok().build();
    }


    /**
     * 推送远程操控数据
     *
     * @param remoteOperation 远程操控
     */
    @RequestMapping(value = "/iec104/server/push", method = RequestMethod.POST)
    public ResponseEntity<Object> push(@RequestBody RemoteOperation remoteOperation) {

        // 防止数据类型转换异常
        if (!CollectionUtils.isEmpty(remoteOperation.getParams())) {
            Map<Integer, Object> params = new HashMap<>();
            remoteOperation.getParams().forEach((k, v) -> {
                if (RemoteOperateTypeEnum.REMOTE_CONTROL == remoteOperation.getOperateType()
                        || RemoteOperateTypeEnum.GENERAL_CALL_HARUNOBU == remoteOperation.getOperateType()
                        || RemoteOperateTypeEnum.HARUNOBU == remoteOperation.getOperateType()
                ) {
                    params.put(k, ((Integer) v));
                } else {
                    params.put(k, ((Double) v).floatValue());
                }
            });
            remoteOperation.setParams(params);
        }

        serverBiz.pushData(remoteOperation);
        return ResponseEntity.ok().build();
    }

}
