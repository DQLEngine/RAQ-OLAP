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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import com.raqsoft.common.Area;
import com.raqsoft.common.ByteMap;
import com.raqsoft.common.DBSession;
import com.raqsoft.common.IByteMap;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.common.Types;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.Param;
import com.raqsoft.dm.ParamList;
import com.raqsoft.dm.Record;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.Table;
import com.raqsoft.guide.resource.GuideMessage;
import com.raqsoft.guide.tag.GuideTag;
import com.raqsoft.guide.tag.QueryTag;
import com.raqsoft.guide.util.DqlUtil;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.querys.QueryTask;
import com.raqsoft.guide.web.querys.TaskQueue;
import com.raqsoft.report.cache.CacheManager;
import com.raqsoft.report.model.ReportDefine;
import com.raqsoft.report.model.ReportDefine2;
import com.raqsoft.report.usermodel.BuiltinDataSetConfig;
import com.raqsoft.report.usermodel.Context;
import com.raqsoft.report.usermodel.DataSetMetaData;
import com.raqsoft.report.usermodel.Engine;
import com.raqsoft.report.usermodel.EsProcDataSetConfig;
import com.raqsoft.report.usermodel.IColCell;
import com.raqsoft.report.usermodel.INormalCell;
import com.raqsoft.report.usermodel.IReport;
import com.raqsoft.report.usermodel.IRowCell;
import com.raqsoft.report.usermodel.Macro;
import com.raqsoft.report.usermodel.MacroMetaData;
//import com.raqsoft.report.usermodel.Param;
import com.raqsoft.report.usermodel.ParamMetaData;
import com.raqsoft.report.usermodel.SQLDataSetConfig;
import com.raqsoft.report.usermodel.graph.GraphCategory;
import com.raqsoft.report.usermodel.graph.GraphSery;
import com.raqsoft.report.usermodel.graph.ReportGraphProperty;
import com.raqsoft.report.util.ReportUtils;
import com.raqsoft.util.JSONUtil;
import com.raqsoft.util.Variant;

