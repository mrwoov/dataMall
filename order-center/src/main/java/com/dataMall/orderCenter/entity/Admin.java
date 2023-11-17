package com.dataMall.orderCenter.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
@Getter
@Setter
@Accessors(chain = true)
public class Admin implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号表id
     */
    private Integer accountId;

    /**
     * 角色
     */
    private Integer role;

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
     * 状态0正常1冻结
     */
    private Integer state;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String roleName;


}
