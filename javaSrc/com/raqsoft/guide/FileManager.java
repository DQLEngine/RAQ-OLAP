package com.raqsoft.guide;

import java.util.*;
import java.io.*;
import javax.servlet.http.*;

import com.raqsoft.common.StringUtils;

public class FileManager {

	public String user;

	public FileManager( String user ) {
		this.user = user;
	}

	public FileInfo getFileInfo( String path ) throws Exception {
		File f = new File( Consts.getAbsPath( user, path ) );
		if( !f.exists() ) {
			throw new Exception( "The File \"" + path + "\" is not exist." );
		}
		FileInfo fi = new FileInfo();
		fi.setProp( user, f.getAbsolutePath() );
		return fi;
	}

	public List listAll( String path ) {
		ArrayList al = new ArrayList();
		File f = new File( Consts.getAbsPath( user, path ) );
		if ( f.exists() ) {
			File[] fs = f.listFiles();
			for ( int i = 0; i < fs.length; i++ ) {
				FileInfo fi = new FileInfo();
				fi.setProp( user, fs[i].getAbsolutePath() );
				al.add( fi );
			}
		}
		return al;
	}

	public List listFiles( String path ) {
		ArrayList al = new ArrayList();
		File f = new File( Consts.getAbsPath( user, path ) );
		if ( f.exists() ) {
			File[] fs = f.listFiles();
			for ( int i = 0; i < fs.length; i++ ) {
				if ( fs[i].isFile() ) {
					FileInfo fi = new FileInfo();
					fi.setProp( user, fs[i].getAbsolutePath() );
					al.add( fi );
				}
			}
		}
		return al;
	}

	public List listDirs( String path ) {
		ArrayList al = new ArrayList();
		File f = new File( Consts.getAbsPath( user, path ) );
		if ( f.exists() ) {
			File[] fs = f.listFiles();
			for ( int i = 0; i < fs.length; i++ ) {
				if ( fs[i].isDirectory() ) {
					FileInfo fi = new FileInfo();
					fi.setProp( user, fs[i].getAbsolutePath() );
					al.add( fi );
				}
			}
		}
		return al;
	}

	public Map listAllShares() {
		Map shares = new HashMap();
		listShare(new File(Consts.dataSphereHome + user + "/share/"), shares);//TODO 在Consts中使用dataSphereHome
		return shares;
	}

	private void listShare(File parent, Map shares) {
		if (!parent.exists()) return;
		String root = Consts.dataSphereHome + user + "/share/";
		File[] fs = parent.listFiles();
		for (int i=0; i<fs.length; i++) {
			if (fs[i].isDirectory()) {
				listShare(fs[i], shares);
			} else {
				if (fs[i].getName().endsWith(".txt")) {
					String path = StringUtils.replace( fs[i].getPath(), "\\", "/" );
					String name = fs[i].getName();
					name = name.substring(0, name.length()-4);//去掉.txt
					path = path.substring(root.length(), path.length()-4);
					shares.put(path, name);
				}
			}
		}
	}

	public boolean mkdirs( String path ) {
		path = Consts.getAbsPath( user, path );
		File f = new File( path );
		return f.mkdirs();
	}

	public byte[] read( String path ) throws Throwable {
		InputStream is = null;
		try {
			is = getInputStream( path );
			return Consts.getStreamBytes( is );
		}
		finally {
			try{ is.close(); }catch( Exception e ) {}
		}
	}

	public InputStream getInputStream( String path ) throws Throwable {
		File f = new File( Consts.getAbsPath( user, path ) );
		if ( f.exists() ) {
			InputStream is = null;
			for ( int i = 1; i <= Consts.tryTimes; i++ ) {
				try {
					is = new FileInputStream( f );
					if ( is != null )return is;
				}
				catch ( Throwable e ) {
					if ( i == Consts.tryTimes ) {
						throw e;
					}
					try {Thread.currentThread().sleep( Consts.tryInterval );
					}
					catch ( Exception ex ) {}
				}
			}
		}
		return null;
	}

	public void write( String path, byte[] b ) throws Throwable {
		OutputStream os = null;
		try {
			os = getOutputStream( path );
			os.write( b );
			os.flush();
		}
		finally {
			try{ os.close(); }catch( Exception e ) {}
		}
	}

	public void write( String path,InputStream in ) throws Throwable {
		OutputStream os = null;
		try {
			byte[] b = new byte[in.available()];
			in.read(b);
			os = getOutputStream( path );
			os.write( b );
			os.flush();
		}
		finally {
			try{ os.close(); }catch( Exception e ) {}
		}
	}

