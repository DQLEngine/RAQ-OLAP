package com.raqsoft.guide.web.dl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.datalogic.jdbc.FieldAttr;
import com.datalogic.jdbc.LogicConnection;
import com.raqsoft.common.DBConfig;
import com.raqsoft.common.DBSession;
import com.raqsoft.common.DBSessionFactory;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.Sequence;
import com.raqsoft.guide.web.querys.QueryTask;

public class ConfigUtil {

	/**
	 * 按一定的格式转换为字符串
	 * @param Object
	 * @param format 转换格式
	 * @return String
	 */
	public static String format(Object o, String format) {
		//Logger.debug(o.getClass().getName() + "--" + o.toString());
		try {
			if (o instanceof Date) {
				//edited by hhw 2011.5.20
				DateFormat sdf ;
				if(format==null || "".equals(format))
					 sdf = new SimpleDateFormat();
				else
				     sdf = new SimpleDateFormat(format);
				return sdf.format(o);
			}
			else if (o instanceof Number) {
				if (format == null || format.length() == 0) return toString(o);
				com.ibm.icu.text.DecimalFormat nf = new com.ibm.icu.text.DecimalFormat(format);
				nf.setRoundingMode(BigDecimal.ROUND_HALF_UP);
				return nf.format(o);
			}
			else {
				return toString(o);
			}
		} catch (RuntimeException e) {
			Logger.error("",e);
			e.printStackTrace();
			return toString(o);
		}
	}

	/**
	 * 转换o为String类型
	 * @param o Object
	 * @return String
	 */
	public static String toString(Object o) {
		if (o == null) {
			return null;
		}
		DateFormat dateFormat = null;
		if (o instanceof java.sql.Date) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateFormat.format((Date) o);
		}
		if (o instanceof java.sql.Time) {
			dateFormat = new SimpleDateFormat("HH:mm:ss");
			dateFormat.format((Date) o);
		}
		if (o instanceof java.sql.Timestamp) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.format((Date) o);
		}

		if (o instanceof java.util.Date) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.format((Date) o);
		}
		
		if (o instanceof byte[]) {
			return new String( (byte[]) o);
		}

		return o.toString();
	}

	public static short getLicense(String dbName) throws Exception {
		ResultPage rp = new ResultPage( dbName, "license" );
		ResultSet rs = rp.getResultSet();
		while (rs.next()) {
			return rs.getShort(1);
		}
		rp.closeResultSet();
		return 0;
	}
	
	/**
	 * 是否有搜索功能
	 * @return
	 */
	public static boolean canSearch(String dbName) {
		short license = 0;
		try {
			license = getLicense(dbName);
		} catch (Exception e) {
			Logger.warn(e.getMessage());
		}
		return (license & (1 << 6)) != 0;
	}
	
	/**
	 * 是否有搜索功能
	 * @return
	 */
	public static boolean isDql(String dbName) {
		DBSession dbs = null;
		Connection con = null;
		try {
			ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dbName);

			dbs = isf.getSession();
			con = (Connection)dbs.getSession();
			boolean lc = con.getClass().getName().indexOf("LogicConnection") == -1;
			con.close();
			dbs.close();
			if (lc) return false;
		} catch (Exception e1) {
			e1.printStackTrace();
			return true;
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
			dbs.close();
		}
		return true;
	}

	/**
	 * 是否有中间表功能
	 * @return
	 */
	public static boolean useMiddleTable(String dbName) {
		if (!isDql(dbName)) return true;

		short license = 0;
		try {
			license = getLicense(dbName);
		} catch (Exception e) {
			Logger.warn(e.getMessage());
		}
		return (license & (1 << 7)) != 0;
	}

	public static String parseDql(String dbName, String dql) throws Exception {
		ResultPage rp = new ResultPage( dbName, "parse " + dql );
		ResultSet rs = rp.getResultSet();
		Logger.debug("PARSE DQL : " + dql);
		while (rs.next()) {
			String sql = rs.getString(1);
			Logger.debug("PARSE SQL : " + sql);
			return sql;
		}
		rp.closeResultSet();
		return "";
	}

	public static String executeDql(String dbName, String dql) throws Exception {
		ResultPage rp = new ResultPage( dbName, dql );
		ResultSet rs = rp.getResultSet();
		Logger.debug("execute DQL : " + dql);
		int cnt = rs.getMetaData().getColumnCount();
		while (rs.next()) {
			for (int i=1; i<=cnt; i++) Logger.debug("execute DQL result : " + rs.getObject(i));
		}
		rp.closeResultSet();
		return "";
	}

	
	private static String _getMetaDataJson(String dbName) throws Exception {
		if (dbName == null || dbName.length() == 0) return "";
		DBSession dbs = null;
		Connection con = null;
		try {
			ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dbName);
			if (isf == null) throw new Exception("no exist dataSource : [" + dbName + "]");
			
			if (isf instanceof DBSessionFactory) {
				Logger.debug(new String("[" + dbName + "]query metadata, driver is : "+((DBSessionFactory)isf).getDBConfig().getDriver()));
			} else Logger.debug(new String("[" + dbName + "]query metadata"));
//			if (isf instanceof DBSessionFactory) {
//				System.out.println(new String("[" + dbName + "]query metadata, driver is : "+((DBSessionFactory)isf).getDBConfig().getDriver()));
//			} else System.out.println(new String("[" + dbName + "]query metadata"));
			dbs = isf.getSession();
			con = (Connection)dbs.getSession();
			ResultSet rs = con.createStatement().executeQuery("metadata");
			Logger.debug("rs : " + rs);
			while (rs.next()) {
				String r = rs.getString(1);
				if (r != null) r = r.replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "");
				return r;
			}
		} finally {
			try {
				if(con != null) con.close();
			} catch (Exception e) {
				Logger.warn(e);
			}
			try {
				if(dbs != null) dbs.close();
			} catch (Exception e) {
				Logger.warn(e);
			}
		}
		return "";
	}

