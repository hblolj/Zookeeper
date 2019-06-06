package com.hblolj.zookeeper.zkclient.master_selector;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * @author: hblolj
 * @Date: 2019/6/6 10:48
 * @Description:
 * @Version:
 **/
public class MasterSelectorTest {

    private final static String CONNECTS_STRING = "47.97.228.113:2181";

    public static void main(String[] args) {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(() -> {
                MasterSelector selector = null;
                try {
                    ZkClient zkClient = new ZkClient(CONNECTS_STRING, 5000, 5000, new SerializableSerializer());
                    UserClient client = new UserClient(finalI, "client-" + finalI);
                    selector = new MasterSelector(zkClient, client);
                    cyclicBarrier.await();
                    // start 后，如果抢到了 master，里面会延迟 2 秒删除 master
                    selector.start();
                    // 这里休眠 5 秒，防止没抢到的线程直接 stop 走了
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }finally {
                    if (selector != null){
                        selector.stop();
                    }
                }
            }).start();

        }
    }
}
