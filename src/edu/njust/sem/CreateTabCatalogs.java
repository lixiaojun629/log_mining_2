package edu.njust.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.njust.sem.util.DBUtil;

/**
 * @author lixiaojun 生成目录路径
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
	 * 生成目录路径表格tab_catalog
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
				if (cataId == 0) {
					rsUrlId.deleteRow();// ResultSet deleteRow()执行后，当前指针上移
				} else {
					rsUrlId.updateInt("catalog_id", cataId);
					rsUrlId.updateRow();
				}
			} else {
				System.out.println("网址urlId: "+urlId+" 没有目录");
				rsUrlId.deleteRow();
			}
		}
	}

	/**
	 * 生成tab_catalog1表格（删除tab_catalog表格中目录路径长度小于2的目录路径）
	 * 
	 * @throws SQLException
	 */
	public void run1() throws SQLException {
		String selectCount = "SELECT count(*) FROM tab_catalogs1 where session_id = ?";
		String delectDecord = "delete FROM tab_catalogs1 where session_id = ?";
		PreparedStatement psDel = conn.prepareStatement(delectDecord);
		PreparedStatement ps = conn.prepareStatement(selectCount,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = null;
		for(int i = 1; i <= 3584; i++){
			ps.setInt(1, i);
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt(1) <= 1){
					psDel.setInt(1, i);
					psDel.executeUpdate();
				}
			}
		}

	}

	public static void main(String[] args) {
		try {
			CreateTabCatalogs c = new CreateTabCatalogs();
			c.run1();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConn();
		}
	}
}
