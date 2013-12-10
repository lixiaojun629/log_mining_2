package edu.njust.sem;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.njust.sem.util.PrintUtil;

public class Cluster {
	public Cluster()   {
//		 PrintStream out = null;
//		try {
//			out = new PrintStream("d:/test.txt");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
//		 System.setOut(out);  
	}
	/**
	 * 把已经过行列变换的矩阵分块
	 * @param matrix 已经过行列变换的矩阵
	 * @param dividePointRow 划分点的位置
	 * @return 两个子块矩阵构成的数组
	 */
	public double[][][] matrixBlock(double[][] matrix,int dividePointRow) {
		double[][][] block = new double[2][][];
		double[][] A11 = new double[dividePointRow +1 ][dividePointRow + 2];
		double[][] A22 = new double[matrix.length - 1 - dividePointRow][matrix.length  - dividePointRow];
		for(int i = 0; i <= dividePointRow; i++){
			for(int j = 0 ; j <= dividePointRow; j++){
					A11[i][j] = matrix[i][j]; 
			}
			//为子块矩阵的最后一列元素赋值，使得子块矩阵行仍保持其在原始的矩阵中的行索引
			A11[i][dividePointRow + 1] = matrix[i][matrix.length];
		}
		for(int i = dividePointRow + 1; i <= matrix.length - 1; i++){
			for(int j = dividePointRow + 1; j <= matrix.length - 1; j++){
				A22[i - 1 - dividePointRow][j - 1 - dividePointRow] = matrix[i][j]; 
			}
			//为子块矩阵的最后一列元素赋值，使得子块矩阵行仍保持其在原始的矩阵中的行索引
			A22[i - 1 - dividePointRow][A22.length] = matrix[i][matrix.length];
		}
		
		block[0] = A11;
		block[1] = A22;
		return block;
	}
	/**
	 * 矩阵的行列变换
	 * @param matrix 
	 * @return 行列变换过后的矩阵
	 */
	public double[][] transformMatrix(double[][] matrix) {
		for (int i = 0, length = matrix.length; i <= length - 2; i++) {
			// 找出matrix 中第i 行从第i + 1 列到第最后一列元素的最大值所在的列k;
			int k = getIndexOfMaxValue(matrix, i);
			// 如果找到该行（第i行）中从第i+1列到最后一列的元素中的最大值（不能为零），则交换第i + 1 行和第k 行，否则不交换。
			if(k != 0 && k != i+1){
				//交换第i + 1 行和第k 行
				double[] temp = null;
				temp = matrix[i + 1];
				matrix[i + 1] = matrix[k];
				matrix[k] = temp;
//				System.out.println("交换第" + (i + 1) + "行和第" + k + "行后的矩阵为：");
//				print(matrix);
				// 交换第i + 1 列和第k 列;
				temp = new double[matrix.length];
				for (int a = 0; a < temp.length; a++) {
					temp[a] = matrix[a][k];
					matrix[a][k] = matrix[a][i + 1];
					matrix[a][i + 1] = temp[a];
				}
				
//				System.out.println("交换第" + (i + 1) + "列和第" + k + "列后的矩阵为：");
//				print(matrix);
			}
		}
		return matrix;

	}
	/**
	 * 返回，在矩阵matrix的第i行中，从第i+1列到最后一列的最大值所在的列。
	 * @param matrix
	 * @param row
	 * @return 若返回0，则表示从第i+1列到最后一列的值全是0，没必要进行行列变换。
	 */
	public int getIndexOfMaxValue(double[][] matrix, int row) {
		double max = 0;// 在第i行中，从第i+1列到最后一列的最大值
		int k = 0;// 最大值max所在的位置(第k列)
		for (int col = row + 1; col < matrix.length; col++) {
			if (max < matrix[row][col]) {
				max = matrix[row][col];
				k = col;
			}
		}
		return k;
	}
	/**
	 * 矩阵行列变换后，按主对角线对矩阵进行分块处理，为了实现分块处理，先要找到划分点（divide point）
	 * @param matrix
	 * @return 第一个符合条件（Fd值最大）的划分点所在的行(或列)数(索引从0开始)
	 */
	public int  getDividePointRow(double[][] matrix){
		int dividePointRow = 0;//划分点所在的行(或列)数;
		double maxFd = getFd(matrix,0);//为maxFd赋初值
		for(int i = 0; i <= matrix.length -2; i++){
			double fd = getFd(matrix ,i);
			if(maxFd < fd){
				maxFd = fd;
				dividePointRow = i;
			}
		}
		return dividePointRow;
	}
	/**
	 * 根据公式 Fd = Md(A11) * Md(A22) - Md(A12) * Md (A21)
	 * @param matrix
	 * @param rowOfPointOnDiagonal 矩阵对角线上的一点的位置（行数或列数）
	 * @return 计算得出的Fd的值
	 */
	private double  getFd(double[][] matrix,int rowOfPointOnDiagonal ) {
		double m11 = 0;
		double m22 = 0;
		double m12 = 0;
		double m21 = 0;
		for(int i = 0; i <=rowOfPointOnDiagonal ; i++){
			for(int j = 0; j <= rowOfPointOnDiagonal; j++){
				m11 += matrix[i][j];
			}
		}
		for(int i = rowOfPointOnDiagonal+1; i < matrix.length; i++){
			for (int j = rowOfPointOnDiagonal+1; j< matrix.length; j++){
				m22 += matrix[i][j];
			}
		}
		
		for(int i = 0; i <= rowOfPointOnDiagonal; i++){
			for(int j = rowOfPointOnDiagonal+1; j < matrix.length; j++){
				m12+= matrix[i][j];
			}
		}
		for(int i = rowOfPointOnDiagonal + 1; i < matrix.length; i++){
			for(int j = 0; j <= rowOfPointOnDiagonal; j++){
				m21+= matrix[i][j];
			}
		}
		
		return m11*m22 - m12*m21;
	}
	/**
	 * 计算矩阵的凝聚度（所有元素的均值）
	 * @param matrix 
	 * @return 凝聚度
	 */
	public double getT(double[][] matrix){
		double sum = 0;
		for(int i = 0; i < matrix.length ; i++){
			for(int j = 0 ; j < matrix.length; j++){
				sum += matrix[i][j];
			}
		}
		return sum/(matrix.length*matrix.length);
	}
	
