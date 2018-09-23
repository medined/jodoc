package com.codebits.jodoc;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.util.Bytes;

public class HbasePlay {

    private static final String TABLE_NAME = "MY_TABLE_NAME_TOO";
    private static final String CF_DEFAULT = "DEFAULT_COLUMN_FAMILY";

    public static void createOrOverwrite(Admin admin, HTableDescriptor table) throws IOException {
        if (admin.tableExists(table.getTableName())) {
            admin.disableTable(table.getTableName());
            admin.deleteTable(table.getTableName());
        }
        admin.createTable(table);
    }

    public static void createSchemaTables(Configuration config) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(config);
                Admin admin = connection.getAdmin()) {

            HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
            table.addFamily(new HColumnDescriptor(CF_DEFAULT).setCompressionType(Algorithm.SNAPPY));

            System.out.print("Creating table. ");
            createOrOverwrite(admin, table);
            System.out.println(" Done.");
        }
    }

    public static void modifySchema(Configuration config) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(config);
                Admin admin = connection.getAdmin()) {

            TableName tableName = TableName.valueOf(TABLE_NAME);
            if (admin.tableExists(tableName)) {
                System.out.println("Table does not exist.");
                System.exit(-1);
            }

            HTableDescriptor table = new HTableDescriptor(tableName);

            // Update existing table
            HColumnDescriptor newColumn = new HColumnDescriptor("NEWCF");
            newColumn.setCompactionCompressionType(Algorithm.GZ);
            newColumn.setMaxVersions(HConstants.ALL_VERSIONS);
            admin.addColumn(tableName, newColumn);

            // Update existing column family
            HColumnDescriptor existingColumn = new HColumnDescriptor(CF_DEFAULT);
            existingColumn.setCompactionCompressionType(Algorithm.GZ);
            existingColumn.setMaxVersions(HConstants.ALL_VERSIONS);
            table.modifyFamily(existingColumn);
            admin.modifyTable(tableName, table);

            // Disable an existing table
            admin.disableTable(tableName);

            // Delete an existing column family
            admin.deleteColumn(tableName, CF_DEFAULT.getBytes("UTF-8"));

            // Delete a table (Need to be disabled first)
            admin.deleteTable(tableName);
        }
    }

    public static void main(String... args) throws IOException {
        HbasePlay driver = new HbasePlay();
        driver.process();
    }

    public void process() throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "hbase");

        Connection connection = ConnectionFactory.createConnection(config);

        final String sTableName = "d4m";
        TableName tableName = TableName.valueOf(sTableName);
        Admin admin = connection.getAdmin();

        /*
         if (admin.isTableAvailable(tableName)) {
         admin.disableTable(tableName);
         admin.deleteTable(tableName);
         System.out.println("TABLE deleted.");
         }

         HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
         tableDescriptor.addFamily(new HColumnDescriptor("edge"));
         tableDescriptor.addFamily(new HColumnDescriptor("degree"));
         tableDescriptor.addFamily(new HColumnDescriptor("field"));
         tableDescriptor.addFamily(new HColumnDescriptor("transpose"));
         admin.createTable(tableDescriptor);
         System.out.println("Table created ");
         */
        byte[] cfEdge = Bytes.toBytes("edge");
        byte[] cfDegree = Bytes.toBytes("degree");
        byte[] cfField = Bytes.toBytes("field");
        byte[] cfTranspose = Bytes.toBytes("transpose");

        HTable table = new HTable(tableName, connection);

        // edge
        byte[] row = Bytes.toBytes("9a127928-b661-4e46-9103-3fc024f4");
        Put p = new Put(row);
        p.addImmutable(cfEdge, Bytes.toBytes("CITY_NAME|AKRON"), Bytes.toBytes("1"));
        table.put(p);

        p = new Put(row);
        p.addImmutable(cfEdge, Bytes.toBytes("STATE_NAME|MAINE"), Bytes.toBytes("1"));
        table.put(p);

        // degree
        row = Bytes.toBytes("CITY_NAME|AKRON");
        Increment increment1 = new Increment(row);
        increment1.addColumn(cfDegree, Bytes.toBytes("Degree"), 1L);

        row = Bytes.toBytes("STATE_NAME|MAINE");
        increment1 = new Increment(row);
        increment1.addColumn(cfDegree, Bytes.toBytes("Degree"), 1L);

        // field
        row = Bytes.toBytes("CITY_NAME");
        p = new Put(row);
        p.addImmutable(cfField, Bytes.toBytes("Field"), Bytes.toBytes("1"));
        table.put(p);
        row = Bytes.toBytes("STATE_NAME");
        p = new Put(row);
        p.addImmutable(cfField, Bytes.toBytes("Field"), Bytes.toBytes("1"));
        table.put(p);

        // transpose
        row = Bytes.toBytes("CITY_NAME|AKRON");
        p = new Put(row);
        p.addImmutable(cfTranspose, Bytes.toBytes("9a127928-b661-4e46-9103-3fc024f4"), Bytes.toBytes("1"));
        table.put(p);
        row = Bytes.toBytes("STATE_NAME|MAINE");
        p = new Put(row);
        p.addImmutable(cfTranspose, Bytes.toBytes("9a127928-b661-4e46-9103-3fc024f4"), Bytes.toBytes("1"));
        table.put(p);

        Scan s = new Scan();
        //s.addColumn(cfEdge, Bytes.toBytes("CITY_NAME|AKRON"));
        ResultScanner scanner = table.getScanner(s);
        for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
            for (Cell cell : rr.listCells()) {
                System.out.println("CF: " + Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength()));
            }
            //rr.value()
        }

        // list tables
        /*
         for (HTableDescriptor tableDescriptorx : admin.listTables()) {
         for (HColumnDescriptor columnDescriptor : tableDescriptorx.getColumnFamilies()) {
         System.out.format("%s %s\n", tableDescriptorx.getNameAsString(), columnDescriptor.getNameAsString());
         }
         }
         */
        System.out.println("END.");

    }

    public static void displayClasspath() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            System.out.println(url.getFile());
        }

    }
}
