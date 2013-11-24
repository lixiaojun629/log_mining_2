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
 * Jxl 的 Excel写数据器.
 * @author lxj
 */
public class JxlExcelWriter {

	/**
	 * @param datas
	 *            封装着Object[]的列表, 一般是String内容.
	 * @param sheetIndex 
	 *            把数据写入第几页.
	 */
	public void writeExcel(OutputStream out, List datas,int sheetIndex) {
		if (datas == null) {
			throw new IllegalArgumentException("写excel流需要List参数!");
		}
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(out);
			WritableSheet ws = workbook.createSheet("sheet "+sheetIndex, sheetIndex);
			int rowNum = 0; // 要写的行
			for (int i = 0; i < datas.size(); i++, rowNum++) {// 写sheet
				Object[] cells = (Object[]) datas.get(i);
				putRow(ws, rowNum, cells); // 压一行到sheet
			}
			workbook.write();
			workbook.close(); // 一定要关闭, 否则没有保存Excel
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
		for (int j = 0; j < cells.length; j++) {// 写一行
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
