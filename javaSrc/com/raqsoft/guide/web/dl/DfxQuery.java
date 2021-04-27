package com.raqsoft.guide.web.dl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.raqsoft.cellset.datamodel.PgmCellSet;
import com.raqsoft.common.Logger;
import com.raqsoft.common.RQException;
import com.raqsoft.common.StringUtils;
import com.raqsoft.common.Types;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.DataStruct;
import com.raqsoft.dm.JobSpace;
import com.raqsoft.dm.JobSpaceManager;
import com.raqsoft.dm.Param;
import com.raqsoft.dm.ParamList;
import com.raqsoft.dm.Record;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.Table;
import com.raqsoft.dm.cursor.ICursor;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.report.view.html.GraphPool;
import com.raqsoft.util.CellSetUtil;
import com.raqsoft.util.Variant;

public class DfxQuery {
	
	private String dql;
	private String dataSource;
	private String dataId;
	private String dataFile;
	private String dataFileFullPath;
	private String fileHome = "";
	private String appmap = "";
	public String getDataFileFullPath() {
		return dataFileFullPath;
	}

	private String error="";
	private Sequence srcFiles = null;
	private ArrayList<String> tableNames = new ArrayList<String>();
	private Context oneCtx = null;
	private String currTable = "";
	private String blobFileNameSuffix = null;
	private int loadedRow = 0;
	private boolean readDfx =false;
	private boolean pause = false;
	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean p) throws Exception {
		if (p) {
			readDfx = true;
			if (!over) {
				int count = 0;
				while (count<1000 && !this.pause) {
					Thread.sleep(100);
					count++;
				}
				if (count == 1000) throw new Exception("Time out 3!");
			}
		} else {
			readDfx = false;
			this.pause = false;
		}
	}

	private boolean over = false;
	private String struct = "";
	private boolean changeFilter = false;
	private String filter = "";
	private long lastVisitTime = 0; //over以前最后的访问时间，超过30秒不访问就自动关闭该查询
	
	private int cursorSize = 1000;
	private String dataFileType = "text";
	
	public void removeTempFile(){
		//System.out.println("removeTempFile-----------" + dataId);
		if (dataId.indexOf("temp/")>=0) {
			new File(dataFileFullPath).delete();
		}
	}
	
	public String generateData(String type) throws Exception {
		String p = dataFileFullPath;
		if (p.endsWith(".b")) p = p.substring(0,p.length()-2);
		else if (p.endsWith(".btx")) p = p.substring(0,p.length()-4);
		else {
			//路径中有txt会有问题20190328
			if (p.endsWith(".txt")) {
				if (type.equals("txt")) return p.replaceAll("\\\\", "/").substring(DataSphereServlet.getFilePath("").length());
				else if (type.equals("csv")) {
					p = p.replaceAll("\\\\", "/");
					int filepos = p.lastIndexOf('/');
					String filename = p.substring(filepos+1);
					p = p.replaceAll(filename, filename.substring(0,filename.indexOf("."))+".csv");
					String p2 = p.replaceAll("\\\\", "/").substring(DataSphereServlet.getFilePath("").length());
					Context ctx = new Context();
					ctx.setParamValue("file1", dataFileFullPath);
					ctx.setParamValue("file2", p);
					ctx.setParamValue("eval1", "file(file1).import@t()");
					ctx.setParamValue("eval2", "file(file2).export@t(B1;\",\")");
					DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx);
//					DfxUtils.execDfxScript(dataFileFullPath+"\n"+p+"\n=file(A1).import@t()\n=file(A2).export@t(A3;\",\")", ctx);
					ctx = null;
					return p2;
				}
			} else throw new RQException("Unknown data file format");
			
		}
		p += "."+type;
		String p2 = p.replaceAll("\\\\", "/").substring(DataSphereServlet.getFilePath("").length());
		if (new File(p).exists()) return p2;
		String s = "\\t";
		if ("csv".equals(type)) s = ",";
		Context ctx = new Context();
		ctx.setParamValue("file1", dataFileFullPath);
		ctx.setParamValue("file2", p);
		ctx.setParamValue("eval1", "file(file1).cursor@b()");
		ctx.setParamValue("eval2", "file(file2).export@t(B1;\""+s+"\")");
		DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx);