//	private static String query(String dbName,String dql) throws Exception {
//		if (dbName == null || dbName.length() == 0) return "";
//		DBSession dbs = null;
//		Connection con = null;
//		try {
//			ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dbName);
//			if (isf == null) throw new Exception("no exist dataSource : [" + dbName + "]");
//			
//			if (isf instanceof DBSessionFactory) {
//				Logger.debug(new String("[" + dbName + "]query metadata, driver is : "+((DBSessionFactory)isf).getDBConfig().getDriver()));
//			} else Logger.debug(new String("[" + dbName + "]query metadata"));
////			if (isf instanceof DBSessionFactory) {
////				System.out.println(new String("[" + dbName + "]query metadata, driver is : "+((DBSessionFactory)isf).getDBConfig().getDriver()));
////			} else System.out.println(new String("[" + dbName + "]query metadata"));
//			dbs = isf.getSession();
//			con = (Connection)dbs.getSession();
//			ResultSet rs = con.createStatement().executeQuery(dql);
//			Logger.debug("rs : " + rs);
//			while (rs.next()) {
//				String r = rs.getString(1);
//				if (r != null) r = r.replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "");
//				return r;
//			}
//		} finally {
//			try {
//				con.close();
//			} catch (Exception e) {
//				Logger.warn(e);
//			}
//			try {
//				dbs.close();
//			} catch (Exception e) {
//				Logger.warn(e);
//			}
//		}
//		return "";
//	}
	
	public static String getMetaDataJson(String dbName) throws Exception {
		String s = _getMetaDataJson(dbName);
		Logger.debug(dbName + " metadata : " + s);
		return s;
	}
	
	public static String getMiddleTablesJson(String path, String dbName) {
		File file = new File(path);
		File[] fs = null;
		if (file.exists()) fs = new File(path).listFiles();
		String mt = "([";
		boolean first = true;
		if (useMiddleTable(dbName) && fs != null) {
			for (int i=0; i<fs.length; i++) {
				File f = fs[i];
				if (!f.getName().endsWith(".tsk")) continue;
				QueryTask qt = new QueryTask(f.getPath());
				if ((!qt.getDBName().equals(dbName)) || !qt.isSuccess()) continue;
				if (!first) mt += ",";
				mt += "{name:'" + qt.getID() + "',alias:'" + qt.getName() + "'}";
				first = false;
			}
		}
		mt += "])";
		return mt;
	}
	
	public static String getDimDispTable(String dbName, String sql) throws Exception {
		try {
			ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dbName);
			DBSession dbs = isf.getSession();
			LogicConnection con = (LogicConnection)(dbs.getSession());
			Logger.debug(new String("2.." + con));
			String str = FieldAttr.queryDispTableStr(con, sql);
			con.commit();// db2的bug。不commit就关不掉con。
			try {
				con.close();
			} catch (Exception e) {
				Logger.warn(e);
			}
			try {
				dbs.close();
			} catch (Exception e) {
				Logger.warn(e);
			}
//		while (!con.isClosed()) {
//		}
			if (str == null) str = "none";
			return str;
		} catch (Exception e) {
			Logger.warn("",e);
			return "none";
		}
	}
	
