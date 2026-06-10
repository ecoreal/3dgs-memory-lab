<template>
    <common-layout pageTitle="超级博物馆广场">
        <view class="community-page">

            <!-- ══ 顶部区域 ══ -->
            <view class="community-header">
                <!-- 纸纹背景装饰 -->
                <view class="header-paper">
                    <view class="paper-grain"></view>
                    <view class="header-ornament left">✦</view>
                    <view class="header-ornament right">✦</view>
                </view>

                <view class="header-content">
                    <!-- 标题区 -->
                    <view class="header-top">
                        <view class="title-divider">
                            <view class="divider-line"></view>
                            <text class="divider-diamond">◆</text>
                            <view class="divider-line"></view>
                        </view>
                        <text class="page-title">超级博物馆广场</text>
                        <text class="page-subtitle">探 索 · 珍 藏 · 交 流</text>
                        <view class="title-divider">
                            <view class="divider-line"></view>
                            <text class="divider-diamond">◆</text>
                            <view class="divider-line"></view>
                        </view>
                    </view>

                    <!-- 搜索栏 -->
                    <view class="search-section">
                        <view class="search-bar">
                            <text class="search-icon">⌕</text>
                            <input
                                class="search-input"
                                v-model="keyword"
                                placeholder="检索展厅名称或作者…"
                                placeholder-class="search-placeholder"
                            />
                            <text class="search-clear" v-if="keyword" @tap="keyword = ''">✕</text>
                        </view>
                    </view>

                    <!-- 筛选标签 -->
                    <view class="filter-section">
                        <scroll-view class="filter-scroll" scroll-x>
                            <view class="filter-tabs">
                                <view
                                    v-for="item in filters"
                                    :key="item.key"
                                    :class="['filter-tab', { active: activeFilter === item.key }]"
                                    @tap.stop="activeFilter = item.key"
                                >
                                    <text class="tab-icon">{{ item.icon }}</text>
                                    <text class="tab-text">{{ item.label }}</text>
                                </view>
                            </view>
                        </scroll-view>
                    </view>
                </view>
            </view>

            <!-- ══ 内容滚动区 ══ -->
            <scroll-view class="content-scroll" scroll-y>
                <view class="load-state" v-if="isLoading">
                    <text class="load-state-title">正在连接展馆广场…</text>
                    <text class="load-state-sub">请打开控制台查看 [square] 请求日志</text>
                </view>

                <view class="load-state error" v-else-if="loadError">
                    <text class="load-state-title">{{ loadError }}</text>
                    <view class="retry-btn" @tap="fetchPublicSchemes('retry tap')">重新加载</view>
                </view>

                <!-- 热门展厅 -->
                <view class="section-hot" v-if="!isLoading && !loadError && !keyword && activeFilter === 'all' && hotSpaces.length">
                    <view class="section-header">
                        <view class="section-label">
                            <text class="label-mark">§</text>
                            <text class="section-title">热门展厅</text>
                        </view>
                        <text class="section-tag">本周精选</text>
                    </view>

                    <scroll-view class="hot-scroll" scroll-x>
                        <view class="hot-list">
                            <view
                                v-for="item in hotSpaces"
                                :key="item.id"
                                class="hot-card"
                                @tap.stop="openPreview(item)"
                            >
                                <view class="hot-thumb" :style="{ background: item.gradient }">
                                    <image v-if="item.coverImage" class="thumb-image" :src="item.coverImage" mode="aspectFill" />
                                    <text v-if="!item.coverImage" class="hot-icon">{{ item.icon }}</text>
                                    <view class="hot-rank-badge">{{ item.rank }}</view>
                                    <view class="sample-badge" v-if="item.isMock">示例</view>
                                    <view class="thumb-vignette"></view>
                                </view>
                                <view class="hot-info">
                                    <text class="hot-name">{{ item.title }}</text>
                                    <text class="hot-author">— {{ item.author }}</text>
                                    <view class="hot-stats">
                                        <text class="stat-pill">❤ {{ item.likes }}</text>
                                        <text class="stat-pill">◎ {{ item.views }}</text>
                                    </view>
                                </view>
                            </view>
                        </view>
                    </scroll-view>
                </view>

                <!-- 章节分隔 -->
                <view class="chapter-rule" v-if="!isLoading && !loadError && !keyword && activeFilter === 'all' && hotSpaces.length && newSpaces.length">
                    <view class="rule-line"></view>
                    <text class="rule-text">· · ·</text>
                    <view class="rule-line"></view>
                </view>

                <!-- 最新发布 -->
                <view class="section-new" v-if="!isLoading && !loadError && !keyword && activeFilter === 'all' && newSpaces.length">
                    <view class="section-header">
                        <view class="section-label">
                            <text class="label-mark">§</text>
                            <text class="section-title">最新发布</text>
                        </view>
                        <text class="section-tag">新鲜入馆</text>
                    </view>

                    <view class="new-list">
                        <view
                            v-for="item in newSpaces"
                            :key="item.id"
                            class="new-card"
                            @tap.stop="openPreview(item)"
                        >
                            <view class="new-thumb" :style="{ background: item.gradient }">
                                <image v-if="item.coverImage" class="thumb-image" :src="item.coverImage" mode="aspectFill" />
                                <text v-if="!item.coverImage" class="new-icon">{{ item.icon }}</text>
                                <view class="sample-badge" v-if="item.isMock">示例</view>
                            </view>
                            <view class="new-content">
                                <view class="new-top">
                                    <text class="new-name">{{ item.title }}</text>
                                    <text class="new-time">{{ item.time }}</text>
                                </view>
                                <text class="new-desc">{{ item.description }}</text>
                                <view class="new-bottom">
                                    <view class="author-tag">
                                        <image
                                            v-if="isImageUrl(item.avatar)"
                                            class="author-avatar-img"
                                            :src="item.avatar"
                                            mode="aspectFill"
                                        />
                                        <text v-else class="author-avatar">{{ item.avatar }}</text>
                                        <text class="author-name">{{ item.author }}</text>
                                    </view>
                                    <view class="new-stats">
                                        <text>❤ {{ item.likes }}</text>
                                        <text>✎ {{ item.comments }}</text>
                                    </view>
                                </view>
                            </view>
                        </view>
                    </view>
                </view>

                <!-- 搜索结果 -->
                <view class="section-result" v-if="!isLoading && !loadError && keyword">
                    <view class="result-header">
                        <text class="result-count">检索到 <text class="result-num">{{ searchResults.length }}</text> 件藏馆</text>
                    </view>
                    <view class="result-grid">
                        <view
                            v-for="item in searchResults"
                            :key="item.id"
                            class="result-card"
                            @tap.stop="openPreview(item)"
                        >
                            <view class="result-thumb" :style="{ background: item.gradient }">
                                <image v-if="item.coverImage" class="thumb-image" :src="item.coverImage" mode="aspectFill" />
                                <text v-if="!item.coverImage" class="result-icon">{{ item.icon }}</text>
                                <view class="sample-badge" v-if="item.isMock">示例</view>
                            </view>
                            <view class="result-info">
                                <text class="result-name">{{ item.title }}</text>
                                <text class="result-author">— {{ item.author }}</text>
                            </view>
                            <view class="result-stats">
                                <text>❤ {{ item.likes }}</text>
                                <text>◎ {{ item.views }}</text>
                            </view>
                        </view>
                    </view>
                </view>

                <!-- 分类网格 -->
                <view class="section-category" v-if="!isLoading && !loadError && activeFilter !== 'all' && !keyword">
                    <view class="category-grid">
                        <view
                            v-for="item in filteredSpaces"
                            :key="item.id"
                            class="category-card"
                            @tap.stop="openPreview(item)"
                        >
                            <view class="category-thumb" :style="{ background: item.gradient }">
                                <image v-if="item.coverImage" class="thumb-image" :src="item.coverImage" mode="aspectFill" />
                                <text v-if="!item.coverImage" class="category-icon">{{ item.icon }}</text>
                                <view class="sample-badge" v-if="item.isMock">示例</view>
                                <view class="category-badge">{{ item.itemCount }} 件</view>
                            </view>
                            <view class="category-info">
                                <text class="category-name">{{ item.title }}</text>
                                <text class="category-desc">{{ item.description }}</text>
                                <view class="category-author">
                                    <image
                                        v-if="isImageUrl(item.avatar)"
                                        class="category-author-avatar-img"
                                        :src="item.avatar"
                                        mode="aspectFill"
                                    />
                                    <text v-else>{{ item.avatar }}</text>
                                    <text>{{ item.author }}</text>
                                </view>
                            </view>
                            <view class="category-footer">
                                <view class="footer-stats">
                                    <text>❤ {{ item.likes }}</text>
                                    <text>◎ {{ item.views }}</text>
                                </view>
                                <text class="footer-time">{{ item.time }}</text>
                            </view>
                        </view>
                    </view>
                </view>

                <!-- 空状态 -->
                <view class="empty-state" v-if="!isLoading && !loadError && filteredSpaces.length === 0 && !keyword">
                    <text class="empty-icon">🏛</text>
                    <text class="empty-title">此馆暂无展藏</text>
                    <text class="empty-sub">成为第一位在此开馆的藏家</text>
                </view>

                <view class="safe-bottom"></view>
            </scroll-view>

            <!-- ══ 预览弹窗 ══ -->
            <view class="preview-overlay" v-if="previewSpace" @tap="previewSpace = null">
                <view class="preview-modal" @tap.stop>
                    <!-- 弹窗顶部把手 -->
                    <view class="modal-handle"></view>

                    <view class="preview-header">
                        <text class="preview-title">{{ previewSpace.title }}</text>
                        <text class="preview-close" @tap="previewSpace = null">✕</text>
                    </view>

                    <view class="preview-banner" :style="{ background: previewSpace.gradient }">
                        <image v-if="previewSpace.coverImage" class="preview-cover-image" :src="previewSpace.coverImage" mode="aspectFill" />
                        <text v-if="!previewSpace.coverImage" class="preview-icon">{{ previewSpace.icon }}</text>
                        <view class="banner-vignette"></view>
                        <view class="preview-sample-badge" v-if="previewSpace.isMock">示例展馆</view>
                        <view class="preview-room-tag">{{ previewSpace.roomType }}</view>
                    </view>

                    <view class="preview-content">
                        <view class="preview-author-row">
                            <image
                                v-if="isImageUrl(previewSpace.avatar)"
                                class="preview-avatar-img"
                                :src="previewSpace.avatar"
                                mode="aspectFill"
                            />
                            <text v-else class="preview-avatar">{{ previewSpace.avatar }}</text>
                            <view class="preview-author-info">
                                <!-- TODO: 后端 - 作者主页跳转 previewSpace.authorId -->
                                <text class="preview-author-name">{{ previewSpace.author }}</text>
                                <text class="preview-author-time">发布于 {{ previewSpace.time }}</text>
                            </view>
                            <!-- TODO: 后端 - 关注作者接口 -->
                            <view class="follow-btn">关注</view>
                        </view>

                        <text class="preview-desc">{{ previewSpace.description }}</text>

                        <view class="preview-stats">
                            <!-- 后端：点赞数来自 /api/scene-scheme-actions/count/{schemeId}?type=like -->
                            <view class="stat-box">
                                <text class="stat-num">{{ previewSpace.likes }}</text>
                                <text class="stat-label">喜欢</text>
                            </view>
                            <view class="stat-divider"></view>
                            <view class="stat-box">
                                <text class="stat-num">{{ previewSpace.views }}</text>
                                <text class="stat-label">浏览</text>
                            </view>
                            <view class="stat-divider"></view>
                            <view class="stat-box">
                                <text class="stat-num">{{ previewSpace.comments }}</text>
                                <text class="stat-label">评论</text>
                            </view>
                        </view>
                    </view>

                    <view class="preview-actions">
                        <!-- TODO: 后端 - 进入展厅 /space/detail?id=previewSpace.id -->
                        <view class="action-btn primary" @tap="enterSpace(previewSpace)">
                            <text class="btn-icon">↗</text>
                            <text>进入展厅</text>
                        </view>
                        <view class="action-btn secondary" @tap="likeSpace(previewSpace)">
                            <text class="btn-icon">♡</text>
                            <text>点赞</text>
                        </view>
                    </view>
                </view>
            </view>

        </view>
    </common-layout>
