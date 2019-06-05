package com.hblolj.zookeeper.javaapi;

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

    private static int sessionTimeout = 5000;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static ZooKeeper getInstance() throws IOException, InterruptedException {

        ZooKeeper zooKeeper = new ZooKeeper(CONNECTS_STRING, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        return zooKeeper;
    }

    public static int getSessionTimeout() {
        return sessionTimeout;
    }

    public static void setSessionTimeout(int sessionTimeout) {
        ZooKeeperClient.sessionTimeout = sessionTimeout;
    }
}
