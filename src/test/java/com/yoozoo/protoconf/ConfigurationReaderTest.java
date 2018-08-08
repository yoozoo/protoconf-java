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
        configurationReader.setValue("/PROD/测试/名字", "测试名字");
        assertTrue(configurationReader.config(Configuration.instance()));

        assertTrue(configurationReader.getValue("/PROD/测试/名字").compareTo("测试名字")==0);

        System.out.println("name current is : " + Configuration.instance().get_name());
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
        Thread.sleep(1000);
        configurationReader.setValue("/PROD/test/name", "testName2");

        configurationReader.setValue("/PROD/test/msg/id", "3");
        Thread.sleep(1000);
    }
}
