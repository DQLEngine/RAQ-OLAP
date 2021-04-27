package com.raqsoft.guide.web;

import java.util.*;
import java.io.*;
import com.raqsoft.guide.*;

public class LastSearches {

	public static ArrayList getLastSearches( String userName ) throws Exception {
		ArrayList al = new ArrayList();
		File f = new File( Consts.dataSphereHome + "/" + userName + "/lastSearch.txt" );
		if( !f.exists() ) return al;
		BufferedReader br = null;
		try {
			br = new BufferedReader( new InputStreamReader( new FileInputStream( f ), "UTF-8" ) );
			String tmp = null;
			while( ( tmp = br.readLine() ) != null ) {
				al.add( tmp );
				if( al.size() == 20 ) break;
			}
		}
		finally {
			try{ br.close(); }catch( Exception e ) {}
		}
		return al;
	}

	public static boolean addLastSearch( String userName, String word ) {
		try {
			ArrayList al = getLastSearches( userName );
			if( al.contains( word ) ) al.remove( al.indexOf( word ) );
			al.add( 0, word );
			PrintWriter pw = null;
			try {
				pw = new PrintWriter( new OutputStreamWriter( new FileOutputStream( Consts.dataSphereHome + "/" + userName + "/lastSearch.txt" ), "UTF-8" ) );
				for( int i = 0; i < al.size(); i++ ) {
					pw.println( al.get( i ) );
				}
				return true;
			}
			finally {
				try{ pw.close(); }catch( Exception e ) {}
			}
		}
		catch( Throwable t ) {
			return false;
		}
	}

	public static String toHtml( String userName ) {
		try {
			ArrayList al = getLastSearches( userName );
			StringBuffer sb = new StringBuffer();
			for( int i = 0; i < al.size(); i++ ) {
				sb.append( "<div class=lastSearch onclick=\"research(this);\" onmouseover=\"highLightHistory(this)\">" + al.get( i ) + "</div>" );
			}
			return sb.toString();
		}
		catch( Throwable t ) {
			t.printStackTrace();
			return "";
		}
	}

}
