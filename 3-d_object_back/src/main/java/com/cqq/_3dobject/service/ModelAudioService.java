package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.ModelAudio;
import com.cqq._3dobject.repository.ModelAudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ModelAudioService {

    @Autowired
    private ModelAudioRepository modelAudioRepository;

    // 存入录音记录
    public ModelAudio saveAudio(ModelAudio audio) {
        return modelAudioRepository.save(audio);
    }

    // 获取某个模型的录音
    public List<ModelAudio> getAudiosByMid(Long mid) {
        return modelAudioRepository.findByMid(mid);
    }
}