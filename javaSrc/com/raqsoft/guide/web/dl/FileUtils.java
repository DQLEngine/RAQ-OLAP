package com.raqsoft.guide.web.dl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.raqsoft.common.Logger;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.logic.metadata.Dictionary;
import com.raqsoft.logic.metadata.Visibility;
import com.raqsoft.logic.util.IOUtil;
import com.raqsoft.report.usermodel.Context;

public class FileUtils {
	public static void getFileList(List<String> filelist, String strPath,
			String[] ends) {
		File dir = new File(strPath);
		File[] files = dir.listFiles(); // ���ļ�Ŀ¼���ļ�ȫ����������
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if (files[i].isDirectory()) { // �ж����ļ������ļ���
					getFileList(filelist, files[i].getPath(), ends); // ��ȡ�ļ�����·��
				} else { // �ж��ļ����Ƿ���.avi��β
					if (ends.length > 0) {
						boolean find = false;
						for (int z = 0; z < ends.length; z++) {
							if (fileName.toLowerCase().endsWith(ends[z])) {
								find = true;
								break;
							}
						}
						if (!find) continue;
					}
					String strFileName = files[i].getPath();
					// System.out.println("---" + strFileName);
					strFileName = strFileName.replaceAll("\\\\", "/").replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
					//2020.3.17�����ļ���
					if(!strFileName.contains("/temp/")) filelist.add(strFileName);
				}
			}

		}
	}


	public static boolean saveFile(String s, File file) throws Exception {
		if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(s.getBytes(Context.getJspCharset()));
			return true;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex2) {
				}
			}
		}
	}
	
	public static String getDict(String dict) throws Exception {
		//StringBuffer json = new StringBuffer();
		Dictionary d = IOUtil.readDictionary(null, dict);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IOUtil.writeDictionary(os, d);
		String s = new String(os.toByteArray(), "utf-8");
		return s;
	}

	
	public static String getVsb(String vsb) throws Exception {
		//StringBuffer json = new StringBuffer();
		Visibility d = IOUtil.readVisibility(null, vsb);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IOUtil.writeVisibility(os, d);
		String s = new String(os.toByteArray(), "utf-8");
		return s;
	}

    private static String MESSAGE = "";  
    /** 
     * ���Ƶ����ļ� 
     *  
     * @param srcFileName 
     *            �����Ƶ��ļ��� 
     * @param descFileName 
     *            Ŀ���ļ��� 
     * @param overlay 
     *            ���Ŀ���ļ����ڣ��Ƿ񸲸� 
     * @return ������Ƴɹ�����true�����򷵻�false 
     */  
    public static String copyFile(String srcFileName, String destFileName,  
            boolean overlay) {  
        File srcFile = new File(srcFileName);  
  
        // �ж�Դ�ļ��Ƿ����  
        if (!srcFile.exists()) {  
            return "Src file : " + srcFileName + " not exist";  
        } else if (!srcFile.isFile()) {  
            return "Copy file failed, not valid file : " + srcFileName;  
        }
  
        // �ж�Ŀ���ļ��Ƿ����  
        File destFile = new File(destFileName);  
		if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();
        if (destFile.exists()) {  
            // ���Ŀ���ļ����ڲ�������  
            if (overlay) {  
                // ɾ���Ѿ����ڵ�Ŀ���ļ�������Ŀ���ļ���Ŀ¼���ǵ����ļ�  
                new File(destFileName).delete();  
            }  
        } else {  
            // ���Ŀ���ļ�����Ŀ¼�����ڣ��򴴽�Ŀ¼  
            if (!destFile.getParentFile().exists()) {  
                // Ŀ���ļ�����Ŀ¼������  
                if (!destFile.getParentFile().mkdirs()) {  
                    // �����ļ�ʧ�ܣ�����Ŀ���ļ�����Ŀ¼ʧ��  
                    return "Copy file failed, can not make directory.";  
                }  
            }  
        }  
  
        // �����ļ�  
        int byteread = 0; // ��ȡ���ֽ���  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(srcFile);  
            out = new FileOutputStream(destFile);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            return "";  
        } catch (FileNotFoundException e) {  
            return "No src file!";  
        } catch (IOException e) {
            return "error IO!";  
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
    }  
  
	
	public static String readFile(File f) throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			return new String(b, com.raqsoft.report.usermodel.Context.getJspCharset());
		} catch (Exception e) {
			Logger.error(new String("read file error:[" + f.getPath() + "]"),e);
			e.printStackTrace();
		} finally {
			try {fis.close();} catch(Exception e) {};
		}
		return "";
	}

	public static int getFileRows(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 0;
			while ((tempString = reader.readLine()) != null) {
				line++;
			}
			reader.close();
			return line;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return 0;
	}	
	/** 
     * ��������Ŀ¼������ 
     *  
     * @param srcDirName 
     *            ������Ŀ¼��Ŀ¼�� 
     * @param destDirName 
     *            Ŀ��Ŀ¼�� 
     * @param overlay 
     *            ���Ŀ��Ŀ¼���ڣ��Ƿ񸲸� 
     * @return ������Ƴɹ�����true�����򷵻�false 
     */  
