package edu.njust.sem.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JdbcUtil {
	private static ComboPooledDataSource dataSource;
	static{
		dataSource = new ComboPooledDataSource();
	}
	//ȡ������Դ
	public static ComboPooledDataSource getDataSource() {
		return dataSource;
	}
	//ȡ������
	public static Connection getMySqlConnection() throws SQLException{
		return  dataSource.getConnection();
	}
	//�ر�����
	public static void close(Connection conn) throws SQLException{
		if(conn!=null){
			conn.close();
		}
	}
	public static void close(PreparedStatement pstmt) throws SQLException {
		if(pstmt!=null){
			pstmt.close();
		}
	}
	public static void close(ResultSet rs) throws SQLException {
		if(rs!=null){
			rs.close();
		}
	}
}
