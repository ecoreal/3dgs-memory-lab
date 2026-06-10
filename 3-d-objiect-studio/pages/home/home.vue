<template>
  <common-layout pageTitle="藏品仓库">
    <view class="museum">

      <!-- ── 展馆入口横幅 ── -->
      <view class="museum-entrance" v-show="!isModalOpen">
        <view class="entrance-tape"></view>
        <view class="entrance-inner">
          <view class="museum-brand">
            <view class="brand-row">
              <view class="brand-seal">
                <!-- ① brand-seal 六边形印章 -->
                <image src="/static/icons/seal-hex.svg" mode="aspectFit" style="width:20px;height:20px;display:block;"/>
              </view>
              <view class="brand-name-row">
                <text class="brand-name">一方阁</text>
                <text class="brand-divider">·</text>
                <text class="brand-sub-name">YiFang Cabinet</text>
              </view>
            </view>
            <text class="museum-desc">兴趣社群与迷你博物馆 · 3D 藏品数字展馆 · 个人收藏 · 爱好者社区</text>
          </view>
          <view class="entrance-right">
            <view class="search-wrap">
              <!-- ② search 搜索图标 -->
              <image src="/static/icons/search.svg" mode="aspectFit" style="width:14px;height:14px;display:block;flex-shrink:0;"/>
              <input v-model="searchQuery" placeholder="搜索藏品..."
                placeholder-style="color:#B8A898;font-size:13px" class="search-input"/>
            </view>
            <view class="live-badge">
              <view class="live-pip"></view>
              <text class="live-txt">Live</text>
            </view>
          </view>
        </view>
      </view>

      <!-- ★ 移动端分类切换栏 -->
      <view class="mobile-cat-bar" v-show="!isModalOpen">
        <scroll-view scroll-x class="mobile-cat-scroll" :show-scrollbar="false" :enhanced="true">
          <view class="mobile-cat-inner">
            <view
              :class="['mobile-cat-chip', activeCat === '全部' ? 'active' : '']"
              @click="activeCat = '全部'">
              <text class="mcc-txt">全部展品</text>
              <text class="mcc-num">{{ models.length }}</text>
            </view>
            <view
              v-for="(cat, ci) in realCategories" :key="ci"
              :class="['mobile-cat-chip', activeCat === cat ? 'active' : '']"
              @click="activeCat = cat">
              <text class="mcc-txt">{{ cat }}</text>
              <text class="mcc-num">{{ modelsByCat(cat).length }}</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- ── 展厅主体 ── -->
      <view class="gallery-hall" v-show="!isModalOpen">

        <!-- 左侧：展馆目录（桌面端） -->
        <view class="exhibit-index">
          <view class="index-header">
            <text class="index-label">展馆目录</text>
            <view class="index-count-row">
              <text class="index-num">{{ models.length }}</text>
              <text class="index-unit">件藏品</text>
            </view>
          </view>

          <view class="index-scroll-wrap">
            <scroll-view scroll-y class="index-scroll" :show-scrollbar="false" :enhanced="true">
              <view
                :class="['hall-item', activeCat === '全部' ? 'active' : '']"
                @click="activeCat = '全部'">
                <view class="hall-bar"></view>
                <view class="hall-item-inner">
                  <text class="hall-name">全部展品</text>
                  <view class="hall-badge">
                    <text class="hall-meta">{{ models.length }}</text>
                  </view>
                </view>
              </view>
              <view
                v-for="(cat, ci) in realCategories" :key="ci"
                :class="['hall-item', activeCat === cat ? 'active' : '']"
                @click="activeCat = cat">
                <view class="hall-bar"></view>
                <view class="hall-item-inner">
                  <text class="hall-name">{{ cat }}</text>
                  <view class="hall-badge">
                    <text class="hall-meta">{{ modelsByCat(cat).length }}</text>
                  </view>
                </view>
              </view>
            </scroll-view>
            <view class="custom-scrollbar-track">
              <view class="custom-scrollbar-thumb"></view>
            </view>
          </view>

          <view class="index-footer">
            <view class="new-exhibit-btn">
              <!-- ③ plus 加号 -->
              <image src="/static/icons/plus.svg" mode="aspectFit" style="width:13px;height:13px;display:block;"/>
              <text class="new-txt">收录新藏品</text>
            </view>
          </view>
        </view>

        <!-- 右侧：展厅主区 -->
        <view class="gallery-main">

          <!-- 展厅导航条 -->
          <view class="gallery-nav">
            <view class="gnav-crumb">
              <text class="gnav-item">一方阁</text>
              <text class="gnav-sep">›</text>
              <text class="gnav-item cur">{{ activeCat === '全部' ? '全部展品' : activeCat }}</text>
            </view>
            <view class="gnav-space"></view>
            <text class="gnav-count-txt">共 {{ filteredModels.length }} 件藏品</text>
          </view>

          <!-- 展区滚动 -->
          <view class="gallery-scroll-wrap">
            <scroll-view scroll-y class="gallery-scroll" :show-scrollbar="false" :enhanced="true" id="gallery-scroll">
              <!-- 展厅标题 -->
              <view class="hall-heading">
                <view class="hall-heading-inner">
                  <view class="hall-title-group">
                    <view class="hall-icon-row">
                      <view class="hall-icon-dot"></view>
                      <text class="hall-heading-name">{{ activeCat === '全部' ? '全部展品' : activeCat }}</text>
                    </view>
                    <text class="hall-heading-desc">Personal Collection · 3D Digital Archive · YiFang Museum</text>
                  </view>
                  <text class="hall-piece-count">{{ filteredModels.length }} 件</text>
                </view>
              </view>

              <!-- 展品网格 -->
              <view class="exhibit-grid" v-if="filteredModels.length > 0">
                <view
                  v-for="(item, idx) in filteredModels" :key="idx"
                  class="exhibit-case"
                  @click="item.status !== 'rendering' && openDetail(item)">
                  <view class="exhibit-tape"></view>
                  <view class="case-img-wrap">
                    <image :src="item.image" mode="aspectFill" class="case-img"/>
                    <view :class="['case-badge', item.status === 'rendering' ? 'rdr' : '']">
                      <text>{{ item.status === 'rendering' ? '建模中' : 'Ready' }}</text>
                    </view>
                    <text class="case-num-tag">No. {{ String(idx + 1).padStart(3, '0') }}</text>
                    <view v-if="item.status === 'rendering'" class="case-progress">
                      <view class="cp-fill" :style="{width: item.progress + '%'}"></view>
                    </view>
                  </view>
                  <view class="case-placard">
                    <text class="placard-category">{{ item.category }}</text>
                    <text class="placard-name">{{ item.name }}</text>
                    <view v-if="item.tags && item.tags.length" class="placard-tags">
                      <text v-for="(t, ti) in item.tags.slice(0, 3)" :key="ti" class="p-tag"># {{ t }}</text>
                    </view>
                    <view class="placard-footer">
                      <text class="pf-id">{{ item.mid }}</text>
                      <view v-if="item.localAudios && item.localAudios.length || modelAudios.length" class="pf-audio" @click.stop="openDetail(item)">
                        <view class="pf-audio-dot"></view>
                        <text class="pf-audio-txt">语音解说</text>
                      </view>
                    </view>
                  </view>
                </view>

                <!-- 添加展品 -->
                <view class="add-exhibit">
                  <view class="add-circle">
                    <!-- ④ plus-lg 大加号 -->
                    <image src="/static/icons/plus-lg.svg" mode="aspectFit" style="width:16px;height:16px;display:block;"/>
                  </view>
                  <text class="add-label">拍照建模</text>
                  <text class="add-label-sub">收录新藏品</text>
                </view>
              </view>

              <!-- 空状态 -->
              <view v-else class="empty-hall">
                <!-- ⑤ empty-frame 空状态画框 -->
                <image src="/static/icons/empty-frame.svg" mode="aspectFit" style="width:44px;height:44px;display:block;"/>
                <text class="empty-t">此展厅暂无藏品</text>
                <text class="empty-s">点击「收录新藏品」开始建馆</text>
              </view>

              <view style="height:28px"></view>
            </scroll-view>

            <!-- 美化滚动条 -->
            <view class="gallery-scrollbar-track">
              <view class="gallery-scrollbar-thumb" id="gallery-thumb"></view>
            </view>
          </view>

          <!-- 展馆底部信息条 -->
          <view class="museum-footer">
            <view class="mf-stat"><text class="mf-num">{{ models.length }}</text><text class="mf-lbl">件藏品</text></view>
            <view class="mf-div"></view>
            <view class="mf-stat"><text class="mf-num">3DGS</text><text class="mf-lbl">核心引擎</text></view>
            <view class="mf-div"></view>
            <view class="mf-stat"><text class="mf-num">AI</text><text class="mf-lbl">驱动建模</text></view>
            <view class="mf-div"></view>
            <view class="mf-stat">
              <text class="mf-num">{{ models.filter(m => m.status === 'done').length }}</text>
              <text class="mf-lbl">件就绪</text>
            </view>
            <view class="mf-spacer"></view>
            <text class="mf-hint">每件藏品均含语音解说与文字考据</text>
          </view>
        </view>
      </view>

      <!-- ── 详情弹窗 ── -->
      <view v-if="isModalOpen && selectedItem" class="modal-mask" @click.self="closeDetail">
        <view class="modal-box">
          <view class="modal-topbar">
            <view class="mtb-wax"></view>
            <text class="mtb-label">展品 · {{ selectedItem.mid }}</text>
            <view class="mtb-rule"></view>
            <text class="mtb-cat">{{ selectedItem.category }}</text>
            <view class="modal-close" @click="closeDetail">
              <!-- ⑥ close X关闭 -->
              <image src="/static/icons/close.svg" mode="aspectFit" style="width:10px;height:10px;display:block;"/>
            </view>
          </view>

          <view class="modal-body">
            <!-- 3D查看器侧（左侧） -->
            <view class="viewer-side">
              <!-- ★ 使用 iframe 加载 3D 模型查看器 -->
              <iframe
                v-if="has3DModelFile && modelViewerUrl && !showFallbackImage"
                :src="modelViewerUrl"
                class="model-iframe"
                :key="iframeKey"
                allow="xr-spatial-tracking"
                allowfullscreen
                @load="onIframeLoad"
                @error="onIframeError"
              ></iframe>
              
              <!-- 图片回退 -->
              <image
                v-else-if="showFallbackImage || !has3DModelFile"
                :src="selectedItem.image"
                mode="aspectFit"
                class="viewer-img"
              />
            </view>

            <!-- 展品详情侧（右侧） -->
            <view class="info-side">
              <scroll-view scroll-y class="info-scroll" :show-scrollbar="true" :enhanced="true">
                <view class="info-inner">
                  <view class="info-placard-head">
                    <view class="iph-left">
                      <text class="iph-cat">{{ selectedItem.category }}</text>
                      <text class="iph-num">No. {{ selectedItem.mid }}</text>
                    </view>
                    <view class="iph-seal">
                      <!-- ⑫ seal-circle 同心圆印章 -->
                      <image src="/static/icons/seal-circle.svg" mode="aspectFit" style="width:24px;height:24px;display:block;opacity:0.4;"/>
                    </view>
                  </view>

                  <text class="d-title">{{ selectedItem.name }}</text>

                  <view v-if="modelTags.length" class="d-block">
                    <text class="d-block-label">藏品标签</text>
                    <view class="d-tags">
                      <text v-for="(t,i) in modelTags" :key="i" class="d-tag"># {{ t.tagName }}</text>
                    </view>
                  </view>

                  <view class="d-block">
                    <view class="d-block-hd">
                      <text class="d-block-label">语音解说</text>
                      <text class="d-block-cnt">{{ modelAudios.length }} 段</text>
                    </view>
                    <view v-if="modelAudios.length" class="audio-list">
                      <view v-for="(au,ai) in modelAudios" :key="ai" class="audio-row">
                        <view class="play-btn" @click="playVoice(au.audioUrl)">
                          <!-- ⑬ play 播放三角 -->
                          <image src="/static/icons/play.svg" mode="aspectFit" style="width:9px;height:9px;display:block;"/>
                        </view>
                        <view class="wave-wrap">
                          <view v-for="w in 16" :key="w" class="w-bar" :style="{height:(4+Math.random()*10)+'px'}"></view>
                        </view>
                        <text class="au-dur">{{ au.duration ? au.duration+'s' : '--' }}</text>
                      </view>
                      <text v-if="modelAudios[0]?.transcript" class="au-trans">"{{ modelAudios[0].transcript }}"</text>
                    </view>
                    <view v-else class="d-empty">暂无语音解说</view>
                  </view>

                  <view class="d-block">
                    <text class="d-block-label">文字考据</text>
                    <text class="d-desc">{{ selectedItem.description }}</text>
                  </view>
                </view>
              </scroll-view>

              <view class="d-footer">
                <view
                  :class="['df-btn', 'primary', { disabled: !canPublishSelectedItem }]"
                  @click="publishSelectedModel">
                  <!-- ⑭ publish 发布圆圈加号 -->
                  <image src="/static/icons/publish.svg" mode="aspectFit" style="width:13px;height:13px;display:block;"/>
                  <text>{{ publishButtonText }}</text>
                </view>
                <view class="df-btn">
                  <!-- ⑮ export 导出下载 -->
                  <image src="/static/icons/export.svg" mode="aspectFit" style="width:13px;height:13px;display:block;"/>
                  <text>导出 OBJ</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

    </view>
  </common-layout>
