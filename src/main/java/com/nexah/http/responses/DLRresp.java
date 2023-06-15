package com.nexah.http.responses;

public class DLRresp {

    private Integer status;

    public DLRresp() {
    }

    public DLRresp(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DLRresp{" +
                "status=" + status +
                '}';
    }
}
