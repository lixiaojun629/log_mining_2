package edu.njust.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import edu.njust.sem.util.DBUtil;

public class CatalogFreq {
	private final double INFINITY = 2;
	private List<Integer> dirs = new ArrayList<>();
	private List<Integer> vertexs = new ArrayList<>();
	private Connection conn;
	/**
	 * ���캯������ʼ�����㼯��vertexs���Լ�Ŀ¼���м���dirs����ͬ·����Ŀ¼������-100�ָ���
	 * @param path
	 * @throws SQLException
	 */
	public CatalogFreq(Integer[] path) throws SQLException {
		String sql = "SELECT catalog_id FROM tab_catalogs2 where session_id = ?";
		conn = DBUtil.getConn();
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = null;
		HashSet<Integer> dirSet = new HashSet<>();

		for (Integer a : path) {
			ps.setInt(1, a);
			rs = ps.executeQuery();
			while (rs.next()) {
				dirSet.add(rs.getInt(1));
				dirs.add(rs.getInt(1));
			}
			dirs.add(-100);//ÿ��·����������һ���ָ�����-100��
		}
		 
		for (Integer a : dirSet) {
			vertexs.add(a);
		}
		dirSet = null;
	}
	/**
	 * @return ���ض�������Ƽ���ŵļ���
	 * @throws SQLException
	 */
	public List<String> getVertexs() throws SQLException {
		List<String> vertexsOfDirNames = new ArrayList<>();
		String sql = "SELECT dir FROM tab_dir where id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = null;
		for (int num : vertexs) {
			ps.setInt(1, num);
			rs = ps.executeQuery();
			if(rs.next()){
				vertexsOfDirNames.add(rs.getString("dir")+"#"+num);
			}else{
				throw new RuntimeException("Ŀ¼��ţ�"+num+"������");
			}
		}
		return vertexsOfDirNames;
	}
	/**
	 * ��ȡ����ı������
	 * @return
	 */
	public List<String> getVertexsNum(){
		ArrayList<String> vlist = new ArrayList<>();
		for(Integer a: vertexs){
			vlist.add(a.toString());
		}
		return vlist;
	}
	/**
	 * @param path
	 * @return ����Ŀ¼Ƶ�ξ���
	 */
	public double[][] getCatalogFreqMatrix() {
		int length = vertexs.size();
		double[][] matrix = new double[length][length];
		int start = 0;
		int end = 1;
		while (end < dirs.size()) {
			int startValue = dirs.get(start);
			int endValue = dirs.get(end);

			int startVertIndex = vertexs.indexOf(startValue);
			int endVertIndex = vertexs.indexOf(endValue);
			if(startVertIndex > -1 && endVertIndex > -1){
				matrix[startVertIndex][endVertIndex]++;
				matrix[endVertIndex][startVertIndex]++;
			}
			start++;
			end++;
		}

		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				if (matrix[i][j] != 0) {
					matrix[i][j] = 1 / matrix[i][j];
				} else {
					matrix[i][j] = INFINITY;
				}
			}
		}
		return matrix;
	}

	public void print(double[][] matrix) {
		for (int a = 0; a < matrix.length; a++) {
			for (int j = 0; j < matrix[a].length; j++) {
				System.out.printf("%.2f", matrix[a][j]);
				System.out.print("  ");
			}
			System.out.println();
		}
	}

}
