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

    private static String getProperty(final String name, final String defaultValue) {
        String value = System.getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static void main(String[] args) throws IOException, InterruptedException, AccumuloException, AccumuloSecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        String accumuloDirectory = getProperty("jodoc.accumulo.directory", "/accumulo");
        String accumuloPassword = getProperty("jodoc.accumulo.password", "password");
        String tserverCount = getProperty("jodoc.tserver.count", "2");
        String zookeeperPort = getProperty("jodoc.zookeeper.port", "20000");
        String monitorPort = getProperty("jodoc.monitor.port", "20001");
        String accumuloSchema = getProperty("jodoc.accumulo.schema", "");

        MiniAccumuloConfigImpl miniAccumuloConfig = new MiniAccumuloConfigImpl(new File(accumuloDirectory), accumuloPassword);
        miniAccumuloConfig.setNumTservers(Integer.parseInt(tserverCount));
        miniAccumuloConfig.setZooKeeperPort(Integer.parseInt(zookeeperPort));
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
