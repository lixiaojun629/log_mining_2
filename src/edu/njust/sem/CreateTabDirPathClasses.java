package edu.njust.sem;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import edu.njust.sem.util.DBUtil;

/**
 * 从文件d:\catalog.txt中抽取聚类后各个类别所包含的目录路径编号，并插入数据库表tab_dir_path_classes中
 * @author lxj
 */
public class CreateTabDirPathClasses {
	private File file = new File("d:\\catalog.txt");
	private Connection conn = DBUtil.getConn();
	private String insertPathClasses = "insert into tab_dir_path_classes (class_id,path_id)values (?,?)";
	private PreparedStatement ps = conn.prepareStatement(insertPathClasses);

	public CreateTabDirPathClasses() throws SQLException {

	}

	public void run() throws IOException, RuntimeException, SQLException {
		Scanner scan = new Scanner(file);
		scan.useDelimiter("\r\n");
		String line = null;
		String pathId = null;
		boolean flag = false;
		int classId = 0;
		while (scan.hasNext()) {
			line = scan.next();
			if (line.indexOf("目录频次倒数矩阵") > -1) {
				flag = false;
			}
			if (flag) {
				String arr[] = line.split(" ");
				pathId = arr[arr.length - 1].split("\\.")[0];
				insert(classId,Integer.parseInt(pathId));
			}

			if (line.indexOf("相似度子矩阵") > -1) {
				flag = true;
				++classId;
			}
		}
		scan.close();
	}

	public void insert(int classId, int pathId) throws SQLException {
		ps.setInt(1, classId);
		ps.setInt(2, pathId);
		ps.executeUpdate();
	}
	/**
	 * 向表中插入用户编号
	 * @throws SQLException 
	 */
	public void insertUser() throws SQLException{
		String select = "select id,user_id,path_id from tab_dir_path_classes";
		String selectUserId = "SELECT user_id FROM tab_catalogs2 where session_id = ?";
		Statement stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		PreparedStatement ps = conn.prepareStatement(selectUserId);
		ResultSet rs = stmt.executeQuery(select);
		ResultSet rsUser = null;
		while(rs.next()){
			int pathId = rs.getInt("path_id");
			ps.setInt(1, pathId);
			rsUser = ps.executeQuery();
			if(rsUser.next()){
				rs.updateInt("user_id", rsUser.getInt("user_id"));
				rs.updateRow();
			}else{
				throw new RuntimeException("目录路径"+pathId+"没有对应的用户编号");
			}
		}
	}
	public static void main(String[] args){
		try {
			CreateTabDirPathClasses c = new CreateTabDirPathClasses();
			//c.run();
			c.insertUser();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBUtil.closeConn();
		}
	}
}
