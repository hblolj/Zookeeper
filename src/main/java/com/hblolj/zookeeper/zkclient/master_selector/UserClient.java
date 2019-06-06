package com.hblolj.zookeeper.zkclient.master_selector;

import java.io.Serializable;

/**
 * @author: hblolj
 * @Date: 2019/6/5 16:55
 * @Description: 客户端
 * @Version: 1.0
 **/
public class UserClient implements Serializable{

    private static final long serialVersionUID = 8766523940310534488L;

    private int clientId;

    private String clientName;

    public UserClient() {
    }

    public UserClient(int clientId, String clientName) {
        this.clientId = clientId;
        this.clientName = clientName;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return "UserClient{" +
                "clientId=" + clientId +
                ", clientName='" + clientName + '\'' +
                '}';
    }
}