	public OutputStream getOutputStream( String path ) throws Throwable {
		File f = new File( path );//Consts.getAbsPath( user, path )
		if( !f.exists() ) {
			f.getParentFile().mkdirs();
		}
		OutputStream os = null;
		for ( int i = 1; i <= Consts.tryTimes; i++ ) {
			try {
				os = new FileOutputStream( f );
				if ( os != null )return os;
			}
			catch ( Throwable e ) {
				if ( i == Consts.tryTimes ) {
					throw e;
				}
				try {Thread.currentThread().sleep( Consts.tryInterval );
				}
				catch ( Exception ex ) {}
			}
		}
		return null;
	}

	public void copy( String path, String toPath, boolean isMove, HttpSession session ) throws Throwable {
		String newFileName = Consts.getAbsPath( user, toPath );
		File f = new File( newFileName );
		if( f.exists() ) {
			if( ! f.isDirectory() ) throw new Exception( "The \"" + Consts.getRelativePath( user, newFileName ) + "\" is not a directory." );
		}
		else {
			f.mkdirs();
		}
		f = new File( Consts.getAbsPath( user, path ) );
		if( !f.exists() ) return;
		if(  Consts.getAbsPath( user, path ).equals( newFileName + "/" + f.getName() ) && isMove ) return;  //同目录移动
		if( f.isDirectory() ) {
			String newPath = newFileName + "/" + f.getName();
			File newFile = new File( newPath );
			if( newFile.exists() ) {
				if( !Consts.getAbsPath( user, path ).equals( newPath ) ) {
					if ( session.getAttribute( "_replaceAll_" ) == null ) { //不是全部覆盖
						String askPath = Consts.getRelativePath( user, newPath );
						session.setAttribute( "_replace_", askPath ); //向客户端发问是否覆盖文件askPath
						while ( true ) {
							String sel = ( String ) session.getAttribute( "_answer_" );
							if ( sel != null ) {
								session.removeAttribute( "_answer_" );
								if ( "0".equals( sel ) )return; //不覆盖
								else break;
							}
							try {Thread.currentThread().sleep( 500 );
							}
							catch ( Throwable e ) {}
						}
					}
				}
			}
			copyDir( f, newFileName, isMove, session );
			if( isMove ) {
				try{ f.delete(); }catch( Throwable t ) {}
			}
		}
		else {
			String newPath = newFileName + "/" + f.getName();
			File newFile = new File( newPath );
			if( newFile.exists() ) {
				if( Consts.getAbsPath( user, path ).equals( newPath ) ) {
					newPath = getFileName2( newFileName, f.getName() );
				}
				else {
					if ( session.getAttribute( "_replaceAll_" ) == null ) { //不是全部覆盖
						String askPath = Consts.getRelativePath( user, newPath );
						session.setAttribute( "_replace_", askPath ); //向客户端发问是否覆盖文件askPath
						while ( true ) {
							String sel = ( String ) session.getAttribute( "_answer_" );
							if ( sel != null ) {
								session.removeAttribute( "_answer_" );
								if ( "0".equals( sel ) )return; //不覆盖
								else break;
							}
							try {Thread.currentThread().sleep( 500 );
							}
							catch ( Throwable e ) {}
						}
					}
				}
			}
			byte[] b = read( path );
			write( newPath, b );
			if( isMove ) delete( path );
		}
	}

	//取同目录复制时副本文件名
	private String getFileName2( String newFilePath, String name ) {
		String newPath = newFilePath + "/复件 " + name;
		File f = new File( newPath );
		if( !f.exists() ) return newPath;
		int index = 1;
		while( true ) {
			newPath = newFilePath + "/复件(" + index + ") " + name;
			f = new File( newPath );
			if( !f.exists() ) return newPath;
			index++;
		}
	}

	private void copyDir( File dir, String toPath, boolean isMove, HttpSession session ) throws Throwable {
		File[] fs = dir.listFiles();
		String oldToPath = toPath;
		toPath += "/" + dir.getName();
		if( Consts.getAbsPath( user, dir.getAbsolutePath() ).equals( Consts.getAbsPath( user, toPath ) ) ) {
			toPath = getFileName2( Consts.getAbsPath( user, oldToPath ), dir.getName() );
		}
		new File( toPath ).mkdirs();
		for( int i = 0; i < fs.length; i++ ) {
			copy( fs[i].getAbsolutePath(), toPath, isMove, session );
		}
	}

