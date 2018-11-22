package com.yoozoo.protoconf.cloud.config;

import com.yoozoo.protoconf.reader.ConfigurationReader;
import com.yoozoo.protoconf.reader.EtcdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;

import java.util.Map;

@ConfigurationProperties("spring.cloud.config.server.protoconf")
public class ProtoconfEnvironmentRepository implements EnvironmentRepository, Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoconfEnvironmentRepository.class);

    private int order = Ordered.LOWEST_PRECEDENCE;

    /**
     * etcd endpoints. Defaults to http://shenshui-test.uuzu.com:2379
     */
    private String[] endPoints = {"http://shenshui-test.uuzu.com:2379"};

    // etcd user. default is root
    private String user = "root";

    // etcd password. default is root
    private String password = "root";

    // protoconf app token
    private String token;

    public ProtoconfEnvironmentRepository() {
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        // connect to etcd
        EtcdReader etcdReader = new EtcdReader();
        if (token != null) {
            etcdReader.setAppToken(token);
        } else {
            etcdReader.setUserName(user);
            etcdReader.setPassword(password);
            etcdReader.setEndpoints(endPoints);
        }
        ConfigurationReader configurationReader = new ConfigurationReader(etcdReader);
        //  get kvs by /env/appName/ prefix
        Map<String, String> kvs = configurationReader.getValuesWithPrefix("/" + profile + "/" + application + "/");

        Environment environment = new Environment(application, profile);
        if (!kvs.isEmpty()) {
            environment.add(new PropertySource("ProtoconfPropertySource:", kvs));
        }
        return environment;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setEndPoints(String[] endPoints) {
        this.endPoints = endPoints;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
