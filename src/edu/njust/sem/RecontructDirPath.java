package edu.njust.sem;

import edu.njust.sem.util.DBUtil;
import java.sql.*;

public class RecontructDirPath {
	public static void main(String[] args) throws SQLException {
		Connection conn = DBUtil.getConn();
		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		String query = "SELECT session_id  FROM tab_catalogs2 group by 1";
		String update = "update tab_catalogs2 set session_id = ? where session_id = ?";
		PreparedStatement psUpdate= conn.prepareStatement(update);
		ResultSet rs = stmt.executeQuery(query);
		int i = 1;
		int pathId = 1;
		while(rs.next()){
			pathId = rs.getInt("session_id");
			psUpdate.setInt(1, i);
			psUpdate.setInt(2, pathId);
			i++;
			psUpdate.executeUpdate();
		}
	}
}
