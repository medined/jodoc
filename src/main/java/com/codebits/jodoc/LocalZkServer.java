package com.codebits.jodoc;

import java.io.IOException;
import java.util.Properties;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

public class LocalZkServer {

    public static void main(String[] args) throws IOException, QuorumPeerConfig.ConfigException {
        
        Properties properties = new Properties();
        properties.setProperty(" maxClientCnxns", "1000");
        properties.setProperty(" clientPort", "20000");
        properties.setProperty(" dataDir", "/zookeeper");
        properties.setProperty(" syncLimit", "5");
        properties.setProperty(" initLimit", "10");
        properties.setProperty(" tickTime", "2000");

        QuorumPeerConfig quorumPeerConfig = new QuorumPeerConfig(); 
        quorumPeerConfig.parseProperties(properties);
        
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.readFrom(quorumPeerConfig);
        
        
        ZooKeeperServerMain zookeeper = new ZooKeeperServerMain();
        zookeeper.runFromConfig(serverConfig);
    }

}
