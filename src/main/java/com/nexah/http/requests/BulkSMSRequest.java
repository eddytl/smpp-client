package com.nexah.http.requests;

import java.util.List;

public class BulkSMSRequest {
   private String apiKey;
   private String traffic;
   private String dlrUrl;
   private String sender;
   private List<SMS> smsList;

    public BulkSMSRequest(String apiKey, String sender, List<SMS> smsList, String traffic, String dlrUrl) {
        this.apiKey = apiKey;
        this.sender = sender;
        this.smsList = smsList;
        this.traffic = traffic;
        this.dlrUrl = dlrUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<SMS> getSmsList() {
        return smsList;
    }

    public void setSmsList(List<SMS> smsList) {
        this.smsList = smsList;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public String getDlrUrl() {
        return dlrUrl;
    }

    public void setDlrUrl(String dlrUrl) {
        this.dlrUrl = dlrUrl;
    }

    @Override
    public String toString() {
        return "BulkSMSRequest{" +
                "apiKey='" + apiKey + '\'' +
                ", traffic='" + traffic + '\'' +
                ", dlrUrl='" + dlrUrl + '\'' +
                ", sender='" + sender + '\'' +
                ", smsList=" + smsList +
                '}';
    }
}
