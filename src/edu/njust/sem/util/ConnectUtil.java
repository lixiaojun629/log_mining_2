package edu.njust.sem.util;

import java.io.BufferedReader;  
import java.io.InputStreamReader;  
  
public class ConnectUtil {  
  
    /** 
     * ִ��CMD����,������String�ַ��� 
     */  
	private static String adslTitle = "�������";
	private static String adslName = "18914751389@njxy";
	private static String adslPass = "900629";
    public static String executeCmd(String strCmd) throws Exception {  
        Process p = Runtime.getRuntime().exec("cmd /c " + strCmd);  
        StringBuilder sbCmd = new StringBuilder();  
        BufferedReader br = new BufferedReader(new InputStreamReader(p  
                .getInputStream()));  
        String line;  
        while ((line = br.readLine()) != null) {  
            sbCmd.append(line + "\n");  
        }  
        return sbCmd.toString();  
    }  
  
    /** 
     * ����ADSL 
     */  
    public static boolean connAdsl(String adslTitle, String adslName, String adslPass) throws Exception {  
        System.out.println("���ڽ�������.");  
        String adslCmd = "rasdial " + adslTitle + " " + adslName + " "  
                + adslPass;  
        String tempCmd = executeCmd(adslCmd);  
        // �ж��Ƿ����ӳɹ�  
        if (tempCmd.indexOf("������") > 0) {  
            System.out.println("�ѳɹ���������.");  
            return true;  
        } else {  
            System.err.println(tempCmd);  
            System.err.println("��������ʧ��");  
            return false;  
        }  
    }  
  
    /** 
     * �Ͽ�ADSL 
     */  
    public static boolean cutAdsl(String adslTitle) throws Exception {  
        String cutAdsl = "rasdial " + adslTitle + " /disconnect";  
        String result = executeCmd(cutAdsl);  
         
        if (result.indexOf("û������")!=-1){  
            System.err.println(adslTitle + "���Ӳ�����!");  
            return false;  
        } else {  
            System.out.println("�����ѶϿ�");  
            return true;  
        }  
    }  
    public static boolean reConnAdsl(){
    	boolean flag = true;
    	try {
			flag = cutAdsl(adslTitle);
			Thread.sleep(1000);
			flag = connAdsl(adslTitle,adslName,adslPass);
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return flag;
    }
    public static void main(String[] args) throws Exception {  
       
    	reConnAdsl();  
       
    }  
}  

