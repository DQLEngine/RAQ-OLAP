package com.raqsoft.guide.web.dl;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;

import com.raqsoft.common.Area;
import com.raqsoft.common.ByteMap;
import com.raqsoft.common.DBSession;
import com.raqsoft.common.Escape;
import com.raqsoft.common.IByteMap;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.common.RQException;
import com.raqsoft.common.Types;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.JobSpaceManager;
import com.raqsoft.dm.Param;
import com.raqsoft.dm.ParamList;
import com.raqsoft.dm.Record;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.Table;
import com.raqsoft.dm.cursor.ICursor;
import com.raqsoft.guide.resource.GuideMessage;
import com.raqsoft.guide.tag.GuideTag;
import com.raqsoft.guide.tag.QueryTag;
import com.raqsoft.guide.util.DqlUtil;
import com.raqsoft.guide.web.DQLTableFilter;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.dbd.DDWData;
import com.raqsoft.guide.web.model.SqlDataset;
import com.raqsoft.guide.web.querys.QueryTask;
import com.raqsoft.guide.web.querys.TaskQueue;
import com.raqsoft.report.cache.CacheManager;
import com.raqsoft.report.model.ReportDefine;
import com.raqsoft.report.model.ReportDefine2;
import com.raqsoft.report.usermodel.BuiltinDataSetConfig;
import com.raqsoft.report.usermodel.Context;
import com.raqsoft.report.usermodel.DataSetConfig;
import com.raqsoft.report.usermodel.DataSetMetaData;
import com.raqsoft.report.usermodel.Engine;
import com.raqsoft.report.usermodel.EsProcDataSetConfig;
import com.raqsoft.report.usermodel.IColCell;
import com.raqsoft.report.usermodel.INormalCell;
import com.raqsoft.report.usermodel.IReport;
import com.raqsoft.report.usermodel.IRowCell;
import com.raqsoft.report.usermodel.Macro;
import com.raqsoft.report.usermodel.MacroMetaData;
import com.raqsoft.report.usermodel.ParamMetaData;
import com.raqsoft.report.usermodel.PrintSetup;
import com.raqsoft.report.util.ReportUtils;
import com.raqsoft.util.JSONUtil;
import com.raqsoft.util.Variant;

public class ActionResultPage {
	
