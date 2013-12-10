package edu.njust.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.njust.sem.util.DBUtil;

public class CreateTabFirstNo {
	String select_url_id = "SELECT url_id FROM tab_site where url = ?";
	String select_user_id_pid = "SELECT user_id FROM tab_user where pid = ?";
	String select_user_id_ipbrowser = "SELECT user_id FROM tab_user where pid is null and ip_browser_info = ?";
	String select_log = "SELECT ip,visit_time,method,resource,status,referer,pid,ip_browser_info FROM tab_first";
	String insert_log = "insert into tab_first_no (user_id,visit_time,resource,referer) values (?,?,?,?)";

	Connection conn = DBUtil.getConn();
	PreparedStatement psSelectUrlId = conn.prepareStatement(select_url_id);
	PreparedStatement psSelectUserIdPid = conn
			.prepareStatement(select_user_id_pid);
	PreparedStatement psSelectUserIdIpBrowser = conn
			.prepareStatement(select_user_id_ipbrowser);
	PreparedStatement psInsertLog = conn.prepareStatement(insert_log);
	Statement stmt = conn.createStatement();
	ResultSet rsLog = stmt.executeQuery(select_log);
	ResultSet rsUrlId = null;
	private long count = 0;
	public CreateTabFirstNo() throws SQLException {
	}

	public void run() throws SQLException {
		while (rsLog.next()) {
			String resource = rsLog.getString("resource");
			String referer = rsLog.getString("referer");

			int resourceId = getUrlId(resource);
			int refererId = getUrlId(referer);
			
			int userId = getUserId(rsLog.getString("pid"),
					rsLog.getString("ip_browser_info"));
			if (userId < 0) {
				throw new RuntimeException("pid: " + rsLog.getString("pid")
						+ " ip_browser_info: "
						+ rsLog.getString("ip_browser_info") + " 该用户不存在!");
			} 
			insertLog(userId,resourceId,refererId);
		}
		psInsertLog.executeBatch();
	}
	private void insertLog(int userId,int resourceId,int refererId) throws SQLException{
		psInsertLog.setInt(1, userId);
		psInsertLog.setTimestamp(2, rsLog.getTimestamp("visit_time"));
		psInsertLog.setInt(3, resourceId);
		psInsertLog.setInt(4, refererId);
		psInsertLog.addBatch();
		count++;
		if(count % 10000 == 0){
			psInsertLog.executeBatch();
		}
	}
	private int getUrlId(String url) throws SQLException {
		int urlId = -1;
		psSelectUrlId.setString(1, url);
		rsUrlId = psSelectUrlId.executeQuery();
		if (rsUrlId.next()) {
			urlId = rsUrlId.getInt("url_id");
		}
		return urlId;
	}

	private int getUserId(String pid, String ipbrowser) throws SQLException {
		int userId = -1;
		ResultSet rsUserId = null;
		if (pid != null && pid.trim().length() > 0) {
			psSelectUserIdPid.setString(1, pid);
			rsUserId = psSelectUserIdPid.executeQuery();
			// pid在tab_user表中是唯一的
			if (rsUserId.next()) {
				userId = rsUserId.getInt("user_id");
			}
		} else {
			if (ipbrowser != null && ipbrowser.trim().length() > 0) {
				psSelectUserIdIpBrowser.setString(1, ipbrowser);
				rsUserId = psSelectUserIdIpBrowser.executeQuery();
				// ip_browser_info在tab_user表中也是唯一的
				if (rsUserId.next()) {
					userId = rsUserId.getInt("user_id");
				}
			}
		}
		return userId;
	}

	public static void main(String[] args) {
		CreateTabFirstNo c = null;
		try {
			c = new CreateTabFirstNo();
			c.run();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConn();
		}
	}
}
