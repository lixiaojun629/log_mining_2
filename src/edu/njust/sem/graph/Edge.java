package edu.njust.sem.graph;

public class Edge {
	public int srcVert; // index of a vertex starting edge
	public int destVert; // index of a vertex ending edge
	public double distance; // distance from src to dest

	public Edge(int sv, int dv, double newDist) {
		srcVert = sv;
		destVert = dv;
		distance = newDist;
	}
}
