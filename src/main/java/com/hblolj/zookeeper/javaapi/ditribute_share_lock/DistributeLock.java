package com.hblolj.zookeeper.javaapi.ditribute_share_lock;

import com.hblolj.zookeeper.javaapi.ZooKeeperClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * @author: hblolj
 * @Date: 2019/6/5 13:42
 * @Description:
 * @Version:
 **/
// TODO: 2019/6/5 目前没有分读锁与写锁，实际上还要分读锁和写锁做区别处理，后面抽时间优化，整理，记录，博客输出
public class DistributeLock {

    private static final String ROOT_LOCKS = "/LOCKS";

    private ZooKeeper zooKeeper;

    private String lockId;

    private int sessionTimeout;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private final static byte[] data = "crlf".getBytes();

    public DistributeLock() {
        try {
            this.zooKeeper = ZooKeeperClient.getInstance();
            this.sessionTimeout = ZooKeeperClient.getSessionTimeout();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // lock
    public Boolean lock(){

        try {
            // 1. 创建临时有序节点
            lockId = zooKeeper.create(ROOT_LOCKS + "/", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName() + " 成功创建了 Lock 节点 " + lockId + "，开始去竞争锁");

            // 2. 获取父节点下所有 child
            List<String> childrenNodes = zooKeeper.getChildren(ROOT_LOCKS, true);

            // 3. 按照读写特性进行判断
            TreeSet<String> sortedSet = new TreeSet<>();
            for (String node : childrenNodes) {
                sortedSet.add(ROOT_LOCKS + "/" + node);
            }

            String first = sortedSet.first();
            if (lockId.equals(first)){
                // 当前节点就是最小的节点
                return true;
            }

            // less than 小于当前参数的数据，找到所有小于当前节点的节点集合
            SortedSet<String> lessThanLockIds = sortedSet.headSet(lockId);
            if (!lessThanLockIds.isEmpty()){
                // 找到当前节点前面最近的一个节点
                String preLockId = lessThanLockIds.last();
                // 监听该节点的删除时间，如果删除了就重新进行锁获取
                zooKeeper.exists(preLockId, new DistributeLockWatcher(countDownLatch));
                // result 为 true 表示不是超时触发通过的，反之则是
                return countDownLatch.await(sessionTimeout, TimeUnit.SECONDS);
            }

            return true;
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }

//        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//        lock.readLock().lock();
//        lock.readLock().unlock();
//        lock.writeLock().lock();
//        lock.writeLock().unlock();

        // between 两个参数之间的数据
//        sortedSet.subSet();
        // greater than 大于当前参数的数据
//        sortedSet.tailSet();

        // 尝试获取锁
        // 如果是获取读锁的话，看前面有没有写锁，如果没有，就获取成功。
        // 如果是获取写锁的话，看前面有没有任意锁，如果没有，就获取成功。

        return false;
    }

    // unlock
    public void unlock(){
        try {
            // 1. 删除当前节点
            System.out.println(lockId + " 开始释放锁");
            zooKeeper.delete(lockId, -1);
            System.out.println(lockId + " 锁节点删除成功");
            // 2. lockId 置为 null
            lockId = null;
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    public String getLockId() {
        return lockId;
    }

    public static void main(String[] args) {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        // 开启 10 个线程来争夺锁
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                DistributeLock lock = null;
                try {
                    lock = new DistributeLock();
                    cyclicBarrier.await();
                    Boolean result = lock.lock();
                    if (!result){
                        // 获取锁失败
                        System.out.println(lock.getLockId() + " 获取锁失败!");
                    }else {
                        // 获取锁成功，休眠 0.5 秒，不直接释放，让后面的人等待一会
                        System.out.println("节点 " + lock.getLockId() + " 成功获取锁!");
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }finally {
                    if (null != lock){
                        lock.unlock();
                    }
                }
            }).start();
        }
    }
}
