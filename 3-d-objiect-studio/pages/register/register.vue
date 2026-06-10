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
						<text class="meta-val">第七卷 · 新馆创建入口</text>
					</view>
					<view class="mh-center">
						<text class="brand-kicker">个人策展 · 兴趣社群 · 超级博物馆</text>
						<text class="brand-name">一方阁</text>
						<text class="brand-roman">Yī Fāng Gé &nbsp;·&nbsp; Personal Museum Studio</text>
					</view>
					<view class="mh-meta mh-right">
						<text class="meta-label">创建时段</text>
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
								<text class="stamp">建馆说明</text>
								<text class="stamp-en">Opening Your Gallery</text>
							</view>
							<text class="hl-cn">开始创建一座<br/>属于你兴趣世界的<br/><em>迷你博物馆</em></text>
							<view class="orn-row">
								<view class="orn-line"></view>
								<text class="orn-leaf">❧</text>
								<view class="orn-line short"></view>
							</view>
							<text class="hl-body">
								注册之后，你可以上传藏品照片，生成三维模型，自由布置虚拟房间，
								并为每件展品补充语音讲解、文字考据与个人策展说明。
							</text>
							<view class="tag-row">
								<text class="tag">藏品上传</text>
								<text class="tag">三维生成</text>
								<text class="tag">虚拟布展</text>
							</view>
						</view>

						<!-- Animated Blueprint -->
						<view class="bp-panel">
							<view class="bp-head">
								<text class="bp-title">新馆创建蓝图</text>
								<text class="bp-sub">Identity · Initialize · Curate</text>
							</view>
							<view class="bp-stage">
								<view class="scan-beam"></view>
								<view class="stage-grid"></view>
								<view class="orbit orbit-eq"></view>
								<view class="orbit orbit-pol"></view>
								<view class="orbit orbit-tlt"></view>
								<view class="wf-wrap">
									<view class="wf-ring wfr-0"></view>
									<view class="wf-ring wfr-1"></view>
									<view class="wf-ring wfr-2"></view>
								</view>
								<view class="ax ax-h"></view>
								<view class="ax ax-v"></view>
								<text class="axl axl-n">N</text>
								<text class="axl axl-e">E</text>
								<text class="axl axl-s">S</text>
								<text class="axl axl-w">W</text>
								<view class="wf-dot-wrap">
									<view class="wf-pulse"></view>
									<view class="wf-dot"></view>
								</view>
								<view class="tick t0"></view>
								<view class="tick t1"></view>
								<view class="tick t2"></view>
								<view class="tick t3"></view>
							</view>
							<text class="bp-cap">从注册身份到开启个人馆藏空间，为后续上传、布展与社群交流完成第一步准备。</text>
						</view>
					</view>

					<view class="stat-row">
						<view class="stat-chip" v-for="item in deskNotes" :key="item.label">
							<text class="stat-lbl">{{ item.label }}</text>
							<text class="stat-val">{{ item.value }}</text>
						</view>
					</view>
				</view>

				<!-- Register Card -->
				<view :class="['card-wrap', showCard ? 'card-visible' : '']">
					<view class="card-mask" @tap="showCard = false"></view>
					<view class="card">
						<view class="card-noise"></view>
						<view class="pin pin-tr"></view>
						<view class="pin pin-bl"></view>
						<view class="card-close" @tap="backToLogin">×</view>
						<view class="drag-handle"></view>

						<view class="c-header">
							<view class="badge-row">
								<text class="badge">注册表单</text>
								<text class="badge olive">创建你的个人展馆</text>
							</view>
							<text class="c-greeting-sub">欢迎加入迷你博物馆计划</text>
							<text class="c-desc">填写基础信息，创建你的策展身份，开启个人主题展馆与兴趣社群交流空间。</text>
						</view>

						<view class="entry-tag">
							<text class="et-lbl">当前入口</text>
							<view class="et-dots">
								<view class="et-dot"></view><view class="et-dot"></view><view class="et-dot"></view>
							</view>
							<text class="et-val">个人账号创建与新馆初始化</text>
						</view>

						<view class="form">
							<view :class="['field', focused==='n' ? 'on' : '']">
								<view class="field-lbl-row">
									<text class="fl">昵称</text>
									<text class="fl-en">Nickname</text>
								</view>
								<input v-model="form.nickname" placeholder="填写你的策展昵称"
									placeholder-style="color:#c2a88c;font-size:15px"
									@focus="focused='n'" @blur="focused=''" class="fi" />
								<view class="fline"></view>
							</view>

							<view :class="['field', focused==='u' ? 'on' : '']">
								<view class="field-lbl-row">
									<text class="fl">邮箱</text>
									<text class="fl-en">Email</text>
								</view>
								<input v-model="form.email" placeholder="填写用于登录的邮箱"
									placeholder-style="color:#c2a88c;font-size:15px"
									@focus="focused='u'" @blur="focused=''" class="fi" />
								<view class="fline"></view>
							</view>

							<!-- password row: two fields side by side on desktop -->
							<view class="pw-row">
								<view :class="['field field-half', focused==='p' ? 'on' : '']">
									<view class="field-lbl-row">
										<text class="fl">密码</text>
										<text class="fl-en">Password</text>
									</view>
									<input v-model="form.password" password placeholder="设置密码"
										placeholder-style="color:#c2a88c;font-size:15px"
										@focus="focused='p'" @blur="focused=''" class="fi" />
									<view class="fline"></view>
								</view>
								<view :class="['field field-half', focused==='cp' ? 'on' : '']">
									<view class="field-lbl-row">
										<text class="fl">确认</text>
										<text class="fl-en">Confirm</text>
									</view>
									<input v-model="form.confirmPassword" password placeholder="再次输入"
										placeholder-style="color:#c2a88c;font-size:15px"
										@focus="focused='cp'" @blur="focused=''" class="fi" />
									<view class="fline"></view>
								</view>
							</view>
						</view>

						<button class="login-btn" @tap="handleRegister">
							<text class="lbt">创建我的迷你博物馆</text>
							<text class="lba">›</text>
						</button>

						<view class="c-links">
							<text class="lnk" @tap="backToLogin">我已有账号，返回登录</text>
						</view>

						<view class="c-foot">
							<text class="cf-quote">"注册后即可开启你的个人主题展馆，加入兴趣社群。"</text>
							<text class="cf-copy">© 2026 迷你博物馆计划 · Mini Museum Project</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- Mobile floating trigger — hidden when card is open -->
		<view class="mob-bar" v-show="!showCard" @tap="showCard = true">
			<view class="mob-bar-inner">
				<view class="mob-left">
					<text class="mob-kicker">进入建馆流程</text>
					<text class="mob-title">创建你的迷你博物馆</text>
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
			form: { nickname: '', email: '', password: '', confirmPassword: '' },
			focused: '',
			showCard: false,
			deskNotes: [
				{ label: '馆藏方向', value: '自由定义' },
				{ label: '展陈方式', value: '虚拟房间' },
				{ label: '社群连接', value: '互访交流' }
			]
		};
	},
	computed: {
		currentDateLabel() {
			const n = new Date();
			return `${n.getFullYear()}.${String(n.getMonth()+1).padStart(2,'0')}.${String(n.getDate()).padStart(2,'0')}`;
		}
	},
	methods: {
		async handleRegister() {
			const { nickname, email, password, confirmPassword } = this.form;
			if (!nickname || !email || !password || !confirmPassword)
				return uni.showToast({ title: '请完善注册信息', icon: 'none' });
			if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email))
				return uni.showToast({ title: '邮箱格式不正确', icon: 'none' });
			if (password.length < 6)
				return uni.showToast({ title: '密码至少需要 6 位', icon: 'none' });
			if (password !== confirmPassword)
				return uni.showToast({ title: '两次输入的密码不一致', icon: 'none' });

			uni.showLoading({ title: '正在创建展馆身份...', mask: true });
			try {
				const res = await uni.request({
					url: `${BASE_URL}/users/register`, method: 'POST',
					header: { 'Content-Type': 'application/json' },
					data: { nickname: nickname.trim(), email: email.trim().toLowerCase(), password },
					timeout: 10000
				});
				uni.hideLoading();
				if (res.statusCode === 201) {
					const d = res.data;
					uni.showToast({ title: '注册成功，欢迎加入', icon: 'success', duration: 1500 });
					if (d.uid) uni.setStorageSync('userInfo', { uid: d.uid, nickname: d.nickname, email: d.email, avatarUrl: d.avatarUrl, bio: d.bio });
					setTimeout(() => this.backToLogin(), 1500);
				} else if (res.statusCode === 400) {
					uni.showToast({ title: typeof res.data==='string' ? res.data : '注册失败', icon: 'none', duration: 2000 });
				} else {
					throw new Error(`HTTP ${res.statusCode}`);
				}
			} catch(e) {
				uni.hideLoading();
				uni.showToast({ title: e.errMsg?.includes('timeout') ? '请求超时，请检查网络' : '网络连接失败', icon: 'none', duration: 2500 });
			}
		},
		backToLogin() { uni.navigateBack(); }
	}
};
</script>

