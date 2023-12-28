package com.nexah.smpp;

public class SmsStatus {
    private boolean isSent;
    private String messageId;

    public SmsStatus() {
    }

    public SmsStatus(boolean isSent, String messageId) {
        this.isSent = isSent;
        this.messageId = messageId;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "SmsStatus{" +
                "isSent=" + isSent +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
