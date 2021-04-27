package com.raqsoft.guide.util;

import java.io.File;

import com.raqsoft.common.DBConfig;
import com.raqsoft.common.Logger;
import com.raqsoft.dm.Env;
import com.raqsoft.guide.web.DataSphereServlet;

public class DBUtil {
	public static void putGlmdIntoDBList(String glmdPath, String dbName, String config) throws Exception{
		if(glmdPath == null) return;
		//if(!(new File(glmdPath).exists())) {Logger.info("["+glmdPath+"] is not exsit!");return;}//可以相对路径？
		DBConfig dbconfig = new DBConfig();
		String url = "jdbc:esproc:dql://lmd="+glmdPath;
		if(config != null && config.length()>0 && config.endsWith(".xml")){
			url += "&config="+config;
		}
		dbconfig.setUrl(url);
		dbconfig.setDriver("com.esproc.dql.jdbc.DQLDriver");
		dbconfig.setUser("");
		dbconfig.setClientCharset("UTF-8");
		dbconfig.setDBCharset("UTF-8");
		dbconfig.setName(dbName);
		dbconfig.setBatchSize(1000);
		Env.setDBSessionFactory(dbName,dbconfig.createSessionFactory());
		if (DataSphereServlet.DQLDATASOURCES.indexOf(dbName+";")>=0 || DataSphereServlet.DQLDATASOURCES.endsWith(";"+dbName)) return;
		if (DataSphereServlet.DQLDATASOURCES.length()>0) DataSphereServlet.DQLDATASOURCES += ";";
		DataSphereServlet.DQLDATASOURCES += dbName;
	}
}
