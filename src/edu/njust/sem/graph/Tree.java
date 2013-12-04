package edu.njust.sem.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * һ�������С������
 * 
 * @author lxj
 * 
 */
public class Tree {
	// ���㼯��
	private Vertex[] vertexs;
	// �ڽӱ�
	private Map<Integer, List<Integer>> adjTable = new HashMap<>();
	// �洢ÿ������ڽӵ㼯�ϵ�����
	private List<Integer>[] adjVertexArray;
	// �ڽӾ���
	private int[][] adjMatrix;
	// ��ʾ����������֮��ľ���ľ���
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
	 * ���һ���ߣ������ڽӱ�����Ӧ�޸�
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
	 * ������ȱ���
	 * 
	 * @param start
	 *            ���α��������
	 * @param curr
	 *            ���ڷ��ʵڵ�
	 * @param distance
	 *            ��������ڷ��ʵĵ�֮��ľ���
	 */
	public void depthFirstSearch(int start, int curr, int distance) {
		// ��ʶ�õ��ѷ��ʹ�
		vertexs[curr].flag = true;
		userMatrix[start][curr] = distance;
		++distance;
		List<Integer> adjVertexs = adjTable.get(curr);
		for (int dest : adjVertexs) {
			// ����õ�δ�����ʹ�
			if (!vertexs[dest].flag) {
				depthFirstSearch(start, dest, distance);
			}
		}
		--distance;
	}

	/**
	 * �ֱ���ͼ�е�ÿ����Ϊ������ͼ
	 * 
	 * @return �洢ͼ����������֮�����ľ���
	 */
	public int[][] getUserMatrix() {
		for (int i = 0; i < vertexs.length; i++) {
			depthFirstSearch(i, i, 0);
			// ÿ�α������Ժ�Ѹ�������ı�־λ��ԭ
			for (Vertex v : vertexs) {
				v.flag = false;
			}
		}
		return userMatrix;
	}

	/**
	 * ����Ŀ¼����վ��ԭ�еĽṹ���õ��洢��������Ŀ¼�ڵ�֮��ľ���ľ���
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
	 * ��ȡ����Ŀ¼��ͬ����͵ȼ����ϲ�Ŀ¼�ĵȼ�
	 * 
	 * @param a
	 *            Ŀ¼1��dirId
	 * @param b
	 *            Ŀ¼2��dirId
	 * @return ��͹�ͬ�ϼ�Ŀ¼�ĵȼ�
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
	 * ����ȫ�����ϵ��
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
	 * �Ѿ����������תΪ���飬���ø������ÿ��Ԫ�ؼ�ȥ����������Ԫ�ص�ƽ����
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
