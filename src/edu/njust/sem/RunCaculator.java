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
		System.out.println("Ŀ¼Ƶ�ε�������----------------------------");
		PrintUtil.printMatrix(catalogFreqMatrix);
		Vertex[] vertexs = catalogFreq.getVertexs();
		Graph graph = new Graph(catalogFreqMatrix, vertexs);
		Tree tree = graph.createMinSpanningTree();
		return tree;
	}

	/**
	 * �õ�һ��Ŀ¼·��ת��ΪĿ¼Ƶ�ξ���
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
		System.out.println("ԭʼ���ƶȾ���------------------------------");
		PrintUtil.printMatrix(simiMatrix);
		simiMatrix = cluster.transformMatrix(simiMatrix);
		System.out.println("���б任��������ƶȾ���---------------------");
		PrintUtil.printMatrix(simiMatrix);
		List<double[][]> matrixs = cluster.getMarixList(simiMatrix);
		List<Integer> path = new ArrayList<Integer>();
		int n = 0;
		PrintUtil.setOut(fileCatalog);
		for (double[][] m : matrixs) {
			if (m.length >= 2) {
				System.out.println("���ƶ��Ӿ���------------------------");
				PrintUtil.printMatrix(m);
				for (double[] row : m) {
					// ��������һ��Ԫ���Ǹ���Ԫ����ԭʼ�����е��к�
					path.add((int) row[row.length - 1]);
				}
				n++;
				makeTree(path.toArray(new Integer[0]));
			}
		}
		System.out.println("���������" + n);
	}
}
