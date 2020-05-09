package com.cheercent.springboot.xserver.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XLogicConfig {

    String[] requiredParameters() default {};
    
    boolean requiredPeerid() default true;
    
    String[] allow() default {};
    
    int version() default 1;
    
}