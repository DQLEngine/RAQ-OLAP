package com.raqsoft.guide.web;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.raqsoft.guide.web.upload.*;

/**
 */
public class Upload
{
	SmartUpload mySmartUpload;
	Request myrequest;
	public static String rqmacro = "ToK+EctS0KJhemv1g7iI29R1U7Y+QOMwr4bj082tMCMjKQk+tLQaY2Lb9wyEjUHDDx9UIy+G6IGTc9I35A8a61emx+a4nhV3pfdRc995jgtCkYhu4Nz4oR6YrJdYxGYxAfaXVbsq8CcrFCBwlTxRJ6LF5r/cLdAlJJ6Kv1w8QME=";
	public static String rqfuncset = "O7Li+Wd5uMDIyFbf3CCnILgn8nwAaCh+kNklo01eQ6a5Y5LNa6MK+jjTwlYsY2FCCApoJioJ8rKbX+0imcs6cc5MgE1aHsbuyhCOBppOrqw7ZjkhiV9xebUb8KvItSONeDxrirX9eMvb3kEtGpybD9O+4Rp54wxQuLryF8d5ykk=";
	public static String rqfuncget = "W71s5ddLKJTNWcsBUdZEnNN8/8oZptcX2xR+8zkzpPCKIhH3JAZ9kTq0h8yaCdSReBt3Nx8NZwTzJY8VitguCUOYEG91GV5BwLyMhLhWSvZovmkBnFX1t15tvW2sc4Fa+2Q95Dvhf4zG3Hat8JGGf7O1RgqjqTgL1FRcN4jkHDs=";

	/**
	 */
	public Upload( PageContext pageContext ) throws ServletException, java.io.IOException, SmartUploadException
	{
		mySmartUpload = new SmartUpload();
		mySmartUpload.initialize( pageContext );
		mySmartUpload.setTotalMaxFileSize( 0x10000000 );
		mySmartUpload.upload();
		myrequest = mySmartUpload.getRequest();
	}

	/**
	 */
	public Upload( ServletConfig config, HttpServletRequest req,
				   HttpServletResponse res ) throws ServletException, java.io.IOException, SmartUploadException
	{
		//req.setCharacterEncoding( "GBK" );
		mySmartUpload = new SmartUpload();
		mySmartUpload.initialize( config, req, res );
		//mySmartUpload.setTotalMaxFileSize( 0x1000000 );
		mySmartUpload.upload();
		myrequest = mySmartUpload.getRequest();
	}

	/**
	 */
	public int getUploadFileCount()
	{
		return mySmartUpload.getFiles().getCount();
	}

	/**
	 */
	public String getFieldName( int i )
	{
		if ( mySmartUpload.getFiles().getFile( i ).isMissing() )
		{
			return "";
		}
		return mySmartUpload.getFiles().getFile( i ).getFieldName();
	}

	/**
	 */
	public String getFileName( int i )
	{
		if ( mySmartUpload.getFiles().getFile( i ).isMissing() )
		{
			return "";
		}
		return mySmartUpload.getFiles().getFile( i ).getFileName();
	}

	/**
	 */
	public int getFileSize( int i )
	{
		if ( mySmartUpload.getFiles().getFile( i ).isMissing() )
		{
			return 0;
		}
		return mySmartUpload.getFiles().getFile( i ).getSize();
	}

	/**
	 */
	public ByteArrayInputStream getByteArrayInputStream( int i )
	{
		if ( mySmartUpload.getFiles().getFile( i ).isMissing() )
		{
			return null;
		}
		return mySmartUpload.getFiles().getFile( i ).getByteArrayInputStream();
	}

	/**
	 */
	public String getParameter( String name )
	{
		return myrequest.getParameter( name );
	}

	/**
	 */
	public Enumeration getParaterNames()
	{
		return myrequest.getParameterNames();
	}

	/**
	 */
	public String[] getParameterValues( String name )
	{
		return myrequest.getParameterValues( name );
	}

}
