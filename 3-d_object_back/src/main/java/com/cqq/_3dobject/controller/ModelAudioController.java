package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.ModelAudio;
import com.cqq._3dobject.service.ModelAudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model-audios")
public class ModelAudioController {

    @Autowired
    private ModelAudioService modelAudioService;

    // 接口：绑定录音到模型
    @PostMapping("/bind")
    public ModelAudio bindAudio(@RequestBody ModelAudio audio) {
        return modelAudioService.saveAudio(audio);
    }

    // 接口：获取模型的故事音频列表
    @GetMapping("/model/{mid}")
    public List<ModelAudio> getAudios(@PathVariable("mid") Long mid) {
        return modelAudioService.getAudiosByMid(mid);
    }
}
