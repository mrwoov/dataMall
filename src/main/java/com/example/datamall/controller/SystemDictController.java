package com.example.datamall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
    private final String authPath = "/system";
    @Resource
    private SystemDictService systemDictService;
    @Resource
    private AccountService accountService;

    //分页查图标
    @PostMapping("/admin/icon_page")
    public ResultData iconPage(@RequestHeader("token") String token, @RequestParam Integer pageNum,
                               @RequestParam Integer pageSize, @RequestBody SystemDict systemDict) {
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("参数缺少");
        }
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        QueryWrapper<SystemDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "icon");
        if (systemDict.getName() != null) {
            queryWrapper.like("name", systemDict.getName());
        }
        if (systemDict.getValue() != null) {
            queryWrapper.like("value", systemDict.getValue());
        }
        IPage<SystemDict> page = systemDictService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return ResultData.success(page);
    }

    //新增或修改
    @PatchMapping("/admin")
    public ResultData saveOrUpdate(@RequestHeader("token") String token, @RequestBody SystemDict systemDict) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(systemDictService.saveOrUpdate(systemDict));
    }

    //删除by id
    @DeleteMapping("/admin/{id}")
    public ResultData delete(@RequestHeader("token") String token, @PathVariable Integer id) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(systemDictService.removeById(id));
    }

    //批量删除
    @PostMapping("/admin/del_batch")
    public ResultData deleteBatch(@RequestHeader("token") String token, @RequestBody List<Integer> ids) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(systemDictService.removeByIds(ids));
    }

    //查找全部
    @GetMapping("/")
    public ResultData findAll(@RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
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

