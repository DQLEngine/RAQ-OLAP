package com.raqsoft.guide.tag;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.raqsoft.common.DBSessionFactory;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.Param;
import com.raqsoft.dm.ParamList;
import com.raqsoft.guide.resource.GuideMessage;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.dl.ConfigUtil;
import com.raqsoft.guide.web.dl.DfxQuery;
import com.raqsoft.guide.web.dl.ReportStyle;
import com.raqsoft.guide.web.dl.SaveUtil;
import com.raqsoft.report.view.ParamsPool;
import com.raqsoft.report.view.ReportConfig;
import com.raqsoft.report.view.ServerMsg;
import com.raqsoft.util.Variant;

public class LayoutDashboardTag2 extends TagSupport{

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
	private String dimDataName;
	private String dbd;
	
	//dbd layout
	private String divide;//方块间隔px
	private String defaultDivWidth;//拖拽拉伸分几区，新增块占满
	private String defaultDivHeight;//最小分区行高px
	private String defaultLiHeight;//新增块占几行
	
	public String getDimDataName() {
		return dimDataName;
	}

	public void setDimDataName(String dimDataName) {
		this.dimDataName = dimDataName;
	}

	public String getDbd() {
		return dbd;
	}

	public void setDbd(String dbd) {
		this.dbd = dbd;
	}

	public String getCanEditDataSet() {
		return canEditDataSet;
	}

	public void setCanEditDataSet(String canEditDataSet) {
		this.canEditDataSet = canEditDataSet;
	}

	public String getCanEditReport() {
		return canEditReport;
	}

	public void setCanEditReport(String canEditReport) {
		this.canEditReport = canEditReport;
	}

	public String getShowHistoryRpx() {
		return showHistoryRpx;
	}

	public void setShowHistoryRpx(String showHistoryRpx) {
		this.showHistoryRpx = showHistoryRpx;
	}


	private String canEditReport;
	private String showHistoryRpx;

	public String getResultRpxPrefixOnServer() {
		return resultRpxPrefixOnServer;
	}

	public void setResultRpxPrefixOnServer(String resultRpxPrefixOnServer) {
		this.resultRpxPrefixOnServer = resultRpxPrefixOnServer;
	}

	public String getFileHome() {
		return fileHome;
	}

	public void setFileHome(String fileHome) {
		this.fileHome = fileHome;
	}

	public String getStyleRpx() {
		return styleRpx;
	}

	public void setStyleRpx(String styleRpx) {
		this.styleRpx = styleRpx;
	}


	private String styleRpx;
	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getOlap() {
		return olap;
	}

