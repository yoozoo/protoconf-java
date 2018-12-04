package com.yoozoo.protoconf.cloud.config;

import com.yoozoo.protoconf.reader.ConfigurationReader;
import com.yoozoo.protoconf.reader.EtcdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
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
    @NotEmpty
    private String token;

    //  protoconf app name
    @NotEmpty
    private String appName;

//    watch keys change call back url
    private String watchUrl;

    private final static String DEFAULT_PROFILE= "default";
    private final static String KEY_SEPARATOR = "/";

    private List<String> watchList = new ArrayList<>();

    public ProtoconfEnvironmentRepository() {
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
//    get kvs from common config and watch for changes
        Map<String, String> kvs = this.getKvs(this.token, DEFAULT_PROFILE, this.appName);
        //        get kvs with app specific config, label is token
        if (label != null) {
//    get kvs from app config and watch for changes
            Map<String, String> kvs1 = this.getKvs(label, profile, application);
//            replace common config with app config if exists
            for (Map.Entry<String, String> entry: kvs1.entrySet()) {
                kvs.put(entry.getKey(), entry.getValue());
            }
            //        watch keys
            this.watchKeys(label, profile, application);
        } else {
            this.watchKeys(this.token, DEFAULT_PROFILE, this.appName);
        }

        Environment environment = new Environment(application, profile);
        if (!kvs.isEmpty()) {
            environment.add(new PropertySource("ProtoconfPropertySource:", kvs));
        }
        return environment;
    }

    private Map<String, String> getKvs(String token, String env, String appName) {
        // connect to etcd
        EtcdReader etcdReader = new EtcdReader();
        etcdReader.setAppToken(token);
        ConfigurationReader configurationReader = new ConfigurationReader(etcdReader);
        //  get kvs by /env/appName/ prefix with common cloud config
        String prefix = KEY_SEPARATOR + env + KEY_SEPARATOR + appName + KEY_SEPARATOR;
        Map<String, String> kvs = configurationReader.getValuesWithPrefix(prefix);

        return kvs;
    }

    private void watchKeys(String token, String env, String appName) {
        String prefix = KEY_SEPARATOR + env + KEY_SEPARATOR + appName + KEY_SEPARATOR;
        //        check watch url is set
        if (this.watchUrl != null && !this.watchUrl.isEmpty() && !this.watchList.contains(prefix)) {
            // connect to etcd
            EtcdReader etcdReader = new EtcdReader();
            etcdReader.setAppToken(token);
            ConfigurationReader configurationReader = new ConfigurationReader(etcdReader);
            //  get kvs by /env/appName/ prefix with common cloud config
            configurationReader.watchKeysWithPrefix(prefix, this.watchUrl);
            this.watchList.add(prefix);
        }

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

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setWatchUrl(String watchUrl) {
        this.watchUrl = watchUrl;
    }

}
