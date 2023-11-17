package com.dataMall.adminCenter.controller;

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
    public ResultData queryUserInfoPageByOption(@RequestHeader("token") String token, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody Account account) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
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
    public ResultData findOne(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        Account account = accountService.getById(id);
        return ResultData.success(account);
    }
}

