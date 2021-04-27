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

public class SqlUtil {
	
	/**
	 *

	{
		fields : [
			{
				aggr:'sum'
				,field:'f1'
				,alias:'a1'
			}
		]
		, where : ''
		, having : 'having1'
	}
		
	 * 
	 * @param dqlSegments
	 * @return
	 */
	public static String getSql(String sqlSegments) throws Exception {
		Logger.debug(sqlSegments);
		String sql = "SELECT ";
		String groupBy = "";
		boolean hasAggr = false;
		Context ctx = new Context();
		ctx.setParamValue("jsonStr", sqlSegments);
		DfxUtils.execDfxFile(DataSphereServlet.class.getResourceAsStream("/com/raqsoft/guide/web/dfx/readJson.dfx"), ctx);
		Object o = ctx.getParam("jsonObj").getValue();
		Record r = (Record)o;
		o = r.getFieldValue("fields");
		Table fields = null;
		if (o != null && o instanceof Table) fields = (Table)o;
		String where = (String)r.getFieldValue("where");
		String having = (String)r.getFieldValue("having");

		for (int i=1; i<=fields.length(); i++) {
			Record fi = fields.getRecord(i);
			if (i>1) sql += ",";
			String aggr = (String)fi.getFieldValue("aggr");
			if (aggr == null) {
				if (groupBy.length()>0) groupBy += ",";
				
			} else {
				
			}
		}
		

		Logger.debug("sql : [" + sql + "]");
		return sql;
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
		getSql("");
	}

}
