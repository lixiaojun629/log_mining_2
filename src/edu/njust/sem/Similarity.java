package edu.njust.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.njust.sem.util.DBUtil;

public class Similarity {
	private Connection conn = DBUtil.getConn();
	private double[][] jz = null;
	private static Logger logger = Logger.getLogger(Similarity.class);
	public Similarity() {

	}

	public double[][] getSimilarityMatrix() {
		String queryDirPath = "select session_id ,catalog_id from tab_catalogs2 where session_id = ?";
		String queryPathCount = "select session_id from tab_catalogs2 order by 1 desc limit 1";
		Statement stmt = null;
		PreparedStatement psDirPath = null;
		ResultSet rsDirPath = null;
		ResultSet rsPathCount = null;
		ArrayList<Integer> alnow = new ArrayList<Integer>();
		ArrayList<Integer> alnext = new ArrayList<Integer>();

		int pathCount = 0;
		double similarity = 0;
		try {
			stmt = conn.createStatement();
			rsPathCount = stmt.executeQuery(queryPathCount);
			rsPathCount.next();
			pathCount = rsPathCount.getInt(1);
			psDirPath = conn.prepareStatement(queryDirPath);
			logger.debug("目录路径条数：" + pathCount);
			jz = new double[pathCount][];
			for (int i = 0; i < pathCount; i++) {
				jz[i] = new double[pathCount + 1];
			}
			for (int i = 0; i < pathCount; i++) {
				psDirPath.setInt(1, i + 1);
				rsDirPath = psDirPath.executeQuery();
				while (rsDirPath.next()) {
					alnow.add(rsDirPath.getInt("catalog_id"));
				}
				logger.debug("正在生成相似度矩阵：" + i + "/" + pathCount);
				for (int j = i + 1; j < pathCount; j++) {
					psDirPath.setInt(1, j + 1);
					rsDirPath = psDirPath.executeQuery();
					while (rsDirPath.next()) {
						alnext.add(rsDirPath.getInt("catalog_id"));
					}
					Integer[] pathNow = alnow.toArray(new Integer[0]);
					Integer[] pathNext = alnext.toArray(new Integer[0]);
					similarity = getSimilarity(pathNow, pathNext);
					if(Double.isNaN(similarity)){
						throw new RuntimeException("矩阵第"+i+"行"+j+"列为NaN");
					}
					jz[i][j] = similarity;
					alnext.clear();
					jz[j][i] = similarity;
				}
				// 最后一列值为其所在行的行号
				jz[i][pathCount] = i + 1;
				jz[i][i] = 1;
				alnow.clear();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jz;
	}

	public Double getSimilarity(Integer[] pathNow, Integer[] pathNext) {
		int innerProudct = getInnerProduct(pathNow, pathNext);
		int length = Math.min(pathNow.length, pathNext.length);
		double innerProduct1 = getInnerProductOfTwoSamePath(pathNow, length);
		double innerProduct2 = getInnerProductOfTwoSamePath(pathNext, length);
		return innerProudct / Math.sqrt(innerProduct1 * innerProduct2);
	}

	public int getInnerProduct(Integer[] pathNow, Integer[] pathNext) {
		int length = Math.min(pathNow.length, pathNext.length);
		int innerProduct = 0;
		for (int i = 0; i < length; i++) {
			int[][] tiaoPathNow = getTiaoPath(pathNow, i);
			int[][] tiaoPathNext = getTiaoPath(pathNext, i);
			innerProduct += getPartInnerProduct(tiaoPathNow, tiaoPathNext);
			// 如果两条路径的0跳子路径的内积为0，则这两条路径的内积肯定为0,可直接返回0，不必继续运算
			if (innerProduct == 0) {
				return 0;
			}
		}
		return innerProduct;
	}

	public int getPartInnerProduct(int[][] tiaoPathNow, int[][] tiaoPathNext) {
		String[] pathNow = new String[tiaoPathNow.length];
		String[] pathNext = new String[tiaoPathNext.length];
		int r = tiaoPathNow[0].length;
		int partInnerProduct = 0;
		for (int i = 0; i < pathNow.length; i++) {
			pathNow[i] = getStringContent(tiaoPathNow[i]);
		}
		for (int i = 0; i < pathNext.length; i++) {
			pathNext[i] = getStringContent(tiaoPathNext[i]);
		}
		for (int i = 0; i < pathNow.length; i++) {
			for (int j = 0; j < pathNext.length; j++) {
				if (pathNow[i].equals(pathNext[j])) {
					partInnerProduct += r * r;
				}
			}
		}
		return partInnerProduct;
	}

	public String getStringContent(int[] a) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < a.length; i++) {
			sb.append(a[i]);
			sb.append(',');
		}
		return sb.toString();
	}

	public int[][] getTiaoPath(Integer[] path, int r) {
		if (r >= path.length) {
			return null;
		} else {
			int tiaoPathCount = path.length - r;// r跳路径的个数
			int[][] tiaoPath = new int[tiaoPathCount][r + 1];
			for (int i = 0; i < tiaoPathCount; i++) {
				for (int j = 0; j < r + 1; j++) {
					tiaoPath[i][j] = path[i + j];
				}
			}
			return tiaoPath;
		}
	}

	public int getInnerProductOfTwoSamePath(Integer[] path, int length) {
		int innerProduct = 0;
		for (int i = 0; i < length; i++) {
			innerProduct += (path.length - i) * (i + 1) * (i + 1);
		}
		return innerProduct;
	}

	@Test
	public void test1(){
		logger.debug("目录路径条数：");
	}
	public static void main(String[] args) {
		Similarity sim = new Similarity();
//		 Integer[] a ={60, 62, 64, 66, 68, 70, 71, 73, 75, 77, 79, 81, 83, 85, 87, 8, 90, 92, 586, 587, 589, 591, 594, 383, 597, 146, 304};
//		 Integer[] b = {60, 62, 64, 66, 68, 70, 72, 74, 76, 78, 80, 83, 85, 87, 8, 90, 92, 586, 587, 589, 591, 594, 383, 597, 146, 304, 1307, 1309, 1311};
//		 System.out.println(sim.getInnerProduct(a, b));
//		 System.out.println(sim.getSimilarity(a, b));
		double[][] m = sim.getSimilarityMatrix();
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				System.out.printf("%.6f", m[i][j]);
				System.out.print("  ");
			}
			System.out.println();
		}
	}
}