//		DfxUtils.execDfxScript(dataFileFullPath+"\n"+p+"\n=file(A1).cursor@b()\nfor\n\t=A3.fetch("+this.cursorSize+")\n\tif (B5 == null)\tbreak\n\telse\t=file(A2).export@at(B5;\""+s+"\")", ctx);
		ctx = null;
		return p2;
	}
	
	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	public String getCurrTable() {
		return currTable;
	}

	public String getError() {
		if (error == null) return "";
		error = error.replaceAll("\"", " ").replaceAll("'", " ");
		return error;
	}
	public String getFilter() {
		return filter;
	}
	
	public void setTableFilter(String currTable, String filter) throws Exception {
		if (this.filter.equals(filter) && this.currTable.equals(currTable)) return;
		this.filter = filter;
		this.currTable = currTable;
		//File f = new File(dataFileFullPath);
		//if (f.exists()) f.delete();
		Context ctx = new Context();
		ctx.setParamValue("file1", dataFileFullPath);
		ctx.setParamValue("eval1", "file(file1).write(\"\")");
		DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx);
//		DfxUtils.execDfxScript("=file(\""+dataFileFullPath+"\").write(\"\")", oneCtx);
//		DfxUtils.execDfxScript("=file(\""+dataFileFullPath+"\").export@b("+currTable+")", oneCtx);	?2018/04/13，这行咋回事不知道
	}

	public void setFilter(String filter) throws Exception {
		//filter = "like(mingch)"
		if (this.filter.equals(filter)) return;
		this.filter = filter;
		
		if (over) {
			error=""; 
			loadedRow = 0;
			readDfx =false;
			pause = false;
			over = false;
			//private String struct = "";
			changeFilter = false;
			this.doQuery();
		} else {
			loadedRow = 0;
			this.changeFilter = true;
		}
	}

	public boolean isChangeFilter() {
		return changeFilter;
	}

	public void setChangeFilter(boolean changeFilter) {
		this.changeFilter = changeFilter;
	}

	public String getStruct() {
		return struct;
	}

	public void setStruct(String struct) {
		this.struct = struct;
	}

	public DfxQuery(String dataSource, String dql, String dataId, String filter, int cursorSize, String dataFileType, String appmap) throws Exception{
		this.dataSource = dataSource;
		this.dql = dql;
		this.dataId = dataId;
		this.filter = filter;
		this.dataFile = dataId;
		this.fileHome = fileHome;
		this.dataFileFullPath = DataSphereServlet.getFilePath(this.dataFile);
		this.cursorSize = cursorSize;
		this.dataFileType = dataFileType;
		this.appmap = appmap;
		lastVisitTime = System.currentTimeMillis();
		doQuery();
	}

	private String dfxFile = null;
	private String dfxScript = null;
	private ParamList dfxParams = null;
	public DfxQuery(String dfx, boolean isFile, ParamList params, String dataId, String filter, int cursorSize, String dataFileType)  throws Exception {
		if (isFile) dfxFile = dfx;
		else dfxScript = dfx;
		dfxParams = params;
		this.dataId = dataId;
		this.filter = filter;
		this.dataFile = dataId;
		this.fileHome = fileHome;
		this.dataFileFullPath = DataSphereServlet.getFilePath(this.dataFile);
		this.cursorSize = cursorSize;
		this.dataFileType = dataFileType;
		lastVisitTime = System.currentTimeMillis();
		doQuery();
	}

	public DfxQuery(String srcFiles, String dataId, String filter, int cursorSize, String dataFileType, String tableName){
		////new Sequence().binary(new String[]{"on"});
		try {
			this.dataId = dataId;
			this.filter = filter;
			this.dataFile = dataId;
			this.dataFileFullPath = DataSphereServlet.getFilePath(this.dataFile);
			this.cursorSize = cursorSize;
			this.dataFileType = dataFileType;
			lastVisitTime = System.currentTimeMillis();

			this.srcFiles = new Sequence();
			oneCtx = new Context();
			
			String fileType = srcFiles.toLowerCase().indexOf(".b")>=0?"binary":"json";
			currTable = (tableName != null && tableName.length() > 0) ? tableName : null;
			tableNames = getInputFileTables(srcFiles,this.srcFiles);
			currTable = currTable == null ? tableNames.get(0) : currTable;

			oneCtx.addParam(new Param("_files",Param.VAR, this.srcFiles));
			oneCtx.addParam(new Param("_groups",Param.VAR,new Sequence()));
			oneCtx.setParamValue("mergeft", "json".equalsIgnoreCase(fileType)?"j":"b");
//			String sss = "=file(A3).import@b()";
//			if ("json".equalsIgnoreCase(fileType)) sss = "=file(A3).read().import@j()";
//			String script = "\t\t\t\t\t\n\t\t\t\t\t\nfor _files\t\t\t\t\t\n\t"+sss+"\t\t\t\t\n\t=B4.fno()\t=B4(1)\t\t\t\n\tfor B5\t\t\t\t\n\t\t=B4.fname(B6)\t\t\t\n\t\t=eval(\"C5.#\"+string(B6))\t\t\t\n\t\tif ift(C8) \t\t\t\n\t\t\t=temp=C8\t/=eval(C7+\"=C8\")\t\n\t\t\tfor B5\t\t\n\t\t\t\t=C5.fname(D11)\t\n\t\t\t\t=eval(\"C5.#\"+string(D11))\t\n\t\t\t\tif !ift(E13) \t\n\t\t\t\t\t=eval(\"C8=C8.derive(E13:\"+E12+\")\")\n\t\t\t\t\t=temp=F15\n\t\t\t\t\t/=eval(C7+\"=F15\")\n\t\t\tif eval(\"ifv(\"+C7+\")\")\t=eval(C7+\"=[\"+C7+\",temp].union()\")\t=eval(C7+\"=\"+C7+\".derive@o()\")\n\t\t\telse\t=eval(C7+\"=temp\")\t\n\t\t\t\t\t\n\tif (ifv(_one_))\t=_one_=_one_&B4\t\t\t\n\telse\t=_one_=B4\t\t\t\n\t\t\t\t\t\n/=t1=test2.groups(a:a1,b:b1;sum(SCORE1):sum1,avg(STUDENTID1):avg1)\t\t\t\t\t\nfor _groups\t\t\t\t\t\n\t=eval(A25)\t\t\t\t";
//			DfxUtils.execDfxScript(script, oneCtx);
			DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/merge.dfx", oneCtx);
			File f = new File(dataFileFullPath);
			if (f.exists()) f.delete();
			//Context ctx = new Context();
			//ctx.setParamValue("file1", dataFileFullPath);
			oneCtx.setParamValue("eval1", "file(\""+dataFileFullPath+"\").export@"+("binary".equalsIgnoreCase(dataFileType)?"b":"t")+"("+currTable+")");
			DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", oneCtx);
//			DfxUtils.execDfxScript("=file(\""+dataFileFullPath+"\").export@"+("binary".equalsIgnoreCase(dataFileType)?"b":"t")+"("+currTable+")", oneCtx);
			Table tab = (Table)oneCtx.getParam(currTable).getValue();
			loadedRow = tab.length();
			over = true;
			pause = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			////new Sequence().binary(new String[]{"off"});
		}
		//doQuery();
	}
	
	public static ArrayList<String> getInputFileTables(String srcFiles, Sequence seq) throws Exception {
		String ds[] = getInputFiles(srcFiles);
		String fileType = srcFiles.toLowerCase().indexOf(".b")>=0?"binary":"json";
		ArrayList<String> tableNames  = new ArrayList<String>();
		Context ctx1 = new Context();

		if (ds.length == 0) throw new RuntimeException("Data file does not exist");
		for (int i=0; i<ds.length; i++) {
			if (!new File(ds[i]).exists()) ds[i] = DataSphereServlet.getFilePath(ds[i]);
			if (!new File(ds[i]).exists()) throw new RuntimeException("Data file does not exist : "+ds[i]);
			seq.add(ds[i]);
			Logger.debug(ds[i]);
			if (i == 0) {
				ctx1.setParamValue("eval1", "firstFile=file(\""+ds[i]+"\")"+("json".equalsIgnoreCase(fileType)?".read().import@j":".import@b")+"()");
				DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx1);
				//DfxUtils.execDfxScript(">firstFile=file(\""+ds[i]+"\")"+("json".equalsIgnoreCase(fileType)?".read().import@j":".import@b")+"()", oneCtx);
				Param p = ctx1.getParam("firstFile");
				//Logger.debug(p);
				if (p == null || p.getValue() == null || !(p.getValue() instanceof Table)) throw new RuntimeException("File format is incorrect : "+ds[i]);
				Table t = (Table)p.getValue();
				if (t.length() != 1) throw new RuntimeException("The format of the file is not correct, it needs to be the data file generated automatically by Raqsoft filling form. "+ds[i]);
				String[] names = t.dataStruct().getFieldNames();
				Record r = t.getRecord(1);
				for (int j=0; j<names.length; j++) {
					Object oij = r.getFieldValue(names[j]);
					if (oij != null && oij instanceof Table) {
						tableNames.add(names[j]);
					}
				}
				if (tableNames.size() == 0) throw new RuntimeException("Not found any table in input data file!");
				
			}
		}

		return tableNames;
		
	}
	
	
	public static String[] getInputFiles(String f) throws Exception {
		ArrayList<String> al = new ArrayList<String>();
		String ss[] = f.split(";");
		for (int i=0; i<ss.length; i++) {
			if (ss[i].trim().length() == 0) continue;
			if (ss[i].indexOf("*")>=0) {
				ss[i] = ss[i].replaceAll("\\\\", "/").replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
				int pos = ss[i].lastIndexOf("/");
				String p = pos>0?ss[i].substring(0, pos+1):"";
				String fi = pos>0?ss[i].substring(pos+1):ss[i];
				fi = fi.endsWith("btx")||fi.endsWith("b")?"btx":"json";
				File parent = new File(DataSphereServlet.getFilePath(p));
				if (parent.isDirectory()) {
					File fs[] = parent.listFiles();
					for (int z=0; z<fs.length; z++) {
						if (fs[z].getName().toLowerCase().endsWith("json") && fi.equals("json")) al.add(fs[z].getCanonicalPath().replaceAll("\\\\", "/"));
						else if (fs[z].getName().toLowerCase().endsWith("btx") || fs[z].getName().toLowerCase().endsWith("b")) al.add(fs[z].getCanonicalPath().replaceAll("\\\\", "/"));
					}
				}
			} else {
				if (!new File(ss[i]).exists()) ss[i] = DataSphereServlet.getFilePath(ss[i]);
				if (new File(ss[i]).exists()) al.add(ss[i].replaceAll("\\\\", "/"));
			}
		}
		return al.toArray(new String[al.size()]);
	}
	
	public ArrayList<String> getTableNames() {
		return tableNames;
	}
	
	public long getLastVisitTime() {
		return lastVisitTime;
	}

	public void setLastVisitTime(long lastVisitTime) {
		this.lastVisitTime = lastVisitTime;
	}

	private void doQuery() throws Exception {
		if (srcFiles != null) {
			
			return;
		}
		Context ctx = new Context();
		ctx.setParamValue("eval1", "file(\""+dataFileFullPath+"\").write(\"\")");
		DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx);