</template>

<script>
import commonLayout from '@/components/common-layout/common-layout.vue';
import request, { BASE_URL } from '@/utils/request.js';

const ROOM_FILTERS = [
    { key: 'all', label: '全部', icon: '❧', roomId: '' },
    { key: 'small', label: '小型展厅', icon: '▧', roomId: 'small' },
    { key: 'medium', label: '中型展厅', icon: '▦', roomId: 'medium' },
    { key: 'large', label: '大型展厅', icon: '▩', roomId: 'large' },
    { key: 'studio', label: '阁楼工作室', icon: '▱', roomId: 'studio' },
    { key: 'garden', label: '庭院展厅', icon: '◌', roomId: 'garden' },
    { key: 'vault', label: '穹顶藏馆', icon: '◇', roomId: 'vault' },
];

const ICON_POOL = ['💎', '📷', '🏺', '🦋', '⚙️', '🎨', '🧭', '📻'];
const GRADIENT_POOL = [
    'linear-gradient(145deg, #9b7fa6 0%, #5c4b7a 100%)',
    'linear-gradient(145deg, #b5835a 0%, #7a4f2e 100%)',
    'linear-gradient(145deg, #6a8f7a 0%, #3d6050 100%)',
    'linear-gradient(145deg, #c9a87c 0%, #8b6340 100%)',
    'linear-gradient(145deg, #7a8fa8 0%, #4a6080 100%)',
];

