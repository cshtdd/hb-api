package com.tddapps.controllers;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class HttpJsonControllerSupplierTest {
    private final HttpSupplierActionStub actionStub = new HttpSupplierActionStub();
    private final HttpJsonControllerSupplier controller = new HttpJsonControllerSupplier(actionStub);

    @Test
    public void ReturnsServerErrorWhenProcessingFails(){
        actionStub.setSeededProcessException(new ActionProcessException("database is down"));

        assertEquals(HttpJsonResponse.ServerErrorWithMessage("database is down"), process());
    }

    @Test
    public void ReturnsTheProcessedResult(){
        actionStub.setSeededStatusCode(200);
        actionStub.setSeededResultBody("OK");

        assertEquals(HttpJsonResponse.Success("OK"), process());
    }

    private HttpJsonResponse process() {
        return controller.process(new HashMap<>());
    }
}