<style scoped>
/* ─────────────────────────────────────────────────────────
   FONTS  (identical to login)
───────────────────────────────────────────────────────── */
@import url('https://fonts.googleapis.com/css2?family=Lora:ital,wght@0,400;0,500;0,600;1,400;1,500&family=Noto+Serif+SC:wght@300;400;500;600&family=Cormorant+Garamond:ital,wght@0,400;0,500;1,300;1,400;1,500&family=Playfair+Display:ital,wght@0,500;0,600;1,500&display=swap');

/* ─────────────────────────────────────────────────────────
   KEYFRAMES  (identical to login)
───────────────────────────────────────────────────────── */
@keyframes spin-eq  { from{transform:translate(-50%,-50%) rotateY(0deg)}   to{transform:translate(-50%,-50%) rotateY(360deg)} }
@keyframes spin-pol { from{transform:translate(-50%,-50%) rotateX(72deg) rotateZ(0deg)}    to{transform:translate(-50%,-50%) rotateX(72deg) rotateZ(360deg)} }
@keyframes spin-tlt { from{transform:translate(-50%,-50%) rotateX(-50deg) rotateY(30deg) rotateZ(0deg)} to{transform:translate(-50%,-50%) rotateX(-50deg) rotateY(30deg) rotateZ(-360deg)} }
@keyframes wfr0     { from{transform:translate(-50%,-50%) rotateY(0deg)}   to{transform:translate(-50%,-50%) rotateY(360deg)} }
@keyframes wfr1     { from{transform:translate(-50%,-50%) rotateX(60deg) rotateZ(0deg)}    to{transform:translate(-50%,-50%) rotateX(60deg) rotateZ(360deg)} }
@keyframes wfr2     { from{transform:translate(-50%,-50%) rotateX(-60deg) rotateZ(0deg)}   to{transform:translate(-50%,-50%) rotateX(-60deg) rotateZ(360deg)} }
@keyframes scan     { 0%{top:8%;opacity:0} 10%{opacity:.5} 90%{opacity:.5} 100%{top:92%;opacity:0} }
@keyframes dot-pulse{ 0%,100%{box-shadow:0 0 0 0 rgba(138,94,78,0),0 0 0 5px rgba(138,94,78,.12)} 50%{box-shadow:0 0 10px 2px rgba(138,94,78,.18),0 0 0 10px rgba(138,94,78,.05)} }
@keyframes ripple   { 0%{transform:translate(-50%,-50%) scale(.6);opacity:.5} 100%{transform:translate(-50%,-50%) scale(1.8);opacity:0} }

