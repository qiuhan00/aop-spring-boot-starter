package com.cfang.config;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @description：
 * @author cfang 2020年8月10日
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ApiLog {

	//操作类型
	String operaterType() default "其他操作";
	//操作模块
	String operaterModule() default "其他模块";
	//是否持久化记录结果数据
	boolean isRecord() default false; 
}
