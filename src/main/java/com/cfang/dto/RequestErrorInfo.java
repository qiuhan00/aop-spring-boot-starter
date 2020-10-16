package com.cfang.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author cfang 2020/10/16 15:48
 * @description
 */
@Data
public class RequestErrorInfo extends BaseInfo{

	private RuntimeException exception;
}
