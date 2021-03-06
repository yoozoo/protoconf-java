package com.yoozoo.protoconf.reader;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.Txn;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.op.Op;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.WatchOption;
import com.coreos.jetcd.watch.WatchResponse;
import com.coreos.jetcd.Watch.Watcher;
import com.coreos.jetcd.watch.WatchEvent;
import com.yoozoo.protoconf.agent.AgentApplicationServiceClient;
import com.yoozoo.protoconf.agent.AgentApplicationServiceOuterClass;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.Charset;

public class EtcdReader implements ConfigurationReader.KVReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdReader.class);
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private String envkey;
    private String userName;
    private String password;
    private String[] endpoints;
    private String appToken;
    Client client;

    //    connect to etcd server
    private void connect() {
        if (client == null) {
            try {
                String env = System.getenv("etcd_envkey");
                if (env == null || env.isEmpty()) {
//            default env
                    env = "default";
                }
                envkey = "/" + env;
//        check from env var
                if (userName == null || password == null || endpoints == null) {
//            set config from protoagent
                    if (appToken != null) {
                        setConfigWithProtoagent(appToken, env);
                    } else {
//                default etcd config
                        userName = "root";
                        password = "root";
                        endpoints = new String[]{"http://localhost:2379"};
                    }
                }
                // create and authenticate etcd client
                client = Client.builder().user(ByteSequence.fromBytes(userName.getBytes(UTF8_CHARSET)))
                        .password(ByteSequence.fromBytes(password.getBytes(UTF8_CHARSET)))
                        .endpoints(endpoints).build();
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage());
            }
        }
    }

    //    default constructor
    public EtcdReader() {
        // getting etcd client username and password and endpoints from env var
        String etcdUser = System.getenv("etcd_user");
//            check etcd config in env
        if (etcdUser != null && etcdUser.matches("\\w+:\\w+")) {
            userName = System.getenv("etcd_user").split(":")[0];
            password = System.getenv("etcd_user").split(":")[1];
            endpoints = System.getenv("etcd_endpoints").split(",");
        }
    }

    // client auth by take parameters from outside
    public EtcdReader(String endpoints, String userName, String password) {
        this.endpoints = endpoints.split(",");
        this.userName = userName;
        this.password = password;

    }

    //    set config with protoagent
    private void setConfigWithProtoagent(String appToken, String env) {
        try {
//            connect to protoagent
            AgentApplicationServiceClient agent = new AgentApplicationServiceClient(appToken, env);
//            retrieve etcd config
            AgentApplicationServiceOuterClass.LogonInfoResponse resp = agent.getEtcdConfig();
            userName = resp.getUser();
            password = resp.getPassword();
            endpoints = resp.getEndpoints().split(",");
//            add default endpoint protocol as http:// if no protocol present in endpoint
            String pattern = "http(s?):\\/\\/.+";
            for(int i=0; i< endpoints.length; i++) {
                if (!endpoints[i].matches(pattern)) {
                    endpoints[i]  = "http://" + endpoints[i];
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    // get all key-values pairs for an application
    public Map<String, String> getValues(String appName, String[] keys) {
        try {
            connect();
            appName = "/" + appName;
            // initiate a transaction
            Txn txn = client.getKVClient().txn();
            for (String key : keys) {
                // for each keys retrieved from config class, get the value from etcd client
                txn = txn.Then(Op.get(ByteSequence.fromBytes((envkey + appName + "/" + key).getBytes(UTF8_CHARSET)), GetOption.DEFAULT));
            }
            List<GetResponse> responses = txn.commit().get().getGetResponses();

            HashMap<String, String> result = new HashMap<>();
            for (String key : keys) {
                result.put(key, null);
            }

            for (GetResponse response : responses) {
                if (response.getCount() == 1) {
                    // if the value can be found, put the value in the result map
                    KeyValue kv = response.getKvs().get(0);
                    result.replace(kv.getKey().toStringUtf8().substring((envkey + appName + "/").length()), kv.getValue().toStringUtf8());
                }
            }
            return result;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }

        return null;
    }

    // put a value to a key (not used for fow)
    public void setValue(String key, String value) {
        try {
            connect();
            client.getKVClient().put(
                    ByteSequence.fromBytes(key.getBytes(UTF8_CHARSET)),
                    ByteSequence.fromBytes(value.getBytes(UTF8_CHARSET))
            ).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // get a value for a key
    public String getValue(String key) {
        try {
            connect();
            // get String value
            GetResponse getResponse = client.getKVClient().get(ByteSequence.fromBytes(key.getBytes(UTF8_CHARSET))).get();
            if (getResponse.getKvs().isEmpty()) {
                // key does not exist
                return null;
            }

            return getResponse.getKvs().get(0).getValue().toStringUtf8();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }

        return null;
    }

    // watch value updates and change accordingly
    public synchronized void watchKeys(ConfigurationInterface data) {
            // start a thread to watch key prefix with app name
            new Thread(() -> {
                String appName = "/" + data.applicationName();
                Watcher watcher = null;
                try {
                    connect();
                    WatchOption watchOption = WatchOption.newBuilder().withPrefix(ByteSequence.fromBytes((envkey + appName + "/").getBytes(UTF8_CHARSET))).build();
                    watcher = client.getWatchClient().watch(ByteSequence.fromBytes((envkey + appName + "/").getBytes(UTF8_CHARSET)), watchOption);

                    while (true) {
                        Map<String, String> changeMap = new HashMap<>();

                        WatchResponse response = watcher.listen();
                        for (WatchEvent event : response.getEvents()) {
                            if (event.getEventType().equals(WatchEvent.EventType.PUT)) {
                                changeMap.put(event.getKeyValue().getKey().toStringUtf8().substring((envkey + appName + "/").length()), event.getKeyValue().getValue().toStringUtf8());
                            }
                        }

                        for (Map.Entry<String, String> entry : changeMap.entrySet()) {
                            data.addKeyChange(entry.getKey(), entry.getValue());
                        }
                    }

                } catch (Exception e) {
                    if (watcher != null) {
                        watcher.close();
                    }
                    LOGGER.error(e.getLocalizedMessage());
                }
            }).start();
    }

    @Override
    public synchronized Map<String, String> getValueWithPrefix(String prefix) {
        Map<String, String> keyValues = new HashMap<>();
        try {
            connect();
            GetOption getOption = GetOption.newBuilder().withPrefix(ByteSequence.fromBytes((prefix).getBytes(UTF8_CHARSET))).build();
            GetResponse getResponse = client.getKVClient().get(ByteSequence.fromBytes((prefix).getBytes(UTF8_CHARSET)), getOption).get();
            if (getResponse.getKvs().isEmpty()) {
                // key does not exist
                return null;
            }
            List<KeyValue> keyValueList = getResponse.getKvs();

            for (KeyValue keyValue : keyValueList) {
                keyValues.put(keyValue.getKey().toStringUtf8().substring(prefix.length()).replaceAll("/", "."), keyValue.getValue().toStringUtf8());
            }
        } catch (Exception e) {
            closeConnection();
            LOGGER.error(e.getLocalizedMessage());
        }
        return keyValues;
    }

    public synchronized void watchKeysWithPrefix(String prefix, String callBackUrl) {
        // start a thread to watch key prefix with app name
        new Thread(() -> {
            Watcher watcher = null;
            try {
                connect();
                WatchOption watchOption = WatchOption.newBuilder().withPrefix(ByteSequence.fromBytes((prefix).getBytes(UTF8_CHARSET))).build();
                watcher = client.getWatchClient().watch(ByteSequence.fromBytes((prefix).getBytes(UTF8_CHARSET)), watchOption);

                LOGGER.info("start to watch keys with prefix: "+prefix);
                while (true) {
                    WatchResponse response = watcher.listen();
                    for (WatchEvent event : response.getEvents()) {
                        if (event.getEventType().equals(WatchEvent.EventType.PUT)) {
                            LOGGER.info("key: " +event.getKeyValue().getKey().toStringUtf8() + " 's value has been changed to " + event.getKeyValue().getValue().toStringUtf8());
//                            send POST request to callBackUrl
                            CloseableHttpClient httpClient = HttpClients.createDefault();
                            HttpPost httpPost = new HttpPost(callBackUrl);
//                          json format request
                            String json = "{\"path\": \"*\"}";
                            StringEntity entity = new StringEntity(json);
                            httpPost.setEntity(entity);
                            httpPost.setHeader("Accept", "application/json");
                            httpPost.setHeader("Content-type", "application/json");

                            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
                            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                                LOGGER.error("error response from callBackUrl: " + httpResponse.getStatusLine().getReasonPhrase());
                            }

                            LOGGER.info("callbackUrl: " + callBackUrl +" has been called.");

                            httpClient.close();
                        }
                    }

                }

            } catch (Exception e) {
                if (watcher != null) {
                    watcher.close();
                }
                closeConnection();
                LOGGER.error(e.getLocalizedMessage());
            }
        }).start();
    }

    private void closeConnection() {
        if (client != null) {
            client.close();
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEndpoints(String[] endpoints) {
        this.endpoints = endpoints;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }
}

