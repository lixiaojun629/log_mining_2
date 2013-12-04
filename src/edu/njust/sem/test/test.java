package edu.njust.sem.test;

import org.junit.Test;

public class test {
	@Test
	public void test1(){
		String pid = "jE4LjMwLjEwMy4zOTIwMTMwNTE5MDIwNzE0MjY4MjgyODY0NAM;sf_img=AM;sid=TY5OTI5MzQ2MzQzNzM5NTI6MjE4LjMwLjEwMy4zOQM;";
		System.out.println(pid.substring(0,pid.indexOf(';')));
	}
}
