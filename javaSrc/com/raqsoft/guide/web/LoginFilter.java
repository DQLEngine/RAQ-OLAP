package com.raqsoft.guide.web;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class LoginFilter implements Filter {

	private FilterConfig config = null;

	public void init( FilterConfig config ) throws ServletException {
		this.config = config;
	}

	public void destroy() {
		config = null;
	}

	public void doFilter( ServletRequest request, ServletResponse response,
						  FilterChain chain ) throws IOException, ServletException {
		if ( request instanceof HttpServletRequest ) {
			String uri = ( ( HttpServletRequest ) request ).getRequestURI();
			if( ! ( uri.endsWith( "index.jsp" ) || uri.endsWith( "login.jsp" ) || uri.indexOf( "viewShare.jsp" ) > 0 ) ) {
				HttpSession session = ( ( HttpServletRequest ) request ).getSession();
				if ( session.getAttribute( "datasphere_username" ) == null ) {
					request.getRequestDispatcher( "/index.jsp" ).forward( request, response );
					return;
				}
			}
		}
		chain.doFilter( request, response );
	}

}
