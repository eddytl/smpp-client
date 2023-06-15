package com.nexah.http.responses;

public class SMSBrokerResp {
    private String queue_msgid;
    private String mobileno;

    public SMSBrokerResp() {
    }

    public String getQueue_msgid() {
        return queue_msgid;
    }

    public void setQueue_msgid(String queue_msgid) {
        this.queue_msgid = queue_msgid;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    @Override
    public String toString() {
        return "SMSBrokerResp{" +
                "queue_msgid='" + queue_msgid + '\'' +
                ", mobileno='" + mobileno + '\'' +
                '}';
    }
}
