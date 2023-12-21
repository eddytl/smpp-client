package com.nexah.http.requests;

public class SMS {
    private String smsId;
    private String msgId;
    private String mobileno;
    private String message;

    public SMS() {
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getMessage() {
        return message;
    }


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
