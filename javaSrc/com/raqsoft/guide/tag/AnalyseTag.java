package com.raqsoft.guide.tag;

import java.io.File;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

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
import com.raqsoft.report.usermodel.IReport;
import com.raqsoft.report.usermodel.Macro;
import com.raqsoft.report.usermodel.MacroMetaData;
import com.raqsoft.report.usermodel.ParamMetaData;
import com.raqsoft.report.util.ReportUtils;
import com.raqsoft.report.view.ParamsPool;
import com.raqsoft.report.view.ReportConfig;
import com.raqsoft.report.view.ServerMsg;
import com.raqsoft.util.Variant;

/**
 * 
	view="simple/olap"
	olap="<%=grpx %>"
	dataSource="<%=dataSource %>"
  	fixedTable="<%=fixedTable %>"
	ql="<%=ql %>"
  	dfxFile="<%=dfxFile %>"
  	dfxScript="<%=dfxScript %>"
  	dfxParams="<%=dfxParams %>"
  	inputFiles="<%=inputFiles %>"
	
	queryPage="/raqsoft/guide/jsp/query.jsp"
	simplePage="/raqsoft/guide/jsp/analyse.jsp"
	olapPage="/raqsoft/guide/jsp/analyse.jsp"
	reportPage="/raqsoft/guide/jsp/showReport.jsp"
  	maxDataSize="10000"
  	maxReportSize="50000"
  	olapFolderOnServer="/WEB-INF/files/olap/"
  	dataFolderOnServer="/WEB-INF/files/data/"
  	dfxFolderOnServer="/WEB-INF/files/dfx/"
  	inputFileFolderOnServer="/WEB-INF/files/inputFile/"
  	rpxFolderOnServer="/WEB-INF/files/rpx/"
  	
  	outerCondition="<%=outerCondition %>"
  	dimDataOnServer="/WEB-INF/files/data/temp/dimData.json"
	dataFileType="<%=dataFileType %>"
	showSubTable="<%=showSubTable %>"
	
	fileHome=""
	styleRpx="/WEB-INF/files/rpx/style.rpx"
	
	resultRpxPrefixOnServer = ""
	canEditDataSet="yes"
	canEditReport="yes"
	showHistoryRpx="yes"

 *
 */
public class AnalyseTag extends Tag {
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
	private String dimDataName;
	
	
	public String getDimDataName() {
		return dimDataName;
	}

