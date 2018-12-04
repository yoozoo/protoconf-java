package com.yoozoo.protoconf.cloud.config;

import java.util.Map;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

public class ProtoconfEnvironmentRepositoryPropertySourceLocator implements PropertySourceLocator {

    private EnvironmentRepository repository;
    private String name;
    private String profiles;
    private String label;

    public ProtoconfEnvironmentRepositoryPropertySourceLocator(EnvironmentRepository repository,
                                                      String name, String profiles, String label) {
        this.repository = repository;
        this.name = name;
        this.profiles = profiles;
        this.label = label;
    }

    @Override
    public org.springframework.core.env.PropertySource<?> locate(Environment environment) {
        CompositePropertySource composite = new CompositePropertySource("configService");
        for (PropertySource source : repository.findOne(name, profiles, label)
                .getPropertySources()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) source.getSource();
            composite.addPropertySource(new MapPropertySource(source.getName(), map));
        }
        return composite;
    }

}