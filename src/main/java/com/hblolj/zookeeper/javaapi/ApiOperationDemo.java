package com.hblolj.zookeeper.javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author: hblolj
 * @Date: 2019/5/27 14:00
 * @Description:
 * @Version:
 **/
public class ApiOperationDemo implements Watcher{

    private final static String CONNECTS_STRING = "47.97.228.113:2181";

    private static Stat stat = new Stat();

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        String path = "/parent/hblolj7";
        String childPath = "/child";

        // 建立连接
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTS_STRING, 5000, new ApiOperationDemo());
        countDownLatch.await();

//        zooKeeper.exists(path, true);

        // 增加节点
        String result = zooKeeper.create(path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("result: " + result);

        List<String> children = zooKeeper.getChildren("/parent", true);
        System.out.println("children: " + children);
//        byte[] data = zooKeeper.getData(path, true, stat);
//        System.out.println("创建后的值: " + new String(data));

        // 增加子节点
        Stat exists2 = zooKeeper.exists(path + childPath, true);
        if (exists2 == null){
            // 子节点尚未存在,创建之
            zooKeeper.create(path + childPath, "child".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            byte[] childData = zooKeeper.getData(path + childPath, true, stat);
            System.out.println("子节点创建后的值: " + new String(childData));
//            TimeUnit.SECONDS.sleep(1);
//            List<String> children = zooKeeper.getChildren(path, true);
//            System.out.println(children.toString());
        }

        // 修改子节点数据
        zooKeeper.setData(path + childPath, "Vinda".getBytes(), -1);
        byte[] childData = zooKeeper.getData(path + childPath, true, stat);
        System.out.println("子节点修改后的值: " + new String(childData));

        // 修改节点数据
        zooKeeper.setData(path, "LuYi".getBytes(), -1);

        // 查询节点数据
        byte[] data = zooKeeper.getData(path, true, new Stat());
        System.out.println("修改后的值: " + new String(data));

        // 删除子节点
        zooKeeper.delete(path + childPath, -1);

        // 删除节点
        zooKeeper.delete(path, -1);
    }

    public void process(WatchedEvent watchedEvent) {

        // watchedEvent.getState() 获取到的是一个枚举类型
//        @Deprecated
//        Unknown(-1),
//        Disconnected(0),
//        @Deprecated
//        NoSyncConnected(1),
//        SyncConnected(3),
//        AuthFailed(4),
//        ConnectedReadOnly(5),
//        SaslAuthenticated(6),
//        Expired(-112);
        System.out.println(watchedEvent.toString());
//        None(-1),
//        NodeCreated(1),
//        NodeDeleted(2),
//        NodeDataChanged(3),
//        NodeChildrenChanged(4);
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
            // 连接成功
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()){
                System.out.println("连接成功");
                countDownLatch.countDown();
            }else if (Event.EventType.NodeDataChanged == watchedEvent.getType()){
                System.out.println("节点数据变更");

            }else if (Event.EventType.NodeChildrenChanged == watchedEvent.getType()){
                System.out.println("子节点数据变更");

            }else if (Event.EventType.NodeCreated == watchedEvent.getType()){
                System.out.println("节点创建");

            }else if (Event.EventType.NodeDeleted == watchedEvent.getType()){
                System.out.println("节点删除");

            }
        }else if (watchedEvent.getState() == Event.KeeperState.Disconnected){
            // 连接断开
            System.out.println("连接断开");
//            countDownLatch.countDown();
        }else if (watchedEvent.getState() == Event.KeeperState.AuthFailed){
            // 认证失败
            System.out.println("认证失败");
//            countDownLatch.countDown();
        }else if (watchedEvent.getState() == Event.KeeperState.ConnectedReadOnly){
            // 只读连接
            System.out.println("只读连接");
//            countDownLatch.countDown();
        }else if (watchedEvent.getState() == Event.KeeperState.SaslAuthenticated){
            // Sasl 认证
            System.out.println("Sasl 认证");
//            countDownLatch.countDown();
        }else if (watchedEvent.getState() == Event.KeeperState.Expired){
            // 超时
            System.out.println("超时");
//            countDownLatch.countDown();
        }

        System.out.println("-------------------------------------------------------------------");
    }
}
