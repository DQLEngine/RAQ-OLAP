package com.raqsoft.guide.web;

import java.util.List;
import java.util.ArrayList;

public class ReportConfigModel {

	private List servletConfigModel = new ArrayList();
	private List __dsArray = new java.util.ArrayList();
	private List jndidsArray = new java.util.ArrayList();

	public void addConfig( ConfigModel cm ) {
		servletConfigModel.add( cm );
	}

	public List getServletConfigModelList() {
		return this.servletConfigModel;
	}

	public void addDS( JDBCDsConfigModel cm ) {
		this.__dsArray.add( cm );
	}

	public java.util.List getDsList() {
		return this.__dsArray;
	}

	public String[] listDsModelKeys() {
		String[] s = new String[this.__dsArray.size()];
		for ( int i = 0; i < s.length; i++ ) {
			JDBCDsConfigModel jcm = ( JDBCDsConfigModel )this.__dsArray.get( i );
			s[i] = jcm.name;
		}
		return s;
	}

	public JDBCDsConfigModel getDsValue( String key ) {
		JDBCDsConfigModel jcm = null;
		for ( int i = 0; i < this.__dsArray.size(); i++ ) {
			jcm = ( JDBCDsConfigModel )this.__dsArray.get( i );
			if ( key.equals( jcm.name ) ) {
				return jcm;
			}
		}
		return null;
	}

	public JDBCDsConfigModel getFirstDsModel() {
		if ( this.listDsModelKeys().length > 0 ) {
			return ( JDBCDsConfigModel )this.__dsArray.get( 0 );
		}
		return null;
	}

	public void addJndiDS( JNDIDsConfigModel cm ) {
		this.jndidsArray.add( cm );
	}

	public List getJndiDsList() {
		return this.jndidsArray;
	}

	public String[] listJndiDsModelKeys() {
		String[] s = new String[this.jndidsArray.size()];
		for ( int i = 0; i < s.length; i++ ) {
			JNDIDsConfigModel jcm = ( JNDIDsConfigModel )this.jndidsArray.get( i );
			s[i] = jcm.name;
		}
		return s;
	}

	public JNDIDsConfigModel getJndiDsValue( String key ) {
		JNDIDsConfigModel jcm = null;
		for ( int i = 0; i < this.jndidsArray.size(); i++ ) {
			jcm = ( JNDIDsConfigModel )this.jndidsArray.get( i );
			if ( key.equals( jcm.name ) ) {
				return jcm;
			}
		}
		return null;
	}

}
