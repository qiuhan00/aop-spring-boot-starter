package com.cfang.config;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.cfang.annotation.ApiLog;
import com.cfang.dto.BaseInfo;
import com.cfang.dto.RequestErrorInfo;
import com.cfang.dto.RequestInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description：
 * @author cfang 2020年8月10日
 */
@Aspect
@Slf4j
public class WebLogAspect {

	private final static String TRACE_ID = "traceId";

	@Pointcut("@annotation(com.cfang.annotation.ApiLog)")
	public void pointcut() {}

	@Before("pointcut()")
	public void before(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		String traceId = UUID.randomUUID().toString(true).toUpperCase();
//		MDC.put(TRACE_ID, traceId);
	}

	@After("pointcut()")
	public void after(JoinPoint joinPoint) {
//		MDC.remove(TRACE_ID);
	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		TimeInterval interval = DateUtil.timer();
		Object result = proceedingJoinPoint.proceed();
		saveLog(proceedingJoinPoint, result, interval);
        return result;
	}

	@AfterThrowing(value = "pointcut()", throwing = "e")
	public void AfterThrowing(JoinPoint joinPoint, RuntimeException e) {
		try {
			// 获取基础参数
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
			HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
			//设置RequestErrorInfo
			RequestErrorInfo requestInfo = (RequestErrorInfo) init(httpServletRequest, joinPoint, true);
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			ApiLog apiLog = method.getAnnotation(ApiLog.class);
			requestInfo.setOperatorType(apiLog.operatorType());
			requestInfo.setOperatorModule(apiLog.operatorModule());
			requestInfo.setOperatorDesc(apiLog.operatorDesc());
			requestInfo.setIsRecord(apiLog.isRecord());
			requestInfo.setException(e);
			log.info("RequestErrorInfo info:{}", JSONUtil.toJsonStr(requestInfo));
		} catch (Exception ex) {
			log.warn("记录异常日志发生异常,msg:{}", ex.getMessage());
		}
	}

	private void saveLog(ProceedingJoinPoint proceedingJoinPoint, Object result, TimeInterval interval) {
        try {
            // 获取基础参数
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
			//设置RequestInfo
	        RequestInfo requestInfo = (RequestInfo) init(httpServletRequest, proceedingJoinPoint, false);
	        requestInfo.setResult(result);
	        requestInfo.setTimeCost(interval.interval());
            Object[] args = proceedingJoinPoint.getArgs();
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = signature.getMethod();
            ApiLog apiLog = method.getAnnotation(ApiLog.class);
	        requestInfo.setOperatorType(apiLog.operatorType());
	        requestInfo.setOperatorModule(apiLog.operatorModule());
	        requestInfo.setOperatorDesc(apiLog.operatorDesc());
	        requestInfo.setIsRecord(apiLog.isRecord());
	        log.info("RequestInfo info:{}", JSONUtil.toJsonStr(requestInfo));
        } catch (Exception ex) {
            log.warn("记录日志发生异常,msg:{}", ex.getMessage());
        }
    }

    private Object init(HttpServletRequest httpServletRequest, JoinPoint joinPoint, boolean isError){
		BaseInfo info = null;
		if(isError){
			info = new RequestErrorInfo();
		}else {
			info = new RequestInfo();
		}
		info.setIp(httpServletRequest.getRemoteAddr())
				.setUrl(httpServletRequest.getRequestURL().toString())
				.setHttpMethod(httpServletRequest.getMethod())
				.setClassMethod(String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName()))
				.setRequestParams(getRequestParamsByJoinPoint(joinPoint));
		return info;
    }

	private Map<String, Object> getRequestParamsByJoinPoint(JoinPoint joinPoint) {
		String[] paramNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
		Object[] paramValues = joinPoint.getArgs();
		return buildRequestParam(paramNames, paramValues);
	}

	private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValues) {
		Map<String, Object> requestParams = new HashMap<>();
		for (int i = 0; i < paramNames.length; i++) {
			Object value = paramValues[i];
			//如果是文件对象
			if (value instanceof MultipartFile) {
				MultipartFile file = (MultipartFile) value;
				value = file.getOriginalFilename();  //获取文件名
			}
			requestParams.put(paramNames[i], value);
		}

		return requestParams;
	}

}
