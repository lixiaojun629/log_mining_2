package edu.njust.sem.graph;

/**
 * 优先级队列
 * @author lxj
 */
public class PriorityQueen {

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
} 
