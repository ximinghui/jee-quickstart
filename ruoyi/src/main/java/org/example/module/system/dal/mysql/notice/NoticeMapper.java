package org.example.module.system.dal.mysql.notice;

import org.example.framework.common.pojo.PageResult;
import org.example.framework.mybatis.core.mapper.BaseMapperX;
import org.example.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.example.module.system.controller.admin.notice.vo.NoticePageReqVO;
import org.example.module.system.dal.dataobject.notice.NoticeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapperX<NoticeDO> {

    default PageResult<NoticeDO> selectPage(NoticePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NoticeDO>()
                .likeIfPresent(NoticeDO::getTitle, reqVO.getTitle())
                .eqIfPresent(NoticeDO::getStatus, reqVO.getStatus())
                .orderByDesc(NoticeDO::getId));
    }

}
