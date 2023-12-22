package com.nexah.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String requestId;
    private String msisdn;
    private String sender;
    private String message;
    private String traffic;
    private String status;
    private String errorMsg;
    private int retry;
    private Date submitedAt;
    private Date deliveredAt;
    private String dlrUrl;
    private boolean dlrIsSent;
    private Date createdAt;
    private Date updatedAt;

    public Message(){
    }

    public String getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDlrUrl() {
        return dlrUrl;
    }

    public void setDlrUrl(String dlrUrl) {
        this.dlrUrl = dlrUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSubmitedAt() {
        return submitedAt;
    }

    public void setSubmitedAt(Date submitedAt) {
        this.submitedAt = submitedAt;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public boolean isDlrIsSent() {
        return dlrIsSent;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setDlrIsSent(boolean dlrIsSent) {
        this.dlrIsSent = dlrIsSent;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", requestId='" + requestId + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", traffic='" + traffic + '\'' +
                ", status='" + status + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", submitedAt=" + submitedAt +
                ", deliveredAt=" + deliveredAt +
                ", dlrUrl='" + dlrUrl + '\'' +
                ", dlrIsSent=" + dlrIsSent +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
