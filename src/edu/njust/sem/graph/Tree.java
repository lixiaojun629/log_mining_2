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
	private List<Vertex> vertexs = new ArrayList<>();
	// �ڽӱ�
	private Map<Vertex, List<Vertex>> adjTable = new HashMap<>();
	// ��ʾ����������֮��ľ���ľ���
	private int[][] userMatrix;
	private int[][] siteMatrix;

	public List<Vertex> getVertexs() {
		return vertexs;
	}

	/**
	 * ���һ���ߣ������ڽӱ�����Ӧ�޸�
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
	 * ������ȱ���
	 * 
	 * @param start
	 *            ���α��������
	 * @param curr
	 *            ���ڷ��ʵڵ�
	 * @param distance
	 *            ��������ڷ��ʵĵ�֮��ľ���
	 */
	public void depthFirstSearch(Vertex start, Vertex curr, int distance) {
		// ��ʶ�õ��ѷ��ʹ�
		curr.flag = true;
		int s = vertexs.indexOf(start);
		int c = vertexs.indexOf(curr);
		userMatrix[s][c] = distance;
		++distance;
		List<Vertex> adjVertexs = adjTable.get(curr);
		for (Vertex dest : adjVertexs) {
			// ����õ�δ�����ʹ�
			if (!dest.flag) {
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
		int length = vertexs.size();
		userMatrix = new int[length][length];
		for (int i = 0; i < vertexs.size(); i++) {
			depthFirstSearch(vertexs.get(i), vertexs.get(i), 0);
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
	 * ��ȡ����Ŀ¼��ͬ����͵ȼ����ϲ�Ŀ¼�ĵȼ�
	 * 
	 * @param a
	 *            Ŀ¼1��dirId
	 * @param b
	 *            Ŀ¼2��dirId
	 * @return ��͹�ͬ�ϼ�Ŀ¼�ĵȼ�
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
	 * ����ȫ�����ϵ��
	 * 
	 * @return
	 */
	public double getGTDCC() {
		double[] vectorA = toArray(userMatrix);
		double[] vectorB = toArray(siteMatrix);
		return getCorrelationIndex(vectorA, vectorB);
	}
	/**
	 * ��ȡ��������֮������ϵ��
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
	 * ��ȡ�����ڵ�����ϵ��
	 * @param v
	 * @return
	 */
	public double getVertexGTDCC(Vertex v){
		int length = vertexs.size() - 1;
		int index = vertexs.indexOf(v);
		if(index == -1){
			throw new RuntimeException(v.dirId+"_"+v.title+"�ýڵ㲻��ͼ��");
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
	 * �Ѿ����������תΪ����
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
	 * �����ÿ��Ԫ�ؼ�ȥ����������Ԫ�ص�ƽ����
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
