package org.example.module.infra.controller.admin.demo.demo02;

import org.example.framework.apilog.core.annotation.ApiAccessLog;
import org.example.framework.common.pojo.CommonResult;
import org.example.framework.common.util.object.BeanUtils;
import org.example.framework.excel.core.util.ExcelUtils;
import org.example.module.infra.controller.admin.demo.demo02.vo.Demo02CategoryListReqVO;
import org.example.module.infra.controller.admin.demo.demo02.vo.Demo02CategoryRespVO;
import org.example.module.infra.controller.admin.demo.demo02.vo.Demo02CategorySaveReqVO;
import org.example.module.infra.dal.dataobject.demo.demo02.Demo02CategoryDO;
import org.example.module.infra.service.demo.demo02.Demo02CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.example.framework.apilog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 示例分类")
@RestController
@RequestMapping("/infra/demo02-category")
@Validated
public class Demo02CategoryController {

    @Resource
    private Demo02CategoryService demo02CategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建示例分类")
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:create')")
    public CommonResult<Long> createDemo02Category(@Valid @RequestBody Demo02CategorySaveReqVO createReqVO) {
        return CommonResult.success(demo02CategoryService.createDemo02Category(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新示例分类")
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:update')")
    public CommonResult<Boolean> updateDemo02Category(@Valid @RequestBody Demo02CategorySaveReqVO updateReqVO) {
        demo02CategoryService.updateDemo02Category(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除示例分类")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:delete')")
    public CommonResult<Boolean> deleteDemo02Category(@RequestParam("id") Long id) {
        demo02CategoryService.deleteDemo02Category(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得示例分类")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:query')")
    public CommonResult<Demo02CategoryRespVO> getDemo02Category(@RequestParam("id") Long id) {
        Demo02CategoryDO demo02Category = demo02CategoryService.getDemo02Category(id);
        return CommonResult.success(BeanUtils.toBean(demo02Category, Demo02CategoryRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得示例分类列表")
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:query')")
    public CommonResult<List<Demo02CategoryRespVO>> getDemo02CategoryList(@Valid Demo02CategoryListReqVO listReqVO) {
        List<Demo02CategoryDO> list = demo02CategoryService.getDemo02CategoryList(listReqVO);
        return CommonResult.success(BeanUtils.toBean(list, Demo02CategoryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出示例分类 Excel")
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDemo02CategoryExcel(@Valid Demo02CategoryListReqVO listReqVO,
                                          HttpServletResponse response) throws IOException {
        List<Demo02CategoryDO> list = demo02CategoryService.getDemo02CategoryList(listReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "示例分类.xls", "数据", Demo02CategoryRespVO.class,
                BeanUtils.toBean(list, Demo02CategoryRespVO.class));
    }

}
