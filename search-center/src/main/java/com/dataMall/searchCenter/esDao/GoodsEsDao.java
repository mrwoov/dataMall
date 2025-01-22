package com.dataMall.searchCenter.esDao;

import com.dataMall.searchCenter.dto.GoodsEsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsEsDao extends ElasticsearchRepository<GoodsEsDTO, Long> {
    Page<GoodsEsDTO> findByName(String name, Pageable pageable);

    Page<GoodsEsDTO> findByNameAndCategoryId(String keyword, Integer categoryId, Pageable pageable);
}
