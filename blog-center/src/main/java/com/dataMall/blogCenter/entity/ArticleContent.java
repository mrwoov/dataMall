package com.dataMall.blogCenter.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "blog_article_contents")
public class ArticleContent {
    @Id
    private String id;

    @Indexed(unique = true)
    private String articleId;

    private String mdContent;
    private String htmlContent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
