package com.dataMall.adminCenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.service.UserService;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.User;
import com.dataMall.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 账号表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
@RestController
@RequestMapping("/accounts")
public class UserController {
    private final String authPath = "accounts";
    @Resource
    private UserService userService;

    //todo:冻结账号

    //管理员分页查账号信息
    @PostMapping("/admin/query")
    @AdminAuth(value = authPath)
    public BaseResponse<IPage<User>> queryUserInfoPageByOption(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody User user) {
        String email = user.getEmail();
        String userName = user.getUsername();
        Integer id = user.getId();
        if (pageNum == null || pageSize == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.query(id, userName, email, pageNum, pageSize));
    }

    //管理员通过账号id查单个信息
    @GetMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public BaseResponse<User> findOne(@PathVariable Integer id) {
        User user = userService.getById(id);
        return ResultUtils.success(user);
    }

    //根据username查相似的username的list
    @GetMapping("/admin/getListByOption")
    public BaseResponse<List<User>> usernameLikeList(@RequestParam("username") String username) {
        QueryWrapper<User> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.like("username", username);
        return ResultUtils.success(userService.list(accountQueryWrapper));
    }
}

