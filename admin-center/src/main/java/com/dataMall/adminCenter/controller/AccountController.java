package com.dataMall.adminCenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.entity.Account;
import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
public class AccountController {
    private final String authPath = "accounts";
    @Resource
    private AccountService accountService;

    //管理员分页查账号信息
    @PostMapping("/admin/query")
    @AdminAuth(value = authPath)
    public ResultData queryUserInfoPageByOption(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody Account account) {
        String email = account.getEmail();
        String userName = account.getUsername();
        Integer id = account.getId();
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("缺少参数");
        }
        return ResultData.success(accountService.query(id, userName, email, pageNum, pageSize));
    }

    //管理员通过账号id查单个信息
    @GetMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public ResultData findOne(@PathVariable Integer id) {
        Account account = accountService.getById(id);
        return ResultData.success(account);
    }
    @GetMapping("/admin/getListByOption")
    public ResultData usernameLikeList(@RequestParam("username") String username) {
        QueryWrapper<Account> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.like("username", username);
        return ResultData.success(accountService.list(accountQueryWrapper));
    }
}

