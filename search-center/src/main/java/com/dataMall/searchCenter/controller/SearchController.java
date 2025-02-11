package com.dataMall.searchCenter.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.searchCenter.service.SearchService;
import com.dataMall.searchCenter.vo.SearchRequestVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 搜索中心 前端控制器
 * </p>
 *
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/")
public class SearchController {

    @Resource
    private SearchService searchService;

    @PostMapping("/")
    public BaseResponse search(@RequestBody SearchRequestVo requestVo) {
        String keyword = requestVo.getKeyword();
        Integer uid = requestVo.getUid();
        String type = requestVo.getType();
        String pageStr = requestVo.getPageNum();
        String sizeStr = requestVo.getPageSize();
        if (StringUtils.isBlank(keyword) && uid == null) {
            return ResultUtils.error("关键词不能为空");
        }
        int page = 1;
        if (!StringUtils.isBlank(pageStr)) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                return ResultUtils.error("页码参数错误");
            }
        }
        int size = 10;
        if (!StringUtils.isBlank(sizeStr)) {
            try {
                size = Integer.parseInt(sizeStr);
            } catch (NumberFormatException e) {
                return ResultUtils.error("每页数量参数错误");
            }
        }
        if (StringUtils.isBlank(type)) {
            type = "all";
        }
        //type为空是综合搜索，不为空时，根据type搜索
        //type: goods(al), post,excel_api
        return switch (type) {
            case "all" -> ResultUtils.success(searchService.searchAll(keyword, uid, page, size));
            case "goods" -> ResultUtils.success(searchService.searchGoods(keyword, uid, page, size));
            case "user" -> ResultUtils.success(searchService.searchUser(keyword, page, size));
            case "blog" -> ResultUtils.success(searchService.searchBlog(keyword, uid, page, size));
            default -> ResultUtils.error("搜索类型错误");
        };
    }
}
