package edu.njust.sem;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.junit.Test;

import edu.njust.sem.util.PrintUtil;

public class ReadFile {
	@Test
	public void run1() throws IOException {
		File sourceFile = new File("D:\\catalog.txt");
		Scanner scan = new Scanner(sourceFile);
		scan.useDelimiter("\r\n");
		boolean flag = false;
		int i = 0;
		while (scan.hasNext()) {
			String str = scan.next();
			i++;
			if(i < 200){
				System.out.println(str);
			}else{
				break;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		ReadFile rf = new ReadFile();
		rf.run1();
	}
}
