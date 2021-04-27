package com.raqsoft.guide;

import java.io.*;

public class Directory {

	/**
	 */
	public static boolean deleteDirByFile( File file, FileManager fm ) throws Throwable {
		if ( !file.exists() ) {
			return false;
		}
		if ( file == null || !file.isDirectory() ) {
			throw new Exception( " " );
		}
		File[] child = file.listFiles();
		for ( int i = 0; i < child.length; i++ ) {
			if ( child[i].isFile() ) {
				if ( ! ( child[i].delete() ) ) {
					return false;
				}
				if( fm != null ) fm.removeFileShare( Consts.getRelativePath( fm.user, child[i].getAbsolutePath() ) );
			}
			if ( child[i].isDirectory() ) {
				if( ! deleteDirByFile( child[i], fm ) ) return false;
			}
		}
		if ( ! ( file.delete() ) ) {
			return false;
		}
		if( fm != null ) fm.removeFileShare( Consts.getRelativePath( fm.user, file.getAbsolutePath() ) );
		return true;
	}

}
