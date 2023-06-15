package com.nexah.smpp;


public class Service {

    private String name;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private Boolean isBound;

    public Service() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getBound() {
        return isBound;
    }

    public void setBound(Boolean bound) {
        isBound = bound;
    }

    @Override
    public String toString() {
        return "Service{" +
                ", name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isBound=" + isBound +
                '}';
    }
}
