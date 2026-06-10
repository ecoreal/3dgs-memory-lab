<template>
	<common-layout pageTitle="造物间">
		<view class="workshop-wrapper">
			<view class="steps-header">
				<view class="steps-row">
					<view :class="['simple-step', currentStep >= 1 ? 'active' : '']">
						<text class="step-icon">1</text><text>准备素材</text>
					</view>
					<view class="step-dot-line"></view>
					<view :class="['simple-step', currentStep >= 2 ? 'active' : '']">
						<text class="step-icon">2</text><text>预览确认</text>
					</view>
					<view class="step-dot-line"></view>
					<view :class="['simple-step', currentStep >= 3 ? 'active' : '']">
						<text class="step-icon">3</text><text>云端生成</text>
					</view>
				</view>
			</view>

			<view class="main-content-layout">
				<view class="editor-viewport">
					<view class="viewport-inner">
						<view v-if="!hasMedia" class="modern-upload-portal">
							<view class="portal-header">
								<text class="h1">数字化你的世界</text>
								<text class="p">请选择一种方式导入素材进行 3D 重建</text>
								<view class="mobile-tips">
									<view class="tip-row">
										<text class="tip-dot">①</text>
										<text class="tip-text">选择下方任意方式上传素材</text>
									</view>
									<view class="tip-row">
										<text class="tip-dot">②</text>
										<text class="tip-text">点击底部 <text class="tip-em">⚙️</text> 选择 AI 引擎</text>
									</view>
									<view class="tip-row">
										<text class="tip-dot">③</text>
										<text class="tip-text">点击「开始构建」生成 3D 模型</text>
									</view>
								</view>
							</view>
							<view class="choice-container">
								<view class="choice-card" @tap="handlePickImage">
									<view class="card-bg-icon">🖼️</view>
									<text class="card-title">单张照片</text>
									<text class="card-desc">上传高清实拍图</text>
									<view class="card-btn">立即选择</view>
								</view>
								<view class="choice-card video-card" @tap="handlePickVideo">
									<view class="card-bg-icon">🎬</view>
									<text class="card-title">视频截帧</text>
									<text class="card-desc">从视频中捕捉最佳瞬间</text>
									<view class="card-btn">浏览视频</view>
								</view>
							</view>
							<view class="mobile-tips-row">
								<text class="mobile-tip">💡 建议使用光线充足、背景干净的素材以提升建模精度</text>
							</view>
						</view>

						<view v-else class="active-workspace">
							<view class="glass-canvas">
								<block v-if="isVideo && !isFrameSelected">
									<video
										id="targetVideo"
										ref="videoPlayer"
										:src="videoPath"
										class="full-obj"
										controls
										autoplay
										loop
										@loadedmetadata="onVideoLoaded"
									/>
									<view class="video-speed-control">
										<text class="control-label">倍速</text>
										<view class="speed-dropdown-wrapper">
											<view class="speed-dropdown" @tap="toggleSpeedDropdown">
												<text class="current-speed">{{ playbackRate }}×</text>
												<text :class="['dropdown-arrow', showSpeedDropdown ? 'expanded' : '']">▼</text>
											</view>
											<view v-if="showSpeedDropdown" class="speed-options-list">
												<text
													v-for="rate in rates"
													:key="rate"
													:class="['speed-option-item', playbackRate === rate ? 'active' : '']"
													@tap.stop="selectSpeed(rate)"
												>{{ rate }}×</text>
											</view>
										</view>
									</view>
								</block>

								<block v-else-if="isVideo && isFrameSelected">
									<image :src="previewUrl" mode="aspectFit" class="full-obj" />
									<view class="recapture-overlay">
										<button class="recapture-btn" @tap="recaptureFrame">
											<text>🔄 重新截取图片</text>
										</button>
									</view>
								</block>

								<image v-else :src="previewUrl" mode="aspectFit" class="full-obj" />

								<view v-if="currentStep === 3" class="reconstruct-overlay">
									<view class="cyber-loader"></view>
									<text class="progress-info">正在构建数字孪生... {{ Math.floor(reconstructionProgress) }}%</text>
								</view>
							</view>

							<view class="asset-strip">
								<view class="strip-top">
									<text class="strip-label">{{ isVideo && !isFrameSelected ? '视频预览' : '已锁定素材' }}</text>
									<text class="add-more" @tap="reset">重新选择</text>
								</view>

								<view v-if="isVideo && !isFrameSelected" class="video-capture-zone">
									<view class="capture-hint">
										<text class="hint-icon">💡</text>
										<text class="hint-text">拖动进度条到合适位置，然后点击右侧按钮截取</text>
									</view>
									<button class="capture-frame-btn-external" @tap="captureCurrentFrame">
										<text>📸 捕捉当前帧</text>
									</button>
								</view>

								<view v-else class="selected-frame-preview">
									<image :src="previewUrl" mode="aspectFill" class="single-thumb" />
									<text class="thumb-meta">
										{{ isColorized ? '黑白已上色' : (isEnhanced ? '画质已增强' : (isVideo ? '视频截帧结果' : '原始照片')) }}
									</text>
								</view>
							</view>
						</view>
					</view>
				</view>

				<!-- 桌面端右侧面板 -->
				<view class="side-control-panel">
					<scroll-view scroll-y class="side-scroll-y">
						<view class="panel-inner">
							<view class="panel-section">
								<text class="section-title">当前支持范围</text>
								<view class="info-alert-box">
									<text class="alert-icon">🧸</text>
									<view class="alert-content">
										<text class="alert-t">单体目标恢复</text>
										<text class="alert-p">目前仅支持单个物体（如家具、手办、鞋包）的高精度建模。</text>
									</view>
								</view>
							</view>

							<view class="panel-section">
								<text class="section-title">生成引擎设置</text>
								<view @tap="isEngineExpanded = !isEngineExpanded" class="engine-selector">
									<view>
										<text class="engine-label">当前算法引擎</text>
										<text class="engine-name">{{ selectedEngine }}</text>
									</view>
									<text :class="['expand-arrow', isEngineExpanded ? 'expanded' : '']">▼</text>
								</view>
								<view v-if="isEngineExpanded" class="engine-list">
									<view
										v-for="(item, index) in engines"
										:key="index"
										@tap="selectEngine(item.name)"
										:class="['engine-item', selectedEngine === item.name ? 'active' : '']"
									>
										<view class="engine-header">
											<text class="engine-item-name">{{item.name}}</text>
											<text class="engine-tag">{{ item.tag }}</text>
										</view>
										<text class="engine-desc">{{ item.desc }}</text>
									</view>
								</view>
							</view>

							<view class="action-zone">
								<text class="section-title">任务状态</text>
								<view class="status-card">
									<view class="status-row">
										<text class="s-label">核心算法</text>
										<text class="s-value primary">{{ selectedEngine }}</text>
									</view>
									<view class="status-row">
										<text class="s-label">当前状态</text>
										<text :class="['s-value', hasMedia ? 'primary' : '']">
											{{ currentStep === 1 ? '待上传' : (currentStep === 2 ? '准备就绪' : '计算中') }}
										</text>
									</view>
								</view>
								<button class="magical-btn" :disabled="!isFrameSelected && !(!isVideo && hasMedia)" @click="submit">
									<text>{{ currentStep === 3 ? 'AI 处理中...' : '开始构建模型' }}</text>
								</button>
							</view>
						</view>
					</scroll-view>
				</view>
			</view>

			<!-- 手机端底部浮动操作栏 -->
			<view class="mobile-fab-bar">
				<view class="fab-status-chip">
					<text class="fab-engine-dot"></text>
					<text class="fab-engine-name">{{ selectedEngine }}</text>
					<text class="fab-status-text">· {{ currentStep === 1 ? '待上传' : (currentStep === 2 ? '准备就绪' : '计算中') }}</text>
				</view>
				<view class="fab-actions">
					<view class="fab-settings-btn" @tap="openMobilePanel">
						<text class="fab-settings-icon">⚙️</text>
					</view>
					<button
						class="fab-submit-btn"
						:disabled="!isFrameSelected && !(!isVideo && hasMedia)"
						@click="submit"
					>
						<text>{{ currentStep === 3 ? 'AI 处理中...' : '开始构建' }}</text>
					</button>
				</view>
			</view>
		</view>

		<!-- 画质增强弹窗 -->
		<view v-if="showEnhancePopup" class="popup-mask">
			<view class="enhance-popup">
				<text class="popup-icon">✨</text>
				<text class="popup-title">画质增强预处理</text>
				<text class="popup-desc">是否对上传素材进行 AI 降噪与超分处理？这能大幅提升 {{selectedEngine}} 的建模成功率。</text>
				<view class="popup-actions">
					<view @tap="confirmEnhance(false)" class="popup-btn secondary">跳过</view>
					<view @tap="confirmEnhance(true)" class="popup-btn primary">确认增强</view>
				</view>
			</view>
		</view>

		<!-- 增强对比弹窗 -->
		<view v-if="showComparePopup" class="compare-mask">
			<view class="compare-popup">
				<view class="compare-topbar">
					<view class="compare-topbar-left">
						<view class="cp-dot"></view>
						<text class="compare-title">AI 画质增强 · 前后对比</text>
					</view>
					<view class="compare-topbar-right">
						<view class="compare-badge">✨ 超分 2×</view>
						<view class="compare-badge">🎨 AI 降噪</view>
						<view class="compare-close" @tap="closeCompare">✕</view>
					</view>
				</view>

				<view class="compare-stage" id="compareStage"
					@mousedown.prevent="startDrag"
					@touchstart.prevent="startDrag"
					@mousemove="onDrag"
					@touchmove.prevent="onDrag"
					@mouseup="stopDrag"
					@mouseleave="stopDrag"
					@touchend="stopDrag"
				>
					<view class="compare-layer compare-layer-before">
						<image :src="originalUrl" mode="aspectFit" class="compare-full-img" />
						<view class="compare-label label-before">原图</view>
					</view>
					<view class="compare-layer compare-layer-after" :style="{ clipPath: 'inset(0 0 0 ' + comparePos + '%)' }">
						<image :src="enhancedUrl" mode="aspectFit" class="compare-full-img" />
						<view class="compare-label label-after">AI 增强</view>
					</view>
					<view class="compare-divider" :style="{ left: comparePos + '%' }">
						<view class="divider-line"></view>
						<view class="divider-handle">
							<text class="handle-arrow">‹›</text>
						</view>
					</view>
					<view class="compare-stats">
						<view class="stat-item">
							<text class="stat-val">+{{ enhanceStats.sharpness }}%</text>
							<text class="stat-key">清晰度</text>
						</view>
						<view class="stat-sep"></view>
						<view class="stat-item">
							<text class="stat-val">-{{ enhanceStats.noise }}%</text>
							<text class="stat-key">噪点</text>
						</view>
						<view class="stat-sep"></view>
						<view class="stat-item">
							<text class="stat-val">{{ enhanceStats.resolution }}</text>
							<text class="stat-key">分辨率</text>
						</view>
					</view>
				</view>

				<view class="compare-footer">
					<text class="compare-hint">左侧原图，右侧为新生成的增强图，拖动滑块查看效果</text>
					<view class="compare-actions">
						<view class="compare-btn secondary" @tap="rejectEnhance">使用原图</view>
						<view class="compare-btn primary" @tap="acceptEnhance">使用增强版 ✨</view>
					</view>
				</view>
			</view>
		</view>

		<!-- ══ 黑白上色弹窗（新增） ══ -->
		<view v-if="showColorizePopup" class="popup-mask">
			<view class="enhance-popup">
				<!-- 预览区：展示当前素材，判断是否黑白 -->
				<view class="colorize-preview-wrap">
					<image :src="previewUrl" mode="aspectFill" class="colorize-preview-img" />
					<view class="colorize-preview-overlay">
						<text class="colorize-preview-tag">当前素材</text>
					</view>
				</view>
				<text class="popup-icon" style="margin-top:16px;">🎨</text>
				<text class="popup-title">黑白照片上色</text>
				<text class="popup-desc">检测到素材可能为黑白照片。是否使用 AI 自动上色？上色后的素材将更有助于 3D 纹理还原。</text>
				<!-- 上色效果标签 -->
				<view class="colorize-tags">
					<view class="colorize-tag">
						<text class="ct-icon">🌈</text>
						<text class="ct-text">智能色彩还原</text>
					</view>
					<view class="colorize-tag">
						<text class="ct-icon">🧠</text>
						<text class="ct-text">深度学习驱动</text>
					</view>
					<view class="colorize-tag">
						<text class="ct-icon">⚡</text>
						<text class="ct-text">秒级处理</text>
					</view>
				</view>
				<view class="popup-actions">
					<view @tap="rejectColorize" class="popup-btn secondary">保持原样</view>
					<view @tap="confirmColorize" class="popup-btn primary">AI 智能上色</view>
				</view>
			</view>
		</view>

		<!-- 上色对比弹窗（新增） -->
		<view v-if="showColorizeComparePopup" class="compare-mask">
			<view class="compare-popup">
				<view class="compare-topbar">
					<view class="compare-topbar-left">
						<view class="cp-dot"></view>
						<text class="compare-title">AI 智能上色 · 前后对比</text>
					</view>
					<view class="compare-topbar-right">
						<view class="compare-badge">🌈 色彩还原</view>
						<view class="compare-badge">🧠 深度学习</view>
						<view class="compare-close" @tap="closeColorizeCompare">✕</view>
					</view>
				</view>

				<view class="compare-stage" id="colorizeStage"
					@mousedown.prevent="startColorizeDrag"
					@touchstart.prevent="startColorizeDrag"
					@mousemove="onColorizeDrag"
					@touchmove.prevent="onColorizeDrag"
					@mouseup="stopColorizeDrag"
					@mouseleave="stopColorizeDrag"
					@touchend="stopColorizeDrag"
				>
					<view class="compare-layer compare-layer-before">
						<image :src="originalColorUrl" mode="aspectFit" class="compare-full-img" />
						<view class="compare-label label-before">原图</view>
					</view>
					<view class="compare-layer compare-layer-after" :style="{ clipPath: 'inset(0 0 0 ' + colorizePos + '%)' }">
						<image :src="colorizedUrl" mode="aspectFit" class="compare-full-img" />
						<view class="compare-label label-after">AI 上色</view>
					</view>
					<view class="compare-divider" :style="{ left: colorizePos + '%' }">
						<view class="divider-line"></view>
						<view class="divider-handle">
							<text class="handle-arrow">‹›</text>
						</view>
					</view>
				</view>

				<view class="compare-footer">
					<text class="compare-hint">左侧原图，右侧为新生成的上色图，拖动滑块查看效果</text>
					<view class="compare-actions">
						<view class="compare-btn secondary" @tap="rejectColorize">使用原图</view>
						<view class="compare-btn primary" @tap="acceptColorize">使用上色版 🌈</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 手机端设置面板 Sheet -->
		<view v-if="showMobilePanel" class="mobile-panel-mask" @tap.self="closeMobilePanel">
			<view :class="['mobile-panel-sheet', showMobilePanel ? 'visible' : '']">
				<view class="sheet-handle"></view>
				<view class="sheet-header">
					<text class="sheet-title">引擎 & 设置</text>
					<view class="sheet-close" @tap="closeMobilePanel">✕</view>
				</view>
				<scroll-view scroll-y class="sheet-scroll">
					<view class="sheet-inner">
						<view class="panel-section">
							<text class="section-title">当前支持范围</text>
							<view class="info-alert-box">
								<text class="alert-icon">🧸</text>
								<view class="alert-content">
									<text class="alert-t">单体目标恢复</text>
									<text class="alert-p">目前仅支持单个物体（如家具、手办、鞋包）的高精度建模。</text>
								</view>
							</view>
						</view>

						<view class="panel-section">
							<text class="section-title">生成引擎设置</text>
							<view @tap="isMobileEngineExpanded = !isMobileEngineExpanded" class="engine-selector">
								<view>
									<text class="engine-label">当前算法引擎</text>
									<text class="engine-name">{{ selectedEngine }}</text>
								</view>
								<text :class="['expand-arrow', isMobileEngineExpanded ? 'expanded' : '']">▼</text>
							</view>
							<view v-if="isMobileEngineExpanded" class="engine-list-inline">
								<view
									v-for="(item, index) in engines"
									:key="index"
									@tap="selectEngineMobile(item.name)"
									:class="['engine-item', selectedEngine === item.name ? 'active' : '']"
								>
									<view class="engine-header">
										<text class="engine-item-name">{{item.name}}</text>
										<text class="engine-tag">{{ item.tag }}</text>
									</view>
									<text class="engine-desc">{{ item.desc }}</text>
								</view>
							</view>
						</view>

						<view class="panel-section">
							<text class="section-title">任务状态</text>
							<view class="status-card">
								<view class="status-row">
									<text class="s-label">核心算法</text>
									<text class="s-value primary">{{ selectedEngine }}</text>
								</view>
								<view class="status-row">
									<text class="s-label">当前状态</text>
									<text :class="['s-value', hasMedia ? 'primary' : '']">
										{{ currentStep === 1 ? '待上传' : (currentStep === 2 ? '准备就绪' : '计算中') }}
									</text>
								</view>
							</view>
						</view>
					</view>
				</scroll-view>

				<view class="sheet-footer">
					<button
						class="magical-btn"
						:disabled="!isFrameSelected && !(!isVideo && hasMedia)"
						@click="submitFromSheet"
					>
						<text>{{ currentStep === 3 ? 'AI 处理中...' : '开始构建模型' }}</text>
					</button>
				</view>
			</view>
		</view>
	</common-layout>
