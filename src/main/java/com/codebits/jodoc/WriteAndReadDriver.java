package com.codebits.jodoc;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.util.CleanUp;

public class WriteAndReadDriver {

    public static void main(String[] args) throws UnknownHostException, SocketException {
        String instanceName = "miniInstance";
        String zooKeepers = null;
        String user = "root";
        AuthenticationToken authToken = new PasswordToken("password");
        String tableName = "Tedge";

        String ipAddress = "10.110.107.34";
        
        String zookeeperPort = System.getProperty("ZOOKEEPER_PORT");
        if (zookeeperPort == null) {
            zooKeepers = ipAddress + ":20000";
        } else {
            zooKeepers = String.format("%s:%s", ipAddress, zookeeperPort);
        }
        
        ZooKeeperInstance instance = new ZooKeeperInstance(instanceName, zooKeepers);
        Connector connector;
        try {
            connector = instance.getConnector(user, authToken);
        } catch (AccumuloException | AccumuloSecurityException e) {
            throw new RuntimeException(e);
        }

        if (!connector.tableOperations().exists(tableName)) {
            System.out.println("TABLE DOES NOT EXIST");
            try {
                connector.tableOperations().create(tableName);
            } catch (AccumuloException | AccumuloSecurityException | TableExistsException e) {
               throw new RuntimeException(e);
            }
        } else {
            System.out.println("TABLE EXISTS");
        }
        
        CleanUp.shutdownNow();
        
    }
}
