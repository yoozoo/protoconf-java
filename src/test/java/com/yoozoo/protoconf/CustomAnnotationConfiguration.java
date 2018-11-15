package com.yoozoo.protoconf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.yoozoo.protoconf")
public class CustomAnnotationConfiguration {
    @Protoconf(token = "U2FsdGVkX19EcRlwdntg2vCprA1rS1GfMshOTl82JKsZXNKWVC+dvU2zvFbThRR2", watch = true)
    Configuration2 config2;

    @Protoconf(token = "U2FsdGVkX18YfLBue46R30MqCQ57MQ1739ZR/XyH4+C15lvw1Hw7vzKMfsWK/5J2")
    Configuration3 config3;

    @Protoconf(etcdUser = "user90:uIZQUqhx", etcdEndpoints = {"http://shenshui-test.uuzu.com:2379"})
    Config config;

}
