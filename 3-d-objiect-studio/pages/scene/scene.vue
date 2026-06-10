<template>
	<common-layout :pageTitle="scenePageTitle">
		<!-- 展馆首页 -->
		<view v-if="view === 'hub'" class="scene-hub">
			<!-- 装饰顶栏 -->
			<view class="scene-rail">
				<text class="rail-left">兴趣社群展馆计划</text>
				<text class="rail-mid">个人策展 · 兴趣社群 · 超级博物馆</text>
				<text class="rail-right">策展周期 {{ curDate }}</text>
			</view>

			<!-- 页面印信 / 日期邮戳 -->
			<view class="scene-stamp-row">
				<view class="scene-postmark">
					<view class="postmark-ring"></view>
					<view class="postmark-ring inner"></view>
					<text class="postmark-title">YIFANGLOU</text>
					<text class="postmark-date">{{ curDate }}</text>
					<text class="postmark-sub">CURATOR · STUDIO</text>
				</view>
				<view class="scene-kicker-block">
					<text class="scene-kicker-en">VOLUME VII · SELECT YOUR HALL</text>
					<text class="scene-kicker-cn">场景选择 · 为每一种热爱留一间展厅</text>
					<text class="scene-kicker-quote">「垂直兴趣的爱好者在此选择底图、摆放藏品、留下语音考据。」</text>
				</view>
				<view class="scene-ticket">
					<view class="ticket-hole"></view>
					<view class="ticket-body">
						<text class="ticket-kicker">ADMIT ONE</text>
						<text class="ticket-name">策展人通道</text>
						<text class="ticket-sub">Gate · Studio</text>
						<view class="ticket-dash"></view>
						<text class="ticket-seat">No. {{ ticketNo }}</text>
					</view>
					<view class="ticket-hole right"></view>
				</view>
			</view>

			<view class="scene-editorial">
				<view class="scene-col scene-col-left">
					<text class="vol-tag">第七卷 · 数字馆藏入口</text>
					<view class="hero-title-stack">
						<text class="hero-h1">把热爱布置成</text>
						<text class="hero-h1 hero-h1-accent">一座属于自己的迷你博物馆</text>
					</view>
					<view class="hero-lead-block">
						<text class="lead-p">矿晶、模型、标本、老旧电子产品……垂直兴趣爱好者将藏品拍照建模，在虚拟展厅中按个人审美布展。</text>
						<text class="lead-p">每个展馆可配以语音解说与文字考据；数字广场上，无数主题小馆将连成一座可探索的「超级博物馆」。</text>
					</view>
					<view class="pill-row">
						<text class="pill">三维藏品</text>
						<text class="pill">个人布展</text>
						<text class="pill">社群参观</text>
					</view>
					<view class="stat-row">
						<view class="stat-cell">
							<text class="stat-num">13542</text>
							<text class="stat-label">馆藏数量</text>
						</view>
						<view class="stat-cell">
							<text class="stat-num">虚拟</text>
							<text class="stat-label">展陈方式</text>
						</view>
						<view class="stat-cell">
							<text class="stat-num">互访</text>
							<text class="stat-label">社群模式</text>
						</view>
					</view>
				</view>

				<view class="scene-col scene-col-center">
					<text class="blueprint-kicker">PLAN · BLUEPRINT</text>
					<text class="blueprint-title">个人展馆蓝图</text>
					<view class="blueprint-frame">
						<svg class="bp-svg" viewBox="0 0 220 220" xmlns="http://www.w3.org/2000/svg">
							<circle cx="110" cy="110" r="98" fill="none" stroke="rgba(139,115,85,0.35)" stroke-width="0.8"/>
							<circle cx="110" cy="110" r="72" fill="none" stroke="rgba(139,115,85,0.25)" stroke-width="0.6" stroke-dasharray="4 3"/>
							<circle cx="110" cy="110" r="44" fill="none" stroke="rgba(200,146,58,0.4)" stroke-width="0.7"/>
							<line x1="110" y1="12" x2="110" y2="208" stroke="rgba(139,115,85,0.2)" stroke-width="0.6"/>
							<line x1="12" y1="110" x2="208" y2="110" stroke="rgba(139,115,85,0.2)" stroke-width="0.6"/>
							<line x1="40" y1="40" x2="180" y2="180" stroke="rgba(139,115,85,0.12)" stroke-width="0.5"/>
							<line x1="180" y1="40" x2="40" y2="180" stroke="rgba(139,115,85,0.12)" stroke-width="0.5"/>
							<text x="110" y="26" text-anchor="middle" fill="#9a8878" font-size="9" font-family="Georgia, serif">N</text>
							<text x="204" y="114" text-anchor="middle" fill="#9a8878" font-size="9" font-family="Georgia, serif">E</text>
							<text x="110" y="212" text-anchor="middle" fill="#9a8878" font-size="9" font-family="Georgia, serif">S</text>
							<text x="16" y="114" text-anchor="middle" fill="#9a8878" font-size="9" font-family="Georgia, serif">W</text>
							<circle cx="110" cy="110" r="4" fill="rgba(139,69,19,0.35)"/>
						</svg>
					</view>
					<text class="blueprint-cap">从影像采集、三维重建，到展厅布展与社群互访的完整策展动线。</text>
				</view>

				<view class="scene-col scene-col-right">
					<text class="cta-greet">欢迎回到你的策展工作台</text>
					<text class="cta-sub">一方阁 · 个人博物馆工作室</text>
					<view class="cta-card">
						<text class="cta-card-t">先为藏品建模，再进入虚拟展厅完成布展。</text>
						<view class="cta-btn primary" @tap="goWorkshop">
							<text>进入建模工坊</text>
							<text class="cta-arrow">›</text>
						</view>
						<view class="cta-btn ghost" @tap="goFloorPlan">
							<text>我已建模 · 选择展厅户型</text>
						</view>
					</view>
					<text class="cta-quote">「每一种兴趣，都值得一座被认真布置的小型展馆。」</text>
				</view>
			</view>

			<!-- 策展方向：垂直兴趣爱好者 4 大主题 -->
			<view class="scene-section-label">
				<view class="sec-rule"></view>
				<text class="sec-kicker">CURATORIAL DIRECTIONS</text>
				<text class="sec-text">策展方向 · 四种热爱</text>
				<view class="sec-rule"></view>
			</view>
			<text class="section-caption">为矿晶、模型、标本、老物——四种垂直热爱，各备一间主题数字展馆。</text>

			<view class="vertical-grid">
				<view v-for="v in verticals" :key="v.key" class="vertical-card" @tap="goFloorPlan">
					<view class="vert-cover" :style="{ background: v.bg }">
						<text class="vert-emoji">{{ v.icon }}</text>
						<view class="vert-tape"></view>
					</view>
					<view class="vert-body">
						<view class="vert-heads">
							<text class="vert-en">{{ v.en }}</text>
							<text class="vert-cn">{{ v.cn }}</text>
						</view>
						<text class="vert-desc">{{ v.desc }}</text>
						<view class="vert-meta">
							<text class="vert-chip">{{ v.chip }}</text>
							<text class="vert-link">选一间展厅 ›</text>
						</view>
					</view>
					<view class="vert-corner"></view>
				</view>
			</view>

			<!-- 应用能力：从影像到社群 -->
			<view class="scene-section-label">
				<view class="sec-rule"></view>
				<text class="sec-kicker">THE CRAFT</text>
				<text class="sec-text">应用能力 · 从快门到展厅</text>
				<view class="sec-rule"></view>
			</view>

			<view class="capability-list">
				<view v-for="(cap, idx) in capabilities" :key="cap.key" class="cap-row">
					<view class="cap-num">
						<text class="cap-num-t">0{{ idx + 1 }}</text>
						<text class="cap-num-sub">STEP</text>
					</view>
					<view class="cap-rule"></view>
					<view class="cap-body">
						<view class="cap-heads">
							<text class="cap-ico">{{ cap.icon }}</text>
							<text class="cap-cn">{{ cap.cn }}</text>
							<text class="cap-en">{{ cap.en }}</text>
						</view>
						<text class="cap-desc">{{ cap.desc }}</text>
					</view>
					<view class="cap-stamp"><text class="cap-stamp-t">{{ cap.tag }}</text></view>
				</view>
			</view>

			<!-- 展馆入口流程条 -->
			<view class="scene-section-label">
				<view class="sec-rule"></view>
				<text class="sec-kicker">WORKFLOW</text>
				<text class="sec-text">策展流程与展馆入口</text>
				<view class="sec-rule"></view>
			</view>
			<view class="flow-line">
				<text class="flow-i">藏品建模</text>
				<text class="flow-arrow">→</text>
				<text class="flow-i on">展厅户型</text>
				<text class="flow-arrow">→</text>
				<text class="flow-i">展馆布展</text>
				<text class="flow-arrow">→</text>
				<text class="flow-i">展馆方案</text>
			</view>

			<view class="card-grid">
				<view class="scene-card" @tap="goFloorPlan">
					<view class="card-visual floor">
						<view class="mini-room r1"></view>
						<view class="mini-room r2"></view>
					</view>
					<text class="card-h">展厅户型</text>
					<text class="card-p">为矿晶打光、为标本留台、为老物造境——择一间虚拟空间作为展场基底。</text>
					<view class="card-cta">进入</view>
				</view>

				<view class="scene-card" @tap="goPlacement">
					<view class="card-visual place">
						<text class="emoji">💎</text>
						<text class="emoji e2">🦋</text>
						<text class="emoji e3">📻</text>
					</view>
					<text class="card-h">展馆布展</text>
					<text class="card-p">从藏品模型库摆上展品，设计动线与视觉节奏，预留解说与考据位。</text>
					<view class="card-cta">进入</view>
				</view>

				<view class="scene-card" @tap="goSchemes">
					<view class="card-visual schemes">
						<view class="binder"></view>
						<view class="binder b2"></view>
					</view>
					<text class="card-h">展馆方案</text>
					<text class="card-p">归档布展方案，便于发布至广场，邀同好串门参观、交流考据。</text>
					<view class="card-cta">进入</view>
				</view>
			</view>

			<!-- 页脚 ephemera：邮票 + 签名 + 语录 -->
			<view class="ephemera-strip">
				<view class="eph-col eph-stamps">
					<view class="eph-stamp s1">
						<text class="stamp-v">MUSEUM</text>
						<text class="stamp-num">¥ 0.8</text>
						<view class="stamp-perf"></view>
					</view>
					<view class="eph-stamp s2">
						<text class="stamp-v">CURATOR</text>
						<text class="stamp-num">POSTAGE</text>
						<view class="stamp-perf"></view>
					</view>
				</view>
				<view class="eph-col eph-quote">
					<text class="eph-q-mark">“</text>
					<text class="eph-q-body">每一种兴趣，都值得一座被认真布置的小型展馆；每一件藏品，都值得被郑重讲述。</text>
					<text class="eph-q-sign">—— 一方阁 · 策展人手记</text>
				</view>
				<view class="eph-col eph-sign">
					<text class="eph-sign-en">Yours in curation,</text>
					<text class="eph-sign-hand">Yifanglou</text>
					<view class="eph-sign-rule"></view>
					<text class="eph-sign-addr">No. 7 Studio Lane · Archive Vol. VII</text>
				</view>
			</view>
		</view>

		<!-- 展厅户型 -->
		<view v-else-if="view === 'floor'" class="fp-page">
			<view class="fp-nav">
				<view class="fp-back" @tap="fpGoBack">
					<text class="fp-back-arrow">‹</text>
					<text class="fp-back-txt">返回展馆</text>
				</view>
			</view>

			<view class="fp-intro">
				<text class="fp-kicker">STEP 1</text>
				<text class="fp-title">选一间展厅空间</text>
				<text class="fp-desc">每种户型示意图与下方布展底图一一对应；标注为参考净尺寸（单位 m），面积与平面示意一致。</text>
			</view>

			<view class="fp-grid">
				<view
					v-for="(room, idx) in rooms"
					:key="idx"
					:class="['fp-cell', selectedId === room.id ? 'selected' : '']"
					@tap="selectRoom(room)"
				>
					<view class="fp-thumb">
						<!-- 缩略图几何与布展页 floor-inner clip-path 一一对应 -->
						<svg v-if="room.shape === 'shape-l'" class="fp-svg" viewBox="0 0 120 90" xmlns="http://www.w3.org/2000/svg">
							<path d="M6 6 L114 6 L114 46 L60 46 L60 84 L6 84 Z" fill="#f8f4ec" stroke="#8b7355" stroke-width="1.2"/>
							<line x1="60" y1="6" x2="60" y2="46" stroke="#c4b498" stroke-width="0.9" stroke-dasharray="3 2"/>
							<line x1="6" y1="46" x2="114" y2="46" stroke="#c4b498" stroke-width="0.7" stroke-dasharray="3 2"/>
						</svg>
						<svg v-else-if="room.shape === 'shape-tri'" class="fp-svg" viewBox="0 0 120 90" xmlns="http://www.w3.org/2000/svg">
							<path d="M60 6 L112 84 L8 84 Z" fill="#f5f0e8" stroke="#8b7355" stroke-width="1.2"/>
							<line x1="60" y1="6" x2="60" y2="70" stroke="#c4b498" stroke-width="0.8" stroke-dasharray="2 2"/>
							<rect x="42" y="58" width="36" height="22" fill="#ddd4c8" stroke="#9a8878" stroke-width="0.8" rx="1"/>
						</svg>
						<svg v-else-if="room.shape === 'shape-bed'" class="fp-svg" viewBox="0 0 120 90" xmlns="http://www.w3.org/2000/svg">
							<rect x="10" y="12" width="100" height="66" rx="8" fill="#f8f4ec" stroke="#8b7355" stroke-width="1.2"/>
							<rect x="22" y="22" width="32" height="24" rx="2" fill="#e8dfd4" stroke="#b8a898"/>
							<ellipse cx="78" cy="48" rx="22" ry="14" fill="#e0d6cc" stroke="#a89880" stroke-width="0.8"/>
						</svg>
						<svg v-else class="fp-svg" viewBox="0 0 120 90" xmlns="http://www.w3.org/2000/svg">
							<path d="M8 22 L112 10 L114 68 L6 82 Z" fill="#f5f0e8" stroke="#8b7355" stroke-width="1.2"/>
							<line x1="32" y1="18" x2="34" y2="78" stroke="#c4b498" stroke-width="0.6" stroke-dasharray="4 3"/>
							<line x1="60" y1="14" x2="62" y2="74" stroke="#c4b498" stroke-width="0.6" stroke-dasharray="4 3"/>
							<line x1="88" y1="12" x2="90" y2="72" stroke="#c4b498" stroke-width="0.6" stroke-dasharray="4 3"/>
						</svg>
					</view>
					<text class="fp-name">{{ room.name }}</text>
					<text class="fp-dim">{{ room.dimLine }}</text>
					<text class="fp-meta">{{ room.area }} · {{ room.light }}</text>
				</view>
			</view>

			<view class="fp-footer">
				<button class="fp-btn ghost" @tap="fpGoBack">返回</button>
				<button class="fp-btn primary" :disabled="!selectedId" @tap="goPlaceFromFloor">
					下一步 · 展馆布展
				</button>
			</view>
		</view>

		<!-- 展馆布展 -->
		<view v-else-if="view === 'placement'" class="pl-page">
			<view class="pl-nav">
				<view class="pl-back" @tap="plGoBack">
					<text class="pl-back-arrow">‹</text>
					<text class="pl-back-txt">返回</text>
				</view>
				<text class="pl-nav-hint">鼠标左键拖动旋转 · 右键平移 · 滚轮缩放</text>
			</view>

			<view class="pl-bar">
				<view class="pl-room-pill">
					<text class="pill-label">当前展厅</text>
					<text class="pill-name">{{ roomName }}</text>
					<text v-if="roomDimLine" class="pill-dim">{{ roomDimLine }}</text>
				</view>
				<text class="pl-hint" @tap="goPickRoom">更换户型</text>
			</view>

			<view class="pl-drag-hint">
				<text class="pl-drag-hint-txt">真 3D 展厅：鼠标左键按住可环绕查看 · 右键平移 · 滚轮缩放 · 单击地面放置所选藏品</text>
			</view>

			<view class="pl-stage">
				<view class="pl-three-wrap">
					<text class="pl-3d-badge">3D 展厅</text>
					<view id="three-placement-stage" class="three-stage"></view>
					<view v-if="!threeReady" class="three-loading">
						<text class="three-loading-txt">正在加载 3D 展厅…</text>
					</view>
					<view class="three-overlay">
						<view class="three-hud">
							<text class="three-hud-k">已布置</text>
							<text class="three-hud-v">{{ placed.length }} 件</text>
						</view>
						<view class="three-hud">
							<text class="three-hud-k">展厅</text>
							<text class="three-hud-v">{{ roomLenM }}m × {{ roomWidM }}m</text>
						</view>
						<view class="three-hud btn" @tap="resetPlacementCamera">
							<text class="three-hud-v">重置视角</text>
						</view>
					</view>
				</view>
				<text class="pl-caption">已切换为真实 3D 展厅：地面长∶宽 = 展厅「长∶宽」米数比。请先在下方选中藏品，再单击地面放置；单击已放置藏品可撤回。再次点同一藏品可取消选中。</text>
			</view>

			<view v-if="placementExitModalVisible" class="pl-modal-mask" @tap="closePlacementExitModal">
				<view class="pl-modal" @tap.stop>
					<text class="pl-modal-title">离开布展</text>
					<text class="pl-modal-body">当前布展尚未保存，是否保存后再离开？</text>
					<view class="pl-modal-actions">
						<button class="pl-modal-btn ghost" @tap="closePlacementExitModal">继续编辑</button>
						<button class="pl-modal-btn danger" @tap="confirmExitPlacementDiscard">不保存</button>
						<button class="pl-modal-btn primary" @tap="confirmExitPlacementSave">保存并离开</button>
					</view>
				</view>
			</view>

			<view class="pl-lib">
				<text class="lib-title">藏品模型库</text>
				<text class="lib-sub">选中后点击展厅地面放置 · 以下为兴趣主题示意</text>
				<scroll-view scroll-x class="lib-scroll" :show-scrollbar="false">
					<view class="lib-row">
						<view
							v-for="m in library"
							:key="m.id"
							:class="['lib-chip', activeId === m.id ? 'on' : '']"
							@tap="pickModel(m)"
						>
							<text class="lc-ico">{{ m.icon }}</text>
							<text class="lc-name">{{ m.name }}</text>
						</view>
					</view>
				</scroll-view>
			</view>

			<view class="pl-actions">
				<button class="pl-btn ghost" @tap="clearAll">清空展厅</button>
				<button class="pl-btn primary" @tap="saveScheme">保存展馆方案</button>
			</view>
		</view>

		<!-- 展馆方案 -->
		<view v-else-if="view === 'schemes'" class="sch-page">
			<view class="sch-nav">
				<view class="sch-back" @tap="schGoBack">
					<text class="sch-back-arrow">‹</text>
					<text class="sch-back-txt">返回展馆</text>
				</view>
			</view>
			<view class="sch-intro">
				<text class="sch-kicker">MINI MUSEUM ARCHIVE</text>
				<text class="sch-title">迷你馆方案册</text>
				<text class="sch-desc">保存布展与解说脉络，便于发布到兴趣社群；广场上的同好可串门参观，无数主题小馆连成一座「超级博物馆」（分享能力示意）。</text>
			</view>

			<view v-if="schemes.length === 0" class="sch-empty">
				<text class="empty-ico">🏛️</text>
				<text class="empty-t">还没有展馆方案</text>
				<text class="empty-p">在「展馆布展」中完成摆放并保存，即可在此归档，日后对接语音解说与文字考据。</text>
				<button class="sch-btn" @tap="schGoPlace">去布展</button>
			</view>

			<view v-else class="sch-list">
				<view v-for="s in schemes" :key="s.id" class="sch-card">
					<view class="sch-thumb-wrap">
						<text class="sch-emoji">{{ s.thumb }}</text>
					</view>
					<view class="sch-body">
						<text class="sch-card-title">{{ s.title }}</text>
						<text class="sch-meta">{{ s.room }} · {{ s.items }} 件展品</text>
						<text class="sch-time">{{ formatDate(s.created) }}</text>
					</view>
					<view class="sch-ops">
						<text class="op" @tap="shareMock(s)">分享</text>
					</view>
				</view>
			</view>
		</view>
	</common-layout>
