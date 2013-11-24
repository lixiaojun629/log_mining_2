package edu.njust.sem;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.njust.sem.util.PrintUtil;

public class RunCaculator1 {
	private static Cluster cluster = new Cluster();
	private static CatalogFreq catalogFreq;

	/**
	 * 得到一组目录路径转换为目录频次矩阵
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static void main(String[] args) throws SQLException, IOException {
		File fileCatalog = new File("D:\\test\\catalog_num.txt");
		PrintUtil.setOut(fileCatalog);
		double[][] simiMatrix = getMatrixFormFile();
		List<double[][]> matrixs = cluster.getMarixList(simiMatrix);
		System.out.println("类别总数:"+matrixs.size());
		List<Integer> path = new ArrayList<Integer>();
		List<List> tree = null;
		int n = 0;//记录分类总数
		for (double[][] m : matrixs) {
			if (m.length >= 2) {
				System.out.println("相似度子矩阵------------------------");
				for (double[] row : m) {
					// 矩阵的最后一列元素是该行元素在原始矩阵中的行号
					path.add((int) row[row.length - 1]);
				}
				n++;
				try{
					PrintUtil.println("类别"+n);
					tree = makeTree(path.toArray(new Integer[0]));
					path.clear();
				}catch(Exception e){
					path.clear();
					PrintUtil.println("此类别涉及目录不能构成最小生成树，舍弃");
					e.printStackTrace();
				}
			}
		}
		System.out.println("类别总数：" + n);
		PrintUtil.close();
	}
	/**
	 * 
	 * @param tree
	 */
	private static void makeMatrix(List<List> tree){
		List<String> vertexs = tree.get(0);
		List<EdgeLabel> edges = tree.get(1);
		int[][] matrix = new int[vertexs.size()][];
		for(int i = 0; i < matrix.length; i++){
			
		}
	}
	/**
	 * 构造最小生成树
	 * @param path
	 * @return 最小生成树边的集合
	 * @throws SQLException
	 * @throws IOException 
	 */
	private static List<List> makeTree(Integer[] path) throws SQLException, IOException {
		catalogFreq = new CatalogFreq(path);
		double[][] catalogFreqMatrix = catalogFreq.getCatalogFreqMatrix();
		List<String> verts = catalogFreq.getVertexsNum();
		Graph graph = new Graph(catalogFreqMatrix, verts);
		List<EdgeLabel> edges =  graph.createMinSpanningTree();
		List<List> tree = new ArrayList<>();
		tree.add(verts);
		tree.add(edges);
		for(EdgeLabel e : edges){
			PrintUtil.println(e.toString());
		}
		return tree;
		
	}
	/**
	 * 读取保存在文件中的已经过行列变换的相似度矩阵，不必从新计算
	 * @return
	 * @throws IOException
	 */
	public static double[][] getMatrixFormFile() throws IOException{
		Scanner scan = new Scanner(new File("D:\\matrix.txt"));
		scan.useDelimiter("\r\n");
		double[][] matrix = null;
		boolean flag = false;
		String[] nums = null;
		int row = 0;
		while(scan.hasNext()){
			String line = scan.next();
			if(flag){
				nums = line.split("\\s+");
				if(matrix == null){
					matrix = new double[nums.length-1][nums.length];
				}
				for(int i = 0,length = nums.length; i < length; i++){
					matrix[row][i] = Double.parseDouble(nums[i]);
				}
				++row;
			}
			if(line.contains("行列变换过后的相似度矩阵")){
				flag = true;
			}
		}
		scan.close();
		return matrix;
	}
}
