package com.raqsoft.guide.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.raqsoft.common.ArgumentTokenizer;
import com.raqsoft.common.Escape;
import com.raqsoft.common.Logger;
import com.raqsoft.dm.Env;
import com.raqsoft.guide.Consts;
import com.raqsoft.guide.FileManager;
import com.raqsoft.guide.web.dbd.CleanFolderTask;
import com.raqsoft.guide.web.dl.ReportStyle;
import com.raqsoft.guide.web.dl.SaveUtil;
import com.raqsoft.guide.web.querys.TaskMonitor;
import com.raqsoft.report.usermodel.Context;

public class DataSphereServlet extends HttpServlet {

	public static final int ACTION_NEWFOLDER = 1;
	public static final int ACTION_DELFOLDER = 2;
	public static final int ACTION_FOLDERDETAIL = 3;
	public static final int ACTION_FOLDERFILES = 4;
	public static final int ACTION_SAVEGEX = 5;
	public static final int ACTION_SAVEQYX = 6;
	public static final int ACTION_DOWNLOAD_QYX = 36;
	public static final int ACTION_SHARE = 7;
	public static final int ACTION_UNSHARE = 8;
	public static final int ACTION_PASTE = 9;
	public static final int ACTION_UPLOAD = 10;
	public static final int ACTION_UPLOAD_GRPX = 37;
	public static final int ACTION_UPLOAD_FILE = 38;
	public static final int ACTION_DOWNLOAD = 11;
	public static final int ACTION_DELETE = 12;
	public static final int ACTION_ASKREPLACE = 13;
	public static final int ACTION_ANSWER = 14;
	public static final int ACTION_REFRESHTREE = 15;
	public static final int ACTION_CHANGEPWD = 16;
	public static final int ACTION_RENAME = 17;
	public static final int ACTION_ASKUPREPLACE = 18;
	public static final int ACTION_ANSWERUP = 19;
	public static final int ACTION_FILENOTE = 20;
	public static final int ACTION_SAVEUSER = 30;
	public static final int ACTION_REFRESHUSERS = 31;
	public static final int ACTION_DELETEUSER = 32;
	public static final int ACTION_GETABSPATH = 33;
	public static final int ACTION_LOGOUT = 34;
	public static final int ACTION_READGEX = 35;
	public static final int ACTION_INIT_DBD = 36;
	public static final int ACTION_DBD_FOLDERINFO = 39;
	public static final int ACTION_DBD_FINALVIEW = 40;
	public static final int ACTION_DBD_REMOVEDBDFILE = 41;
	public static final int ACTION_DBD_PASTEDBDFILE = 42;
	public static final int ACTION_REFRESH_ANACONFIG_META = 43; //max
	public static int maxExportRow = 100000;//最大导出行数，防止非流式导出时内存溢出。
	//public static HashMap lexiconMap = new HashMap();
	//public static HashMap logicmetadataMap = new HashMap();
	public static String ROOT_PATH = null;
	public static String APP_ROOT = null;
	public static String DFX_SAVE = null;
	public static String DFX_READ = null;
	public static String DFX_REPORT = null;
	public static String DFX_SAVE_TXT = null;
	public static String DFX_READ_TXT = null;
	public static String DFX_REPORT_TXT = null;
	public static String DATASET_CONFIG = null;
	public static String DATASET_FILE_HOME = null;
	public static String dbd_olapFileTempdir = "/WEB-INF/files/olap/temp";
	
