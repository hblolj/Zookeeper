package com.hblolj.zookeeper.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: hblolj
 * @Date: 2019/5/31 8:59
 * @Description:
 * @Version:
 **/
public class SessionDemo {

    private final static String CONNECTS_STRING = "47.97.228.113:2181";

    private static String path = null;

    public static void main(String[] args) throws InterruptedException, IOException {

        ZkClient zkClient = new ZkClient(CONNECTS_STRING, 5000);

//        String test = zkClient.createEphemeralSequential("/test", "123", ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        System.out.println(test);
//
//        TimeUnit.SECONDS.sleep(5);

//        String test = zkClient.createEphemeralSequential("/parent/xxxx-", "123", ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        System.out.println(test);

//        List<String> children = zkClient.getChildren("/curator");
//        System.out.println(children);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        IZkDataListener listener = new IZkDataListener() {

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println(dataPath + " -> " + data);
                zkClient.subscribeDataChanges("/parent/b", this);
                countDownLatch.await();
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {

            }
        };

        zkClient.subscribeDataChanges("/parent/a", listener);

        zkClient.writeData("/parent/a", "999");

        TimeUnit.SECONDS.sleep(5);

        zkClient.writeData("/parent/b", "888");

//        zkClient.subscribeDataChanges("/LOCKS", listener);
//
//        zkClient.writeData("/LOCKS", "666");

        System.in.read();
    }
}
