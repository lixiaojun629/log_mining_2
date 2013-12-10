package edu.njust.sem.test;

import org.junit.Test;

public class test {
	@Test
	public void test1(){
		String dir = "Flower & Gardening Plant";
		String [] arr = dir.split("\\W+");
		System.out.println(arr.length);
		for(String str : arr){
			System.out.println(str);
		}
	}
}
