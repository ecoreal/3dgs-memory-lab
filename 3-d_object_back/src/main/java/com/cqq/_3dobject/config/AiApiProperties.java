package com.cqq._3dobject.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Component
@ConfigurationProperties(prefix = "app.ai")
public class AiApiProperties {

    private static final String RUN_ENV_VAR = "3D_OBJECT_RUN_ENV";

    private String scheme = "http";
    private int port = 8000;
    private String serverIp;
    private String localIp;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getHunyuanSubmitUrl() {
        return buildUrl("/hunyuan3d/submit");
    }

    public String getHunyuanQueryUrl() {
        return buildUrl("/hunyuan3d/query");
    }

    public String getHunyuanFastQueryUrl() {
        return buildUrl("/hunyuan3d/query_fast");
    }

    public String getSeedreamEnhanceUrl() {
        return buildUrl("/seedream/enhance");
    }

    public String getSeedreamColorizeUrl() {
        return buildUrl("/seedream/colorize");
    }

    public String getSeed3dSubmitUrl() {
        return buildUrl("/seed3d/submit");
    }

    public String getSeed3dQueryUrl() {
        return buildUrl("/seed3d/query");
    }

    private String buildUrl(String path) {
        String normalizedScheme = StringUtils.hasText(scheme) ? scheme.trim() : "http";
        String host = resolveAiHost();
        return normalizedScheme + "://" + host + ":" + port + path;
    }

    private String resolveAiHost() {
        String runEnv = System.getenv(RUN_ENV_VAR);
        if ("SERVER".equalsIgnoreCase(runEnv) && StringUtils.hasText(serverIp)) {
            return serverIp.trim();
        }
        if (StringUtils.hasText(localIp)) {
            return localIp.trim();
        }
        return detectLocalIpv4();
    }

    private String detectLocalIpv4() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces != null && interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException ignored) {
        }
        return "127.0.0.1";
    }
}
