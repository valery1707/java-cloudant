/*
 * Copyright (C) 2011 lightcouch.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lightcouch;

/**
 * Represents configuration properties for connecting to CouchDB.
 *
 * @author Ahmed Yehia
 * @author Daan van Berkel
 */
public class CouchDbProperties {
    private String authCookie;       // optional
    private int    connectionTimeout; // optional
    private String host;             // required
    private int    maxConnections;   // optional
    private String password;         // required
    private String path;             // required
    private int    port;             // required
    private String protocol;         // required

    private String proxyHost;        // optional
    private String proxyPass;        // optional
    private int    proxyPort;        // optional
    private String proxyUser;        // optional

    private int    socketTimeout;    // optional
    private String username;         // required

    public CouchDbProperties() {
        // default constructor
    }

    public CouchDbProperties(String protocol, String host, int port, String authCookie) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.authCookie = authCookie;
    }

    public CouchDbProperties(String protocol, String host, int port, String username, String password) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void clearPassword() {
        setPassword("");
        setPassword(null);
    }

    public String getAuthCookie() {
        return authCookie;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public String getHost() {
        return host;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public String getPassword() {
        return password;
    }

    public String getPath() {
        return path;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
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

    public String getUsername() {
        return username;
    }

    public void setAuthCookie(String authCookie) {
        this.authCookie = authCookie;
    }

    public CouchDbProperties setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public CouchDbProperties setHost(String host) {
        this.host = host;
        return this;
    }

    public CouchDbProperties setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    public CouchDbProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public CouchDbProperties setPath(String path) {
        this.path = path;
        return this;
    }

    public CouchDbProperties setPort(int port) {
        this.port = port;
        return this;
    }

    public CouchDbProperties setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public CouchDbProperties setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        return this;
    }

    public CouchDbProperties setProxyPass(String proxyPass) {
        this.proxyPass = proxyPass;
        return this;
    }

    public CouchDbProperties setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }

    public CouchDbProperties setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
        return this;
    }

    public CouchDbProperties setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public CouchDbProperties setUsername(String username) {
        this.username = username;
        return this;
    }
}
