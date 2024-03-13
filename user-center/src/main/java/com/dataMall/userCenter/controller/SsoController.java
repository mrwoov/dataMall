package com.dataMall.userCenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.userCenter.entity.Sso;
import com.dataMall.userCenter.service.SsoService;
import com.dataMall.userCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author woov
 * @since 2024-03-02
 */
@RestController
@RequestMapping("/sso")
public class SsoController {
    @Resource
    private SsoService ssoService;

    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody Sso sso) {
        return ResultData.state(ssoService.saveOrUpdate(sso));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return ssoService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return ssoService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<Sso> findAll() {
        return ssoService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public List<Sso> findOne(@PathVariable Integer id) {
        return ssoService.list();
    }

    //分页查询
    @GetMapping("/page")
    public Page<Sso> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return ssoService.page(new Page<>(pageNum, pageSize));
    }
}

