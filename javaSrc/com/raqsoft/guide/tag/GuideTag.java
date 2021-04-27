package com.raqsoft.guide.tag;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.raqsoft.common.DBSessionFactory;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.Param;
import com.raqsoft.dm.ParamList;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.dl.ConfigUtil;
import com.raqsoft.guide.web.dl.DfxQuery;
import com.raqsoft.guide.web.dl.FileUtils;
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
  <!--
  	view：source/data/report
  	grpx:服务器上的文件或grpx内容
  	dataSource
  	ql
  	dfxFile
  	dfxScript
  	dfxParams
  	inputFiles
  	
  	grpxSourcePage //默认是当前访问的页面
  	grpxDataPage //默认是当前访问的页面
  	grpxReportPage//默认是当前访问的页面
  	reportPage 默认/dl/jsp/showReport.jsp
  	showColMax = getDefaultParam( showColMax, "30" );
  	grpxFolderOnServer = getDefaultParam( grpxFolderOnServer, "/WEB-INF/files/grpx/" );
  	dataFolderOnServer = getDefaultParam( dataFolderOnServer, "/WEB-INF/files/data/" );
  	dfxFolderOnServer = getDefaultParam( dfxFolderOnServer, "/WEB-INF/files/dfx/" );
  	inputFileFolderOnServer = getDefaultParam( inputFileFolderOnServer, "/WEB-INF/files/inputFile/" );
  	rpxFolderOnServer = getDefaultParam( rpxFolderOnServer, "/WEB-INF/files/rpx/" );
  	uploadFolderOnServer = getDefaultParam( uploadFolderOnServer, "/WEB-INF/files/" );
  	
  	
  	dqlCategory
  	fixedTable
  	outerCondition
  	dimDataOnServer
  	useDataPage
  	maxDataSize
  	maxReportSize
  	
  	dataFileType
  	
  -->
 *
 */
public class GuideTag extends TagSupport {
	private String view;
	private String showColMax;
	
	private String grpxFolderOnServer;
	private String dataFolderOnServer;
	private String dqlCategory;
	private String fixedTable;
	private String grpxSourcePage;
	private String dimDataOnServer;
	private String useDataPage;
	private String maxDataSize;
	private String showSubTable;
	private String dataFileType;
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

	public String getShowToolBar() {
		return showToolBar;
	}

	public void setShowToolBar(String showToolBar) {
		this.showToolBar = showToolBar;
	}

	private String showToolBar;
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

	private String maxReportSize;
	public String getUseDataPage() {
		return useDataPage;
	}

	public void setUseDataPage(String useDataPage) {
		this.useDataPage = useDataPage;
	}

	public String getDimDataOnServer() {
		return dimDataOnServer;
	}

	public void setDimDataOnServer(String dimDataOnServer) {
		this.dimDataOnServer = dimDataOnServer;
	}

	public String getGrpxSourcePage() {
		return grpxSourcePage;
	}

	public void setGrpxSourcePage(String grpxSourcePage) {
		this.grpxSourcePage = grpxSourcePage;
	}

	public String getGrpxDataPage() {
		return grpxDataPage;
	}

	public void setGrpxDataPage(String grpxDataPage) {
		this.grpxDataPage = grpxDataPage;
	}

	public String getGrpxReportPage() {
		return grpxReportPage;
	}

	public void setGrpxReportPage(String grpxReportPage) {
		this.grpxReportPage = grpxReportPage;
	}

	private String grpxDataPage;
	private String grpxReportPage;
	public String getFixedTable() {
		return fixedTable;
	}

	public void setFixedTable(String fixedTable) {
		this.fixedTable = fixedTable;
	}

	public String getOuterCondition() {
		return outerCondition;
	}

	public void setOuterCondition(String outerCondition) {
		this.outerCondition = outerCondition;
	}

	private String outerCondition;
	public String getDqlCategory() {
		return dqlCategory;
	}

	public void setDqlCategory(String dqlCategory) {
		this.dqlCategory = dqlCategory;
	}

	public String getShowColMax() {
		return showColMax;
	}