	/**
	 * 得到所有划分后的矩阵子块
	 * @param matrix
	 * @return
	 */
	public List<double[][]> getMarixList(double[][] matrix){
		List<double[][]> matrixs = new ArrayList<double[][]>();
		getAllMatrixBlocks(matrix,matrixs);
		return matrixs;
	}
	/**
	 * 通过递归，把满足条件的子块矩阵放进List集合中
	 * @param matrix 已经过行列变化的矩阵
	 * @param matrixs 存放矩阵的List集合
	 */
	public void getAllMatrixBlocks(double[][] matrix,List<double[][]> matrixs){
		int dividePointRow = getDividePointRow(matrix);
		double[][][] block = matrixBlock(matrix, dividePointRow);
		for(int i = 0; i <block.length; i++){
			if(getT(block[i]) >= 0.4){
				matrixs.add(block[i]);
			}else{
				getAllMatrixBlocks(block[i], matrixs);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		double[][] matrix = new double[][] {
				{ 1 ,.88, 0 ,.88, 0 , 0 , 0 ,.93,.95, 1},
				{.88, 1 , 0 ,.94, 0 , 0 , 0 ,.95,.93, 2},
				{ 0 , 0 , 1 , 0 ,.96, 0 , 0 , 0 , 0 , 3},
				{.88,.94, 0 , 1 , 0 , 0 , 0 ,.92,.95, 4},
				{ 0 , 0 ,.96, 0 , 1 , 0 , 0 , 0 , 0 , 5},
				{ 0 , 0 , 0 , 0 , 0 , 1 ,.99, 0 , 0 , 6},
				{ 0 , 0 , 0 , 0 , 0 ,.99, 1 , 0 , 0 , 7},
				{.93,.95, 0 ,.92, 0 , 0 , 0 , 1 ,.90, 8},
				{.95,.93, 0 ,.95, 0 , 0 , 0 ,.90, 1 , 9}
		};
		Cluster cluster= new Cluster();
		matrix = cluster.transformMatrix(matrix);
		File file = new File("test1.txt");
		PrintUtil.setOut(file);
		PrintUtil.printMatrixToFile(matrix);
		List<double[][]> matrixs = cluster.getMarixList(matrix);
		for(double[][] m : matrixs){
			PrintUtil.println("子块矩阵：");
			PrintUtil.printMatrixToFile(m);
		}
		PrintUtil.close();
	}
}
