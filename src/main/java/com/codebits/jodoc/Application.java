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

    private static String getStringProperty(final String name, final String defaultValue) {
        String value = System.getenv(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    private static Integer getIntegerProperty(final String name, final Integer defaultValue) {
        String value = System.getenv(name);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }
    
    
    public static void main(String[] args) throws IOException, InterruptedException, AccumuloException, AccumuloSecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        String accumuloDirectory = getStringProperty("jodoc.accumulo.directory", "/accumulo");
        String accumuloPassword = getStringProperty("jodoc.accumulo.password", "password");
        Integer tserverCount = getIntegerProperty("jodoc.tserver.count", 2);
        Integer zookeeperPort = getIntegerProperty("jodoc.zookeeper.port", 20000);
        String monitorPort = getStringProperty("jodoc.monitor.port", "20001");
        String accumuloSchema = getStringProperty("jodoc.accumulo.schema", "");

        System.out.println("Configuration Report");
        System.out.println("accumuloDirectory: " + accumuloDirectory);
        System.out.println("accumuloPassword: XXXXXXXX");
        System.out.println("accumuloSchema: " + accumuloSchema);
        System.out.println("monitorPort: " + monitorPort);
        System.out.println("tserverCount: " + tserverCount);
        System.out.println("zookeeperPort: " + zookeeperPort);
        
        MiniAccumuloConfigImpl miniAccumuloConfig = new MiniAccumuloConfigImpl(new File(accumuloDirectory), accumuloPassword);
        miniAccumuloConfig.setNumTservers(tserverCount);
        miniAccumuloConfig.setZooKeeperPort(zookeeperPort);
        miniAccumuloConfig.setProperty(Property.MONITOR_PORT, monitorPort);

        MiniAccumuloClusterImpl accumulo = new MiniAccumuloClusterImpl(miniAccumuloConfig);
        accumulo.start();
        accumulo.exec(Monitor.class);

        if (accumuloSchema.equalsIgnoreCase("D4M")) {
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