/* ─────────────────────────────────────────────────────────
   TOKENS  (identical to login)
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

	--f-display: 'Cormorant Garamond','Playfair Display',Georgia,serif;
	--f-heading: 'Playfair Display','Cormorant Garamond','Noto Serif SC',Georgia,serif;
	--f-body:    'Lora','Noto Serif SC','STSong','Songti SC',Georgia,serif;
	--f-cn:      'Noto Serif SC','STSong','Songti SC','Source Han Serif SC',Georgia,serif;
	--f-ui:      'PingFang SC','Hiragino Sans GB','Microsoft YaHei',sans-serif;

	--t-2xs:   clamp(9px,.9vw,10px);
	--t-xs:    clamp(10px,1.0vw,12px);
	--t-sm:    clamp(12px,1.2vw,14px);
	--t-md:    clamp(14px,1.4vw,16px);
	--t-lg:    clamp(17px,1.8vw,20px);
	--t-2xl:   clamp(34px,4.2vw,46px);
	--t-brand: clamp(38px,4.8vw,54px);

	min-height: 100vh;
	position: relative; overflow: hidden;
	background:
		radial-gradient(circle at 7% 8%,  rgba(255,250,240,.88), transparent 24%),
		radial-gradient(circle at 93% 91%, rgba(198,176,148,.34), transparent 26%),
		linear-gradient(152deg, #ede4d3 0%, #f6f0e4 46%, #e9e0cc 100%);
	font-family: var(--f-ui); color: var(--ink);
}

.paper-grain,.dust { position:absolute; inset:0; pointer-events:none; }
.paper-grain {
	opacity:.24;
	background-image:
		radial-gradient(rgba(98,70,46,.07) .8px,transparent .8px),
		radial-gradient(rgba(255,255,255,.26) .6px,transparent .6px);
	background-size:18px 18px,26px 26px;
	background-position:0 0,10px 12px;
}
.dust-left  { background:radial-gradient(circle at 9% 14%,rgba(182,144,108,.13),transparent 22%); }
.dust-right { background:radial-gradient(circle at 92% 85%,rgba(136,102,74,.09),transparent 24%); }

/* ─────────────────────────────────────────────────────────
   FRAME
───────────────────────────────────────────────────────── */
.desktop-frame {
	position:relative; z-index:1;
	max-width:1400px; margin:0 auto;
	padding:clamp(16px,2.5vw,28px) clamp(16px,2.5vw,32px) clamp(20px,3vw,32px);
}

