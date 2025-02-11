package com.dataMall.searchCenter.esDao;

import com.dataMall.searchCenter.dto.BlogEsDTO;
import com.dataMall.searchCenter.dto.GoodsEsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsEsDao extends ElasticsearchRepository<GoodsEsDTO, Long> {
    Page<GoodsEsDTO> findByName(String name, Pageable pageable);

    Page<GoodsEsDTO> findByNameAndCategoryId(String keyword, Integer categoryId, Pageable pageable);


    long countByName(String name);

    Page<GoodsEsDTO> findByNameAndUid(String name, int uid, Pageable pageable);

    long countByNameAndUid(String name, int uid);

    Page<GoodsEsDTO> findByUid(int uid);

    Page<GoodsEsDTO> findByUid(int uid, Pageable pageable);

    long countByUid(int uid);
}
