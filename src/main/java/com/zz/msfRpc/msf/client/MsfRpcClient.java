package com.zz.msfRpc.msf.client;



import com.zz.msfRpc.msf.util.MsfRpcUtils;
import com.zz.msfRpc.config.AuthError;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MsfRpcClient {

    private String user;
    private String passwd;
    private String server;
    private int port;
    private Map<String, String> headers;
    private String token;

    public MsfRpcClient(String ip, int port, String user, String passwd) {
        this.user = user;
        this.passwd = passwd;
        this.server = ip;
        this.port = port;
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "binary/message-pack");
        this.auth();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    /**
     * 认证登录状态
     */
    public void auth() {
        System.out.println("Attempting to access token");
        byte[] options;
        try {
            options = MsfRpcUtils.packOptions("auth.login", user, passwd);
        } catch (IOException e) {
            throw new RuntimeException("Failed to pack options", e);
        }

        try {
            Map<String, Object> response = MsfRpcUtils.sendRequest(server, port, null, options);
            if (!response.containsKey("error") && "success".equals(response.get("result"))) {
                this.token = (String) response.get("token");
                System.out.println("Token received: " + this.token);
            } else {
                throw new AuthError("Authentication failed: " + response);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect", e);
        }
    }

    /**
     * 创建控制器
     * @return
     */
    public Map<String, Object> createConsole() {
        return sendCommand("console.create", token);
    }

    /**
     * 执行命令
     * @param options
     * @return
     */
    public Map<String, Object> sendCommand(Object... options) {
        byte[] packedOptions;
        try {
            packedOptions = MsfRpcUtils.packOptions(options);
        } catch (IOException e) {
            throw new RuntimeException("Failed to pack options", e);
        }

        try {
            return MsfRpcUtils.sendRequest(server, port, token, packedOptions);
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect", e);
        }
    }


    /**
     * 获取一个已获取的终端列表，【id,prompt,busy】
     */
    public Map<String, Object> listConsoles(){
        return sendCommand("console.list", token);
    }

    /**
     * 向终端中写命令
     * @param consoleId
     * @param data
     * @param process
     * @return
     */
    public Map<String, Object> writeConsole(int consoleId, String data, boolean process) {
        if (process) {
            data += "\n";
        }

        return sendCommand("console.write", token, consoleId, data);
    }
    /**
     * 获取发送命令后终端的执行结果
     */
    public Map<String, Object> readConsole(int consoleId) {
        // 确保 consoleId 是字符串
        // 发送命令并获取结果
        return sendCommand("console.read", token, consoleId);
    }

    /**
     * 销毁控制台
     * @param consoleId
     * @return
     */
    public Map<String, Object> destroyConsole(int consoleId) {
        // 确保 consoleId 是字符串
        // 发送命令并获取结果
        return sendCommand("console.read", token, consoleId);
    }

    /**
     * 获得会话
     * @return
     */
    public Map<String, Object> listSessions() {
        return sendCommand("session.list", token);
    }
    /**
     * 停止会话
     */
    public Map<String, Object> stopSession(int consoleId) {
        return sendCommand("session.stop", token,consoleId);
    }

    /**
     * meterpreter_write
     * @param sessionId
     * @param data
     * @return
     */
    public Map<String, Object> writeMeterpreter(int sessionId,String data) {
        return sendCommand("session.meterpreter_write", token,sessionId,data);
    }

    /**
     * 读取meterpreter的操作结果
     * @param sessionId
     * @param
     * @return
     */
    public Map<String, Object> readMeterpreter(int sessionId) {
        return sendCommand("session.meterpreter_read", token,sessionId);
    }

}