/* ─────────────────────────────────────────────────────────
   MASTHEAD
───────────────────────────────────────────────────────── */
.masthead { margin-bottom:clamp(14px,2vw,22px); }
.mh-rule { height:1px; background:linear-gradient(90deg,transparent 0%,var(--line-mid) 15%,var(--line-mid) 85%,transparent 100%); }
.mh-rule.thick { height:2px; margin-bottom:2px; }
.mh-inner {
	display:grid; grid-template-columns:1fr auto 1fr;
	align-items:center; gap:clamp(12px,2vw,22px);
	padding:clamp(12px,1.6vw,20px) 6px clamp(10px,1.4vw,18px);
}
.mh-meta { display:flex; flex-direction:column; gap:5px; }
.mh-right { text-align:right; align-items:flex-end; }
.meta-label { font-family:var(--f-ui); font-size:var(--t-2xs); letter-spacing:1.8px; color:var(--ink-faint); font-weight:500; text-transform:uppercase; }
.meta-val   { font-family:var(--f-body); font-size:var(--t-sm); color:var(--ink-mid); }
.mh-center  { text-align:center; }
.brand-kicker { display:block; font-family:var(--f-ui); font-size:var(--t-2xs); letter-spacing:2.5px; color:var(--ink-faint); margin-bottom:8px; }
.brand-name   { display:block; font-family:var(--f-cn); font-size:var(--t-brand); letter-spacing:clamp(5px,.8vw,12px); font-weight:500; color:var(--accent-dk); line-height:1; text-shadow:0 1px 0 rgba(255,255,255,.55); }
.brand-roman  { display:block; font-family:var(--f-display); font-size:clamp(11px,1.1vw,13px); letter-spacing:clamp(2px,.4vw,4px); color:var(--ink-faint); margin-top:8px; font-style:italic; font-weight:400; }

/* ─────────────────────────────────────────────────────────
   LAYOUT
───────────────────────────────────────────────────────── */
.layout {
	display:grid;
	grid-template-columns:minmax(0,1.15fr) minmax(340px,440px);
	gap:clamp(14px,2vw,24px); align-items:stretch;
	min-height:calc(100vh - clamp(120px,14vw,165px));
}
.story-panel,.card {
	position:relative;
	background:linear-gradient(168deg,rgba(253,249,244,.97),rgba(243,235,222,.98));
	border:1px solid rgba(158,120,84,.14);
	box-shadow:0 2px 0 rgba(255,255,255,.76) inset, 0 clamp(14px,3vw,30px) clamp(28px,6vw,66px) rgba(82,54,28,.09);
	overflow:hidden;
}

/* ─────────────────────────────────────────────────────────
   STORY PANEL
───────────────────────────────────────────────────────── */
.story-panel {
	border-radius:clamp(18px,2.5vw,28px);
	padding:clamp(20px,2.5vw,30px);
	display:flex; flex-direction:column; gap:clamp(16px,2vw,24px);
}
.panel-noise { position:absolute; inset:0; pointer-events:none; opacity:.14; background-image:radial-gradient(rgba(76,50,28,.08) .7px,transparent .7px); background-size:16px 16px; }
.corner { position:absolute; width:56px; height:56px; pointer-events:none; background:linear-gradient(135deg,rgba(196,174,146,.32),transparent); }
.corner.tl { top:0;left:0;clip-path:polygon(0 0,100% 0,0 100%); }
.corner.br { bottom:0;right:0;clip-path:polygon(100% 0,100% 100%,0 100%); }

.story-main {
	flex:1; display:grid;
	grid-template-columns:minmax(0,1fr) minmax(260px,380px);
	gap:clamp(14px,2vw,22px); align-items:center;
}

