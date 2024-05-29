package org.example.module.infra.controller.admin.job;

import org.example.framework.apilog.core.annotation.ApiAccessLog;
import org.example.framework.common.pojo.CommonResult;
import org.example.framework.common.pojo.PageParam;
import org.example.framework.common.pojo.PageResult;
import org.example.framework.common.util.object.BeanUtils;
import org.example.framework.excel.core.util.ExcelUtils;
import org.example.framework.quartz.core.util.CronUtils;
import org.example.module.infra.controller.admin.job.vo.job.JobPageReqVO;
import org.example.module.infra.controller.admin.job.vo.job.JobRespVO;
import org.example.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import org.example.module.infra.dal.dataobject.job.JobDO;
import org.example.module.infra.service.job.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.example.framework.apilog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 定时任务")
@RestController
@RequestMapping("/infra/job")
@Validated
public class JobController {

    @Resource
    private JobService jobService;

    @PostMapping("/create")
    @Operation(summary = "创建定时任务")
    @PreAuthorize("@ss.hasPermission('infra:job:create')")
    public CommonResult<Long> createJob(@Valid @RequestBody JobSaveReqVO createReqVO)
            throws SchedulerException {
        return CommonResult.success(jobService.createJob(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新定时任务")
    @PreAuthorize("@ss.hasPermission('infra:job:update')")
    public CommonResult<Boolean> updateJob(@Valid @RequestBody JobSaveReqVO updateReqVO)
            throws SchedulerException {
        jobService.updateJob(updateReqVO);
        return CommonResult.success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新定时任务的状态")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true, example = "1024"),
            @Parameter(name = "status", description = "状态", required = true, example = "1"),
    })
    @PreAuthorize("@ss.hasPermission('infra:job:update')")
    public CommonResult<Boolean> updateJobStatus(@RequestParam(value = "id") Long id, @RequestParam("status") Integer status)
            throws SchedulerException {
        jobService.updateJobStatus(id, status);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除定时任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:job:delete')")
    public CommonResult<Boolean> deleteJob(@RequestParam("id") Long id)
            throws SchedulerException {
        jobService.deleteJob(id);
        return CommonResult.success(true);
    }

    @PutMapping("/trigger")
    @Operation(summary = "触发定时任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:job:trigger')")
    public CommonResult<Boolean> triggerJob(@RequestParam("id") Long id) throws SchedulerException {
        jobService.triggerJob(id);
        return CommonResult.success(true);
    }

    @PostMapping("/sync")
    @Operation(summary = "同步定时任务")
    @PreAuthorize("@ss.hasPermission('infra:job:create')")
    public CommonResult<Boolean> syncJob() throws SchedulerException {
        jobService.syncJob();
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得定时任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<JobRespVO> getJob(@RequestParam("id") Long id) {
        JobDO job = jobService.getJob(id);
        return CommonResult.success(BeanUtils.toBean(job, JobRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得定时任务分页")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<PageResult<JobRespVO>> getJobPage(@Valid JobPageReqVO pageVO) {
        PageResult<JobDO> pageResult = jobService.getJobPage(pageVO);
        return CommonResult.success(BeanUtils.toBean(pageResult, JobRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出定时任务 Excel")
    @PreAuthorize("@ss.hasPermission('infra:job:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportJobExcel(@Valid JobPageReqVO exportReqVO,
                               HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<JobDO> list = jobService.getJobPage(exportReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "定时任务.xls", "数据", JobRespVO.class,
                BeanUtils.toBean(list, JobRespVO.class));
    }

    @GetMapping("/get_next_times")
    @Operation(summary = "获得定时任务的下 n 次执行时间")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true, example = "1024"),
            @Parameter(name = "count", description = "数量", example = "5")
    })
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<List<LocalDateTime>> getJobNextTimes(
            @RequestParam("id") Long id,
            @RequestParam(value = "count", required = false, defaultValue = "5") Integer count) {
        JobDO job = jobService.getJob(id);
        if (job == null) {
            return CommonResult.success(Collections.emptyList());
        }
        return CommonResult.success(CronUtils.getNextTimes(job.getCronExpression(), count));
    }

}
