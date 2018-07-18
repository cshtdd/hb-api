package com.tddapps.controllers;

import java.util.Map;

public interface HttpJsonController {
    HttpJsonResponse process(Map<String, Object> input);
}