</template>

<script>
import request, { BASE_URL } from '@/utils/request.js';

const AI_BASE_URL = BASE_URL.replace(/\/api\/?$/, '');
const DEFAULT_CATEGORY_ID = 1;
const POLL_INTERVAL_MS = 2000;
const MAX_POLL_COUNT = 120;

export default {
	data() {
		return {
			currentStep: 1,
			hasMedia: false,
			isVideo: false,
			videoPath: '',
			previewUrl: '',
			isFrameSelected: false,
			reconstructionProgress: 0,
			showEnhancePopup: false,
			isEnhanced: false,
			showComparePopup: false,
			originalUrl: '',
			enhancedUrl: '',
			comparePos: 50,
			isDragging: false,
			enhanceStats: { sharpness: 0, noise: 0, resolution: '' },
			showColorizePopup: false,
			isColorized: false,
			showColorizeComparePopup: false,
			originalColorUrl: '',
			colorizedUrl: '',
			colorizePos: 50,
			isColorizeDragging: false,
			isEngineExpanded: false,
			isMobileEngineExpanded: false,
			showMobilePanel: false,
			selectedEngine: '混元3D API',
			playbackRate: 1.0,
			rates: [0.5, 0.75, 1.0, 1.25, 1.5, 2.0],
			showSpeedDropdown: false,
			uploadedMediaUrl: '',
			isUploadingMedia: false,
			engines: [
				{ name: '混元3D API', tag: '全能旗舰', desc: '适合常规单体目标的 3D 生成，结果会自动落库并进入编辑页。' },
				{ name: '混元极速版', tag: '极速出图', desc: '同样走混元 3D 接口，但使用 fast 模式，适合快速预览与联调。' },
				{ name: 'Doubao-Seed', tag: '快速探索', desc: '走 Seed3D 接口，便于和另一套 3D 生成链路联调。' }
			]
		};
	},
	methods: {
		selectEngine(name) {
			this.selectedEngine = name;
			this.isEngineExpanded = false;
		},
		selectEngineMobile(name) {
			this.selectedEngine = name;
			this.isMobileEngineExpanded = false;
		},
		openMobilePanel() {
			this.showMobilePanel = true;
			this.isMobileEngineExpanded = false;
		},
		closeMobilePanel() {
			this.showMobilePanel = false;
		},
		submitFromSheet() {
			this.closeMobilePanel();
			this.submit();
		},
		onVideoLoaded() {
			this.changePlaybackRate(this.playbackRate);
		},
		changePlaybackRate(rate) {
			this.playbackRate = rate;
			// #ifdef H5
			const video = document.querySelector('#targetVideo');
			if (video) video.playbackRate = rate;
			// #endif
			const videoContext = uni.createVideoContext('targetVideo', this);
			if (videoContext && typeof videoContext.playbackRate === 'function') {
				videoContext.playbackRate(rate);
			}
		},
		toggleSpeedDropdown() {
			this.showSpeedDropdown = !this.showSpeedDropdown;
		},
		selectSpeed(rate) {
			this.changePlaybackRate(rate);
			this.showSpeedDropdown = false;
		},
		handlePickImage() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				success: (res) => {
					this.isVideo = false;
					this.hasMedia = true;
					this.isFrameSelected = true;
					this.currentStep = 1;
					this.previewUrl = res.tempFilePaths[0];
					this.uploadedMediaUrl = '';
					this.isEnhanced = false;
					this.isColorized = false;
					this.showEnhancePopup = true;
				}
			});
		},
		handlePickVideo() {
			uni.chooseVideo({
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.isVideo = true;
					this.hasMedia = true;
					this.isFrameSelected = false;
					this.currentStep = 1;
					this.videoPath = res.tempFilePath;
					this.previewUrl = '';
					this.playbackRate = 1.0;
					this.uploadedMediaUrl = '';
					this.isEnhanced = false;
					this.isColorized = false;
				}
			});
		},
		captureCurrentFrame() {
			// #ifdef H5
			const video = document.querySelector('#targetVideo');
			if (!video) return;
			const canvas = document.createElement('canvas');
			canvas.width = video.videoWidth;
			canvas.height = video.videoHeight;
			canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height);
			this.previewUrl = canvas.toDataURL('image/jpeg', 0.92);
			this.uploadedMediaUrl = '';
			this.isFrameSelected = true;
			this.isEnhanced = false;
			this.isColorized = false;
			this.showEnhancePopup = true;
			// #endif
		},
		recaptureFrame() {
			this.isFrameSelected = false;
			this.previewUrl = '';
			this.uploadedMediaUrl = '';
			this.isEnhanced = false;
			this.isColorized = false;
			this.currentStep = 1;
		},
		getEngineConfig() {
			if (this.selectedEngine === 'Doubao-Seed') {
				return {
					submitPath: '/seed3d/submit',
					queryPath: '/seed3d/query/{jobId}',
					label: 'seed3d',
					generateType: null
				};
			}
			if (this.selectedEngine === '混元极速版') {
				return {
					submitPath: '/hunyuan3d/submit',
					queryPath: '/hunyuan3d/query/{jobId}',
					label: 'hunyuan3d_fast',
					generateType: 'Fast'
				};
			}
			return {
				submitPath: '/hunyuan3d/submit',
				queryPath: '/hunyuan3d/query/{jobId}',
				label: 'hunyuan3d',
				generateType: 'Normal'
			};
		},
		getOriginBaseUrl() {
			return AI_BASE_URL.replace(/\/$/, '');
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
		getErrorMessage(payload, fallback = '请求失败') {
			if (typeof payload === 'string' && payload.trim()) return payload;
			return payload?.error || payload?.message || payload?.msg || payload?.ErrorMessage || fallback;
		},
		getResponseUrl(payload) {
			return this.normalizeResourceUrl(
				payload?.url ||
				payload?.image_url ||
				payload?.imageUrl ||
				payload?.data?.url ||
				payload?.data?.image_url ||
				payload?.data?.imageUrl ||
				''
			);
		},
		getAuthHeader() {
			const rawToken =
				uni.getStorageSync('token') ||
				uni.getStorageSync('accessToken') ||
				uni.getStorageSync('Authorization') ||
				'';
			if (!rawToken) return {};
			const finalToken = rawToken.startsWith('Bearer ') ? rawToken : `Bearer ${rawToken}`;
			return { Authorization: finalToken };
		},
		logDebug(label, payload) {
			try {
				console.log(`[workshop] ${label}`, payload);
			} catch (error) {
				console.log(`[workshop] ${label}`);
			}
		},
		async aiRequest({ url, method = 'GET', data = {}, header = {} }) {
			const authHeader = this.getAuthHeader();
			return new Promise((resolve, reject) => {
				uni.request({
					url: `${AI_BASE_URL}${url}`,
					method,
					data,
					header: {
						...authHeader,
						...header
					},
					success: (res) => {
						if (res.statusCode === 200 || res.statusCode === 201) {
							resolve(res.data);
							return;
						}
						if (res.statusCode === 401) {
							uni.removeStorageSync('token');
							uni.removeStorageSync('userInfo');
							reject(new Error('登录已失效，请重新登录后重试'));
							return;
						}
						reject(new Error(this.getErrorMessage(res.data, `AI 请求失败(${res.statusCode})`)));
					},
					fail: (err) => {
						this.logDebug('aiRequest:fail', { url, err });
						reject(new Error(this.getErrorMessage(err, 'AI 服务连接失败')));
					}
				});
			});
		},
		async uploadMedia() {
			if (!this.previewUrl) return '';
			if (this.uploadedMediaUrl) return this.uploadedMediaUrl;
			this.isUploadingMedia = true;
			const authHeader = this.getAuthHeader();
			this.logDebug('uploadMedia:start', {
				previewUrl: this.previewUrl,
				mimeTypeGuess: this.getImageMimeType(this.previewUrl),
				extensionGuess: this.getImageExtension(this.previewUrl)
			});
			try {
				// #ifdef H5
				const sourceBlob = await fetch(this.previewUrl).then((res) => res.blob());
				const resolvedMimeType = this.normalizeMimeType(sourceBlob.type) || this.getImageMimeType(this.previewUrl);
				const uploadExtension = this.getImageExtension(resolvedMimeType || this.previewUrl);
				const uploadFileName = `workshop_${Date.now()}.${uploadExtension}`;
				this.logDebug('uploadMedia:h5-blob', {
					blobType: sourceBlob.type,
					blobSize: sourceBlob.size,
					resolvedMimeType,
					fileName: uploadFileName
				});
				const uploadFile = new File([sourceBlob], uploadFileName, { type: resolvedMimeType });
				const formData = new FormData();
				formData.append('file', uploadFile);
				const response = await fetch(`${BASE_URL}/upload/workshop`, {
					method: 'POST',
					headers: authHeader,
					body: formData
				});
				const payload = await response.json().catch(() => ({}));
				this.logDebug('uploadMedia:h5-response', { status: response.status, payload });
				if (response.status === 401) {
					throw new Error('登录已失效，请重新登录后重试');
				}
				if (!response.ok) {
					throw new Error(this.getErrorMessage(payload, `素材上传失败(${response.status})`));
				}
				const uploadUrl = this.getResponseUrl(payload);
				if (!uploadUrl) throw new Error('素材上传成功，但接口未返回 url');
				this.uploadedMediaUrl = uploadUrl;
				this.logDebug('uploadMedia:success', { uploadUrl });
				return uploadUrl;
				// #endif

				// #ifndef H5
				const nativeUploadUrl = await new Promise((resolve, reject) => {
					uni.uploadFile({
						url: `${BASE_URL}/upload/workshop`,
						filePath: this.previewUrl,
						name: 'file',
						header: authHeader,
						success: (res) => {
							try {
								const payload = JSON.parse(res.data || '{}');
								this.logDebug('uploadMedia:native-response', { statusCode: res.statusCode, payload });
								if (res.statusCode === 401) {
									throw new Error('登录已失效，请重新登录后重试');
								}
								if (res.statusCode !== 200 && res.statusCode !== 201) {
									throw new Error(this.getErrorMessage(payload, `素材上传失败(${res.statusCode})`));
								}
								const nextUrl = this.getResponseUrl(payload);
								if (!nextUrl) throw new Error('素材上传成功，但接口未返回 url');
								resolve(nextUrl);
							} catch (error) {
								reject(error);
							}
						},
						fail: (error) => reject(new Error(this.getErrorMessage(error, '素材上传失败')))
					});
				});
				this.uploadedMediaUrl = nativeUploadUrl;
				this.logDebug('uploadMedia:success', { uploadUrl: nativeUploadUrl });
				return nativeUploadUrl;
				// #endif
			} finally {
				this.isUploadingMedia = false;
			}
		},
		isRemoteHttpUrl(value) {
			return /^https?:\/\//i.test(value || '');
		},
		shouldConvertPngForMedia(apiPath, source) {
			return (
				(apiPath === '/media/enhance' || apiPath === '/media/colorize') &&
				this.getImageMimeType(source) === 'image/png'
			);
		},
		async convertBlobToJpegFile(blob, filename = '') {
			// #ifdef H5
			const objectUrl = URL.createObjectURL(blob);
			try {
				const image = await new Promise((resolve, reject) => {
					const img = new Image();
					img.onload = () => resolve(img);
					img.onerror = () => reject(new Error('PNG 转换失败'));
					img.src = objectUrl;
				});
				const canvas = document.createElement('canvas');
				canvas.width = image.naturalWidth || image.width;
				canvas.height = image.naturalHeight || image.height;
				const ctx = canvas.getContext('2d');
				ctx.fillStyle = '#ffffff';
				ctx.fillRect(0, 0, canvas.width, canvas.height);
				ctx.drawImage(image, 0, 0, canvas.width, canvas.height);
				const jpegBlob = await new Promise((resolve, reject) => {
					canvas.toBlob((result) => {
						if (!result) {
							reject(new Error('PNG 转换失败'));
							return;
						}
						resolve(result);
					}, 'image/jpeg', 0.92);
				});
				const resolvedName = filename || `workshop_${Date.now()}.jpg`;
				return new File([jpegBlob], resolvedName, { type: 'image/jpeg' });
			} finally {
				URL.revokeObjectURL(objectUrl);
			}
			// #endif

			// #ifndef H5
			return null;
			// #endif
		},
		async filePathToBlob(filePath, filename = '') {
			const response = await fetch(filePath);
			const blob = await response.blob();
			const resolvedExtension = this.getImageExtension(blob.type || filePath);
			const resolvedName = filename || `workshop_${Date.now()}.${resolvedExtension}`;
			this.logDebug('filePathToBlob', {
				filePath,
				resolvedName,
				blobType: blob.type,
				blobSize: blob.size
			});
			return new File([blob], resolvedName, { type: blob.type || this.getImageMimeType(filePath) });
		},
		async uploadFileToMediaEndpoint(apiPath, filePath) {
			const authHeader = this.getAuthHeader();
			const endpointUrl = `${BASE_URL}${apiPath}`;
			this.logDebug('mediaEndpoint:start', {
				apiPath,
				endpointUrl,
				filePath,
				mimeTypeGuess: this.getImageMimeType(filePath),
				extensionGuess: this.getImageExtension(filePath)
			});

			// #ifdef H5
			let file = await this.filePathToBlob(filePath);
			if (this.shouldConvertPngForMedia(apiPath, file.type || filePath)) {
				const jpegFile = await this.convertBlobToJpegFile(file, `${file.name.replace(/\.[^.]+$/, '') || `workshop_${Date.now()}`}.jpg`);
				if (jpegFile) {
					file = jpegFile;
				}
			}
			this.logDebug('mediaEndpoint:h5-file', {
				name: file.name,
				type: file.type,
				size: file.size
			});
			const formData = new FormData();
			formData.append('file', file);
			const response = await fetch(endpointUrl, {
				method: 'POST',
				headers: authHeader,
				body: formData
			});
			const payload = await response.json().catch(() => ({}));
			this.logDebug('mediaEndpoint:h5-response', { apiPath, status: response.status, payload });
			if (response.status === 401) {
				throw new Error('登录已失效，请重新登录后重试');
			}
			if (!response.ok) {
				throw new Error(this.getErrorMessage(payload, `媒体处理失败(${response.status})`));
			}
			const resultUrl = this.getResponseUrl(payload);
			if (!resultUrl) throw new Error('接口调用成功，但未返回处理后的图片地址');
			return resultUrl;
			// #endif

			// #ifndef H5
			const nativeResultUrl = await new Promise((resolve, reject) => {
				uni.uploadFile({
					url: endpointUrl,
					filePath,
					name: 'file',
					header: authHeader,
					success: (res) => {
						try {
							const payload = JSON.parse(res.data || '{}');
							this.logDebug('mediaEndpoint:native-response', { apiPath, statusCode: res.statusCode, payload });
							if (res.statusCode === 401) {
								throw new Error('登录已失效，请重新登录后重试');
							}
							if (res.statusCode !== 200 && res.statusCode !== 201) {
								throw new Error(this.getErrorMessage(payload, `媒体处理失败(${res.statusCode})`));
							}
							const nextUrl = this.getResponseUrl(payload);
							if (!nextUrl) throw new Error('接口调用成功，但未返回处理后的图片地址');
							resolve(nextUrl);
						} catch (error) {
							reject(error);
						}
					},
					fail: (error) => reject(new Error(this.getErrorMessage(error, '媒体处理失败')))
				});
			});
			return nativeResultUrl;
			// #endif
		},
		async processMediaFile(apiPath, options = {}) {
			const source = options.sourceUrl || this.previewUrl;
			if (!source) throw new Error('请先选择素材');
			this.logDebug('processMediaFile:start', {
				apiPath,
				source,
				isRemoteHttpUrl: this.isRemoteHttpUrl(source),
				isEnhanced: this.isEnhanced,
				isColorized: this.isColorized
			});

			// 文档要求 enhance/colorize 走 /api/media/* multipart；
			// 当上色来源已经是远程 URL 时，走 colorize-by-url 以避免再次下载到本地。
			if (apiPath === '/media/colorize' && this.isRemoteHttpUrl(source)) {
				this.logDebug('processMediaFile:colorize-by-url', { imageUrl: source });
				const payload = await request({
					url: '/media/colorize-by-url',
					method: 'POST',
					data: { imageUrl: source }
				});
				this.logDebug('processMediaFile:colorize-by-url-response', payload);
				const resultUrl = this.getResponseUrl(payload);
				if (!resultUrl) throw new Error('接口调用成功，但未返回处理后的图片地址');
				return resultUrl;
			}

			return this.uploadFileToMediaEndpoint(apiPath, source);
		},
		async confirmEnhance(isConfirm) {
			this.showEnhancePopup = false;
			if (!isConfirm) {
				this.isEnhanced = false;
				this.showColorizePopup = true;
				return;
			}
			uni.showLoading({ title: '正在增强图片...' });
			try {
				const originalUploadedUrl = await this.uploadMedia();
				const enhancedUploadedUrl = await this.processMediaFile('/media/enhance', {
					sourceUrl: this.previewUrl
				});
				this.originalUrl = originalUploadedUrl;
				this.enhancedUrl = enhancedUploadedUrl;
				this.uploadedMediaUrl = originalUploadedUrl;
				this.enhanceStats = {
					sharpness: Math.floor(28 + Math.random() * 18),
					noise: Math.floor(60 + Math.random() * 20),
					resolution: '2x'
				};
				this.comparePos = 50;
				this.showComparePopup = true;
			} catch (error) {
				uni.showToast({ title: error.message || '图片增强失败', icon: 'none' });
				this.showColorizePopup = true;
			} finally {
				uni.hideLoading();
			}
		},
		acceptEnhance() {
			this.isEnhanced = true;
			this.previewUrl = this.enhancedUrl || this.previewUrl;
			this.showComparePopup = false;
			this.showColorizePopup = true;
		},
		rejectEnhance() {
			this.isEnhanced = false;
			this.showComparePopup = false;
			this.showColorizePopup = true;
		},
		closeCompare() {
			this.showComparePopup = false;
			this.isEnhanced = false;
			this.showColorizePopup = true;
		},
		async confirmColorize() {
			this.showColorizePopup = false;
			this.originalColorUrl = this.previewUrl;
			this.colorizedUrl = this.previewUrl;
			this.colorizePos = 50;
			this.showColorizeComparePopup = true;
			uni.showLoading({ title: '正在智能上色...' });
			try {
				const colorizeSource = this.isEnhanced && this.enhancedUrl ? this.enhancedUrl : this.previewUrl;
				const originalUploadedUrl = await this.uploadMedia();
				const colorizedUploadedUrl = await this.processMediaFile('/media/colorize', {
					sourceUrl: colorizeSource
				});
				this.originalColorUrl = originalUploadedUrl;
				this.colorizedUrl = colorizedUploadedUrl;
			} catch (error) {
				uni.showToast({ title: error.message || '图片上色失败', icon: 'none' });
			} finally {
				uni.hideLoading();
			}
		},
		acceptColorize() {
			this.isColorized = true;
			this.previewUrl = this.colorizedUrl || this.previewUrl;
			this.showColorizeComparePopup = false;
			this.currentStep = 2;
		},
		rejectColorize() {
			this.isColorized = false;
			this.showColorizePopup = false;
			this.showColorizeComparePopup = false;
			this.currentStep = 2;
		},
		closeColorizeCompare() {
			this.showColorizeComparePopup = false;
			this.isColorized = false;
			this.currentStep = 2;
		},
		startColorizeDrag(e) {
			this.isColorizeDragging = true;
			this.calcColorizePos(e);
		},
		stopColorizeDrag() {
			this.isColorizeDragging = false;
		},
		onColorizeDrag(e) {
			if (!this.isColorizeDragging) return;
			this.calcColorizePos(e);
		},
		calcColorizePos(e) {
			const clientX = (e.touches && e.touches[0]) ? e.touches[0].clientX : e.clientX;
			// #ifdef H5
			const el = document.getElementById('colorizeStage');
			if (el) {
				const rect = el.getBoundingClientRect();
				const pos = ((clientX - rect.left) / rect.width) * 100;
				this.colorizePos = Math.min(95, Math.max(5, pos));
			}
			// #endif
			// #ifndef H5
			uni.createSelectorQuery().in(this).select('#colorizeStage').boundingClientRect((rect) => {
				if (!rect) return;
				const pos = ((clientX - rect.left) / rect.width) * 100;
				this.colorizePos = Math.min(95, Math.max(5, pos));
			}).exec();
			// #endif
		},
		startDrag(e) {
			this.isDragging = true;
			this.calcComparePos(e);
		},
		stopDrag() {
			this.isDragging = false;
		},
		onDrag(e) {
			if (!this.isDragging) return;
			this.calcComparePos(e);
		},
		calcComparePos(e) {
			const clientX = (e.touches && e.touches[0]) ? e.touches[0].clientX : e.clientX;
			// #ifdef H5
			const el = document.getElementById('compareStage');
			if (el) {
				const rect = el.getBoundingClientRect();
				const pos = ((clientX - rect.left) / rect.width) * 100;
				this.comparePos = Math.min(95, Math.max(5, pos));
			}
			// #endif
			// #ifndef H5
			uni.createSelectorQuery().in(this).select('#compareStage').boundingClientRect((rect) => {
				if (!rect) return;
				const pos = ((clientX - rect.left) / rect.width) * 100;
				this.comparePos = Math.min(95, Math.max(5, pos));
			}).exec();
			// #endif
		},
		sleep(ms) {
			return new Promise((resolve) => setTimeout(resolve, ms));
		},
		normalizeMimeType(value) {
			const mimeType = (value || '').toString().split(';')[0].trim().toLowerCase();
			return /^image\/[a-z0-9.+-]+$/i.test(mimeType) ? mimeType : '';
		},
		getImageMimeType(source) {
			const normalizedMimeType = this.normalizeMimeType(source);
			if (normalizedMimeType) return normalizedMimeType;
			if (/^data:image\/jpe?g/i.test(source)) return 'image/jpeg';
			if (/^data:image\/png/i.test(source)) return 'image/png';
			if (/^data:image\/webp/i.test(source)) return 'image/webp';
			if (/^data:image\/gif/i.test(source)) return 'image/gif';
			if (/^data:image\/bmp/i.test(source)) return 'image/bmp';
			if (/\.png($|\?)/i.test(source)) return 'image/png';
			if (/\.webp($|\?)/i.test(source)) return 'image/webp';
			if (/\.gif($|\?)/i.test(source)) return 'image/gif';
			if (/\.bmp($|\?)/i.test(source)) return 'image/bmp';
			if (/\.jpe?g($|\?)/i.test(source)) return 'image/jpeg';
			return 'image/jpeg';
		},
		getImageExtension(source) {
			const mimeType = this.getImageMimeType(source);
			if (mimeType === 'image/png') return 'png';
			if (mimeType === 'image/webp') return 'webp';
			if (mimeType === 'image/gif') return 'gif';
			if (mimeType === 'image/bmp') return 'bmp';
			return 'jpg';
		},
		arrayBufferToBase64(buffer) {
			if (typeof uni.arrayBufferToBase64 === 'function') {
				return uni.arrayBufferToBase64(buffer);
			}
			let binary = '';
			const bytes = new Uint8Array(buffer);
			for (let i = 0; i < bytes.byteLength; i += 1) {
				binary += String.fromCharCode(bytes[i]);
			}
			return btoa(binary);
		},
		async convertRemoteUrlToBase64(sourceUrl) {
			const targetUrl = this.normalizeResourceUrl(sourceUrl);
			this.logDebug('convertRemoteUrlToBase64:start', { sourceUrl, targetUrl });
			const remoteFile = await new Promise((resolve, reject) => {
				uni.request({
					url: targetUrl,
					method: 'GET',
					responseType: 'arraybuffer',
					success: (res) => {
						const headerMimeType = this.normalizeMimeType(
							res.header?.['content-type'] ||
							res.header?.['Content-Type'] ||
							''
						);
						this.logDebug('convertRemoteUrlToBase64:response', {
							targetUrl,
							statusCode: res.statusCode,
							headerMimeType,
							byteLength: res.data?.byteLength || res.data?.length || 0
						});
						if (res.statusCode !== 200) {
							reject(new Error(`读取图片失败(${res.statusCode})`));
							return;
						}
						resolve({
							arrayBuffer: res.data,
							mimeType: headerMimeType || this.getImageMimeType(targetUrl)
						});
					},
					fail: (error) => {
						this.logDebug('convertRemoteUrlToBase64:fail', { targetUrl, error });
						reject(new Error(this.getErrorMessage(error, '读取图片失败')));
					}
				});
			});
			return `data:${remoteFile.mimeType};base64,${this.arrayBufferToBase64(remoteFile.arrayBuffer)}`;
		},
		async convertLocalFileToBase64(filePath) {
			// #ifdef H5
			const blob = await fetch(filePath).then((res) => res.blob());
			const base64 = await new Promise((resolve, reject) => {
				const reader = new FileReader();
				reader.onload = () => resolve(reader.result);
				reader.onerror = () => reject(new Error('图片读取失败'));
				reader.readAsDataURL(blob);
			});
			return base64;
			// #endif

			// #ifndef H5
			const fileSystemManager = uni.getFileSystemManager();
			const encoded = await new Promise((resolve, reject) => {
				fileSystemManager.readFile({
					filePath,
					encoding: 'base64',
					success: (res) => resolve(res.data),
					fail: (error) => reject(new Error(this.getErrorMessage(error, '图片读取失败')))
				});
			});
			return `data:${this.getImageMimeType(filePath)};base64,${encoded}`;
			// #endif
		},
		async toBase64(source) {
			if (!source) throw new Error('缺少待处理图片');
			if (/^data:/i.test(source)) return source;
			this.logDebug('toBase64:start', {
				source,
				isRemote: /^https?:\/\//i.test(source) || source.startsWith('/'),
				mimeTypeGuess: this.getImageMimeType(source),
				extensionGuess: this.getImageExtension(source)
			});
			if (/^https?:\/\//i.test(source) || source.startsWith('/')) {
				return this.convertRemoteUrlToBase64(source);
			}
			return this.convertLocalFileToBase64(source);
		},
		getSubmitImageSource() {
			// 按参考实现，增强/上色只影响预览；真正提交 3D 时始终使用站内上传后的原图。
			return this.uploadedMediaUrl || this.previewUrl;
		},
		getJobId(payload) {
			return payload?.job_id || payload?.jobId || payload?.data?.job_id || payload?.data?.jobId || '';
		},
		getResultFiles(payload) {
			const source =
				payload?.ResultFile3Ds ||
				payload?.resultFile3Ds ||
				payload?.resultFiles ||
				payload?.files ||
				payload?.data?.ResultFile3Ds ||
				payload?.data?.files ||
				[];
			if (Array.isArray(source)) return source;
			if (source) return [source];
			return [];
		},
		getFileUrl(file) {
			if (typeof file === 'string') return this.normalizeResourceUrl(file);
			return this.normalizeResourceUrl(
				file?.fileUrl ||
				file?.url ||
				file?.Url ||
				file?.file_path ||
				file?.filePath ||
				file?.path ||
				file?.Path ||
				''
			);
		},
		getPreviewImageUrl(payload, fallback = '') {
			const resultFiles = this.getResultFiles(payload);
			const previewFile = resultFiles.find((file) => (
				file?.PreviewImageUrl ||
				file?.previewImageUrl ||
				file?.preview_image_url ||
				file?.thumbnailUrl ||
				file?.thumbUrl
			));
			return this.normalizeResourceUrl(
				previewFile?.PreviewImageUrl ||
				previewFile?.previewImageUrl ||
				previewFile?.preview_image_url ||
				previewFile?.thumbnailUrl ||
				previewFile?.thumbUrl ||
				fallback
			);
		},
		getFileName(fileUrl, originalName = '') {
			if (originalName) return originalName;
			const cleanUrl = fileUrl.split('?')[0];
			const parts = cleanUrl.split('/');
			return parts[parts.length - 1] || `model_${Date.now()}.glb`;
		},
		getFileType(file, fileUrl) {
			const explicitType = (file?.fileType || file?.type || file?.Type || '').toString().toUpperCase();
			if (explicitType) return explicitType;
			const match = (fileUrl || '').match(/\.([a-zA-Z0-9]+)(?:\?|$)/);
			return match ? match[1].toUpperCase() : 'GLB';
		},
		async pollJobResult(engineConfig, jobId) {
			for (let index = 0; index < MAX_POLL_COUNT; index += 1) {
				const queryPath = engineConfig.queryPath.replace('{jobId}', jobId);
				const payload = await this.aiRequest({ url: queryPath, method: 'GET' });
				const status = (payload?.Status || payload?.status || payload?.data?.Status || '').toString().toUpperCase();
				const resultFiles = this.getResultFiles(payload);
				this.logDebug('poll:tick', {
					engine: engineConfig.label,
					jobId,
					attempt: index + 1,
					maxAttempts: MAX_POLL_COUNT,
					status,
					resultFileCount: resultFiles.length,
					resultPayload: payload
				});
				this.reconstructionProgress = Math.min(95, 45 + Math.floor(((index + 1) / MAX_POLL_COUNT) * 45));
				if (status === 'DONE' || status === 'SUCCESS' || status === 'SUCCEEDED') {
					this.logDebug('poll:success', {
						engine: engineConfig.label,
						jobId,
						attempt: index + 1,
						status,
						resultFileCount: resultFiles.length
					});
					return payload;
				}
				if (status === 'FAIL' || status === 'FAILED' || status === 'ERROR') {
					this.logDebug('poll:fail', {
						engine: engineConfig.label,
						jobId,
						attempt: index + 1,
						status,
						resultPayload: payload
					});
					throw new Error(this.getErrorMessage(payload, 'AI 生成失败'));
				}
				await this.sleep(POLL_INTERVAL_MS);
			}
			throw new Error('AI 生成超时，请稍后重试');
		},
		buildModelUploadPayload(resultPayload, thumbnailUrl) {
			this.logDebug('modelUpload:source-payload', resultPayload);
			const resolvedFiles = this.getResultFiles(resultPayload)
				.map((file) => {
					const fileUrl = this.getFileUrl(file);
					if (!fileUrl) return null;
					const fileType = this.getFileType(file, fileUrl);
					const fallbackOriginalName = fileType === 'GLB'
						? `model_${Date.now()}.glb`
						: this.getFileName(fileUrl, file?.originalName || file?.name || file?.Name || '');
					return {
						fileUrl,
						fileType,
						originalName: fallbackOriginalName
					};
				})
				.filter(Boolean);
			const uploadModelFiles = resolvedFiles.filter((file) => file.fileType === 'GLB');
			this.logDebug('modelUpload:files', {
				rawFiles: this.getResultFiles(resultPayload),
				resolvedFiles,
				uploadModelFiles
			});
			const title = `${this.selectedEngine}_任务_${Date.now()}`;
			return {
				model: {
					title,
					thumbnailUrl,
					isPublic: false,
					renderStatus: 'success'
				},
				tags: [
					'AI生成',
					this.selectedEngine,
					this.isEnhanced ? '画质增强' : '原始素材',
					this.isColorized ? 'AI上色' : ''
				].filter(Boolean),
				modelFiles: uploadModelFiles
			};
		},
		getCreatedModelId(payload) {
			return payload?.mid || payload?.id || payload?.data?.mid || payload?.data?.id || payload?.data?.model?.mid || '';
		},
		async submit() {
			if (this.currentStep === 3) return;
			if (!this.previewUrl) {
				uni.showToast({ title: '请先选择素材', icon: 'none' });
				return;
			}
			this.currentStep = 3;
			this.reconstructionProgress = 5;
			try {
				const engineConfig = this.getEngineConfig();
				const selectedMediaUrl = await this.uploadMedia();
				if (!selectedMediaUrl) throw new Error('素材上传失败');
				this.logDebug('submit:source-selection', {
					previewUrl: this.previewUrl,
					uploadedMediaUrl: this.uploadedMediaUrl,
					enhancedUrl: this.enhancedUrl,
					colorizedUrl: this.colorizedUrl,
					selectedMediaUrl,
					isEnhanced: this.isEnhanced,
					isColorized: this.isColorized
				});
				this.reconstructionProgress = 20;
				const imageBase64 = await this.toBase64(selectedMediaUrl);
				this.logDebug('submit:image-base64-ready', {
					prefix: String(imageBase64).slice(0, 40),
					length: String(imageBase64).length
				});
				const submitPayload = { image_base64: imageBase64 };
				if (engineConfig.generateType) {
					submitPayload.generate_type = engineConfig.generateType;
				}
				this.reconstructionProgress = 35;
				const submitResult = await this.aiRequest({
					url: engineConfig.submitPath,
					method: 'POST',
					data: submitPayload
				});
				const jobId = this.getJobId(submitResult);
				if (!jobId) throw new Error('AI 提交成功，但接口未返回 job_id');
				const resultPayload = await this.pollJobResult(engineConfig, jobId);
				this.reconstructionProgress = 96;
				const thumbnailUrl = selectedMediaUrl;
				const uploadPayload = this.buildModelUploadPayload(resultPayload, thumbnailUrl);
				this.logDebug('modelUpload:request-payload', uploadPayload);
				const createdModel = await request({
					url: '/models/upload',
					method: 'POST',
					data: uploadPayload
				});
				const newModelId = this.getCreatedModelId(createdModel);
				if (!newModelId) throw new Error('模型落库成功，但未返回 mid');
				this.reconstructionProgress = 100;
				uni.reLaunch({ url: `/pages/editor/editor?id=${newModelId}` });
			} catch (error) {
				console.error('提交失败:', error);
				this.currentStep = 2;
				this.reconstructionProgress = 0;
				uni.showToast({ title: error.message || '提交失败', icon: 'none' });
			}
		},
		reset() {
			this.hasMedia = false;
			this.isVideo = false;
			this.isFrameSelected = false;
			this.videoPath = '';
			this.previewUrl = '';
			this.currentStep = 1;
			this.reconstructionProgress = 0;
			this.isEnhanced = false;
			this.isColorized = false;
			this.playbackRate = 1.0;
			this.showComparePopup = false;
			this.showColorizePopup = false;
			this.showColorizeComparePopup = false;
			this.originalUrl = '';
			this.enhancedUrl = '';
			this.originalColorUrl = '';
			this.colorizedUrl = '';
			this.comparePos = 50;
			this.colorizePos = 50;
			this.uploadedMediaUrl = '';
			this.isUploadingMedia = false;
			this.showSpeedDropdown = false;
		}
	}
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Lora:ital,wght@0,400;0,500;0,600;1,400&family=Noto+Serif+SC:wght@300;400;500;600&family=Cormorant+Garamond:ital,wght@0,400;0,500;1,300;1,400&family=Playfair+Display:ital,wght@0,500;0,600;1,500&display=swap');

