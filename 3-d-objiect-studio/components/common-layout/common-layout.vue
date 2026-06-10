<template>
  <view class="app">
    <!-- 纸纹背景层 -->
    <view class="bg-grain"></view>
    <view class="bg-dust g1"></view>
    <view class="bg-dust g2"></view>

    <!-- ── 桌面端侧边栏 ── -->
    <aside :class="['sidebar', sidebarCollapsed ? 'collapsed' : '']">

      <!-- Logo 区 -->
      <view class="logo-area">
        <view class="logo-emblem">
          <image src="/static/icons/logo.svg" mode="aspectFit" style="width:36px;height:36px;display:block;"/>
        </view>
        <view class="logo-text" v-if="!sidebarCollapsed">
          <text class="logo-name">一方阁</text>
          <text class="logo-sub">Personal Museum Studio</text>
        </view>
        <view class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          <image
            :src="sidebarCollapsed ? '/static/icons/arrow-right.svg' : '/static/icons/arrow-left.svg'"
            mode="aspectFit"
            style="width:11px;height:11px;display:block;"
          />
        </view>
      </view>

      <!-- 导航分隔 -->
      <view class="nav-sep" v-if="!sidebarCollapsed">
        <view class="nav-sep-line"></view>
        <text class="nav-sep-txt">Navigation</text>
        <view class="nav-sep-line"></view>
      </view>
      <view class="nav-sep-simple" v-else></view>

      <!-- 导航列表 -->
      <nav class="nav">
        <view
          v-for="(item, i) in menuItems" :key="i"
          :class="['nav-item', activePath === item.path ? 'active' : '']"
          @click="handleNav(item.path)">
          <view class="nav-icon-wrap">
            <image
              :src="activePath === item.path ? item.iconActive : item.icon"
              mode="aspectFit"
              style="width:14px;height:14px;display:block;flex-shrink:0;"
            />
            <view v-if="activePath === item.path" class="active-ring"></view>
          </view>
          <view v-if="!sidebarCollapsed" class="nav-content">
            <text class="nav-name">{{ item.name }}</text>
          </view>
          <view v-if="activePath === item.path && !sidebarCollapsed" class="nav-arrow">
            <image src="/static/icons/arrow-nav.svg" mode="aspectFit" style="width:8px;height:8px;display:block;"/>
          </view>
          <view v-if="activePath === item.path" class="nav-active-bar"></view>
        </view>
      </nav>

      <!-- 底部用户区 -->
      <view class="sidebar-foot">
        <view class="sys-status" v-if="!sidebarCollapsed">
          <view class="status-row">
            <view class="status-dot live"></view>
            <text class="status-txt">系统在线</text>
          </view>
          <view class="status-row" style="margin-top:4px">
            <view class="status-dot idle"></view>
            <text class="status-txt">http://localhost:8080</text>
          </view>
        </view>
        <view class="sys-status-mini" v-else>
          <view class="status-dot live"></view>
        </view>

        <view class="foot-rule"></view>

        <view :class="['user-row', sidebarCollapsed ? 'mini' : '']">
          <view class="user-ava">
            <image :src="userInfo.avatarUrl || 'https://api.dicebear.com/7.x/shapes/svg?seed=Yifanglou'" mode="aspectFill" class="ava-img"/>
            <view class="ava-shine"></view>
            <view class="ava-pip"></view>
          </view>
          <view v-if="!sidebarCollapsed" class="user-meta">
            <text class="user-name">{{ userInfo.nickname || '策展人' }}</text>
            <text class="user-sub">个人展馆 · Pro</text>
          </view>
          <view v-if="!sidebarCollapsed" class="user-btns">
            <view class="u-btn" @click="handleNotif">
              <image src="/static/icons/notif.svg" mode="aspectFit" style="width:12px;height:12px;display:block;"/>
            </view>
            <view class="u-btn danger" @click="handleLogout">
              <image src="/static/icons/logout.svg" mode="aspectFit" style="width:12px;height:12px;display:block;"/>
            </view>
          </view>
          <view v-if="sidebarCollapsed" class="u-btn danger" @click="handleLogout">
            <image src="/static/icons/logout.svg" mode="aspectFit" style="width:11px;height:11px;display:block;"/>
          </view>
        </view>
      </view>
    </aside>

    <!-- 移动端遮罩 -->
    <view v-if="mobileMenuOpen" class="mob-overlay" @click="mobileMenuOpen = false"></view>

    <!-- 主区域 -->
    <section class="main">
      <header class="top-bar">
        <view class="top-bar-left">
          <view class="hamburger" @click="mobileMenuOpen = !mobileMenuOpen">
            <image src="/static/icons/hamburger.svg" mode="aspectFit" style="width:17px;height:17px;display:block;"/>
          </view>
          <view class="mob-brand">
            <text class="mob-brand-name">一方阁</text>
          </view>
        </view>
      </header>

      <main class="content">
        <view class="content-frame">
          <view class="fc tl"></view>
          <view class="fc tr"></view>
          <view class="fc bl"></view>
          <view class="fc br"></view>
          <view class="frame-header">
            <view class="fh-seal"></view>
            <text class="fh-title">{{ pageTitle }}</text>
            <view class="fh-ornament">
              <view class="fh-orn-line"></view>
              <text class="fh-orn-glyph">❧</text>
              <view class="fh-orn-line short"></view>
            </view>
            <text class="fh-sub">Workspace</text>
            <view class="fh-rule"></view>
          </view>
          <view class="frame-body">
            <slot></slot>
          </view>
        </view>
      </main>
    </section>

    <!-- 移动端抽屉 -->
    <aside :class="['mob-sidebar', mobileMenuOpen ? 'open' : '']">
      <view class="logo-area" style="margin-bottom:20px">
        <view class="logo-emblem">
          <image src="/static/icons/logo.svg" mode="aspectFit" style="width:32px;height:32px;display:block;"/>
        </view>
        <view class="logo-text">
          <text class="logo-name">一方阁</text>
          <text class="logo-sub">Personal Museum Studio</text>
        </view>
      </view>

      <nav class="nav">
        <view
          v-for="(item, i) in menuItems" :key="'m'+i"
          :class="['nav-item', activePath === item.path ? 'active' : '']"
          @click="handleNav(item.path); mobileMenuOpen = false">
          <view class="nav-icon-wrap">
            <image
              :src="activePath === item.path ? item.iconActive : item.icon"
              mode="aspectFit"
              style="width:14px;height:14px;display:block;flex-shrink:0;"
            />
            <view v-if="activePath === item.path" class="active-ring"></view>
          </view>
          <view class="nav-content">
            <text class="nav-name">{{ item.name }}</text>
          </view>
          <view v-if="activePath === item.path" class="nav-active-bar"></view>
        </view>
      </nav>

      <view class="sidebar-foot" style="margin-top:auto">
        <view class="foot-rule"></view>
        <view class="user-row">
          <view class="user-ava">
            <image :src="userInfo.avatarUrl || 'https://api.dicebear.com/7.x/shapes/svg?seed=Yifanglou'" mode="aspectFill" class="ava-img"/>
            <view class="ava-shine"></view>
          </view>
          <view class="user-meta">
            <text class="user-name">{{ userInfo.nickname || '策展人' }}</text>
            <text class="user-sub">个人展馆 · Pro</text>
          </view>
          <view class="user-btns">
            <view class="u-btn danger" @click="handleLogout">
              <image src="/static/icons/logout.svg" mode="aspectFit" style="width:12px;height:12px;display:block;"/>
            </view>
          </view>
        </view>
      </view>
    </aside>

  </view>
