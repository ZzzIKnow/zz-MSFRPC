package com.zz.msfRpc.entity;

import lombok.Data;

@Data
public class Console {
    /**
     * 控制台的唯一标识
     */


    private String id;
    /**
     * 控制台会话的提示符
     */

    private String prompt;
    /**
     * 控制台是否在执行命令
     */

    private  String  busy;


}
