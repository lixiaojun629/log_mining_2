package edu.njust.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.njust.sem.util.DBUtil;

/**
 * @author lixiaojun ����Ŀ¼·��
 */
public class CreateTabCatalogs {
	private String selectUrlId = "SELECT id,catalog_id FROM tab_catalogs";
	private String selectCataId = "SELECT catalog_id FROM tab_site where url_id = ?";

	Connection conn = DBUtil.getConn();
	PreparedStatement psSelectCataId = conn.prepareStatement(selectCataId);
	Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_UPDATABLE);

	public CreateTabCatalogs() throws SQLException {

	}

	/**
	 * ����Ŀ¼·�����tab_catalog
	 * 
	 * @throws SQLException
	 */
	public void run() throws SQLException {
		ResultSet rsUrlId = stmt.executeQuery(selectUrlId);
		ResultSet rsCataId = null;
		while (rsUrlId.next()) {
			int urlId = rsUrlId.getInt("catalog_id");
			psSelectCataId.setInt(1, urlId);
			rsCataId = psSelectCataId.executeQuery();
			if (rsCataId.next()) {
				int cataId = rsCataId.getInt("catalog_id");
				if (cataId < 0) {
					rsUrlId.deleteRow();// ResultSet deleteRow()ִ�к󣬵�ǰָ������
				} else {
					rsUrlId.updateInt("catalog_id", cataId);
					rsUrlId.updateRow();
				}
			} else {
				throw new RuntimeException("��ַurlId: " + urlId + " û��Ŀ¼");
			}
		}
	}

	/**
	 * ɾ��tab_catalog�����Ŀ¼·������С��2��Ŀ¼·��
	 * 
	 * @throws SQLException
	 */
	public void run1() throws SQLException {
		String selectCount = "SELECT count(*) FROM tab_catalogs where session_id = ?";
		String delectDecord = "delete FROM tab_catalogs where session_id = ?";
		PreparedStatement psDel = conn.prepareStatement(delectDecord);
		PreparedStatement ps = conn.prepareStatement(selectCount,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = null;
		for (int i = 1; i <= 4279; i++) {
			ps.setInt(1, i);
			rs = ps.executeQuery();
			rs.next();
			if (rs.getInt(1) <= 1) {
				psDel.setInt(1, i);
				psDel.executeUpdate();
			}
		}
	}
	/**
	 * ��tab_catalog���е�session_id�ֶ��ع���ʹ��session_idΪ��������
	 * @throws SQLException
	 */
	public void run2() throws SQLException{
		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		String query = "SELECT session_id  FROM tab_catalogs group by 1";
		String update = "update tab_catalogs set session_id = ? where session_id = ?";
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
	public static void main(String[] args) {
		try {
			CreateTabCatalogs c = new CreateTabCatalogs();
			c.run2();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConn();
		}
	}
}
