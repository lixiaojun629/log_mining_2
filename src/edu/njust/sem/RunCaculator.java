package edu.njust.sem;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.njust.sem.graph.Graph;
import edu.njust.sem.graph.Tree;
import edu.njust.sem.graph.Vertex;
import edu.njust.sem.util.PrintUtil;

public class RunCaculator {
	private static Similarity simi = new Similarity();
	private static Cluster cluster = new Cluster();
	private static CatalogFreq catalogFreq;

	private static Tree makeTree(Integer[] path) throws SQLException {
		catalogFreq = new CatalogFreq(path);
		double[][] catalogFreqMatrix = catalogFreq.getCatalogFreqMatrix();
		System.out.println("目录频次倒数矩阵----------------------------");
		PrintUtil.printMatrix(catalogFreqMatrix);
		Vertex[] vertexs = catalogFreq.getVertexs();
		Graph graph = new Graph(catalogFreqMatrix, vertexs);
		Tree tree = graph.createMinSpanningTree();
		return tree;
	}

	/**
	 * 得到一组目录路径转换为目录频次矩阵
	 * 
	 * @throws SQLException
	 * @throws IOException 
	 */

	public static void main(String[] args) throws SQLException, IOException {
		File fileMatrix = new File("D:\\matrix.txt");
		File fileCatalog = new File("D:\\catalog.txt");
		if(!fileCatalog.exists()){
			fileCatalog.createNewFile();
		}
		if(!fileMatrix.exists()){
			fileMatrix.createNewFile();
		}
		PrintUtil.setOut(fileMatrix);
		double[][] simiMatrix = simi.getSimilarityMatrix();
		System.out.println("原始形似度矩阵------------------------------");
		PrintUtil.printMatrix(simiMatrix);
		simiMatrix = cluster.transformMatrix(simiMatrix);
		System.out.println("行列变换过后的相似度矩阵---------------------");
		PrintUtil.printMatrix(simiMatrix);
		List<double[][]> matrixs = cluster.getMarixList(simiMatrix);
		List<Integer> path = new ArrayList<Integer>();
		int n = 0;
		PrintUtil.setOut(fileCatalog);
		for (double[][] m : matrixs) {
			if (m.length >= 2) {
				System.out.println("相似度子矩阵------------------------");
				PrintUtil.printMatrix(m);
				for (double[] row : m) {
					// 矩阵的最后一列元素是该行元素在原始矩阵中的行号
					path.add((int) row[row.length - 1]);
				}
				n++;
				makeTree(path.toArray(new Integer[0]));
			}
		}
		System.out.println("类别总数：" + n);
	}
}
