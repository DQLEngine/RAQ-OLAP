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
		File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if (files[i].isDirectory()) { // 判断是文件还是文件夹
					getFileList(filelist, files[i].getPath(), ends); // 获取文件绝对路径
				} else { // 判断文件名是否以.avi结尾
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
					//2020.3.17屏蔽文件夹
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
     * 复制单个文件 
     *  
     * @param srcFileName 
     *            待复制的文件名 
     * @param descFileName 
     *            目标文件名 
     * @param overlay 
     *            如果目标文件存在，是否覆盖 
     * @return 如果复制成功返回true，否则返回false 
     */  
    public static String copyFile(String srcFileName, String destFileName,  
            boolean overlay) {  
        File srcFile = new File(srcFileName);  
  
        // 判断源文件是否存在  
        if (!srcFile.exists()) {  
            return "Src file : " + srcFileName + " not exist";  
        } else if (!srcFile.isFile()) {  
            return "Copy file failed, not valid file : " + srcFileName;  
        }
  
        // 判断目标文件是否存在  
        File destFile = new File(destFileName);  
		if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();
        if (destFile.exists()) {  
            // 如果目标文件存在并允许覆盖  
            if (overlay) {  
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件  
                new File(destFileName).delete();  
            }  
        } else {  
            // 如果目标文件所在目录不存在，则创建目录  
            if (!destFile.getParentFile().exists()) {  
                // 目标文件所在目录不存在  
                if (!destFile.getParentFile().mkdirs()) {  
                    // 复制文件失败：创建目标文件所在目录失败  
                    return "Copy file failed, can not make directory.";  
                }  
            }  
        }  
  
        // 复制文件  
        int byteread = 0; // 读取的字节数  
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
     * 复制整个目录的内容 
     *  
     * @param srcDirName 
     *            待复制目录的目录名 
     * @param destDirName 
     *            目标目录名 
     * @param overlay 
     *            如果目标目录存在，是否覆盖 
     * @return 如果复制成功返回true，否则返回false 
     */  
//    public static boolean copyDirectory(String srcDirName, String destDirName,  
//            boolean overlay) {  
//        // 判断源目录是否存在  
//        File srcDir = new File(srcDirName);  
//        if (!srcDir.exists()) {  
//            MESSAGE = "复制目录失败：源目录" + srcDirName + "不存在！";  
//            return false;  
//        } else if (!srcDir.isDirectory()) {  
//            MESSAGE = "复制目录失败：" + srcDirName + "不是目录！";  
//            return false;  
//        }  
//  
//        // 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符  
//        if (!destDirName.endsWith(File.separator)) {  
//            destDirName = destDirName + File.separator;  
//        }  
//        File destDir = new File(destDirName);  
//        // 如果目标文件夹存在  
//        if (destDir.exists()) {  
//            // 如果允许覆盖则删除已存在的目标目录  
//            if (overlay) {  
//                new File(destDirName).delete();  
//            } else {  
//                MESSAGE = "复制目录失败：目的目录" + destDirName + "已存在！";  
//                return false;  
//            }  
//        } else {  
//            // 创建目的目录  
//            System.out.println("目的目录不存在，准备创建。。。");  
//            if (!destDir.mkdirs()) {  
//                System.out.println("复制目录失败：创建目的目录失败！");  
//                return false;  
//            }  
//        }  
//  
//        String flag = "";  
//        File[] files = srcDir.listFiles();  
//        for (int i = 0; i < files.length; i++) {  
//            // 复制文件  
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
//            MESSAGE = "复制目录" + srcDirName + "至" + destDirName + "失败！";  
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
		//以下四行 322---89671 175--43613
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
