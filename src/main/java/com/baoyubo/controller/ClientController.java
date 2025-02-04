package com.baoyubo.controller;

import com.baoyubo.business.ClientBiz;
import com.baoyubo.business.enums.RemoteOperateTypeEnum;
import com.baoyubo.business.model.RemoteOperation;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端 业务API
 *
 * @author yubo.bao
 * @date 2023/7/22 21:24
 */
@RestController
public class ClientController {

    @Resource
    private ClientBiz clientBiz;

    /**
     * 启动客户端
     *
     * @param remoteHost 远程主机地址
     * @param remotePort 远程端口
     */
    @RequestMapping(value = "/iec104/client/start", method = RequestMethod.POST)
    public ResponseEntity<Object> start(
        @RequestParam(value = "remote_host") String remoteHost,
        @RequestParam(value = "remote_port") Integer remotePort
    ) {
        clientBiz.startClient(remoteHost, remotePort);
        return ResponseEntity.ok().build();
    }


    /**
     * 关闭客户端
     */
    @RequestMapping(value = "/iec104/client/close", method = RequestMethod.POST)
    public ResponseEntity<Object> close() {
        clientBiz.closeClient();
        return ResponseEntity.ok().build();
    }


    /**
     * 推送远程操控数据
     *
     * @param remoteOperation 远程操控
     */
    @RequestMapping(value = "/iec104/client/push", method = RequestMethod.POST)
    public ResponseEntity<Object> push(@RequestBody RemoteOperation remoteOperation) {

        // 防止数据类型转换异常
        if (!CollectionUtils.isEmpty(remoteOperation.getParams())) {
            Map<Integer, Object> params = new HashMap<>();
            remoteOperation.getParams().forEach((k, v) -> {
                if (RemoteOperateTypeEnum.REMOTE_CONTROL == remoteOperation.getOperateType()
                    || RemoteOperateTypeEnum.GENERAL_CALL_HARUNOBU == remoteOperation.getOperateType()
                    || RemoteOperateTypeEnum.HARUNOBU == remoteOperation.getOperateType()
                ) {
                    params.put(k, v);
                } else {
                    params.put(k, ((Double) v).floatValue());
                }
            });
            remoteOperation.setParams(params);
        }

        clientBiz.pushData(remoteOperation);
        return ResponseEntity.ok().build();
    }

}