</template>

<script>
import { markRaw } from 'vue';
import MyLayout from '@/components/common-layout/common-layout.vue';
import request, { BASE_URL } from '@/utils/request.js';

export default {
	components: { 'common-layout': MyLayout },
	data() {
		return {
			// 搜索和分类
			searchQuery: '', 
			activeCat: '全部',
			openCat: null,
			
			// 模态框和选中项
			isModalOpen: false, 
			selectedItem: null,
			
			// 数据列表
			categories: [], 
			models: [], 
			modelFiles: [], 
			modelAudios: [], 
			modelTags: [],
			
			// 音频
			audioContext: null,

			modelViewerUrl: '',        
			showFallbackImage: false,     
			iframeLoaded: false,      
			iframeKey: 0,               
			iframeLoadTimeout: null,      
			
			// 静态模型数据
			staticModels: [
				{ 
					mid:'FIXED_TOY_01', 
					name:'发条小玩具', 
					category:'艺术品', 
					description:'这是小玩具。', 
					image:'/static/models_for_test/Toy1_after.jpg', 
					status:'done', 
					progress:100, 
					tags:['可爱','好玩'], 
					localAudios:[
						{
							audioUrl:'/static/models_for_test/toy1.m4a',
							transcript:'这是我小时候最喜欢的发条玩具。',
							duration:12
						}
					], 
					isStatic:true, 
					localModelPath:'/models/Toy1.glb' 
				},
				{ 
					mid:'FIXED_TOY_02', 
					name:'小黄人', 
					category:'温情时刻', 
					description:'这个小黄人我非常喜欢，因为在求学过程中它一直陪伴着我，以这种方式留存，我很高兴。', 
					image:'/static/models_for_test/Toy2_after.jpg', 
					status:'done', 
					progress:100, 
					tags:['黄色'], 
					localAudios:[
						{
							audioUrl:'/static/models_for_test/toy2.m4a',
							transcript:'这个小黄人手办陪伴我度过了无数个学习的夜晚。',
							duration:12
						}
					], 
					isStatic:true, 
					localModelPath:'/models/Toy2.glb' 
				},
				{ 
					mid:'FIXED_TOY_03', 
					name:'灯具', 
					category:'日常用品', 
					description:'这是小时候奶奶家使用的灯具。', 
					image:'/static/models_for_test/Antique1_after.jpg', 
					status:'done', 
					progress:100, 
					tags:['生活'], 
					isStatic:true, 
					localModelPath:'/models/Antique1.glb' 
				},
				{ 
					mid:'FIXED_TOY_04', 
					name:'大鳄鱼', 
					category:'家庭回忆', 
					description:'这是我哥哥送给我的新年礼物。我想纪念下来', 
					image:'/static/models_for_test/lego2.jpg', 
					status:'done', 
					progress:100, 
					tags:['乐高','新年礼物'], 
					localAudios:[
						{
							audioUrl:'/static/models_for_test/Lego2.m4a',
							transcript:'这是我哥哥送给我的新年礼物。',
							duration:5
						}
					], 
					isStatic:true, 
					localModelPath:'/static/models_for_test/lego2.glb' 
				}
			]
		};
	},
	computed: {
		shelfCategories() {
			if (this.activeCat !== '全部') return [this.activeCat];
			return [...new Set(this.models.map(m => m.category).filter(Boolean))];
		},
		dynamicCategories() { 
			return ['全部', ...this.categories.map(c => c.name)]; 
		},
		realCategories() { 
			return [...new Set(this.models.map(m => m.category).filter(Boolean))]; 
		},
		openCatModels() {
			if (!this.openCat) return [];
			const q = this.searchQuery.toLowerCase();
			return this.models.filter(m => m.category === this.openCat && (!q || m.name.toLowerCase().includes(q)));
		},
		filteredModels() {
			const q = this.searchQuery.toLowerCase();
			return this.models.filter(m => {
				const matchCat = this.activeCat === '全部' || m.category === this.activeCat;
				const matchQ = !q || m.name.toLowerCase().includes(q);
				return matchCat && matchQ;
			});
		},

		has3DModelFile() { 
			return this.modelFiles.some(f => /\.(glb|gltf|ply)$/i.test(f.filePath)); 
		},
		currentModelPath() { 
			const f = this.modelFiles.find(f => /\.(glb|gltf|ply)$/i.test(f.filePath)); 
			return f ? f.filePath : null; 
		},
		canPublishSelectedItem() {
			return !!this.selectedItem && !this.selectedItem.isStatic && !this.selectedItem.isPublic;
		},
		publishButtonText() {
			if (!this.selectedItem) return '发布到超级博物馆';
			if (this.selectedItem.isStatic) return '示例藏品不可发布';
			if (this.selectedItem.isPublic) return '已发布到超级博物馆';
			return '发布到超级博物馆';
		}
	},
	created() {
		// 移除 threeManager 初始化
	},
	async onShow() { 
		await this.initData(); 
	},
	mounted() {
		// 初始化自定义滚动条
		this.$nextTick(() => { 
			this.initCustomScrollbar(); 
		});
	},
	beforeUnmount() {
		// 清理音频
		if (this.audioContext) this.audioContext.destroy();
		
		// 清理 iframe 加载超时定时器
		if (this.iframeLoadTimeout) {
			clearTimeout(this.iframeLoadTimeout);
			this.iframeLoadTimeout = null;
		}
	},
	methods: {
		getOriginBaseUrl() {
			return BASE_URL.replace(/\/api\/?$/, '').replace(/\/$/, '');
		},
		normalizeResourceUrl(url) {
			if (!url || typeof url !== 'string') return '';
			if (/^data:/i.test(url)) return url;
			const origin = this.getOriginBaseUrl();
			if (/^https?:\/\//i.test(url)) {
				return url.replace(/^https?:\/\/(localhost|127\.0\.0\.1)(:\d+)?/i, origin);
			}
			if (url.startsWith('//')) return `http:${url}`;
			if (url.startsWith('/')) return `${origin}${url}`;
			return `${origin}/${url.replace(/^\.?\//, '')}`;
		},
		// 分类过滤
		filteredByCat(cat) {
			const q = this.searchQuery.toLowerCase();
			return this.models.filter(m =>
				m.category === cat && (!q || m.name.toLowerCase().includes(q))
			);
		},

		// ── 自定义滚动条 ──
		initCustomScrollbar() {
			// #ifdef H5
			this.$nextTick(() => {
				const scrollView = document.querySelector('.gallery-scroll');
				const thumb = document.getElementById('gallery-thumb');
				if (!scrollView || !thumb) return;
				
				const updateThumb = () => {
					const scrollHeight = scrollView.scrollHeight;
					const clientHeight = scrollView.clientHeight;
					if (scrollHeight <= clientHeight) { 
						thumb.style.height = '0'; 
						return; 
					}
					const ratio = clientHeight / scrollHeight;
					thumb.style.height = Math.max(40, ratio * clientHeight) + 'px';
					thumb.style.top = (scrollView.scrollTop / scrollHeight * clientHeight) + 'px';
				};
				
				scrollView.addEventListener('scroll', updateThumb);
				setTimeout(updateThumb, 300);
				this._scrollbarCleanup = () => scrollView.removeEventListener('scroll', updateThumb);
			});
			// #endif
		},

		// ── 数据初始化 ──
		async initData() {
			try { 
				uni.showLoading({title:'加载中...'}); 
				await this.fetchCategories(); 
				await this.fetchMyModels(); 
				uni.hideLoading(); 
			} catch(e) { 
				uni.hideLoading(); 
			}
		},
		
		async fetchCategories() { 
			try { 
				this.categories = await request({url:'/categories',method:'GET'}); 
			} catch(e){} 
		},
		
		async fetchMyModels() {
			try {
				const resData = await request({url:'/models/my',method:'GET'});
				const remoteModels = resData.map(m => {
					const catObj = this.categories.find(c => Number(c.id)===Number(m.categoryId));
					return { 
						mid:m.mid, 
						name:m.title, 
						title:m.title,
						category:catObj?catObj.name:'未分类', 
						categoryId:m.categoryId,
						description:m.story, 
						story:m.story || '',
						productionYear:m.productionYear || null,
						image:m.thumbnailUrl||'https://img.js.design/assets/smartFill/img445164da74a8a8.png', 
						status:m.renderStatus==='success'?'done':'rendering', 
						progress:m.renderStatus==='success'?100:50, 
						tags:[], 
						isStatic:false,
						isPublic: !!m.isPublic
					};
				});
				
				await Promise.all(remoteModels.map(model => this.fetchModelTags(model.mid, model)));
				this.models = [...this.staticModels, ...remoteModels];
			} catch(e) { 
				this.models = [...this.staticModels]; 
			}
		},
		
		async fetchModelTags(mid, model=null) {
			try { 
				const data = await request({url:`/model-tags/model/${mid}`,method:'GET'}); 
				const tags=data.map(t=>t.tagName); 
				if(model) model.tags=tags; 
				return tags; 
			} catch(e) { 
				return []; 
			}
		},

		async publishSelectedModel() {
			if (!this.selectedItem) return;
			if (this.selectedItem.isStatic) {
				uni.showToast({ title:'示例藏品不可发布', icon:'none' });
				return;
			}
			if (this.selectedItem.isPublic) {
				uni.showToast({ title:'该藏品已发布', icon:'none' });
				return;
			}

			const item = this.selectedItem;
			const categoryId = item.categoryId || this.getCategoryIdByName(item.category);
			if (!categoryId) {
				uni.showToast({ title:'缺少分类，暂无法发布', icon:'none' });
				return;
			}

			const confirmRes = await new Promise((resolve) => {
				uni.showModal({
					title: '发布到超级博物馆',
					content: `确认将“${item.name || item.title || '该藏品'}”发布到超级博物馆吗？`,
					confirmText: '发布',
					cancelText: '取消',
					success: resolve,
					fail: () => resolve({ confirm: false, cancel: true })
				});
			});
			if (!confirmRes.confirm) return;

			try {
				const payload = {
					title: item.title || item.name || '未命名藏品',
					story: item.story || item.description || '',
					categoryId,
					isPublic: true,
					tags: Array.isArray(item.tags) ? item.tags : []
				};
				if (item.productionYear) payload.productionYear = item.productionYear;

				uni.showLoading({ title:'发布中...' });
				await request({
					url: `/models/update/${item.mid}`,
					method: 'POST',
					data: payload
				});
				this.markModelAsPublished(item.mid);
				uni.hideLoading();
				uni.showToast({ title:'发布成功', icon:'success' });
			} catch(e) {
				uni.hideLoading();
			}
		},

		getCategoryIdByName(categoryName) {
			const category = this.categories.find(c => c.name === categoryName);
			return category ? category.id : null;
		},

		markModelAsPublished(mid) {
			this.models = this.models.map(model => (
				String(model.mid) === String(mid)
					? { ...model, isPublic: true }
					: model
			));
			if (this.selectedItem && String(this.selectedItem.mid) === String(mid)) {
				this.selectedItem = { ...this.selectedItem, isPublic: true };
			}
		},
		
		// ── 打开详情 ──
		async openDetail(item) {
			// 重置状态
			this.selectedItem = item; 
			this.isModalOpen = true;
			this.modelFiles = []; 
			this.modelAudios = []; 
			this.modelTags = [];
			this.showFallbackImage = false;
			this.iframeLoaded = false;
			this.iframeKey += 1; // 强制 iframe 重新加载
			
			if (item.isStatic) {
				// 静态模型
				this.modelTags = (item.tags||[]).map(t=>({tagName:t}));
				this.modelAudios = item.localAudios||[];
				
				if (item.localModelPath) { 
					this.modelFiles=[{filePath:item.localModelPath}]; 
					this.$nextTick(()=>this.initIframeModelViewer(item.localModelPath, true)); 
				}
			} else { 
				// 动态模型
				await this.fetchModelAssets(item.mid); 
			}
		},
		
		// 播放语音
		playVoice(url) {
			if (this.audioContext) { 
				this.audioContext.stop(); 
				this.audioContext.destroy(); 
			}
			this.audioContext = uni.createInnerAudioContext();
			this.audioContext.src = this.normalizeResourceUrl(url); 
			this.audioContext.play();
			this.audioContext.onError(()=>uni.showToast({title:'播放失败',icon:'none'}));
		},
		
		// 获取模型资源
		async fetchModelAssets(mid) {
			// 获取模型文件
			request({url:`/model-files/model/${mid}`})
				.then(data=>{
					this.modelFiles=data;
					if(this.currentModelPath) {
						this.initIframeModelViewer(this.currentModelPath, false);
					}
				})
				.catch(error => {
					console.error('获取模型文件失败:', error);
				});
				
			// 获取音频
			request({url:`/model-audios/model/${mid}`})
				.then(data=>{
					this.modelAudios = data.map(au => ({
						...au,
						audioUrl: this.normalizeResourceUrl(au.audioUrl || au.url || ''),
						transcript: au.description || '',
						duration: au.duration || null
					}));
				})
				.catch(error => {
					console.error('获取音频失败:', error);
				});
				
			// 获取标签
			request({url:`/model-tags/model/${mid}`})
				.then(data=>{
					this.modelTags=data;
				})
				.catch(error => {
					console.error('获取标签失败:', error);
				});
		},
		
		initIframeModelViewer(modelPath, isStatic = false) {
			console.log('初始化 iframe 查看器，模型路径:', modelPath, '静态模型:', isStatic);

			if (!modelPath) {
				console.error('模型路径为空');
				this.showFallbackImage = true;
				return;
			}

			const localUrl = this.getBaseUrl();
			if (!localUrl) {
				console.error('无法获取基础 URL');
				this.showFallbackImage = true;
				return;
			}

			let cleanModelPath = modelPath;
			try {
				cleanModelPath = decodeURIComponent(modelPath);
			} catch (e) {
				console.log(`路径解码失败，使用原始路径: ${e}`);
			}

			if (isStatic) {
				if (cleanModelPath.startsWith('/')) {
				} else if (cleanModelPath.startsWith('http://') || cleanModelPath.startsWith('https://')) {
				} else {
					cleanModelPath = '/' + cleanModelPath;
				}
			} else {
				if (!cleanModelPath.startsWith('http://') && !cleanModelPath.startsWith('https://')) {
					if (!cleanModelPath.startsWith('/')) {
						cleanModelPath = '/' + cleanModelPath;
					}
				}
			}

			console.log('清理后模型路径:', cleanModelPath);

			try {
				let queryString = '';

				// #ifdef H5
				console.log('H5 环境，使用 URLSearchParams');
				const params = new URLSearchParams();

				let modelUrl = '';
				if (isStatic) {
					modelUrl = cleanModelPath;
				} else {
					let baseUrl = BASE_URL;
					if (baseUrl.endsWith('/api')) {
						baseUrl = baseUrl.replace('/api', '');
					}

					if (!baseUrl.endsWith('/')) {
						baseUrl = baseUrl + '/';
					}

					let relativePath = cleanModelPath;
					if (relativePath.startsWith('/')) {
						relativePath = relativePath.substring(1);
					}

					modelUrl = baseUrl + relativePath;
				}

				console.log('最终模型 URL:', modelUrl);

				params.set('model', modelUrl);

				
				let baseUrl = BASE_URL;
				if (baseUrl.endsWith('/api')) {
					baseUrl = baseUrl.replace('/api', '');
				}
				params.set('base_url', baseUrl);
				

				if (isStatic) {
					params.set('isStatic', 'true');
				} else {
					params.set('localPath', cleanModelPath);
				}

				queryString = params.toString();
				console.log('生成的查询字符串:', queryString);
				// #endif

				// #ifdef APP-PLUS
				console.log('APP 环境，手动构建查询字符串');

				let modelUrl = '';
				if (isStatic) {
				    if (cleanModelPath.startsWith('/')) {
				        modelUrl = cleanModelPath;
				    } else if (cleanModelPath.startsWith('stat')) {
				        modelUrl = cleanModelPath;
				    } else {
				        modelUrl = '/' + cleanModelPath;
				    }
				} else {
					let baseUrl = BASE_URL;
					if (baseUrl.endsWith('/api')) {
						baseUrl = baseUrl.replace('/api', '');
					}

					if (!baseUrl.endsWith('/')) {
						baseUrl = baseUrl + '/';
					}

					let relativePath = cleanModelPath;
					if (relativePath.startsWith('/')) {
						relativePath = relativePath.substring(1);
					}

					modelUrl = baseUrl + relativePath;
				}

				const paramsObj = {
					model: modelUrl
				};

				let baseUrl = BASE_URL;
				if (baseUrl.endsWith('/api')) {
					baseUrl = baseUrl.replace('/api', '');
				}
				paramsObj.base_url = baseUrl;		

				if (isStatic) {
					paramsObj.isStatic = 'true';
				} else {
					paramsObj.localPath = cleanModelPath;
				}

				const queryParts = [];
				for (const [key, value] of Object.entries(paramsObj)) {
					if (value !== null && value !== undefined) {
						queryParts.push(`${key}=${encodeURIComponent(value)}`);
					}
				}
				queryString = queryParts.join('&');
				// #endif

				const iframeUrl = localUrl + queryString;
				this.modelViewerUrl = iframeUrl;
				this.showFallbackImage = false;

				console.log('生成的 iframe URL:', iframeUrl);

				if (this.iframeLoadTimeout) {
					clearTimeout(this.iframeLoadTimeout);
				}

				this.iframeLoadTimeout = setTimeout(() => {
					if (!this.iframeLoaded) {
						console.warn('iframe 加载超时');
						this.showFallbackImage = true;
						uni.showToast({
							title: '3D模型加载超时',
							icon: 'none',
							duration: 2000
						});
					}
				}, 15000);
			} catch(e) {
				console.error('构建 iframe URL 时出错:', e);
				this.showFallbackImage = true;
				uni.showToast({
					title: '构建模型链接失败',
					icon: 'none',
					duration: 3000
				});
			}
		},
		
		// iframe 加载完成
		onIframeLoad() {
			console.log('iframe 加载完成');
			this.iframeLoaded = true;
			
			if (this.iframeLoadTimeout) {
				clearTimeout(this.iframeLoadTimeout);
				this.iframeLoadTimeout = null;
			}
		},
		
		// iframe 加载错误
		onIframeError(error) {
			console.error('iframe 加载错误:', error);
			this.showFallbackImage = true;
			this.iframeLoaded = false;
			
			if (this.iframeLoadTimeout) {
				clearTimeout(this.iframeLoadTimeout);
				this.iframeLoadTimeout = null;
			}
			
			uni.showToast({
				title: '3D模型加载失败',
				icon: 'none',
				duration: 3000
			});
		},
		
		// 获取基础URL
		getBaseUrl() {
			try {
				// #ifdef H5
				if (typeof window !== 'undefined' && window.location) {
					return window.location.origin + '/static/model-viewer.html?';
				}
				// #endif

				// #ifdef APP-PLUS
					return  'static/model-viewer.html?';
				// #endif

				return BASE_URL;
			} catch (e) {
				console.error('获取基础URL时出错: ', e);
				return BASE_URL;
			}
		},
		
		// ── 分类工具方法 ──
		getCategoryCount(catName) { 
			if(catName==='全部') return this.models.length; 
			return this.models.filter(m=>m.category===catName).length; 
		},
		
		modelsByCat(cat) { 
			return this.models.filter(m => m.category === cat); 
		},
		
		// ── 关闭详情 ──
		closeDetail() {
			this.isModalOpen = false; 
			this.selectedItem = null;
			this.modelViewerUrl = '';
			this.showFallbackImage = false;
			this.iframeLoaded = false;
			
			// 清理音频
			if(this.audioContext) this.audioContext.stop();
			
			// 清理 iframe 加载超时定时器
			if (this.iframeLoadTimeout) {
				clearTimeout(this.iframeLoadTimeout);
				this.iframeLoadTimeout = null;
			}
		}
	}
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;0,700;1,400&family=Lora:wght@400;500&family=IM+Fell+English+SC&display=swap');

/* iframe 容器样式 */
.model-iframe {
  width: 100%;
  height: 100%;
  border: none;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
  background: transparent;
  display: block;
}

/* 图片回退样式 */
.viewer-img { 
  width: 76%; 
  height: 66%; 
  border-radius: 4px; 
  filter: sepia(10%); 
  object-fit: contain; 
  z-index: 0;
}

.df-btn.disabled {
  opacity: 0.55;
}

/* ══════════════════════════════════════════
   CSS 变量
══════════════════════════════════════════ */
.museum {
  --parch:  #FAF7EF;
  --cream:  #F2EBD9;
  --ink:    #3D2B1A;
  --rust:   #8B4513;
  --gold:   #C8923A;
  --dust:   #B8A898;
  --fade:   #9A8878;
  --warm:   #7A5C3A;
  --edge:   rgba(122,92,58,0.15);
  --edge2:  rgba(122,92,58,0.28);

  display: flex;
  flex-direction: column;
  height: 100%;
  max-height: 100%;
  overflow: hidden;

  font-family: 'Lora', Georgia, serif;
  background: #E8DFC8;
  background-image:
    radial-gradient(ellipse at 18% 18%, rgba(200,146,58,0.09) 0%, transparent 55%),
    radial-gradient(ellipse at 82% 82%, rgba(139,69,19,0.07) 0%, transparent 55%),
    linear-gradient(rgba(122,92,58,0.025) 1px, transparent 1px),
    linear-gradient(90deg, rgba(122,92,58,0.025) 1px, transparent 1px);
  background-size: auto, auto, 52px 52px, 52px 52px;
}

/* ══════════════════════════════════════════
   入口横幅
══════════════════════════════════════════ */
.museum-entrance {
  flex-shrink: 0;
  background: rgba(250,247,239,0.98);
  border-bottom: 1px solid var(--edge2);
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 16px rgba(61,43,26,0.07);
}
.entrance-tape {
  height: 6px;
  background: repeating-linear-gradient(
    -45deg, var(--rust), var(--rust) 6px,
    rgba(139,69,19,0.14) 6px, rgba(139,69,19,0.14) 12px
  );
  opacity: 0.28;
}
.entrance-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 28px 13px;
  gap: 20px;
}
.museum-brand { display: flex; flex-direction: column; gap: 5px; }
.brand-row { display: flex; align-items: center; gap: 12px; }
.brand-seal {
  width: 36px; height: 36px; border-radius: 50%;
  border: 1.5px solid rgba(139,69,19,0.38);
  background: rgba(139,69,19,0.07);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  animation: sealBreath 4s ease-in-out infinite;
}
@keyframes sealBreath { 0%,100%{border-color:rgba(139,69,19,0.28)} 50%{border-color:rgba(200,146,58,0.65)} }
.brand-name-row { display: flex; align-items: baseline; gap: 10px; }
.brand-name { font-family: 'Playfair Display', serif; font-size: 24px; font-weight: 700; color: var(--ink); }
.brand-divider { color: var(--dust); }
.brand-sub-name { font-family: 'IM Fell English SC', serif; font-size: 12px; color: var(--fade); letter-spacing: 2.5px; }
.museum-desc { font-family: 'IM Fell English SC', serif; font-size: 10px; color: var(--dust); letter-spacing: 1.5px; }
.entrance-right { display: flex; align-items: center; gap: 12px; flex-shrink: 0; }
.search-wrap {
  display: flex; align-items: center; gap: 8px;
  background: rgba(122,92,58,0.04); border: 1px solid var(--edge2);
  border-radius: 4px; padding: 0 14px; width: 210px;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.search-wrap:focus-within { border-color: rgba(139,69,19,0.45); box-shadow: 0 0 0 3px rgba(139,69,19,0.06); }
.search-input { font-family: 'Lora', serif; font-size: 13px; color: var(--ink); flex: 1; padding: 9px 0; }
.live-badge {
  display: flex; align-items: center; gap: 6px;
  padding: 7px 14px; border-radius: 4px;
  border: 1px dashed rgba(139,69,19,0.3); background: rgba(139,69,19,0.04);
  transition: 0.2s; cursor: default;
}
.live-badge:hover { border-color: rgba(139,69,19,0.5); background: rgba(139,69,19,0.08); }
.live-pip { width: 6px; height: 6px; border-radius: 50%; background: var(--rust); animation: pip 2s ease-in-out infinite; }
@keyframes pip { 0%,100%{opacity:0.5;transform:scale(1)} 50%{opacity:1;transform:scale(1.6)} }
.live-txt { font-family: 'IM Fell English SC', serif; font-size: 11px; color: var(--rust); letter-spacing: 2.5px; }

/* ══════════════════════════════════════════
   移动端分类切换栏
══════════════════════════════════════════ */
.mobile-cat-bar { display: none; flex-shrink: 0; }
.mobile-cat-scroll { width: 100%; white-space: nowrap; }
.mobile-cat-inner { display: inline-flex; align-items: center; gap: 8px; padding: 9px 14px; }
.mobile-cat-chip {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 6px 13px; border-radius: 20px;
  border: 1px solid var(--edge2); background: rgba(250,247,239,0.9);
  cursor: pointer; transition: all 0.2s; white-space: nowrap; flex-shrink: 0;
}
.mobile-cat-chip.active { background: var(--rust); border-color: var(--rust); }
.mobile-cat-chip.active .mcc-txt { color: #FAF7EF; }
.mobile-cat-chip.active .mcc-num { background: rgba(255,255,255,0.25); color: #FAF7EF; border-color: transparent; }
.mcc-txt { font-family: 'Lora', serif; font-size: 12px; color: var(--warm); letter-spacing: 0.5px; }
.mcc-num { font-family: 'IM Fell English SC', serif; font-size: 10px; color: var(--fade); background: rgba(122,92,58,0.1); border: 1px solid var(--edge); border-radius: 10px; padding: 1px 6px; min-width: 18px; text-align: center; }

/* ══════════════════════════════════════════
   展厅主体
══════════════════════════════════════════ */
.gallery-hall { flex: 1; min-height: 0; display: flex; overflow: hidden; }

/* ── 左侧目录 ── */
.exhibit-index { width: 190px; flex-shrink: 0; background: rgba(245,240,228,0.95); border-right: 1px solid var(--edge2); display: flex; flex-direction: column; overflow: hidden; min-height: 0; }
.index-header { flex-shrink: 0; padding: 18px 14px 12px; border-bottom: 1px dashed var(--edge); }
.index-label { font-family: 'IM Fell English SC', serif; font-size: 10px; color: var(--dust); letter-spacing: 2.5px; display: block; margin-bottom: 8px; }
.index-count-row { display: flex; align-items: baseline; gap: 5px; }
.index-num { font-family: 'Playfair Display', serif; font-size: 28px; font-weight: 700; color: var(--rust); }
.index-unit { font-family: 'IM Fell English SC', serif; font-size: 11px; color: var(--fade); letter-spacing: 1.5px; }

.index-scroll-wrap { flex: 1; min-height: 0; position: relative; display: flex; overflow: hidden; }
.index-scroll { flex: 1; overflow-y: auto; padding: 10px 8px; scrollbar-width: none; }
.index-scroll::-webkit-scrollbar { display: none; }

.custom-scrollbar-track { width: 4px; background: rgba(122,92,58,0.08); margin: 12px 5px 12px 0; border-radius: 4px; flex-shrink: 0; position: relative; overflow: hidden; }
.custom-scrollbar-thumb { position: absolute; top: 0; left: 0; width: 100%; height: 50px; background: linear-gradient(180deg, var(--rust) 0%, var(--gold) 100%); border-radius: 4px; opacity: 0.45; transition: opacity 0.2s; }
.custom-scrollbar-track:hover .custom-scrollbar-thumb { opacity: 0.8; }

.hall-item { padding: 0; border-radius: 4px; cursor: pointer; border: 1px solid transparent; transition: all 0.22s; position: relative; margin-bottom: 3px; overflow: hidden; }
.hall-item.active { background: rgba(139,69,19,0.09); border-color: rgba(139,69,19,0.22); }
.hall-item:hover:not(.active) { background: rgba(122,92,58,0.06); border-color: var(--edge); }
.hall-bar { position: absolute; left: 0; top: 0; bottom: 0; width: 3px; background: linear-gradient(180deg, var(--rust), var(--gold)); border-radius: 0 2px 2px 0; opacity: 0; transition: opacity 0.2s; }
.hall-item.active .hall-bar { opacity: 1; }
.hall-item-inner { display: flex; align-items: center; justify-content: space-between; padding: 9px 10px 9px 14px; }
.hall-name { font-family: 'Lora', serif; font-size: 13px; color: var(--ink); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex: 1; }
.hall-item.active .hall-name { color: var(--rust); font-weight: 500; }
.hall-badge { flex-shrink: 0; min-width: 22px; height: 18px; border-radius: 9px; background: rgba(122,92,58,0.1); border: 1px solid var(--edge); display: flex; align-items: center; justify-content: center; padding: 0 5px; }
.hall-item.active .hall-badge { background: rgba(139,69,19,0.15); border-color: rgba(139,69,19,0.25); }
.hall-meta { font-family: 'IM Fell English SC', serif; font-size: 10px; color: var(--fade); }
.hall-item.active .hall-meta { color: var(--rust); }

.index-footer { flex-shrink: 0; padding: 0 14px; border-top: 1px dashed var(--edge); min-height: 44px; display: flex; align-items: center; }
.new-exhibit-btn { width: 100%; height: 38px; border-radius: 4px; border: 1.5px dashed rgba(139,69,19,0.3); display: flex; align-items: center; justify-content: center; gap: 7px; cursor: pointer; transition: all 0.22s; }
.new-exhibit-btn:hover { border-color: rgba(139,69,19,0.55); background: rgba(139,69,19,0.05); transform: translateY(-1px); }
.new-txt { font-family: 'IM Fell English SC', serif; font-size: 11px; color: var(--warm); letter-spacing: 1.5px; }

/* ── 右侧展厅 ── */
.gallery-main { flex: 1; min-width: 0; min-height: 0; display: flex; flex-direction: column; overflow: hidden; }

.gallery-nav { flex-shrink: 0; padding: 10px 22px; display: flex; align-items: center; gap: 10px; background: rgba(250,247,239,0.82); border-bottom: 1px dashed var(--edge); }
.gnav-crumb { display: flex; align-items: center; gap: 6px; }
.gnav-item { font-family: 'IM Fell English SC', serif; font-size: 12px; color: var(--fade); letter-spacing: 1px; }
.gnav-sep { color: var(--dust); font-size: 13px; }
.gnav-item.cur { color: var(--rust); }
.gnav-space { flex: 1; }
.gnav-count-txt { font-family: 'IM Fell English SC', serif; font-size: 11px; color: var(--dust); letter-spacing: 1px; }

.gallery-scroll-wrap { flex: 1; min-height: 0; position: relative; display: flex; overflow: hidden; }
.gallery-scroll { flex: 1; overflow-y: auto; padding: 22px 24px 20px; scrollbar-width: none; }
.gallery-scroll::-webkit-scrollbar { display: none; }

.gallery-scrollbar-track { width: 6px; background: rgba(122,92,58,0.07); margin: 14px 6px 14px 2px; border-radius: 6px; flex-shrink: 0; position: relative; overflow: hidden; border: 1px solid rgba(122,92,58,0.1); }
.gallery-scrollbar-thumb { position: absolute; top: 0; left: 0; width: 100%; height: 80px; background: linear-gradient(180deg, rgba(139,69,19,0.5) 0%, rgba(200,146,58,0.65) 50%, rgba(139,69,19,0.4) 100%); border-radius: 6px; transition: top 0.08s linear, height 0.15s ease; box-shadow: 0 1px 4px rgba(139,69,19,0.18); }
.gallery-scrollbar-track:hover .gallery-scrollbar-thumb { background: linear-gradient(180deg, var(--rust) 0%, var(--gold) 50%, var(--rust) 100%); }

/* 展厅标题 */
.hall-heading { margin-bottom: 18px; flex-shrink: 0; }
.hall-heading-inner { display: flex; align-items: flex-end; justify-content: space-between; padding-bottom: 12px; border-bottom: 1px solid rgba(122,92,58,0.17); }
.hall-title-group { display: flex; flex-direction: column; gap: 4px; }
.hall-icon-row { display: flex; align-items: center; gap: 10px; }
.hall-icon-dot { width: 10px; height: 10px; border-radius: 50%; background: var(--gold); box-shadow: 0 0 0 3px rgba(200,146,58,0.18); }
.hall-heading-name { font-family: 'Playfair Display', serif; font-size: 22px; font-weight: 700; color: var(--ink); }
.hall-heading-desc { font-family: 'IM Fell English SC', serif; font-size: 10px; color: var(--dust); letter-spacing: 2px; margin-left: 20px; }
.hall-piece-count { font-family: 'Playfair Display', serif; font-size: 15px; color: var(--fade); }

/* ══════════════════════════════════════════
   展品网格
══════════════════════════════════════════ */
.exhibit-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(190px, 1fr)); gap: 18px; width: 100%; box-sizing: border-box; align-content: start; }

.exhibit-case { background: var(--parch); border: 1px solid rgba(122,92,58,0.2); border-radius: 5px; overflow: hidden; cursor: pointer; transition: transform 0.28s cubic-bezier(0.16,1,0.3,1), box-shadow 0.28s; box-shadow: 0 1px 0 rgba(255,255,255,0.65) inset, 2px 4px 0 rgba(122,92,58,0.08), 4px 8px 0 rgba(122,92,58,0.04); display: flex; flex-direction: column; position: relative; min-width: 0; }
.exhibit-case:hover { transform: translateY(-6px) scale(1.01); box-shadow: 0 1px 0 rgba(255,255,255,0.65) inset, 3px 8px 0 rgba(122,92,58,0.1), 6px 16px 24px rgba(61,43,26,0.13); }
.exhibit-tape { height: 4px; background: repeating-linear-gradient(90deg, var(--rust) 0, var(--rust) 6px, transparent 6px, transparent 14px); opacity: 0.2; flex-shrink: 0; }

.case-img-wrap { position: relative; flex-shrink: 0; background: rgba(122,92,58,0.06); aspect-ratio: 4 / 3; overflow: hidden; }
.case-img { width: 100%; height: 100%; display: block; filter: sepia(10%) brightness(0.95); object-fit: cover; }
.case-badge { position: absolute; top: 10px; right: 10px; z-index: 2; padding: 4px 9px; border-radius: 3px; font-family: 'IM Fell English SC', serif; font-size: 9px; letter-spacing: 1.5px; background: rgba(250,247,239,0.94); border: 1px solid rgba(122,92,58,0.25); color: var(--warm); }
.case-badge.rdr { color: #9A6020; border-color: rgba(154,96,32,0.3); }
.case-num-tag { position: absolute; bottom: 9px; left: 9px; z-index: 2; font-family: 'IM Fell English SC', serif; font-size: 9px; color: var(--dust); background: rgba(245,240,228,0.9); padding: 3px 7px; border-radius: 3px; border: 1px solid rgba(122,92,58,0.15); }
.case-progress { position: absolute; bottom: 0; left: 0; right: 0; height: 3px; background: rgba(122,92,58,0.1); }
.cp-fill { height: 100%; background: linear-gradient(90deg, var(--rust), var(--gold)); transition: width 0.4s; }

.case-placard { padding: 13px 14px 15px; border-top: 1px solid rgba(122,92,58,0.1); flex: 1; display: flex; flex-direction: column; }
.placard-category { font-family: 'IM Fell English SC', serif; font-size: 10px; color: var(--dust); letter-spacing: 2px; display: block; margin-bottom: 4px; }
.placard-name { font-family: 'Playfair Display', serif; font-size: 15px; font-weight: 700; color: var(--ink); display: block; line-height: 1.3; margin-bottom: 8px; }
.placard-tags { display: flex; flex-wrap: wrap; gap: 4px; margin-bottom: 10px; flex: 1; }
.p-tag { font-family: 'IM Fell English SC', serif; font-size: 9px; color: var(--fade); padding: 3px 7px; border: 1px dashed rgba(122,92,58,0.22); border-radius: 3px; }
.placard-footer { display: flex; align-items: center; justify-content: space-between; margin-top: auto; }
.pf-id { font-family: 'IM Fell English SC', serif; font-size: 9px; color: var(--dust); }
.pf-audio { display: flex; align-items: center; gap: 4px; padding: 4px 9px; border-radius: 3px; border: 1px solid rgba(122,92,58,0.2); background: rgba(122,92,58,0.05); cursor: pointer; transition: 0.2s; }
.pf-audio:hover { border-color: var(--edge2); background: rgba(122,92,58,0.12); }
.pf-audio-dot { width: 5px; height: 5px; border-radius: 50%; background: var(--rust); }
.pf-audio-txt { font-family: 'IM Fell English SC', serif; font-size: 9px; color: var(--warm); letter-spacing: 0.5px; }

.add-exhibit { border: 1.5px dashed rgba(122,92,58,0.2); border-radius: 5px; cursor: pointer; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 10px; transition: all 0.25s; background: rgba(245,240,228,0.45); padding: 24px 0; }
.add-exhibit:hover { border-color: rgba(139,69,19,0.42); background: rgba(122,92,58,0.06); transform: translateY(-3px); }
.add-circle { width: 42px; height: 42px; border-radius: 50%; border: 1.5px dashed rgba(122,92,58,0.32); display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.add-exhibit:hover .add-circle { border-color: rgba(139,69,19,0.5); }
.add-label { font-family: 'Lora', serif; font-size: 13px; color: var(--fade); letter-spacing: 1px; }
.add-label-sub { font-family: 'IM Fell English SC', serif; font-size: 10px; color: rgba(184,168,152,0.7); letter-spacing: 1px; }

.empty-hall { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 60px 0; }
.empty-t { font-family: 'Playfair Display', serif; font-size: 17px; color: var(--fade); }
.empty-s { font-family: 'IM Fell English SC', serif; font-size: 12px; color: var(--dust); letter-spacing: 1.5px; }

/* ══════════════════════════════════════════
   底部信息条
══════════════════════════════════════════ */
.museum-footer { flex-shrink: 0; padding: 9px 22px; display: flex; align-items: center; gap: 18px; background: rgba(245,240,228,0.95); border-top: 1px solid var(--edge2); box-shadow: 0 -2px 10px rgba(61,43,26,0.05); min-height: 44px; }
.mf-stat { display: flex; align-items: baseline; gap: 4px; }
.mf-num { font-family: 'Playfair Display', serif; font-size: 15px; font-weight: 700; color: var(--rust); }
.mf-lbl { font-family: 'IM Fell English SC', serif; font-size: 10px; color: var(--fade); letter-spacing: 1.5px; }
.mf-div { width: 1px; height: 14px; background: var(--edge2); flex-shrink: 0; }
.mf-spacer { flex: 1; }
.mf-hint { font-family: 'IM Fell English SC', serif; font-size: 10px; color: var(--dust); letter-spacing: 1.5px; font-style: italic; }

/* ══════════════════════════════════════════
   弹窗
══════════════════════════════════════════ */
.modal-mask { position: absolute; inset: 0; z-index: 100; background: rgba(30,18,8,0.74); backdrop-filter: blur(14px); display: flex; align-items: center; justify-content: center; }
.modal-box { width: 90%; height: 92%; background: var(--parch); border: 1px solid var(--edge2); border-radius: 6px; overflow: hidden; display: flex; flex-direction: column; box-shadow: 6px 8px 0 rgba(61,43,26,0.1), 12px 16px 0 rgba(61,43,26,0.06), 0 40px 80px rgba(61,43,26,0.28); position: relative; }
.modal-box::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 4px; background: repeating-linear-gradient(-45deg, var(--rust), var(--rust) 6px, rgba(139,69,19,0.14) 6px, rgba(139,69,19,0.14) 12px); opacity: 0.32; z-index: 1; }
.modal-topbar { flex-shrink: 0; display: flex; align-items: center; gap: 10px; padding: 13px 20px; border-bottom: 1px dashed rgba(122,92,58,0.2); background: rgba(245,240,228,0.95); }
.mtb-wax { width: 11px; height: 11px; border-radius: 50%; background: var(--rust); opacity: 0.75; flex-shrink: 0; }
.mtb-label { font-family: 'IM Fell English SC', serif; font-size: 13px; color: var(--warm); letter-spacing: 2px; }
.mtb-rule { flex: 1; height: 1px; background: linear-gradient(90deg, var(--edge2), transparent); }
.mtb-cat { font-family: 'IM Fell English SC', serif; font-size: 11px; color: var(--dust); letter-spacing: 1px; }
.modal-close { width: 28px; height: 28px; border-radius: 4px; flex-shrink: 0; background: rgba(122,92,58,0.07); border: 1px solid var(--edge); display: flex; align-items: center; justify-content: center; cursor: pointer; color: var(--fade); transition: 0.2s; }
.modal-close:hover { background: rgba(139,50,20,0.1); border-color: rgba(139,50,20,0.28); color: var(--rust); }
.modal-body { display: flex; flex: 1; min-height: 0; }

/* 3D 查看器 */
.viewer-side { 
  flex: 1.3; 
  background: rgba(28,22,16,0.98); 
  border-right: 1px solid rgba(122,92,58,0.2); 
  display: flex; 
  flex-direction: column; 
  justify-content: center; 
  align-items: center; 
  position: relative; 
  overflow: hidden; 
  min-height: 0; 
}

/* 展品详情侧 */
.info-side { 
  flex: 1; 
  display: flex; 
  flex-direction: column; 
  min-height: 0; 
  background: var(--parch); 
}
.info-scroll { 
  flex: 1; 
  min-height: 0; 
}
.info-scroll::-webkit-scrollbar { 
  width: 5px; 
}
.info-scroll::-webkit-scrollbar-track { 
  background: rgba(122,92,58,0.05); 
}
.info-scroll::-webkit-scrollbar-thumb { 
  background: linear-gradient(180deg, var(--rust), var(--gold)); 
  border-radius: 4px; 
}
.info-scroll::-webkit-scrollbar-thumb:hover { 
  background: var(--rust); 
}

.info-inner { 
  padding: 22px 26px 10px; 
  display: flex; 
  flex-direction: column; 
}
.info-placard-head { 
  display: flex; 
  align-items: flex-start; 
  justify-content: space-between; 
  margin-bottom: 10px; 
}
.iph-left { 
  display: flex; 
  flex-direction: column; 
  gap: 3px; 
}
.iph-cat { 
  font-family: 'IM Fell English SC', serif; 
  font-size: 11px; 
  color: var(--dust); 
  letter-spacing: 2.5px; 
  display: block; 
}
.iph-num { 
  font-family: 'IM Fell English SC', serif; 
  font-size: 11px; 
  color: var(--fade); 
  letter-spacing: 1.5px; 
  display: block; 
}
.iph-seal { 
  opacity: 0.4; 
}
.d-title { 
  display: block; 
  font-family: 'Playfair Display', serif; 
  font-size: 22px; 
  font-weight: 700; 
  color: var(--ink); 
  margin-bottom: 18px; 
  line-height: 1.3; 
}

.d-block { 
  margin-bottom: 18px; 
}
.d-block-hd { 
  display: flex; 
  align-items: center; 
  gap: 10px; 
  margin-bottom: 11px; 
}
.d-block-label { 
  display: block; 
  font-family: 'IM Fell English SC', serif; 
  font-size: 10px; 
  color: var(--dust); 
  letter-spacing: 3px; 
  margin-bottom: 11px; 
  text-transform: uppercase; 
}
.d-block-cnt { 
  font-family: 'IM Fell English SC', serif; 
  font-size: 10px; 
  color: var(--fade); 
  background: rgba(122,92,58,0.07); 
  border: 1px solid var(--edge); 
  border-radius: 3px; 
  padding: 2px 8px; 
}
.d-tags { 
  display: flex; 
  flex-wrap: wrap; 
  gap: 7px; 
}
.d-tag { 
  font-family: 'IM Fell English SC', serif; 
  font-size: 11px; 
  color: var(--warm); 
  padding: 5px 12px; 
  border-radius: 3px; 
  background: rgba(122,92,58,0.07); 
  border: 1px dashed rgba(122,92,58,0.22); 
  transition: 0.2s; 
}
.d-tag:hover { 
  border-color: var(--edge2); 
  background: rgba(122,92,58,0.13); 
}
.d-desc { 
  font-family: 'Lora', Georgia, serif; 
  font-size: 14px; 
  color: var(--fade); 
  line-height: 2.0; 
}
.d-empty { 
  font-family: 'IM Fell English SC', serif; 
  font-size: 11px; 
  color: var(--dust); 
  text-align: center; 
  padding: 14px; 
  border: 1px dashed var(--edge); 
  border-radius: 4px; 
  letter-spacing: 1.5px; 
}

/* 音频 */
.audio-list { 
  display: flex; 
  flex-direction: column; 
  gap: 9px; 
}
.audio-row { 
  display: flex; 
  align-items: center; 
  gap: 11px; 
  padding: 11px 14px; 
  border-radius: 5px; 
  background: rgba(122,92,58,0.05); 
  border: 1px solid var(--edge); 
  transition: 0.2s; 
}
.audio-row:hover { 
  border-color: var(--edge2); 
  background: rgba(122,92,58,0.09); 
}
.play-btn { 
  width: 32px; 
  height: 32px; 
  border-radius: 50%; 
  flex-shrink: 0; 
  background: rgba(139,69,19,0.1); 
  border: 1px solid rgba(139,69,19,0.28); 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  color: var(--rust); 
  cursor: pointer; 
  transition: 0.2s; 
}
.play-btn:hover { 
  background: rgba(139,69,19,0.22); 
  transform: scale(1.05); 
}
.wave-wrap { 
  flex: 1; 
  display: flex; 
  align-items: center; 
  gap: 2px; 
  height: 18px; 
  overflow: hidden; 
}
.w-bar { 
  flex: 1; 
  max-width: 3px; 
  background: rgba(122,92,58,0.25); 
  border-radius: 2px; 
}
.audio-row:hover .w-bar { 
  background: rgba(139,69,19,0.5); 
}
.au-dur { 
  font-family: 'IM Fell English SC', serif; 
  font-size: 10px; 
  color: var(--dust); 
  min-width: 26px; 
  text-align: right; 
}
.au-trans { 
  display: block; 
  margin-top: 9px; 
  padding: 9px 14px; 
  font-family: 'Lora', serif; 
  font-size: 13px; 
  color: var(--fade); 
  font-style: italic; 
  line-height: 1.8; 
  border-left: 2.5px solid rgba(139,69,19,0.32); 
  background: rgba(122,92,58,0.04); 
  border-radius: 0 5px 5px 0; 
}

/* 底部按钮 */
.d-footer { 
  flex-shrink: 0; 
  display: flex; 
  gap: 12px; 
  padding: 14px 26px 17px; 
  border-top: 1px dashed var(--edge); 
}
.df-btn { 
  flex: 1; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  gap: 8px; 
  height: 44px; 
  border-radius: 5px; 
  cursor: pointer; 
  font-family: 'IM Fell English SC', serif; 
  font-size: 12px; 
  letter-spacing: 0.8px; 
  border: 1px solid var(--edge); 
  background: rgba(122,92,58,0.05); 
  color: var(--fade); 
  transition: 0.22s; 
}
.df-btn:hover { 
  background: rgba(122,92,58,0.11); 
  border-color: var(--edge2); 
  color: var(--warm); 
  transform: translateY(-1px); 
}
.df-btn.primary { 
  flex: 1.5; 
  background: rgba(139,69,19,0.09); 
  border-color: rgba(139,69,19,0.28); 
  color: var(--rust); 
}
.df-btn.primary:hover { 
  background: rgba(139,69,19,0.16); 
  box-shadow: 2px 4px 0 rgba(61,43,26,0.12); 
}

/* ══════════════════════════════════════════
   响应式
══════════════════════════════════════════ */
@media (max-width: 1199px) {
  .exhibit-index { width: 165px; }
  .search-wrap { width: 180px; }
  .brand-name { font-size: 21px; }
  .exhibit-grid { grid-template-columns: repeat(auto-fill, minmax(175px, 1fr)); gap: 14px; }
}
@media (max-width: 899px) {
  .exhibit-index { width: 138px; }
  .entrance-inner { padding: 12px 18px 10px; }
  .search-wrap { width: 140px; }
  .exhibit-grid { grid-template-columns: repeat(auto-fill, minmax(148px, 1fr)); gap: 12px; }
  .gallery-scroll { padding: 16px 16px 16px; }
  .mf-hint { display: none; }
  .hall-name { font-size: 12px; }
  .index-num { font-size: 24px; }
}
@media (max-width: 599px) {
  .entrance-inner { flex-direction: column; align-items: flex-start; gap: 10px; padding: 12px 16px 10px; }
  .entrance-right { width: 100%; }
  .search-wrap { flex: 1; width: auto; }
  .museum-desc { display: none; }
  .mobile-cat-bar { display: block; background: rgba(245,240,228,0.97); border-bottom: 1px solid var(--edge2); box-shadow: 0 2px 8px rgba(61,43,26,0.05); }
  .exhibit-index { display: none; }
  .gallery-scroll { padding: 12px 12px 12px; }
  .exhibit-grid { grid-template-columns: repeat(2, 1fr); gap: 10px; }
  .case-img-wrap { aspect-ratio: 1 / 1; }
  .placard-tags { display: none; }
  .case-placard { padding: 10px 10px 11px; }
  .placard-name { font-size: 13px; margin-bottom: 6px; }
  .modal-box { width: 100%; height: 96%; border-radius: 6px 6px 0 0; }
  .modal-body { flex-direction: column; }
  .viewer-side { flex: none; height: 52vw; min-height: 200px; border-right: none; border-bottom: 1px solid var(--edge); }
  .info-inner { padding: 14px 16px 8px; }
  .d-footer { padding: 10px 16px 14px; }
  .museum-footer { gap: 10px; padding: 7px 14px; min-height: 40px; }
  .mf-hint { display: none; }
  .mf-div:nth-child(n+8) { display: none; }
  .gnav-item, .gnav-count-txt { font-size: 11px; }
  .gallery-nav { padding: 8px 14px; }
  .hall-heading-name { font-size: 17px; }
  .hall-heading-desc { display: none; }
}
@media (max-width: 375px) {
  .exhibit-grid { gap: 8px; }
  .case-placard { padding: 8px 8px 10px; }
  .placard-name { font-size: 12px; }
  .placard-category { font-size: 9px; }
  .mcc-txt { font-size: 11px; }
  .brand-name { font-size: 19px; }
}
</style>
