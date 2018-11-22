package com.yoozoo.protoconf.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.LOCAL_VARIABLE, ElementType.FIELD})
@Documented
public @interface Protoconf {
    String token() default "";
    boolean watch() default false;
    String etcdUser() default "root:root";
    String[] etcdEndpoints() default {"http://localhost:2379"};
}
