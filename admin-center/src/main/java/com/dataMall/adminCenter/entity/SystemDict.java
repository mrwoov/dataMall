package com.dataMall.adminCenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author woov
 * @since 2023-10-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("system_dict")
public class SystemDict implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 名称
     */
    private String name;

    /**
     * 值
     */
    private String value;

    /**
     * 类型
     */
    private String type;


}
