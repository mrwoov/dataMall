package com.dataMall.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@TableName("excel_app")
public class ExcelApp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应用唯一标识
     */
    private String appId;

    /**
     * 应用名称
     */
    private String filename;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签
     */
    private String tags;

    /**
     * 展示图片
     */
    private String picIndex;

    private Integer sort;
    
    private Integer uid;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 0正常-1冻结
     */
    private Integer status;

}