const SAMPLE_SPACES = [
    {
        id: 'sample-mineral',
        schemeId: 'sample-mineral',
        title: '矿物晶体收藏馆',
        author: '石光标本室',
        icon: '💎',
        avatar: '💎',
        gradient: 'linear-gradient(145deg, #7E6A9E 0%, #3B315D 55%, #172231 100%)',
        likes: 328,
        views: 2156,
        comments: 41,
        time: '2小时前',
        createdAt: '2026-05-07T18:20:00',
        description: '荧石、紫水晶、黄铁矿与祖母绿按晶系陈列，像把地质年代切成一柜一柜的光。',
        roomType: '雅致中厅',
        roomId: 'medium',
        itemCount: 12,
        isMock: true,
    },
    {
        id: 'sample-camera',
        schemeId: 'sample-camera',
        title: '老式相机暗房',
        author: '光影记录者',
        icon: '📷',
        avatar: '📷',
        gradient: 'linear-gradient(145deg, #C49A6C 0%, #7E5839 52%, #2E211B 100%)',
        likes: 214,
        views: 1320,
        comments: 23,
        time: '45分钟前',
        createdAt: '2026-05-07T19:05:00',
        description: '从折叠皮腔到旁轴机身，展厅里保留了胶片时代的金属冷光和皮革纹理。',
        roomType: '玲珑小阁',
        roomId: 'small',
        itemCount: 8,
        isMock: true,
    },
    {
        id: 'sample-steam',
        schemeId: 'sample-steam',
        title: '蒸汽朋克机械厅',
        author: '齿轮档案员',
        icon: '⚙️',
        avatar: '⚙️',
        gradient: 'linear-gradient(145deg, #B77B52 0%, #76503A 48%, #34404A 100%)',
        likes: 192,
        views: 1088,
        comments: 17,
        time: '3小时前',
        createdAt: '2026-05-07T16:40:00',
        description: '黄铜齿轮、压力表和老式机械模型组成一条微型工业考古路线。',
        roomType: '阁楼工作室',
        roomId: 'studio',
        itemCount: 10,
        isMock: true,
    },
    {
        id: 'sample-fossil',
        schemeId: 'sample-fossil',
        title: '远古生物研究所',
        author: '化石猎人',
        icon: '🦴',
        avatar: '🦴',
        gradient: 'linear-gradient(145deg, #8A9674 0%, #53694F 50%, #243C35 100%)',
        likes: 168,
        views: 970,
        comments: 19,
        time: '1天前',
        createdAt: '2026-05-06T20:10:00',
        description: '三叶虫、菊石和恐龙骨骼切片被摆成一条生命演化年表。',
        roomType: '恢宏大厅',
        roomId: 'large',
        itemCount: 15,
        isMock: true,
    },
    {
        id: 'sample-bronze',
        schemeId: 'sample-bronze',
        title: '青铜器鉴赏室',
        author: '古器物札记',
        icon: '🏺',
        avatar: '🏺',
        gradient: 'linear-gradient(145deg, #789077 0%, #4D665A 48%, #28342F 100%)',
        likes: 136,
        views: 856,
        comments: 12,
        time: '2天前',
        createdAt: '2026-05-05T13:30:00',
        description: '器形、纹样与铭文分区展示，沉稳的青绿色调让每件器物都像刚从时间里出土。',
        roomType: '庭院展厅',
        roomId: 'garden',
        itemCount: 9,
        isMock: true,
    },
    {
        id: 'sample-butterfly',
        schemeId: 'sample-butterfly',
        title: '蝴蝶标本长廊',
        author: '自然精灵',
        icon: '🦋',
        avatar: '🦋',
        gradient: 'linear-gradient(145deg, #93A9A2 0%, #5E7772 50%, #2D4450 100%)',
        likes: 121,
        views: 704,
        comments: 8,
        time: '3天前',
        createdAt: '2026-05-04T09:00:00',
        description: '按地域与翅面纹理组织标本，像翻开一本缓慢发光的自然图谱。',
        roomType: '穹顶藏馆',
        roomId: 'vault',
        itemCount: 24,
        isMock: true,
    },
];

