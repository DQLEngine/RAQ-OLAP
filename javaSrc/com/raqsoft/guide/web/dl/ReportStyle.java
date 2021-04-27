package com.raqsoft.guide.web.dl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.raqsoft.common.Logger;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.report.usermodel.INormalCell;
import com.raqsoft.report.usermodel.IReport;
import com.raqsoft.report.usermodel.Macro;
import com.raqsoft.report.usermodel.MacroMetaData;
import com.raqsoft.report.usermodel.Param;
import com.raqsoft.report.usermodel.ParamMetaData;
import com.raqsoft.report.util.ReportUtils;

public class ReportStyle {
//	1、左上合并格子
//	2、网格字段名格、分组格
//	3、总计格
//	4、测度标题n
//	5、值格n
	public static int TYPE_1 = 1;
	public static int TYPE_2 = 1;
	public static int TYPE_3 = 1;
	public static int TYPE_4 = 1;
	public static int TYPE_5 = 1;

	
//	rd.setLBColor(i,(short)j, -16763905); 
//	rd.setLBStyle(i,(short)j, INormalCell.LINE_SOLID); 
//	rd.setLBWidth(i,(short)j, (float) 0.75); 
//	inc.setBackColor(-10793923);
//	inc.setForeColor(-1);
//	inc.setHAlign(INormalCell.HALIGN_CENTER);
	public CellStyle c1 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-10793923,-1,INormalCell.HALIGN_CENTER);
	public CellStyle c2 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-10793923,-1,INormalCell.HALIGN_CENTER);
	public CellStyle c3 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-10793923,-1,INormalCell.HALIGN_CENTER);
	public ArrayList<CellStyle> c4 = new ArrayList<CellStyle>();
	public ArrayList<CellStyle> c5 = new ArrayList<CellStyle>();
	
	public float width = 25; 
	public float height = 8; 
	public int borderColor = -16763905;
	public byte borderStyle = INormalCell.LINE_SOLID;
	public float boderWidth = (float) 0.75;

	public ReportStyle() {
	}
	
	public static String getStyleJson(String styleRpx) {
		String json = "";
		try {
			if (!new File(styleRpx).exists()) return "[]"; 
			IReport ir = ReportUtils.read(styleRpx);
			json += "[";
			for (int i=1; i<=ir.getRowCount(); i++) {
				for (int j=1; j<=ir.getColCount(); j++) {
					INormalCell c = ir.getCell(i, j);
					Object n = c.getValue();
					if (n == null || n.toString().length()==0) continue;
					if (json.length()>1) json += ",";
					json += "{\"name\":\""+n.toString()+"\",\"backColor\":"+c.getBackColor()+",\"color\":"+c.getForeColor()+",\"hAlign\":"+c.getHAlign()+"}";
				}
			}
			json += "]";
		} catch (Exception e) {
			Logger.warn("",e);
			return "[]";
			//e.printStackTrace();
		}		
		return json;
	}
	
	public static String[] styles = null;
	public static void setStyles(String s){
//		try {
//			
//			IReport ir = null;//ReportUtils.read(DataSphereServlet.ROOT_PATH + s);
//			//INormalCell cell = ir.getCell(3, 1);
//			//Logger.debug("3 1 : " + cell.getValue());
//			String ri = "";
//			if (ir.getColCount()>=6) {
//				for (int i=3; i<=ir.getRowCount(); i++) {
//					if (i%2==1) {
//						if (ri.length()>0) ri += ":";
//						String r = "";
//						Object name = ir.getCell(i, 1).getValue();
//						if (name == null || name.toString().trim().length() == 0) continue;
//						r = name.toString();
//						INormalCell border = ir.getCell(i, 2);
//						r += "," + border.getBBColor();
//						r += "," + border.getBBStyle();
//						r += "," + border.getBBWidth();
//						INormalCell title = ir.getCell(i, 4);
//						r += "," + ir.getRowCell(i).getRowHeight();
//						
//						//r += "," + ir.getColCell(4).getColWidth();
//						//";"
//						
//						String r2 = "" + title.getBackColor();
//						r2 += "," + title.getForeColor();
//						r2 += "," + title.getHAlign();
//						//width = ir.getColCell(4).getColWidth();
//						for (int j=1; j<=ir.getColCount(); j++) {
//							INormalCell inc = ir.getCell(i, j);
//							if ("列宽".equals(inc.getValue())) {
//								r += "," + ir.getColCell(j).getColWidth();
//							}
//							if (j>=6 && j%2 == 0) {
//								Object v = inc.getValue();
//								if (v == null || v.toString().trim().length() == 0) continue;
//								r2 += ";" + inc.getBackColor();
//								r2 += "," + inc.getForeColor();
//								r2 += "," + inc.getHAlign();
//							}
//						}
//						ri += r + ";" + r2;
//					}
//				}
//			}
//			s = ri;
//		} catch (Exception e) {
//			Logger.warn("",e);
//			//e.printStackTrace();
//		}
//		//样式模板名称,边框颜色,边框样式,边框线宽,行高,列宽;标题及维背景色,标题及维前景色,标题及维水平对齐方式;指标1背景色,指标1前景色,指标1水平对齐方式;...指标n背景色,指标n前景色,指标n水平对齐方式:另一组样式模板定义
//		//s = "朴素,-16763905,83,0.75;-10793923,-1,-47;-1,-16777216,-47;-1250068,-16777216,-47;-2302756,-16777216,-47;-3355444,-16777216,-47;-4408132,-16777216,-47
//		//:彩色,-1,83,0.75;-9127214,-1,-47;-3286789,-16777216,-47;-5981697,-16777216,-47";
//		if (s != null && s.length()>0) {
//			styles = s.split(":");
//		}
	}
	
	public static String getStyles() {
		ArrayList<String> list = new ArrayList<String>();
		//list.add("style1");
		//list.add("style2");
		//list.add("style3");
		if (styles != null) {
			for (int i=0; i<styles.length; i++) {
				String n = styles[i].substring(0,styles[i].indexOf(","));
				if (list.indexOf(n)==-1) {
					list.add(n);
				}
			}
		}
		String ss = "";
		for (int i=0; i<list.size(); i++) {
			if (i>0) ss += ",";
			ss += list.get(i).toString();
		}
		Iterator iter = templates.keySet().iterator();
		while (iter.hasNext()) {
			if (ss.length()>0) ss += ",";
			String name = iter.next().toString();
			IReport temp = templates.get(name);
			String disc = null;//temp.getCell(1, 1).getNotes();
			MacroMetaData mmd = temp.getMacroMetaData();
			ParamMetaData pmd = temp.getParamMetaData();
			if (mmd != null && mmd.getMacroCount()>0) {
				for (int i=0; i<mmd.getMacroCount(); i++) {
					Macro m = mmd.getMacro(i);
					if (disc == null) disc = m.getMacroName();
					else disc += ";" + m.getMacroName();
				}
			} else if (pmd != null && pmd.getParamCount()>0) {
				for (int i=0; i<pmd.getParamCount(); i++) {
					Param m = pmd.getParam(i);
					if (disc == null) disc = m.getParamName();
					else disc += ";" + m.getParamName();
				}
			} else disc = temp.getCell(1, 1).getNotes();
			if (disc == null) disc = "";
			disc.replaceAll(",", "");
			ss += name+";"+disc;
		}
		return ss;
	}
