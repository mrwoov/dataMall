package com.dataMall.goodsCenter.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品数据文件
 * </p>
 *
 * @author woov
 * @since 2023-10-26
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("goods_file")
public class GoodsFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer accountId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件路径
     */
    private String filePath;

    private String md5;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 0正常-1冻结
     */
    private Integer status;

    public GoodsFile() {

    }

    public GoodsFile(Integer accountId, String filename, String filePath, String md5) {
        this.setAccountId(accountId);
        this.setFilename(filename);
        this.setFilePath(filePath);
        this.setMd5(md5);
    }

}
