package com.codebits.jodoc;

import java.io.IOException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

public class LocalZkClient {

    public static void main(String[] args) throws IOException, QuorumPeerConfig.ConfigException, Exception {
        int BASE_SLEEP_TIME_MS = 200;
        int MAX_RETRIES = 3;
        String CONNECTION_STRING = "localhost:20000";

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(CONNECTION_STRING, retryPolicy);
        client.start();

        //client.create().forPath("/my");
        
        byte[] configBytes;
        
        final GetDataBuilder getDataBuilder = client.getData();
        try {
            configBytes = getDataBuilder.forPath("/my/path/david");
        } catch (NoNodeException e) {
            System.out.println("Creating Zookeeper Node.");
            configBytes = "david".getBytes();
            client.create().forPath("/my/path/david", configBytes);
        }
        final String config = new String(configBytes);

        System.out.println("Data: " + config);
    }

}
