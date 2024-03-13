package com.dataMall.userCenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.userCenter.entity.SsoType;
import com.dataMall.userCenter.service.SsoTypeService;
import com.dataMall.userCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author woov
 * @since 2024-03-02
 */
@RestController
@RequestMapping("/ssoType")
    public class SsoTypeController {
@Resource
private SsoTypeService ssoTypeService;

//新增或修改
@PatchMapping("/")
public ResultData saveOrUpdate(@RequestBody SsoType ssoType){
        return ResultData.state(ssoTypeService.saveOrUpdate(ssoType));
        }
//删除by id
@DeleteMapping("/{id}")
public Boolean delete(@PathVariable Integer id){
        return ssoTypeService.removeById(id);
        }
//批量删除
@PostMapping("/del_batch")
public Boolean deleteBatch(@RequestBody List<Integer> ids){
        return ssoTypeService.removeByIds(ids);
        }
//查找全部
@GetMapping
public List<SsoType> findAll(){
        return ssoTypeService.list();
        }
//查找单个
@GetMapping("/{id}")
public List<SsoType> findOne(@PathVariable Integer id){
        return ssoTypeService.list();
        }
//分页查询
@GetMapping("/page")
public Page<SsoType> findPage(@RequestParam Integer pageNum,
@RequestParam Integer pageSize){
        return ssoTypeService.page(new Page<>(pageNum,pageSize));
        }
        }

