package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.ModelFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ModelFileRepository extends JpaRepository<ModelFile, Long> {

    List<ModelFile> findByMid(Long mid);

    List<ModelFile> findByMidIn(Collection<Long> mids);

    List<ModelFile> findByMidAndFileType(Long mid, String fileType);

    void deleteByMid(Long mid);
}
