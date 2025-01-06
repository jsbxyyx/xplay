package com.github.jsbxyyx.xbook.data.bean;

public class Ip implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String ip;
    private String colo;
    private String latency;
    private String speed;
    private String uptime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getColo() {
        return colo;
    }

    public void setColo(String colo) {
        this.colo = colo;
    }

    public String getLatency() {
        return latency;
    }

    public void setLatency(String latency) {
        this.latency = latency;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    @Override
    public String toString() {
        return "Ip{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", colo='" + colo + '\'' +
                ", latency='" + latency + '\'' +
                ", speed='" + speed + '\'' +
                ", uptime='" + uptime + '\'' +
                '}';
    }
}
