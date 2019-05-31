package com.hblolj.zookeeper.zkclient;

import org.I0Itec.zkclient.ZkClient;

/**
 * @author: hblolj
 * @Date: 2019/5/31 8:59
 * @Description:
 * @Version:
 **/
public class SessionDemo {

    private final static String CONNECTS_STRING = "47.97.228.113:2181";

    public static void main(String[] args) {

        ZkClient zkClient = new ZkClient(CONNECTS_STRING, 5000);

        System.out.println(zkClient);
    }
}
