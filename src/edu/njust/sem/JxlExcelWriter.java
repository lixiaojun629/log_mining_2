package edu.njust.sem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Jxl �� Excelд������.
 * @author lxj
 */
public class JxlExcelWriter {

	/**
	 * @param datas
	 *            ��װ��Object[]���б�, һ����String����.
	 * @param sheetIndex 
	 *            ������д��ڼ�ҳ.
	 */
	public void writeExcel(OutputStream out, List datas,int sheetIndex) {
		if (datas == null) {
			throw new IllegalArgumentException("дexcel����ҪList����!");
		}
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(out);
			WritableSheet ws = workbook.createSheet("sheet "+sheetIndex, sheetIndex);
			int rowNum = 0; // Ҫд����
			for (int i = 0; i < datas.size(); i++, rowNum++) {// дsheet
				Object[] cells = (Object[]) datas.get(i);
				putRow(ws, rowNum, cells); // ѹһ�е�sheet
			}
			workbook.write();
			workbook.close(); // һ��Ҫ�ر�, ����û�б���Excel
		} catch (RowsExceededException e) {
			System.out.println("jxl write RowsExceededException: "
					+ e.getMessage());
		} catch (WriteException e) {
			System.out.println("jxl write WriteException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("jxl write file i/o exception!, cause by: "
					+ e.getMessage());
		}
	}

	private void putRow(WritableSheet ws, int rowNum, Object[] cells)
			throws RowsExceededException, WriteException {
		for (int j = 0; j < cells.length; j++) {// дһ��
			Label cell = new Label(j, rowNum, "" + cells[j]);
			ws.addCell(cell);
		}
	}

	public static void main(String[] args) {

		List datas = new ArrayList();
		String[] data = { "1", "chenlb" };
		datas.add(data);
		try {
			OutputStream out = new FileOutputStream(new File("d://a.xls"));
			JxlExcelWriter jxlExcelWriter = new JxlExcelWriter();
			jxlExcelWriter
					.writeExcel(out, datas, 1);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
