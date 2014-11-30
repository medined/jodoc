package com.codebits.jodoc;


import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;

public class WriteAndReadDriver {

    public static void main(String[] args) {
        String instanceName = "miniInstance";
        String zooKeepers = null;
        String user = "root";
        AuthenticationToken authToken = new PasswordToken("password");
        String tableName = "demo";

        String zookeeperPort = System.getProperty("ZOOKEEPER_PORT");
        if (zookeeperPort == null) {
            zooKeepers = "localhost:20000";
        } else {
            zooKeepers = String.format("localhost:%s", zookeeperPort);
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

    }
}
