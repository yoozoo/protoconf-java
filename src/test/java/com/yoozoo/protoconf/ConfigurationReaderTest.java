package com.yoozoo.protoconf;

import com.yoozoo.protoconf.ChangeListener;
import com.yoozoo.protoconf.ConfigurationReader;
import com.yoozoo.protoconf.EtcdReader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigurationReaderTest {
    @Test
    public void testConfig() throws InterruptedException {
        EtcdReader etcdReader = new EtcdReader();
        ConfigurationReader configurationReader = new ConfigurationReader(etcdReader);

        configurationReader.setValue("/default/test/msg/id", "1");
        assertEquals(configurationReader.getValue("/default/test/msg/id"), "1");

        configurationReader.setValue("/default/test/name", "测试名字");
        assertEquals(configurationReader.getValue("/default/test/name"), "测试名字");

        assertTrue(configurationReader.config(Configuration.instance()));

        assertEquals(Configuration.instance().get_name(), "测试名字");
        assertEquals(Configuration.instance().get_msg().get_id(),1);

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
        configurationReader.setValue("/default/test/name", "testName2");

        configurationReader.setValue("/default/test/msg/id", "3");
        Thread.sleep(1000);
    }

    @Test
    public void testAgent() {
        //        Test protoagent connection
        String appToken = "U2FsdGVkX1+EGNROfb41wAhtOumHKQPkli1FEL54C/U=";
        EtcdReader etcdReader = new EtcdReader();
        etcdReader.setAppToken(appToken);
        ConfigurationReader configurationReader = new ConfigurationReader(etcdReader);
        configurationReader.setValue("/default/测试平台-测试服务/dbHost", "localhost");
        assertEquals(configurationReader.getValue("/default/测试平台-测试服务/dbHost"), "localhost");

        configurationReader.setValue("/default/测试平台-测试服务/dbPort", "3306");
        assertEquals(configurationReader.getValue("/default/测试平台-测试服务/dbPort"), "3306");

        configurationReader.setValue("/default/测试平台-测试服务/dbName", "testDB");
        assertEquals(configurationReader.getValue("/default/测试平台-测试服务/dbName"), "testDB");

        configurationReader.setValue("/default/测试平台-测试服务/routerMapMhyy", "none");
        assertEquals(configurationReader.getValue("/default/测试平台-测试服务/routerMapMhyy"), "none");

        assertTrue(configurationReader.config(Configuration1.instance()));

        assertEquals(Configuration1.instance().get_dbHost(), "localhost");
        assertEquals(Configuration1.instance().get_dbName(), "testDB");
        assertEquals(Configuration1.instance().get_dbPort(),"3306");
        assertEquals(Configuration1.instance().get_routerMapMhyy(),"none");
    }
}
