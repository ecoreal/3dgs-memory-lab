<template>
	<view class="shell">
		<view class="paper-grain"></view>
		<view class="dust dust-left"></view>
		<view class="dust dust-right"></view>

		<view class="desktop-frame">
			<!-- ── Masthead ── -->
			<view class="masthead">
				<view class="mh-rule thick"></view>
				<view class="mh-rule thin"></view>
				<view class="mh-inner">
					<view class="mh-meta">
						<text class="meta-label">兴趣社群展馆计划</text>
						<text class="meta-val">第七卷 · 数字馆藏入口</text>
					</view>
					<view class="mh-center">
						<text class="brand-kicker">个人策展 · 兴趣社群 · 超级博物馆</text>
						<text class="brand-name">一方阁</text>
						<text class="brand-roman">Yī Fāng Gé &nbsp;·&nbsp; Personal Museum Studio</text>
					</view>
					<view class="mh-meta mh-right">
						<text class="meta-label">策展时段</text>
						<text class="meta-val">{{ currentDateLabel }}</text>
					</view>
				</view>
				<view class="mh-rule thin"></view>
			</view>

			<!-- ── Layout ── -->
			<view class="layout">

				<!-- Story Panel -->
				<view class="story-panel">
					<view class="panel-noise"></view>
					<view class="corner tl"></view>
					<view class="corner br"></view>

					<view class="story-main">
						<view class="headline-block">
							<view class="stamp-row">
								<text class="stamp">策展前言</text>
							<text class="stamp-en">Curatorial Preface</text>
							</view>
							<text class="hl-cn">把热爱布置成<br/>一座属于自己的<br/><em>迷你博物馆</em></text>
							<view class="orn-row">
								<view class="orn-line"></view>
								<text class="orn-leaf">❧</text>
								<view class="orn-line short"></view>
							</view>
							<text class="hl-body">
								无论是矿晶收藏、模型制作、昆虫标本，还是老旧电子产品，
								你都可以把藏品拍照生成三维模型，再按自己的审美布置进虚拟房间，
								让每一件展品都拥有讲解、考据与观看动线。
							</text>
							<view class="tag-row">
								<text class="tag">三维藏品</text>
								<text class="tag">个人布展</text>
								<text class="tag">社群参观</text>
							</view>
						</view>

						<!-- ── Animated Blueprint ── -->
						<view class="bp-panel">
							<view class="bp-head">
								<text class="bp-title">个人展馆蓝图</text>
								<text class="bp-sub">Model · Layout · Visit</text>
							</view>
							<view class="bp-stage">
								<!-- scanning beam -->
								<view class="scan-beam"></view>
								<!-- dot grid bg (decorative) -->
								<view class="stage-grid"></view>

								<!-- outer equator ring — slow Y spin -->
								<view class="orbit orbit-eq"></view>
								<!-- tilted polar orbit -->
								<view class="orbit orbit-pol"></view>
								<!-- second tilt -->
								<view class="orbit orbit-tlt"></view>

								<!-- inner wireframe sphere rings -->
								<view class="wf-wrap">
									<view class="wf-ring wfr-0"></view>
									<view class="wf-ring wfr-1"></view>
									<view class="wf-ring wfr-2"></view>
								</view>

								<!-- axis lines -->
								<view class="ax ax-h"></view>
								<view class="ax ax-v"></view>

								<!-- compass labels -->
								<text class="axl axl-n">N</text>
								<text class="axl axl-e">E</text>
								<text class="axl axl-s">S</text>
								<text class="axl axl-w">W</text>

								<!-- core dot with pulse -->
								<view class="wf-dot-wrap">
									<view class="wf-pulse"></view>
									<view class="wf-dot"></view>
								</view>

								<!-- measurement tick marks -->
								<view class="tick t0"></view>
								<view class="tick t1"></view>
								<view class="tick t2"></view>
								<view class="tick t3"></view>
							</view>
							<text class="bp-cap">从藏品采集到虚拟布展，再到社群互访，逐步搭建你的个人主题数字展馆。</text>
						</view>
					</view>

					<view class="stat-row">
						<view class="stat-chip" v-for="item in deskNotes" :key="item.label">
							<text class="stat-lbl">{{ item.label }}</text>
							<text class="stat-val">{{ item.value }}</text>
						</view>
					</view>
				</view>

				<!-- Login Card -->
				<view :class="['card-wrap', showCard ? 'card-visible' : '']">
					<view class="card-mask" @tap="showCard = false"></view>
					<view class="card">
						<view class="card-noise"></view>
						<view class="pin pin-tr"></view>
						<view class="pin pin-bl"></view>
						<view class="card-close" @tap="showCard = false">×</view>
						<view class="drag-handle"></view>

						<view class="c-header">
							<view class="badge-row">
								<text class="badge">登录表单</text>
								<text class="badge olive">迷你博物馆入口</text>
							</view>
							<text class="c-greeting">{{ greeting }}，</text>
							<text class="c-greeting-sub">欢迎回到你的策展工作台</text>
							<text class="c-desc">登录后继续上传藏品、布置虚拟展馆，并在兴趣社群中分享你的个人主题小馆。</text>
						</view>

						<view class="entry-tag">
							<text class="et-lbl">当前入口</text>
							<view class="et-dots">
								<view class="et-dot"></view><view class="et-dot"></view><view class="et-dot"></view>
							</view>
							<text class="et-val">个人展馆创建与社群参观</text>
						</view>

						<view class="form">
							<view :class="['field', focused==='u' ? 'on' : '']">
								<view class="field-lbl-row">
									<text class="fl">账号</text>
									<text class="fl-en">Account</text>
								</view>
								<input v-model="account" placeholder="输入你的邮箱或账号"
									placeholder-style="color:#c2a88c;font-size:15px"
									@focus="focused='u'" @blur="focused=''" class="fi" />
								<view class="fline"></view>
							</view>
							<!-- 修改：在密码输入框上添加 @confirm 事件监听器，并设置 confirm-type 属性 -->
							<view :class="['field', focused==='p' ? 'on' : '']">
								<view class="field-lbl-row">
									<text class="fl">密码</text>
									<text class="fl-en">Password</text>
								</view>
								<input v-model="password" password placeholder="输入安全口令"
									placeholder-style="color:#c2a88c;font-size:15px"
									@focus="focused='p'" @blur="focused=''" 
									@confirm="handleLogin"
									:confirm-type="'go'"
									class="fi" />
								<view class="fline"></view>
							</view>
						</view>

						<button class="login-btn" @tap="handleLogin">
							<text class="lbt">进入迷你博物馆工作台</text>
							<text class="lba">›</text>
						</button>

						<view class="c-links">
							<text class="lnk" @tap="toRegister">注册新档案账户</text>
							<text class="lnk-sep">·</text>
							<text class="lnk">找回访问口令</text>
						</view>

						<view class="c-foot">
							<text class="cf-quote">"每一种兴趣，都值得一座被认真布置的小型展馆。"</text>
							<text class="cf-copy">© 2026 迷你博物馆计划 · Mini Museum Project</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<view class="mob-bar" v-show="!showCard" @tap="showCard = true">
			<view class="mob-bar-inner">
				<view class="mob-left">
					<text class="mob-kicker">进入档案空间</text>
					<text class="mob-title">打开你的迷你博物馆</text>
				</view>
				<text class="mob-arr">›</text>
			</view>
		</view>
	</view>
