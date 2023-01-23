package com.kcbgroup.kcbbnplloanreceiver.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponseObject {

    private String ResponseCode;
    private String ResponseID;
    private String ResponseDescription;
    private String Amount;
    private String MpesaRef;
}
