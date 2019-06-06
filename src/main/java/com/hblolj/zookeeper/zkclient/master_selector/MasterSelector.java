package com.hblolj.zookeeper.zkclient.master_selector;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: hblolj
 * @Date: 2019/6/5 17:21
 * @Description: Master 竞选
 * @Version:
 **/
public class MasterSelector {

    private static final String MASTER_PATH = "/master";

    private ZkClient zkClient;

    private UserClient client;

    private UserClient master;

    private Boolean isRunning = false;

    private IZkDataListener listener;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public MasterSelector(ZkClient zkClient, UserClient client) {
        // client 去竞选 master
        this.zkClient = zkClient;
        this.client = client;
        listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                // 节点删除触发，Master 节点被删除，当前节点尝试去竞选
                System.out.println(" Master 节点被删除了，" + client.getClientId() + " 继续参加竞选");
                doSelectorMaster();
            }
        };
    }

    public void start(){
        System.out.println(client.getClientId() + " 开始竞选 Master, status: " + isRunning);
        if (!isRunning) {
            isRunning = true;
            zkClient.subscribeDataChanges(MASTER_PATH, listener);
            // 开始竞选
            doSelectorMaster();
        }
    }

    public void stop(){
        System.out.println(client.getClientId() + " 节点，stop, status: " + isRunning);
        if (isRunning){
            isRunning = false;
            scheduledExecutorService.shutdown();
            zkClient.unsubscribeDataChanges(MASTER_PATH, listener);
            // 删除节点
            releaseMaster();
        }
    }

    private void doSelectorMaster(){
        // 核心逻辑，竞选 master 的客户端，所有客户端都监听 master 节点，一起创建去临时 master 节点，创建成功者就是 master。
        // 当之前的 master 断开连接(节点被删除后)，监听触发，剩下的 client 继续去竞争创建 master 节点
        try {
            zkClient.createEphemeral(MASTER_PATH, client);
            // 没有抛出异常，代表竞选 Master 节点成功
            master = client;
            System.out.println(client.getClientId() + " 竞选 Master 节点成功!");

            // 5 秒钟之后 Master 删除，让其他人进行竞选，模拟 Master 断开
            scheduledExecutorService.schedule(this::stop, 2, TimeUnit.SECONDS);
        }catch (ZkNodeExistsException e){
            // master 节点已经被创建了
            UserClient node = zkClient.readData(MASTER_PATH, true);
            if (null == node){
                System.out.println(" Master 节点已经被删除了!");
                // 进行节点竞选
            }else {
                // 更新当前存储的 Master 节点
                System.out.println(client.getClientId() + " 竞选 Master 节点失败!");
                master = node;
            }
        }
    }

    private void releaseMaster(){
        // 如果当前节点是 Master 节点，删除
        if (checkIsMaster()){
            zkClient.delete(MASTER_PATH, -1);
        }
    }

    private Boolean checkIsMaster(){
        UserClient node = zkClient.readData(MASTER_PATH, true);
        if (null == node){
            return false;
        }
        if (node.getClientId() == client.getClientId()){
            master = client;
            return true;
        }
        return false;
    }
}
