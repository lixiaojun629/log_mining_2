package edu.njust.sem.test;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.njust.sem.CatalogFreq;
import edu.njust.sem.graph.Graph;
import edu.njust.sem.graph.Tree;
import edu.njust.sem.graph.Vertex;
import edu.njust.sem.util.DBUtil;
import edu.njust.sem.util.PrintUtil;

public class Test1 {
	public void test() throws SQLException{
		Integer[] path = {773,1117,1911,2405,2667,86,2668};
		CatalogFreq catalogFreq = new CatalogFreq(path);
		double[][] catalogFreqMatrix = catalogFreq.getCatalogFreqMatrix();
		Vertex[] vertexs = catalogFreq.getVertexs();
		for(Vertex v:vertexs){
			System.out.println(v.dirId);
		}
		Graph graph = new Graph(catalogFreqMatrix, vertexs);
		List<Tree> tree = graph.createMinSpanningTree();
		System.out.println("---------我是万恶的分割线-----------------");
		
	}
	public static void main(String[] args) {
		Test1 t = new Test1();
		try {
			t.test();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.closeConn();
		}
	}
}
