package com.nexah.http.responses;

public class ResultPageResponse {

    private String status;
    private String message;
    private Paginate paginate;
    private Object data;

    public ResultPageResponse(String status, String message, Paginate paginate, Object data) {
        this.status = status;
        this.message = message;
        this.paginate = paginate;
        this.data = data;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Paginate getPaginate() {
        return paginate;
    }

    public void setPaginate(Paginate paginate) {
        this.paginate = paginate;
    }

    @Override
    public String toString() {
        return "ResultPageResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", paginate=" + paginate +
                ", data=" + data +
                '}';
    }
}
