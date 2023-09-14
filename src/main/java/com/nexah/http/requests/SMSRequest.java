package com.nexah.http.requests;

public class SMSRequest {
   private String apiKey;
   private String traffic;
   private String mobileno;
   private String sender;
   private String message;
   private int dataEncoding;
   private int charset;


    public SMSRequest(String apiKey, String mobileno, String sender, String message, String traffic, int dataEncoding, int charset) {
        this.apiKey = apiKey;
        this.mobileno = mobileno;
        this.sender = sender;
        this.message = message;
        this.traffic = traffic;
        this.dataEncoding = dataEncoding;
        this.charset = charset;
    }
    public int getCharset() {
        return charset;
    }

    public void setCharset(int charset) {
        this.charset = charset;
    }
    public int getDataEncoding() {
        return dataEncoding;
    }

    public void setDataEncoding(int dataEncoding) {
        this.dataEncoding = dataEncoding;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
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

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    @Override
    public String toString() {
        return "SMSRequest{" +
                "apiKey='" + apiKey + '\'' +
                ", mobileno='" + mobileno + '\'' +
                ", sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", traffic='" + traffic + '\'' +
                ", dataEncoding='" + dataEncoding + '\'' +
                '}';
    }
}
