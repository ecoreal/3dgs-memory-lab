<template>
  <common-layout pageTitle="藏品登记">
    <view :class="['relic-editor', mobileStep === 'form' ? 'step-form' : '']">

      <view class="stage-section">

        <view class="mobile-hint-bar">
          <view class="hint-seal">
            <svg width="18" height="18" viewBox="0 0 20 20" fill="none">
              <polygon points="10,2 17,6 17,14 10,18 3,14 3,6"
                stroke="#C8923A" stroke-width="1.2" fill="rgba(200,146,58,0.12)"/>
              <polygon points="10,5 14,7.5 14,12.5 10,15 6,12.5 6,7.5"
                stroke="#C8923A" stroke-width="0.7" fill="none"/>
            </svg>
          </view>
          <view class="hint-text-wrap">
            <text class="hint-title">{{ previewUrl ? '藏品已载入展台' : '等待藏品呈堂' }}</text>
            <text class="hint-desc">{{ previewUrl ? '请继续填写藏品档案，为它留下历史考据' : '仔细审视藏品细节，完成后为它建立专属档案' }}</text>
          </view>
        </view>

        <!-- 展台主体 -->
        <view 
          class="stage-frame" 
          :class="{ 'clickable-stage': !has3DModelFile && !isDefaultModelLoaded }"
          @click="loadDefaultModel"
        >
          <!-- 四角装饰 -->
          <view class="frame-corner tl"></view>
          <view class="frame-corner tr"></view>
          <view class="frame-corner bl"></view>
          <view class="frame-corner br"></view>
          <!-- 胶带纹理顶条 -->
          <view class="stage-tape"></view>

          <!-- 3D 查看器 iframe -->
          <iframe 
            v-if="has3DModelFile && model3DUrl"
            :key="viewerKey"
            :src="getViewerUrl()"
            class="model-viewer-iframe"
            allow="fullscreen"
            allowfullscreen
          />
          
          <!-- 图片预览 -->
          <image v-else-if="previewUrl" :src="previewUrl" mode="aspectFit" class="stage-img"/>
          
          <!-- 空状态 -->
          <view v-else class="stage-empty">
            <svg width="52" height="52" viewBox="0 0 52 52" fill="none" style="opacity:0.22">
              <rect x="6" y="12" width="40" height="30" rx="3" stroke="#8B4513" stroke-width="1.4"/>
              <line x1="6" y1="20" x2="46" y2="20" stroke="#8B4513" stroke-width="0.9"/>
              <circle cx="26" cy="32" r="6" stroke="#8B4513" stroke-width="1.1"/>
              <line x1="14" y1="16" x2="14" y2="16" stroke="#8B4513" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <text class="stage-empty-txt">藏品待入馆</text>
            <text class="stage-empty-sub">Awaiting Specimen</text>
            
            <!-- 新增点击提示 -->
            <view v-if="!isDefaultModelLoaded" class="click-hint">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <circle cx="8" cy="8" r="7" stroke="#8B4513" stroke-width="1.2" fill="rgba(139,69,19,0.1)"/>
                <text x="5" y="11" font-size="10" fill="#8B4513">+</text>
              </svg>
              <text class="hint-text">点击加载 3D 预览模型</text>
            </view>
          </view>

          <!-- 状态标签 -->
          <view class="stage-tag br-tag">
            <text class="tag-txt">{{ platformText }}</text>
          </view>

          <!-- 音频播放（桌面端） -->
          <view v-if="audioPath && !isRecording" class="audio-reveal-btn" @click="toggleAudioPlay">
            <view class="arb-dot"></view>
            <svg width="11" height="11" viewBox="0 0 12 12" fill="none">
              <polygon points="2,1 11,6 2,11" fill="#8B4513" opacity="0.7"/>
            </svg>
            <text class="arb-txt">播放语音旁白</text>
          </view>
        </view>

        <!-- 3D 模型状态提示 -->
        <view v-if="has3DModelFile" class="model-status">
          <view class="status-icon">📦</view>
          <text class="status-text">{{ isDefaultModelLoaded ? '正在使用示例 3D 模型' : '3D 模型已加载' }}</text>
          <text class="status-action" @click="clear3DModel">移除</text>
        </view>

        <!-- 桌面端语音胶囊 -->
        <view :class="['voice-capsule', 'desktop-voice', isRecording ? 'rec-active' : '']" @click="toggleRecording">
          <view class="vc-left">
            <view class="vc-ornament">
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                <rect x="5" y="2" width="6" height="9" rx="3" stroke="#8B4513" stroke-width="1.1"/>
                <path d="M3 9C3 11.76 5.24 14 8 14C10.76 14 13 11.76 13 9" stroke="#8B4513" stroke-width="1.1" stroke-linecap="round"/>
                <line x1="8" y1="14" x2="8" y2="15.5" stroke="#8B4513" stroke-width="1.1" stroke-linecap="round"/>
              </svg>
            </view>
            <view :class="['vc-pip', isRecording ? 'rec' : (audioPath ? 'ready' : '')]"></view>
            <text class="vc-txt">
              {{ isRecording ? '录音中 · 点击停止' : (audioPath ? '✓ 语音已留存，点击重录' : '留下语音旁白') }}
            </text>
          </view>
          <view class="vc-btn">
            <text class="vc-icon">{{ isRecording ? '⏹' : '●' }}</text>
          </view>
        </view>

        <!-- 手机端语音区 -->
        <view class="voice-area-mobile">
          <view class="vam-header">
            <view class="vam-rule"></view>
            <text class="vam-label">语音旁白</text>
            <view class="vam-rule"></view>
          </view>
          <view :class="['voice-capsule', isRecording ? 'rec-active' : '']" @click="toggleRecording">
            <view class="vc-left">
              <view class="vc-ornament">
                <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                  <rect x="5" y="2" width="6" height="9" rx="3" stroke="#8B4513" stroke-width="1.1"/>
                  <path d="M3 9C3 11.76 5.24 14 8 14C10.76 14 13 11.76 13 9" stroke="#8B4513" stroke-width="1.1" stroke-linecap="round"/>
                  <line x1="8" y1="14" x2="8" y2="15.5" stroke="#8B4513" stroke-width="1.1" stroke-linecap="round"/>
                </svg>
              </view>
              <view :class="['vc-pip', isRecording ? 'rec' : (audioPath ? 'ready' : '')]"></view>
              <text class="vc-txt">{{ isRecording ? '录音中 · 点击停止' : (audioPath ? '✓ 语音已留存，点击重录' : '点击开始录音') }}</text>
            </view>
            <view class="vc-btn">
              <text class="vc-icon">{{ isRecording ? '⏹' : '●' }}</text>
            </view>
          </view>
          <!-- 播放旁白 -->
          <view v-if="audioPath && !isRecording" class="mobile-audio-play" 
            @click="toggleAudioPlay"
            :class="{ 'playing': isAudioPlaying }">
            <view class="map-waves">
              <view v-for="w in 5" :key="w" class="map-wave"></view>
            </view>
            <text class="map-label">{{ isAudioPlaying ? '暂停播放' : '回听语音旁白' }}</text>
            <view class="map-play-btn">
              <text class="play-text-icon">{{ isAudioPlaying ? '❙❙' : '▶' }}</text>
            </view>
          </view>
        </view>

        <!-- 手机端下一步 -->
        <view class="mobile-next-btn" @tap="goToForm">
          <view class="mnb-seal">
            <svg width="16" height="16" viewBox="0 0 20 20" fill="none">
              <polygon points="10,2 17,6 17,14 10,18 3,14 3,6" stroke="rgba(245,240,228,0.6)" stroke-width="1.1" fill="none"/>
            </svg>
          </view>
          <text class="mnb-txt">建立藏品档案</text>
          <text class="mnb-arrow">→</text>
        </view>
      </view>

      <!-- ══ 右侧：档案填写区 ══ -->
      <view class="archive-panel">
        <scroll-view scroll-y class="archive-scroll" :show-scrollbar="false" :enhanced="true">

          <!-- 档案头部 -->
          <view class="archive-header">
            <!-- 手机端返回 -->
            <view class="mobile-back-btn" @tap="backToPreview">
              <text class="back-arrow">←</text>
              <text class="back-label">返回展台</text>
            </view>
            <view class="ah-ornament">
              <view class="ah-rule-l"></view>
              <view class="ah-wax">
                <svg width="26" height="26" viewBox="0 0 28 28" fill="none">
                  <polygon points="14,3 23,8 23,20 14,25 5,20 5,8"
                    stroke="#8B4513" stroke-width="1.3" fill="rgba(139,69,19,0.1)"/>
                  <polygon points="14,7 20,10.5 20,17.5 14,21 8,17.5 8,10.5"
                    stroke="#C8923A" stroke-width="0.8" fill="none"/>
                </svg>
              </view>
              <view class="ah-rule-r"></view>
            </view>
            <text class="ah-title">{{ modelId ? '藏品档案卡' : '新藏品登记册' }}</text>
            <text class="ah-sub">{{ modelId ? 'Collection Record · Amendment' : 'New Specimen · Registration' }}</text>
            <view class="ah-stamp" v-if="modelId">
              <text class="ah-stamp-txt">AMENDMENT</text>
            </view>
          </view>

          <!-- 档案内容 -->
          <view class="archive-body">

            <!-- §1 核心识别 -->
            <view class="archive-section">
              <view class="section-head">
                <view class="sh-num">§ 1</view>
                <text class="sh-label">核心识别</text>
                <view class="sh-rule"></view>
              </view>

              <view class="field-entry">
                <view class="fe-label-row">
                  <text class="fe-label">物品名称</text>
                  <text class="fe-en">NAME OF SPECIMEN</text>
                </view>
                <view :class="['fe-input-wrap', formData.name ? 'fe-filled' : '']">
                  <input class="fe-input" v-model="formData.name"
                    placeholder="请输入藏品名称"
                    placeholder-style="color:#B8A898;font-size:14px"/>
                  <view v-if="formData.name" class="fe-check">✓</view>
                </view>
              </view>

              <view class="field-row-duo">
                <!-- 年份 -->
                <view class="field-entry half">
                  <view class="fe-label-row">
                    <text class="fe-label">制造年代</text>
                    <text class="fe-en">YEAR</text>
                  </view>
                  <view :class="['fe-input-wrap', formData.year ? 'fe-filled' : '']">
                    <picker @change="handleYearChange" :range="yearOptions" class="year-picker">
                      <view class="year-trigger">
                        <text class="year-val">{{ formData.year || '选择' }}</text>
                        <text class="year-caret">▾</text>
                      </view>
                    </picker>
                    <view class="fe-divider"></view>
                    <input class="fe-input year-manual"
                      v-model="formData.year"
                      placeholder="手动输入"
                      placeholder-style="color:#B8A898;font-size:12px"
                      type="number" maxlength="4"/>
                  </view>
                </view>
                <!-- 分类 -->
                <view class="field-entry half">
                  <view class="fe-label-row">
                    <text class="fe-label">展馆分类</text>
                    <text class="fe-en">CATEGORY</text>
                  </view>
                  <view :class="['fe-input-wrap', formData.categoryId ? 'fe-filled' : '']">
                    <picker @change="handleCategoryChange" :range="categories" range-key="name">
                      <view class="fe-select">
                        <text class="fe-select-val">{{ formData.categoryName || '归类' }}</text>
                        <text class="fe-select-caret">▾</text>
                      </view>
                    </picker>
                  </view>
                </view>
              </view>
            </view>

            <!-- §2 关键词标签 -->
            <view class="archive-section">
              <view class="section-head">
                <view class="sh-num">§ 2</view>
                <text class="sh-label">情感关键词</text>
                <view class="sh-rule"></view>
              </view>
              <view class="tag-field">
                <view class="tag-pill" v-for="(tag, i) in formData.tags" :key="i">
                  <text class="tp-hash">#</text>
                  <text class="tp-txt">{{ tag }}</text>
                  <text class="tp-rm" @click.stop="removeTag(i)">×</text>
                </view>
                <input class="tag-input" v-model="newTag"
                  placeholder="+ 新词条"
                  placeholder-style="color:#B8A898;font-size:12px"
                  @confirm="addTag"/>
              </view>
            </view>

            <!-- §3 岁月叙事 -->
            <view class="archive-section">
              <view class="section-head">
                <view class="sh-num">§ 3</view>
                <text class="sh-label">岁月叙事</text>
                <view class="sh-rule"></view>
              </view>
              <view :class="['story-field', formData.story ? 'sf-filled' : '']">
                <view class="sf-corner c1"></view>
                <view class="sf-corner c2"></view>
                <textarea class="sf-textarea" v-model="formData.story"
                  placeholder="记录它对你独一无二的意义，它经历过什么岁月…"
                  placeholder-style="color:#B8A898;font-size:13px;line-height:2"
                  :maxlength="-1"/>
              </view>
            </view>

            <!-- §4 3D模型管理（当有3D模型时显示） -->
            <view class="archive-section" v-if="has3DModelFile">
              <view class="section-head">
                <view class="sh-num">§ 4</view>
                <text class="sh-label">3D模型管理</text>
                <view class="sh-rule"></view>
              </view>
              
              <view class="field-entry">
                <view class="fe-label-row">
                  <text class="fe-label">当前模型</text>
                  <text class="fe-en">3D MODEL</text>
                </view>
                
                <!-- 模型文件信息显示 -->
                <view class="model-file-info">
                  <view class="file-icon">📦</view>
                  <text class="file-name">{{ getModelFileName() }}</text>
                  <text class="file-action" @click="clear3DModel">移除</text>
                </view>
                
                <!-- 上传新模型按钮 -->
                <view class="upload-model-btn" @click="upload3DModel">
                  <view class="upload-icon">📤</view>
                  <text class="upload-text">上传自定义 3D 模型</text>
                  <text class="upload-hint">支持 .glb/.gltf 格式，替换当前模型</text>
                </view>
              </view>
            </view>

            <!-- §5 音频管理（当有音频时显示） -->
            <view class="archive-section" v-if="audioPath">
              <view class="section-head">
                <view class="sh-num">§ 5</view>
                <text class="sh-label">语音旁白</text>
                <view class="sh-rule"></view>
              </view>
              
              <view class="field-entry">
                <view class="fe-label-row">
                  <text class="fe-label">当前语音</text>
                  <text class="fe-en">VOICE NARRATION</text>
                </view>
                
                <!-- 音频播放器完整组件 -->
                <view class="audio-player">
                  <!-- 左侧播放按钮 -->
                  <view class="audio-player-left" @click="toggleAudioPlay">
                    <text class="audio-play-icon">{{ isAudioPlaying ? '❙❙' : '▶' }}</text>
                  </view>
                  
                  <!-- 中间进度条区域 -->
                  <view class="audio-player-center">
                    <!-- 进度条slider -->
                    <slider
                      :value="audioCurrentTime"
                      :max="audioDuration"
                      @change="onAudioSliderChange"
                      @changing="onAudioSliderChanging"
                      activeColor="#8B4513"
                      backgroundColor="#E0D8C8"
                      block-size="12"
                      block-color="#8B4513"
                    />
                    
                    <!-- 时间显示 -->
                    <view class="audio-time">
                      <text class="audio-time-current">{{ formatTime(audioCurrentTime) }}</text>
                      <text class="audio-time-duration">{{ formatTime(audioDuration) }}</text>
                    </view>
                  </view>
                  
                  <!-- 右侧删除按钮 -->
                  <view class="audio-player-right" @click="deleteAudio">
                    <text class="audio-delete-icon">×</text>
                  </view>
                </view>
              </view>
            </view>

          </view>

          <!-- 底部存档操作 -->
          <view class="archive-footer">
            <view class="af-divider">
              <view class="afd-line"></view>
              <view class="afd-seal">
                <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                  <polygon points="8,1.5 13,4.5 13,11.5 8,14.5 3,11.5 3,4.5"
                    stroke="#8B4513" stroke-width="1" fill="rgba(139,69,19,0.08)"/>
                </svg>
              </view>
              <view class="afd-line"></view>
            </view>
            <view class="submit-btn" @click.stop="handleSave">
              <view class="sb-tape"></view>
              <view class="sb-inner">
                <svg width="15" height="15" viewBox="0 0 16 16" fill="none">
                  <polygon points="8,1 13.5,4 13.5,12 8,15 2.5,12 2.5,4"
                    stroke="rgba(245,240,228,0.7)" stroke-width="1" fill="none"/>
                  <line x1="5" y1="8" x2="7.5" y2="10.5" stroke="rgba(245,240,228,0.9)" stroke-width="1.2" stroke-linecap="round"/>
                  <line x1="7.5" y1="10.5" x2="11.5" y2="6" stroke="rgba(245,240,228,0.9)" stroke-width="1.2" stroke-linecap="round"/>
                </svg>
                <text class="sb-txt">{{ modelId ? '确认归档入馆' : '保存为草稿' }}</text>
              </view>
            </view>
            <text class="af-hint">本档案将永久存入一方阁数字展馆</text>
          </view>

        </scroll-view>
      </view>

    </view>
  </common-layout>
