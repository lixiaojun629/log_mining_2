package edu.njust.sem;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.njust.sem.graph.Graph;
import edu.njust.sem.graph.Tree;
import edu.njust.sem.graph.Vertex;
import edu.njust.sem.util.PrintUtil;

public class RunCaculator1 {
	private static Cluster cluster = new Cluster();

	/**
	 * �õ�һ��Ŀ¼·��ת��ΪĿ¼Ƶ�ξ���
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void run() throws SQLException, IOException {
		File fileCatalog = new File("D:\\test\\catalog_num.txt");
		PrintUtil.setOut(fileCatalog);
		double[][] simiMatrix = getMatrixFormFile();
		List<double[][]> matrixs = cluster.getMarixList(simiMatrix);
		System.out.println("�������:" + matrixs.size());
		List<Integer> path = new ArrayList<Integer>();
		int n = 0;// ��¼��������
		for (double[][] m : matrixs) {
			if (m.length >= 2) {
				System.out.println("���ƶ��Ӿ���------------------------");
				for (double[] row : m) {
					// ��������һ��Ԫ���Ǹ���Ԫ����ԭʼ�����е��к�
					path.add((int) row[row.length - 1]);
				}
				n++;
				try {
					PrintUtil.println("���" + n);
					Tree tree = makeTree(path.toArray(new Integer[0]));
					path.clear();
				} catch (Exception e) {
					path.clear();
					PrintUtil.println("������漰Ŀ¼���ܹ�����С������������");
					e.printStackTrace();
				}
			}
		}
		System.out.println("���������" + n);
		PrintUtil.close();
	}

	/**
	 * ������С������
	 * 
	 * @param path
	 * @return ��С�������ߵļ���
	 * @throws SQLException
	 * @throws IOException
	 */
	private static Tree makeTree(Integer[] path) throws SQLException,IOException {
		CatalogFreq catalogFreq = new CatalogFreq(path);
		double[][] catalogFreqMatrix = catalogFreq.getCatalogFreqMatrix();
		Vertex[] vertexs = catalogFreq.getVertexs();
		Graph graph = new Graph(catalogFreqMatrix, vertexs);
		Tree tree = graph.createMinSpanningTree();
		return tree;
	}

	/**
	 * ��ȡ�������ļ��е��Ѿ������б任�����ƶȾ��󣬲��ش��¼���
	 * 
	 * @return
	 * @throws IOException
	 */
	public static double[][] getMatrixFormFile() throws IOException {
		Scanner scan = new Scanner(new File("D:\\matrix.txt"));
		scan.useDelimiter("\r\n");
		double[][] matrix = null;
		boolean flag = false;
		String[] nums = null;
		int row = 0;
		while (scan.hasNext()) {
			String line = scan.next();
			if (flag) {
				nums = line.split("\\s+");
				if (matrix == null) {
					matrix = new double[nums.length - 1][nums.length];
				}
				for (int i = 0, length = nums.length; i < length; i++) {
					matrix[row][i] = Double.parseDouble(nums[i]);
				}
				++row;
			}
			if (line.contains("���б任��������ƶȾ���")) {
				flag = true;
			}
		}
		scan.close();
		return matrix;
	}
}
