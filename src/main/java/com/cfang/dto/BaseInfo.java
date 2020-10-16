package com.cfang.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author cfang 2020/10/16 15:49
 * @description
 */
@Data
@Accessors(chain = true)
public class BaseInfo {

	private String ip;
	private String url;
	private String httpMethod;
	private String classMethod;
	private Object requestParams;
	private String operatorType;
	private String operatorModule;
	private String operatorDesc;
	private Boolean isRecord;
}
