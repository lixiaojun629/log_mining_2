package edu.njust.sem;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import edu.njust.sem.util.DBUtil;

public class CreateTabSession {
	Connection conn = DBUtil.getConn();
	String query = "select user_id,resource,referer,ip,visit_time from tab_first_no order by user_id,visit_time";
	String insert = "insert into tab_session(session_id,url_id,user_id,ip,visit_time)values(?,?,?,?,?)";
	int count = 0;
	int userPre = 0;
	int userNow = 0;
	int sessionID = 1;
	Timestamp datePre = null;
	Timestamp dateNow = null;
	ArrayList<Integer> sessionUrls = new ArrayList<>();
	ArrayList<String> ips = new ArrayList<>();
	ArrayList<Timestamp> times = new ArrayList<>();
	PreparedStatement ps = null;
	ResultSet rs = null;
	Statement stmt = null;

	/**
	 * �ڱ�tab_first_no�в�ѯ��ͬһ���û�(user�����ͬ)�ļ�¼�����Ӽ�¼������������referer��url�ֶ�,
	 * �������referer1,url1,referer2,url2,referer3,url3......����վ���У�
	 * �ڸ������У��Ȱ�referer1���뵽�����У�Ȼ�����ΰѺ����Ԫ�ذ����¹��������У�
	 * ���������Ԫ������е����һ��Ԫ����ͬ���������ô�����Ԫ�أ�ѡȡ��һ��������Ԫ����ͬ���Ĵ��� �����������Ժ�Ϳ��Եõ�һ�������ĻỰ��
	 */
	public void run() {
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			ps = conn.prepareStatement(insert);
			rs.first();

			userPre = userNow = rs.getInt("user_id");
			datePre = dateNow = rs.getTimestamp("visit_time");

			do {
				userPre = userNow;
				userNow = rs.getInt("user_id");

				datePre = dateNow;
				dateNow = rs.getTimestamp("visit_time");
				System.out.println(dateNow.getTime() - datePre.getTime());
				// ������ڵ�������¼�е�user����ͬ������ʱ�䳬��30���ӣ�������Ϊͬһ���Ự�����Ѿ�ʶ��ĻỰ·�����뵽���ݿ���
				if (userPre != userNow
						|| (dateNow.getTime() - datePre.getTime())
								/ (1000 * 60) >= 30) {
					insertToDB();//
					System.out.println("new session--------------------------");
					add(rs.getInt("referer"), rs.getString("ip"),rs.getTimestamp("visit_time"));
					add(rs.getInt("resource"), rs.getString("ip"),rs.getTimestamp("visit_time"));
				} else {
					add(rs.getInt("referer"), rs.getString("ip"),rs.getTimestamp("visit_time"));
					add(rs.getInt("resource"), rs.getString("ip"),rs.getTimestamp("visit_time"));
				}
			} while (rs.next());
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void insertToDB() throws SQLException {
		if (sessionUrls.size() > 1) {
			if (sessionUrls.size() != ips.size()
					|| sessionUrls.size() != times.size()) {
				throw new RuntimeException("·����ip�ֶλ�ʱ���ֶζ�����Ϊ�գ�");
			}
			for (int i = 0; i < sessionUrls.size(); i++, count++) {
				ps.setInt(1, sessionID);
				ps.setInt(2, sessionUrls.get(i));
				ps.setInt(3, userPre);
				ps.setString(4, ips.get(i));
				ps.setTimestamp(5, times.get(i));
				ps.executeUpdate();
			}
			sessionID++;
		}
		sessionUrls.clear();
		ips.clear();
		times.clear();
	}

	private void add(int urlId, String ip, Timestamp timestamp) {
		if (urlId > 0
				&& (sessionUrls.isEmpty() || urlId != sessionUrls
						.get(sessionUrls.size() - 1))) {
			if (ip == null || timestamp == null || ip.length() == 0) {
				throw new RuntimeException("·����ip�ֶλ�ʱ���ֶζ�����Ϊ�գ�");
			}
			sessionUrls.add(urlId);
			ips.add(ip);
			times.add(timestamp);
		}
	}

	public static void main(String[] args) {
		CreateTabSession c = new CreateTabSession();
		try {
			c.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConn();
		}

	}
}
