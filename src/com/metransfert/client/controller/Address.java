package com.metransfert.client.controller;

public class Address {
    String name, ip;

    int port;

    public Address(String name, String ip, int port){
        this.name = name;
        this.ip = ip;
        this.port = port;
    }
    public void setAddress(String ip, int port){
        this.ip = ip;
        this.port = port;
    }
    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
    public boolean hasAddressChanged(String ip, int port){
        return (!this.ip.equals(ip) && this.port != port);
    }
}
