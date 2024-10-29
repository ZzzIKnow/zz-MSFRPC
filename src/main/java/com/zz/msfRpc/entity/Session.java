package com.zz.msfRpc.entity;

import lombok.Data;

@Data
public class Session {
    private String type;
    private String tunnel_local;
    private String tunnel_peer;
    private String via_exploit;
    private String via_payload;
    private String desc;
    private String info;
    private String workspace;
    private String session_host;
    private int session_port; // 确保数据类型匹配
    private String target_host;
    private String username;
    private String uuid;
    private String exploit_uuid;
    private String routes;
    private String arch;
    private String platform;

    // getters and setters
}
