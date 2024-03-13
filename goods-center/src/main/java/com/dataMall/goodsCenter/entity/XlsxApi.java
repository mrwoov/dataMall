package com.dataMall.goodsCenter.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author woov
 * @since 2024-01-24
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("xlsx_api")
public class XlsxApi implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    /**
     * mongodb集合唯一索引
     */
    private String apiId;

    private Integer accountId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    private Integer state;

    @TableField(exist = false)
    private MultipartFile file;


}
