package edu.njust.sem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import edu.njust.sem.util.DBUtil;

public class GetCatalog {
	private static Document doc;

	public static String getHtmlByUrl(String url) throws Exception {
		String html = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();// 创建httpClient对象

		HttpGet httpGet = new HttpGet(url);// 以get方式请求该URL
		httpGet.setHeader("User-Agent",
				"Mozilla/5.0 (compatible; JikeSpider; +http://shoulu.jike.com/spider.html)");
		try {
			HttpResponse responce = httpClient.execute(httpGet);// 得到responce对象
			int resStatu = responce.getStatusLine().getStatusCode();// 返回码
			if (resStatu == HttpStatus.SC_OK) {// 200正常 其他就不对
				// 获得相应实体
				HttpEntity entity = responce.getEntity();
				if (entity != null) {
					html = EntityUtils.toString(entity);// 获得html源代码
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		// System.out.println(html);
		doc = Jsoup.parse(html);
		Elements es = doc.select("title");
		Element e = es.first();
		if (e.ownText().contains("Access Denied")) {
			System.out.println("已被拉入黑名单");
			// ConnectUtil.reConnAdsl();
			System.out.println(html);
			// html = getHtmlByUrl(url);
			throw new Exception("已被拉入黑名单，停止抓取");
		}
		return html;
	}

	public static String getDirFromHtml(String html) throws Exception {
		String dir = "";
		Elements elements;
		Element elementDiv;
		Element elementLastA;

		Node nodeDir;
		try {
			elements = doc.select("div.crumb");
			if (elements.size() > 1) {
				System.out.println("目录提取异常，网页中存在多个目录");
			}
			elementDiv = elements.first();
			elements = elementDiv.select("a[href]");
			elementLastA = elements.last();
			nodeDir = elementLastA.nextSibling();
			dir = nodeDir.outerHtml();
		} catch (Exception e) {
			nodeDir = null;
			elementLastA = null;
			elementDiv = null;
			elements = null;
			doc = null;
			throw new Exception(e);
		}

		dir = dir.replaceAll("&raquo;", "");
		dir = dir.replaceAll("&amp;", "&");
		dir = dir.replaceAll("Product List", "");
		dir = dir.replaceAll("&quot;", "\"");
		dir = dir.trim();
		return dir;
	}

	private static Logger logger = Logger.getLogger(GetCatalog.class.getName());

	public static void main(String[] args) {
		String sql = "SELECT url_id, url,catalog from tab_site where url_id > 5135";
		Connection conn = DBUtil.getConn();
		Statement stmt;
		ResultSet rs;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String url = rs.getString("url");
				String html = null;
				String catalog = null;
				try {
					html = getHtmlByUrl(url);
					System.out.println(html);
					catalog = getDirFromHtml(html);
				} catch (Exception e) {
					logger.error(url);
					e.printStackTrace();
					continue;
				}
				rs.updateString("catalog", catalog);
				rs.updateRow();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
