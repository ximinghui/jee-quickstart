package org.example.module.system.controller.admin.logger;

import org.example.framework.apilog.core.annotation.ApiAccessLog;
import org.example.framework.common.pojo.CommonResult;
import org.example.framework.common.pojo.PageParam;
import org.example.framework.common.pojo.PageResult;
import org.example.framework.common.util.object.BeanUtils;
import org.example.framework.excel.core.util.ExcelUtils;
import org.example.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import org.example.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import org.example.module.system.dal.dataobject.logger.OperateLogDO;
import org.example.module.system.service.logger.OperateLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static org.example.framework.apilog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 操作日志")
@RestController
@RequestMapping("/system/operate-log")
@Validated
public class OperateLogController {

    @Resource
    private OperateLogService operateLogService;

    @GetMapping("/page")
    @Operation(summary = "查看操作日志分页列表")
    @PreAuthorize("@ss.hasPermission('system:operate-log:query')")
    public CommonResult<PageResult<OperateLogRespVO>> pageOperateLog(@Valid OperateLogPageReqVO pageReqVO) {
        PageResult<OperateLogDO> pageResult = operateLogService.getOperateLogPage(pageReqVO);
        return CommonResult.success(BeanUtils.toBean(pageResult, OperateLogRespVO.class));
    }

    @Operation(summary = "导出操作日志")
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:operate-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOperateLog(HttpServletResponse response, @Valid OperateLogPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OperateLogDO> list = operateLogService.getOperateLogPage(exportReqVO).getList();
        String filename = "操作日志.xls";
        String sheetName = "数据列表";
        List<OperateLogRespVO> OperateLogRespVOList = BeanUtils.toBean(list, OperateLogRespVO.class);
        ExcelUtils.write(response, filename, sheetName, OperateLogRespVO.class, OperateLogRespVOList);
    }

}
