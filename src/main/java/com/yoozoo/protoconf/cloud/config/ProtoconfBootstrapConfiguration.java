package com.yoozoo.protoconf.cloud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.environment.EnvironmentRepositoryPropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty("spring.cloud.config.server.bootstrap")
public class ProtoconfBootstrapConfiguration {

    @EnableConfigurationProperties(ConfigServerProperties.class)
    @Import({ ProtoconfEnvironmentRepository.class })
    protected static class LocalPropertySourceLocatorConfiguration {

        @Autowired
        private ProtoconfEnvironmentRepository repository;

        @Autowired
        private ConfigClientProperties client;

        @Autowired
        private ConfigServerProperties server;

        @Bean
        public ProtoconfEnvironmentRepositoryPropertySourceLocator environmentRepositoryPropertySourceLocator() {
            return new ProtoconfEnvironmentRepositoryPropertySourceLocator(this.repository, this.client.getName(),
                    this.client.getProfile(), getDefaultLabel());
        }

        private String getDefaultLabel() {
            if (StringUtils.hasText(this.client.getLabel())) {
                return this.client.getLabel();
            } else if (StringUtils.hasText(this.server.getDefaultLabel())) {
                return this.server.getDefaultLabel();
            }
            return null;
        }

    }

}