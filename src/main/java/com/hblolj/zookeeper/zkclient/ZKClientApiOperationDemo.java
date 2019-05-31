package com.hblolj.zookeeper.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: hblolj
 * @Date: 2019/5/31 9:05
 * @Description:
 * @Version:
 **/
public class ZKClientApiOperationDemo {

    private final static String CONNECTS_STRING = "47.97.228.113:2181";

    private static ZkClient getInstance(){
        return new ZkClient(CONNECTS_STRING, 5000);
    }

    public static void main(String[] args) throws InterruptedException {

        ZkClient zkClient = getInstance();

        zkClient.subscribeStateChanges(new IZkStateListener() {
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                System.out.println("当前客户端变化变化后的状态: " + keeperState.getIntValue());
            }

            public void handleNewSession() throws Exception {
                System.out.println("建立连接成功!");
            }

            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {
                System.out.println("建立连接失败! " + throwable.getMessage());
            }
        });

        zkClient.subscribeDataChanges("/hblolj", new IZkDataListener() {
            public void handleDataChange(String s, Object o) throws Exception {
                // 监听当前节点的变化
                System.out.println("handleDataChange 节点: " + s + " 变化后的值: " + o);
            }

            public void handleDataDeleted(String s) throws Exception {
                // 监听当前节点的删除
                System.out.println("handleDataChange 节点: " + s + " 被删除了");
            }
        });

        zkClient.subscribeChildChanges("/hblolj", new IZkChildListener() {
            public void handleChildChange(String s, List<String> list) throws Exception {
                // 监听的是节点下子节点的创建
                // 创建的话，似乎只会监听直接子节点
                // 删除的话似乎所有子节点都会被监听
                System.out.println("handleChildChange 父节点路径: " + s + " 子节点路径: " + list);
            }
        });

        // 增删改查、子节点、监听、权限
        // 递归创建
        zkClient.createPersistent("/hblolj/child/xxx", true);

        // 获取直系子节点
        List<String> children = zkClient.getChildren("/hblolj");
        System.out.println(children);

        String result = zkClient.readData("/hblolj/child/xxx");
        System.out.println("result: " + result);

        // 给子节点设置值时，会触发父节点的监听
        zkClient.writeData("/hblolj/child/xxx", "321");

        result = zkClient.readData("/hblolj/child/xxx");
        System.out.println("result: " + result);

        // 给当前节点设置值时，会触发当前节点的监听
        zkClient.writeData("/hblolj", "123");
        result = zkClient.readData("/hblolj");
        System.out.println("result: " + result);

        // 删除 xxx 节点，会触发 hblolj 节点的 handleChildChange
        boolean b = zkClient.delete("/hblolj/child/xxx");
        System.out.println("删除结果: " + b);

        // 递归删除
        // 删除 child 节点，会触发 hblolj 节点的 handleChildChange
        // 删除 hblolj 节点，会触发 hblolj 节点的 handleDataDeleted
        b = zkClient.deleteRecursive("/hblolj");
        System.out.println("递归删除结果: " + b);
        TimeUnit.SECONDS.sleep(2);
    }
}
