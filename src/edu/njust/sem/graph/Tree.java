package edu.njust.sem.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个类别最小生成树
 * 
 * @author lxj
 * 
 */
public class Tree {
	// 顶点集合
	private List<Vertex> vertexs = new ArrayList<>();
	// 邻接表
	private Map<Vertex, List<Vertex>> adjTable = new HashMap<>();
	// 表示任意两个点之间的距离的矩阵
	private int[][] userMatrix;
	private int[][] siteMatrix;

	public List<Vertex> getVertexs() {
		return vertexs;
	}

	/**
	 * 添加一条边，并对邻接表做相应修改
	 * 
	 * @param src
	 * @param dest
	 */
	public void addEdge(Vertex src, Vertex dest) {
		// System.out.println(src.dirId + "->" + dest.dirId);
		addToAdjTable(src, dest);
		addToAdjTable(dest, src);
	}

	private void addToAdjTable(Vertex src, Vertex dest) {
		List<Vertex> adjVs = adjTable.get(src);
		if (adjVs == null) {
			List<Vertex> adja = new ArrayList<>();
			adja.add(dest);
			adjTable.put(src, adja);
			vertexs.add(src);
		} else {
			adjVs.add(dest);
		}
	}

	/**
	 * 深度优先遍历
	 * 
	 * @param start
	 *            本次遍历的起点
	 * @param curr
	 *            正在访问第点
	 * @param distance
	 *            起点与正在访问的点之间的距离
	 */
	public void depthFirstSearch(Vertex start, Vertex curr, int distance) {
		// 标识该点已访问过
		curr.flag = true;
		int s = vertexs.indexOf(start);
		int c = vertexs.indexOf(curr);
		userMatrix[s][c] = distance;
		++distance;
		List<Vertex> adjVertexs = adjTable.get(curr);
		for (Vertex dest : adjVertexs) {
			// 如果该点未被访问过
			if (!dest.flag) {
				depthFirstSearch(start, dest, distance);
			}
		}
		--distance;
	}

	/**
	 * 分别以图中的每个点为起点遍历图
	 * 
	 * @return 存储图中任意两点之间距离的矩阵
	 */
	public int[][] getUserMatrix() {
		int length = vertexs.size();
		userMatrix = new int[length][length];
		for (int i = 0; i < vertexs.size(); i++) {
			depthFirstSearch(vertexs.get(i), vertexs.get(i), 0);
			// 每次遍历完以后把各个顶点的标志位还原
			for (Vertex v : vertexs) {
				v.flag = false;
			}
		}
		return userMatrix;
	}

	/**
	 * 根据目录在网站上原有的结构，得到存储任意两个目录节点之间的距离的矩阵
	 * 
	 * @return
	 */
	public int[][] getSiteMatrix() {
		int length = vertexs.size();
		siteMatrix = new int[length][length];
		for (int i = 0; i < length; i++) {
			for (int j = i + 1; j < length; j++) {
				int sameDegree = getSameDegree(vertexs.get(i).dirId,
						vertexs.get(j).dirId);
				siteMatrix[i][j] = vertexs.get(i).degree
						+ vertexs.get(j).degree - sameDegree * 2;
			}
		}
		return siteMatrix;
	}

	/**
	 * 获取两个目录共同的最低等级的上层目录的等级
	 * 
	 * @param a
	 *            目录1的dirId
	 * @param b
	 *            目录2的dirId
	 * @return 最低共同上级目录的等级
	 */
	private int getSameDegree(int a, int b) {
		int sameDegree = 3;
		while (a != b) {
			a /= 100;
			b /= 100;
			--sameDegree;
		}
		return sameDegree;
	}

	/**
	 * 计算全局相关系数
	 * 
	 * @return
	 */
	public double getGTDCC() {
		double[] vectorA = toArray(userMatrix);
		double[] vectorB = toArray(siteMatrix);
		return getCorrelationIndex(vectorA, vectorB);
	}
	/**
	 * 获取两个向量之间的相关系数
	 * @param vectorA
	 * @param vectorB
	 * @return
	 */
	private double getCorrelationIndex(double[] vectorA, double[] vectorB) {
		minusAvg(vectorA);
		minusAvg(vectorB);
		double a = 0;
		double m1 = 0;
		double m2 = 0;
		for (int i = 0, l = vectorA.length; i < l; i++) {
			a += vectorA[i] * vectorB[i];
			m1 += vectorA[i] * vectorA[i];
			m2 += vectorB[i] * vectorB[i];
		}
		if (m1 == 0 || m2 == 0) {
			return 0;
		}
		double m = Math.sqrt(m1 * m2);
		return a / m;
	}
	/**
	 * 获取单个节点的相关系数
	 * @param v
	 * @return
	 */
	public double getVertexGTDCC(Vertex v){
		int length = vertexs.size() - 1;
		int index = vertexs.indexOf(v);
		if(index == -1){
			throw new RuntimeException(v.dirId+"_"+v.title+"该节点不在图中");
		}
		double [] vectorUser = new double[length];
		double [] vectorSite = new double[length];
		
		for(int i = 0 ; i < index; i ++){
			vectorUser[i] = userMatrix[i][index];
		}
		if(index != length){
			for(int i = index; i < length; i++){
				vectorUser[i] = userMatrix[index][i];
			}
		}
		
		for(int i = 0 ; i < index; i ++){
			vectorSite[i] = siteMatrix[i][index];
		}
		if(index != length){
			for(int i = index; i < length; i++){
				vectorSite[i] = siteMatrix[index][i];
			}
		}
		
		return getCorrelationIndex(vectorUser,vectorSite);
	}

	/**
	 * 把矩阵的上三角转为数组
	 * 
	 * @param matrix
	 * @return
	 */
	private double[] toArray(int[][] matrix) {
		int length = vertexs.size();
		double[] array = new double[length * (length - 1) / 2];
		for (int i = 0, a = 0; i < length; i++) {
			for (int j = i + 1; j < length; j++) {
				array[a] = matrix[i][j];
				++a;
			}
		}
		return array;
	}

	/**
	 * 数组的每个元素减去该数组所有元素的平均数
	 * 
	 * @param matrix
	 * @return
	 */
	private double[] minusAvg(double[] array) {
		int avg = 0;
		for (int i = 0, l = array.length; i < l; i++) {
			avg += array[i];
		}
		avg = avg / array.length;
		for (int i = 0, l = array.length; i < l; i++) {
			array[i] -= avg;
		}
		return array;
	}
	
	
}
