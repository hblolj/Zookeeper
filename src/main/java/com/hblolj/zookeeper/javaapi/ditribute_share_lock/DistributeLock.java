package com.hblolj.zookeeper.javaapi.ditribute_share_lock;

import com.hblolj.zookeeper.javaapi.ZooKeeperClient;
import com.hblolj.zookeeper.javaapi.enums.LockEnum;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: hblolj
 * @Date: 2019/6/5 13:42
 * @Description:
 * @Version:
 **/
public class DistributeLock {

    private static final String ROOT_LOCKS = "/LOCKS";

    private ZkClient zkClient;

    private String currentNodeName;

    private String readLock;

    private String writeLock;

    private int sessionTimeout;

    private CountDownLatch readCountDownLatch = new CountDownLatch(1);

    private CountDownLatch writeCountDownLatch = new CountDownLatch(1);

    private DistributeLockWatcher readWatcher = new DistributeLockWatcher(readCountDownLatch, LockEnum.READ, this);

    private DistributeLockWatcher writeWatcher = new DistributeLockWatcher(writeCountDownLatch, LockEnum.WRITE, this);

    private final static String data = "crlf";

    public DistributeLock(String name) {
        try {
            this.currentNodeName = name;
            this.zkClient = ZooKeeperClient.getInstance();
            this.sessionTimeout = ZooKeeperClient.getSessionTimeout();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 获取读锁
    public Boolean readLock(){
        try {
            createNodeAndGetChildrens(LockEnum.READ);
            System.out.println(currentNodeName + " 开始尝试获取读锁!");
            return validateReadLock(true);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("Duplicates")
    boolean validateReadLock(Boolean canBlock) throws KeeperException, InterruptedException {

        Boolean exist = false;

        // 2. 获取父节点下所有 child
        List<String> childrenNodes = zkClient.getChildren(ROOT_LOCKS);
        childrenNodes.sort(Comparator.comparing(c -> c.split("-")[1]));

        // 判断前面是否存在写锁，如果存在，获取前面的写锁列表，监听最后一个写锁的删除事件，
        // 事件触发后再次判断当前节点前面是否还存在写锁，不存在获取锁，存在监听最后一个

        int currentIndex = 0;
        String readLockName = readLock.replace(ROOT_LOCKS + "/", "");
        for (int i = childrenNodes.size() - 1; i >= 0; i--) {
            if (readLockName.equals(childrenNodes.get(i))){
                currentIndex = i;
                exist = true;
            }else {
                if (i < currentIndex && childrenNodes.get(i).split("-")[0].equals(LockEnum.WRITE.name())){
                    System.out.println(currentNodeName + " 前面存在写锁: " + childrenNodes.get(i) + " 现在对其进行监听，等待释放");
                    // 监听该写写节点
                    String path = ROOT_LOCKS + "/" + childrenNodes.get(i);
                    zkClient.subscribeDataChanges(path, readWatcher);
//                    zooKeeper.exists(ROOT_LOCKS + "/" + childrenNodes.get(i), new DistributeLockWatcher(readCountDownLatch, LockEnum.READ, this));
                    // result 为 true 表示不是超时触发通过的，反之则是
                    if (canBlock){
                        boolean b = readCountDownLatch.await(sessionTimeout, TimeUnit.SECONDS);
                        if (!b){
                            System.out.println(currentNodeName + " 阻塞超时释放!");
                        }
                        return b;
                    }else {
                        return false;
                    }
                }
            }
        }

        if (exist){
            System.out.println(currentNodeName + " 位置是: " + currentIndex);
        }
        return exist;
    }

    // 释放读锁
    public void unReadLock(){
        System.out.println(currentNodeName + " 开始释放读锁");
        if (readLock != null){
            zkClient.delete(readLock, -1);
            System.out.println(currentNodeName + " 释放读锁成功");
            readLock = null;
        }
    }

    // 获取写锁
    public Boolean writeLock(){

        try {
            createNodeAndGetChildrens(LockEnum.WRITE);
            System.out.println(currentNodeName + " 开始尝试获取写锁!");

            // 判断前面是否存任意锁，如果存在，获取前面的锁列表，监听最后一个锁的删除事件，
            // 事件触发后再次判断当前节点前面是否还存在锁，不存在获取锁，存在监听最后一个
            return validateWriteLock(true);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }

        return false;
    }

    @SuppressWarnings("Duplicates")
    boolean validateWriteLock(Boolean canBlock) throws KeeperException, InterruptedException {

        Boolean exist = false;

        // 2. 获取父节点下所有 child
        List<String> childrenNodes = zkClient.getChildren(ROOT_LOCKS);
        childrenNodes.sort(Comparator.comparing(c -> c.split("-")[1]));

        int currentIndex = 0;
        String writeLockName = writeLock.replace(ROOT_LOCKS + "/", "");
        for (int i = childrenNodes.size() - 1; i >= 0; i--) {
            if (writeLockName.equals(childrenNodes.get(i))){
                currentIndex = i;
                exist = true;

                if (currentIndex > 0){
                    System.out.println(currentNodeName + " 前面存在锁: " + childrenNodes.get(i-1) + " 现在对其进行监听，等待释放");
                    // 监听该写写节点
                    final String path = ROOT_LOCKS + "/" + childrenNodes.get(i - 1);
                    zkClient.subscribeDataChanges(path, writeWatcher);
//                    zooKeeper.exists(ROOT_LOCKS + "/" + childrenNodes.get(i-1), new DistributeLockWatcher(writeCountDownLatch, LockEnum.WRITE, this));
                    // result 为 true 表示不是超时触发通过的，反之则是
                    if (canBlock){
                        boolean b = writeCountDownLatch.await(sessionTimeout, TimeUnit.SECONDS);
                        if (!b){
                            System.out.println(currentNodeName + " 阻塞超时释放!");
                        }
                        return b;
                    }else {
                        return false;
                    }
                }
            }
        }
        if (exist){
            System.out.println(currentNodeName + " 位置是: " + currentIndex);
        }
        return exist;
    }

    // 释放写锁
    public void unWriteLock(){
        System.out.println(currentNodeName + " 开始释放写锁");
        if (writeLock != null){
            zkClient.delete(writeLock, -1);
            System.out.println(currentNodeName + " 释放写锁成功");
            writeLock = null;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            DistributeLock lock1 = null;
            try {
                lock1 = new DistributeLock("第一个读锁");
                Boolean b = lock1.readLock();
                System.out.println("第一个读锁获取结果: " + (b ? "成功" : "失败"));
                for (int i = 0; i < 5; i++) {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("第一个读锁的睡眠的第" + (i+1) + "秒!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if (null != lock1){
                    lock1.unReadLock();
                }
            }
        }).start();

        TimeUnit.SECONDS.sleep(1);

        // 第二个线程也是获取读锁
        new Thread(() -> {
            DistributeLock lock2 = null;
            try {
                lock2 = new DistributeLock("第二个读锁");
                Boolean b = lock2.readLock();
                System.out.println("第二个读锁获取结果: " + (b ? "成功" : "失败"));
                for (int i = 0; i < 2; i++) {
                    System.out.println("第二个读锁的睡眠的第" + (i+1) + "秒!");
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (null != lock2){
                    lock2.unReadLock();
                }
            }
        }).start();

        TimeUnit.SECONDS.sleep(1);

        // 第三个线程获取写锁，获取成功后，睡眠 10 秒，然后释放锁
        new Thread(() -> {
            DistributeLock lock3 = null;
            try {
                lock3 = new DistributeLock("第三个写锁");
                Boolean b = lock3.writeLock();
                System.out.println("第三个写锁获取结果: " + (b ? "成功" : "失败"));
                for (int i = 0; i < 3; i++) {
                    System.out.println("第三个写锁的睡眠的第" + (i+1) + "秒!");
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (null != lock3){
                    lock3.unWriteLock();
                }
            }
        }).start();

        TimeUnit.SECONDS.sleep(1);

        // 第四个节点获取写锁
        new Thread(() -> {
            DistributeLock lock4 = null;
            try {
                lock4 = new DistributeLock("第四个写锁");
                Boolean b = lock4.writeLock();
                System.out.println("第四个写锁获取结果: " + (b ? "成功" : "失败"));
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (null != lock4){
                    lock4.unWriteLock();
                }
            }
        }).start();

        TimeUnit.SECONDS.sleep(1);

        // 第五个线程获取读锁
        new Thread(() -> {
            DistributeLock lock5 = null;
            try {
                lock5 = new DistributeLock("第五个读锁");
                Boolean b = lock5.readLock();
                System.out.println("第五个读锁获取结果: " + (b ? "成功" : "失败"));
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (null != lock5){
                    lock5.unReadLock();
                }
            }
        }).start();
    }

    // 创建当前客户端端的节点
    private void createNodeAndGetChildrens(LockEnum type) throws KeeperException, InterruptedException {
        // 1. 创建临时有序节点
        if (type == LockEnum.READ){
            readLock = zkClient.createEphemeralSequential(ROOT_LOCKS + "/" + type + "-", data, ZooDefs.Ids.OPEN_ACL_UNSAFE);
            System.out.println(currentNodeName + " 成功创建了 Lock 节点 " + readLock + "，开始去竞争读锁");
        }else if (type == LockEnum.WRITE){
            writeLock = zkClient.createEphemeralSequential(ROOT_LOCKS + "/" + type + "-", data, ZooDefs.Ids.OPEN_ACL_UNSAFE);
            System.out.println(currentNodeName + " 成功创建了 Lock 节点 " + writeLock + "，开始去竞争写锁");
        }
    }

    public String getCurrentNodeName() {
        return currentNodeName;
    }
}
