package com.raqsoft.guide.web.dl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.raqsoft.cellset.datamodel.PgmCellSet;
import com.raqsoft.common.Logger;
import com.raqsoft.common.StringUtils;
import com.raqsoft.common.Types;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.JobSpace;
import com.raqsoft.dm.JobSpaceManager;
import com.raqsoft.dm.Param;
import com.raqsoft.dm.Record;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.Table;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.report.cache.CacheManager;
import com.raqsoft.report.model.ReportDefine;
import com.raqsoft.report.usermodel.BuiltinDataSetConfig;
import com.raqsoft.report.usermodel.EsProcDataSetConfig;
import com.raqsoft.report.usermodel.IReport;
import com.raqsoft.util.CellSetUtil;
import com.raqsoft.util.Variant;

public class DfxData {
	private String file;
	private Object guideResult;
	private IReport iReport;
	private ReportDefine reportDefine;
	private String reportId;
	private BuiltinDataSetConfig bdsc = null;
	private EsProcDataSetConfig edsc = null;
	private Object dc = null; //data cursor
	public Object getDc() {
		return dc;
	}

	public void setDc(Object dc) {
		this.dc = dc;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public IReport getIReport() {
		return iReport;
	}
	
	public void setIReport(IReport iReport) {
		if (this.iReport != null) {
			this.iReport = null;
			CacheManager manager = CacheManager.getInstance();
			manager.deleteReportEntry(reportId);
//			Iterator iter = manager.getReportEntries().keySet().iterator();
//			while (iter.hasNext()) {
//				Logger.debug("----" + iter.next());
//			}
		}
		//Logger.debug("----setIReport");
		this.iReport = iReport;
	}

	public ReportDefine getReportDefine() {
		return reportDefine;
	}

	public void setReportDefine(ReportDefine reportDefine) {
		this.reportDefine = reportDefine;
	}

	public Object getGuideResult() {
		return guideResult;
	}
	private Table t = null;
	public Table getTable() {
		return t;
	}
	public static int getReportSize(BuiltinDataSetConfig sc, String lefts, String tops) {
		Table t = new Table(sc.getColNames());
		String[][] vs = sc.getValues();
		for (int i=0; i< vs.length; i++) {
			Sequence seq = new Sequence(vs[i]);
			t.record(0, seq, null);
		}
		return getReportSize(t,lefts,tops);
	}
	public static int getReportSize(Table t, String lefts, String tops) {
		int left=1,top=1;
		if (lefts.length() > 0) {
			try {
				Context c = new Context();
				c.setParamValue("t", t);
				c.setParamValue("eval1", "v1=t.group("+lefts+").len()");
				DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", c);
//				DfxUtils.execDfxScript("result t.group("+lefts+").len()", c, true);
				Object o = c.getParam("v1").getValue();
				Logger.debug("left size : " + o);
				if (o instanceof Integer) left = (Integer)o;
				c = null;
			} catch (Exception e) {
			}			
		}
		if (tops.length() > 0) {
			try {
				Context c = new Context();
				c.setParamValue("t", t);
				c.setParamValue("eval1", "v1=t.group("+tops+").len()");
				DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", c);
//				DfxUtils.execDfxScript("result t.group("+tops+").len()", c, true);
				Object o = c.getParam("v1").getValue();
				Logger.debug("top size : " + o);
				if (o instanceof Integer) top = (Integer)o;
				c = null;
			} catch (Exception e) {
			}			
		}
		return left*top;
	}
	
	public BuiltinDataSetConfig getDs() {
		return bdsc;
	}
	
	public BuiltinDataSetConfig getDs(String[] fields) {
		ArrayList<String> fs = new ArrayList<String>();
		for (int i=0; i<fields.length; i++) fs.add(fields[i].split("<,>")[1]); 
		BuiltinDataSetConfig bdsc2 = new BuiltinDataSetConfig();
		String names[] = new String[bdsc.getColNames().length];
		for (int i=0; i<names.length; i++) {
			//names[i] = "f1";
			int idx = fs.indexOf(bdsc.getColNames()[i])+1;
			if (idx == 0) throw new RuntimeException("");
			names[i] = "f"+idx;
		}
		bdsc2.setColNames(names);
		bdsc2.setColTypes(bdsc.getColType());
		bdsc2.setValues(bdsc.getValues());
		bdsc2.setName("ds1");
		return bdsc2;
	}

	public boolean isFinish() {
		return finish;
	}

	private boolean finish = false;
	
	public DfxData(String file)  {
		//Logger.debug(getTableInfo("D:/data/workspace/guide/web/WEB-INF/tmp/order"));
		this.file = file;
	}
	
	public DfxData(BuiltinDataSetConfig bdsc) {
		this.bdsc = bdsc;
	}

	public String calc(String calcs, String filters, String fields, String resultExp, String cacheType, String types,String dataFileType, int maxDataSize, String isGlmd,String srcTypes, String aggrFieldFilters,boolean isQuery) throws Exception {
		Object[] os = new Object[2];
		String jsId = "jsId" + System.currentTimeMillis();
		JobSpace space = JobSpaceManager.getSpace(jsId);
		try{
			String dfx = DataSphereServlet.DFX_REPORT;
			//if (!"binary".equalsIgnoreCase(dataFileType)) dfx = DataSphereServlet.DFX_REPORT_TXT;
			//dfx = "D:\\data\\workspace\\guide\\web\\WEB-INF\\dfx\\report.dfx";
			PgmCellSet pcs = CellSetUtil.readPgmCellSet(DataSphereServlet.class.getResourceAsStream(dfx));
			Context ctx = pcs.getContext();
			//ParamList pl = ctx.getParamList();
			ctx.setParamValue("dataFile", file);
			Sequence s1 = getSeq(calcs);
			Sequence s2 = getSeq(filters);
			Sequence s3 = getSeq(fields);
			Sequence s4 = getSeq(aggrFieldFilters);
			int len = s1.length();
			if (len<s2.length()) len = s2.length();
			if (len<s3.length()) len = s3.length();
			for (int i=0; i<len-s1.length(); i++) s1.add("");
			for (int i=0; i<len-s2.length(); i++) s2.add("");
			for (int i=0; i<len-s3.length(); i++) s3.add("");
			ctx.setParamValue("calcs", s1);
			ctx.setParamValue("filters", s2);
			ctx.setParamValue("fields", s3);
			ctx.setParamValue("aggrFieldFilters", s4);
			ctx.setParamValue("resultExp", resultExp);
			ctx.setParamValue("dataFileType", dataFileType);
			ctx.setParamValue("maxDataSize", maxDataSize);
			String esProcTypes = "";
			boolean glmd = "true".equals(isGlmd);
			if (StringUtils.isValidString(srcTypes)) {
				if(glmd) esProcTypes = srcTypes.replaceAll(":91", "datetime").replaceAll(":92", "time").replaceAll(":16", ":bool").replaceAll(":12", ":string").replaceAll(":10", ":datetime").replaceAll(":9", ":time").replaceAll(":11", ":string").replaceAll(":-1", ":string").replaceAll(":1", ":decimal")
						.replaceAll(":-7", ":decimal").replaceAll(":-6", ":decimal").replaceAll(":-5", ":decimal").replaceAll(":-2", ":decimal").replaceAll(":-3", ":decimal").replaceAll(":-4", ":decimal")
						.replaceAll(":5", ":decimal").replaceAll(":6", ":decimal").replaceAll(":7", ":decimal").replaceAll(":8", ":decimal").replaceAll(":2", ":decimal").replaceAll(":4", ":int").replace(":3", ":decimal");
				else esProcTypes = srcTypes.replaceAll(":0", ":string").replaceAll(":1", ":decimal").replaceAll(":2", ":string").replaceAll(":3", ":date").replaceAll(":4", ":time").replaceAll(":5", ":datetime");//.replaceAll(":93", "timestamp");
				if(glmd){
					if(esProcTypes.indexOf(":0") > 0){
						esProcTypes = esProcTypes.replaceAll(":0", ":string");
					}
				}
			}
			//esProcTypes="";
			ctx.setParamValue("types", esProcTypes);
			ctx.setJobSpace(space);
			Object o = pcs.execute();
			Param p1 = ctx.getParam("guideResult");
			Param p2 = ctx.getParam("finish");
			os[0] = p1.getValue();
			os[1] = p2.getValue();
			finish = "1".equals(os[1]);
			if ("1".equals(cacheType)) {
				Sequence seq = (Sequence)os[0];
				StringBuffer sb = new StringBuffer();
				sb.append("[");
				for (int i=1; i<=seq.length(); i++) {
					if (seq.get(i) == null || "null".equals(seq.get(i)) || "".equals(seq.get(i))) continue;
					if (sb.length()>1) sb.append(",");
					sb.append(DfxUtils.toString(seq.get(i)));
				}
				sb.append("]");
				//String s = seq.toString(",", "q");
				//if (s.indexOf("-")>=0) s = s.replaceAll(",", "\",\"");
				return sb.toString();
			} else if ("2".equals(cacheType)) {
				Object oo = os[0];
//				if (oo instanceof Table) {
//					bdsc = new BuiltinDataSetConfig();
//					bdsc.setName("ds1");
//					t = (Table)oo;
//					String[] names = t.dataStruct().getFieldNames();
//					//t.dataStruct().g
//					Logger.debug(t);
//					if (t.length() == 0) throw new Exception("No qualified data");
//					byte colTypes[] = new byte[names.length];
//					String ss3[] = types.split(",");
//					ArrayList<String> ss1 = new ArrayList<String>();
//					ArrayList<Byte> ss2 = new ArrayList<Byte>();
//					for (int i=0; i<ss3.length; i++) {
//						String[] ssi = ss3[i].split(":");
//						ss1.add(ssi[0]);
//						if(glmd && isQuery) ss2.add(Byte.parseByte(ssi[1]));
//						else ss2.add(getType(ssi[1]));
//					}
//					for (int i=0; i<names.length; i++) {
//						int pos = ss1.indexOf(names[i]);
//						byte ti = Types.DT_STRING;
//						if (pos >= 0) ti = ss2.get(pos);
//						colTypes[i] = ti;
//					}
//					
//					//TODO
//					//for (int i=0; i<colTypes.length; i++) colTypes[i] = Types.DT_STRING;
//					bdsc.setColTypes(colTypes);
//					bdsc.setColNames(names);
//					String[][] ss = new String[maxDataSize>t.length()?t.length():maxDataSize][names.length];
//					for (int i=0; i<ss.length; i++) {
//						Record r = t.getRecord(i+1);
//						for (int j=0; j<names.length; j++) {
//							Object oij = r.getFieldValue(names[j]);
//							ss[i][j] = oij==null?null:oij.toString().replaceAll("\t", " ").replaceAll("\r\n", " ").replaceAll("\n", " ");
//						}
//					}
//					bdsc.setValues(ss);
//					//bdsc.setValues()
//				} else 
				if (oo instanceof com.raqsoft.dm.cursor.ICursor) {
					this.dc = os[0];
//					edsc = new EsProcDataSetConfig();
//					edsc.setDataSourceName("ds1");
//					edsc.setDfxFileName("analyseReportDS.dfx");
//					List<String> names = new ArrayList<String>();
//					List<String> exps = new ArrayList<String>();
//					names.add("dataCursor");
//					exps.add("@dataCursor");
//					edsc.setParamNames(names);
//					edsc.setParamExps(exps);
					
					
				} else throw new Exception("没有符合条件的数据");
				return "{finish:"+os[1]+"}";
			}
//		}catch(Exception e){
//			e.printStackTrace();
//			Logger.warn(e);
//			return "error:" + e.getMessage();
		}finally{
			JobSpaceManager.closeSpace(jsId);
		}
		return "{info:'ok'}";
	}
	
	public static byte getType(String t) {
		int ti = Integer.parseInt(t);
		if (ti == 1) {
			return Types.DT_DECIMAL;
		} else if (ti == 2) {
			return Types.DT_STRING;
		} else if (ti == 3) {
			return Types.DT_DATE;
		} else if (ti == 4) {
			return Types.DT_TIME;
		} else if (ti == 5) {
			return Types.DT_DATETIME;
		}
		return Types.DT_STRING;
	}
	
	public static String getEsprocType(String t) {
		int ti = Integer.parseInt(t);
		if (ti == 1) {
			return "decimal";
		} else if (ti == 2) {
			return "string";
		} else if (ti == 3) {
			return "time";
		} else if (ti == 4) {
			return "time";
		} else if (ti == 5) {
			return "datetime";
		}
		return "string";
	}
	
	
	public static String getTableInfo(String dataFile, String dataFileType,int scanRow, boolean isGlmd) throws Exception {
		Context ctx = new Context();
		ctx.setParamValue("dataFile", dataFile);
//		if ("text".equals(dataFileType)) DfxUtils.execDfxScript("return file(dataFile).import@t()", ctx);
//		else DfxUtils.execDfxScript("=file(dataFile).cursor@b()\t=A1.fetch(1)\t=A1.close()\treturn B1", ctx);
		if ("text".equals(dataFileType)) {
			ctx.setParamValue("eval1", "file(dataFile).import@qt()");
			ctx.setParamValue("eval2", "v1=B1");
			DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx);
		} else {
			ctx.setParamValue("eval1", "file(dataFile).cursor@b()");
			ctx.setParamValue("eval2", "v1=B1.fetch("+scanRow+")");
			ctx.setParamValue("eval3", "B1.close()");
			DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx);
		}
		Param p = ctx.getParam("v1");
		if (p == null || p.getValue() == null || !(p.getValue() instanceof Table)) throw new Exception("can load data from : " + dataFile);
		Table t = (Table)p.getValue();
		String names[] = t.dataStruct().getFieldNames();
		if (names==null||names.length==0) throw new RuntimeException("can not load data from : " + dataFile);
		return getTableInfo(t,scanRow, isGlmd);
	}
	
	public static String getTableInfo(Table t, int scanRow, boolean isGlmd) throws Exception {
		String struct = "";
		String names[] = t.dataStruct().getFieldNames();
		Logger.debug("scanRow : " + scanRow);
		Logger.debug("scan table : " + t);
		byte types[] = new byte[names.length];
		for (int j=1; j<=t.length(); j++) {
			Record r = t.getRecord(j);
			for (int i=0; i<names.length; i++) {
				Object oi = r.getFieldValue(names[i]);
				if (oi != null) {
					byte b = Variant.getObjectType(oi);
					//System.out.println(b+"----"+oi);
					if(!isGlmd){
					if (b == Types.DT_INT || b == Types.DT_DOUBLE || b == Types.DT_DECIMAL || b == Types.DT_LONG ) b = 1;
					else if (b == Types.DT_DATE) b = 3;
					else if (b == Types.DT_TIME) b = 4;
					else if (b == Types.DT_DATETIME) b = 5;
					else b = 2;}
					if (0 == types[i]) types[i] = b;
					else if (b != types[i]) types[i] = 2;
					//if (types[i] == 2);
				}
			}
			if (j>scanRow) break;
		}
		if (names==null||names.length==0) throw new RuntimeException("can not load data from : " + t);
		for (int i=0; i<names.length; i++) {
			if (i>0) {
				struct += ",";
			} else {
				struct = "{\"resource\":{\"type\":1,\"id\":\"\",\"dataSource\":\"\",\"dql\":\"\"},\"fields\":[";
				//{fields:[{name:'f1',type:1},{name:'f2',type:2},{name:'f3',type:3},{name:'f4',type:2},{name:'f5',type:1}]}
			}
			struct += "{\"name\":\""+names[i]+"\",\"dataType\":"+types[i]+",\"edit\":\"\"}"; // TODO 检测类型
		}
		return (struct+"]}").replaceAll("\"", "<d_q>");
	}

	
	public static Map<String,Byte> getTableFieldTypes(Table t, int scanRow, boolean isGlmd) throws Exception {
		String struct = "";
		Map<String,Byte> map = new HashMap<String,Byte>();
		String names[] = t.dataStruct().getFieldNames();
		Logger.debug("scanRow : " + scanRow);
		Logger.debug("scan table : " + t);
		byte types[] = new byte[names.length];
		for (int j=1; j<=t.length(); j++) {
			Record r = t.getRecord(j);
			for (int i=0; i<names.length; i++) {
				Object oi = r.getFieldValue(names[i]);
				if (oi != null) {
					byte b = Variant.getObjectType(oi);
					//System.out.println(b+"----"+oi);
					
//					if(!isGlmd){
//					if (b == Types.DT_INT || b == Types.DT_DOUBLE || b == Types.DT_DECIMAL || b == Types.DT_LONG ) b = 1;
//					else if (b == Types.DT_DATE) b = 3;
//					else if (b == Types.DT_TIME) b = 4;
//					else if (b == Types.DT_DATETIME) b = 5;
//					else b = 2;}
					
					if (0 == types[i]) types[i] = b;
					else if (b != types[i]) types[i] = Types.DT_STRING;

				}
			}
			if (j>scanRow) break;
		}
		if (names==null||names.length==0) throw new RuntimeException("can not load data from : " + t);
		for (int i=0; i<names.length; i++) {
			map.put(names[i], types[i]);
			struct += "{\"name\":\""+names[i]+"\",\"dataType\":"+types[i]+",\"edit\":\"\"}"; // TODO 检测类型
		}
		return map;
	}

	
	private static Sequence getSeq(String s) {
		Sequence seq = new Sequence();
		if (s == null) return seq;
		String ss[] = s.split("<;>");
		for (int i=0; i<ss.length; i++) seq.add(ss[i]); 
		return seq;
	}
	
	public static void main(String args[]) {
		Sequence s1 = new Sequence();
		s1.add("\"aa\"+说明:a1");
		s1.add("\"bb\"+a1:a2");
		
		
		Sequence s2 = new Sequence();
		s2.add("!like(a1,\"*软*\")");
		s2.add("!like(a2,\"*软*\")");
		
		Sequence s3 = new Sequence();
		s3.add("说明,a1");
		s3.add("说明,a2");
		
		//getIds("d:/temp/a1","\"aa\"+说明:a1<;>\"bb\"+a1:a2","!like(a1,\"*软*\")<;>!like(a2,\"*软*\")","说明,a1<;>说明,a2","groups(说明:A;count(a2):B;1)");
	}
}
