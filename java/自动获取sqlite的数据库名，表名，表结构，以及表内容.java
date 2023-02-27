import java.sql.*;

public class SQLiteExplorer {
    
    public static void main(String[] args) {
        // SQLite连接URL
        String url = "jdbc:sqlite:/path/to/database.db";
        
        try {
            // 创建连接
            Connection conn = DriverManager.getConnection(url);
            
            // 获取所有表名
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, null, new String[] {"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                System.out.println("Table Name: " + tableName);
                
                // 获取表结构
                ResultSet rs2 = meta.getColumns(null, null, tableName, null);
                while (rs2.next()) {
                    String columnName = rs2.getString("COLUMN_NAME");
                    String columnType = rs2.getString("TYPE_NAME");
                    int columnSize = rs2.getInt("COLUMN_SIZE");
                    System.out.println("  " + columnName + " (" + columnType + "(" + columnSize + "))");
                }
                rs2.close();
                
                // 获取表内容
                Statement stmt = conn.createStatement();
                rs2 = stmt.executeQuery("SELECT * FROM " + tableName);
                ResultSetMetaData rsmd = rs2.getMetaData();
                int numColumns = rsmd.getColumnCount();
                while (rs2.next()) {
                    for (int i = 1; i <= numColumns; i++) {
                        System.out.print(rs2.getObject(i) + " ");
                    }
                    System.out.println();
                }
                rs2.close();
                stmt.close();
            }
            rs.close();
            
            // 关闭连接
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
