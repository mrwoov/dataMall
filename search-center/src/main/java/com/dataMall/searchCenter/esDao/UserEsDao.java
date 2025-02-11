package com.dataMall.searchCenter.esDao;

import com.dataMall.searchCenter.dto.UserEsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserEsDao extends ElasticsearchRepository<UserEsDTO, Long> {
    Page<UserEsDTO> findByUsername(String username, Pageable pageable);

    long countByUsername(String keyword);
}
