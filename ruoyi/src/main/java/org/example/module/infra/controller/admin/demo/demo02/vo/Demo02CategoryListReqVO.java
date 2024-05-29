package org.example.module.infra.controller.admin.demo.demo02.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.framework.common.util.date.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 示例分类列表 Request VO")
@Data
public class Demo02CategoryListReqVO {

    @Schema(description = "名字", example = "芋艿")
    private String name;

    @Schema(description = "父级编号", example = "6080")
    private Long parentId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
