package com.raqsoft.guide.web.dl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.raqsoft.common.Logger;
import com.raqsoft.dm.Env;
import com.raqsoft.guide.Consts;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.report.usermodel.Context;

public class SaveUtil {
	public static String getFiles(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ServletContext application = session.getServletContext();
		String result = "";
		File folder = new File(Consts.getAbsPath("", ""));
		if (folder.exists()) {
			File[] children = folder.listFiles();
			for (int i=0; i<children.length; i++) {
				if (children[i].getPath().endsWith(".qyx")) {
					if (result.length() == 0)
						result += children[i].getName().substring(0, children[i].getName().length()-4);
					else
						result += "," + children[i].getName().substring(0, children[i].getName().length()-4);
				}
			}
		}
		return result;
	}

	public static String getFiles(HttpServletRequest request, String end) {
		HttpSession session = request.getSession();
		ServletContext application = session.getServletContext();
		String result = "";
		File folder = new File(Consts.getAbsPath("", ""));
		if (folder.exists()) {
			File[] children = folder.listFiles();
			for (int i=0; i<children.length; i++) {
				if (children[i].getPath().endsWith(end)) {
					if (result.length() == 0)
						result += children[i].getName().substring(0, children[i].getName().length()-4);
					else
						result += "," + children[i].getName().substring(0, children[i].getName().length()-4);
				}
			}
		}
		return result;
	}

	public static boolean save(HttpServletRequest request, String name) throws Exception {
		HttpSession session = request.getSession();
		ServletContext application = session.getServletContext();
		File folder = new File(application.getRealPath(getCurrUserPath(session)));
		folder.mkdirs();
		return save(request, new File(folder.getPath() + File.separator + name + ".qyx"));
	}

	public static boolean save(HttpServletRequest request, File file) throws Exception {
		String type = request.getParameter("type");
		String pageId = request.getParameter("pageId");
		if ("qyx".equals(type)) {
			if (!file.getName().toLowerCase().endsWith(".q_y_x") && !file.getName().toLowerCase().endsWith(".qyx")){
				file = new File(file.getPath() + ".qyx");
			}
			file.getParentFile().mkdirs();

			String dlConf = request.getParameter("dlConf");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				fos.write(dlConf.getBytes(Context.getJspCharset()));
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
		return true;
	}

	public static void remove(HttpServletRequest request, String name) {
		HttpSession session = request.getSession();
		ServletContext application = session.getServletContext();
		File folder = new File(application.getRealPath(getCurrUserPath(session)));
		try {
			File f = new File(folder.getPath() + File.separator + name + ".qyx");
			Logger.debug("[" + f.getPath() + "]delete[" + f.delete() + "]");
		} catch (Exception e) {
			Logger.error("",e);
			e.printStackTrace();
		}
	}

	public static String open(String qyx) throws Exception {
		Object user = "";
		String path = null;
		File f = null;
		if (user != null) {
			path = Consts.getAbsPath(user.toString(), qyx);
			f = new File(path);
		}
		if (!f.exists()) {
			Logger.warn(new String("file not exist:[" + qyx + "]"));
			return "";
		}
//		if (f==null || !f.exists()) {
//			File folder = new File(application.getRealPath(getCurrUserPath(session)));
//			f = new File(folder.getPath() + File.separator + request.getParameter("name") + ".qyx");
//		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			return new String(b, Context.getJspCharset());
		} catch (Exception e) {
			Logger.error(new String("read file error:[" + qyx + "]"),e);
			e.printStackTrace();
		} finally {
			try {fis.close();} catch(Exception e) {};
			
		}
		return "";
	}
	
	public static String readFile(File f) throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			return new String(b, Context.getJspCharset());
		} catch (Exception e) {
			Logger.error(new String("read file error:[" + f.getPath() + "]"),e);
			e.printStackTrace();
		} finally {
			try {fis.close();} catch(Exception e) {};
		}
		return "";
	}

	public static String[] readFields(String txtFile) throws Exception {
		File f = new File(txtFile);
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			BufferedReader dr=new BufferedReader(new InputStreamReader(fis));
			String firstLine = dr.readLine();
			return firstLine.split("\t");
		} catch (Exception e) {
			Logger.error("",e);
			e.printStackTrace();
		} finally {
			try {fis.close();} catch(Exception e) {};
			
		}
		return null;
	}

	public static String getCurrUserPath(HttpSession session) {
		if (DLServlet.filePathInSession == null) return "/files/";
		Object o = session.getAttribute(DLServlet.filePathInSession);
		return o == null ? "/files/" : o.toString();
	}
	
	
	
	public static int getNewGexIndex(HttpSession session) {
		Object obj = session.getAttribute("gexIndex");
		int idx = 1;
		if (obj != null) {
			idx = ((Integer)obj).intValue() + 1;
		}
		session.setAttribute("gexIndex", new Integer(idx));
		return idx;
	}
	
	public static void main(String args[]) {
//		FileInputStream fis = null;
//		try {
//			fis = new FileInputStream("d:/temp/treemap.json");
//			byte[] b = new byte[fis.available()];
//			fis.read(b);
//			System.out.println("----");
//			String s = new String(b).replaceAll("\r\n", "");
//			System.out.println(s.length());
//			System.out.println("----");
//			FileOutputStream fos = null;
//			try {
//				fos = new FileOutputStream("d:/temp/treemap2.json");
//				fos.write(s.getBytes());
//			} finally {
//				if (fos != null) {
//					try {
//						fos.close();
//					} catch (IOException ex2) {
//					}
//				}
//			}
//		} catch (Exception e) {
//			Logger.error("",e);
//			e.printStackTrace();
//		} finally {
//			try {fis.close();} catch(Exception e) {};
//			
//		}
		double d = 12.12345645642342356;
		System.out.println(d);
	}

}
