package com.raqsoft.guide.ctxtag;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.raqsoft.guide.resource.GuideMessage;
import com.raqsoft.guide.util.DBUtil;
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
	qyx="<%=grpx %>"
	dataSource="<%=dataSource %>"
	
	analysePage="/raqsoft/guide/jsp/analyse.jsp"
  	qyxFolderOnServer="/WEB-INF/files/qyx/"
  	
  	dqlCategory="国内公司;海外公司"
  	fixedTable="<%=fixedTable %>"
  	outerCondition="<%=outerCondition %>"
  	dimDataOnServer="/WEB-INF/files/data/temp/dimData.json"

	showToolBar="<%=showToolBar %>"
	showSubTable="<%=showSubTable %>"
	maxDimSize=5000
	showNullGroup="yes"
	detectLevel="4"
	
	dictionary = "";
	visibility = "";
 *
 */
public class QueryTag extends TagSupport {
	private String qyx;
	private String dataSource;
	private String fileHome;
	private String maxDimSize;
	private String showNullGroup;
	private String dictionary;
	private String tempDBName;
	
	public String getTempDBName() {
		return tempDBName;
	}

	public void setTempDBName(String tempDBName) {
		this.tempDBName = tempDBName;
	}
	
	public String getDictionary() {
		return dictionary;
	}

	public void setDictionary(String dictionary) {
		this.dictionary = dictionary;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}


	private String visibility;
	public String getShowNullGroup() {
		return showNullGroup;
	}

	public void setShowNullGroup(String showNullGroup) {
		this.showNullGroup = showNullGroup;
	}

	public String getDetectLevel() {
		return detectLevel;
	}

	public void setDetectLevel(String detectLevel) {
		this.detectLevel = detectLevel;
	}


	private String detectLevel;
	public String getMaxDimSize() {
		return maxDimSize;
	}

	public void setMaxDimSize(String maxDimSize) {
		this.maxDimSize = maxDimSize;
	}

	public String getFileHome() {
		return fileHome;
	}

	public void setFileHome(String fileHome) {
		this.fileHome = fileHome;
	}

	public String getQyx() {
		return qyx;
	}

	public void setQyx(String qyx) {
		this.qyx = qyx;
	}

	public String getAnalysePage() {
		return analysePage;
	}

	public void setAnalysePage(String analysePage) {
		this.analysePage = analysePage;
	}

	public String getQyxFolderOnServer() {
		return qyxFolderOnServer;
	}

	public void setQyxFolderOnServer(String qyxFolderOnServer) {
		this.qyxFolderOnServer = qyxFolderOnServer;
	}

	public String getDqlCategory() {
		return dqlCategory;
	}

	public void setDqlCategory(String dqlCategory) {
		this.dqlCategory = dqlCategory;
	}

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

	public String getDimDataOnServer() {
		return dimDataOnServer;
	}

	public void setDimDataOnServer(String dimDataOnServer) {
		this.dimDataOnServer = dimDataOnServer;
	}

	public String getShowToolBar() {
		return showToolBar;
	}

	public void setShowToolBar(String showToolBar) {
		this.showToolBar = showToolBar;
	}

	public String getShowSubTable() {
		return showSubTable;
	}

	public void setShowSubTable(String showSubTable) {
		this.showSubTable = showSubTable;
	}


	private String analysePage;
	private String qyxFolderOnServer;
	private String dqlCategory;
	private String fixedTable;
	private String outerCondition;
	private String dimDataOnServer;
	private String showToolBar;
	private String showSubTable;
	public QueryTag() {
		qyx = null;
		dataSource = null;
		analysePage = null;
		qyxFolderOnServer = null;
		dqlCategory = null;
		fixedTable = null;
		outerCondition = null;
		dimDataOnServer = null;
		showToolBar = null;
		showSubTable = null;
		fileHome = null;
		maxDimSize = null;
		showNullGroup=null;
		detectLevel=null;
		dictionary = null;
		visibility = null;
		tempDBName = null;
	}

	public void release() {
		super.release();
		qyx = null;
		dataSource = null;
		analysePage = null;
		qyxFolderOnServer = null;
		dqlCategory = null;
		fixedTable = null;
		outerCondition = null;
		dimDataOnServer = null;
		showToolBar = null;
		showSubTable = null;
		fileHome = null;
		maxDimSize = null;
		showNullGroup=null;
		detectLevel=null;
		dictionary = null;
		visibility = null;
		tempDBName = null;
	}

