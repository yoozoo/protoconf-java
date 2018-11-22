package com.yoozoo.protoconf.annotation;

import com.yoozoo.protoconf.reader.ConfigurationInterface;
import com.yoozoo.protoconf.reader.ConfigurationReader;
import com.yoozoo.protoconf.reader.EtcdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ProtoconfFieldCallback implements ReflectionUtils.FieldCallback {
    private static Logger logger = LoggerFactory.getLogger(ProtoconfFieldCallback.class);

    private static int AUTOWIRE_MODE = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

    private ConfigurableListableBeanFactory configurableBeanFactory;
    private Object bean;

    public ProtoconfFieldCallback(ConfigurableListableBeanFactory bf, Object bean) {
        configurableBeanFactory = bf;
        this.bean = bean;
    }

    @Override
    public void doWith(Field field)
            throws IllegalArgumentException, IllegalAccessException {
        if (!field.isAnnotationPresent(Protoconf.class)) {
            return;
        }
        ReflectionUtils.makeAccessible(field);
//        get class from reflections
        Class<?> configClass = field.getType();
        Protoconf protoconf = field.getDeclaredAnnotation(Protoconf.class);
        Object beanInstance = getBeanInstance(configClass, protoconf);
        field.set(bean, beanInstance);
    }

    public Object getBeanInstance(Class<?> configClass, Protoconf protoconf) {
        Object rInstance = null;
        String beanName = configClass.getName();
        logger.info("Config new Protoconf config named '{}'.", beanName);

        if (!configurableBeanFactory.containsBean(beanName)) {
            logger.info("Creating new bean named '{}'.", beanName);

            Object toRegister = null;
            try {
                Constructor<?> ctr = configClass.getConstructor();
                toRegister = ctr.newInstance();
                EtcdReader etcdReader = new EtcdReader();
//                check app token
                String token = protoconf.token();
                if (token.length() > 0) {
                    etcdReader.setAppToken(token);
                }else {
//                    for local dev, use etcd user and endpoints
                    String etcdUser = protoconf.etcdUser();
                    String[] etcdEndpoints = protoconf.etcdEndpoints();
                    if (etcdUser.split(":").length == 2) {
                        etcdReader.setUserName(etcdUser.split(":")[0]);
                        etcdReader.setPassword(etcdUser.split(":")[1]);
                        etcdReader.setEndpoints(etcdEndpoints);
                    }
                }
                ConfigurationReader configurationReader = new ConfigurationReader(etcdReader);
                configurationReader.config((ConfigurationInterface) toRegister);
//                start watch if watch flag set
                boolean watch = protoconf.watch();
                if (watch) {
                    configurationReader.watchKeys((ConfigurationInterface) toRegister);
                    logger.info("Start to watch keys of Protoconf config named '{}'.", beanName);
                }
                logger.info("Config Protoconf config named '{}' successfully.", beanName);

                rInstance = configurableBeanFactory.initializeBean(toRegister, beanName);
                configurableBeanFactory.autowireBeanProperties(rInstance, AUTOWIRE_MODE, true);
                configurableBeanFactory.registerSingleton(beanName, rInstance);
                logger.info("Bean named '{}' created successfully.", beanName);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            rInstance = configurableBeanFactory.getBean(beanName);
            logger.info(
                    "Bean named '{}' already exists used as current bean reference.", beanName);
        }
        return rInstance;
    }
}
