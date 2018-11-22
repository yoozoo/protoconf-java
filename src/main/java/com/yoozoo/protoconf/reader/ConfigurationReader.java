package com.yoozoo.protoconf.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ConfigurationReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationReader.class);

    interface KVReader {
        Map<String, String> getValues(String appName, String[] key);

        String getValue(String key);

        void watchKeys(ConfigurationInterface data);

        void setValue(String key, String value);

        Map<String, String> getValueWithPrefix(String prefix);
    }

    private KVReader kv_reader;

    public ConfigurationReader(KVReader reader) {
        kv_reader = reader;
    }

    //    fill in the fields in the java config class
    public boolean config(ConfigurationInterface data) {
        String[] keys = data.validKeys();
        String appName = data.applicationName();
        Map<String, String> v = kv_reader.getValues(appName, keys);

        for (Map.Entry<String, String> entry : v.entrySet()) {
            String value = entry.getValue();
            if(value ==null) {
                value = data.getDefaultValue(entry.getKey());
            }
            if (value == null) {
//                failed to found value
                LOGGER.error("failed to found value for key: " + entry.getKey());
                return false;
            }
            if (!data.setValue(entry.getKey(), value)) {
//                failed to set value
                LOGGER.error("failed to set value for key: " + entry.getKey());
                return false;
            }
        }
        return true;
    }

    public String getValue(String key) {
        return kv_reader.getValue(key);
    }

    public void watchKeys(ConfigurationInterface data) {
        kv_reader.watchKeys(data);
    }

    public void setValue(String key, String value) {
        kv_reader.setValue(key, value);
    }

    public Map<String, String> getValuesWithPrefix(String prefix) {
        return kv_reader.getValueWithPrefix(prefix);
    }

}
