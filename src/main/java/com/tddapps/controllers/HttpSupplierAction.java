package com.tddapps.controllers;

public interface HttpSupplierAction<Response> {
    HttpJsonResponse<Response> process() throws BodyProcessException;
}
