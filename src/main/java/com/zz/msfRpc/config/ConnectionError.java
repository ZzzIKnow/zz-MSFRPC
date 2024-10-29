package com.zz.msfRpc.config;

public class ConnectionError extends RuntimeException {
    public ConnectionError(String message) {
        super(message);
    }
}
