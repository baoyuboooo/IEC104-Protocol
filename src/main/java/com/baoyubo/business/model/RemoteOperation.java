package com.baoyubo.business.model;

import com.baoyubo.business.enums.RemoteOperateTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * 远程操控 (业务模型)
 *
 * @author yubo.bao
 * @date 2023/7/20 13:25
 */
@Data
public class RemoteOperation {

    /**
     * 远程操控 业务类型
     */
    private RemoteOperateTypeEnum operateType;

    /**
     * 远程操控 业务参数
     * <p>
     * 遥控: 参数值为整数类型, 占用1字节 (0-分， 1-合)
     * 遥信: 参数值为整数类型, 占用1字节 (0-分， 1-合)
     * 遥测: 参数值为浮点类型, 占用4字节
     */
    private Map<Integer, Object> params;

}
