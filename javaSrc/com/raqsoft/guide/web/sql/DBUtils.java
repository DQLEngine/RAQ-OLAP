package com.raqsoft.guide.web.sql;

import java.sql.Connection;
import java.sql.ResultSet;

import com.raqsoft.common.DBSession;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.dm.Env;


public class DBUtils {

	String dbTables = "({tables:["+
	"{name:'北京通话记录',fields:[{name:'主叫号码',type:2,pk:0},{name:'被叫号码',type:2},{name:'开始时间',type:5},{name:'结束时间',type:5},{name:'主叫地区编码',type:2},{name:'被叫地区编码',type:2}]}"+
	",{name:'河北通话记录',fields:[{name:'主叫号码',type:2,pk:0},{name:'被叫号码',type:2},{name:'开始时间',type:5},{name:'结束时间',type:5},{name:'主叫地区编码',type:2},{name:'被叫地区编码',type:2}]}"+
	",{name:'用户表',fields:[{name:'号码',type:2,pk:1},{name:'地区编码',type:2},{name:'入网时间',type:5},{name:'身份证号码',type:2},{name:'用户类型',type:1}]}"+
	",{name:'公民表',fields:[{name:'身份证号码',type:2,pk:1},{name:'姓名',type:2},{name:'籍贯',type:2},{name:'性别',type:1},{name:'出生日期',type:3}]}"+
	",{name:'地区表',fields:[{name:'地区编码',type:2,pk:1},{name:'地区名称',type:2}]}"+
	//",{name:'地区表',sql:'...',def:'...',fields:[{name:'地区编码',type:2,pk:1},{name:'地区名称',type:2}]}"+
	"]})";

	public static String getDBJson(String dbName) throws Exception{
		String json = "({tables:[";
		ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dbName);
		DBSession dbs = isf.getSession();
		Connection con = (Connection)dbs.getSession();
		ResultSet rs = con.getMetaData().getTables("PUBLIC", "PUBLIC", null, null);
		boolean first = true;
		while (rs.next()) {
			if (first)  first = false;
			else json += ",";
			String tName = rs.getString(3);
			//Logger.debug(rs.getObject(1)+"--"+rs.getObject(2)+"--"+rs.getObject(3));
			json += "{name:'"+tName+"',fields:[";
			ResultSet rs1 = con.getMetaData().getColumns("PUBLIC", "PUBLIC", tName, null);
			boolean f2 = true;
			while (rs1.next()) {
				if (f2) f2= false;
				else json += ",";
				json += "{name:'"+rs1.getObject(4)+"',pk:0,type:2}";
			}
			json += "]}";
		}
		json += "]})";
		con.close();
		dbs.close();
		return json;
	}
}
