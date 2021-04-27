package com.raqsoft.guide.web;

import javax.servlet.http.*;
import javax.servlet.*;

import com.raqsoft.common.Logger;
import com.raqsoft.guide.web.dl.*;

import java.util.*;

public class GuideSessionListener implements HttpSessionListener {

	public void sessionCreated( HttpSessionEvent event ) {
	}

	public void sessionDestroyed( HttpSessionEvent event ) {
		HttpSession session = event.getSession();
		//Logger.debug("session time out-----------");
		String names[] = session.getValueNames();
		for (int i=0; i<names.length; i++) {
			Object o = session.getAttribute( names[i] );
			//Logger.debug( key + "=" + o );
			if( o instanceof DfxQuery ) {
				try{ ( ( DfxQuery ) o ).removeTempFile(); }catch( Throwable tt ) {}
			}
			session.removeAttribute( names[i] );
		}
//		Enumeration em = session.getAttributeNames();
//		while( em.hasMoreElements() ) {
//			Object o1 = em.nextElement();
//			if (o1==null || !(o1 instanceof String)) continue;
//			Object o = session.getAttribute( (String)o1 );
//			//Logger.debug( key + "=" + o );
//			if( o instanceof DfxQuery ) {
//				try{ ( ( DfxQuery ) o ).removeTempFile(); }catch( Throwable tt ) {}
//			}
//			session.removeAttribute( o1.toString() );
//		}
	}

}
