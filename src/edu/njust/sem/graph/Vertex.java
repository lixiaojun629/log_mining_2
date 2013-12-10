package edu.njust.sem.graph;

public class Vertex {

	// 每个顶点都是一个目录，id为目录编号（e.g. '010203'）
	public int dirId;
	// 顶点状态的标志，例如在最小生成树算法中标识该顶点是否已经在树中，在图的遍历中标识该顶点是否已被访问过
	public boolean flag = false;
	// 目录的具体内容（e.g. 'Linght&Lighting'）
	public String title;
	// 目录的级别(0,1,2,3)
	public int degree = 3;

	/**
	 * 构造函数
	 * 
	 * @param dirId
	 * @param title
	 */
	public Vertex(int dirId, String title) {
		this.dirId = dirId;
		this.title = title;
		while (dirId % 100 == 0) {
			--degree;
			dirId /= 100;
			if (degree == 0) {
				break;
			}
		}
	}

	@Override
	public String toString() {
		return dirId + "_" + flag;
	}

}