	public void delete( String path ) throws Throwable {
		File f = new File( Consts.getAbsPath( user, path ) );
		if ( f.exists() ) {
			for ( int i = 1; i <= Consts.tryTimes; i++ ) {
				try {
					if( f.isDirectory() ) {
						if( Directory.deleteDirByFile( f, this ) ) return;
					}
					else if( f.delete() ) {
						removeFileShare( path );
						return;
					}
				}
				catch ( Throwable e ) {
					if ( i == Consts.tryTimes ) {
						throw e;
					}
					try {Thread.currentThread().sleep( Consts.tryInterval );
					}
					catch ( Exception ex ) {}
				}
			}
			throw new Exception( "The file \"" + path + "\" can not be deleted." );
		}
	}

	public void move( String path, String toPath, HttpSession session ) throws Throwable {
		copy( path, toPath, true, session );
	}

	public void setFileShare( String fileName, String pwd ) throws Throwable {
		String shareName = Consts.getAbsSharedPath( user, fileName );
		File f = new File( shareName );
		if( !f.exists() ) {
			f.getParentFile().mkdirs();
		}
		OutputStream os = null;
		try {
			for ( int i = 1; i <= Consts.tryTimes; i++ ) {
				try {
					os = new FileOutputStream( f );
					if ( os != null )break;
				}
				catch ( Throwable e ) {
					if ( i == Consts.tryTimes ) {
						throw e;
					}
					try {Thread.currentThread().sleep( Consts.tryInterval );
					}
					catch ( Exception ex ) {}
				}
			}
			os.write( pwd.getBytes() );
		}
		finally {
			try{ os.close(); }catch( Exception e ) {}
		}
	}

	public void removeFileShare( String fileName ) throws Throwable {
		String shareName = Consts.getAbsSharedPath( user, fileName );
		File f = new File( shareName );
		if( !f.exists() ) return;
		for ( int i = 1; i <= Consts.tryTimes; i++ ) {
			try {
				if( f.delete() ) return;
			}
			catch ( Throwable e ) {
				if ( i == Consts.tryTimes ) {
					throw e;
				}
				try {Thread.currentThread().sleep( Consts.tryInterval );
				}
				catch ( Exception ex ) {}
			}
		}
		throw new Exception( "The shared file of \"" + fileName + "\" can not be deleted." );
	}

	//获得路径path的共享路径(父路径或它自己)和密码
	public String[] getSharedPathPwd( String path ) throws Throwable {
		String oldPath = path;
		File f = new File( Consts.getAbsSharedPath( user, path ) );
		boolean found = false;
		if( !f.exists() ) {
			while( true ) {
				int pos = path.lastIndexOf( "/" );
				if( pos < 0 ) break;
				path = path.substring( 0, pos );
				f = new File( Consts.getAbsSharedPath( user, path ) );
				if( f.exists() ) {
					found = true;
					break;
				}
			}
		}
		else found = true;
		if( !found ) throw new Exception( "用户" + user + "的文件" + oldPath + "没有共享！" );
		for ( int i = 1; i <= Consts.tryTimes; i++ ) {
			BufferedReader br = null;
			try {
				br = new BufferedReader( new InputStreamReader( new FileInputStream( f ) ) );
				return new String[]{ path, br.readLine() };
			}
			catch ( Throwable e ) {
				if ( i == Consts.tryTimes ) {
					throw e;
				}
				try {Thread.currentThread().sleep( Consts.tryInterval );
				}
				catch ( Exception ex ) {}
			}
			finally{
				try{ br.close(); }catch( Throwable t ) {}
			}
		}
		return new String[]{ path, "notRead" };
	}

	public void rename( String path, String newName ) throws Exception {
		File file = new File( Consts.getAbsPath( user, path ) );
		if( !file.exists() ) return;
		File f = new File( file.getParent(), newName );
		if( !file.renameTo( f ) ) throw new Exception( "同名文件已存在！" );
		String tmp = Consts.getAbsSharedPath( user, path );
		File share = new File( tmp );
		if( share.exists() ) {
			int pos = tmp.lastIndexOf( "/" );
			if( pos < 0 ) tmp = newName + ".txt";
			else tmp = tmp.substring( 0, pos ) + "/" + newName + ".txt";
			share.renameTo( new File( tmp ) );
		}
	}

}
