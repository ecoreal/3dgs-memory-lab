package com.cqq._3dobject.controller;

import com.cqq._3dobject.entity.SceneSchemeSocialAction;
import com.cqq._3dobject.service.SceneSchemeSocialActionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scene-scheme-actions")
public class SceneSchemeSocialActionController {

    private final SceneSchemeSocialActionService actionService;

    public SceneSchemeSocialActionController(SceneSchemeSocialActionService actionService) {
        this.actionService = actionService;
    }

    @PostMapping("/like")
    public String toggleLike(@RequestBody Map<String, Long> params) {
        return actionService.toggleAction(
                params.get("uid"),
                params.get("schemeId"),
                SceneSchemeSocialAction.ActionType.LIKE
        );
    }

    @PostMapping("/collect")
    public String toggleCollect(@RequestBody Map<String, Long> params) {
        return actionService.toggleAction(
                params.get("uid"),
                params.get("schemeId"),
                SceneSchemeSocialAction.ActionType.COLLECT
        );
    }

    @GetMapping("/count/{schemeId}")
    public long getCount(@PathVariable("schemeId") Long schemeId, @RequestParam("type") String type) {
        SceneSchemeSocialAction.ActionType actionType = SceneSchemeSocialAction.ActionType.valueOf(type.toUpperCase());
        return actionService.getCount(schemeId, actionType);
    }
}
