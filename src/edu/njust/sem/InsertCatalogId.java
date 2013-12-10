package edu.njust.sem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.njust.sem.util.DBUtil;

public class InsertCatalogId {
	private static Logger logger = Logger.getLogger(InsertCatalogId.class);
	private Connection conn = DBUtil.getConn();
	private String selectUrlInfo = "SELECT url_id,url,catalog ,catalog_id FROM tab_site";
	private Statement stmtUrlInfo = conn.createStatement(
			ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
	private Statement stmtCatalogId = conn.createStatement();

	public InsertCatalogId() throws SQLException {

	}

	public void run() throws SQLException {
		ResultSet rsUrlInfo = stmtUrlInfo.executeQuery(selectUrlInfo);
		ResultSet rsCatalogId = null;
		while (rsUrlInfo.next()) {
			String catalog = rsUrlInfo.getString("catalog");
			String[] parts = null;
			try {
				parts = catalog.split("\\W+");
			} catch (NullPointerException e) {
				e.printStackTrace();
				logger.error("url_id: " + rsUrlInfo.getInt("url_id") + " url: "
						+ rsUrlInfo.getString("url") + " catalog: "
						+ rsUrlInfo.getString("catalog"));
				continue;
			}
			String sql = getSQL(parts);
			rsCatalogId = stmtCatalogId.executeQuery(sql);
			int size = DBUtil.getRowCount(rsCatalogId);
			if (1 == size) {
				rsCatalogId.next();
				int id = rsCatalogId.getInt("id");
				rsUrlInfo.updateInt("catalog_id", id);
				rsUrlInfo.updateRow();
				// System.out.println(rsCatalogId.getString("dir"));
			} else if (size > 1) {
				List<String> catalogs = new ArrayList<>();
				while (rsCatalogId.next()) {
					catalogs.add(rsCatalogId.getString("dir"));
				}
				String siteDir = null;
				String[] siteDirParts = null;
				boolean insert = false;
				for (int i = 0; i < catalogs.size(); i++) {
					siteDir = catalogs.get(i);
					siteDirParts = siteDir.split("\\W+");
					if (siteDirParts.length == parts.length) {
						boolean equal = true;
						for (int k = 0; k < parts.length; k++) {
							if (!parts[k].equals(siteDirParts[k])) {
								equal = false;
							}
						}
						if (equal) {
							int id = getCatalogId(siteDir,rsCatalogId);
							if(id > -1){
								rsUrlInfo.updateInt("catalog_id", id);
								rsUrlInfo.updateRow();
								insert = true;
							}
						}
					} else {
						continue;
					}
				}
				if(!insert){
					logger.error("url_id: " + rsUrlInfo.getInt("url_id") + " url: "
							+ rsUrlInfo.getString("url") + " catalog: "
							+ rsUrlInfo.getString("catalog") + "  sql: " + sql
							+ "----拥有多个目录匹配，却无一个是正确的");
				}

			} else {
				logger.error("url_id: " + rsUrlInfo.getInt("url_id") + " url: "
						+ rsUrlInfo.getString("url") + " catalog: "
						+ rsUrlInfo.getString("catalog") + "  sql: " + sql
						+ "----此目录未被编码");
			}
		}
	}

	private int getCatalogId(String catalog,ResultSet rs) throws SQLException {
		int id = -1;
		rs.beforeFirst();
		while (rs.next()) {
			if(catalog.equals( rs.getString("dir"))){
				id = rs.getInt("id");
			}
		}
		return id;
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
		// System.out.println(selectCatalogId);
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
