package com.dataMall.searchCenter.feign;

import com.dataMall.common.vo.BlogVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient(value = "blog-center",path = "/blog-article")
public interface BlogService {
    // 获取博客列表,feign调用
    @GetMapping("/getBlogList")
    List<BlogVO> getBlogListAll();

    @PostMapping("/getBlogListByIds")
    List<BlogVO> getBlogListByIds(@RequestBody List<Integer> blogIds);
}
