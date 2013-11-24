package edu.njust.sem;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.njust.sem.exception.LogFormatException;
import edu.njust.sem.util.DBUtil;

public class LogImport {
	File file = new File("D:\\log\\log\\host1213\\log2.txt");
	private static Logger logger = Logger.getLogger(LogImport.class.getName());
	private Connection conn = DBUtil.getConn();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
	private String sql = "insert into tab_first(ip,visit_time,method,resourse,protocal,status,referer,browser_info,cookie,pid,domain)values(?,str_to_date(?, '%d/%m/%Y:%H:%i:%s'),?,?,?,?,?,?,?,?,?)";
	private PreparedStatement ps;
	private long count = 0;

	public LogImport() {
		try {
			ps = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void importLog() throws FileNotFoundException, SQLException {
		Scanner scan = new Scanner(file);
		scan.useDelimiter("\n");
		conn.setAutoCommit(false);
		while (scan.hasNext()) {
			String aLog = scan.next();
			String[] logEntry = null;
			try {
				logEntry = getLogEntry(aLog);
			} catch (LogFormatException e) {
				logger.error(aLog);
				System.out.println(e.getMessage());
				continue;
			}
			if (this.validateLog(logEntry)) {
				try {
					insertToDB(logEntry);
				} catch (Exception e) {
					System.out.println(aLog);
					conn.rollback();
					e.printStackTrace();
				}
			}
		}
		ps.executeBatch();
		conn.commit();
	}

	private void insertToDB(String[] logEntry) throws SQLException {

		for (int i = 0; i < logEntry.length; i++) {
			ps.setString(i + 1, logEntry[i]);
		}
		ps.addBatch();
		++count;
		if (count % 50000 == 0) {
			System.out.println(count);
			ps.executeBatch();
		}
	}

	@Test
	public void test()  {
		Scanner scan = null;
		
		try {
			scan = new Scanner(new File("D:\\log\\log\\test.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		scan.useDelimiter("\n");
		while(scan.hasNext()){
			try {
				String log = scan.next();
				System.out.println("log:"+log);
				String[] entries = getLogEntry(log);
				for(String str: entries){
					System.out.println(str);
				}
			} catch (LogFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean validateLog(String[] logEntry) {
		// ����������Դurl����Դurl(referer)����������-Catalog����/mic/���ֶΣ���˵��û�з�����Ŀ¼����ҳ�������������־
		if (!(logEntry[3].contains("-Catalog") || logEntry[3].contains("/mic/"))
				&& !(logEntry[6].contains("-Catalog") || logEntry[6]
						.contains("/mic/"))) {
			return false;
		}
		// ֻ����״̬��Ϊ2��3��ͷ����־��¼
		if (!logEntry[5].startsWith("2") && !logEntry[5].startsWith("3")) {
			return false;
		}
		// ����û����ʵĲ���www.���µ���Դ���򲻵��������־
		if (logEntry[10] != null && !logEntry[10].equals("www.")) {
			return false;
		}
		// ������Դ��ϴ
		if (logEntry[3].startsWith("/ajax")
				|| logEntry[3].startsWith("/im.do?")
				|| logEntry[3].indexOf(".css") != -1
				|| logEntry[3].indexOf(".js") != -1
				|| logEntry[3].indexOf(".jpg") != -1
				|| logEntry[3].indexOf(".gif") != -1
				|| logEntry[3].indexOf(".png") != -1
				|| logEntry[3].indexOf(".ico") != -1
				|| logEntry[3].indexOf(".swf") != -1
				|| logEntry[3].indexOf(".rss") != -1
				|| logEntry[3].indexOf(".los") != -1
				|| logEntry[3].indexOf(".txt") != -1) {
			return false;
		}
		return true;
	}

	/**
	 * ��һ����־�г�ȡ�������õ��ֶ�
	 * 
	 * @param log
	 * @return
	 * @throws LogFormatException
	 */
	private String[] getLogEntry(String log) throws LogFormatException {
		String[] logEntry = new String[11];
		String[] arrayLine = log.split("\"\\s+\"|\\s+\"|\"\\s+|\"");

		if (arrayLine.length == 8 || arrayLine.length == 7) {
			String[] arr1 = arrayLine[0].split("\\s+\\[?");
			String[] arr2 = arrayLine[1].split("\\s+");
			String[] arr3 = arrayLine[2].split("\\s+");

			if (arr1.length == 5) {
				logEntry[0] = arr1[0];
				logEntry[1] = arr1[3].replaceFirst("May", "05");
			} else {
				throw new LogFormatException("��־��ʽ�쳣��");
			}

			if (arr2.length == 3) {
				logEntry[2] = arr2[0];
				logEntry[3] = arr2[1];
				logEntry[4] = arr2[2];
			} else {
				for (int i = 0; i < arrayLine.length; i++) {
					System.out.println(arrayLine[i]);
				}
				throw new LogFormatException("��־��ʽ�쳣��");
			}

			if (arr3.length == 2) {
				logEntry[5] = arr3[0];
			} else {
				throw new LogFormatException("��־��ʽ�쳣��");
			}
			//ȷ��referer�ֶβ�����2020���ַ�
			if(arrayLine[3].length() > 2020){
				arrayLine[3]  = arrayLine[3].substring(0, 2019);
			}
			//������ֶβ�����500���ַ�
			if(arrayLine[4].length() > 500){
				arrayLine[4] = arrayLine[4].substring(0, 499);
			}
			//ȥ��pid�ֶε�ǰ�ĸ��ַ���pid=��
			arrayLine[6] = arrayLine[6].substring(4);
			for (int i = 3; i < arrayLine.length; i++) {
				logEntry[i + 3] = arrayLine[i];
			}
		} else {
			for (int i = 0; i < arrayLine.length; i++) {
				System.out.println(arrayLine[i]);
			}
			throw new LogFormatException("��־��ʽ�쳣��");
		}
		return logEntry;
	}

	public static void main(String[] args) {
		LogImport logImport = new LogImport();

		try {
			logImport.importLog();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConn();
		}
	}
}
