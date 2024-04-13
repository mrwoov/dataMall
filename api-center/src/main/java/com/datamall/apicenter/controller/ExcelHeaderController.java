package com.datamall.apicenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.datamall.apicenter.entity.ExcelHeader;
import com.datamall.apicenter.service.ExcelHeaderService;
import com.datamall.apicenter.vo.ResultData;

import java.util.List;

/**
 * <p>
 *  前端控制器
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
public ResultData saveOrUpdate(@RequestBody ExcelHeader excelHeader){
        return ResultData.state(excelHeaderService.saveOrUpdate(excelHeader));
        }
//删除by id
@DeleteMapping("/{id}")
public Boolean delete(@PathVariable Integer id){
        return excelHeaderService.removeById(id);
        }
//批量删除
@PostMapping("/del_batch")
public Boolean deleteBatch(@RequestBody List<Integer> ids){
        return excelHeaderService.removeByIds(ids);
        }
//查找全部
@GetMapping
public List<ExcelHeader> findAll(){
        return excelHeaderService.list();
        }
//查找单个
@GetMapping("/{id}")
public List<ExcelHeader> findOne(@PathVariable Integer id){
        return excelHeaderService.list();
        }
//分页查询
@GetMapping("/page")
public Page<ExcelHeader> findPage(@RequestParam Integer pageNum,
@RequestParam Integer pageSize){
        return excelHeaderService.page(new Page<>(pageNum,pageSize));
        }
        }

