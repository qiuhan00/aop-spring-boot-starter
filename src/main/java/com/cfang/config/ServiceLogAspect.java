package com.cfang.config;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

/**
 * @description：
 * @author cfang 2020年8月10日
 */
@Aspect
@Slf4j
@ConditionalOnProperty(prefix = "spring", name = "aop.path", havingValue = "true")
public class ServiceLogAspect {

	@Pointcut("execution(public * com.cfang.service..*.*(..))")
	public void pointcut() {}
	
	@Before("pointcut()")
	public void logBefore(JoinPoint joinPoint) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
		if(null != requestAttributes) { //前台点击产生的request请求，可获取
			HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
			//记录下请求内容
			log.info(String.format("==>%s URL : %s", request.getMethod(), request.getRequestURL().toString()));
			log.info(String.format("==>IP : %s", request.getRemoteAddr()));
		}
        log.info("==>请求参数 : " + Arrays.toString(joinPoint.getArgs()));
        //getSignature().getDeclaringTypeName()是获取包+类名, joinPoint.getSignature.getName()获取方法名
        log.info("==>请求service方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
//        log.info("==>TARGET: " + joinPoint.getTarget());//返回的是需要加强的目标类的对象
//        log.info("==>THIS: " + joinPoint.getThis());//返回的是经过加强后的代理类的对象
	}
	
	@Around("pointcut()")
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
		// 记录异常信息
        String errorCode = null;
        // 处理业务，并透传业务异常
        Object result = null;
        long s1 = System.currentTimeMillis();
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Exception ex) {
            errorCode = ex.getMessage();
            throw ex;
        } finally {
            //记录日志
        	log.info(String.format("%s 执行耗时:%s 毫秒", proceedingJoinPoint.getSignature().getDeclaringTypeName()+ "." + proceedingJoinPoint.getSignature().getName(), 
        			(System.currentTimeMillis() - s1)));
        }
        return result;
	}
}
