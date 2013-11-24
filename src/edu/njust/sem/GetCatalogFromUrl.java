package edu.njust.sem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.njust.sem.util.DBUtil;

public class GetCatalogFromUrl {
	private String sql = "SELECT url_id, url,catalog from tab_site";
	private Connection conn = DBUtil.getConn();

	public void run() throws SQLException {
		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String url = rs.getString("url");
			String catalog = getPartsOfCatalog(url);
			rs.updateString("catalog", catalog);
			rs.updateRow();
		}
	}

	public String getPartsOfCatalog(String url) {
		Matcher m1 = Pattern.compile("-Catalog/.*html").matcher(url);
		if (m1.find()) {
			String catalog = m1.group();
			return catalog.substring(9, catalog.length() - 5);
		} else {
			Matcher m2 = Pattern.compile("/mic/.*html").matcher(url);
			if(m2.find()){
				String catalog = m2.group();
				return catalog.substring(5, catalog.length() - 5);
			}else{
				System.out.println(url);
				return null;
			}
		}

	}

	public static void main(String[] args) throws SQLException {
		GetCatalogFromUrl g = new GetCatalogFromUrl();
		g.run();

	}
}
