package com.cfang.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author cfang 2020/10/16 14:02
 * @description
 */
@Data
@Accessors(chain = true)
public class RequestInfo extends BaseInfo{
	private Object result;
	private Long timeCost;

}
