package com.i4u.hub.domain.repository;

import com.i4u.hub.domain.model.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubCustomRepository {
    Page<Hub> findAllWithPagination(Pageable pageable);
}
