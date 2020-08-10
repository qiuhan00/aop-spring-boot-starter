package com.cfang.config;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
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
public class WebLogAspect {

	@Pointcut("@annotation(com.cfang.config.ApiLog)")
	public void pointcut() {}
	
	@Around("pointcut()")
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		// 记录异常信息
        String errorCode = null;
        // 处理业务，并透传业务异常
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Exception ex) {
            errorCode = ex.getMessage();
            throw ex;
        } finally {
            //记录日志
        	saveLog(proceedingJoinPoint, errorCode);
        }
        return result;
	}
	
	private void saveLog(ProceedingJoinPoint proceedingJoinPoint, String errorCode) {
        try {
            // 获取基础参数
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
            Object[] args = proceedingJoinPoint.getArgs();
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = signature.getMethod();
            ApiLog apiLog = method.getAnnotation(ApiLog.class);
            log.info(getLogMsg(apiLog, args));
        } catch (Exception ex) {
            log.error("记录日志发生异常", ex);
        }
    }
	
	private String getLogMsg(ApiLog apiLog, Object[] args) {
		return String.join(",", apiLog.operaterType(), apiLog.operaterModule(), "result:" + Arrays.toString(args));
	}
}
