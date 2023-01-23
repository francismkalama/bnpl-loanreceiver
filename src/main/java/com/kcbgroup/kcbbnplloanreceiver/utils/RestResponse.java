package com.kcbgroup.kcbbnplloanreceiver.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestResponse extends ResponseEntity<RestAckObject> {


    public RestResponse(RestAckObject body, HttpStatus status) {
        super(body, status);
    }
}
