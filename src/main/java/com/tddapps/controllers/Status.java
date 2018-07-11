package com.tddapps.controllers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.controllers.response.ApiGatewayResponse;

import java.util.Map;

public class Status implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody("OK")
				.build();
	}
}