export default {
    components: { commonLayout },
    data() {
        return {
            keyword: '',
            activeFilter: 'all',
            filters: ROOM_FILTERS,
            isLoading: false,
            loadError: '',
            publicSpaces: [],
            hotSpaces: [],
            newSpaces: [],
            allSpaces: [],
            previewSpace: null,
            searchTimer: null,
        }
    },
    computed: {
        searchResults() {
            if (!this.keyword) return [];
            return this.publicSpaces;
        },
        filteredSpaces() {
            if (this.activeFilter === 'all') return this.allSpaces;
            return this.publicSpaces;
        },
        currentRoomId() {
            const filter = this.filters.find(item => item.key === this.activeFilter);
            return filter ? filter.roomId : '';
        },
    },
    watch: {
        keyword() {
            this.queueFetchPublicSchemes('keyword changed');
        },
        activeFilter() {
            this.fetchPublicSchemes('filter changed');
        },
    },
    onLoad() {
        console.log('[square] onLoad, BASE_URL =', BASE_URL);
        this.fetchPublicSchemes('page load');
    },
    beforeDestroy() {
        if (this.searchTimer) clearTimeout(this.searchTimer);
    },
    unmounted() {
        if (this.searchTimer) clearTimeout(this.searchTimer);
    },
    methods: {
        queueFetchPublicSchemes(reason) {
            if (this.searchTimer) clearTimeout(this.searchTimer);
            this.searchTimer = setTimeout(() => {
                this.fetchPublicSchemes(reason);
            }, 300);
        },
        async fetchPublicSchemes(reason = 'manual') {
            const params = {};
            const keyword = this.keyword.trim();
            if (keyword) params.keyword = keyword;
            if (this.currentRoomId) params.roomId = this.currentRoomId;

            this.isLoading = true;
            this.loadError = '';

            console.log('[square] fetch public schemes start:', {
                reason,
                endpoint: '/scene-schemes/public',
                params,
            });

            try {
                const rawList = await request({
                    url: '/scene-schemes/public',
                    method: 'GET',
                    data: params,
                });
                console.log('[square] public schemes raw response:', rawList);

                const list = Array.isArray(rawList) ? rawList : [];
                const mapped = list.map((item, index) => this.mapSchemeToSpace(item, index));
                console.log('[square] mapped public spaces:', mapped);

                if (mapped.length) {
                    this.publicSpaces = await this.enrichSpacesWithStats(mapped);
                } else {
                    this.publicSpaces = this.getSampleSpaces(params);
                    console.log('[square] backend returned empty list, using sample spaces for display:', this.publicSpaces);
                }
                this.splitSpaces(this.publicSpaces);

                console.log('[square] fetch public schemes done:', {
                    total: this.publicSpaces.length,
                    hot: this.hotSpaces.length,
                    newest: this.newSpaces.length,
                    rest: this.allSpaces.length,
                });
            } catch (err) {
                console.error('[square] fetch public schemes failed:', err);
                this.loadError = '展馆列表加载失败，请检查后端服务和控制台日志';
                this.publicSpaces = [];
                this.hotSpaces = [];
                this.newSpaces = [];
                this.allSpaces = [];
            } finally {
                this.isLoading = false;
            }
        },
        async enrichSpacesWithStats(spaces) {
            const enriched = await Promise.all(spaces.map(async (space) => {
                const next = { ...space };
                if (next.isMock) return next;
                try {
                    next.likes = await request({
                        url: `/scene-scheme-actions/count/${space.id}`,
                        method: 'GET',
                        data: { type: 'like' },
                    });
                    console.log('[square] like count loaded:', { schemeId: space.id, likes: next.likes });
                } catch (err) {
                    console.warn('[square] like count load failed:', { schemeId: space.id, err });
                }

                try {
                    const comments = await request({
                        url: `/scene-scheme-comments/scheme/${space.id}`,
                        method: 'GET',
                    });
                    next.comments = Array.isArray(comments) ? comments.length : 0;
                    console.log('[square] comments loaded:', { schemeId: space.id, comments: next.comments, raw: comments });
                } catch (err) {
                    console.warn('[square] comments load failed:', { schemeId: space.id, err });
                }

                return next;
            }));

            console.log('[square] enriched spaces:', enriched);
            return enriched;
        },
        splitSpaces(spaces) {
            const ranked = [...spaces].sort((a, b) => (Number(b.likes) || 0) - (Number(a.likes) || 0));
            this.hotSpaces = ranked.slice(0, 3).map((item, index) => ({
                ...item,
                rank: `NO.${index + 1}`,
            }));

            this.newSpaces = [...spaces]
                .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
                .slice(0, 3);

            const featuredIds = new Set([...this.hotSpaces, ...this.newSpaces].map(item => item.id));
            this.allSpaces = spaces.filter(item => !featuredIds.has(item.id));

            if (!this.allSpaces.length && this.activeFilter !== 'all') {
                this.allSpaces = spaces;
            }
        },
        getSampleSpaces(params = {}) {
            const keyword = (params.keyword || '').toLowerCase();
            const roomId = params.roomId || '';
            return SAMPLE_SPACES
                .filter(item => {
                    const keywordMatched = !keyword ||
                        item.title.toLowerCase().includes(keyword) ||
                        item.author.toLowerCase().includes(keyword) ||
                        item.description.toLowerCase().includes(keyword) ||
                        item.roomType.toLowerCase().includes(keyword);
                    const roomMatched = !roomId || item.roomId === roomId;
                    return keywordMatched && roomMatched;
                })
                .map(item => ({ ...item }));
        },
        mapSchemeToSpace(item, index) {
            const placement = Array.isArray(item.placements) && item.placements.length
                ? item.placements[0]
                : null;
            const fallbackIcon = ICON_POOL[index % ICON_POOL.length];
            const icon = this.resolveIcon(item.thumb || placement?.icon || fallbackIcon, fallbackIcon);
            const title = item.title || item.roomDisplayName || item.roomName || `展馆方案 ${item.schemeId || index + 1}`;

            return {
                id: item.schemeId,
                schemeId: item.schemeId,
                uid: item.uid,
                title,
                author: item.nickname || `策展人 ${item.uid || ''}`.trim(),
                authorId: item.uid,
                icon,
                gradient: GRADIENT_POOL[index % GRADIENT_POOL.length],
                coverImage: item.coverImageUrl || '',
                coverModelUrl: item.coverModelUrl || placement?.modelUrl || '',
                likes: Number(item.likeCount || item.likes || 0),
                views: Number(item.views || item.viewCount || 0),
                comments: Number(item.commentCount || 0),
                time: this.formatRelativeTime(item.createdAt),
                createdAt: item.createdAt,
                description: item.description || this.buildDescription(item),
                roomType: item.roomDisplayName || item.roomName || item.roomId || '未命名展厅',
                roomId: item.roomId,
                itemCount: item.itemCount || item.placementCount || (Array.isArray(item.placements) ? item.placements.length : 0),
                avatar: item.avatarUrl || icon,
                raw: item,
            };
        },
        resolveIcon(value, fallback) {
            const iconMap = {
                artifact: '🏺',
                mineral: '💎',
                fossil: '🦴',
                toy: '🚂',
                art: '🎨',
                audio: '📻',
            };
            return iconMap[value] || value || fallback;
        },
        isImageUrl(value) {
            return typeof value === 'string' && (
                /^https?:\/\//i.test(value) ||
                /^\/?uploads\//i.test(value) ||
                /\.(png|jpe?g|webp|gif|bmp|svg)(?:$|\?)/i.test(value)
            );
        },
        buildDescription(item) {
            const parts = [
                item.roomDisplayName || item.roomName,
                item.area,
                item.kind || item.shape,
            ].filter(Boolean);
            return parts.length
                ? `${parts.join(' · ')}，共陈列 ${item.itemCount || item.placementCount || 0} 件藏品。`
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
        openPreview(item) {
            console.log('[square] open preview:', item);
            this.previewSpace = item;
        },
        enterSpace(item) {
            console.log('[square] enter space:', { schemeId: item.id, raw: item.raw });
            if (item.isMock) {
                uni.showToast({ title: '示例展馆暂无详情', icon: 'none' });
                return;
            }
            this.previewSpace = null;
            uni.navigateTo({
                url: `/pages/space-detail/space-detail?id=${item.id}`
            });
        },
        async likeSpace(item) {
            if (item.isMock) {
                item.likes = Number(item.likes || 0) + 1;
                console.log('[square] sample like locally:', { schemeId: item.id, likes: item.likes });
                uni.showToast({ title: '已点赞示例', icon: 'none' });
                return;
            }
            const userInfo = uni.getStorageSync('userInfo') || {};
            const uid = userInfo.uid || item.uid;

            console.log('[square] like scheme start:', {
                endpoint: '/scene-scheme-actions/like',
                uid,
                schemeId: item.id,
            });

            if (!uid) {
                console.warn('[square] like scheme blocked: missing uid');
                uni.showToast({ title: '请先登录后点赞', icon: 'none' });
                return;
            }

            try {
                const result = await request({
                    url: '/scene-scheme-actions/like',
                    method: 'POST',
                    data: {
                        uid,
                        schemeId: item.id,
                    },
                });
                console.log('[square] like scheme response:', result);

                const delta = result === 'CANCELLED' ? -1 : 1;
                item.likes = Math.max(0, Number(item.likes || 0) + delta);
                uni.showToast({
                    title: result === 'CANCELLED' ? '已取消点赞' : '已点赞',
                    icon: 'none',
                });
            } catch (err) {
                console.error('[square] like scheme failed:', err);
            }
        },
    },
}
</script>

<style>
/* ══════════════════════════════════
   全局变量 — 米黄回忆风格系统
══════════════════════════════════ */
page {
    /* 底色 */
    --cream:        #FAF7F2;
    --warm:         #F2EDE4;
    --parchment:    #E8E0D0;
    --sepia-light:  #D4C5A9;

    /* 主色调 */
    --ink:          #3D3229;
    --ink-mid:      #6B5B4E;
    --ink-light:    #9A8878;
    --ink-faint:    #C4B5A5;

    /* 点缀色 */
    --rust:         #8B5E3C;
    --rust-dark:    #6B4426;
    --olive:        #6B7355;
    --slate:        #4E5D6C;

    /* 阴影 */
    --shadow-warm:  rgba(90, 65, 45, 0.10);
    --shadow-deep:  rgba(90, 65, 45, 0.18);

    /* 纸张纹理感 */
    --radius-card:  10px;
    --radius-sm:    6px;
}

/* ══ 页面底色 ══
   .frame-body 已是 flex 列容器，slot 内容直接撑满它的剩余高度。
   这里用 width:100% + flex:1 + overflow:hidden 接管整个可用区域，
   不依赖任何 vw / vh，避免把侧边栏和 frame 的 padding 算进去。
══ */
.community-page {
    width: 100%;
    /* 撑满 .frame-body 分配给 slot 的全部高度 */
    flex: 1;
    min-height: 0;          /* 关键：让 flex 子项可以收缩，scroll-view 才能取到有限高度 */
    background: var(--cream);
    display: flex;
    flex-direction: column;
    overflow: hidden;       /* 防止内容撑破 frame */
    box-sizing: border-box;
    position: relative;     /* 为弹窗 position:absolute 提供定位基准 */
    background-image:
        url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.75' numOctaves='4' stitchTiles='stitch'/%3E%3CfeColorMatrix type='saturate' values='0'/%3E%3C/filter%3E%3Crect width='300' height='300' filter='url(%23n)' opacity='0.035'/%3E%3C/svg%3E");
}

/* ══════════════════════════════════
   顶部 Header — 固定在 community-page 顶部，不占滚动区高度
══════════════════════════════════ */
.community-header {
    width: 100%;
    flex-shrink: 0;         /* 高度由内容决定，不被压缩 */
    position: relative;
    background: linear-gradient(175deg, #5C4435 0%, #3D2E22 100%);
    padding-bottom: 6px;
    overflow: hidden;
    box-sizing: border-box;
}

/* 纸纹装饰层 */
.header-paper {
    position: absolute;
    inset: 0;
    background-image:
        url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='4' stitchTiles='stitch'/%3E%3CfeColorMatrix type='saturate' values='0'/%3E%3C/filter%3E%3Crect width='200' height='200' filter='url(%23n)' opacity='0.06'/%3E%3C/svg%3E");
    pointer-events: none;
}

.header-ornament {
    position: absolute;
    top: 16px;
    font-size: 10px;
    color: rgba(201,168,124,0.35);
    letter-spacing: 2px;
}
.header-ornament.left  { left: 16px; }
.header-ornament.right { right: 16px; }

.header-content {
    position: relative;
    padding: 16px 16px 0;
}

/* 标题区 */
.header-top {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8px 0 18px;
    gap: 6px;
}

.title-divider {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 80%;
}

.divider-line {
    flex: 1;
    height: 1px;
    background: rgba(201,168,124,0.3);
}

.divider-diamond {
    font-size: 8px;
    color: rgba(201,168,124,0.5);
}

.page-title {
    font-size: 22px;
    font-weight: 700;
    color: #F2E6D4;
    letter-spacing: 3px;
    font-family: Georgia, 'Times New Roman', serif;
}

.page-subtitle {
    font-size: 11px;
    color: rgba(201,168,124,0.7);
    letter-spacing: 5px;
    font-family: Georgia, serif;
}

/* 搜索栏 */
.search-section {
    margin-bottom: 14px;
}

.search-bar {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 14px;
    background: rgba(250,247,242,0.97);
    border-radius: 8px;
    border: 1px solid rgba(201,168,124,0.25);
    box-shadow: 0 2px 8px rgba(0,0,0,0.15), inset 0 1px 0 rgba(255,255,255,0.8);
}

.search-icon {
    font-size: 18px;
    color: var(--ink-light);
    font-family: Georgia, serif;
    line-height: 1;
}

.search-input {
    flex: 1;
    font-size: 14px;
    color: var(--ink);
    font-family: Georgia, serif;
}

.search-placeholder {
    color: var(--ink-faint);
    font-style: italic;
}

.search-clear {
    font-size: 11px;
    color: var(--ink-light);
    padding: 4px 6px;
    background: var(--parchment);
    border-radius: 4px;
}

/* 筛选标签 */
.filter-section {
    margin-bottom: 2px;
}

.filter-scroll { white-space: nowrap; }

.filter-tabs {
    display: inline-flex;
    gap: 6px;
    padding: 2px 0 10px;
}

.filter-tab {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 5px 12px;
    background: rgba(255,255,255,0.10);
    border-radius: 4px;
    border: 1px solid rgba(201,168,124,0.20);
    white-space: nowrap;
    transition: all 0.25s ease;
}

.filter-tab.active {
    background: rgba(250,247,242,0.95);
    border-color: rgba(201,168,124,0.45);
    box-shadow: 0 2px 6px rgba(0,0,0,0.12);
}

.tab-icon {
    font-size: 12px;
    line-height: 1;
}

.tab-text {
    font-size: 12px;
    color: rgba(255,255,255,0.75);
    font-family: Georgia, serif;
}

.filter-tab.active .tab-text {
    color: var(--ink);
    font-weight: 600;
}

/* ══════════════════════════════════
   内容滚动区
   flex:1 + min-height:0 让 scroll-view 接管 header 以下的全部剩余空间，
   无需手动计算像素高度。
══════════════════════════════════ */
.content-scroll {
    flex: 1;
    min-height: 0;          /* 同上，必须加 */
    width: 100%;
    box-sizing: border-box;
    padding: 20px 14px;
    overflow-y: auto;       /* H5 fallback；小程序端 scroll-view 自己管滚动 */
}

/* 通用 section header */
.load-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 10px;
    min-height: 180px;
    padding: 28px 18px;
    text-align: center;
    box-sizing: border-box;
}

.load-state-title {
    font-size: 14px;
    font-weight: 700;
    color: var(--ink);
    font-family: Georgia, serif;
}

.load-state-sub {
    font-size: 12px;
    color: var(--ink-light);
    font-family: Georgia, serif;
}

.load-state.error .load-state-title {
    color: var(--rust-dark);
}

.retry-btn {
    padding: 8px 14px;
    background: var(--rust-dark);
    color: #F2E6D4;
    border-radius: var(--radius-sm);
    font-size: 12px;
    font-family: Georgia, serif;
}

.thumb-image,
.preview-cover-image {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
}

.sample-badge,
.preview-sample-badge {
    position: absolute;
    z-index: 3;
    padding: 2px 7px;
    background: rgba(250,247,242,0.88);
    border: 1px solid rgba(201,168,124,0.42);
    border-radius: 3px;
    color: var(--rust-dark);
    font-size: 9px;
    font-family: Georgia, serif;
    letter-spacing: 1px;
    box-shadow: 0 1px 4px rgba(0,0,0,0.12);
}

.sample-badge {
    right: 8px;
    bottom: 8px;
}

.preview-sample-badge {
    top: 10px;
    left: 10px;
    font-size: 10px;
}

.section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 14px;
}