	public static TaskQueue tsks = new TaskQueue();
	public static Map<String, List<String>> cacheBlobFields = new HashMap<String,List<String>>();
	private static Map<String, Object> cacheCursor = new HashMap<String,Object>();
	public static String JSID = "_JSID";
	public void service( HttpServletRequest req, HttpServletResponse res, JspWriter pw, String pageId ) {
		//PrintWriter pw = null;
		Object o = null;
		PrintWriter writer = null;
		res.setContentType( "text/html;charset=UTF-8" );
		HttpSession session = req.getSession();
		String oper = req.getParameter( "oper" );
		//Logger.debug("olap 后续请求："+oper);
		
		
		try {
			//集算器授权超期检查
			////new Sequence().binary(new String[]{"on"});
			if ("query".equals(oper)) {
				com.raqsoft.app.config.ConfigUtil.checkEsprocExpiration();
				writer = res.getWriter();
				String reQuery = req.getParameter("reQuery");//数据文件已存在，强制重新查询
				String dataId = req.getParameter("dataId");//数据文件，含目录
				String dataFileType = req.getParameter("dataFileType");//文本、二进制
				boolean isBtx = dataId.indexOf(".b")>=0;
				dataFileType = isBtx?"binary":"text";
				Logger.debug("query file is " + dataId);
				String type = req.getParameter("type");//查询类型
				String dataSource = req.getParameter("dataSource");//数据源
				String ql = req.getParameter("ql");//sql或dql
				//ql支持使用参数${}形式 select * from T where F = ${f} 任意位置的参数进行拼接
				//防止注入？
				ql = solveQl(ql, req);
				Logger.debug("dql or sql is ： " + ql);
				String dqlSegments = req.getParameter("dqlSegments");
				if (dqlSegments != null) {
					String isGlmd = req.getParameter("isGlmd");//数据文件，含目录
					if("true".equals(isGlmd)) ql = DqlUtil.getDql(dqlSegments.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("<d_q>", "\""),null,null,dataSource);
					else ql = DqlUtil.getDql(dqlSegments.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("<d_q>", "\""),null,null,dataSource);
				}
				String dfxFile = req.getParameter("dfxFile");//dfx文件
				String dfxScript = req.getParameter("dfxScript");//dfx脚本
				String dfxParams = req.getParameter("dfxParams");//dfx参数
				String inputFiles = req.getParameter("inputFiles");//
				int cursorSize = 10000;//10000;
				if(req.getParameter("cursorSize") != null) Integer.parseInt(req.getParameter("cursorSize"));
				String filter = req.getParameter("filter");
				String currTable = req.getParameter("currTable");
				Object o1 = session.getAttribute(dataId);
				
//				String paths[] = Env.getPaths();
//				String root = DataSphereServlet.getFilePath("");				
//				String paths2[] = null;
//				if (paths != null) {
//					boolean find = false;
//					for (int i=0; i<paths.length; i++) {
//						if (paths[i].equals(root)) {
//							find = true;
//							break;
//						}
//					}
//					if (find) {
//						paths2 = new String[paths.length+1];
//						paths2[0] = root;
//						for (int i=0; i<paths.length; i++) {
//							paths2[i+1] = paths[i];
//						}
//					} else paths2 = paths;
//				} else {
//					paths2 = new String[]{root};
//				}
				//Env.setPaths(paths2);
				//Env.setTempPath(root);//.setMainPath(root);
				

				if (o1 != null) {
					DfxQuery dq = (DfxQuery)o1;
					File f = new File(dq.getDataFileFullPath());
					if (f.exists() && f.isFile() && f.length()>0) {
					} else {
						dq = null;
						o1 = null;
						session.removeAttribute(dataId);
					}
				}
				if ("1".equals(reQuery) || o1 == null) {
					DfxQuery dq = null;
					ParamList pl = new ParamList();

					if (dfxParams != null && dfxParams.length()>0) {
						String paramMode = "i";
						String params = dfxParams;
						if (paramMode.indexOf("i")>=0 || paramMode.indexOf("p")>=0) {
							if (params != null) {
								if (paramMode.indexOf("p")>=0) {
									String[] ss = params.split("\"");
									for (int i=0; i<ss.length; i++) {
										if (i%2 == 1) {
											ss[i] = ss[i].replaceAll("=", "_dengyu_").replaceAll(";", "_fenhao_");
										}
									}
									params = "";
									for (int i=0; i<ss.length; i++) {
										params += ss[i];
									}
								}
								String ps[] = params.split(";");
								for (int i=0; i<ps.length; i++) {
									String[] psi = ps[i].split("=");
									if(psi[0].equals("f")) psi[1] = DataSphereServlet.getFilePath(psi[1]);
									if (psi.length == 2) {
										//System.out.println(psi[0] + "-----" + psi[1]);
										String ps0 = psi[0].replaceAll("_dengyu_","=").replaceAll("_fenhao_",";");
										String ps1 = psi[1].replaceAll("_dengyu_","=").replaceAll("_fenhao_",";");
										Param p = pl.get(ps0);
										if (p != null) p.setValue(Variant.parse(ps1));
										else {
											Param p2 = new Param();
											p2.setName(ps0);
											p2.setValue(Variant.parse(ps1));
											pl.add(p2);
										}
									}
								}
							}
						} else if (paramMode.indexOf("r")>=0) {
							Enumeration e = req.getAttributeNames();
							while (e.hasMoreElements()) {
								String n = e.nextElement().toString();
								Object v = req.getAttribute(n);
								if (v != null && v.toString().length()>0) {
									Param p = pl.get(n);
									if (p != null) p.setValue(v);
									else {
										Param p2 = new Param();
										p2.setName(n);
										p2.setValue(v);
										pl.add(p2);
									}
								}
							}
						}
					}
					if ("binary".equalsIgnoreCase(dataFileType) && !DataSphereServlet.binAuth) {
						//验证集算器授权ip不可用
						throw new Exception("对不起，集算器授权不可用，不能使用bin格式缓存");
					}
					//处理dfxParams
					GuideTag.putParams(null, pl, req);
					if ("2".equals(type)) {
						dq = new DfxQuery(dataSource, ql, dataId,filter,cursorSize,dataFileType,getUrlPrefix(req));
					} else if ("1".equals(type)) {
						//dq = new DfxQuery(dataId);
					} else if ("3".equals(type)) {
						dq = new DfxQuery(dfxFile, true, pl, dataId,filter,cursorSize,dataFileType);
					} else if ("4".equals(type)) {
						dfxScript = dfxScript.replace("\\"+"n", "\n").replace("\\"+"t", "\t");
						dq = new DfxQuery(dfxScript, false, pl, dataId,filter,cursorSize,dataFileType);
					} else if ("5".equals(type)) {//填报文件增加选表
						String tableName = req.getParameter("tableName");
						dq = new DfxQuery(inputFiles, dataId,filter,cursorSize,dataFileType, tableName);
						if (currTable!=null && currTable.length()>0) dq.setTableFilter(currTable, filter);
					}
					session.setAttribute(dataId, dq);
				}
				writer.print("ok");
			} else if ("getLmd".equals(oper)) {
				writer = res.getWriter();
				writer.print(ConfigUtil.getMetaDataJson(req.getParameter("dataSource")));
			} else if ("getGlmd".equals(oper)) {
				writer = res.getWriter();
				writer.print(ConfigUtil.getListJsonData(req.getParameter("dataSource")));
			} else if ("getDct".equals(oper)) {
				writer = res.getWriter();
				writer.print(FileUtils.getDict(DataSphereServlet.getFilePath(req.getParameter("dct"))).replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("\"", "<d_q>"));
			} else if ("getVsb".equals(oper)) {
				writer = res.getWriter();
				writer.print(FileUtils.getVsb(DataSphereServlet.getFilePath(req.getParameter("vsb"))).replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("\"", "<d_q>"));
			} else if ("getFiles".equals(oper)) {
				writer = res.getWriter();
				if(req.getMethod().equalsIgnoreCase("post")){
					//writer.print(QueryTag.getFiles(req.getParameter("qyxFolderOnServer"),req.getParameter("olapFolderOnServer"),req.getParameter("dfxFolderOnServer"), req.getParameter("rpxFolderOnServer"), req.getParameter("inputFileFolderOnServer"), req.getParameter("fileDataFolderOnServer")));
					writer.print(QueryTag.getFiles());
				}
			} else if ("fileExist".equals(oper)) {
				writer = res.getWriter();
				String file = req.getParameter("file");
				String type = req.getParameter("filetype");
				if(type == null || type.length() == 0){
					File f = new File(DataSphereServlet.getFilePath(file));
					if (f.exists() && f.length()==0) f.delete();
					writer.print(( f.exists() && f.length()>0)?"1":"0");
				}else{
					File f = new File(DataSphereServlet.getFilePath(file)+'.'+type);
					if (f.exists() && f.length()==0) f.delete();
					writer.print(( f.exists() && f.length()>0)?"1":"0");
				}
			} else if ("downloadData".equals(oper)) {
				writer = res.getWriter();
				String type = req.getParameter("type");
				String dataId = req.getParameter("dataId");
				o = session.getAttribute(dataId);
				if (o == null) {
					writer.println("error:Time out!");
					return;
				}
				DfxQuery dq = (DfxQuery)o;
				writer.print(dq.generateData(type));
			} else if ("saveCacheData".equals(oper)) {
				writer = res.getWriter();
				String path = req.getParameter("path");
				String dataId = req.getParameter("dataId");
				String s = FileUtils.copyFile(DataSphereServlet.getFilePath(dataId),DataSphereServlet.getFilePath(path),false);
				if (s.length()>0) {
					Logger.warn("data file cope error : " + s);
				}
				writer.println("ok");
				
			} else if ("getDql".equals(oper)) {
				writer = res.getWriter();
				String dqlSegments = req.getParameter("dqlSegments");
				String dataSource = req.getParameter("dataSource");
				String outerConditionId = req.getParameter("filter");
				if (outerConditionId == null) {
					outerConditionId = "default";
				}

				Object filters = session.getAttribute("_raqsoft_filters_");
				DQLTableFilter filter = null;
				if (filters != null) {
					ArrayList os = (ArrayList)filters;
					for (int i=0; i<os.size(); i++) {
						DQLTableFilter fi = (DQLTableFilter)os.get(i);
						if (fi.getID().equals(outerConditionId)) { // && fi.getDataSource().equals(dataSource)
							filter = fi;
							break;
						}
					}
				}
				String isGlmd = req.getParameter("isGlmd");//数据文件，含目录
				String dql = null;
				if("true".equals(isGlmd)) dql = DqlUtil.getDql(dqlSegments.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("<d_q>", "\""),filter!=null?filter.getFilters():null,filter!=null?filter.getParamValues():null,dataSource);
				else dql = DqlUtil.getDql(dqlSegments.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("<d_q>", "\""),filter!=null?filter.getFilters():null,filter!=null?filter.getParamValues():null,dataSource);
				writer.print(dql);
			} else if ("querySqlData".equals(oper)) {
				//reportId:conf.reportId,sqlTemplate:sqlTemplate,sqlId:sqlId,dataSource:currsd.dataSource,sql:currsd.sql
				writer = res.getWriter();
				String reportId = req.getParameter("reportId");
				String sqlTemplate = req.getParameter("sqlTemplate");
				String sqlId = req.getParameter("sqlId");
				String dataSource = req.getParameter("dataSource");
				String sql = req.getParameter("sql");
				if (sql == null || dataSource == null) {
					if (sqlId == null) throw new RQException("code logic error!");
					SqlDataset sd = SqlDataset.DATASETS.get(sqlId);
					if (sd == null) throw new RQException("not find dataset of sqlId ["+sqlId+"]!");
					sql = sd.getSql();
					dataSource = sd.getDataSource();
				}
				sqlTemplate = sqlTemplate.replace("${SRCSQL}", "("+sql+")");

				int maxDataSize = 50000;
				try {
					maxDataSize = Integer.parseInt(req.getParameter("maxDataSize"));
				} catch (Exception e1) {
					Logger.warn("maxDataSize error : " + req.getParameter("maxDataSize"));
				}

				com.raqsoft.dm.Context ctx = new com.raqsoft.dm.Context();
				ctx.setParamValue("eval1", "dbCon=connect(\""+dataSource+"\")");
				ctx.setParamValue("eval2", "cs=dbCon.query(\""+sqlTemplate+"\").cursor()");
//				ctx.setParamValue("eval1", "dbCon=connect(\""+dataSource+"\")");
//				ctx.setParamValue("eval2", "cs=dbCon.cursor(\""+sqlTemplate+"\")");
//				ctx.setParamValue("eval4", "debug(cs)");
				
				String jsId = DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx, false);

				//int count = (Integer)ctx.getParam("cnt").getValue();
//				Object o2 = session.getAttribute(reportId);
//				if (o2 != null) {
//					session.removeAttribute(reportId);
//				}
				Object cs = ctx.getParam("cs").getValue();
				session.setAttribute(reportId, cs);
				session.setAttribute(reportId+JSID, jsId);
				int count = 1;
				boolean over = true;
				if(cs != null && cs instanceof ICursor){
					ICursor cursor = (ICursor) ctx.getParam("cs").getValue();
					Sequence t = cursor.fetch();
					count = t.count(null);
					cursor.reset();
				}
				if (count>maxDataSize) {
					over = false;
				}
//				BuiltinDataSetConfig bdsc = new BuiltinDataSetConfig();
//				bdsc.setName("ds1");
//				Logger.debug("querySqlData : " + sqlTemplate);
//
//				DBSession dbs = null;
//				Connection con = null;
//				Statement stmt = null;
//				try {
//					ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dataSource);
//					dbs = isf.getSession();
//					con = (Connection)dbs.getSession();
//					stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
//					stmt.setFetchSize(1000);
//					ResultSet rs = stmt.executeQuery(sqlTemplate);
//					java.sql.ResultSetMetaData rsmd = rs.getMetaData();
//					String names[] = new String[rsmd.getColumnCount()];
//					byte colTypes[] = new byte[rsmd.getColumnCount()];
//					for (int i=1; i<=rsmd.getColumnCount(); i++) {
//						names[i-1] = rsmd.getColumnLabel(i);
//						colTypes[i-1] = Types.getTypeBySQLType(rsmd.getColumnType(i));
//					}
//					ArrayList<String[]> values = new ArrayList<String[]>();
//					
//					while (rs.next()) {
//						if (count>maxDataSize) {
//							over = false;
//							break;
//						}
//						count++;
//						String[] value = new String[names.length];
//						for (int i=0; i<value.length; i++) {
//							Object o9 = rs.getObject(names[i]);
//							value[i] = o9==null?"":o9.toString();
//						}
//						values.add(value);
//					}
//					if (count > 0) {
//						bdsc.setColNames(names);
//						bdsc.setColTypes(colTypes);
//						String[][] vs = new String[values.size()][];
//						bdsc.setValues(values.toArray(vs));
//						Object o2 = session.getAttribute(reportId);
//						if (o2 != null) {
//							session.removeAttribute(reportId);
//						}
//						DfxData dd = new DfxData(bdsc);
//						dd.setReportId(reportId);
//						session.setAttribute(reportId, dd);
//						
//					} 
//				} finally {
//					try {
//						con.close();
//					} catch (Exception e) {
//						Logger.warn(e);
//					}
//					try {
//						dbs.close();
//					} catch (Exception e) {
//						Logger.warn(e);
//					}
//				}
				writer.println(100+":"+(over?1:0));
			} else if ("queryDqlData".equals(oper)) {
				writer = res.getWriter();
				String reportId = req.getParameter("reportId");
				String dql = req.getParameter("dql");
				String dqlSegments = req.getParameter("dqlSegments");
				String dataSource = req.getParameter("dataSource");
				String outerConditionId = req.getParameter("filter");
				if (outerConditionId == null) {
					outerConditionId = "default";
				}

				Object filters = session.getAttribute("_raqsoft_filters_");
				DQLTableFilter filter = null;
				if (filters != null) {
					ArrayList os = (ArrayList)filters;
					for (int i=0; i<os.size(); i++) {
						DQLTableFilter fi = (DQLTableFilter)os.get(i);
						if (fi.getID().equals(outerConditionId)) { // && fi.getDataSource().equals(dataSource)
							filter = fi;
							break;
						}
					}
				}

				if (dqlSegments != null) dql = DqlUtil.getDql(dqlSegments.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("<d_q>", "\""),filter!=null?filter.getFilters():null,filter!=null?filter.getParamValues():null,dataSource);
				int maxDataSize = 50000;
				try {
					maxDataSize = Integer.parseInt(req.getParameter("maxDataSize"));
				} catch (Exception e1) {
					Logger.warn("maxDataSize error : " + req.getParameter("maxDataSize"));
				}
				JSONObject calcFieldTypeJSON = null;
				try{
					if(req.getParameter("calcFieldTypeJSON") != null && req.getParameter("calcFieldTypeJSON").length() > 0)
						calcFieldTypeJSON = new JSONObject(req.getParameter("calcFieldTypeJSON"));
				}catch(Exception e){
					Logger.debug(e.getMessage());
				}
				
				com.raqsoft.dm.Context ctx = new com.raqsoft.dm.Context();
				ctx.setParamValue("eval1", "dbCon=connect(\""+dataSource+"\")");
				ctx.setParamValue("eval2", "cs=dbCon.cursor(\""+dql+"\")");
				//ctx.setParamValue("eval3", "dbCon.close()");
//				ctx.setParamValue("eval4", "debug(cs.fetch())");
//				ctx.setParamValue("eval5", "cs.reset()");
				String jsId = DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx, false);

				boolean over = true;
//				Object o2 = session.getAttribute(reportId);
//				if (o2 != null) {
//					session.removeAttribute(reportId);
//				}
				session.setAttribute(reportId, ctx.getParam("cs").getValue());
				session.setAttribute(reportId+JSID, jsId);
				//cacheCursor.put(reportId, ctx.getParam("cs").getValue());
				
//				HashMap calcFieldsTypes = new HashMap();
//				Iterator it = calcFieldTypeJSON.keys();
//				while(it.hasNext()){
//					calcFieldTypeJSON.get((String) it.next());
//				}
				
//				BuiltinDataSetConfig bdsc = new BuiltinDataSetConfig();
//				bdsc.setName("ds1");
//				int count = 1;
//				boolean over = true;
//				Logger.debug("queryDqlData : " + dql);
//
//				DBSession dbs = null;
//				Connection con = null;
//				Statement stmt = null;
//				try {
//					ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dataSource);
//					dbs = isf.getSession();
//					con = (Connection)dbs.getSession();
//					stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
//					stmt.setFetchSize(1000);
//					ResultSet rs = stmt.executeQuery(dql);
//					java.sql.ResultSetMetaData rsmd = rs.getMetaData();
//					String names[] = new String[rsmd.getColumnCount()];
//					byte colTypes[] = new byte[rsmd.getColumnCount()];
//					for (int i=1; i<=rsmd.getColumnCount(); i++) {
//						names[i-1] = rsmd.getColumnLabel(i);
//						colTypes[i-1] = Types.getTypeBySQLType(rsmd.getColumnType(i));
//						if(calcFieldTypeJSON != null) {
//							try{
//								Object cftype = calcFieldTypeJSON.get(names[i-1]);
//								if(cftype != null && cftype instanceof String){//是计算字段
//									String cftypeString = (String) cftype;
//									if(cftypeString.length() > 0) {
//										colTypes[i-1] = DfxData.getType(cftypeString);
//									}
//								}
//							}catch(org.json.JSONException e){}
//						}
//					}
//					ArrayList<String[]> values = new ArrayList<String[]>();
//					
//					while (rs.next()) {
//						if (count>maxDataSize) {
//							over = false;
//							break;
//						}
//						count++;
//						String[] value = new String[names.length];
//						for (int i=0; i<value.length; i++) {
//							Object o9 = rs.getObject(names[i]);
//							value[i] = o9==null?"":o9.toString();
//						}
//						values.add(value);
//					}
//					if (count > 0) {
//						bdsc.setColNames(names);
//						bdsc.setColTypes(colTypes);
//						String[][] vs = new String[values.size()][];
//						bdsc.setValues(values.toArray(vs));
//						Object o2 = session.getAttribute(reportId);
//						if (o2 != null) {
//							session.removeAttribute(reportId);
//						}
//						DfxData dd = new DfxData(bdsc);
//						dd.setReportId(reportId);
//						session.setAttribute(reportId, dd);
//						
//					} 
//				} finally {
//					try {
//						con.close();
//					} catch (Exception e) {
//						Logger.warn(e);
//					}
//					try {
//						dbs.close();
//					} catch (Exception e) {
//						Logger.warn(e);
//					}
//				}
				writer.println(100+":"+(over?1:0));
			} else if ("queryGroupDqlData".equals(oper)) {
				writer = res.getWriter();
				String reportId = req.getParameter("reportId");
				String dql = req.getParameter("dql");
				String dqlSegments = req.getParameter("dqlSegments");
				String dataSource = req.getParameter("dataSource");
				String outerConditionId = req.getParameter("filter");
				String glmdDataType = req.getParameter("glmdDataType");
				JSONArray gdtJsonArr = null;
				boolean useGlmdType = true;
				if(glmdDataType != null) {
					try{
						gdtJsonArr = new JSONArray(glmdDataType);
					}catch(Exception e){
						Logger.debug(e);
						Logger.debug("获取数据类型有误，使用dqljdbc查询数据类型");
						useGlmdType = false;
					}
				}
				if (outerConditionId == null) {
					outerConditionId = "default";
				}

				Object filters = session.getAttribute("_raqsoft_filters_");
				DQLTableFilter filter = null;
				if (filters != null) {
					ArrayList os = (ArrayList)filters;
					for (int i=0; i<os.size(); i++) {
						DQLTableFilter fi = (DQLTableFilter)os.get(i);
						if (fi.getID().equals(outerConditionId)) { // && fi.getDataSource().equals(dataSource)
							filter = fi;
							break;
						}
					}
				}

				if (dqlSegments != null) dql = DqlUtil.getDql(dqlSegments.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("<d_q>", "\""),filter!=null?filter.getFilters():null,filter!=null?filter.getParamValues():null,dataSource);
				int maxDataSize = 50000;
				try {
					maxDataSize = Integer.parseInt(req.getParameter("maxDataSize"));
				} catch (Exception e1) {
					Logger.warn("maxDataSize error : " + req.getParameter("maxDataSize"));
				}
				
				BuiltinDataSetConfig bdsc = new BuiltinDataSetConfig();
				bdsc.setName("ds1");
				int count = 1;
				boolean over = true;
				Logger.debug("queryDqlData : " + dql);

				DBSession dbs = null;
				Connection con = null;
				Statement stmt = null;
				try {
					ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dataSource);
					dbs = isf.getSession();
					con = (Connection)dbs.getSession();
					stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					stmt.setFetchSize(1000);
					ResultSet rs = stmt.executeQuery(dql);
					java.sql.ResultSetMetaData rsmd = rs.getMetaData();
					String names[] = new String[rsmd.getColumnCount()];
					byte colTypes[] = new byte[rsmd.getColumnCount()];
					for (int i=1; i<=rsmd.getColumnCount(); i++) {
						names[i-1] = rsmd.getColumnLabel(i);
						//GlmdType获取正确的情况下，不使用dqljdbc查询出来的类型了，使用glmd里面设置的类型作为报表数据集字段类型
						if(useGlmdType) {
							for(int j = 0; j < gdtJsonArr.length(); j++){
								JSONObject jobj = gdtJsonArr.getJSONObject(j);
								if(names[i-1].equals(jobj.get("name"))){
									colTypes[i-1] = Byte.parseByte((String) jobj.get("type").toString());
									break;
								}else{
									if(j == gdtJsonArr.length() - 1){
										colTypes[i-1] = (byte)11;//如果没找到，默认字符串;2019.5.29统计字段这样做有问题
									}
									continue;
								}
							}
						}else{
							colTypes[i-1] = Types.getTypeBySQLType(rsmd.getColumnType(i));
						}
						
					}
					ArrayList<String[]> values = new ArrayList<String[]>();
					
					while (rs.next()) {
						if (count>maxDataSize) {
							over = false;
							break;
						}
						count++;
						String[] value = new String[names.length];
						for (int i=0; i<value.length; i++) {
							Object o9 = rs.getObject(names[i]);
							value[i] = o9==null?"":o9.toString();
						}
						values.add(value);
					}
					if (count > 0) {
						bdsc.setColNames(names);
						bdsc.setColTypes(colTypes);
						String[][] vs = new String[values.size()][];
						bdsc.setValues(values.toArray(vs));
						Object o2 = session.getAttribute(reportId);
						if (o2 != null) {
							session.removeAttribute(reportId);
						}
						DfxData dd = new DfxData(bdsc);
						dd.setReportId(reportId);
						session.setAttribute(reportId, dd.getDc());
						
					} 
				} finally {
					try {
						con.close();
					} catch (Exception e) {
						Logger.warn(e);
					}
					try {
						dbs.close();
					} catch (Exception e) {
						Logger.warn(e);
					}
				}
				writer.println(count+":"+(over?1:0));
			} else if ("getTableInfo".equals(oper)) {
				writer = res.getWriter();
				String dataId = req.getParameter("dataId");
				String sqlId = req.getParameter("sqlId");
				String sql = req.getParameter("sql");
				int isCursor = "1".equals(req.getParameter("isCursor"))?1:2;
				isCursor = 1;
				String isGlmd = req.getParameter("isGlmd");
				boolean glmd = "true".equals(isGlmd);
//				JSONArray gdtJsonArr = null;
//				if(glmdDataType != null) {
//					try{
//						gdtJsonArr = new JSONArray(glmdDataType);
//					}catch(Exception e){
//						Logger.debug(e);
//						Logger.debug("获取数据类型有误，使用dqljdbc查询数据类型");
//					}
//				}
				
				int scanRow = 100;
				try {
					scanRow = Integer.parseInt(req.getParameter("scanRow"));
				} catch(Exception e){}
				if (dataId != null) {
					dataId = DataSphereServlet.getFilePath(dataId);
					dataId = dataId.replaceAll("///", "/").replaceAll("//", "/");
					String dataFileType = req.getParameter("dataFileType");
					boolean isBtx = dataId.indexOf(".btx")>=0;
					dataFileType = isBtx?"binary":"text";

					writer.println(DfxData.getTableInfo(dataId,dataFileType,scanRow, glmd));
				} else if (sqlId != null || sql != null) {
					String dataSource = req.getParameter("dataSource");
					if (sql == null || dataSource == null) {
						if (sqlId == null) throw new RQException("code logic error!");
						SqlDataset sd = SqlDataset.DATASETS.get(sqlId);
						if (sd == null) throw new RQException("not find dataset of sqlId ["+sqlId+"]!");
						sql = sd.getSql();
						dataSource = sd.getDataSource();
					}
					sql = solveQl(sql, req);
					com.raqsoft.dm.Context ctx = new com.raqsoft.dm.Context();
					ctx.setParamValue("dataSource", dataSource);
					ctx.setParamValue("ql", sql);
					ctx.setParamValue("isCursor", isCursor);//
					ctx.setParamValue("returnRow", scanRow);
					DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/query.dfx", ctx);
					Param p = ctx.getParam("result");
					if (p == null || p.getValue() == null || !(p.getValue() instanceof Table)) throw new RQException("Query failed!");
					writer.println(DfxData.getTableInfo((Table)p.getValue(),scanRow,glmd));
				}
			} else if ("calc".equals(oper)) {
				writer = res.getWriter();
				String result;
				String reportId = req.getParameter("reportId");
				String isGlmd = req.getParameter("isGlmd");
				try {
					String dataId = req.getParameter("dataId");
					Logger.debug("data file is " + dataId);
					File f = new File(DataSphereServlet.getFilePath(dataId));
					if (!f.exists()) {
						//数据加载失败，过期已被删除 或 查询不到符合条件的数据，要重新查询吗？
						writer.write("{error:'"+GuideMessage.get(req).getMessage("guide.requery")+"',action:'reQuery'}");
						return;
					}
					String cacheType = req.getParameter("cacheType");
					DfxData dd = null;
//					Object o2 = session.getAttribute(reportId);
//					if (o2 != null) {
//						session.removeAttribute(dataId);
//					}
					dd = new DfxData(DataSphereServlet.getFilePath(dataId));//(DfxDataSetManager.getFile(dataId));//("D:/data/workspace/guide/web/WEB-INF/tmp/order");
					dd.setReportId(reportId);
					
//					if (o2 != null && o2 instanceof File && !"where".equals(reportId)) {
//						//session.removeAttribute(dataId);
//						//o2 = null;
//						//dd = (DfxData)o2;
//						dd = new DfxData(((File)o2).getPath());
//						dd.setReportId(reportId);
//
//					} else {
//						dd = new DfxData(DataSphereServlet.getFilePath(dataId));//(DfxDataSetManager.getFile(dataId));//("D:/data/workspace/guide/web/WEB-INF/tmp/order");
//						dd.setReportId(reportId);
//						session.setAttribute(reportId, dd);
//					}
//					dd = new DfxData(DataSphereServlet.getFilePath(dataId));//(DfxDataSetManager.getFile(dataId));//("D:/data/workspace/guide/web/WEB-INF/tmp/order");
//					dd.setReportId(reportId);

					int maxDataSize = 50000;
					try {
						maxDataSize = Integer.parseInt(req.getParameter("maxDataSize"));
					} catch (Exception e1) {
						Logger.warn("maxDataSize error : " + req.getParameter("maxDataSize"));
					}

					DfxQuery dq = null;
					Object o3 = session.getAttribute(dataId);
					if (o3 != null && o3 instanceof DfxQuery) {
						dq = (DfxQuery)o3;
					}
					if (dq != null) dq.setPause(true);
					
					boolean isQuery = "1".equals(req.getParameter("isQuery"));
					String dataFileType = req.getParameter("dataFileType");
					boolean isBtx = dataId.indexOf(".b")>=0;
					dataFileType = isBtx?"binary":"text";
					if("binary".equalsIgnoreCase(dataFileType) && !DataSphereServlet.binAuth){
						//验证集算器授权ip不可用
						throw new Exception("对不起，集算器授权不可用，不能使用bin格式缓存");
					}
					result = dd.calc(req.getParameter("calcs"), req.getParameter("filters"), req.getParameter("fields"), req.getParameter("resultExp"), cacheType, req.getParameter("types"),dataFileType, maxDataSize, isGlmd,req.getParameter("srcTypes"), req.getParameter("aggrFieldFilters"),isQuery);
					if (dq != null) dq.setPause(false);
					session.setAttribute(reportId, dd.getDc());
					cacheCursor.put(reportId, dd.getDc());
					writer.println(result);
				} catch (Exception e) {
					e.printStackTrace();
					String err = e.getMessage().replaceAll("\n\r", " ").replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("'", " ").replaceAll("\"", " ");
					//错误信息：{0}！ 要重新查询数据吗？
					writer.write("{error:'"+GuideMessage.get(req).getMessage("guide.requery2",err)+"',action:'reQuery'}");
					session.removeAttribute(reportId);
				}
				
			} else if ("getInputTableNames".equals(oper)) {
				com.raqsoft.app.config.ConfigUtil.checkEsprocExpiration();
				writer = res.getWriter();
				String srcFiles = req.getParameter("srcFiles");
				ArrayList<String> al = DfxQuery.getInputFileTables(srcFiles, new Sequence());
				String tableNames = "";
				String currTable = "";
				for (int i=0; i<al.size(); i++) {
					if (i>0) tableNames += ",";
					tableNames += "'" + al.get(i) + "'";
				}
				//currTable = al.getCurrTable();
				writer.print("["+tableNames + "]");
			} else if ("getInputTables".equals(oper)) {
				writer = res.getWriter();
				String dataId = req.getParameter("dataId");
				o = session.getAttribute(dataId);
				if (o == null) {
					writer.println("error:Time out!");
					return;
				}
				DfxQuery dq = (DfxQuery)o;
				ArrayList<String> al = dq.getTableNames();
				String tableNames = "";
				String currTable = "";
				for (int i=0; i<al.size(); i++) {
					if (i>0) tableNames += ",";
					tableNames += "'" + al.get(i) + "'";
				}
				currTable = dq.getCurrTable();
				writer.print("["+tableNames + "]<|>"+currTable);
			} else if ("getLoadedStatus".equals(oper)) {
				writer = res.getWriter();
				String dataId = req.getParameter("dataId");
				o = session.getAttribute(dataId);
				if (o == null) {
					writer.println("error:Time out! Dataset:["+dataId+"] may empty!");
					Logger.debug("error:Time out!"+dataId);
					return;
				}
				DfxQuery dq = (DfxQuery)o;
				dq.setLastVisitTime(System.currentTimeMillis());
//				if (dq.isOver()) {
//					DfxDataSetManager.add(dataId, dq.getDataFile());
//				}
				writer.println("{loadedRow:'"+dq.getLoadedRow(req.getParameter("calcFields"),req.getParameter("filter"),req.getParameter("fields"))+"',error:'"+dq.getError()+"',over:'"+(dq.isOver()?1:0)+"'}");				
			} else if ("changeFilter".equals(oper)) {
				writer = res.getWriter();
				String dataId = req.getParameter("dataId");
				o = session.getAttribute(dataId);
				if (o == null) {
					writer.println("error:Time out!");
					return;
				}
				DfxQuery dq = (DfxQuery)o;
				String currTable = req.getParameter("currTable");
				if (currTable != null && currTable.length() == 0)
					dq.setFilter(req.getParameter("filter"));
				else dq.setTableFilter(currTable, req.getParameter("filter"));
				writer.println("ok");
				//writer.println("{loadedRow:'"+dq.getLoadedRow(req.getParameter("calcFields"),req.getParameter("filter"),req.getParameter("fields"))+"',error:'"+dq.getError()+"',over:'"+(dq.isOver()?1:0)+"'}");				
			} else if ("getRows".equals(oper)) {
				writer = res.getWriter();
				String dataId = req.getParameter("dataId");
				o = session.getAttribute(dataId);
				if (o == null) {
					//dd = new DfxData(DataSphereServlet.ROOT_PATH+dataId);
					writer.println("error:Time out!");
					return;
				}
				DfxQuery dq = (DfxQuery)o;
				int begin = Integer.parseInt(req.getParameter("begin"));
				int end = Integer.parseInt(req.getParameter("end"));
				writer.println(dq.getRows(begin,end,req.getParameter("calcFields"),req.getParameter("filter"),req.getParameter("fields")));				
			} else if ("generateGuideTrees".equals(oper)) {
				writer = res.getWriter();
				//Thread.currentThread().sleep(1000000);
				String file = req.getParameter("file");
				File f = new File(DataSphereServlet.getFilePath(DataSphereServlet.dimDataFolderOnServer+file));	
				boolean reload = !"no".equalsIgnoreCase(req.getParameter("autoReload"));
				String trees = req.getParameter("trees");
				if (f.exists() && f.length()>0) {
					if (reload) {// && trees != null && trees.length()>0 
						f.delete();
					} else {
						writer.println(FileUtils.readFile(f).replaceAll("\\\n", "").replaceAll("\\\t", ""));
						return;
					}
				}
				if (trees == null || trees.length()==0) {
					writer.print("no");
					return;
				} 
				int maxDimSize = 5000;
				try {
					maxDimSize = Integer.parseInt(req.getParameter("maxDimSize"));
				} catch (Exception e1) {
				}
				String dataSource = req.getParameter("dataSource");
				String[] ts = trees.split("_;_");
				//String[] dimNames = new String[ts.length];
				//String[][] fields1 = new String[ts.length][];
				//String[][] fields2 = new String[ts.length][];
				Sequence seq = new Sequence();
				
				com.raqsoft.dm.Context ctx = new com.raqsoft.dm.Context();
				for (int i=0; i<ts.length; i++) {
					String tsi[] = ts[i].split("_,_");
					String dimName = tsi[0];
//					String vs = tsi[1];
					if (tsi.length>2) {
						String table = tsi[2];
						String code = tsi[3];
						String disp = "";
						if (tsi.length>4) disp = tsi[4];
						String dimTitle = null;
						if (tsi.length>5) dimTitle = tsi[5];
						//String levels = tsi[];
						String dql = "SELECT t1."+code+" CODE" + (disp.length()>0?",t1."+disp+" DISP":"");
						for (int j = 6; j<tsi.length; j++) {
							String tsij[] = tsi[j].split("_:_");
							dql += "," + tsij[1].replaceAll("[?]","t1."+code) + " " + tsij[0];
						}
						dql += " FROM " + table + " t1";
						Logger.debug(dimName + " : " + dql);
						try {
							ctx.setParamValue("eval1", "dbCon=connect(\""+dataSource+"\")");
							ctx.setParamValue("eval2", dimTitle+"=B1.query(\""+dql+"\")");
							ctx.setParamValue("eval3", "file(\""+DataSphereServlet.ROOT_PATH+"/temp/"+dimTitle+"\").export@t("+dimTitle+")");
							//ctx.setParamValue("eval4", "dbCon.close()");
							DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx);
							//DfxUtils.execDfxScript("=connect(\""+dataSource+"\")\t="+dimName+"=A1.query(\""+dql+"\")\t=file(\""+DataSphereServlet.ROOT_PATH+"/temp/"+dimName+"\").export@t("+dimName+")\t=debug("+dimName+")", ctx, true);
							seq.add(dimTitle);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						//TODO
					}
				}
				try {
					ctx.setParamValue("trees", seq);
					DfxUtils.execDfxFile(DataSphereServlet.class.getResourceAsStream("/com/raqsoft/guide/web/dfx/trees.dfx"), ctx, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				/**
				[
					{
						name:''
						,data : [
							{ id:2, pId:1, name:"张颖",real:"1",dim:'雇员'}
						]
					}
				]
				 */
				StringBuffer s = new StringBuffer();
				Logger.debug("seq length : "+seq.length());
				s.append("[\n");
				boolean first = true;
				for (int i=1; i<=seq.length(); i++) {
					String name = seq.get(i).toString();
					com.raqsoft.dm.Param p = ctx.getParam(name+"_tree");
					if (p == null) continue;
					Sequence si = (Sequence)p.getValue();
					Sequence levels = (Sequence)ctx.getParam(name+"_levels").getValue();
					s.append("\t");
					if (!first) s.append(",");
					first = false;
					//Logger.debug(levels);
					//Logger.debug(si);
					this.currId = i*1000000;
					s.append("{\n");
					s.append("\t\t\"name\":\""+name+"\"\n");
					s.append("\t\t,\"data\":[\n");
					genJson(s, si, levels, name, 1, 0, true,maxDimSize);
					s.append("\t\t]\n");
					s.append("\t}\n");
				}
				s.append("]\n");
				//Logger.debug(s.toString());
				FileUtils.saveFile(s.toString(), f);
				writer.println(s.toString().replaceAll("\\\n", "").replaceAll("\\\t", ""));				
			} else if ("genReport".equals(oper)) {
				writer = res.getWriter();
				String reportId = req.getParameter("reportId");
				String dataId = null;
				dataId = req.getParameter("dataId");
				CacheManager manager = CacheManager.getInstance();
				manager.deleteReportEntry(reportId);				
				String title = req.getParameter("title");
				//客户可拦截转义尖括号
				String rpt = req.getParameter("rpt").replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("<d_q>", "\"");
				Record r = (Record)JSONUtil.parseJSON(rpt.toCharArray(), 0, rpt.length()-1);
				Object o1 = r.getFieldValue("rows");
				Object o2 = r.getFieldValue("cols");
				boolean zongji = Boolean.parseBoolean(req.getParameter("zongji"));
				Table rows = null;
				if (o1 instanceof Table) rows = (Table)o1;
				Table cols = null;
				if (o2 instanceof Table) cols = (Table)o2;
				Table cells = (Table)r.getFieldValue("cells");
				int rowCount = (Integer)r.getFieldValue("rowCount");
				int colCount = (Integer)r.getFieldValue("colCount");
				String lefts = req.getParameter("lefts");
				String tops = req.getParameter("tops");
				String _pagerStyle = req.getParameter("pagerStyle");
				if(_pagerStyle == null) _pagerStyle = "1";
				int pagerStyle = Integer.parseInt(_pagerStyle);
				String resultRpxPrefixOnServer = req.getParameter("resultRpxPrefixOnServer");
				boolean onlyRefreshDs = "yes".equals(req.getParameter("onlyRefreshDs"));
				BuiltinDataSetConfig bdsc = null;
				EsProcDataSetConfig edsc = null;
				DataSetConfig idsc = null;
				//Object o8 = cacheCursor.get(reportId);//
				Object o8 = session.getAttribute(reportId);
				Object o9 = session.getAttribute(reportId+JSID);
				Logger.debug("dataset for olap 1 : "+o8);
				
				boolean isBig = "1".equals(session.getAttribute("isBig"));
				//isBig = true;
				if (isBig) {
					edsc = new EsProcDataSetConfig();
					edsc.setName("ds1");
					
					edsc.setDfxFileName(DataSphereServlet.getFilePath("/WEB-INF/files/dfx/official/analyseReportDS.dfx"));
					//edsc.setDfxFileName("/com/raqsoft/guide/web/dfx/analyseReportDS.dfx");
					List<String> names = new ArrayList<String>();
					List<String> exps = new ArrayList<String>();
					String cn = reportId + "_cursor_";
					names.add("cursorName");
					exps.add("\""+cn+"\"");
					Env.setParamValue(cn, o8);
					edsc.setParamNames(names);
					edsc.setParamExps(exps);
					edsc.setDataMode(EsProcDataSetConfig.MODE_CACHE);
					idsc = edsc;
				} else {
					String fields = req.getParameter("dsj_fields");
					JSONArray j1 = null;
					Map<String, Byte> fieldType = new HashMap<String, Byte>();
					if(fields != null) {
						j1 = new JSONArray(req.getParameter("dsj_fields"));
						for (int i = 0; i < j1.length(); i++) {
							JSONObject jo = (JSONObject) j1.get(i);
							byte btype = 2;
							try{
								btype = Byte.parseByte(jo.get("dataType")+"");
							}catch(Exception e) {
								btype = Byte.parseByte(jo.get("_finalType")+"");
							}
							String _fieldType = null;
							try {
								_fieldType = jo.get("_fieldType").toString();
								if("aggr".equals(_fieldType)) {
									//字符串聚合字段,max min 还是字符串
									if(!jo.get("aggr").toString().toLowerCase().startsWith("m")) {
										 btype = 1;
									}
								}
							}catch(org.json.JSONException e) {
								
							}
							
							
							fieldType.put(jo.getString("name"), btype);
						}
					}
					bdsc = new BuiltinDataSetConfig();
					bdsc.setName("ds1");
					com.raqsoft.dm.Context ctx = new com.raqsoft.dm.Context();
					ctx.setParamValue("dc5", o8);
					
					ctx.setParamValue("eval1", "tbl2=dc5.fetch()");					
					ctx.setParamValue("eval3", "tbl2=tbl2.new(${tbl2.fname().concat(\",\")})");					
					ctx.setParamValue("eval5", "debug(tbl2)");
					DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx);
//					DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/dc5_fetch_tbl2.dfx", ctx);
					Table t = (Table)(ctx.getParam("tbl2").getValue());
					String[] names = null;//目前没查到数据会null
					if(t != null){//没查到数据
						names = t.dataStruct().getFieldNames();
						Logger.debug(t);
						if (o9 != null) JobSpaceManager.closeSpace(o9.toString());
						byte colTypes[] = new byte[names.length];
						for (int i=0; i<names.length; i++) {
							byte ti = Types.DT_STRING;
							if (fieldType.get(names[i]) != null) ti = fieldType.get(names[i]);
							colTypes[i] = DfxData.getType(ti+"");
						}
						
						bdsc.setColTypes(colTypes);
						bdsc.setColNames(names);
						String[][] ss = new String[t.length()][names.length];
						for (int i=0; i<ss.length; i++) {
							Record rr = t.getRecord(i+1);
							for (int j=0; j<names.length; j++) {
								Object oij = rr.getFieldValue(names[j]);
								if (oij!=null) {
									String s = oij.toString();
									s = s.replaceAll("\t", " ").replaceAll("\r\n", " ").replaceAll("\n", " ");
									//if (s.indexOf(".")>0) s = s.substring(0,s.indexOf("."));//数字断小数点，不需要这个步骤
									ss[i][j] = s;
								} else ss[i][j] = null;
							}
						}
						bdsc.setValues(ss);
					}else{
						names = new String[fieldType.size()];
						byte[] colTypes = new byte[fieldType.size()];
						Set<String> nameset = fieldType.keySet();
						int n = 0;
						for (String name : nameset) {
							byte ti = Types.DT_STRING;
							names[n] = name;
							colTypes[n] = DfxData.getType(ti+"");
							n++;
						}
						bdsc.setColTypes(colTypes);
						bdsc.setColNames(names);
					}

					idsc = bdsc;
				}
				int size = 10;
//				DfxData rc = null;
//				if (o8 instanceof DfxData) {
//					rc = (DfxData)o8;
//					bdsc = rc.getDs();
//					size = DfxData.getReportSize(rc.getTable(),lefts, tops);
//					if(rc.getTable() == null){//dataset type 6 7
//						size = DfxData.getReportSize(bdsc,lefts, tops);
//					}
//				} else {
//					bdsc = (BuiltinDataSetConfig)o8;
//					size = 10;//DfxData.getReportSize(bdsc,lefts, tops);
//				}

				
				int maxSize = 40000;
				try {
					maxSize = Integer.parseInt(req.getParameter( "maxSize" ));
				} catch (Exception e1) {
				}
				if (size > maxSize) {
					//error1 分组数据太多，导致交叉报表过大，建议：减少分组个数/减少分组值数量/改用分组报表（交叉报表的上表头调整到左表头）
					//error2 分组数据太多，导致报表过大，建议：减少分组个数/减少分组值数量
					if (lefts.length()>0 && tops.length()>0) writer.print("error:"+GuideMessage.get(req).getMessage("guide.error1")); 
					else writer.print("error:"+GuideMessage.get(req).getMessage("guide.error2"));
					return;
				}

				String rpxFile = resultRpxPrefixOnServer+title+".rpx";
				File f = new File(DataSphereServlet.getFilePath(rpxFile));
				File f2 = new File(DataSphereServlet.getFilePath(resultRpxPrefixOnServer+title+"2.rpx"));
//				if (false) {
//					ReportDefine rd = (ReportDefine)ReportUtils.read(f.getPath());
//					if (edsc != null) {
//						rd.setBigDataSetName("ds1");
//					}
//					if(pagerStyle == 0) {
//						rd.getPrintSetup().setPagerStyle(PrintSetup.PAGER_NONE);
//					}
//					DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
//					dsmd.addDataSetConfig(idsc); //把数据集定义添加到数据集元数据 
//					rd.setDataSetMetaData(dsmd); //把数据集元数据赋给ReportDefine
//
//					Logger.debug(f.getPath());
//					if (!new File(f.getParent()).exists())new File(f.getParent()).mkdirs(); 
//					new File(f.getParent()).mkdirs();
//					ReportUtils.write(f.getAbsolutePath(),rd);
//					ReportUtils.write(f2.getAbsolutePath(),rd);
//					
//					
//					Context cxt = new Context();
//					//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
//					Engine engine = new Engine(rd, cxt); //构造报表引擎
//					
//					IReport iReport = engine.calc(); //运算报表
//					session.setAttribute(reportId+"_REPORT", iReport);
//					writer.print("ok");
//					return;
//				}
				ReportDefine rd = new ReportDefine2(rowCount,colCount);
				//rd.getParamMetaData().addParam(new com.raqsoft.reParam("",""));
				if (rows != null) {
					for (int i=1; i<=rows.length(); i++) {
						Record r1 = rows.getRecord(i);
						IRowCell row = rd.getRowCell((Integer)r1.getFieldValue("row"));
						try {
							row.setRowType(((Integer)r1.getFieldValue("type")) != null ? ((Integer)r1.getFieldValue("type")).byteValue() : IRowCell.TYPE_NORMAL);
//							if(zongji){
//								if(row.getRowType() == IRowCell.TYPE_TABLE_HEADER || i == rows.length()){
//									row.setRowVisible(true);
//								}else{
//									row.setRowVisible(false);
//								}
//							}
						} catch(Exception e) {
							e.printStackTrace();
						}
						try {
							row.setRowHeight((Integer)r1.getFieldValue("height"));
						} catch(Exception e) {
						}
					}
				}
				if (cols != null) {
					for (int i=1; i<=cols.length(); i++) {
						Record c1 = cols.getRecord(i);
						IColCell col = rd.getColCell((Integer)c1.getFieldValue("col"));
						try {
							col.setColType(((Integer)c1.getFieldValue("type")).byteValue());
						} catch(Exception e) {
						}
						try {
							col.setColWidth((Integer)c1.getFieldValue("width"));
						} catch(Exception e) {
						}
					}
				}
				for (int i=1; i<=cells.length(); i++) {
					Record r1 = cells.getRecord(i);
					//{row:1,col:1,row2:1,col2:1,format:'',valueExp:'',value:'',extend:'',leftMain:'左主格',topMain:'上主格'}
					int row = (Integer)r1.getFieldValue("row");
					int col = (Integer)r1.getFieldValue("col");
					int row2 = (Integer)r1.getFieldValue("row2");
					int col2 = (Integer)r1.getFieldValue("col2");
					Object format = r1.getFieldValue("format");
					Object formatExp = null;
					try {
						formatExp = r1.getFieldValue("formatExp");
					} catch (Exception e1) {
					}
					Object valueExp = r1.getFieldValue("valueExp");
					Object value = r1.getFieldValue("value");
					byte extend = ((Integer)r1.getFieldValue("extend")).byteValue();
					Object leftMain = r1.getFieldValue("leftMain");
					Object topMain = r1.getFieldValue("topMain");
					Object tip = r1.getFieldValue("tip");
					String dispExp = null;
					if(r1.getFieldValue("dispExp") != null)
						dispExp = r1.getFieldValue("dispExp").toString().replaceAll("'", "\"");
					String backColor = r1.getFieldValue("backColor").toString();
					String backColorExp = null;
					try {
						backColorExp = r1.getFieldValue("backColorExp").toString();
					} catch (Exception e1) {
					}
					//Logger.debug("backColorExp : "+backColorExp);
					
					String color = r1.getFieldValue("color").toString();
					byte hAlign = ((Integer)r1.getFieldValue("hAlign")).byteValue();
					byte adjustSizeMode = ((Integer)r1.getFieldValue("adjustSizeMode")).byteValue();
					String textWrap = r1.getFieldValue("textWrap").toString();
					//adjustSizeMode:48,textWrap:1,hAlign:208/209/210,color:'',backColor:''
					INormalCell inc=rd.getCell(row,col);
					//图片
					if( row > 1 ){
						INormalCell colTopCell = rd.getCell(1, col);
						if(colTopCell != null && dataId != null && ActionResultPage.cacheBlobFields.get(dataId) != null && ActionResultPage.cacheBlobFields.get(dataId).contains(colTopCell.getValue())) {
							inc.setCellType((byte) -63);
						}
					}
					IByteMap map=new ByteMap();
					inc.setExpMap(map);
					if (row != row2 || col != col2) {
						ReportUtils.mergeReport(rd,new Area(row,col,row2,col2));
					}
					if (format!= null && format.toString().length()>0) {
						if(!format.equals("_noformat")) inc.setFormat(format.toString());
					} else if (formatExp!= null && formatExp.toString().length()>0) {
						map.put(INormalCell.FORMAT, formatExp.toString());
					} else map.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),if(pos(string(value()),\":\")==null,\"yyyy-MM-dd\",\"yyyy-MM-dd HH:mm:ss\"),\"\"))");
					//if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\")))
					if (valueExp!=null && valueExp.toString().length()>0) map.put(INormalCell.VALUE, valueExp);
					if (dispExp!=null && dispExp.toString().length()>0) map.put(INormalCell.DISPVALUE, dispExp);
					if (backColorExp!=null && backColorExp.toString().length()>0) map.put(INormalCell.BCOLOR, backColorExp);
					if (value!=null && value.toString().length()>0) inc.setValue(value.toString());
					if (leftMain!=null && leftMain.toString().length()>0) inc.setLeftHead(leftMain.toString());
					if (topMain!=null && topMain.toString().length()>0) inc.setTopHead(topMain.toString());
					if (tip != null && tip.toString().length()>0) inc.setTip(tip.toString());
					inc.setExtendMode(extend);
					if (backColor.length()>0) inc.setBackColor(getRGB(backColor));
					if (color.length()>0) inc.setForeColor(getRGB(color));
					inc.setHAlign(hAlign);
					inc.setAdjustSizeMode(adjustSizeMode);
					if (textWrap.length()>0) inc.setTextWrap("1".equals(textWrap));
					inc.setIndent(2);
					try {
						inc.setDiagonalStyle(((Integer)r1.getFieldValue("diagonal")).byteValue());
						inc.setDiagonalWidth(new Double(0.75).floatValue());
						if (row == 1 && col == 1) {
							int widths = (col2-col+1)*25;
							int w = 25;
							if(widths<50) w = 50/(col2-col+1);
							int heights = (row2-row+1)*8;
							int h = 8;
							if(heights<16) h = 16/(row2-row+1);
							for (int m=1; m<=col2; m++) {
								//2017/05/26去掉
								//rd.getColCell(m).setColWidth(w);
							}
							for (int m=1; m<=row2; m++) {
								//rd.getRowCell(m).setRowHeight(h);
							}
						}
					} catch(Exception e) {}
				}
				ReportStyle style = new ReportStyle("单色");
				int color = new Color(165,216,255).getRGB();
				for(int i=1;i<=rowCount;i++){ 
					for(int j=1;j<=colCount;j++){
						// A5D8FF
						
						rd.setBBColor(i,(short)j, color); //设定下边框线色 
						rd.setBBStyle(i,(short)j, style.borderStyle); //设定下边框类型 
						rd.setBBWidth(i,(short)j, style.boderWidth); //设定下边框线粗 
						
						//左边框 
						rd.setLBColor(i,(short)j, color); 
						rd.setLBStyle(i,(short)j, style.borderStyle); 
						rd.setLBWidth(i,(short)j, style.boderWidth); 
						//右边框 
						rd.setRBColor(i,(short)j, color); 
						rd.setRBStyle(i,(short)j, style.borderStyle); 
						rd.setRBWidth(i,(short)j, style.boderWidth); 
						//上边框 
						rd.setTBColor(i,(short)j, color); 
						rd.setTBStyle(i,(short)j, style.borderStyle);
						rd.setTBWidth(i,(short)j, style.boderWidth);
					}
				}
				
				rd.setReportType(ReportDefine.RPT_NORMAL);
				DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
				dsmd.addDataSetConfig(idsc); //把数据集定义添加到数据集元数据 
				rd.setDataSetMetaData(dsmd); //把数据集元数据赋给ReportDefine
				
				if(pagerStyle == 0) {
					rd.getPrintSetup().setPagerStyle(PrintSetup.PAGER_NONE);
				}
				if (edsc != null) {
					rd.setBigDataSetName("ds1");
				}
				try {
					Logger.debug(f.getPath());
					if (!new File(f.getParent()).exists())new File(f.getParent()).mkdirs(); 
					new File(f.getParent()).mkdirs();
					ReportUtils.write(f.getAbsolutePath(),rd);
				} catch (Exception e) {
					Logger.warn("write rpx error : ", e);
				}
				//cxt.setDefDataSourceName(dbName);
				//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
				
//				if (rc != null) {
//					rc.setIReport(iReport);
//					rc.setReportDefine(rd);
//				}
				
				if (edsc != null) {
					//Context cxt = new Context();
					//cxt.setParamValue("dataCursor", o8);
					//BigEngine engine = new BigEngine(rd, cxt); //构造报表引擎
					//IReport iReport = engine.getPage(arg0, arg1).calc(); //运算报表
					session.setAttribute(reportId+"_REPORT_FILE", f.getAbsolutePath());
				} else {
					Context cxt = new Context();
					//cxt.setParamValue("dataCursor", o8);
					Engine engine = new Engine(rd, cxt); //构造报表引擎
					IReport iReport = engine.calc(); //运算报表
					session.setAttribute(reportId+"_REPORT", iReport);
				}
				writer.print("ok");

			} else if ("calcReport".equals(oper)) {
				writer = res.getWriter();
				String reportId = req.getParameter("reportId");
				//2018.12.25
				CacheManager manager = CacheManager.getInstance();
				manager.deleteReportEntry(reportId);	
				String title = req.getParameter("title");
				String reportType = req.getParameter("reportType");
				String tops = req.getParameter("tops");
				String lefts = req.getParameter("lefts");
				String fields = req.getParameter("fields");
				String structType = req.getParameter("structType");
				boolean isRowData = "1".equals(req.getParameter("isRowData"));
				String _pagerStyle = req.getParameter("pagerStyle");
				if(_pagerStyle == null) _pagerStyle = "1";
				int pagerStyle = Integer.parseInt(_pagerStyle);
				String template = req.getParameter("template");
				String fpath = DataSphereServlet.getFilePath(template);
				
				BuiltinDataSetConfig bdsc = null;
				EsProcDataSetConfig edsc = null;
				DataSetConfig idsc = null;
				//Object o8 = cacheCursor.get(reportId);//
				Object o8 = session.getAttribute(reportId);
				Object o9 = session.getAttribute(reportId+JSID);
				Logger.debug("dataset for olap 2 : "+o8);
				
				boolean isBig = "1".equals(session.getAttribute("isBig"));
				//isBig = true;
				if (isBig) {
					edsc = new EsProcDataSetConfig();
					edsc.setName("ds1");
					
					edsc.setDfxFileName(DataSphereServlet.getFilePath("/WEB-INF/files/dfx/official/analyseReportDS.dfx"));
					//edsc.setDfxFileName("/com/raqsoft/guide/web/dfx/analyseReportDS.dfx");
					List<String> names = new ArrayList<String>();
					List<String> exps = new ArrayList<String>();
					String cn = reportId + "_cursor_";
					names.add("cursorName");
					exps.add("\""+cn+"\"");
					Env.setParamValue(cn, o8);
					edsc.setDataMode(EsProcDataSetConfig.MODE_CACHE);
					edsc.setParamNames(names);
					edsc.setParamExps(exps);
					idsc = edsc;
				} else {
					String dsj_fields = req.getParameter("dsj_fields");
					JSONArray j1 = null;
					Map<String, Byte> fieldType = new HashMap<String, Byte>();
					if(dsj_fields != null) {
						j1 = new JSONArray(dsj_fields);
						for (int i = 0; i < j1.length(); i++) {
							JSONObject jo = (JSONObject) j1.get(i);
							byte btype = Byte.parseByte(jo.get("dataType")+"");
							try{
								btype = Byte.parseByte(jo.get("_finalType")+"");
							}catch(Exception e) {
								try{
//									String ftype = jo.getString("_fieldType");
//									if("aggr".equals(ftype)) btype = 1;
								}catch(Exception e2) {
									
								}
							}
							
							
							fieldType.put(jo.getString("name"), btype);
						}
					}

					
					bdsc = new BuiltinDataSetConfig();
					bdsc.setName("ds1");
					com.raqsoft.dm.Context ctx = new com.raqsoft.dm.Context();
					ctx.setParamValue("dc5", o8);
					ctx.setParamValue("eval1", "tbl2=dc5.fetch()");					
					//ctx.setParamValue("eval2", "debug(tbl2)");					
					ctx.setParamValue("eval3", "tbl2=tbl2.new(${tbl2.fname().concat(\",\")})");					
					//ctx.setParamValue("eval4", "debug(\"tbl2\")");					
					ctx.setParamValue("eval5", "debug(tbl2)");
					DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx);
					Table t = (Table)(ctx.getParam("tbl2").getValue());
					String[] names = t.dataStruct().getFieldNames();
					//t.dataStruct().g
					Logger.debug(t);
					if (o9 != null) JobSpaceManager.closeSpace(o9.toString());
					if (t.length() == 0) throw new Exception("No qualified data");
					
					//TODO
					//for (int i=0; i<colTypes.length; i++) colTypes[i] = Types.DT_STRING;
					bdsc.setColNames(names);
					String[][] ss = new String[t.length()][names.length];
					Map<String,Byte> types = DfxData.getTableFieldTypes(t, 100, false);
					for (int i=0; i<ss.length; i++) {
						Record rr = t.getRecord(i+1);
						for (int j=0; j<names.length; j++) {
							Object oij = rr.getFieldValue(names[j]);
							if (oij!=null) {
								String s = oij.toString();
								s = s.replaceAll("\t", " ").replaceAll("\r\n", " ").replaceAll("\n", " ");
								//if (s.indexOf(".")>0) s = s.substring(0,s.indexOf("."));
								ss[i][j] = s;
							} else ss[i][j] = null;
						}
					}
					bdsc.setValues(ss);

					//2021.3.26 模板报表类型错误问题
//					int cateNumber = 1;
//					String fname = new File(fpath).getName();
//					if (fname.indexOf("2")>=0) cateNumber = 2;
//					else if (fname.indexOf("3")>=0) cateNumber = 3;
//					else if (fname.indexOf("4")>=0) cateNumber = 4;
					byte colTypes[] = new byte[names.length];
					for (int j=0; j<names.length; j++) {
//						if (j<cateNumber) colTypes[j] = Types.DT_STRING;else 
						//if (j==names.length-1) colTypes[j] = Types.DT_INT;else //总计
						colTypes[j] = types.get(names[j]);
					}
					bdsc.setColTypes(colTypes);

					idsc = bdsc;
				}
				
				
				
				//DfxData rc = (DfxData)session.getAttribute(reportId);
				//rc.setReportId(reportId);
				if ("2".equals(reportType)) {
					IReport temp = ReportUtils.read(fpath);
					String[] fs = fields.split("<;>");
					DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
					
					MacroMetaData mmd = temp.getMacroMetaData();
					ParamMetaData pmd = temp.getParamMetaData();
					if (mmd != null && mmd.getMacroCount()>=0) {
						dsmd.addDataSetConfig(idsc); //rc.getDs(fs) 把数据集定义添加到数据集元数据
						for (int i=0; i<fs.length; i++) {
							String[] ss = fs[i].split("<,>");
							Macro m = mmd.getMacro(ss[0]);
							if (m != null) m.setMacroValue(ss[1]);
							else {
								mmd.addMacro(ss[0],"",Macro.MACRO_NORMAL,ss[1]);
							}
							Macro m2 = mmd.getMacro(ss[0]+"_DISP");
							if (m2 != null) m2.setMacroValue(ss.length>2?ss[2]:"");
							else {
								mmd.addMacro(ss[0]+"_DISP","",Macro.MACRO_NORMAL,ss.length>2?ss[2]:"");
							}
						}
					} else if (pmd != null && pmd.getParamCount()>=0) {
						dsmd.addDataSetConfig(idsc); //rc.getDs(fs) 把数据集定义添加到数据集元数据
						for (int i=0; i<fs.length; i++) {
							String[] ss = fs[i].split("<,>");
							com.raqsoft.report.usermodel.Param m = pmd.getParam(ss[0]);
							if (m != null) m.setValue(ss[1]);
							else {
								pmd.addParam(ss[0],"",com.raqsoft.report.usermodel.Param.PARAM_NORMAL,Types.DT_STRING,ss[1]);
							}
							com.raqsoft.report.usermodel.Param m2 = pmd.getParam(ss[0]+"_DISP");
							if (m2 != null) m2.setValue(ss.length>2?ss[2]:"");
							else {
								pmd.addParam(ss[0]+"_DISP","",com.raqsoft.report.usermodel.Param.PARAM_NORMAL,Types.DT_STRING,ss.length>2?ss[2]:"");
							}
						}
					} else {
						dsmd.addDataSetConfig(idsc);//rc.getDs(fs)); //rc.getDs(fs) 把数据集定义添加到数据集元数据  
					}
					

					
					if (edsc != null) {
						temp.setBigDataSetName("ds1");
					}
					
					temp.setDataSetMetaData(dsmd);
					Context cxt = new Context();
					//Logger.debug();
					//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
					Engine engine = new Engine(temp, cxt); //构造报表引擎
					

					//if (new File("d:/temp/").exists() && new File("d:/temp/").isDirectory()) ReportUtils.write("d:/temp/template.rpx",temp);					
					File f = null;
					try {
						String resultRpxPrefixOnServer = req.getParameter("resultRpxPrefixOnServer");
						f = new File(DataSphereServlet.getFilePath(resultRpxPrefixOnServer+title+".rpx"));
						if (!new File(f.getParent()).exists()) new File(f.getParent()).mkdirs();
						ReportUtils.write(f.getAbsolutePath(),temp);
					} catch (Exception e) {
						Logger.warn("write rpx error : ", e);
					}

					if (edsc != null) {
						//Context cxt = new Context();
						//cxt.setParamValue("dataCursor", o8);
						//BigEngine engine = new BigEngine(rd, cxt); //构造报表引擎
						//IReport iReport = engine.getPage(arg0, arg1).calc(); //运算报表
						session.setAttribute(reportId+"_REPORT_FILE", f.getAbsolutePath());
					} else {
						IReport rd = engine.calc(); //运算报表
						if(pagerStyle == 0) {
							rd.getPrintSetup().setPagerStyle(PrintSetup.PAGER_NONE);
						}
						//rc.setIReport(rd);
						if(pagerStyle == 0){
							rd.getPrintSetup().setPagerStyle(PrintSetup.PAGER_NONE);
						}
						//rc.setReportDefine(temp);
						session.setAttribute(reportId+"_REPORT", rd);
					}

				} else {
					writer.print("error:not support!");
				}		
				writer.print(title+","+reportId);
			} else if ("txt".equals(oper)) {
				ResultPage rp = new ResultPage();
				//session.setAttribute(pageId + "_rp", rp);
				String dql = req.getParameter("dql").replaceAll("<dq>", "\"");
				QueryTask tsk = new QueryTask(dql, "user", req.getParameter("dbName"), null);
				//tsk.setAttrs(attrs);
				tsks.put(tsk);
				while (tsk.getEndTime() == 0) {
					Thread.currentThread().sleep(1000);
				}
				rp.setTsk(tsk);
				try {
					res.setContentType( "text/plain;charset=" + Context.getJspCharset() );
					res.setHeader( "Content-Disposition", "attachment; filename=result.txt" );
					writer = res.getWriter();
					rp.txt( writer, null);
				}
				catch ( Throwable e ) {
					rp.status = "error:" + e.getMessage();
					Logger.error("",e);
					e.printStackTrace();
				}
			} else if ("gex".equals(oper)) {
				ResultPage rp = new ResultPage();
				//session.setAttribute(pageId + "_rp", rp);
				String dql = req.getParameter("dql").replaceAll("<dq>", "\"");
				QueryTask tsk = new QueryTask(dql, "user", req.getParameter("dbName"), null);
				//tsk.setAttrs(attrs);
				tsks.put(tsk);
				while (tsk.getEndTime() == 0) {
					Thread.currentThread().sleep(1000);
				}
				rp.setTsk(tsk);
				writer = res.getWriter();
				try {
					//rp.gex(DataSphereServlet.ROOT_PATH + "/gexTmp/" + req.getParameter("gex") + ".gex");
				}
				catch ( Throwable e ) {
					writer.println("error:" + e.getMessage());
					Logger.error("",e);
					e.printStackTrace();
				}
				writer.println("ok");
			} else if ("gexDownload".equals(oper)) {
				writer = res.getWriter();
				
				String gex = req.getParameter("gex");
				Object oo = session.getAttribute(req.getParameter("src"));
				if (oo == null) {
					writer.println("error:Time out!");
					return;
				}
				//CellSetUtil.writeCalcCellSet(DataSphereServlet.ROOT_PATH + "/gexTmp/" + gex + ".gex", (CalcCellSet)oo);
				writer.println("ok");
			} else if ("generateTxt".equals(oper)) {

				
				writer = res.getWriter();
				Object obj = session.getAttribute(req.getParameter("rgid"));
				if (obj == null) {
					writer.println("error:Time out");
					return;
				}
				writer.println("ok");
			} else if ("report".equals(oper)) {
				writer = res.getWriter();
				String dql = req.getParameter("dql");
				Logger.debug(req.getParameter("report"));
				String qyx = req.getParameter("qyx");
				String dbName = req.getParameter("dbName");
				String rid = "r"+System.currentTimeMillis();
				ReportConf rc = new ReportConf();
				rc.getRss().put("dql", dql);
				rc.setDql(dql);
				rc.setReportId(rid);
				rc.setDbName(dbName);
				rc.setQyx(qyx);
				session.setAttribute(rid, rc);
				writer.print(rid);
			} else if ("ddwQuery".equals(oper)){
				new DDWData().queryData(res, req);
			} else if ("ddwDfxCalc".equals(oper)){
				new DDWData().calcData(res, req);
			} else if ("existDbdStyleImage".equals(oper)){
				writer = res.getWriter();
				String file = req.getParameter("filename");
				String path = DataSphereServlet.getFilePath(DataSphereServlet.dbdImageFileFolderOnServer);//req.getSession().getServletContext().getRealPath("/raqsoft/guide/dbd2.0/img/dbdStyleImage/");
				File pathF = new File(path);
				if(!pathF.exists()) {
					pathF.mkdirs();
				}
				File f = new File(path,file);
				if(!f.exists()) {
					writer.print("0");
					return;
				}
				writer.print(( f.exists() && f.length()>0)?"1":"0");
			}  else if ("dbdStyleImageList".equals(oper)){
				writer = res.getWriter();
				String path = DataSphereServlet.getFilePath(DataSphereServlet.dbdImageFileFolderOnServer);//req.getSession().getServletContext().getRealPath("/raqsoft/guide/dbd2.0/img/dbdStyleImage/");
				File pathF = new File(path);
				File[] files = pathF.listFiles();
				if(files == null || files.length == 0) {
					writer.print("");
					return;
				}
				ArrayList<String> fileNames = new ArrayList<String>();
				for(int n = 0 ; n < files.length ; n++) {
					fileNames.add(Escape.addEscAndQuote(files[n].getName()));
				}
				writer.print(fileNames);
				return;
			} else if ("uploadDbdStyleImage".equals(oper)){
				DiskFileItemFactory factory = new DiskFileItemFactory();
				//factory.setSizeThreshold(5 * 1024); // 最大缓存
				String serverPath =  DataSphereServlet.getFilePath(DataSphereServlet.dbdImageFileFolderOnServer);// req.getSession().getServletContext().getRealPath("/raqsoft/guide/dbd2.0/img/dbdStyleImage");
				factory.setRepository(new File(serverPath));
				ServletFileUpload upload = new ServletFileUpload(factory);
				//upload.setSizeMax(sizeMax * 1024 * 1024);// 文件最大上限
				List<FileItem> items = upload.parseRequest(req);// 获取所有文件列表
				for (FileItem item : items) {
					if (!item.isFormField()) {
						String fileName = item.getName();
						Logger.debug("fileItem: "+fileName);
						String suffix = "";
						try{
							suffix = fileName.substring(fileName.lastIndexOf('.'));
						}catch(java.lang.StringIndexOutOfBoundsException e){
							suffix = "";
						}
						String[] types = new String[]{".jpg",".png",".gif",".bmp"};
						HashSet<String> typeSet = new HashSet<String>();
						for(String tmpTypeName : types){
							typeSet.add(tmpTypeName);
						}
						if (!typeSet.contains(suffix.toLowerCase())) {
							throw new Exception("wrong type");
						}
						String UA = req.getHeader( "user-agent" );
						Logger.debug("UA: "+UA);
						File file = null;
						if(UA.toLowerCase().indexOf("IE") >= 0 || UA.equals("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")){
							Logger.info("isIE");
							int lastSlash = fileName.lastIndexOf('\\');
							if(lastSlash == -1){
								lastSlash = fileName.lastIndexOf('/');
							}
							if(lastSlash != -1){
								fileName = fileName.substring(lastSlash+1);
							}
						}
						file = new File(serverPath + "/" + fileName);
						item.write(file);
					}
				}
			}
		} 
		catch ( Throwable e ) {
			try {
				writer = res.getWriter();
			} catch (IOException e2) {
			}
			Logger.error("",e);
			e.printStackTrace();
			if (o != null) {
				//ResultPage rp = (ResultPage)o;
				//rp.status = "error:" + e.getMessage();
			}
			String msg = e.getMessage();
			try {
				if(e instanceof java.sql.SQLException || msg.equals("lessOn")){
					if(msg.equals("lessOn")){
						msg = "缺少关联";
					}else{
						msg = "sql error:"+msg;
					}
				}
				if (pw != null) pw.write("error:" + msg);
			} catch (IOException e1) {
			}
			try {
				writer.write("error:" +  e.getMessage());
			} catch (Exception e1) {
			}
		}
		finally {
			////new Sequence().binary(new String[]{"off"});
			//			try {
//				if ( pw != null ) {
//					pw.close();
//				}
//			}
//			catch ( Exception e ) {}
		}
	}
	
	private String solveQl(String ql, HttpServletRequest req) {
		String dqlTableFilterName = req.getParameter("dqlTableFilterName");
		ArrayList<DQLTableFilter> farr = (ArrayList<DQLTableFilter>)(req.getSession().getAttribute("_raqsoft_filters_"));
		Logger.debug(req.getSession().getId());
		if(farr == null) {
			Logger.debug("未在session中找到过滤条件_raqsoft_filters_");
			return ql;
		}
		DQLTableFilter filter = null;
		for(DQLTableFilter f : farr){
			if(f.getID().equals(dqlTableFilterName)){
				filter = f;
				break;
			}
		}
		if(filter == null) {
			Logger.debug("缺少过滤条件："+ dqlTableFilterName);
			return ql;
		}
		else{
			return replaceQlMacro(ql,filter);
		}
	}

	//{ id:2, pId:1, name:"张颖",real:"1",dim:'雇员'}
	private int currId = 0;
	private void genJson(StringBuffer s, Sequence si, Sequence levels, String name, int level, int parent, boolean first, int maxDimSize){
		for (int i=1; i<=si.length(); i++) {
			Object o = si.get(i);
			if (!(o instanceof Sequence)) continue;
			if (currId%1000000>=maxDimSize) {
				//
				Logger.warn("Dimension ["+name+"] too much data, loaded a part only");
				break;
			}
			Record r = getRecord(o);
			s.append("\t\t\t");
			if (!first || i!=1) s.append(",");
			String names[] = r.getFieldNames();
			int codePos = ((Integer)levels.get(level)).intValue()-1;
			String codeName = names[codePos];
			String dimName = codeName;
			if ("CODE".equals(dimName)) dimName = name;
			String dispName = "DISP";
			if (codePos != 0) dispName = codeName+"DISP";
			int dispPos = -1;
			for (int j=0; j<names.length; j++) {
				if (dispName.equalsIgnoreCase(names[j])) dispPos = j;
			}

			Object code1 = r.getFieldValue(codePos);
			String code = code1.toString();
			if (!(code1 instanceof Number)) code = "\""+code+"\"";
			String disp = "";
			if (dispPos != -1) {
				Object disp1 = r.getFieldValue(dispPos);
				disp = disp1.toString();
				if (!(disp1 instanceof Number)) disp = "\""+disp+"\"";
			} else disp = code;
			currId++;
			s.append("{\"id\":"+currId+",\"pId\":"+parent+",\"name\":"+disp+",\"real\":"+code+",\"dim\":\""+dimName+"\"}\n");
			genJson(s,(Sequence)o, levels, name, level+1, currId, false, maxDimSize);
		}
	}
	
	private Record getRecord(Object o) {
		if (o instanceof Record) {
			return (Record)o;
		} else if (o instanceof Sequence) {
			return getRecord(((Sequence)o).get(1));
		}
		return null;
	}
	
	public static int getRGB(String rgb) {
		if (rgb.indexOf(",")==-1) return Integer.parseInt(rgb); 
		String[] s = rgb.split(",");
		if (s.length != 3) return 0;
		return new Color(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2])).getRGB();
	}
	public static String[] zimu = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	//public static String getZiMu(short i){
	
	//}
	public static void main(String args[]) {
//		Logger.debug("aaa_;_".split("_;_").length);
//		String ss = "sdfasdfsa";
//		String [] s = new String[ss.length()];
//		for (int i=0; i<s.length; i++) s[i] = ss.substring(i, i+1);
		String rpt = "{cells:[],colCount:2,cols:[{row: 1, type: 161, height: 8},{col: 1, width: 25}],rowCount:1,rows:[{row: 1, type: 161, height: 8}]}";
		Record r = (Record)JSONUtil.parseJSON(rpt.toCharArray(), 0, rpt.length()-1);
		System.out.println(r.getFieldValue("rows"));
	}
	
	public String replaceQlMacro(String ql, DQLTableFilter filter){
		Logger.debug("开始替换sql宏");
		Map<String,String> map = null;
		Set<String> set = null;
		if(filter == null || ql == null) return ql;
		map = filter.getParamValues();
		//map = new HashMap<String,String>();
		set = map.keySet();//宏参数名定义
		//map.put("T", "雇员");
		//map.put("F", "ID");
		//map.put("f", "1");
		//ql = "select * from ${T} where ${F} = ${f}";
		Logger.debug(set);
		for(String name : set){
			Logger.debug("current macro:"+"${"+name+"}");
			String macro = "${"+name+"}";
			Logger.debug("sql before change:"+ql);
			if(map.get(name) != null) ql = ql.replace(macro, map.get(name));
			Logger.debug("sql after change:"+ql);
		}
		return ql;
	}
	
	public static String getUrlPrefix( HttpServletRequest request ) {
		String appRoot = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		return appRoot;
	}
	
}