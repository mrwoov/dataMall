package com.dataMall.searchCenter.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.searchCenter.service.SearchService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 搜索中心 前端控制器
 * </p>
 *
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Resource
    private SearchService searchService;

    @GetMapping()
    public BaseResponse search(@RequestParam(value = "keyword") String keyword,
                               @RequestParam(value = "category_id", required = false) String categoryIdStr,
                               @RequestParam(value = "page", required = false) String pageStr,
                               @RequestParam(value = "size", required = false) String sizeStr,
                               @RequestParam(value = "type", required = false) String type) {
        if (StringUtils.isBlank(keyword)) {
            return ResultUtils.error("关键词不能为空");
        }
        int categoryId = -1;
        if (!StringUtils.isBlank(categoryIdStr)) {
            try {
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) {
                return ResultUtils.error("分类参数错误");
            }
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
            type = "goods";
        }

        //type: goods(al), post,excel_api
        return switch (type) {
            case "goods" -> ResultUtils.success(searchService.searchGoods(keyword, page, size, categoryId));
            case "post" -> ResultUtils.success();
            default -> ResultUtils.error("搜索类型错误");
        };
    }
}
