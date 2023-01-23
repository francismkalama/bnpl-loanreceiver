package com.kcbgroup.kcbbnplloanreceiver.controller;

import com.kcbgroup.kcbbnplloanreceiver.models.Bnplrequest;
import com.kcbgroup.kcbbnplloanreceiver.services.BnplLoanreceiverservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("api/v1/bnpl")
public class BnplLoanreceiverController {

    @Autowired
    private BnplLoanreceiverservice bnplLoanreceiverservice;

    @PostMapping("/loanrequest")
    public ResponseEntity processLoanRequest(@RequestBody Bnplrequest bnplrequest, HttpServletRequest request, HttpServletResponse res){
        return bnplLoanreceiverservice.bnplloanReceiver(bnplrequest, request);
    }


}
