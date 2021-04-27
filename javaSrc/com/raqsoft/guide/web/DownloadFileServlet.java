package com.raqsoft.guide.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raqsoft.common.Escape;
import com.raqsoft.common.Logger;
import com.raqsoft.guide.web.dl.FileUtils;
import com.raqsoft.report.usermodel.Context;

//2021.2.7 ������olap�����ļ�����������������Ӧ����DownloadFile...SaveAndDownload Save��������ʱ���漰�����ɡ������ļ�
//servlet/dataSphereServlet?action=11&path=index.html&content=1&mode=server,client�ɴ۸���ҳ��ʵ���������ļ��ɱ��۸ĺ����
//ͨ�����ڱ����ļ���ָ��·�������������������ӡ����ɡ����أ�1�������ɡ�·������Դ·���𣬰������Ͷ�λ���涨Ŀ¼�£�2��ָ�����Ͱ�������3�������������ļ�
public class DownloadFileServlet {
	public void service( HttpServletRequest request, HttpServletResponse response, String originFilePath, String content, String mode ) throws ServletException {
		ServletOutputStream os = null;
		String serverPath = DataSphereServlet.getFilePath(originFilePath);
		File file =new File(serverPath);
		String fileName = file.getName();
		String fileType = fileName.indexOf('.') >= 0 ? fileName.substring(fileName.indexOf('.')+1) : null;
		try {
			if("olap".equals(fileType)) {
				serverPath = DataSphereServlet.getFilePath(DataSphereServlet.olapFolderOnServer + originFilePath);
			}else if("qyx".equals(fileType)) {
				serverPath = DataSphereServlet.getFilePath(DataSphereServlet.qyxFolderOnServer + originFilePath);
			}else if("dbd".equals(fileType)) {
				serverPath = DataSphereServlet.getFilePath(DataSphereServlet.dbdFolderOnServer + originFilePath);
			}else {
				throw new Exception("wrong file type:"+fileType);
			}
			//if( serverPath.indexOf( ".." ) >= 0 ) throw new Exception( "�ļ�·����" + serverPath + "�����������!" );
			if (mode.indexOf("server")>=0) {
				FileUtils.saveFile(content, file);
			}
			if (mode.indexOf("client")>=0) {
				fileName = new String( fileName.getBytes(), "iso-8859-1" );
				response.setHeader( "Pragma", "public" );
				response.setHeader( "Cache-Control", "must-revalidate, post-check=0, pre-check=0" );
				byte[] b = null;
				b = content.getBytes(Context.getJspCharset());
				try {
					response.setContentType( "application/x-msdownload" );
					response.setHeader( "Content-Disposition", "attachment; filename=" + fileName );
					os = response.getOutputStream();
					os.write( b );
					os.flush();
				}
				catch ( Exception e ) {}
				finally {
					try {os.close();}catch ( Exception e ) {}
				}
			} else {
				if(serverPath.indexOf("temp") < 0){
					try {
						response.setContentType( "text/html;charset=UTF-8" );
						PrintWriter pw = response.getWriter();
						pw.println( "<html><body>" );
						pw.println( "<script language=javascript>" );
						pw.println( "alert( \"" + Escape.add( "save success" ) + "\" );" );
						pw.println( "</script>" );
						pw.println( "</body></html>" );
					}
					catch ( Exception ex ) {}
				}
			} 
		}
		catch ( java.net.SocketException se ) {}
		catch ( Throwable e ) {
			String msg = e.getMessage();
			if ( msg == null ) {
				return;
			}
			msg = msg.toLowerCase();
			if ( msg.indexOf( "socket write error" ) < 0 ) {
				Logger.error( "Error��", e );
				try {
					response.setContentType( "text/html;charset=UTF-8" );
					PrintWriter pw = response.getWriter();
					pw.println( "<html><body>" );
					pw.println( "<script language=javascript>" );
					pw.println( "alert( \"" + Escape.add( msg ) + "\" );" );
					pw.println( "</script>" );
					pw.println( "</body></html>" );
				}
				catch ( Exception ex ) {}
			}
		}
	}
	
	public void downLoad(String filePath, HttpServletResponse response, boolean isOnLine, boolean notFF) throws Exception {
		try {
			filePath = filePath.replaceAll("////", "/").replaceAll("///", "/").replaceAll("//", "/");
	        File f = new File(filePath);
	        if (!f.exists()) {
	        	throw new Exception( "file not exist! ��" + filePath + "��" );
	            //response.sendError(404, "File not found!");
	            //return;
	        }
	        BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
	        byte[] buf = new byte[1024];
	        int len = 0;
	        
	        String fileName = new String( f.getName().getBytes(), "iso-8859-1" );
	        response.setHeader( "Pragma", "public" );
			response.setHeader( "Cache-Control", "must-revalidate, post-check=0, pre-check=0" );
	        response.reset(); // �ǳ���Ҫ
	        if (isOnLine) { // ���ߴ򿪷�ʽ
	            URL u = new URL("file:///" + filePath);
	            response.setContentType(u.openConnection().getContentType());
	            response.setHeader("Content-Disposition", "inline; filename=" + fileName);
	            // �ļ���Ӧ�ñ����UTF-8
	        } else { // �����ط�ʽ
	            response.setContentType("application/octet-stream");
	            if( notFF ) {
					//��ʵ���������Ҳ�������ֱ��뷽ʽ�������˼���ֻ�л������
	            	response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
				}else{
					String suffix = fileName.substring(fileName.lastIndexOf('.'));
					fileName = fileName.substring(0,fileName.lastIndexOf('.'));
					fileName = fileName.replace('+', ' ');
					response.setHeader("Content-Disposition", "attachment; filename*=\"UTF8''" + fileName + suffix +"\"");
				}
	        }
	        OutputStream out = response.getOutputStream();
	        while ((len = br.read(buf)) > 0)
	            out.write(buf, 0, len);
	        out.flush();
	        br.close();
	        out.close();
		}
		catch ( java.net.SocketException se ) {}
		catch ( Throwable e ) {
			String msg = e.getMessage();
			if ( msg == null ) {
				return;
			}
			msg = msg.toLowerCase();
			if ( msg.indexOf( "socket write error" ) < 0 ) {
				Logger.error( "Error��", e );
				try {
					response.setContentType( "text/html;charset=UTF-8" );
					PrintWriter pw = response.getWriter();
					pw.println( "<html><body>" );
					pw.println( "<script language=javascript>" );
					pw.println( "alert( \"" + Escape.add( msg ) + "\" );" );
					pw.println( "</script>" );
					pw.println( "</body></html>" );
				}
				catch ( Exception ex ) {}
			}
		}
    }

}
