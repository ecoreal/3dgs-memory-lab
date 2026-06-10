package com.cqq._3dobject.dto;

import java.util.Map;

public class LoginResponse {

    private String accessToken;
    private int expiresIn;          // 秒
    private String refreshToken;
    private int refreshExpiresIn;   // 秒
    private Map<String, Object> userInfo;

    public LoginResponse() {}

    public LoginResponse(String accessToken, int expiresIn,
                         String refreshToken, int refreshExpiresIn,
                         Map<String, Object> userInfo) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.refreshExpiresIn = refreshExpiresIn;
        this.userInfo = userInfo;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public int getExpiresIn() { return expiresIn; }
    public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public int getRefreshExpiresIn() { return refreshExpiresIn; }
    public void setRefreshExpiresIn(int refreshExpiresIn) { this.refreshExpiresIn = refreshExpiresIn; }

    public Map<String, Object> getUserInfo() { return userInfo; }
    public void setUserInfo(Map<String, Object> userInfo) { this.userInfo = userInfo; }
}