</template>

<script>
import MyLayout from '@/components/common-layout/common-layout.vue';
// #ifdef H5
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
// #endif

let _uid = 0;
let _lastPointerTs = 0;

const SHAPE_BY_ROOM_ID = {
	studio: 'shape-l',
	study: 'shape-tri',
	bedroom: 'shape-bed',
	courtyard: 'shape-open'
};

const ROOMS = [
	{
		id: 'studio',
		name: '通透明厅',
		shape: 'shape-l',
		area: '约 24㎡',
		areaM2: 24,
		/** 地面外接矩形：长×宽（m），决定 3D 地面像素长宽比 */
		lenM: 6.0,
		widM: 4.0,
		heightM: 3.2,
		dimLine: '长 6.0m · 宽 4.0m · 净空高 3.2m',
		light: '适合矿晶打光'
	},
	{
		id: 'study',
		name: '标本阁楼',
		shape: 'shape-tri',
		kind: 'pitch',
		area: '约 16㎡',
		areaM2: 16,
		lenM: 8.0,
		widM: 4.0,
		heightM: 4.2,
		eaveM: 2.4,
		dimLine: '底边 8.0m · 进深 4.0m · 檐口 2.4m · 峰高 4.2m',
		light: '台面与立面'
	},
	{
		id: 'bedroom',
		name: '怀旧展室',
		shape: 'shape-bed',
		area: '约 18㎡',
		areaM2: 18,
		lenM: 4.5,
		widM: 4.0,
		heightM: 2.8,
		dimLine: '长 4.5m · 宽 4.0m · 净空高 2.8m',
		light: '老电子与器物'
	},
	{
		id: 'courtyard',
		name: '静阅长廊',
		shape: 'shape-open',
		area: '约 20㎡',
		areaM2: 20,
		lenM: 10.0,
		widM: 2.0,
		heightM: 3.0,
		dimLine: '长 10.0m · 宽 2.0m · 净空高 3.0m',
		light: '图文与解说动线'
	}
];

/**
 * 展厅户型轮廓：以 clip-path 百分比给出 (x%, y%)（y% 沿屏幕向下）。
 * 会被统一换算到世界坐标 (worldX, worldZ)，保证 3D 地面形状与户型缩略图一致。
 */
const SHAPE_OUTLINES = {
	'shape-l': [
		[0, 0],
		[100, 0],
		[100, 51],
		[50, 51],
		[50, 100],
		[0, 100]
	],
	'shape-tri': [
		[50, 0],
		[100, 100],
		[0, 100]
	],
	'shape-bed': [
		[0, 0],
		[100, 0],
		[100, 100],
		[0, 100]
	],
	'shape-open': [
		[3, 24],
		[94, 3],
		[97, 76],
		[2, 100]
	]
};

function dimLineByRoomId(id) {
	const row = ROOMS.find((r) => r.id === id);
	return row ? row.dimLine : '';
}

function footprintByRoomId(id) {
	const row = ROOMS.find((r) => r.id === id);
	if (row)
		return {
			lenM: row.lenM,
			widM: row.widM,
			heightM: row.heightM || 3.0,
			kind: row.kind || 'flat',
			eaveM: row.eaveM || null
		};
	return { lenM: 4.5, widM: 4.0, heightM: 3.0, kind: 'flat', eaveM: null };
}

/** 判断世界坐标 (x,z) 是否在指定户型轮廓内（基于给定 L×W 的包围） */
function pointInShape(shapeId, x, z, L, W) {
	const outline = SHAPE_OUTLINES[shapeId] || SHAPE_OUTLINES['shape-bed'];
	// 将轮廓的 (px%,py%) 映射到世界 (wx,wz)：wx=(px-50)/100*L, wz=(py-50)/100*W
	const pts = outline.map(([px, py]) => [((px - 50) / 100) * L, ((py - 50) / 100) * W]);
	let inside = false;
	for (let i = 0, j = pts.length - 1; i < pts.length; j = i++) {
		const xi = pts[i][0],
			zi = pts[i][1];
		const xj = pts[j][0],
			zj = pts[j][1];
		const intersect = zi > z !== zj > z && x < ((xj - xi) * (z - zi)) / (zj - zi || 1e-9) + xi;
		if (intersect) inside = !inside;
	}
	return inside;
}

