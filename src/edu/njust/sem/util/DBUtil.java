package edu.njust.sem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
	private static Connection conn;

	public static Connection getConn() {
		if (conn == null) {

			try {
				// The newInstance() call is a work around for some
				// broken Java implementations
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {

				conn = DriverManager
						.getConnection(
								"jdbc:mysql://localhost:3306/log2",
								"root", "890");

			} catch (SQLException ex) {
				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
			
		}
		return conn;
	}

	public static void closeConn() {
		try {
			if(conn != null){
				conn.close();
				conn = null;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int getRowCount(ResultSet rs) throws SQLException{
		rs.last();
		int n = rs.getRow();
		rs.beforeFirst();
		return n;
	}
	
}
