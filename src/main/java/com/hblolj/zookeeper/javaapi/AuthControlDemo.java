package com.hblolj.zookeeper.javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author: hblolj
 * @Date: 2019/5/30 17:22
 * @Description:
 * @Version:
 **/
public class AuthControlDemo implements Watcher {

    private final static String CONNECTS_STRING = "47.97.228.113:2181";

    private static Stat stat = new Stat();

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {

        // 建立连接
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTS_STRING, 5000, new AuthControlDemo());
        countDownLatch.await();

        String path = "/auth1";

        ACL acl1 = new ACL(ZooDefs.Perms.ALL, new Id("digest", "root:123"));
//        ACL acl2 = new ACL(ZooDefs.Perms.DELETE, new Id("digest", "root:123"));
//        ACL acl3 = new ACL(ZooDefs.Perms.READ, new Id("digest", "root:123"));
//        ACL acl4 = new ACL(ZooDefs.Perms.WRITE, new Id("digest", "root:123"));
//        ACL acl5 = new ACL(ZooDefs.Perms.ADMIN, new Id("digest", "root:123"));
//        ACL acl2 = new ACL(ZooDefs.Perms.CREATE, new Id("ip", "122.245.122.210"));

        List<ACL> acls = Arrays.asList(acl1);

        zooKeeper.create(path, "123".getBytes(), acls, CreateMode.PERSISTENT);


        // 给当前客户端设置权限
        zooKeeper.addAuthInfo("digest", "root:123".getBytes());
        // 使用创建者的所有权限来进行限制
        zooKeeper.create("/auth1", "123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        byte[] data = zooKeeper.getData(path, true, stat);
        System.out.println("data: " + new String(data));

        ZooKeeper zooKeeper2 = new ZooKeeper(CONNECTS_STRING, 5000, new AuthControlDemo());
        zooKeeper2.addAuthInfo("digest", "root:root".getBytes());
        data = zooKeeper2.getData(path, true, stat);
        System.out.println("data: " + new String(data));
        //        zooKeeper.create("/auth1", "123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
//        zooKeeper.create("/auth1/auth1-1", "123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
    }

    public void process(WatchedEvent watchedEvent) {
        //如果当前的连接状态是连接成功的，那么通过计数器去控制
        if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
            if(Event.EventType.None==watchedEvent.getType()&&null==watchedEvent.getPath()){
                countDownLatch.countDown();
                System.out.println(watchedEvent.getState()+"-->"+watchedEvent.getType());
            }
        }
    }
}
