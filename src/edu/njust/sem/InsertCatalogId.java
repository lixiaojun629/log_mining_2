package edu.njust.sem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import edu.njust.sem.util.DBUtil;

public class InsertCatalogId {
	private static Logger logger = Logger.getLogger(InsertCatalogId.class);
	private Connection conn = DBUtil.getConn();

	private String selectUrls = "SELECT url_id,url,catalog ,catalog_id FROM tab_site";

	private Statement stmtUrls = conn.createStatement(
			ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
	private Statement stmtCatalogId = conn.createStatement();
	public InsertCatalogId() throws SQLException {

	}

	public void run() throws SQLException {
		ResultSet rsUrls = stmtUrls.executeQuery(selectUrls);
		ResultSet rsCatalogId = null;
		while (rsUrls.next()) {
			String catalog = rsUrls.getString("catalog");
			String[] parts = null;
			try{
				parts = catalog.split("-|_");
			}catch(NullPointerException e){
				e.printStackTrace();
				logger.error("url_id: " + rsUrls.getInt("url_id") 
						+" url: "+ rsUrls.getString("url")
						+ " catalog: "+ rsUrls.getString("catalog"));
				continue;
			}
			rsCatalogId = stmtCatalogId.executeQuery(getSQL(parts));
			if (1 == DBUtil.getRowCount(rsCatalogId)) {
				rsCatalogId.next();
				int id = rsCatalogId.getInt("id");
				rsUrls.updateInt("catalog_id", id);
				rsUrls.updateRow();
				System.out.println(rsCatalogId.getString("dir"));
			} else {
				logger.debug("url_id: " + rsUrls.getInt("url_id") 
						+" url: "+ rsUrls.getString("url")
						+ " catalog: "+ rsUrls.getString("catalog"));
			}
		}
	}

	private String getSQL(String[] parts) {
		String selectCatalogId = "SELECT id,dir FROM tab_dir where dir like ";
		if (parts.length == 1) {
			selectCatalogId += "\'" + parts[0] + "\'";

		} else if (parts.length >= 2) {
			selectCatalogId += "\'" + parts[0] + '%' + "\' ";
			selectCatalogId += " and dir like " + "\'" + '%'
					+ parts[parts.length - 1] + "\' ";

			for (int i = 1; i < parts.length - 1; i++) {
				selectCatalogId += " and dir like '%" + parts[i] + "%' ";
			}
		}
		System.out.println(selectCatalogId);
		return selectCatalogId;
	}

	public static void main(String[] args) {
		try {
			InsertCatalogId i = new InsertCatalogId();
			i.run();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConn();
		}

	}

}
