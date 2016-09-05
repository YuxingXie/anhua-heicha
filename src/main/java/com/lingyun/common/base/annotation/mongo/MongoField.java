package com.lingyun.common.base.annotation.mongo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Created by Administrator on 2015/9/16.
 */
@Target({ FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoField {
    String value() default "";
}