	//路径转移到服务器默认配置上来，不受web访问影响，可被web.xml初始化servlet修改
	//已经应用到DownloadFileServlet
	public static String olapFolderOnServer="/WEB-INF/files/olap/";
	public static String dataFolderOnServer="/WEB-INF/files/data/";
	public static String fileDataFolderOnServer = "/WEB-INF/files/fileData/";
	public static String dfxFolderOnServer="/WEB-INF/files/dfx/";
	public static String inputFileFolderOnServer="/WEB-INF/files/inputFile/";
	public static String rpxFolderOnServer="/WEB-INF/files/rpx/";
	public static final String dimDataName="dimData.json";
	public static String dimDataFolderOnServer="/WEB-INF/files/data/temp/";
	public static String dimDataOnServer=dimDataFolderOnServer+dimDataName;
	public static String qyxFolderOnServer="/WEB-INF/files/qyx/";
	public static String dbdFolderOnServer="/WEB-INF/files/dbd/";
	public static String dbdImageFileFolderOnServer="/raqsoft/guide/dbd2.0/img/dbdStyleImage/";
	
	public static String DATASOURCES = null;
	public static String DQLDATASOURCES = null;
	
	public static String TESTER = "";
	

	public static QueryListener queryListener;

	/** 初始化，从web.xml文件中读取初始参数 */
	public void init() throws ServletException {
		Logger.info( new String("Guide initing......") );
		try {
			ServletContext application = this.getServletContext();
			String configFilePath = this.getServletConfig().getInitParameter( "configFile" );
			application.setAttribute( "___dataSphereConfigFile", configFilePath );
			loadConfig( application, configFilePath );
//			tm = new TaskMonitor(ActionResultPage.tsks);
//			tm.start();
			CleanFolderTask cft = new CleanFolderTask();
			Thread t = new Thread(cft);
			t.start();
		}
		catch ( Exception e ) {
			Logger.error("",e);
			e.printStackTrace();
		}
		Logger.info( new String("Guide initialized......") );
	}
	private TaskMonitor tm = null;
	public void destroy(){
		//tm.stop();
	}

	public void loadConfig( ServletContext application ) throws Exception {
		String configFilePath = ( String ) application.getAttribute( "___dataSphereConfigFile" );
		if ( "".equals( configFilePath ) || configFilePath == null ) {
			Logger.info( "No dataSphere config file!" );
		}
		loadConfig( application, configFilePath );
	}

