package com.raqsoft.guide.web;

import java.util.*;
import java.io.*;
import com.raqsoft.guide.*;

/**
 * �û����ļ����ļ��е�ע��
 */
public class FileNotes {

	public static HashMap getFileNotes( String userName ) throws Exception {
		HashMap map = new HashMap();
		File f = new File( Consts.dataSphereHome + "/" + userName + "/fileNotes.txt" );
		if( !f.exists() ) return map;
		BufferedReader br = null;
		try {
			br = new BufferedReader( new InputStreamReader( new FileInputStream( f ), "UTF-8" ) );
			String tmp = null;
			while( ( tmp = br.readLine() ) != null ) {
				int pos = tmp.indexOf( "=" );
				if( pos < 0 ) continue;
				map.put( tmp.substring( 0, pos ).trim(), tmp.substring( pos + 1 ).trim() );
			}
		}
		finally {
			try{ br.close(); }catch( Exception e ) {}
		}
		return map;
	}

	public static void addFileNotes( String userName, String path, String note ) throws Exception {
		HashMap map = getFileNotes( userName );
		map.put( path, note );
		PrintWriter pw = null;
		try {
			pw = new PrintWriter( new OutputStreamWriter( new FileOutputStream( Consts.dataSphereHome + "/" + userName + "/fileNotes.txt" ), "UTF-8" ) );
			Iterator it = map.keySet().iterator();
			while ( it.hasNext() ) {
				String key = ( String ) it.next();
				String value = ( String ) map.get( key );
				pw.println( key + "=" + value );
			}
		}
		finally {
			pw.close();
		}
	}



}
