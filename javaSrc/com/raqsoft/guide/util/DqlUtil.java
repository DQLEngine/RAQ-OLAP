package com.raqsoft.guide.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.raqsoft.app.config.ConfigUtil;
import com.raqsoft.common.DBSession;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.Record;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.Table;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.dl.DfxUtils;
import com.raqsoft.logic.metadata.TableVisibility;
import com.raqsoft.logic.metadata.TableVisibilityList;
import com.raqsoft.logic.metadata.Visibility;
import com.raqsoft.logic.util.IOUtil;

public class DqlUtil {
	
	/**
	 *

	{
		fields : [
			{
				tableAlias:''
				,subTable:''
				,aggr:''
				,field:''
				,alias:''
			}
		]
		, dims : [
			{
				dim : ''
				,alias:''
			}
		]
		,dimWhere : ''
		, tables : [
			{
				table : ''
				,alias : ''
				,bys : [
					''
				]
				,where : ''
				,joinType : ''
			}
		]
		, having : ''
	}

		
	{
		fields : [
			{
				tableAlias:'t1'
				,subTable:''
				,aggr:'sum'
				,field:'f1'
				,alias:'a1'
			}
			,
			{
				tableAlias:'t2'
				,subTable:''
				,aggr:'count'
				,field:'f2'
				,alias:'a2'
			}
		]
		, dims : [
			{
				dim : 'dim1'
				,alias:'a3'
			}
		]
		,dimWhere : ''
		, tables : [
			{
				table : 't1'
				,alias : 'a4'
				,bys : [
					'byf1'
				]
				,where : 'where2'
				,joinType : ''
			}
			,{
				table : 't2'
				,alias : 'a5'
				,bys : [
					'byf2'
				]
				,where : 'where2'
				,joinType : ' join '
			}
		]
		, having : 'having1'
	}
		
	 * 
	 * @param dqlSegments
	 * @return
	 */
	public static String getDql(String dqlSegments,Map<String,String> outerConditions, Map<String,String> params, String dataSource) throws Exception {
		Logger.debug(dqlSegments);
		String dql = "SELECT ";
		Context ctx = new Context();
		ctx.setParamValue("jsonStr", dqlSegments);
		DfxUtils.execDfxFile(DataSphereServlet.class.getResourceAsStream("/com/raqsoft/guide/web/dfx/readJson.dfx"), ctx);
		Object o = ctx.getParam("jsonObj").getValue();
		//if (o == null) return null;
		Record r = (Record)o;
		//Record r = t.getRecord(1);
		o = r.getFieldValue("fields");
		Table fields = null;
		if (o != null && o instanceof Table) fields = (Table)o;
		Table dims = null;
		o = r.getFieldValue("dims");
		if (o != null && o instanceof Table) dims = (Table)o;
		Table tables = (Table)r.getFieldValue("tables");
		String having = (String)r.getFieldValue("having");
		String dimWhere = (String)r.getFieldValue("dimWhere");
		String tableWhere = null;
		try{
			tableWhere = (String)r.getFieldValue("tableWhere");
		}catch(Exception e){
			tableWhere = null;
		}
//		Logger.debug(fields);
//		Logger.debug(having);
//		Logger.debug(dimWhere);

		
		DBSession dbs = null;
		Connection con = null;
		try {
			if (dataSource != null) {
				ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dataSource);
				if (isf == null) throw new Exception("no exist dataSource : [" + dataSource + "]");
				dbs = isf.getSession();
				con = (Connection)dbs.getSession();
			}
		
		
			if (fields != null) {
				for (int i=1; i<=fields.length(); i++) {
					Record fi = fields.getRecord(i);
					if (i>1) dql += ",";
					String aggr = (String)fi.getFieldValue("aggr");
					if (aggr == null) aggr = "";
					else aggr = aggr+"(";
					String subTable = (String)fi.getFieldValue("subTable");
					if (subTable == null || subTable.length() == 0) subTable = "";
					else subTable = "@"+subTable; 
					dql += fi.getFieldValue("tableAlias")+subTable+"."+aggr+fi.getFieldValue("field")+(aggr.length()>0?") ":" ")+fi.getFieldValue("alias")+" ";
				}
			}
			
			if (dims != null) {
				dql += "ON ";
				for (int i=1; i<=dims.length(); i++) {
					Record fi = dims.getRecord(i);
					if (i>1) dql += ",";
					dql += fi.getFieldValue("dim")+" "+fi.getFieldValue("alias")+" ";
				}
				
				if (dimWhere != null && dimWhere.length()>0) dql += "WHERE "+checkWhere(dimWhere)+" ";
			}
			dql += "FROM ";
			//c.put("雇员", "${T}.姓名 like '%张%'");
			List<String> useConds = new ArrayList<String>();
			for (int i=1; i<=tables.length(); i++) {
				Record fi = tables.getRecord(i);
				if (i>1) dql += fi.getFieldValue("joinType")+" ";
				String tAlias = (String)fi.getFieldValue("alias");
				String tName = fi.getFieldValue("table").toString();
				dql += tName+" "+tAlias+" ";
				String where = checkWhere((String)fi.getFieldValue("where"));
				ArrayList<String> where2 = new ArrayList<String>();
				ArrayList<String> realTName = new ArrayList<String>();
				if (outerConditions != null) {
					
					Iterator<String> iter = outerConditions.keySet().iterator();
					while (iter.hasNext()) {
						String t = iter.next();
						if (useConds.indexOf(t)>=0) continue;
						String v = outerConditions.get(t);
						
						if (tName.equals(t)) {
							where2.add(v);
							realTName.add(tAlias);
							useConds.add(t);
						} else {
							String dim = null;
							ResultSet rs = null;
							try {
								rs = con.createStatement().executeQuery("list field,dim of "+t+" primary key");//list field of 订单 dim 雇员
							} catch (Exception e) {
								Logger.error("execute failed [list field,dim of "+t+" primary key]");
								throw e;
							}
							while (rs.next()) {
								dim = rs.getString("dimName");
								break;
							}			
							if (dim ==null) continue;
							try {
								rs = con.createStatement().executeQuery("list field of "+tName+" dim "+dim+" depth 4");//list field of 订单 dim 雇员
							} catch (Exception e) {
								Logger.error("execute failed [list field of "+tName+" dim "+dim+" depth 4]");
								throw e;
							}
							ResultSetMetaData md =  rs.getMetaData();
//							for (int z=1; z<=md.getColumnCount(); z++) {
//								System.out.println(md.getColumnLabel(z));
//							}
							String fn = null;
							while (rs.next()) {
								String fn2 = rs.getString("fieldName");
								if (fn == null || fn.length()>fn2.length()) fn = fn2;
//								for (int z=1; z<=md.getColumnCount(); z++) {
//									//String ri = rs.getString(1);							
//									System.out.println(rs.getObject(z));
//								}
							}
							Logger.error("table : "+tName+", field : "+fn+", dim "+dim+"");
							if (fn != null) {
								where2.add(v);
								realTName.add(tAlias+fn.substring(tName.length()));
								useConds.add(t);
							}
						}
					}
					
				} 
				
				for (int z=0; z<where2.size(); z++) {
					if (where.length()>0) where += " AND ";
					String v = where2.get(z);
					v = v.replaceAll("\\$\\{T\\}", realTName.get(z));
//					while (v.indexOf("${T}")>=0) {
//						v = v.replace("${T}", realTName.get(z));
//					}
					where += "("+v+")";
				}
				if(tableWhere != null && tableWhere.length() > 0){
					if (where.length()>0) where += " AND ";
					where += tableWhere;
				}
				if (where != null && where.length()>0) dql += "WHERE "+where+" ";
				Sequence seq = (Sequence)fi.getFieldValue("bys");
				if (seq.length()>0) {
					dql += "BY ";
					for (int j=1; j<=seq.length(); j++) {
						if (j>1) dql += ",";
						dql += tAlias+"."+seq.get(j)+" ";
					}
				}
			}
			if (having != null && having.length()>0) dql += "HAVING "+checkWhere(having)+" ";
			
			if (params != null) {
				Iterator<String> iter = params.keySet().iterator();
				while (iter.hasNext()) {
					String p = iter.next();
					String v = params.get(p);
					dql = dql.replaceAll("\\$\\{"+p+"\\}", v);
				}
			} 
		
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

		Logger.debug("dql : [" + dql + "]");
		return dql;
	}
	/*
	 * 
	 * @param dqlSegments
	 * @return
	 */
	public static String getCtxDql(String dqlSegments,Map<String,String> outerConditions, Map<String,String> params, String dataSource) throws Exception {
		Logger.debug(dqlSegments);
		String dql = "SELECT ";
		Context ctx = new Context();
		ctx.setParamValue("jsonStr", dqlSegments);
		DfxUtils.execDfxFile(DataSphereServlet.class.getResourceAsStream("/com/raqsoft/guide/web/dfx/readJson.dfx"), ctx);
		Object o = ctx.getParam("jsonObj").getValue();
		//if (o == null) return null;
		Record r = (Record)o;
		//Record r = t.getRecord(1);
		o = r.getFieldValue("fields");
		Table fields = null;
		if (o != null && o instanceof Table) fields = (Table)o;
		Table dims = null;
		o = r.getFieldValue("dims");
		if (o != null && o instanceof Table) dims = (Table)o;
		Table tables = (Table)r.getFieldValue("tables");
		String having = (String)r.getFieldValue("having");
		String dimWhere = (String)r.getFieldValue("dimWhere");
		
//		Logger.debug(fields);
//		Logger.debug(having);
//		Logger.debug(dimWhere);

		
		DBSession dbs = null;
		Connection con = null;
		try {
			if (dataSource != null) {
				ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dataSource);
				if (isf == null) throw new Exception("no exist dataSource : [" + dataSource + "]");
				dbs = isf.getSession();
				con = (Connection)dbs.getSession();
			}
		
		
			if (fields != null) {
				for (int i=1; i<=fields.length(); i++) {
					Record fi = fields.getRecord(i);
					if (i>1) dql += ",";
					String aggr = (String)fi.getFieldValue("aggr");
					if (aggr == null || aggr.length() == 0) aggr = "";
					else aggr = aggr+"(";
//					String subTable = (String)fi.getFieldValue("subTable");
//					if (subTable == null || subTable.length() == 0) subTable = "";
//					else subTable = "@"+subTable; 
					dql += aggr+fi.getFieldValue("field")+(aggr.length()>0?") ":" ")+fi.getFieldValue("alias")+" ";
				}
			}
			
			if (dims != null) {
				dql += "ON ";
				for (int i=1; i<=dims.length(); i++) {
					Record fi = dims.getRecord(i);
					if (i>1) dql += ",";
					dql += fi.getFieldValue("dim")+" "+fi.getFieldValue("alias")+" ";
				}
				
				if (dimWhere != null && dimWhere.length()>0) dql += "WHERE "+checkWhere(dimWhere)+" ";
			}
			dql += "FROM ";
			//c.put("雇员", "${T}.姓名 like '%张%'");
			List<String> useConds = new ArrayList<String>();
			for (int i=1; i<=tables.length(); i++) {
				Record fi = tables.getRecord(i);
				if (i>1) dql += fi.getFieldValue("joinType")+" ";
				String tAlias = (String)fi.getFieldValue("alias");
				String tName = fi.getFieldValue("table").toString();
				dql += tName+" "+tAlias+" ";
				String where = checkWhere((String)fi.getFieldValue("where"));
				ArrayList<String> where2 = new ArrayList<String>();
				ArrayList<String> realTName = new ArrayList<String>();
				if (outerConditions != null) {
					
					Iterator<String> iter = outerConditions.keySet().iterator();
					while (iter.hasNext()) {
						String t = iter.next();
						if (useConds.indexOf(t)>=0) continue;
						String v = outerConditions.get(t);
						
						if (tName.equals(t)) {
							where2.add(v);
							realTName.add(tAlias);
							useConds.add(t);
						} else {
							String dim = null;
							ResultSet rs = null;
							try {
								rs = con.createStatement().executeQuery("list field,dim of "+t+" primary key");//list field of 订单 dim 雇员
							} catch (Exception e) {
								Logger.error("execute failed [list field,dim of "+t+" primary key]");
								throw e;
							}
							while (rs.next()) {
								dim = rs.getString("dimName");
								break;
							}			
							if (dim ==null) continue;
							try {
								rs = con.createStatement().executeQuery("list field of "+tName+" dim "+dim+" depth 4");//list field of 订单 dim 雇员
							} catch (Exception e) {
								Logger.error("execute failed [list field of "+tName+" dim "+dim+" depth 4]");
								throw e;
							}
							ResultSetMetaData md =  rs.getMetaData();
//							for (int z=1; z<=md.getColumnCount(); z++) {
//								System.out.println(md.getColumnLabel(z));
//							}
							String fn = null;
							while (rs.next()) {
								String fn2 = rs.getString("fieldName");
								if (fn == null || fn.length()>fn2.length()) fn = fn2;
//								for (int z=1; z<=md.getColumnCount(); z++) {
//									//String ri = rs.getString(1);							
//									System.out.println(rs.getObject(z));
//								}
							}
							Logger.error("table : "+tName+", field : "+fn+", dim "+dim+"");
							if (fn != null) {
								where2.add(v);
								realTName.add(tAlias+fn.substring(tName.length()));
								useConds.add(t);
							}
						}
					}
					
				} 
				
				for (int z=0; z<where2.size(); z++) {
					if (where.length()>0) where += " AND ";
					String v = where2.get(z);
					v = v.replaceAll("\\$\\{T\\}", realTName.get(z));
//					while (v.indexOf("${T}")>=0) {
//						v = v.replace("${T}", realTName.get(z));
//					}
					where += "("+v+")";
				}
				
				if (where != null && where.length()>0) dql += "WHERE "+where+" ";
				Sequence seq = (Sequence)fi.getFieldValue("bys");
				if (seq.length()>0) {
					dql += "BY ";
					for (int j=1; j<=seq.length(); j++) {
						if (j>1) dql += ",";
						dql += tAlias+"."+seq.get(j)+" ";
					}
				}
			}
			if (having != null && having.length()>0) dql += "HAVING "+checkWhere(having)+" ";
			
			if (params != null) {
				Iterator<String> iter = params.keySet().iterator();
				while (iter.hasNext()) {
					String p = iter.next();
					String v = params.get(p);
					dql = dql.replaceAll("\\$\\{"+p+"\\}", v);
				}
			} 
		
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

		Logger.debug("dql : [" + dql + "]");
		return dql;
	}
//	private static String getOuterCondition() {
//		
//	}
	
	
	/**
	 * 检查where的括号匹配情况，避免出现类似： 1=1) or (2=2，从而造成权限泄露
	 * @param w
	 * @return
	 */
	public static String checkWhere(String w) {
		if (w == null || w.length() == 0) return "";
		int m = 0;
		for (int i=0; i<w.length(); i++) {
			if (w.charAt(i) == '(') m++;
			else {
				if (w.charAt(i) == ')') m--;
				if (m<0) throw new RQException("Brackets not match:["+w+"]");
			}
		}
		return "("+w+")";
	}
	
//	if (vsb != null) {
//		for (var i=lmd.tables.length-1; i>=0; i--) {
//			var t = lmd.tables[i];
//			var ti = null;
//			for (var j=0; j<vsb.tableVisibilityList.length; j++) {
//				var tj = vsb.tableVisibilityList[j];
//				if (tj.name == t.name) {
//					ti = tj;
//					break;
//				}					
//			}
//			
//			if (ti == null || ti.isVisible==0) {
//				var d = mdUtils.getDimByTable(t.name);
//				if (d) d.hide = true;
//				lmd.tables.remove(t);
//				continue;
//			}
//			if (ti.isVisible == 2 && ti.filter) {
//				where.push({"table":t.name,"exp":ti.filter});
//			}
//			if (ti.invisibleFieldList) {
//				for (var j=0; j<ti.invisibleFieldList.length; j++) {
//					for (var z=0; z<t.fields.length; z++) {
//						if (ti.invisibleFieldList[j] == t.fields[z].name) {
//							t.fields.remove(t.fields[z]);
//							break;
//						}
//					}
//					for (var z=0; z<t.fks.length; z++) {
//						if (ti.invisibleFieldList[j] == t.fks[z].name) {
//							t.fks.remove(t.fks[z]);
//							break;
//						}
//					}
//				}
//			}
//		}
//	}
	public static Map<String,String> getVsbConditions(String vsbFile, Map<String,String> conds) throws Exception {
		Map result = conds;
		if (result == null) result = new HashMap<String,String>();
		
		Visibility vsb = IOUtil.readVisibility("", DataSphereServlet.getFilePath(vsbFile));//FileUtils.getVsb(DataSphereServlet.getFilePath(vsbFile));
		if (vsb == null) return result;
		TableVisibilityList ts = vsb.getTableVisibilityList();
		for (int i=0; i<ts.size(); i++) {
			TableVisibility ti = ts.get(i);
			if (ti == null || ti.isVisible() != TableVisibility.V_FILTER) continue;
			result.put(ti.getName(), ti.getFilter());
		}
		return result;
	}
	
