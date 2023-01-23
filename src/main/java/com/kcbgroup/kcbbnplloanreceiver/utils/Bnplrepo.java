package com.kcbgroup.kcbbnplloanreceiver.utils;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Bnplrepo {
    //method to check null on strings
    //-----------------for checking on nulls on supplied fields ----------------
    public  boolean validateInput(String reqstring){
        return (reqstring!=null && reqstring.length()> 0);
    }

    //-------------------Perfoms checks on the supplied phone number ---------------
    public String standardizeMSISDN( String phoneNumber ){

        if (phoneNumber != null && phoneNumber.length() > 0) {
            phoneNumber = removeSpeacialXters(phoneNumber);
            if (phoneNumber.length() >= 9)
                phoneNumber = "254" + phoneNumber.substring(phoneNumber.length() - 9);
            return phoneNumber;
        } else {
            return phoneNumber;
        }
    }

    public String removeSpeacialXters(String myString) {

        return myString.replaceAll("[^\\dA-Za-z ]", "");
    }

    //--------------generate a checksum ------------------


}
