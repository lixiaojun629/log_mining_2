package edu.njust.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.njust.sem.graph.Vertex;
import edu.njust.sem.util.DBUtil;

public class CatalogFreq {
	private final double INFINITY = 2;
	private List<Integer> dirs = new ArrayList<>();
	private List<Integer> vertexIds = new ArrayList<>();
	private Connection conn;

	/**
	 * ���캯������ʼ�����㼯��vertexIds���Լ�Ŀ¼���м���dirs����ͬ·����Ŀ¼������-100�ָ���
	 * @param path
	 * @throws SQLException
	 */
	public CatalogFreq(Integer[] path) throws SQLException {
		String sql = "SELECT catalog_id FROM tab_catalogs where session_id = ?";
		conn = DBUtil.getConn();
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = null;
		HashSet<Integer> dirSet = new HashSet<>();
		
		for (Integer a : path) {
			ps.setInt(1, a);
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("catalog_id");
				dirSet.add(id);
				dirs.add(id);
			}
			dirs.add(-100);// ÿ��·����������һ���ָ�����-100��
		}

		for (Integer a : dirSet) {
			vertexIds.add(a);
		}
		dirSet = null;
	}

	/**
	 * @return ���ض���Vertex����ļ���
	 * @throws SQLException
	 */
	public Vertex[] getVertexs() throws SQLException {
		Vertex[] vertexs = new Vertex[vertexIds.size()];
		String sql = "SELECT dir FROM tab_dir where id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = null;
		for (int i = 0; i <vertexIds.size(); i++) {
			int id = vertexIds.get(i);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				vertexs[i]=new Vertex(id, rs.getString("dir"));
			} else {
				throw new RuntimeException("Ŀ¼��ţ�" + id + "������");
			}
		}
		return vertexs;
	}

	/**
	 * @param path
	 * @return ����Ŀ¼Ƶ�ξ���
	 */
	public double[][] getCatalogFreqMatrix() {
		int length = vertexIds.size();
		double[][] matrix = new double[length][length];
		int start = 0;
		int end = 1;
		while (end < dirs.size()) {
			int startValue = dirs.get(start);
			int endValue = dirs.get(end);

			int startVertIndex = vertexIds.indexOf(startValue);
			int endVertIndex = vertexIds.indexOf(endValue);
			if (startVertIndex > -1 && endVertIndex > -1) {
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
}
