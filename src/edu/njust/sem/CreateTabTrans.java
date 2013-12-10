package edu.njust.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import edu.njust.sem.util.DBUtil;

public class CreateTabTrans {
	private String query = "SELECT id, session_id,url_id,user_id,visit_time FROM tab_trans where session_id = ?";
	private String update = "update tab_trans set session_id = session_id + 1 where id >= ?";
	private Connection conn = DBUtil.getConn();
	private PreparedStatement psQuery = conn.prepareStatement(query);
	private PreparedStatement psUpdate = conn.prepareStatement(update);
	private Statement stmt = conn.createStatement();
	private ResultSet rs = stmt
			.executeQuery("select count(*) from tab_trans");
	int rows = 0;
	int rowIndex = 0;
	private HashSet<Integer> urls = new HashSet<>();

	public CreateTabTrans() throws SQLException {
		rs.next();
		rows = rs.getInt(1);
	}

	public void run() throws SQLException {
		for (int i = 1;; i++) {
			System.out.println(i);
			psQuery.setInt(1, i);
			rs = psQuery.executeQuery();
			if (rs.next()) {
				rs.last();
				rowIndex = rs.getInt("id");
				rs.beforeFirst();
				while (rs.next()) {
					if (!urls.add(rs.getInt("url_id"))) {
						psUpdate.setInt(1, rs.getInt("id"));
						psUpdate.executeUpdate();
						break;
					}
				}
			} else {
				if (rowIndex < rows) {
					continue;
				} else {
					break;
				}
			}

			urls.clear();
		}
	}

	public static void main(String[] args) {
		try {
			CreateTabTrans c = new CreateTabTrans();
			c.run();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConn();
		}

	}
}
