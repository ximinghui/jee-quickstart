package org.example.module.system.controller.admin.sms;

import org.example.framework.common.pojo.CommonResult;
import org.example.framework.common.pojo.PageResult;
import org.example.framework.common.util.object.BeanUtils;
import org.example.module.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import org.example.module.system.controller.admin.sms.vo.channel.SmsChannelRespVO;
import org.example.module.system.controller.admin.sms.vo.channel.SmsChannelSaveReqVO;
import org.example.module.system.controller.admin.sms.vo.channel.SmsChannelSimpleRespVO;
import org.example.module.system.dal.dataobject.sms.SmsChannelDO;
import org.example.module.system.service.sms.SmsChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.Comparator;
import java.util.List;

@Tag(name = "管理后台 - 短信渠道")
@RestController
@RequestMapping("system/sms-channel")
public class SmsChannelController {

    @Resource
    private SmsChannelService smsChannelService;

    @PostMapping("/create")
    @Operation(summary = "创建短信渠道")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:create')")
    public CommonResult<Long> createSmsChannel(@Valid @RequestBody SmsChannelSaveReqVO createReqVO) {
        return CommonResult.success(smsChannelService.createSmsChannel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新短信渠道")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:update')")
    public CommonResult<Boolean> updateSmsChannel(@Valid @RequestBody SmsChannelSaveReqVO updateReqVO) {
        smsChannelService.updateSmsChannel(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除短信渠道")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:sms-channel:delete')")
    public CommonResult<Boolean> deleteSmsChannel(@RequestParam("id") Long id) {
        smsChannelService.deleteSmsChannel(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得短信渠道")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:query')")
    public CommonResult<SmsChannelRespVO> getSmsChannel(@RequestParam("id") Long id) {
        SmsChannelDO channel = smsChannelService.getSmsChannel(id);
        return CommonResult.success(BeanUtils.toBean(channel, SmsChannelRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得短信渠道分页")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:query')")
    public CommonResult<PageResult<SmsChannelRespVO>> getSmsChannelPage(@Valid SmsChannelPageReqVO pageVO) {
        PageResult<SmsChannelDO> pageResult = smsChannelService.getSmsChannelPage(pageVO);
        return CommonResult.success(BeanUtils.toBean(pageResult, SmsChannelRespVO.class));
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "获得短信渠道精简列表", description = "包含被禁用的短信渠道")
    public CommonResult<List<SmsChannelSimpleRespVO>> getSimpleSmsChannelList() {
        List<SmsChannelDO> list = smsChannelService.getSmsChannelList();
        list.sort(Comparator.comparing(SmsChannelDO::getId));
        return CommonResult.success(BeanUtils.toBean(list, SmsChannelSimpleRespVO.class));
    }

}