</template>

<script>
export default {
  name: 'CommonLayout',
  props: { pageTitle: { type: String, default: '工作台' } },
  data() {
    return {
      activePath: '',
      userInfo: { nickname: '', avatarUrl: '' },
      sidebarCollapsed: false,
      mobileMenuOpen: false,
      menuItems: [
        {
          name: '藏品仓库',
          path: '/pages/home/home',
          icon: '/static/icons/home.svg',
          iconActive: '/static/icons/home-active.svg'
        },
        {
          name: '造物间',
          path: '/pages/workshop/workshop',
          icon: '/static/icons/workshop.svg',
          iconActive: '/static/icons/workshop-active.svg'
        },
        {
          name: '展签台',
          path: '/pages/editor/editor',
          icon: '/static/icons/editor.svg',
          iconActive: '/static/icons/editor-active.svg'
        },
        // {
        //   name: '场景选择',
        //   path: '/pages/scene/scene',
        //   icon: '/static/icons/empty-frame.svg',
        //   iconActive: '/static/icons/empty-frame.svg'
        // },
        {
          name: '超级博物馆广场',
          path: '/pages/square/square',
          icon: '/static/icons/square.svg',
          iconActive: '/static/icons/square-active.svg'
        },
        {
          name: '策展工作室',
          path: '/pages/profile/profile',
          icon: '/static/icons/profile.svg',
          iconActive: '/static/icons/profile-active.svg'
        }
      ]
    };
  },
  mounted() {
    this.initActiveMenu();
    this.loadUserInfo();
  },
  methods: {
    loadUserInfo() {
      const info = uni.getStorageSync('userInfo');
      if (info) this.userInfo = info;
    },
    handleNotif() {
      uni.showToast({ title: '暂无新通知', icon: 'none' });
    },
    handleLogout() {
      uni.showModal({
        title: '离开展馆',
        content: '确认退出当前账号？',
        confirmColor: '#664832',
        success: (res) => {
          if (res.confirm) {
            uni.removeStorageSync('token');
            uni.removeStorageSync('userInfo');
            uni.showToast({ title: '已安全退出', icon: 'none' });
            setTimeout(() => uni.reLaunch({ url: '/pages/login/login' }), 800);
          }
        }
      });
    },
    initActiveMenu() {
      const pages = getCurrentPages();
      if (pages.length > 0) this.activePath = '/' + (pages[pages.length - 1].route || '');
    },
    handleNav(path) {
      if (this.activePath === path) return;
      uni.redirectTo({ url: path });
    }
  }
};
</script>

