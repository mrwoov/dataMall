package com.dataMall.userCenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2024-03-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sso_type")
public class SsoType implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String type;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 0正常1关闭
     */
    private Integer state;


}
