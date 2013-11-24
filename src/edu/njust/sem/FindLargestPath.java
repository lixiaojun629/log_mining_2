package edu.njust.sem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FindLargestPath {
	File file = new File("D:\\result2\\result2.txt");
	public void run() throws IOException {
		int number = 0;
		int count = 0;
		int maxCount = 0;
		int maxNumber = 0;
		Scanner scan = new Scanner(file);
		scan.useDelimiter("\r\n");
		boolean isVert = false;
		while (scan.hasNext()) {
			String str = scan.next();
			if (str.indexOf("所有顶点") > -1) {
				isVert = true;
				count = 0;
				number++;
			}
			if (isVert) {
				count++;
			}
			if (str.indexOf("最小生成树") > -1 && isVert) {
				isVert = false;
				if (maxCount < count) {
					maxCount = count;
					maxNumber = number;
				}
			}
		}
		scan.close();
		System.out.println("number:"+maxNumber + " count:"+maxCount);
	}
	public void run2() throws IOException{
		Scanner scan1 = new Scanner(file);
		scan1.useDelimiter("\r\n");
		int number = 0;
		boolean flag = false;
		while (scan1.hasNext()) {
			String str = scan1.next();
			if (str.indexOf("最小生成树") > -1) {
				number++;
				if(number == 1075){
					flag = true;
				}else{
					flag = false;
				}
			}
			if(flag){
				System.out.println(str);
			}
		}
		scan1.close();
	}
	public static void main(String[] args) throws IOException {
		FindLargestPath f = new FindLargestPath();
		f.run2();
	}
}
