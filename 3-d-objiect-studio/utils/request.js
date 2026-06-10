// utils/request.js
export const BASE_URL = 'http://8.162.0.21/api';

const request = (options) => {
    return new Promise((resolve, reject) => {
        const token = uni.getStorageSync('token');
        uni.request({
            url: BASE_URL + options.url,
            method: options.method || 'GET',
            data: options.data || {},
            header: {
                ...options.header,
                ...(token ? { 'Authorization': 'Bearer ' + token } : {})
            },
            success: (res) => {
                if (res.statusCode === 200 || res.statusCode === 201) {
                    resolve(res.data);
                } else if (res.statusCode === 401) {
                    uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' });
                    uni.removeStorageSync('token');
                    uni.removeStorageSync('userInfo');
                    setTimeout(() => {
                        uni.navigateTo({ url: '/pages/login/login' });
                    }, 1500);
                    reject('Unauthorized');
                } else {
                    // res.data 可能是 Object，提取可读字符串
                    const msg = (typeof res.data === 'string' && res.data)
                        ? res.data
                        : res.data?.error || res.data?.message || `请求失败（${res.statusCode}）`;
                    uni.showToast({ title: msg, icon: 'none' });
                    reject(res.data);
                }
            },
            fail: (err) => {
                uni.showToast({ title: '网络连接失败', icon: 'none' });
                reject(err);
            }
        });
    });
};

export default request;
