package com.dataMall.blogCenter.service.impl;


import com.dataMall.common.entity.ArticleContent;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class MarkdownService {
    private static final Parser MARKDOWN_PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();
    private static final PolicyFactory HTML_POLICY = new HtmlPolicyBuilder()
            .allowElements("h1", "h2", "h3", "p", "pre", "code", "img")
            .allowUrlProtocols("https")
            .allowAttributes("src", "alt").onElements("img")
            .toFactory();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 安全过滤并转换Markdown
    public ArticleContent processMarkdown(String rawMarkdown) {
        // 1. HTML过滤
        String safeMarkdown = HTML_POLICY.sanitize(rawMarkdown);

        // 2. Markdown解析
        Node document = MARKDOWN_PARSER.parse(safeMarkdown);
        String html = HTML_RENDERER.render(document);

        // 3. 生成缓存Key
        String cacheKey = "md:" + DigestUtils.md5DigestAsHex(safeMarkdown.getBytes());

        // 4. 缓存处理
        //redisTemplate.opsForValue().set(cacheKey, html, 1, TimeUnit.DAYS);
        
        return ArticleContent.builder()
                .mdContent(safeMarkdown)
                .htmlContent(html)
                .build();
    }

    // 从缓存获取HTML
    public String getCachedHtml(String markdown) {
        String cacheKey = "md:" + DigestUtils.md5DigestAsHex(markdown.getBytes());
        return redisTemplate.opsForValue().get(cacheKey);
    }
}
