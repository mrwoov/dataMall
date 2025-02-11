package com.dataMall.searchCenter.esDao;

import com.dataMall.searchCenter.dto.BlogEsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BlogEsDao extends ElasticsearchRepository<BlogEsDTO, Long> {


    Page<BlogEsDTO> findBlogByTitle(String keyword, PageRequest of);

    long countBlogByTitle(String keyword);

    Page<BlogEsDTO> findBlogByTitleAndUid(String title, int uid, Pageable pageable);

    long countBlogByTitleAndUid(String title, int uid);

    Page<BlogEsDTO> findByUid(int uid, Pageable pageable);

    long countByUid(int uid);
}