<style>
/* ─────────────────────────────────────────────────────────
   FONTS
───────────────────────────────────────────────────────── */
@import url('https://fonts.googleapis.com/css2?family=Lora:ital,wght@0,400;0,500;0,600;1,400&family=Noto+Serif+SC:wght@300;400;500;600&family=Cormorant+Garamond:ital,wght@0,400;0,500;1,300;1,400&family=Playfair+Display:ital,wght@0,500;0,600;1,500&display=swap');

page { margin:0; padding:0; height:100%; background:#ede4d3; }

/* ─────────────────────────────────────────────────────────
   KEYFRAMES
───────────────────────────────────────────────────────── */
@keyframes dot-live {
  0%,100% { box-shadow:0 0 2px rgba(102,72,50,.30); transform:scale(1); }
  50%     { box-shadow:0 0 7px rgba(102,72,50,.70); transform:scale(1.45); }
}
@keyframes ava-shine {
  0%   { transform:translateY(-100%); }
  100% { transform:translateY(200%); }
}

/* ─────────────────────────────────────────────────────────
   ROOT
───────────────────────────────────────────────────────── */
.app {
  --paper:       #f5f0e8;
  --paper-off:   #faf7f2;
  --paper-deep:  #ede4d3;
  --ink:         #3b2c1e;
  --ink-mid:     #72604e;
  --ink-faint:   #a69180;
  --accent:      #8a6850;
  --accent-dk:   #664832;
  --gold:        #c49050;
  --line:        rgba(118,88,60,0.16);
  --line-mid:    rgba(96,68,42,0.26);
  --line-strong: rgba(96,68,42,0.38);
  --shadow:      rgba(82,54,28,0.09);

  --f-display: 'Cormorant Garamond','Playfair Display',Georgia,serif;
  --f-heading: 'Playfair Display','Cormorant Garamond','Noto Serif SC',Georgia,serif;
  --f-body:    'Lora','Noto Serif SC','STSong','Songti SC',Georgia,serif;
  --f-cn:      'Noto Serif SC','STSong','Songti SC','Source Han Serif SC',Georgia,serif;
  --f-ui:      'PingFang SC','Hiragino Sans GB','Microsoft YaHei',sans-serif;

  --sb-w:   218px;
  --sb-col: 58px;

  display:flex; height:100vh; width:100vw; overflow:hidden;
  font-family:var(--f-body); color:var(--ink);
  background:
    radial-gradient(circle at 6% 8%,  rgba(255,250,240,.82), transparent 22%),
    radial-gradient(circle at 94% 92%, rgba(198,176,148,.30), transparent 24%),
    linear-gradient(150deg, #ede4d3 0%, #f5efe2 46%, #e8dfcc 100%);
  position:relative;
}

.bg-grain {
  position:absolute; inset:0; pointer-events:none; z-index:0; opacity:.22;
  background-image:
    radial-gradient(rgba(96,68,44,.07) .8px,transparent .8px),
    radial-gradient(rgba(255,255,255,.24) .6px,transparent .6px);
  background-size:18px 18px,26px 26px;
  background-position:0 0,10px 12px;
}
.bg-dust { position:absolute; pointer-events:none; z-index:0; border-radius:50%; filter:blur(90px); opacity:.12; }
.bg-dust.g1 { width:52vw; height:52vw; bottom:-14%; left:4%; background:radial-gradient(circle,#d4a574,#c49050); }
.bg-dust.g2 { width:32vw; height:32vw; top:-8%;  right:4%; background:radial-gradient(circle,#8a6850,#664832); }

/* ─────────────────────────────────────────────────────────
   SIDEBAR
───────────────────────────────────────────────────────── */
.sidebar {
  width:var(--sb-w); flex-shrink:0; z-index:20; position:relative;
  background:linear-gradient(180deg, rgba(252,249,244,.97), rgba(242,234,220,.98));
  border-right:1px solid var(--line-mid);
  box-shadow:2px 0 0 rgba(255,255,255,.70) inset;
  display:flex; flex-direction:column;
  padding:20px 12px 16px;
  transition:width .35s cubic-bezier(.16,1,.3,1);
  overflow:hidden;
}
.sidebar::before {
  content:''; position:absolute; top:0; left:0; right:0; height:3px;
  background:repeating-linear-gradient(90deg, var(--accent-dk) 0, var(--accent-dk) 7px, transparent 7px, transparent 14px);
  opacity:.30;
}
.sidebar::after {
  content:''; position:absolute; inset:0; pointer-events:none; opacity:.12;
  background-image:radial-gradient(rgba(76,50,28,.08) .7px,transparent .7px);
  background-size:14px 14px;
}
.sidebar.collapsed { width:var(--sb-col); }

/* ── Logo area ── */
.logo-area {
  display:flex; align-items:center; gap:11px;
  padding:6px 4px 16px; position:relative; z-index:1;
  border-bottom:1px dashed var(--line-mid);
  margin-bottom:14px;
}
.logo-emblem {
  width:38px; height:38px; min-width:38px; min-height:38px;
  flex-shrink:0; display:flex; align-items:center; justify-content:center;
}
.logo-text { display:flex; flex-direction:column; gap:3px; flex:1; min-width:0; }
.logo-name {
  display:block; font-family:var(--f-cn);
  font-size:20px; font-weight:500; color:var(--accent-dk);
  letter-spacing:3px; line-height:1;
  text-shadow:0 1px 0 rgba(255,255,255,.55);
  white-space:nowrap;
}
.logo-sub {
  display:block; font-family:var(--f-display); font-style:italic; font-weight:400;
  font-size:9px; color:var(--ink-faint); letter-spacing:1.5px; white-space:nowrap;
}
.collapse-btn {
  margin-left:auto; flex-shrink:0; align-self:flex-start; margin-top:2px;
  width:22px; height:22px; min-width:22px; min-height:22px;
  border-radius:4px;
  background:rgba(136,100,76,.07); border:1px solid var(--line);
  display:flex; align-items:center; justify-content:center;
  cursor:pointer; transition:.2s;
}
.collapse-btn:hover { background:rgba(136,100,76,.14); }

/* ── Nav separator ── */
.nav-sep { display:flex; align-items:center; gap:7px; margin-bottom:10px; padding:0 3px; }
.nav-sep-line { flex:1; height:1px; background:var(--line); }
.nav-sep-txt { font-family:var(--f-display); font-style:italic; font-size:8px; color:var(--ink-faint); letter-spacing:2px; white-space:nowrap; }
.nav-sep-simple { height:1px; background:var(--line); margin:0 3px 12px; }

/* ── Nav items ── */
.nav { display:flex; flex-direction:column; gap:2px; flex:1; position:relative; z-index:1; }

.nav-item {
  display:flex; align-items:center; gap:9px;
  padding:8px 8px 8px 12px; border-radius:6px; position:relative;
  color:var(--ink-faint); cursor:pointer;
  border:1px solid transparent;
  transition:all .24s cubic-bezier(.16,1,.3,1);
}
.nav-item:hover {
  color:var(--ink); border-color:var(--line);
  background:rgba(136,100,76,.05); transform:translateX(2px);
}
.nav-item.active {
  color:var(--accent-dk); border-color:rgba(102,72,50,.20);
  background:rgba(102,72,50,.07);
}

.nav-icon-wrap {
  width:28px; height:28px; min-width:28px; min-height:28px;
  flex-shrink:0; border-radius:5px;
  display:flex; align-items:center; justify-content:center; position:relative;
  background:rgba(136,100,76,.05); border:1px solid var(--line);
  transition:all .24s;
}
.nav-item:hover .nav-icon-wrap {
  background:rgba(136,100,76,.10); border-color:var(--line-mid);
}
.nav-item.active .nav-icon-wrap {
  background:rgba(102,72,50,.12); border-color:rgba(102,72,50,.28);
}

.active-ring {
  position:absolute;
  top:-3px; right:-3px; bottom:-3px; left:-3px;
  border-radius:8px;
  border:1px dashed rgba(102,72,50,.35);
  pointer-events:none;
  box-sizing:border-box;
}

.nav-content { display:flex; align-items:center; flex:1; min-width:0; overflow:hidden; }
.nav-name { font-family:var(--f-cn); font-size:13px; font-weight:400; color:inherit; white-space:nowrap; }
.nav-item.active .nav-name { font-weight:500; }

.nav-arrow {
  display:flex; align-items:center; flex-shrink:0;
  width:14px; height:14px; min-width:14px;
}

.nav-active-bar {
  position:absolute; left:0; top:22%; bottom:22%;
  width:2.5px; border-radius:0 1.5px 1.5px 0;
  background:linear-gradient(180deg, transparent, var(--accent-dk), transparent);
}

/* ── Sidebar footer ── */
.sidebar-foot { flex-shrink:0; display:flex; flex-direction:column; padding-top:4px; position:relative; z-index:1; }

.sys-status {
  background:rgba(136,100,76,.05); border:1px dashed rgba(136,100,76,.18);
  border-radius:6px; padding:8px 10px; margin-bottom:10px;
}
.sys-status-mini { display:flex; justify-content:center; padding:6px 0 10px; }
.status-row { display:flex; align-items:center; gap:7px; }

.status-dot {
  width:5px; height:5px; min-width:5px; min-height:5px; flex-shrink:0;
  border-radius:50%; background:var(--accent-dk);
}
.status-dot.live { animation:dot-live 2.5s ease-in-out infinite; }
.status-dot.idle { background:rgba(136,100,76,.35); }
.status-txt { font-family:var(--f-display); font-style:italic; font-size:9px; color:var(--ink-faint); letter-spacing:.8px; }

.foot-rule { height:1px; background:var(--line-mid); margin-bottom:10px; opacity:.65; }

.user-row {
  display:flex; align-items:center; gap:8px;
  padding:8px 9px; border-radius:6px;
  background:rgba(136,100,76,.04); border:1px solid var(--line);
  transition:border-color .2s;
}
.user-row:hover { border-color:var(--line-mid); }
.user-row.mini { justify-content:center; padding:7px 0; background:transparent; border-color:transparent; flex-direction:column; gap:5px; }

.user-ava {
  width:30px; height:30px; min-width:30px; min-height:30px;
  border-radius:5px; overflow:hidden; position:relative; flex-shrink:0;
  border:1px solid rgba(136,100,76,.24);
}
.ava-img { width:100%; height:100%; display:block; }
.ava-shine {
  position:absolute; inset:0;
  background:linear-gradient(180deg,transparent 30%,rgba(102,72,50,.14) 50%,transparent 70%);
  animation:ava-shine 4s linear infinite;
}
.ava-pip {
  position:absolute; top:-2px; right:-2px;
  width:6px; height:6px; min-width:6px; min-height:6px;
  border-radius:50%;
  background:var(--accent-dk); border:1.5px solid var(--paper-off);
  animation:dot-live 2.5s ease-in-out infinite;
}

.user-meta { display:flex; flex-direction:column; gap:1px; flex:1; min-width:0; }
.user-name {
  display:block; font-family:var(--f-cn); font-size:12px; font-weight:500;
  color:var(--ink); white-space:nowrap; overflow:hidden; text-overflow:ellipsis;
}
.user-sub {
  display:block; font-family:var(--f-display); font-style:italic;
  font-size:9px; color:var(--accent-dk); letter-spacing:.8px;
}
.user-btns { display:flex; align-items:center; gap:5px; flex-shrink:0; }
.u-btn {
  width:26px; height:26px; min-width:26px; min-height:26px;
  border-radius:5px; display:flex; align-items:center; justify-content:center;
  background:rgba(136,100,76,.05); border:1px solid var(--line);
  cursor:pointer; transition:.2s; flex-shrink:0;
}
.u-btn:hover { background:rgba(136,100,76,.12); border-color:var(--line-mid); }
.u-btn.danger:hover { background:rgba(120,40,20,.08); border-color:rgba(120,40,20,.24); }

/* ─────────────────────────────────────────────────────────
   MAIN AREA
───────────────────────────────────────────────────────── */
.main { flex:1; display:flex; flex-direction:column; padding:20px; z-index:10; min-width:0; }

.top-bar { display:flex; align-items:center; margin-bottom:16px; flex-shrink:0; }
.top-bar-left { display:flex; align-items:center; gap:12px; }
.hamburger {
  display:none;
  width:34px; height:34px; min-width:34px; min-height:34px;
  align-items:center; justify-content:center;
  background:rgba(136,100,76,.06); border:1px solid var(--line);
  border-radius:5px; cursor:pointer; flex-shrink:0;
}
.mob-brand { display:none; }
.mob-brand-name { font-family:var(--f-cn); font-size:18px; font-weight:500; color:var(--accent-dk); letter-spacing:2px; }

.content { flex:1; display:flex; min-height:0; }

.content-frame {
  flex:1; position:relative;
  background:linear-gradient(168deg, rgba(252,249,244,.96), rgba(242,234,220,.97));
  border:1px solid var(--line-mid); border-radius:8px; overflow:hidden;
  box-shadow:0 2px 0 rgba(255,255,255,.72) inset, 0 clamp(8px,2vw,20px) clamp(16px,4vw,44px) var(--shadow);
}

.fc { position:absolute; width:14px; height:14px; z-index:2; border-style:solid; border-color:rgba(102,72,50,.28); }
.fc.tl { top:0;    left:0;  border-width:1.5px 0 0 1.5px; }
.fc.tr { top:0;    right:0; border-width:1.5px 1.5px 0 0; }
.fc.bl { bottom:0; left:0;  border-width:0 0 1.5px 1.5px; }
.fc.br { bottom:0; right:0; border-width:0 1.5px 1.5px 0; }

.frame-header {
  display:flex; align-items:center; gap:10px; padding:10px 18px;
  border-bottom:1px dashed var(--line);
  background:rgba(136,100,76,.03);
}
.fh-seal {
  width:7px; height:7px; min-width:7px; min-height:7px; flex-shrink:0;
  border-radius:50%; background:var(--gold); opacity:.80;
}
.fh-title { font-family:var(--f-cn); font-size:13px; font-weight:500; color:var(--ink); letter-spacing:1px; white-space:nowrap; }
.fh-ornament { display:flex; align-items:center; gap:6px; }
.fh-orn-line { width:18px; height:1px; background:linear-gradient(90deg, var(--line-mid), transparent); }
.fh-orn-line.short { width:10px; }
.fh-orn-glyph { font-family:Georgia,serif; font-size:12px; color:var(--accent); opacity:.45; line-height:1; }
.fh-sub { font-family:var(--f-display); font-style:italic; font-weight:400; font-size:10px; color:var(--ink-faint); letter-spacing:1.5px; white-space:nowrap; }
.fh-rule { flex:1; height:1px; background:linear-gradient(90deg, var(--line), transparent); }

.frame-body {
  padding:20px; height:calc(100% - 42px);
  overflow-y:auto; overflow-x:hidden;
  scrollbar-width:thin; scrollbar-color:rgba(136,100,76,.14) transparent;
}
.frame-body::-webkit-scrollbar { width:3px; }
.frame-body::-webkit-scrollbar-thumb { background:rgba(136,100,76,.20); border-radius:2px; }
.frame-body::-webkit-scrollbar-thumb:hover { background:rgba(136,100,76,.38); }

/* ─────────────────────────────────────────────────────────
   MOBILE
───────────────────────────────────────────────────────── */
.mob-overlay {
  display:none; position:fixed; inset:0;
  background:rgba(28,18,8,.55); z-index:40; backdrop-filter:blur(6px);
}
.mob-sidebar {
  display:none; position:fixed; left:0; top:0; bottom:0; width:242px;
  z-index:50;
  background:linear-gradient(180deg, rgba(252,249,244,.99), rgba(242,234,220,.99));
  border-right:1px solid var(--line-mid);
  padding:22px 13px 18px; flex-direction:column;
  transform:translateX(-100%);
  transition:transform .36s cubic-bezier(.16,1,.3,1);
  box-shadow:4px 0 20px rgba(82,54,28,.12);
}
.mob-sidebar::before {
  content:''; position:absolute; top:0; left:0; right:0; height:3px;
  background:repeating-linear-gradient(90deg,var(--accent-dk) 0,var(--accent-dk) 7px,transparent 7px,transparent 14px);
  opacity:.30;
}
.mob-sidebar.open { transform:translateX(0); }

/* ─────────────────────────────────────────────────────────
   RESPONSIVE
───────────────────────────────────────────────────────── */
@media (max-width: 768px) {
  .sidebar     { display:none; }
  .hamburger   { display:flex; }
  .mob-brand   { display:block; }
  .mob-overlay { display:block; }
  .mob-sidebar { display:flex; }
  .main        { padding:14px 14px 16px; }
  .top-bar     { margin-bottom:12px; }
}

@media (min-width: 769px) and (max-width: 1100px) {
  .sidebar { width:var(--sb-col); }
  .logo-text, .nav-content, .nav-arrow, .nav-sep,
  .sys-status, .user-meta, .user-btns, .collapse-btn { display:none; }
  .nav-sep-simple { display:block; }
  .nav-item { justify-content:center; padding:9px 0; }
  .user-row { background:transparent; border-color:transparent; padding:6px 0; justify-content:center; }
  .nav-active-bar { display:none; }
}

@media (min-width: 1101px) {
  .sys-status-mini { display:none; }
  .nav-sep-simple  { display:none; }
  .hamburger       { display:none; }
  .mob-brand       { display:none; }
  .mob-overlay     { display:none !important; }
  .mob-sidebar     { display:none !important; }
}
</style>