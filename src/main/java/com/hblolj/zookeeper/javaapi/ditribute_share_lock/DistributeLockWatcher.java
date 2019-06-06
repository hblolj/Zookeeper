package com.hblolj.zookeeper.javaapi.ditribute_share_lock;

import com.hblolj.zookeeper.javaapi.enums.LockEnum;
import org.I0Itec.zkclient.IZkDataListener;

import java.util.concurrent.CountDownLatch;

/**
 * @author: hblolj
 * @Date: 2019/6/5 15:40
 * @Description:
 * @Version:
 **/
public class DistributeLockWatcher implements IZkDataListener {

    private CountDownLatch countDownLatch;

    private LockEnum lockType;

    private DistributeLock distributeLock;

    public DistributeLockWatcher(CountDownLatch countDownLatch, LockEnum lockEnum, DistributeLock distributeLock) {
        this.countDownLatch = countDownLatch;
        this.lockType = lockEnum;
        this.distributeLock = distributeLock;
    }

    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {

    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {

//        System.out.println(distributeLock.getCurrentNodeName() + " 解除了对 " + dataPath + " 的监听");
//        distributeLock.getZkClient().unsubscribeDataChanges(dataPath, this);

        // 判断是读锁还是写锁
        // 然后按照对应的逻辑判断是否可以获取锁，可以就放行，不可以就继续堵塞
        System.out.println(distributeLock.getCurrentNodeName() + " 监听到了节点 " + dataPath + " 的删除事件!");
        if (lockType.equals(LockEnum.READ)){
            // 读锁
            System.out.println(distributeLock.getCurrentNodeName() + " 尝试获取读锁!");
            boolean result = distributeLock.validateReadLock(false);
            if (result){
                // -1，释放阻塞
                System.out.println(distributeLock.getCurrentNodeName() + " 获取读锁成功!");
                this.countDownLatch.countDown();
            }
        }else if (lockType.equals(LockEnum.WRITE)){
            // 写锁
            System.out.println(distributeLock.getCurrentNodeName() + " 尝试获取写锁!");
            // 这里不能阻塞，阻塞了就获取不到监听信息了
            boolean result = distributeLock.validateWriteLock(false);
            if (result){
                System.out.println(distributeLock.getCurrentNodeName() + " 获取写锁成功!");
                // -1，释放阻塞
                this.countDownLatch.countDown();
            }
        }
    }
}
