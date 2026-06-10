package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.ScenePlacement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScenePlacementRepository extends JpaRepository<ScenePlacement, String> {

    List<ScenePlacement> findBySchemeIdOrderBySortOrderAscCreatedAtAsc(Long schemeId);

    void deleteBySchemeId(Long schemeId);
}
