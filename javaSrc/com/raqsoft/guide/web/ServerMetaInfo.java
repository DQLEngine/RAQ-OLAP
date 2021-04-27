package com.raqsoft.guide.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.raqsoft.common.DBSessionFactory;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.ParamList;
import com.raqsoft.guide.resource.GuideMessage;
import com.raqsoft.guide.web.dl.ConfigUtil;
import com.raqsoft.guide.web.dl.ReportStyle;
import com.raqsoft.guide.web.dl.SaveUtil;
import com.raqsoft.report.view.ReportConfig;

public class ServerMetaInfo {
	//异步刷新页面的一系列内容
	private String view;
	private String olap;
	private String dataSource;
	private String fixedTable;
	private String ql;
	private String dfxFile;
	private String dfxScript;
	private String dfxParams;
	private String inputFiles;
	private String queryPage;
	private String simplePage;
	private String olapPage;
	private String fileHome;
	private String resultRpxPrefixOnServer;
	private String canEditDataSet;
	private String sqlType;
	
	private String reportPage;
	private String maxDataSize;
	private String maxReportSize;
	private String olapFolderOnServer;
	private String dataFolderOnServer;
	private String dfxFolderOnServer;
	private String inputFileFolderOnServer;
	private String rpxFolderOnServer;
	private String outerCondition;
	private String dimDataOnServer;
	private String dataFileType;
	private String showSubTable;
	
	private String canEditReport;
	private String showHistoryRpx;
	private String styleRpx;
	
