package com.dataMall.orderCenter.feign;

import com.dataMall.common.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(value = "user-center", path = "/accounts")
public interface UserService {
    @GetMapping("/tokenToUid")
    Integer tokenToUid(@RequestHeader("token") String token);

    @GetMapping("/getById/{id}")
    User getById(@PathVariable Integer id);

    @GetMapping("/getOneByOption")
    User getOneByOption(@RequestParam("column") String column, @RequestParam("value") String value);

    @GetMapping("getListByOption")
    List<User> usernameLikeList(@RequestParam("username") String username);
}
