package edu.njust.sem.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * �ļ�������ع�����
 * @author lxj
 *
 */
public class FileUtil {
	/**
	 * ��ȡĿ¼�µ������ļ�
	 * @param parent
	 * @return
	 */
	public static List<File> getChildFiles(File parent) {
		List<File> files = new ArrayList<>();
		getFile(parent, files);
		return files;
	}

	private static void getFile(File rootFile, List<File> files) {
		if (rootFile.isDirectory()) {
			File[] childFiles = rootFile.listFiles();
			for (File f : childFiles) {
				if (f.isFile()) {
					files.add(f);
				} else {
					getFile(f, files);
				}
			}
		}
	}
}
