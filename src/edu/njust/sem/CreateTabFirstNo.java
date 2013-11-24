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
	String select_user_id_ipbrowser = "SELECT user_id FROM tab_user where ip_browser_info = ?";
	String select_log = "SELECT ip,visit_time,method,resource,status,referer,pid,ip_browser_info FROM tab_first";
	String insert_log = "insert into tab_first_no (user_id,ip,visit_time,resource,status,referer) values (?,?,?,?,?,?)";

	Connection conn = DBUtil.getConn();
	PreparedStatement psSelectUrlId1 = conn.prepareStatement(select_url_id);
	PreparedStatement psSelectUrlId2 = conn.prepareStatement(select_url_id);
	PreparedStatement psSelectUserIdPid = conn
			.prepareStatement(select_user_id_pid);
	PreparedStatement psSelectUserIdIpBrowser = conn
			.prepareStatement(select_user_id_ipbrowser);
	PreparedStatement psInsertLog = conn.prepareStatement(insert_log);
	Statement stmt = conn.createStatement();
	ResultSet rsLog = stmt.executeQuery(select_log);
	ResultSet rsResource = null;
	ResultSet rsReferer = null;

	public CreateTabFirstNo() throws SQLException {
	}
	public void run() throws SQLException {
		while (rsLog.next()) {
			String resource = rsLog.getString("resource");
			String referer = rsLog.getString("referer");

			psSelectUrlId1.setString(1, resource);
			rsResource = psSelectUrlId1.executeQuery();

			psSelectUrlId2.setString(1, referer);
			rsReferer = psSelectUrlId2.executeQuery();

			int resourceId = -1;
			int refererId = -1;

			boolean flag = false;
			//tab_site表中不可能有两个相同的url，所以无需判断rsResource,rsReferer中有两条以技术结果的情况
			if (rsResource.next()) {
				flag = true;
				resourceId = rsResource.getInt("url_id");
			}else{//该用户此次请求的资源不在我们要分析的范围之内，跳过此条日志
				continue;
			}
			if (rsReferer.next()) {
				flag = true;
				refererId = rsReferer.getInt("url_id");
			}

			if (flag) {
				int userId = getUserId(rsLog.getString("pid"),rsLog.getString("ip_browser_info"));
				if(userId < 0){
					throw new RuntimeException("pid: "+rsLog.getString("pid")+" ip_browser_info: "+rsLog.getString("ip_browser_info")+" 该用户不存在!");
				}else{
					psInsertLog.setInt(1, userId);
					psInsertLog.setString(2, rsLog.getString("ip"));
					psInsertLog.setTimestamp(3, rsLog.getTimestamp("visit_time"));
					psInsertLog.setInt(4, resourceId);
					psInsertLog.setString(5,rsLog.getString("status"));
					psInsertLog.setInt(6, refererId);
					psInsertLog.executeUpdate();
				}
			}
		}
	}

	private int getUserId(String pid, String ipbrowser) throws SQLException {
		int userId = -1;
		ResultSet rsUserId = null;
		if (pid != null && pid.trim().length() > 0) {
			psSelectUserIdPid.setString(1, pid);
			rsUserId = psSelectUserIdPid.executeQuery();
			//pid在tab_user表中是唯一的
			if(rsUserId.next()){
				userId = rsUserId.getInt("user_id");
			}
		} else {
			if (ipbrowser != null && ipbrowser.trim().length() > 0) {
				psSelectUserIdIpBrowser.setString(1, ipbrowser);
				rsUserId = psSelectUserIdIpBrowser.executeQuery();
				//ip_browser_info在tab_user表中也是唯一的
				if(rsUserId.next()){
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
		}finally{
			DBUtil.closeConn();
		}
	}
}