</template>

<script>
import MyLayout from '@/components/common-layout/common-layout.vue'
import request, { BASE_URL } from '@/utils/request.js';

export default {
  components: { 'common-layout': MyLayout },
  data() {
    const currentYear = new Date().getFullYear();
    const yearOptions = Array.from({ length: 101 }, (_, i) => String(currentYear - i));
    return {
      modelId: null,
      previewUrl: '',
      has3DModelFile: false,
      model3DUrl: '',
      viewerKey: 0,
      isDefaultModelLoaded: false,
      uploadedModelFile: null,
      viewerParams: { brightness: 4.0, autoRotate: false, bgMode: 'default' },
      isRecording: false,
      audioPath: '',
      uploadedAudioUrl: '',
      isUploadingAudio: false,
      audioContext: null,
      platformText: '系统就绪',
      recorderManager: null,
      mediaRecorder: null,
      audioChunks: [],
      newTag: '',
      mobileStep: 'preview',
      yearOptions,
      categories: [
        { id: 1, name: '矿晶标本' },
        { id: 2, name: '复古家电' },
        { id: 3, name: '昆虫标本' },
        { id: 4, name: '模型手办' },
        { id: 5, name: '工业遗迹' },
        { id: 6, name: '生活百货' }
      ],
      formData: { name: '', year: '', categoryName: '', categoryId: null, tags: [], story: '' },
      
      isAudioPlaying: false,
      audioCurrentTime: 0,
      audioDuration: 0,
      audioUpdateInterval: null,
      isDraggingSlider: false
    }
  },

  onLoad(options) {
    if (options?.id && options.id !== 'undefined') {
      this.modelId = options.id;
      this.fetchModelDetail();
    }
    
    this.platformText = '浏览器核心';
    
    this.initNativeRecorder();
  },

  onUnload() {
    this.stopUpdateProgress();
    if (this.audioContext) {
      this.audioContext.stop();
      this.audioContext.destroy();
    }
  },

  methods: {
    goToForm() { this.mobileStep = 'form'; },
    backToPreview() { this.mobileStep = 'preview'; },
    handleYearChange(e) { this.formData.year = this.yearOptions[e.detail.value]; },
    handleCategoryChange(e) {
      const s = this.categories[e.detail.value];
      this.formData.categoryName = s.name;
      this.formData.categoryId = s.id;
    },
    addTag() {
      const v = this.newTag.trim();
      if (v && !this.formData.tags.includes(v)) {
        this.formData.tags.push(v);
        this.newTag = '';
      }
    },
    removeTag(i) { this.formData.tags.splice(i, 1); },

    async fetchModelDetail() {
      if (!this.modelId) return;
      try {
        const res = await request({ url: `/models/detail/${this.modelId}`, method: 'GET' });
        if (res) {
          this.previewUrl = res.thumbnailUrl;
          this.formData.name = res.title || '';
          this.formData.story = res.story || '';
          this.formData.year = res.productionYear ? String(res.productionYear) : '';
          const target = this.categories.find(c => c.id == res.categoryId);
          if (target) {
            this.formData.categoryId = target.id;
            this.formData.categoryName = target.name;
          }
          if (res.tags) {
            this.formData.tags = Array.isArray(res.tags) ? res.tags : res.tags.split(',');
          }
          
          try {
            const filesRes = await request({ url: `/model-files/model/${this.modelId}`, method: 'GET' });
			const glbFile = filesRes?.find(file => file.fileType === "GLB");
			if (glbFile) {
			  this.model3DUrl = glbFile.filePath;
			  this.has3DModelFile = true;
			  this.isDefaultModelLoaded = false;
			} else if (res.model3dUrl) {
			  this.model3DUrl = res.model3dUrl;
			  this.has3DModelFile = true;
			  this.isDefaultModelLoaded = false;
			}
          } catch (filesError) {
            console.warn('获取模型文件列表失败，使用详情中的URL:', filesError);
            if (res.model3dUrl) {
              this.model3DUrl = res.model3dUrl;
              this.has3DModelFile = true;
              this.isDefaultModelLoaded = false;
            }
          }
        }
      } catch (e) {
        console.error('详情加载失败', e);
        const msg = e?.error || e?.message || '详情加载失败';
        setTimeout(() => uni.showToast({ title: msg, icon: 'none' }), 300);
      }
    },

    getModelFileName() {
      if (this.isDefaultModelLoaded) return '示例模型 (example.glb)';
      if (this.uploadedModelFile) return this.uploadedModelFile.name;
      if (this.model3DUrl) {
        const parts = this.model3DUrl.split('/');
        return parts[parts.length - 1] || '自定义模型';
      }
      return '自定义模型';
    },

    loadDefaultModel() {
      if (this.has3DModelFile) return;
      uni.showToast({ title: '正在加载 3D 模型...', icon: 'loading', duration: 2000 });
      const defaultModelUrl = '/models/Toy1.glb';
      this.model3DUrl = defaultModelUrl;
      this.has3DModelFile = true;
      this.isDefaultModelLoaded = true;
      this.viewerKey += 1;
      setTimeout(() => {
        uni.showToast({ title: '3D 模型已加载', icon: 'success', duration: 1500 });
      }, 1000);
    },

    getViewerUrl() {
      if (!this.model3DUrl) return '';
      const localUrl = this.getBaseUrl();
      if (!localUrl) {
        console.error('无法获取基础 URL');
        return '';
      }
      let cleanModelPath = this.model3DUrl;
      try { cleanModelPath = decodeURIComponent(cleanModelPath); } catch (e) { console.log('路径解码失败，使用原始路径'); }
      if (!cleanModelPath.startsWith('http://') && !cleanModelPath.startsWith('https://')) {
        if (!cleanModelPath.startsWith('/')) cleanModelPath = '/' + cleanModelPath;
      }
      const encodedModelPath = encodeURIComponent(cleanModelPath);
      let queryString = '';
      // #ifdef H5
      const params = new URLSearchParams();
      params.set('model', encodedModelPath);
      params.set('base_url', BASE_URL);
      params.set('isStatic', 'true');
      queryString = params.toString();
      // #endif
      // #ifdef APP-PLUS
      const paramsObj = { model: encodedModelPath, base_url: BASE_URL, isStatic: 'true' };
      const queryParts = [];
      for (const [key, value] of Object.entries(paramsObj)) {
        if (value !== null && value !== undefined) queryParts.push(`${key}=${value}`);
      }
      queryString = queryParts.join('&');
      // #endif
      return localUrl + queryString;
    },

    getBaseUrl() {
      try {
        if (typeof window !== 'undefined' && window.location) {
          return window.location.origin + '/static/model-viewer.html?';
        }
        return 'static/model-viewer.html?';
        return BASE_URL;
      } catch (e) {
        console.error('获取基础URL时出错: ', e);
        return BASE_URL;
      }
    },

    clear3DModel() {
      this.has3DModelFile = false;
      this.model3DUrl = '';
      this.isDefaultModelLoaded = false;
      this.uploadedModelFile = null;
      this.viewerKey += 1;
      uni.showToast({ title: '已移除 3D 模型', icon: 'success', duration: 1500 });
    },

    async upload3DModel() {
      try {
        const res = await uni.chooseFile({ count: 1, type: 'file', extension: ['.glb', '.gltf'] });
        if (res.tempFiles[0]) {
          this.uploadedModelFile = res.tempFiles[0];
          this.isDefaultModelLoaded = false;
          this.has3DModelFile = true;
          if (this.modelId) await this.uploadModelToServer();
          else this.model3DUrl = this.uploadedModelFile.path;
          this.viewerKey += 1;
          uni.showToast({ title: '3D模型已加载', icon: 'success' });
        }
      } catch (error) {
        console.error('上传3D模型失败:', error);
        uni.showToast({ title: '上传失败', icon: 'none' });
      }
    },

    async uploadModelToServer() {
      uni.showLoading({ title: '上传3D模型中...' });
      try {
        const token = uni.getStorageSync('token');
        const formData = new FormData();
        formData.append('file', this.uploadedModelFile);
        const response = await fetch(`${BASE_URL}/upload/3d-model`, {
          method: 'POST',
          headers: { 'Authorization': token ? `Bearer ${token}` : '' },
          body: formData
        });
        const data = await response.json();
        this.model3DUrl = data.url;
        uni.hideLoading();
        uni.showToast({ title: '3D模型上传成功', icon: 'success' });
        return data.url;
      } catch (error) {
        console.error('上传3D模型到服务器失败:', error);
        uni.hideLoading();
        uni.showToast({ title: '上传失败，请重试', icon: 'none' });
        throw error;
      }
    },

    async handleSave() {
      if (!this.formData.name) {
        uni.showToast({ title: '请填写藏品名称', icon: 'none' });
        return;
      }
      uni.showLoading({ title: '档案归档中...' });
      try {
        const requestData = {
          mid: Number(this.modelId),
          title: this.formData.name,
          productionYear: this.formData.year,
          story: this.formData.story,
          tags: Array.isArray(this.formData.tags) ? this.formData.tags : this.formData.tags.split(',').map(t => t.trim()).filter(Boolean),
          categoryId: this.formData.categoryId ? Number(this.formData.categoryId) : null
        };
        if (this.model3DUrl && this.has3DModelFile && !this.isDefaultModelLoaded) {
          if (this.uploadedModelFile) {
            try {
              const uploadedUrl = await this.uploadModelToServer();
              if (uploadedUrl) requestData.model3dUrl = uploadedUrl;
            } catch (uploadError) { console.error('3D模型上传失败:', uploadError); }
          } else if (this.model3DUrl && !this.model3DUrl.startsWith('/models/')) {
            requestData.model3dUrl = this.model3DUrl;
          }
        }
        await request({ url: `/models/update/${this.modelId}`, method: 'POST', data: requestData });
        if (this.audioPath) {
          try {
            const audioUrl = await this.uploadAudio();
            if (audioUrl) {
              await request({
                url: '/model-audios/bind',
                method: 'POST',
                data: { mid: Number(this.modelId), audioUrl, duration: null, transcript: '', isPublic: false }
              });
            }
          } catch (audioError) {
            console.error('语音绑定失败:', audioError);
            uni.showToast({ title: '语音保存失败，档案已同步', icon: 'none', duration: 2000 });
          }
        }
        uni.hideLoading();
        setTimeout(() => {
          uni.showToast({ title: '已归档入馆', icon: 'success' });
          setTimeout(() => uni.reLaunch({ url: '/pages/home/home' }), 1200);
        }, 300);
      } catch (e) {
        uni.hideLoading();
        const msg = e?.error || e?.message || '归档失败，请重试';
        setTimeout(() => {
          uni.showModal({ title: '归档错误', content: msg, showCancel: false });
        }, 300);
      }
    },

    async uploadAudio() {
      if (!this.audioPath) return null;
      if (this.uploadedAudioUrl) return this.uploadedAudioUrl;
      this.isUploadingAudio = true;
      const token = uni.getStorageSync('token');
      return new Promise((resolve, reject) => {
        fetch(this.audioPath)
          .then(r => r.blob())
          .then(blob => {
            const fd = new FormData();
            fd.append('file', blob, 'voice.mp3');
            return fetch(`${BASE_URL}/upload/workshop`, {
              method: 'POST',
              headers: { 'Authorization': token ? `Bearer ${token}` : '' },
              body: fd
            });
          })
          .then(r => r.json())
          .then(data => {
            this.uploadedAudioUrl = data.url;
            this.isUploadingAudio = false;
            resolve(data.url);
          })
          .catch(err => { this.isUploadingAudio = false; reject(err); });
      });
    },

    initNativeRecorder() {
      try {
        this.recorderManager = uni.getRecorderManager();
        this.recorderManager.onStop(res => {
          this.audioPath = res.tempFilePath;
          this.uploadedAudioUrl = '';
          this.isRecording = false;
        });
      } catch (e) { console.error('初始化录音器失败:', e); }
    },

	async toggleRecording() {
	  if (this.audioContext && this.isAudioPlaying) {
		this.audioContext.stop();
		this.audioContext.destroy();
		this.audioContext = null;
		this.isAudioPlaying = false;
		this.stopUpdateProgress();
	  }
	  
	  if (this.isRecording) {
		if (this.mediaRecorder) {
		  this.mediaRecorder.stop();
		} else if (this.recorderManager) {
		  this.recorderManager.stop();
		}
		return;
	  }
	  
	  const platform = uni.getSystemInfoSync().platform;
	  
	  if (platform === 'h5') {
		if (!navigator || !navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
		  uni.showToast({ 
			title: '当前浏览器不支持录音，请使用Chrome、Edge等现代浏览器', 
			icon: 'none' 
		  });
		  return;
		}
		
		try {
		  const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
		  this.mediaRecorder = new MediaRecorder(stream);
		  this.audioChunks = [];
		  this.mediaRecorder.ondataavailable = e => this.audioChunks.push(e.data);
		  this.mediaRecorder.onstop = () => {
			this.audioPath = URL.createObjectURL(new Blob(this.audioChunks, { type: 'audio/mp3' }));
			this.uploadedAudioUrl = '';
			this.isRecording = false;
		  };
		  this.mediaRecorder.start();
		  this.isRecording = true;
		} catch (e) {
		  console.error('麦克风访问失败:', e);
		  let errorMsg = '麦克风访问失败';
		  if (e.name === 'NotAllowedError') {
			errorMsg = '请允许使用麦克风权限';
		  } else if (e.name === 'NotFoundError') {
			errorMsg = '未找到可用的麦克风设备';
		  }
		  uni.showToast({ title: errorMsg, icon: 'none' });
		}
	  } else {
		if (!this.recorderManager) {
		  this.initNativeRecorder();
		}
		
		if (this.recorderManager) {
		  this.recorderManager.start();
		  this.isRecording = true;
		} else {
		  uni.showToast({ title: '当前环境不支持录音', icon: 'none' });
		}
	  }
	},

    toggleAudioPlay() {
      if (!this.audioPath) return;
      
      if (this.isAudioPlaying) {
        this.audioContext.pause();
      } else {
        this.playAudio();
      }
    },

    playAudio() {
      if (!this.audioPath) return;
      
      if (this.audioContext) {
        this.audioContext.stop();
        this.audioContext.destroy();
      }
      
      this.audioContext = uni.createInnerAudioContext();
      this.audioContext.src = this.audioPath;
      
      this.audioContext.onPlay(() => {
        this.isAudioPlaying = true;
        this.startUpdateProgress();
      });
      
      this.audioContext.onPause(() => {
        this.isAudioPlaying = false;
        this.stopUpdateProgress();
      });
      
      this.audioContext.onEnded(() => {
        this.isAudioPlaying = false;
        this.audioCurrentTime = 0;
        this.stopUpdateProgress();
      });
      
      this.audioContext.onTimeUpdate(() => {
        if (!this.isDraggingSlider) {
          this.audioCurrentTime = this.audioContext.currentTime;
          this.audioDuration = this.audioContext.duration;
        }
      });
      
      this.audioContext.onError((err) => {
        console.error('音频播放错误', err);
        this.isAudioPlaying = false;
        this.stopUpdateProgress();
        uni.showToast({ title: '音频播放失败', icon: 'none' });
      });
      
      this.audioContext.play();
    },
    
    startUpdateProgress() {
      this.stopUpdateProgress();
      this.audioUpdateInterval = setInterval(() => {
        if (this.audioContext && !this.isDraggingSlider) {
          this.audioCurrentTime = this.audioContext.currentTime;
          this.audioDuration = this.audioContext.duration;
        }
      }, 200);
    },
    
    stopUpdateProgress() {
      if (this.audioUpdateInterval) {
        clearInterval(this.audioUpdateInterval);
        this.audioUpdateInterval = null;
      }
    },
    
    onAudioSliderChange(e) {
      const newTime = e.detail.value;
      this.audioCurrentTime = newTime;
      this.isDraggingSlider = false;
      
      if (this.audioContext) {
        this.audioContext.seek(newTime);
      }
    },
    
    onAudioSliderChanging(e) {
      this.isDraggingSlider = true;
      this.audioCurrentTime = e.detail.value;
    },
    
    deleteAudio() {
      uni.showModal({
        title: '删除语音',
        content: '确定要删除此语音旁白吗？',
        success: (res) => {
          if (res.confirm) {
            if (this.audioContext) {
              this.audioContext.stop();
              this.audioContext.destroy();
              this.audioContext = null;
            }
            
            this.stopUpdateProgress();
            
            this.audioPath = '';
            this.uploadedAudioUrl = '';
            this.isAudioPlaying = false;
            this.audioCurrentTime = 0;
            this.audioDuration = 0;
            
            uni.showToast({ title: '语音已删除', icon: 'success' });
          }
        }
      });
    },
    
    formatTime(seconds) {
      if (isNaN(seconds) || seconds === 0) return '00:00';
      
      const mins = Math.floor(seconds / 60);
      const secs = Math.floor(seconds % 60);
      return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
  }
}
</script>

<style scoped>
.relic-editor {
  --rust:   #8B4513;
  --gold:   #C8923A;
  --ink:    #2A1F14;
  --parch:  #F5F0E4;
  --warm:   #7A5C3A;
  --fade:   #5C4A36;
  --dust:   #9A8878;
  --edge:   rgba(122,92,58,0.18);
  --edge2:  rgba(122,92,58,0.38);
  --bg:     #EDE8DC;
  --paper:  #F9F5EB;

  display: flex;
  width: 100%;
  height: 100%;
  background: var(--bg);
  overflow: hidden;
  font-family: 'Lora', Georgia, serif;
  background-image:
    radial-gradient(ellipse at 20% 50%, rgba(200,146,58,0.04) 0%, transparent 60%),
    radial-gradient(ellipse at 80% 20%, rgba(139,69,19,0.03) 0%, transparent 50%);
}

.stage-section {
  flex: 1.1;
  padding: 24px 20px 24px 24px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 14px;
  position: relative;
}

.stage-frame {
  flex: 1;
  position: relative;
  border-radius: 6px;
  background: var(--paper);
  border: 1px solid var(--edge);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 1px 0 rgba(255,255,255,0.9) inset,
    4px 8px 0 rgba(122,92,58,0.06),
    8px 16px 32px rgba(61,43,26,0.08);
  background-image:
    radial-gradient(ellipse at 50% 50%, rgba(200,146,58,0.04) 0%, transparent 70%);
}

.clickable-stage {
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.clickable-stage:hover {
  box-shadow: 0 0 0 2px rgba(139, 69, 19, 0.3);
}

.clickable-stage:hover .stage-empty {
  background-color: rgba(245, 240, 228, 0.9);
}

.stage-tape {
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 5px;
  background: repeating-linear-gradient(
    90deg,
    var(--rust) 0, var(--rust) 7px,
    transparent 7px, transparent 16px
  );
  opacity: 0.18;
  flex-shrink: 0;
  z-index: 3;
}

.frame-corner {
  position: absolute;
  width: 16px; height: 16px;
  z-index: 4; opacity: 0.45;
}
.frame-corner.tl { top: 10px; left: 10px; border-top: 1.5px solid var(--rust); border-left: 1.5px solid var(--rust); }
.frame-corner.tr { top: 10px; right: 10px; border-top: 1.5px solid var(--rust); border-right: 1.5px solid var(--rust); }
.frame-corner.bl { bottom: 10px; left: 10px; border-bottom: 1.5px solid var(--rust); border-left: 1.5px solid var(--rust); }
.frame-corner.br { bottom: 10px; right: 10px; border-bottom: 1.5px solid var(--rust); border-right: 1.5px solid var(--rust); }

.model-viewer-iframe {
  width: 100%;
  height: 100%;
  border: none;
  background: transparent;
  z-index: 1;
  position: absolute;
  top: 0;
  left: 0;
}

.stage-img {
  width: 80%; height: 80%;
  filter: sepia(8%) brightness(0.96);
}

.stage-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
.stage-empty-txt {
  font-family: 'Playfair Display', serif;
  font-size: 15px;
  color: var(--dust);
  letter-spacing: 1px;
}
.stage-empty-sub {
  font-family: 'IM Fell English SC', serif;
  font-size: 10px;
  color: var(--dust);
  letter-spacing: 2.5px;
  opacity: 0.6;
}

.click-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  animation: pulse 2s infinite;
}

.hint-text {
  font-family: 'IM Fell English SC', serif;
  font-size: 11px;
  color: var(--warm);
  letter-spacing: 1px;
  text-align: center;
}

@keyframes pulse {
  0% { opacity: 0.7; }
  50% { opacity: 1; }
  100% { opacity: 0.7; }
}

.stage-tag {
  position: absolute;
  z-index: 5;
  padding: 4px 10px;
  background: rgba(245,240,228,0.92);
  border: 1px solid var(--edge);
  border-radius: 3px;
  backdrop-filter: blur(6px);
}
.tl-tag { top: 14px; left: 14px; }
.br-tag { bottom: 14px; right: 14px; }
.tag-txt {
  font-family: 'IM Fell English SC', serif;
  font-size: 9px;
  color: var(--warm);
  letter-spacing: 1.5px;
}

.model-status {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(245,240,228,0.8);
  border: 1px solid var(--edge);
  border-radius: 6px;
  padding: 8px 12px;
  margin-top: 4px;
}

.status-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.status-text {
  flex: 1;
  font-family: 'IM Fell English SC', serif;
  font-size: 12px;
  color: var(--fade);
  letter-spacing: 0.5px;
}

.status-action {
  font-family: 'IM Fell English SC', serif;
  font-size: 11px;
  color: var(--rust);
  cursor: pointer;
  letter-spacing: 0.5px;
  transition: color 0.2s;
}

.status-action:hover {
  color: #6B340F;
  text-decoration: underline;
}

.audio-reveal-btn {
  position: absolute;
  bottom: 14px; left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 7px 16px;
  background: rgba(245,240,228,0.94);
  border: 1px solid var(--edge2);
  border-radius: 100px;
  z-index: 10;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 10px rgba(61,43,26,0.1);
  animation: fadeUpIn 0.35s ease;
}
.audio-reveal-btn:hover {
  background: var(--paper);
  box-shadow: 0 4px 14px rgba(61,43,26,0.14);
  transform: translateX(-50%) translateY(-1px);
}
.arb-dot {
  width: 5px; height: 5px;
  border-radius: 50%;
  background: var(--rust);
  box-shadow: 0 0 5px rgba(139,69,19,0.5);
  animation: blink 2s ease-in-out infinite;
}
.arb-txt {
  font-family: 'IM Fell English SC', serif;
  font-size: 11px;
  color: var(--warm);
  letter-spacing: 1px;
}

.voice-capsule {
  height: 50px;
  background: var(--paper);
  border: 1px solid var(--edge);
  border-radius: 100px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 6px 0 14px;
  box-sizing: border-box;
  cursor: pointer;
  transition: border-color 0.25s, box-shadow 0.25s;
  box-shadow: 0 1px 0 rgba(255,255,255,0.8) inset, 2px 3px 0 rgba(122,92,58,0.06);
}
.voice-capsule:hover { border-color: var(--edge2); }
.voice-capsule.rec-active {
  border-color: rgba(180,60,60,0.4);
  box-shadow: 0 0 16px rgba(180,60,60,0.08), 0 1px 0 rgba(255,255,255,0.8) inset;
}
.desktop-voice { flex-shrink: 0; }

.vc-left {
  display: flex;
  align-items: center;
  gap: 9px;
  flex: 1;
  min-width: 0;
}
.vc-ornament { flex-shrink: 0; opacity: 0.65; }
.vc-pip {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: var(--dust);
  flex-shrink: 0;
  transition: all 0.25s;
}
.vc-pip.ready {
  background: var(--gold);
  box-shadow: 0 0 6px rgba(200,146,58,0.5);
}
.vc-pip.rec {
  background: #b43c3c;
  box-shadow: 0 0 7px rgba(180,60,60,0.5);
  animation: recPulse 1s ease-in-out infinite;
}
.vc-txt {
  font-family: 'IM Fell English SC', serif;
  font-size: 12px;
  color: var(--fade);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: 0.5px;
}
.vc-btn {
  width: 38px; height: 38px;
  border-radius: 50%;
  background: rgba(122,92,58,0.07);
  border: 1px solid var(--edge);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s;
}
.voice-capsule:hover .vc-btn { background: rgba(122,92,58,0.13); border-color: var(--edge2); }
.vc-icon {
  font-size: 13px;
  color: var(--rust);
}

.voice-area-mobile { display: none; }
.mobile-hint-bar   { display: none; }
.mobile-next-btn   { display: none; }

.archive-panel {
  width: 400px;
  background: var(--parch);
  border-left: 1px solid var(--edge);
  display: flex;
  flex-direction: column;
  position: relative;
}
.archive-panel::before {
  content: '';
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 3px;
  background: linear-gradient(90deg, transparent, rgba(139,69,19,0.25), transparent);
}

.archive-scroll {
  height: 100%;
  overflow-y: auto;
}
.archive-scroll::-webkit-scrollbar { width: 5px; }
.archive-scroll::-webkit-scrollbar-track { background: rgba(122,92,58,0.05); }
.archive-scroll::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, var(--rust), var(--gold));
  border-radius: 4px;
}

