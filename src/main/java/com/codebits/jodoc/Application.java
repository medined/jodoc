package com.codebits.jodoc;

import java.io.File;
import java.io.IOException;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.accumulo.minicluster.MiniAccumuloConfig;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        
        MiniAccumuloConfig miniAccumuloConfig = new MiniAccumuloConfig(new File("/accumulo"), "password");
        miniAccumuloConfig.setNumTservers(1);

        String zookeeperPort = System.getProperty("ZOOKEEPER_PORT");
        if (zookeeperPort != null) {
            miniAccumuloConfig.setZooKeeperPort(Integer.parseInt(zookeeperPort));
        }
        
        MiniAccumuloCluster accumulo = new MiniAccumuloCluster(miniAccumuloConfig);
        System.out.println(accumulo.getZooKeepers().split(":")[1]);
        accumulo.start();
        while (true) {
            Thread.sleep(10000);
        } 
    }
}
