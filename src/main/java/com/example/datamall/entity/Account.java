package com.example.datamall.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 账号表
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
@Getter
@Setter
@Accessors(chain = true)
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String passWord;

    /**
     * 邮箱
     */
    private String email;

    /**
     * token
     */
    private String token;

    /**
     * 用户状态（0正常1冻结2假删）
     */
    private Integer state;

    /**
     * 角色id
     */
    private Integer role;

    @TableField(exist = false)
    private String roleName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", userName='" + userName + '\'' + ", passWord='" + passWord + '\'' + ", email='" + email + '\'' + ", token='" + token + '\'' + ", state=" + state + ", role=" + role + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }
}
