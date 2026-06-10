<template>
    <common-layout :pageTitle="spaceInfo.title">
        <view class="space-detail">

            <!-- ══ 顶部栏：返回 + 标题 ══ -->
            <view class="detail-topbar">
                <view class="back-btn" @tap="goBack">
                    <text class="back-arrow">←</text>
                    <text class="back-label">返回广场</text>
                </view>
                <text class="topbar-title">{{ spaceInfo.title }}</text>
                <view class="topbar-filler"></view>
            </view>

            <!-- ══ 主体：左右布局 ══ -->
            <view class="detail-body">

            <!-- ══ 左侧：3D 展厅（固定尺寸，不滚动）══ -->
            <view class="gallery-panel">

                <!-- 3D 渲染区 -->
                <view class="gallery-viewport">
                    <!-- TODO: 后端 - 替换为真实 Three.js / WebGL 展厅组件 -->
                    <!-- <three-gallery :spaceId="spaceId" :sceneConfig="spaceInfo.sceneConfig" /> -->

                    <!-- 占位展示层 -->
                    <view class="gallery-stage" :style="{ background: spaceInfo.gradient }">
                        <view class="stage-vignette"></view>

                        <!-- 整体 3D 展馆占位层 -->
                        <view class="gallery-room">
                            <view class="room-back-wall"></view>
                            <view class="room-side left"></view>
                            <view class="room-side right"></view>
                            <view class="room-light l1"></view>
                            <view class="room-light l2"></view>
                            <view class="room-light l3"></view>
                            <view class="room-center-mark">
                                <text class="room-mark-title">3D Exhibition Hall</text>
                                <text class="room-mark-sub">整体展馆预览区域</text>
                            </view>
                        </view>

                        <!-- 展厅地板透视线 -->
                        <view class="floor-grid">
                            <view class="floor-line" v-for="n in 6" :key="n"></view>
                        </view>

                        <!-- 展厅顶部横幅 -->
                        <view class="gallery-banner">
                            <text class="banner-ornament">✦</text>
                            <text class="banner-name">{{ spaceInfo.title }}</text>
                            <text class="banner-ornament">✦</text>
                        </view>
                    </view>

                    <!-- 3D 控制栏 -->
                    <view class="gallery-controls">
                        <!-- TODO: 后端 - 绑定 Three.js 相机控制 -->
                        <view class="ctrl-btn" @tap="rotateLeft">
                            <text class="ctrl-icon">←</text>
                            <text class="ctrl-label">旋转</text>
                        </view>
                        <view class="ctrl-btn zoom" @tap="toggleFullscreen">
                            <text class="ctrl-icon">⊞</text>
                            <text class="ctrl-label">全屏</text>
                        </view>
                        <view class="ctrl-btn" @tap="rotateRight">
                            <text class="ctrl-icon">→</text>
                            <text class="ctrl-label">旋转</text>
                        </view>
                    </view>
                </view>

            </view>

            <!-- ══ 分隔线 ══ -->
            <view class="panel-divider">
                <view class="divider-track">
                    <view class="divider-pip" v-for="n in 5" :key="n"></view>
                </view>
            </view>

            <!-- ══ 右侧：信息 + 评论 ══ -->
            <view class="info-panel">
                <scroll-view class="info-scroll" scroll-y>

                    <!-- 展厅信息头 -->
                    <view class="info-header">
                        <view class="info-title-row">
                            <text class="info-title">{{ spaceInfo.title }}</text>
                            <view class="room-chip">{{ spaceInfo.roomType }}</view>
                        </view>
                        <view class="title-rule">
                            <view class="rule-line"></view>
                            <text class="rule-diamond">◆</text>
                            <view class="rule-line"></view>
                        </view>
                        <text class="info-desc">{{ spaceInfo.description }}</text>
                    </view>

                    <!-- 作者卡片 -->
                    <view class="author-card">
                        <view class="author-avatar-wrap">
                            <image
                                v-if="isImageUrl(spaceInfo.avatar)"
                                class="author-avatar-img"
                                :src="spaceInfo.avatar"
                                mode="aspectFill"
                            />
                            <text v-else class="author-avatar-emoji">{{ spaceInfo.avatar }}</text>
                        </view>
                        <view class="author-body">
                            <view class="author-name-row">
                                <!-- TODO: 后端 - 跳转作者主页 /user/:authorId -->
                                <text class="author-name">{{ spaceInfo.author }}</text>
                                <text class="author-badge">藏家</text>
                            </view>
                            <text class="author-bio">{{ spaceInfo.authorBio }}</text>
                        </view>
                        <!-- TODO: 后端 - 关注接口 POST /api/user/follow -->
                        <view
                            :class="['follow-btn', { followed: isFollowed }]"
                            @tap="toggleFollow"
                        >
                            <text>{{ isFollowed ? '已关注' : '+ 关注' }}</text>
                        </view>
                    </view>

                    <!-- 数据概览 -->
                    <view class="stats-row">
                        <!-- 后端：/api/scene-scheme-actions/like -->
                        <view :class="['stat-item', { liked: isLiked }]" @tap="toggleLike">
                            <text class="stat-icon">{{ isLiked ? '❤' : '♡' }}</text>
                            <text class="stat-num">{{ displayLikes }}</text>
                            <text class="stat-label">喜欢</text>
                        </view>
                        <view class="stat-divider"></view>
                        <view class="stat-item">
                            <text class="stat-icon">◎</text>
                            <text class="stat-num">{{ spaceInfo.views }}</text>
                            <text class="stat-label">浏览</text>
                        </view>
                        <view class="stat-divider"></view>
                        <view class="stat-item">
                            <text class="stat-icon">✎</text>
                            <text class="stat-num">{{ spaceInfo.comments }}</text>
                            <text class="stat-label">评论</text>
                        </view>
                        <view class="stat-divider"></view>
                        <view :class="['stat-item', { collected: isCollected }]" @tap="toggleCollect">
                            <text class="stat-icon">{{ isCollected ? '★' : '☆' }}</text>
                            <text class="stat-num">{{ displayCollects }}</text>
                            <text class="stat-label">收藏</text>
                        </view>
                    </view>

                    <!-- 评论区 -->
                    <view class="comment-section">
                        <view class="section-header-row">
                            <view class="section-label">
                                <text class="label-mark">§</text>
                                <text class="section-title">留言墙</text>
                            </view>
                            <text class="comment-count">{{ totalCommentCount }} 条留言</text>
                        </view>

                        <view class="reply-target" v-if="replyingTo">
                            <text class="reply-target-text">正在回复 {{ replyingTo.author }}</text>
                            <text class="reply-cancel" @tap="cancelReply">取消</text>
                        </view>

                        <!-- 评论输入框 -->
                        <view class="comment-input-wrap">
                            <input
                                class="comment-input"
                                v-model="commentText"
                                :placeholder="commentPlaceholder"
                                placeholder-class="comment-placeholder"
                                @confirm="submitComment"
                            />
                            <view
                                :class="['send-btn', { active: commentText.trim() }]"
                                @tap="submitComment"
                            >
                                <text>投递</text>
                            </view>
                        </view>

                        <!-- 评论列表 -->
                        <view class="comment-list">
                            <view
                                v-for="(c, i) in commentList"
                                :key="c.id"
                                class="comment-item"
                                :style="{ animationDelay: (i * 0.06) + 's' }"
                            >
                                <view class="comment-avatar">
                                    <image
                                        v-if="isImageUrl(c.avatar)"
                                        class="comment-avatar-img"
                                        :src="c.avatar"
                                        mode="aspectFill"
                                    />
                                    <text v-else>{{ c.avatar }}</text>
                                </view>
                                <view class="comment-body">
                                    <view class="comment-meta">
                                        <text class="comment-author">{{ c.author }}</text>
                                        <text class="comment-time">{{ c.time }}</text>
                                    </view>
                                    <text class="comment-text">{{ c.content }}</text>
                                    <view class="comment-actions">
                                        <view class="comment-like" @tap="likeComment(c)">
                                            <text>{{ c.liked ? '❤' : '♡' }} {{ c.likes }}</text>
                                        </view>
                                        <view class="comment-reply" @tap="replyTo(c)">
                                            <text>↩ 回复</text>
                                        </view>
                                    </view>
                                    <!-- 回复列表 -->
                                    <view class="reply-list" v-if="c.replies && c.replies.length">
                                        <view class="reply-item" v-for="r in c.replies" :key="r.id">
                                            <text class="reply-author">{{ r.author }}</text>
                                            <text class="reply-text">{{ r.content }}</text>
                                        </view>
                                    </view>
                                </view>
                            </view>
                        </view>

                        <view class="load-more" @tap="loadMoreComments" v-if="hasMoreComments">
                            <view class="load-rule"></view>
                            <text class="load-text">展开更多留言</text>
                            <view class="load-rule"></view>
                        </view>
                    </view>

                    <view class="safe-bottom"></view>
                </scroll-view>
            </view>

            </view><!-- /.detail-body -->

        </view>
    </common-layout>
