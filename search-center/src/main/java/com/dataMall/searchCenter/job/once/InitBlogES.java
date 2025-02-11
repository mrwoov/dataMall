package com.dataMall.searchCenter.job.once;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dataMall.common.vo.BlogVO;
import com.dataMall.searchCenter.dto.BlogEsDTO;
import com.dataMall.searchCenter.esDao.BlogEsDao;
import com.dataMall.searchCenter.feign.BlogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class InitBlogES implements CommandLineRunner {
    @Resource
    private BlogService blogService;
    @Resource
    private BlogEsDao blogEsDao;


    @Override
    public void run(String... args) {
        List<BlogVO> blogList = blogService.getBlogListAll();
        if (CollectionUtils.isEmpty(blogList)) {
            return;
        }
        List<BlogEsDTO> blogEsDTOList = blogList.stream().map(BlogEsDTO::objToDto).toList();
        final int pageSize = 500;
        log.info("InitBlogEs start, total {}", blogEsDTOList.size());
        for (int i = 0; i < blogEsDTOList.size(); i += pageSize) {
            int end = Math.min(i + pageSize, blogEsDTOList.size());
            log.info("sync from {} to {}", i, end);
            blogEsDao.saveAll(blogEsDTOList.subList(i, end));
        }
        log.info("InitBlogEs end, total {}", blogEsDTOList.size());
    }
}
