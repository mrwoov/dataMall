package com.dataMall.apicenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.ExcelHeader;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.apicenter.service.ExcelHeaderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author woov
 * @since 2024-04-02
 */
@RestController
@RequestMapping("/excelHeader")
public class ExcelHeaderController {
    @Resource
    private ExcelHeaderService excelHeaderService;

    //新增或修改
    @PatchMapping("/")
    public BaseResponse<Object> saveOrUpdate(@RequestBody ExcelHeader excelHeader) {
        boolean state = excelHeaderService.saveOrUpdate(excelHeader);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return excelHeaderService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return excelHeaderService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<ExcelHeader> findAll() {
        return excelHeaderService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public List<ExcelHeader> findOne(@PathVariable Integer id) {
        return excelHeaderService.list();
    }

    //分页查询
    @GetMapping("/page")
    public Page<ExcelHeader> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return excelHeaderService.page(new Page<>(pageNum, pageSize));
    }
}

