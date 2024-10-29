package com.zz.msfRpc.properties;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "msf")
@Slf4j
public class MSfRpcProperties {

    private String user;
    private String passwd;
    private String server;
    private int port;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "MSfRpcProperties{" +
                "user='" + user + '\'' +
                ", passwd='" + passwd + '\'' +
                ", server='" + server + '\'' +
                ", port=" + port +
                '}';
    }
}
