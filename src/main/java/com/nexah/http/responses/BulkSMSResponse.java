package com.nexah.http.responses;

import com.nexah.http.requests.SMS;

import java.util.List;

public class BulkSMSResponse {

    private String status;
    private String message;
    private List<SMS> smsList;

    public BulkSMSResponse() {
    }

    public BulkSMSResponse(String status, String message, List<SMS> smsList) {
        this.status = status;
        this.message = message;
        this.smsList = smsList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SMS> getSmsList() {
        return smsList;
    }

    public void setSmsList(List<SMS> smsList) {
        this.smsList = smsList;
    }

    @Override
    public String toString() {
        return "BulkSMSResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", smsList=" + smsList +
                '}';
    }
}
