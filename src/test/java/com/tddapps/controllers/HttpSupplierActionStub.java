package com.tddapps.controllers;

import lombok.Data;

@Data
public class HttpSupplierActionStub implements HttpSupplierAction<String> {
    private ActionProcessException seededProcessException = null;
    private int seededStatusCode = -1;
    private String seededResultBody = null;

    @Override
    public HttpJsonResponse<String> process() throws ActionProcessException {
        if (seededProcessException != null){
            throw seededProcessException;
        }

        return new HttpJsonResponse<>(seededStatusCode, seededResultBody);
    }
}