.section-label {
    display: flex;
    align-items: baseline;
    gap: 6px;
}

.label-mark {
    font-size: 16px;
    color: var(--rust);
    font-family: Georgia, serif;
    line-height: 1;
}

.section-title {
    font-size: 16px;
    font-weight: 700;
    color: var(--ink);
    font-family: Georgia, 'Times New Roman', serif;
    letter-spacing: 1px;
}

.section-tag {
    font-size: 10px;
    color: var(--ink-light);
    padding: 2px 8px;
    border: 1px dashed var(--sepia-light);
    border-radius: 3px;
    font-family: Georgia, serif;
    font-style: italic;
}

/* 章节分隔线 */
.chapter-rule {
    display: flex;
    align-items: center;
    gap: 10px;
    margin: 6px 0 20px;
}

.rule-line {
    flex: 1;
    height: 1px;
    background: linear-gradient(to right, transparent, var(--sepia-light), transparent);
}

.rule-text {
    font-size: 12px;
    color: var(--ink-faint);
    letter-spacing: 4px;
}

/* ══════════════════════════════════
   热门展厅
══════════════════════════════════ */
.section-hot {
    margin-bottom: 20px;
}

.hot-scroll { white-space: nowrap; }

.hot-list {
    display: inline-flex;
    gap: 10px;
    padding-bottom: 4px;
}

