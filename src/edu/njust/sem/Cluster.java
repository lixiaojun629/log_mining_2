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
	 * ���Ѿ������б任�ľ���ֿ�
	 * @param matrix �Ѿ������б任�ľ���
	 * @param dividePointRow ���ֵ��λ��
	 * @return �����ӿ���󹹳ɵ�����
	 */
	public double[][][] matrixBlock(double[][] matrix,int dividePointRow) {
		double[][][] block = new double[2][][];
		double[][] A11 = new double[dividePointRow +1 ][dividePointRow + 2];
		double[][] A22 = new double[matrix.length - 1 - dividePointRow][matrix.length  - dividePointRow];
		for(int i = 0; i <= dividePointRow; i++){
			for(int j = 0 ; j <= dividePointRow; j++){
					A11[i][j] = matrix[i][j]; 
			}
			//Ϊ�ӿ��������һ��Ԫ�ظ�ֵ��ʹ���ӿ�������Ա�������ԭʼ�ľ����е�������
			A11[i][dividePointRow + 1] = matrix[i][matrix.length];
		}
		for(int i = dividePointRow + 1; i <= matrix.length - 1; i++){
			for(int j = dividePointRow + 1; j <= matrix.length - 1; j++){
				A22[i - 1 - dividePointRow][j - 1 - dividePointRow] = matrix[i][j]; 
			}
			//Ϊ�ӿ��������һ��Ԫ�ظ�ֵ��ʹ���ӿ�������Ա�������ԭʼ�ľ����е�������
			A22[i - 1 - dividePointRow][A22.length] = matrix[i][matrix.length];
		}
		
		block[0] = A11;
		block[1] = A22;
		return block;
	}
	/**
	 * ��������б任
	 * @param matrix 
	 * @return ���б任����ľ���
	 */
	public double[][] transformMatrix(double[][] matrix) {
		for (int i = 0, length = matrix.length; i <= length - 2; i++) {
			// �ҳ�matrix �е�i �дӵ�i + 1 �е������һ��Ԫ�ص����ֵ���ڵ���k;
			int k = getIndexOfMaxValue(matrix, i);
			// ����ҵ����У���i�У��дӵ�i+1�е����һ�е�Ԫ���е����ֵ������Ϊ�㣩���򽻻���i + 1 �к͵�k �У����򲻽�����
			if(k != 0 && k != i+1){
				//������i + 1 �к͵�k ��
				double[] temp = null;
				temp = matrix[i + 1];
				matrix[i + 1] = matrix[k];
				matrix[k] = temp;
//				System.out.println("������" + (i + 1) + "�к͵�" + k + "�к�ľ���Ϊ��");
//				print(matrix);
				// ������i + 1 �к͵�k ��;
				temp = new double[matrix.length];
				for (int a = 0; a < temp.length; a++) {
					temp[a] = matrix[a][k];
					matrix[a][k] = matrix[a][i + 1];
					matrix[a][i + 1] = temp[a];
				}
				
//				System.out.println("������" + (i + 1) + "�к͵�" + k + "�к�ľ���Ϊ��");
//				print(matrix);
			}
		}
		return matrix;

	}
	/**
	 * ���أ��ھ���matrix�ĵ�i���У��ӵ�i+1�е����һ�е����ֵ���ڵ��С�
	 * @param matrix
	 * @param row
	 * @return ������0�����ʾ�ӵ�i+1�е����һ�е�ֵȫ��0��û��Ҫ�������б任��
	 */
	public int getIndexOfMaxValue(double[][] matrix, int row) {
		double max = 0;// �ڵ�i���У��ӵ�i+1�е����һ�е����ֵ
		int k = 0;// ���ֵmax���ڵ�λ��(��k��)
		for (int col = row + 1; col < matrix.length; col++) {
			if (max < matrix[row][col]) {
				max = matrix[row][col];
				k = col;
			}
		}
		return k;
	}
	/**
	 * �������б任�󣬰����Խ��߶Ծ�����зֿ鴦��Ϊ��ʵ�ַֿ鴦����Ҫ�ҵ����ֵ㣨divide point��
	 * @param matrix
	 * @return ��һ������������Fdֵ��󣩵Ļ��ֵ����ڵ���(����)��(������0��ʼ)
	 */
	public int  getDividePointRow(double[][] matrix){
		int dividePointRow = 0;//���ֵ����ڵ���(����)��;
		double maxFd = getFd(matrix,0);//ΪmaxFd����ֵ
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
	 * ���ݹ�ʽ Fd = Md(A11) * Md(A22) - Md(A12) * Md (A21)
	 * @param matrix
	 * @param rowOfPointOnDiagonal ����Խ����ϵ�һ���λ�ã�������������
	 * @return ����ó���Fd��ֵ
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
	 * �����������۶ȣ�����Ԫ�صľ�ֵ��
	 * @param matrix 
	 * @return ���۶�
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
	 * �õ����л��ֺ�ľ����ӿ�
	 * @param matrix
	 * @return
	 */
	public List<double[][]> getMarixList(double[][] matrix){
		List<double[][]> matrixs = new ArrayList<double[][]>();
		getAllMatrixBlocks(matrix,matrixs);
		return matrixs;
	}
	/**
	 * ͨ���ݹ飬�������������ӿ����Ž�List������
	 * @param matrix �Ѿ������б仯�ľ���
	 * @param matrixs ��ž����List����
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
			PrintUtil.println("�ӿ����");
			PrintUtil.printMatrixToFile(m);
		}
		PrintUtil.close();
	}
}
