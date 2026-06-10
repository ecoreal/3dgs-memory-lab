package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.Comment;
import com.cqq._3dobject.entity.ModelEntity;
import com.cqq._3dobject.entity.SceneScheme;
import com.cqq._3dobject.entity.SceneSchemeComment;
import com.cqq._3dobject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ViewAssembler {

    @Autowired
    private UserService userService;

    public List<Map<String, Object>> toPublicModelViews(List<ModelEntity> models) {
        Map<Long, User> usersById = loadUsers(extractUserIds(models.stream().map(ModelEntity::getUid).toList()));
        return models.stream().map(model -> toPublicModelView(model, usersById)).toList();
    }

    public List<Map<String, Object>> toCommentViews(List<Comment> comments) {
        Map<Long, User> usersById = loadUsers(extractUserIds(comments.stream().map(Comment::getUid).toList()));
        return comments.stream().map(comment -> toCommentView(comment, usersById)).toList();
    }

    public List<Map<String, Object>> toPublicSceneSchemeViews(List<SceneScheme> schemes) {
        Map<Long, User> usersById = loadUsers(extractUserIds(schemes.stream().map(SceneScheme::getUid).toList()));
        return schemes.stream().map(scheme -> toPublicSceneSchemeView(scheme, usersById)).toList();
    }

    public List<Map<String, Object>> toSceneSchemeCommentViews(List<SceneSchemeComment> comments) {
        Map<Long, User> usersById = loadUsers(extractUserIds(comments.stream().map(SceneSchemeComment::getUid).toList()));
        return comments.stream().map(comment -> toSceneSchemeCommentView(comment, usersById)).toList();
    }

    public Map<String, Object> toCommentView(Comment comment) {
        Map<Long, User> usersById = loadUsers(
                comment != null && comment.getUid() != null ? List.of(comment.getUid()) : List.of()
        );
        return toCommentView(comment, usersById);
    }

    public Map<String, Object> toSceneSchemeCommentView(SceneSchemeComment comment) {
        Map<Long, User> usersById = loadUsers(
                comment != null && comment.getUid() != null ? List.of(comment.getUid()) : List.of()
        );
        return toSceneSchemeCommentView(comment, usersById);
    }

    private Map<String, Object> toPublicModelView(ModelEntity model, Map<Long, User> usersById) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (model == null) {
            return result;
        }

        result.put("mid", model.getMid());
        result.put("uid", model.getUid());
        result.put("categoryId", model.getCategoryId());
        result.put("title", model.getTitle());
        result.put("story", model.getStory());
        result.put("productionYear", model.getProductionYear());
        result.put("renderStatus", model.getRenderStatus());
        result.put("isPublic", model.getIsPublic());
        result.put("thumbnailUrl", model.getThumbnailUrl());
        result.put("createdAt", model.getCreatedAt());
        result.put("formats", model.getFormats());

        User user = usersById.get(model.getUid());
        if (user != null) {
            result.put("nickname", user.getNickname());
            result.put("avatarUrl", user.getAvatarUrl());
        }

        return result;
    }

    private Map<String, Object> toPublicSceneSchemeView(SceneScheme scheme, Map<Long, User> usersById) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (scheme == null) {
            return result;
        }

        result.put("schemeId", scheme.getSchemeId());
        result.put("uid", scheme.getUid());
        result.put("title", scheme.getTitle());
        result.put("roomDisplayName", scheme.getRoomDisplayName());
        result.put("thumb", scheme.getThumb());
        result.put("itemCount", scheme.getItemCount());
        result.put("roomId", scheme.getRoomId());
        result.put("roomName", scheme.getRoomName());
        result.put("shape", scheme.getShape());
        result.put("area", scheme.getArea());
        result.put("kind", scheme.getKind());
        result.put("createdAt", scheme.getCreatedAt());

        User user = usersById.get(scheme.getUid());
        if (user != null) {
            result.put("nickname", user.getNickname());
            result.put("avatarUrl", user.getAvatarUrl());
        }

        return result;
    }

    private Map<String, Object> toCommentView(Comment comment, Map<Long, User> usersById) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (comment == null) {
            return result;
        }

        result.put("id", comment.getId());
        result.put("mid", comment.getMid());
        result.put("uid", comment.getUid());
        result.put("content", comment.getContent());
        result.put("createdAt", comment.getCreatedAt());

        User user = usersById.get(comment.getUid());
        if (user != null) {
            result.put("nickname", user.getNickname());
            result.put("avatarUrl", user.getAvatarUrl());
        }

        return result;
    }

    private Map<String, Object> toSceneSchemeCommentView(SceneSchemeComment comment, Map<Long, User> usersById) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (comment == null) {
            return result;
        }

        result.put("id", comment.getId());
        result.put("schemeId", comment.getSchemeId());
        result.put("uid", comment.getUid());
        result.put("content", comment.getContent());
        result.put("createdAt", comment.getCreatedAt());

        User user = usersById.get(comment.getUid());
        if (user != null) {
            result.put("nickname", user.getNickname());
            result.put("avatarUrl", user.getAvatarUrl());
        }

        return result;
    }

    private Collection<Long> extractUserIds(List<Long> uids) {
        return uids.stream().filter(Objects::nonNull).distinct().toList();
    }

    private Map<Long, User> loadUsers(Collection<Long> uids) {
        return userService.getUserMapByIds(uids);
    }
}
