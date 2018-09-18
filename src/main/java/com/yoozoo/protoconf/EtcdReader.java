package com.yoozoo.protoconf;

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
    Client client;

    private void init ()
    {
        // create and authenticate etcd client
        client = Client.builder().user(ByteSequence.fromBytes(userName.getBytes(UTF8_CHARSET)))
                .password(ByteSequence.fromBytes(password.getBytes(UTF8_CHARSET)))
                .endpoints(endpoints).build();
    }

//    connect etcd with environment variable config
    public EtcdReader() {
        try {
            // getting etcd client username and password and endpoints from env var
            String etcdUser = System.getenv("etcd_user");
//            check etcd config in env
            if (etcdUser != null && etcdUser.matches("\\w+:\\w+")) {
                userName = System.getenv("etcd_user").split(":")[0];
                password = System.getenv("etcd_user").split(":")[1];
                endpoints = System.getenv("etcd_endpoints").split(",");
                envkey = "/" + System.getenv("etcd_envkey");
//                init etcd connection
                init();
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }
//    TODO: connect etcd with protoagent
    public EtcdReader(String appToken, String env) {
        try {
//            connect to protoagent
            AgentApplicationServiceClient agent = new AgentApplicationServiceClient(appToken, env);
//            retrieve etcd config
            AgentApplicationServiceOuterClass.LogonInfoResponse resp = agent.getEtcdConfig();
            userName = resp.getUser();
            password = resp.getPassword();
            endpoints = resp.getEndpoints().split(",");
            envkey = "/" + env;

            init();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    // client auth by take parameters from outside
    public EtcdReader(String urls, String userName, String password, String envkey) {
        try {
            endpoints = urls.split(",");
            this.userName = userName;
            this.envkey = "/" + envkey;
            this.password = password;
            init();
        } catch (Exception e) {
            // failed to authenticate
            LOGGER.error(e.getLocalizedMessage());
        }

    }

    // get all key-values pairs for an application
    public Map<String, String> getValues(String appName, String[] keys) {
        try {
            appName = "/" + appName;
            // initiate a transaction
            Txn txn = client.getKVClient().txn();
            for (String key : keys) {
                // for each keys retrieved from config class, get the value from etcd client
                txn = txn.Then(Op.get(ByteSequence.fromBytes((envkey  + appName + "/" + key).getBytes(UTF8_CHARSET)), GetOption.DEFAULT));
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
    public void watchKeys(ConfigurationInterface data) {
        // start a thread to watch key prefix with app name
        new Thread(() -> {
            String appName = "/" + data.applicationName();
            Watcher watcher = null;
            try {
                WatchOption watchOption = WatchOption.newBuilder().withPrefix(ByteSequence.fromBytes((envkey + appName + "/").getBytes(UTF8_CHARSET))).build();
                watcher = client.getWatchClient().watch(ByteSequence.fromBytes(("").getBytes(UTF8_CHARSET)), watchOption);

                while (true) {
                    Map<String, String> changeMap = new HashMap<>();

                    WatchResponse response = watcher.listen();
                    for (WatchEvent event : response.getEvents()) {
                        if (event.getEventType().equals(WatchEvent.EventType.PUT)) {
                            changeMap.put(event.getKeyValue().getKey().toStringUtf8().substring((envkey + appName + "/").length()), event.getKeyValue().getValue().toStringUtf8());
                        }
                    }

                    for(Map.Entry<String, String> entry: changeMap.entrySet()) {
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
    public Map<String, String> getValueWithPrefix(String prefix) {
        try {
            GetResponse getResponse = client.getKVClient().get(ByteSequence.fromBytes(prefix.getBytes(UTF8_CHARSET)), GetOption.newBuilder().withPrefix(ByteSequence.fromBytes(prefix.getBytes(UTF8_CHARSET))).build()).get();
            if (getResponse.getKvs().isEmpty()) {
                // key does not exist
                return null;
            }
            Map<String, String> keyValues = new HashMap<>();
            List<KeyValue> keyValueList = getResponse.getKvs();

            for(KeyValue keyValue: keyValueList) {
                keyValues.put(keyValue.getKey().toStringUtf8(), keyValue.getValue().toStringUtf8());
            }
            return keyValues;
        }catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return null;
    }
}