.hot-card {
    width: 148px;
    background: var(--warm);
    border-radius: var(--radius-card);
    overflow: hidden;
    box-shadow: 0 3px 10px var(--shadow-warm), 0 1px 0 rgba(255,255,255,0.6) inset;
    border: 1px solid rgba(201,168,124,0.18);
}

.hot-thumb {
    position: relative;
    height: 96px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
}

.hot-icon {
    font-size: 38px;
    position: relative;
    z-index: 2;
    /* sepia 暖色滤镜 */
    filter: sepia(15%) brightness(0.95);
}

.hot-rank-badge {
    position: absolute;
    top: 8px;
    left: 8px;
    padding: 2px 7px;
    background: rgba(30,20,10,0.65);
    border-radius: 3px;
    font-size: 9px;
    color: #D4A85A;
    font-family: Georgia, serif;
    font-weight: 700;
    letter-spacing: 1px;
    z-index: 3;
}

/* 暗角效果 */
.thumb-vignette {
    position: absolute;
    inset: 0;
    background: radial-gradient(ellipse at center, transparent 50%, rgba(0,0,0,0.18) 100%);
    pointer-events: none;
    z-index: 1;
}

.hot-info {
    padding: 10px 10px 4px;
}

.hot-name {
    display: block;
    font-size: 13px;
    font-weight: 600;
    color: var(--ink);
    font-family: Georgia, serif;
    margin-bottom: 3px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.hot-author {
    font-size: 11px;
    color: var(--ink-light);
    font-style: italic;
    font-family: Georgia, serif;
}

.hot-stats {
    display: flex;
    gap: 8px;
    padding: 8px 10px 10px;
}

.stat-pill {
    font-size: 11px;
    color: var(--ink-light);
    padding: 1px 6px;
    background: var(--parchment);
    border-radius: 3px;
}

/* ══════════════════════════════════
   最新发布
══════════════════════════════════ */
.section-new {
    margin-bottom: 20px;
}

.new-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.new-card {
    display: flex;
    background: var(--warm);
    border-radius: var(--radius-card);
    overflow: hidden;
    box-shadow: 0 2px 8px var(--shadow-warm);
    border: 1px solid rgba(201,168,124,0.15);
}

.new-thumb {
    width: 86px;
    height: 86px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    position: relative;
    overflow: hidden;
}

.new-thumb::after {
    content: '';
    position: absolute;
    inset: 0;
    background: radial-gradient(ellipse at center, transparent 40%, rgba(0,0,0,0.15) 100%);
}

.new-icon {
    font-size: 34px;
    filter: sepia(15%) brightness(0.95);
    position: relative;
    z-index: 1;
}

.new-content {
    flex: 1;
    padding: 11px 12px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-width: 0;
}

.new-top {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 4px;
}

.new-name {
    font-size: 14px;
    font-weight: 600;
    color: var(--ink);
    font-family: Georgia, serif;
    flex: 1;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.new-time {
    font-size: 10px;
    flex-shrink: 0;
    color: var(--ink-faint);
    font-style: italic;
    font-family: Georgia, serif;
    margin-left: 6px;
    margin-top: 2px;
}

.new-desc {
    font-size: 11px;
    color: var(--ink-mid);
    line-height: 1.55;
    font-family: Georgia, serif;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.new-bottom {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.author-tag {
    display: flex;
    align-items: center;
    gap: 4px;
}

.author-avatar { font-size: 13px; }

.author-avatar-img {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
}

.author-name {
    font-size: 11px;
    color: var(--ink-light);
    font-style: italic;
    font-family: Georgia, serif;
}

.new-stats {
    display: flex;
    gap: 8px;
    font-size: 11px;
    color: var(--ink-faint);
    flex-shrink: 0;
}

/* ══════════════════════════════════
   分类网格
══════════════════════════════════ */
.category-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
}

.category-card {
    background: var(--warm);
    border-radius: var(--radius-card);
    overflow: hidden;
    box-shadow: 0 2px 8px var(--shadow-warm);
    border: 1px solid rgba(201,168,124,0.15);
}

.category-thumb {
    position: relative;
    height: 110px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
}

.category-thumb::after {
    content: '';
    position: absolute;
    inset: 0;
    background: radial-gradient(ellipse at center, transparent 45%, rgba(0,0,0,0.18) 100%);
}

.category-icon {
    font-size: 44px;
    filter: sepia(15%) brightness(0.95);
    position: relative;
    z-index: 1;
}

.category-badge {
    position: absolute;
    top: 8px;
    right: 8px;
    padding: 2px 7px;
    background: rgba(30,20,10,0.55);
    border-radius: 3px;
    font-size: 10px;
    color: rgba(255,255,255,0.85);
    z-index: 2;
}

.category-info { padding: 10px 10px 4px; }

.category-name {
    display: block;
    font-size: 13px;
    font-weight: 600;
    color: var(--ink);
    font-family: Georgia, serif;
    margin-bottom: 4px;
}

.category-desc {
    font-size: 11px;
    color: var(--ink-light);
    line-height: 1.5;
    font-family: Georgia, serif;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    margin-bottom: 6px;
}

.category-author {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 11px;
    color: var(--ink-mid);
    font-style: italic;
    font-family: Georgia, serif;
}

.category-author-avatar-img {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
}

.category-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 6px 10px 10px;
    border-top: 1px dashed var(--sepia-light);
    margin: 0 6px;
}

.footer-stats {
    display: flex;
    gap: 8px;
    font-size: 11px;
    color: var(--ink-faint);
}

.footer-time {
    font-size: 10px;
    color: var(--ink-faint);
    font-style: italic;
    font-family: Georgia, serif;
}

/* ══════════════════════════════════
   搜索结果
══════════════════════════════════ */
.result-header { margin-bottom: 12px; }

.result-count {
    font-size: 13px;
    color: var(--ink-mid);
    font-family: Georgia, serif;
    font-style: italic;
}

.result-num {
    font-weight: 700;
    font-style: normal;
    color: var(--rust);
}

.result-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
}

.result-card {
    background: var(--warm);
    border-radius: var(--radius-card);
    overflow: hidden;
    box-shadow: 0 2px 8px var(--shadow-warm);
    border: 1px solid rgba(201,168,124,0.15);
}

.result-thumb {
    height: 76px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
}

.result-thumb::after {
    content: '';
    position: absolute;
    inset: 0;
    background: radial-gradient(ellipse at center, transparent 45%, rgba(0,0,0,0.15) 100%);
}

.result-icon {
    font-size: 30px;
    filter: sepia(15%);
    position: relative;
    z-index: 1;
}

.result-info { padding: 10px 10px 4px; }

.result-name {
    display: block;
    font-size: 13px;
    font-weight: 600;
    color: var(--ink);
    font-family: Georgia, serif;
    margin-bottom: 2px;
}

.result-author {
    font-size: 11px;
    color: var(--ink-light);
    font-style: italic;
    font-family: Georgia, serif;
}

.result-stats {
    display: flex;
    gap: 8px;
    padding: 6px 10px 10px;
    font-size: 11px;
    color: var(--ink-faint);
}

/* ══════════════════════════════════
   空状态
══════════════════════════════════ */
.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 0 40px;
}

