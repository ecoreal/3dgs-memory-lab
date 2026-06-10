<script>
	export default {
		onLaunch: function() { console.log('App Launch') },
		onShow: function() { console.log('App Show') },
		onHide: function() { console.log('App Hide') }
	}
</script>

<style>
	/* 全局样式：确保 body 和 html 撑满屏幕 */
	html, body, #app {
		height: 100%;
		margin: 0;
		padding: 0;
		background-color: #080808;
	}
</style>