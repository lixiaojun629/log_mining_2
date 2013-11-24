/**
 * 带权值的最小生成树算法
 */
package edu.njust.sem;

import java.util.ArrayList;
import java.util.List;

/**
 * 邻接矩阵的边
 * 
 * @author lxj
 * 
 */
class EdgeLabel{

	public String srcVert;
	public String destVert;
	public double distance;
	
	public EdgeLabel(String sv,String dv,double newDist){
		srcVert = sv;
		destVert = dv;
		distance = newDist;
	}
	@Override
	public String toString() {
		return srcVert + "→" + destVert+ ":" + distance;
	}
}
class Edge {
	public int srcVert; // index of a vertex starting edge
	public int destVert; // index of a vertex ending edge
	public double distance; // distance from src to dest

	public Edge(int sv, int dv, double newDist) {
		srcVert = sv;
		destVert = dv;
		distance = newDist;
	}
}

/**
 * 优先级队列
 * @author lxj
 */
class PriorityQueen {

	private Edge[] queArray;
	private int currSize;

	public PriorityQueen(int size) {
		queArray = new Edge[size];
		currSize = 0;
	}

	// insert item in sorted order 插入排序
	public void insert(Edge item) {
		int j;
		for (j = 0; j < currSize; j++) {
			// find place to insert
			if (item.distance >= queArray[j].distance) {
				break;
			}
		}

		for (int k = currSize - 1; k >= j; k--) {
			// move items up
			queArray[k + 1] = queArray[k];
		}
		queArray[j] = item; // insert item
		currSize++;
	}

	// remove minimum item
	public Edge removeMin() {
		return queArray[--currSize];
	}

	// remove item at n
	public void removeAtN(int n) {
		for (int j = n; j < currSize - 1; j++) {
			// move items down
			queArray[j] = queArray[j + 1];
		}
		currSize--;
	}

	// peek at minimum item
	public Edge peekMin() {
		return queArray[currSize - 1];
	}

	// return number of items
	public int size() {
		return currSize;
	}

	// true if queue is empty
	public boolean isEmpty() {
		return (currSize == 0);
	}

	// peek at item n
	public Edge peekN(int n) {
		return queArray[n];
	}

	// find item with specified
	public int find(int findDex) { // destVert value
		for (int j = 0; j < currSize; j++) {
			if (queArray[j].destVert == findDex) {
				return j;
			}
		}
		return -1;
	}

} // end class PriorityQueen

class Vertex {
	public String label; // label (e.g. "A")
	public boolean isInTree;

	// constructor
	public Vertex(String lab) {
		label = lab;
		isInTree = false;
	}

} // end class Vertex

public class Graph {
	private final int INFINITY = 2;
	private Vertex[] vertexArray; // list of vertices
	private double adjMatrix[][]; // adjacency matrix
	private int nVerts; // current number of vertices
	private int currentVert;
	private PriorityQueen priorityQueen;
	private int nTree = 0; // number of verts in tree

	/**
	 * 
	 * @param matrix
	 *            原始邻接矩阵
	 * @param vertexs
	 *            图的顶点集合（List）
	 */
	public Graph(double[][] matrix, List<String> vertexs) {
		adjMatrix = matrix;
		nVerts = matrix.length;
		vertexArray = new Vertex[matrix.length];
		for (int i = 0; i < vertexs.size(); i++) {
			vertexArray[i] = new Vertex(vertexs.get(i));
		}
		priorityQueen = new PriorityQueen(matrix.length);
	}

	// minimum spanning tree
	public ArrayList<EdgeLabel> createMinSpanningTree() {
		ArrayList<EdgeLabel> edges = new ArrayList<>();
		currentVert = 0; // start at 0
		// while not all verts in tree
		while (nTree < nVerts - 1) {
			// put currentVert in tree
			vertexArray[currentVert].isInTree = true;
			nTree++;
			// insert edges adjacent to currentVert into PQ
			for (int j = 0; j < nVerts; j++) {
				if (j == currentVert) // skip if it's us
					continue;
				if (vertexArray[j].isInTree) // skip if in the tree
					continue;
				double distance = adjMatrix[currentVert][j];
				if (distance == INFINITY) // skip if no edge
					continue;
				putInPQ(j, distance); // put it in PQ (maybe)
			}
			// no vertices in PQ?
			if (priorityQueen.size() == 0) {
				throw new RuntimeException("此图非连通");
			}
			// remove edge with minimum distance, from PQ
			Edge theEdge = priorityQueen.removeMin();
			int sourceVert = theEdge.srcVert;
			currentVert = theEdge.destVert;

			// display edge from source to current
			edges.add(new EdgeLabel(vertexArray[sourceVert].label,vertexArray[currentVert].label,theEdge.distance));
//			System.out.print("(" + vertexArray[sourceVert].label + ")" + "→");
//			System.out.print("(" + vertexArray[currentVert].label + ") ："
//					+ theEdge.distance);
//			System.out.println();
		}
		// mst is complete unmark vertices
		for (int j = 0; j < nVerts; j++){
			vertexArray[j].isInTree = false;
		}
		return edges;
	}

	public void putInPQ(int newVert, double newDist) {
		// is there another edge with the same destination vertex?
		int queueIndex = priorityQueen.find(newVert);
		// got edge's index
		if (queueIndex != -1) {
			Edge tempEdge = priorityQueen.peekN(queueIndex); // get edge
			double oldDist = tempEdge.distance;
			// if new edge shorter,
			if (oldDist > newDist) {
				priorityQueen.removeAtN(queueIndex); // remove old edge
				Edge theEdge = new Edge(currentVert, newVert, newDist);
				priorityQueen.insert(theEdge); // insert new edge
			}
			// else no action; just leave the old vertex there
		} else {
			Edge theEdge = new Edge(currentVert, newVert, newDist);
			priorityQueen.insert(theEdge);
		}
	}
}
