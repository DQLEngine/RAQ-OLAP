package com.raqsoft.guide.web.dl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raqsoft.app.config.ConfigUtil;
import com.raqsoft.app.config.RaqsoftConfig;
import com.raqsoft.common.IOUtils;
import com.raqsoft.common.Logger;
import com.raqsoft.dm.Sequence;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.parallel.MulticastMonitor;
import com.raqsoft.report.resources.ServerMessage;
import com.raqsoft.report.view.ReportServlet;

public class DLServlet extends HttpServlet {

	public static final int ACTION_READJS = 1;
	public static final int ACTION_RESULT_PAGE = 2;
	public static final int ACTION_RESULT_ESCALC = 3;
	public static final int ACTION_DIM = 4;
	public static final int ACTION_SEARCH = 5;
	public static final int ACTION_VIEW = 6; //
	
	public static String filePathInSession;
	public static String cachePath;
	
	public static String appUrlPrefix = null;
	private static String configPath = null;

	/** 初始化，从web.xml文件中读取初始参数 */
	public void init() throws ServletException {
		//Logger.setLevel(Level.ALL.toString());
		Logger.info( new String("DL System initing......") );
		ServletContext application = this.getServletContext();
		String configFilePath = this.getServletConfig().getInitParameter( "configFile" );
		application.setAttribute( "___reportConfigFile", configFilePath );
		try {
			loadConfig( application, false );
		} catch( Exception e ) {
			Logger.error("",e);
			e.printStackTrace();
//			throw new ServletException( e );
		}
		Logger.info( new String("DL System initialized......") );
	}

	public static void loadConfig( ServletContext application, boolean isReload ) throws Exception {
		String configFilePath = ( String ) application.getAttribute( "___reportConfigFile" );
//		if ( "".equals( configFilePath ) || configFilePath == null ) {
//			throw new ServletException( "No config file!" );
//		}
		cachePath = application.getRealPath("/WEB-INF/caches/");
		if(cachePath != null) new File(cachePath).mkdir();
		else{
			//war包发布找不到根路径，直接用系统临时路径
			//Properties props = config.getServerProperties();
			String sysTempdir = System.getProperty( "java.io.tmpdir" );
			sysTempdir = new File( sysTempdir ).getAbsolutePath();
			if ( cachePath != null && cachePath.trim().length() > 0 ) {
				//Logger.info( ServerMessage.get().getMessage( "config.cachdir4", sysTempdir ) ); // 没有指定报表缓存文件夹，采用系统临时文件夹sysTempdir
				Logger.info( "can't find home path, redirect to system temp path: "+ sysTempdir );
				cachePath = sysTempdir;
			}
		}
		//loadConfig( null, null, isReload );
		//loadConfig( application, configFilePath, isReload );
		try{
			Sequence.checkExpiration();
		}catch(Exception e){
			e.printStackTrace();
		}
		RaqsoftConfig config = null;
		InputStream is = null;
		is = null;
		try {
			is = application.getResourceAsStream( configFilePath );
			if ( configFilePath != null ) {
				is = new FileInputStream( configFilePath );
			}
			if(is != null) config = ConfigUtil.load( is );
		}
		catch ( Throwable e ) {
			Logger.debug( e );
			throw new ServletException( e );
		}
		try{
			Sequence.checkExpiration();//如果已有集算器授权跳过这步
		}catch(Exception e){
			e.printStackTrace();
			try { 
				String licFile = null;
				if(config != null) licFile = config.getEsprocLicense();
				//如果没有配置授权就加载内置
				if(licFile == null || licFile.length() == 0) licFile = ConfigUtil.getBuiltinLicenseFileName( ConfigUtil.FROM_REPORT );
				is = path2Stream( licFile, application );
				if( is != null ) Sequence.readLicense( Sequence.P_PROC, is );
			}
			catch( Throwable t ){}finally{
				is.close();
			}
		}
		String ips = Sequence.getIPRange( Sequence.P_PROC );
		//Logger.debug("start dql proc auth ip checking...");
		if (ips != null && "127.0.0.1".equals(ips.trim())) {
			try{
				MulticastMonitor.checkMajorNo(Sequence.P_PROC);// 主号
				DataSphereServlet.binAuth = true;
			}catch(Exception e){
				e.printStackTrace();
				Logger.debug("set bin auth false");
				DataSphereServlet.binAuth = false;
			}
			
		}
		//Logger.debug("dql proc auth ip check end");
	}


	/**
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void service( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		try {

			//if (request.getRequestURI().indexOf("DLServletAjax") != -1){
				request.setCharacterEncoding("UTF-8");
			//}
			String tmp = request.getParameter( "action" );
//			Logger.debug(request.getSession().getId() + "------" + request.getSession().getMaxInactiveInterval());
			int action = 1;
			try {
				action = Integer.parseInt( tmp );
			}
			catch ( Throwable t ) {
				//throw new ServletException( ServerMsg.getMessage( request, "actionError" ) );
				// 没有指定action或action错误
			}
			switch ( action ) {
//				case ACTION_READJS:
//					new ReadJavascript().service( request, response );
//					break;
				case ACTION_RESULT_PAGE:
					new ActionResultPage().service( request, response, null, null );
					break;
//				case ACTION_RESULT_ESCALC:
//					new ActionResult4EsCalc().service( request, response );
//					break;
				case ACTION_DIM:
					new ActionDim().service( request, response );
					break;
										
			}
		}
		catch ( Exception e ) {
			try {
				response.getWriter().print("error:"+e.getMessage());
			}catch ( Exception e2 ) {
			}
			throw new ServletException( e );
		}
	}



	public static void reloadConfig( ServletContext application ) throws Exception {
		Logger.info( new String("Reload DL Config.............") );
		loadConfig( application, true );
	}

	public static void reloadConfig( ServletContext application, String configFilePath ) throws Exception {
		Logger.info( new String("DL System initing......") );
//		DLEnv.loadConfig();
		Logger.info( new String("DL System initialized......") );
	}

	public void destroy() {
	}
	
	private static InputStream getInputStreamByPath(String path, ServletContext application) {
		InputStream is = null;
		try {
			if (new File(path).exists()) {
				return new FileInputStream(path);
			}
			is = application.getResourceAsStream(path);
			if (is != null) return is;
			is = InputStream.class.getResourceAsStream( path );
			if (is != null) return is;
		} catch (Exception e) {
			Logger.error("",e);
			e.printStackTrace();
		} 
		return null;
	}
	
	private static InputStream path2Stream( String path, ServletContext application ) throws Exception {
		InputStream lis = null;
		if( ! ( new File( path ).exists() ) ) {
			try{
				lis = Thread.currentThread().getContextClassLoader().getResourceAsStream( path );
			}catch( Throwable th ) {}
			if( lis == null ) {
				try{
					lis = DLServlet.class.getResourceAsStream( path );
				}catch( Throwable th ) {}
			}
			if( lis == null ) {
				if( !path.startsWith( "/" ) ) path = "/" + path; 
				lis = application.getResourceAsStream( path );
			}
		}
		else {
			lis = new FileInputStream( path );
		}
		return lis;
	}
}
