package com.raqsoft.guide.tag;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.raqsoft.common.DBSessionFactory;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.common.StringUtils;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.Param;
import com.raqsoft.dm.ParamList;
import com.raqsoft.guide.resource.GuideMessage;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.dl.ConfigUtil;
import com.raqsoft.guide.web.dl.DfxQuery;
import com.raqsoft.guide.web.dl.DfxUtils;
import com.raqsoft.guide.web.dl.FileUtils;
import com.raqsoft.guide.web.dl.ReportConf;
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
public class CommonQueryTag extends TagSupport {
	private String metadata;
	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	private String params;
	private String cqx;
	public String getCqx() {
		return cqx;
	}

	public void setCqx(String cqx) {
		this.cqx = cqx;
	}

	public CommonQueryTag() {
		metadata = null;
		params = null;
		cqx = null;
	}

	public void release() {
		super.release();
		metadata = null;
		params = null;
		cqx = null;
	}

	private void initParameters() {
		metadata = getDefaultParam( metadata, "" );
		params = getDefaultParam( params, "" );
		cqx = getDefaultParam( cqx, "" );
	}

	public int doStartTag() throws JspTagException {
		JspWriter out = null;
		try {
			initParameters();
			HttpServletRequest request = ( HttpServletRequest ) pageContext.getRequest();
			request.setCharacterEncoding( "UTF-8" );
			HttpSession session = request.getSession( true );
			//ReportServlet.reloadConfig( pageContext.getServletContext() );
			HttpServletResponse response = ( HttpServletResponse ) pageContext.getResponse();
			response.setContentType("text/html;charset=utf8");  
			//PrintWriter out = response.getWriter();
			String error = "";
			out = pageContext.getOut();
			//TODO 对所有输入的参数进行检查，不符合要求的明确报出错误，避免到后面用的时候发生未知的错误
			//if (dataScope.matches("")) {	
			//}
			String json = "";
			if (metadata.length()==0) {
				throw new Exception("need metadata file.");
			} else {
				if (metadata.toLowerCase().endsWith(".json")) {
					File f = new File(DataSphereServlet.getFilePath(metadata));
					if (!f.exists()) throw new Exception("not found json file.");
					json = FileUtils.readFile(new File(DataSphereServlet.getFilePath(metadata)));
				} else if (metadata.toLowerCase().endsWith(".dfx")) {
					File f = new File(DataSphereServlet.getFilePath(metadata));
					if (!f.exists()) throw new Exception("not found dfx file.");

					Context ctx = new Context();
					if (params.length()>0) {
//						String[] ps = params.split("&");
//						for (int i=0; i<ps.length; i++) {
//							String[] pss = ps[i].split("=");
//							ctx.setParamValue(pss[0], pss[1]);
//							System.out.println(ps[i]);
//						}
						
						ctx.setParamValue("params", params);
					} 

					DfxUtils.execDfxFile(DataSphereServlet.getFilePath(metadata), ctx);
					json = ctx.getParam("_returnValue_").getValue().toString();
				} else {
					json = metadata;
					//throw new Exception("metadata file not correct, need *.json or *.dfx");
				}
			}
			
			out.println("<script type='text/javascript'>");
			out.println("var commQuery = {};");
			out.println("commQuery.cqx = `" + cqx + "`;");
			out.println("commQuery.params = `" + params + "`;");
			out.println("commQuery.metadata = `" + json + "`;");
				
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
			session.setAttribute("fileHome", "");
			
			
		  	
			
			out.println("</script>");
			String title = "Raqsoft Query";
			pageContext.include("/raqsoft/guide/jsp/common.jsp",false);
			
		}
		catch ( Throwable e ) {
			if (out != null) out.println(e.getMessage());
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

	private String getDefaultParam( String param, String def ) {
		if ( param == null || param.trim().length() == 0) {
			return def;
		}
		return param;
	}

}