</template>

<script>
import commonLayout from '@/components/common-layout/common-layout.vue';
import request, { BASE_URL } from '@/utils/request.js';

const ASSET_HOST = BASE_URL.replace(/\/api\/?$/i, '');

export default {
    components: { commonLayout },

    data() {
        return {
            spaceId: '',

            isLoading: false,
            loadError: '',
            isLiked: false,
            isCollected: false,
            isFollowed: false,
            commentText: '',
            replyingTo: null,
            hasMoreComments: true,
            commentPage: 1,

            spaceInfo: {
                id: '',
                title: '展馆详情',
                author: '策展人',
                authorBio: '这位策展人暂未填写简介',
                avatar: '🏛️',
                gradient: 'linear-gradient(145deg, #2C1F3E 0%, #1A1228 50%, #0D1F2D 100%)',
                roomType: '未命名展厅',
                description: '正在加载展馆说明…',
                likes: 0,
                views: 0,
                comments: 0,
                collects: 0,
                exhibits: [],
                sceneConfig: {},
            },

            commentList: [],
        }
    },

    computed: {
        displayLikes() {
            return Number(this.spaceInfo.likes || 0);
        },
        displayCollects() {
            return Number(this.spaceInfo.collects || 0) + (this.isCollected ? 1 : 0);
        },
        totalCommentCount() {
            return this.countComments(this.commentList);
        },
        commentPlaceholder() {
            return this.replyingTo ? `回复 ${this.replyingTo.author}...` : '写下你的感想...';
        },
    },

    onLoad(options) {
        this.spaceId = options?.id || options?.schemeId || '';
        console.log('[space-detail] onLoad:', { options, spaceId: this.spaceId });
        if (!this.spaceId) {
            this.loadError = '缺少展馆 ID';
            uni.showToast({ title: '缺少展馆 ID', icon: 'none' });
            return;
        }
        this.fetchSpaceDetail();
    },

    methods: {
        async fetchSpaceDetail() {
            this.isLoading = true;
            this.loadError = '';
            console.log('[space-detail] fetch detail start:', {
                endpoint: `/scene-schemes/public/${this.spaceId}`,
            });

            try {
                const detail = await request({
                    url: `/scene-schemes/public/${this.spaceId}`,
                    method: 'GET',
                });
                console.log('[space-detail] detail raw response:', detail);
                this.spaceInfo = this.mapSchemeToSpaceInfo(detail);
                console.log('[space-detail] mapped detail:', this.spaceInfo);

                await Promise.all([
                    this.fetchLikeCount(),
                    this.fetchComments(),
                ]);
            } catch (err) {
                console.error('[space-detail] fetch detail failed:', err);
                this.loadError = '展馆详情加载失败';
                uni.showToast({ title: '展馆详情加载失败', icon: 'none' });
            } finally {
                this.isLoading = false;
            }
        },

        async fetchLikeCount() {
            console.log('[space-detail] fetch like count start:', {
                endpoint: `/scene-scheme-actions/count/${this.spaceId}`,
                type: 'like',
            });
            try {
                const count = await request({
                    url: `/scene-scheme-actions/count/${this.spaceId}`,
                    method: 'GET',
                    data: { type: 'like' },
                });
                this.spaceInfo.likes = Number(count || 0);
                console.log('[space-detail] like count response:', count);
            } catch (err) {
                console.warn('[space-detail] fetch like count failed:', err);
            }
        },

        async fetchComments() {
            console.log('[space-detail] fetch comments start:', {
                endpoint: `/scene-scheme-comments/scheme/${this.spaceId}`,
            });
            try {
                const list = await request({
                    url: `/scene-scheme-comments/scheme/${this.spaceId}`,
                    method: 'GET',
                });
                console.log('[space-detail] comments raw response:', list);
                const mappedComments = Array.isArray(list)
                    ? list.map(item => this.mapComment(item))
                    : [];
                this.commentList = this.groupCommentThreads(mappedComments);
                this.spaceInfo.comments = this.countComments(this.commentList);
                this.hasMoreComments = false;
                console.log('[space-detail] mapped comments:', this.commentList);
            } catch (err) {
                console.warn('[space-detail] fetch comments failed:', err);
            }
        },

        async toggleLike() {
            const userInfo = uni.getStorageSync('userInfo') || {};
            const uid = userInfo.uid || this.spaceInfo.authorId;

            console.log('[space-detail] toggle like start:', {
                endpoint: '/scene-scheme-actions/like',
                uid,
                schemeId: this.spaceId,
            });

            if (!uid) {
                uni.showToast({ title: '请先登录后点赞', icon: 'none' });
                console.warn('[space-detail] toggle like blocked: missing uid');
                return;
            }

            try {
                const result = await request({
                    url: '/scene-scheme-actions/like',
                    method: 'POST',
                    data: {
                        uid,
                        schemeId: Number(this.spaceId),
                    },
                });
                console.log('[space-detail] toggle like response:', result);
                this.isLiked = result !== 'CANCELLED';
                await this.fetchLikeCount();
                uni.showToast({
                    title: result === 'CANCELLED' ? '已取消点赞' : '已点赞',
                    icon: 'none',
                });
            } catch (err) {
                console.error('[space-detail] toggle like failed:', err);
            }
        },

        toggleCollect() {
            this.isCollected = !this.isCollected;
            console.log('[space-detail] collect is local only:', { isCollected: this.isCollected });
            uni.showToast({ title: this.isCollected ? '已收藏到本地' : '已取消收藏', icon: 'none' });
        },

        toggleFollow() {
            this.isFollowed = !this.isFollowed;
            console.log('[space-detail] follow is local only:', { isFollowed: this.isFollowed });
        },

        async submitComment() {
            const text = this.commentText.trim();
            if (!text) return;
            const userInfo = uni.getStorageSync('userInfo') || {};
            const uid = userInfo.uid;

            console.log('[space-detail] submit comment start:', {
                endpoint: '/scene-scheme-comments/add',
                schemeId: this.spaceId,
                uid,
                content: text,
                replyingTo: this.replyingTo,
            });

            if (!uid) {
                uni.showToast({ title: '请先登录后评论', icon: 'none' });
                console.warn('[space-detail] submit comment blocked: missing uid');
                return;
            }

            try {
                const parent = this.replyingTo;
                const created = await request({
                    url: '/scene-scheme-comments/add',
                    method: 'POST',
                    data: {
                        schemeId: Number(this.spaceId),
                        uid,
                        content: parent ? `@${parent.author} ${text}` : text,
                    },
                });
                console.log('[space-detail] submit comment response:', created);
                if (parent && created?.id) {
                    this.saveReplyParent(created.id, parent.id);
                }
                this.commentText = '';
                this.replyingTo = null;
                await this.fetchComments();
                uni.showToast({ title: '评论已发布', icon: 'none' });
            } catch (err) {
                console.error('[space-detail] submit comment failed:', err);
            }
        },

        likeComment(comment) {
            comment.liked = !comment.liked;
            comment.likes += comment.liked ? 1 : -1;
            console.log('[space-detail] comment like is local only:', comment);
        },

        replyTo(comment) {
            this.replyingTo = {
                id: comment.id,
                author: comment.author,
            };
            this.commentText = '';
            console.log('[space-detail] reply target selected:', this.replyingTo);
        },

        cancelReply() {
            this.replyingTo = null;
            this.commentText = '';
            console.log('[space-detail] reply cancelled');
        },

        loadMoreComments() {
            this.commentPage++;
            this.hasMoreComments = false;
            console.log('[space-detail] comments API is non-paginated, no more pages');
        },

        goBack() {
            uni.navigateBack();
            // 若无历史栈（直接进入），跳回广场
            // uni.redirectTo({ url: '/pages/square/square' });
        },

        rotateLeft() {
            // TODO: 后端/3D - 调用 Three.js 相机向左旋转
        },

        rotateRight() {
            // TODO: 后端/3D - 调用 Three.js 相机向右旋转
        },

        toggleFullscreen() {
            // TODO: 3D - 展厅全屏模式
        },

        groupCommentThreads(comments = []) {
            const replyParentMap = this.getReplyParentMap();
            const byId = new Map();
            comments.forEach(comment => {
                const normalized = {
                    ...comment,
                    replies: [],
                };
                byId.set(String(normalized.id), normalized);
            });

            const attachedIds = new Set();
            comments.forEach(comment => {
                const parentId = comment.parentId || replyParentMap[String(comment.id)];
                const parent = parentId ? byId.get(String(parentId)) : null;
                if (!parent || String(parent.id) === String(comment.id)) return;

                const reply = this.toReplyComment(byId.get(String(comment.id)), parent);
                parent.replies.unshift(reply);
                attachedIds.add(String(comment.id));
            });

            comments.forEach(comment => {
                const id = String(comment.id);
                if (attachedIds.has(id)) return;

                const meta = this.extractReplyMeta(comment.content);
                if (!meta) return;

                const parent = comments
                    .map(item => byId.get(String(item.id)))
                    .find(item => (
                        item &&
                        String(item.id) !== id &&
                        !attachedIds.has(String(item.id)) &&
                        item.author === meta.author
                    ));

                if (!parent) return;
                const reply = this.toReplyComment(byId.get(id), parent, meta.content);
                parent.replies.unshift(reply);
                attachedIds.add(id);
            });

            return comments
                .filter(comment => !attachedIds.has(String(comment.id)))
                .map(comment => byId.get(String(comment.id)));
        },

        toReplyComment(comment, parent, content) {
            const meta = this.extractReplyMeta(comment?.content);
            return {
                ...comment,
                content: content || meta?.content || comment?.content || '',
                replyTo: parent?.author || '',
                replies: [],
            };
        },

        extractReplyMeta(content) {
            if (!content || typeof content !== 'string') return null;
            const match = content.match(/^@(\S+)\s+([\s\S]+)$/);
            if (!match) return null;
            return {
                author: match[1],
                content: match[2],
            };
        },

        countComments(comments = []) {
            return comments.reduce((total, comment) => {
                return total + 1 + this.countComments(comment.replies || []);
            }, 0);
        },

        replyParentStorageKey() {
            return `sceneSchemeReplyParents:${this.spaceId}`;
        },

        getReplyParentMap() {
            const stored = uni.getStorageSync(this.replyParentStorageKey());
            return stored && typeof stored === 'object' ? stored : {};
        },

        saveReplyParent(replyId, parentId) {
            const map = this.getReplyParentMap();
            map[String(replyId)] = parentId;
            uni.setStorageSync(this.replyParentStorageKey(), map);
            console.log('[space-detail] saved local reply parent:', { replyId, parentId });
        },

        mapSchemeToSpaceInfo(item = {}) {
            const placements = Array.isArray(item.placements) ? item.placements : [];
            const first = placements[0] || {};
            const icon = this.resolveIcon(item.thumb || first.icon || '🏛️');
            return {
                id: item.schemeId || this.spaceId,
                schemeId: item.schemeId || this.spaceId,
                authorId: item.uid,
                title: item.title || item.roomDisplayName || item.roomName || '展馆详情',
                author: item.nickname || `策展人 ${item.uid || ''}`.trim(),
                authorBio: item.description || '这位策展人暂未填写简介',
                avatar: this.normalizeMediaUrl(item.avatarUrl) || icon,
                gradient: 'linear-gradient(145deg, #2C1F3E 0%, #1A1228 50%, #0D1F2D 100%)',
                roomType: item.roomDisplayName || item.roomName || item.roomId || '未命名展厅',
                description: item.description || this.buildDescription(item),
                likes: Number(item.likeCount || item.likes || 0),
                views: Number(item.views || item.viewCount || 0),
                comments: Number(item.commentCount || 0),
                collects: 0,
                coverModelUrl: item.coverModelUrl || first.modelUrl || '',
                coverImageUrl: item.coverImageUrl || '',
                exhibits: placements.map((placement, index) => ({
                    id: placement.mid || placement.placementUid || index,
                    icon: this.resolveIcon(placement.icon || '📦'),
                    name: placement.title || `展品 ${index + 1}`,
                    origin: '',
                    year: '',
                    description: placement.modelUrl || '',
                    tags: [],
                })),
                sceneConfig: {
                    roomData: item.roomData || {},
                    placements,
                },
                raw: item,
            };
        },

        mapComment(item = {}) {
            return {
                id: item.id,
                author: item.nickname || `用户 ${item.uid || ''}`.trim(),
                avatar: this.normalizeMediaUrl(item.avatarUrl) || '🙂',
                time: this.formatRelativeTime(item.createdAt),
                content: item.content || '',
                parentId: item.parentId || item.parentCommentId || item.replyToCommentId || null,
                likes: 0,
                liked: false,
                replies: [],
                raw: item,
            };
        },

        resolveIcon(value) {
            const iconMap = {
                artifact: '🏺',
                mineral: '💎',
                fossil: '🦴',
                toy: '🚂',
                art: '🎨',
                audio: '📻',
            };
            return iconMap[value] || value || '🏛️';
        },

        isImageUrl(value) {
            return typeof value === 'string' && (
                /^https?:\/\//i.test(value) ||
                /^\/?uploads\//i.test(value) ||
                /\.(png|jpe?g|webp|gif|bmp|svg)(?:$|\?)/i.test(value)
            );
        },

        normalizeMediaUrl(value) {
            if (!value || typeof value !== 'string') return '';
            const url = value.trim();
            if (!url) return '';
            if (/^https?:\/\//i.test(url)) return url;
            if (url.startsWith('/')) return `${ASSET_HOST}${url}`;
            if (/^uploads\//i.test(url)) return `${ASSET_HOST}/${url}`;
            return url;
        },

        buildDescription(item) {
            const count = item.itemCount || item.placementCount || (Array.isArray(item.placements) ? item.placements.length : 0);
            const parts = [item.roomDisplayName || item.roomName, item.area, item.kind || item.shape].filter(Boolean);
            return parts.length
                ? `${parts.join(' · ')}，共陈列 ${count} 件藏品。`
                : '这位策展人暂未填写展馆说明。';
        },

        formatRelativeTime(dateText) {
            if (!dateText) return '刚刚';
            const time = new Date(dateText).getTime();
            if (Number.isNaN(time)) return dateText;
            const diff = Date.now() - time;
            const minute = 60 * 1000;
            const hour = 60 * minute;
            const day = 24 * hour;
            if (diff < minute) return '刚刚';
            if (diff < hour) return `${Math.floor(diff / minute)}分钟前`;
            if (diff < day) return `${Math.floor(diff / hour)}小时前`;
            return `${Math.floor(diff / day)}天前`;
        },
    },
}
</script>

<style>
/* ══════════════════════════════════
   变量继承项目风格系统
══════════════════════════════════ */
page {
    --cream:       #FAF7F2;
    --warm:        #F2EDE4;
    --parchment:   #E8E0D0;
    --sepia-light: #D4C5A9;
    --ink:         #3D3229;
    --ink-mid:     #6B5B4E;
    --ink-light:   #9A8878;
    --ink-faint:   #C4B5A5;
    --rust:        #8B5E3C;
    --rust-dark:   #6B4426;
    --shadow-warm: rgba(90,65,45,0.10);
    --shadow-deep: rgba(90,65,45,0.20);
    --radius-card: 10px;
}

/* ══════════════════════════════════
   页面根容器 — 纵向：topbar + body
══════════════════════════════════ */
.space-detail {
    width: 100%;
    height: 100%;
    max-height: 100%;
    flex: 1;
    min-height: 0;
    display: flex;
    flex-direction: column;   /* topbar 在上，detail-body 在下 */
    overflow: hidden;
    position: relative;
    background: var(--cream);
    background-image:
        url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.75' numOctaves='4' stitchTiles='stitch'/%3E%3CfeColorMatrix type='saturate' values='0'/%3E%3C/filter%3E%3Crect width='300' height='300' filter='url(%23n)' opacity='0.03'/%3E%3C/svg%3E");
}

/* ══ 顶部返回栏 ══ */
.detail-topbar {
    flex-shrink: 0;
    height: 44px;
    display: flex;
    align-items: center;
    padding: 0 14px;
    background: linear-gradient(to bottom, #3D2E22, #4A3828);
    border-bottom: 1px solid rgba(201,168,124,0.18);
    box-shadow: 0 2px 6px rgba(0,0,0,0.12);
    gap: 12px;
}

.back-btn {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 5px 10px;
    border-radius: 5px;
    background: rgba(255,255,255,0.08);
    border: 1px solid rgba(201,168,124,0.22);
    flex-shrink: 0;
    transition: background 0.2s;
}
.back-btn:hover { background: rgba(255,255,255,0.13); }

.back-arrow {
    font-size: 15px;
    color: rgba(240,228,210,0.9);
    font-family: Georgia, serif;
    line-height: 1;
}

.back-label {
    font-size: 12px;
    color: rgba(220,200,170,0.85);
    font-family: Georgia, serif;
}

.topbar-title {
    flex: 1;
    text-align: center;
    font-size: 15px;
    font-weight: 700;
    color: rgba(245,235,215,0.92);
    font-family: Georgia, serif;
    letter-spacing: 1.5px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.topbar-filler {
    /* 占位，让标题居中 */
    flex-shrink: 0;
    width: 88px;   /* 与 back-btn 宽度近似 */
}

/* ══ 主体左右容器 ══ */
.detail-body {
    flex: 1;
    min-height: 0;
    display: flex;
    flex-direction: row;
    overflow: hidden;
}

@media (max-width: 768px) {
    .detail-body { flex-direction: column; }
}

/* ══════════════════════════════════
   左侧：展厅面板 — 固定尺寸，不滚动
══════════════════════════════════ */
.gallery-panel {
    /* 左侧固定为展馆画布，不参与页面滚动 */
    flex: 0 0 70%;
    min-width: 0;
    max-width: 70%;
    display: flex;
    flex-direction: column;
    overflow: hidden;          /* 左侧整体不产生滚动条 */
    background: #0D1520;
}

@media (max-width: 768px) {
    .gallery-panel {
        flex: 0 0 auto;
        max-width: 100%;
        height: 260px;
    }
}

/* 3D 视口 */
.gallery-viewport {
    flex: 1;
    min-height: 0;
    position: relative;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

/* 展厅舞台（3D 占位） */
.gallery-stage {
    flex: 1;
    min-height: 0;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-end;
    overflow: hidden;
}

/* 暗角 */
.stage-vignette {
    position: absolute;
    inset: 0;
    background: radial-gradient(ellipse at 50% 40%, transparent 35%, rgba(0,0,0,0.55) 100%);
    pointer-events: none;
    z-index: 2;
}

/* 透视地板线 */
.floor-grid {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 40%;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    pointer-events: none;
    z-index: 1;
    perspective: 400px;
}

.floor-line {
    height: 1px;
    background: rgba(255,255,255,0.06);
    transform-origin: center bottom;
    margin-bottom: 12%;
}
.floor-line:nth-child(1) { transform: perspective(300px) rotateX(60deg) scaleX(0.3); opacity: 0.4; }
.floor-line:nth-child(2) { transform: perspective(300px) rotateX(55deg) scaleX(0.5); opacity: 0.35; }
.floor-line:nth-child(3) { transform: perspective(300px) rotateX(50deg) scaleX(0.65); opacity: 0.3; }
.floor-line:nth-child(4) { transform: perspective(300px) rotateX(45deg) scaleX(0.78); opacity: 0.25; }
.floor-line:nth-child(5) { transform: perspective(300px) rotateX(40deg) scaleX(0.88); opacity: 0.2; }
.floor-line:nth-child(6) { transform: perspective(300px) rotateX(35deg) scaleX(0.96); opacity: 0.15; }

/* 整体展馆占位 */
.gallery-room {
    position: absolute;
    inset: 0;
    z-index: 3;
    overflow: hidden;
}

.room-back-wall {
    position: absolute;
    left: 10%;
    right: 10%;
    top: 14%;
    bottom: 26%;
    border: 1px solid rgba(201,168,124,0.18);
    background:
        linear-gradient(90deg, rgba(255,255,255,0.04), transparent 18%, transparent 82%, rgba(255,255,255,0.04)),
        radial-gradient(ellipse at 50% 35%, rgba(201,168,124,0.12), transparent 58%);
    box-shadow: inset 0 0 48px rgba(0,0,0,0.28);
}

.room-side {
    position: absolute;
    top: 16%;
    bottom: 21%;
    width: 22%;
    border: 1px solid rgba(201,168,124,0.12);
    background: linear-gradient(180deg, rgba(255,255,255,0.05), rgba(0,0,0,0.12));
    transform-origin: center;
}

.room-side.left {
    left: -2%;
    transform: skewY(-10deg);
}

.room-side.right {
    right: -2%;
    transform: skewY(10deg);
}

.room-light {
    position: absolute;
    top: 12%;
    width: 16%;
    height: 54%;
    background: linear-gradient(180deg, rgba(255,232,190,0.18), transparent 72%);
    clip-path: polygon(42% 0, 58% 0, 100% 100%, 0 100%);
    opacity: 0.58;
    pointer-events: none;
}

.room-light.l1 { left: 18%; }
.room-light.l2 { left: 42%; opacity: 0.72; }
.room-light.l3 { right: 18%; }

.room-center-mark {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 16px 24px;
    border: 1px solid rgba(201,168,124,0.22);
    background: rgba(0,0,0,0.28);
    backdrop-filter: blur(3px);
}

.room-mark-title {
    font-size: 15px;
    color: rgba(245,235,215,0.9);
    font-family: Georgia, serif;
    letter-spacing: 2px;
    white-space: nowrap;
}

.room-mark-sub {
    font-size: 11px;
    color: rgba(201,168,124,0.72);
    font-family: Georgia, serif;
    letter-spacing: 1px;
    white-space: nowrap;
}

/* 展厅横幅 */
.gallery-banner {
    position: absolute;
    top: 16px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    align-items: center;
    gap: 8px;
    background: rgba(0,0,0,0.45);
    border: 1px solid rgba(201,168,124,0.25);
    border-radius: 4px;
    padding: 5px 14px;
    white-space: nowrap;
    z-index: 4;
}

.banner-ornament {
    font-size: 8px;
    color: rgba(201,168,124,0.6);
}

.banner-name {
    font-size: 13px;
    color: rgba(240,228,210,0.9);
    font-family: Georgia, serif;
    letter-spacing: 2px;
}

/* 展厅操控栏 */
.gallery-controls {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 10px 16px;
    background: rgba(0,0,0,0.6);
    border-top: 1px solid rgba(255,255,255,0.05);
}

.ctrl-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    padding: 6px 14px;
    border: 1px solid rgba(255,255,255,0.10);
    border-radius: 6px;
    background: rgba(255,255,255,0.04);
    transition: background 0.2s;
}

.ctrl-btn:hover {
    background: rgba(255,255,255,0.08);
}

.ctrl-btn.zoom {
    border-color: rgba(201,168,124,0.30);
}

.ctrl-icon {
    font-size: 16px;
    color: rgba(220,200,170,0.85);
    font-family: Georgia, serif;
}

.ctrl-label {
    font-size: 10px;
    color: rgba(160,140,110,0.7);
    font-family: Georgia, serif;
}

/* ══════════════════════════════════
   中间分隔线
══════════════════════════════════ */
.panel-divider {
    flex-shrink: 0;
    width: 1px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: var(--parchment);
    position: relative;
}

@media (max-width: 768px) {
    .panel-divider {
        width: 100%;
        height: 1px;
        flex-direction: row;
    }
}

.divider-track {
    display: flex;
    flex-direction: column;
    gap: 6px;
    position: absolute;
}

@media (max-width: 768px) {
    .divider-track {
        flex-direction: row;
    }
}

.divider-pip {
    width: 4px;
    height: 4px;
    border-radius: 50%;
    background: var(--sepia-light);
}

/* ══════════════════════════════════
   右侧：信息面板 — 独立滚动
══════════════════════════════════ */
.info-panel {
    flex: 1;
    min-width: 0;
    min-height: 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    background: var(--cream);
}

@media (max-width: 768px) {
    .info-panel {
        flex: 1;
        min-height: 0;
    }
}

/* 唯一有滚动条的区域 */
.info-scroll {
    flex: 1;
    min-height: 0;
    height: 100%;
    width: 100%;
    overflow-y: auto;
    box-sizing: border-box;
}

/* 展厅信息头 */
.info-header {
    padding: 18px 18px 14px;
    border-bottom: 1px dashed var(--sepia-light);
}

.info-title-row {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 8px;
    margin-bottom: 8px;
}

.info-title {
    font-size: 20px;
    font-weight: 700;
    color: var(--ink);
    font-family: Georgia, 'Times New Roman', serif;
    letter-spacing: 1px;
    line-height: 1.3;
    flex: 1;
}

.room-chip {
    flex-shrink: 0;
    padding: 3px 9px;
    background: var(--parchment);
    border: 1px dashed var(--sepia-light);
    border-radius: 3px;
    font-size: 10px;
    color: var(--ink-light);
    font-family: Georgia, serif;
    font-style: italic;
    margin-top: 4px;
}

.title-rule {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 10px 0;
}

.rule-line {
    flex: 1;
    height: 1px;
    background: linear-gradient(to right, transparent, var(--sepia-light), transparent);
}

.rule-diamond {
    font-size: 8px;
    color: var(--ink-faint);
}

.info-desc {
    font-size: 13px;
    color: var(--ink-mid);
    line-height: 1.75;
    font-family: Georgia, serif;
}

/* 作者卡片 */
.author-card {
    margin: 14px 18px;
    padding: 12px 14px;
    background: var(--warm);
    border-radius: var(--radius-card);
    border: 1px solid rgba(201,168,124,0.18);
    box-shadow: 0 2px 6px var(--shadow-warm);
    display: flex;
    align-items: center;
    gap: 12px;
}

.author-avatar-wrap {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    background: var(--parchment);
    border: 2px solid var(--sepia-light);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    overflow: hidden;
}

.author-avatar-emoji { font-size: 22px; }

.author-avatar-img {
    width: 100%;
    height: 100%;
    display: block;
    border-radius: 50%;
}

.author-body {
    flex: 1;
    min-width: 0;
}

.author-name-row {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 3px;
}

.author-name {
    font-size: 14px;
    font-weight: 700;
    color: var(--ink);
    font-family: Georgia, serif;
}

.author-badge {
    font-size: 10px;
    color: var(--rust);
    padding: 1px 6px;
    border: 1px solid rgba(139,94,60,0.35);
    border-radius: 3px;
    font-family: Georgia, serif;
}

.author-bio {
    font-size: 11px;
    color: var(--ink-light);
    font-style: italic;
    font-family: Georgia, serif;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.follow-btn {
    flex-shrink: 0;
    padding: 5px 12px;
    border: 1px solid var(--rust);
    border-radius: 4px;
    font-size: 12px;
    color: var(--rust);
    font-family: Georgia, serif;
    transition: all 0.25s ease;
}

.follow-btn.followed {
    background: var(--rust);
    color: #FAF7F2;
}

/* 数据概览 */
.stats-row {
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding: 14px 18px;
    border-top: 1px dashed var(--sepia-light);
    border-bottom: 1px dashed var(--sepia-light);
    margin: 0 18px 14px;
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    padding: 4px 8px;
    border-radius: 6px;
    transition: background 0.2s;
}

.stat-item:hover {
    background: var(--warm);
}

.stat-item.liked .stat-icon,
.stat-item.collected .stat-icon {
    color: var(--rust);
}

.stat-icon {
    font-size: 16px;
    color: var(--ink-light);
    line-height: 1;
    font-family: Georgia, serif;
}

.stat-num {
    font-size: 16px;
    font-weight: 700;
    color: var(--ink);
    font-family: Georgia, serif;
}

.stat-label {
    font-size: 10px;
    color: var(--ink-faint);
    font-style: italic;
    font-family: Georgia, serif;
}

.stat-divider {
    width: 1px;
    height: 32px;
    background: var(--sepia-light);
}

/* 当前展品详情 */
.exhibit-detail {
    margin: 0 18px 14px;
    padding: 14px;
    background: var(--warm);
    border-radius: var(--radius-card);
    border: 1px solid rgba(201,168,124,0.18);
    box-shadow: 0 2px 6px var(--shadow-warm);
}

.detail-header {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 10px;
}

.label-mark {
    font-size: 15px;
    color: var(--rust);
    font-family: Georgia, serif;
    line-height: 1;
}

.detail-title {
    font-size: 13px;
    font-weight: 700;
    color: var(--ink-mid);
    font-family: Georgia, serif;
    letter-spacing: 1px;
}

.detail-card {
    display: flex;
    gap: 12px;
    align-items: flex-start;
}

.detail-icon-wrap {
    width: 50px;
    height: 50px;
    background: var(--parchment);
    border-radius: 8px;
    border: 1px solid var(--sepia-light);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.detail-icon { font-size: 26px; }

.detail-body { flex: 1; min-width: 0; }

.detail-name {
    display: block;
    font-size: 14px;
    font-weight: 700;
    color: var(--ink);
    font-family: Georgia, serif;
    margin-bottom: 3px;
}

.detail-meta {
    display: block;
    font-size: 11px;
    color: var(--rust);
    font-style: italic;
    font-family: Georgia, serif;
    margin-bottom: 5px;
}

.detail-desc {
    font-size: 12px;
    color: var(--ink-mid);
    line-height: 1.6;
    font-family: Georgia, serif;
}

.detail-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
    margin-top: 10px;
}

.tag {
    padding: 2px 8px;
    background: var(--parchment);
    border: 1px dashed var(--sepia-light);
    border-radius: 3px;
    font-size: 10px;
    color: var(--ink-light);
    font-family: Georgia, serif;
}

/* ══════════════════════════════════
   评论区
══════════════════════════════════ */
.comment-section {
    padding: 0 18px;
}

.section-header-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
}

.section-label {
    display: flex;
    align-items: center;
    gap: 6px;
}

.section-title {
    font-size: 15px;
    font-weight: 700;
    color: var(--ink);
    font-family: Georgia, serif;
    letter-spacing: 1px;
}

.comment-count {
    font-size: 11px;
    color: var(--ink-faint);
    font-style: italic;
    font-family: Georgia, serif;
}

.reply-target {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    margin-bottom: 8px;
    padding: 7px 10px;
    border: 1px dashed var(--sepia-light);
    border-radius: 6px;
    background: rgba(236,224,205,0.42);
}

.reply-target-text {
    min-width: 0;
    flex: 1;
    font-size: 12px;
    color: var(--ink-mid);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.reply-cancel {
    flex-shrink: 0;
    font-size: 12px;
    color: var(--rust);
}

/* 评论输入框 */
.comment-input-wrap {
    display: flex;
    gap: 8px;
    align-items: center;
    padding: 10px 12px;
    background: var(--warm);
    border-radius: 8px;
    border: 1px solid rgba(201,168,124,0.22);
    box-shadow: inset 0 1px 3px rgba(0,0,0,0.04);
    margin-bottom: 14px;
}

.comment-input {
    flex: 1;
    font-size: 13px;
    color: var(--ink);
    font-family: Georgia, serif;
}

.comment-placeholder {
    color: var(--ink-faint);
    font-style: italic;
}

.send-btn {
    padding: 5px 12px;
    border-radius: 4px;
    border: 1px solid var(--sepia-light);
    background: var(--parchment);
    font-size: 12px;
    color: var(--ink-light);
    font-family: Georgia, serif;
    transition: all 0.2s;
    flex-shrink: 0;
}

.send-btn.active {
    background: linear-gradient(145deg, #6B4426, #4A2E18);
    color: #F2E6D4;
    border-color: transparent;
}

/* 评论列表 */
.comment-list {
    display: flex;
    flex-direction: column;
    gap: 0;
}

@keyframes commentIn {
    from { opacity: 0; transform: translateX(-6px); }
    to   { opacity: 1; transform: translateX(0); }
}

.comment-item {
    display: flex;
    gap: 10px;
    padding: 12px 0;
    border-bottom: 1px dashed var(--sepia-light);
    animation: commentIn 0.4s ease both;
    align-items: flex-start;
    overflow: hidden;
}

.comment-item:last-child {
    border-bottom: none;
}

.comment-avatar {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    background: var(--parchment);
    border: 1px solid var(--sepia-light);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    flex-shrink: 0;
    margin-top: 2px;
    overflow: hidden;
}

.comment-avatar text {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 1;
    overflow: hidden;
}

.comment-avatar-img {
    width: 100%;
    height: 100%;
    display: block;
    border-radius: 50%;
}

.comment-body { flex: 1; min-width: 0; }

.comment-meta {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-bottom: 5px;
    min-width: 0;
}

.comment-author {
    font-size: 13px;
    font-weight: 600;
    color: var(--ink);
    font-family: Georgia, serif;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.comment-time {
    font-size: 10px;
    color: var(--ink-faint);
    font-style: italic;
    font-family: Georgia, serif;
}

.comment-text {
    font-size: 13px;
    color: var(--ink-mid);
    line-height: 1.65;
    font-family: Georgia, serif;
    word-break: break-word;
    overflow-wrap: anywhere;
}

.comment-actions {
    display: flex;
    gap: 14px;
    margin-top: 6px;
}

.comment-like,
.comment-reply {
    font-size: 11px;
    color: var(--ink-faint);
    font-family: Georgia, serif;
    font-style: italic;
    transition: color 0.2s;
}

.comment-like:hover,
.comment-reply:hover {
    color: var(--rust);
}

/* 回复列表 */
.reply-list {
    margin-top: 8px;
    padding: 8px 10px;
    background: var(--warm);
    border-left: 2px solid var(--sepia-light);
    border-radius: 0 4px 4px 0;
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.reply-item {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
}

.reply-author {
    font-size: 12px;
    font-weight: 600;
    color: var(--rust);
    font-family: Georgia, serif;
}

.reply-text {
    font-size: 12px;
    color: var(--ink-mid);
    font-family: Georgia, serif;
    line-height: 1.55;
    word-break: break-word;
    overflow-wrap: anywhere;
}

/* 加载更多 */
.load-more {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 16px 0;
}

.load-rule {
    flex: 1;
    height: 1px;
    background: linear-gradient(to right, transparent, var(--sepia-light), transparent);
}

.load-text {
    font-size: 12px;
    color: var(--ink-faint);
    font-style: italic;
    font-family: Georgia, serif;
    white-space: nowrap;
}

.safe-bottom { height: 32px; }
</style>
