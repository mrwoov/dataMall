package com.dataMall.blogCenter.vo;

import com.dataMall.blogCenter.entity.BlogArticle;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogVO {
    /**
     * 文章ID
     */
    private Long id;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章描述
     */
    private String description;
    /**
     * 文章内容
     */
    private String contentMd;
    private String contentHtml;
    /**
     * 作者ID
     */
    private Integer authorId;
    /**
     * 分类ID
     */
    private Integer categoryId;

    private String categoryName;

    /**
     * 标签
     */
    private List<String> tagNames;

    private List<Integer> tagIds;
    /**
     * 状态：0-草稿 1-已发布 2-归档
     */
    private Integer state;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;

    public BlogArticle BlogVOToBlogArticle() {
        BlogArticle blogArticle = new BlogArticle();
        blogArticle.setId(this.id);
        blogArticle.setTitle(this.title);
        blogArticle.setDescription(this.description);
        blogArticle.setAuthorId(this.authorId);
        blogArticle.setCategoryId(this.categoryId);
        blogArticle.setState(this.state);
        blogArticle.setCoverImage(this.coverImage);
        return blogArticle;
    }
}
