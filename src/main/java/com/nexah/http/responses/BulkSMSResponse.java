package com.nexah.http.responses;

import com.nexah.http.requests.SMS;

import java.util.List;

public class BulkSMSResponse {

    private String status;
    private List<SMS> smsList;

    public BulkSMSResponse() {
    }

    public BulkSMSResponse(String status, List<SMS> smsList) {
        this.status = status;
        this.smsList = smsList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
                ", smsList=" + smsList +
                '}';
    }
}
