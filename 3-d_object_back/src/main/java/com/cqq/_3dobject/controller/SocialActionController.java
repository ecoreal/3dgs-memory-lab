package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.SocialAction;
import com.cqq._3dobject.service.SocialActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/actions")
public class SocialActionController {

    @Autowired
    private SocialActionService actionService;

    // 点赞接口
    @PostMapping("/like")
    public String toggleLike(@RequestBody Map<String, Long> params) {
        return actionService.toggleAction(params.get("uid"), params.get("mid"), SocialAction.ActionType.LIKE);
    }

    // 收藏接口
    @PostMapping("/collect")
    public String toggleCollect(@RequestBody Map<String, Long> params) {
        return actionService.toggleAction(params.get("uid"), params.get("mid"), SocialAction.ActionType.COLLECT);
    }

    // 获取统计数（通过 type 参数区分）
    @GetMapping("/count/{mid}")
    public long getCount(@PathVariable("mid") Long mid, @RequestParam("type") String type) {
        SocialAction.ActionType actionType = SocialAction.ActionType.valueOf(type.toUpperCase());
        return actionService.getCount(mid, actionType);
    }
}
