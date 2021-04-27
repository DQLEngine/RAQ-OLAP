package com.raqsoft.guide.web;

import java.util.*;
import com.raqsoft.common.*;

public class TreeNode {
	private String label;
	private String path;
	private String id;
	private String parentId;
	private String prevId, nextId;
	private ArrayList children;

	protected TreeNode( String id, String path ) {
		this.id = id;
		this.path = path;
		children = new ArrayList();
	}

	private String getLabel() {
		if ( id.equals( "0" ) ) {
			return "&nbsp;&nbsp;&nbsp;&nbsp;";
		}
		String tmp = "";
		ArgumentTokenizer at = new ArgumentTokenizer( path, '/' );
		while ( at.hasMoreTokens() ) {
			tmp = at.nextToken();
		}
		label = tmp;
		return label;
	}

	protected String getPath() {return path;}

	protected int getLevel() {
		ArgumentTokenizer at = new ArgumentTokenizer( path, '/' );
		return at.countTokens() + 1;
	}

	protected TreeNode[] getChildren() {
		TreeNode[] tn = new TreeNode[children.size()];
		for ( int i = 0; i < tn.length; i++ ) {
			tn[i] = ( TreeNode ) children.get( i );
		}
		return tn;
	}

	protected void addChild( TreeNode node ) {
		children.add( node );
	}

	protected String getId() {
		return id;
	}

	protected int getChildCount() {
		return children.size();
	}

	protected String getFirstChildId() {
		TreeNode[] ln = this.getChildren();
		if ( ln.length == 0 ) {
			return getId();
		}
		return ln[0].getId();
	}

	protected String generateHtml( Tree tree, boolean isLastNode, String leftSide ) {
		
		String display = "block"; 

		String font = tree.getLabelFace( tree.LABEL_FONT );
		String fontSize = tree.getLabelFace( tree.LABEL_SIZE );
		String fontColor = tree.getLabelFace( tree.LABEL_COLOR );
		StringBuffer html = new StringBuffer();
		html.append( "<table id=\"_tbl_" + id + "\"" );
		if( id.equals( "0" ) ) html.append( " class=rootNode" );
	   html.append( " border=0 cellspacing=0 cellpadding=0 width=100% style=\"" );
	   if( id.equals( "0" ) ) html.append( "border-right:1px solid #b9babc;" );
	   else html.append( " font-size:" + fontSize + "; font-family:" + font + "; color:" + fontColor + ";" );
	   html.append( " border-bottom:1px solid #b9babc;background-color:#f9fbfc\" >\n" + "<tr>" );

		if ( leftSide == null ) {
			leftSide = "";
		}
		int childCount = getChildCount();

		
		if ( getLevel() != 1 ) {
			html.append( "<td valign=middle nowrap>" + leftSide );
			if ( isLastNode ) { //the last 'brother' line style: |_
				leftSide += "<img id=\"_leftimg_" + id + "\" src=\"" + tree.getImage( tree.BLANK )
					+ "\" width=16 height=22>";
				if ( childCount > 0 ) {
					html.append( "<img src=\"" + tree.getImage( tree.LASTMINUS ) + "\" width=16 height=22 border=noborder "
						+ "id=\"_img_" + id + "\" nodevalue=\"3\" " + "onClick=\"tree_iconClick(event)\" >" );
				}
				else {
					html.append( "<img src=\"" + tree.getImage( tree.LASTNODE ) + "\" width=16 height=22 "
						+ "id=\"_img_" + id + "\">" );
				}
			}
			else { // non last brother line style: |-
				leftSide += "<img id=\"_leftimg_" + id + "\" src=\"" + tree.getImage( tree.VERTLINE )
					+ "\" width=16 height=22>";
				if ( childCount > 0 ) {
					html.append( "<img src=\"" + tree.getImage( tree.MINUS ) + "\" width=16 height=22 border=noborder "
						+ "id=\"_img_" + id + "\" nodevalue=\"1\" " + "onClick=\"tree_iconClick(event)\" >" );
				}
				else {
					html.append( "<img src=\"" + tree.getImage( tree.NODE ) + "\" width=16 height=22 "
						+ "id=\"_img_" + id + "\">" );
				}
			}
			html.append( "</td>\n" );
		}

		
		html.append( "<td valign=middle align=left nowrap>" );
		String nodeImage = tree.getImage( tree.NODEIMAGE );
		if ( getLevel() > 1 ) {
			html.append( "<img src=\"" + nodeImage + "\" border=noborder id=\"_img2_" + id + "\" >" );
		}
		html.append( "</td>\n" );

		
		html.append( "<td id=\"id_" + id + "\" nodeid=\"" + id + "\" width=100% valign=middle align=left nowrap"
			+ " onclick=\"tree_nodeClick(event)\" ondblclick=\"tree_nodeDoubleClick(event)\""
			+ " style=\"cursor:default\" path=\"" + getPath() + "\" " );
		html.append( "childCount=\"" + this.getChildCount() + "\" isLast=\"" + isLastNode + "\" "
			+ "label=\"" + this.getLabel() + "\" parentId=\"id_" + parentId + "\" prevId=\"id_" + prevId
			+ "\" nextId=\"id_" + nextId + "\" firstChildId=\"id_" + getFirstChildId() + "\" "
			+ "leftSide=\'" + leftSide + "\' >" );
		//html += "onmouseup=\"tree_showMenu()\">";
		html.append( this.getLabel() + "</td>\n" );

		
		html.append( "</tr></table>\n" );

		
		if ( childCount > 0 ) {
			html.append( "<div id=\"_div_" + id + "\" style=\"display:" + display + "\">\n" );
			TreeNode[] children = this.getChildren();
			for ( int i = 0; i < childCount; i++ ) {
				children[i].parentId = this.getId();
				if ( i == 0 ) {
					children[i].prevId = children[i].id;
				}
				else {
					children[i].prevId = children[i - 1].id;
				}
				if ( i == childCount - 1 ) {
					children[i].nextId = children[i].id;
					html.append( children[i].generateHtml( tree, true, leftSide ) );
				}
				else {
					children[i].nextId = children[i + 1].id;
					html.append( children[i].generateHtml( tree, false, leftSide ) );
				}
			}
			html.append( "</div>" );
		}

		return html.toString();
	}

}
