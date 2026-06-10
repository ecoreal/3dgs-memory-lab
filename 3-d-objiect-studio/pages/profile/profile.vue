<template>
    <common-layout pageTitle="策展工作室">
        <view class="profile-page">
            <scroll-view class="page-scroll" scroll-y="true" show-scrollbar="true" :enhanced="true">
                
                <view class="profile-cover-section">
                    <view class="cover-bg">
                        <view class="cover-pattern"></view>
                    </view>
                    
                    <view class="profile-hero-card">
                        <view class="hero-avatar-wrap">
                            <image class="hero-avatar" :src="displayAvatarUrl" mode="aspectFill"></image>
                            <view class="avatar-ring"></view>
                            <button class="avatar-edit-btn" @click="editProfile">✎</button>
                        </view> 
                        <view class="hero-info">
                            <text class="hero-nickname">{{ userInfo.nickname || '未设置昵称' }}</text>
                            <text class="hero-email">{{ userInfo.email || '' }}</text>
                            <text class="hero-bio">{{ userInfo.bio || '这个人很懒，什么都没写' }}</text>
                        </view>
                    </view>
                    
                    <view class="stats-strip">
                        <view class="stat-item">
                            <text class="stat-num">{{ stats.schemes || 0 }}</text>
                            <text class="stat-label">方案</text>
                        </view>
                        <view class="stat-divider"></view>
                        <view class="stat-item">
                            <text class="stat-num">{{ stats.models || 0 }}</text>
                            <text class="stat-label">模型</text>
                        </view>
                        <view class="stat-divider"></view>
                        <view class="stat-item">
                            <text class="stat-num">{{ stats.storageUsed || '0' }}</text>
                            <text class="stat-label">存储</text>
                        </view>
                    </view>
                </view>
                
                <view class="hall-entrance-section" @tap.stop="goSceneFloor" @click.stop="goSceneFloor">
                    <view class="hall-entrance-bg">
                        <view class="hall-entrance-glow"></view>
                    </view>
                    <view class="hall-entrance-content">
                        <view class="hall-icon-wrap">
                            <text class="hall-main-icon">🏛️</text>
                            <view class="hall-icon-ring ring-1"></view>
                            <view class="hall-icon-ring ring-2"></view>
                        </view>
                        <view class="hall-text-wrap">
                            <text class="hall-title">选择户型</text>
                            <text class="hall-desc">打造专属数字展馆空间</text>
                        </view>
                        <view class="hall-action">
                            <text class="hall-action-text">立即选择</text>
                            <text class="hall-arrow">→</text>
                        </view>
                    </view>
                </view>
                
                <view class="collection-section">
                    <view class="section-header">
                        <text class="section-title">我的方案</text>
                        <button class="section-action" @click="openSceneMetaModal">展馆信息</button>
                    </view>
                    
                    <view class="schemes-list" v-if="sceneSchemes.length > 0">
                        <view v-for="(scheme, idx) in sceneSchemes" :key="scheme.id" class="scheme-card"
                              @click="loadSceneScheme(scheme)">
                            <text class="scheme-icon">{{ scheme.thumb || '🏛️' }}</text>
                            <view class="scheme-info">
                                <text class="scheme-title">{{ scheme.title }}</text>
                                <text class="scheme-meta">{{ scheme.room }} · {{ scheme.items }}件藏品 · {{ formatDate(scheme.created) }}</text>
                            </view>
                            <text class="scheme-delete" @click.stop="deleteSceneScheme(idx)">删除</text>
                        </view>
                    </view>
                    
                    <view class="empty-collection" v-else>
                        <text class="empty-icon">📭</text>
                        <text class="empty-text">暂无方案</text>
                        <text class="empty-sub">点击上方选择户型开始布展</text>
                    </view>
                </view>
                
                <view style="height: 40px"></view>
            </scroll-view>
        </view>
    </common-layout>
    
    <view class="edit-overlay" v-if="isEditing" @touchmove.stop.prevent="noop">
        <view class="edit-modal-modern" @touchmove.stop.prevent="noop">
            <view class="edit-header">
                <text class="edit-title">编辑资料</text>
                <view class="edit-close" @click="cancelEdit">✕</view>
            </view>
            
            <view class="edit-body">
                <view class="avatar-upload-center" @click="chooseAvatar">
                    <image class="edit-avatar" :src="displayTempAvatarUrl" mode="aspectFill"/>
                    <text class="upload-tip">点击更换头像</text>
                </view>
                
                <view class="form-row">
                    <text class="form-label">昵称</text>
                    <input v-model="tempUserInfo.nickname" placeholder="请输入昵称" class="form-input"/>
                </view>
                
                <view class="form-row">
                    <text class="form-label">简介</text>
                    <textarea v-model="tempUserInfo.bio" placeholder="介绍一下自己..." class="form-textarea"></textarea>
                </view>
            </view>
            
            <view class="edit-footer">
                <button class="btn-secondary" @click="cancelEdit">取消</button>
                <button class="btn-primary-full" @click="saveProfile">保存</button>
            </view>
        </view>
    </view>

    <view class="edit-overlay" v-if="sceneMetaModalVisible" @touchmove.stop.prevent="noop">
        <view class="edit-modal-modern scene-meta-modal" @touchmove.stop.prevent="noop">
            <view class="edit-header">
                <text class="edit-title">展馆信息</text>
                <view class="edit-close" @click="cancelSceneMetaEdit">✕</view>
            </view>

            <view class="edit-body">
                <view class="scene-cover-upload" @click="chooseSceneCover">
                    <image class="scene-cover-preview" :src="displayTempSceneCoverUrl" mode="aspectFill"/>
                    <text class="upload-tip">点击选择展馆封面</text>
                </view>

                <button class="btn-secondary scene-cover-pick" @click.stop="useFirstPlacedCover">使用首个藏品封面</button>

                <view class="form-row">
                    <text class="form-label">展馆标题</text>
                    <input v-model="tempSceneMeta.title" placeholder="请输入展馆标题" class="form-input"/>
                </view>

                <view class="form-row">
                    <text class="form-label">展馆简介</text>
                    <textarea v-model="tempSceneMeta.description" placeholder="介绍这个展馆的主题、故事或策展思路..." class="form-textarea scene-meta-textarea"></textarea>
                </view>
            </view>

            <view class="edit-footer">
                <button class="btn-secondary" @click="cancelSceneMetaEdit">取消</button>
                <button class="btn-primary-full" @click="saveSceneMetaEdit">确定</button>
            </view>
        </view>
    </view>

    <view class="scene-floor" v-if="sceneView === 'floor'">
        <view class="scene-hub-header">
            <button class="scene-back-btn" @click="sceneView = ''">‹ 返回</button>
            <text class="scene-hub-title">📐 选择户型</text>
            <button class="scene-back-btn" style="background:#8B5A2B;color:#fff;" @click="goPlaceFromFloor">下一步 ›</button>
        </view>
        <scroll-view class="scene-hub-body" scroll-y="true">
            <view class="scene-room-intro">
                <text class="scene-room-kicker">展馆模板</text>
                <text class="scene-room-copy">选择适合本次策展规模的空间，之后可以进入 3D 布展继续摆放藏品。</text>
            </view>
            <view class="scene-room-grid">
                <view class="scene-room-card" :class="{selected: sceneSelectedRoom === 'small'}" @tap.stop="selectSceneRoom({id:'small',name:'玲珑小阁',shape:'shape-bed',area:'小',lenM:4,widM:3,heightM:3,kind:'flat',dimLine:'适中小空间，适合亲密、聚焦的主题陈列'})">
                    <view class="r-icon-wrap"><text class="r-icon">🏠</text></view>
                    <view class="r-name">玲珑小阁</view>
                    <view class="r-size">4m × 3m</view>
                    <view class="r-desc">轻量陈列</view>
                    <text class="r-check" v-if="sceneSelectedRoom === 'small'">✓</text>
                </view>
                <view class="scene-room-card" :class="{selected: sceneSelectedRoom === 'medium'}" @tap.stop="selectSceneRoom({id:'medium',name:'雅致中厅',shape:'shape-bed',area:'中',lenM:6,widM:4,heightM:3.2,kind:'flat',dimLine:'标准空间，适合均衡清晰的展线组织'})">
                    <view class="r-icon-wrap"><text class="r-icon">🏛️</text></view>
                    <view class="r-name">雅致中厅</view>
                    <view class="r-size">6m × 4m</view>
                    <view class="r-desc">标准空间</view>
                    <text class="r-check" v-if="sceneSelectedRoom === 'medium'">✓</text>
                </view>
                <view class="scene-room-card" :class="{selected: sceneSelectedRoom === 'large'}" @tap.stop="selectSceneRoom({id:'large',name:'恢宏大厅',shape:'shape-bed',area:'大',lenM:9,widM:6,heightM:4,kind:'flat',dimLine:'宽敞空间，适合叙事感更强的大型主题展'})">
                    <view class="r-icon-wrap"><text class="r-icon">🏯</text></view>
                    <view class="r-name">恢宏大厅</view>
                    <view class="r-size">9m × 6m</view>
                    <view class="r-desc">大型叙事</view>
                    <text class="r-check" v-if="sceneSelectedRoom === 'large'">✓</text>
                </view>
                <view class="scene-room-card" :class="{selected: sceneSelectedRoom === 'studio'}" @tap.stop="selectSceneRoom({id:'studio',name:'阁楼工作室',shape:'shape-bed',area:'特殊',lenM:8,widM:5,heightM:5,kind:'loft',eaveM:2,dimLine:'挑高空间，适合工作室感和立体层次陈列'})">
                    <view class="r-icon-wrap"><text class="r-icon">🗼</text></view>
                    <view class="r-name">阁楼工作室</view>
                    <view class="r-size">8m × 5m 挑高</view>
                    <view class="r-desc">挑高结构</view>
                    <text class="r-check" v-if="sceneSelectedRoom === 'studio'">✓</text>
                </view>
                <view class="scene-room-card" :class="{selected: sceneSelectedRoom === 'garden'}" @tap.stop="selectSceneRoom({id:'garden',name:'庭院展厅',shape:'shape-bed',area:'户外',lenM:10,widM:8,heightM:3.5,kind:'garden',dimLine:'半户外庭院，适合开放、轻松的漫游式陈列'})">
                    <view class="r-icon-wrap"><text class="r-icon">⛩️</text></view>
                    <view class="r-name">庭院展厅</view>
                    <view class="r-size">10m × 8m</view>
                    <view class="r-desc">半户外陈列</view>
                    <text class="r-check" v-if="sceneSelectedRoom === 'garden'">✓</text>
                </view>
                <view class="scene-room-card" :class="{selected: sceneSelectedRoom === 'vault'}" @tap.stop="selectSceneRoom({id:'vault',name:'穹顶藏馆',shape:'shape-vault',area:'特殊',lenM:7,widM:7,heightM:6,kind:'vault',dimLine:'穹顶圆形空间，适合中心聚焦和环形参观动线'})">
                    <view class="r-icon-wrap"><text class="r-icon">🛕</text></view>
                    <view class="r-name">穹顶藏馆</view>
                    <view class="r-size">7m × 7m 穹顶</view>
                    <view class="r-desc">圆形动线</view>
                    <text class="r-check" v-if="sceneSelectedRoom === 'vault'">✓</text>
                </view>
            </view>
            <view class="scene-room-recommend">
                <text class="recommend-mark">策展建议</text>
                <text class="recommend-text">每个展馆推荐放 4-6 件展品，留出足够视线和动线空间。</text>
            </view>
            <view v-if="sceneRoomDimLine" class="scene-room-note">
                <text class="scene-room-note-label">已选空间</text>
                <text class="scene-room-note-text">{{ sceneRoomDimLine }}</text>
            </view>
        </scroll-view>
    </view>

    <!-- 布展模式：用 iframe 加载独立的 3D 查看器 -->
    <view class="scene-placement" v-if="sceneView === 'placement'">
        <view class="scene-hub-header">
            <button class="scene-back-btn" @click="sceneView = 'floor'">‹ 返回</button>
            <text class="scene-hub-title">🎨 布展模式 · {{ sceneRoomName }}</text>
            <view class="scene-header-actions">
                <button class="scene-back-btn scene-meta-entry-btn" @click="openSceneMetaModal">展馆信息</button>
                <button class="scene-back-btn" style="background:#5A8A4A;color:#fff;" @click="saveSceneScheme">💾 保存</button>
            </view>
        </view>
        <view class="scene-stage">
            <iframe
                ref="sceneIframe"
                v-if="sceneIframeSrc"
				:key="iframRefreshKey"
                :src="sceneIframeSrc"
                class="scene-iframe"
                frameborder="0"
                allow="camera;microphone"
                @load="onIframeLoad"
            ></iframe>
            <view class="scene-placement-panel">
                <view class="scene-panel-title">📦 选择藏品点击放置</view>
                <scroll-view class="scene-item-list scene-model-strip" scroll-x="true">
                    <view v-for="(m, idx) in sceneMyModelsList" :key="m.id"
                          class="scene-item-row" :style="{opacity: sceneActiveId === m.id ? 1 : 0.65}"
                          @tap.stop="pickSceneModel(m)">
                        <text class="s-icon">{{ m.icon || '📦' }}</text>
                        <text class="scene-model-name">{{ m.name }}</text>
                    </view>
                    <view v-if="!sceneMyModelsList.length" style="font-size:12px;color:#8A7A68;font-family:Georgia,serif;text-align:center;padding:10px;">
                        暂无可布展的藏品<br/><text style="font-size:11px;color:#A09080;">请先在建模工坊创建3D模型</text>
                    </view>
                </scroll-view>
                <view v-if="scenePlaced.length" style="margin-top:10px;padding-top:10px;border-top:1px solid #C4A882;">
                    <view class="scene-panel-title">🏛️ 已放置 ({{ scenePlaced.length }})</view>
                    <view class="scene-item-list">
                        <view v-for="(p, idx) in scenePlaced" :key="p.uid" class="scene-item-row">
                            <text class="s-icon">{{ p.icon }}</text>
                            <text style="flex:1;font-size:11px;">{{ p.name }}</text>
                            <text class="s-del" @click.stop="removeScenePlaced(idx)">✕</text>
                        </view>
                    </view>
                </view>
            </view>
        </view>
        <view class="scene-placement-bar">
            <view class="scene-btn scene-btn-secondary" @tap.stop="resetSceneCamera" @click.stop="resetSceneCamera">🔄 重置视角</view>
            <view class="scene-btn scene-btn-danger" @tap.stop="clearSceneAll" @click.stop="clearSceneAll">🗑️ 清空</view>
        </view>
    </view>
