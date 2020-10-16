package com.cfang.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @description：
 * @author cfang 2020年8月10日
 */
@Configuration
@ConditionalOnProperty(prefix = "spring", name = "aop.auto", havingValue = "true")
public class AopConfiguration {

	@Bean
	@ConditionalOnProperty(name = "spring.aop.log.annotation", havingValue = "true")
	public WebLogAspect initWebLogAspect() {
		return new WebLogAspect();
	}
	
	@Bean
	@ConditionalOnProperty(name = "spring.aop.log.fixed", havingValue = "true")
	public ServiceLogAspect inServiceLogAspect() {
		return new ServiceLogAspect();
	}
}
