package com.tddapps.controllers;

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

    public ActionProcessException getSeededProcessException() {
        return seededProcessException;
    }

    public void setSeededProcessException(ActionProcessException seededProcessException) {
        this.seededProcessException = seededProcessException;
    }

    public String getSeededResultBody() {
        return seededResultBody;
    }

    public void setSeededResultBody(String seededResultBody) {
        this.seededResultBody = seededResultBody;
    }

    public int getSeededStatusCode() {
        return seededStatusCode;
    }

    public void setSeededStatusCode(int seededStatusCode) {
        this.seededStatusCode = seededStatusCode;
    }
}
