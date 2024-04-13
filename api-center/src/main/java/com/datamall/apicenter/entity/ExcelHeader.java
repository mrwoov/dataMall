package com.datamall.apicenter.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author woov
 * @since 2024-04-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("excel_header")
public class ExcelHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private int excelApp;

    /**
     * excel原表头名称
     */
    private String headerName;

    private String unit;

    /**
     * excel表头类型
     */
    private String headerType;

    /**
     * 是否可查询
     */
    private Integer queryAble;
    /**
     * 排序
     */
    private Integer sort;

    @TableField(exist = false)
    private String appId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 0正常-1冻结
     */
    private Integer status;

    @TableField(exist = false)
    private List<String> dropdownList;

    @TableField(exist = false)
    private String value;

}