	public void setDimDataName(String dimDataName) {
		this.dimDataName = dimDataName;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
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
	private String fileDataFolderOnServer;
	
	private String dfxFolderOnServer;
	private String inputFileFolderOnServer;
	private String rpxFolderOnServer;
	private String outerCondition;
	private String dataFileType;
	private String showSubTable;


	public AnalyseTag() {
		view = null;
		olap = null;
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
		fileDataFolderOnServer = null;
		dfxFolderOnServer = null;
		inputFileFolderOnServer = null;
		rpxFolderOnServer = null;
		outerCondition = null;
		dimDataName = null;
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
		fileDataFolderOnServer = null;
		dfxFolderOnServer = null;
		inputFileFolderOnServer = null;
		rpxFolderOnServer = null;
		outerCondition = null;
		dimDataName = null;
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
	  	olapFolderOnServer = getDefaultParam( olapFolderOnServer, DataSphereServlet.olapFolderOnServer );
	  	dataFolderOnServer = getDefaultParam( dataFolderOnServer, DataSphereServlet.dataFolderOnServer );
	  	dfxFolderOnServer = getDefaultParam( dfxFolderOnServer, DataSphereServlet.dfxFolderOnServer );
	  	inputFileFolderOnServer = getDefaultParam( inputFileFolderOnServer, DataSphereServlet.inputFileFolderOnServer );
	  	rpxFolderOnServer = getDefaultParam( rpxFolderOnServer, DataSphereServlet.rpxFolderOnServer );
	  	fileDataFolderOnServer = getDefaultParam( fileDataFolderOnServer, DataSphereServlet.fileDataFolderOnServer );
	  	fixedTable = getDefaultParam( fixedTable, "" );
	  	outerCondition = getDefaultParam( outerCondition, "" );
	  	dimDataName = getDefaultParam( dimDataName, dimDataName );
	  	dataFileType = getDefaultParam( dataFileType, "text" );
	  	showSubTable = getDefaultParam( showSubTable, "yes" );
		styleRpx = getDefaultParam( styleRpx, "WEB-INF/files/style.rpx" );
		fileHome = getDefaultParam( fileHome, "" );
		resultRpxPrefixOnServer = getDefaultParam( resultRpxPrefixOnServer, "WEB-INF/files/resultRpx/" );
		canEditDataSet=getDefaultParam( canEditDataSet, "yes" );
		canEditReport=getDefaultParam( canEditReport, "yes" );
		showHistoryRpx=getDefaultParam( showHistoryRpx, "no" );
	}

	private String getDefaultParam( String param, String def ) {
		if ( param == null ) {
			return def;
		}
		return param;
	}

//	private String view;
//	private String grpx;
//	private String dataSource;
//	private String ql;
//	private String dfxFile;
//	private String dfxScript;
//	private String dfxParams;
//	private String inputFiles; 
//	private String reportPage;
	public int doStartTag() throws JspTagException {
		try {
			initParameters();
			HttpSession session = pageContext.getSession();
			Logger.debug("????tag????????????????1??session??????????");
			Logger.debug(session != null);
			HttpServletRequest request = ( HttpServletRequest ) pageContext.getRequest();
			request.setCharacterEncoding( "UTF-8" );
			boolean renewSession = true; 
			Logger.debug("??????session????????????????????");
			Logger.debug(renewSession);
			if(!renewSession){
				session = request.getSession( renewSession );
				Logger.debug("????tag????????????????2??session??????????");
				Logger.debug(session != null);
			}
			Logger.debug("tag????????sessionId??");
			Logger.debug(session.getId());
			//ReportServlet.reloadConfig( pageContext.getServletContext() );
			HttpServletResponse response = ( HttpServletResponse ) pageContext.getResponse();
			response.setContentType("text/html;charset=utf8");  
			JspWriter out = pageContext.getOut();
			//PrintWriter out = response.getWriter();
			
			String error = "";
			
			//TODO ????????????????????????????????????????????????????????????????????????????????????
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
			DfxQuery dq = null;
			String currTable = "";
			String tableNames = "";

			ParamList pl = new ParamList();
			//TODO ????dfxParams
			
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
			if (olap.length()>0) { // grpx??
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
			try{
			if(olapStr.length() > 0) {
				olapStr = olapStr.replaceAll("\r", "").replaceAll("\n", "");
				org.json.JSONObject o = new org.json.JSONObject(olapStr.replace("<d_q>","\""));
				//????????????olap????????datasource
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
			}catch(Exception e){
				e.printStackTrace();
				Logger.debug(olapStr);
				throw e;
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
			out.println("guideConf.fileDataFolderOnServer = '"+fileDataFolderOnServer+"';");
			
			out.println("guideConf.dfxFolderOnServer = '"+dfxFolderOnServer+"';");
			out.println("guideConf.inputFileFolderOnServer = '"+inputFileFolderOnServer+"';");
			out.println("guideConf.rpxFolderOnServer = '"+rpxFolderOnServer+"';");
			out.println("guideConf.fixedTable = '"+fixedTable+"';");
			out.println("guideConf.outerCondition = \""+outerCondition.replaceAll("\"", "<d_q>")+"\";");
			out.println("guideConf.dimDataName = '"+dimDataName+"';");
			out.println("guideConf.dataFileType = '"+dataFileType+"';");
			out.println("guideConf.cursorSize = '"+ 10000 +"';");
//			out.println("guideConf.fileHome = '"+fileHome+"';");
			session.setAttribute("fileHome", fileHome);
			out.println("guideConf.style = "+ReportStyle.getStyleJson(DataSphereServlet.getFilePath(styleRpx))+";");
			showSubTable = "no";
			out.println("guideConf.showSubTable = '"+showSubTable+"';");//TODO
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
			out.println("var resources = {};");
			out.println("resources.guide = "+sb.toString());
			out.println("var props = {};");
			out.println("props = "+getSqlLang(sqlType,request.getSession().getServletContext()));
//			out.println(QueryTag.getFiles(null , olapFolderOnServer, dfxFolderOnServer, rpxFolderOnServer, inputFileFolderOnServer,null));
			out.println(QueryTag.getFiles());
			
			out.println("</script>");
			String title = "Raqsoft Analyse";
			pageContext.include(guideDir+"jsp/template.jsp?jsv=new&guideDir="+URLEncoder.encode(guideDir, "UTF-8")+"&title="+URLEncoder.encode(title, "UTF-8"),true);
			
		}
		catch ( Throwable e ) {
			Logger.error( new String("ERROR:"), e );
		}
		finally {
			if (true) release();
			return EVAL_PAGE;
		}
	}


	private String getCurrURL( HttpServletRequest request ) {
		String url = request.getServletPath();
		String queryString = "";
		Enumeration em = request.getParameterNames();
		while ( em.hasMoreElements() ) {
			String param = ( String ) em.nextElement();
			if ( param.equals( "reportParamsId" )
				 || param.indexOf( "_currPage" ) >= 0
				 || param.indexOf( "_cachedId" ) >= 0
				 || param.indexOf( "_sessionId" ) >= 0
				 || param.indexOf( "t_i_m_e" ) >= 0
				 || param.indexOf( "_total_count_" ) >= 0
				 ) {
				continue;
			}
			String[] values = request.getParameterValues( param );
			for ( int i = 0; i < values.length; i++ ) {
				queryString += param + "=" + URLEncoder.encode( values[i] ) + "&";
			}
		}
		if ( queryString.length() > 0 ) {
			return url + "?" + queryString.substring( 0, queryString.length() - 1 );
		}
		return url;
	}
	
	public static void putParams(String reportParamsId, ParamList pl,
			 HttpServletRequest request) throws Exception {
		Hashtable params = null;
		if (reportParamsId != null) {
			params = ParamsPool.get(reportParamsId);
			if (params == null) {
				throw new Exception(ServerMsg.getMessage(request, "calc.paramTimeout")); //????????????????????????????????????????????????????????!
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
	
	public static String guideDecode(String s) {
		return s.replaceAll("<d_q>", "\"");
	}
	
	public static String guideEncode(String s) {
		return s.replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("\"", "<d_q>");
	}
	
	
	public static String getDisc(String file) {
		try {
			IReport temp = ReportUtils.read(file);
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
					com.raqsoft.report.usermodel.Param m = pmd.getParam(i);
					if (disc == null) disc = m.getParamName();
					else disc += ";" + m.getParamName();
				}
			} else disc = temp.getCell(1, 1).getNotes();
			if (disc == null) disc = "";
			disc.replaceAll(",", "");
			return disc;
		} catch (Exception e) {
			Logger.warn("read rpx error");
			return null;
		}
		
	}


}