.empty-icon {
    font-size: 56px;
    opacity: 0.35;
    margin-bottom: 14px;
    filter: sepia(30%);
}

.empty-title {
    font-size: 15px;
    font-weight: 600;
    color: var(--ink-mid);
    font-family: Georgia, serif;
    margin-bottom: 6px;
}

.empty-sub {
    font-size: 12px;
    color: var(--ink-faint);
    font-style: italic;
    font-family: Georgia, serif;
}

.safe-bottom { height: 40px; }

/* ══════════════════════════════════
   预览弹窗
   改用 position:absolute（相对于 .community-page 的 position:relative），
   不再用 fixed + vw/vh，避免遮住侧边栏或超出 frame 边界。
══════════════════════════════════ */
.preview-overlay {
    position: absolute;     /* 相对于 .community-page，而非视口 */
    inset: 0;
    background: rgba(30,20,10,0.55);
    display: flex;
    align-items: flex-end;
    z-index: 100;
}

.preview-modal {
    width: 100%;
    background: var(--cream);
    border-radius: 18px 18px 0 0;
    max-height: 88%;        /* 相对于 .community-page 高度，而非 88vh */
    overflow-y: auto;
    box-shadow: 0 -4px 24px rgba(0,0,0,0.2);
    /* 纸纹 */
    background-image:
        url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.75' numOctaves='4' stitchTiles='stitch'/%3E%3CfeColorMatrix type='saturate' values='0'/%3E%3C/filter%3E%3Crect width='200' height='200' filter='url(%23n)' opacity='0.04'/%3E%3C/svg%3E");
}

