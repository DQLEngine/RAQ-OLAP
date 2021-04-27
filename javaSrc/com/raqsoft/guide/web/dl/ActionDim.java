package com.raqsoft.guide.web.dl;

import javax.servlet.http.*;

import java.io.*;

public class ActionDim {

	public void service( HttpServletRequest req, HttpServletResponse res ) {
		PrintWriter pw = null;
		try {
			res.setContentType( "text/html;charset=UTF-8" );
			String oper = req.getParameter( "oper" );
			String dbName = req.getParameter( "dbName" );
			if ("dispTable".equals(oper)) {
				pw = res.getWriter();
				pw.write(ConfigUtil.getDimDispTable(dbName, req.getParameter("sql")));
			}
		} catch ( Throwable e ) {
			pw.write("error:" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if ( pw != null ) {
					pw.close();
				}
			}
			catch ( Exception e ) {}
		}
	}

}
