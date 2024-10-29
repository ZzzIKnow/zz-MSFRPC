package com.zz.msfRpc.config;

public class AuthError extends RuntimeException {
    public AuthError(String message) {
        super(message);
    }

}