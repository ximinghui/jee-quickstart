package org.example.module.system.dal.mysql.mail;

import org.example.framework.common.pojo.PageResult;
import org.example.framework.mybatis.core.mapper.BaseMapperX;
import org.example.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.example.module.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import org.example.module.system.dal.dataobject.mail.MailAccountDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailAccountMapper extends BaseMapperX<MailAccountDO> {

    default PageResult<MailAccountDO> selectPage(MailAccountPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MailAccountDO>()
                .likeIfPresent(MailAccountDO::getMail, pageReqVO.getMail())
                .likeIfPresent(MailAccountDO::getUsername, pageReqVO.getUsername()));
    }

}