</template>

<script>
import { BASE_URL } from '../../utils/request.js';
export default {
	data() {
		return {
			account: '', password: '', focused: '',
			greeting: '你好', showCard: false,
			deskNotes: [
				{ label: '馆藏规模', value: '12,480' },
				{ label: '布展方式', value: '虚拟展陈' },
				{ label: '社群模式', value: '互访交流' }
			]
		};
	},
	computed: {
		currentDateLabel() {
			const n = new Date();
			return `${n.getFullYear()}.${String(n.getMonth()+1).padStart(2,'0')}.${String(n.getDate()).padStart(2,'0')}`;
		}
	},
	onLoad() {
		const h = new Date().getHours();
		if (h < 9) this.greeting = '早安';
		else if (h < 12) this.greeting = '上午好';
		else if (h < 18) this.greeting = '下午好';
		else this.greeting = '晚上好';
	},
	methods: {
		async handleLogin() {
			if (!this.account || !this.password)
				return uni.showToast({ title: '请输入账号和密码', icon: 'none' });
			uni.showLoading({ title: '正在验证身份...', mask: true });
			try {
				const res = await uni.request({
					url: `${BASE_URL}/users/login`, method: 'POST',
					header: { 'Content-Type': 'application/json' },
					data: { account: this.account.trim(), password: this.password },
					timeout: 10000
				});
				uni.hideLoading();
				if (res.statusCode === 200) {
					const d = res.data;
					const token = d.token || d.accessToken;
					console.log('[login] login success:', { hasToken: Boolean(token), userInfo: d.userInfo });
					if (token) uni.setStorageSync('token', token);
					if (d.userInfo) { uni.setStorageSync('userInfo', d.userInfo); uni.setStorageSync('currentUser', d.userInfo); }
					uni.setStorageSync('isLoggedIn', true);
					uni.showToast({ title: `欢迎回来，${d.userInfo?.nickname||'用户'}`, icon: 'success', duration: 1500 });
					setTimeout(() => uni.reLaunch({ url: '/pages/home/home', fail: () => uni.switchTab({ url: '/pages/home/home' }) }), 1500);
				} else if (res.statusCode === 401) {
					uni.showToast({ title: typeof res.data==='string' ? res.data : '账号或密码错误', icon: 'none', duration: 2000 });
				} else {
					uni.showToast({ title: `请求失败 (${res.statusCode})`, icon: 'none', duration: 2000 });
				}
			} catch(e) {
				uni.hideLoading();
				uni.showToast({ title: e.errMsg?.includes('timeout') ? '请求超时，请检查网络' : '网络连接失败', icon: 'none', duration: 2500 });
			}
		},
		toRegister() {
			uni.navigateTo({ url: '/pages/register/register', animationType: 'pop-in', animationDuration: 300 });
		}
	}
};
</script>

<style scoped>
/* ─────────────────────────────────────────────────────────
   FONTS
───────────────────────────────────────────────────────── */
@import url('https://fonts.googleapis.com/css2?family=Lora:ital,wght@0,400;0,500;0,600;1,400;1,500&family=Noto+Serif+SC:wght@300;400;500;600&family=Cormorant+Garamond:ital,wght@0,400;0,500;1,300;1,400;1,500&family=Playfair+Display:ital,wght@0,500;0,600;1,500&display=swap');

