package org.example.module.system.api.logger;

import org.example.framework.common.pojo.PageResult;
import org.example.module.system.api.logger.dto.OperateLogCreateReqDTO;
import org.example.module.system.api.logger.dto.OperateLogPageReqDTO;
import org.example.module.system.api.logger.dto.OperateLogRespDTO;
import jakarta.validation.Valid;

/**
 * 操作日志 API 接口
 *
 * @author 芋道源码
 */
public interface OperateLogApi {

    /**
     * 创建操作日志
     *
     * @param createReqDTO 请求
     */
    void createOperateLog(@Valid OperateLogCreateReqDTO createReqDTO);

    /**
     * 获取指定模块的指定数据的操作日志分页
     *
     * @param pageReqVO 请求
     * @return 操作日志分页
     */
    PageResult<OperateLogRespDTO> getOperateLogPage(OperateLogPageReqDTO pageReqVO);

}