/*
	toString : function() {
		var fs = "[";
		for (var i=0; i<domInfos.fields.length; i++) {
			var fDom = domInfos.fields[i];
			if (i > 0) fs += ",";
			fs += "{name:'" + fDom.name + "',type:" + fDom.type + ",where:'" + fDom.where.replaceAll("'","<single_quote>") + "',whereDisp:'" + fDom.whereDisp.replaceAll("'","<single_quote>") + "',wherePos:'" + fDom.wherePos + "',order:" + fDom.order + ",seq:" + fDom.seq + ",format:'" + fDom.format + "',useDisp:" + fDom.useDisp + ",dim:'" + fDom.dim + "',selectOut:" + fDom.selectOut + ",infos:'" + fDom.infos.replaceAll("'","<single_quote>") + "',aggr:'" + fDom.aggr + "',tAlias:'" + fDom.tAlias + "',level:'" + fDom.level + "',colWidth:" + fDom.colWidth + ",lcts:'" + fDom.lcts.replaceAll("'","<single_quote>") + "',exp:'" + fDom.exp.replaceAll("'","<single_quote>") + "'}";
		}
		fs += "]";
		var ts = "[";
		for (var i=0; i<domInfos.tables.length; i++) {
			var tDom = domInfos.tables[i];
			if (i > 0) ts += ",";
			ts += "{name:'" + tDom.name + "',annexT:'" + tDom.annexT + "',joinType:" + tDom.joinType + "}";
		}
		ts += "]";
		var bs = "[";
		for (var i=0; i<domInfos.bys.length; i++) {
			var bDom = domInfos.bys[i];
			if (i > 0) bs += ",";
			bs += "{infos:'" + bDom.infos + "',dimAlias:'" + bDom.dimAlias + "',tAlias:'" + bDom.tAlias + "'}";
		}
		bs += "]";
		return "{fields:" + fs + ",tables:" + ts + ",bys:" + bs + "}";
	},
*/
	
	public static String transDQL2DomInfos(String dql) throws Exception {
//		Context ctx = new Context();
//		Token[] ts = Tokenizer.parse(dql);
//		LogicMetaData lmd = IOUtil.readLogicMetaData("D:\\Program Files\\MicroInsight\\DataLogic\\services\\datalogic\\conf\\demo.lmd");
//		Visibility v = IOUtil.readVisibility("D:\\Program Files\\MicroInsight\\DataLogic\\services\\datalogic\\conf\\demo.vsb");
//		ctx.setLogicMetaData(lmd);
//		ctx.setVisibility(v);
//		Translator t = new Translator(dql,ctx);
////		t.getQuery();
//		Logger.debug(ts.length);
		return null;
	}
	
	public static void main(String args[]) {

		
		java.util.Date dt=new java.util.Date("1901/05/12 11:10:21");
		System.out.println("dt ="+dt.getTime());
		//java.util.Date d3 = new java.util.Date("1956/01/23 11:10:21");
		java.util.Date d3 = new java.util.Date("1901/05/12");
		System.out.println("d3 ="+d3.getTime());
		
		java.util.Date d7 = new java.util.Date("1901/05/12 00:00:01");
		System.out.println("d7 ="+d7.getTime());
		java.util.Date d8 = new java.util.Date("1901/05/12 23:59:59");
		System.out.println("d8 ="+(d8.getTime()-d7.getTime()));		
		
//		long d = DateUtils.parseDate("2010-01-01").getTime()-DateUtils.parseDate("2000-01-01").getTime();
//		System.out.println(d);
		//Logger.debug(format(new Double(0.95),null));
//		try {
//			String[] s = new String[100];
//			System.out.println(111);
//			for (int i=0; i<s.length; i++) {
//				s[i] = ";";
//			}
//			System.out.println(s[0].length());
//			Thread.currentThread().sleep(1000000000);
//			//transDQL2DomInfos("select a from b");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			Logger.error("",e);
//			e.printStackTrace();
//		}
	}

	private static String getDimsJson(Connection con, List<String> tables) throws SQLException {
		StringBuffer jsonsb = new StringBuffer();
		ResultSet rs = con.createStatement().executeQuery("list dim");
		while(rs.next()){
			String dimName = rs.getString(1);
			String table = "";
			String field = "";
			ResultSet rs1 = con.createStatement().executeQuery("list field of "+dimName+" dim "+ dimName);
			while(rs1.next()){
				table = rs1.getString(1);
				if(table.equals(dimName)) {
					field = rs1.getString(2);
					break;
				}else{
					table = "";
				}
			}
			if(jsonsb.length() > 0){
				jsonsb.append(",");
			}
			jsonsb.append("{");
			jsonsb.append("<d_q>name<d_q>:");
			jsonsb.append("<d_q>").append(dimName).append("<d_q>,");
			jsonsb.append("<d_q>table<d_q>:");
			jsonsb.append("<d_q>").append(table).append("<d_q>,");
			jsonsb.append("<d_q>field<d_q>:");
			field = field.substring(table.length()+1);
			jsonsb.append("<d_q>").append(field).append("<d_q>");
			jsonsb.append("}");
		}
		return jsonsb.toString();
	}

	private static Object getFieldsJson(Connection conn, String tname, String json_str, StringBuffer fksb, Map<String,List<String>> pkMap) throws SQLException, JSONException {
		JSONArray json_arr = new JSONArray("["+json_str.replace("<d_q>", "\"")+"]");
		List<String> fkList =  new ArrayList<String>();
		List<String> fieldDim =  new ArrayList<String>();
		findFKOfTable(conn,tname, fkList, fieldDim);
		StringBuffer jsonsb = new StringBuffer();
		ResultSet fieldsRs = conn.createStatement().executeQuery("list field, dim, type of "+tname);
//		ResultSet pk = conn.createStatement().executeQuery("list field of "+tname+" primary key");
		ArrayList<String> pkList = new ArrayList<String>();
//		while(pk.next()){
//			String pkField = pk.getString(2);
//			pkList.add(pkField);
//		}
//		pk.close();
		pkList = (ArrayList<String>) pkMap.get(tname);
		for(int i = 0; i < pkList.size(); i++){
			String pkField = pkList.get(i);
			pkField = pkField.substring(pkField.indexOf(tname+".")+tname.length()+1);
			pkList.set(i, pkField);
		}
		while(fieldsRs.next()){
			String originTable = fieldsRs.getString(1);
			if(!originTable.equals(tname)) continue;
			String fieldName = fieldsRs.getString(2);
			fieldName = fieldName.replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "");
			fieldName = fieldName.substring(fieldName.indexOf(tname+".")+tname.length()+1);
			String dim = fieldsRs.getString(3);
			String type = fieldsRs.getString(4);
			if(jsonsb.length() > 0){
				jsonsb.append(",");
			}
			jsonsb.append("{");
			jsonsb.append("<d_q>name<d_q>:");
			jsonsb.append("<d_q>").append(fieldName).append("<d_q>,");
			jsonsb.append("<d_q>dim<d_q>:");
			jsonsb.append("<d_q>").append(dim).append("<d_q>,");
			jsonsb.append("<d_q>type<d_q>:");
			jsonsb.append("<d_q>").append(type).append("<d_q>,");
			jsonsb.append("<d_q>desc<d_q>:");
			jsonsb.append("<d_q><d_q>,");
			jsonsb.append("<d_q>pk<d_q>:");
			int ispk = 0;
			if(pkList.size() > 0 && pkList.contains(fieldName)) ispk = 1;
			jsonsb.append("<d_q>").append(ispk).append("<d_q>,");
			String fkField = null;
			//
			ResultSet layer = null;
			if(dim != null && !"".equals(dim)){
				layer = conn.createStatement().executeQuery("list layer of "+dim);
				int index = fieldDim.indexOf(dim);
				if(index >= 0){
					if(fksb.length() > 0){
						fksb.append(",");
					}
					fkField = fkList.get(index);
					fksb.append("{<d_q>name<d_q>: <d_q>"+fieldName+"<d_q>,");//{name: "fk2", hide: 0, destTable: "雇员", desc: "", fields: Array(1)}
					fksb.append("<d_q>hide<d_q>: "+0+",");
					fksb.append("<d_q>destTable<d_q>: <d_q>"+dim+"<d_q>,");
					fksb.append("<d_q>desc<d_q>: <d_q><d_q>,");
					fksb.append("<d_q>fields<d_q>: [<d_q>"+fkField);
					fksb.append("<d_q>]}");
				}
			}
			jsonsb.append("<d_q>destTable<d_q>:");
			jsonsb.append("<d_q>");
			if(fksb.length()>0) jsonsb.append(dim);//fksb没有再用到
			jsonsb.append("<d_q>,");
			jsonsb.append("<d_q>destLevels<d_q>:[");
			if(dim != null && !"".equals(dim)){
				//ResultSet layer = conn.createStatement().executeQuery("list layer of "+dim);
				int count = 0;
				while(layer.next()){
					//String layerFatherName = layer.getString(1);
					String layerChildName = layer.getString(2);
					String layerField = "";
					String layerTable = "";
					for(int i = 0; i<json_arr.length(); i++){
						JSONObject dimJsonObj = json_arr.getJSONObject(i);
						if(dimJsonObj.get("name").equals(layerChildName)){
							layerField = (String) dimJsonObj.get("field");
							layerTable = (String) dimJsonObj.get("table");
							break;
						}
					}
					if(count > 0){
						jsonsb.append(",");
					}
					count++;
					jsonsb.append("{");
					jsonsb.append("<d_q>name<d_q>:");
					jsonsb.append("<d_q>"+layerChildName+"<d_q>,");
					jsonsb.append("<d_q>dest<d_q>:");
					jsonsb.append("<d_q>"+layerTable+"."+layerField+"<d_q>");
					jsonsb.append("}");
				}
				layer.close();
			}
			jsonsb.append("]");
			jsonsb.append("}");
		}
