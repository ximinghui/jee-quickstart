package org.example.module.system.service.mail;

import org.example.framework.common.pojo.PageResult;
import org.example.module.system.controller.admin.mail.vo.log.MailLogPageReqVO;
import org.example.module.system.dal.dataobject.mail.MailAccountDO;
import org.example.module.system.dal.dataobject.mail.MailLogDO;
import org.example.module.system.dal.dataobject.mail.MailTemplateDO;
import org.example.module.system.dal.mysql.mail.MailLogMapper;
import org.example.module.system.enums.mail.MailSendStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static cn.hutool.core.exceptions.ExceptionUtil.getRootCauseMessage;

/**
 * 邮件日志 Service 实现类
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Service
@Validated
public class MailLogServiceImpl implements MailLogService {

    @Resource
    private MailLogMapper mailLogMapper;

    @Override
    public PageResult<MailLogDO> getMailLogPage(MailLogPageReqVO pageVO) {
        return mailLogMapper.selectPage(pageVO);
    }

    @Override
    public MailLogDO getMailLog(Long id) {
        return mailLogMapper.selectById(id);
    }

    @Override
    public Long createMailLog(Long userId, Integer userType, String toMail,
                              MailAccountDO account, MailTemplateDO template,
                              String templateContent, Map<String, Object> templateParams, Boolean isSend) {
        MailLogDO.MailLogDOBuilder logDOBuilder = MailLogDO.builder();
        // 根据是否要发送，设置状态
        logDOBuilder.sendStatus(Objects.equals(isSend, true) ? MailSendStatusEnum.INIT.getStatus()
                        : MailSendStatusEnum.IGNORE.getStatus())
                // 用户信息
                .userId(userId).userType(userType).toMail(toMail)
                .accountId(account.getId()).fromMail(account.getMail())
                // 模板相关字段
                .templateId(template.getId()).templateCode(template.getCode()).templateNickname(template.getNickname())
                .templateTitle(template.getTitle()).templateContent(templateContent).templateParams(templateParams);

        // 插入数据库
        MailLogDO logDO = logDOBuilder.build();
        mailLogMapper.insert(logDO);
        return logDO.getId();
    }

    @Override
    public void updateMailSendResult(Long logId, String messageId, Exception exception) {
        // 1. 成功
        if (exception == null) {
            MailLogDO mailLogDO = new MailLogDO();
            mailLogDO.setId(logId);
            mailLogDO.setSendTime(LocalDateTime.now());
            mailLogDO.setSendStatus(MailSendStatusEnum.SUCCESS.getStatus());
            mailLogDO.setSendMessageId(messageId);
            mailLogMapper.updateById(mailLogDO);
            return;
        }
        // 2. 失败
        MailLogDO mailLogDO = new MailLogDO();
        mailLogDO.setId(logId);
        mailLogDO.setSendTime(LocalDateTime.now());
        mailLogDO.setSendStatus(MailSendStatusEnum.FAILURE.getStatus());
        mailLogDO.setSendException(getRootCauseMessage(exception));
        mailLogMapper.updateById(mailLogDO);

    }

}
