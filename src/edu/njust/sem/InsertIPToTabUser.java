package edu.njust.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.njust.sem.util.DBUtil;

public class InsertIPToTabUser {
	private Connection conn = DBUtil.getConn();
	private String selectIP = "SELECT ip_browser_info FROM tab_first where pid = ?";
	private String selectUser = "SELECT user_id, pid,ip_browser_info FROM tab_user";
	public void run() throws SQLException{
		PreparedStatement ps = conn.prepareStatement(selectIP);
		Statement stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery(selectUser);
		ResultSet rsIp = null;
		String pid = null;
		while(rs.next()){
			pid = rs.getString("pid");
			if(pid != null && pid.trim().length() > 0){
				ps.setString(1, pid);
				try{
					rsIp = ps.executeQuery();
					rsIp.next();
					String ipBrowser = rsIp.getString("ip_browser_info");
					rs.updateString("ip_browser_info", ipBrowser);
					rs.updateRow();
				}catch(SQLException e){
					System.out.println(pid);
					e.printStackTrace();
					continue;
				}
				
			}
		}
	}
	public static void main(String[] args) {
		InsertIPToTabUser i = new InsertIPToTabUser();
		try {
			i.run();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
