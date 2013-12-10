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
import edu.njust.sem.util.DBUtil;
import edu.njust.sem.util.PrintUtil;

public class Main {
	private Cluster cluster = new Cluster();
	private Similarity simi = new Similarity();

	public static void main(String[] args) {
		Main r = new Main();
		try {
			r.run2();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConn();
		}
	}

	/**
	 * 先把数据聚类，再执行pathfinder算法
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public void run() throws SQLException, IOException {
		double[][] simiMatrix = getMatrixFormFile();
		List<double[][]> matrixs = cluster.getMarixList(simiMatrix);
		List<Integer> path = new ArrayList<Integer>();
		int n = 0;// 记录分类总数
		for (double[][] m : matrixs) {
			if (m.length >= 2) {
				System.out.println("相似度子矩阵------------------------");
				for (double[] row : m) {
					// 矩阵的最后一列元素是该行元素在原始矩阵中的行号
					path.add((int) row[row.length - 1]);
				}
				n++;
				try {
					List<Tree> trees = makeTree(path.toArray(new Integer[0]));
					for (int j = 0; j < trees.size(); j++) {
						save(trees.get(j), new File("d:\\matrixs\\" + n + '_'
								+ j + ".txt"));
					}
					path.clear();
				} catch (Exception e) {
					path.clear();
					e.printStackTrace();
				}
			}
		}
		System.out.println("类别总数：" + n);
	}

	/**
	 * 不进行聚类，直执行pathfinder算法
	 * 
	 * @throws IOException
	 */
	public void run2() throws IOException {
		double[][] simiMatrix = getMatrixFormFile();
		List<Integer> path = new ArrayList<Integer>();
		for (double[] row : simiMatrix) {
			// 矩阵的最后一列元素是该行元素在原始矩阵中的行号
			path.add((int) row[row.length - 1]);
		}
		try {
			List<Tree> trees = makeTree(path.toArray(new Integer[0]));
			Tree tree = trees.get(0);
			save(tree, new File("d:\\tree.txt"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把树保存在文件中
	 * 
	 * @param tree
	 * @param file
	 * @throws IOException
	 */
	private void save(Tree tree, File file) throws IOException {
		PrintUtil.setOut(file);
		List<Vertex> vs = tree.getVertexs();
		int[][] sm = tree.getSiteMatrix();
		int[][] um = tree.getUserMatrix();
		PrintUtil.println("目录：");
		for (Vertex v : vs) {
			PrintUtil.println(v.dirId + "__" + tree.getVertexGTDCC(v));
		}
		PrintUtil.println("");
		PrintUtil.println("网站----------------------------------");
		PrintUtil.printMatrixToFile(sm);
		PrintUtil.println("用户----------------------------------");
		PrintUtil.printMatrixToFile(um);
		PrintUtil.println("全局相似度：" + tree.getGTDCC());
		if (tree.getGTDCC() < 0) {
			System.out.println(file);
		}
		PrintUtil.close();
	}

	/**
	 * 构造最小生成树，pathfinder
	 * 
	 * @param path
	 * @return 最小生成树边的集合
	 * @throws SQLException
	 * @throws IOException
	 */
	private List<Tree> makeTree(Integer[] path) throws SQLException,
			IOException {
		CatalogFreq catalogFreq = new CatalogFreq(path);
		double[][] catalogFreqMatrix = catalogFreq.getCatalogFreqMatrix();
		Vertex[] vertexs = catalogFreq.getVertexs();
		Graph graph = new Graph(catalogFreqMatrix, vertexs);
		List<Tree> trees = graph.createMinSpanningTree();
		return trees;
	}

	/**
	 * 计算出相似度矩阵，并把矩阵进行行列变换，分别把这两个矩阵存放在"D:\\matrix1.txt","D:\\matrix2.txt"这两个文件中
	 * 
	 * @throws IOException
	 */
	public void getSimiMatrix() throws IOException {
		File fileMatrix1 = new File("D:\\matrix1.txt");
		File fileMatrix2 = new File("D:\\matrix2.txt");

		double[][] simiMatrix = simi.getSimilarityMatrix();
		PrintUtil.setOut(fileMatrix1);
		PrintUtil.printMatrixToFile(simiMatrix);
		PrintUtil.close();
		simiMatrix = cluster.transformMatrix(simiMatrix);
		PrintUtil.setOut(fileMatrix2);
		PrintUtil.printMatrixToFile(simiMatrix);
		PrintUtil.close();
	}

	/**
	 * 读取保存在文件中的已经过行列变换的相似度矩阵，不必从新计算
	 * 
	 * @return
	 * @throws IOException
	 */
	public double[][] getMatrixFormFile() throws IOException {
		Scanner scan = new Scanner(new File("D:\\matrix2.txt"));
		scan.useDelimiter("\r\n");
		double[][] matrix = null;
		String[] nums = null;
		int row = 0;
		while (scan.hasNext()) {
			String line = scan.next();
			nums = line.split("\\s+");
			if (matrix == null) {
				matrix = new double[nums.length - 1][nums.length];
			}
			for (int i = 0, length = nums.length; i < length; i++) {
				matrix[row][i] = Double.parseDouble(nums[i]);
			}
			++row;
		}
		scan.close();
		return matrix;
	}
}
