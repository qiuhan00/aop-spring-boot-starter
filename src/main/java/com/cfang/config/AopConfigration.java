package com.cfang.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description：
 * @author cfang 2020年8月10日
 */
@Configuration
@ConditionalOnProperty(prefix = "spring", name = "aop.auto", havingValue = "true")
public class AopConfigration {

	@Bean
	public WebLogAspect initWebLogAspect() {
		return new WebLogAspect();
	}
	
	@Bean
	public ServiceLogAspect inServiceLogAspect() {
		return new ServiceLogAspect();
	}
}