	public static void main(String args[]) throws Exception {
//		String s = "";
//		System.out.println(combineDql("{}"));
		
		//Sequence.readLicense(Sequence.P_PROC, "D:\\data\\workspace\\集算器内部测试版.lic");
		ConfigUtil.load("D:\\data\\workspace\\guide\\web\\WEB-INF\\raqsoftConfig.xml");

//		System.out.println(Env.getDBSessionFactory("DataLogic"));
//		System.out.println(Env.getDBSessionFactory("DataLog8ic"));
		String a = "{\"fields\":[{\"tableAlias\":\"T1\",\"subTable\":\"\",\"aggr\":\"\",\"field\":\"数量\",\"alias\":\"数量\"}],\"dims\":[],\"dimWhere\":\"\",\"tables\":[{\"table\":\"订单明细\",\"alias\":\"T1\",\"bys\":[],\"where\":\"\",\"joinType\":\"JOIN\"}],\"having\":\"\"}";
		Map<String,String> c = new HashMap<String,String>();
		c.put("雇员", "${T}.姓名 like '${gg}'");
		c.put("日期", "${T}.日期 > '2013-02-05'");
		Map<String,String> p = new HashMap<String,String>();
		p.put("gg", "%张%");
		getDql(a,c,p,"DataLogic");
	}

}
