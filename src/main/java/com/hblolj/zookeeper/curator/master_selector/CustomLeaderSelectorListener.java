package com.hblolj.zookeeper.curator.master_selector;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * @author: hblolj
 * @Date: 2019/6/6 17:25
 * @Description:
 * @Version:
 **/
public class CustomLeaderSelectorListener extends LeaderSelectorListenerAdapter{

    private String name;

    private LeaderSelector leaderSelector;

    private Integer count = 0;

    public CustomLeaderSelectorListener(CuratorFramework client, String name, String path) {
        this.name = name;
        this.leaderSelector = new LeaderSelector(client, path, this);
        leaderSelector.autoRequeue();
    }

    public void start(){
        leaderSelector.start();
    }

    public void stop(){
        leaderSelector.close();
    }

    @Override
    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        // 成功抢到了 master
        try {
            count++;
            System.out.println(name + " 抢到了 Leader，历史抢到次数: " + count);
            // 睡眠 1 秒
            TimeUnit.SECONDS.sleep(1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