	private void initParameters() {
		qyx = getDefaultParam( qyx, "" );
		dataSource = getDefaultParam( dataSource, "" );
		analysePage = getDefaultParam( analysePage, "" );
		qyxFolderOnServer = getDefaultParam( qyxFolderOnServer, "WEB-INF/files/qyx/" );
		dqlCategory = getDefaultParam( dqlCategory, "" );
		fixedTable = getDefaultParam( fixedTable, "" );
		String guideDir = ReportConfig.raqsoftDir;
		if (guideDir.startsWith("/")) guideDir = guideDir.substring(1);
	  	outerCondition = getDefaultParam( outerCondition, "" );
	  	dimDataOnServer = getDefaultParam( dimDataOnServer, "WEB-INF/files/data/temp/dimData.json" );
	  	showToolBar = getDefaultParam( showToolBar, "yes" );
	  	showSubTable = getDefaultParam( showSubTable, "yes" );
	  	fileHome = getDefaultParam( fileHome, "" );
	  	maxDimSize = getDefaultParam( maxDimSize, "5000" );
		showNullGroup = getDefaultParam( showNullGroup, "yes" );
		detectLevel = getDefaultParam( detectLevel, "4" );
		dictionary = getDefaultParam( dictionary, "" );
		visibility = getDefaultParam( visibility, "" );
		tempDBName = getDefaultParam( tempDBName, "raq_tempDB" );
	}

