package de.fhzwickau.reisewelle.config;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseXMLExport {

    public static void main(String[] args) {
        String outputFile = "DB_EXPORT_2.xml";

        try (Connection conn = JDBCConfig.getInstance();
             FileWriter writer = new FileWriter(outputFile)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<Database>\n");

            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, "dbo", "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                String sql = "SELECT * FROM [" + tableName + "] FOR XML PATH('Row'), ROOT('" + tableName + "')";

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {

                    if (rs.next()) {
                        String xmlContent = rs.getString(1);
                        writer.write(xmlContent + "\n");
                    }

                } catch (Exception e) {
                    System.err.println("Failed to export table: " + tableName);
                    e.printStackTrace();
                }
            }

            writer.write("</Database>");
            System.out.println("Export completed to: " + outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
