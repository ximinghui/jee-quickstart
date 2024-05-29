package org.example.module.system.controller.admin.notify;

import org.example.framework.apilog.core.annotation.ApiAccessLog;
import org.example.framework.common.enums.UserTypeEnum;
import org.example.framework.common.pojo.CommonResult;
import org.example.framework.common.pojo.PageResult;
import org.example.framework.common.util.object.BeanUtils;
import org.example.module.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import org.example.module.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import org.example.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import org.example.module.system.dal.dataobject.notify.NotifyMessageDO;
import org.example.module.system.service.notify.NotifyMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.List;

import static org.example.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 我的站内信")
@RestController
@RequestMapping("/system/notify-message")
@Validated
public class NotifyMessageController {

    @Resource
    private NotifyMessageService notifyMessageService;

    // ========== 管理所有的站内信 ==========

    @GetMapping("/get")
    @Operation(summary = "获得站内信")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notify-message:query')")
    public CommonResult<NotifyMessageRespVO> getNotifyMessage(@RequestParam("id") Long id) {
        NotifyMessageDO message = notifyMessageService.getNotifyMessage(id);
        return CommonResult.success(BeanUtils.toBean(message, NotifyMessageRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得站内信分页")
    @PreAuthorize("@ss.hasPermission('system:notify-message:query')")
    public CommonResult<PageResult<NotifyMessageRespVO>> getNotifyMessagePage(@Valid NotifyMessagePageReqVO pageVO) {
        PageResult<NotifyMessageDO> pageResult = notifyMessageService.getNotifyMessagePage(pageVO);
        return CommonResult.success(BeanUtils.toBean(pageResult, NotifyMessageRespVO.class));
    }

    // ========== 查看自己的站内信 ==========

    @GetMapping("/my-page")
    @Operation(summary = "获得我的站内信分页")
    public CommonResult<PageResult<NotifyMessageRespVO>> getMyMyNotifyMessagePage(@Valid NotifyMessageMyPageReqVO pageVO) {
        PageResult<NotifyMessageDO> pageResult = notifyMessageService.getMyMyNotifyMessagePage(pageVO,
                getLoginUserId(), UserTypeEnum.ADMIN.getValue());
        return CommonResult.success(BeanUtils.toBean(pageResult, NotifyMessageRespVO.class));
    }

    @PutMapping("/update-read")
    @Operation(summary = "标记站内信为已读")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<Boolean> updateNotifyMessageRead(@RequestParam("ids") List<Long> ids) {
        notifyMessageService.updateNotifyMessageRead(ids, getLoginUserId(), UserTypeEnum.ADMIN.getValue());
        return CommonResult.success(Boolean.TRUE);
    }

    @PutMapping("/update-all-read")
    @Operation(summary = "标记所有站内信为已读")
    public CommonResult<Boolean> updateAllNotifyMessageRead() {
        notifyMessageService.updateAllNotifyMessageRead(getLoginUserId(), UserTypeEnum.ADMIN.getValue());
        return CommonResult.success(Boolean.TRUE);
    }

    @GetMapping("/get-unread-list")
    @Operation(summary = "获取当前用户的最新站内信列表，默认 10 条")
    @Parameter(name = "size", description = "10")
    public CommonResult<List<NotifyMessageRespVO>> getUnreadNotifyMessageList(
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<NotifyMessageDO> list = notifyMessageService.getUnreadNotifyMessageList(
                getLoginUserId(), UserTypeEnum.ADMIN.getValue(), size);
        return CommonResult.success(BeanUtils.toBean(list, NotifyMessageRespVO.class));
    }

    @GetMapping("/get-unread-count")
    @Operation(summary = "获得当前用户的未读站内信数量")
    @ApiAccessLog(enable = false) // 由于前端会不断轮询该接口，记录日志没有意义
    public CommonResult<Long> getUnreadNotifyMessageCount() {
        return CommonResult.success(notifyMessageService.getUnreadNotifyMessageCount(
                getLoginUserId(), UserTypeEnum.ADMIN.getValue()));
    }

}