</template>

<script>
import MyLayout from '@/components/common-layout/common-layout.vue'
import request, { BASE_URL } from '@/utils/request.js'

const HOST_URL = BASE_URL.replace(/\/api\/?$/, '')

export default {
    components: {
        'common-layout': MyLayout
    },
    data() {
        return {
            userInfo: {
                avatarUrl: '',
                nickname: '加载中...',
                email: '',
                bio: ''
            },
            stats: {
                schemes: 0,
                models: 0,
                storageUsed: '0 GB'
            },
            files: [],
            isEditing: false,
            tempUserInfo: {
                avatarUrl: '',
                nickname: '',
                bio: ''
            },
            sceneMetaModalVisible: false,
            sceneMetaDescription: '',
            sceneMetaCoverImageUrl: '',
            tempSceneMeta: {
                title: '',
                description: '',
                coverImageUrl: ''
            },
            
            sceneView: '',
            sceneSelectedRoom: null,
            sceneRoomName: '尚未选择展厅',
            sceneRoomDimLine: '',
            sceneRoomLenM: 6.0,
            sceneRoomWidM: 4.0,
            sceneRoomHeightM: 3.2,
            sceneRoomShape: 'shape-bed',
            sceneRoomKind: 'flat',
            sceneRoomEaveM: null,
            sceneActiveId: '',
            sceneActiveIcon: '📦',
            sceneActiveName: '',
            sceneActiveItem: null,
            scenePlaced: [],
            mySchemes: [],
            sceneMyModels: [],
            sceneSchemes: [],
            sceneFloorRotationDeg: 0,
            scenePlacementDirty: false,
            scenePlacementExitModalVisible: false,
            scenePendingExitAction: null,
            scenePlacementFromFloor: false,
            sceneCurrentSchemeId: null,
            sceneLoadedSchemeTitle: '',
			currentRoomData:null,
			iframRefreshKey:0,
            sceneRooms: [
                { id: 'small', name: '玲珑小阁', shape: 'shape-bed', area: '小', lenM: 4.0, widM: 3.0, heightM: 3.0, kind: 'flat', dimLine: '4m × 3m · 适合亲密、聚焦的主题陈列' },
                { id: 'medium', name: '雅致中厅', shape: 'shape-bed', area: '中', lenM: 6.0, widM: 4.0, heightM: 3.2, kind: 'flat', dimLine: '6m × 4m · 适合均衡清晰的展线组织' },
                { id: 'large', name: '恢宏大厅', shape: 'shape-bed', area: '大', lenM: 9.0, widM: 6.0, heightM: 4.0, kind: 'flat', dimLine: '9m × 6m · 适合叙事感更强的大型主题展' },
                { id: 'studio', name: '阁楼工作室', shape: 'shape-bed', area: '特殊', lenM: 8.0, widM: 5.0, heightM: 5.0, kind: 'loft', eaveM: 2.5, dimLine: '8m × 5m · 适合工作室感和立体层次陈列' },
                { id: 'garden', name: '庭院展厅', shape: 'shape-bed', area: '户外', lenM: 10.0, widM: 8.0, heightM: 3.5, kind: 'garden', dimLine: '10m × 8m · 适合开放、轻松的漫游式陈列' },
                { id: 'vault', name: '穹顶藏馆', shape: 'shape-vault', area: '特殊', lenM: 7.0, widM: 7.0, heightM: 6.0, kind: 'vault', dimLine: '7m × 7m · 适合中心聚焦和环形参观动线' }
            ]
        }
    },
    onShow() {
        this.fetchUserInfo()
        this.fetchUserModels()
        this.loadSchemesFromStorage()
		const saved = uni.getStorageSync('sceneSelectedRoom');
		this.currentRoomData = saved && saved.id ? saved : null;
    },
    mounted() {
        if (typeof window !== 'undefined') {
            window.addEventListener('message', this.handleSceneViewerMessage);
        }
    },
    beforeUnmount() {
        if (typeof window !== 'undefined') {
            window.removeEventListener('message', this.handleSceneViewerMessage);
        }
    },
    beforeDestroy() {
        if (typeof window !== 'undefined') {
            window.removeEventListener('message', this.handleSceneViewerMessage);
        }
    },
    computed: {
        displayAvatarUrl() {
            return this.normalizeUrl(this.userInfo.avatarUrl) || 'https://via.placeholder.com/150'
        },
        displayTempAvatarUrl() {
            return this.normalizeUrl(this.tempUserInfo.avatarUrl) || 'https://via.placeholder.com/150'
        },
        displayTempSceneCoverUrl() {
            return this.normalizePreviewUrl(this.tempSceneMeta.coverImageUrl)
                || this.getDefaultSceneCover()
        },
        sceneMyModelsList() {
            if (!this.files || !this.files.length) {
                return []
            }
            return this.files.map(file => ({
                id: file.id,
                name: file.name || '未命名模型',
                icon: this._getModelIcon(file),
                cover: file.cover || file.thumbnail || '',
                modelUrl: file.modelUrl || '',
                glbUrl: file.glbUrl || file.model3dUrl || ''
            }))
        },
        sceneIframeSrc() {
             const base = this.getIframeBaseUrl();
                    const params = {
                        room: JSON.stringify(this.getCurrentRoomData()),
                        placed: JSON.stringify(this.scenePlaced),
                        models: JSON.stringify(this.sceneMyModelsList)
                    };
                    const query = Object.entries(params)
                        .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
                        .join('&');
                    return base + query;
                }
    },
    methods: {
		 // 获取 iframe 的基础 URL（区分平台）
		getIframeBaseUrl() {
		    try {
		        // #ifdef H5
		    if (typeof window !== 'undefined' && window.location) {
		        return window.location.origin + '/static/scene-viewer.html?';
		        }
		        // #endif
		
		        // #ifdef APP-PLUS
		    return 'static/scene-viewer.html?';
		        // #endif
		
		    return '/static/scene-viewer.html?';
		        } catch (e) {
		            console.error('获取iframe基础URL出错: ', e);
		            return '/static/scene-viewer.html?';
		        }
		    },
        _getModelIcon(file) {
            const formats = file.formats || []
            if (formats.includes('GLB') || formats.includes('GLTF')) return '📦'
            if (formats.includes('OBJ')) return '🧊'
            if (formats.includes('图片')) return '🖼️'
            return '🎨'
        },
        async loadSchemesFromStorage() {
            try {
                console.log('[profile][scene-schemes] fetch mine: GET /scene-schemes/mine');
                const data = await request({
                    url: '/scene-schemes/mine',
                    method: 'GET'
                });
                const list = Array.isArray(data) ? data : [];
                this.sceneSchemes = list.map(this.normalizeSceneSchemeSummary);
                this.stats.schemes = this.sceneSchemes.length;
                this.stats.models = this.files.length;
                console.log('[profile][scene-schemes] mine loaded:', {
                    count: this.sceneSchemes.length,
                    schemes: this.sceneSchemes.map(s => ({
                        schemeId: s.schemeId,
                        title: s.title,
                        room: s.room,
                        items: s.items
                    }))
                });
            } catch (err) {
                console.error('[profile] load scene schemes failed:', err);
                this.sceneSchemes = [];
                this.stats.schemes = 0;
            }
        },

        normalizeSceneSchemeSummary(scheme) {
            const created = scheme.createdAt ? Date.parse(scheme.createdAt) : scheme.created;
            return {
                ...scheme,
                id: scheme.schemeId || scheme.id,
                schemeId: scheme.schemeId || scheme.id,
                title: scheme.title || '未命名展馆方案',
                description: scheme.description || '',
                coverImageUrl: this.normalizePreviewUrl(scheme.coverImageUrl || ''),
                thumb: scheme.thumb || '🏛️',
                room: scheme.roomDisplayName || scheme.roomName || scheme.room || '展馆',
                items: scheme.itemCount ?? scheme.placementCount ?? scheme.items ?? 0,
                created: Number.isFinite(created) ? created : Date.now()
            };
        },

        normalizeSceneRoomData(detail) {
            const roomData = detail.roomData || {};
            return {
                id: detail.roomId || roomData.id || 'medium',
                name: detail.roomName || roomData.name || detail.roomDisplayName || '雅致中厅',
                shape: detail.shape || roomData.shape || 'shape-bed',
                area: detail.area || roomData.area || '',
                lenM: Number(detail.lenM ?? roomData.lenM ?? 6),
                widM: Number(detail.widM ?? roomData.widM ?? 4),
                heightM: Number(detail.heightM ?? roomData.heightM ?? 3.2),
                kind: detail.kind || roomData.kind || 'flat',
                eaveM: detail.eaveM ?? roomData.eaveM ?? null,
                dimLine: detail.dimLine || roomData.dimLine || ''
            };
        },

        normalizeScenePlacement(placement) {
            const modelUrl = this.normalizeUrl(placement.modelUrl || placement.glbUrl || '');
            return {
                uid: placement.placementUid || placement.uid || ('p' + Date.now() + Math.random().toString(36).slice(2, 7)),
                id: placement.mid || placement.id,
                icon: placement.icon || '📦',
                name: placement.title || placement.name || '展品',
                modelUrl,
                glbUrl: modelUrl,
                x: Number(placement.normX ?? placement.x ?? 50),
                y: Number(placement.normY ?? placement.y ?? 50)
            };
        },

        buildSceneSchemePayload() {
            const roomData = this.getCurrentRoomData();
            const placements = (this.scenePlaced || [])
                .map((p, index) => ({
                    placementUid: p.uid || p.placementUid || '',
                    mid: Number(p.id || p.mid),
                    title: p.name || p.title || '',
                    icon: p.icon || '📦',
                    modelUrl: p.modelUrl || p.glbUrl || '',
                    normX: Number(p.x ?? p.normX),
                    normY: Number(p.y ?? p.normY),
                    sortOrder: index
                }))
                .filter(p => Number.isFinite(p.mid) && Number.isFinite(p.normX) && Number.isFinite(p.normY));
            const title = this.sceneCurrentSchemeId && this.sceneLoadedSchemeTitle
                ? this.sceneLoadedSchemeTitle
                : (this.tempSceneMeta.title || `${roomData.name || this.sceneRoomName || '展馆'} · ${this.formatNow()}`);
            const payload = {
                title,
                description: this.sceneMetaDescription || this.tempSceneMeta.description || '',
                coverImageUrl: this.sceneMetaCoverImageUrl || this.tempSceneMeta.coverImageUrl || '',
                roomDisplayName: roomData.name || this.sceneRoomName || '展馆',
                thumb: placements[0]?.icon || '🏛️',
                itemCount: placements.length,
                roomId: roomData.id || 'medium',
                roomName: roomData.name || this.sceneRoomName || '展馆',
                shape: roomData.shape || 'shape-bed',
                area: roomData.area || '',
                lenM: Number(roomData.lenM || 6),
                widM: Number(roomData.widM || 4),
                heightM: Number(roomData.heightM || 3.2),
                kind: roomData.kind || 'flat',
                eaveM: roomData.eaveM ?? null,
                dimLine: roomData.dimLine || '',
                roomData,
                placedSnapshot: placements.map(p => ({
                    placementUid: p.placementUid,
                    mid: p.mid,
                    title: p.title,
                    icon: p.icon,
                    modelUrl: p.modelUrl,
                    normX: p.normX,
                    normY: p.normY,
                    sortOrder: p.sortOrder
                })),
                placements
            };
            console.log('[profile][scene-schemes] build payload:', {
                schemeId: this.sceneCurrentSchemeId,
                roomId: payload.roomId,
                roomName: payload.roomName,
                placementCount: payload.placements.length,
                placements: payload.placements.map(p => ({
                    placementUid: p.placementUid,
                    mid: p.mid,
                    normX: p.normX,
                    normY: p.normY,
                    hasModelUrl: !!p.modelUrl
                }))
            });
            return payload;
        },
        async fetchUserInfo() {
            const localInfo = uni.getStorageSync('userInfo');
            if (localInfo) {
                this.userInfo = localInfo;
            }

            try {
                const res = await request({
                    url: '/users/me',
                    method: 'GET'
                });
                
                if (res) {
                    this.userInfo = {
                        ...res,
                        avatarUrl: this.normalizeUrl(res.avatarUrl)
                    };
                    uni.setStorageSync('userInfo', this.userInfo);
                }
            } catch (e) {
                console.error('获取用户信息失败', e);
                if (!localInfo) {
                    uni.showToast({ title: '请先登录', icon: 'none' });
                    setTimeout(() => {
                        uni.navigateTo({ url: '/pages/login/login' });
                    }, 1500);
                }
            }
        },
        
        async fetchUserModels() {
            const uid = this.userInfo.uid || (uni.getStorageSync('userInfo') || {}).uid;
            if (!uid) return;

            try {
                const data = await request({
                    url: '/models/my',
                    method: 'GET'
                });
                
                if (data) {
                    const toTs = (v) => {
                        if (!v) return 0;
                        if (typeof v === 'number') return v;
                        if (typeof v === 'string') {
                            const t = Date.parse(v);
                            return Number.isFinite(t) ? t : 0;
                        }
                        if (Array.isArray(v)) {
                            const [y, m, d, hh, mm, ss, nano] = v;
                            if (!y || !m || !d) return 0;
                            return new Date(y, (m - 1), d, hh || 0, mm || 0, ss || 0, nano ? Math.floor(nano / 1e6) : 0).getTime();
                        }
                        return 0;
                    };
                    const validData = data
                        .filter(item => item.renderStatus === 'success')
                        .slice()
                        .sort((a, b) => {
                            const dt = toTs(b?.createdAt) - toTs(a?.createdAt);
                            if (dt !== 0) return dt;
                            return Number(b?.mid || 0) - Number(a?.mid || 0);
                        });

                    const modelFilesMap = await this.fetchSceneModelFiles(validData);
                    this.files = validData.map(item => {
                        const modelFile = modelFilesMap[item.mid] || null;
                        const modelUrl = modelFile ? this.normalizeUrl(modelFile.filePath) : '';
                        console.log('[profile] scene model mapped:', {
                            mid: item.mid,
                            title: item.title,
                            modelFile,
                            modelUrl
                        });
                        return {
                            id: item.mid,
                            name: item.title,
                            formats: item.formats || [], 
                            size: '--',
                            cover: item.thumbnailUrl || 'https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
                            modelUrl,
                            glbUrl: modelUrl,
                            likeCount: null,
                            commentCount: null,
                            createdAt: item.createdAt ?? null
                        };
                    });
                    this.stats.models = this.files.length;
                }
            } catch (e) {
                console.error('Failed to fetch user models', e);
            }
        },

        async fetchSceneModelFiles(models) {
            const entries = await Promise.all(models.map(async (model) => {
                try {
                    const files = await request({
                        url: `/model-files/model/${model.mid}`,
                        method: 'GET'
                    });
                    const list = Array.isArray(files) ? files : [];
                    const preferred = list.find(file => /glb/i.test(file.fileType || '') || /\.glb($|\?)/i.test(file.filePath || ''))
                        || list.find(file => /gltf/i.test(file.fileType || '') || /\.gltf($|\?)/i.test(file.filePath || ''))
                        || null;
                    console.log('[profile] model files loaded:', {
                        mid: model.mid,
                        files: list,
                        preferred
                    });
                    return [model.mid, preferred];
                } catch (err) {
                    console.warn('[profile] model files load failed:', {
                        mid: model.mid,
                        err
                    });
                    return [model.mid, null];
                }
            }));
            return Object.fromEntries(entries);
        },
        
        async saveProfile() {
            const nickname = (this.tempUserInfo.nickname || '').trim();
            if (!nickname) {
                uni.showToast({
                    title: '昵称不能为空',
                    icon: 'none'
                })
                return
            }
            
            uni.showLoading({ title: '保存中...' });
            
            const payload = {
                nickname,
                bio: this.tempUserInfo.bio,
                avatarUrl: this.tempUserInfo.avatarUrl
            };
            
            try {
                const updatedUser = await request({
                    url: '/users/update',
                    method: 'PUT',
                    data: payload
                });
                
                uni.hideLoading();
                
                this.userInfo = {
                    ...updatedUser,
                    avatarUrl: this.normalizeUrl(updatedUser.avatarUrl)
                };
                uni.setStorageSync('userInfo', this.userInfo);
                
                this.isEditing = false;
                uni.showToast({
                    title: '保存成功',
                    icon: 'success'
                });
                
            } catch (err) {
                uni.hideLoading();
                console.error('Update request failed', err);
            }
        },
        
        editProfile() {
            Object.assign(this.tempUserInfo, {
                avatarUrl: this.normalizeUrl(this.userInfo.avatarUrl) || '',
                nickname: this.userInfo.nickname || '',
                bio: this.userInfo.bio || ''
            });
            this.isEditing = true;
        },
        
        cancelEdit() {
            this.isEditing = false;
        },

        noop() {},

        chooseAvatar() {
            uni.chooseImage({
                count: 1,
                sizeType: ['compressed'],
                sourceType: ['album', 'camera'],
                success: (res) => {
                    const tempFilePath = res.tempFilePaths[0];
                    this.uploadAvatar(tempFilePath);
                }
            });
        },

        uploadAvatar(filePath) {
            uni.showLoading({ title: '上传中...' });
            const token = uni.getStorageSync('token');

            if (!token) {
                uni.hideLoading();
                uni.showToast({ title: '请先登录', icon: 'none' });
                setTimeout(() => {
                    uni.navigateTo({ url: '/pages/login/login' });
                }, 1200);
                return;
            }
            
            uni.uploadFile({
                url: `${BASE_URL}/upload/avatar`,
                filePath: filePath,
                name: 'file',
                header: {
                    'Authorization': 'Bearer ' + token
                },
                success: (uploadFileRes) => {
                    uni.hideLoading();
                    if (uploadFileRes.statusCode === 200 || uploadFileRes.statusCode === 201) {
                        try {
                            const data = JSON.parse(uploadFileRes.data);
                            const uploadedUrl = data.url || data; 
                            this.tempUserInfo.avatarUrl = this.normalizeUrl(uploadedUrl);
                            uni.showToast({ title: '上传成功', icon: 'success' });
                        } catch (e) {
                            console.error('解析上传响应失败', e);
                            uni.showToast({ title: '上传失败', icon: 'none' });
                        }
                    } else if (uploadFileRes.statusCode === 401) {
                        uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' });
                        uni.removeStorageSync('token');
                        uni.removeStorageSync('userInfo');
                        setTimeout(() => {
                            uni.navigateTo({ url: '/pages/login/login' });
                        }, 1200);
                    } else {
                        uni.showToast({ title: '上传失败', icon: 'none' });
                    }
                },
                fail: (err) => {
                    uni.hideLoading();
                    console.error('Upload failed', err);
                    uni.showToast({ title: '网络错误', icon: 'none' });
                }
            });
        },
        normalizeUrl(url) {
            if (!url) return '';
            if (typeof url !== 'string') return String(url);
            if (/^https?:\/\//i.test(url)) {
                const hostOrigin = HOST_URL.replace(/\/$/, '');
                if (/^https?:\/\/localhost(?::\d+)?/i.test(url) && hostOrigin && !/localhost/i.test(hostOrigin)) {
                    return url.replace(/^https?:\/\/localhost(?::\d+)?/i, hostOrigin);
                }
                return url;
            }
            if (url.startsWith('/')) return `${HOST_URL}${url}`;
            return `${HOST_URL}/${url}`;
        },

        normalizePreviewUrl(url) {
            if (!url) return '';
            if (typeof url !== 'string') return String(url);
            if (/^(https?:|blob:|data:|file:|_www\/|wxfile:|\.\/|\.\.\/)/i.test(url)) return url;
            return this.normalizeUrl(url);
        },

        getDefaultSceneCover() {
            const firstPlaced = this.scenePlaced && this.scenePlaced[0];
            const firstModel = firstPlaced
                ? this.sceneMyModelsList.find(model => String(model.id) === String(firstPlaced.id || firstPlaced.mid))
                : null;
            return this.normalizePreviewUrl(firstModel?.cover || this.files?.[0]?.cover || 'https://via.placeholder.com/640x360');
        },

        openSceneMetaModal() {
            const roomData = this.getCurrentRoomData();
            Object.assign(this.tempSceneMeta, {
                title: this.sceneLoadedSchemeTitle || this.tempSceneMeta.title || `${roomData.name || this.sceneRoomName || '展馆'} · ${this.formatNow()}`,
                description: this.sceneMetaDescription || this.tempSceneMeta.description || '',
                coverImageUrl: this.sceneMetaCoverImageUrl || this.tempSceneMeta.coverImageUrl || this.getDefaultSceneCover()
            });
            this.sceneMetaModalVisible = true;
        },

        cancelSceneMetaEdit() {
            this.sceneMetaModalVisible = false;
        },

        saveSceneMetaEdit() {
            const title = (this.tempSceneMeta.title || '').trim();
            if (!title) {
                uni.showToast({ title: '展馆标题不能为空', icon: 'none' });
                return;
            }
            this.sceneLoadedSchemeTitle = title;
            this.sceneMetaDescription = (this.tempSceneMeta.description || '').trim();
            this.sceneMetaCoverImageUrl = this.normalizePreviewUrl(this.tempSceneMeta.coverImageUrl || '');
            this.sceneMetaModalVisible = false;
            this.scenePlacementDirty = true;
            uni.showToast({ title: '展馆信息已暂存', icon: 'none' });
        },

        chooseSceneCover() {
            uni.chooseImage({
                count: 1,
                sizeType: ['compressed'],
                sourceType: ['album', 'camera'],
                success: (res) => {
                    this.tempSceneMeta.coverImageUrl = res.tempFilePaths[0];
                }
            });
        },

        useFirstPlacedCover() {
            const cover = this.getDefaultSceneCover();
            if (!cover) {
                uni.showToast({ title: '暂无可用藏品封面', icon: 'none' });
                return;
            }
            this.tempSceneMeta.coverImageUrl = cover;
        },
        
        goSceneFloor() {
            this.sceneView = 'floor';
            const prev = uni.getStorageSync('sceneSelectedRoom');
            if (prev && prev.id) this.sceneSelectedRoom = prev.id;
        },
        
        goPlaceFromFloor() {
            if (!this.sceneSelectedRoom) return;
            this.scenePlacementFromFloor = true;
            this.sceneView = 'placement';
            this.syncSceneRoom();
			this.scenePlaced=[];
			this.scenePlacementDirty=false;
            this.sceneCurrentSchemeId = null;
            this.sceneLoadedSchemeTitle = '';
            this.sceneMetaDescription = '';
            this.sceneMetaCoverImageUrl = '';
            this.tempSceneMeta = { title: '', description: '', coverImageUrl: '' };
			this.iframRefreshKey++;
            // 移除所有 Three.js 初始化代码
        },

        syncSceneRoom() {
            const r = uni.getStorageSync('sceneSelectedRoom');
            if (r && r.id) {
                this.sceneRoomId = r.id;
                this.sceneRoomName = r.name;
                this.sceneRoomDimLine = r.dimLine || '';
                this.sceneRoomLenM = r.lenM || 6.0;
                this.sceneRoomWidM = r.widM || 4.0;
                this.sceneRoomHeightM = r.heightM || 3.2;
                this.sceneRoomKind = r.kind || 'flat';
                this.sceneRoomEaveM = r.eaveM || null;
                this.sceneRoomShape = r.shape || 'shape-bed';
				this.currentRoomData=r;
            } else {
                this.sceneRoomId = 'medium';
                this.sceneRoomName = '';
                this.sceneRoomDimLine = '';
                this.sceneRoomLenM = 6.0;
                this.sceneRoomWidM = 4.0;
                this.sceneRoomHeightM = 3.2;
                this.sceneRoomKind = 'flat';
                this.sceneRoomEaveM = null;
                this.sceneRoomShape = 'shape-bed';
				this.currentRoomData = {
				            id: 'medium', name: '雅致中厅', shape: 'shape-bed',
				            lenM: 6, widM: 4, heightM: 3.2, kind: 'flat', eaveM: null
				        };
            }
            this.scenePlacementDirty = false;
        },

        getCurrentRoomData() {
            return this.currentRoomData || {
                    id: 'medium', name: '雅致中厅', shape: 'shape-bed',
                    lenM: 6, widM: 4, heightM: 3.2, kind: 'flat', eaveM: null
                };
        },

        selectSceneRoom(room) {
            this.sceneSelectedRoom = room.id;
            const savedRoom = {
                id: room.id, name: room.name, shape: room.shape,
                area: room.area, lenM: room.lenM, widM: room.widM,
                heightM: room.heightM, kind: room.kind || 'flat', eaveM: room.eaveM || null,
                dimLine: room.dimLine
            };
            uni.setStorageSync('sceneSelectedRoom', savedRoom);
			this.currentRoomData=savedRoom;
        },

        pickSceneModel(m) {
            const modelUrl = m.glbUrl || m.modelUrl || '';
            console.log('[profile] pick scene model:', {
                clickedId: m.id,
                activeId: this.sceneActiveId,
                modelUrl,
                model: m
            });
            if (!/\.(glb|gltf)(?:$|\?)/i.test(modelUrl)) {
                uni.showToast({ title: '该藏品暂无可布展的3D模型', icon: 'none' });
                this.sceneActiveId = '';
                this.sceneActiveIcon = '📦';
                this.sceneActiveName = '';
                this.sceneActiveItem = null;
                this.sendToIframe({ type: 'selectModel', model: null });
                return;
            }
            if (this.sceneActiveId === m.id) {
                this.sceneActiveId = '';
                this.sceneActiveIcon = '📦';
                this.sceneActiveName = '';
                this.sceneActiveItem = null;
                this.sendToIframe({ type: 'selectModel', model: null });
            } else {
                this.sceneActiveId = m.id;
                this.sceneActiveIcon = m.icon || '📦';
                this.sceneActiveName = m.name || '展品';
                this.sceneActiveItem = {
                    id: m.id,
                    name: m.name || '展品',
                    icon: m.icon || '📦',
                    category: m.category || 'default',
                    modelUrl,
                    glbUrl: modelUrl,
                    cover: m.cover || '',
                    description: m.description || ''
                };
                this.sendToIframe({ type: 'selectModel', model: this.sceneActiveItem });
            }
        },

        removeScenePlaced(idx) {
            const removed = this.scenePlaced[idx];
            if (removed) {
                this.scenePlaced.splice(idx, 1);
                this.scenePlacementDirty = this.scenePlaced.length > 0;
                this.sendToIframe({ type: 'removeItem', uid: removed.uid });
            }
        },

        clearSceneAll() {
            this.scenePlaced = [];
            this.scenePlacementDirty = false;
            this.sendToIframe({ type: 'clearAll' });
        },

        resetSceneCamera() {
            this.sendToIframe({ type: 'resetCamera' });
        },

        async saveSceneScheme() {
            const payload = this.buildSceneSchemePayload();
            if (!payload.roomId) {
                uni.showToast({ title: '请先选择展馆', icon: 'none' });
                return;
            }
            if (!payload.placements.length) {
                uni.showToast({ title: '请先放置至少一件藏品', icon: 'none' });
                return;
            }

            uni.showLoading({ title: '保存中...' });
            try {
                const isUpdate = !!this.sceneCurrentSchemeId;
                console.log('[profile][scene-schemes] save request:', {
                    method: isUpdate ? 'PUT' : 'POST',
                    url: isUpdate ? `/scene-schemes/${this.sceneCurrentSchemeId}` : '/scene-schemes',
                    title: payload.title,
                    roomId: payload.roomId,
                    placementCount: payload.placements.length
                });
                const saved = await request({
                    url: isUpdate ? `/scene-schemes/${this.sceneCurrentSchemeId}` : '/scene-schemes',
                    method: isUpdate ? 'PUT' : 'POST',
                    data: payload
                });
                uni.hideLoading();
                console.log('[profile][scene-schemes] save response:', {
                    schemeId: saved?.schemeId || saved?.id,
                    title: saved?.title,
                    itemCount: saved?.itemCount,
                    placementCount: saved?.placements?.length
                });
                const schemeId = saved?.schemeId || saved?.id || this.sceneCurrentSchemeId;
                if (schemeId) this.sceneCurrentSchemeId = schemeId;
                this.sceneLoadedSchemeTitle = saved?.title || payload.title;
                this.sceneMetaDescription = saved?.description || payload.description || '';
                this.sceneMetaCoverImageUrl = this.normalizePreviewUrl(saved?.coverImageUrl || payload.coverImageUrl || '');
                await this.loadSchemesFromStorage();
                uni.showToast({ title: isUpdate ? '展馆方案已更新' : '展馆方案已保存', icon: 'none' });
                this.scenePlacementDirty = false;
            } catch (err) {
                uni.hideLoading();
                console.error('[profile] save scene scheme failed:', err);
            }
        },

        async loadSceneScheme(scheme) {
            const schemeId = scheme.schemeId || scheme.id;
            if (!schemeId) return;
            uni.showLoading({ title: '加载中...' });
            try {
                console.log('[profile][scene-schemes] detail request:', {
                    method: 'GET',
                    url: `/scene-schemes/${schemeId}`,
                    scheme
                });
                const detail = await request({
                    url: `/scene-schemes/${schemeId}`,
                    method: 'GET'
                });
                uni.hideLoading();
                console.log('[profile][scene-schemes] detail response:', {
                    schemeId: detail?.schemeId,
                    title: detail?.title,
                    roomId: detail?.roomId || detail?.roomData?.id,
                    placements: detail?.placements?.length || detail?.placedSnapshot?.length || 0
                });
                const roomData = this.normalizeSceneRoomData(detail || {});
                const placements = Array.isArray(detail?.placements) && detail.placements.length
                    ? detail.placements
                    : (Array.isArray(detail?.placedSnapshot) ? detail.placedSnapshot : []);

                this.sceneCurrentSchemeId = detail?.schemeId || schemeId;
                this.sceneLoadedSchemeTitle = detail?.title || scheme.title || '';
                this.sceneMetaDescription = detail?.description || scheme.description || '';
                this.sceneMetaCoverImageUrl = this.normalizePreviewUrl(detail?.coverImageUrl || scheme.coverImageUrl || '');
                this.scenePlaced = placements.map(p => this.normalizeScenePlacement(p));
                this.sceneSelectedRoom = roomData.id;
                uni.setStorageSync('sceneSelectedRoom', roomData);
                this.currentRoomData = roomData;
                this.syncSceneRoom();
                this.sceneView = 'placement';
                this.scenePlacementDirty = false;
			    this.iframRefreshKey++;
                uni.showToast({ title: '已加载方案', icon: 'none' });
            } catch (err) {
                uni.hideLoading();
                console.error('[profile] load scene scheme failed:', err);
            }
        },

        async deleteSceneScheme(idx) {
            const scheme = this.sceneSchemes[idx];
            const schemeId = scheme && (scheme.schemeId || scheme.id);
            if (!schemeId) return;
            uni.showLoading({ title: '删除中...' });
            try {
                console.log('[profile][scene-schemes] delete request:', {
                    method: 'DELETE',
                    url: `/scene-schemes/${schemeId}`,
                    scheme
                });
                await request({
                    url: `/scene-schemes/${schemeId}`,
                    method: 'DELETE'
                });
                uni.hideLoading();
                console.log('[profile][scene-schemes] delete success:', { schemeId });
                if (this.sceneCurrentSchemeId === schemeId) {
                    this.sceneCurrentSchemeId = null;
                    this.sceneLoadedSchemeTitle = '';
                    this.sceneMetaDescription = '';
                    this.sceneMetaCoverImageUrl = '';
                }
                await this.loadSchemesFromStorage();
                uni.showToast({ title: '已删除', icon: 'none' });
            } catch (err) {
                uni.hideLoading();
                console.error('[profile] delete scene scheme failed:', err);
            }
        },

        sendToIframe(message) {
            const safeMessage = this.toPlainMessage(message);
            console.log('[profile] send to scene iframe:', safeMessage);
            const iframe = this.$refs.sceneIframe;
            if (iframe && iframe.contentWindow) {
                iframe.contentWindow.postMessage(safeMessage, '*');
            }
        },

        toPlainMessage(message) {
            try {
                return JSON.parse(JSON.stringify(message));
            } catch (err) {
                console.warn('[profile] message clone fallback:', err, message);
                if (message && message.type === 'selectModel' && message.model) {
                    const m = message.model;
                    return {
                        type: 'selectModel',
                        model: {
                            id: m.id,
                            name: m.name || '展品',
                            icon: m.icon || '📦',
                            modelUrl: m.modelUrl || '',
                            glbUrl: m.glbUrl || '',
                            cover: m.cover || '',
                            description: m.description || ''
                        }
                    };
                }
                return { type: message && message.type ? message.type : 'unknown' };
            }
        },

        handleSceneViewerMessage(event) {
            const data = event && event.data;
            if (!data || !data.type) return;
            console.log('[profile] receive from scene iframe:', data);

            if (data.type === 'addItem' && data.item) {
                this.scenePlaced.push(data.item);
                this.scenePlacementDirty = true;
                return;
            }

            if (data.type === 'modelDeselected') {
                this.sceneActiveId = '';
                this.sceneActiveIcon = '📦';
                this.sceneActiveName = '';
                this.sceneActiveItem = null;
                return;
            }

            if (data.type === 'itemsUpdated') {
                this.scenePlaced = Array.isArray(data.placed) ? data.placed : [];
                this.scenePlacementDirty = this.scenePlaced.length > 0;
            }
        },

        onIframeLoad() {
            // iframe 加载完成后，可发送当前选中模型状态
            if (this.sceneActiveItem) {
                this.sendToIframe({ type: 'selectModel', model: this.sceneActiveItem });
            }
        },

        formatNow() {
            const d = new Date();
            const m = d.getMinutes();
            return `${d.getMonth() + 1}月${d.getDate()}日 ${d.getHours()}:${m < 10 ? '0' : ''}${m}`;
        },
        formatDate(ts) {
            const d = new Date(ts);
            return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`;
        }
    }
}
</script>

<style scoped>
.profile-page {
    width: 100%;
    height: 100%;
    background: linear-gradient(180deg, #F5F0E8 0%, #EBE4D8 100%);
}

.page-scroll {
    width: 100%;
    height: 100%;
}

.profile-cover-section {
    position: relative;
    padding: 20px 16px 0;
}

.cover-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 140px;
    background: linear-gradient(135deg, #C9A87C 0%, #8B7355 50%, #6B5344 100%);
    border-radius: 0 0 24px 24px;
    overflow: hidden;
}

.cover-pattern {
    position: absolute;
    inset: 0;
    background-image: 
        radial-gradient(circle at 20% 50%, rgba(255,255,255,0.1) 0%, transparent 50%),
        radial-gradient(circle at 80% 30%, rgba(255,255,255,0.08) 0%, transparent 40%);
}

.profile-hero-card {
    position: relative;
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    background: rgba(255,255,255,0.95);
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.08);
    margin-top: 60px;
}

.hero-avatar-wrap {
    position: relative;
    flex-shrink: 0;
}

.hero-avatar {
    width: 72px;
    height: 72px;
    border-radius: 50%;
    border: 3px solid #fff;
    box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.avatar-ring {
    position: absolute;
    inset: -4px;
    border-radius: 50%;
    border: 2px solid rgba(201, 168, 124, 0.5);
    animation: pulse-ring 2s ease-in-out infinite;
}

@keyframes pulse-ring {
    0%, 100% { transform: scale(1); opacity: 1; }
    50% { transform: scale(1.05); opacity: 0.7; }
}

.avatar-edit-btn {
    position: absolute;
    bottom: -2px;
    right: -2px;
    width: 24px;
    height: 24px;
    background: #8B7355;
    border: 2px solid #fff;
    border-radius: 50%;
    color: #fff;
    font-size: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 1;
}

.hero-info {
    flex: 1;
    min-width: 0;
}

.hero-nickname {
    display: block;
    font-size: 20px;
    font-weight: 700;
    color: #3D3229;
    margin-bottom: 4px;
}

.hero-email {
    display: block;
    font-size: 12px;
    color: #8B7355;
    margin-bottom: 6px;
}

.hero-bio {
    display: block;
    font-size: 13px;
    color: #A69076;
    font-style: italic;
}

.stats-strip {
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding: 14px 20px;
    margin: 16px 16px 0;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
}

.stat-num {
    font-size: 20px;
    font-weight: 700;
    color: #3D3229;
}

.stat-label {
    font-size: 11px;
    color: #8B7355;
}

.stat-divider {
    width: 1px;
    height: 30px;
    background: rgba(139,115,85,0.2);
}

.hall-entrance-section {
    position: relative;
    margin: 20px 16px;
    border-radius: 20px;
    overflow: hidden;
    background: linear-gradient(135deg, #3D3229 0%, #5C4A3A 100%);
}

.hall-entrance-bg {
    position: absolute;
    inset: 0;
    background: 
        radial-gradient(circle at 70% 50%, rgba(201,168,124,0.3) 0%, transparent 60%),
        radial-gradient(circle at 30% 80%, rgba(107,83,68,0.5) 0%, transparent 40%);
}

.hall-entrance-glow {
    position: absolute;
    top: -50%;
    right: -20%;
    width: 200px;
    height: 200px;
    background: radial-gradient(circle, rgba(201,168,124,0.4) 0%, transparent 70%);
    animation: glow-pulse 3s ease-in-out infinite;
}

@keyframes glow-pulse {
    0%, 100% { opacity: 0.5; transform: scale(1); }
    50% { opacity: 1; transform: scale(1.1); }
}

.hall-entrance-content {
    position: relative;
    display: flex;
    align-items: center;
    padding: 20px;
    gap: 16px;
}

.hall-icon-wrap {
    position: relative;
    width: 64px;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255,255,255,0.1);
    border-radius: 16px;
}

.hall-main-icon {
    font-size: 36px;
    z-index: 1;
}

.hall-icon-ring {
    position: absolute;
    border: 1px solid rgba(201,168,124,0.4);
    border-radius: 50%;
    animation: ring-expand 2s ease-out infinite;
}

.ring-1 {
    width: 80px;
    height: 80px;
    animation-delay: 0s;
}

.ring-2 {
    width: 100px;
    height: 100px;
    animation-delay: 0.5s;
}

@keyframes ring-expand {
    0% { transform: scale(0.8); opacity: 1; }
    100% { transform: scale(1.5); opacity: 0; }
}

.hall-text-wrap {
    flex: 1;
}

.hall-title {
    display: block;
    font-size: 18px;
    font-weight: 700;
    color: #fff;
    margin-bottom: 4px;
}

.hall-desc {
    display: block;
    font-size: 12px;
    color: rgba(255,255,255,0.7);
}

.hall-action {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 14px;
    background: linear-gradient(135deg, #C9A87C 0%, #A68B5B 100%);
    border-radius: 20px;
}

.hall-action-text {
    font-size: 13px;
    font-weight: 600;
    color: #fff;
}

.hall-arrow {
    font-size: 14px;
    color: #fff;
}

.collection-section {
    padding: 20px 16px 0;
}

.section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
}

.section-title {
    display: block;
    font-size: 16px;
    font-weight: 700;
    color: #3D3229;
}

.section-action {
    padding: 6px 12px;
    background: #E8D8C4;
    border: 1px solid #C4A882;
    border-radius: 7px;
    color: #4A3424;
    font-size: 12px;
    line-height: 1.2;
    margin: 0;
}

.schemes-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.scheme-card {
    background: #EDE5D8;
    border: 1px solid #C4A882;
    border-radius: 10px;
    padding: 14px;
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    transition: all 0.2s;
}

.scheme-card:hover {
    background: #E5DDD0;
}

.scheme-icon {
    font-size: 32px;
    flex-shrink: 0;
}

.scheme-info {
    flex: 1;
}

.scheme-title {
    font-family: Georgia, serif;
    font-size: 14px;
    color: #4A3424;
}

.scheme-meta {
    font-size: 11px;
    color: #8A7A68;
    margin-top: 3px;
    font-family: Georgia, serif;
}

.scheme-delete {
    padding: 5px 10px;
    background: #D4A0A0;
    border: 1px solid #B08080;
    border-radius: 5px;
    color: #fff;
    font-size: 12px;
    cursor: pointer;
}

.empty-collection {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40px 0;
}

.empty-icon {
    font-size: 48px;
    margin-bottom: 12px;
    opacity: 0.5;
}

.empty-text {
    font-size: 14px;
    color: #8B7355;
    margin-bottom: 4px;
}

.empty-sub {
    font-size: 12px;
    color: #A69076;
}

.edit-modal-modern {
    width: 90%;
    max-width: 360px;
    background: #fff;
    border-radius: 20px;
    overflow: hidden;
}

.scene-meta-modal {
    max-width: 420px;
}

.scene-cover-upload {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 12px;
}

.scene-cover-preview {
    width: 100%;
    height: 150px;
    box-sizing: border-box;
    border-radius: 12px;
    background: #F5F0E8;
    border: 1px solid #E8E0D0;
    margin-bottom: 8px;
}

.scene-cover-pick {
    width: 100%;
    margin-bottom: 16px;
}

.scene-meta-textarea {
    height: 96px;
}

.edit-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    background: linear-gradient(135deg, #8B7355 0%, #6B5344 100%);
}

.edit-title {
    font-size: 16px;
    font-weight: 600;
    color: #fff;
}

.edit-close {
    width: 28px;
    height: 28px;
    background: rgba(255,255,255,0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 14px;
}

.edit-body {
    padding: 20px;
    box-sizing: border-box;
}

.avatar-upload-center {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20px;
}

.edit-avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    border: 3px solid #E8E0D0;
    margin-bottom: 8px;
}

.upload-tip {
    font-size: 12px;
    color: #8B7355;
}

.form-row {
    margin-bottom: 16px;
}

.form-label {
    display: block;
    font-size: 13px;
    font-weight: 600;
    color: #3D3229;
    margin-bottom: 6px;
}

.form-input {
    width: 100%;
    box-sizing: border-box;
    padding: 10px 12px;
    background: #F5F0E8;
    border-radius: 10px;
    font-size: 14px;
    line-height: 20px;
    min-height: 44px;
    color: #3D3229;
}

.form-textarea {
    width: 100%;
    box-sizing: border-box;
    height: 80px;
    padding: 10px 12px;
    background: #F5F0E8;
    border-radius: 10px;
    font-size: 14px;
    line-height: 20px;
    color: #3D3229;
}

.edit-footer {
    display: flex;
    gap: 12px;
    padding: 16px 20px;
    border-top: 1px solid #E8E0D0;
}

.btn-secondary {
    flex: 1;
    padding: 10px;
    background: #E8E0D0;
    border-radius: 10px;
    font-size: 14px;
    color: #8B7355;
}

.btn-primary-full {
    flex: 1;
    padding: 10px;
    background: linear-gradient(135deg, #8B7355 0%, #6B5344 100%);
    border-radius: 10px;
    font-size: 14px;
    color: #fff;
}

.edit-overlay {
    position: fixed;
    inset: 0;
    background: rgba(61, 50, 41, 0.6);
    backdrop-filter: blur(4px);
    z-index: 1200;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
}

.scene-hub-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-height: 72px;
    padding: 12px 22px;
    background:
        linear-gradient(180deg, rgba(255,255,255,0.38), rgba(255,255,255,0)),
        linear-gradient(180deg, #E9DDCA 0%, #DAC8AF 100%);
    border-bottom: 1px solid rgba(150, 106, 64, 0.35);
    box-shadow: 0 8px 24px rgba(74, 52, 36, 0.08);
    flex-shrink: 0;
    position: relative;
    gap: 12px;
    box-sizing: border-box;
}

.scene-hub-header::after {
    content: "";
    position: absolute;
    left: 22px;
    right: 22px;
    bottom: 7px;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(139,90,43,0.28), transparent);
}

.scene-hub-title {
    font-family: Georgia, serif;
    font-size: 18px;
    font-weight: 700;
    color: #4A3424;
    letter-spacing: 2px;
    text-align: center;
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    max-width: calc(100% - 260px);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    pointer-events: none;
}

.scene-header-actions {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-shrink: 0;
    margin-left: auto;
    position: relative;
    z-index: 1;
}

.scene-header-actions .scene-back-btn {
    margin: 0;
    white-space: nowrap;
}

.scene-meta-entry-btn {
    min-width: 86px;
    overflow: visible;
}

.scene-back-btn {
    min-height: 42px;
    padding: 8px 16px;
    background: rgba(196,168,130,0.75);
    border: 1px solid rgba(128,91,57,0.55);
    border-radius: 8px;
    color: #4A3424;
    font-size: 13px;
    font-family: Georgia, serif;
    cursor: pointer;
    transition: all 0.2s;
    margin: 0;
    flex-shrink: 0;
    position: relative;
    z-index: 1;
    box-shadow: 0 4px 10px rgba(74, 52, 36, 0.08);
}

.scene-back-btn:hover {
    background: #B89870;
    transform: translateY(-1px);
    box-shadow: 0 7px 16px rgba(74, 52, 36, 0.14);
}

@media (max-width: 720px) {
    .scene-hub-title {
        position: static;
        transform: none;
        flex: 1;
        max-width: none;
        font-size: 14px;
        letter-spacing: 1px;
        pointer-events: auto;
    }

    .scene-back-btn {
        padding: 6px 10px;
        font-size: 12px;
    }

    .scene-meta-entry-btn {
        min-width: 74px;
    }

    .scene-header-actions {
        gap: 6px;
    }
}

.scene-hub-body {
    flex: 1;
    overflow-y: auto;
    padding: 28px 22px 40px;
    box-sizing: border-box;
}

.scene-floor {
    position: fixed;
    inset: 0;
    z-index: 999;
    background:
        linear-gradient(180deg, rgba(255,255,255,0.48), rgba(255,255,255,0) 240px),
        #F5F0E8;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.scene-room-intro {
    max-width: 980px;
    margin: 0 auto 22px;
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    gap: 18px;
}

.scene-room-kicker {
    font-family: Georgia, serif;
    font-size: 24px;
    font-weight: 700;
    color: #4A3424;
    letter-spacing: 2px;
    white-space: nowrap;
}

.scene-room-copy {
    max-width: 520px;
    font-family: Georgia, serif;
    font-size: 13px;
    color: #8A7A68;
    line-height: 1.7;
    text-align: right;
}

.scene-room-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 18px;
    max-width: 1180px;
    margin: 0 auto;
}

.scene-room-card {
    min-height: 168px;
    background: rgba(255,255,255,0.5);
    border: 1px solid #C9AF8C;
    border-radius: 8px;
    padding: 22px 18px 18px;
    text-align: center;
    cursor: pointer;
    transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease, background 0.18s ease;
    position: relative;
    overflow: hidden;
    box-shadow: 0 8px 22px rgba(74, 52, 36, 0.06);
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.scene-room-card::before {
    content: "";
    position: absolute;
    left: 18px;
    right: 18px;
    top: 14px;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(160,128,96,0.34), transparent);
}

.scene-room-card:hover {
    transform: translateY(-2px);
    border-color: #A06B35;
    box-shadow: 0 12px 28px rgba(74, 52, 36, 0.12);
}

.scene-room-card.selected {
    border-color: #8B5A2B;
    background: #E8D8C4;
    box-shadow: inset 0 0 0 1px rgba(139,90,43,0.28), 0 14px 30px rgba(74, 52, 36, 0.16);
}

.scene-room-card.selected::before {
    background: linear-gradient(90deg, transparent, rgba(139,90,43,0.72), transparent);
}

.scene-room-card .r-icon-wrap {
    width: 54px;
    height: 54px;
    border-radius: 50%;
    background: rgba(255,255,255,0.52);
    border: 1px solid rgba(196,168,130,0.7);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12px;
}

.scene-room-card .r-icon {
    font-size: 28px;
    line-height: 1;
}

.scene-room-card .r-name {
    font-family: Georgia, serif;
    font-size: 17px;
    font-weight: 700;
    color: #4A3424;
    line-height: 1.3;
}

.scene-room-card .r-size {
    font-size: 12px;
    color: #7C6652;
    margin-top: 6px;
    font-family: Georgia, serif;
}

.scene-room-card .r-desc {
    margin-top: 8px;
    padding: 4px 9px;
    border-radius: 999px;
    background: rgba(255,255,255,0.44);
    color: #8A6F55;
    font-size: 11px;
    font-family: Georgia, serif;
    line-height: 1.4;
}

.scene-room-recommend {
    max-width: 1180px;
    margin: 18px auto 0;
    padding: 13px 16px;
    background: linear-gradient(180deg, rgba(255,255,255,0.62), rgba(245,240,232,0.7));
    border: 1px solid rgba(196,168,130,0.8);
    border-radius: 8px;
    box-shadow: 0 8px 20px rgba(74,52,36,0.06);
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    box-sizing: border-box;
}

.recommend-mark {
    padding: 4px 9px;
    border-radius: 999px;
    background: #E8D8C4;
    color: #6A4A32;
    border: 1px solid rgba(160,128,96,0.5);
    font-size: 11px;
    white-space: nowrap;
}

.recommend-text {
    font-family: Georgia, serif;
    font-size: 13px;
    color: #6A5040;
    line-height: 1.55;
}

.scene-room-card .r-check {
    position: absolute;
    top: 10px;
    right: 10px;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: #8B5A2B;
    color: #fff;
    font-size: 14px;
    line-height: 24px;
    text-align: center;
}

.scene-room-note {
    max-width: 1180px;
    margin: 18px auto 0;
    padding: 12px 16px;
    background: rgba(255,255,255,0.52);
    border: 1px solid #C4A882;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 12px;
    box-sizing: border-box;
}

.scene-room-note-label {
    padding: 3px 8px;
    border-radius: 999px;
    background: #8B5A2B;
    color: #fff;
    font-size: 11px;
    white-space: nowrap;
}

.scene-room-note-text {
    font-family: Georgia, serif;
    font-size: 13px;
    color: #6A5040;
    line-height: 1.5;
}

@media (max-width: 980px) {
    .scene-room-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .scene-room-intro {
        align-items: flex-start;
        flex-direction: column;
    }

    .scene-room-copy {
        text-align: left;
        max-width: none;
    }
}

@media (max-width: 560px) {
    .scene-hub-body {
        padding: 20px 14px 32px;
    }

    .scene-room-grid {
        grid-template-columns: 1fr;
        gap: 12px;
    }

    .scene-room-card {
        min-height: 142px;
        padding: 18px 14px;
    }

    .scene-room-kicker {
        font-size: 20px;
    }

    .scene-room-recommend {
        align-items: flex-start;
        flex-direction: column;
        gap: 8px;
    }

    .scene-stage {
        min-height: 0;
    }

    .scene-placement-panel {
        left: 10px;
        right: 10px;
        top: auto;
        bottom: 12px;
        min-width: 0;
        max-height: 210px;
        padding: 10px;
        border-radius: 12px;
        background: rgba(245, 240, 232, 0.92);
        backdrop-filter: blur(6px);
        box-shadow: 0 -8px 24px rgba(74,52,36,0.14);
    }

    .scene-placement-panel .scene-panel-title {
        margin-bottom: 8px;
        padding-bottom: 7px;
        font-size: 12px;
        white-space: nowrap;
    }

    .scene-model-strip {
        display: block;
        width: 100%;
        white-space: nowrap;
    }

    .scene-model-strip .scene-item-row {
        display: inline-flex;
        width: 132px;
        height: 42px;
        margin-right: 8px;
        box-sizing: border-box;
        vertical-align: top;
    }

    .scene-model-strip .scene-model-name {
        max-width: 88px;
        font-size: 11px;
    }

    .scene-placement-panel > view[style*="border-top"] {
        max-height: 86px;
        overflow-y: auto;
    }

    .scene-placement-panel > view[style*="border-top"] .scene-item-list {
        gap: 4px;
    }

    .scene-placement-panel > view[style*="border-top"] .scene-item-row {
        padding: 5px 7px;
    }
}

.scene-placement {
    position: fixed;
    inset: 0;
    z-index: 999;
    background: #F5F0E8;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.scene-stage {
    flex: 1;
    position: relative;
    overflow: hidden;
    min-height: 450px;
    background: #e8ded0;
}

/* 替换 canvas 为 iframe */
.scene-iframe {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    border: none;
}

.scene-placement-panel {
    position: absolute;
    right: 10px;
    top: 10px;
    background: rgba(245, 240, 232, 0.95);
    border: 1px solid #C4A882;
    border-radius: 10px;
    padding: 12px;
    min-width: 160px;
    max-height: 60%;
    overflow-y: auto;
    box-shadow: 2px 3px 10px rgba(0,0,0,0.1);
}

.scene-panel-title {
    font-family: Georgia, serif;
    font-size: 12px;
    color: #4A3424;
    letter-spacing: 1px;
    margin-bottom: 8px;
    padding-bottom: 6px;
    border-bottom: 1px solid #C4A882;
}

.scene-item-list {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.scene-model-name {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.scene-item-row {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 8px;
    background: #EDE5D8;
    border-radius: 6px;
    font-size: 12px;
    font-family: Georgia, serif;
    color: #4A3424;
}

.scene-item-row .s-icon { font-size: 18px; }
.scene-item-row .s-del { margin-left: auto; color: #C06060; cursor: pointer; font-size: 14px; }

.scene-placement-bar {
    padding: 10px 14px;
    background: linear-gradient(180deg, #E8D8C4 0%, #DDD0BC 100%);
    border-top: 1px solid #C4A882;
    display: flex;
    gap: 8px;
    flex-shrink: 0;
}

.scene-btn {
    flex: 1;
    padding: 9px;
    border-radius: 7px;
    font-size: 13px;
    font-family: Georgia, serif;
    letter-spacing: 1px;
    cursor: pointer;
    border: 1px solid #C4A882;
    text-align: center;
    transition: all 0.2s;
}

.scene-btn:active { transform: scale(0.97); }
.scene-btn-primary { background: #8B5A2B; color: #fff; border-color: #6B4020; }
.scene-btn-secondary { background: #EDE5D8; color: #4A3424; }
.scene-btn-danger { background: #D4A0A0; color: #fff; border-color: #B08080; }

@media (max-width: 560px) {
    .scene-stage {
        min-height: 0;
    }

    .scene-placement-panel {
        left: 10px;
        right: 10px;
        top: auto;
        bottom: 12px;
        min-width: 0;
        max-height: 210px;
        padding: 10px;
        border-radius: 12px;
        background: rgba(245, 240, 232, 0.92);
        backdrop-filter: blur(6px);
        box-shadow: 0 -8px 24px rgba(74,52,36,0.14);
    }

    .scene-placement-panel .scene-panel-title {
        margin-bottom: 8px;
        padding-bottom: 7px;
        font-size: 12px;
        white-space: nowrap;
    }

    .scene-model-strip {
        display: block;
        width: 100%;
        white-space: nowrap;
        overflow: hidden;
    }

    .scene-model-strip .scene-item-row {
        display: inline-flex;
        width: 132px;
        height: 42px;
        margin-right: 8px;
        box-sizing: border-box;
        vertical-align: top;
    }

    .scene-model-strip .scene-model-name {
        max-width: 88px;
        font-size: 11px;
    }

    .scene-placement-panel > view[style*="border-top"] {
        max-height: 86px;
        overflow-y: auto;
    }

    .scene-placement-panel > view[style*="border-top"] .scene-item-list {
        gap: 4px;
    }

    .scene-placement-panel > view[style*="border-top"] .scene-item-row {
        padding: 5px 7px;
    }
}

* {
    transition: all 350ms ease;
}
</style>
