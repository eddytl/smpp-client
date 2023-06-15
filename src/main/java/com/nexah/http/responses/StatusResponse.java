package com.nexah.http.responses;

import com.nexah.utils.DateUtils;

import java.util.Date;

public class StatusResponse {

    private String msgId;
    private String status;
    private String msisdn;
    private String service;
    private String submitDate;
    private String deliveryDate;

    public StatusResponse() {
    }

    public StatusResponse(String msgId, String status, String msisdn, String service, Date submitDate, Date deliveryDate) {
        this.msgId = msgId;
        this.status = status;
        this.msisdn = msisdn;
        this.service = service;
        this.submitDate = DateUtils.dateToString(submitDate);
        this.deliveryDate = DateUtils.dateToString(deliveryDate);
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = DateUtils.dateToString(submitDate);
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = DateUtils.dateToString(deliveryDate);
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "msgId='" + msgId + '\'' +
                ", status='" + status + '\'' +
                ", submitDate=" + submitDate +
                ", deliveryDate=" + deliveryDate +
                ", msisdn=" + msisdn +
                '}';
    }
}
