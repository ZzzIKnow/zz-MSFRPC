package com.zz.msfRpc.config;

import com.zz.msfRpc.msf.client.MsfRpcClient;
import com.zz.msfRpc.properties.MSfRpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@EnableConfigurationProperties(MSfRpcProperties.class)
@Slf4j
public class MsfRpcServiceAutoConfiguration {
//    private static final Logger logger = LoggerFactory.getLogger(MsfRpcServiceAutoConfiguration.class);
    private  MSfRpcProperties mSfRpcProperties;
    // //通过构造方法注入配置属性对象
    public MsfRpcServiceAutoConfiguration(MSfRpcProperties mSfRpcProperties) {
        this.mSfRpcProperties = mSfRpcProperties;
        log.info("MsfRpcServiceAutoConfiguration initialized with properties: {}", mSfRpcProperties);
    }

    //实例化HelloService并载入Spring IoC容器
    @Bean
    @ConditionalOnMissingBean
    public MsfRpcClient GetInstanceMsfService(){
        log.info("Creating MsfRpcClient instance with properties: {}", mSfRpcProperties);
        return new MsfRpcClient(mSfRpcProperties.getServer(),
                mSfRpcProperties.getPort(),
                mSfRpcProperties.getUser(),
                mSfRpcProperties.getPasswd());
    }

}
