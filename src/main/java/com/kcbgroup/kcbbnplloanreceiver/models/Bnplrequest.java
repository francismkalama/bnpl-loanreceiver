package com.kcbgroup.kcbbnplloanreceiver.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Bnplrequest {
    private String requestId;
    private String msisdn;
    private String amount;
    private String currency;
    private Customerinfo customerinfo;
    private String paybill;
    private String account;
    private String quoteId;
    private String insurance;
    private String tenure;
    private String callbackUrl;

    public static class Customerinfo{
        private String name;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

}