	public void setShowColMax(String showColMax) {
		this.showColMax = showColMax;
	}

	public String getGrpxFolderOnServer() {
		return grpxFolderOnServer;
	}

	public void setGrpxFolderOnServer(String grpxFolderOnServer) {
		this.grpxFolderOnServer = grpxFolderOnServer;
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

	public String getUploadFolderOnServer() {
		return uploadFolderOnServer;
	}

	public void setUploadFolderOnServer(String uploadFolderOnServer) {
		this.uploadFolderOnServer = uploadFolderOnServer;
	}

	private String dfxFolderOnServer;
	private String inputFileFolderOnServer;
	private String rpxFolderOnServer;
	private String uploadFolderOnServer;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getGrpx() {
		return grpx;
	}

	public void setGrpx(String grpx) {
		this.grpx = grpx;
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

	private String grpx;
	private String dataSource;
	private String ql;
	private String dfxFile;
	private String dfxScript;
	private String dfxParams;
	private String inputFiles; 
	private String reportPage; 
	
	public String getReportPage() {
		return reportPage;
	}

	public void setReportPage(String reportPage) {
		this.reportPage = reportPage;
	}

	public GuideTag() {
		view = null;
		grpx = null;
		dataSource = null;
		ql = null;
		dfxFile = null;
		dfxScript = null;
		dfxParams = null;
		inputFiles = null;
		reportPage = null;
	  	showColMax = null;
	  	grpxFolderOnServer = null;
	  	dataFolderOnServer = null;
	  	dfxFolderOnServer = null;
	  	inputFileFolderOnServer = null;
	  	rpxFolderOnServer = null;
	  	uploadFolderOnServer = null;
	  	dqlCategory = null;
	  	fixedTable = null;
	  	outerCondition = null;
	  	grpxSourcePage = null;
	  	grpxDataPage = null;
	  	grpxReportPage = null;
	  	dimDataOnServer = null;
	  	maxDataSize = null;
	  	maxReportSize = null;
	  	useDataPage = null;
	  	showSubTable = null;
	  	showToolBar = null;
	  	dataFileType = null;
	}

	public void release() {
		super.release();
		view = null;
		grpx = null;
		dataSource = null;
		ql = null;
		dfxFile = null;
		dfxScript = null;
		dfxParams = null;
		inputFiles = null;
		reportPage = null;
	  	showColMax = null;
	  	grpxFolderOnServer = null;
	  	dataFolderOnServer = null;
	  	dfxFolderOnServer = null;
	  	inputFileFolderOnServer = null;
	  	rpxFolderOnServer = null;
	  	uploadFolderOnServer = null;
	  	dqlCategory = null;
	  	fixedTable = null;
	  	outerCondition = null;
	  	grpxSourcePage = null;
	  	grpxDataPage = null;
	  	grpxReportPage = null;
	  	dimDataOnServer = null;
	  	maxDataSize = null;
	  	maxReportSize = null;
	  	useDataPage = null;
	  	showSubTable = null;
	  	showToolBar = null;
	  	dataFileType = null;
	}

	private void initParameters() {
		view = getDefaultParam( view, "" );
		grpx = getDefaultParam( grpx, "" );
		dataSource = getDefaultParam( dataSource, "" );
		ql = getDefaultParam( ql, "" );
		dfxFile = getDefaultParam( dfxFile, "" );
		dfxScript = getDefaultParam( dfxScript, "" );
		dfxParams = getDefaultParam( dfxParams, "" );
		inputFiles = getDefaultParam( inputFiles, "" );
		String guideDir = ReportConfig.raqsoftDir;
		if (!guideDir.startsWith("/")) guideDir = "/" + guideDir;
		reportPage = getDefaultParam( reportPage, guideDir+"/guide/jsp/showReport.jsp" );
	  	showColMax = getDefaultParam( showColMax, "30" );
	  	grpxFolderOnServer = getDefaultParam( grpxFolderOnServer, "/WEB-INF/files/grpx/" );
	  	dataFolderOnServer = getDefaultParam( dataFolderOnServer, "/WEB-INF/files/data/" );
	  	dfxFolderOnServer = getDefaultParam( dfxFolderOnServer, "/WEB-INF/files/dfx/" );
	  	inputFileFolderOnServer = getDefaultParam( inputFileFolderOnServer, "/WEB-INF/files/inputFile/" );
	  	rpxFolderOnServer = getDefaultParam( rpxFolderOnServer, "/WEB-INF/files/rpx/" );
	  	uploadFolderOnServer = getDefaultParam( uploadFolderOnServer, "/WEB-INF/files/" );
	  	dqlCategory = getDefaultParam( dqlCategory, "" );
	  	fixedTable = getDefaultParam( fixedTable, "" );
	  	outerCondition = getDefaultParam( outerCondition, "" );
	  	grpxSourcePage = getDefaultParam( grpxSourcePage, "" );
	  	grpxDataPage = getDefaultParam( grpxDataPage, "" );
	  	grpxReportPage = getDefaultParam( grpxReportPage, "" );
	  	dimDataOnServer = getDefaultParam( dimDataOnServer, "/WEB-INF/files/data/temp/dimData.json" );
	  	maxDataSize = getDefaultParam( maxDataSize, "10000" );
	  	maxReportSize = getDefaultParam( maxReportSize, "40000" );
	  	useDataPage = getDefaultParam( useDataPage, "yes" );
	  	showSubTable = getDefaultParam( showSubTable, "yes" );
	  	showToolBar = getDefaultParam( showToolBar, "yes" );
	  	dataFileType = getDefaultParam( dataFileType, "text" );
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
			HttpServletRequest request = ( HttpServletRequest ) pageContext.getRequest();
			request.setCharacterEncoding( "UTF-8" );
			boolean renewSession = false;
			HttpSession session = request.getSession( renewSession );
			Logger.debug("开始tag计算，会话session是否存在：");
			Logger.debug(session != null);
			Logger.debug("策略参数：是否会新建会话");
			Logger.debug(renewSession);
			//ReportServlet.reloadConfig( pageContext.getServletContext() );
			JspWriter out = pageContext.getOut();
			String error = "";
			
			//TODO 对所有输入的参数进行检查，不符合要求的明确报出错误，避免到后面用的时候发生未知的错误
			//if (dataScope.matches("")) {	
			//}

			if (DataSphereServlet.DATASOURCES == null) {
				DataSphereServlet.DATASOURCES = "";
				DataSphereServlet.DQLDATASOURCES = "";
				Map map = Env.getDBSessionFactories();
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
			
			String dataId = "";
			DfxQuery dq = null;
			String currTable = "";
			String tableNames = "";

			ParamList pl = new ParamList();
			//TODO 处理dfxParams
			putParams(null, pl, request);
			
			String grpxStr = "";
			if (grpx.length()>0) { // grpx源
				File f = new File(grpx);
				if (f.exists()) {
					grpxStr = SaveUtil.readFile(f);
				} else {
					f = new File(DataSphereServlet.ROOT_PATH+"/"+grpx);
					if (f.exists()) {
						grpxStr = SaveUtil.readFile(f);
					} else grpxStr = grpx;
				}
			}
			if (dataSource.length()>0) { //dql/sql源，支持dql源的查询界面
				if (ql.length() == 0) {
					if ((";"+DataSphereServlet.DQLDATASOURCES+";").indexOf(dataSource)>=0) {
						//view = "source";
					} else throw new Exception("data source“"+dataSource+"” not exist!");
				} else {
//					dataId = "q"+System.currentTimeMillis();
//					dq = new DfxQuery(dataSource, ql, dataId,"",cs);
				}
			} else if (dfxFile.length()>0) {
//				dataId = "q"+System.currentTimeMillis();
//				dq = new DfxQuery(dfxFile, true, pl, dataId,"",cs);
			} else if (dfxScript.length()>0) {
//				dataId = "q"+System.currentTimeMillis();
//				dq = new DfxQuery(dfxScript, false, pl, dataId,"",cs);
			} else if (inputFiles.length()>0) {
//				dataId = "q"+System.currentTimeMillis();
//				dq = new DfxQuery(inputFiles, dataId,"",cs);
//				ArrayList<String> al = dq.getTableNames();
//				for (int i=0; i<al.size(); i++) {
//					if (i>0) tableNames += ",";
//					tableNames += "'" + al.get(i) + "'";
//				}
//				currTable = dq.getCurrTable();
			}
//			if (dataId.length()>0) {
//				Object o = session.getAttribute("tempDatas");
//				ArrayList<String> tempDatas = null;
//				if (o == null) {
//					tempDatas = new ArrayList<String>();
//					session.setAttribute("tempDatas",tempDatas);
//				} else tempDatas = (ArrayList<String>)o;
//				tempDatas.add(dataId);
//				session.setAttribute(dataId, dq);
//			}
			
			String json = "";
			if (dataSource.length()>0 && DataSphereServlet.DQLDATASOURCES.indexOf(dataSource)>=0) { //"source".equalsIgnoreCase(view)
				try {
					json = ConfigUtil.getMetaDataJson(dataSource);
				} catch (Throwable e) {
					//e.printStackTrace();
					Logger.debug("--------" + e.getMessage());
					error = e.getMessage();
					Logger.warn("", e);
				}
			}

			String guideDir = ReportConfig.raqsoftDir;
			if (!guideDir.startsWith("/")) guideDir = "/" + guideDir;
			guideDir = guideDir+"/guide/";
			
			dqlCategory = dqlCategory.replaceAll("'", "").replaceAll("\"", "").replaceAll(";", "','");
			if (dqlCategory.length()>0) dqlCategory = "'"+dqlCategory+"'"; 
			
			out.println("<script type='text/javascript'>");
			out.println("var guideConf = {};");
			out.println("var lmdStr = \"" + json.replaceAll("\"", "<d__q>") + "\"");
			out.println("var grpxStr = \""+guideEncode(grpxStr)+"\";");
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
			out.println("guideConf.currTable = '"+currTable+"';");
			out.println("guideConf.tableNames = ["+tableNames+"];");
			out.println("guideConf.reportPage = '"+reportPage+"';");
			out.println("guideConf.showColMax = '"+showColMax+"';");
			out.println("guideConf.cursorSize = 10000;");
			out.println("guideConf.maxDataSize = '"+maxDataSize+"';");
			out.println("guideConf.maxReportSize = '"+maxReportSize+"';");
			out.println("guideConf.grpxFolderOnServer = '"+grpxFolderOnServer+"';");
			out.println("guideConf.dataFolderOnServer = '"+dataFolderOnServer+"';");
			out.println("guideConf.dfxFolderOnServer = '"+dfxFolderOnServer+"';");
			out.println("guideConf.inputFileFolderOnServer = '"+inputFileFolderOnServer+"';");
			out.println("guideConf.rpxFolderOnServer = '"+rpxFolderOnServer+"';");
			out.println("guideConf.uploadFolderOnServer = '"+uploadFolderOnServer+"';");
			out.println("guideConf.dqlCategory = ["+dqlCategory+"];");
			out.println("guideConf.fixedTable = '"+fixedTable+"';");
			out.println("guideConf.outerCondition = \""+outerCondition.replaceAll("\"", "<d_q>")+"\";");
			out.println("guideConf.grpxSourcePage = '"+grpxSourcePage+"';");
			out.println("guideConf.grpxDataPage = '"+grpxDataPage+"';");
			out.println("guideConf.grpxReportPage = '"+grpxReportPage+"';");
			out.println("guideConf.dimDataOnServer = '"+dimDataOnServer+"';");
			out.println("guideConf.useDataPage = '"+useDataPage+"';");
			out.println("guideConf.showToolBar = '"+showToolBar+"';");
			out.println("guideConf.showSubTable = '"+showSubTable+"';");
			out.println("guideConf.dataFileType = '"+dataFileType+"';");
			
					  	
			out.println(getFiles(dfxFolderOnServer, grpxFolderOnServer, rpxFolderOnServer, inputFileFolderOnServer));
			
			out.println("</script>");
			String title = "Raqsoft";
			pageContext.include(guideDir+"jsp/template.jsp?jsv=v1&guideDir="+URLEncoder.encode(guideDir, "UTF-8")+"&title="+URLEncoder.encode(title, "UTF-8"),true);
			
			out.println("</body>");
			out.println("</html>");

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
	
	public static String getFiles(String dfxFolderOnServer, String grpxFolderOnServer, String rpxFolderOnServer, String inputFileFolderOnServer) {
		List<String> dfxs = new ArrayList<String>();
		String dfxMain = (DataSphereServlet.ROOT_PATH+dfxFolderOnServer).replaceAll("\\\\", "/");
		dfxMain = dfxMain.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
		FileUtils.getFileList(dfxs, dfxMain, new String[]{".dfx"});
		String dfxs2 = null;
		for (int i=0; i<dfxs.size(); i++) {
			if (dfxs2 == null) dfxs2 = "'" + dfxs.get(i).substring(DataSphereServlet.ROOT_PATH.length()) + "'";
			else dfxs2 += ",'" + dfxs.get(i).substring(DataSphereServlet.ROOT_PATH.length()) + "'";
		}
		
		List<String> grpxs = new ArrayList<String>();
		String grpxMain = (DataSphereServlet.ROOT_PATH+grpxFolderOnServer).replaceAll("\\\\", "/");
		grpxMain = grpxMain.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
		FileUtils.getFileList(grpxs, grpxMain, new String[]{".grpx"});
		String grpxs2 = null;
		for (int i=0; i<grpxs.size(); i++) {
			if (grpxs2 == null) grpxs2 = "'" + grpxs.get(i).substring(DataSphereServlet.ROOT_PATH.length()) + "'";
			else grpxs2 += ",'" + grpxs.get(i).substring(DataSphereServlet.ROOT_PATH.length()) + "'";
		}

		List<String> rpxs = new ArrayList<String>();
		String rpxMain = (DataSphereServlet.ROOT_PATH+rpxFolderOnServer).replaceAll("\\\\", "/");
		rpxMain = rpxMain.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
		FileUtils.getFileList(rpxs, rpxMain, new String[]{".rpx"});
		String rpxs2 = null;
		String rpxs3 = null;
		for (int i=0; i<rpxs.size(); i++) {
			String disc = getDisc(rpxs.get(i));
			if (disc == null) continue;
			if (rpxs2 == null) rpxs2 = "'" + rpxs.get(i).substring(DataSphereServlet.ROOT_PATH.length()) + "'";
			else rpxs2 += ",'" + rpxs.get(i).substring(DataSphereServlet.ROOT_PATH.length()) + "'";
			if (rpxs3 == null) rpxs3 = "'"+disc+"'";
			else rpxs3 += ",'"+disc+"'";
		}

		List<String> inputs = new ArrayList<String>();
		String root = (DataSphereServlet.ROOT_PATH+inputFileFolderOnServer).replaceAll("\\\\", "/");
		root = root.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
		FileUtils.getFileList(inputs, root, new String[]{".b",".json"});
		String inputs2 = null;
		for (int i=0; i<inputs.size(); i++) {
			if (inputs2 == null) inputs2 = "'" + inputs.get(i).substring(DataSphereServlet.ROOT_PATH.length()) + "'";
			else inputs2 += ",'" + inputs.get(i).substring(DataSphereServlet.ROOT_PATH.length()) + "'";
		}

		String s = "var existGrpx = ["+(grpxs2==null?"":grpxs2)+"];";
		s += "var existRpx = ["+(rpxs2==null?"":rpxs2)+"];";
		s += "var existRpxDisc = ["+(rpxs3==null?"":rpxs3)+"];";
		s += "var existDfx = ["+(dfxs2==null?"":dfxs2)+"];";
		s += "var existInputFiles = ["+(inputs2==null?"":inputs2)+"];";
		return s;
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

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
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