	private String getDefaultParam( String param, String def ) {
		if ( param == null || param.trim().length() == 0) {
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
			HttpSession session = request.getSession( true );
			//ReportServlet.reloadConfig( pageContext.getServletContext() );
			HttpServletResponse response = ( HttpServletResponse ) pageContext.getResponse();
			response.setContentType("text/html;charset=utf8");  
			response.setCharacterEncoding("UTF-8");
			JspWriter out = pageContext.getOut();
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
							if ("com.datalogic.jdbc.LogicDriver".equals(dbsf.getDBConfig().getDriver()) || "com.esproc.dql.jdbc.DQLDriver".equals(dbsf.getDBConfig().getDriver())) {
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
			putParams(null, pl, request);
			
			String qyxStr = "";
			if (qyx.length()>0) { // grpx源
				File f = new File(qyx);
				if (f.exists()) {
					qyxStr = SaveUtil.readFile(f);
				} else {
					try {
						f = new File(DataSphereServlet.getFilePath(qyx));
						if (f.exists()) {
							qyxStr = SaveUtil.readFile(f);
						} else qyxStr = qyx;
					} catch (Exception e) {
						qyxStr = qyx;
					}
				}
			}
			
			if (qyxStr.length()>0) {
				int pos1 = qyxStr.indexOf("_db_pre_");
				int pos2 = qyxStr.indexOf("_db_end_");
				if (pos1>0 && pos2>pos1) {
					dataSource = qyxStr.substring(pos1+"_db_pre_".length(), pos2);
				}
			}
//			Logger.debug("dataSource : " + dataSource);
//			Logger.debug("qyxStr : " + qyxStr);
			boolean isGlmd = true;
			ISessionFactory isf = Env.getDBSessionFactory(dataSource);
			if (isf instanceof DBSessionFactory) {
				DBSessionFactory dbsf = (DBSessionFactory)isf;
				if ("com.esproc.dql.jdbc.DQLDriver".equals(dbsf.getDBConfig().getDriver())) {
					isGlmd = true;
				}
			}
			boolean noGlmdOrDs = false;
			String configFile = request.getParameter("config");
			if(dataSource == null || dataSource.length() == 0){
				String glmdPath = request.getParameter("glmd");
				if(glmdPath != null && glmdPath.length() != 0){
					DBUtil.putGlmdIntoDBList(glmdPath, tempDBName, configFile);
					dataSource = tempDBName;
				}else{
					//throw new Exception("no glmd file or dataSource");
					noGlmdOrDs=true;
				}
			}
			String[] infos = getDqlInfo(dataSource,this.dictionary,this.visibility, isGlmd, request, this.tempDBName);
			String guideDir = ReportConfig.raqsoftDir;
			if (!guideDir.startsWith("/")) guideDir = "/" + guideDir;
			guideDir = guideDir+"/guide/";
			
			dqlCategory = dqlCategory.replaceAll("'", "").replaceAll("\"", "").replaceAll(";", "','");
			if (dqlCategory.length()>0) dqlCategory = "'"+dqlCategory+"'";
			
			
			out.println("<script type='text/javascript'>");
			out.println("var guideConf = {};");
			out.println("var lmdStr = \"" + infos[0] + "\";");
			out.println("var isGlmd = "+isGlmd+";");
			out.println("var qyxStr = \""+guideEncode(qyxStr)+"\";");
			out.println("guideConf.guideDir = '"+guideDir+"';");
//			out.println("guideConf.view = '"+view+"';");
			out.println("guideConf.error = '"+error.replaceAll("'", " ")+"';");
			out.println("guideConf.dataSource = '"+dataSource+"';");
			out.println("guideConf.analysePage = '"+analysePage+"';");
			out.println("guideConf.dataSources = '"+DataSphereServlet.DATASOURCES+"';");
			out.println("guideConf.dqlDataSources = '"+DataSphereServlet.DQLDATASOURCES+"';");
			out.println("guideConf.qyxFolderOnServer = '"+qyxFolderOnServer+"';");
			out.println("guideConf.dqlCategory = ["+dqlCategory+"];");
			out.println("guideConf.fixedTable = '"+fixedTable+"';");
			out.println("guideConf.outerCondition = \""+outerCondition.replaceAll("\"", "<d_q>")+"\";");
			out.println("guideConf.dimDataOnServer = '"+dimDataOnServer+"';");
			out.println("guideConf.showSubTable = '"+showSubTable+"';");
			out.println("guideConf.maxDimSize = '"+maxDimSize+"';");
			out.println("guideConf.showNullGroup = '"+showNullGroup+"';");
			out.println("guideConf.detectLevel = '"+detectLevel+"';");
			out.println("guideConf.showToolBar = '"+showToolBar+"';");
			out.println("guideConf.dictionary = \""+infos[1]+"\";");
			out.println("guideConf.visibility = \""+infos[2]+"\";");
			
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
			
			//out.println("guideConf.fileHome = '"+fileHome+"';");
			session.setAttribute("fileHome", fileHome);
			
			
		  	
			out.println(getFiles(qyxFolderOnServer, null, null, null, null,null));
			if(noGlmdOrDs){
				out.println("var noGlmdFile = true;");
			}else{
				out.println("var noGlmdFile = false;");
			}
			out.println("</script>");
			String title = "Raqsoft Query";
			pageContext.include(guideDir+"jsp/template_c.jsp?jsv=new&guideDir="+URLEncoder.encode(guideDir, "UTF-8")+"&title="+URLEncoder.encode(title, "UTF-8"),false);
			
		}
		catch ( Throwable e ) {
			Logger.error( new String("ERROR:"), e );
			JspWriter out = pageContext.getOut();
			out.println("<script>alert('"+e.getMessage()+"');</script>");
		}
		finally {
			//if( ReportServlet.releaseTag ) release();
			if (true) release();
			return EVAL_PAGE;
		}
	}
	
	public static String[] getDqlInfo(String dataSource, String gdct, String gvsb, boolean isGlmd, ServletRequest request, String tempDBName) throws Throwable {
		String [] r = new String[3];
		r[0] = "";
		try {
			if (isGlmd) {
				r[0] = ConfigUtil.getListJsonData(dataSource).replaceAll("\"", "<d__q>");
			}else{
				r[0] = ConfigUtil.getMetaDataJson(dataSource).replaceAll("\"", "<d__q>");
			}
			
		} catch (Throwable e) {
			Logger.warn("", e);
			r[0] = "";
			throw e;
		}
		
		if (gdct.length()>0) {
			try {
				r[1] = FileUtils.getDict(DataSphereServlet.getFilePath(gdct)).replaceAll("\"", "<d_q>");
			} catch (Exception e) {
				Logger.warn("read dictionary error ! " + r[1]);
				r[1] = "";
			}
		} else r[1] = "";
		
		if (gvsb.length()>0) {
			try {
				r[2] = FileUtils.getVsb(DataSphereServlet.getFilePath(gvsb)).replaceAll("\"", "<d_q>");
			} catch (Exception e) {
				Logger.warn("read visibility error ! " + gvsb);
				r[2] = "";
			}
		} else r[2] = "";
		return r;
	}
	
	public static String getFiles(String qyxFolderOnServer, String olapFolderOnServer, String dfxFolderOnServer, String rpxFolderOnServer, String inputFileFolderOnServer, String fileDataFolderOnServer) {
		String s = "";
		String realHome = DataSphereServlet.getFilePath("");
		if (dfxFolderOnServer != null) {
			List<String> dfxs = new ArrayList<String>();
			String dfxMain = (DataSphereServlet.getFilePath(dfxFolderOnServer)).replaceAll("\\\\", "/");
			dfxMain = dfxMain.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
			FileUtils.getFileList(dfxs, dfxMain, new String[]{".dfx"});
			String dfxs2 = null;
			for (int i=0; i<dfxs.size(); i++) {
				if (dfxs2 == null) dfxs2 = "'" + dfxs.get(i).substring(realHome.length()+1) + "'";
				else dfxs2 += ",'" + dfxs.get(i).substring(realHome.length()+1) + "'";
			}
			s += "var existDfx = ["+(dfxs2==null?"":dfxs2)+"];";
		}
		
		if (olapFolderOnServer != null) {
			List<String> olap = new ArrayList<String>();
			String olapMain = (DataSphereServlet.getFilePath(olapFolderOnServer)).replaceAll("\\\\", "/");
			olapMain = olapMain.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
			FileUtils.getFileList(olap, olapMain, new String[]{".olap"});
			String olap2 = null;
			for (int i=0; i<olap.size(); i++) {
				if (olap2 == null) olap2 = "'" + olap.get(i).substring(realHome.length()+1) + "'";
				else olap2 += ",'" + olap.get(i).substring(realHome.length()+1) + "'";
			}
			s += "var existOlap = ["+(olap2==null?"":olap2)+"];";
		}

		if (qyxFolderOnServer != null) {
			List<String> qyx = new ArrayList<String>();
			String qyxMain = (DataSphereServlet.getFilePath(qyxFolderOnServer)).replaceAll("\\\\", "/");
			qyxMain = qyxMain.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
			FileUtils.getFileList(qyx, qyxMain, new String[]{".qyx"});
			String qyx2 = null;
			for (int i=0; i<qyx.size(); i++) {
				if (qyx2 == null) qyx2 = "'" + qyx.get(i).substring(realHome.length()+1) + "'";
				else qyx2 += ",'" + qyx.get(i).substring(realHome.length()+1) + "'";
			}
			s += "var existQyx = ["+(qyx2==null?"":qyx2)+"];";
		}
		if (rpxFolderOnServer != null) {
			List<String> rpxs = new ArrayList<String>();
			String rpxMain = (DataSphereServlet.getFilePath(rpxFolderOnServer)).replaceAll("\\\\", "/");
			rpxMain = rpxMain.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
			FileUtils.getFileList(rpxs, rpxMain, new String[]{".rpx"});
			String rpxs2 = null;
			String rpxs3 = null;
			for (int i=0; i<rpxs.size(); i++) {
				String disc = getDisc(rpxs.get(i));
				if (disc == null) continue;
				if (rpxs2 == null) rpxs2 = "'" + rpxs.get(i).substring(realHome.length()+1) + "'";
				else rpxs2 += ",'" + rpxs.get(i).substring(realHome.length()+1) + "'";
				if (rpxs3 == null) rpxs3 = "'"+disc+"'";
				else rpxs3 += ",'"+disc+"'";
			}
			s += "var existRpx = ["+(rpxs2==null?"":rpxs2)+"];";
			s += "var existRpxDisc = ["+(rpxs3==null?"":rpxs3)+"];";
		}

		if (inputFileFolderOnServer != null) {
			List<String> inputs = new ArrayList<String>();
			String root = (DataSphereServlet.getFilePath(inputFileFolderOnServer)).replaceAll("\\\\", "/");
			root = root.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
			FileUtils.getFileList(inputs, root, new String[]{".b",".json"});
			String inputs2 = null;
			for (int i=0; i<inputs.size(); i++) {
				if (inputs2 == null) inputs2 = "'" + inputs.get(i).substring(realHome.length()+1) + "'";
				else inputs2 += ",'" + inputs.get(i).substring(realHome.length()+1) + "'";
			}
			s += "var existInputFiles = ["+(inputs2==null?"":inputs2)+"];";
		}

		if (fileDataFolderOnServer != null) {
			List<String> inputs = new ArrayList<String>();
			String root = (DataSphereServlet.getFilePath(fileDataFolderOnServer)).replaceAll("\\\\", "/");
			root = root.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
			FileUtils.getFileList(inputs, root, new String[]{".txt",".csv",".xls",".xlsx"});
			String inputs2 = null;
			for (int i=0; i<inputs.size(); i++) {
				if (inputs2 == null) inputs2 = "'" + inputs.get(i).substring(realHome.length()+1) + "'";
				else inputs2 += ",'" + inputs.get(i).substring(realHome.length()+1) + "'";
			}
			s += "var existFileDatas = ["+(inputs2==null?"":inputs2)+"];";
		}
		
		

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
