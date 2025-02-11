package com.dataMall.searchCenter.vo;

import lombok.Data;

@Data
public class SearchRequestVo {
    private String keyword;
    private String type;
    private Integer uid;
    private String pageNum;
    private String pageSize;
}
