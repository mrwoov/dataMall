package com.datamall.apicenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.datamall.apicenter.entity.ExcelColDropdown;
import com.datamall.apicenter.service.ExcelColDropdownService;
import com.datamall.apicenter.vo.ResultData;

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
@RequestMapping("/excelColDropdown")
public class ExcelColDropdownController {
    @Resource
    private ExcelColDropdownService excelColDropdownService;

    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody ExcelColDropdown excelColDropdown) {
        return ResultData.state(excelColDropdownService.saveOrUpdate(excelColDropdown));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return excelColDropdownService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return excelColDropdownService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<ExcelColDropdown> findAll() {
        return excelColDropdownService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public List<ExcelColDropdown> findOne(@PathVariable Integer id) {
        return excelColDropdownService.list();
    }

    //分页查询
    @GetMapping("/page")
    public Page<ExcelColDropdown> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return excelColDropdownService.page(new Page<>(pageNum, pageSize));
    }
}

