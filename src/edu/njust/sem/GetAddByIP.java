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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.njust.sem.util.DBUtil;

public class GetAddByIP {
	private static DefaultHttpClient httpClient = new DefaultHttpClient();// 创建httpClient对象
	private Connection conn = DBUtil.getConn();
	
	public String getAdd(String ip) {
		String resp = null;
		String url = "http://www.ip138.com/ips138.asp?action=2&ip=";
		url += ip;
		HttpGet httpget = new HttpGet(url);// 以get方式请求该URL
		httpget.setHeader("User-Agent",
				"Mozilla/5.0 (compatible; JikeSpider; +http://shoulu.jike.com/spider.html)");
		httpget.setHeader("Set-Cookie",
				"ASPSESSIONIDASARSDCT=HOMONJLDPILOCJOOPAANFGKK");
		try {
			HttpResponse responce = httpClient.execute(httpget);// 得到responce对象
			int resStatu = responce.getStatusLine().getStatusCode();// 返回码
			if (resStatu == HttpStatus.SC_OK) {// 200正常 其他就不对
				// 获得相应实体
				HttpEntity entity = responce.getEntity();
				if (entity != null) {
					byte[] bytes = EntityUtils.toString(entity).getBytes("8859_1");//
					resp = new String(bytes, "gbk");
					resp = getContentFormHtml(resp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public String getContentFormHtml(String html) {
		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("ul.ul1");
		Element e = elements.first().child(0);
		return e.ownText().split("：")[1];
	}
	public void run() throws SQLException{
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery("SELECT user_id,ip_browser_info,address FROM tab_user where address is null");
		String address = null;
		String ip = null;
		while(rs.next()){
			ip = rs.getString("ip_browser_info");
			if(ip!=null && ip.trim().length() > 0){
				ip = ip.split("#")[0];
				address = getAdd(ip);
				System.out.println(address);
				rs.updateString("address", address);
				rs.updateRow();
			}
		}
	}
	public static void main(String[] args) {
		GetAddByIP g = new GetAddByIP();
		try {
			g.run();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtil.closeConn();
			httpClient.getConnectionManager().shutdown();
		}
	}
}