	public void setOlap(String olap) {
		this.olap = olap;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getFixedTable() {
		return fixedTable;
	}

	public void setFixedTable(String fixedTable) {
		this.fixedTable = fixedTable;
	}

	public String getQl() {
		return ql;
	}

	public void setQl(String ql) {
		this.ql = ql;
	}

	public String getDfxFile() {
		return dfxFile;
	}

	public void setDfxFile(String dfxFile) {
		this.dfxFile = dfxFile;
	}

	public String getDfxScript() {
		return dfxScript;
	}

	public void setDfxScript(String dfxScript) {
		this.dfxScript = dfxScript;
	}

	public String getDfxParams() {
		return dfxParams;
	}

	public void setDfxParams(String dfxParams) {
		this.dfxParams = dfxParams;
	}

	public String getInputFiles() {
		return inputFiles;
	}

	public void setInputFiles(String inputFiles) {
		this.inputFiles = inputFiles;
	}

	public String getQueryPage() {
		return queryPage;
	}

	public void setQueryPage(String queryPage) {
		this.queryPage = queryPage;
	}

	public String getSimplePage() {
		return simplePage;
	}

	public void setSimplePage(String simplePage) {
		this.simplePage = simplePage;
	}

	public String getOlapPage() {
		return olapPage;
	}

	public void setOlapPage(String olapPage) {
		this.olapPage = olapPage;
	}

	public String getReportPage() {
		return reportPage;
	}

	public void setReportPage(String reportPage) {
		this.reportPage = reportPage;
	}

	public String getMaxDataSize() {
		return maxDataSize;
	}

	public void setMaxDataSize(String maxDataSize) {
		this.maxDataSize = maxDataSize;
	}

	public String getMaxReportSize() {
		return maxReportSize;
	}

	public void setMaxReportSize(String maxReportSize) {
		this.maxReportSize = maxReportSize;
	}

	public String getOlapFolderOnServer() {
		return olapFolderOnServer;
	}

	public void setOlapFolderOnServer(String olapFolderOnServer) {
		this.olapFolderOnServer = olapFolderOnServer;
	}

	public String getDataFolderOnServer() {
		return dataFolderOnServer;
	}

	public void setDataFolderOnServer(String dataFolderOnServer) {
		this.dataFolderOnServer = dataFolderOnServer;
	}

	public String getDfxFolderOnServer() {
		return dfxFolderOnServer;
	}

	public void setDfxFolderOnServer(String dfxFolderOnServer) {
		this.dfxFolderOnServer = dfxFolderOnServer;
	}

	public String getInputFileFolderOnServer() {
		return inputFileFolderOnServer;
	}

	public void setInputFileFolderOnServer(String inputFileFolderOnServer) {
		this.inputFileFolderOnServer = inputFileFolderOnServer;
	}

	public String getRpxFolderOnServer() {
		return rpxFolderOnServer;
	}

	public void setRpxFolderOnServer(String rpxFolderOnServer) {
		this.rpxFolderOnServer = rpxFolderOnServer;
	}

	public String getOuterCondition() {
		return outerCondition;
	}

	public void setOuterCondition(String outerCondition) {
		this.outerCondition = outerCondition;
	}

	public String getDataFileType() {
		return dataFileType;
	}

	public void setDataFileType(String dataFileType) {
		this.dataFileType = dataFileType;
	}

	public String getShowSubTable() {
		return showSubTable;
	}

	public void setShowSubTable(String showSubTable) {
		this.showSubTable = showSubTable;
	}


	private String reportPage;
	private String maxDataSize;
	private String maxReportSize;
	private String olapFolderOnServer;
	private String dataFolderOnServer;
	private String dfxFolderOnServer;
	private String inputFileFolderOnServer;
	private String rpxFolderOnServer;
	private String outerCondition;
	private String dataFileType;
	private String showSubTable;


	public LayoutDashboardTag2() {
		view = null;
		olap = null;
		dbd = null;
		dataSource = null;
		fixedTable = null;
		ql = null;
		dfxFile = null;
		dfxScript = null;
		dfxParams = null;
		inputFiles = null;
		queryPage = null;
		simplePage = null;
		olapPage = null;
		reportPage = null;
		maxDataSize = null;
		maxReportSize = null;
		olapFolderOnServer = null;
		dataFolderOnServer = null;
		dfxFolderOnServer = null;
		inputFileFolderOnServer = null;
		rpxFolderOnServer = null;
		outerCondition = null;
		dataFileType = null;
		showSubTable = null;
		styleRpx = null;
		fileHome = null;
		resultRpxPrefixOnServer = null;
		canEditDataSet=null;
		canEditReport=null;
		showHistoryRpx=null;
	}

	public void release() {
		super.release();
		view = null;
		olap = null;
		dbd = null;
		dataSource = null;
		fixedTable = null;
		ql = null;
		dfxFile = null;
		dfxScript = null;
		dfxParams = null;
		inputFiles = null;
		queryPage = null;
		simplePage = null;
		olapPage = null;
		reportPage = null;
		maxDataSize = null;
		maxReportSize = null;
		olapFolderOnServer = null;
		dataFolderOnServer = null;
		dfxFolderOnServer = null;
		inputFileFolderOnServer = null;
		rpxFolderOnServer = null;
		outerCondition = null;
		dataFileType = null;
		showSubTable = null;
		styleRpx = null;
		fileHome = null;
		resultRpxPrefixOnServer = null;
		canEditDataSet=null;
		canEditReport=null;
		showHistoryRpx=null;
	}

	private void initParameters() {
		view = getDefaultParam( view, "olap" );
		olap = getDefaultParam( olap, "" );
		dbd = getDefaultParam( dbd, "" );
		dataSource = getDefaultParam( dataSource, "" );
		ql = getDefaultParam( ql, "" );
		dfxFile = getDefaultParam( dfxFile, "" );
		dfxScript = getDefaultParam( dfxScript, "" );
		dfxParams = getDefaultParam( dfxParams, "" );
		inputFiles = getDefaultParam( inputFiles, "" );
		String guideDir = ReportConfig.raqsoftDir;
		if (guideDir.startsWith("/")) guideDir = guideDir.substring(1);
		queryPage = getDefaultParam( queryPage, "" );
		simplePage = getDefaultParam( simplePage, guideDir+"/guide/jsp/analyse.jsp" );
		olapPage = getDefaultParam( reportPage, guideDir+"/guide/jsp/analyse.jsp" );
		reportPage = getDefaultParam( reportPage, guideDir+"/guide/jsp/showReport.jsp" );
	  	maxDataSize = getDefaultParam( maxDataSize, "10000" );
	  	maxReportSize = getDefaultParam( maxReportSize, "50000" );
	  	olapFolderOnServer = getDefaultParam( olapFolderOnServer, "WEB-INF/files/olap/" );
	  	dataFolderOnServer = getDefaultParam( dataFolderOnServer, "WEB-INF/files/data/" );
	  	dfxFolderOnServer = getDefaultParam( dfxFolderOnServer, "WEB-INF/files/dfx/" );
	  	inputFileFolderOnServer = getDefaultParam( inputFileFolderOnServer, "WEB-INF/files/inputFile/" );
	  	rpxFolderOnServer = getDefaultParam( rpxFolderOnServer, "WEB-INF/files/rpx/" );
	  	fixedTable = getDefaultParam( fixedTable, "" );
	  	outerCondition = getDefaultParam( outerCondition, "" );
	  	dimDataName = getDefaultParam( dimDataName, DataSphereServlet.dimDataName );
	  	dataFileType = getDefaultParam( dataFileType, "text" );
	  	showSubTable = getDefaultParam( showSubTable, "yes" );
		styleRpx = getDefaultParam( styleRpx, "WEB-INF/files/style.rpx" );
		fileHome = getDefaultParam( fileHome, "" );
		resultRpxPrefixOnServer = getDefaultParam( resultRpxPrefixOnServer, "WEB-INF/files/resultRpx/" );
		canEditDataSet=getDefaultParam( canEditDataSet, "yes" );
		canEditReport=getDefaultParam( canEditReport, "yes" );
		showHistoryRpx=getDefaultParam( showHistoryRpx, "no" );
		divide="10";
		defaultDivWidth="9";
		defaultDivHeight="150";
		defaultLiHeight="2";
	}

	private String getDefaultParam( String param, String def ) {
		if ( param == null ) {
			return def;
		}
		return param;
	}
	public int doStartTag(){
		try {
			initParameters();
			HttpServletRequest request = ( HttpServletRequest ) pageContext.getRequest();
			request.setCharacterEncoding( "UTF-8" );
			HttpServletResponse response = ( HttpServletResponse ) pageContext.getResponse();
			response.setContentType("text/html;charset=utf8");  
			JspWriter out = pageContext.getOut();
			HttpSession session = request.getSession( true );
			String error = "";
			

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
			DfxQuery dq = null;
			String currTable = "";
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
			
			putParams(null, pl, request);
			
			String olapStr = "";
			if(olap == null || olap.equals("")) olap = dbd;
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
			
			olapStr = olapStr.replace("\\r\\n", "");//有时保存时多出换行
			olapStr = olapStr.replaceAll("\\r", "").replaceAll("\\n", "");
			
//			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//			Matcher m = p.matcher(olapStr);
//			olapStr = m.replaceAll("");
			if(olapStr.length() > 0 && olapStr.startsWith("{")) {
				org.json.JSONObject o = new org.json.JSONObject(olapStr.replace("<d_q>","\""));
				//解析当前打开olap中使用的datasource
				boolean findCurrentDatalogicDs = false;
				org.json.JSONArray a = o.getJSONArray("dataSets");
				for(int i = 0; i < a.length(); i++) {
					org.json.JSONObject o1 = a.getJSONObject(i);
					String dsName = o1.getString("dataSource");
					String dataFileType1 = o1.getString("dataId");
					if(dataFileType1.indexOf("txt") >= 0) {
						dataFileType = "text";
					}else if(dataFileType1.indexOf("bin") >= 0) { 
						dataFileType = "binay";
					}
					if(dsName != null && dsName.length() > 0){
						if(DataSphereServlet.DQLDATASOURCES.indexOf(dsName)>=0){
							dataSource = dsName;
							findCurrentDatalogicDs = true;
							break;
						}
					}
				}
				if(!findCurrentDatalogicDs) dataSource = "";
			}else{
				Logger.debug("parsing olapStr: not start with '{' character!");
				Logger.debug("olapStr is: "+olapStr);
				File f = new File(olap);
				if (!f.exists()) {
					Logger.debug("olap param is: "+olap);
					Logger.debug("file doesn't exists");
				}
			}
			String json = "";
			if (dataSource.length()>0 && DataSphereServlet.DQLDATASOURCES.indexOf(dataSource)>=0) { //"source".equalsIgnoreCase(view)
				try {
					json = ConfigUtil.getMetaDataJson(dataSource);
				} catch (Throwable e) {
					e.printStackTrace();
					Logger.warn("", e);
					//Logger.debug("--------" + e.getMessage());
					error = e.getMessage();
				}
			}

			String guideDir = ReportConfig.raqsoftDir;
			if (!guideDir.startsWith("/")) guideDir = "/" + guideDir;
			guideDir = guideDir+"/guide/";
			
			out.println("<script type='text/javascript'>");
			out.println("var guideConf = {};");
			out.println("var lmdStr = \"" + json.replaceAll("\"", "<d__q>") + "\"");
			out.println("var olapStr = \""+guideEncode(olapStr)+"\";");
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
			out.println("guideConf.olapFolderOnServer = '"+olapFolderOnServer+"';");
			out.println("guideConf.dataFolderOnServer = '"+dataFolderOnServer+"';");
			out.println("guideConf.dfxFolderOnServer = '"+dfxFolderOnServer+"';");
			out.println("guideConf.inputFileFolderOnServer = '"+inputFileFolderOnServer+"';");
			out.println("guideConf.dbdImageFileFolderOnServer = '"+DataSphereServlet.dbdImageFileFolderOnServer+"';");
			out.println("guideConf.rpxFolderOnServer = '"+rpxFolderOnServer+"';");
			out.println("guideConf.fixedTable = '"+fixedTable+"';");
			out.println("guideConf.outerCondition = \""+outerCondition.replaceAll("\"", "<d_q>")+"\";");
			out.println("guideConf.dimDataName = '"+dimDataName+"';");
			out.println("guideConf.dataFileType = '"+dataFileType+"';");
//			out.println("guideConf.fileHome = '"+fileHome+"';");
			session.setAttribute("fileHome", fileHome);
			out.println("guideConf.style = "+ReportStyle.getStyleJson(DataSphereServlet.getFilePath(styleRpx))+";");
			showSubTable = "no";
			out.println("guideConf.showSubTable = '"+showSubTable+"';");
			out.println("guideConf.resultRpxPrefixOnServer = '"+resultRpxPrefixOnServer+"';");
			out.println("guideConf.canEditDataSet = '"+canEditDataSet+"';");
			out.println("guideConf.canEditReport = '"+canEditReport+"';");
			out.println("guideConf.showHistoryRpx = '"+showHistoryRpx+"';");
			try {
				Properties layoutProperties = new Properties();
				InputStream in = request.getSession().getServletContext().getResourceAsStream("/WEB-INF/dbdSettings.properties");
				layoutProperties.load( in );
				
				divide = layoutProperties.getProperty("divide");
				defaultDivWidth = layoutProperties.getProperty("defaultDivWidth");;
				defaultDivHeight = layoutProperties.getProperty("defaultDivHeight");
				defaultLiHeight = layoutProperties.getProperty("defaultLiHeight");
				int divide = Integer.parseInt(this.divide);
				int defaultDivWidth = Integer.parseInt(this.defaultDivWidth);
				int defaultDivHeight = Integer.parseInt(this.defaultDivHeight);
				if(divide > 5) this.divide = "5";
				if(defaultDivWidth > 9) this.defaultDivWidth = "9";
				if(defaultDivHeight < 100) this.defaultDivHeight = "100";
				Integer.parseInt(defaultLiHeight);
				out.println("var divide = "+divide+";");
				out.println("var defaultDivWidth = "+defaultDivWidth+";");
				out.println("var defaultDivHeight = "+defaultDivHeight+";");
				out.println("var defaultLiHeight = "+defaultLiHeight+";");
			}catch(Exception e) {
				out.println("var divide = "+divide+";");
				out.println("var defaultDivWidth = "+defaultDivWidth+";");
				out.println("var defaultDivHeight = "+defaultDivHeight+";");
				out.println("var defaultLiHeight = "+defaultLiHeight+";");
			}
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
			out.println("var resources = {};");
			out.println("resources.guide = "+sb.toString());

			//out.println(QueryTag.getFiles(null , olapFolderOnServer, dfxFolderOnServer, rpxFolderOnServer, inputFileFolderOnServer,null));
			
			out.println("</script>");
			//pageContext.include(guideDir+"jsp/dashboardInclude.jsp?jsv=new&guideDir="+URLEncoder.encode(guideDir, "UTF-8")+"&title="+URLEncoder.encode(title, "UTF-8"),true);
			
		}
		catch ( Throwable e ) {
			Logger.error( new String("ERROR:"), e );
		}
		finally {
			//if( ReportServlet.releaseTag ) release();
			if (true) release();
			return EVAL_PAGE;
		}
	}

	public static String guideDecode(String s) {
		return s.replaceAll("<d_q>", "\"");
	}
	
	public static String guideEncode(String s) {
		return s.replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("\"", "<d_q>");
	}
	
	
	public static void putParams(String reportParamsId, ParamList pl,
			 HttpServletRequest request) throws Exception {
		Hashtable params = null;
		if (reportParamsId != null) {
			params = ParamsPool.get(reportParamsId);
			if (params == null) {
				throw new Exception(ServerMsg.getMessage(request, "calc.paramTimeout")); //缓存的报表参数或报表宏因超时已被清除，请重新输入参数及宏!
			}
		}
		//Logger.debug("reportParamsId : " + reportParamsId);
		//Logger.debug("params : " + params);
		if (params != null) {
			Enumeration e = params.keys();
			while (e.hasMoreElements()) {
				String key = e.nextElement().toString();
				Object value = params.get(key);
				if (value != null && value instanceof String) value = Variant.parse(value.toString());
				//Logger.debug("param : " + key + " : " + params.get(key));
				if (pl.get(key) != null) pl.get(key).setValue(value);
				else {
					pl.add(new Param(key, Param.VAR, value));
				}
			}
		} 

		Enumeration paramNames = request.getParameterNames();
		if(paramNames!=null){
			while(paramNames.hasMoreElements()){
				String paramName = (String) paramNames.nextElement();
				Object paramValue=Variant.parse(request.getParameter(paramName));
				if(paramValue!=null){
					if (pl.get(paramName) != null) pl.get(paramName).setValue(paramValue);
					else {
						pl.add(new Param(paramName, Param.VAR, paramValue));
					}
				}
			}
		}
	}
}