.archive-header {
  padding: 28px 28px 22px;
  border-bottom: 1px dashed var(--edge);
  position: relative;
  text-align: center;
}
.ah-ornament {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.ah-rule-l, .ah-rule-r {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--edge2));
}
.ah-rule-r { background: linear-gradient(90deg, var(--edge2), transparent); }
.ah-wax { flex-shrink: 0; opacity: 0.7; }
.ah-title {
  display: block;
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 700;
  color: var(--ink);
  letter-spacing: 0.5px;
}
.ah-sub {
  display: block;
  font-family: 'IM Fell English SC', serif;
  font-size: 10px;
  color: var(--dust);
  letter-spacing: 2.5px;
  margin-top: 5px;
}
.ah-stamp {
  display: inline-block;
  margin-top: 10px;
  padding: 3px 10px;
  border: 1.5px solid rgba(139,69,19,0.3);
  border-radius: 2px;
  transform: rotate(-2deg);
}
.ah-stamp-txt {
  font-family: 'IM Fell English SC', serif;
  font-size: 9px;
  color: rgba(139,69,19,0.5);
  letter-spacing: 3px;
}
.mobile-back-btn { display: none; }

.archive-body {
  padding: 22px 28px;
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.archive-section { display: flex; flex-direction: column; gap: 14px; }
.section-head {
  display: flex;
  align-items: center;
  gap: 10px;
}
.sh-num {
  font-family: 'IM Fell English SC', serif;
  font-size: 10px;
  color: var(--rust);
  letter-spacing: 1px;
  flex-shrink: 0;
  opacity: 0.8;
}
.sh-label {
  font-family: 'Playfair Display', serif;
  font-size: 13px;
  font-weight: 700;
  color: var(--ink);
  flex-shrink: 0;
  letter-spacing: 0.3px;
}
.sh-rule {
  flex: 1;
  height: 1px;
  background: var(--edge);
}

.field-entry {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.field-entry.half { flex: 1; min-width: 0; }
.fe-label-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.fe-label {
  font-family: 'Lora', serif;
  font-size: 12px;
  font-weight: 600;
  color: var(--fade);
}
.fe-en {
  font-family: 'IM Fell English SC', serif;
  font-size: 9px;
  color: var(--dust);
  letter-spacing: 1.5px;
  opacity: 0.7;
}

.fe-input-wrap {
  position: relative;
  background: rgba(245,240,228,0.6);
  border: 1px solid var(--edge);
  border-radius: 4px;
  padding: 10px 12px;
  display: flex;
  align-items: center;
  gap: 0;
  transition: border-color 0.2s, background 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 0 rgba(255,255,255,0.7) inset;
}
.fe-input-wrap:focus-within {
  border-color: var(--edge2);
  background: rgba(245,240,228,0.9);
  box-shadow: 0 1px 0 rgba(255,255,255,0.7) inset, 0 0 0 2px rgba(122,92,58,0.07);
}
.fe-input-wrap.fe-filled {
  border-color: rgba(122,92,58,0.28);
  background: rgba(245,240,228,0.85);
}
.fe-input {
  flex: 1;
  font-family: 'Playfair Display', serif;
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
  min-width: 0;
}
.fe-check {
  font-size: 11px;
  color: var(--gold);
  flex-shrink: 0;
  margin-left: 6px;
}

.year-trigger {
  display: flex;
  align-items: center;
  gap: 3px;
  flex-shrink: 0;
  cursor: pointer;
}
.year-val {
  font-family: 'IM Fell English SC', serif;
  font-size: 13px;
  color: var(--rust);
  font-weight: 700;
  white-space: nowrap;
}
.year-caret { font-size: 9px; color: var(--rust); opacity: 0.7; }
.fe-divider {
  width: 1px;
  height: 14px;
  background: var(--edge);
  margin: 0 8px;
  flex-shrink: 0;
}
.year-manual {
  font-size: 13px !important;
  flex: 1;
  min-width: 0;
  font-family: 'Lora', serif !important;
}
.fe-select {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
  width: 100%;
}
.fe-select-val {
  font-family: 'Lora', serif;
  font-size: 13px;
  color: var(--ink);
  flex: 1;
}
.fe-select-caret { font-size: 10px; color: var(--dust); }

.field-row-duo {
  display: flex;
  gap: 12px;
}

.tag-field {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  background: rgba(245,240,228,0.5);
  border: 1px dashed rgba(122,92,58,0.22);
  border-radius: 5px;
  padding: 12px 13px;
  box-shadow: 0 1px 0 rgba(255,255,255,0.6) inset;
}
.tag-pill {
  display: flex;
  align-items: center;
  gap: 3px;
  padding: 5px 10px;
  background: rgba(139,69,19,0.08);
  border: 1px solid rgba(139,69,19,0.2);
  border-radius: 100px;
}
.tp-hash {
  font-family: 'IM Fell English SC', serif;
  font-size: 11px;
  color: var(--rust);
  opacity: 0.6;
}
.tp-txt {
  font-family: 'IM Fell English SC', serif;
  font-size: 11px;
  color: var(--warm);
  letter-spacing: 0.5px;
}
.tp-rm {
  font-size: 14px;
  color: var(--dust);
  cursor: pointer;
  margin-left: 2px;
  line-height: 1;
  transition: color 0.15s;
}
.tp-rm:hover { color: var(--rust); }
.tag-input {
  font-family: 'Lora', serif;
  font-size: 12px;
  color: var(--fade);
  flex: 1;
  min-width: 72px;
}

.upload-model-btn {
  background: rgba(245,240,228,0.6);
  border: 2px dashed var(--edge);
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 8px;
}

.upload-model-btn:hover {
  background: rgba(245,240,228,0.8);
  border-color: var(--edge2);
}

.upload-icon {
  font-size: 24px;
  margin-bottom: 8px;
  color: var(--rust);
}

.upload-text {
  display: block;
  font-family: 'Playfair Display', serif;
  font-size: 14px;
  color: var(--ink);
  margin-bottom: 4px;
}

.upload-hint {
  display: block;
  font-family: 'Lora', serif;
  font-size: 11px;
  color: var(--dust);
}

.model-file-info {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(245,240,228,0.8);
  border: 1px solid var(--edge);
  border-radius: 6px;
  padding: 12px 14px;
  margin-bottom: 8px;
}

.file-icon {
  font-size: 16px;
  flex-shrink: 0;
  color: var(--gold);
}

.file-name {
  flex: 1;
  font-family: 'Lora', serif;
  font-size: 13px;
  color: var(--ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-action {
  font-family: 'IM Fell English SC', serif;
  font-size: 11px;
  color: var(--rust);
  cursor: pointer;
  letter-spacing: 0.5px;
  transition: color 0.2s;
}

.file-action:hover {
  color: #6B340F;
  text-decoration: underline;
}

.audio-player {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(245,240,228,0.8);
  border: 1px solid var(--edge);
  border-radius: 8px;
  padding: 12px 16px;
}

.audio-player-left {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(139,69,19,0.1);
  border: 1px solid rgba(139,69,19,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
  transition: all 0.2s;
}

.audio-player-left:hover {
  background: rgba(139,69,19,0.2);
  border-color: rgba(139,69,19,0.3);
}

.audio-play-icon {
  font-size: 16px;
  color: #8B4513;
  margin-left: 2px;
}

.audio-player-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.audio-time {
  display: flex;
  justify-content: space-between;
}

.audio-time-current, .audio-time-duration {
  font-family: 'IM Fell English SC', serif;
  font-size: 10px;
  color: var(--dust);
  letter-spacing: 0.5px;
}

.audio-player-right {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(180, 60, 60, 0.1);
  border: 1px solid rgba(180, 60, 60, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
  transition: all 0.2s;
}

.audio-player-right:hover {
  background: rgba(180, 60, 60, 0.2);
  border-color: rgba(180, 60, 60, 0.4);
}

.audio-delete-icon {
  font-size: 20px;
  color: #b43c3c;
  line-height: 1;
}

.story-field {
  position: relative;
  background: rgba(245,240,228,0.5);
  border: 1px solid var(--edge);
  border-radius: 5px;
  padding: 14px 15px;
  transition: border-color 0.2s, background 0.2s;
  box-shadow: 0 1px 0 rgba(255,255,255,0.7) inset;
}
.story-field:focus-within,
.story-field.sf-filled {
  border-color: rgba(122,92,58,0.3);
  background: rgba(245,240,228,0.82);
}
.sf-corner {
  position: absolute;
  width: 10px; height: 10px;
  opacity: 0.3;
}
.sf-corner.c1 {
  top: 0; right: 0;
  border-top: 2px solid var(--rust);
  border-right: 2px solid var(--rust);
}
.sf-corner.c2 {
  bottom: 0; left: 0;
  border-bottom: 2px solid var(--rust);
  border-left: 2px solid var(--rust);
}
.sf-textarea {
  width: 100%;
  height: 140px;
  font-family: 'Lora', serif;
  font-size: 13px;
  color: var(--fade);
  line-height: 2.0;
}

.archive-footer {
  padding: 4px 28px 36px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
}
.af-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}
.afd-line {
  flex: 1;
  height: 1px;
  background: var(--edge);
}
.afd-seal { opacity: 0.4; }

.submit-btn {
  width: 100%;
  border-radius: 5px;
  background: linear-gradient(135deg, #6B340F 0%, var(--rust) 50%, #9B5520 100%);
  cursor: pointer;
  overflow: hidden;
  position: relative;
  transition: all 0.25s;
  box-shadow:
    0 1px 0 rgba(255,255,255,0.12) inset,
    3px 6px 0 rgba(61,43,26,0.18),
    6px 12px 20px rgba(61,43,26,0.1);
}
.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow:
    0 1px 0 rgba(255,255,255,0.12) inset,
    4px 8px 0 rgba(61,43,26,0.2),
    8px 16px 28px rgba(61,43,26,0.14);
}
.submit-btn:active { transform: translateY(0); }
.sb-tape {
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 4px;
  background: repeating-linear-gradient(
    90deg,
    rgba(255,255,255,0.07) 0px, rgba(255,255,255,0.07) 6px,
    transparent 6px, transparent 14px
  );
}
.sb-inner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 16px 0;
}
.sb-txt {
  font-family: 'Playfair Display', serif;
  font-size: 15px;
  font-weight: 700;
  color: #FAF7EF;
  letter-spacing: 1px;
}
.af-hint {
  font-family: 'IM Fell English SC', serif;
  font-size: 10px;
  color: var(--dust);
  letter-spacing: 1px;
  text-align: center;
}

@keyframes fadeUpIn {
  from { opacity: 0; transform: translateX(-50%) translateY(8px); }
  to   { opacity: 1; transform: translateX(-50%) translateY(0); }
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0.4; }
}
@keyframes recPulse {
  0%   { box-shadow: 0 0 4px rgba(180,60,60,0.4); }
  50%  { box-shadow: 0 0 10px rgba(180,60,60,0.7); }
  100% { box-shadow: 0 0 4px rgba(180,60,60,0.4); }
}
@keyframes wave-bar {
  0%, 100% { transform: scaleY(0.45); opacity: 0.4; }
  50%       { transform: scaleY(1);    opacity: 1;   }
}

@media (max-width: 768px) {
  .relic-editor {
    position: relative;
    overflow: hidden;
    height: 100%;
  }
  .stage-section {
    position: absolute; inset: 0;
    padding: 12px 14px;
    display: flex; flex-direction: column; gap: 10px;
    transform: translateX(0); opacity: 1;
    transition: transform 0.38s cubic-bezier(0.16,1,0.3,1), opacity 0.28s;
    z-index: 1; will-change: transform;
    overflow-y: auto;
  }
  .relic-editor.step-form .stage-section {
    transform: translateX(-100%); opacity: 0; pointer-events: none;
  }
  .mobile-hint-bar {
    display: flex; align-items: flex-start; gap: 10px; flex-shrink: 0;
    background: rgba(245,240,228,0.7);
    border: 1px solid var(--edge);
    border-radius: 6px; padding: 11px 13px;
  }
  .hint-seal { flex-shrink: 0; margin-top: 2px; }
  .hint-text-wrap { display: flex; flex-direction: column; gap: 3px; }
  .hint-title {
    font-family: 'Playfair Display', serif;
    font-size: 13px; font-weight: 700; color: var(--ink);
  }
  .hint-desc {
    font-family: 'Lora', serif;
    font-size: 11px; color: var(--fade); line-height: 1.5;
  }
  .stage-frame {
    flex-shrink: 0;
    height: 54vw; min-height: 210px; max-height: 300px;
  }
  .desktop-voice { display: none !important; }
  .model-status {
    margin-top: 8px;
    padding: 6px 10px;
  }
  .status-text { font-size: 11px; }
  .status-action { font-size: 10px; }
  .voice-area-mobile {
    display: flex; flex-direction: column; gap: 8px; flex-shrink: 0;
  }
  .vam-header {
    display: flex; align-items: center; gap: 10px;
  }
  .vam-rule {
    flex: 1; height: 1px; background: var(--edge);
  }
  .vam-label {
    font-family: 'IM Fell English SC', serif;
    font-size: 10px; color: var(--dust); letter-spacing: 2px;
    flex-shrink: 0;
  }
  .voice-area-mobile .voice-capsule {
    position: static; transform: none;
    width: 100%; max-width: none; height: 46px;
  }
  .mobile-audio-play {
    display: flex; align-items: center; gap: 12px; flex-shrink: 0;
    background: rgba(245,240,228,0.7);
    border: 1px solid var(--edge); border-radius: 5px;
    padding: 10px 14px; cursor: pointer;
    transition: background 0.2s;
  }
  .mobile-audio-play:active { background: rgba(245,240,228,0.95); }
  .map-waves {
    display: flex; align-items: center; gap: 2px; flex-shrink: 0;
  }
  .map-wave {
    width: 3px; border-radius: 2px;
    background: rgba(122,92,58,0.45);
    animation: wave-bar 1.2s ease-in-out infinite;
  }
  .map-wave:nth-child(1) { height: 7px;  animation-delay: 0s;    }
  .map-wave:nth-child(2) { height: 12px; animation-delay: 0.1s;  }
  .map-wave:nth-child(3) { height: 8px;  animation-delay: 0.2s;  }
  .map-wave:nth-child(4) { height: 14px; animation-delay: 0.15s; }
  .map-wave:nth-child(5) { height: 7px;  animation-delay: 0.05s; }
  .map-label {
    flex: 1;
    font-family: 'IM Fell English SC', serif;
    font-size: 12px; color: var(--fade); letter-spacing: 0.5px;
  }
  .map-play-btn {
    width: 30px; height: 30px; border-radius: 50%; flex-shrink: 0;
    background: rgba(139,69,19,0.09);
    border: 1px solid rgba(139,69,19,0.22);
    display: flex; align-items: center; justify-content: center;
  }
  .play-text-icon {
    font-size: 13px;
    color: #8B4513;
    line-height: 1;
    margin-top: -1px;
  }
  .mobile-next-btn {
    display: flex; flex-shrink: 0;
    width: 100%; height: 50px;
    margin-top: 2px; margin-bottom: 4px;
    background: linear-gradient(135deg, #6B340F 0%, var(--rust) 60%, #9B5520 100%);
    border-radius: 5px;
    align-items: center; justify-content: center; gap: 10px;
    cursor: pointer;
    box-shadow: 3px 6px 0 rgba(61,43,26,0.2), 6px 12px 20px rgba(61,43,26,0.1);
    position: relative; overflow: hidden;
  }
  .mnb-seal { flex-shrink: 0; }
  .mnb-txt {
    font-family: 'Playfair Display', serif;
    font-size: 14px; font-weight: 700; color: #FAF7EF;
    letter-spacing: 0.5px;
  }
  .mnb-arrow { font-size: 16px; color: rgba(245,240,228,0.6); }
  .archive-panel {
    position: absolute; inset: 0;
    width: 100%; border-left: none;
    transform: translateX(100%); opacity: 0; pointer-events: none;
    transition: transform 0.38s cubic-bezier(0.16,1,0.3,1), opacity 0.28s;
    z-index: 2; will-change: transform;
  }
  .relic-editor.step-form .archive-panel {
    transform: translateX(0); opacity: 1; pointer-events: auto;
  }
  .mobile-back-btn {
    display: flex; align-items: center; gap: 8px;
    margin-bottom: 14px; cursor: pointer;
  }
  .back-arrow {
    font-size: 18px; color: var(--rust);
    line-height: 1;
  }
  .back-label {
    font-family: 'IM Fell English SC', serif;
    font-size: 13px; color: var(--rust); letter-spacing: 1px;
  }
  .archive-header { padding: 14px 18px 14px; }
  .archive-body   { padding: 16px 18px; gap: 22px; }
  .archive-footer { padding: 4px 18px 32px; }
  .field-row-duo  { flex-direction: column; gap: 12px; }
  .sf-textarea    { height: 100px; }
  .upload-model-btn { padding: 15px; }
  .upload-icon { font-size: 20px; }
  .upload-text { font-size: 12px; }
  .upload-hint { font-size: 10px; }
  .model-file-info { padding: 10px 12px; }
  .file-name { font-size: 12px; }
  .audio-player { padding: 10px 12px; }
  .audio-player-left, .audio-player-right { width: 30px; height: 30px; }
  .audio-play-icon { font-size: 14px; }
  .audio-delete-icon { font-size: 18px; }
}
</style>