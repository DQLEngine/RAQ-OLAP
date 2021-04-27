package com.raqsoft.guide;

import com.raqsoft.common.*;
import java.io.*;
import java.util.*;

public class Consts {

	public static String dataSphereHome = "/WEB-INF/tmp/";

	/** 文件操作失败时重试的次数 */
	public static int tryTimes = 5;

	/** 文件操作失败时重试的时间间隔(毫秒) */
	public static int tryInterval = 1000;

	public static String getAbsPath( String user, String name ) {
		name = StringUtils.replace( name, "\\", "/" );
		if( name.startsWith( dataSphereHome ) ) return name;
		if ( !name.startsWith( "/" ) && name.length() > 0 ) {
			name = "/" + name;
		}
		return dataSphereHome + "/" + user + "/" + name;
	}

	public static String getAbsSharedPath( String user, String name ) {
		name = StringUtils.replace( name, "\\", "/" );
		if ( !name.startsWith( "/" ) ) {
			name = "/" + name;
		}
		return dataSphereHome + user + "/share" + name + ".txt";
	}

	public static String getRelativePath( String user, String absName ) {
		absName = StringUtils.replace( absName, "\\", "/" );
		return absName.substring( ( dataSphereHome + user + "/data/" ).length() );
	}

	public static byte[] getStreamBytes( InputStream is ) throws Exception {
		ArrayList al = new ArrayList();
		int totalBytes = 0;
		byte[] b = new byte[102400];
		int readBytes = 0;
		while ( ( readBytes = is.read( b ) ) > 0 ) {
			byte[] bb = new byte[readBytes];
			System.arraycopy( b, 0, bb, 0, readBytes );
			al.add( bb );
			totalBytes += readBytes;
		}
		b = new byte[totalBytes];
		int pos = 0;
		for ( int i = 0; i < al.size(); i++ ) {
			byte[] bb = ( byte[] ) al.get( i );
			System.arraycopy( bb, 0, b, pos, bb.length );
			pos += bb.length;
		}
		return b;
	}

//	public static List getAllDBNames() {
//		List list = new ArrayList();
//		Map map = Env.getDBSessionFactories();
//		if( map != null ) {
//			Iterator it = map.keySet().iterator();
//			while( it.hasNext() ) {
//				list.add( it.next() );
//			}
//		}
//		return list;
//	}

}