/* Headline */
.headline-block { display:flex; flex-direction:column; padding:4px 10px 4px 2px; }
.stamp-row { display:flex; align-items:center; gap:10px; margin-bottom:clamp(10px,1.5vw,16px); }
.stamp { padding:5px 13px; border-radius:999px; background:rgba(136,100,76,.09); border:1px solid rgba(136,100,76,.18); font-family:var(--f-ui); font-size:var(--t-2xs); letter-spacing:1.2px; color:var(--accent-dk); font-weight:600; }
.stamp-en { font-family:var(--f-display); font-size:var(--t-sm); font-style:italic; font-weight:400; color:var(--ink-faint); }
.hl-cn { font-family:var(--f-cn); font-size:var(--t-2xl); line-height:1.5; color:#4a3120; font-weight:500; letter-spacing:1px; margin-bottom:clamp(12px,1.6vw,20px); }
.hl-cn em { font-style:normal; color:var(--accent-dk); border-bottom:1.5px solid rgba(136,100,76,.42); padding-bottom:1px; }
.orn-row { display:flex; align-items:center; gap:10px; margin-bottom:clamp(12px,1.6vw,18px); }
.orn-line { flex:1; height:1px; background:linear-gradient(90deg,rgba(136,100,76,.44),transparent); }
.orn-line.short { max-width:56px; }
.orn-leaf { font-family:Georgia,serif; font-size:15px; color:var(--accent); opacity:.52; line-height:1; }
.hl-body { font-family:var(--f-body); font-size:var(--t-md); line-height:2.05; color:var(--ink-mid); font-weight:400; margin-bottom:clamp(14px,1.8vw,22px); }
.tag-row { display:flex; gap:8px; flex-wrap:wrap; }
.tag { padding:5px 13px; border-radius:999px; font-family:var(--f-cn); font-size:var(--t-2xs); font-weight:400; letter-spacing:.8px; color:var(--ink-mid); background:rgba(136,100,76,.06); border:1px solid rgba(136,100,76,.15); }

/* ─────────────────────────────────────────────────────────
   BLUEPRINT — ANIMATED  (identical to login)
───────────────────────────────────────────────────────── */
.bp-panel {
	padding:clamp(14px,1.8vw,20px); border-radius:clamp(14px,1.8vw,20px);
	background:
		linear-gradient(180deg,rgba(228,222,212,.78),rgba(212,202,188,.82)),
		repeating-linear-gradient(0deg,rgba(92,112,122,.07) 0 1px,transparent 1px 28px),
		repeating-linear-gradient(90deg,rgba(92,112,122,.07) 0 1px,transparent 1px 28px);
	border:1px solid rgba(92,112,122,.16);
	box-shadow:inset 0 0 0 1px rgba(255,255,255,.24);
}
.bp-head { display:flex; justify-content:space-between; align-items:baseline; gap:10px; margin-bottom:14px; }
.bp-title { font-family:var(--f-cn); font-size:var(--t-lg); color:#4d6472; font-weight:500; letter-spacing:1.5px; }
.bp-sub   { font-family:var(--f-display); font-size:var(--t-2xs); font-style:italic; color:rgba(76,98,112,.72); }

.bp-stage {
	position:relative; border-radius:14px;
	height:clamp(180px,22vw,252px);
	background:radial-gradient(circle at 50% 48%,rgba(228,240,248,.30),rgba(186,202,212,.12) 60%,transparent 80%);
	border:1px solid rgba(92,112,122,.12);
	overflow:hidden; margin-bottom:12px;
	perspective:520px; perspective-origin:50% 50%;
}
.stage-grid { position:absolute; inset:0; pointer-events:none; background-image:radial-gradient(circle,rgba(92,112,122,.20) 1px,transparent 1px); background-size:22px 22px; opacity:.55; }
.scan-beam { position:absolute; left:10%; right:10%; height:1px; background:linear-gradient(90deg,transparent,rgba(138,168,184,.72) 40%,rgba(180,210,224,.90) 50%,rgba(138,168,184,.72) 60%,transparent); box-shadow:0 0 8px 2px rgba(138,168,184,.28); animation:scan 4.5s ease-in-out infinite; pointer-events:none; }
.orbit { position:absolute; top:50%; left:50%; border-radius:50%; border:1px solid rgba(88,110,122,.40); }
.orbit-eq  { width:200px;height:200px; animation:spin-eq  28s linear infinite; }
.orbit-pol { width:162px;height:162px; border-color:rgba(88,110,122,.32); animation:spin-pol 18s linear infinite; }
.orbit-tlt { width:162px;height:162px; border-color:rgba(88,110,122,.28); animation:spin-tlt 14s linear infinite; }
.wf-wrap { position:absolute; top:50%; left:50%; width:130px; height:130px; transform:translate(-50%,-50%); }
.wf-ring { position:absolute; top:50%; left:50%; border-radius:50%; border:1px solid rgba(100,124,136,.50); }
.wfr-0 { width:130px;height:130px; animation:wfr0 22s linear infinite; }
.wfr-1 { width:100px;height:100px; animation:wfr1 16s linear infinite; }
.wfr-2 { width: 70px;height: 70px; animation:wfr2 12s linear infinite; }
.ax { position:absolute; background:rgba(88,110,122,.26); pointer-events:none; }
.ax-h { top:50%;left:12%;right:12%;height:1px; }
.ax-v { left:50%;top:12%;bottom:12%;width:1px; }
.tick { position:absolute; background:rgba(88,110,122,.36); width:1px; height:8px; }
.t0{left:12%;top:calc(50% - 4px)} .t1{right:12%;top:calc(50% - 4px)} .t2{top:12%;left:calc(50% - 4px);width:8px;height:1px} .t3{bottom:12%;left:calc(50% - 4px);width:8px;height:1px}
.axl { position:absolute; font-family:var(--f-display); font-size:10px; font-style:italic; font-weight:400; letter-spacing:1px; color:rgba(74,96,110,.64); pointer-events:none; }
.axl-n{top:6%;left:50%;transform:translateX(-50%)} .axl-e{right:5%;top:50%;transform:translateY(-50%)} .axl-s{bottom:6%;left:50%;transform:translateX(-50%)} .axl-w{left:5%;top:50%;transform:translateY(-50%)}
.wf-dot-wrap { position:absolute; top:50%; left:50%; transform:translate(-50%,-50%); width:14px; height:14px; }
.wf-dot   { position:absolute;top:50%;left:50%;width:8px;height:8px;transform:translate(-50%,-50%);border-radius:50%;background:#8a5e4e;animation:dot-pulse 3s ease-in-out infinite; }
.wf-pulse { position:absolute;top:50%;left:50%;width:24px;height:24px;border-radius:50%;border:1px solid rgba(138,94,78,.32);animation:ripple 3s ease-out infinite;pointer-events:none; }
.bp-cap { display:block; font-family:var(--f-body); font-size:clamp(11px,1.1vw,13px); font-weight:400; line-height:1.9; color:#58707a; }

/* Stat row */
.stat-row { display:flex; gap:10px; }
.stat-chip { flex:1; padding:12px 14px; border-radius:14px; background:rgba(255,252,246,.52); border:1px dashed rgba(136,100,76,.18); display:flex; flex-direction:column; gap:6px; }
.stat-lbl { font-family:var(--f-ui); font-size:var(--t-2xs); letter-spacing:1.2px; color:var(--ink-faint); }
.stat-val { font-family:var(--f-heading); font-size:clamp(15px,1.8vw,19px); font-weight:600; color:var(--accent-dk); }

/* ─────────────────────────────────────────────────────────
   REGISTER CARD
───────────────────────────────────────────────────────── */
.card-wrap { position:relative; display:flex; align-items:stretch; }
.card {
	width:100%; box-sizing:border-box;
	border-radius:clamp(18px,2.5vw,28px);
	padding:clamp(20px,2.2vw,28px) clamp(18px,2vw,26px) clamp(16px,1.8vw,22px);
	display:flex; flex-direction:column; gap:clamp(12px,1.5vw,16px);
	justify-content:center;
}
.card-noise { position:absolute;inset:0;pointer-events:none;opacity:.14;background-image:radial-gradient(rgba(70,46,28,.08) .6px,transparent .6px);background-size:14px 14px; }
.pin { position:absolute;width:12px;height:12px;border-radius:50%;background:linear-gradient(148deg,#c49876,#8a6248);box-shadow:0 3px 8px rgba(100,64,40,.22),inset 0 1px 0 rgba(255,255,255,.26); }
.pin-tr{top:16px;right:20px} .pin-bl{bottom:16px;left:20px}
.drag-handle { display:none; }
.card-close,.card-mask { display:none; }

/* Badge + greeting */
.badge-row { display:flex; justify-content:space-between; align-items:center; gap:10px; }
.badge { padding:5px 13px; border-radius:999px; background:rgba(136,100,76,.09); border:1px solid rgba(136,100,76,.18); font-family:var(--f-ui); font-size:var(--t-2xs); letter-spacing:1.2px; color:var(--accent-dk); font-weight:600; }
.badge.olive { background:rgba(112,116,94,.07); border-color:rgba(112,116,94,.16); color:#5e6254; }
.c-greeting-sub { display:block; font-family:var(--f-cn); font-size:clamp(18px,2.4vw,26px); font-weight:600; color:#462e1c; letter-spacing:1.5px; margin-top:12px; line-height:1.4; }
.c-desc { display:block; font-family:var(--f-body); font-size:clamp(13px,1.2vw,14px); line-height:1.95; color:var(--ink-mid); margin-top:7px; font-weight:400; }

/* Entry tag */
.entry-tag { display:flex; align-items:center; gap:8px; padding:9px 14px; border-radius:12px; background:rgba(136,100,76,.06); border:1px dashed rgba(136,100,76,.22); }
.et-lbl { font-family:var(--f-ui); font-size:var(--t-2xs); letter-spacing:1.2px; color:var(--ink-faint); font-weight:500; white-space:nowrap; }
.et-dots { display:flex; gap:3px; flex:1; justify-content:center; }
.et-dot  { width:3px; height:3px; border-radius:50%; background:rgba(136,100,76,.24); }
.et-val  { font-family:var(--f-display); font-size:clamp(11px,1.1vw,12px); font-style:italic; font-weight:500; color:var(--accent-dk); letter-spacing:.6px; white-space:nowrap; }

/* Form */
.form { display:flex; flex-direction:column; gap:10px; }

/* password pair: side by side */
.pw-row { display:flex; gap:10px; }
.field-half { flex:1; min-width:0; }

.field { padding:12px 15px 10px; border-radius:14px; background:rgba(255,252,248,.56); border:1px solid rgba(152,114,80,.14); transition:background .30s,border-color .30s,box-shadow .30s; box-sizing:border-box; }
.field.on { background:rgba(255,253,249,.96); border-color:rgba(136,100,76,.34); box-shadow:0 5px 18px rgba(136,100,76,.10); }
.field-lbl-row { display:flex; align-items:baseline; gap:6px; margin-bottom:7px; }
.fl    { font-family:var(--f-ui); font-size:var(--t-2xs); letter-spacing:1px; color:var(--ink-faint); font-weight:500; }
.fl-en { font-family:var(--f-display); font-size:var(--t-2xs); font-style:italic; font-weight:400; color:var(--ink-faint); opacity:.68; }
.fi { width:100%; height:32px; border:none; background:transparent; font-family:var(--f-body); font-size:clamp(14px,1.4vw,16px); font-weight:400; color:var(--ink); letter-spacing:.3px; }
.fline { margin-top:7px; height:1px; background:linear-gradient(90deg,rgba(136,100,76,.46),transparent); }

/* Button */
.login-btn {
	width:100%; height:clamp(48px,5vw,56px); border:none; border-radius:14px;
	background:linear-gradient(142deg,#a07a60 0%,#7e5644 50%,#643830 100%);
	display:flex; align-items:center; justify-content:center; gap:10px;
	box-shadow:0 12px 28px rgba(100,62,38,.24),0 1px 0 rgba(255,255,255,.14) inset;
	transition:transform .32s ease,box-shadow .32s ease;
}
.login-btn:hover { transform:translateY(-2px); box-shadow:0 16px 36px rgba(100,62,38,.30),0 1px 0 rgba(255,255,255,.14) inset; }
.lbt { font-family:var(--f-cn); font-size:clamp(14px,1.4vw,16px); font-weight:500; letter-spacing:2px; color:#fdf6ec; }
.lba { font-family:var(--f-display); font-size:26px; line-height:1; color:rgba(253,246,236,.62); font-style:italic; }

/* Links */
.c-links { display:flex; align-items:center; justify-content:center; }
.lnk { font-family:var(--f-cn); font-size:clamp(12px,1.1vw,13px); font-weight:400; letter-spacing:.5px; color:var(--ink-mid); transition:color .28s; }
.lnk:hover { color:var(--accent-dk); }

/* Foot */
.c-foot { margin-top:auto; padding-top:12px; border-top:1px dashed rgba(136,100,76,.18); }
.cf-quote { display:block; font-family:var(--f-display); font-size:clamp(11px,1.1vw,13px); font-style:italic; font-weight:500; line-height:1.85; color:var(--ink-mid); text-align:center; margin-bottom:7px; }
.cf-copy  { display:block; font-family:var(--f-display); font-size:clamp(9px,.9vw,11px); font-style:italic; font-weight:400; letter-spacing:.5px; color:var(--ink-faint); text-align:center; }

/* ─────────────────────────────────────────────────────────
   MOB BAR
───────────────────────────────────────────────────────── */
.mob-bar { display:none; }

/* ═══════════════════════════════════════════════════════
   RESPONSIVE
═══════════════════════════════════════════════════════ */

/* Tablet ≤ 1100px */
@media (max-width:1100px) {
	.mh-meta { display:none; }
	.mh-inner { grid-template-columns:1fr; }
	.layout { grid-template-columns:1fr; min-height:auto; gap:16px; }
	.card-wrap { max-width:640px; width:100%; margin:0 auto; }
	.card { justify-content:flex-start; }
	.story-main { grid-template-columns:minmax(0,1fr) minmax(220px,310px); }
	.stat-row { flex-wrap:wrap; }
}

/* Mobile ≤ 720px */
@media (max-width:720px) {
	.desktop-frame { padding:14px 14px 100px; }
	.masthead { margin-bottom:10px; }
	.layout { display:block; }
	.story-panel { border-radius:20px; padding:18px 16px 20px; }
	.story-main { grid-template-columns:1fr; gap:14px; }
	.bp-panel { display:none; }
	.stat-row { gap:8px; }

	/* pw-row: stack vertically on mobile */
	.pw-row { flex-direction:column; gap:10px; }

	/* Card → bottom sheet */
	.card-wrap {
		position:fixed; top:0;left:0;right:0;bottom:0;
		z-index:100; display:block; max-width:none; margin:0;
		pointer-events:none; opacity:0; transition:opacity .26s ease;
	}
	.card-wrap.card-visible { pointer-events:auto; opacity:1; }

	.card-mask {
		display:block; position:absolute; top:0;left:0;right:0;bottom:0;
		background:rgba(44,30,18,.42);
		backdrop-filter:blur(10px) saturate(.82);
		-webkit-backdrop-filter:blur(10px) saturate(.82);
	}
	.card {
		position:absolute; left:0;right:0;bottom:0;
		width:auto; box-sizing:border-box;
		height:auto; max-height:92vh; min-height:60vh;
		overflow-y:auto; -webkit-overflow-scrolling:touch; overscroll-behavior:contain;
		border-radius:24px 24px 0 0;
		padding:6px 18px calc(28px + env(safe-area-inset-bottom,16px));
		transform:translateY(102%);
		transition:transform .38s cubic-bezier(.34,1.06,.64,1);
		z-index:1; justify-content:flex-start; gap:14px;
	}
	.card-wrap.card-visible .card { transform:translateY(0); }

	.drag-handle { display:block; width:40px; height:4px; border-radius:2px; background:rgba(136,100,76,.26); margin:0 auto 4px; flex-shrink:0; }
	.card-close  { display:flex; align-items:center; justify-content:center; position:absolute; top:14px; right:16px; width:30px; height:30px; border-radius:50%; background:rgba(136,100,76,.10); font-family:var(--f-display); font-size:18px; color:var(--accent-dk); z-index:2; }

	.c-greeting-sub { font-size:18px; margin-top:8px; }
	.c-desc { font-size:13px; }
	.login-btn { height:50px; }

	/* Floating trigger */
	.mob-bar {
		display:block; position:fixed;
		left:12px;right:12px;bottom:calc(10px + env(safe-area-inset-bottom,0px));
		z-index:50; border-radius:18px;
		background:linear-gradient(138deg,rgba(252,249,244,.97),rgba(234,224,208,.97));
		border:1px solid rgba(136,100,76,.20);
		box-shadow:0 14px 32px rgba(78,50,26,.22),0 1px 0 rgba(255,255,255,.72) inset;
	}
	.mob-bar-inner { display:flex; align-items:center; justify-content:space-between; padding:13px 18px; gap:12px; }
	.mob-left { display:flex; flex-direction:column; gap:3px; }
	.mob-kicker { font-family:var(--f-ui); font-size:10px; letter-spacing:2px; color:var(--ink-faint); }
	.mob-title  { font-family:var(--f-cn); font-size:18px; font-weight:500; letter-spacing:1.5px; color:var(--accent-dk); }
	.mob-arr    { font-family:var(--f-display); font-size:30px; font-style:italic; color:var(--accent); line-height:1; }
}

/* XSmall ≤ 420px */
@media (max-width:420px) {
	.brand-name { letter-spacing:5px; }
	.hl-cn { font-size:26px; }
	.stat-row { flex-direction:column; }
}
</style>