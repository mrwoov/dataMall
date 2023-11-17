package com.dataMall.orderCenter.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限角色对应表
 * </p>
 *
 * @author woov
 * @since 2023-06-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("role_to_auth")
public class RoleToAuth implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer roleId;

    private Integer authId;

    @TableField(exist = false)
    private String roleName;

    @TableField(exist = false)
    private String authName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
