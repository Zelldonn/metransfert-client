package com.metransfert.client.controller;

public class Address {

    protected String name, ip;

    protected int port;

    public Address(String name, String ip, int port){
        setIp(ip);
        setPort(port);
        setName(name);
    }

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        if(ip == null) throw new IllegalArgumentException("ip must not be null");
        else this.ip = ip;
    }

    public String getName() {
        return name;
    }
    public void setName(String name){
        if(name == null) this.name = "N/A";
        else this.name = name;
    }

    public int getPort() {
        return port;
    }
    public void setPort(int port){
        if(port < 1 || port > 65565) throw new IllegalArgumentException("port must be between 1 and 65565");
        else this.port = port;
    }

    public boolean hasAddressChanged(String ip, int port){
        return (!this.ip.equals(ip) && this.port != port);
    }
}
