package com.raqsoft.guide.web;

import com.raqsoft.guide.*;
import java.util.*;

public class FolderFiles {

	private String path;
	private String user;
	private String ext;
	private String appmap;

	public FolderFiles( String user, String path, String ext, String appmap ) {
		this.user = user;
		this.path = path;
		this.ext = ext;
		this.appmap = appmap;
	}

	public String generateHtml() {
		StringBuffer sb = new StringBuffer();
		sb.append( "<table cellspacing=0 cellpadding=3 width=100% style='font-size:12px'>\n" );
		FileManager fm = new FileManager( user );
		List list = fm.listAll( path );
		ArrayList al = new ArrayList();
		for( int i = 0; i < list.size(); i++ ) {
			FileInfo fi = ( FileInfo ) list.get( i );
			if( fi.isFile() && fi.getFileName().toLowerCase().endsWith( "." + ext ) ) al.add( fi );
		}
		for( int i = 0; i < al.size(); i++ ) {
			FileInfo fi = ( FileInfo ) al.get( i );
			String dispName = fi.getFileName();
			if( fi.isFile() ) {
				dispName = dispName.substring( 0, dispName.lastIndexOf( "." ) );
			}
			sb.append( "\t<tr height=22>\n" );
			String img = "file.png";
			sb.append( "\t\t<td width=18><img src=\"" + appmap + "/images/tree/" + img + "\" border=no></td>\n" );
			sb.append( "\t\t<td onclick=\"tree_fileClicked(event)\"" +
					   " ondblclick=\"tree_fileDblClicked(event)\" style=\"cursor:default\">" + dispName );
			sb.append( "</td>\n" );
			sb.append( "</tr>\n" );
		}
		sb.append( "</table>\n" );
		return sb.toString();
	}

}