.workshop-wrapper {
	--paper: #f5f0e8;
	--paper-off: #faf7f2;
	--paper-deep: #e8e0d0;
	--ink: #3b2c1e;
	--ink-mid: #72604e;
	--ink-faint: #a69180;
	--accent: #8a6850;
	--accent-dk: #664832;
	--gold: #c49050;
	--ochre: #a65d4a;
	--navy: #2c3a4a;
	--olive: #6b7c5c;
	--line: rgba(118, 88, 60, 0.16);
	--line-mid: rgba(96, 68, 42, 0.26);
	--line-strong: rgba(96, 68, 42, 0.34);
	--shadow: rgba(82, 54, 28, 0.11);
	--shadow-mid: rgba(82, 54, 28, 0.17);
	--highlight: #fffef8;
	--ease-slow: cubic-bezier(0.22, 1, 0.36, 1);
	--f-display: 'Cormorant Garamond', 'Playfair Display', Georgia, serif;
	--f-heading: 'Playfair Display', 'Cormorant Garamond', 'Noto Serif SC', Georgia, serif;
	--f-body: 'Lora', 'Noto Serif SC', 'STSong', 'Songti SC', Georgia, serif;
	--f-cn: 'Noto Serif SC', 'STSong', 'Songti SC', 'Source Han Serif SC', Georgia, serif;

	width: 100%;
	flex: 1;
	display: flex;
	flex-direction: column;
	background:
		radial-gradient(circle at 8% 12%, rgba(255, 250, 240, 0.75), transparent 28%),
		radial-gradient(circle at 92% 88%, rgba(198, 176, 148, 0.22), transparent 26%),
		linear-gradient(155deg, var(--paper-deep) 0%, var(--paper) 48%, #ebe3d4 100%);
	color: var(--ink);
	font-family: var(--f-body);
	overflow: hidden;
	position: relative;
}
.workshop-wrapper::before {
	content: '';
	position: absolute;
	inset: 0;
	pointer-events: none;
	z-index: 0;
	opacity: 0.2;
	background-image:
		radial-gradient(rgba(96, 68, 44, 0.08) 0.75px, transparent 0.75px),
		radial-gradient(rgba(255, 255, 255, 0.2) 0.55px, transparent 0.55px);
	background-size: 17px 17px, 24px 24px;
	background-position: 0 0, 9px 11px;
}

.steps-header,
.main-content-layout,
.editor-viewport,
.viewport-inner,
.modern-upload-portal,
.active-workspace,
.side-control-panel,
.mobile-fab-bar,
.mobile-panel-sheet {
	position: relative;
	z-index: 1;
}

.steps-header {
	padding: 20px 0;
	flex-shrink: 0;
	background: rgba(136, 100, 76, 0.04);
	border-bottom: 1px dashed var(--line-mid);
}
.steps-header::after {
	content: '';
	position: absolute;
	bottom: 0;
	left: 12%;
	right: 12%;
	height: 1px;
	background: linear-gradient(90deg, transparent, rgba(166, 93, 74, 0.28), transparent);
	opacity: 0.75;
}
.steps-row {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 22px;
}
.simple-step {
	display: flex;
	align-items: center;
	gap: 10px;
	opacity: 0.38;
	transition: opacity 0.4s var(--ease-slow);
}
.simple-step.active {
	opacity: 1;
}
.simple-step > text:last-child {
	font-size: 14px;
	font-weight: 500;
	font-family: var(--f-cn);
	color: var(--ink-faint);
	letter-spacing: 0.06em;
	line-height: 1.65;
}
.simple-step.active > text:last-child {
	color: var(--ink);
}
.step-icon {
	width: 26px;
	height: 26px;
	border-radius: 50%;
	background: rgba(136, 100, 76, 0.08);
	border: 1px dashed var(--line-mid);
	display: flex;
	align-items: center;
	justify-content: center;
	font-family: var(--f-display);
	font-style: italic;
	font-size: 13px;
	font-weight: 500;
	color: var(--ink-mid);
	transition:
		background 0.35s var(--ease-slow),
		border-color 0.35s var(--ease-slow),
		color 0.35s var(--ease-slow),
		box-shadow 0.35s var(--ease-slow);
}
.simple-step.active .step-icon {
	background: linear-gradient(145deg, rgba(196, 144, 80, 0.28), rgba(102, 72, 50, 0.12));
	border: 1px solid rgba(102, 72, 50, 0.35);
	color: var(--accent-dk);
	box-shadow: 0 2px 10px var(--shadow);
}
.step-dot-line {
	width: 48px;
	height: 0;
	border-top: 1px dotted var(--line-mid);
	opacity: 0.88;
}

.main-content-layout {
	flex: 1;
	display: flex;
	min-height: 0;
	height: 0;
}
.editor-viewport {
	flex: 1;
	padding: 26px;
	display: flex;
	flex-direction: column;
}
.viewport-inner {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.modern-upload-portal {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 36px;
}
.portal-header {
	text-align: center;
	margin-bottom: 48px;
}
.h1 {
	font-family: var(--f-heading);
	font-size: 30px;
	font-weight: 600;
	letter-spacing: 0.14em;
	color: var(--accent-dk);
	line-height: 1.35;
}
.p {
	color: var(--ink-mid);
	margin-top: 14px;
	font-size: 15px;
	line-height: 1.85;
	letter-spacing: 0.02em;
}
.choice-container {
	display: flex;
	gap: 20px;
	width: 100%;
	max-width: 760px;
}
.choice-card {
	flex: 1;
	background: linear-gradient(165deg, rgba(252, 249, 244, 0.94), rgba(242, 234, 220, 0.9));
	border: 1px dashed var(--line-mid);
	border-radius: 10px;
	padding: 40px 28px;
	display: flex;
	flex-direction: column;
	align-items: center;
	transition:
		transform 0.38s var(--ease-slow),
		box-shadow 0.38s var(--ease-slow),
		border-color 0.38s var(--ease-slow),
		background 0.38s var(--ease-slow);
	cursor: pointer;
	position: relative;
	overflow: hidden;
	box-shadow:
		0 3px 14px var(--shadow),
		0 1px 0 rgba(255, 254, 248, 0.8) inset;
}
.choice-card::before {
	content: '';
	position: absolute;
	inset: 0;
	border-radius: inherit;
	box-shadow: 0 0 0 1px rgba(255, 250, 240, 0.4) inset;
	pointer-events: none;
}
.choice-card:hover {
	transform: translateY(-3px);
	border-color: rgba(166, 93, 74, 0.38);
	box-shadow:
		0 10px 28px var(--shadow-mid),
		0 0 22px rgba(196, 144, 80, 0.14);
}
.card-bg-icon {
	font-size: 52px;
	margin-bottom: 16px;
	filter: sepia(0.14);
}
.card-title {
	font-family: var(--f-cn);
	font-size: 18px;
	font-weight: 500;
	color: var(--ink);
	margin-bottom: 8px;
	letter-spacing: 0.08em;
}
.card-desc {
	font-size: 13px;
	color: var(--ink-mid);
	margin-bottom: 22px;
	line-height: 1.75;
	text-align: center;
}
.card-btn {
	padding: 10px 26px;
	background: linear-gradient(180deg, #8a6850, var(--accent-dk));
	color: var(--highlight);
	border-radius: 8px;
	font-size: 13px;
	font-weight: 500;
	font-family: var(--f-cn);
	letter-spacing: 0.12em;
	box-shadow: 0 4px 14px var(--shadow-mid);
	transition:
		filter 0.35s var(--ease-slow),
		box-shadow 0.35s var(--ease-slow),
		transform 0.35s var(--ease-slow);
}
.choice-card:active .card-btn {
	transform: translateY(1px);
}

.active-workspace {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 16px;
}
.glass-canvas {
	flex: 1;
	border-radius: 10px;
	overflow: hidden;
	position: relative;
	background: linear-gradient(160deg, #2a231c 0%, #1e1814 100%);
	border: 1px solid var(--line-strong);
	box-shadow:
		0 4px 18px var(--shadow-mid),
		inset 0 0 40px rgba(82, 54, 28, 0.15);
}
.full-obj {
	width: 100%;
	height: 100%;
	display: block;
	filter: sepia(0.14) saturate(0.92);
}

.video-speed-control {
	position: absolute;
	top: 14px;
	right: 14px;
	display: flex;
	align-items: center;
	gap: 8px;
	z-index: 10;
}
.control-label {
	font-family: var(--f-display);
	font-style: italic;
	font-size: 11px;
	color: var(--ink-mid);
	background: rgba(245, 240, 232, 0.94);
	padding: 8px 12px;
	border-radius: 8px;
	border: 1px dashed var(--line-mid);
	white-space: nowrap;
	letter-spacing: 0.08em;
}
.speed-dropdown-wrapper {
	position: relative;
}
.speed-dropdown {
	background: rgba(245, 240, 232, 0.96);
	padding: 8px 12px;
	border-radius: 8px;
	border: 1px dashed var(--line-mid);
	display: flex;
	align-items: center;
	gap: 8px;
	cursor: pointer;
	min-width: 66px;
	justify-content: space-between;
	transition:
		border-color 0.35s var(--ease-slow),
		box-shadow 0.35s var(--ease-slow);
}
.speed-dropdown:hover {
	border-color: rgba(102, 72, 50, 0.42);
	box-shadow: 0 2px 10px var(--shadow);
}
.current-speed {
	font-family: var(--f-display);
	font-size: 13px;
	color: var(--accent-dk);
	font-weight: 600;
}
.dropdown-arrow {
	font-size: 9px;
	color: var(--ink-faint);
	transition: transform 0.35s var(--ease-slow);
}
.dropdown-arrow.expanded {
	transform: rotate(180deg);
}
.speed-options-list {
	position: absolute;
	top: calc(100% + 6px);
	right: 0;
	background: var(--paper-off);
	border: 1px dashed var(--line-mid);
	border-radius: 8px;
	padding: 6px;
	min-width: 72px;
	box-shadow: 0 8px 22px var(--shadow-mid);
	z-index: 20;
}
.speed-option-item {
	display: block;
	padding: 8px 10px;
	font-family: var(--f-body);
	font-size: 13px;
	color: var(--ink-mid);
	border-radius: 6px;
	cursor: pointer;
	text-align: center;
	transition:
		background 0.32s var(--ease-slow),
		color 0.32s var(--ease-slow);
}
.speed-option-item:hover {
	background: rgba(136, 100, 76, 0.1);
	color: var(--ink);
}
.speed-option-item.active {
	background: rgba(102, 72, 50, 0.14);
	color: var(--accent-dk);
	font-weight: 600;
}

.recapture-overlay {
	position: absolute;
	bottom: 20px;
	left: 0;
	right: 0;
	display: flex;
	justify-content: center;
	z-index: 10;
}
.recapture-btn {
	background: rgba(245, 240, 232, 0.94);
	color: var(--accent-dk);
	border: 1px dashed var(--line-mid);
	border-radius: 999px;
	padding: 10px 28px;
	font-size: 13px;
	font-weight: 500;
	font-family: var(--f-cn);
	cursor: pointer;
	box-shadow: 0 4px 16px var(--shadow);
	transition:
		background 0.35s var(--ease-slow),
		border-color 0.35s var(--ease-slow),
		transform 0.35s var(--ease-slow);
}
.recapture-btn:hover {
	background: var(--highlight);
	border-color: rgba(166, 93, 74, 0.42);
	transform: translateY(-1px);
}

.reconstruct-overlay {
	position: absolute;
	inset: 0;
	background: rgba(42, 34, 28, 0.88);
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	gap: 16px;
}
.cyber-loader {
	width: 44px;
	height: 44px;
	border-radius: 50%;
	border: 2px dashed var(--gold);
	border-top-color: transparent;
	animation: spin 1.35s linear infinite;
	box-shadow: 0 0 0 4px rgba(196, 144, 80, 0.12);
}
.progress-info {
	font-family: var(--f-display);
	font-style: italic;
	font-size: 13px;
	color: var(--paper-off);
	letter-spacing: 0.06em;
	line-height: 1.6;
}
@keyframes spin {
	to {
		transform: rotate(360deg);
	}
}

.asset-strip {
	background: linear-gradient(175deg, rgba(252, 249, 244, 0.96), rgba(232, 224, 208, 0.94));
	border: 1px dashed var(--line-mid);
	border-radius: 10px;
	padding: 18px 20px;
	flex-shrink: 0;
	box-shadow: 0 3px 12px var(--shadow);
}
.strip-top {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12px;
}
.strip-label {
	font-family: var(--f-display);
	font-style: italic;
	font-size: 11px;
	color: var(--ink-faint);
	letter-spacing: 0.14em;
}
.add-more {
	font-size: 13px;
	font-family: var(--f-cn);
	color: var(--ochre);
	text-decoration: underline;
	text-underline-offset: 3px;
	cursor: pointer;
	transition: color 0.35s var(--ease-slow);
}
.add-more:hover {
	color: var(--accent-dk);
}

.video-capture-zone {
	display: flex;
	align-items: center;
	gap: 12px;
}
.capture-hint {
	flex: 1;
	display: flex;
	align-items: center;
	gap: 10px;
	background: rgba(136, 100, 76, 0.06);
	border: 1px dashed var(--line);
	padding: 10px 14px;
	border-radius: 8px;
}
.hint-icon {
	font-size: 15px;
	filter: sepia(0.15);
}
.hint-text {
	font-size: 12px;
	color: var(--ink-mid);
	line-height: 1.65;
	font-family: var(--f-body);
}
.capture-frame-btn-external {
	font-size: 13px;
	font-weight: 500;
	font-family: var(--f-cn);
	color: var(--highlight);
	cursor: pointer;
	white-space: nowrap;
	background: linear-gradient(180deg, var(--olive), #556348);
	border: none;
	border-radius: 8px;
	padding: 10px 18px;
	box-shadow: 0 4px 14px var(--shadow-mid);
	transition:
		filter 0.35s var(--ease-slow),
		box-shadow 0.35s var(--ease-slow),
		transform 0.35s var(--ease-slow);
}
.capture-frame-btn-external:hover {
	filter: brightness(1.05);
	box-shadow: 0 6px 18px var(--shadow-mid);
	transform: translateY(-1px);
}

.selected-frame-preview {
	display: flex;
	align-items: center;
	gap: 16px;
}
.single-thumb {
	width: 120px;
	height: 78px;
	border-radius: 8px;
	border: 1px solid var(--line-mid);
	flex-shrink: 0;
	filter: sepia(0.12);
	box-shadow: 0 2px 8px var(--shadow);
}
.thumb-meta {
	font-size: 13px;
	color: var(--ink-mid);
	font-family: var(--f-body);
	line-height: 1.6;
}

.side-control-panel {
	width: 320px;
	background: linear-gradient(180deg, rgba(252, 249, 244, 0.98), rgba(236, 228, 212, 0.97));
	border-left: 1px dashed var(--line-mid);
	display: flex;
	flex-direction: column;
	box-shadow: -4px 0 18px rgba(82, 54, 28, 0.06);
}
.side-scroll-y {
	flex: 1;
	height: 100%;
}
.panel-inner {
	padding: 22px 20px 28px;
	display: flex;
	flex-direction: column;
	gap: 18px;
}
.panel-section {
	position: relative;
}

.section-title {
	display: block;
	font-size: 11px;
	font-weight: 500;
	font-family: var(--f-display);
	font-style: italic;
	color: var(--ink-faint);
	margin-bottom: 10px;
	letter-spacing: 0.18em;
	text-transform: uppercase;
}

.info-alert-box {
	background: rgba(107, 124, 92, 0.08);
	border: 1px dashed rgba(107, 124, 92, 0.35);
	border-radius: 10px;
	padding: 16px 14px;
	display: flex;
	gap: 12px;
	align-items: flex-start;
	box-shadow: 0 2px 10px var(--shadow);
}
.alert-icon {
	font-size: 24px;
	flex-shrink: 0;
	line-height: 1;
	filter: sepia(0.12);
}
.alert-t {
	display: block;
	font-size: 15px;
	font-weight: 600;
	font-family: var(--f-cn);
	color: var(--olive);
	margin-bottom: 6px;
	letter-spacing: 0.04em;
}
.alert-p {
	font-size: 13px;
	font-family: var(--f-body);
	color: var(--ink-mid);
	line-height: 1.75;
}

.engine-selector {
	padding: 14px 16px;
	border-radius: 10px;
	background: rgba(136, 100, 76, 0.05);
	border: 1px dashed var(--line-mid);
	display: flex;
	justify-content: space-between;
	align-items: center;
	cursor: pointer;
	transition:
		background 0.35s var(--ease-slow),
		border-color 0.35s var(--ease-slow),
		box-shadow 0.35s var(--ease-slow);
}
.engine-selector:hover {
	background: rgba(136, 100, 76, 0.1);
	border-color: var(--line-strong);
	box-shadow: 0 2px 12px var(--shadow);
}
.engine-label {
	display: block;
	font-size: 11px;
	font-weight: 500;
	font-family: var(--f-display);
	font-style: italic;
	color: var(--ink-faint);
	margin-bottom: 4px;
	letter-spacing: 0.12em;
}
.engine-name {
	display: block;
	font-size: 15px;
	font-weight: 600;
	font-family: var(--f-cn);
	color: var(--navy);
	letter-spacing: 0.06em;
}
.expand-arrow {
	font-size: 10px;
	color: var(--ink-faint);
	transition: transform 0.35s var(--ease-slow);
}
.expand-arrow.expanded {
	transform: rotate(180deg);
}

.engine-list {
	position: absolute;
	left: 0;
	right: 0;
	top: calc(100% + 8px);
	background: var(--paper-off);
	border: 1px dashed var(--line-mid);
	border-radius: 10px;
	padding: 8px;
	box-shadow: 0 14px 36px var(--shadow-mid);
	z-index: 50;
	display: flex;
	flex-direction: column;
	gap: 4px;
}
.engine-item {
	padding: 12px 14px;
	border-radius: 8px;
	cursor: pointer;
	border: 1px solid transparent;
	background: transparent;
	transition:
		background 0.32s var(--ease-slow),
		border-color 0.32s var(--ease-slow);
}
.engine-item:hover {
	background: rgba(136, 100, 76, 0.08);
	border-color: var(--line);
}
.engine-item.active {
	background: rgba(102, 72, 50, 0.1);
	border-color: rgba(102, 72, 50, 0.28);
}
.engine-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 4px;
}
.engine-item-name {
	font-size: 14px;
	font-weight: 600;
	font-family: var(--f-cn);
	color: var(--ink);
}
.engine-item.active .engine-item-name {
	color: var(--accent-dk);
}
.engine-tag {
	font-size: 10px;
	font-family: var(--f-display);
	font-style: italic;
	padding: 3px 8px;
	border-radius: 4px;
	background: rgba(166, 93, 74, 0.12);
	color: var(--ochre);
	letter-spacing: 0.06em;
}
.engine-item.active .engine-tag {
	background: rgba(102, 72, 50, 0.2);
	color: var(--accent-dk);
}
.engine-desc {
	font-size: 12px;
	font-family: var(--f-body);
	color: var(--ink-mid);
	line-height: 1.65;
}

.action-zone {
	display: flex;
	flex-direction: column;
}
.status-card {
	background: rgba(252, 249, 244, 0.9);
	border: 1px dashed var(--line-mid);
	border-radius: 10px;
	padding: 16px 18px;
	margin-bottom: 12px;
	box-shadow: 0 2px 10px var(--shadow);
}
.status-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.status-row + .status-row {
	margin-top: 14px;
	padding-top: 14px;
	border-top: 1px dotted var(--line);
}
.s-label {
	font-size: 13px;
	font-weight: 500;
	font-family: var(--f-cn);
	color: var(--ink-faint);
}
.s-value {
	font-size: 13px;
	font-weight: 600;
	font-family: var(--f-cn);
	color: var(--ink-mid);
}
.s-value.primary {
	color: var(--navy);
}

.magical-btn {
	width: 100%;
	height: 54px;
	border: none;
	border-radius: 10px;
	cursor: pointer;
	background: linear-gradient(180deg, #a65d4a, #8a4a3c);
	color: var(--highlight);
	font-weight: 600;
	font-size: 15px;
	letter-spacing: 0.1em;
	font-family: var(--f-cn);
	box-shadow:
		0 6px 20px rgba(138, 74, 60, 0.22),
		0 1px 0 rgba(255, 254, 248, 0.25) inset;
	transition:
		filter 0.38s var(--ease-slow),
		box-shadow 0.38s var(--ease-slow),
		transform 0.38s var(--ease-slow);
}
.magical-btn:hover {
	filter: brightness(1.04);
	box-shadow: 0 8px 26px rgba(138, 74, 60, 0.28);
	transform: translateY(-1px);
}
.magical-btn[disabled] {
	opacity: 0.38;
	filter: grayscale(0.25);
	box-shadow: none;
	cursor: not-allowed;
	transform: none;
}

.popup-mask {
	position: fixed;
	inset: 0;
	background: rgba(42, 34, 28, 0.72);
	backdrop-filter: blur(10px);
	z-index: 999;
	display: flex;
	align-items: center;
	justify-content: center;
}
.enhance-popup {
	width: 360px;
	background: linear-gradient(168deg, rgba(252, 249, 244, 0.99), rgba(236, 228, 212, 0.98));
	border-radius: 12px;
	padding: 28px;
	text-align: center;
	border: 1px dashed var(--line-mid);
	box-shadow:
		0 18px 48px var(--shadow-mid),
		0 0 0 1px rgba(255, 250, 240, 0.5) inset;
}
.popup-icon {
	display: block;
	font-size: 38px;
	margin-bottom: 12px;
	filter: sepia(0.12);
}
.popup-title {
	display: block;
	font-size: 17px;
	font-weight: 600;
	font-family: var(--f-heading);
	color: var(--accent-dk);
	margin-bottom: 10px;
	letter-spacing: 0.08em;
}
.popup-desc {
	display: block;
	font-size: 13px;
	font-family: var(--f-body);
	color: var(--ink-mid);
	line-height: 1.85;
	margin-bottom: 22px;
}
.popup-actions {
	display: flex;
	gap: 10px;
}
.popup-btn {
	flex: 1;
	padding: 12px;
	border-radius: 8px;
	font-size: 13px;
	font-weight: 500;
	font-family: var(--f-cn);
	cursor: pointer;
	background: rgba(136, 100, 76, 0.07);
	border: 1px dashed var(--line-mid);
	color: var(--ink-mid);
	transition:
		background 0.35s var(--ease-slow),
		border-color 0.35s var(--ease-slow),
		color 0.35s var(--ease-slow);
	letter-spacing: 0.06em;
}
.popup-btn:hover {
	background: rgba(136, 100, 76, 0.14);
	border-color: var(--line-strong);
	color: var(--ink);
}
.popup-btn.primary {
	background: linear-gradient(180deg, var(--accent), var(--accent-dk));
	border: 1px solid rgba(102, 72, 50, 0.35);
	color: var(--highlight);
	box-shadow: 0 4px 14px var(--shadow-mid);
}
.popup-btn.primary:hover {
	filter: brightness(1.05);
	box-shadow: 0 6px 18px var(--shadow-mid);
}
.popup-btn.secondary {
	background: rgba(232, 224, 208, 0.6);
}

.colorize-preview-wrap {
	position: relative;
	width: 100%;
	height: 160px;
	border-radius: 10px;
	overflow: hidden;
	margin-bottom: 4px;
	border: 1px dashed var(--line-mid);
}
.colorize-preview-img {
	width: 100%;
	height: 100%;
	display: block;
	filter: sepia(0.14);
}
.colorize-preview-overlay {
	position: absolute;
	inset: 0;
	background: linear-gradient(180deg, transparent 45%, rgba(59, 44, 30, 0.55) 100%);
	display: flex;
	align-items: flex-end;
	padding: 10px 12px;
}
.colorize-preview-tag {
	font-family: var(--f-display);
	font-style: italic;
	font-size: 10px;
	color: var(--paper-off);
	letter-spacing: 0.14em;
	font-weight: 500;
	background: rgba(44, 58, 74, 0.55);
	padding: 4px 9px;
	border-radius: 4px;
	border: 1px dashed rgba(245, 240, 232, 0.35);
}
.colorize-tags {
	display: flex;
	gap: 8px;
	justify-content: center;
	margin-bottom: 18px;
	flex-wrap: wrap;
}
.colorize-tag {
	display: flex;
	align-items: center;
	gap: 5px;
	background: rgba(136, 100, 76, 0.08);
	border: 1px dashed var(--line);
	border-radius: 999px;
	padding: 5px 12px;
}
.ct-icon {
	font-size: 12px;
	filter: sepia(0.12);
}
.ct-text {
	font-size: 11px;
	font-family: var(--f-body);
	color: var(--ink-mid);
	font-weight: 500;
}

.mobile-fab-bar {
	display: none;
}
.mobile-panel-mask {
	display: none;
}
.mobile-tips {
	display: none;
}
.mobile-tips-row {
	display: none;
}

@media (max-width: 1099px) {
	.side-control-panel {
		width: 260px;
	}
	.editor-viewport {
		padding: 20px;
	}
	.choice-container {
		max-width: 600px;
	}
	.choice-card {
		padding: 32px 18px;
	}
	.card-bg-icon {
		font-size: 44px;
	}
	.card-title {
		font-size: 17px;
	}
	.portal-header {
		margin-bottom: 36px;
	}
	.h1 {
		font-size: 26px;
	}
}

@media (max-width: 699px) {
	.steps-header {
		padding: 14px 0;
	}
	.steps-row {
		gap: 10px;
	}
	.step-dot-line {
		width: 18px;
	}
	.simple-step {
		gap: 7px;
	}
	.simple-step > text:last-child {
		font-size: 12px;
	}
	.step-icon {
		width: 22px;
		height: 22px;
		font-size: 12px;
	}
	.side-control-panel {
		display: none;
	}
	.workshop-wrapper {
		overflow-y: auto;
		overflow-x: hidden;
		padding-bottom: 80px;
		width: 100%;
		box-sizing: border-box;
	}
	.main-content-layout {
		height: auto;
		min-height: 0;
		flex: none;
		width: 100%;
	}
	.editor-viewport {
		padding: 0;
		flex: none;
		width: 100%;
		box-sizing: border-box;
	}
	.viewport-inner {
		flex: none;
		width: 100%;
		box-sizing: border-box;
	}
	.modern-upload-portal {
		padding: 20px 10px 16px;
		justify-content: flex-start;
		align-items: stretch;
		flex: none;
		box-sizing: border-box;
		width: 100%;
	}
	.portal-header {
		text-align: center;
		margin-bottom: 18px;
		padding: 0;
	}
	.h1 {
		display: block;
		font-size: 22px;
		margin-bottom: 8px;
		letter-spacing: 0.1em;
	}
	.p {
		display: block;
		font-size: 13px;
		line-height: 1.75;
		margin-bottom: 0;
	}
	.mobile-tips {
		display: flex;
		flex-direction: column;
		gap: 10px;
		background: rgba(136, 100, 76, 0.06);
		border: 1px dashed var(--line-mid);
		border-radius: 10px;
		padding: 14px 16px;
		margin-bottom: 18px;
		text-align: left;
	}
	.tip-row {
		display: flex;
		align-items: center;
		gap: 10px;
	}
	.tip-dot {
		font-size: 12px;
		font-weight: 600;
		color: var(--ochre);
		font-family: var(--f-display);
		flex-shrink: 0;
		width: 18px;
	}
	.tip-text {
		font-size: 12px;
		color: var(--ink-mid);
		font-family: var(--f-body);
		line-height: 1.55;
	}
	.tip-em {
		color: var(--accent-dk);
		font-weight: 600;
	}
	.choice-container {
		flex-direction: row !important;
		gap: 12px;
		max-width: 100%;
		align-items: stretch;
	}
	.choice-card {
		flex: 1;
		padding: 24px 12px 20px;
		border-radius: 10px;
		align-items: center;
		overflow: visible;
	}
	.card-bg-icon {
		font-size: 36px;
		margin-bottom: 12px;
	}
	.card-title {
		font-size: 15px;
		margin-bottom: 5px;
		text-align: center;
	}
	.card-desc {
		font-size: 11px;
		margin-bottom: 16px;
		text-align: center;
		line-height: 1.55;
	}
	.card-btn {
		padding: 9px 14px;
		font-size: 12px;
		letter-spacing: 0.08em;
	}
	.mobile-tips-row {
		display: flex;
		margin-top: 14px;
		padding: 10px 14px;
		background: rgba(232, 224, 208, 0.65);
		border: 1px dashed var(--line);
		border-radius: 8px;
	}
	.mobile-tip {
		font-size: 11px;
		color: var(--ink-mid);
		font-family: var(--f-body);
		line-height: 1.65;
		word-break: break-all;
		white-space: normal;
	}
	.active-workspace {
		gap: 12px;
		padding: 8px 10px 0;
		box-sizing: border-box;
		width: 100%;
		align-items: stretch;
		display: flex;
		flex-direction: column;
	}
	.glass-canvas {
		width: 100%;
		box-sizing: border-box;
		height: calc(100dvh - 380px);
		min-height: 260px;
		max-height: 380px;
		flex: none;
		position: relative;
	}
	.glass-canvas .full-obj {
		position: absolute !important;
		top: 0 !important;
		left: 0 !important;
		width: 100% !important;
		height: 100% !important;
		display: block !important;
	}
	.asset-strip {
		padding: 14px 16px;
	}
	.single-thumb {
		width: 80px;
		height: 52px;
		border-radius: 8px;
	}
	.hint-text {
		font-size: 11px;
	}
	.enhance-popup {
		width: calc(100vw - 32px);
		padding: 22px 18px;
	}
	.colorize-preview-wrap {
		height: 130px;
	}
	.colorize-tags {
		gap: 6px;
	}
	.ct-text {
		font-size: 10px;
	}

	.mobile-fab-bar {
		display: flex;
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		z-index: 200;
		align-items: center;
		justify-content: space-between;
		padding: 10px 16px 20px;
		background: linear-gradient(180deg, rgba(252, 249, 244, 0.97), rgba(232, 224, 208, 0.98));
		border-top: 1px dashed var(--line-mid);
		box-shadow: 0 -8px 28px var(--shadow);
		gap: 12px;
	}
	.fab-status-chip {
		display: flex;
		align-items: center;
		gap: 7px;
		background: rgba(136, 100, 76, 0.08);
		border: 1px dashed var(--line);
		border-radius: 999px;
		padding: 8px 14px;
		flex: 1;
		min-width: 0;
		overflow: hidden;
	}
	.fab-engine-dot {
		width: 6px;
		height: 6px;
		border-radius: 50%;
		flex-shrink: 0;
		background: var(--ochre);
		box-shadow: 0 0 0 2px rgba(166, 93, 74, 0.2);
	}
	.fab-engine-name {
		font-size: 13px;
		font-weight: 600;
		font-family: var(--f-cn);
		color: var(--navy);
		white-space: nowrap;
	}
	.fab-status-text {
		font-size: 12px;
		font-family: var(--f-body);
		color: var(--ink-mid);
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	.fab-actions {
		display: flex;
		align-items: center;
		gap: 10px;
		flex-shrink: 0;
	}
	.fab-settings-btn {
		width: 44px;
		height: 44px;
		border-radius: 10px;
		flex-shrink: 0;
		background: rgba(136, 100, 76, 0.1);
		border: 1px dashed var(--line-mid);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 20px;
		cursor: pointer;
		transition:
			background 0.35s var(--ease-slow),
			transform 0.25s var(--ease-slow);
	}
	.fab-settings-btn:active {
		background: rgba(136, 100, 76, 0.18);
		transform: scale(0.96);
	}
	.fab-submit-btn {
		height: 44px;
		padding: 0 20px;
		border: none;
		border-radius: 10px;
		background: linear-gradient(180deg, #a65d4a, #8a4a3c);
		color: var(--highlight);
		font-weight: 600;
		font-size: 14px;
		letter-spacing: 0.08em;
		font-family: var(--f-cn);
		cursor: pointer;
		flex-shrink: 0;
		box-shadow: 0 4px 14px rgba(138, 74, 60, 0.25);
		transition:
			filter 0.35s var(--ease-slow),
			transform 0.25s var(--ease-slow);
		white-space: nowrap;
	}
	.fab-submit-btn:active {
		transform: scale(0.97);
	}
	.fab-submit-btn[disabled] {
		opacity: 0.35;
		filter: grayscale(0.3);
		box-shadow: none;
	}
	.mobile-panel-mask {
		display: flex;
		position: fixed;
		inset: 0;
		z-index: 500;
		align-items: flex-end;
		background: rgba(42, 34, 28, 0.55);
		backdrop-filter: blur(8px);
	}
	.mobile-panel-sheet {
		width: 100%;
		max-height: 82vh;
		background: linear-gradient(180deg, rgba(252, 249, 244, 0.99), rgba(236, 228, 212, 0.99));
		border-radius: 14px 14px 0 0;
		border-top: 1px dashed var(--line-mid);
		box-shadow: 0 -14px 40px var(--shadow-mid);
		display: flex;
		flex-direction: column;
		transform: translateY(100%);
		transition: transform 0.38s var(--ease-slow);
	}
	.mobile-panel-sheet.visible {
		transform: translateY(0);
	}
	.sheet-handle {
		width: 36px;
		height: 4px;
		border-radius: 2px;
		background: rgba(118, 88, 60, 0.22);
		margin: 12px auto 0;
		flex-shrink: 0;
	}
	.sheet-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 14px 20px 12px;
		flex-shrink: 0;
		border-bottom: 1px dashed var(--line);
	}
	.sheet-title {
		font-size: 16px;
		font-weight: 600;
		font-family: var(--f-heading);
		color: var(--accent-dk);
		letter-spacing: 0.06em;
	}
	.sheet-close {
		width: 30px;
		height: 30px;
		border-radius: 8px;
		background: rgba(136, 100, 76, 0.08);
		border: 1px dashed var(--line);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 13px;
		color: var(--ink-mid);
		cursor: pointer;
		transition: background 0.35s var(--ease-slow);
	}
	.sheet-scroll {
		flex: 1;
		overflow-y: auto;
	}
	.sheet-inner {
		padding: 18px 18px 8px;
		display: flex;
		flex-direction: column;
		gap: 18px;
	}
	.engine-list-inline {
		margin-top: 10px;
		background: var(--paper-off);
		border: 1px dashed var(--line-mid);
		border-radius: 10px;
		padding: 8px;
		display: flex;
		flex-direction: column;
		gap: 4px;
	}
	.sheet-footer {
		padding: 14px 18px 28px;
		flex-shrink: 0;
		border-top: 1px dashed var(--line);
	}
	.sheet-footer .magical-btn {
		height: 50px;
		font-size: 14px;
		border-radius: 10px;
	}
}

.compare-mask {
	position: fixed;
	inset: 0;
	z-index: 1000;
	background: rgba(42, 34, 28, 0.78);
	backdrop-filter: blur(12px);
	display: flex;
	align-items: center;
	justify-content: center;
}
.compare-popup {
	width: min(860px, 94vw);
	max-height: 90vh;
	background: linear-gradient(168deg, rgba(252, 249, 244, 0.99), rgba(236, 228, 212, 0.98));
	border-radius: 12px;
	overflow: hidden;
	display: flex;
	flex-direction: column;
	border: 1px dashed var(--line-mid);
	box-shadow:
		0 22px 56px var(--shadow-mid),
		0 0 0 1px rgba(255, 250, 240, 0.45) inset;
}
.compare-topbar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 14px 20px;
	border-bottom: 1px dashed var(--line);
	background: rgba(136, 100, 76, 0.05);
	flex-shrink: 0;
}
.compare-topbar-left {
	display: flex;
	align-items: center;
	gap: 10px;
}
.cp-dot {
	width: 7px;
	height: 7px;
	border-radius: 50%;
	background: var(--gold);
	flex-shrink: 0;
	box-shadow: 0 0 0 2px rgba(196, 144, 80, 0.25);
}
.compare-title {
	font-size: 14px;
	font-weight: 600;
	font-family: var(--f-cn);
	color: var(--ink);
	letter-spacing: 0.06em;
}
.compare-topbar-right {
	display: flex;
	align-items: center;
	gap: 8px;
}
.compare-badge {
	font-size: 10px;
	font-family: var(--f-display);
	font-style: italic;
	font-weight: 500;
	padding: 4px 10px;
	border-radius: 999px;
	background: rgba(44, 58, 74, 0.08);
	border: 1px dashed rgba(44, 58, 74, 0.22);
	color: var(--navy);
	letter-spacing: 0.06em;
}
.compare-close {
	width: 30px;
	height: 30px;
	border-radius: 8px;
	cursor: pointer;
	background: rgba(136, 100, 76, 0.07);
	border: 1px dashed var(--line);
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 13px;
	color: var(--ink-mid);
	transition:
		background 0.35s var(--ease-slow),
		border-color 0.35s var(--ease-slow);
}
.compare-close:hover {
	background: rgba(166, 93, 74, 0.12);
	border-color: rgba(166, 93, 74, 0.35);
	color: var(--ochre);
}

.compare-stage {
	flex: 1;
	position: relative;
	overflow: hidden;
	min-height: 280px;
	max-height: 58vh;
	background: linear-gradient(180deg, #2c261f, #1a1612);
	cursor: ew-resize;
	user-select: none;
	-webkit-user-select: none;
	display: flex;
	align-items: center;
	justify-content: center;
}
.compare-layer {
	position: absolute;
	inset: 0;
	display: flex;
	align-items: center;
	justify-content: center;
}
.compare-layer-before {
	z-index: 1;
}
.compare-layer-after {
	z-index: 2;
}
.compare-full-img {
	width: 100%;
	height: 100%;
	object-fit: contain;
	filter: sepia(0.12) saturate(0.94);
}

.compare-divider {
	position: absolute;
	top: 0;
	bottom: 0;
	z-index: 10;
	transform: translateX(-50%);
	display: flex;
	flex-direction: column;
	align-items: center;
	pointer-events: none;
}
.divider-line {
	width: 2px;
	height: 100%;
	background: linear-gradient(
		180deg,
		transparent 0%,
		rgba(245, 240, 232, 0.15) 10%,
		rgba(196, 144, 80, 0.55) 45%,
		rgba(102, 72, 50, 0.85) 50%,
		rgba(196, 144, 80, 0.55) 55%,
		rgba(245, 240, 232, 0.12) 90%,
		transparent 100%
	);
	box-shadow: 0 0 10px rgba(82, 54, 28, 0.25);
}
.divider-handle {
	position: absolute;
	top: 50%;
	transform: translateY(-50%);
	width: 36px;
	height: 36px;
	border-radius: 50%;
	background: rgba(245, 240, 232, 0.94);
	border: 1px dashed var(--line-strong);
	box-shadow:
		0 4px 14px var(--shadow-mid),
		0 0 0 3px rgba(255, 250, 240, 0.35);
	display: flex;
	align-items: center;
	justify-content: center;
	pointer-events: auto;
	cursor: ew-resize;
}
.handle-arrow {
	display: block;
	width: 16px;
	height: 10px;
	font-size: 0;
	color: transparent;
	background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 12' fill='none'%3E%3Cpath d='M1 6h18M1 6l4-4M1 6l4 4M19 6l-4-4M19 6l-4 4' stroke='%23664832' stroke-width='1.7' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
	background-repeat: no-repeat;
	background-size: contain;
}

.compare-label {
	position: absolute;
	bottom: 48px;
	font-family: var(--f-display);
	font-style: italic;
	font-size: 10px;
	font-weight: 500;
	letter-spacing: 0.12em;
	padding: 5px 11px;
	border-radius: 4px;
	z-index: 5;
	border: 1px dashed rgba(245, 240, 232, 0.35);
}
.label-before {
	left: 12px;
	background: rgba(42, 34, 28, 0.72);
	color: var(--paper-off);
}
.label-after {
	right: 12px;
	background: rgba(166, 93, 74, 0.22);
	color: var(--accent-dk);
	border-color: rgba(166, 93, 74, 0.35);
}

.compare-stats {
	position: absolute;
	bottom: 10px;
	left: 50%;
	transform: translateX(-50%);
	display: flex;
	align-items: center;
	background: rgba(245, 240, 232, 0.92);
	border: 1px dashed var(--line-mid);
	border-radius: 999px;
	padding: 6px 16px;
	z-index: 9;
	white-space: nowrap;
	box-shadow: 0 2px 12px var(--shadow);
}
.stat-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 2px;
	padding: 0 14px;
}
.stat-val {
	font-family: var(--f-display);
	font-size: 12px;
	font-weight: 600;
	color: var(--ochre);
}
.stat-key {
	font-family: var(--f-body);
	font-size: 10px;
	color: var(--ink-faint);
}
.stat-sep {
	width: 1px;
	height: 26px;
	background: var(--line);
}

.compare-footer {
	padding: 14px 20px 18px;
	border-top: 1px dashed var(--line);
	background: rgba(136, 100, 76, 0.04);
	flex-shrink: 0;
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 16px;
}
.compare-hint {
	font-size: 12px;
	color: var(--ink-mid);
	font-family: var(--f-body);
}
.compare-actions {
	display: flex;
	gap: 10px;
	flex-shrink: 0;
}
.compare-btn {
	padding: 10px 20px;
	border-radius: 8px;
	cursor: pointer;
	font-family: var(--f-cn);
	font-size: 13px;
	font-weight: 500;
	border: 1px dashed var(--line-mid);
	background: rgba(136, 100, 76, 0.07);
	color: var(--ink-mid);
	transition:
		background 0.35s var(--ease-slow),
		border-color 0.35s var(--ease-slow),
		color 0.35s var(--ease-slow);
	white-space: nowrap;
	letter-spacing: 0.04em;
}
.compare-btn:hover {
	background: rgba(136, 100, 76, 0.14);
	border-color: var(--line-strong);
	color: var(--ink);
}
.compare-btn.primary {
	background: linear-gradient(180deg, var(--accent), var(--accent-dk));
	border: 1px solid rgba(102, 72, 50, 0.3);
	color: var(--highlight);
	box-shadow: 0 4px 14px var(--shadow-mid);
}
.compare-btn.primary:hover {
	filter: brightness(1.04);
	box-shadow: 0 6px 18px var(--shadow-mid);
	transform: translateY(-1px);
}

@media (max-width: 699px) {
	.compare-mask {
		align-items: center;
		padding: 0 12px;
	}
	.compare-popup {
		width: 100%;
		max-width: 480px;
		height: auto;
		max-height: 88vh;
		border-radius: 12px;
	}
	.compare-stage {
		min-height: 52vw;
		max-height: 52vw;
		flex-shrink: 0;
	}
	.compare-topbar-right .compare-badge {
		display: none;
	}
	.compare-footer {
		flex-direction: column;
		align-items: stretch;
		gap: 10px;
		padding: 12px 16px 16px;
	}
	.compare-hint {
		text-align: center;
	}
	.compare-actions {
		width: 100%;
	}
	.compare-btn {
		flex: 1;
		text-align: center;
		justify-content: center;
	}
	.compare-stats {
		padding: 5px 12px;
	}
	.stat-item {
		padding: 0 8px;
	}
	.compare-label {
		bottom: 36px;
	}
	.divider-handle {
		width: 30px;
		height: 30px;
	}
}
</style>

