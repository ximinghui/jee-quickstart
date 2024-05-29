package org.example.module.system.api.logger.dto;

import org.example.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志分页 Request DTO
 *
 * @author HUIHUI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperateLogPageReqDTO extends PageParam {

    /**
     * 模块类型
     */
    private String type;
    /**
     * 模块数据编号
     */
    private Long bizId;

    /**
     * 用户编号
     */
    private Long userId;

}
