package com.raqsoft.guide.web.dl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.raqsoft.common.Logger;
import com.raqsoft.guide.web.DataSphereServlet;

public class DfxDataSetManager {
	
	private static ArrayList<String> ids = new ArrayList<String>();
	private static ArrayList<String> files = new ArrayList<String>();
	//private static ArrayList<String> ids = new ArrayList<String>();
	
	public static void add(String id, String file) {
		if (addOnly(id, file)) save();
	}
	
	public static String getFile(String id) {
		int idx = ids.indexOf(id);
		if (idx >= 0) return DataSphereServlet.DATASET_FILE_HOME + "/" + files.get(idx);
		else return null;
	}

	public static boolean addOnly(String id, String file) {
		if (ids.indexOf(id)>=0) return false; 
		ids.add(id);
		files.add(file);
		return true;
	}

	public static void save() {
		File f = new File(DataSphereServlet.DATASET_CONFIG);
		//if (!f.exists()) return;
		StringBuffer sb = new StringBuffer();
		boolean win = System.getProperties().getProperty("os.name").toLowerCase().indexOf("win")>=0;
		String rn = win?"\r\n":"\n";
		for (int i=0; i<ids.size(); i++) {
			sb.append(ids.get(i)+rn);
			sb.append(files.get(i)+rn);
			sb.append(rn);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			fos.write(sb.toString().getBytes());
		} catch (Exception e) {
			Logger.error("",e);
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex2) {
				}
			}
		}
	}

	
	public static void load() {
		File f = new File(DataSphereServlet.DATASET_CONFIG);
		if (!f.exists()) return;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			BufferedReader dr=new BufferedReader(new InputStreamReader(fis));
			String line = dr.readLine();
			int count = 0;
			String id = "";
			String file = "";
			while (line != null) {
				if (line.length() == 0) {
					addOnly(id, file);
					count = 0;
				} else {
					if (count == 0) id = line;
					else if (count == 1) file = line;
					count++;
				}
				line = dr.readLine();
			}
		} catch (Exception e) {
			Logger.error("",e);
			e.printStackTrace();
		} finally {
			try {fis.close();} catch(Exception e) {};
		}
	}
}



