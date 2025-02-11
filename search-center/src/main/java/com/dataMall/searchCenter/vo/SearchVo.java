package com.dataMall.searchCenter.vo;

import com.dataMall.common.entity.BlogArticle;
import com.dataMall.common.entity.ExcelApp;
import com.dataMall.common.entity.Goods;
import com.dataMall.common.vo.BlogVO;
import com.dataMall.searchCenter.dto.UserEsDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchVo {
    private String type;
    private Goods goods;
    private ExcelApp excelApp;
    private BlogVO blogVO;
    private UserEsDTO userEsDTO;
    private LocalDateTime updateTime;
    //暂无，保留字段
    private long thumbCount;
    private long viewCount;
}
