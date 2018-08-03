package com.yoozoo.protoconf;

import com.yoozoo.protoconf.ChangeListener;
import com.yoozoo.protoconf.ConfigurationReader;
import com.yoozoo.protoconf.EtcdReader;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConfigurationReaderTest {
    @Test
    public void testConfig() throws InterruptedException {
        ConfigurationReader configurationReader = new ConfigurationReader(new EtcdReader());

        configurationReader.setValue("/PROD/test/msg/id", "1");
        configurationReader.setValue("/PROD/test/name", "testName");
        assertTrue(configurationReader.config(Configuration.instance()));

        Configuration.instance().watch_name(new ChangeListener() {
            @Override
            public void onChange(String newValue) {
                System.out.println("name has been changed to: " + newValue);
            }
        });

        Configuration.instance().get_msg().watch_id(new ChangeListener() {
            @Override
            public void onChange(String newValue) {
                System.out.println("msg id has been changed: " + newValue);
            }
        });

        configurationReader.watchKeys(Configuration.instance());

        configurationReader.setValue("/PROD/test/name", "testName2");

        configurationReader.setValue("/PROD/test/msg/id", "3");

    }
}
