package com.nexah.smpp;

public class Async {
    /**
     * This number should be lower than the value assigned to the core-pool-size
     */
    private int smppSessionSize = 2;

    private int corePoolSize = 5;

    private int maxPoolSize = 50;

    private int queueCapacity = 10000;

    private int initialDelay = 1000;

    private int timeout = 10000;

    public int getSmppSessionSize() {
        return smppSessionSize;
    }

    public void setSmppSessionSize(int smppSessionSize) {
        this.smppSessionSize = smppSessionSize;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(int initialDelay) {
        this.initialDelay = initialDelay;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