//
//	public static void setTemplates(String folder) {
//		File f = new File(DataSphereServlet.ROOT_PATH + folder);
//		if (!f.exists() || !f.isDirectory()) return;
//		File[] fs = f.listFiles();
//		if (fs == null) return;
//		for (int i=0; i<fs.length; i++) {
//			File fi = fs[i];
//			if (!fi.getPath().endsWith(".rpx")) continue;
//			String name = fi.getName().replace(".rpx", "");
//			try {
//				IReport temp = ReportUtils.read(fi.getPath());
//				String disc = temp.getCell(1, 1).getNotes();
//				templates.put(name, temp);
//			} catch (Exception e) {
//				Logger.warn("read rpx error");
//				//e.printStackTrace();
//			}
//		}
//	}
	
	public static HashMap<String,IReport> templates = new HashMap<String,IReport>();

	public static IReport getTemplate(String name) {
		return templates.get(name);
	}

	public ReportStyle(String name) {
		if (styles != null) {
			for (int i=0; i<styles.length; i++) {
				if (styles[i].startsWith(name)) {
					String[] items = styles[i].split(";");
					String[] i0 = items[0].split(",");
					borderColor = Integer.parseInt(i0[1]);
					borderStyle = Byte.parseByte(i0[2]);
					boderWidth = Float.parseFloat(i0[3]);
					if (i0.length>4) height = Float.parseFloat(i0[4]);
					if (i0.length>5) width = Float.parseFloat(i0[5]);
					
					String[] i1 = items[1].split(",");
					c1 = new CellStyle(borderColor,borderStyle,boderWidth,Integer.parseInt(i1[0]),Integer.parseInt(i1[1]),Byte.parseByte(i1[2]));
					c2 = new CellStyle(borderColor,borderStyle,boderWidth,Integer.parseInt(i1[0]),Integer.parseInt(i1[1]),Byte.parseByte(i1[2]));
					c3 = new CellStyle(borderColor,borderStyle,boderWidth,Integer.parseInt(i1[0]),Integer.parseInt(i1[1]),Byte.parseByte(i1[2]));

					for (int j=2; j<items.length; j++) {
						String[] ij = items[j].split(",");
						c4.add(new CellStyle(borderColor,borderStyle,boderWidth,Integer.parseInt(ij[0]),Integer.parseInt(ij[1]),Byte.parseByte(ij[2])));
						c5.add(new CellStyle(borderColor,borderStyle,boderWidth,Integer.parseInt(ij[0]),Integer.parseInt(ij[1]),Byte.parseByte(ij[2])));
					}
					return;
				}
			}
		}
		if ("style1".equalsIgnoreCase(name)) {
			borderColor = -16763905;
			borderStyle = INormalCell.LINE_SOLID;
			boderWidth = (float) 0.75;

			c1 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-10793923,-1,INormalCell.HALIGN_CENTER);
			c2 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-10793923,-1,INormalCell.HALIGN_CENTER);
			c3 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-10793923,-1,INormalCell.HALIGN_CENTER);
			c4.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-10793923,-1,INormalCell.HALIGN_CENTER));
			c5.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-1,-10793923,INormalCell.HALIGN_LEFT));
		} else if ("style2".equalsIgnoreCase(name)) {
			borderColor = -10066330;
			borderStyle = INormalCell.LINE_SOLID;
			boderWidth = (float) 0.75;
			
			c1 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-10793923,-1,INormalCell.HALIGN_CENTER);
			c2 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-10793923,-1,INormalCell.HALIGN_CENTER);
			c3 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-10793923,-1,INormalCell.HALIGN_CENTER);
			c4.clear();
			c5.clear();
			c4.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-6710785,-3407821,INormalCell.HALIGN_CENTER));
			c5.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-6710785,-3407821,INormalCell.HALIGN_LEFT));
			c4.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-26215,-16724992,INormalCell.HALIGN_CENTER));
			c5.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-26215,-16724992,INormalCell.HALIGN_LEFT));
		} else if ("style3".equalsIgnoreCase(name)) {
			borderColor = -10066330;
			borderStyle = INormalCell.LINE_SOLID;
			boderWidth = (float) 0.75;

			c1 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-16777216,-1,INormalCell.HALIGN_CENTER);
			c2 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-16777216,-1,INormalCell.HALIGN_CENTER);
			c3 = new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-16777216,-1,INormalCell.HALIGN_CENTER);
			c4.clear();
			c5.clear();
			c4.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-1,-16777216,INormalCell.HALIGN_CENTER));
			c4.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-1250068,-16777216,INormalCell.HALIGN_CENTER));
			c4.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-2302756,-16777216,INormalCell.HALIGN_CENTER));
			c4.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-3355444,-16777216,INormalCell.HALIGN_CENTER));
			c4.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-4408132,-16777216,INormalCell.HALIGN_CENTER));
			c5.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-1,-16777216,INormalCell.HALIGN_CENTER));
			c5.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-1250068,-16777216,INormalCell.HALIGN_CENTER));
			c5.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-2302756,-16777216,INormalCell.HALIGN_CENTER));
			c5.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-3355444,-16777216,INormalCell.HALIGN_CENTER));
			c5.add(new CellStyle(-16763905,INormalCell.LINE_SOLID,(float) 0.75,-4408132,-16777216,INormalCell.HALIGN_CENTER));
		} else if ("style4".equalsIgnoreCase(name)) {
			
		}
	} 
	
//	public void setStyle(int type, int subType, int borderColor, byte borderStyle, float boderWidth, int backColor, int foreColor, byte hAlian){
//		
//	}
}

class CellStyle {
	public int borderColor;
	public byte borderStyle;
	public float boderWidth;
	public int backColor;
	public int foreColor;
	public byte hAlian;
	
	public CellStyle(int borderColor, byte borderStyle, float boderWidth, int backColor, int foreColor, byte hAlian) {
		this.borderColor = borderColor;
		this.borderStyle = borderStyle;
		this.boderWidth = boderWidth;
		this.backColor = backColor;
		this.foreColor = foreColor;
		this.hAlian = hAlian;
	}
}