package edu.njust.sem.graph;

public class Vertex {

	// ÿ�����㶼��һ��Ŀ¼��idΪĿ¼��ţ�e.g. '010203'��
	public int dirId;
	// ����״̬�ı�־����������С�������㷨�б�ʶ�ö����Ƿ��Ѿ������У���ͼ�ı����б�ʶ�ö����Ƿ��ѱ����ʹ�
	public boolean flag = false;
	// Ŀ¼�ľ������ݣ�e.g. 'Linght&Lighting'��
	public String title;
	// Ŀ¼�ļ���(0,1,2,3)
	public int degree = 3;

	/**
	 * ���캯��
	 * 
	 * @param dirId
	 * @param title
	 */
	public Vertex(int dirId, String title) {
		this.dirId = dirId;
		this.title = title;
		while (dirId % 100 == 0) {
			--degree;
			dirId /= 100;
			if (degree == 0) {
				break;
			}
		}
	}

	@Override
	public String toString() {
		return dirId + "_" + flag;
	}

}