	public void service(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding( "UTF-8" );
			HttpSession session = request.getSession( true );
			//ReportServlet.reloadConfig( pageContext.getServletContext() );
			response.setContentType("text/html;charset=utf8");  
			PrintWriter out = response.getWriter();
			//PrintWriter out = response.getWriter();
			
			String error = "";
			
			//TODO 对所有输入的参数进行检查，不符合要求的明确报出错误，避免到后面用的时候发生未知的错误
			//if (dataScope.matches("")) {	
			//}

			if (DataSphereServlet.DATASOURCES == null) {
				DataSphereServlet.DATASOURCES = "";
				DataSphereServlet.DQLDATASOURCES = "";
				Map map = Env.getDBSessionFactories();
				if (map == null || map.keySet() == null) {
					Logger.warn("not config any data source!");
				} else {
					Iterator iter = map.keySet().iterator();
					while (iter.hasNext()) {
						String n = iter.next().toString();
						ISessionFactory isf = Env.getDBSessionFactory(n);
						if (isf instanceof DBSessionFactory) {
							DBSessionFactory dbsf = (DBSessionFactory)isf;
							if ("com.datalogic.jdbc.LogicDriver".equals(dbsf.getDBConfig().getDriver())) {
								if (DataSphereServlet.DQLDATASOURCES.length()>0) DataSphereServlet.DQLDATASOURCES += ";";
								DataSphereServlet.DQLDATASOURCES += n;
							}
							if (DataSphereServlet.DATASOURCES.length()>0) DataSphereServlet.DATASOURCES += ";";
							DataSphereServlet.DATASOURCES += n;
						}
					}
				}
			}
			
			String dataId = "";
			String tableNames = "";

			ParamList pl = new ParamList();
			//TODO 处理dfxParams
			
			if (this.dfxParams != null) {
//				if (obj.paramMode.indexOf("p")>=0) {
//					String[] ss = obj.params.split("\"");
//					for (int i=0; i<ss.length; i++) {
//						if (i%2 == 1) {
//							ss[i] = ss[i].replaceAll("=", "_dengyu_").replaceAll(";", "_fenhao_");
//						}
//					}
//					obj.params = "";
//					for (int i=0; i<ss.length; i++) {
//						obj.params += ss[i];
//					}
//				}
//				String ps[] = dfxParams.split(";");
//				for (int i=0; i<ps.length; i++) {
//					String[] psi = ps[i].split("=");
//					if (psi.length == 2) {
//						//System.out.println(psi[0] + "-----" + psi[1]);
//						String ps0 = psi[0].replaceAll("_dengyu_","=").replaceAll("_fenhao_",";");
//						String ps1 = psi[1].replaceAll("_dengyu_","=").replaceAll("_fenhao_",";");
//						Param p = obj.paramList.get(ps0);
//						if (p != null) p.setValue(ps1);//p.setValue(Variant.parse(ps1));
//						else {
//							Param p2 = new Param();
//							p2.setName(ps0);
//							p2.setValue(ps1);//.setValue(Variant.parse(ps1));
//							pl.add(p2);
//						}
//					}
//				}
			}
			
			String olapStr = "";
			if (olap.length()>0) { // grpx源
				File f = new File(olap);
				if (f.exists()) {
					olapStr = SaveUtil.readFile(f);
				} else {
					try {
						f = new File(DataSphereServlet.getFilePath(olap));
						if (f.exists()) {
							olapStr = SaveUtil.readFile(f);
						} else olapStr = olap;
					} catch (Exception e) {
						olapStr = olap;
					}
				}
			}
			if(olapStr.length() > 0) {
				olapStr = olapStr.replaceAll("\r", "").replaceAll("\n", "");
				org.json.JSONObject o = new org.json.JSONObject(olapStr.replace("<d_q>","\""));
				//解析当前打开olap中使用的datasource
				boolean findCurrentDatalogicDs = false;
				org.json.JSONArray a = o.getJSONArray("dataSets");
				for(int i = 0; i < a.length(); i++) {
					org.json.JSONObject o1 = a.getJSONObject(i);
					String dsName = o1.getString("dataSource");
					if(dsName != null && dsName.length() > 0){
						if(DataSphereServlet.DQLDATASOURCES.indexOf(dsName)>=0){
							dataSource = dsName;
							findCurrentDatalogicDs = true;
							break;
						}
					}
				}
				if(!findCurrentDatalogicDs) dataSource = "";
			}
			
 			
			String json = "";
			if (dataSource.length()>0 && DataSphereServlet.DQLDATASOURCES.indexOf(dataSource)>=0) { //"source".equalsIgnoreCase(view)
				try {
					json = ConfigUtil.getMetaDataJson(dataSource);
				} catch (Throwable e) {
					//e.printStackTrace();
					Logger.warn("", e);
					//Logger.debug("--------" + e.getMessage());
					error = e.getMessage();
				}
			}

			String guideDir = ReportConfig.raqsoftDir;
			if (!guideDir.startsWith("/")) guideDir = "/" + guideDir;
			guideDir = guideDir+"/guide/";
			
			out.println("<script type='text/javascript'>");
			//out.println("var guideConf = {};");
			//out.println("var lmdStr = \"" + json.replaceAll("\"", "<d__q>") + "\"");
			out.println("lmdStr = \"" + json.replaceAll("\"", "<d__q>") + "\"");
			//out.println("var olapStr = \""+guideEncode(olapStr)+"\";");
			out.println("olapStr = \""+guideEncode(olapStr)+"\";");
			out.println("guideConf.guideDir = '"+guideDir+"';");
			out.println("guideConf.view = '"+view+"';");
			out.println("guideConf.error = '"+error.replaceAll("'", " ")+"';");
			out.println("guideConf.dataSource = '"+dataSource+"';");
			out.println("guideConf.dataSources = '"+DataSphereServlet.DATASOURCES+"';");
			out.println("guideConf.dqlDataSources = '"+DataSphereServlet.DQLDATASOURCES+"';");
			out.println("guideConf.dataId = '"+dataId+"';");
			out.println("guideConf.ql = \""+guideEncode(ql)+"\";");
			out.println("guideConf.dfxFile = \""+guideEncode(dfxFile)+"\";");
			out.println("guideConf.dfxScript = \""+guideEncode(dfxScript)+"\";");
			out.println("guideConf.dfxParams = \""+guideEncode(dfxParams)+"\";");
			out.println("guideConf.inputFiles = \""+guideEncode(inputFiles)+"\";");
			out.println("guideConf.tableNames = ["+tableNames+"];");
			out.println("guideConf.queryPage = '"+queryPage+"';");
			out.println("guideConf.simplePage = '"+simplePage+"';");
			out.println("guideConf.olapPage = '"+olapPage+"';");
			out.println("guideConf.reportPage = '"+reportPage+"';");
			out.println("guideConf.maxDataSize = '"+maxDataSize+"';");
			out.println("guideConf.maxReportSize = '"+maxReportSize+"';");
			//out.println("guideConf.olapFolderOnServer = '"+DataSphereServlet.olapFolderOnServer+"';");
			//out.println("guideConf.dataFolderOnServer = '"+DataSphereServlet.dataFolderOnServer+"';");
			//out.println("guideConf.dfxFolderOnServer = '"+DataSphereServlet.dfxFolderOnServer+"';");
			//out.println("guideConf.inputFileFolderOnServer = '"+DataSphereServlet.inputFileFolderOnServer+"';");
			//out.println("guideConf.rpxFolderOnServer = '"+DataSphereServlet.rpxFolderOnServer+"';");
			out.println("guideConf.fixedTable = '"+fixedTable+"';");
			out.println("guideConf.outerCondition = \""+outerCondition.replaceAll("\"", "<d_q>")+"\";");
			//out.println("guideConf.dimDataOnServer = '"+DataSphereServlet.dimDataOnServer+"';");
			out.println("guideConf.dataFileType = '"+dataFileType+"';");
			session.setAttribute("fileHome", fileHome);
			out.println("guideConf.style = "+ReportStyle.getStyleJson(DataSphereServlet.getFilePath(styleRpx))+";");
			showSubTable = "no";
			out.println("guideConf.showSubTable = '"+showSubTable+"';");
			out.println("guideConf.resultRpxPrefixOnServer = '"+resultRpxPrefixOnServer+"';");
			out.println("guideConf.canEditDataSet = '"+canEditDataSet+"';");
			out.println("guideConf.canEditReport = '"+canEditReport+"';");
			out.println("guideConf.showHistoryRpx = '"+showHistoryRpx+"';");

			StringBuffer sb = new StringBuffer();
			sb.append("{");
			boolean first = true;
			for (int i=1; i<250; i++) {
				String ni = "guide.js"+i;
				String vi = GuideMessage.get(request).getMessage(ni);
				if (vi.equals(ni)) continue;
				if (!first) sb.append(",");
				sb.append("js"+i+":'"+vi+"'");
				first = false;
				//System.out.println(vi);
			}
			sb.append("};");
			//out.println("var resources = {};");
			out.println("resources.guide = "+sb.toString());
			//out.println("var props = {};");
			out.println("props = "+getSqlLang(sqlType,request.getSession().getServletContext()));
			//out.println(QueryTag.getFiles(null , olapFolderOnServer, dfxFolderOnServer, rpxFolderOnServer, inputFileFolderOnServer,null));
			
			out.println("</script>");
			//pageContext.include(guideDir+"jsp/template.jsp?jsv=new&guideDir="+URLEncoder.encode(guideDir, "UTF-8")+"&title="+URLEncoder.encode(title, "UTF-8"),true);
		}
		catch ( Throwable e ) {
			Logger.error( new String("ERROR:"), e );
		}
	}
	
	public String guideEncode(String s) {
		return s.replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("\"", "<d_q>");
	}
	
	protected String getSqlLang(String sqlType, ServletContext application) {
		if(sqlType == null) sqlType = "default";
		StringBuffer sb = new StringBuffer();
		String fileName = "/raqsoft/guide/asset/"+sqlType+".properties";
		InputStream is = application.getResourceAsStream(fileName);
		if(is == null) {
			Logger.debug("没有找到文件："+fileName);
		}
		ResourceBundle resource = null;
		try {
			if(is != null) {
				resource = new PropertyResourceBundle(is);
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String _isnull = "_x_ is null";
		String _isnotnull = "_x_ is not null";
		String _like_prefix = "_x_ like '%";
		String _like_suffix = "%'";
		String _notlike_prefix = "_x_ not like '%";
		String _notlike_suffix = "%'";
		String _startfrom_prefix = "_x_ not like '";
		String _startfrom_suffix = "%'";
		String _endat_prefix = "_x_ like '%";
		String _endat_suffix = "'";
		String _date_prefix = "_x_=date('";
		String _date_suffix = "')";
		String _timestamp_prefix = "";
		String _timestamp_suffix = "";
		if( resource == null ) {
			Logger.debug("没有找到文件："+fileName);
		}else{
			try{
				_isnull = resource.getString("_isnull");
			}catch(java.util.MissingResourceException e){}
			try{
				_isnotnull = resource.getString("_isnotnull");
			}catch(java.util.MissingResourceException e){}
			try{
				_like_prefix = resource.getString("_like").split("_v_")[0];
				_like_suffix = resource.getString("_like").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
			try{
				_notlike_prefix = resource.getString("_notlike").split("_v_")[0];
				_notlike_suffix = resource.getString("_notlike").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
			try{
				_startfrom_prefix = resource.getString("_startfrom").split("_v_")[0];
				_startfrom_suffix = resource.getString("_startfrom").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
			try{
				_endat_prefix = resource.getString("_endat").split("_v_")[0];
				_endat_suffix = resource.getString("_endat").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
			try{
				_date_prefix = resource.getString("_date").split("_v_")[0];
				_date_suffix = resource.getString("_date").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
			try{
				_timestamp_prefix = resource.getString("_timestamp").split("_v_")[0];
				_timestamp_suffix = resource.getString("_timestamp").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
		}
		sb.append("{\n");
		sb.append("\t_isnull:\"").append(_isnull).append("\",\n");
		sb.append("\t_isnotnull:\"").append(_isnotnull).append("\",\n");
		sb.append("\t_like_prefix:\"").append(_like_prefix).append("\",\n");
		sb.append("\t_like_suffix:\"").append(_like_suffix).append("\",\n");
		sb.append("\t_notlike_prefix:\"").append(_notlike_prefix).append("\",\n");
		sb.append("\t_notlike_suffix:\"").append(_notlike_suffix).append("\",\n");
		sb.append("\t_startfrom_prefix:\"").append(_startfrom_prefix).append("\",\n");
		sb.append("\t_startfrom_suffix:\"").append(_startfrom_suffix).append("\",\n");
		sb.append("\t_endat_prefix:\"").append(_endat_prefix).append("\",\n");
		sb.append("\t_endat_suffix:\"").append(_endat_suffix).append("\",\n");
		sb.append("\t_date_prefix:\"").append(_date_prefix).append("\",\n");
		sb.append("\t_date_suffix:\"").append(_date_suffix).append("\",\n");
		sb.append("\t_timestamp_prefix:\"").append(_timestamp_prefix).append("\",\n");
		sb.append("\t_timestamp_suffix:\"").append(_timestamp_suffix).append("\",\n");
		sb.append("}\n");
		return sb.toString();
	}
}
