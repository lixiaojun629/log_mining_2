package edu.njust.sem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

import edu.njust.sem.util.DBUtil;
import edu.njust.sem.util.PrintUtil;

public class CreateDirMatrix {
	private int classCount;
	private String selectPaths = "SELECT user_id,path_id FROM tab_dir_path_classes where class_id = ?";
	private String selectCatalogs = "SELECT catalog_id FROM tab_catalogs3 where session_id = ?";
	private Connection conn = DBUtil.getConn();
	private PreparedStatement psPaths = conn.prepareStatement(selectPaths);
	private PreparedStatement psCatalogs = conn
			.prepareStatement(selectCatalogs);
	private ResultSet rsPaths;
	private ResultSet rsCatalogs;
	private int[][][] matrixs = new int[4][][];

	public CreateDirMatrix() throws SQLException {
		String selectClassCount = "SELECT class_id FROM tab_dir_path_classes order by 1 desc limit 1";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(selectClassCount);
		rs.next();
		classCount = rs.getInt(1);
	}
	public static void main(String[] args) {
		try {
			PrintStream pw = new PrintStream(new File("d:/matrixs.txt"));
			System.setOut(pw);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			CreateDirMatrix c = new CreateDirMatrix();
			c.run();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtil.closeConn();
		}
	}
	public void run() throws SQLException {
		int[][] matrix = null;
		for (int i = 0; i < classCount; i++) {
			psPaths.setInt(1, i + 1);
			rsPaths = psPaths.executeQuery();
			Integer[] x = getCatalogIds(rsPaths);
			matrix = new int[x.length + 1][x.length + 1];
			//初始化矩阵的第一行和第一列为各个叶子目录的编号
			for (int k = 0; k <= x.length - 1; k++) {
				matrix[0][k + 1] = x[k];
				matrix[k + 1][0] = x[k];
			}
			complMatrix(matrix,rsPaths);
			
			putInMatrixs(matrix);
			System.out.println("类别： "+(i+1)+"---------------------------------");
			PrintUtil.printMatrix(matrix);
		}
//		for(int[][] m : matrixs){
//			PrintUtil.printMatrix(m);
//		}
	}
	/**
	 * 只保存4个最大的共现矩阵
	 * @param matrix
	 */
	private void putInMatrixs(int[][] matrix){
		for(int i = 0; i < matrixs.length; i++){
			if(matrixs[i] == null){
				matrixs[i] = matrix;
				break;
			}else{
				if(matrixs[i].length < matrix.length){
					matrixs[i] = matrix;
					break;
				}
			}
		}
	}
	/**
	 * 根据rsPaths中的数据，填充共现矩阵
	 * @param matrix
	 * @param rsPaths
	 * @throws SQLException
	 */
	private void complMatrix(int[][] matrix, ResultSet rsPaths)throws SQLException {
		while (rsPaths.next()) {
			ArrayList<Integer> catalogs = new ArrayList<Integer>();
			int pathId = rsPaths.getInt("path_id");
			psCatalogs.setInt(1, pathId);
			rsCatalogs = psCatalogs.executeQuery();
			while (rsCatalogs.next()) {
				int catalogId = rsCatalogs.getInt("catalog_id");
				for (int a : catalogs) {
					if (catalogId != a) {
						addToMatrix(matrix,catalogId,a);
					}
				}
				catalogs.add(catalogId);
			}
			catalogs = null;
		}
		rsPaths.beforeFirst();
		rsCatalogs = null;
	}
	/**
	 * 
	 * @param matrix
	 * @param a
	 * @param b
	 */
	private void addToMatrix(int[][] matrix, int a, int b) {
		int i = 0;
		int j = 0;
		for (int k = 1; k < matrix.length; k++) {
			if (a == matrix[0][k]) {
				i = k;
			}
			if (b == matrix[0][k]) {
				j = k;
			}
		}
		if (i != 0 && j != 0) {
			matrix[i][j]++;
			matrix[j][i]++;
		}else{
			throw new RuntimeException("目录编号"+i+"或"+j+"不在矩阵中");
		}
	}
	/**
	 * 
	 * @param rsPaths
	 * @return
	 * @throws SQLException
	 */
	private Integer[] getCatalogIds(ResultSet rsPaths) throws SQLException {
		HashSet<Integer> dirSet = new HashSet<>();
		int pathId = 0;
		while (rsPaths.next()) {
			pathId = rsPaths.getInt("path_id");
			psCatalogs.setInt(1, pathId);
			rsCatalogs = psCatalogs.executeQuery();
			while (rsCatalogs.next()) {
				int catalogId = rsCatalogs.getInt("catalog_id");
				dirSet.add(catalogId);
			}
		}
		rsCatalogs = null;
		rsPaths.beforeFirst();
		return dirSet.toArray(new Integer[0]);
	}
}
