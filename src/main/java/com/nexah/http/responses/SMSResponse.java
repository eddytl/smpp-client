package com.nexah.http.responses;

public class SMSResponse {

    private String status;
    private String message;
    private String msgId;

    public SMSResponse(String status, String message, String msgId) {
        this.status = status;
        this.message = message;
        this.msgId = msgId;
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

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "SMSResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", msgId='" + msgId + '\'' +
                '}';
    }
}