.modal-handle {
    width: 36px;
    height: 4px;
    background: var(--sepia-light);
    border-radius: 2px;
    margin: 12px auto 0;
}

.preview-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 16px 0;
}

.preview-title {
    font-size: 18px;
    font-weight: 700;
    color: var(--ink);
    font-family: Georgia, 'Times New Roman', serif;
    letter-spacing: 1px;
}

.preview-close {
    width: 28px;
    height: 28px;
    background: var(--parchment);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    color: var(--ink-mid);
}

.preview-banner {
    margin: 12px 16px;
    height: 150px;
    border-radius: var(--radius-card);
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
}

.banner-vignette {
    position: absolute;
    inset: 0;
    background: radial-gradient(ellipse at center, transparent 40%, rgba(0,0,0,0.25) 100%);
}

.preview-icon {
    font-size: 68px;
    filter: sepia(15%) brightness(0.95);
    position: relative;
    z-index: 1;
}

.preview-room-tag {
    position: absolute;
    bottom: 10px;
    right: 10px;
    padding: 3px 10px;
    background: rgba(30,20,10,0.6);
    border-radius: 4px;
    font-size: 11px;
    color: rgba(240,220,190,0.9);
    font-family: Georgia, serif;
    font-style: italic;
    z-index: 2;
}

.preview-content {
    padding: 0 16px 12px;
}

.preview-author-row {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;
}

.preview-avatar { font-size: 28px; }

.preview-avatar-img {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
    border: 2px solid rgba(201,168,124,0.35);
}

.preview-author-info {
    display: flex;
    flex-direction: column;
    flex: 1;
}

.preview-author-name {
    font-size: 14px;
    font-weight: 600;
    color: var(--ink);
    font-family: Georgia, serif;
}

.preview-author-time {
    font-size: 11px;
    color: var(--ink-faint);
    font-style: italic;
    font-family: Georgia, serif;
}

.follow-btn {
    padding: 4px 12px;
    border: 1px solid var(--rust);
    border-radius: 4px;
    font-size: 12px;
    color: var(--rust);
    font-family: Georgia, serif;
}

.preview-desc {
    font-size: 13px;
    color: var(--ink-mid);
    line-height: 1.7;
    font-family: Georgia, serif;
    margin-bottom: 14px;
}

.preview-stats {
    display: flex;
    justify-content: space-around;
    align-items: center;
    padding: 14px 0;
    border-top: 1px dashed var(--sepia-light);
    border-bottom: 1px dashed var(--sepia-light);
    margin-bottom: 4px;
}

.stat-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
}

.stat-divider {
    width: 1px;
    height: 28px;
    background: var(--sepia-light);
}

.stat-num {
    font-size: 20px;
    font-weight: 700;
    color: var(--ink);
    font-family: Georgia, serif;
}

.stat-label {
    font-size: 11px;
    color: var(--ink-faint);
    font-style: italic;
    font-family: Georgia, serif;
}

/* 弹窗操作按钮 */
.preview-actions {
    display: flex;
    gap: 10px;
    padding: 14px 16px 20px;
    box-sizing: border-box;
}

.action-btn {
    flex: 1;
    padding: 13px;
    border-radius: var(--radius-card);
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    font-size: 14px;
    font-weight: 600;
    font-family: Georgia, serif;
    letter-spacing: 0.5px;
}

.btn-icon {
    font-size: 16px;
    font-style: normal;
}

.action-btn.primary {
    background: linear-gradient(145deg, #6B4426 0%, #4A2E18 100%);
    color: #F2E6D4;
    box-shadow: 0 3px 8px rgba(90,50,20,0.3), inset 0 1px 0 rgba(255,255,255,0.1);
}

.action-btn.secondary {
    background: var(--warm);
    color: var(--ink-mid);
    border: 1px solid var(--sepia-light);
    box-shadow: 0 2px 6px var(--shadow-warm);
}
</style>
