package com.nexah.http.responses;

import java.io.Serializable;
import java.util.Arrays;

public class SMSMTargetResp implements Serializable {
    private Result[] results;

    public SMSMTargetResp() {
    }

    public Result[] getResults() {
        return results;
    }

    public void setResults(Result[] results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "SMSMTargetResp{" +
                "results=" + Arrays.toString(results) +
                '}';
    }

    public static class Result{
        public String msisdn;
        public String smscount;
        public String code;
        public String reason;
        public String ticket;

        public Result() {
        }

        public Result(String msisdn, String smscount, String code, String reason, String ticket) {
            this.msisdn = msisdn;
            this.smscount = smscount;
            this.code = code;
            this.reason = reason;
            this.ticket = ticket;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "msisdn='" + msisdn + '\'' +
                    ", smscount='" + smscount + '\'' +
                    ", code='" + code + '\'' +
                    ", reason='" + reason + '\'' +
                    ", ticket='" + ticket + '\'' +
                    '}';
        }
    }
}
