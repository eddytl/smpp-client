package com.nexah.http.requests;

import com.nexah.utils.DateUtils;

import java.time.ZonedDateTime;
import java.util.Date;

public class DLRreq {

    private String requestId;
    private String deliveryStatus;
    private String mobileno;
    private String provider;
    private String submitDate;
    private String deliverytime;
    private Integer isSmpp;

    public DLRreq() {
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = DateUtils.dateToString(submitDate);;
    }

    public String getDeliverytime() {
        return deliverytime;
    }

    public void setDeliverytime(Date deliveryDate) {
        this.deliverytime = DateUtils.dateToString(deliveryDate);
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(ZonedDateTime submitDate) {
        this.submitDate = DateUtils.dateToString(Date.from(submitDate.toInstant()));
    }

    public Integer getIsSmpp() {
        return isSmpp;
    }

    public void setIsSmpp(Integer isSmpp) {
        this.isSmpp = isSmpp;
    }

    @Override
    public String toString() {
        return "DLRreq{" +
                "requestId='" + requestId + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", mobileno='" + mobileno + '\'' +
                ", provider='" + provider + '\'' +
                ", submitDate='" + submitDate + '\'' +
                ", deliverytime='" + deliverytime + '\'' +
                ", isSmpp='" + isSmpp + '\'' +
                '}';
    }
}
