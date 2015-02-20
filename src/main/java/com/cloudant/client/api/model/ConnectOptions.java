package com.cloudant.client.api.model;

/**
 * Represents optional configuration properties for connecting to CloudantDB.
 *
 * @author Ganesh K Choudhary
 */
public class ConnectOptions {
    private int    connectionTimeout;
    private int    maxConnections;
    private int    socketTimeout;

    private String proxyHost;
    private String proxyPass;
    private int    proxyPort;
    private String proxyUser;

    public ConnectOptions() {
        // default constructor
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public String getProxyPass() {
        return proxyPass;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public ConnectOptions setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public ConnectOptions setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    public ConnectOptions setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        return this;
    }

    public ConnectOptions setProxyPass(String proxyPass) {
        this.proxyPass = proxyPass;
        return this;
    }

    public ConnectOptions setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }

    public ConnectOptions setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
        return this;
    }

    public ConnectOptions setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }
}
