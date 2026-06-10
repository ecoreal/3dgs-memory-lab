package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.ModelAudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModelAudioRepository extends JpaRepository<ModelAudio, Long> {
    // 根据模型 ID 获取其关联的所有音频（故事可能由多段录音组成）
    List<ModelAudio> findByMid(Long mid);
}