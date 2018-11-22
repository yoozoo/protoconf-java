package com.yoozoo.protoconf;

import com.yoozoo.protoconf.cloud.config.ProtoconfEnvironmentRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;

import java.util.List;

import static org.junit.Assert.*;

public class ProtoconfEnvironmentRepositoryTest {

    private ProtoconfEnvironmentRepository dut;

    @Before
    public void before() throws Exception {
        dut = new ProtoconfEnvironmentRepository();
    }

    @Test
    public void findEnvironment() throws Exception
    {
        final Environment env = dut.findOne("北美平台-测试java", "default", null);

        assertNotNull(env);
        assertEquals("北美平台-测试java", env.getName());
        assertArrayEquals(new String[] {"default"}, env.getProfiles());
        assertNotNull(env.getPropertySources());
        assertEquals(1, env.getPropertySources().size());
    }


    @Test
    public void getProperties() throws Exception {

        final Environment env = dut.findOne("北美平台-测试java", "default", null);
        final List<PropertySource> testProps = env.getPropertySources();

        assertEquals("root", testProps.get(0).getSource().get("username"));
    }

}
