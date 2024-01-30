package com.nexah.http.requests;

public class DLRequest {

    private String requestId;
    private String deliveryStatus;
    private String deliverytime;

    public DLRequest() {
    }

    public DLRequest(String requestId, String deliveryStatus, String deliverytime) {
        this.requestId = requestId;
        this.deliveryStatus = deliveryStatus;
        this.deliverytime = deliverytime;
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


    public String getDeliverytime() {
        return deliverytime;
    }

    public void setDeliverytime(String deliverytime) {
        this.deliverytime = deliverytime;
    }


    @Override
    public String toString() {
        return "DLRequest{" +
                "requestId='" + requestId + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", deliverytime='" + deliverytime + '\'' +
                '}';
    }
}