/* ─────────────────────────────────────────────────────────
   ANIMATION KEYFRAMES
───────────────────────────────────────────────────────── */

/* equator ring: slow Y-axis spin */
@keyframes spin-eq {
	from { transform: translate(-50%,-50%) rotateY(0deg); }
	to   { transform: translate(-50%,-50%) rotateY(360deg); }
}
/* polar orbit: stays tilted 70deg on X, rotates Z */
@keyframes spin-pol {
	from { transform: translate(-50%,-50%) rotateX(72deg) rotateZ(0deg); }
	to   { transform: translate(-50%,-50%) rotateX(72deg) rotateZ(360deg); }
}
/* 3rd orbit: different tilt, opposite direction */
@keyframes spin-tlt {
	from { transform: translate(-50%,-50%) rotateX(-50deg) rotateY(30deg) rotateZ(0deg); }
	to   { transform: translate(-50%,-50%) rotateX(-50deg) rotateY(30deg) rotateZ(-360deg); }
}
/* inner ring 0: free rotate */
@keyframes wfr0 {
	from { transform: translate(-50%,-50%) rotateY(0deg); }
	to   { transform: translate(-50%,-50%) rotateY(360deg); }
}
/* inner ring 1: tilt + rotate */
@keyframes wfr1 {
	from { transform: translate(-50%,-50%) rotateX(60deg) rotateZ(0deg); }
	to   { transform: translate(-50%,-50%) rotateX(60deg) rotateZ(360deg); }
}
/* inner ring 2: opposite tilt */
@keyframes wfr2 {
	from { transform: translate(-50%,-50%) rotateX(-60deg) rotateZ(0deg); }
	to   { transform: translate(-50%,-50%) rotateX(-60deg) rotateZ(360deg); }
}
/* scanning beam top→bottom */
@keyframes scan {
	0%   { top: 8%;  opacity: 0; }
	10%  { opacity: 0.5; }
	90%  { opacity: 0.5; }
	100% { top: 92%; opacity: 0; }
}
/* core dot pulse */
@keyframes dot-pulse {
	0%,100% { box-shadow: 0 0 0 0 rgba(138,94,78,0.0),  0 0 0 5px rgba(138,94,78,0.12); }
	50%     { box-shadow: 0 0 10px 2px rgba(138,94,78,0.18), 0 0 0 10px rgba(138,94,78,0.05); }
}
/* ripple ring */
@keyframes ripple {
	0%   { transform: translate(-50%,-50%) scale(0.6); opacity: 0.5; }
	100% { transform: translate(-50%,-50%) scale(1.8); opacity: 0; }
}

