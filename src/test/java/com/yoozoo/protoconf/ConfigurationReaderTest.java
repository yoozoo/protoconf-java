package com.yoozoo.protoconf;

import com.yoozoo.protoconf.annotation.Protoconf;
import com.yoozoo.protoconf.reader.ChangeListener;
import com.yoozoo.protoconf.reader.ConfigurationReader;
import com.yoozoo.protoconf.reader.EtcdReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={CustomAnnotationConfiguration.class})
public class ConfigurationReaderTest {
    @Autowired
    Configuration2 config2;

    @Autowired
    Configuration3 config3;

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
        configurationReader.watchKeys(Configuration.instance());

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

        configurationReader.setValue("/default/test/name", "testName2");
        configurationReader.setValue("/default/test/msg/id", "3");
    }

    @Test
    public void testAgent() throws InterruptedException {
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

        configurationReader.watchKeys(Configuration1.instance());

        Thread.sleep(50000);
    }

    @Test
    public void testAnnotation() throws InterruptedException {
        assertEquals(config2.get_mysqlDsn(), "jdbc:mysql://localhost:3306/test?useSSL=false");
        assertEquals(config3.get_username(), "root");

        config2.watch_username(new ChangeListener() {
            @Override
            public void onChange(String newValue) {
                System.out.println("username has been changed: " + newValue);
            }
        });

    }
}
