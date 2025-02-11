package com.dataMall.searchCenter.job.once;

import com.dataMall.common.entity.User;
import com.dataMall.searchCenter.dto.UserEsDTO;
import com.dataMall.searchCenter.esDao.UserEsDao;
import com.dataMall.searchCenter.feign.UserService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Component
public class InitUserES implements CommandLineRunner {
    @Resource
    private UserService userService;
    @Resource
    private UserEsDao userEsDao;
    
    @Override
    public void run(String... args) {
        List<User> userList = userService.getUserList();
        if (userList == null || userList.isEmpty()) {
            return;
        }
        List<UserEsDTO> userEsDTOList = userList.stream().map(UserEsDTO::objToDto).toList();
        final int pageSize = 500;
        log.info("InitBlogEs start, total {}", userEsDTOList.size());
        for (int i = 0; i < userEsDTOList.size(); i += pageSize) {
            int end = Math.min(i + pageSize, userEsDTOList.size());
            log.info("sync from {} to {}", i, end);
            userEsDao.saveAll(userEsDTOList.subList(i, end));
        }
        log.info("InitBlogEs end, total {}", userEsDTOList.size());
    }

}