//		pk.close();
		fieldsRs.close();
		return jsonsb.toString();
	}

	private static void findFKOfTable(Connection con, String tname, List<String> fkList, List<String> fkDim) throws SQLException {
		ResultSet rs = con.createStatement().executeQuery("list dim table of "+tname);
		Set<String> dimTableSet = new HashSet<String>();
		Set<String> dimTableFields = new HashSet<String>();
		while(rs.next()){
			String dimTableName = rs.getString(1);
			dimTableSet.add(dimTableName);
			ResultSet rs2 = con.createStatement().executeQuery("list field of "+dimTableName+" depth 0");
			while(rs2.next()){
				String r1 = rs2.getString(2);
				r1 = r1.replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "");
				r1 = r1.substring(r1.indexOf(dimTableName+".")+dimTableName.length()+1);
				dimTableFields.add(r1);
			}
		}
		//rs.close();
		ResultSet rs1 = con.createStatement().executeQuery("list field, dim of "+tname+" depth 2");
		List<String> fields = new ArrayList<String>();
		List<String> fieldDims = new ArrayList<String>();
		while(rs1.next()){
			String originTable = rs1.getString(1);
			if(dimTableSet.contains(originTable) || originTable.equals(tname) || fieldOfDimTable(dimTableFields,rs1.getString(2)))//|| rs1.getString(3).equals("") 
			{continue;}
			String fk = rs1.getString(2).split("\\.")[1];
			if(!fields.contains(fk)){
				fields.add(fk);//组表中：客户.市.市
				fieldDims.add(rs1.getString(3));//组表中：市
			}
		}
		//fkList是外键字段
		fkList.addAll(fields);
		fkDim.addAll(fieldDims);
	}

	private static boolean fieldOfDimTable(Set<String> dimTableFields, String testfield) {
		for(String dimTableField: dimTableFields){
			if(dimTableField != null && testfield.indexOf(dimTableField) >= 0){
				return true;
			}
		}
		return false;
	}

	public static String getListJsonData(String dbName) throws Exception {
		if (dbName == null || dbName.length() == 0) return "";
		DBSession dbs = null;
		Connection con = null;
		try {
			ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dbName);
			if (isf == null) throw new Exception("no exist dataSource : [" + dbName + "]");
			
			if (isf instanceof DBSessionFactory) {
				Logger.debug(new String("[" + dbName + "]query metadata, driver is : "+((DBSessionFactory)isf).getDBConfig().getDriver()));
			} else Logger.debug(new String("[" + dbName + "]query metadata"));
//			if (isf instanceof DBSessionFactory) {
//				System.out.println(new String("[" + dbName + "]query metadata, driver is : "+((DBSessionFactory)isf).getDBConfig().getDriver()));
//			} else System.out.println(new String("[" + dbName + "]query metadata"));
			dbs = isf.getSession();
			con = (Connection)dbs.getSession();
			ResultSet tableNamesRs = con.createStatement().executeQuery("list table");
			ArrayList<String> tableNames = new ArrayList<String>();
			while(tableNamesRs.next()){
				tableNames.add(tableNamesRs.getString(1));
			}
			tableNamesRs.close();
			StringBuffer jsonsb = new StringBuffer();
			String dimsStr = getDimsJson(con,tableNames);
			
			Map<String, List<String>> pkMap = getTablePks(con,tableNames);
			JSONArray json_arr = new JSONArray("["+dimsStr+"]");
			jsonsb.append("{<d_q>tables<d_q>:");
			jsonsb.append("[");
			for(int i = 0; i < tableNames.size(); i++){
				String tname = tableNames.get(i);
				if(i>0) jsonsb.append(",");
				jsonsb.append("{");
				jsonsb.append("<d_q>name<d_q>:");
				jsonsb.append("<d_q>").append(tname).append("<d_q>,");
				jsonsb.append("<d_q>dispName<d_q>:");
				jsonsb.append("<d_q>").append(tname).append("<d_q>,");
				jsonsb.append("<d_q>type<d_q>:");
				jsonsb.append("<d_q>").append(0).append("<d_q>,");//普通表
				jsonsb.append("<d_q>desc<d_q>:");
				jsonsb.append("<d_q><d_q>,");
				jsonsb.append("<d_q>fields<d_q>:[");
				StringBuffer fksb = new StringBuffer();
				jsonsb.append(getFieldsJson(con,tname, dimsStr,fksb, pkMap));
				jsonsb.append("],");
				jsonsb.append("<d_q>fks<d_q>:[");
				jsonsb.append(fksb);
				jsonsb.append("]");
				jsonsb.append("}");
				
			}
			jsonsb.append("],");
			jsonsb.append("<d_q>dims<d_q>:[");
			jsonsb.append(dimsStr);
			
			jsonsb.append("],");
			jsonsb.append("<d_q>levels<d_q>:[");
			
			jsonsb.append("],");
			jsonsb.append("<d_q>annexTables<d_q>:[");
			StringBuffer tmpsb = getAnnexTablesJson(con,tableNames, pkMap);
			//多一个逗号
			if(tmpsb.length()>0) jsonsb.append(tmpsb.substring(0, tmpsb.length() - 1).toString());
			
			jsonsb.append("],");
			jsonsb.append("<d_q>classTables<d_q>:[");
			
			
			jsonsb.append("],");
			jsonsb.append("<d_q>editStyles<d_q>:[");
			
			
			jsonsb.append("]");
			jsonsb.append("}");
			System.out.println(jsonsb.toString());
			return jsonsb.toString();
		} finally {
			try {
				con.close();
			} catch (Exception e) {
				Logger.warn(e);
			}
			try {
				dbs.close();
			} catch (Exception e) {
				Logger.warn(e);
			}
		}
	}

	private static Map<String, List<String>> getTablePks(Connection conn, ArrayList<String> tnames) throws SQLException {
		Map<String, List<String>> map = new HashMap<String,List<String>>();
		for(String tname: tnames){
			ResultSet pk = conn.createStatement().executeQuery("list field of "+tname+" primary key");
			ArrayList<String> pkList = new ArrayList<String>();
			while(pk.next()){
				String pkField = pk.getString(2);
				pkList.add(pkField);
			}
			map.put(tname, pkList);
		}
		return map;
	}

	private static StringBuffer getAnnexTablesJson(Connection con, ArrayList<String> tableNames, Map<String,List<String>> pkMap) throws SQLException {
		StringBuffer sb = new StringBuffer();
		List<String> doneSearchForDim = new ArrayList<String>();
		for(String tname : tableNames){
			if(doneSearchForDim.contains(tname)) continue;
			else doneSearchForDim.add(tname);
			ResultSet rs = con.createStatement().executeQuery("list dim table of "+tname);
			List<String> pkList1 = pkMap.get(tname);
			String pkField1 = "";
			if(pkList1.size()>0) pkField1 = pkList1.get(0);
			//这里逻辑是否有问题？多个pk
//			ResultSet currTablePK = con.createStatement().executeQuery("list field of "+tname+" primary key");
//			String pkField1 = "";
//			if(currTablePK.next()){
//				pkField1 = currTablePK.getString(2);
//			}
			if(pkField1.length() > 0 && pkField1.indexOf(tname+".") >= 0) pkField1 = pkField1.substring(pkField1.indexOf(tname+".")+tname.length()+1);
			Map<String,String> table_pk = new Hashtable<String,String>();
			while(rs.next()){
				String dimTableName = rs.getString(1);//2是boolean是否是假表
				doneSearchForDim.add(dimTableName);
				List<String> pkList = pkMap.get(dimTableName);
				String pkField = "";
				if(pkList.size()>0) pkField = pkList.get(0);
				else continue;
//				String pkField = "";
//				ResultSet pk = con.createStatement().executeQuery("list field of "+dimTableName+" primary key");
//				if(pk.next()){
//					pkField = pk.getString(2);
//				}
				pkField = pkField.replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "");
				if(pkField.length() > 0 && pkField.indexOf(dimTableName+".") >= 0) pkField = pkField.substring(pkField.indexOf(dimTableName+".")+dimTableName.length()+1);
				table_pk.put(dimTableName,pkField);
			}
			if(table_pk.size() > 0){
				Set<String> choosenTables = table_pk.keySet();
				sb.append("[");
				StringBuffer dimTablesGroup = new StringBuffer();
				dimTablesGroup.append("{");
				dimTablesGroup.append("<d_q>name<d_q>:");
				dimTablesGroup.append("<d_q>").append(tname).append("<d_q>,");
				dimTablesGroup.append("<d_q>pks<d_q>:[");
				dimTablesGroup.append("<d_q>").append(pkField1).append("<d_q>");
				dimTablesGroup.append("]}");
				for(String ctname : choosenTables){
					dimTablesGroup.append(",");
					dimTablesGroup.append("{");
					dimTablesGroup.append("<d_q>name<d_q>:");
					dimTablesGroup.append("<d_q>").append(ctname).append("<d_q>,");
					dimTablesGroup.append("<d_q>pks<d_q>:[");
					dimTablesGroup.append("<d_q>").append(table_pk.get(ctname)).append("<d_q>");
					dimTablesGroup.append("]}");
				}
				sb.append(dimTablesGroup);
				sb.append("],");
			}
		}
		return sb;
	}
}