public class ActionResultPage20180507 {
	
//	public static TaskQueue tsks = new TaskQueue();
//
//	public void service( HttpServletRequest req, HttpServletResponse res, JspWriter pw, String pageId ) {
//		//PrintWriter pw = null;
//		Object o = null;
//		PrintWriter writer = null;
//		res.setContentType( "text/html;charset=UTF-8" );
//		HttpSession session = req.getSession();
//		String oper = req.getParameter( "oper" );
//		try {
//			////new Sequence().binary(new String[]{"on"});
//			if ("query".equals(oper)) {
//				writer = res.getWriter();
//				String reQuery = req.getParameter("reQuery");//数据文件已存在，强制重新查询
//				String dataId = req.getParameter("dataId");//数据文件，含目录
//				String dataFileType = req.getParameter("dataFileType");//文本、二进制
//				Logger.debug("query file is " + dataId);
//				String type = req.getParameter("type");//查询类型
//				String dataSource = req.getParameter("dataSource");//数据源
//				String ql = req.getParameter("ql");//sql或dql
//				Logger.debug("dql or sql is ： " + ql);
//				String dqlSegments = req.getParameter("dqlSegments");
//				if (dqlSegments != null) ql = DqlUtil.getDql(dqlSegments.replaceAll("<d_q>", "\""),null,null,dataSource);
//				String dfxFile = req.getParameter("dfxFile");//dfx文件
//				String dfxScript = req.getParameter("dfxScript");//dfx脚本
//				String dfxParams = req.getParameter("dfxParams");//dfx参数
//				String inputFiles = req.getParameter("inputFiles");//
//				int cursorSize = 10000;//Integer.parseInt(req.getParameter("cursorSize"));
//				String filter = req.getParameter("filter");
//				String currTable = req.getParameter("currTable");
//				Object o1 = session.getAttribute(dataId);
//				
//				
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
//				//Env.setPaths(paths2);
//				//Env.setTempPath(root);//.setMainPath(root);
//				
//
//				if (o1 != null) {
//					DfxQuery dq = (DfxQuery)o1;
//					File f = new File(dq.getDataFileFullPath());
//					if (f.exists() && f.isFile() && f.length()>0) {
//					} else {
//						dq = null;
//						o1 = null;
//						session.removeAttribute(dataId);
//					}
//				}
//				if ("1".equals(reQuery) || o1 == null) {
//					DfxQuery dq = null;
//					ParamList pl = new ParamList();
//
//					if (dfxParams != null && dfxParams.length()>0) {
//						String paramMode = "i";
//						String params = dfxParams;
//						if (paramMode.indexOf("i")>=0 || paramMode.indexOf("p")>=0) {
//							if (params != null) {
//								if (paramMode.indexOf("p")>=0) {
//									String[] ss = params.split("\"");
//									for (int i=0; i<ss.length; i++) {
//										if (i%2 == 1) {
//											ss[i] = ss[i].replaceAll("=", "_dengyu_").replaceAll(";", "_fenhao_");
//										}
//									}
//									params = "";
//									for (int i=0; i<ss.length; i++) {
//										params += ss[i];
//									}
//								}
//								String ps[] = params.split(";");
//								for (int i=0; i<ps.length; i++) {
//									String[] psi = ps[i].split("=");
//									if (psi.length == 2) {
//										//System.out.println(psi[0] + "-----" + psi[1]);
//										String ps0 = psi[0].replaceAll("_dengyu_","=").replaceAll("_fenhao_",";");
//										String ps1 = psi[1].replaceAll("_dengyu_","=").replaceAll("_fenhao_",";");
//										Param p = pl.get(ps0);
//										if (p != null) p.setValue(Variant.parse(ps1));
//										else {
//											Param p2 = new Param();
//											p2.setName(ps0);
//											p2.setValue(Variant.parse(ps1));
//											pl.add(p2);
//										}
//									}
//								}
//							}
//						} else if (paramMode.indexOf("r")>=0) {
//							Enumeration e = req.getAttributeNames();
//							while (e.hasMoreElements()) {
//								String n = e.nextElement().toString();
//								Object v = req.getAttribute(n);
//								if (v != null && v.toString().length()>0) {
//									Param p = pl.get(n);
//									if (p != null) p.setValue(v);
//									else {
//										Param p2 = new Param();
//										p2.setName(n);
//										p2.setValue(v);
//										pl.add(p2);
//									}
//								}
//							}
//						}
//					}
//					
//					//TODO 处理dfxParams
//					GuideTag.putParams(null, pl, req);
//					if ("2".equals(type)) {
//						dq = new DfxQuery(dataSource, ql, dataId,filter,cursorSize,dataFileType);
//					} else if ("1".equals(type)) {
//						//dq = new DfxQuery(dataId);
//					} else if ("3".equals(type)) {
//						dq = new DfxQuery(dfxFile, true, pl, dataId,filter,cursorSize,dataFileType);
//					} else if ("4".equals(type)) {
//						dfxScript = dfxScript.replace("\\"+"n", "\n").replace("\\"+"t", "\t");
//						dq = new DfxQuery(dfxScript, false, pl, dataId,filter,cursorSize,dataFileType);
//					} else if ("5".equals(type)) {
//						dq = new DfxQuery(inputFiles, dataId,filter,cursorSize,dataFileType);
//						if (currTable!=null && currTable.length()>0) dq.setTableFilter(currTable, filter);
//					}
//					session.setAttribute(dataId, dq);
//				}
//				writer.println("ok");
//			} else if ("getLmd".equals(oper)) {
//				writer = res.getWriter();
//				writer.print(ConfigUtil.getMetaDataJson(req.getParameter("dataSource")));
//			} else if ("getFiles".equals(oper)) {
//				writer = res.getWriter();
//				writer.print(QueryTag.getFiles(req.getParameter("qyxFolderOnServer"),req.getParameter("olapFolderOnServer"),req.getParameter("dfxFolderOnServer"), req.getParameter("rpxFolderOnServer"), req.getParameter("inputFileFolderOnServer"), req.getParameter("fileDataFolderOnServer")));
//			} else if ("fileExist".equals(oper)) {
//				writer = res.getWriter();
//				String file = req.getParameter("file");
//				File f = new File(DataSphereServlet.getFilePath(file));
//				if (f.exists() && f.length()==0) f.delete();
//				writer.print(( f.exists() && f.length()>0)?"1":"0");
//			} else if ("downloadData".equals(oper)) {
//				writer = res.getWriter();
//				String type = req.getParameter("type");
//				String dataId = req.getParameter("dataId");
//				o = session.getAttribute(dataId);
//				if (o == null) {
//					writer.println("error:Time out!");
//					return;
//				}
//				DfxQuery dq = (DfxQuery)o;
//				writer.print(dq.generateData(type));
//			} else if ("saveCacheData".equals(oper)) {
//				writer = res.getWriter();
//				String path = req.getParameter("path");
//				String dataId = req.getParameter("dataId");
//				String s = FileUtils.copyFile(DataSphereServlet.getFilePath(dataId),DataSphereServlet.getFilePath(path),false);
//				if (s.length()>0) {
//					Logger.warn("data file cope error : " + s);
//				}
//				writer.println("ok");
//				
//			} else if ("queryDqlData".equals(oper)) {
//				writer = res.getWriter();
//				String reportId = req.getParameter("reportId");
//				String dql = req.getParameter("dql");
//				String dqlSegments = req.getParameter("dqlSegments");
//				String dataSource = req.getParameter("dataSource");
//				String outerConditionId = req.getParameter("outerConditionID");
//				Object o1 = null;//session.getAttribute("_raqsoft_outerCondition_");
//				Object o22 = null;//session.getAttribute("_raqsoft_dataSource_");
//				if (outerConditionId == null) {
//					outerConditionId = "default";
//				}
//
//				Object o3 = session.getAttribute("_raqsoft_outerConditionID_");
//				if (o3 != null && o3 instanceof ArrayList) {
//					int idx = ((ArrayList)o3).indexOf(outerConditionId);
//					if (idx >= 0) {
//						o1 = ((ArrayList)session.getAttribute("_raqsoft_outerCondition_")).get(idx);
//						o22 = ((ArrayList)session.getAttribute("_raqsoft_dataSource_")).get(idx);
//					}
//				}
//
//				if (dqlSegments != null) dql = DqlUtil.getDql(dqlSegments.replaceAll("<d_q>", "\""),o1!=null&&o22!=null?(Map<String,String>)o1:null,null,o1!=null&&o22!=null?o22.toString():null);
//				int maxDataSize = 50000;
//				try {
//					maxDataSize = Integer.parseInt(req.getParameter("maxDataSize"));
//				} catch (Exception e1) {
//					Logger.warn("maxDataSize error : " + req.getParameter("maxDataSize"));
//				}
//				
//				BuiltinDataSetConfig bdsc = new BuiltinDataSetConfig();
//				bdsc.setName("ds1");
//				int count = 0;
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
//				writer.println(count+":"+(over?1:0));
//			} else if ("getTableInfo".equals(oper)) {
//				writer = res.getWriter();
//				String dataId = DataSphereServlet.getFilePath(req.getParameter("dataId"));
//				dataId = dataId.replaceAll("///", "/").replaceAll("//", "/");
//				int scanRow = 100;
//				try {
//					scanRow = Integer.parseInt(req.getParameter("scanRow"));
//				} catch(Exception e){}
//				writer.println(DfxData.getTableInfo(dataId,req.getParameter("dataFileType"),scanRow));
//			} else if ("calc".equals(oper)) {
//				writer = res.getWriter();
//				String result;
//				try {
//					String reportId = req.getParameter("reportId");
//					String dataId = req.getParameter("dataId");
//					Logger.debug("data file is " + dataId);
//					File f = new File(DataSphereServlet.getFilePath(dataId));
//					if (!f.exists()) {
//						//数据加载失败，过期已被删除 或 查询不到符合条件的数据，要重新查询吗？
//						writer.write("{error:'"+GuideMessage.get(req).getMessage("guide.requery")+"',action:'reQuery'}");
//						return;
//					}
//					Object o2 = session.getAttribute(reportId);
//					String cacheType = req.getParameter("cacheType");
//					DfxData dd = null;
//					if (o2 != null && o2 instanceof DfxData && !"where".equals(reportId)) {
//						//session.removeAttribute(dataId);
//						//o2 = null;
//						dd = (DfxData)o2;
//					} else {
//						dd = new DfxData(DataSphereServlet.getFilePath(dataId));//(DfxDataSetManager.getFile(dataId));//("D:/data/workspace/guide/web/WEB-INF/tmp/order");
//						dd.setReportId(reportId);
//						session.setAttribute(reportId, dd);
//					}
//
//					int maxDataSize = 50000;
//					try {
//						maxDataSize = Integer.parseInt(req.getParameter("maxDataSize"));
//					} catch (Exception e1) {
//						Logger.warn("maxDataSize error : " + req.getParameter("maxDataSize"));
//					}
//
//					DfxQuery dq = null;
//					Object o3 = session.getAttribute(dataId);
//					if (o3 != null && o3 instanceof DfxQuery) {
//						dq = (DfxQuery)o3;
//					}
//					if (dq != null) dq.setPause(true);
//					result = dd.calc(req.getParameter("calcs"), req.getParameter("filters"), req.getParameter("fields"), req.getParameter("resultExp"), cacheType, req.getParameter("types"),req.getParameter("dataFileType"), maxDataSize);
//					if (dq != null) dq.setPause(false);
//					writer.println(result);
//				} catch (Exception e) {
//					e.printStackTrace();
//					String err = e.getMessage().replaceAll("\n\r", " ").replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("'", " ").replaceAll("\"", " ");
//					//错误信息：{0}！ 要重新查询数据吗？
//					writer.write("{error:'"+GuideMessage.get(req).getMessage("guide.requery2",err)+"',action:'reQuery'}");
//				}
//				
//			} else if ("getInputTables".equals(oper)) {
//				writer = res.getWriter();
//				String dataId = req.getParameter("dataId");
//				o = session.getAttribute(dataId);
//				if (o == null) {
//					writer.println("error:Time out!");
//					return;
//				}
//				DfxQuery dq = (DfxQuery)o;
//				ArrayList<String> al = dq.getTableNames();
//				String tableNames = "";
//				String currTable = "";
//				for (int i=0; i<al.size(); i++) {
//					if (i>0) tableNames += ",";
//					tableNames += "'" + al.get(i) + "'";
//				}
//				currTable = dq.getCurrTable();
//				writer.print("["+tableNames + "]<|>"+currTable);
//			} else if ("getLoadedStatus".equals(oper)) {
//				writer = res.getWriter();
//				String dataId = req.getParameter("dataId");
//				o = session.getAttribute(dataId);
//				if (o == null) {
//					writer.println("error:Time out!");
//					return;
//				}
//				DfxQuery dq = (DfxQuery)o;
//				dq.setLastVisitTime(System.currentTimeMillis());
////				if (dq.isOver()) {
////					DfxDataSetManager.add(dataId, dq.getDataFile());
////				}
//				writer.println("{loadedRow:'"+dq.getLoadedRow(req.getParameter("calcFields"),req.getParameter("filter"),req.getParameter("fields"))+"',error:'"+dq.getError()+"',over:'"+(dq.isOver()?1:0)+"'}");				
//			} else if ("changeFilter".equals(oper)) {
//				writer = res.getWriter();
//				String dataId = req.getParameter("dataId");
//				o = session.getAttribute(dataId);
//				if (o == null) {
//					writer.println("error:Time out!");
//					return;
//				}
//				DfxQuery dq = (DfxQuery)o;
//				String currTable = req.getParameter("currTable");
//				if (currTable != null && currTable.length() == 0)
//					dq.setFilter(req.getParameter("filter"));
//				else dq.setTableFilter(currTable, req.getParameter("filter"));
//				writer.println("ok");
//				//writer.println("{loadedRow:'"+dq.getLoadedRow(req.getParameter("calcFields"),req.getParameter("filter"),req.getParameter("fields"))+"',error:'"+dq.getError()+"',over:'"+(dq.isOver()?1:0)+"'}");				
//			} else if ("getRows".equals(oper)) {
//				writer = res.getWriter();
//				String dataId = req.getParameter("dataId");
//				o = session.getAttribute(dataId);
//				if (o == null) {
//					//dd = new DfxData(DataSphereServlet.ROOT_PATH+dataId);
//					writer.println("error:Time out!");
//					return;
//				}
//				DfxQuery dq = (DfxQuery)o;
//				int begin = Integer.parseInt(req.getParameter("begin"));
//				int end = Integer.parseInt(req.getParameter("end"));
//				writer.println(dq.getRows(begin,end,req.getParameter("calcFields"),req.getParameter("filter"),req.getParameter("fields")));				
//			} else if ("generateGuideTrees".equals(oper)) {
//				writer = res.getWriter();
//				//Thread.currentThread().sleep(1000000);
//				String file = req.getParameter("file");
//				File f = new File(DataSphereServlet.getFilePath(file));				
//				if (f.exists() && f.length()>0) {
//					writer.println(FileUtils.readFile(f).replaceAll("\\\n", "").replaceAll("\\\t", ""));
//					return;
//				}
//				String trees = req.getParameter("trees");
//				if (trees == null || trees.length()==0) {
//					writer.print("no");
//					return;
//				} 
//				int maxDimSize = 5000;
//				try {
//					maxDimSize = Integer.parseInt(req.getParameter("maxDimSize"));
//				} catch (Exception e1) {
//				}
//				String dataSource = req.getParameter("dataSource");
//				String[] ts = trees.split("_;_");
//				//String[] dimNames = new String[ts.length];
//				//String[][] fields1 = new String[ts.length][];
//				//String[][] fields2 = new String[ts.length][];
//				Sequence seq = new Sequence();
//				
//				com.raqsoft.dm.Context ctx = new com.raqsoft.dm.Context();
//				for (int i=0; i<ts.length; i++) {
//					String tsi[] = ts[i].split("_,_");
//					String dimName = tsi[0];
////					String vs = tsi[1];
//					if (tsi.length>2) {
//						String table = tsi[2];
//						String code = tsi[3];
//						String disp = "";
//						if (tsi.length>4) disp = tsi[4];
//						//String levels = tsi[];
//						String dql = "SELECT t1."+code+" CODE" + (disp.length()>0?",t1."+disp+" DISP":"");
//						for (int j=5; j<tsi.length; j++) {
//							String tsij[] = tsi[j].split("_:_");
//							dql += "," + tsij[1].replaceAll("[?]","t1."+code) + " " + tsij[0];
//						}
//						dql += " FROM " + table + " t1";
//						Logger.debug(dimName + " : " + dql);
//						try {
//							ctx.setParamValue("eval1", "connect(\""+dataSource+"\")");
//							ctx.setParamValue("eval2", dimName+"=B1.query(\""+dql+"\")");
//							ctx.setParamValue("eval3", "file(\""+DataSphereServlet.ROOT_PATH+"/temp/"+dimName+"\").export@t("+dimName+")");
//							DfxUtils.execDfxFile("/com/raqsoft/guide/web/dfx/lave.dfx", ctx);
//							//DfxUtils.execDfxScript("=connect(\""+dataSource+"\")\t="+dimName+"=A1.query(\""+dql+"\")\t=file(\""+DataSphereServlet.ROOT_PATH+"/temp/"+dimName+"\").export@t("+dimName+")\t=debug("+dimName+")", ctx, true);
//							seq.add(dimName);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					} else {
//						//TODO
//					}
//				}
//				try {
//					ctx.setParamValue("trees", seq);
//					DfxUtils.execDfxFile(DataSphereServlet.class.getResourceAsStream("/com/raqsoft/guide/web/dfx/trees.dfx"), ctx, true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				/**
//				[
//					{
//						name:''
//						,data : [
//							{ id:2, pId:1, name:"张颖",real:"1",dim:'雇员'}
//						]
//					}
//				]
//				 */
//				StringBuffer s = new StringBuffer();
//				Logger.debug("seq length : "+seq.length());
//				s.append("[\n");
//				boolean first = true;
//				for (int i=1; i<=seq.length(); i++) {
//					String name = seq.get(i).toString();
//					com.raqsoft.dm.Param p = ctx.getParam(name+"_tree");
//					if (p == null) continue;
//					Sequence si = (Sequence)p.getValue();
//					Sequence levels = (Sequence)ctx.getParam(name+"_levels").getValue();
//					s.append("\t");
//					if (!first) s.append(",");
//					first = false;
//					//Logger.debug(levels);
//					//Logger.debug(si);
//					this.currId = i*1000000;
//					s.append("{\n");
//					s.append("\t\t\"name\":\""+name+"\"\n");
//					s.append("\t\t,\"data\":[\n");
//					genJson(s, si, levels, name, 1, 0, true,maxDimSize);
//					s.append("\t\t]\n");
//					s.append("\t}\n");
//				}
//				s.append("]\n");
//				//Logger.debug(s.toString());
//				FileUtils.saveFile(s.toString(), f);
//				writer.println(s.toString().replaceAll("\\\n", "").replaceAll("\\\t", ""));				
//			} else if ("genReport".equals(oper)) {
//				writer = res.getWriter();
//				String reportId = req.getParameter("reportId");
//				CacheManager manager = CacheManager.getInstance();
//				manager.deleteReportEntry(reportId);				
//				String title = req.getParameter("title");
//				String rpt = req.getParameter("rpt").replaceAll("<d_q>", "\"");
//				Record r = (Record)JSONUtil.parseJSON(rpt.toCharArray(), 0, rpt.length()-1);
//				Object o1 = r.getFieldValue("rows");
//				Object o2 = r.getFieldValue("cols");
//				Table rows = null;
//				if (o1 instanceof Table) rows = (Table)o1;
//				Table cols = null;
//				if (o2 instanceof Table) cols = (Table)o2;
//				Table cells = (Table)r.getFieldValue("cells");
//				int rowCount = (Integer)r.getFieldValue("rowCount");
//				int colCount = (Integer)r.getFieldValue("colCount");
//				String lefts = req.getParameter("lefts");
//				String tops = req.getParameter("tops");
//				String resultRpxPrefixOnServer = req.getParameter("resultRpxPrefixOnServer");
//				BuiltinDataSetConfig bdsc = null;
//				Object o8 = session.getAttribute(reportId);
//				DfxData rc = null;
//				int size = 0;
//				if (o8 instanceof DfxData) {
//					rc = (DfxData)o8;
//					bdsc = rc.getDs();
//					size = DfxData.getReportSize(rc.getTable(),lefts, tops);
//				} else {
//					bdsc = (BuiltinDataSetConfig)o8;
//					size = 10;//DfxData.getReportSize(bdsc,lefts, tops);
//				}
//				
//				int maxSize = 40000;
//				try {
//					maxSize = Integer.parseInt(req.getParameter("maxSize"));
//				} catch (Exception e1) {
//				}
//				if (size > maxSize) {
//					//error1 分组数据太多，导致交叉报表过大，建议：减少分组个数/减少分组值数量/改用分组报表（交叉报表的上表头调整到左表头）
//					//error2 分组数据太多，导致报表过大，建议：减少分组个数/减少分组值数量
//					if (lefts.length()>0 && tops.length()>0) writer.print("error:"+GuideMessage.get(req).getMessage("guide.error1")); 
//					else writer.print("error:"+GuideMessage.get(req).getMessage("guide.error2"));
//					return;
//				} 
//				
//				ReportDefine rd = new ReportDefine2(rowCount,colCount);
//				if (rows != null) {
//					for (int i=1; i<=rows.length(); i++) {
//						Record r1 = rows.getRecord(i);
//						IRowCell row = rd.getRowCell((Integer)r1.getFieldValue("row"));
//						try {
//							row.setRowType(((Integer)r1.getFieldValue("type")).byteValue());
//						} catch(Exception e) {
//						}
//						try {
//							row.setRowHeight((Integer)r1.getFieldValue("height"));
//						} catch(Exception e) {
//						}
//					}
//				}
//				if (cols != null) {
//					for (int i=1; i<=cols.length(); i++) {
//						Record c1 = cols.getRecord(i);
//						IColCell col = rd.getColCell((Integer)c1.getFieldValue("col"));
//						try {
//							col.setColType(((Integer)c1.getFieldValue("type")).byteValue());
//						} catch(Exception e) {
//						}
//						try {
//							col.setColWidth((Integer)c1.getFieldValue("width"));
//						} catch(Exception e) {
//						}
//					}
//				}
//				for (int i=1; i<=cells.length(); i++) {
//					Record r1 = cells.getRecord(i);
//					//{row:1,col:1,row2:1,col2:1,format:'',valueExp:'',value:'',extend:'',leftMain:'左主格',topMain:'上主格'}
//					int row = (Integer)r1.getFieldValue("row");
//					int col = (Integer)r1.getFieldValue("col");
//					int row2 = (Integer)r1.getFieldValue("row2");
//					int col2 = (Integer)r1.getFieldValue("col2");
//					Object format = r1.getFieldValue("format");
//					Object formatExp = null;
//					try {
//						formatExp = r1.getFieldValue("formatExp");
//					} catch (Exception e1) {
//					}
//					Object valueExp = r1.getFieldValue("valueExp");
//					Object value = r1.getFieldValue("value");
//					byte extend = ((Integer)r1.getFieldValue("extend")).byteValue();
//					Object leftMain = r1.getFieldValue("leftMain");
//					Object topMain = r1.getFieldValue("topMain");
//					Object tip = r1.getFieldValue("tip");
//					String dispExp = r1.getFieldValue("dispExp").toString().replaceAll("'", "\"");
//					String backColor = r1.getFieldValue("backColor").toString();
//					String backColorExp = null;
//					try {
//						backColorExp = r1.getFieldValue("backColorExp").toString();
//					} catch (Exception e1) {
//					}
//					//Logger.debug("backColorExp : "+backColorExp);
//					
//					String color = r1.getFieldValue("color").toString();
//					byte hAlign = ((Integer)r1.getFieldValue("hAlign")).byteValue();
//					byte adjustSizeMode = ((Integer)r1.getFieldValue("adjustSizeMode")).byteValue();
//					String textWrap = r1.getFieldValue("textWrap").toString();
//					//adjustSizeMode:48,textWrap:1,hAlign:208/209/210,color:'',backColor:''
//					INormalCell inc=rd.getCell(row,col);
//					IByteMap map=new ByteMap();
//					inc.setExpMap(map);
//					if (row != row2 || col != col2) {
//						ReportUtils.mergeReport(rd,new Area(row,col,row2,col2));
//					}
//					if (format!= null && format.toString().length()>0) {
//						inc.setFormat(format.toString());
//					} else if (formatExp!= null && formatExp.toString().length()>0) {
//						map.put(INormalCell.FORMAT, formatExp.toString());
//					} else map.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//					if (valueExp!=null && valueExp.toString().length()>0) map.put(INormalCell.VALUE, valueExp);
//					if (dispExp!=null && dispExp.toString().length()>0) map.put(INormalCell.DISPVALUE, dispExp);
//					if (backColorExp!=null && backColorExp.toString().length()>0) map.put(INormalCell.BCOLOR, backColorExp);
//					if (value!=null && value.toString().length()>0) inc.setValue(value.toString());
//					if (leftMain!=null && leftMain.toString().length()>0) inc.setLeftHead(leftMain.toString());
//					if (topMain!=null && topMain.toString().length()>0) inc.setTopHead(topMain.toString());
//					if (tip != null && tip.toString().length()>0) inc.setTip(tip.toString());
//					inc.setExtendMode(extend);
//					if (backColor.length()>0) inc.setBackColor(getRGB(backColor));
//					if (color.length()>0) inc.setForeColor(getRGB(color));
//					inc.setHAlign(hAlign);
//					inc.setAdjustSizeMode(adjustSizeMode);
//					if (textWrap.length()>0) inc.setTextWrap("1".equals(textWrap));
//					inc.setIndent(2);
//					try {
//						inc.setDiagonalStyle(((Integer)r1.getFieldValue("diagonal")).byteValue());
//						inc.setDiagonalWidth(new Double(0.75).floatValue());
//						if (row == 1 && col == 1) {
//							int widths = (col2-col+1)*25;
//							int w = 25;
//							if(widths<50) w = 50/(col2-col+1);
//							int heights = (row2-row+1)*8;
//							int h = 8;
//							if(heights<16) h = 16/(row2-row+1);
//							for (int m=1; m<=col2; m++) {
//								//2017/05/26去掉
//								//rd.getColCell(m).setColWidth(w);
//							}
//							for (int m=1; m<=row2; m++) {
//								//rd.getRowCell(m).setRowHeight(h);
//							}
//						}
//					} catch(Exception e) {}
//				}
//				ReportStyle style = new ReportStyle("单色");
//				int color = new Color(165,216,255).getRGB();
//				for(int i=1;i<=rowCount;i++){ 
//					for(int j=1;j<=colCount;j++){
//						// A5D8FF
//						
//						rd.setBBColor(i,(short)j, color); //设定下边框线色 
//						rd.setBBStyle(i,(short)j, style.borderStyle); //设定下边框类型 
//						rd.setBBWidth(i,(short)j, style.boderWidth); //设定下边框线粗 
//						
//						//左边框 
//						rd.setLBColor(i,(short)j, color); 
//						rd.setLBStyle(i,(short)j, style.borderStyle); 
//						rd.setLBWidth(i,(short)j, style.boderWidth); 
//						//右边框 
//						rd.setRBColor(i,(short)j, color); 
//						rd.setRBStyle(i,(short)j, style.borderStyle); 
//						rd.setRBWidth(i,(short)j, style.boderWidth); 
//						//上边框 
//						rd.setTBColor(i,(short)j, color); 
//						rd.setTBStyle(i,(short)j, style.borderStyle);
//						rd.setTBWidth(i,(short)j, style.boderWidth);
//					}
//				}
//				
//				rd.setReportType(ReportDefine.RPT_NORMAL);
//				DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
//				dsmd.addDataSetConfig(bdsc); //把数据集定义添加到数据集元数据 
//				rd.setDataSetMetaData(dsmd); //把数据集元数据赋给ReportDefine
//				
//				
//				try {
//					File f = new File(DataSphereServlet.getFilePath(resultRpxPrefixOnServer+title+".rpx"));
//					Logger.debug(f.getPath());
//					if (!new File(f.getParent()).exists())new File(f.getParent()).mkdirs(); 
//					new File(f.getParent()).mkdirs();
//					ReportUtils.write(f.getAbsolutePath(),rd);
//				} catch (Exception e) {
//					Logger.warn("write rpx error : ", e);
//				}
//				Context cxt = new Context();
//				//cxt.setDefDataSourceName(dbName);
//				//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
//				Engine engine = new Engine(rd, cxt); //构造报表引擎
//				
//				IReport iReport = engine.calc(); //运算报表
//				
//				if (rc != null) {
//					rc.setIReport(iReport);
//					rc.setReportDefine(rd);
//				}
//				session.setAttribute(reportId+"_REPORT", iReport);
//				writer.print("ok");
//
//			} else if ("calcReport".equals(oper)) {
//				writer = res.getWriter();
//				String reportId = req.getParameter("reportId");
//				String title = req.getParameter("title");
//				String reportType = req.getParameter("reportType");
//				String tops = req.getParameter("tops");
//				String lefts = req.getParameter("lefts");
//				String fields = req.getParameter("fields");
//				String structType = req.getParameter("structType");
//				boolean isRowData = "1".equals(req.getParameter("isRowData"));
//				
//				DfxData rc = (DfxData)session.getAttribute(reportId);
//				rc.setReportId(reportId);
//				String template = req.getParameter("template");
//				if ("2".equals(reportType)) {
//					IReport temp = ReportUtils.read(DataSphereServlet.getFilePath(template));
//					String[] fs = fields.split("<;>");
//					DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
//					
//					MacroMetaData mmd = temp.getMacroMetaData();
//					ParamMetaData pmd = temp.getParamMetaData();
//					if (mmd != null && mmd.getMacroCount()>=0) {
//						dsmd.addDataSetConfig(rc.getDs()); //rc.getDs(fs) 把数据集定义添加到数据集元数据
//						for (int i=0; i<fs.length; i++) {
//							String[] ss = fs[i].split("<,>");
//							Macro m = mmd.getMacro(ss[0]);
//							if (m != null) m.setMacroValue(ss[1]);
//							else {
//								mmd.addMacro(ss[0],"",Macro.MACRO_NORMAL,ss[1]);
//							}
//							Macro m2 = mmd.getMacro(ss[0]+"_DISP");
//							if (m2 != null) m2.setMacroValue(ss.length>2?ss[2]:"");
//							else {
//								mmd.addMacro(ss[0]+"_DISP","",Macro.MACRO_NORMAL,ss.length>2?ss[2]:"");
//							}
//						}
//					} else if (pmd != null && pmd.getParamCount()>=0) {
//						dsmd.addDataSetConfig(rc.getDs()); //rc.getDs(fs) 把数据集定义添加到数据集元数据
//						for (int i=0; i<fs.length; i++) {
//							String[] ss = fs[i].split("<,>");
//							com.raqsoft.report.usermodel.Param m = pmd.getParam(ss[0]);
//							if (m != null) m.setValue(ss[1]);
//							else {
//								pmd.addParam(ss[0],"",com.raqsoft.report.usermodel.Param.PARAM_NORMAL,Types.DT_STRING,ss[1]);
//							}
//							com.raqsoft.report.usermodel.Param m2 = pmd.getParam(ss[0]+"_DISP");
//							if (m2 != null) m2.setValue(ss.length>2?ss[2]:"");
//							else {
//								pmd.addParam(ss[0]+"_DISP","",com.raqsoft.report.usermodel.Param.PARAM_NORMAL,Types.DT_STRING,ss.length>2?ss[2]:"");
//							}
//						}
//					} else {
//						dsmd.addDataSetConfig(rc.getDs(fs)); //rc.getDs(fs) 把数据集定义添加到数据集元数据  
//					}
//					
//					
//					
//					temp.setDataSetMetaData(dsmd);
//					Context cxt = new Context();
//					//Logger.debug();
//					//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
//					Engine engine = new Engine(temp, cxt); //构造报表引擎
//					
//
//					//if (new File("d:/temp/").exists() && new File("d:/temp/").isDirectory()) ReportUtils.write("d:/temp/template.rpx",temp);					
//					try {
//						String resultRpxPrefixOnServer = req.getParameter("resultRpxPrefixOnServer");
//						File f = new File(DataSphereServlet.getFilePath(resultRpxPrefixOnServer+title+".rpx"));
//						if (!new File(f.getParent()).exists()) new File(f.getParent()).mkdirs();
//						ReportUtils.write(f.getAbsolutePath(),temp);
//					} catch (Exception e) {
//						Logger.warn("write rpx error : ", e);
//					}
//					
//					IReport rd = engine.calc(); //运算报表
//					rc.setIReport(rd);
//					//rc.setReportDefine(temp);
//					session.setAttribute(reportId+"_REPORT", rd);
//						
//				} else if ("1".equals(reportType)) {
//					ReportStyle style = new ReportStyle("彩色");
//					
//					//明细报表
//					if ("1".equals(structType) || "2".equals(structType)) {
//						String[] fs = fields.split("<;>");
//						
//						ReportDefine rd = null;
//						if (isRowData) {
//							rd = new ReportDefine2(2,fs.length);
//							for(int i=1;i<3;i++){ 
//								for(int j=1;j<=fs.length;j++){
//									rd.setBBColor(i,(short)j, style.borderColor); //设定下边框线色 
//									rd.setBBStyle(i,(short)j, style.borderStyle); //设定下边框类型 
//									rd.setBBWidth(i,(short)j, style.boderWidth); //设定下边框线粗 
//									
//									//左边框 
//									rd.setLBColor(i,(short)j, style.borderColor); 
//									rd.setLBStyle(i,(short)j, style.borderStyle); 
//									rd.setLBWidth(i,(short)j, style.boderWidth); 
//									//右边框 
//									rd.setRBColor(i,(short)j, style.borderColor); 
//									rd.setRBStyle(i,(short)j, style.borderStyle); 
//									rd.setRBWidth(i,(short)j, style.boderWidth); 
//									//上边框 
//									rd.setTBColor(i,(short)j, style.borderColor); 
//									rd.setTBStyle(i,(short)j, style.borderStyle);
//									rd.setTBWidth(i,(short)j, style.boderWidth);
//								}
//							}
//							rd.getRowCell(1).setRowType(IRowCell.TYPE_TABLE_HEADER);
//							for (int i=1; i<3; i++) {
//								rd.getRowCell(i).setRowHeight(style.height);
//							}
//							for (int j=1; j<=fs.length; j++) {
//								rd.getColCell(j).setAutoWidth(true);//.setColWidth(style.width);
//							}
//							
//							for(int i=1; i<=fs.length; i++){
//								INormalCell inc=rd.getCell(1,(short)i);
//								String fss[] = fs[i-1].split("<,>");
//								inc.setValue(fss[0]);
//								inc.setBackColor(style.c2.backColor);
//								inc.setForeColor(style.c2.foreColor);
//								inc.setHAlign(style.c2.hAlian);
//								inc.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//
//								INormalCell inc4=rd.getCell(2,(short)i); 
//								IByteMap map1=new ByteMap();
//								
//								map1.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//								String exp = fss[1];//ai1[0];
//								map1.put(INormalCell.TIP,"4");
//								map1.put(INormalCell.VALUE,exp); //设置单元格的数据值表达式 
//								inc4.setExpMap(map1);
//								inc4.setBackColor(style.c5.get(0).backColor);
//								inc4.setForeColor(style.c5.get(0).foreColor);
//								inc4.setHAlign(style.c5.get(0).hAlian);
//								inc4.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//								inc4.setTextWrap(true);
//								//inc4.setFormat("#.##");
//							}
//						} else {
//							rd = new ReportDefine2(fs.length,2);
//							for(int i=1;i<=fs.length;i++){ 
//								for(int j=1;j<=2;j++){
//									rd.setBBColor(i,(short)j, style.borderColor); //设定下边框线色 
//									rd.setBBStyle(i,(short)j, style.borderStyle); //设定下边框类型 
//									rd.setBBWidth(i,(short)j, style.boderWidth); //设定下边框线粗 
//									
//									//左边框 
//									rd.setLBColor(i,(short)j, style.borderColor); 
//									rd.setLBStyle(i,(short)j, style.borderStyle); 
//									rd.setLBWidth(i,(short)j, style.boderWidth); 
//									//右边框 
//									rd.setRBColor(i,(short)j, style.borderColor); 
//									rd.setRBStyle(i,(short)j, style.borderStyle); 
//									rd.setRBWidth(i,(short)j, style.boderWidth); 
//									//上边框 
//									rd.setTBColor(i,(short)j, style.borderColor); 
//									rd.setTBStyle(i,(short)j, style.borderStyle);
//									rd.setTBWidth(i,(short)j, style.boderWidth);
//								}
//							}
//							for (int i=1; i<3; i++) {
//								rd.getColCell(i).setAutoWidth(true);//.setColWidth(style.width);
//							}
//							for (int j=1; j<=fs.length; j++) {
//								rd.getRowCell(j).setRowHeight(style.height);
//							}
//							
//							for(int i=1; i<=fs.length; i++){
//								INormalCell inc=rd.getCell(i,(short)1);
//								String fss[] = fs[i-1].split("<,>");
//								inc.setValue(fss[0]);
//								inc.setBackColor(style.c2.backColor);
//								inc.setForeColor(style.c2.foreColor);
//								inc.setHAlign(style.c2.hAlian);
//								inc.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//
//								INormalCell inc4=rd.getCell(i,(short)2); 
//								IByteMap map1=new ByteMap();
//								map1.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//								String exp = fss[1];//ai1[0];
//								map1.put(INormalCell.TIP,"4");
//								map1.put(INormalCell.VALUE,exp); //设置单元格的数据值表达式 
//								inc4.setExpMap(map1);
//								inc4.setBackColor(style.c5.get(0).backColor);
//								inc4.setForeColor(style.c5.get(0).foreColor);
//								inc4.setHAlign(style.c5.get(0).hAlian);
//								inc4.setExtendMode(INormalCell.EXTEND_HORIZONTAL);
//								inc4.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//								inc4.setTextWrap(true);
//								//inc4.setFormat("#.##");
//							}
//						}
//
//						rd.setReportType(ReportDefine.RPT_NORMAL);
//						DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
//						dsmd.addDataSetConfig(rc.getDs()); //把数据集定义添加到数据集元数据 
//						rd.setDataSetMetaData(dsmd); //把数据集元数据赋给ReportDefine
//						
//						
//						if (new File("d:/temp/").exists() && new File("d:/temp/").isDirectory()) ReportUtils.write("d:/temp/detail.rpx",rd);
//						Context cxt = new Context();
//						//cxt.setDefDataSourceName(dbName);
//						//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
//						Engine engine = new Engine(rd, cxt); //构造报表引擎
//						
//						IReport iReport = engine.calc(); //运算报表
//						
//						rc.setIReport(iReport);
//						rc.setReportDefine(rd);
//
////						ProcDataSetConfig pdc = new ProcDataSetConfig();
////						pdc.setName("ds1");
////						pdc.setSQL("");
//					//分组报表
//					} else if ("3".equals(structType)) {
//						String topArr[] = {};
//						if (tops.length()>0) topArr = tops.split("<;>");
//						String leftArr[] = {};
//						if (lefts.length()>0) leftArr = lefts.split("<;>");
//						String aggrs[] = new String[0];
//						if (fields.length()>0) aggrs = fields.split("<;>");
//						boolean zongji = true;//TODO rc.isShowTotal();
//						
//						int row=0,col=0;
//						int rowShift = 0;//1;
//						row += topArr.length + rowShift;
//						if (isRowData) {
//							row += 1;
//							if (zongji) row += leftArr.length+1;
//							else row += 1;
//						} else {
//							row += (zongji?(leftArr.length+1):1) * (aggrs.length>0?aggrs.length:1);
//						}
//						col += leftArr.length;
//						if (isRowData) {
//							col += (zongji?(topArr.length+1):1) * (aggrs.length>0?aggrs.length:1);
//						} else {
//							col += 1;
//							if (zongji) {
//								col += topArr.length + 1;
//							} else {
//								col += 1;
//							}
//						}
//						String flts = "";
//						if ("none".equals(flts)) flts = "";
//
//						ReportDefine rd = new ReportDefine2(row,col);
//						
////						ReportUtils.mergeReport(rd,new Area(1,(short)1,1,col));
////						IRowCell rowcell=rd.getRowCell(1); //根据行号获得行首格 
////						rowcell.setRowType(IRowCell.TYPE_TABLE_HEADER); //对行首格设置行类型
////						INormalCell inc11=rd.getCell(1,1);
////						inc11.setValue(title);
////						inc11.setHAlign(INormalCell.HALIGN_CENTER);
////						inc11.setFontSize((short)16);
////						inc11.setBold(true);
////						inc11.setBackColor(-13334105);
////						inc11.setForeColor(-1);
//
//						for(int i=1;i<=row;i++){ 
//							for(int j=1;j<=col;j++){ 
//								rd.setBBColor(i,(short)j, style.borderColor); //设定下边框线色 
//								rd.setBBStyle(i,(short)j, style.borderStyle); //设定下边框类型 
//								rd.setBBWidth(i,(short)j, style.boderWidth); //设定下边框线粗 
//								
//								//左边框 
//								rd.setLBColor(i,(short)j, style.borderColor); 
//								rd.setLBStyle(i,(short)j, style.borderStyle);
//								rd.setLBWidth(i,(short)j, style.boderWidth); 
//								//右边框 
//								rd.setRBColor(i,(short)j, style.borderColor); 
//								rd.setRBStyle(i,(short)j, style.borderStyle);
//								rd.setRBWidth(i,(short)j, style.boderWidth); 
//								//上边框 
//								rd.setTBColor(i,(short)j, style.borderColor); 
//								rd.setTBStyle(i,(short)j, style.borderStyle);
//								rd.setTBWidth(i,(short)j, style.boderWidth);
//							}
//						}
//						for (int i=1; i<=row; i++) {
//							rd.getRowCell(i).setRowHeight(style.height);
//						}
//						for (int j=1; j<=col; j++) {
//							rd.getColCell(j).setAutoWidth(true);//.setColWidth(style.width);
//						}
//						
//						rd.setReportType(ReportDefine.RPT_NORMAL);
//						DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
//						
//						if (topArr.length>0 && leftArr.length>0) {
//							ReportUtils.mergeReport(rd,new Area(1+rowShift,(short)1,topArr.length+(isRowData?1:0)+rowShift,leftArr.length+(isRowData?0:1)));
//							INormalCell inc1=rd.getCell(1+rowShift,(short)1); 
//							inc1.setBackColor(style.c1.backColor);
//							inc1.setForeColor(style.c1.foreColor);
//							inc1.setHAlign(style.c1.hAlian);
//							inc1.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//							//inc1.setFormat("#.##");
//						}
//						//Logger.debug(new String("lefts : " + leftArr.length));
//						
//						String dims = "";
//						int leftRowBegin = topArr.length+(isRowData?2:1)+rowShift;
//						int leftColBegin = 1;
//						for (int i=0; i<leftArr.length; i++) {
//							String lss[] = leftArr[i].split("<,>");
//							ReportUtils.mergeReport(rd,new Area(leftRowBegin,(short)i+leftColBegin,leftRowBegin+(zongji?((isRowData?1:(aggrs.length>0?aggrs.length:1))*(leftArr.length-i)-1):(isRowData?0:(aggrs.length>0?aggrs.length:1)-1)),(short)i+leftColBegin));
//							INormalCell inc1=rd.getCell(leftRowBegin,(short)i+leftColBegin); 
//							inc1.setBackColor(style.c2.backColor);
//							inc1.setForeColor(style.c2.foreColor);
//							inc1.setHAlign(style.c2.hAlian);
//							//inc1.setFormat("#.##");
//							IByteMap map1=new ByteMap();
//							map1.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//							map1.put(INormalCell.VALUE,lss[1]);
//							map1.put(INormalCell.TIP, "1");
//							inc1.setExtendMode(INormalCell.EXTEND_VERTICAL);
//							inc1.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//							inc1.setTextWrap(true);
////							String n = zimu[i]+(topArr.length+(isRowData?2:1)+rowShift);
////							if (i>0) dims += "+\";\"+";
////							dims += "\""+lss[0]+":\"+string(" + n+")";
////							if (lss.length>2 && lss[2].length() > 0) {
////								String name = "ds" + (i+300);
////								SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
////								sdc.setName(name); //设置数据集名 
////								sdc.setSQL(lss[2]); //设置sql语句 
////								dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
////								map1.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
////							};
//							inc1.setExpMap(map1);
//							if (zongji) {
//								ReportUtils.mergeReport(rd,new Area(leftRowBegin+(leftArr.length-i)*(isRowData?1:(aggrs.length>0?aggrs.length:1)),(short)i+leftColBegin,leftRowBegin+((isRowData?1:(aggrs.length>0?aggrs.length:1))*(leftArr.length-i+1)-1),(short)leftArr.length));
//								INormalCell inc2=rd.getCell(leftRowBegin+(leftArr.length-i)*(isRowData?1:(aggrs.length>0?aggrs.length:1)),(short)i+leftColBegin); 
//								inc2.setBackColor(style.c3.backColor);
//								inc2.setForeColor(style.c3.foreColor);
//								inc2.setHAlign(style.c3.hAlian);
//								inc2.setValue("总计");
//								inc2.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//							}
//						}
//						dims = "";
//						int topRowBegin = 1+rowShift;
//						int topColBegin = leftArr.length+(isRowData?1:2);
//						for (int i=0; i<topArr.length; i++) {
//							String lss[] = topArr[i].split("<,>");
//							ReportUtils.mergeReport(rd,new Area(i+topRowBegin,(short)topColBegin,i+topRowBegin,(short)(topColBegin+(zongji?((isRowData?(aggrs.length>0?aggrs.length:1):1)*(topArr.length-i)-1):(isRowData?(aggrs.length>0?aggrs.length:1)-1:0)))));
//							INormalCell inc1=rd.getCell(i+topRowBegin,(short)topColBegin); 
//							inc1.setBackColor(style.c2.backColor);
//							inc1.setForeColor(style.c2.foreColor);
//							inc1.setHAlign(style.c2.hAlian);
//							//inc1.setFormat("#.##");
//							IByteMap map1=new ByteMap();
//							map1.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//							map1.put(INormalCell.VALUE,lss[1]);
//							map1.put(INormalCell.TIP, "2");
//							inc1.setExtendMode(INormalCell.EXTEND_HORIZONTAL);
//							inc1.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
////							String n = zimu[leftArr.length+(isRowData?1:2)-1]+(i+1+rowShift);
////							if (i>0) dims += "+\";\"+";
////							dims += "\""+lss[0]+":\"+string(" + n+")";
////							map1.put(INormalCell.TIP, dims);
////							if (lss.length>2 && lss[2].length() > 0) {
////								String name = "ds" + (i+200);
////								SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
////								sdc.setName(name); //设置数据集名 
////								sdc.setSQL(lss[2]); //设置sql语句 
////								dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
////								map1.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
////							};
//							inc1.setExpMap(map1);
//							inc1.setTextWrap(true);
//							if (zongji) {
//								ReportUtils.mergeReport(rd,new Area(i+topRowBegin,(short)topColBegin+(topArr.length-i)*(isRowData?(aggrs.length>0?aggrs.length:1):1),topArr.length,(short)(topColBegin+((isRowData?(aggrs.length>0?aggrs.length:1):1)*(topArr.length-i+1)-1))));
//								INormalCell inc2=rd.getCell(i+topRowBegin,(short)topColBegin+(topArr.length-i)*(isRowData?(aggrs.length>0?aggrs.length:1):1)); 
//								inc2.setBackColor(style.c3.backColor);
//								inc2.setForeColor(style.c3.foreColor);
//								inc2.setHAlign(style.c3.hAlian);
//								inc2.setValue("总计");
//								inc2.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//							}
//						}
//						if (isRowData) {
//							Logger.debug(fields);
//							for (int i=0; i<aggrs.length; i++) {
//								String[] ai = aggrs[i].split("<,>");
//								String exp = ai[1];
//								
//								//if
//								for (int j=0; j<(topArr.length+1); j++ ) {
//									if (j>0 && !zongji) break;
//									INormalCell inc3=rd.getCell(topArr.length+1+rowShift,(short)leftArr.length+i+1+(aggrs.length>0?aggrs.length:1)*j); 
//									inc3.setBackColor(style.c4.get(i%style.c4.size()).backColor);
//									inc3.setForeColor(style.c4.get(i%style.c4.size()).foreColor);
//									inc3.setHAlign(style.c4.get(i%style.c4.size()).hAlian);
//									//inc3.setFormat("#.##");
//									IByteMap map3=new ByteMap();
//									//map3.put(INormalCell.VALUE,ai[0]);
//									inc3.setValue(ai[0]);
//									map3.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//									inc3.setExpMap(map3);
//									rd.getColCell((short)leftArr.length+i+1+(aggrs.length>0?aggrs.length:1)*j).setColWidth(45);
//									inc3.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//									inc3.setTextWrap(true);
//								}
//								
////								INormalCell inc5=rd.getCell(topArr.length+2,(short)leftArr.length+i+1); 
////								IByteMap map5=new ByteMap();
////								map5.put(INormalCell.VALUE,"ds1."+exp);
////								if (ai.length>2 && ai[2].length() > 0) {
////									String name = "ds" + (i+100);
////									SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
////									sdc.setName(name); //设置数据集名 
////									sdc.setSQL(ai[2]); //设置sql语句 
////									dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
////									map5.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
////								};
////								inc5.setExpMap(map5);
//
//								for (int j=0; j<(topArr.length+1); j++ ) {
//									for (int k=0; k<(leftArr.length+1); k++ ) {
//										if (!(j==0 && k==0)) if(!zongji) break;
//										INormalCell inc4=rd.getCell(topArr.length+2+k+rowShift,(short)leftArr.length+i+1+(aggrs.length>0?aggrs.length:1)*j); 
//										inc4.setBackColor(style.c5.get(i%style.c5.size()).backColor);
//										inc4.setForeColor(style.c5.get(i%style.c5.size()).foreColor);
//										inc4.setHAlign(style.c5.get(i%style.c5.size()).hAlian);
//										inc4.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//										//inc4.setFormat("#.##");
//										IByteMap map4=new ByteMap();
//										map4.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//										map4.put(INormalCell.VALUE,exp);
//										map4.put(INormalCell.TIP, "3");
////										if (ai.length>2 && ai[2].length() > 0) {
////											String name = "ds" + (i+100);
////											SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
////											sdc.setName(name); //设置数据集名 
////											sdc.setSQL(ai[2]); //设置sql语句 
////											dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
////											map4.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
////										};
//										inc4.setExpMap(map4);
//										inc4.setTextWrap(true);
//									}
//								}
//							}
//						} else {
//							for (int i=0; i<aggrs.length; i++) {
//								String[] ai = aggrs[i].split("<,>");
//								String exp = ai[1];
//								
//								for (int j=0; j<(leftArr.length+1); j++ ) {
//									if (j>0 && !zongji) break;
//									INormalCell inc1=rd.getCell(topArr.length+i+1+(aggrs.length>0?aggrs.length:1)*j+rowShift,(short)leftArr.length+1); 
//									inc1.setBackColor(style.c4.get(i%style.c4.size()).backColor);
//									inc1.setForeColor(style.c4.get(i%style.c4.size()).foreColor);
//									inc1.setHAlign(style.c4.get(i%style.c4.size()).hAlian);
//									inc1.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//									//inc1.setFormat("#.##");
//									IByteMap map1=new ByteMap();
//									map1.put(INormalCell.VALUE,ai[0]);
//									inc1.setPropertyMap(map1);
//									inc1.setTextWrap(true);
//								}
//								rd.getColCell((short)leftArr.length+1).setColWidth(45);
//								
////								INormalCell inc6=rd.getCell(topArr.length+i+1,(short)leftArr.length+2); 
////								IByteMap map6=new ByteMap();
////								map6.put(INormalCell.VALUE,"ds1."+exp);
////								if (ai.length>2 && ai[2].length() > 0) {
////									String name = "ds" + (i+1000);
////									SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
////									sdc.setName(name); //设置数据集名 
////									sdc.setSQL(ai[2]); //设置sql语句 
////									dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
////									map6.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
////								};
////								inc6.setExpMap(map6);
//
//								for (int j=0; j<(topArr.length+1); j++ ) {
//									for (int k=0; k<(leftArr.length+1); k++ ) {
//										if (!(j==0 && k==0)) if(!zongji) break;
//										INormalCell inc2=rd.getCell(topArr.length+i+1+(aggrs.length>0?aggrs.length:1)*k+rowShift,(short)leftArr.length+2+j); 
//										inc2.setBackColor(style.c5.get(i%style.c5.size()).backColor);
//										inc2.setForeColor(style.c5.get(i%style.c5.size()).foreColor);
//										inc2.setHAlign(style.c5.get(i%style.c5.size()).hAlian);
//										inc2.setAdjustSizeMode(INormalCell.ADJUST_EXTEND);
//										//inc2.setFormat("#.##");
//										IByteMap map2=new ByteMap();
//										map2.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//										map2.put(INormalCell.VALUE,exp);
//										map2.put(INormalCell.TIP, "3");
////										if (ai.length>2 && ai[2].length() > 0) {
////											String name = "ds" + (i+1000);
////											SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
////											sdc.setName(name); //设置数据集名 
////											sdc.setSQL(ai[2]); //设置sql语句 
////											dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
////											map2.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
////										};
//										inc2.setExpMap(map2);
//										inc2.setTextWrap(true);
//									}
//								}
//							}
//						}
//
//						dsmd.addDataSetConfig(rc.getDs()); //把数据集定义添加到数据集元数据 
//						rd.setDataSetMetaData(dsmd); //把数据集元数据赋给ReportDefine
//						
//						if (new File("d:/temp/").exists() && new File("d:/temp/").isDirectory()) ReportUtils.write("d:/temp/olap.rpx",rd);
//						
//						Context cxt = new Context();
//						//cxt.setDefDataSourceName(dbName);
//						//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
//						Engine engine = new Engine(rd, cxt); //构造报表引擎 
//						IReport iReport = engine.calc(); //运算报表
//						rc.setIReport(iReport);
//						rc.setReportDefine(rd);
//					}
//				}
//				
//				writer.print("ok");
//				
//			} else if ("txt".equals(oper)) {
//				ResultPage rp = new ResultPage();
//				//session.setAttribute(pageId + "_rp", rp);
//				String dql = req.getParameter("dql").replaceAll("<dq>", "\"");
//				QueryTask tsk = new QueryTask(dql, "user", req.getParameter("dbName"), null);
//				//tsk.setAttrs(attrs);
//				tsks.put(tsk);
//				while (tsk.getEndTime() == 0) {
//					Thread.currentThread().sleep(1000);
//				}
//				rp.setTsk(tsk);
//				try {
//					res.setContentType( "text/plain;charset=" + Context.getJspCharset() );
//					res.setHeader( "Content-Disposition", "attachment; filename=result.txt" );
//					writer = res.getWriter();
//					rp.txt( writer, null);
//				}
//				catch ( Throwable e ) {
//					rp.status = "error:" + e.getMessage();
//					Logger.error("",e);
//					e.printStackTrace();
//				}
//			} else if ("gex".equals(oper)) {
//				ResultPage rp = new ResultPage();
//				//session.setAttribute(pageId + "_rp", rp);
//				String dql = req.getParameter("dql").replaceAll("<dq>", "\"");
//				QueryTask tsk = new QueryTask(dql, "user", req.getParameter("dbName"), null);
//				//tsk.setAttrs(attrs);
//				tsks.put(tsk);
//				while (tsk.getEndTime() == 0) {
//					Thread.currentThread().sleep(1000);
//				}
//				rp.setTsk(tsk);
//				writer = res.getWriter();
//				try {
//					//rp.gex(DataSphereServlet.ROOT_PATH + "/gexTmp/" + req.getParameter("gex") + ".gex");
//				}
//				catch ( Throwable e ) {
//					writer.println("error:" + e.getMessage());
//					Logger.error("",e);
//					e.printStackTrace();
//				}
//				writer.println("ok");
//			} else if ("gexDownload".equals(oper)) {
//				writer = res.getWriter();
//				
//				String gex = req.getParameter("gex");
//				Object oo = session.getAttribute(req.getParameter("src"));
//				if (oo == null) {
//					writer.println("error:Time out!");
//					return;
//				}
//				//CellSetUtil.writeCalcCellSet(DataSphereServlet.ROOT_PATH + "/gexTmp/" + gex + ".gex", (CalcCellSet)oo);
//				writer.println("ok");
//			} else if ("generateTxt".equals(oper)) {
//
//				
//				writer = res.getWriter();
//				Object obj = session.getAttribute(req.getParameter("rgid"));
//				if (obj == null) {
//					writer.println("error:Time out");
//					return;
//				}
////				ReportGroupConf rgc = (ReportGroupConf)obj;
//				//Logger.debug("generateTxt---" + rgc);
////				rgc.generateTxt();
//				writer.println("ok");
//			} else if ("report".equals(oper)) {
//				writer = res.getWriter();
//				String dql = req.getParameter("dql");
//				Logger.debug(req.getParameter("report"));
//				String qyx = req.getParameter("qyx");
//				String dbName = req.getParameter("dbName");
//				String rid = "r"+System.currentTimeMillis();
//				ReportConf rc = new ReportConf();
//				rc.getRss().put("dql", dql);
//				rc.setDql(dql);
//				rc.setReportId(rid);
//				rc.setDbName(dbName);
//				rc.setQyx(qyx);
//				session.setAttribute(rid, rc);
//				writer.print(rid);
//			} else if ("showTxtReport".equals(oper)) {
//				writer = res.getWriter();
//				ReportStyle style = new ReportStyle("style1");
//
//				ReportDefine rd = new ReportDefine2(1,1);
//				
//				for(int i=1;i<2;i++){ 
//					for(int j=1;j<2;j++){ 
//						rd.setBBColor(i,(short)j, style.borderColor); //设定下边框线色 
//						rd.setBBStyle(i,(short)j, style.borderStyle); //设定下边框类型 
//						rd.setBBWidth(i,(short)j, style.boderWidth); //设定下边框线粗 
//						
//						//左边框 
//						rd.setLBColor(i,(short)j, style.borderColor); 
//						rd.setLBStyle(i,(short)j, style.borderStyle); 
//						rd.setLBWidth(i,(short)j, style.boderWidth); 
//						//右边框 
//						rd.setRBColor(i,(short)j, style.borderColor); 
//						rd.setRBStyle(i,(short)j, style.borderStyle); 
//						rd.setRBWidth(i,(short)j, style.boderWidth); 
//						//上边框 
//						rd.setTBColor(i,(short)j, style.borderColor); 
//						rd.setTBStyle(i,(short)j, style.borderStyle);
//						rd.setTBWidth(i,(short)j, style.boderWidth);
//					}
//				}
//				for (int i=1; i<2; i++) {
//					rd.getRowCell(i).setRowHeight(style.height);
//				}
//				for (int j=1; j<2; j++) {
//					rd.getColCell(j).setColWidth(style.width);
//				}
//				
//				rd.setReportType(ReportDefine.RPT_NORMAL);
//				DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
//				
//				INormalCell inc=rd.getCell(1,(short)1);
//				IByteMap map1=new ByteMap();
//				map1.put(INormalCell.VALUE, "ds1.select(省份)");
//				inc.setExpMap(map1);
//
//				EsProcDataSetConfig epdsc = new EsProcDataSetConfig();
//				//epdsc.setDfxFileName(DataSphereServlet.ROOT_PATH+"/WEB-INF/dataSource.dfx");
//				ArrayList names = new ArrayList();
//				ArrayList values = new ArrayList();
//				names.add("txts");
//				values.add("\"d:/abcde.txt,d:/abcde2.txt\"");
//				names.add("dimName");
//				values.add("\"省份\"");
//				epdsc.setParamNames(names);
//				epdsc.setParamExps(values);
//				epdsc.setName("ds1");
//		
//
//				dsmd.addDataSetConfig(epdsc); //把数据集定义添加到数据集元数据 
//				rd.setDataSetMetaData(dsmd); //把数据集元数据赋给ReportDefine
//				
//				//Logger.debug("--------" + rd);
//				//if (new File("d:/").exists() && new File("d:/").isDirectory()) ReportUtils.write("d:/txt.rpx",rd);
//				Context cxt = new Context();
//				//cxt.setDefDataSourceName(dbName);
//				//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
//				Engine engine = new Engine(rd, cxt); //构造报表引擎
//				
//				IReport iReport = engine.calc(); //运算报表
//				
//				
//			} else if ("showReport".equals(oper)) {
//				//_;_地区<;><;>sql_,_年月_;_top_,_折扣率总和<;>折扣率总和,<;>_,_名称个数<;>名称个数,<;>
//				writer = res.getWriter();
//				String rid = req.getParameter("rid");
//				ReportConf rc = (ReportConf)session.getAttribute(rid);
//				String dql = req.getParameter("dql");
//				if (rc == null) {
//					writer.println("报表已超时失效，请访问查询页面重新查询！");
//					return;
//				}
//				if (rc.getTxts() == null){
//					if (dql != null) rc.getRss().put("dql", dql);
//					else dql = rc.getRss().get("dql").toString();
//				}
//				Logger.debug(req.getParameter("reportDetail"));
//				rc.setReportDetail(req.getParameter("reportDetail"));
//				String title = req.getParameter("title");
//				if ("".equals(title)) title = "报表";
//				String dbName = rc.getDbName();
//				String[] report = rc.getReportDetail().split("_;_");
//				String styleStr = req.getParameter("style");
//				rc.setStyle(styleStr);
//				IReport temp = ReportStyle.getTemplate(styleStr);
//				if (temp != null) {
//					String[] fs = null;
//					if (report.length == 2) fs = report[0].split("_,_");
//					else fs = report[3].split("_,_");
//					//TODO 2016、02、25去掉f1
//					int count = 1;
//					for (int i=0; i<fs.length; i++) {
//						String[] fss = fs[i].split("<;>");
//						String f = fss[1].split(",")[0];
//						if (fss.length>=6 && fss[5].equals("1")) {
//							dql = dql.replace(" "+f+" WHERE", " f"+count+" WHERE")
//									.replace(" "+f+" where", " f"+count+" where")
//									.replace(" "+f+" ON", " f"+count+" ON")
//									.replace(" "+f+" on", " f"+count+" on")
//									.replace(" "+f+" FROM", " f"+count+" FROM")
//									.replace(" "+f+" from", " f"+count+" from")
//									.replaceAll(""+f+" is not null", " f"+count+" is not null")
//									.replace(" "+f+",", " f"+count+",");
//							count++;
//						} 
//					}
//					
//					Logger.debug("rpx dql : " + dql);
//
//					DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
//					SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//					sdc.setName("ds1"); //设置数据集名 
//					sdc.setSQL(dql); //设置sql语句 
//					dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据 
//					
//					temp.setDataSetMetaData(dsmd); 
//					Context cxt = new Context();
//					cxt.setDefDataSourceName(dbName);
//					//Logger.debug();
//					//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
//					Engine engine = new Engine(temp, cxt); //构造报表引擎
//					
//					IReport iReport = engine.calc(); //运算报表
//					
//					rc.setIReport(iReport);
//					//rc.setReportDefine(rd);
//				} else {
//					ReportStyle style = new ReportStyle(styleStr);
//					if (report.length == 2) {
//						String[] fs = report[0].split("_,_");
//						int rowShift = 1;
//						ReportDefine rd = new ReportDefine2(2+rowShift,fs.length);
//						
//						for(int i=1;i<4;i++){ 
//							for(int j=1;j<=fs.length;j++){
//								rd.setBBColor(i,(short)j, style.borderColor); //设定下边框线色 
//								rd.setBBStyle(i,(short)j, style.borderStyle); //设定下边框类型 
//								rd.setBBWidth(i,(short)j, style.boderWidth); //设定下边框线粗 
//								
//								//左边框 
//								rd.setLBColor(i,(short)j, style.borderColor); 
//								rd.setLBStyle(i,(short)j, style.borderStyle); 
//								rd.setLBWidth(i,(short)j, style.boderWidth); 
//								//右边框 
//								rd.setRBColor(i,(short)j, style.borderColor); 
//								rd.setRBStyle(i,(short)j, style.borderStyle); 
//								rd.setRBWidth(i,(short)j, style.boderWidth); 
//								//上边框 
//								rd.setTBColor(i,(short)j, style.borderColor); 
//								rd.setTBStyle(i,(short)j, style.borderStyle);
//								rd.setTBWidth(i,(short)j, style.boderWidth);
//							}
//						}
//						for (int i=1; i<4; i++) {
//							rd.getRowCell(i).setRowHeight(style.height);
//						}
//						for (int j=1; j<=fs.length; j++) {
//							rd.getColCell(j).setColWidth(style.width);
//						}
//						
//						rd.setReportType(ReportDefine.RPT_NORMAL);
//						DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
//						
//						String sel = null;
//						String sort = "";
//						boolean oneResult = true;
//						String flts = report[1];
//						if ("none".equals(flts)) flts = "";
//						for(int i=1; i<=fs.length; i++){
//							INormalCell inc=rd.getCell(1+rowShift,(short)i);
//							String fss[] = fs[i-1].split("<;>");
//							inc.setValue(fss[0]);
//							inc.setBackColor(style.c2.backColor);
//							inc.setForeColor(style.c2.foreColor);
//							inc.setHAlign(style.c2.hAlian);
//							//inc.setTip(fss[1]+"<;>"+fss[5]);
//							
//							//inc.setFormat("#.##");
//							INormalCell inc4=rd.getCell(2+rowShift,(short)i); 
//							IByteMap map1=new ByteMap();
//							map1.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//							String[] ai1 = fss[1].split(",");
//							String exp = ai1[0];
//							if (!"0".equals(fss[3])) {
//								if (sort.length()>0) sort += ",";
//								sort += ai1[0] + ":" + fss[3];
//							}
//							if (ai1.length>1) {
//								if ("count".endsWith(ai1[1])) exp = "count("+flts+")";
//								else if ("countd".endsWith(ai1[1])) exp = "dcount("+flts+")";
//								else exp = ai1[1]+"("+ai1[0]+(flts.length()>0?","+flts:"")+")";
//								map1.put(INormalCell.VALUE,"ds1." + exp); //设置单元格的数据值表达式 
//								map1.put(INormalCell.TIP,"\""+(fss[1]+"<;>"+fss[5]+"<;>")+"\"+string(ds1."+exp+")");
//							} else {
//								oneResult = false;
//								if (i>1) {
//									map1.put(INormalCell.VALUE, "ds1." + exp);
//								} else {
//									sel = ai1[0];
//									//map1.put(INormalCell.VALUE,"ds1.select("+fss[0]+")"); //设置单元格的数据值表达式 
//								//	map1.put(INormalCell.EXTEND,INormalCell.EXTEND_VERTICAL); //设置其他属性的表达式 
//								}
//								if (fss.length>2 && fss[2].length()>0) {
//									String name = "ds" + (i+10);
//									SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//									sdc.setName(name); //设置数据集名 
//									sdc.setSQL(fss[2]); //设置sql语句 
//									dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
//									map1.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
//								}
//								map1.put(INormalCell.TIP,"\""+(fss[1]+"<;>"+fss[5]+"<;>")+"\"+string(ds1."+exp+")");
//							}
//							map1.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//							inc4.setExpMap(map1);
//							inc4.setBackColor(style.c5.get(0).backColor);
//							inc4.setForeColor(style.c5.get(0).foreColor);
//							inc4.setHAlign(style.c5.get(0).hAlian);
//							//inc4.setFormat("#.##");
//						}
//						if (!oneResult) rd.getCell(2+rowShift,(short)1).getExpMap().put(INormalCell.VALUE,"ds1.select("+sel+(flts.length()>0?","+flts:"")+";"+sort+")");
//						ReportUtils.mergeReport(rd,new Area(1,(short)1,1,fs.length));
//						IRowCell rowcell=rd.getRowCell(1); //根据行号获得行首格 
//						rowcell.setRowType(IRowCell.TYPE_TABLE_HEADER); //对行首格设置行类型
//						INormalCell inc11=rd.getCell(1,1);
//						inc11.setValue(title);
//						inc11.setHAlign(INormalCell.HALIGN_CENTER);
//						inc11.setFontSize((short)16);
//						inc11.setBold(true);
//						inc11.setBackColor(-13334105);
//						inc11.setForeColor(-1);
//						IRowCell rowcell2=rd.getRowCell(1+rowShift); //根据行号获得行首格 
//						rowcell2.setRowType(IRowCell.TYPE_TABLE_HEADER); //对行首格设置行类型
//
//						if (rc.getTxts() == null){
//							SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//							sdc.setName("ds1"); //设置数据集名 
//							sdc.setSQL(dql); //设置sql语句 
//							dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据 
//						} else {
//							EsProcDataSetConfig epdsc = new EsProcDataSetConfig();
//							//epdsc.setDfxFileName(DataSphereServlet.ROOT_PATH+"/WEB-INF/dataSource.dfx");
//							ArrayList names = new ArrayList();
//							ArrayList values = new ArrayList();
//							names.add("txts");
////							values.add("\""+rc.getTxtFulls()+"\"");
//							names.add("dimName");
//							values.add("\""+rc.getDimName()+"\"");
//							names.add("type");
//							values.add("2");
//							epdsc.setParamNames(names);
//							epdsc.setParamExps(values);
//							epdsc.setName("ds1");
//							dsmd.addDataSetConfig(epdsc); //把数据集定义添加到数据集元数据 
//						}
//						rd.setDataSetMetaData(dsmd); //把数据集元数据赋给ReportDefine
//						
//						
//						//if (new File("d:/").exists() && new File("d:/").isDirectory()) ReportUtils.write("d:/group.rpx",rd);
//						Context cxt = new Context();
//						cxt.setDefDataSourceName(dbName);
//						//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
//						Engine engine = new Engine(rd, cxt); //构造报表引擎
//						
//						IReport iReport = engine.calc(); //运算报表
//						
//						rc.setIReport(iReport);
//						rc.setReportDefine(rd);
//
////						ProcDataSetConfig pdc = new ProcDataSetConfig();
////						pdc.setName("ds1");
////						pdc.setSQL("");
//					
//					} else {
//						String tops[] = {};
//						if (report[0].length()>0) tops = report[0].split("_,_");
//						String lefts[] = {};
//						if (report[1].length()>0) lefts = report[1].split("_,_");
//						boolean isRowData = "top".equals(report[2]);
//						String aggrs[] = report[3].split("_,_");
//						boolean zongji = rc.isShowTotal();
//						
//						int row=0,col=0;
//						int rowShift = 1;
//						row += tops.length + rowShift;
//						if (isRowData) {
//							row += 1;
//							if (zongji) row += lefts.length+1;
//							else row += 1;
//						} else {
//							row += (zongji?(lefts.length+1):1) * aggrs.length;
//						}
//						col += lefts.length;
//						if (isRowData) {
//							col += (zongji?(tops.length+1):1) * aggrs.length;
//						} else {
//							col += 1;
//							if (zongji) {
//								col += tops.length + 1;
//							} else {
//								col += 1;
//							}
//						}
//						String flts = report[4];
//						if ("none".equals(flts)) flts = "";
//						//col += isRowData?aggrs.length:2;
//
//						ReportDefine rd = new ReportDefine2(row,col);
//						
//						ReportUtils.mergeReport(rd,new Area(1,(short)1,1,col));
//						IRowCell rowcell=rd.getRowCell(1); //根据行号获得行首格 
//						rowcell.setRowType(IRowCell.TYPE_TABLE_HEADER); //对行首格设置行类型
//						INormalCell inc11=rd.getCell(1,1);
//						inc11.setValue(title);
//						inc11.setHAlign(INormalCell.HALIGN_CENTER);
//						inc11.setFontSize((short)16);
//						inc11.setBold(true);
//						inc11.setBackColor(-13334105);
//						inc11.setForeColor(-1);
//						
//						for(int i=1;i<=row;i++){ 
//							for(int j=1;j<=col;j++){ 
//								rd.setBBColor(i,(short)j, style.borderColor); //设定下边框线色 
//								rd.setBBStyle(i,(short)j, style.borderStyle); //设定下边框类型 
//								rd.setBBWidth(i,(short)j, style.boderWidth); //设定下边框线粗 
//								
//								//左边框 
//								rd.setLBColor(i,(short)j, style.borderColor); 
//								rd.setLBStyle(i,(short)j, style.borderStyle);
//								rd.setLBWidth(i,(short)j, style.boderWidth); 
//								//右边框 
//								rd.setRBColor(i,(short)j, style.borderColor); 
//								rd.setRBStyle(i,(short)j, style.borderStyle);
//								rd.setRBWidth(i,(short)j, style.boderWidth); 
//								//上边框 
//								rd.setTBColor(i,(short)j, style.borderColor); 
//								rd.setTBStyle(i,(short)j, style.borderStyle);
//								rd.setTBWidth(i,(short)j, style.boderWidth);
//							}
//						}
//						for (int i=1; i<=row; i++) {
//							rd.getRowCell(i).setRowHeight(style.height);
//						}
//						for (int j=1; j<=col; j++) {
//							rd.getColCell(j).setColWidth(style.width);
//						}
//						
//						rd.setReportType(ReportDefine.RPT_NORMAL);
//						DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
//						
//						if (tops.length>0 && lefts.length>0) {
//							ReportUtils.mergeReport(rd,new Area(1+rowShift,(short)1,tops.length+(isRowData?1:0)+rowShift,lefts.length+(isRowData?0:1)));
//							INormalCell inc1=rd.getCell(1+rowShift,(short)1); 
//							inc1.setBackColor(style.c1.backColor);
//							inc1.setForeColor(style.c1.foreColor);
//							inc1.setHAlign(style.c1.hAlian);
//							//inc1.setFormat("#.##");
//						}
//						Logger.debug(new String("lefts : " + lefts.length));
//						
//						String dims = "";
//						int leftRowBegin = tops.length+(isRowData?2:1)+rowShift;
//						int leftColBegin = 1;
//						//String allFilter = "";
//						for (int i=0; i<lefts.length; i++) {
//							String lss[] = lefts[i].split("<;>");
//							ReportUtils.mergeReport(rd,new Area(leftRowBegin,(short)i+leftColBegin,leftRowBegin+(zongji?((isRowData?1:aggrs.length)*(lefts.length-i)-1):(isRowData?0:aggrs.length-1)),(short)i+leftColBegin));
//							INormalCell inc1=rd.getCell(leftRowBegin,(short)i+leftColBegin); 
//							inc1.setBackColor(style.c2.backColor);
//							inc1.setForeColor(style.c2.foreColor);
//							inc1.setHAlign(style.c2.hAlian);
//							//inc1.setFormat("#.##");
//							IByteMap map1=new ByteMap();
//							map1.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//							String filter = "";
//							if(lss.length>4) {
//								filter = lss[4];
//								if (filter != "") {
//									String v0 = filter.indexOf(",")>0?filter.substring(0,filter.indexOf(",")):filter;
//									String v = "";
//									try {
//										Integer.parseInt(v0);
//									} catch(Exception e){
//										v = "\"";
//									}
//									filter = filter.replaceAll(",", v+" || "+lss[0]+"=="+v);
//									filter = ","+lss[0]+"=="+v+filter+v;
//								}
//							} 
////							if (filter.length()>0) {
////								if (allFilter.length()>0) allFilter += " && ";
////								allFilter += "(" + filter.substring(1) + ")";
////							}
//							map1.put(INormalCell.VALUE,"ds1.group("+lss[0]+","+(i==0?flts:"")+";"+lss[0]+":"+lss[3]+")");
//							inc1.setExtendMode(INormalCell.EXTEND_VERTICAL);
//							String n = zimu[i]+(tops.length+(isRowData?2:1)+rowShift);
//							if (i>0) dims += "+\";\"+";
//							dims += "\""+lss[0]+":\"+string(" + n+")";
//							//if (i>0) inc1.setLeftHead(zimu[i-1]+tops.length+(isRowData?2:1));
//							map1.put(INormalCell.TIP, dims);
//							if (lss.length>2 && lss[2].length() > 0) {
//								String name = "ds" + (i+300);
//								SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//								sdc.setName(name); //设置数据集名 
//								sdc.setSQL(lss[2]); //设置sql语句 
//								dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
//								map1.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
//							};
//							inc1.setExpMap(map1);
//							if (zongji/* && i<lefts.length-1*/) {
//								ReportUtils.mergeReport(rd,new Area(leftRowBegin+(lefts.length-i)*(isRowData?1:aggrs.length),(short)i+leftColBegin,leftRowBegin+((isRowData?1:aggrs.length)*(lefts.length-i+1)-1),(short)lefts.length));
//								INormalCell inc2=rd.getCell(leftRowBegin+(lefts.length-i)*(isRowData?1:aggrs.length),(short)i+leftColBegin); 
//								inc2.setBackColor(style.c3.backColor);
//								inc2.setForeColor(style.c3.foreColor);
//								inc2.setHAlign(style.c3.hAlian);
//								inc2.setValue("总计");
//							}
//						}
//						dims = "";
//						int topRowBegin = 1+rowShift;
//						int topColBegin = lefts.length+(isRowData?1:2);
//						for (int i=0; i<tops.length; i++) {
//							String lss[] = tops[i].split("<;>");
//							ReportUtils.mergeReport(rd,new Area(i+topRowBegin,(short)topColBegin,i+topRowBegin,(short)(topColBegin+(zongji?((isRowData?aggrs.length:1)*(tops.length-i)-1):(isRowData?aggrs.length-1:0)))));
//							INormalCell inc1=rd.getCell(i+topRowBegin,(short)topColBegin); 
//							inc1.setBackColor(style.c2.backColor);
//							inc1.setForeColor(style.c2.foreColor);
//							inc1.setHAlign(style.c2.hAlian);
//							//inc1.setFormat("#.##");
//							IByteMap map1=new ByteMap();
//							map1.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//							String filter = "";
//							if(lss.length>4) {
//								filter = lss[4];
//								if (filter != "") {
//									String v0 = filter.indexOf(",")>0?filter.substring(0,filter.indexOf(",")):filter;
//									String v = "";
//									try {
//										Integer.parseInt(v0);
//									} catch(Exception e){
//										v = "\"";
//									}
//									filter = filter.replaceAll(",", v+" || "+lss[0]+"=="+v);
//									filter = ","+lss[0]+"=="+v+filter+v;
//								}
//							} 
////							if (filter.length()>0) {
////								if (allFilter.length()>0) allFilter += " && ";
////								allFilter += "(" + filter.substring(1) + ")";
////							}
//							map1.put(INormalCell.VALUE,"ds1.group("+lss[0]+","+(i==0?flts:"")+";"+lss[0]+":"+lss[3]+")");
//							inc1.setExtendMode(INormalCell.EXTEND_HORIZONTAL);
//							String n = zimu[lefts.length+(isRowData?1:2)-1]+(i+1+rowShift);
//							if (i>0) dims += "+\";\"+";
//							dims += "\""+lss[0]+":\"+string(" + n+")";
//							map1.put(INormalCell.TIP, dims);
//							if (lss.length>2 && lss[2].length() > 0) {
//								String name = "ds" + (i+200);
//								SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//								sdc.setName(name); //设置数据集名 
//								sdc.setSQL(lss[2]); //设置sql语句 
//								dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
//								map1.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
//							};
//							inc1.setExpMap(map1);
//							if (zongji/* && i<tops.length-1*/) {
////								Logger.debug(i+topRowBegin+1);
////								Logger.debug((short)topColBegin+(tops.length-i-1)*aggrs.length);
////								Logger.debug(tops.length);
////								Logger.debug((short)(topColBegin+((isRowData?aggrs.length:1)*(tops.length-i)-1)));
//								ReportUtils.mergeReport(rd,new Area(i+topRowBegin,(short)topColBegin+(tops.length-i)*(isRowData?aggrs.length:1),tops.length,(short)(topColBegin+((isRowData?aggrs.length:1)*(tops.length-i+1)-1))));
//								INormalCell inc2=rd.getCell(i+topRowBegin,(short)topColBegin+(tops.length-i)*(isRowData?aggrs.length:1)); 
//								inc2.setBackColor(style.c3.backColor);
//								inc2.setForeColor(style.c3.foreColor);
//								inc2.setHAlign(style.c3.hAlian);
//								inc2.setValue("总计");
//							}
//						}
//						if (isRowData) {
//							for (int i=0; i<aggrs.length; i++) {
//								String[] ai = aggrs[i].split("<;>");
//								String[] ai1 = ai[1].split(",");
//								String exp = ai1[0];
//								if (ai1.length>1) {
//									if ("count".endsWith(ai1[1])) exp = "count("+flts+")";
//									else if ("countd".endsWith(ai1[1])) exp = "dcount("+flts+")";
//									else exp = ai1[1]+"("+ai1[0]+","+flts+")";
//								}
//								
//								for (int j=0; j<(tops.length+1); j++ ) {
//									if (j>0 && !zongji) break;
//									INormalCell inc3=rd.getCell(tops.length+1+rowShift,(short)lefts.length+i+1+aggrs.length*j); 
//									inc3.setBackColor(style.c4.get(i%style.c4.size()).backColor);
//									inc3.setForeColor(style.c4.get(i%style.c4.size()).foreColor);
//									inc3.setHAlign(style.c4.get(i%style.c4.size()).hAlian);
//									//inc3.setFormat("#.##");
//									IByteMap map3=new ByteMap();
//									map3.put(INormalCell.VALUE,ai[0]);
//									map3.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//									inc3.setExpMap(map3);
//									rd.getColCell((short)lefts.length+i+1+aggrs.length*j).setColWidth(45);
//								}
//								
////								INormalCell inc5=rd.getCell(tops.length+2,(short)lefts.length+i+1); 
////								IByteMap map5=new ByteMap();
////								map5.put(INormalCell.VALUE,"ds1."+exp);
////								if (ai.length>2 && ai[2].length() > 0) {
////									String name = "ds" + (i+100);
////									SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
////									sdc.setName(name); //设置数据集名 
////									sdc.setSQL(ai[2]); //设置sql语句 
////									dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
////									map5.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
////								};
////								inc5.setExpMap(map5);
//
//								for (int j=0; j<(tops.length+1); j++ ) {
//									for (int k=0; k<(lefts.length+1); k++ ) {
//										if (!(j==0 && k==0)) if(!zongji) break;
//										INormalCell inc4=rd.getCell(tops.length+2+k+rowShift,(short)lefts.length+i+1+aggrs.length*j); 
//										inc4.setBackColor(style.c5.get(i%style.c5.size()).backColor);
//										inc4.setForeColor(style.c5.get(i%style.c5.size()).foreColor);
//										inc4.setHAlign(style.c5.get(i%style.c5.size()).hAlian);
//										//inc4.setFormat("#.##");
//										IByteMap map4=new ByteMap();
//										map4.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//										map4.put(INormalCell.VALUE,"ds1."+exp);
//										if (ai.length>2 && ai[2].length() > 0) {
//											String name = "ds" + (i+100);
//											SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//											sdc.setName(name); //设置数据集名 
//											sdc.setSQL(ai[2]); //设置sql语句 
//											dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
//											map4.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
//										};
//										inc4.setExpMap(map4);
//									}
//								}
//							}
//						} else {
//							for (int i=0; i<aggrs.length; i++) {
//								String[] ai = aggrs[i].split("<;>");
//								String[] ai1 = ai[1].split(",");
//								String exp = ai1[0];
//								if (ai1.length>1) {
//									if ("count".endsWith(ai1[1])) exp = "count("+flts+")";
//									else if ("countd".endsWith(ai1[1])) exp = "dcount("+flts+")";
//									else exp = ai1[1]+"("+ai1[0]+","+flts+")";
//								}
//								
//								for (int j=0; j<(lefts.length+1); j++ ) {
//									if (j>0 && !zongji) break;
//									INormalCell inc1=rd.getCell(tops.length+i+1+aggrs.length*j+rowShift,(short)lefts.length+1); 
//									inc1.setBackColor(style.c4.get(i%style.c4.size()).backColor);
//									inc1.setForeColor(style.c4.get(i%style.c4.size()).foreColor);
//									inc1.setHAlign(style.c4.get(i%style.c4.size()).hAlian);
//									//inc1.setFormat("#.##");
//									IByteMap map1=new ByteMap();
//									map1.put(INormalCell.VALUE,ai[0]);
//									inc1.setPropertyMap(map1);
//								}
//								rd.getColCell((short)lefts.length+1).setColWidth(45);
//								
////								INormalCell inc6=rd.getCell(tops.length+i+1,(short)lefts.length+2); 
////								IByteMap map6=new ByteMap();
////								map6.put(INormalCell.VALUE,"ds1."+exp);
////								if (ai.length>2 && ai[2].length() > 0) {
////									String name = "ds" + (i+1000);
////									SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
////									sdc.setName(name); //设置数据集名 
////									sdc.setSQL(ai[2]); //设置sql语句 
////									dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
////									map6.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
////								};
////								inc6.setExpMap(map6);
//
//								for (int j=0; j<(tops.length+1); j++ ) {
//									for (int k=0; k<(lefts.length+1); k++ ) {
//										if (!(j==0 && k==0)) if(!zongji) break;
//										INormalCell inc2=rd.getCell(tops.length+i+1+aggrs.length*k+rowShift,(short)lefts.length+2+j); 
//										inc2.setBackColor(style.c5.get(i%style.c5.size()).backColor);
//										inc2.setForeColor(style.c5.get(i%style.c5.size()).foreColor);
//										inc2.setHAlign(style.c5.get(i%style.c5.size()).hAlian);
//										//inc2.setFormat("#.##");
//										IByteMap map2=new ByteMap();
//										map2.put(INormalCell.FORMAT, "if(ifnumber(if(value()==null,\"\",value())),\"#.##\",if(ifdate(if(value()==null,\"\",value())),\"yyyy-MM-dd HH:mm:ss\",\"\"))");
//										map2.put(INormalCell.VALUE,"ds1."+exp);
//										if (ai.length>2 && ai[2].length() > 0) {
//											String name = "ds" + (i+1000);
//											SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//											sdc.setName(name); //设置数据集名 
//											sdc.setSQL(ai[2]); //设置sql语句 
//											dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
//											map2.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
//										};
//										inc2.setExpMap(map2);
//									}
//								}
//							}
//						}
//						
//						if (rc.getTxts() == null){
//							SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//							sdc.setName("ds1"); //设置数据集名 
//							sdc.setSQL(dql); //设置sql语句 
//							dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据 
//						} else {
//							EsProcDataSetConfig epdsc = new EsProcDataSetConfig();
//							////epdsc.setDfxFileName(DataSphereServlet.ROOT_PATH+"/WEB-INF/dataSource.dfx");
//							ArrayList names = new ArrayList();
//							ArrayList values = new ArrayList();
//							names.add("txts");
////							values.add("\""+rc.getTxtFulls()+"\"");
//							names.add("dimName");
//							values.add("\""+rc.getDimName()+"\"");
//							names.add("type");
//							values.add("2");
//							epdsc.setParamNames(names);
//							epdsc.setParamExps(values);
//							epdsc.setName("ds1");
//							dsmd.addDataSetConfig(epdsc); //把数据集定义添加到数据集元数据 
//						}
//						rd.setDataSetMetaData(dsmd); //把数据集元数据赋给ReportDefine
//						
//						//if (new File("d:/").exists() && new File("d:/").isDirectory()) ReportUtils.write("d:/olap.rpx",rd);
//						
//						Context cxt = new Context();
//						cxt.setDefDataSourceName(dbName);
//						//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
//						Engine engine = new Engine(rd, cxt); //构造报表引擎 
//						IReport iReport = engine.calc(); //运算报表
//						rc.setIReport(iReport);
//						rc.setReportDefine(rd);
//					}
//				}
//				writer.print(rid);
//			} else if ("showGraph".equals(oper)) {
//				writer = res.getWriter();
//				byte graphType = Byte.parseByte(req.getParameter("graphType"));
//				String gid = req.getParameter("gid");
//				//_;_地区<;><;>sql_,_年月_;_top_,_折扣率总和<;>折扣率总和,<;>_,_名称个数<;>名称个数,<;>
//				String rid = req.getParameter("rid");
//				int width = Integer.parseInt(req.getParameter("width"));
//				int height = Integer.parseInt(req.getParameter("height"));
//				String title = req.getParameter("title");
//				if ("".equals(title)) title = "统计图";
//				ReportConf rc = (ReportConf)session.getAttribute(rid);
//				if (rc == null) {
//					writer.println("统计图已超时失效，请访问查询页面重新查询！");
//					return;
//				}
//				String dql = req.getParameter("dql");
//				if (rc.getTxts() == null){
//					if (dql != null) rc.getRss().put("dql", dql);
//					else dql = rc.getRss().get("dql").toString();
//				}
//				String def = req.getParameter("reportDetail");
//				Logger.debug(new String("GRAPH : " + def));
//				String dbName = rc.getDbName();
////				GraphConf conf = new GraphConf();
////				conf.setGraphId(gid);
//				String[] report = def.split("_;_");
//				if (report.length == 1) {
//					
//				} else {
//					String tops[] = {};
//					if (report[0].length()>0) tops = report[0].split("_,_");
//					String lefts[] = {};
//					if (report[1].length()>0) lefts = report[1].split("_,_");
//					String[] dims = new String[tops.length + lefts.length];
//					for (int i=0; i<tops.length; i++) {
//						dims[i] = tops[i];
//					}
//					for (int i=0; i<lefts.length; i++) {
//						dims[tops.length+i] = lefts[i];
//					}
//					//boolean isRowData = "top".equals(report[2]);
//					String aggrs[] = report[3].split("_,_");
//					//boolean zongji = rc.isShowTotal();
//					
//					int row=2,col=3;
//					
//					ReportDefine rd = new ReportDefine2(row,col);
//					
//					for(int i=1;i<=row;i++){ 
//						for(int j=1;j<=col;j++){ 
//							rd.setBBColor(i,(short)j, 16777215); //设定下边框线色 
//							rd.setBBStyle(i,(short)j, INormalCell.LINE_SOLID); //设定下边框类型 
//							rd.setBBWidth(i,(short)j, (float) 0.75); //设定下边框线粗 
//							
//							//左边框 
//							rd.setLBColor(i,(short)j, 16777215); 
//							rd.setLBStyle(i,(short)j, INormalCell.LINE_SOLID); 
//							rd.setLBWidth(i,(short)j, (float) 0.75); 
//							//右边框 
//							rd.setRBColor(i,(short)j, 16777215); 
//							rd.setRBStyle(i,(short)j, INormalCell.LINE_SOLID); 
//							rd.setRBWidth(i,(short)j, (float) 0.75); 
//							//上边框 
//							rd.setTBColor(i,(short)j, 16777215); 
//							rd.setTBStyle(i,(short)j, INormalCell.LINE_SOLID); 
//							rd.setTBWidth(i,(short)j, (float) 0.75);
//						}
//					}
//					
//					rd.setReportType(ReportDefine.RPT_NORMAL);
//					DataSetMetaData dsmd=new DataSetMetaData(); //构造数据集元数据 
//					
//					Logger.debug(new String("dims : " + dims.length));
//					
//					String allFilter = report[4];
//					if ("none".equals(allFilter)) allFilter = "";
//					else allFilter = "," + allFilter;
//					for (int i=0; i<dims.length; i++) {
//						if (i>1) break;
//						String lss[] = dims[i].split("<;>");
//						INormalCell inc1=rd.getCell(1,(short)i+1); 
//						inc1.setBackColor(-10793923);
//						inc1.setForeColor(-1);
//						inc1.setHAlign(INormalCell.HALIGN_CENTER);
//						IByteMap map1=new ByteMap();
////						String filter = "";
////						if(lss.length>4) {
////							filter = lss[4];
////							if (filter != "") {
////								String v0 = filter.indexOf(",")>0?filter.substring(0,filter.indexOf(",")):filter;
////								String v = "";
////								try {
////									Integer.parseInt(v0);
////								} catch(Exception e){
////									v = "\"";
////								}
////								filter = filter.replaceAll(",", v+" || "+lss[0]+"=="+v);
////								filter = ","+lss[0]+"=="+v+filter+v;
////							}
////						} 
////						if (filter.length()>0) {
////							if (allFilter.length()>0) allFilter += " && ";
////							allFilter += "(" + filter.substring(1) + ")";
////						}
//						
//						map1.put(INormalCell.VALUE,"ds1.group("+lss[0]+(i==0?allFilter:"")+";"+lss[0]+":1)");
//						inc1.setExtendMode(INormalCell.EXTEND_VERTICAL);
//						if (lss.length>2 && lss[2].length() > 0) {
//							String name = "ds" + (i+300);
//							SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//							sdc.setName(name); //设置数据集名 
//							sdc.setSQL(lss[2]); //设置sql语句 
//							dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
//							map1.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
//						};
//						inc1.setExpMap(map1);
//					}
//					allFilter = "";
//
//					String[] ai = aggrs[0].split("<;>");
//					String[] ai1 = ai[1].split(",");
//					String exp = ai1[0];
//					if (ai1.length>1) {
//						if ("count".endsWith(ai1[1])) exp = "count("+allFilter+")";
//						else if ("countd".endsWith(ai1[1])) exp = "dcount("+allFilter+")";
//						else exp = ai1[1]+"("+ai1[0]+","+allFilter+")";
//					}
//					
//					INormalCell inc5=rd.getCell(1,(short)3); 
//					IByteMap map5=new ByteMap();
//					map5.put(INormalCell.VALUE,"ds1."+exp);
//					if (ai.length>2 && ai[2].length() > 0) {
//						String name = "ds100";
//						SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//						sdc.setName(name); //设置数据集名 
//						sdc.setSQL(ai[2]); //设置sql语句 
//						dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据
//						map5.put(INormalCell.DISPVALUE, name + ".select(#2,#1==value(),1)");
//					};
//					inc5.setExpMap(map5);
//					rd.getRowCell(1).setRowVisible(false);
//
//					ReportUtils.mergeReport(rd,new Area(2,(short)1,2,(short)3));
//					INormalCell inc=rd.getCell(2,(short)1);
//					//inc.setCellType(INormalCell.TYPE_GRAPH);
//					ReportGraphProperty g = inc.getGraphProperty();
//					Logger.debug("--------" + g);
//					if (g == null) g = new ReportGraphProperty();
//					g.setGraphTitle(title);
//					GraphCategory[] gcs = new GraphCategory[1];
//					GraphCategory gc = new GraphCategory();
//					gcs[0] = gc;
//					gc.setCategory("=A1");
//					GraphSery[] gss = new GraphSery[1];
//					GraphSery gs = new GraphSery();
//					gs.setExp("=C1");
//					gs.setName(dims.length>1?"=B1":"=\""+ai[0]+"\"");
//					gss[0] = gs;
//					gc.setSeries(gss);
//					g.setCategories(gcs);
//					g.setType(graphType);
//					IByteMap map1=new ByteMap();
//					map1.put(INormalCell.TYPE, INormalCell.TYPE_GRAPH);
//					map1.put(INormalCell.BCOLOR, new Integer(16777215));
//					map1.put(INormalCell.GRAPH, g);
//					inc.setPropertyMap(map1);
//					IByteMap map2=new ByteMap();
//					map2.put(INormalCell.VALUE, "graph()");
//					inc.setExpMap(map2);
////					inc.setValue("graph()");
////					inc.setBackColor(16777215);
////					inc.setGraphProperty(g);
//
//					rd.getColCell(1).setColWidth(new Double(width/2.84).intValue() - 50);
//					rd.getRowCell(2).setRowHeight(new Double(height/2.84).intValue());
//
//					if (rc.getTxts() == null){
//						SQLDataSetConfig sdc=new SQLDataSetConfig(); //构造数据集定义 
//						sdc.setName("ds1"); //设置数据集名 
//						sdc.setSQL(dql); //设置sql语句 
//						dsmd.addDataSetConfig(sdc); //把数据集定义添加到数据集元数据 
//					} else {
//						EsProcDataSetConfig epdsc = new EsProcDataSetConfig();
//						////epdsc.setDfxFileName(DataSphereServlet.ROOT_PATH+"/WEB-INF/dataSource.dfx");
//						ArrayList names = new ArrayList();
//						ArrayList values = new ArrayList();
//						names.add("txts");
////						values.add("\""+rc.getTxtFulls()+"\"");
//						names.add("dimName");
//						values.add("\""+rc.getDimName()+"\"");
//						names.add("type");
//						values.add("2");
//						epdsc.setParamNames(names);
//						epdsc.setParamExps(values);
//						epdsc.setName("ds1");
//						dsmd.addDataSetConfig(epdsc); //把数据集定义添加到数据集元数据 
//					}
//					rd.setDataSetMetaData(dsmd); //把数据集元数据赋给ReportDefine
//					
//					//if (new File("d:/").exists() && new File("d:/").isDirectory()) ReportUtils.write("d:/graph.rpx",rd);
//					
//					Context cxt = new Context();
//					cxt.setDefDataSourceName(dbName);
//					//.......................... //其它辅助代码，例如往报表引擎传递参数和宏，传递数据库连接参数等，见后面的介绍 
//					Engine engine = new Engine(rd, cxt); //构造报表引擎 
//					IReport iReport = engine.calc(); //运算报表
//					//rc.setIReport(iReport);
////					conf.setGraphReport(iReport);
////					conf.setGraphReportDefine(rd);
////					session.setAttribute(gid, conf);
//				}
//				writer.print(gid);
//			}
//		}
//		catch ( Throwable e ) {
//			Logger.error("",e);
//			e.printStackTrace();
//			if (o != null) {
//				//ResultPage rp = (ResultPage)o;
//				//rp.status = "error:" + e.getMessage();
//			}
//			try {
//				if (pw != null) pw.write("error:" + e.getMessage());
//			} catch (IOException e1) {
//			}
//			try {
//				writer.write("error:" + e.getMessage());
//			} catch (Exception e1) {
//			}
//		}
//		finally {
//			////new Sequence().binary(new String[]{"off"});
//			//			try {
////				if ( pw != null ) {
////					pw.close();
////				}
////			}
////			catch ( Exception e ) {}
//		}
//	}
//	
//	//{ id:2, pId:1, name:"张颖",real:"1",dim:'雇员'}
//	private int currId = 0;
//	private void genJson(StringBuffer s, Sequence si, Sequence levels, String name, int level, int parent, boolean first, int maxDimSize){
//		for (int i=1; i<=si.length(); i++) {
//			Object o = si.get(i);
//			if (!(o instanceof Sequence)) continue;
//			if (currId%1000000>=maxDimSize) {
//				Logger.warn("维【"+name+"】的数据太多，未加载完整");
//				break;
//			}
//			Record r = getRecord(o);
//			s.append("\t\t\t");
//			if (!first || i!=1) s.append(",");
//			String names[] = r.getFieldNames();
//			int codePos = ((Integer)levels.get(level)).intValue()-1;
//			String codeName = names[codePos];
//			String dimName = codeName;
//			if ("CODE".equals(dimName)) dimName = name;
//			String dispName = "DISP";
//			if (codePos != 0) dispName = codeName+"DISP";
//			int dispPos = -1;
//			for (int j=0; j<names.length; j++) {
//				if (dispName.equalsIgnoreCase(names[j])) dispPos = j;
//			}
//
//			Object code1 = r.getFieldValue(codePos);
//			String code = code1.toString();
//			if (!(code1 instanceof Number)) code = "\""+code+"\"";
//			String disp = "";
//			if (dispPos != -1) {
//				Object disp1 = r.getFieldValue(dispPos);
//				disp = disp1.toString();
//				if (!(disp1 instanceof Number)) disp = "\""+disp+"\"";
//			} else disp = code;
//			currId++;
//			s.append("{\"id\":"+currId+",\"pId\":"+parent+",\"name\":"+disp+",\"real\":"+code+",\"dim\":\""+dimName+"\"}\n");
//			genJson(s,(Sequence)o, levels, name, level+1, currId, false, maxDimSize);
//		}
//	}
//	
//	private Record getRecord(Object o) {
//		if (o instanceof Record) {
//			return (Record)o;
//		} else if (o instanceof Sequence) {
//			return getRecord(((Sequence)o).get(1));
//		}
//		return null;
//	}
//	
//	public static int getRGB(String rgb) {
//		if (rgb.indexOf(",")==-1) return Integer.parseInt(rgb); 
//		String[] s = rgb.split(",");
//		if (s.length != 3) return 0;
//		return new Color(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2])).getRGB();
//	}
//	public static String[] zimu = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
//	//public static String getZiMu(short i){
//	
//	//}
//	public static void main(String args[]) {
//		Logger.debug("aaa_;_".split("_;_").length);
//		String ss = "sdfasdfsa";
//		String [] s = new String[ss.length()];
//		for (int i=0; i<s.length; i++) s[i] = ss.substring(i, i+1);
//	}
}