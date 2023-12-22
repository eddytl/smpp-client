package com.nexah.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "settings")
public class Setting {
    @Id
    private String id;
    private int windowsSize;
    private int submitSmTimeOut;
    private String serviceName;
    private String host;
    private int port;
    private String username;
    private String password;

    public Setting() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWindowsSize() {
        return windowsSize;
    }

    public void setWindowsSize(int windowsSize) {
        this.windowsSize = windowsSize;
    }

    public int getSubmitSmTimeOut() {
        return submitSmTimeOut;
    }

    public void setSubmitSmTimeOut(int submitSmTimeOut) {
        this.submitSmTimeOut = submitSmTimeOut;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
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
}
