package com.raqsoft.guide.web;

import java.util.*;

public class UsersTable {

	public UsersTable() {
	}

	public String generateHtml() throws Exception {
		StringBuffer sb = new StringBuffer();
		return sb.toString();
	}

	private String list2string( List list ) {
		String s = "";
		for( int i = 0; i < list.size(); i++ ) {
			if( s.length() > 0 ) s += ",";
			s += list.get( i );
		}
		return s;
	}

}
