package com.dataMall.orderCenter.feign;

import com.dataMall.orderCenter.entity.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(value = "user-center",path = "/accounts")
public interface AccountService {
    @GetMapping("/tokenToUid")
    Integer tokenToUid(@RequestHeader("token") String token);

    @GetMapping("/getById/{id}")
    Account getById(@PathVariable Integer id);

    @GetMapping("/getOneByOption")
    Account getOneByOption(@RequestParam("column")String column, @RequestParam("value") String value);

    @GetMapping("getListByOption")
    List<Account> usernameLikeList(@RequestParam("username") String username);
}
