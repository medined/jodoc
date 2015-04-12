package com.codebits.jodoc;

import com.codebits.d4m.TableManager;
import java.io.File;
import java.io.IOException;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.admin.TableOperations;
import org.apache.accumulo.core.conf.Property;
import org.apache.accumulo.minicluster.impl.MiniAccumuloClusterImpl;
import org.apache.accumulo.minicluster.impl.MiniAccumuloConfigImpl;
import org.apache.accumulo.monitor.Monitor;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException, AccumuloException, AccumuloSecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        
        MiniAccumuloConfigImpl miniAccumuloConfig = new MiniAccumuloConfigImpl(new File("/accumulo"), "password");

        String tserverCount = System.getProperty("TSERVER_COUNT");
        if (tserverCount != null) {
            miniAccumuloConfig.setNumTservers(Integer.parseInt(tserverCount));
        } else {
            miniAccumuloConfig.setNumTservers(1);
        }
        
        String zookeeperPort = System.getProperty("ZOOKEEPER_PORT");
        if (zookeeperPort != null) {
            miniAccumuloConfig.setZooKeeperPort(Integer.parseInt(zookeeperPort));
        }
        
        System.out.println("Property.MONITOR_PORT.getKey(): " + Property.MONITOR_PORT.getKey());
        
        String monitorPort = System.getProperty(Property.MONITOR_PORT.getKey());
        if (tserverCount != null) {
            miniAccumuloConfig.setProperty(Property.MONITOR_PORT, monitorPort);
        } else {
            miniAccumuloConfig.setProperty(Property.MONITOR_PORT, "20001");
        }
        
        MiniAccumuloClusterImpl accumulo = new MiniAccumuloClusterImpl(miniAccumuloConfig);
        accumulo.start();
        accumulo.exec(Monitor.class);
        
        String accumuloSchema = System.getProperty("ACCUMULO_SCHEMA");
        if (accumuloSchema != null && accumuloSchema.equals("D4M")) {
            Connector connector = accumulo.getConnector("root", "password");
            TableOperations tableOperations = connector.tableOperations();
            TableManager tableManager = new TableManager(connector, tableOperations);
            tableManager.createTables();
            tableManager.addSplitsForSha1();
        }
        
        while (true) {
            Thread.sleep(10000);
        }
    }
}