//		DfxUtils.execDfxScript("=file(\""+dataFileFullPath+"\").write(\"\")", new Context());
		final DfxQuery curr = this;
		new Thread() {
			//public 
			public void run(){
				
				String jsId = "jsId" + System.currentTimeMillis();
				String userJsId = null;
				JobSpace space = JobSpaceManager.getSpace(jsId);
				Logger.debug("begin query");
				String save = DataSphereServlet.DFX_SAVE;
				////new Sequence().binary(new String[]{"on"});
				Context ctx = new Context();
				try{
					if (curr.dfxParams != null) {
						ctx.setParamList(curr.dfxParams);
					}
					String dft = curr.dataFileType;
					if (curr.dfxFile != null) {
						userJsId = DfxUtils.execDfxFile(DataSphereServlet.getFilePath(curr.dfxFile), ctx, false);
						Object c = ctx.getParam("_returnValue_").getValue();
						if (c == null) throw new RuntimeException("DFX data set need return Cursor or Table type.");
						if (c instanceof ICursor) {
							ctx.setParamValue("outerCursor", c);
						} else if (c instanceof Table) {
							ctx.setParamValue("outerTable", c);
							save = DataSphereServlet.DFX_SAVE_TXT;
						} else if (c instanceof Sequence) {
							Sequence s = (Sequence)c;
							if (!s.isPurePmt()) throw new RuntimeException("DFX data set need return Cursor or Table type.");
							DataStruct ds = s.dataStruct();
							Table t = s.derive("o");
//							Table t = new Table(ds);
//							for (int m=1; m<=s.length(); m++) {
//								t.add(s.get(m));
//							}
							ctx.setParamValue("outerTable", t);
							save = DataSphereServlet.DFX_SAVE_TXT;
						} else throw new RuntimeException("DFX data set need return Cursor or Table type.");
						Logger.debug(curr);
						Logger.debug(curr.dataFileType);
						Logger.debug(save);
						if (save.equals(DataSphereServlet.DFX_SAVE) && curr.dataFileType.equals("text")) {
							//throw new Exception("When DFX return Cursor，please set dataFileType=\"binary\"");
							Logger.warn("When DFX return Cursor,please set dataFileType=\"binary\"");
							dft = "binary";
						}
					} else if (curr.dfxScript != null) {
						userJsId = DfxUtils.execUserDfxScript(curr.dfxScript, ctx, false);
						Object c = ctx.getParam("_returnValue_").getValue();
						if (c == null) throw new RuntimeException("DFX data set need return Cursor or Table type.");
						if (c instanceof ICursor) {
							ctx.setParamValue("outerCursor", c);
						} else if (c instanceof Table) {
							ctx.setParamValue("outerTable", c);
							save = DataSphereServlet.DFX_SAVE_TXT;
						} else if (c instanceof Sequence) {
							Sequence s = (Sequence)c;
							if (!s.isPurePmt()) throw new RuntimeException("DFX data set need return Cursor or Table type.");
							DataStruct ds = s.dataStruct();
							Table t = new Table(ds);
							for (int m=1; m<=s.length(); m++) {
								t.add(s.get(m));
							}
							ctx.setParamValue("outerTable", t);
							save = DataSphereServlet.DFX_SAVE_TXT;
						} else throw new RuntimeException("DFX data set need return Cursor or Table type.");
						if (save.equals(DataSphereServlet.DFX_SAVE) && curr.dataFileType.equals("text")) throw new Exception("When DFX return Cursor，please set dataFileType=\"binary\"");
					} else {
						ctx.setParamValue("outerCursor", "");
						ctx.setParamValue("outerTable", "");
						if (!"binary".equalsIgnoreCase(curr.dataFileType)) {
							save = DataSphereServlet.DFX_SAVE_TXT;
						}
					}
					//save = DataSphereServlet.DFX_SAVE;
					PgmCellSet pcs = CellSetUtil.readPgmCellSet(DataSphereServlet.class.getResourceAsStream(save));
					pcs.setContext(ctx);
					Logger.debug("curr.dataSource : " + curr.dataSource);
					ctx.setParamValue("dataSource", curr.dataSource);
					ctx.setParamValue("dql", curr.dql);
					ctx.setParamValue("src", curr.dataFileFullPath);
					ctx.setParamValue("filter", curr.filter);
					ctx.setParamValue("cancelSave", false);
					ctx.setParamValue("changeFilter", false);
					ctx.setParamValue("cursorSize", curr.cursorSize);
					ctx.setParamValue("dataFileType", dft);
					ctx.setParamValue("blobCount", 1);
					ctx.setParamValue("blobFileName", "");
					ctx.setParamValue("picrootpath", appmap+"/reportServlet?action=9&graphId=");//new File(curr.dataFileFullPath).getParent().replace("\\","/")+"/");
//					space.setParamValue("@dataSource", curr.dataSource);
//					space.setParamValue("@dql", curr.dql);
//					space.setParamValue("@src", curr.dataFile);
					//space.setAppHome(svr.getAppPath(appId));  //设应用主目录
					//pcs.setParamToContext();
					ctx.setJobSpace(space);
					//Logger.debug("Sequence.P_PROGRAM_1 : " + Sequence.getFunctionPoint(Sequence.P_PROGRAM, 1));
					//for (int p=1; p<10000; p++) DataSphereServlet.TESTER += "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
					pcs.calculateResult();
					boolean hasBlob = Integer.parseInt(ctx.getParam("blobCount").getValue()+"") > 1;
					while(pcs.hasNextResult()) {
						curr.pause = true;
						if (curr.readDfx) {
							Thread.sleep(100);
						} else {
							curr.pause = false;
							if (System.currentTimeMillis()-curr.lastVisitTime>30000) {
								ctx.setParamValue("cancelSave", true);
								break;
							}
							if (curr.changeFilter) {
								ctx.setParamValue("changeFilter", true);
								ctx.setParamValue("filter", curr.filter);
								if (userJsId != null) {
									try {
										JobSpaceManager.closeSpace(jsId);
									} catch (Exception e) {
									}
								}
								if (curr.dfxFile != null) {
									userJsId = DfxUtils.execDfxFile(DataSphereServlet.getFilePath(curr.dfxFile), ctx, false);
									Object c = ctx.getParam("_returnValue_").getValue();
									if (c == null) throw new RuntimeException("DFX data set need return Cursor or Table type.");
									if (c instanceof ICursor) {
										ctx.setParamValue("outerCursor", c);
										ctx.setParamValue("outerTable", "");
									} else if (c instanceof Table) {
										ctx.setParamValue("outerTable", c);
										ctx.setParamValue("outerCursor", "");
									} else if (c instanceof Sequence) {
										Sequence s = (Sequence)c;
										if (!s.isPurePmt()) throw new RuntimeException("DFX data set need return Cursor or Table type.");
										DataStruct ds = s.dataStruct();
										Table t = new Table(ds);
										for (int m=1; m<=s.length(); m++) {
											t.add(s.get(m));
										}
										ctx.setParamValue("outerTable", t);
										ctx.setParamValue("outerCursor", "");
									} else throw new RuntimeException("DFX data set need return Cursor or Table type.");
								} else if (curr.dfxScript != null) {
									userJsId = DfxUtils.execUserDfxScript(curr.dfxScript, ctx, false);
									Object c = ctx.getParam("_returnValue_").getValue();
									if (c == null) throw new RuntimeException("DFX data set need return Cursor or Table type.");
									if (c instanceof ICursor) {
										ctx.setParamValue("outerCursor", c);
										ctx.setParamValue("outerTable", "");
									} else if (c instanceof Table) {
										ctx.setParamValue("outerTable", c);
										ctx.setParamValue("outerCursor", "");
									} else if (c instanceof Sequence) {
										Sequence s = (Sequence)c;
										for (int m=1; m<=s.length(); m++) {
											Logger.debug(s.get(m));
										}
										if (!s.isPurePmt()) throw new RuntimeException("DFX data set need return Cursor or Table type.");
										DataStruct ds = s.dataStruct();
										Table t = new Table(ds);
										for (int m=1; m<=s.length(); m++) t.add(s.get(m));
										ctx.setParamValue("outerTable", t);
										ctx.setParamValue("outerCursor", "");
									} else throw new RuntimeException("DFX data set need return Cursor or Table type.");
								} else {
									ctx.setParamValue("outerCursor", "");
									ctx.setParamValue("outerTable", "");
								}

								//Logger.debug("------" + curr.filter);
							} 
							Object o = pcs.nextResult();
							if (curr.changeFilter) {
								//ctx.setParamValue("changeFilter", false);
								curr.changeFilter = false;
							} 
							//Logger.debug("result : " + o);
							if (o != null && o instanceof Integer) {
								int oi = (Integer)o;
								if ("binary".equalsIgnoreCase(dataFileType)) {
									if (oi == 0) {
										curr.over = true;
										curr.pause = true;
										break;
									} else {
										curr.loadedRow += oi;
										//Thread.sleep(500);
									}
								} else {
									curr.loadedRow = oi;
									curr.over = true;
									curr.pause = true;
								}
							}
						}
					}
					//图片格转文件：集算器程序中转储了一个blob序表blobs，有id和blob（对象）两个字段。id为新名字，作为图片池的key。转原序表中的blob类型字段为图片访问链接+&graphId=key
					if(hasBlob){
						Table blobsTable = (Table) ctx.getParam("blobs").getValue();
						for(int b = 1 ; b <= blobsTable.length(); b++){
							Record r = (Record) blobsTable.get(b);
							String pid = (String) r.getFieldValue("pid");
							Object o = r.getFieldValue("blob");
							GraphPool.put(pid, (byte[]) o);
							String fieldName = (String) r.getFieldValue("fieldname");
							List<String> list = null;
							if(ActionResultPage.cacheBlobFields.containsKey(dataId)){
								list = ActionResultPage.cacheBlobFields.get(dataId);
							}else{
								list = new ArrayList<String>();
								ActionResultPage.cacheBlobFields.put(dataId,list);
							}
							if(!list.contains(fieldName)) list.add(fieldName);
						}
						/*File srcFile1 = new File(curr.dataFileFullPath);
						String path = srcFile1.getParent();
						String srcFileName = srcFile1.getName();
						blobFileNameSuffix = srcFileName.substring(0,srcFileName.indexOf(".")) + "_blob_";
						File folder = new File(path);
						if(folder.isDirectory()){
							FilenameFilter fnfilter = new FilenameFilter() {
								public boolean accept(File dir, String name) {
									return name.startsWith(blobFileNameSuffix);
								}
							};
							String[] selectBlobFiles = folder.list( fnfilter );
							for(String blobFileName : selectBlobFiles){
								BufferedReader br=new BufferedReader(new FileReader(path+File.separator+blobFileName));
								StringBuffer sb=new StringBuffer(4096);
								String temp=null;
								int line=0;
								while((temp=br.readLine())!=null){
									line++;
									if(line == 1) {
										continue;
									}
									String wrap = "\r\n";
									if(line > 2) sb.append(wrap);
									sb.append(temp);
								}
								br.close();
								BufferedWriter bw=new BufferedWriter(new FileWriter( path+File.separator+"pic_"+blobFileName));
								bw.write(sb.toString());
								bw.flush();
								bw.close();
							}
						}*/
					}
				}catch(Exception e){
					String mes = e.getLocalizedMessage();
					if (mes == null || mes.length() == 0) mes = "DfxQuery unknown error!";
					Logger.warn("",e);
					Throwable t = e.getCause();
					curr.error = Variant.parse(mes+(t!=null&&t.getMessage()!=null&&mes.indexOf(t.getMessage()) <0 ?" " + t.getMessage():""), true).toString().replaceAll("\r\n", " ").replaceAll("\n", " ").replaceAll("\\\\", "/");
					//e.printStackTrace();
					curr.over = true;
					curr.pause = true;
				}finally{
					JobSpaceManager.closeSpace(jsId);
					if (userJsId != null) {
						try {
							JobSpaceManager.closeSpace(jsId);
						} catch (Exception e) {
						}
					}
					////new Sequence().binary(new String[]{"off"});
				}
			}
		}.start();
	}
	
	public String getRows(int begin, int end, String calcFields, String filter, String fields) throws Exception {
		if (calcFields == null) calcFields = "";
		if (filter == null) filter = "";
		if (fields == null) fields = "";
		boolean genStruct = this.struct.length() == 0;
		//if (this.loadedRow < end || changeFilter) {
		//	if (genStruct) return "{rows:[],struct:{fields:[]}}";
		//	else return "{rows:[],struct:"+struct+"}";
		//}
		StringBuffer sb = new StringBuffer("{rows:[");
		readDfx = true;
		if (!over) {
			int count = 0;
			while (count<100 && !pause) {
				Thread.sleep(100);
				count++;
			}
			if (count == 100) throw new Exception("Time out 2!");
		}

		String jsId = "jsId" + System.currentTimeMillis();
		JobSpace space = JobSpaceManager.getSpace(jsId);
		try{
			String read = DataSphereServlet.DFX_READ;
			if (!"binary".equalsIgnoreCase(dataFileType)) read = DataSphereServlet.DFX_READ_TXT;
			PgmCellSet pcs = CellSetUtil.readPgmCellSet(DataSphereServlet.class.getResourceAsStream(read));
			Context ctx = pcs.getContext();
			//ParamList pl = ctx.getParamList();
			ctx.setParamValue("src", dataFileFullPath);
			ctx.setParamValue("type", 1);
			ctx.setParamValue("begin", begin);
			ctx.setParamValue("end", end);
			ctx.setParamValue("calcFields", calcFields);
			ctx.setParamValue("filter", filter);
			ctx.setParamValue("fields", fields);
			ctx.setParamValue("dataFileType", dataFileType);
//			space.setParamValue("@src", dataFile);
//			space.setParamValue("@type", 1);
//			space.setParamValue("@begin", begin);
//			space.setParamValue("@end", end);
			//space.setAppHome(svr.getAppPath(appId));  //设应用主目录
//			pcs.setParamToContext();
			ctx.setJobSpace(space);
			Object o = pcs.execute();
			readDfx = false;
			if (o != null) {
				Table t = (Table)o;
				if (t.length() == 0) return "{error:'no data be found'}";
				String fs[] = ((Record)t.get(1)).dataStruct().getFieldNames();
				byte types[] = new byte[fs.length];
				//sb.append("[");

				//sb.append("]");
				for (int j=1; j<=t.length(); j++) {
					if (j>1) sb.append(",");
					sb.append("[");
					Record r = (Record)t.get(j);
					for (int i=0; i<fs.length; i++) {
						if (i>0) sb.append(",");
						Object oi = r.getFieldValue(fs[i]);
						String v = "";
						if (oi != null) {
							v = oi.toString().replaceAll("\"", "<d_q>");
							byte b = Variant.getObjectType(oi);
							if (b == Types.DT_INT || b == Types.DT_DOUBLE || b == Types.DT_DECIMAL || b == Types.DT_LONG ) b = 1;
							else if (b == Types.DT_DATE) b = 3;
							else if (b == Types.DT_TIME) b = 4;
							else if (b == Types.DT_DATETIME) b = 5;
							else b = 2;
							types[i] = b;
						}
						sb.append("\""+v+"\"");
					}
					sb.append("]");
				}
				if (genStruct) {
					for (int i=0; i<fs.length; i++) {
						if (i>0) {
							struct += ",";
						} else {
							struct = "{\"resource\":{\"type\":2,\"id\":\""+this.dataId+"\",\"dataSource\":\""+this.dataSource+"\",\"dql\":\""+/*this.dql.replaceAll("\"", "<dq>").replaceAll("\'", "<sq>")+*/"\"},\"fields\":[";
							//{fields:[{name:'f1',type:1},{name:'f2',type:2},{name:'f3',type:3},{name:'f4',type:2},{name:'f5',type:1}]}
						}
						struct += "{\"name\":\""+fs[i]+"\",\"type\":"+types[i]+"}"; // TODO 检测类型
					}
					struct = (struct+"]}").replaceAll("\"", "<d_q>");
					Logger.debug("struct : " + struct);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
			Logger.warn(e);
			return "error:" + e.getMessage();
		}finally{
			JobSpaceManager.closeSpace(jsId);
		}

		
		String s = sb.append("],struct:\""+struct+"\"}").toString();
		Logger.debug("" + begin + " : " + end + " : " + s);
		return s;
	}

	public String getDql() {
		return dql;
	}

	public String getDataSource() {
		return dataSource;
	}

	public String getDataFile() {
		return dataFile;
	}

	public int getLoadedRow(String calcFields, String filter, String fields) throws Exception {
		if (calcFields == null) calcFields = "";
		if (filter == null) filter = "";
		if (fields == null) fields = "";
		if (filter.length()==0 || loadedRow == 0) return loadedRow;

		readDfx = true;
		int count = 0;
		while (count<100 && !pause) {
			Thread.sleep(100);
			count++;
		}
		if (count == 100) throw new Exception("Time out 3!");

		String jsId = "jsId" + System.currentTimeMillis();
		JobSpace space = JobSpaceManager.getSpace(jsId);
		try{
			String dfx = DataSphereServlet.DFX_READ;
			if (!"binary".equalsIgnoreCase(dataFileType)) dfx = DataSphereServlet.DFX_READ_TXT;
			PgmCellSet pcs = CellSetUtil.readPgmCellSet(DataSphereServlet.class.getResourceAsStream(dfx));
			Context ctx = pcs.getContext();
			//ParamList pl = ctx.getParamList();
			ctx.setParamValue("src", dataFile);
			ctx.setParamValue("type", 2);
			ctx.setParamValue("begin", 0);
			ctx.setParamValue("end", 0);
			ctx.setParamValue("calcFields", calcFields);
			ctx.setParamValue("filter", filter);
			ctx.setParamValue("fields", fields);
//			space.setParamValue("@src", dataFile);
//			space.setParamValue("@type", 1);
//			space.setParamValue("@begin", begin);
//			space.setParamValue("@end", end);
			//space.setAppHome(svr.getAppPath(appId));  //设应用主目录
//			pcs.setParamToContext();
			ctx.setJobSpace(space);
			Object o = pcs.execute();
			readDfx = false;
			if (o != null) {
				loadedRow = (Integer)o;
			}
			//Logger.debug("result : " + v);
		}catch(Exception e){
			this.error = Variant.parse(e.getMessage(), true).toString().replaceAll("\r\n", " ").replaceAll("\n", " ");;
			e.printStackTrace();
			Logger.warn(e);
			//throw e; 
		}finally{
			JobSpaceManager.closeSpace(jsId);
		}
		
		return loadedRow;

	}
	
	public boolean isReadDfx() {
		return readDfx;
	}

	public boolean isOver() {
		return over;
	}

	public static void main(String args[]){
//		File f = new File("D:/data/workspace/guide/web/WEB-INF/dataSet/qqq/kao.txt");
//		System.out.println(f.exists());
//		String s = "create table t2 ( id bigint not null primary key auto_increment";
//		for (int i=0; i<1000; i++) {
//			s += ",f"+(i+1)+" varchar(10)";
//		}
//		s += ");";
		String s = "insert into t2 ( ";
		for (int i=0; i<1000; i++) {
			if (i>0) s += ",";
			s += "f"+(i+1)+"";
		}
		s += ") values ("; 
		for (int i=0; i<1000; i++) {
			if (i>0) s += ",";
			s += "'v"+new Double(Math.random()*100000).intValue()+"'";
		}
		s+=");";
		System.out.println(s);

		System.out.println(s);
	}
}
