package com.raqsoft.guide.web;

import java.util.*;
import javax.servlet.http.*;
import com.raqsoft.guide.*;

public class Tree {
	private String user;
	private HttpServletRequest request;
	private String appmap;
	private String imagePath;
/*
	private String node = "node.gif"; // |-
	private String vertLine = "vertline.gif"; // |
	private String lastNode = "lastnode.gif"; // |_
	private String blank = "blank.gif";
	private String plus = "plus.gif";
	private String minus = "minus.gif";
	private String lastPlus = "lastplus.gif";
	private String lastMinus = "lastminus.gif";
	private String nodeImage = "folder.gif";
	private String rootIcon = "rootNode.png";
*/
	private String node = "blank.gif"; // |-
	private String vertLine = "blank.gif"; // |
	private String lastNode = "blank.gif"; // |_
	private String blank = "blank.gif";
	private String plus = "plus.gif";
	private String minus = "minus.gif";
	private String lastPlus = "plus.gif";
	private String lastMinus = "minus.gif";
	private String nodeImage = "blank2.gif";
	private String rootIcon = "rootNode.png";
	private String labelColor = "#000000";
	private String labelFont = "����";
	private String labelSize = "12px";
	private TreeNode rootNode;

	protected final static int NODE = 1;
	protected final static int MINUS = 2;
	protected final static int PLUS = 3;
	protected final static int VERTLINE = 4;
	protected final static int LASTNODE = 5;
	protected final static int BLANK = 6;
	protected final static int LASTPLUS = 7;
	protected final static int LASTMINUS = 8;
	protected final static int ROOTICON = 9;
	protected final static int NODEIMAGE = 10;

	protected final static int LABEL_FONT = 1;
	protected final static int LABEL_SIZE = 2;
	protected final static int LABEL_COLOR = 3;

	public static final int RED_COLOR = 1;
	public static final int GREEN_COLOR = 2;
	public static final int BLUE_COLOR = 3;
	public static final int ORANGE_COLOR = 4;
	public static final int CYAN_COLOR = 5;
	public static final int MAGENTA_COLOR = 6;
	public static final int PINK_COLOR = 7;
	public static final int YELLOW_COLOR = 8;

	private int nodeId = 1;

	public Tree( String user, HttpServletRequest request ) throws Exception {
		this.user = user;
		this.request = request;
		appmap = request.getContextPath();
		imagePath = appmap + "/images/tree/";
	}

	protected String getImage( int imageIndex ) {
		switch ( imageIndex ) {
			case LASTPLUS:
				return imagePath + lastPlus;
			case PLUS:
				return imagePath + plus;
			case MINUS:
				return imagePath + minus;
			case LASTMINUS:
				return imagePath + lastMinus;
			case NODE:
				return imagePath + node;
			case VERTLINE:
				return imagePath + vertLine;
			case LASTNODE:
				return imagePath + lastNode;
			case BLANK:
				return imagePath + blank;
			case ROOTICON:
				return imagePath + rootIcon;
			case NODEIMAGE:
				return imagePath + nodeImage;
		}
		return "";
	}

	public void setLabelFace( String font, String color, String size ) {
		if ( isValidString( font ) ) {
			labelFont = font;
		}
		if ( isValidString( color ) ) {
			labelColor = color;
		}
		if ( isValidString( size ) ) {
			labelSize = size;
		}
	}

	protected String getLabelFace( int property ) {
		switch ( property ) {
			case LABEL_FONT:
				return labelFont;
			case LABEL_SIZE:
				return labelSize;
			case LABEL_COLOR:
				return labelColor;
		}
		return "";
	}

	public void create() throws Exception {
		rootNode = new TreeNode( "0", "" );
		createSubNode( rootNode );
	}

	private void createSubNode( TreeNode p_node ) throws Exception {
		FileManager fm = new FileManager( user );
		List list = fm.listDirs( p_node.getPath() );
		for( int i = 0; i < list.size(); i++ ) {
			FileInfo fi = ( FileInfo ) list.get( i );
			TreeNode child = new TreeNode( nodeId + "", fi.getPath() );
			nodeId++;
			p_node.addChild( child );
			createSubNode( child );
		}
	}

	public String generateHtml() {
		StringBuffer html = new StringBuffer();
		html.append( "<script language=javascript>\n" );
		html.append( "\tvar newFolderId = " + nodeId + ";\n" );
		html.append( "   tree_setPath( \"http://" + request.getServerName() + ":" + request.getServerPort()
			+ "\", \"" + appmap + "\", \"" + imagePath + "\" );\n" );
		html.append( "   tree_setImages( \"" + blank + "\",\"" + node + "\",\"" + lastNode + "\",\""
			+ vertLine + "\",\"" + minus + "\",\"" + lastMinus + "\",\"" + plus + "\",\"" + lastPlus +
			"\",\"" + nodeImage + "\" );\n" );
		html.append( "   tree_setLabelFace( \"" + labelFont + "\",\"" + labelSize + "\",\"" + labelColor + "\" );\n" );
		html.append( "</script>\n" );

		html.append( rootNode.generateHtml( this, false, null ) );
		return html.toString();
	}

	private static boolean isValidString( String str ) {
		if ( str == null ) {
			return false;
		}
		if ( str.trim().length() == 0 ) {
			return false;
		}
		return true;
	}

}
