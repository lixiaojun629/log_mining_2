package edu.njust.sem.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class PrintUtil {
	private static PrintWriter pw = null;
	/**
	 * constructor
	 * @throws FileNotFoundException
	 */
	public PrintUtil(PrintWriter pw) throws FileNotFoundException{
		System.out.println("PrintUtil's constructor is called");
		this.pw = pw;
	}
	
	/**
	 * ����pw����������ļ�
	 * @param file
	 * @throws FileNotFoundException
	 */
	public static void setOut(File file) throws FileNotFoundException{
		if(pw != null){
			pw.close();
		}
		pw = new PrintWriter(file);
	}
	
	/**
	 * �ڿ���̨��ӡ����
	 * @param matrix
	 */
	public static void printMatrix(double[][] matrix){
		for(int i = 0; i < matrix.length;i++){
			for(int j = 0; j < matrix[i].length; j++){
				pw.printf("%.6f", matrix[i][j]);
				pw.print("  ");
			}
			pw.println();
		}
	}
	public static void println(String str){
		pw.println(str);
	}
	/**
	 * �ر��ļ���������Ա������������ļ�
	 */
	public static void close(){
		pw.close();
	}

	public static void printMatrix(int[][] matrix) {
		for(int i = 0; i < matrix.length;i++){
			for(int j = 0; j < matrix[i].length; j++){
				 System.out.printf("%6d",matrix[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}		
	}
}
	