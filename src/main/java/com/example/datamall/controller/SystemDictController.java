package com.example.datamall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamall.entity.SystemDict;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.SystemDictService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-10-07
 */
@RestController
@RequestMapping("/systemDict")
public class SystemDictController {
    @Resource
    private SystemDictService systemDictService;
    @Resource
    private AccountService accountService;

    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestHeader("token") String token, @RequestBody SystemDict systemDict) {
        boolean isAdmin = accountService.checkAdminHavaAuth("/admin", token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(systemDictService.saveOrUpdate(systemDict));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public ResultData delete(@RequestHeader("token") String token, @PathVariable Integer id) {
        boolean isAdmin = accountService.checkAdminHavaAuth("/admin", token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(systemDictService.removeById(id));
    }

    //批量删除
    @PostMapping("/del_batch")
    public ResultData deleteBatch(@RequestHeader("token") String token, @RequestBody List<Integer> ids) {
        boolean isAdmin = accountService.checkAdminHavaAuth("/admin", token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(systemDictService.removeByIds(ids));
    }

    //查找全部
    @GetMapping("/")
    public ResultData findAll(@RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth("/admin", token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.success(systemDictService.list());
    }

    //查找单个
    @GetMapping("/{id}")
    public ResultData findOne(@PathVariable Integer id) {
        QueryWrapper<SystemDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return ResultData.success(systemDictService.list());
    }

    //分页查询
    @GetMapping("/page")
    public Page<SystemDict> findPage(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize) {
        return systemDictService.page(new Page<>(pageNum, pageSize));
    }
}

