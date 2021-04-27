package com.raqsoft.guide.web;

import com.raqsoft.common.Logger;
import com.raqsoft.guide.*;
import java.util.*;
import java.text.*;

public class FolderDetail {

	private String path;
	private String user;
	private String appmap;
	private String sortCol;
	private int sort;
	private String col1, col2, col3, col4;

	public FolderDetail( String user, String path, String appmap, String sortCol, int sort, String c1, String c2, String c3, String c4 ) {
		this.user = user;
		this.path = path;
		this.appmap = appmap;
		this.sortCol = sortCol;
		this.sort = sort;
		this.col1 = c1;
		this.col2 = c2;
		this.col3 = c3;
		this.col4 = c4;
	}

	public String generateHtml() {
		StringBuffer sb = new StringBuffer();
		sb.append( "<table cellspacing=0 cellpadding=3 style=\"table-layout:fixed;width:100%\" class=detailTable>\n" );
		sb.append( "<colgroup>\n" );
		sb.append( "\t<col style=\"width:18px\"></col>\n" );
		sb.append( "\t<col id=cg1 style=\"width:" + col1 + "\"></col>\n" );
		sb.append( "\t<col id=cg2 style=\"width:" + col2 + "\"></col>\n" );
		sb.append( "\t<col id=cg3 style=\"width:" + col3 + "\"></col>\n" );
		sb.append( "\t<col id=cg4 style=\"width:" + col4 + "\"></col>\n" );
		sb.append( "</colgroup>\n" );
		FileManager fm = new FileManager( user );
		List list = fm.listAll( path );
		FileInfo[] fis = new FileInfo[ list.size() ];
		int iSortCol = Integer.parseInt( sortCol );
		for( int i = 0; i < list.size(); i++ ) {
			FileInfo fi = ( FileInfo ) list.get( i );
			fi.sortBy = iSortCol;
			fis[i] = fi;
		}
		Arrays.sort( fis );
		list.clear();
		if( sort == 1 ) {
			for( int i = fis.length; i > 0; i-- ) {
				list.add( fis[ i - 1 ] );
			}
		}
		else {
			for ( int i = 0; i < fis.length; i++ ) {
				list.add( fis[i] );
			}
		}
		ArrayList al = new ArrayList();
		for( int i = 0; i < list.size(); i++ ) {
			FileInfo fi = ( FileInfo ) list.get( i );
			if( !fi.isFile() ) al.add( fi );
		}
		for( int i = 0; i < list.size(); i++ ) {
			FileInfo fi = ( FileInfo ) list.get( i );
			if( fi.isFile() ) {
				if( fi.getFileName().toLowerCase().endsWith( ".qyx" ) )	al.add( fi );
			}
		}
		HashMap map = null;
		try {
			map = FileNotes.getFileNotes( user );
		}catch( Exception e ) {
			map = new HashMap();
			Logger.error("",e);
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		for( int i = 0; i < al.size(); i++ ) {
			FileInfo fi = ( FileInfo ) al.get( i );
			sb.append( "\t<tr height=22>\n" );
			String img = "file.png";
			if( !fi.isFile() ) {
				img = "folder.png";
				if( fi.isShared() ) img = "sharefolder.png";
			}
			else {
				if( fi.isShared() ) img = "sharefile.png";
			}
			sb.append( "\t\t<td><img id=\"fileImg_" + i + "\" src=\"" + appmap + "/images/tree/" + img + "\" border=no></td>\n" );
			String dispName = fi.getFileName();
			if( fi.isFile() ) {
				dispName = dispName.substring( 0, dispName.lastIndexOf( "." ) );
			}
			sb.append( "\t\t<td id=\"fileName_" + i + "\" isFile=\"" + fi.isFile() + "\" onclick=\"tree_fileClicked(event)\"" +
					   " ondblclick=\"tree_fileDblClicked(event)\" path=\"" + fi.getPath() + "\" isShare=\"" + fi.isShared() + "\">" + dispName );
			sb.append( "</td>\n" );
			sb.append( "\t\t<td align=right>" );
			int kb = (int)( fi.length() / 1024 );
			if( kb == 0 && fi.length() > 0 ) kb = 1;
			if( fi.isFile() ) sb.append( kb + " KB" );
			sb.append( "</td>\n" );
			sb.append( "\t\t<td align=center>" + sdf.format( new Date( fi.lastModified() ) ) + "</td>\n" );
			String note = ( String ) map.get( fi.getPath() );
			if( note == null ) note = "";
			sb.append( "\t\t<td align=left>" + note + "</td>\n" );
			sb.append( "</tr>\n" );
		}
		sb.append( "</table>\n" );
		return sb.toString();
	}

}
