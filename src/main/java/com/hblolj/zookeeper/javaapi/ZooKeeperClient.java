package com.hblolj.zookeeper.javaapi;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author: hblolj
 * @Date: 2019/6/5 15:10
 * @Description:
 * @Version:
 **/
public class ZooKeeperClient {

    private final static String CONNECTS_STRING = "47.97.228.113:2181";

    private static int sessionTimeout = 20000;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static ZkClient getInstance() throws IOException, InterruptedException {
        ZkClient zkClient = new ZkClient(CONNECTS_STRING, sessionTimeout, 5000);
//        ZooKeeper zooKeeper = new ZooKeeper(CONNECTS_STRING, sessionTimeout, new Watcher() {
//            @Override
//            public void process(WatchedEvent watchedEvent) {
//                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
//                    countDownLatch.countDown();
//                }
//            }
//        });
//        countDownLatch.await();
        return zkClient;
    }

    public static int getSessionTimeout() {
        return sessionTimeout;
    }
}
