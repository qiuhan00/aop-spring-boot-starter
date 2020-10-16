package com.cfang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @description：
 * @author cfang 2020年8月10日
 */
@Retention(RUNTIME)
@Target(METHOD)
@Documented
public @interface ApiLog {

	//操作类型
	String operatorType() default "operator type";
	//操作模块
	String operatorModule() default "operator module";

	String operatorDesc() default "operator sth...";
	//是否持久化记录结果数据
	boolean isRecord() default false;
}