//    public static boolean copyDirectory(String srcDirName, String destDirName,  
//            boolean overlay) {  
//        // �ж�ԴĿ¼�Ƿ����  
//        File srcDir = new File(srcDirName);  
//        if (!srcDir.exists()) {  
//            MESSAGE = "����Ŀ¼ʧ�ܣ�ԴĿ¼" + srcDirName + "�����ڣ�";  
//            return false;  
//        } else if (!srcDir.isDirectory()) {  
//            MESSAGE = "����Ŀ¼ʧ�ܣ�" + srcDirName + "����Ŀ¼��";  
//            return false;  
//        }  
//  
//        // ���Ŀ��Ŀ¼���������ļ��ָ�����β��������ļ��ָ���  
//        if (!destDirName.endsWith(File.separator)) {  
//            destDirName = destDirName + File.separator;  
//        }  
//        File destDir = new File(destDirName);  
//        // ���Ŀ���ļ��д���  
//        if (destDir.exists()) {  
//            // �����������ɾ���Ѵ��ڵ�Ŀ��Ŀ¼  
//            if (overlay) {  
//                new File(destDirName).delete();  
//            } else {  
//                MESSAGE = "����Ŀ¼ʧ�ܣ�Ŀ��Ŀ¼" + destDirName + "�Ѵ��ڣ�";  
//                return false;  
//            }  
//        } else {  
//            // ����Ŀ��Ŀ¼  
//            System.out.println("Ŀ��Ŀ¼�����ڣ�׼������������");  
//            if (!destDir.mkdirs()) {  
//                System.out.println("����Ŀ¼ʧ�ܣ�����Ŀ��Ŀ¼ʧ�ܣ�");  
//                return false;  
//            }  
//        }  
//  
//        String flag = "";  
//        File[] files = srcDir.listFiles();  
//        for (int i = 0; i < files.length; i++) {  
//            // �����ļ�  
//            if (files[i].isFile()) {  
//                flag = FileUtils.copyFile(files[i].getAbsolutePath(),  
//                        destDirName + files[i].getName(), overlay);  
//                if (flag.length()>0)  
//                    break;  
//            } else if (files[i].isDirectory()) {  
//                flag = FileUtils.copyDirectory(files[i].getAbsolutePath(),  
//                        destDirName + files[i].getName(), overlay);  
//                if (flag.length()>0)  
//                    break;  
//            }  
//        }  
//        if (flag.length()>0) {  
//            MESSAGE = "����Ŀ¼" + srcDirName + "��" + destDirName + "ʧ�ܣ�";  
//            JOptionPane.showMessageDialog(null, MESSAGE);  
//            return false;  
//        } else {  
//            return true;  
//        }  
//    }
	
	public static void main(String argrs[]) throws Exception {
		List<String> fs = new ArrayList<String>();
		//getFileList(fs, "D:/data/workspace/guide/src",new String[]{"java"});//52---12710
		//getFileList(fs, "D:\\data\\workspace\\datalogic2\\src",new String[]{"java"});//390---110449
		//�������� 322---89671 175--43613
//		getFileList(fs, "D:\\data\\workspace\\datalogic2\\src\\com\\raqsoft\\logic\\ide",new String[]{"java"});//215---66836
//		getFileList(fs, "D:\\data\\workspace\\datalogic2\\src\\com\\raqsoft\\logic\\metadata",new String[]{"java"});
//		getFileList(fs, "D:\\data\\workspace\\datalogic2\\src\\com\\raqsoft\\logic\\parse",new String[]{"java"});
//		getFileList(fs, "D:\\data\\workspace\\datalogic2\\src\\com\\raqsoft\\logic\\util",new String[]{"java"});
//		
//		int rows = 0;
//		for (int i=0; i<fs.size(); i++) {
//			rows += getFileRows(fs.get(i));
//		}
//		System.out.println(fs.size() + "---" + rows);
		
		System.out.println(getDict("D:\\data\\workspace\\datalogic2\\services\\datalogic\\conf\\demo.dct"));
	}
	
	public static void cleanFolder(String dir) {
		String filePath = DataSphereServlet.getFilePath(dir);
		File f = new File(filePath);
		if(f.isDirectory()){
			File[] files = f.listFiles();
			for(File file: files){
				if(file.isFile()){
					file.delete();
				}
			}
		}
	}
}
