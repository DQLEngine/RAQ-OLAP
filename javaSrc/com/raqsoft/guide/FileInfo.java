package com.raqsoft.guide;

import java.io.*;
import java.util.*;
import com.ibm.icu.text.*;

public class FileInfo implements Externalizable, Comparable {
	private final static long serialVersionUID = 82857881736578L;
	private static Collator collator = Collator.getInstance(Locale.CHINA);

	private String user;
	private String path;
	private String name;
	private long len;
	private boolean isFile;
	private long lastModified;
	private boolean isShared;
	public int sortBy = 1;

	public FileInfo() {}

	protected void setProp( String user, String absName ) {
		this.user = user;
		this.path = Consts.getRelativePath( user, absName );
		File file = new File( absName );
		this.name = file.getName();
		if( file.exists() ) {
			len = file.length();
			isFile = file.isFile();
			lastModified = file.lastModified();
		}
		File f = new File( Consts.getAbsSharedPath( user, path ) );
		isShared = f.exists();
	}

	public String getUser() {
		return user;
	}

	public String getPath() {
		return path;
	}

	public String getFileName() {
		return name;
	}

	public long length() {
		return len;
	}

	public boolean isFile() {
		return isFile;
	}

	public long lastModified() {
		return lastModified;
	}

	public boolean isShared() {
		return isShared;
	}

	public String getSharedPwd() throws Exception {
		String fileName = Consts.getAbsSharedPath( user, name );
		BufferedReader br = null;
		try {
			for( int i = 1; i <= Consts.tryTimes; i++ ) {
				try {
					br = new BufferedReader( new InputStreamReader( new FileInputStream( fileName ) ) );
					String pwd = br.readLine();
					return pwd;
				}
				catch( Exception e ) {
					try{ br.close(); }catch( Exception ex ) {}
					if( i == Consts.tryTimes ) throw e;
					try{ Thread.currentThread().sleep( Consts.tryInterval ); }catch( Exception ex ) {}
				}
			}
		}
		finally {
			try{ br.close(); }catch( Exception ex ) {}
		}
		return "";
	}

	public void writeExternal( ObjectOutput out ) throws IOException{
		out.writeObject( user );
		out.writeObject( path );
		out.writeObject( name );
		out.writeLong( len );
		out.writeBoolean( isFile );
		out.writeLong( lastModified );
		out.writeBoolean( isShared );
	}

	public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException{
		user = ( String ) in.readObject();
		path = ( String ) in.readObject();
		name = ( String ) in.readObject();
		len = in.readLong();
		isFile = in.readBoolean();
		lastModified = in.readLong();
		isShared = in.readBoolean();
	}

	/**
	 * compareTo
	 *
	 * @param o Object
	 * @return int
	 */
	public int compareTo( Object o ) {
		if( sortBy == 1 ) {
			return collator.compare( name, ( ( FileInfo )o ).getFileName() );
		}
		if( sortBy == 3 ) {
			//return ( new Date( lastModified ) ).compareTo( new Date( ( ( FileInfo )o ).lastModified() ) );
			long lm = ( ( FileInfo )o ).lastModified();
			if( lastModified == lm ) return 0;
			if( lastModified > lm ) return 1;
			return -1;
		}
		return 0;
	}

}
