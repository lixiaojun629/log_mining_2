package edu.njust.sem.graph;


/**
 * ��Ȩֵ����С�������㷨
 */
public class Graph {
	private final int INFINITY = 2;
	private Vertex[] vertexArray; // list of vertices
	private double adjMatrix[][]; // adjacency matrix
	private int nVerts; // current number of vertices
	private int currentVert;
	private PriorityQueen priorityQueen;
	private int nTree = 0; // number of verts in tree
	// ��С������
	private Tree tree;

	/**
	 * 
	 * @param matrix
	 *            ԭʼ�ڽӾ���
	 * @param vertexs
	 *            ͼ�Ķ��㼯�ϣ�List��
	 */
	public Graph(double[][] matrix, Vertex[] vertexs) {
		adjMatrix = matrix;
		nVerts = matrix.length;
		vertexArray = vertexs;
		priorityQueen = new PriorityQueen(matrix.length);
		tree = new Tree(vertexs);
	}

	/**
	 * ��ͼ�����С������
	 * @return ��С������
	 */
	public Tree createMinSpanningTree() {
		currentVert = 0; // start at 0
		// while not all verts in tree
		while (nTree < nVerts - 1) {
			// put currentVert in tree
			vertexArray[currentVert].flag = true;
			nTree++;
			// insert edges adjacent to currentVert into PQ
			for (int j = 0; j < nVerts; j++) {
				if (j == currentVert) // skip if it's us
					continue;
				if (vertexArray[j].flag) // skip if in the tree
					continue;
				double distance = adjMatrix[currentVert][j];
				if (distance == INFINITY) // skip if no edge
					continue;
				putInPQ(j, distance); // put it in PQ (maybe)
			}
			// no vertices in PQ?
			if (priorityQueen.size() == 0) {
				throw new RuntimeException("��ͼ����ͨ");
			}
			// remove edge with minimum distance, from PQ
			Edge theEdge = priorityQueen.removeMin();
			currentVert = theEdge.destVert;
			tree.addEdge(theEdge.srcVert, theEdge.destVert);
		}
		for (int j = 0; j < nVerts; j++) {
			vertexArray[j].flag = false;
		}
		return tree;
	}

	/**
	 * �������ȼ�����
	 * 
	 * @param newVert
	 * @param newDist
	 */
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