/* ─────────────────────────────────────────────────────────
   TOKENS
───────────────────────────────────────────────────────── */
.shell {
	--paper:       #f5f0e8;
	--paper-light: #faf7f2;
	--paper-deep:  #e8dfd0;
	--ink:         #3b2c1e;
	--ink-mid:     #72604e;
	--ink-faint:   #a69180;
	--accent:      #8a6850;
	--accent-dk:   #664832;
	--line-mid:    rgba(96,68,42,0.28);

	--f-display: 'Cormorant Garamond', 'Playfair Display', Georgia, serif;
	--f-heading: 'Playfair Display', 'Cormorant Garamond', 'Noto Serif SC', Georgia, serif;
	--f-body:    'Lora', 'Noto Serif SC', 'STSong', 'Songti SC', Georgia, serif;
	--f-cn:      'Noto Serif SC', 'STSong', 'Songti SC', 'Source Han Serif SC', Georgia, serif;
	--f-ui:      'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;

	--t-2xs:   clamp(9px,  0.9vw, 10px);
	--t-xs:    clamp(10px, 1.0vw, 12px);
	--t-sm:    clamp(12px, 1.2vw, 14px);
	--t-md:    clamp(14px, 1.4vw, 16px);
	--t-lg:    clamp(17px, 1.8vw, 20px);
	--t-xl:    clamp(26px, 3.0vw, 34px);
	--t-2xl:   clamp(34px, 4.2vw, 46px);
	--t-brand: clamp(38px, 4.8vw, 54px);

	min-height: 100vh;
	position: relative; overflow: hidden;
	background:
		radial-gradient(circle at 7% 8%,  rgba(255,250,240,0.88), transparent 24%),
		radial-gradient(circle at 93% 91%, rgba(198,176,148,0.34), transparent 26%),
		linear-gradient(152deg, #ede4d3 0%, #f6f0e4 46%, #e9e0cc 100%);
	font-family: var(--f-ui);
	color: var(--ink);
}

.paper-grain, .dust { position: absolute; inset: 0; pointer-events: none; }
.paper-grain {
	opacity: 0.24;
	background-image:
		radial-gradient(rgba(98,70,46,0.07) 0.8px, transparent 0.8px),
		radial-gradient(rgba(255,255,255,0.26) 0.6px, transparent 0.6px);
	background-size: 18px 18px, 26px 26px;
	background-position: 0 0, 10px 12px;
}
.dust-left  { background: radial-gradient(circle at 9% 14%, rgba(182,144,108,0.13), transparent 22%); }
.dust-right { background: radial-gradient(circle at 92% 85%, rgba(136,102,74,0.09), transparent 24%); }

/* ─────────────────────────────────────────────────────────
   FRAME
───────────────────────────────────────────────────────── */
.desktop-frame {
	position: relative; z-index: 1;
	max-width: 1400px; margin: 0 auto;
	padding: clamp(16px,2.5vw,28px) clamp(16px,2.5vw,32px) clamp(20px,3vw,32px);
}

/* ─────────────────────────────────────────────────────────
   MASTHEAD
───────────────────────────────────────────────────────── */
.masthead { margin-bottom: clamp(14px,2vw,22px); }
.mh-rule {
	height: 1px;
	background: linear-gradient(90deg, transparent 0%, var(--line-mid) 15%, var(--line-mid) 85%, transparent 100%);
}
.mh-rule.thick { height: 2px; margin-bottom: 2px; }

.mh-inner {
	display: grid; grid-template-columns: 1fr auto 1fr;
	align-items: center; gap: clamp(12px,2vw,22px);
	padding: clamp(12px,1.6vw,20px) 6px clamp(10px,1.4vw,18px);
}
.mh-meta { display: flex; flex-direction: column; gap: 5px; }
.mh-right { text-align: right; align-items: flex-end; }
.meta-label {
	font-family: var(--f-ui); font-size: var(--t-2xs); letter-spacing: 1.8px;
	color: var(--ink-faint); font-weight: 500; text-transform: uppercase;
}
.meta-val { font-family: var(--f-body); font-size: var(--t-sm); color: var(--ink-mid); }
.mh-center { text-align: center; }
.brand-kicker {
	display: block; font-family: var(--f-ui);
	font-size: var(--t-2xs); letter-spacing: 2.5px;
	color: var(--ink-faint); margin-bottom: 8px;
}
.brand-name {
	display: block; font-family: var(--f-cn);
	font-size: var(--t-brand); letter-spacing: clamp(5px,0.8vw,12px);
	font-weight: 500; color: var(--accent-dk); line-height: 1;
	text-shadow: 0 1px 0 rgba(255,255,255,0.55);
}
.brand-roman {
	display: block; font-family: var(--f-display);
	font-size: clamp(11px,1.1vw,13px); letter-spacing: clamp(2px,0.4vw,4px);
	color: var(--ink-faint); margin-top: 8px; font-style: italic; font-weight: 400;
}

/* ─────────────────────────────────────────────────────────
   LAYOUT
───────────────────────────────────────────────────────── */
.layout {
	display: grid;
	grid-template-columns: minmax(0,1.15fr) minmax(340px,440px);
	gap: clamp(14px,2vw,24px); align-items: stretch;
	min-height: calc(100vh - clamp(120px,14vw,165px));
}

.story-panel, .card {
	position: relative;
	background: linear-gradient(168deg, rgba(253,249,244,0.97), rgba(243,235,222,0.98));
	border: 1px solid rgba(158,120,84,0.14);
	box-shadow:
		0 2px 0 rgba(255,255,255,0.76) inset,
		0 clamp(14px,3vw,30px) clamp(28px,6vw,66px) rgba(82,54,28,0.09);
	overflow: hidden;
}

/* ─────────────────────────────────────────────────────────
   STORY PANEL
───────────────────────────────────────────────────────── */
.story-panel {
	border-radius: clamp(18px,2.5vw,28px);
	padding: clamp(20px,2.5vw,30px);
	display: flex; flex-direction: column; gap: clamp(16px,2vw,24px);
}
.panel-noise {
	position: absolute; inset: 0; pointer-events: none; opacity: 0.14;
	background-image: radial-gradient(rgba(76,50,28,0.08) 0.7px, transparent 0.7px);
	background-size: 16px 16px;
}
.corner {
	position: absolute; width: 56px; height: 56px; pointer-events: none;
	background: linear-gradient(135deg, rgba(196,174,146,0.32), transparent);
}
.corner.tl { top: 0; left: 0; clip-path: polygon(0 0,100% 0,0 100%); }
.corner.br { bottom: 0; right: 0; clip-path: polygon(100% 0,100% 100%,0 100%); }

.story-main {
	flex: 1; display: grid;
	grid-template-columns: minmax(0,1fr) minmax(260px,380px);
	gap: clamp(14px,2vw,22px); align-items: center;
}

/* Headline */
.headline-block { display: flex; flex-direction: column; padding: 4px 10px 4px 2px; }
.stamp-row { display: flex; align-items: center; gap: 10px; margin-bottom: clamp(10px,1.5vw,16px); }
.stamp {
	padding: 5px 13px; border-radius: 999px;
	background: rgba(136,100,76,0.09); border: 1px solid rgba(136,100,76,0.18);
	font-family: var(--f-ui); font-size: var(--t-2xs); letter-spacing: 1.2px;
	color: var(--accent-dk); font-weight: 600;
}
.stamp-en { font-family: var(--f-display); font-size: var(--t-sm); font-style: italic; font-weight: 400; color: var(--ink-faint); }
.hl-cn {
	font-family: var(--f-cn); font-size: var(--t-2xl); line-height: 1.5;
	color: #4a3120; font-weight: 500; letter-spacing: 1px;
	margin-bottom: clamp(12px,1.6vw,20px);
}
.hl-cn em {
	font-style: normal; color: var(--accent-dk);
	border-bottom: 1.5px solid rgba(136,100,76,0.42); padding-bottom: 1px;
}
.orn-row { display: flex; align-items: center; gap: 10px; margin-bottom: clamp(12px,1.6vw,18px); }
.orn-line { flex: 1; height: 1px; background: linear-gradient(90deg, rgba(136,100,76,0.44), transparent); }
.orn-line.short { max-width: 56px; }
.orn-leaf { font-family: Georgia,serif; font-size: 15px; color: var(--accent); opacity: 0.52; line-height: 1; }
.hl-body {
	font-family: var(--f-body); font-size: var(--t-md); line-height: 2.05;
	color: var(--ink-mid); font-weight: 400; letter-spacing: 0.1px;
	margin-bottom: clamp(14px,1.8vw,22px);
}
.tag-row { display: flex; gap: 8px; flex-wrap: wrap; }
.tag {
	padding: 5px 13px; border-radius: 999px;
	font-family: var(--f-cn); font-size: var(--t-2xs); font-weight: 400; letter-spacing: 0.8px;
	color: var(--ink-mid); background: rgba(136,100,76,0.06); border: 1px solid rgba(136,100,76,0.15);
}

/* ─────────────────────────────────────────────────────────
   BLUEPRINT — ANIMATED
   Uses CSS perspective + keyframe animations for 3D feel
───────────────────────────────────────────────────────── */
.bp-panel {
	padding: clamp(14px,1.8vw,20px); border-radius: clamp(14px,1.8vw,20px);
	background:
		linear-gradient(180deg, rgba(228,222,212,0.78), rgba(212,202,188,0.82)),
		repeating-linear-gradient(0deg,  rgba(92,112,122,0.07) 0 1px, transparent 1px 28px),
		repeating-linear-gradient(90deg, rgba(92,112,122,0.07) 0 1px, transparent 1px 28px);
	border: 1px solid rgba(92,112,122,0.16);
	box-shadow: inset 0 0 0 1px rgba(255,255,255,0.24);
}
.bp-head { display: flex; justify-content: space-between; align-items: baseline; gap: 10px; margin-bottom: 14px; }
.bp-title { font-family: var(--f-cn); font-size: var(--t-lg); color: #4d6472; font-weight: 500; letter-spacing: 1.5px; }
.bp-sub { font-family: var(--f-display); font-size: var(--t-2xs); font-style: italic; color: rgba(76,98,112,0.72); }

.bp-stage {
	position: relative; border-radius: 14px;
	height: clamp(180px,22vw,252px);
	background: radial-gradient(circle at 50% 48%, rgba(228,240,248,0.30), rgba(186,202,212,0.12) 60%, transparent 80%);
	border: 1px solid rgba(92,112,122,0.12);
	overflow: hidden; margin-bottom: 12px;
	/* perspective for 3D children */
	perspective: 520px;
	perspective-origin: 50% 50%;
}

/* dot-grid overlay */
.stage-grid {
	position: absolute; inset: 0; pointer-events: none;
	background-image:
		radial-gradient(circle, rgba(92,112,122,0.20) 1px, transparent 1px);
	background-size: 22px 22px;
	opacity: 0.55;
}

/* scanning beam — horizontal line sweeping top→bottom */
.scan-beam {
	position: absolute; left: 10%; right: 10%; height: 1px;
	background: linear-gradient(90deg, transparent, rgba(138,168,184,0.72) 40%, rgba(180,210,224,0.90) 50%, rgba(138,168,184,0.72) 60%, transparent);
	box-shadow: 0 0 8px 2px rgba(138,168,184,0.28);
	animation: scan 4.5s ease-in-out infinite;
	pointer-events: none;
}

/* ── Orbit rings ── */
.orbit {
	position: absolute; top: 50%; left: 50%;
	border-radius: 50%;
	border: 1px solid rgba(88,110,122,0.40);
}
/* equator — wide, gentle Y-spin */
.orbit-eq {
	width: 200px; height: 200px;
	box-shadow: 0 0 0 1px rgba(140,168,184,0.08);
	animation: spin-eq 28s linear infinite;
}
/* polar orbit */
.orbit-pol {
	width: 162px; height: 162px;
	border-color: rgba(88,110,122,0.32);
	animation: spin-pol 18s linear infinite;
}
/* tilted orbit — faster, opposite direction */
.orbit-tlt {
	width: 162px; height: 162px;
	border-color: rgba(88,110,122,0.28);
	animation: spin-tlt 14s linear infinite;
}

/* ── Inner wireframe rings ── */
.wf-wrap {
	position: absolute; top: 50%; left: 50%;
	width: 130px; height: 130px;
	transform: translate(-50%,-50%);
}
.wf-ring {
	position: absolute; top: 50%; left: 50%;
	border-radius: 50%;
	border: 1px solid rgba(100,124,136,0.50);
}
.wfr-0 { width: 130px; height: 130px; animation: wfr0 22s linear infinite; }
.wfr-1 { width: 100px; height: 100px; animation: wfr1 16s linear infinite; }
.wfr-2 { width:  70px; height:  70px; animation: wfr2 12s linear infinite; }

/* ── Axis lines ── */
.ax { position: absolute; background: rgba(88,110,122,0.26); pointer-events: none; }
.ax-h { top: 50%; left: 12%; right: 12%; height: 1px; }
.ax-v { left: 50%; top: 12%; bottom: 12%; width: 1px; }

/* tick marks at ends of axes */
.tick {
	position: absolute; background: rgba(88,110,122,0.36);
	width: 1px; height: 8px;
}
.t0 { left: 12%; top: calc(50% - 4px); }
.t1 { right: 12%; top: calc(50% - 4px); }
.t2 { top: 12%; left: calc(50% - 4px); width: 8px; height: 1px; }
.t3 { bottom: 12%; left: calc(50% - 4px); width: 8px; height: 1px; }

/* compass labels */
.axl {
	position: absolute; font-family: var(--f-display);
	font-size: 10px; font-style: italic; font-weight: 400;
	letter-spacing: 1px; color: rgba(74,96,110,0.64); pointer-events: none;
}
.axl-n { top: 6%;  left: 50%; transform: translateX(-50%); }
.axl-e { right: 5%; top: 50%; transform: translateY(-50%); }
.axl-s { bottom: 6%; left: 50%; transform: translateX(-50%); }
.axl-w { left: 5%; top: 50%; transform: translateY(-50%); }

/* ── Core dot with pulse ring ── */
.wf-dot-wrap {
	position: absolute; top: 50%; left: 50%;
	transform: translate(-50%,-50%);
	width: 14px; height: 14px;
}
.wf-dot {
	position: absolute; top: 50%; left: 50%;
	width: 8px; height: 8px; transform: translate(-50%,-50%);
	border-radius: 50%; background: #8a5e4e;
	animation: dot-pulse 3s ease-in-out infinite;
}
.wf-pulse {
	position: absolute; top: 50%; left: 50%;
	width: 24px; height: 24px;
	border-radius: 50%;
	border: 1px solid rgba(138,94,78,0.32);
	animation: ripple 3s ease-out infinite;
	pointer-events: none;
}

.bp-cap {
	display: block; font-family: var(--f-body); font-size: clamp(11px,1.1vw,13px);
	font-weight: 400; line-height: 1.9; color: #58707a;
}

/* Stat row */
.stat-row { display: flex; gap: 10px; }
.stat-chip {
	flex: 1; padding: 12px 14px; border-radius: 14px;
	background: rgba(255,252,246,0.52); border: 1px dashed rgba(136,100,76,0.18);
	display: flex; flex-direction: column; gap: 6px;
}
.stat-lbl { font-family: var(--f-ui); font-size: var(--t-2xs); letter-spacing: 1.2px; color: var(--ink-faint); }
.stat-val { font-family: var(--f-heading); font-size: clamp(16px,2vw,20px); font-weight: 600; color: var(--accent-dk); }

/* ─────────────────────────────────────────────────────────
   LOGIN CARD
───────────────────────────────────────────────────────── */
.card-wrap { position: relative; display: flex; align-items: stretch; }
.card {
	width: 100%; box-sizing: border-box; border-radius: clamp(18px,2.5vw,28px);
	padding: clamp(22px,2.5vw,30px) clamp(20px,2.2vw,28px) clamp(18px,2vw,26px);
	display: flex; flex-direction: column; gap: clamp(14px,1.8vw,20px);
	justify-content: center;
}
.card-noise {
	position: absolute; inset: 0; pointer-events: none; opacity: 0.14;
	background-image: radial-gradient(rgba(70,46,28,0.08) 0.6px, transparent 0.6px);
	background-size: 14px 14px;
}
.pin {
	position: absolute; width: 12px; height: 12px; border-radius: 50%;
	background: linear-gradient(148deg,#c49876,#8a6248);
	box-shadow: 0 3px 8px rgba(100,64,40,0.22), inset 0 1px 0 rgba(255,255,255,0.26);
}
.pin-tr { top: 16px; right: 20px; }
.pin-bl { bottom: 16px; left: 20px; }
.drag-handle { display: none; }
.card-close, .card-mask { display: none; }

.badge-row { display: flex; justify-content: space-between; align-items: center; gap: 10px; }
.badge {
	padding: 5px 13px; border-radius: 999px;
	background: rgba(136,100,76,0.09); border: 1px solid rgba(136,100,76,0.18);
	font-family: var(--f-ui); font-size: var(--t-2xs); letter-spacing: 1.2px;
	color: var(--accent-dk); font-weight: 600;
}
.badge.olive { background: rgba(112,116,94,0.07); border-color: rgba(112,116,94,0.16); color: #5e6254; }

.c-greeting {
	display: block; font-family: var(--f-cn);
	font-size: clamp(20px,2.8vw,28px); font-weight: 400;
	color: var(--ink-faint); margin-top: 14px; letter-spacing: 2px; line-height: 1.2;
}
.c-greeting-sub {
	display: block; font-family: var(--f-cn);
	font-size: clamp(18px,2.4vw,26px); font-weight: 600;
	color: #462e1c; letter-spacing: 1.5px; margin-top: 3px; line-height: 1.4;
}
.c-desc {
	display: block; font-family: var(--f-body);
	font-size: clamp(13px,1.3vw,15px); line-height: 2.0;
	color: var(--ink-mid); margin-top: 8px; font-weight: 400;
}

.entry-tag {
	display: flex; align-items: center; gap: 8px;
	padding: 10px 14px; border-radius: 12px;
	background: rgba(136,100,76,0.06); border: 1px dashed rgba(136,100,76,0.22);
}
.et-lbl { font-family: var(--f-ui); font-size: var(--t-2xs); letter-spacing: 1.2px; color: var(--ink-faint); font-weight: 500; white-space: nowrap; }
.et-dots { display: flex; gap: 3px; flex: 1; justify-content: center; }
.et-dot  { width: 3px; height: 3px; border-radius: 50%; background: rgba(136,100,76,0.24); }
.et-val  { font-family: var(--f-display); font-size: clamp(11px,1.1vw,13px); font-style: italic; font-weight: 500; color: var(--accent-dk); letter-spacing: 0.6px; white-space: nowrap; }

.form { display: flex; flex-direction: column; gap: 12px; }
.field {
	padding: 13px 16px 11px; border-radius: 14px;
	background: rgba(255,252,248,0.56); border: 1px solid rgba(152,114,80,0.14);
	transition: background 0.30s, border-color 0.30s, box-shadow 0.30s;
}
.field.on { background: rgba(255,253,249,0.96); border-color: rgba(136,100,76,0.34); box-shadow: 0 6px 20px rgba(136,100,76,0.10); }
.field-lbl-row { display: flex; align-items: baseline; gap: 7px; margin-bottom: 8px; }
.fl { font-family: var(--f-ui); font-size: var(--t-2xs); letter-spacing: 1px; color: var(--ink-faint); font-weight: 500; }
.fl-en { font-family: var(--f-display); font-size: var(--t-2xs); font-style: italic; font-weight: 400; color: var(--ink-faint); opacity: 0.68; }
.fi {
	width: 100%; height: 34px; border: none; background: transparent;
	font-family: var(--f-body); font-size: clamp(15px,1.5vw,17px);
	font-weight: 400; color: var(--ink); letter-spacing: 0.3px;
	/* 确保input在uni-app中正常显示 */
	-webkit-appearance: none;
	appearance: none;
}
.fi:focus {
	outline: none;
}
.fline { margin-top: 8px; height: 1px; background: linear-gradient(90deg, rgba(136,100,76,0.46), transparent); }

/* 修复：确保登录按钮正常显示 */
.login-btn {
	width: 100%; height: clamp(50px,5.5vw,58px);
	border: none; border-radius: 14px;
	background: linear-gradient(142deg, #a07a60 0%, #7e5644 50%, #643830 100%);
	display: flex; align-items: center; justify-content: center; gap: 10px;
	box-shadow: 0 12px 28px rgba(100,62,38,0.24), 0 1px 0 rgba(255,255,255,0.14) inset;
	transition: transform 0.32s ease, box-shadow 0.32s ease;
	cursor: pointer;
	/* 确保按钮在所有平台上正常显示 */
	-webkit-appearance: none;
	appearance: none;
	user-select: none;
	touch-action: manipulation;
	position: relative;
	overflow: hidden;
}
.login-btn:active {
	transform: scale(0.98);
}
.login-btn::after {
	content: '';
	position: absolute;
	top: 0; left: 0; right: 0; bottom: 0;
	background: linear-gradient(rgba(255,255,255,0.1), rgba(255,255,255,0));
	pointer-events: none;
}
.lbt { font-family: var(--f-cn); font-size: clamp(14px,1.4vw,16px); font-weight: 500; letter-spacing: 2px; color: #fdf6ec; z-index: 1; }
.lba { font-family: var(--f-display); font-size: 26px; line-height: 1; color: rgba(253,246,236,0.62); font-style: italic; z-index: 1; }

.c-links { display: flex; align-items: center; justify-content: center; gap: 12px; }
.lnk { font-family: var(--f-cn); font-size: clamp(12px,1.1vw,13px); font-weight: 400; letter-spacing: 0.5px; color: var(--ink-mid); transition: color 0.28s; cursor: pointer; }
.lnk:hover { color: var(--accent-dk); }
.lnk-sep { font-family: Georgia,serif; font-size: 18px; color: rgba(136,100,76,0.38); line-height: 1; }

.c-foot { margin-top: auto; padding-top: 14px; border-top: 1px dashed rgba(136,100,76,0.18); }
.cf-quote { display: block; font-family: var(--f-display); font-size: clamp(12px,1.1vw,14px); font-style: italic; font-weight: 500; line-height: 1.85; color: var(--ink-mid); text-align: center; margin-bottom: 8px; }
.cf-copy  { display: block; font-family: var(--f-display); font-size: clamp(9px,0.9vw,11px); font-style: italic; font-weight: 400; letter-spacing: 0.5px; color: var(--ink-faint); text-align: center; }

/* ─────────────────────────────────────────────────────────
   MOB BAR  (desktop: hidden)
───────────────────────────────────────────────────────── */
.mob-bar { display: none; }

/* ═══════════════════════════════════════════════════════
   RESPONSIVE
═══════════════════════════════════════════════════════ */

/* ── Tablet ≤ 1100px ── */
@media (max-width: 1100px) {
	.mh-meta { display: none; }
	.mh-inner { grid-template-columns: 1fr; }
	.layout { grid-template-columns: 1fr; min-height: auto; gap: 16px; }
	.card-wrap { max-width: 640px; width: 100%; margin: 0 auto; }
	.card { justify-content: flex-start; }
	.story-main { grid-template-columns: minmax(0,1fr) minmax(220px,310px); }
	.stat-row { flex-wrap: wrap; }
}

/* ── Mobile ≤ 720px ── */
@media (max-width: 720px) {
	/* page: bottom padding clears the floating bar (~82px) */
	.desktop-frame { padding: 14px 14px 100px; }
	.masthead { margin-bottom: 10px; }

	.layout { display: block; }
	.story-panel { border-radius: 20px; padding: 18px 16px 20px; }
	.story-main { grid-template-columns: 1fr; gap: 14px; }
	.bp-panel { display: none; }
	.stat-row { gap: 8px; }

	/* ── Card overlay ─────────────────────────────────────
	   card-wrap: full-screen fixed layer
	   card: bottom-anchored sheet, scrollable internally
	──────────────────────────────────────────────────── */
	.card-wrap {
		position: fixed;
		top: 0; left: 0; right: 0; bottom: 0;
		z-index: 100;
		display: block;           /* override desktop flex */
		max-width: none;
		margin: 0;
		pointer-events: none;
		opacity: 0;
		transition: opacity 0.26s ease;
	}
	.card-wrap.card-visible {
		pointer-events: auto;
		opacity: 1;
	}

	/* dim backdrop */
	.card-mask {
		display: block;
		position: absolute; top: 0; left: 0; right: 0; bottom: 0;
		background: rgba(44,30,18,0.42);
		backdrop-filter: blur(10px) saturate(0.82);
		-webkit-backdrop-filter: blur(10px) saturate(0.82);
	}

	/* the sheet */
	.card {
		/* anchor to bottom, width constrained by left+right — no width:100% needed */
		position: absolute;
		left: 0; right: 0; bottom: 0;
		width: auto;                  /* let left+right do the sizing */
		box-sizing: border-box;       /* padding stays inside the box */
		/* let it be as tall as content, up to 90vh */
		height: auto;
		max-height: 90vh;
		min-height: 50vh;
		/* scroll the inner content */
		overflow-y: auto;
		-webkit-overflow-scrolling: touch;
		overscroll-behavior: contain;
		/* shape */
		border-radius: 24px 24px 0 0;
		/* spacing: top room for drag handle, bottom for safe area */
		padding: 6px 18px calc(28px + env(safe-area-inset-bottom, 16px));
		/* slide animation */
		transform: translateY(102%);
		transition: transform 0.38s cubic-bezier(0.34, 1.06, 0.64, 1);
		/* stack above mask */
		z-index: 1;
		justify-content: flex-start;
		gap: 16px;
	}
	.card-wrap.card-visible .card {
		transform: translateY(0);
	}

	/* drag pill — visible on mobile */
	.drag-handle {
		display: block;
		width: 40px; height: 4px; border-radius: 2px;
		background: rgba(136,100,76,0.26);
		margin: 0 auto 6px;
		flex-shrink: 0;
	}

	/* close button */
	.card-close {
		display: flex; align-items: center; justify-content: center;
		position: absolute; top: 14px; right: 16px;
		width: 30px; height: 30px; border-radius: 50%;
		background: rgba(136,100,76,0.10);
		font-family: var(--f-display); font-size: 18px;
		color: var(--accent-dk); z-index: 2;
		cursor: pointer;
	}

	/* tighten card internals */
	.c-greeting { margin-top: 8px; font-size: 20px; }
	.c-greeting-sub { font-size: 18px; }
	.c-desc { font-size: 13px; line-height: 1.85; }
	.login-btn { height: 52px; }

	/* ── Floating trigger bar ── */
	.mob-bar {
		display: block;
		position: fixed;
		left: 12px; right: 12px;
		bottom: calc(10px + env(safe-area-inset-bottom, 0px));
		z-index: 50;          /* lower than card-wrap (100) */
		border-radius: 18px;
		background: linear-gradient(138deg, rgba(252,249,244,0.97), rgba(234,224,208,0.97));
		border: 1px solid rgba(136,100,76,0.20);
		box-shadow: 0 14px 32px rgba(78,50,26,0.22), 0 1px 0 rgba(255,255,255,0.72) inset;
		cursor: pointer;
	}
	.mob-bar-inner {
		display: flex; align-items: center; justify-content: space-between;
		padding: 13px 18px; gap: 12px;
	}
	.mob-left { display: flex; flex-direction: column; gap: 3px; }
	.mob-kicker { font-family: var(--f-ui); font-size: 10px; letter-spacing: 2px; color: var(--ink-faint); }
	.mob-title  { font-family: var(--f-cn); font-size: 18px; font-weight: 500; letter-spacing: 1.5px; color: var(--accent-dk); }
	.mob-arr    { font-family: var(--f-display); font-size: 30px; font-style: italic; color: var(--accent); line-height: 1; }
}

/* ── XSmall ≤ 420px ── */
@media (max-width: 420px) {
	.brand-name { letter-spacing: 5px; }
	.hl-cn { font-size: 26px; }
	.stat-row { flex-direction: column; }
}
</style>
