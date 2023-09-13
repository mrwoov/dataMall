package com.example.datamall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 基础用户表
 * </p>
 *
 * @author woov
 * @since 2023-06-03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user_base")
public class UserBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String userName;

    private String passWord;

    private String email;

    private String token;

    private Integer state;

    private Integer role;

    @TableField(exist = false)
    private String roleName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "UserBase{" + "id=" + id + ", userName='" + userName + '\'' + ", passWord='" + passWord + '\'' + ", email='" + email + '\'' + ", token='" + token + '\'' + ", state=" + state + ", role=" + role + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }
}