export default {
	components: { 'common-layout': MyLayout },
	data() {
		return {
			view: 'hub',
			placementFromFloor: false,
			curDate: '',
			rooms: ROOMS,
			selectedId: '',
			roomName: '尚未选择展厅',
			roomDimLine: '',
			roomLenM: 4.5,
			roomWidM: 4.0,
			roomHeightM: 3.0,
			roomShape: 'shape-bed',
			/** 结构类型：flat = 平顶；pitch = 坡屋顶（山墙朝上三角形） */
			roomKind: 'flat',
			/** 仅坡屋顶有效：檐口高度 */
			roomEaveM: null,
			activeId: '',
			activeIcon: '📦',
			activeName: '',
			library: [
				{ id: 'm1', name: '矿晶标本', icon: '💎' },
				{ id: 'm2', name: '手作模型', icon: '🎨' },
				{ id: 'm3', name: '昆虫标本', icon: '🦋' },
				{ id: 'm4', name: '怀旧电子', icon: '📻' },
				{ id: 'm5', name: '展签说明', icon: '📜' },
				{ id: 'm6', name: '我的藏品', icon: '✦' }
			],
			placed: [],
			schemes: [],
			/** 地面绕中心旋转角（度），展品随地面一起转；点击时用逆变换算本地百分比 */
			floorRotationDeg: 0,
			_lastPlacementRoomId: '',
			/** 拖动旋转手势 */
			floorGesture: {
				active: false,
				startX: 0,
				startY: 0,
				lastX: 0,
				lastY: 0,
				isRotating: false,
				startTime: 0
			},
			/** 是否有未保存的布展改动（展品增删） */
			placementDirty: false,
			placementExitModalVisible: false,
			pendingExitAction: null,
			_h5Drag: false,
			/** Three.js 场景是否已就绪 */
			threeReady: false,
			/** 入场券编号（随进入 hub 生成，展示用） */
			ticketNo: '0000',
			/** 垂直兴趣四大策展方向 */
			verticals: [
				{
					key: 'crystal',
					cn: '矿晶收藏',
					en: 'Minerals & Crystals',
					icon: '💎',
					desc: '萤石、方解石、水晶簇……为每一枚矿晶打光，记下产地、化学式与收得之日。',
					chip: '打光 · 旋转 · 矿物档案',
					bg: 'linear-gradient(150deg, #eadfcb 0%, #d9c9ac 100%)'
				},
				{
					key: 'model',
					cn: '模型制作',
					en: 'Scale Models',
					icon: '🏯',
					desc: '高达、古建、汽车比例模型……360° 展陈作品，记录工序、涂装与合成配件。',
					chip: '工序 · 涂装 · 合成件',
					bg: 'linear-gradient(150deg, #e6dcca 0%, #c9b493 100%)'
				},
				{
					key: 'insect',
					cn: '昆虫标本',
					en: 'Insect Specimens',
					icon: '🦋',
					desc: '鳞翅目、鞘翅目……为每一只标本配考据卡与拉丁学名，复刻博物柜陈列感。',
					chip: '学名 · 产地 · 采集日',
					bg: 'linear-gradient(150deg, #ece2ce 0%, #d0be9d 100%)'
				},
				{
					key: 'retro',
					cn: '老旧电子',
					en: 'Vintage Electronics',
					icon: '📻',
					desc: '胶片机、老收音机、随身听、拨号电话……把昭和年代的按键与霓虹留在展柜里。',
					chip: '年代 · 品牌 · 口述往事',
					bg: 'linear-gradient(150deg, #e8dec9 0%, #c6b393 100%)'
				}
			],
			/** 应用能力：一条完整的策展工序 */
			capabilities: [
				{
					key: 'capture',
					cn: '影像采集 · AI 建模',
					en: 'Photo → 3D Model',
					icon: '📷',
					tag: 'CAPTURE',
					desc: '把藏品多角度拍照上传，系统自动重建带纹理的 3D 模型，保留每一道岁月的痕迹。'
				},
				{
					key: 'staging',
					cn: '虚拟布展 · 动线节奏',
					en: 'Virtual Staging',
					icon: '🏛',
					tag: 'STAGE',
					desc: '在真 3D 展厅中为藏品选位，拖动查看，调整灯光与相邻节奏，像排一册私藏图录。'
				},
				{
					key: 'audio',
					cn: '语音解说 · 文字考据',
					en: 'Audio & Annotation',
					icon: '🎙',
					tag: 'NARRATE',
					desc: '为每件展品配一段语音讲解、一则手写考据，让逛展者听见你的偏爱与研究。'
				},
				{
					key: 'social',
					cn: '社群互访 · 超级博物馆',
					en: 'Community Visits',
					icon: '🚪',
					tag: 'VISIT',
					desc: '把迷你馆发布到数字广场，让同好串门参观、留言交流，众多小馆缀成一座超级博物馆。'
				}
			]
		};
	},
	created() {
		this._h5MoveHandler = (ev) => {
			this.onFloorGestureMove(ev);
		};
		this._h5UpHandler = (ev) => {
			this.onFloorGestureEnd(ev);
		};
	},
	beforeDestroy() {
		this.detachH5FloorDragListeners();
	},
	computed: {
		scenePageTitle() {
			const m = {
				hub: '一方阁 · 展馆',
				floor: '展厅户型',
				placement: '展馆布展',
				schemes: '展馆方案'
			};
			return m[this.view] || m.hub;
		},
		/** 地面「横向像素 : 纵向像素」= 长(m) : 宽(m)，避免 1m×3m 却画成横边更长 */
		floorInnerMetricStyle() {
			const l = Math.max(0.01, Number(this.roomLenM) || 4);
			const w = Math.max(0.01, Number(this.roomWidM) || 4);
			return {
				aspectRatio: `${l} / ${w}`
			};
		},
		floorRotateStyle() {
			return {
				transform: `rotateZ(${this.floorRotationDeg}deg)`,
				transformOrigin: 'center center'
			};
		}
	},
	onLoad(options) {
		const v = options && options.view;
		if (v === 'floor' || v === 'placement' || v === 'schemes') {
			this.view = v;
			if (v === 'floor') this.loadFloorSelection();
			if (v === 'placement') {
				this.syncPlacementRoom();
				this.$nextTick(() => this.initPlacementThree());
			}
			if (v === 'schemes') this.schemes = uni.getStorageSync('sceneSchemes') || [];
		}
	},
	onShow() {
		if (this.view === 'placement') {
			this.syncPlacementRoom();
			this.$nextTick(() => this.initPlacementThree());
		}
		if (this.view === 'schemes') this.schemes = uni.getStorageSync('sceneSchemes') || [];
	},
	onHide() {
		this.disposePlacementThree();
	},
	onUnload() {
		this.disposePlacementThree();
	},
	mounted() {
		const d = new Date();
		const pad = (n) => (n < 10 ? '0' + n : '' + n);
		this.curDate = `${d.getFullYear()}.${pad(d.getMonth() + 1)}.${pad(d.getDate())}`;
		/** 随机生成一个 4 位入场券编号（仅展示用，保持 session 一致） */
		this.ticketNo = String(1000 + Math.floor(Math.random() * 8999));
		if (this.view === 'placement') {
			this.$nextTick(() => this.initPlacementThree());
		}
	},
	beforeDestroy() {
		this.disposePlacementThree();
	},
	watch: {
		view(nv, ov) {
			if (nv === 'placement') {
				this.$nextTick(() => this.initPlacementThree());
			} else if (ov === 'placement') {
				this.disposePlacementThree();
			}
		},
		placed: {
			deep: true,
			handler() {
				this.rebuildPlacementItems();
			}
		},
		roomLenM() {
			this.rebuildPlacementRoom();
		},
		roomWidM() {
			this.rebuildPlacementRoom();
		},
		roomHeightM() {
			this.rebuildPlacementRoom();
		},
		roomShape() {
			this.rebuildPlacementRoom();
		},
		roomKind() {
			this.rebuildPlacementRoom();
		},
		roomEaveM() {
			this.rebuildPlacementRoom();
		},
		activeId() {
			this.updatePlacementSelection();
		}
	},
	methods: {
		loadFloorSelection() {
			const prev = uni.getStorageSync('sceneSelectedRoom');
			if (prev && prev.id) this.selectedId = prev.id;
		},
		syncPlacementRoom() {
			const r = uni.getStorageSync('sceneSelectedRoom');
			if (r && r.name) this.roomName = r.name;
			this.roomDimLine = (r && r.dimLine) || (r && r.id ? dimLineByRoomId(r.id) : '');
			const fp = r && r.id ? footprintByRoomId(r.id) : { lenM: 4.5, widM: 4.0, heightM: 3.0 };
			if (r && typeof r.lenM === 'number' && typeof r.widM === 'number') {
				this.roomLenM = r.lenM;
				this.roomWidM = r.widM;
			} else {
				this.roomLenM = fp.lenM;
				this.roomWidM = fp.widM;
			}
			this.roomHeightM = (r && typeof r.heightM === 'number' ? r.heightM : fp.heightM) || 3.0;
			this.roomKind = (r && r.kind) || fp.kind || 'flat';
			this.roomEaveM = (r && typeof r.eaveM === 'number' ? r.eaveM : fp.eaveM) || null;
			if (r && r.shape) {
				this.roomShape = r.shape;
			} else if (r && r.id && SHAPE_BY_ROOM_ID[r.id]) {
				this.roomShape = SHAPE_BY_ROOM_ID[r.id];
			} else {
				this.roomShape = 'shape-bed';
			}
			const rid = r && r.id ? r.id : '';
			if (rid && rid !== this._lastPlacementRoomId) {
				this.floorRotationDeg = 0;
			}
			this._lastPlacementRoomId = rid;
			this.placementDirty = false;
		},
		goWorkshop() {
			uni.navigateTo({ url: '/pages/workshop/workshop' });
		},
		goFloorPlan() {
			this.view = 'floor';
			this.loadFloorSelection();
		},
		goPlacement() {
			this.placementFromFloor = false;
			this.view = 'placement';
			this.syncPlacementRoom();
		},
		goSchemes() {
			this.view = 'schemes';
			this.schemes = uni.getStorageSync('sceneSchemes') || [];
		},
		fpGoBack() {
			this.view = 'hub';
		},
		selectRoom(room) {
			this.selectedId = room.id;
			uni.setStorageSync('sceneSelectedRoom', {
				id: room.id,
				name: room.name,
				shape: room.shape,
				area: room.area,
				areaM2: room.areaM2,
				dimLine: room.dimLine,
				lenM: room.lenM,
				widM: room.widM,
				heightM: room.heightM,
				kind: room.kind || 'flat',
				eaveM: room.eaveM || null
			});
		},
		goPlaceFromFloor() {
			if (!this.selectedId) return;
			this.placementFromFloor = true;
			this.view = 'placement';
			this.syncPlacementRoom();
		},
		plGoBack() {
			this.requestExitPlacement('back');
		},
		goPickRoom() {
			this.requestExitPlacement('pickRoom');
		},
		requestExitPlacement(action) {
			if (!this.hasUnsavedPlacement()) {
				this._executeExitPlacement(action);
				return;
			}
			this.pendingExitAction = action;
			this.placementExitModalVisible = true;
		},
		hasUnsavedPlacement() {
			return this.placementDirty && this.placed.length > 0;
		},
		_executeExitPlacement(action) {
			if (action === 'back') {
				if (this.placementFromFloor) {
					this.view = 'floor';
					this.placementFromFloor = false;
				} else {
					this.view = 'hub';
				}
			} else if (action === 'pickRoom') {
				this.view = 'floor';
				this.loadFloorSelection();
			}
			this.placementExitModalVisible = false;
			this.pendingExitAction = null;
		},
		closePlacementExitModal() {
			this.placementExitModalVisible = false;
			this.pendingExitAction = null;
		},
		confirmExitPlacementDiscard() {
			const action = this.pendingExitAction;
			this.placed = [];
			this.placementDirty = false;
			this.floorRotationDeg = 0;
			this.placementExitModalVisible = false;
			this.pendingExitAction = null;
			if (action) this._executeExitPlacement(action);
		},
		confirmExitPlacementSave() {
			const action = this.pendingExitAction;
			this.placementExitModalVisible = false;
			this.pendingExitAction = null;
			this.persistScheme(false);
			this.placementDirty = false;
			if (action) this._executeExitPlacement(action);
		},
		stopFloorGesture() {},
		pickModel(m) {
			if (this.activeId === m.id) {
				this.activeId = '';
				this.activeIcon = '📦';
				this.activeName = '';
			} else {
				this.activeId = m.id;
				this.activeIcon = m.icon;
				this.activeName = m.name;
			}
		},
		eventTargetIsPlItem(e) {
			let el = e && (e.target || e.currentTarget);
			// #ifdef H5
			if (el && typeof el.closest === 'function') {
				return !!el.closest('.pl-item');
			}
			// #endif
			while (el) {
				const c = el.className;
				if (c && String(c).indexOf('pl-item') !== -1) return true;
				el = el.parentNode;
			}
			return false;
		},
		onFloorMouseDown(e) {
			// #ifdef H5
			if (e.button !== 0) return;
			this.onFloorGestureStart(e);
			// #endif
		},
		attachH5FloorDragListeners() {
			// #ifdef H5
			if (this._h5Drag || typeof document === 'undefined') return;
			this._h5Drag = true;
			document.addEventListener('mousemove', this._h5MoveHandler);
			document.addEventListener('mouseup', this._h5UpHandler);
			// #endif
		},
		detachH5FloorDragListeners() {
			// #ifdef H5
			if (!this._h5Drag || typeof document === 'undefined') return;
			document.removeEventListener('mousemove', this._h5MoveHandler);
			document.removeEventListener('mouseup', this._h5UpHandler);
			this._h5Drag = false;
			// #endif
		},
		onFloorGestureStart(e) {
			if (this.eventTargetIsPlItem(e)) return;
			const t = this.getPointerClientXY(e);
			if (!t) return;
			this.floorGesture = {
				active: true,
				startX: t.px,
				startY: t.py,
				lastX: t.px,
				lastY: t.py,
				isRotating: false,
				startTime: Date.now()
			};
			// #ifdef H5
			if (e.type === 'mousedown') {
				this.attachH5FloorDragListeners();
			}
			// #endif
		},
		onFloorGestureMove(e) {
			if (!this.floorGesture.active) return;
			const t = this.getPointerClientXY(e);
			if (!t) return;
			const dx = t.px - this.floorGesture.startX;
			const dy = t.py - this.floorGesture.startY;
			const dist = Math.sqrt(dx * dx + dy * dy);
			if (dist > 12) {
				this.floorGesture.isRotating = true;
			}
			if (this.floorGesture.isRotating) {
				const deltaX = t.px - this.floorGesture.lastX;
				this.floorGesture.lastX = t.px;
				this.floorGesture.lastY = t.py;
				this.floorRotationDeg = ((this.floorRotationDeg + deltaX * 0.55) % 360 + 360) % 360;
				if (e.preventDefault) e.preventDefault();
			}
		},
		onFloorGestureEnd(e) {
			if (!this.floorGesture.active) return;
			const g = { ...this.floorGesture };
			const dur = Date.now() - g.startTime;
			this.floorGesture = {
				active: false,
				startX: 0,
				startY: 0,
				lastX: 0,
				lastY: 0,
				isRotating: false,
				startTime: 0
			};
			this.detachH5FloorDragListeners();
			const t = this.getPointerClientXY(e);
			if (!t) return;
			const mx = t.px - g.startX;
			const my = t.py - g.startY;
			const moved = Math.sqrt(mx * mx + my * my) > 12;
			if (!g.isRotating && !moved && dur < 650 && this.activeId) {
				this.doPlaceAtPointer(e);
			}
		},
		/** 屏幕坐标相对未旋转地面中心的位移，逆旋转得到本地坐标（与 CSS rotateZ 一致） */
		screenDeltaToLocalPercent(px, py, rect, deg) {
			const W = rect.width;
			const H = rect.height;
			const cx = rect.left + W / 2;
			const cy = rect.top + H / 2;
			const dx = px - cx;
			const dy = py - cy;
			const rad = (deg * Math.PI) / 180;
			const cos = Math.cos(rad);
			const sin = Math.sin(rad);
			const localX = dx * cos + dy * sin;
			const localY = -dx * sin + dy * cos;
			if (Math.abs(localX) > W / 2 + 1 || Math.abs(localY) > H / 2 + 1) {
				return null;
			}
			return {
				x: ((localX + W / 2) / W) * 100,
				y: ((localY + H / 2) / H) * 100
			};
		},
		getPointerClientXY(e) {
			if (e.changedTouches && e.changedTouches[0]) {
				return { px: e.changedTouches[0].clientX, py: e.changedTouches[0].clientY };
			}
			if (e.touches && e.touches[0]) {
				return { px: e.touches[0].clientX, py: e.touches[0].clientY };
			}
			// #ifdef H5
			const raw = e && (e.nativeEvent || e.originalEvent || e);
			if (raw && typeof raw.clientX === 'number' && typeof raw.clientY === 'number') {
				return { px: raw.clientX, py: raw.clientY };
			}
			// #endif
			if (typeof e.clientX === 'number' && typeof e.clientY === 'number') {
				return { px: e.clientX, py: e.clientY };
			}
			return null;
		},
		doPlaceAtPointer(e) {
			const now = Date.now();
			if (now - _lastPointerTs < 45) return;
			_lastPointerTs = now;

			if (!this.activeId) {
				uni.showToast({ title: '请先在下方选一件藏品', icon: 'none' });
				return;
			}

			this.$nextTick(() => {
				uni.createSelectorQuery()
					.in(this)
					.select('#floorPhys')
					.boundingClientRect((rect) => {
						if (!rect || rect.width <= 0 || rect.height <= 0) return;

						let px;
						let py;
						const fromClient = this.getPointerClientXY(e);
						if (fromClient) {
							px = fromClient.px;
							py = fromClient.py;
						} else if (
							e.detail &&
							typeof e.detail.x === 'number' &&
							typeof e.detail.y === 'number' &&
							Math.abs(this.floorRotationDeg) < 0.5
						) {
							px = rect.left + e.detail.x;
							py = rect.top + e.detail.y;
						} else {
							return;
						}

						const pt = this.screenDeltaToLocalPercent(px, py, rect, this.floorRotationDeg);
						if (!pt) return;
						this.pushPlaced(pt.x, pt.y);
					})
					.exec();
			});
		},
		pushPlaced(x, y) {
			_uid += 1;
			this.placed.push({
				uid: 'p' + _uid + Date.now(),
				id: this.activeId,
				icon: this.activeIcon,
				name: this.activeName,
				x: Math.min(99, Math.max(1, x)),
				y: Math.min(99, Math.max(1, y))
			});
			this.placementDirty = true;
		},
		removePlaced(i) {
			this.placed.splice(i, 1);
			this.placementDirty = this.placed.length > 0;
		},
		clearAll() {
			this.placed = [];
			this.placementDirty = false;
		},
		persistScheme(navigateToSchemes) {
			const list = uni.getStorageSync('sceneSchemes') || [];
			const title = `${this.roomName} · ${this.formatNow()}`;
			list.unshift({
				id: 's' + Date.now(),
				title,
				thumb: this.placed[0] ? this.placed[0].icon : '🏠',
				room: this.roomName,
				items: this.placed.length,
				created: Date.now()
			});
			uni.setStorageSync('sceneSchemes', list);
			uni.showToast({ title: '已收入展馆方案', icon: 'none' });
			if (navigateToSchemes) {
				setTimeout(() => {
					this.view = 'schemes';
					this.schemes = uni.getStorageSync('sceneSchemes') || [];
				}, 400);
			}
		},
		saveScheme() {
			this.persistScheme(true);
			this.placementDirty = false;
		},
		formatNow() {
			const d = new Date();
			const m = d.getMinutes();
			return `${d.getMonth() + 1}月${d.getDate()}日 ${d.getHours()}:${m < 10 ? '0' : ''}${m}`;
		},
		schGoBack() {
			this.view = 'hub';
		},
		schGoPlace() {
			this.view = 'placement';
			this.syncPlacementRoom();
		},
		formatDate(ts) {
			const d = new Date(ts);
			return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`;
		},
		shareMock() {
			uni.showToast({ title: '展馆链接（示意）已复制，可发到广场与同好交流', icon: 'none' });
		},

		// ============================================================
		// Three.js 真 3D 展厅（H5 专用；App/小程序降级为占位）
		// ============================================================
		initPlacementThree() {
			// #ifdef H5
			if (typeof document === 'undefined') return;
			if (this._three && this._three.renderer) return;
			const host = document.getElementById('three-placement-stage');
			if (!host) {
				setTimeout(() => this.initPlacementThree(), 80);
				return;
			}
			const w = host.clientWidth || 600;
			const h = host.clientHeight || 420;

			const scene = new THREE.Scene();
			scene.background = new THREE.Color(0xe8ded0);
			scene.fog = new THREE.Fog(0xe8ded0, 18, 46);

			const camera = new THREE.PerspectiveCamera(48, w / h, 0.1, 200);

			const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: false });
			renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));
			renderer.setSize(w, h);
			renderer.shadowMap.enabled = true;
			renderer.shadowMap.type = THREE.PCFSoftShadowMap;
			renderer.outputColorSpace = THREE.SRGBColorSpace;
			host.innerHTML = '';
			host.appendChild(renderer.domElement);

			const hemi = new THREE.HemisphereLight(0xfff7e6, 0x7a6a55, 0.85);
			scene.add(hemi);
			const dir = new THREE.DirectionalLight(0xfff2d8, 1.1);
			dir.position.set(6, 12, 8);
			dir.castShadow = true;
			dir.shadow.mapSize.set(1024, 1024);
			dir.shadow.camera.left = -12;
			dir.shadow.camera.right = 12;
			dir.shadow.camera.top = 12;
			dir.shadow.camera.bottom = -12;
			dir.shadow.camera.near = 0.5;
			dir.shadow.camera.far = 40;
			scene.add(dir);
			const fill = new THREE.DirectionalLight(0xfffbe8, 0.25);
			fill.position.set(-6, 6, -4);
			scene.add(fill);

			const controls = new OrbitControls(camera, renderer.domElement);
			controls.enableDamping = true;
			controls.dampingFactor = 0.08;
			controls.minDistance = 3;
			controls.maxDistance = 30;
			controls.maxPolarAngle = Math.PI * 0.49;
			controls.minPolarAngle = Math.PI * 0.08;
			controls.target.set(0, 0.8, 0);

			const roomGroup = new THREE.Group();
			scene.add(roomGroup);
			const itemsGroup = new THREE.Group();
			scene.add(itemsGroup);

			const raycaster = new THREE.Raycaster();
			const pointer = new THREE.Vector2();
			let dragMoved = false;
			let rafId = 0;
			let disposed = false;

			this._three = {
				scene,
				camera,
				renderer,
				controls,
				roomGroup,
				itemsGroup,
				raycaster,
				pointer,
				floorMesh: null,
				itemMeshes: [],
				handlers: {},
				dispose: () => {
					disposed = true;
					cancelAnimationFrame(rafId);
					window.removeEventListener('resize', this._three.handlers.resize);
					controls.removeEventListener('start', this._three.handlers.controlsStart);
					controls.removeEventListener('end', this._three.handlers.controlsEnd);
					renderer.domElement.removeEventListener('click', this._three.handlers.click);
					controls.dispose();
					scene.traverse((o) => {
						if (o.geometry) o.geometry.dispose && o.geometry.dispose();
						if (o.material) {
							const mats = Array.isArray(o.material) ? o.material : [o.material];
							mats.forEach((m) => {
								if (m.map) m.map.dispose && m.map.dispose();
								m.dispose && m.dispose();
							});
						}
					});
					renderer.dispose();
					if (renderer.domElement && renderer.domElement.parentNode) {
						renderer.domElement.parentNode.removeChild(renderer.domElement);
					}
				}
			};

			const onResize = () => {
				if (disposed) return;
				const cw = host.clientWidth || w;
				const ch = host.clientHeight || h;
				camera.aspect = cw / ch;
				camera.updateProjectionMatrix();
				renderer.setSize(cw, ch);
			};
			const onControlsStart = () => {
				dragMoved = false;
			};
			const onControlsChange = () => {
				dragMoved = true;
			};
			controls.addEventListener('change', onControlsChange);
			const onControlsEnd = () => {
				setTimeout(() => {
					dragMoved = false;
				}, 60);
			};
			const onClick = (ev) => {
				if (dragMoved) return;
				this.onPlacementCanvasClick(ev);
			};
			this._three.handlers = {
				resize: onResize,
				controlsStart: onControlsStart,
				controlsEnd: onControlsEnd,
				change: onControlsChange,
				click: onClick
			};
			window.addEventListener('resize', onResize);
			controls.addEventListener('start', onControlsStart);
			controls.addEventListener('end', onControlsEnd);
			renderer.domElement.addEventListener('click', onClick);

			this.rebuildPlacementRoom();
			this.rebuildPlacementItems();
			this.resetPlacementCamera();

			const tick = () => {
				if (disposed) return;
				controls.update();
				this.updateWallVisibility();
				renderer.render(scene, camera);
				rafId = requestAnimationFrame(tick);
			};
			tick();
			this.threeReady = true;
			// #endif
		},
		/** 根据相机视角，将面朝相机的墙渐变为开口，其它墙恢复实体 */
		updateWallVisibility() {
			// #ifdef H5
			const t = this._three;
			if (!t || !t.walls || !t.walls.length) return;
			const cam = t.camera;
			const ctrlTarget = t.controls.target;
			const view = new THREE.Vector3().subVectors(cam.position, ctrlTarget);
			view.y = 0;
			if (view.lengthSq() < 1e-6) return;
			view.normalize();

			let maxDot = -Infinity;
			for (const w of t.walls) {
				const n = w.userData.outwardNormal;
				const d = n.x * view.x + n.z * view.z;
				w.userData._dot = d;
				if (d > maxDot) maxDot = d;
			}
			for (const w of t.walls) {
				const d = w.userData._dot;
				/** 目标不透明度：相机越正对（d 越大）越透明；始终保留至少 0 给开口效果 */
				let goal;
				if (d >= Math.max(0.35, maxDot - 0.05)) {
					goal = 0;
				} else if (d > 0.1) {
					goal = THREE.MathUtils.mapLinear(d, 0.1, 0.35, 0.9, 0.15);
				} else {
					goal = 1;
				}
				const mat = w.material;
				mat.opacity += (goal - mat.opacity) * 0.2;
				/** 完全透明时关闭写深度，让已放置物件在开口方向依旧可见；半透明时保留深度避免闪烁 */
				mat.depthWrite = mat.opacity > 0.7;
				w.visible = mat.opacity > 0.01;
			}
			// #endif
		},
		disposePlacementThree() {
			// #ifdef H5
			if (this._three && typeof this._three.dispose === 'function') {
				this._three.dispose();
			}
			this._three = null;
			this.threeReady = false;
			// #endif
		},
		rebuildPlacementRoom() {
			// #ifdef H5
			const t = this._three;
			if (!t) return;
			const L = Math.max(1, Number(this.roomLenM) || 4);
			const W = Math.max(1, Number(this.roomWidM) || 4);
			const H = Math.max(2, Number(this.roomHeightM) || 3);
			const kind = this.roomKind || 'flat';
			const eave =
				kind === 'pitch'
					? Math.min(
							Math.max(1.5, Number(this.roomEaveM) || H * 0.6),
							H - 0.4
					  )
					: null;
			/** pitch（坡屋顶）强制使用矩形底图；其他户型按 roomShape 构造 */
			const shapeId = kind === 'pitch' ? 'shape-bed' : this.roomShape || 'shape-bed';

			while (t.roomGroup.children.length) {
				const c = t.roomGroup.children.pop();
				if (c.geometry) c.geometry.dispose && c.geometry.dispose();
				if (c.material) {
					const mats = Array.isArray(c.material) ? c.material : [c.material];
					mats.forEach((m) => {
						if (m.map) m.map.dispose && m.map.dispose();
						m.dispose && m.dispose();
					});
				}
			}

			const outlinePct = SHAPE_OUTLINES[shapeId] || SHAPE_OUTLINES['shape-bed'];
			/** 在 shape XY 平面的 2D 轮廓点：x = (px-50)/100*L，y = -(py-50)/100*W
			 *  经 rotation.x=-PI/2 → 世界 (x, 0, (py-50)/100*W)，与放置坐标换算一致 */
			const shapePts2D = outlinePct.map(([px, py]) => [((px - 50) / 100) * L, -((py - 50) / 100) * W]);
			const threeShape = new THREE.Shape();
			threeShape.moveTo(shapePts2D[0][0], shapePts2D[0][1]);
			for (let i = 1; i < shapePts2D.length; i++) threeShape.lineTo(shapePts2D[i][0], shapePts2D[i][1]);
			threeShape.closePath();

			const floorGeo = new THREE.ShapeGeometry(threeShape);
			/** uv 归一到 [0,1]×[0,1]（按外接矩形），便于使用瓷砖贴图 */
			const posAttr = floorGeo.attributes.position;
			const uvs = new Float32Array(posAttr.count * 2);
			for (let i = 0; i < posAttr.count; i++) {
				const x = posAttr.getX(i);
				const y = posAttr.getY(i);
				uvs[i * 2] = x / L + 0.5;
				uvs[i * 2 + 1] = -y / W + 0.5;
			}
			floorGeo.setAttribute('uv', new THREE.BufferAttribute(uvs, 2));

			const floorTex = this._makeFloorTexture(L, W);
			const floorMat = new THREE.MeshStandardMaterial({
				map: floorTex,
				roughness: 0.85,
				metalness: 0.05,
				polygonOffset: true,
				polygonOffsetFactor: -1,
				polygonOffsetUnits: -1
			});
			const floor = new THREE.Mesh(floorGeo, floorMat);
			floor.rotation.x = -Math.PI / 2;
			floor.position.y = 0;
			floor.receiveShadow = true;
			floor.name = 'placement-floor';
			t.roomGroup.add(floor);
			t.floorMesh = floor;

			/** 地面外轮廓加一条勒边线（LineLoop），立体感更强且避免与地面 Z 冲突 */
			const outlinePts3D = [];
			for (const [x, z] of shapePts2D.map(([sx, sy]) => [sx, -sy])) {
				outlinePts3D.push(new THREE.Vector3(x, 0.005, z));
			}
			outlinePts3D.push(outlinePts3D[0].clone());
			const outlineGeo = new THREE.BufferGeometry().setFromPoints(outlinePts3D);
			const outlineMat = new THREE.LineBasicMaterial({ color: 0x7a5f42 });
			const outlineLine = new THREE.Line(outlineGeo, outlineMat);
			t.roomGroup.add(outlineLine);

			t.walls = [];
			const pts3D = shapePts2D.map(([sx, sy]) => [sx, -sy]);

			if (kind === 'pitch') {
				/** 坡屋顶（山墙朝上）：两面长边墙为矩形到檐口高；两面端山墙为五边形（矩形 + 三角）
				 *  约定：ridge 沿 X 轴，在 z=0，y=peak；长边墙位于 z=±W/2，端山墙位于 x=±L/2 */
				this._buildPitchWalls(t, pts3D, L, W, H, eave);
				this._buildPitchRoof(t, L, W, H, eave);
				this._addPitchDimensionLabels(t.roomGroup, L, W, H, eave);
			} else {
				/** 平顶：沿轮廓每条边构造一面矩形墙，每面独立材质，便于单独淡出 */
				for (let i = 0; i < pts3D.length; i++) {
					const [ax, az] = pts3D[i];
					const [bx, bz] = pts3D[(i + 1) % pts3D.length];
					const dx = bx - ax;
					const dz = bz - az;
					const len = Math.sqrt(dx * dx + dz * dz);
					if (len < 0.01) continue;
					const wallMat = new THREE.MeshStandardMaterial({
						color: 0xefe6d6,
						roughness: 0.9,
						metalness: 0,
						side: THREE.DoubleSide,
						transparent: true,
						opacity: 1,
						depthWrite: true
					});
					const wall = new THREE.Mesh(new THREE.PlaneGeometry(len, H), wallMat);
					wall.position.set((ax + bx) / 2, H / 2, (az + bz) / 2);
					/** PlaneGeometry 默认长边沿 +X、法线沿 +Z；将 +X 对齐到边方向 (dx,0,dz)，即 rotation.y = atan2(-dz, dx) */
					wall.rotation.y = Math.atan2(-dz, dx);
					wall.receiveShadow = true;
					wall.name = 'placement-wall-' + i;
					/** 外向法线 = 边方向绕 Y 轴旋转 +90° = (dz, 0, -dx)，对 CW 轮廓指向房间外 */
					const nx = dz / len;
					const nz = -dx / len;
					wall.userData.outwardNormal = new THREE.Vector3(nx, 0, nz);
					wall.userData.edge = { ax, az, bx, bz };
					t.roomGroup.add(wall);
					t.walls.push(wall);

					const topLineGeo = new THREE.BufferGeometry().setFromPoints([
						new THREE.Vector3(ax, H - 0.002, az),
						new THREE.Vector3(bx, H - 0.002, bz)
					]);
					const topLine = new THREE.Line(topLineGeo, new THREE.LineBasicMaterial({ color: 0x9b7e58 }));
					topLine.renderOrder = 1;
					t.roomGroup.add(topLine);
				}

				/** 天花板：用与地面相同的 ShapeGeometry + 同方向旋转，footprint 完全与地面重合，避免镜像错位 */
				const ceilGeo = floorGeo.clone();
				const ceilMat = new THREE.MeshStandardMaterial({
					color: 0xf7eedd,
					roughness: 0.95,
					side: THREE.DoubleSide,
					polygonOffset: true,
					polygonOffsetFactor: -1,
					polygonOffsetUnits: -1
				});
				const ceil = new THREE.Mesh(ceilGeo, ceilMat);
				ceil.rotation.x = -Math.PI / 2;
				ceil.position.y = H - 0.005;
				t.roomGroup.add(ceil);

				this._addRoomDimensionLabels(t.roomGroup, L, W, H);
			}

			this.rebuildPlacementItems();
			// #endif
		},
		/** 为坡屋顶户型构建 4 面墙（2 面长矩形墙 + 2 面山墙五边形） */
		_buildPitchWalls(t, pts3D, L, W, H, eave) {
			// #ifdef H5
			const wallMatConfig = {
				color: 0xefe6d6,
				roughness: 0.9,
				metalness: 0,
				side: THREE.DoubleSide,
				transparent: true,
				opacity: 1,
				depthWrite: true
			};
			for (let i = 0; i < pts3D.length; i++) {
				const [ax, az] = pts3D[i];
				const [bx, bz] = pts3D[(i + 1) % pts3D.length];
				const dx = bx - ax;
				const dz = bz - az;
				const len = Math.sqrt(dx * dx + dz * dz);
				if (len < 0.01) continue;
				const isLongWall = Math.abs(dx) > Math.abs(dz);
				const nx = dz / len;
				const nz = -dx / len;
				let wall;
				if (isLongWall) {
					/** 长边墙（檐口墙）：矩形 L × eave，顶面水平 */
					const mat = new THREE.MeshStandardMaterial(wallMatConfig);
					wall = new THREE.Mesh(new THREE.PlaneGeometry(len, eave), mat);
					wall.position.set((ax + bx) / 2, eave / 2, (az + bz) / 2);
					wall.rotation.y = Math.atan2(-dz, dx);
				} else {
					/** 端山墙：五边形（矩形 + 顶部三角），ridge 位于 x=0（墙局部坐标）处高 H */
					const gableShape = new THREE.Shape();
					gableShape.moveTo(-len / 2, 0);
					gableShape.lineTo(len / 2, 0);
					gableShape.lineTo(len / 2, eave);
					gableShape.lineTo(0, H);
					gableShape.lineTo(-len / 2, eave);
					gableShape.closePath();
					const geo = new THREE.ShapeGeometry(gableShape);
					const mat = new THREE.MeshStandardMaterial(wallMatConfig);
					wall = new THREE.Mesh(geo, mat);
					wall.position.set((ax + bx) / 2, 0, (az + bz) / 2);
					wall.rotation.y = Math.atan2(-dz, dx);
				}
				wall.receiveShadow = true;
				wall.name = 'placement-wall-' + i;
				wall.userData.outwardNormal = new THREE.Vector3(nx, 0, nz);
				wall.userData.edge = { ax, az, bx, bz };
				t.roomGroup.add(wall);
				t.walls.push(wall);

				/** 长边墙顶棱 / 山墙檐口棱（沿水平方向） */
				const topY = isLongWall ? eave : eave;
				const topLineGeo = new THREE.BufferGeometry().setFromPoints([
					new THREE.Vector3(ax, topY - 0.002, az),
					new THREE.Vector3(bx, topY - 0.002, bz)
				]);
				const topLine = new THREE.Line(topLineGeo, new THREE.LineBasicMaterial({ color: 0x9b7e58 }));
				topLine.renderOrder = 1;
				t.roomGroup.add(topLine);
			}
			// #endif
		},
		/** 坡屋顶：两面斜顶 + 屋脊线（沿 X 方向，z=0，y=H） */
		_buildPitchRoof(t, L, W, H, eave) {
			// #ifdef H5
			const roofMat = new THREE.MeshStandardMaterial({
				color: 0x7a5a3a,
				roughness: 0.8,
				metalness: 0.05,
				side: THREE.DoubleSide
			});

			const makeSlope = (zEave) => {
				/** 从屋檐 (z=zEave, y=eave) 向屋脊 (z=0, y=H) 倾斜一块矩形板，横跨 x∈[-L/2, L/2] */
				const positions = new Float32Array([
					-L / 2, eave, zEave,
					L / 2, eave, zEave,
					L / 2, H, 0,
					-L / 2, H, 0
				]);
				const indices = [0, 1, 2, 0, 2, 3];
				const geo = new THREE.BufferGeometry();
				geo.setAttribute('position', new THREE.BufferAttribute(positions, 3));
				geo.setIndex(indices);
				geo.computeVertexNormals();
				const mesh = new THREE.Mesh(geo, roofMat);
				mesh.receiveShadow = true;
				mesh.castShadow = false;
				return mesh;
			};

			t.roomGroup.add(makeSlope(-W / 2));
			t.roomGroup.add(makeSlope(W / 2));

			/** 屋脊线（深色高光） */
			const ridgeGeo = new THREE.BufferGeometry().setFromPoints([
				new THREE.Vector3(-L / 2, H + 0.001, 0),
				new THREE.Vector3(L / 2, H + 0.001, 0)
			]);
			const ridge = new THREE.Line(ridgeGeo, new THREE.LineBasicMaterial({ color: 0x5c3f22 }));
			ridge.renderOrder = 1;
			t.roomGroup.add(ridge);

			/** 山墙顶棱线（斜边）和屋檐线 */
			const eaveColor = new THREE.LineBasicMaterial({ color: 0x9b7e58 });
			const gableEdgeL = new THREE.BufferGeometry().setFromPoints([
				new THREE.Vector3(-L / 2, eave, -W / 2),
				new THREE.Vector3(-L / 2, H, 0),
				new THREE.Vector3(-L / 2, eave, W / 2)
			]);
			const gableEdgeR = new THREE.BufferGeometry().setFromPoints([
				new THREE.Vector3(L / 2, eave, -W / 2),
				new THREE.Vector3(L / 2, H, 0),
				new THREE.Vector3(L / 2, eave, W / 2)
			]);
			t.roomGroup.add(new THREE.Line(gableEdgeL, eaveColor));
			t.roomGroup.add(new THREE.Line(gableEdgeR, eaveColor));
			// #endif
		},
		_addPitchDimensionLabels(group, L, W, H, eave) {
			// #ifdef H5
			const fmt = (v) => `${Number(v).toFixed(1)}m`;
			const lenLabel = this._makeDimSprite(`长 ${fmt(L)}`);
			lenLabel.scale.set(1.4, 0.4, 1);
			lenLabel.position.set(0, 0.18, W / 2 + 0.5);
			group.add(lenLabel);

			const widLabel = this._makeDimSprite(`宽 ${fmt(W)}`);
			widLabel.scale.set(1.4, 0.4, 1);
			widLabel.position.set(L / 2 + 0.5, 0.18, 0);
			group.add(widLabel);

			const eaveLabel = this._makeDimSprite(`檐口 ${fmt(eave)}`);
			eaveLabel.scale.set(1.6, 0.4, 1);
			eaveLabel.position.set(L / 2 + 0.5, eave / 2, W / 2 + 0.3);
			group.add(eaveLabel);

			const peakLabel = this._makeDimSprite(`峰高 ${fmt(H)}`);
			peakLabel.scale.set(1.6, 0.4, 1);
			peakLabel.position.set(L / 2 + 0.5, H, W / 2 + 0.3);
			group.add(peakLabel);

			const lineMat = new THREE.LineBasicMaterial({ color: 0x8b6e4a, transparent: true, opacity: 0.8 });
			const mkLine = (a, b) => {
				const g = new THREE.BufferGeometry().setFromPoints([a, b]);
				group.add(new THREE.Line(g, lineMat));
			};
			const yTick = 0.01;
			const off = 0.25;
			mkLine(new THREE.Vector3(-L / 2, yTick, W / 2 + off), new THREE.Vector3(L / 2, yTick, W / 2 + off));
			mkLine(new THREE.Vector3(L / 2 + off, yTick, -W / 2), new THREE.Vector3(L / 2 + off, yTick, W / 2));
			mkLine(new THREE.Vector3(L / 2 + off, 0, W / 2 + off), new THREE.Vector3(L / 2 + off, H, W / 2 + off));
			mkLine(
				new THREE.Vector3(L / 2 + off - 0.25, eave, W / 2 + off),
				new THREE.Vector3(L / 2 + off + 0.25, eave, W / 2 + off)
			);
			// #endif
		},
		/** 生成类似「长 6.0m」的透明底标签 */
		_makeDimSprite(text, options) {
			// #ifdef H5
			const w = 256;
			const h = 72;
			const cvs = document.createElement('canvas');
			cvs.width = w;
			cvs.height = h;
			const ctx = cvs.getContext('2d');
			ctx.clearRect(0, 0, w, h);
			ctx.fillStyle = (options && options.bg) || 'rgba(44, 34, 24, 0.72)';
			const r = 14;
			ctx.beginPath();
			ctx.moveTo(r, 0);
			ctx.lineTo(w - r, 0);
			ctx.quadraticCurveTo(w, 0, w, r);
			ctx.lineTo(w, h - r);
			ctx.quadraticCurveTo(w, h, w - r, h);
			ctx.lineTo(r, h);
			ctx.quadraticCurveTo(0, h, 0, h - r);
			ctx.lineTo(0, r);
			ctx.quadraticCurveTo(0, 0, r, 0);
			ctx.closePath();
			ctx.fill();
			ctx.strokeStyle = 'rgba(246,238,221,0.35)';
			ctx.lineWidth = 2;
			ctx.stroke();
			ctx.fillStyle = '#fbf4e2';
			ctx.font = '600 30px "Noto Serif SC","PingFang SC",serif';
			ctx.textAlign = 'center';
			ctx.textBaseline = 'middle';
			ctx.fillText(text, w / 2, h / 2 + 2);
			const tex = new THREE.CanvasTexture(cvs);
			tex.colorSpace = THREE.SRGBColorSpace;
			const mat = new THREE.SpriteMaterial({
				map: tex,
				transparent: true,
				depthTest: false,
				depthWrite: false
			});
			const sp = new THREE.Sprite(mat);
			sp.renderOrder = 999;
			return sp;
			// #endif
		},
		_addRoomDimensionLabels(group, L, W, H) {
			// #ifdef H5
			const fmt = (v) => `${Number(v).toFixed(1)}m`;
			const lenLabel = this._makeDimSprite(`长 ${fmt(L)}`);
			lenLabel.scale.set(1.4, 0.4, 1);
			lenLabel.position.set(0, 0.18, W / 2 + 0.5);
			group.add(lenLabel);

			const widLabel = this._makeDimSprite(`宽 ${fmt(W)}`);
			widLabel.scale.set(1.4, 0.4, 1);
			widLabel.position.set(L / 2 + 0.5, 0.18, 0);
			group.add(widLabel);

			const hLabel = this._makeDimSprite(`高 ${fmt(H)}`);
			hLabel.scale.set(1.4, 0.4, 1);
			hLabel.position.set(L / 2 + 0.5, H / 2, W / 2 + 0.3);
			group.add(hLabel);

			/** 对应的尺寸辅助线（细线），起止点与边对齐 */
			const lineMat = new THREE.LineBasicMaterial({ color: 0x8b6e4a, transparent: true, opacity: 0.8 });
			const mkLine = (a, b) => {
				const g = new THREE.BufferGeometry().setFromPoints([a, b]);
				const ln = new THREE.Line(g, lineMat);
				group.add(ln);
			};
			const yTick = 0.01;
			const off = 0.25;
			mkLine(new THREE.Vector3(-L / 2, yTick, W / 2 + off), new THREE.Vector3(L / 2, yTick, W / 2 + off));
			mkLine(new THREE.Vector3(L / 2 + off, yTick, -W / 2), new THREE.Vector3(L / 2 + off, yTick, W / 2));
			mkLine(new THREE.Vector3(L / 2 + off, 0, W / 2 + off), new THREE.Vector3(L / 2 + off, H, W / 2 + off));
			// #endif
		},
		_makeFloorTexture(L, W) {
			// #ifdef H5
			const px = 512;
			const cvs = document.createElement('canvas');
			cvs.width = px;
			cvs.height = px;
			const ctx = cvs.getContext('2d');
			const grd = ctx.createLinearGradient(0, 0, px, px);
			grd.addColorStop(0, '#f5ecde');
			grd.addColorStop(1, '#d9cbb4');
			ctx.fillStyle = grd;
			ctx.fillRect(0, 0, px, px);
			ctx.strokeStyle = 'rgba(90,60,40,0.18)';
			ctx.lineWidth = 1;
			const cell = px / 8;
			for (let i = 0; i <= 8; i++) {
				ctx.beginPath();
				ctx.moveTo(i * cell, 0);
				ctx.lineTo(i * cell, px);
				ctx.stroke();
				ctx.beginPath();
				ctx.moveTo(0, i * cell);
				ctx.lineTo(px, i * cell);
				ctx.stroke();
			}
			ctx.strokeStyle = 'rgba(139,115,85,0.55)';
			ctx.lineWidth = 4;
			ctx.strokeRect(3, 3, px - 6, px - 6);
			const tex = new THREE.CanvasTexture(cvs);
			tex.wrapS = THREE.ClampToEdgeWrapping;
			tex.wrapT = THREE.ClampToEdgeWrapping;
			tex.colorSpace = THREE.SRGBColorSpace;
			return tex;
			// #endif
		},
		rebuildPlacementItems() {
			// #ifdef H5
			const t = this._three;
			if (!t) return;
			while (t.itemsGroup.children.length) {
				const c = t.itemsGroup.children.pop();
				c.traverse &&
					c.traverse((o) => {
						if (o.geometry) o.geometry.dispose && o.geometry.dispose();
						if (o.material) {
							const mats = Array.isArray(o.material) ? o.material : [o.material];
							mats.forEach((m) => {
								if (m.map) m.map.dispose && m.map.dispose();
								m.dispose && m.dispose();
							});
						}
					});
			}
			t.itemMeshes = [];
			const L = Math.max(1, Number(this.roomLenM) || 4);
			const W = Math.max(1, Number(this.roomWidM) || 4);
			this.placed.forEach((p, idx) => {
				const group = this._buildPlacementItemMesh(p);
				const px = (p.x / 100 - 0.5) * L;
				const pz = (p.y / 100 - 0.5) * W;
				group.position.set(px, 0, pz);
				group.userData = { placedIndex: idx, uid: p.uid };
				t.itemsGroup.add(group);
				t.itemMeshes.push(group);
			});
			// #endif
		},
		_buildPlacementItemMesh(p) {
			// #ifdef H5
			const group = new THREE.Group();
			const pedestalH = 0.5;
			const pedestal = new THREE.Mesh(
				new THREE.CylinderGeometry(0.28, 0.32, pedestalH, 24),
				new THREE.MeshStandardMaterial({ color: 0xc9b890, roughness: 0.55 })
			);
			pedestal.position.y = pedestalH / 2;
			pedestal.castShadow = true;
			pedestal.receiveShadow = true;
			group.add(pedestal);

			const top = new THREE.Mesh(
				new THREE.CylinderGeometry(0.3, 0.3, 0.04, 24),
				new THREE.MeshStandardMaterial({ color: 0x8b6e4a, roughness: 0.4 })
			);
			top.position.y = pedestalH + 0.02;
			top.castShadow = true;
			group.add(top);

			const iconSprite = new THREE.Sprite(
				new THREE.SpriteMaterial({
					map: this._makeEmojiTexture(p.icon || '📦'),
					transparent: true,
					depthWrite: false
				})
			);
			iconSprite.scale.set(0.7, 0.7, 0.7);
			iconSprite.position.y = pedestalH + 0.55;
			group.add(iconSprite);

			const labelSprite = new THREE.Sprite(
				new THREE.SpriteMaterial({
					map: this._makeLabelTexture(p.name || ''),
					transparent: true,
					depthWrite: false
				})
			);
			labelSprite.scale.set(1.2, 0.32, 1);
			labelSprite.position.y = pedestalH + 1.05;
			group.add(labelSprite);
			return group;
			// #endif
		},
		_makeEmojiTexture(ch) {
			// #ifdef H5
			const s = 128;
			const cvs = document.createElement('canvas');
			cvs.width = s;
			cvs.height = s;
			const ctx = cvs.getContext('2d');
			ctx.clearRect(0, 0, s, s);
			ctx.font = '92px "Segoe UI Emoji","Apple Color Emoji","Noto Color Emoji",serif';
			ctx.textAlign = 'center';
			ctx.textBaseline = 'middle';
			ctx.fillText(ch, s / 2, s / 2 + 6);
			const tex = new THREE.CanvasTexture(cvs);
			tex.colorSpace = THREE.SRGBColorSpace;
			return tex;
			// #endif
		},
		_makeLabelTexture(txt) {
			// #ifdef H5
			const w = 256;
			const h = 64;
			const cvs = document.createElement('canvas');
			cvs.width = w;
			cvs.height = h;
			const ctx = cvs.getContext('2d');
			ctx.clearRect(0, 0, w, h);
			ctx.fillStyle = 'rgba(44, 34, 24, 0.78)';
			const r = 12;
			ctx.beginPath();
			ctx.moveTo(r, 0);
			ctx.lineTo(w - r, 0);
			ctx.quadraticCurveTo(w, 0, w, r);
			ctx.lineTo(w, h - r);
			ctx.quadraticCurveTo(w, h, w - r, h);
			ctx.lineTo(r, h);
			ctx.quadraticCurveTo(0, h, 0, h - r);
			ctx.lineTo(0, r);
			ctx.quadraticCurveTo(0, 0, r, 0);
			ctx.closePath();
			ctx.fill();
			ctx.fillStyle = '#f6eedd';
			ctx.font = '600 26px "Noto Serif SC","PingFang SC",serif';
			ctx.textAlign = 'center';
			ctx.textBaseline = 'middle';
			const t = txt.length > 10 ? txt.slice(0, 9) + '…' : txt;
			ctx.fillText(t, w / 2, h / 2 + 2);
			const tex = new THREE.CanvasTexture(cvs);
			tex.colorSpace = THREE.SRGBColorSpace;
			return tex;
			// #endif
		},
		updatePlacementSelection() {
			// 占位：后续可通过在选中的藏品样式上高亮指示
		},
		onPlacementCanvasClick(ev) {
			// #ifdef H5
			const t = this._three;
			if (!t || !t.floorMesh) return;
			const rect = t.renderer.domElement.getBoundingClientRect();
			t.pointer.x = ((ev.clientX - rect.left) / rect.width) * 2 - 1;
			t.pointer.y = -((ev.clientY - rect.top) / rect.height) * 2 + 1;
			t.raycaster.setFromCamera(t.pointer, t.camera);

			if (t.itemMeshes.length) {
				const hitItems = t.raycaster.intersectObjects(t.itemMeshes, true);
				if (hitItems.length) {
					let obj = hitItems[0].object;
					while (obj && obj.parent !== t.itemsGroup) obj = obj.parent;
					if (obj && obj.userData && typeof obj.userData.placedIndex === 'number') {
						const uid = obj.userData.uid;
						const idx = this.placed.findIndex((p) => p.uid === uid);
						if (idx >= 0) this.removePlaced(idx);
						return;
					}
				}
			}

			const hit = t.raycaster.intersectObject(t.floorMesh, false);
			if (!hit.length) return;
			if (!this.activeId) {
				uni.showToast({ title: '请先在下方选一件藏品', icon: 'none' });
				return;
			}
			const now = Date.now();
			if (now - _lastPointerTs < 45) return;
			_lastPointerTs = now;
			const p = hit[0].point;
			const L = Math.max(1, Number(this.roomLenM) || 4);
			const W = Math.max(1, Number(this.roomWidM) || 4);
			const x = (p.x / L + 0.5) * 100;
			const y = (p.z / W + 0.5) * 100;
			this.pushPlaced(x, y);
			// #endif
		},
		resetPlacementCamera() {
			// #ifdef H5
			const t = this._three;
			if (!t) return;
			const L = Math.max(1, Number(this.roomLenM) || 4);
			const W = Math.max(1, Number(this.roomWidM) || 4);
			const H = Math.max(2, Number(this.roomHeightM) || 3);
			const diag = Math.sqrt(L * L + W * W);
			t.camera.position.set(diag * 0.85, Math.max(H * 0.9, diag * 0.65), diag * 1.05);
			t.controls.target.set(0, Math.min(1.2, H * 0.35), 0);
			t.controls.update();
			// #endif
		}
	}
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;600;700&family=Noto+Serif+SC:wght@400;500;600&display=swap');

/* ========== 展馆首页 ========== */
.scene-hub {
	min-height: 100%;
	padding: 8px 0 40px;
	font-family: 'Noto Serif SC', Georgia, 'Songti SC', serif;
	color: #2c2218;
}

.scene-rail {
	display: flex;
	align-items: center;
	justify-content: space-between;
	flex-wrap: wrap;
	gap: 10px 16px;
	padding: 12px 4px 20px;
	margin-bottom: 10px;
	border-bottom: 1px solid rgba(122, 92, 58, 0.14);
}
.rail-left,
.rail-mid,
.rail-right {
	font-size: 11px;
	letter-spacing: 0.1em;
	color: #8a7868;
}
.rail-mid {
	flex: 1;
	text-align: center;
	min-width: 200px;
	color: #6b5344;
	font-weight: 500;
}
.rail-right {
	font-variant-numeric: tabular-nums;
}

.scene-editorial {
	display: grid;
	grid-template-columns: 1.15fr 0.95fr 1fr;
	gap: 22px 20px;
	margin-bottom: 32px;
	align-items: start;
}

.scene-col-left {
	padding-right: 8px;
}

.vol-tag {
	display: block;
	font-size: 11px;
	letter-spacing: 0.18em;
	color: #a09080;
	margin-bottom: 12px;
}

.hero-title-stack {
	margin-bottom: 18px;
}

.hero-h1 {
	display: block;
	font-family: 'Playfair Display', Georgia, serif;
	font-size: clamp(22px, 2.6vw, 34px);
	font-weight: 600;
	line-height: 1.28;
	letter-spacing: 0.04em;
	color: #2c2218;
}

.hero-h1-accent {
	margin-top: 6px;
	color: #5c3d26;
}

.hero-lead-block {
	margin-bottom: 20px;
}

.lead-p {
	display: block;
	font-size: 14px;
	line-height: 1.85;
	color: #5c4a3a;
	margin-bottom: 12px;
	text-align: justify;
	text-justify: inter-ideograph;
}

.pill-row {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	margin-bottom: 22px;
}

.pill {
	padding: 8px 16px;
	border-radius: 999px;
	font-size: 11px;
	letter-spacing: 0.08em;
	color: #6b5344;
	background: rgba(250, 247, 242, 0.95);
	border: 1px solid rgba(139, 115, 85, 0.28);
	box-shadow: 0 2px 10px rgba(90, 60, 40, 0.06);
}

.stat-row {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 12px;
	padding-top: 18px;
	border-top: 1px dashed rgba(122, 92, 58, 0.2);
}

.stat-cell {
	text-align: center;
	padding: 10px 6px;
	background: rgba(245, 240, 232, 0.65);
	border-radius: 8px;
	border: 1px solid rgba(122, 92, 58, 0.1);
}

.stat-num {
	display: block;
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 17px;
	font-weight: 600;
	color: #8b4513;
	margin-bottom: 4px;
}

.stat-label {
	font-size: 9px;
	letter-spacing: 0.14em;
	color: #9a8878;
	text-transform: uppercase;
}

.scene-col-center {
	text-align: center;
	padding: 8px 12px 0;
}

.blueprint-kicker {
	display: block;
	font-size: 9px;
	letter-spacing: 0.28em;
	color: #c8923a;
	margin-bottom: 6px;
}

.blueprint-title {
	display: block;
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 18px;
	font-weight: 600;
	color: #4a3a2c;
	margin-bottom: 16px;
	letter-spacing: 0.06em;
}

.blueprint-frame {
	background: linear-gradient(165deg, #faf7f2 0%, #ede6dc 100%);
	border: 1px solid rgba(139, 115, 85, 0.2);
	border-radius: 12px;
	padding: 20px 16px;
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6), 0 8px 28px rgba(90, 60, 40, 0.08);
}

.bp-svg {
	width: 100%;
	max-width: 260px;
	height: auto;
	display: block;
	margin: 0 auto;
}

.blueprint-cap {
	display: block;
	margin-top: 14px;
	font-size: 11px;
	line-height: 1.7;
	color: #9a8878;
	padding: 0 4px;
}

.scene-col-right {
	padding: 12px 8px 0 12px;
	border-left: 1px dashed rgba(122, 92, 58, 0.15);
}

.cta-greet {
	display: block;
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 17px;
	font-weight: 600;
	color: #3d2b1a;
	margin-bottom: 6px;
	line-height: 1.4;
}

.cta-sub {
	display: block;
	font-size: 11px;
	color: #9a8878;
	letter-spacing: 0.06em;
	margin-bottom: 18px;
}

.cta-card {
	background: rgba(250, 247, 242, 0.98);
	border: 1px solid rgba(139, 115, 85, 0.22);
	border-radius: 12px;
	padding: 20px 18px 16px;
	box-shadow: 0 6px 24px rgba(90, 60, 40, 0.07);
}

.cta-card-t {
	display: block;
	font-size: 12px;
	line-height: 1.75;
	color: #7a6a58;
	margin-bottom: 16px;
}

.cta-btn {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8px;
	padding: 12px 16px;
	border-radius: 10px;
	margin-bottom: 10px;
	font-size: 13px;
	font-weight: 600;
	letter-spacing: 0.04em;
	transition: opacity 0.3s ease, transform 0.2s ease;
}

.cta-btn.primary {
	background: linear-gradient(180deg, #6b4423 0%, #4a2f18 100%);
	color: #faf7f2;
	box-shadow: 0 4px 16px rgba(60, 40, 25, 0.25);
}

.cta-btn.ghost {
	background: transparent;
	border: 1px dashed rgba(122, 92, 58, 0.35);
	color: #7a5c3a;
	margin-bottom: 0;
}

.cta-arrow {
	font-size: 18px;
	line-height: 1;
	opacity: 0.9;
}

.cta-quote {
	display: block;
	margin-top: 18px;
	font-size: 11px;
	font-style: italic;
	line-height: 1.75;
	color: #a09080;
	padding-left: 8px;
	border-left: 2px solid rgba(200, 146, 58, 0.45);
}

.scene-section-label {
	display: flex;
	align-items: center;
	gap: 14px;
	margin: 28px 0 14px;
}
.sec-rule {
	flex: 1;
	height: 1px;
	background: linear-gradient(90deg, transparent, rgba(122, 92, 58, 0.2), transparent);
}
.sec-text {
	font-family: 'Playfair Display', 'Noto Serif SC', serif;
	font-size: 12px;
	font-weight: 600;
	letter-spacing: 0.12em;
	color: #6b5344;
	white-space: nowrap;
}

.flow-line {
	display: flex;
	align-items: center;
	justify-content: center;
	flex-wrap: wrap;
	gap: 8px 10px;
	font-size: 12px;
	color: #9a8878;
	margin-bottom: 24px;
}

.flow-i {
	padding: 5px 12px;
	border-radius: 6px;
	background: rgba(245, 240, 232, 0.9);
	border: 1px dashed rgba(122, 92, 58, 0.2);
}

.flow-i.on {
	color: #8b4513;
	border-color: rgba(139, 69, 19, 0.35);
	background: rgba(139, 69, 19, 0.06);
}

.flow-arrow {
	color: #c8923a;
	font-size: 11px;
}

.card-grid {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
	gap: 20px;
}

.scene-card {
	background: linear-gradient(180deg, #fdfcfa 0%, #faf7f2 100%);
	border: 1px solid rgba(139, 115, 85, 0.18);
	border-radius: 12px;
	padding: 22px 20px 20px;
	box-shadow: 0 6px 22px rgba(90, 60, 40, 0.07);
	transition: transform 0.35s ease, box-shadow 0.35s ease;
}

.scene-card:active {
	transform: translateY(1px);
}

.card-visual {
	height: 108px;
	border-radius: 10px;
	margin-bottom: 16px;
	position: relative;
	overflow: hidden;
	background: #ede6dc;
	border: 1px solid rgba(122, 92, 58, 0.1);
}

.card-visual.floor {
	display: flex;
	gap: 10px;
	padding: 14px;
	align-items: stretch;
}

.mini-room {
	flex: 1;
	background: rgba(250, 247, 242, 0.95);
	border: 1px dashed rgba(139, 69, 19, 0.22);
	border-radius: 4px;
}

.mini-room.r2 {
	opacity: 0.88;
}

.card-visual.place {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 14px;
}

.emoji {
	font-size: 34px;
	filter: sepia(0.15);
}

.emoji.e2 {
	font-size: 30px;
	margin-top: 6px;
}

.emoji.e3 {
	font-size: 26px;
	align-self: flex-end;
}

.card-visual.schemes {
	display: flex;
	align-items: center;
	justify-content: center;
}

.binder {
	width: 38px;
	height: 58px;
	background: linear-gradient(90deg, #d4c4a8, #b8a892);
	border-radius: 2px;
	box-shadow: 3px 3px 0 rgba(90, 60, 40, 0.12);
	transform: rotate(-5deg);
}

.binder.b2 {
	transform: rotate(7deg);
	margin-left: -10px;
	opacity: 0.92;
}

.card-h {
	display: block;
	font-family: 'Playfair Display', 'Noto Serif SC', serif;
	font-size: 17px;
	font-weight: 600;
	margin-bottom: 8px;
	letter-spacing: 0.03em;
	color: #2c2218;
}

.card-p {
	display: block;
	font-size: 13px;
	line-height: 1.72;
	color: #7a6a58;
	margin-bottom: 16px;
	min-height: 64px;
}

.card-cta {
	display: inline-block;
	font-size: 11px;
	font-weight: 600;
	letter-spacing: 0.14em;
	color: #faf7f2;
	background: linear-gradient(180deg, #7a4e30 0%, #5c381f 100%);
	padding: 9px 20px;
	border-radius: 8px;
	box-shadow: 0 3px 12px rgba(90, 60, 40, 0.18);
}

/* ========== 印信行：邮戳 + 标题 + 入场券 ========== */
@keyframes postmark-rot {
	from { transform: rotate(0deg); }
	to   { transform: rotate(360deg); }
}
@keyframes paper-fade-in {
	from { opacity: 0; transform: translateY(6px); }
	to   { opacity: 1; transform: translateY(0); }
}

.scene-stamp-row {
	display: grid;
	grid-template-columns: auto 1fr auto;
	gap: 22px;
	align-items: center;
	padding: 18px 4px 22px;
	margin-bottom: 14px;
	border-bottom: 1px dashed rgba(122, 92, 58, 0.22);
	animation: paper-fade-in 500ms ease-out both;
}

.scene-postmark {
	position: relative;
	width: 104px;
	height: 104px;
	border-radius: 50%;
	flex-shrink: 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	transform: rotate(-8deg);
	background:
		radial-gradient(circle at 30% 30%, rgba(255, 250, 240, 0.6), transparent 60%),
		rgba(164, 80, 40, 0.05);
}
.postmark-ring {
	position: absolute; inset: 0; border-radius: 50%;
	border: 1.5px solid rgba(139, 60, 30, 0.55);
	mask-image: repeating-conic-gradient(#000 0 8deg, transparent 8deg 12deg);
	-webkit-mask-image: repeating-conic-gradient(#000 0 8deg, transparent 8deg 12deg);
}
.postmark-ring.inner {
	inset: 10px;
	border-width: 1px;
	border-color: rgba(139, 60, 30, 0.4);
	mask-image: none;
	-webkit-mask-image: none;
	border-style: dashed;
}
.postmark-title {
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 10px;
	letter-spacing: 0.18em;
	color: #8b3a1e;
	opacity: 0.85;
}
.postmark-date {
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 15px;
	font-weight: 600;
	color: #6d2c16;
	margin-top: 2px;
	font-variant-numeric: tabular-nums;
}
.postmark-sub {
	font-family: 'Playfair Display', Georgia, serif;
	font-style: italic;
	font-size: 8px;
	letter-spacing: 0.24em;
	color: #9a5a3a;
	margin-top: 4px;
	opacity: 0.8;
}

.scene-kicker-block {
	min-width: 0;
	padding: 0 6px;
	display: flex;
	flex-direction: column;
	gap: 6px;
}
.scene-kicker-en {
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 10px;
	letter-spacing: 0.34em;
	color: #a8906e;
}
.scene-kicker-cn {
	font-family: 'Noto Serif SC', Georgia, serif;
	font-size: 20px;
	font-weight: 600;
	letter-spacing: 0.06em;
	color: #3d2b1a;
	line-height: 1.35;
}
.scene-kicker-quote {
	font-family: 'Playfair Display', 'Noto Serif SC', serif;
	font-style: italic;
	font-size: 12px;
	color: #9a8878;
	line-height: 1.7;
}

.scene-ticket {
	position: relative;
	display: flex;
	align-items: stretch;
	width: 186px;
	min-height: 78px;
	background: linear-gradient(180deg, #faf5ea 0%, #efe4cb 100%);
	border: 1px solid rgba(139, 69, 19, 0.28);
	border-radius: 3px;
	box-shadow: 0 6px 16px rgba(90, 60, 40, 0.1);
	transform: rotate(2deg);
	overflow: hidden;
}
.scene-ticket::before {
	content: '';
	position: absolute;
	inset: 0;
	background-image: radial-gradient(rgba(139, 69, 19, 0.07) 1px, transparent 1px);
	background-size: 6px 6px;
	opacity: 0.55;
	pointer-events: none;
}
.ticket-hole {
	width: 12px;
	background:
		radial-gradient(circle at 0 50%, #ede4d3 0 6px, transparent 6.5px);
	flex-shrink: 0;
}
.ticket-hole.right {
	background: radial-gradient(circle at 100% 50%, #ede4d3 0 6px, transparent 6.5px);
}
.ticket-body {
	flex: 1;
	position: relative;
	padding: 10px 14px;
	display: flex;
	flex-direction: column;
	gap: 2px;
	border-left: 1px dashed rgba(139, 69, 19, 0.28);
	border-right: 1px dashed rgba(139, 69, 19, 0.28);
}
.ticket-kicker {
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 9px;
	letter-spacing: 0.24em;
	color: #8b4513;
}
.ticket-name {
	font-family: 'Noto Serif SC', Georgia, serif;
	font-size: 15px;
	font-weight: 600;
	color: #4a2d18;
	letter-spacing: 0.04em;
}
.ticket-sub {
	font-family: 'Playfair Display', Georgia, serif;
	font-style: italic;
	font-size: 10px;
	color: #9a8878;
}
.ticket-dash {
	height: 1px;
	margin: 4px 0 2px;
	background-image: linear-gradient(to right, rgba(139, 69, 19, 0.3) 50%, transparent 0);
	background-size: 5px 1px;
	background-repeat: repeat-x;
}
.ticket-seat {
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 10px;
	letter-spacing: 0.14em;
	color: #8b4513;
	font-variant-numeric: tabular-nums;
}

@media (max-width: 860px) {
	.scene-stamp-row {
		grid-template-columns: auto 1fr;
	}
	.scene-ticket {
		grid-column: 1 / -1;
		transform: none;
		width: 100%;
	}
}

/* ========== 分区标签（升级版）========== */
.sec-kicker {
	font-family: 'Playfair Display', Georgia, serif;
	font-style: italic;
	font-size: 9px;
	letter-spacing: 0.3em;
	color: #c8923a;
	text-transform: uppercase;
	margin-right: 2px;
}
.section-caption {
	display: block;
	text-align: center;
	font-size: 12px;
	color: #9a8878;
	letter-spacing: 0.05em;
	margin: -6px 0 18px;
	font-family: 'Noto Serif SC', 'Playfair Display', serif;
}

/* ========== 策展方向：四张垂直卡片 ========== */
.vertical-grid {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
	gap: 16px;
	margin-bottom: 28px;
}

.vertical-card {
	position: relative;
	background: linear-gradient(180deg, #fdfcfa 0%, #f6efe3 100%);
	border: 1px solid rgba(139, 115, 85, 0.22);
	border-radius: 10px;
	padding: 14px 14px 16px;
	box-shadow: 0 4px 14px rgba(90, 60, 40, 0.06), inset 0 0 0 1px rgba(255, 255, 255, 0.4);
	transition: transform 0.35s ease, box-shadow 0.35s ease, border-color 0.35s ease;
	cursor: pointer;
	overflow: hidden;
}
.vertical-card::before {
	content: '';
	position: absolute; inset: 0;
	background-image: radial-gradient(rgba(96, 68, 44, 0.05) 0.8px, transparent 0.8px);
	background-size: 14px 14px;
	opacity: 0.5;
	pointer-events: none;
}
.vertical-card:hover {
	transform: translateY(-2px);
	box-shadow: 0 10px 24px rgba(90, 60, 40, 0.12);
	border-color: rgba(139, 69, 19, 0.38);
}

.vert-cover {
	position: relative;
	height: 94px;
	border-radius: 6px;
	margin-bottom: 12px;
	display: flex;
	align-items: center;
	justify-content: center;
	filter: sepia(0.1) saturate(0.9);
	box-shadow: inset 0 0 0 1px rgba(139, 69, 19, 0.12), inset 0 -12px 24px rgba(139, 69, 19, 0.05);
	overflow: hidden;
}
.vert-cover::after {
	content: '';
	position: absolute; inset: 0;
	background-image: repeating-linear-gradient(
		135deg,
		rgba(139, 69, 19, 0.02) 0 3px,
		rgba(139, 69, 19, 0.04) 3px 6px
	);
	pointer-events: none;
}
.vert-emoji {
	font-size: 46px;
	line-height: 1;
	filter: drop-shadow(0 2px 2px rgba(90, 60, 40, 0.12));
}
.vert-tape {
	position: absolute;
	top: -6px;
	left: 50%;
	transform: translateX(-50%) rotate(-3deg);
	width: 52px;
	height: 14px;
	background: rgba(228, 207, 170, 0.75);
	border-left: 1px dashed rgba(139, 69, 19, 0.2);
	border-right: 1px dashed rgba(139, 69, 19, 0.2);
	box-shadow: 0 1px 3px rgba(90, 60, 40, 0.1);
}

.vert-body { display: flex; flex-direction: column; gap: 6px; }
.vert-heads { display: flex; align-items: baseline; gap: 8px; flex-wrap: wrap; }
.vert-en {
	font-family: 'Playfair Display', Georgia, serif;
	font-style: italic;
	font-size: 10px;
	letter-spacing: 0.14em;
	color: #a8906e;
	text-transform: uppercase;
}
.vert-cn {
	font-family: 'Noto Serif SC', Georgia, serif;
	font-size: 15px;
	font-weight: 600;
	color: #2c2218;
	letter-spacing: 0.04em;
}
.vert-desc {
	font-size: 12px;
	line-height: 1.75;
	color: #7a6a58;
	text-align: justify;
	text-justify: inter-ideograph;
	min-height: 62px;
}
.vert-meta {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 8px;
	padding-top: 8px;
	margin-top: 2px;
	border-top: 1px dashed rgba(139, 69, 19, 0.2);
}
.vert-chip {
	font-size: 10px;
	letter-spacing: 0.08em;
	color: #8b6e4a;
	padding: 3px 8px;
	background: rgba(200, 146, 58, 0.08);
	border: 1px solid rgba(200, 146, 58, 0.28);
	border-radius: 999px;
}
.vert-link {
	font-family: 'Playfair Display', 'Noto Serif SC', serif;
	font-size: 11px;
	letter-spacing: 0.08em;
	color: #6d2c16;
}
.vert-corner {
	position: absolute;
	top: 0;
	right: 0;
	width: 26px;
	height: 26px;
	background: linear-gradient(135deg, transparent 50%, rgba(139, 69, 19, 0.1) 50%);
}

/* ========== 应用能力：时间轴式清单 ========== */
.capability-list {
	display: flex;
	flex-direction: column;
	gap: 2px;
	margin-bottom: 28px;
	background: linear-gradient(180deg, rgba(250, 247, 242, 0.9) 0%, rgba(237, 230, 220, 0.65) 100%);
	border: 1px solid rgba(139, 115, 85, 0.18);
	border-radius: 10px;
	padding: 6px 8px;
	box-shadow: 0 4px 14px rgba(90, 60, 40, 0.06);
}

.cap-row {
	display: grid;
	grid-template-columns: 70px 10px 1fr auto;
	align-items: center;
	gap: 14px;
	padding: 14px 10px;
	border-bottom: 1px dashed rgba(139, 69, 19, 0.15);
	position: relative;
}
.cap-row:last-child { border-bottom: 0; }

.cap-num {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	font-family: 'Playfair Display', Georgia, serif;
	color: #8b4513;
}
.cap-num-t {
	font-size: 26px;
	font-weight: 600;
	line-height: 1;
	font-variant-numeric: tabular-nums;
	color: #6d2c16;
}
.cap-num-sub {
	font-size: 8px;
	letter-spacing: 0.3em;
	color: #a8906e;
	margin-top: 4px;
}
.cap-rule {
	width: 1px;
	align-self: stretch;
	background: repeating-linear-gradient(
		to bottom,
		rgba(139, 69, 19, 0.35) 0 3px,
		transparent 3px 6px
	);
	margin: 6px 0;
}
.cap-body { display: flex; flex-direction: column; gap: 4px; min-width: 0; }
.cap-heads { display: flex; align-items: baseline; gap: 8px; flex-wrap: wrap; }
.cap-ico {
	font-size: 18px;
	line-height: 1;
	filter: sepia(0.2);
}
.cap-cn {
	font-family: 'Noto Serif SC', Georgia, serif;
	font-size: 14px;
	font-weight: 600;
	color: #3d2b1a;
	letter-spacing: 0.04em;
}
.cap-en {
	font-family: 'Playfair Display', Georgia, serif;
	font-style: italic;
	font-size: 10px;
	letter-spacing: 0.1em;
	color: #a8906e;
}
.cap-desc {
	font-size: 12px;
	line-height: 1.72;
	color: #7a6a58;
}
.cap-stamp {
	flex-shrink: 0;
	transform: rotate(-6deg);
	padding: 4px 10px;
	border: 1.2px solid rgba(139, 60, 30, 0.55);
	border-radius: 4px;
	background: rgba(164, 80, 40, 0.04);
}
.cap-stamp-t {
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 10px;
	letter-spacing: 0.22em;
	color: #8b3a1e;
	font-weight: 600;
}

@media (max-width: 720px) {
	.cap-row { grid-template-columns: 56px 6px 1fr; }
	.cap-stamp { grid-column: 3 / 4; justify-self: end; margin-top: 4px; }
}

/* ========== 页脚 ephemera：邮票 + 语录 + 签名 ========== */
.ephemera-strip {
	display: grid;
	grid-template-columns: auto 1fr auto;
	gap: 26px;
	align-items: center;
	margin-top: 32px;
	padding: 22px 14px 10px;
	border-top: 1px dashed rgba(122, 92, 58, 0.22);
}

.eph-col { min-width: 0; }

.eph-stamps {
	display: flex;
	align-items: flex-start;
	gap: 10px;
}
.eph-stamp {
	position: relative;
	width: 60px;
	height: 76px;
	padding: 8px 4px 6px;
	text-align: center;
	background: linear-gradient(180deg, #faf2dd 0%, #e9d5a8 100%);
	border: 1.5px solid rgba(139, 60, 30, 0.4);
	box-shadow: 0 3px 8px rgba(90, 60, 40, 0.15), inset 0 0 0 3px rgba(250, 247, 242, 0.85);
	mask-image: radial-gradient(circle at 4px 50%, transparent 2.5px, #000 3px),
		radial-gradient(circle at calc(100% - 4px) 50%, transparent 2.5px, #000 3px);
	-webkit-mask-image: radial-gradient(circle at 4px 50%, transparent 2.5px, #000 3px),
		radial-gradient(circle at calc(100% - 4px) 50%, transparent 2.5px, #000 3px);
}
.eph-stamp.s1 { transform: rotate(-4deg); }
.eph-stamp.s2 { transform: rotate(5deg) translateY(4px); background: linear-gradient(180deg, #efe4cb 0%, #d4b98a 100%); }
.stamp-v {
	display: block;
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 9px;
	letter-spacing: 0.18em;
	color: #6d2c16;
	font-weight: 600;
	margin-top: 6px;
}
.stamp-num {
	display: block;
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 11px;
	color: #4a2d18;
	letter-spacing: 0.1em;
	margin-top: 24px;
	font-weight: 600;
}
.stamp-perf {
	position: absolute; inset: 3px;
	border: 1px dashed rgba(139, 60, 30, 0.3);
}

.eph-quote {
	position: relative;
	padding: 12px 18px 12px 28px;
	background: rgba(250, 247, 242, 0.6);
	border-left: 2px solid rgba(200, 146, 58, 0.55);
	border-radius: 0 8px 8px 0;
}
.eph-q-mark {
	position: absolute;
	left: 6px; top: 2px;
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 36px;
	line-height: 1;
	color: rgba(139, 69, 19, 0.35);
}
.eph-q-body {
	display: block;
	font-family: 'Noto Serif SC', Georgia, serif;
	font-size: 13px;
	line-height: 1.8;
	color: #5c4a3a;
	letter-spacing: 0.03em;
}
.eph-q-sign {
	display: block;
	margin-top: 8px;
	text-align: right;
	font-family: 'Playfair Display', Georgia, serif;
	font-style: italic;
	font-size: 11px;
	color: #9a8878;
	letter-spacing: 0.06em;
}

.eph-sign {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
	gap: 4px;
	min-width: 168px;
}
.eph-sign-en {
	font-family: 'Playfair Display', Georgia, serif;
	font-style: italic;
	font-size: 11px;
	color: #9a8878;
	letter-spacing: 0.08em;
}
.eph-sign-hand {
	font-family: 'Cormorant Garamond', 'Playfair Display', 'Noto Serif SC', serif;
	font-style: italic;
	font-weight: 500;
	font-size: 28px;
	color: #3b2c1e;
	letter-spacing: 0.02em;
	transform: rotate(-3deg) translateX(-4px);
	line-height: 1.1;
	text-shadow: 0 1px 0 rgba(255, 255, 255, 0.7);
}
.eph-sign-rule {
	width: 120px;
	height: 1px;
	background: linear-gradient(90deg, transparent, rgba(122, 92, 58, 0.4), transparent);
	margin: 2px 0;
}
.eph-sign-addr {
	font-family: 'Playfair Display', Georgia, serif;
	font-size: 9px;
	letter-spacing: 0.14em;
	color: #a8906e;
	text-align: right;
}

@media (max-width: 860px) {
	.ephemera-strip {
		grid-template-columns: 1fr;
		gap: 18px;
		padding: 22px 4px 4px;
	}
	.eph-sign { align-items: flex-start; }
	.eph-sign-addr { text-align: left; }
}

/* ========== 展厅户型 fp- ========== */
.fp-page {
	min-height: 100%;
	padding-bottom: 28px;
	font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
	color: #3d2b1a;
}

.fp-nav {
	margin-bottom: 12px;
}

.fp-back {
	display: inline-flex;
	align-items: center;
	gap: 4px;
	padding: 6px 4px;
	color: #7a5c3a;
	font-size: 14px;
}

.fp-back-arrow {
	font-size: 22px;
	line-height: 1;
	opacity: 0.85;
}

.fp-back-txt {
	letter-spacing: 0.06em;
}

.fp-intro {
	margin-bottom: 20px;
	padding: 0 2px;
}

.fp-kicker {
	display: block;
	font-size: 10px;
	letter-spacing: 0.22em;
	color: #b8a898;
	margin-bottom: 8px;
}

.fp-title {
	display: block;
	font-family: 'Playfair Display', 'Noto Serif SC', serif;
	font-size: 26px;
	font-weight: 600;
	letter-spacing: 0.06em;
	margin-bottom: 10px;
	color: #2c2218;
	line-height: 1.3;
}

.fp-desc {
	display: block;
	font-size: 14px;
	line-height: 1.85;
	color: #7a6a58;
	max-width: 560px;
}

.fp-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(208px, 1fr));
	gap: 16px;
}

.fp-cell {
	background: #fcfaf6;
	border: 1px solid rgba(139, 115, 85, 0.15);
	border-radius: 12px;
	padding: 16px;
	transition: border-color 0.25s ease, box-shadow 0.25s ease;
}

.fp-cell.selected {
	border-color: rgba(139, 69, 19, 0.4);
	box-shadow: 0 6px 20px rgba(90, 60, 40, 0.1);
	background: rgba(139, 69, 19, 0.04);
}

.fp-thumb {
	height: 112px;
	background: linear-gradient(165deg, #f3ede4 0%, #e8e0d6 100%);
	border-radius: 10px;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 12px;
	border: 1px solid rgba(122, 92, 58, 0.12);
	overflow: hidden;
	padding: 8px;
}

.fp-svg {
	width: 100%;
	height: 100%;
	max-height: 96px;
}

.fp-name {
	display: block;
	font-weight: 600;
	font-size: 16px;
	margin-bottom: 4px;
	letter-spacing: 0.02em;
}

.fp-dim {
	display: block;
	font-size: 11px;
	color: #5c4a3a;
	line-height: 1.5;
	margin-bottom: 6px;
}

.fp-meta {
	display: block;
	font-size: 12px;
	color: #8a7a6a;
	line-height: 1.5;
}

.fp-footer {
	display: flex;
	flex-wrap: wrap;
	gap: 12px;
	margin-top: 28px;
	padding-top: 20px;
	border-top: 1px dashed rgba(122, 92, 58, 0.18);
}

.fp-btn {
	flex: 1;
	min-width: 120px;
	height: 48px;
	line-height: 48px;
	border-radius: 10px;
	font-size: 14px;
	font-weight: 600;
	font-family: inherit;
	border: none;
}

.fp-btn.ghost {
	background: transparent;
	border: 1px solid rgba(122, 92, 58, 0.28);
	color: #6b5344;
}

.fp-btn.primary {
	background: linear-gradient(180deg, #7a4e32 0%, #5a381f 100%);
	color: #faf7f2;
	box-shadow: 0 4px 14px rgba(90, 60, 40, 0.18);
}

.fp-btn[disabled] {
	opacity: 0.4;
}

/* ========== 展馆布展 pl- ========== */
.pl-page {
	min-height: 100%;
	padding-bottom: 24px;
	font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
	color: #3d2b1a;
}

.pl-nav {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
	margin-bottom: 10px;
	flex-wrap: wrap;
}

.pl-back {
	display: inline-flex;
	align-items: center;
	gap: 4px;
	padding: 4px 2px;
	color: #6b5344;
	font-size: 15px;
}

.pl-back-arrow {
	font-size: 24px;
	line-height: 1;
	opacity: 0.9;
}

.pl-back-txt {
	letter-spacing: 0.08em;
	font-weight: 500;
}

.pl-nav-hint {
	font-size: 12px;
	color: #a09080;
	letter-spacing: 0.04em;
}

.pl-bar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
	margin-bottom: 14px;
	flex-wrap: wrap;
}

.pl-room-pill {
	background: rgba(252, 250, 246, 0.98);
	border: 1px solid rgba(139, 115, 85, 0.2);
	border-radius: 999px;
	padding: 10px 18px;
}

.pill-label {
	display: block;
	font-size: 10px;
	letter-spacing: 0.18em;
	color: #a09080;
	margin-bottom: 2px;
}

.pill-name {
	font-size: 15px;
	font-weight: 600;
	color: #5a3818;
}

.pill-dim {
	display: block;
	font-size: 11px;
	color: #8a7868;
	line-height: 1.45;
	margin-top: 8px;
	max-width: 280px;
}

.pl-hint {
	font-size: 13px;
	color: #7a5c3a;
	text-decoration: underline;
	text-underline-offset: 3px;
}

.pl-drag-hint {
	margin-bottom: 12px;
	padding: 8px 12px;
	background: rgba(252, 250, 246, 0.9);
	border: 1px dashed rgba(139, 115, 85, 0.22);
	border-radius: 10px;
}

.pl-drag-hint-txt {
	font-size: 12px;
	color: #7a6a58;
	line-height: 1.55;
}

.pl-modal-mask {
	position: fixed;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	background: rgba(40, 32, 24, 0.45);
	z-index: 999;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 20px;
	box-sizing: border-box;
}

.pl-modal {
	width: 100%;
	max-width: 420px;
	background: #fcfaf6;
	border-radius: 14px;
	padding: 22px 20px 18px;
	border: 1px solid rgba(139, 115, 85, 0.25);
	box-shadow: 0 12px 40px rgba(40, 28, 18, 0.2);
}

.pl-modal-title {
	display: block;
	font-size: 18px;
	font-weight: 600;
	color: #2c2218;
	margin-bottom: 10px;
	text-align: center;
}

.pl-modal-body {
	display: block;
	font-size: 14px;
	color: #7a6a58;
	line-height: 1.65;
	margin-bottom: 20px;
	text-align: center;
}

.pl-modal-actions {
	display: flex;
	flex-direction: row;
	flex-wrap: nowrap;
	align-items: stretch;
	gap: 8px;
}

.pl-modal-btn {
	flex: 1;
	min-width: 0;
	height: 44px;
	line-height: 44px;
	border-radius: 10px;
	font-size: 12px;
	font-weight: 600;
	border: none;
	font-family: inherit;
	padding: 0 6px;
	box-sizing: border-box;
}

.pl-modal-btn.primary {
	background: linear-gradient(180deg, #7a4e32 0%, #5a381f 100%);
	color: #faf7f2;
}

.pl-modal-btn.ghost {
	background: #f5f0e8;
	color: #6b5344;
	border: 1px solid rgba(122, 92, 58, 0.25);
}

.pl-modal-btn.danger {
	background: transparent;
	color: #8b4513;
	border: 1px dashed rgba(139, 69, 19, 0.45);
}

.pl-stage {
	margin-bottom: 18px;
}

.pl-three-wrap {
	position: relative;
	width: 100%;
	min-height: 460px;
	height: 60vh;
	max-height: 640px;
	border-radius: 14px;
	overflow: hidden;
	border: 1px solid rgba(139, 115, 85, 0.28);
	box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.4), 0 10px 32px rgba(90, 60, 40, 0.12);
	background: #e8ded0;
}

.three-stage {
	position: absolute;
	inset: 0;
	width: 100%;
	height: 100%;
	touch-action: none;
	cursor: grab;
}

.three-stage:active {
	cursor: grabbing;
}

.three-loading {
	position: absolute;
	inset: 0;
	display: flex;
	align-items: center;
	justify-content: center;
	background: rgba(232, 222, 208, 0.92);
	z-index: 2;
}

.three-loading-txt {
	font-size: 13px;
	color: #5c4a3a;
	letter-spacing: 0.08em;
}

.three-overlay {
	position: absolute;
	left: 12px;
	bottom: 12px;
	z-index: 3;
	display: flex;
	gap: 8px;
	flex-wrap: wrap;
}

.three-hud {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	padding: 6px 10px;
	border-radius: 999px;
	background: rgba(44, 34, 24, 0.62);
	backdrop-filter: blur(4px);
	color: #f6eedd;
}

.three-hud.btn {
	cursor: pointer;
	background: rgba(160, 108, 48, 0.72);
}

.three-hud-k {
	font-size: 10px;
	letter-spacing: 0.14em;
	color: rgba(246, 238, 221, 0.7);
}

.three-hud-v {
	font-size: 12px;
	color: #f6eedd;
	letter-spacing: 0.04em;
}

.pl-floor {
	position: relative;
	min-height: 440px;
	padding: 10px 8px 16px;
	border-radius: 12px;
	overflow: visible;
	border: 1px solid rgba(139, 115, 85, 0.2);
	box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.4), 0 8px 28px rgba(90, 60, 40, 0.08);
	background: linear-gradient(180deg, #b8c4b0 0%, #a09888 14%, #e0d8cc 32%, #d8cfc2 100%);
}

.pl-3d-badge {
	position: absolute;
	top: 8px;
	right: 10px;
	font-size: 10px;
	letter-spacing: 0.22em;
	color: rgba(60, 44, 28, 0.5);
	z-index: 5;
}

.pl-floor-3d {
	position: relative;
	padding-top: 4px;
}

.pl-room-cube {
	position: relative;
	width: min(100%, 720px);
	max-width: 100%;
	margin: 0 auto;
	overflow: visible;
}

.pl-ceiling {
	width: 88%;
	min-height: 26px;
	margin: 0 auto;
	background: linear-gradient(180deg, #9a8a78 0%, #c8b8a8 100%);
	clip-path: polygon(0 100%, 6% 0, 94% 0, 100% 100%);
	opacity: 0.92;
}

.pl-wall-back {
	width: min(100%, 720px);
	max-width: 100%;
	margin: 0 auto;
	height: 38px;
	background: linear-gradient(180deg, #d4c4a8 0%, #8a7868 100%);
	clip-path: polygon(0 100%, 6% 0, 94% 0, 100% 100%);
}

.pl-wall-left,
.pl-wall-right {
	position: absolute;
	bottom: 0;
	width: 30%;
	height: 42%;
	min-height: 130px;
	pointer-events: none;
	z-index: 0;
}

.pl-wall-left {
	left: 0;
	background: linear-gradient(90deg, rgba(130, 118, 102, 0.6) 0%, rgba(230, 220, 208, 0) 100%);
	clip-path: polygon(0 100%, 0 0, 100% 8%, 100% 100%);
}

.pl-wall-right {
	right: 0;
	background: linear-gradient(270deg, rgba(130, 118, 102, 0.6) 0%, rgba(230, 220, 208, 0) 100%);
	clip-path: polygon(0 100%, 0 8%, 100% 0, 100% 100%);
}

.pl-floor-board-wrap {
	position: relative;
	z-index: 2;
	width: min(100%, 720px);
	max-width: 100%;
	margin: -2px auto 0;
	padding: 28px 14px 8px;
	overflow: visible;
}

/* 外框仅决定长:宽比；旋转作用在内层，点击用 #floorPhys 未旋转盒 + 逆旋转换算 */
.pl-floor-phys {
	position: relative;
	width: 100%;
	max-width: 100%;
	margin: 0 auto;
	overflow: visible;
	touch-action: none;
	cursor: grab;
}

.pl-floor-phys.is-dragging-floor {
	cursor: grabbing;
}

/* 地面：横向 = 长(lenM)，纵向 = 宽(widM)，比例由父级 aspect-ratio 决定 */
.floor-inner {
	position: absolute;
	left: 0;
	top: 0;
	width: 100%;
	height: 100%;
	z-index: 2;
	box-sizing: border-box;
	background: linear-gradient(165deg, #faf7f2 0%, #ebe3d6 55%, #ddd4c8 100%);
	cursor: crosshair;
	will-change: transform;
}

/* 与户型缩略图同一轮廓；外框长宽比已由 lenM/widM 决定 */
.floor-inner.shape-l {
	clip-path: polygon(0% 0%, 100% 0%, 100% 51%, 50% 51%, 50% 100%, 0% 100%);
}

.floor-inner.shape-tri {
	clip-path: polygon(50% 0%, 100% 100%, 0% 100%);
}

.floor-inner.shape-bed {
	clip-path: inset(0 round 12px);
}

.floor-inner.shape-open {
	clip-path: polygon(3% 24%, 94% 3%, 97% 76%, 2% 100%);
}

.floor-shade {
	position: absolute;
	inset: 0;
	pointer-events: none;
	opacity: 0.4;
	background: radial-gradient(ellipse 85% 65% at 50% 38%, rgba(255, 255, 255, 0.5) 0%, transparent 58%);
}

.floor-grid-3d {
	position: absolute;
	inset: 0;
	pointer-events: none;
	opacity: 0.1;
	background-image: linear-gradient(rgba(90, 60, 40, 0.18) 1px, transparent 1px),
		linear-gradient(90deg, rgba(90, 60, 40, 0.15) 1px, transparent 1px);
	background-size: 100% 14.28%, 14.28% 100%;
}

.pl-floor-dim-note {
	display: flex;
	align-items: center;
	justify-content: center;
	flex-wrap: wrap;
	gap: 6px 10px;
	margin-top: 10px;
	padding: 0 4px;
}

.pl-dim-note-txt {
	font-size: 11px;
	color: #5c4a3a;
	letter-spacing: 0.04em;
}

.pl-dim-sep {
	font-size: 11px;
	color: #a09080;
}

.pl-item {
	position: absolute;
	transform: translate(-50%, -50%);
	display: flex;
	flex-direction: column;
	align-items: center;
	z-index: 4;
	pointer-events: auto;
}

.pl-ico {
	font-size: 30px;
	filter: sepia(0.15);
	text-shadow: 0 1px 3px rgba(90, 60, 40, 0.12);
}

.pl-tag {
	font-size: 10px;
	color: #6b5344;
	margin-top: 2px;
	max-width: 76px;
	text-align: center;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.pl-caption {
	display: block;
	text-align: center;
	font-size: 12px;
	color: #9a8878;
	margin-top: 12px;
	line-height: 1.65;
	padding: 0 6px;
}

.pl-lib {
	margin-bottom: 20px;
}

.lib-title {
	display: block;
	font-family: 'Playfair Display', 'Noto Serif SC', serif;
	font-size: 19px;
	font-weight: 600;
	margin-bottom: 6px;
	color: #2c2218;
	letter-spacing: 0.04em;
}

.lib-sub {
	display: block;
	font-size: 13px;
	color: #8a7a6a;
	margin-bottom: 12px;
}

.lib-scroll {
	width: 100%;
	white-space: nowrap;
}

.lib-row {
	display: inline-flex;
	gap: 12px;
	padding: 4px 2px 12px;
}

.lib-chip {
	min-width: 92px;
	padding: 12px 14px;
	border-radius: 12px;
	background: #fcfaf6;
	border: 1px solid rgba(139, 115, 85, 0.18);
	display: inline-flex;
	flex-direction: column;
	align-items: center;
	transition: border-color 0.25s ease, background 0.25s ease;
}

.lib-chip.on {
	border-color: rgba(139, 69, 19, 0.45);
	background: rgba(139, 69, 19, 0.06);
	box-shadow: 0 2px 10px rgba(90, 60, 40, 0.06);
}

.lc-ico {
	font-size: 26px;
	margin-bottom: 6px;
	filter: sepia(0.1);
}

.lc-name {
	font-size: 12px;
	color: #5c4a3a;
}

.pl-actions {
	display: flex;
	gap: 12px;
	flex-wrap: wrap;
}

.pl-btn {
	flex: 1;
	min-width: 140px;
	height: 48px;
	line-height: 48px;
	border-radius: 10px;
	font-size: 14px;
	font-weight: 600;
	font-family: inherit;
	border: none;
}

.pl-btn.ghost {
	background: transparent;
	border: 1px solid rgba(122, 92, 58, 0.28);
	color: #6b5344;
}

.pl-btn.primary {
	background: linear-gradient(180deg, #7a4e32 0%, #5a381f 100%);
	color: #faf7f2;
	box-shadow: 0 4px 16px rgba(90, 60, 40, 0.15);
}

/* ========== 展馆方案 sch- ========== */
.sch-page {
	min-height: 100%;
	padding-bottom: 28px;
	font-family: 'Noto Serif SC', Georgia, 'Songti SC', serif;
	color: #2c2218;
}

.sch-nav {
	margin-bottom: 12px;
}

.sch-back {
	display: inline-flex;
	align-items: center;
	gap: 4px;
	padding: 6px 4px;
	color: #7a5c3a;
	font-size: 14px;
}

.sch-back-arrow {
	font-size: 22px;
	line-height: 1;
	opacity: 0.85;
}

.sch-back-txt {
	letter-spacing: 0.06em;
}

.sch-intro {
	margin-bottom: 22px;
}

.sch-kicker {
	display: block;
	font-size: 10px;
	letter-spacing: 0.22em;
	color: #a09080;
	margin-bottom: 8px;
}

.sch-title {
	display: block;
	font-family: 'Playfair Display', 'Noto Serif SC', serif;
	font-size: 22px;
	font-weight: 600;
	letter-spacing: 0.04em;
	margin-bottom: 8px;
	color: #2c2218;
}

.sch-desc {
	display: block;
	font-size: 13px;
	line-height: 1.75;
	color: #7a6a58;
	max-width: 520px;
}

.sch-empty {
	text-align: center;
	padding: 36px 20px;
	background: #faf7f2;
	border: 1px dashed rgba(122, 92, 58, 0.22);
	border-radius: 10px;
}

.empty-ico {
	display: block;
	font-size: 42px;
	margin-bottom: 12px;
	filter: sepia(0.15);
}

.empty-t {
	display: block;
	font-weight: 600;
	font-size: 16px;
	margin-bottom: 8px;
}

.empty-p {
	display: block;
	font-size: 13px;
	color: #9a8878;
	line-height: 1.65;
	margin-bottom: 18px;
}

.sch-btn {
	display: inline-block;
	padding: 10px 28px;
	border-radius: 10px;
	background: linear-gradient(180deg, #8b4513, #6b3810);
	color: #faf7f2;
	font-size: 14px;
	font-weight: 600;
	border: none;
	font-family: inherit;
}

.sch-list {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.sch-card {
	display: flex;
	align-items: center;
	gap: 14px;
	padding: 14px 16px;
	background: #faf7f2;
	border: 1px dashed rgba(122, 92, 58, 0.2);
	border-radius: 10px;
	box-shadow: 0 3px 14px rgba(90, 60, 40, 0.06);
}

.sch-thumb-wrap {
	width: 56px;
	height: 56px;
	border-radius: 8px;
	background: #ede6dc;
	display: flex;
	align-items: center;
	justify-content: center;
	border: 1px solid rgba(122, 92, 58, 0.12);
	flex-shrink: 0;
}

.sch-emoji {
	font-size: 28px;
	filter: sepia(0.18);
}

.sch-body {
	flex: 1;
	min-width: 0;
}

.sch-card-title {
	display: block;
	font-weight: 600;
	font-size: 15px;
	margin-bottom: 4px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.sch-meta {
	display: block;
	font-size: 12px;
	color: #9a8878;
	margin-bottom: 2px;
}

.sch-time {
	display: block;
	font-size: 11px;
	color: #b8a898;
}

.sch-ops {
	flex-shrink: 0;
}

.op {
	font-size: 12px;
	color: #8b4513;
	text-decoration: underline;
	text-underline-offset: 3px;
}

@media (max-width: 1020px) {
	.scene-editorial {
		grid-template-columns: 1fr;
		gap: 28px;
	}
	.scene-col-right {
		border-left: none;
		padding-left: 0;
		border-top: 1px dashed rgba(122, 92, 58, 0.15);
		padding-top: 22px;
	}
}

@media (max-width: 520px) {
	.rail-mid {
		width: 100%;
		order: 3;
		text-align: left;
	}
	.scene-rail {
		flex-direction: column;
		align-items: flex-start;
	}
	.stat-row {
		grid-template-columns: 1fr;
	}
	.hero-h1 {
		font-size: 22px;
	}
	.pl-floor {
		min-height: 300px;
	}
}
</style>
