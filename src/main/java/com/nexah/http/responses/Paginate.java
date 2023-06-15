package com.nexah.http.responses;

public class Paginate {
    private Integer from;
    private long to;
    private long total;

    public Paginate(Integer pageNo, Integer pageSize, Integer totalData, long total) {
        this.from = ((pageNo - 1) * pageSize) + 1 ;
        this.to = (this.from + totalData ) - 1;
        this.total = total;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