	public void loadConfig( ServletContext application, String configFilePath ) throws Exception {
//		IReportConfigManager config = ReportConfigManagerImpl.getInstance(); // 读servlet配置文件
//		config.setInputStream( application.getResourceAsStream( configFilePath ) );

//		String logConfig = config.getInitParameter( "logConfig" );
//		if ( logConfig != null && logConfig.trim().length() > 0 ) {
//			InputStream lcis = application.getResourceAsStream( logConfig );
//			if ( lcis != null ) {
//				Properties p = new Properties();
//				try {
//					p.load( lcis );
//					Logger.setPropertyConfig( p );
//				
//					lcis.close();
//				}
//				catch ( Exception e1 ) {
//					e1.printStackTrace();
//				}
//			}
//		}
		//Scanner s = new Scanner(System.in);
		if(configFilePath != null) {
			InputStream is = application.getResourceAsStream("/WEB-INF/"+configFilePath);
			if(is == null) {
				Logger.debug("没有找到文件："+configFilePath);
			}else {
				ResourceBundle resource = null;
				try {
					if(is != null) {
						resource = new PropertyResourceBundle(is);
						is.close();
						
						
						DataSphereServlet.olapFolderOnServer = resource.getString("olapFolderOnServer");
						DataSphereServlet.dataFolderOnServer = resource.getString("dataFolderOnServer");
						DataSphereServlet.fileDataFolderOnServer = resource.getString("fileDataFolderOnServer");
						
						DataSphereServlet.dfxFolderOnServer = resource.getString("dfxFolderOnServer");
						DataSphereServlet.inputFileFolderOnServer = resource.getString("inputFileFolderOnServer");
						DataSphereServlet.rpxFolderOnServer = resource.getString("rpxFolderOnServer");
						DataSphereServlet.dimDataFolderOnServer = resource.getString("dimDataFolderOnServer");
						DataSphereServlet.qyxFolderOnServer = resource.getString("qyxFolderOnServer");
						DataSphereServlet.dimDataOnServer = DataSphereServlet.dimDataFolderOnServer + DataSphereServlet.dimDataName;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		ROOT_PATH = application.getRealPath("/");
		String configRootPath = this.getServletConfig().getInitParameter( "root-path" );
		File configRootPathFolder = null;
		if(configRootPath != null && configRootPath.length() > 0){
			configRootPathFolder = new File(configRootPath);
		}else{
			Logger.debug("lack of DataSphereServlet init-param: root-path ");
		}
		if(configRootPathFolder != null){
			if(!configRootPathFolder.isAbsolute()){
				//在项目根目录创建子目录
				int n = 0;
				if(configRootPath.charAt(0) == '/'){
					n = 1;
				}
				ROOT_PATH += configRootPath.substring(n);
				configRootPathFolder = new File(ROOT_PATH);
			}
			if(!configRootPathFolder.exists()){
				configRootPathFolder.mkdir();
			}
			ROOT_PATH = configRootPathFolder.getPath();
		}
		APP_ROOT = application.getContextPath();
		if (ROOT_PATH != null) {
			ROOT_PATH = ROOT_PATH.replaceAll("\\\\", "/");
			if (!ROOT_PATH.endsWith("/")) ROOT_PATH += "/";
			ROOT_PATH = ROOT_PATH.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
		}
		DFX_SAVE = "/com/raqsoft/guide/web/dfx/save.dfx";
		DFX_READ = "/com/raqsoft/guide/web/dfx/read.dfx";
		DFX_REPORT = "/com/raqsoft/guide/web/dfx/report.dfx";
		DFX_SAVE_TXT = "/com/raqsoft/guide/web/dfx/save_txt.dfx";
		DFX_READ_TXT = "/com/raqsoft/guide/web/dfx/read_txt.dfx";
		DFX_REPORT_TXT = "/com/raqsoft/guide/web/dfx/report_txt.dfx";

		//Logger.setLevel(Level.DEBUG);

//		String dataSetConfig = config.getInitParameter( "dataSetConfig" );
//		if ( dataSetConfig != null && dataSetConfig.trim().length() > 0 ) {
//			DATASET_CONFIG = dataSetConfig;
//		} else DATASET_CONFIG = "/WEB-INF/dataSet/dataSet.conf";
//		DATASET_CONFIG = ROOT_PATH + "/" + DATASET_CONFIG;
//
//		String dataSetFileHome = config.getInitParameter( "dataSetFileHome" );
//		if ( dataSetFileHome != null && dataSetFileHome.trim().length() > 0 ) {
//			DATASET_FILE_HOME = dataSetFileHome;
//		} else DATASET_FILE_HOME = "/WEB-INF/dataSet/";
//		DATASET_FILE_HOME = ROOT_PATH + "/" + DATASET_FILE_HOME;
//		File p = new File(DATASET_FILE_HOME);
//		if (!p.exists()) p.mkdirs();
//		DfxDataSetManager.load();		

		Consts.dataSphereHome = application.getRealPath( "/WEB-INF/qyx/" );
		if (Context.getJspCharset() == null && !Context.getJspCharset().equalsIgnoreCase(Env.getDefaultCharsetName())){
			Logger.debug("getJspCharset : " + Context.getJspCharset());
			Logger.debug("Env   Charset : " + Env.getDefaultCharsetName());
			Logger.warn("Report and DFX' Charactor set are not same, may cause messy code.");
		}
		
//		String home = config.getInitParameter( "dataSphereHome" );
//		if ( home != null && home.trim().length() > 0 ) {
//			home = StringUtils.replace( home, "\\", "/" );
//			File tmpFile = new File( home );
//			if ( !tmpFile.exists() ) {
//				if ( !home.startsWith( "/" ) ) {
//					home = "/" + home;
//				}
//				home = application.getRealPath( home );
//				home = StringUtils.replace( home, "\\", "/" );
//			}
//			if ( !home.endsWith( "/" ) ) {
//				home += "/";
//			}
//			Consts.dataSphereHome = home;
//		}
//		else {
//			throw new Exception( "Not setup dataSphereHome." );
//		}
//		String um = config.getInitParameter( "userManager" );
//		//userManager = (IUserManager) Class.forName( um ).newInstance();
//		dbManager = DBManager.newInstance();
//		String times = config.getInitParameter( "tryTimes" );
//		if ( times != null && times.trim().length() > 0 ) {
//			try {Consts.tryTimes = Integer.parseInt( times );
//			}
//			catch ( Exception e ) {}
//		}
//		String tryInterval = config.getInitParameter( "tryInterval" );
//		if ( tryInterval != null && tryInterval.trim().length() > 0 ) {
//			try {Consts.tryInterval = Integer.parseInt( tryInterval );
//			}
//			catch ( Exception e ) {}
//		}
//		String counts = config.getInitParameter( "rowsPerPage" );
//		if ( counts != null && counts.trim().length() > 0 ) {
//			try{ ActionSearch.rowsPerPage = Integer.parseInt( counts );}catch( Exception e ) {};
//		}
//		String cachePage = config.getInitParameter( "cachePage" );
//		if ( cachePage != null && cachePage.trim().length() > 0 ) {
//			try{ ResultPage.CACHE_NUM = Integer.parseInt( cachePage ) + 1;}catch( Exception e ) {};
//		}
//		String mer = config.getInitParameter( "maxExportRow" );
//		if ( mer != null && mer.trim().length() > 0 ) {
//			try{ maxExportRow = Integer.parseInt( mer );}catch( Exception e ) {};
//		}
//		String charset = config.getInitParameter( "qyxCharset" );
//		if ( charset != null && charset.trim().length() > 0 ) {
//			qyxCharset = charset.trim();
//		}
//		String styleDefine = config.getInitParameter( "styleDefine" );
//		if ( styleDefine != null && styleDefine.trim().length() > 0 ) {
//			ReportStyle.setStyles(styleDefine.trim());
//		} else 
		ReportStyle.setStyles("/WEB-INF/style.rpx");
//		String rpxTemplates = config.getInitParameter( "rpxTemplates" );
//		if ( rpxTemplates != null && rpxTemplates.trim().length() > 0 ) {
//			ReportStyle.setTemplates(rpxTemplates.trim());
//		}
		
//		String guideLicense = config.getInitParameter( "guideLicense" );
//		System.out.println("guideLicense : " + guideLicense);
//		if( guideLicense != null && guideLicense.trim().length() > 0 ) {
//			InputStream lis = null;
//			try {
//				if( ! ( new File( guideLicense ).exists() ) ) {
//					if ( guideLicense.startsWith( "/" ) ) lis = application.getResourceAsStream( guideLicense );
//					else {
//						try{
//							lis = Thread.currentThread().getContextClassLoader().getResourceAsStream( guideLicense );
//						}catch( Throwable th ) {}
//						if( lis == null ) {
//							lis = ReportServlet.class.getResourceAsStream( guideLicense );
//						}
//					}
//					
//					System.out.println("guideLicense2 : " + lis);
//				}
//				else {
//					lis = new FileInputStream( guideLicense );
//					System.out.println("guideLicense3 : " + lis);
//				}
//				if (lis == null) throw new Exception( "未找到超维报表授权" );
//				Sequence.readLicense( Sequence.P_GUIDE, lis );
//			}
//			finally {
//				if( lis != null ) lis.close();
//			}
//			Sequence.checkExpiration( Sequence.P_GUIDE );
//		} else {
//			throw new Exception( "未设置超维报表授权" );   //没有设置授权文件
//		}

		
//		try {
//			Sequence.checkExpiration( Sequence.P_RPT );
//		} catch (Exception e) {
//			InputStream lis = null;
//			try {
//				lis = DataSphereServlet.class.getClassLoader().getResourceAsStream( "com/raqsoft/guide/web/zhima" );
//				if (lis != null) Sequence.readLicense( Sequence.P_RPT, lis );
//			} catch (Exception e2) {
//				e2.printStackTrace();
//			} finally {
//				try {
//					if( lis != null ) lis.close();
//				} catch (IOException e1) {
//				}
//			}
//		}
//		try {
//			Sequence.checkExpiration( Sequence.P_PROGRAM );
//		} catch (Exception e) {
//			InputStream lis = null;
//			try {
//				lis = DataSphereServlet.class.getClassLoader().getResourceAsStream( "com/raqsoft/guide/web/kaimen" );
//				if (lis != null) Sequence.readLicense( Sequence.P_PROGRAM, lis );
//			} catch (Exception e2) {
//				e2.printStackTrace();
//			} finally {
//				try {
//					if( lis != null ) lis.close();
//				} catch (IOException e1) {
//				}
//			}
//		}
		
//		processJDBCDsConfig( config.getReportConfigModel(), application );
//		processJNDIDsConfig( config.getReportConfigModel(), application );

	}


	private static boolean s2Boolean( String s ) {
		if ( s == null ) {
			return false;
		}
		if ( s.equals( "1" ) ) {
			return true;
		}
		else if ( s.equals( "0" ) ) {
			return false;
		}
		else if ( s.equalsIgnoreCase( "true" ) ) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean binAuth = true;
	public static String getFilePath(String file) {
		String s;
		try {
			if ( ROOT_PATH != null ) {
				
				ROOT_PATH = ROOT_PATH.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
				File f1 = new File(ROOT_PATH);
				ROOT_PATH = f1.getCanonicalPath().replaceAll("\\\\", "/");
				if (!ROOT_PATH.endsWith("/")) ROOT_PATH = ROOT_PATH + "/";
				//修正主路径
//				if( ROOT != null && !ROOT.equals(mainPath)){
//					Env.setMainPath(ROOT);
//				}
			}
			File f = null;
			//Logger.debug(p + " : " +file);
			if (file != null && file.length()>0) f= new File(ROOT_PATH, file);
			else f = new File(ROOT_PATH);
			s = f.getCanonicalPath().replaceAll("\\\\", "/");
		} catch (IOException e) {
			if (file.length()<200) Logger.warn("getFilePath fail : " + ROOT_PATH+" : "+file);
			//e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		//Logger.debug(s);
		return s;
	}

	/**
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void service( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		PrintWriter pw = null;
		try {
			request.setCharacterEncoding( "UTF-8" );
			String tmp = request.getParameter( "action" );
			int action = 1;
			try {
				action = Integer.parseInt( tmp );
			}
			catch ( Throwable t ) {}
			HttpSession session = request.getSession();
			if( action == ACTION_DOWNLOAD ) {
				String filePathParam = request.getParameter("path");
				String content = request.getParameter("content");
				String mode = request.getParameter("mode");
				String ua = request.getHeader( "user-agent" ).toLowerCase();
				boolean notFF = !(ua.indexOf( "firefox" ) > 0) && !"mozilla/5.0 (windows nt 10.0; win64; x64; rv:64.0) gecko/20100101 firefox/64.0".equals(ua);
				if (content != null && content.trim().length()>0) ( new DownloadFileServlet() ).service( request, response, filePathParam, content, mode );
				else ( new DownloadFileServlet() ).downLoad(filePathParam, response, false, notFF);
				return;
			}
			if( action == ACTION_READGEX ) {
			}
			if( action == ACTION_DOWNLOAD_QYX ) {
				( new DownloadFileServlet() ).service( request, response, request.getParameter("path"), request.getParameter("dlConf"), "client" );
				return;
			}
			response.setContentType( "text/html; charset=UTF-8" );
			pw = response.getWriter();
			String path = request.getParameter( "path" );
			if( path != null && path.indexOf( ".." ) >= 0 ) throw new Exception( "file path can not be allowed “" + path + "”." );
			FileManager fm = new FileManager("tmp");
			switch ( action ) {
				case ACTION_INIT_DBD:
//					DBDAction da = new DBDAction();
//					da.init();
				break;
				case ACTION_UPLOAD:
					try {
						Upload upload = new Upload( this.getServletConfig(), request, response );
						String upName = upload.getFileName( 0 );
						path = upload.getParameter( "path" );
						String pathName = upName;//path + "/" + upName;
						File upFile = new File( Consts.getAbsPath( "tmp",pathName ) );
						if( upFile.exists() ) {
							upFile.delete();
//							session.setAttribute( "up_replace_", pathName ); //向客户端发问是否覆盖文件pathName
//							while ( true ) {
//								String sel = ( String ) session.getAttribute( "up_answer_" );
//								if ( sel != null ) {
//									session.removeAttribute( "up_answer_" );
//									if ( "0".equals( sel ) ) return; //不覆盖
//									else break;
//								}
//								try {Thread.currentThread().sleep( 500 );
//								}
//								catch ( Throwable e ) {}
//							}
						}
						fm.write( pathName, Consts.getStreamBytes( upload.getByteArrayInputStream( 0 ) ) );
						pw.println( "<script language=javascript>" );
						//pw.println( "alert('文件已上载！');" );
						pw.println( "parent.tree_setCurrNode( parent.currNode );" );
						pw.println( "</script>" );
					}
					catch( Throwable t ) {
						pw.println( "<script language=javascript>" );
						pw.println( "alert( " + Escape.addEscAndQuote( t.getMessage() ) + " );" );
						pw.println( "</script>" );
						t.printStackTrace();
					}
					break;
				case ACTION_UPLOAD_GRPX:
					try {
						Upload upload = new Upload( this.getServletConfig(), request, response );
						String s = new String(Consts.getStreamBytes( upload.getByteArrayInputStream( 0 ) ), Context.getJspCharset());
						pw.println( "<script language=javascript>" );
						//pw.println( "alert('文件已上载！');" );
						pw.println( "parent.openGrpxCallback(\""+s+"\");" );
						pw.println( "</script>" );
					}
					catch( Throwable t ) {
						pw.println( "<script language=javascript>" );
						pw.println( "alert( " + Escape.addEscAndQuote( t.getMessage() ) + " );" );
						pw.println( "</script>" );
						t.printStackTrace();
					}
					break;
				case ACTION_UPLOAD_FILE:
					try {
						Upload upload = new Upload( this.getServletConfig(), request, response );
						String upName = upload.getFileName( 0 );
						path = upload.getParameter( "path" );
						boolean saveServer = "1".equals(upload.getParameter( "saveServer" ));
						//Logger.debug(upName);
						//Logger.debug(path);
						String s =null;
						//String pathName = upName;//path + "/" + upName;
						String abPath = getFilePath(path+"/"+upName );
						File upFile = new File( abPath );
						if( upFile.exists() ) {
							upFile.delete();
						}
						fm.write( abPath, upload.getByteArrayInputStream( 0 )/*s.getBytes(Context.getJspCharset())*/ );
						if (!saveServer) s = SaveUtil.readFile(new File(abPath));//new String(Consts.getStreamBytes( upload.getByteArrayInputStream( 0 ) ),Context.getJspCharset());
						pw.println( "<script language=javascript>" );
						//pw.println( "alert('文件已上载！');" );
						pw.println( "parent.openFileCallback("+(saveServer?"null":"\""+s+"\"")+",\""+path+"/"+upName+"\");" );
						pw.println( "</script>" );
					}
					catch( Throwable t ) {
						pw.println( "<script language=javascript>" );
						pw.println( "alert( " + Escape.addEscAndQuote( t.getMessage() ) + " );" );
						pw.println( "</script>" );
						t.printStackTrace();
					}
					break;
				case ACTION_ASKREPLACE:
					path = ( String ) session.getAttribute( "_replace_" );
					if( path != null ) {
						session.removeAttribute( "_replace_" );
						pw.print( path );
					}
					else pw.print( "no" );
					break;
				case ACTION_ANSWER:
					String answer = request.getParameter( "answer" );
					if( "all".equals( answer ) ) {
						session.setAttribute( "_replaceAll_", "1" );
						answer = "1";
					}
					session.setAttribute( "_answer_", answer );
					break;
				case ACTION_ASKUPREPLACE:
					path = ( String ) session.getAttribute( "up_replace_" );
					if( path != null ) {
						session.removeAttribute( "up_replace_" );
						pw.print( path );
					}
					else pw.print( "no" );
					break;
				case ACTION_ANSWERUP:
					answer = request.getParameter( "answer" );
					session.setAttribute( "up_answer_", answer );
					break;
				case ACTION_DBD_FOLDERINFO:
					String dbdPath = request.getRealPath("/")+"WEB-INF/files/dbd/";
					JSONObject jdbd = new JSONObject("{folders:{},files:[]}");
					JSONObject jfolders = jdbd.getJSONObject("folders");// new JSONObject();
					JSONArray jfiles = jdbd.getJSONArray("files");//new JSONArray();
					//jdbd.append("folders", jfolders);
					//jdbd.append("files", jfiles);
					File dbdFolder = new File(dbdPath);
					int level = 0;
					HashMap map = new HashMap();
					map.put("folderId", 0);
					map.put("fileId", 0);
					if(!dbdFolder.exists() || dbdFolder.listFiles().length == 0 || !dbdFolder.isDirectory()) {
						if(!dbdFolder.exists()){
							dbdFolder.mkdir();
						}
					}
					JSONArray folder0 = new JSONArray();
					jfolders.put("level"+level,folder0);
					JSONObject folder0_ = new JSONObject();
					folder0.put(folder0_);
					folder0_.put("id", 0);
					folder0_.put("name", "dbd");
					folder0_.put("belong", "-1");
					folder0_.put("files", new JSONArray());
					level = 1;
					this.listFiles(level, dbdFolder, 0, jfolders, jfiles, map);
					pw.println(jdbd.toString());
					break;
				case ACTION_DBD_FINALVIEW:
					request.getSession().setAttribute("dbd", URLDecoder.decode(request.getParameter("dbd"),"UTF-8"));
					String ua = request.getParameter("ua");
					String currFolderId = request.getParameter("currFolderId");
					String currLevel = request.getParameter("currLevel");
					String version = request.getParameter("version");
					if(version == null) version = "";
					else if (version.equals("2")) version = "2.0";
					else version = "";
					if(ua == null) response.sendRedirect(request.getContextPath()+"/raqsoft/guide/dbd"+version+"/jsp/finalView.jsp?currFolderId="+currFolderId+"&currLevel="+currLevel);
					else if(ua.equals("mobile")) response.sendRedirect(request.getContextPath()+"/raqsoft/guide/dbd"+version+"/jsp/finalView-mobile.jsp?currFolderId="+currFolderId+"&currLevel="+currLevel);
					break;
				case ACTION_DBD_REMOVEDBDFILE:
					String filePath = request.getParameter("file");
					filePath = getFilePath(filePath);
					File f = new File(filePath);
					if(filePath.indexOf("WEB-INF/files/dbd") < 0 ){//删除限定目录
						throw new Exception("wrong path");
					}
					if(f.isDirectory()){
						/*File[] files_folders = f.listFiles();
						for(File file: files_folders){
							if(file.isDirectory()){
								if(deleteInnerFile(file)) continue;
								else{
									success = false;
								}
							}else{
								if(deleteFile(file)) continue;
								else{
									success = false;
								}
							}
						}*/
						if(deleteFolderAndInnerFile(f)){
							pw.append("ok");
							pw.flush();
						}
					} else if(deleteFile(f)){
						pw.append("ok");
						pw.flush();
					}
					break;
				case ACTION_DBD_PASTEDBDFILE:
					String copyfilePath = request.getParameter("file");
					String dest = request.getParameter("toPath");
					copyfilePath = getFilePath(copyfilePath);
					File cf = new File(copyfilePath);
					if(copyfilePath.indexOf("WEB-INF/files/dbd") < 0 ){//删除限定目录
						throw new Exception("wrong path");
					}
					File newFile = new File(getFilePath(dest)+"/"+cf.getName());
					if(!cf.isDirectory() && cf.canRead() && !newFile.exists()){
						copyFileUsingFileStreams(cf,newFile);
						cf.delete();
						if(!cf.exists() && newFile.exists()){
							pw.append("ok");
							pw.flush();
						}
					}
					break;
			}
			
		}
		catch ( Throwable t ) {
			if ( pw != null ) {
				pw.println( "error:" + t.getMessage() );
			}
			t.printStackTrace();
		}
		finally {
			try {pw.flush();pw.close();
			}
			catch ( Exception e ) {}
		}
	}

	private List string2list( String s ) {
		List list = new ArrayList();
		ArgumentTokenizer st = new ArgumentTokenizer( s, ',' );
		while( st.hasMoreTokens() ) list.add( st.nextToken() );
		return list;
	}

	public static String getUserName( HttpSession session ) {
		String userName = ( String ) session.getAttribute( "datasphere_username" );
		return userName;
	}
	
	private void listFiles(int level, File topfolder, int topfolderId, JSONObject jfolders, JSONArray jfiles, HashMap<String,Integer> map) throws JSONException{
		//level从1开始
		try{
			jfolders.getJSONArray("level"+level);
		}catch(Exception e){
			jfolders.put("level"+level,new JSONArray());
		}
		JSONArray folderLeveln = jfolders.getJSONArray("level"+level);//1
		File[] files = topfolder.listFiles();//0
		Integer currFolderId = map.get("folderId");
		for(File file: files){
			if(file.isDirectory()){//1
				JSONObject folder = new JSONObject();
				folder.put("id", map.get("folderId")+1);
				map.put("folderId", map.get("folderId")+1);
				folder.put("name", file.getName());
				folder.put("belong", topfolderId);
				folder.put("files", new JSONArray());
				folderLeveln.put(folder);
				this.listFiles(level + 1, file, map.get("folderId"), jfolders,  jfiles, map );
			}else{
				JSONObject jfile = new JSONObject();
				jfile.put("id", map.get("fileId"));
				jfile.put("name", file.getName());
				jfiles.put(jfile);
				JSONArray foldersl = jfolders.getJSONArray("level"+(level-1));
				for(int i = 0; i < foldersl.length(); i++){
					JSONObject foldersn = foldersl.getJSONObject(i);
					if(currFolderId==foldersn.getInt("id")) {
						JSONArray ofiles = (JSONArray) foldersn.get("files");
						ofiles.put(map.get("fileId"));
					}
				}
				map.put("fileId",map.get("fileId")+1);
			}
		}
	}
	
	private boolean deleteFolderAndInnerFile(File folder){
		boolean success = true;
		File[] files = folder.listFiles();
		for(File file: files){
			if(file.isDirectory()){
				if(deleteFolderAndInnerFile(file)) continue;
				else{
					success = false;
				}
			}else{
				if(deleteFile(file)) continue;
				else{
					success = false;
				}
			}
		}
		if(!folder.delete()) success = false;
		return success;
	}
	
	private boolean deleteFile(File f){
		if(f.exists() && f.canWrite()){
			f.delete();
			if(!f.exists()){
				return true;
			}
		}else{
			return false;
		}
		return false;
	}
	
	private void copyFileUsingFileStreams(File source, File dest)
	        throws IOException {    
		FileInputStream input = null;    
		FileOutputStream output = null;    
	    try {
	           input = new FileInputStream(source);
	           output = new FileOutputStream(dest);        
	           byte[] buf = new byte[1024];        
	           int bytesRead;        
	           while ((bytesRead = input.read(buf)) > 0) {
	               output.write(buf, 0, bytesRead);
	           }
	    } finally {
	        input.close();
	        output.close();
	    }
	}
}