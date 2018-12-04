package com.yoozoo.protoconf.cloud.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("protoconf")
public class ProtoconfAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ProtoconfEnvironmentRepository.class)
    public ProtoconfEnvironmentRepository protoconfEnvironmentRepository() {
        return new ProtoconfEnvironmentRepository();
    }

}
