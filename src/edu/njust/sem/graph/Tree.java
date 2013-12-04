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
	private Vertex[] vertexs;
	// 邻接表
	private Map<Integer, List<Integer>> adjTable = new HashMap<>();
	// 存储每个点的邻接点集合的数组
	private List<Integer>[] adjVertexArray;
	// 邻接矩阵
	private int[][] adjMatrix;
	// 表示任意两个点之间的距离的矩阵
	private int[][] userMatrix;
	private final int length;
	private int[][] siteMatrix;

	@SuppressWarnings("unchecked")
	public Tree(Vertex[] vertexs) {
		this.vertexs = vertexs;
		length = vertexs.length;
		adjVertexArray = new ArrayList[length];
		for (int i = 0; i < length; i++) {
			adjVertexArray[i] = new ArrayList<Integer>();
			adjTable.put(i, adjVertexArray[i]);
		}
		adjMatrix = new int[length][length];
		userMatrix = new int[length][length];
	}

	@SuppressWarnings("unchecked")
	public Tree(Vertex[] vertexs, Map<Integer, List<Integer>> adjTable) {
		this.vertexs = vertexs;
		this.adjTable = adjTable;
		length = vertexs.length;
	}

	public void setAdjTable(Map<Integer, List<Integer>> adjTable) {
		this.adjTable = adjTable;
	}

	/**
	 * 添加一条边，并对邻接表做相应修改
	 * 
	 * @param src
	 * @param dest
	 */
	public void addEdge(int src, int dest) {
		System.out.println(src + "->" + dest);
		adjVertexArray[src].add(dest);
		adjVertexArray[dest].add(src);
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
	public void depthFirstSearch(int start, int curr, int distance) {
		// 标识该点已访问过
		vertexs[curr].flag = true;
		userMatrix[start][curr] = distance;
		++distance;
		List<Integer> adjVertexs = adjTable.get(curr);
		for (int dest : adjVertexs) {
			// 如果该点未被访问过
			if (!vertexs[dest].flag) {
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
		for (int i = 0; i < vertexs.length; i++) {
			depthFirstSearch(i, i, 0);
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
		siteMatrix = new int[length][length];
		for (int i = 0; i < length; i++) {
			for (int j = i + 1; j < length; j++) {
				int sameDegree = getSameDegree(vertexs[i].dirId,
						vertexs[j].dirId);
				siteMatrix[i][j] = vertexs[i].degree + vertexs[j].degree
						- sameDegree * 2;
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
	public int getSameDegree(int a, int b) {
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
	 * @return
	 */
	public double getGTDCC() {
		double[] vectorA = toMinusAvgArray(userMatrix);
		double[] vectorB = toMinusAvgArray(siteMatrix);
		double a = 0;
		double m1 = 0;
		double m2 = 0;
		for (int i = 0, l = vectorA.length; i < l; i++) {
			a += vectorA[i]*vectorB[i];
			m1 += vectorA[i]*vectorA[i];
			m2 += vectorB[i]*vectorB[i];
		}
		double m = Math.sqrt(m1*m2);
		return a/m;
	}
	/**
	 * 把矩阵的上三角转为数组，并让该数组的每个元素减去该数组所有元素的平均数
	 * @param matrix
	 * @return 
	 */
	public double[] toMinusAvgArray(int[][] matrix) {
		double[] array = new double[length * (length - 1) /2];
		double avg = 0;
		for (int i = 0, a = 0; i < length; i++) {
			for (int j = i + 1; j < length; j++) {
				array[a] = matrix[i][j];
				avg += array[a];
				++a;
			}
		}
		avg = avg / array.length;
		for (int i = 0, l = array.length; i < l; i++) {
			array[i] -= avg;
		}
		return array;
	}
}
