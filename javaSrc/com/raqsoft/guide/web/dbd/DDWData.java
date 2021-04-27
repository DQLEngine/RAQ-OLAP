package com.raqsoft.guide.web.dbd;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.raqsoft.cellset.datamodel.PgmCellSet;
import com.raqsoft.common.DBSession;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.common.Types;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.JobSpace;
import com.raqsoft.dm.JobSpaceManager;
import com.raqsoft.dm.Param;
import com.raqsoft.dm.Record;
import com.raqsoft.dm.Sequence;
import com.raqsoft.guide.resource.GuideMessage;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.dl.DfxData;
import com.raqsoft.guide.web.dl.DfxQuery;
import com.raqsoft.guide.web.dl.DfxUtils;
import com.raqsoft.report.usermodel.BuiltinDataSetConfig;
import com.raqsoft.util.CellSetUtil;

public class DDWData {
	public void queryData(HttpServletResponse res, HttpServletRequest req){
		PrintWriter writer = null;
		try {
			writer = res.getWriter();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		String dqlField = req.getParameter("dqlField");
		String dataSource = req.getParameter("dataSource");
		
		String table = dqlField.substring(0,dqlField.indexOf("."));
		StringBuffer dql = new StringBuffer("SELECT DISTINCT ");
		dql.append(dqlField).append(" FROM ").append(table);
		JSONObject calcFieldTypeJSON = null;
		try{
			if(req.getParameter("calcFieldTypeJSON") != null && req.getParameter("calcFieldTypeJSON").length() > 0)
				calcFieldTypeJSON = new JSONObject(req.getParameter("calcFieldTypeJSON"));
		}catch(Exception e){
			Logger.debug(e.getMessage());
		}
//		HashMap calcFieldsTypes = new HashMap();
//		Iterator it = calcFieldTypeJSON.keys();
//		while(it.hasNext()){
//			calcFieldTypeJSON.get((String) it.next());
//		}
		BuiltinDataSetConfig bdsc = new BuiltinDataSetConfig();
		bdsc.setName("ds1");
		int count = 1;
		Logger.debug("queryDqlData : " + dql);

		DBSession dbs = null;
		Connection con = null;
		Statement stmt = null;
		try {
			ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dataSource);
			dbs = isf.getSession();
			con = (Connection)dbs.getSession();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(1000);
			ResultSet rs = stmt.executeQuery(dql.toString());
			java.sql.ResultSetMetaData rsmd = rs.getMetaData();
			String names[] = new String[rsmd.getColumnCount()];
			byte colTypes[] = new byte[rsmd.getColumnCount()];
			for (int i=1; i<=rsmd.getColumnCount(); i++) {
				names[i-1] = rsmd.getColumnLabel(i);
				colTypes[i-1] = Types.getTypeBySQLType(rsmd.getColumnType(i));
				if(calcFieldTypeJSON != null) {
					try{
						Object cftype = calcFieldTypeJSON.get(names[i-1]);
						if(cftype != null && cftype instanceof String){//是计算字段
							String cftypeString = (String) cftype;
							if(cftypeString.length() > 0) {
								colTypes[i-1] = DfxData.getType(cftypeString);
							}
						}
					}catch(org.json.JSONException e){}
				}
			}
			StringBuffer editorConfig = new StringBuffer();
			editorConfig.append("[");
			editorConfig.append("{\"v\":null,\"d\":\"\"}");
			while (rs.next()) {
				count++;
				String value = null;
				Object o9 = rs.getObject(names[0]);
				value = o9==null?"":o9.toString();
				if(value != null) editorConfig.append(",{\"v\":\"").append(value).append("\",\"d\":\"").append(value).append("\"}");
			}
			editorConfig.append("]");
			writer.println(count+"|||"+editorConfig.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

	public void calcData(HttpServletResponse res, HttpServletRequest req) throws Exception {//(String calcs, String filters, String fields, String resultExp, String cacheType, String types,String dataFileType, int maxDataSize, String isGlmd,String srcTypes, String aggrFieldFilters,boolean isQuery)
		PrintWriter writer = null;
		try {
			writer = res.getWriter();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		String dqlField = req.getParameter("dqlField");
		
		HttpSession session = req.getSession();
		String reportId = req.getParameter("reportId");
		String dataFileType = req.getParameter("dataFileType");
		String dataId = req.getParameter("dataId");
		String cacheType = "1";
		try {
			Logger.debug("ddwdata calculating data file : " + dataId);
			File f = new File(DataSphereServlet.getFilePath(dataId));
			if (!f.exists()) {
				//数据加载失败，过期已被删除 或 查询不到符合条件的数据，要重新查询吗？
				writer.write("{error:'"+GuideMessage.get(req).getMessage("guide.requery")+"',action:'reQuery'}");
				return;
			}
			Object o2 = session.getAttribute(reportId);
			DfxData dd = null;
			if (o2 != null && o2 instanceof DfxData && !"where".equals(reportId)) {
				dd = (DfxData)o2;
			} else {
				dd = new DfxData(DataSphereServlet.getFilePath(dataId));//(DfxDataSetManager.getFile(dataId));//("D:/data/workspace/guide/web/WEB-INF/tmp/order");
				dd.setReportId(reportId);
				session.setAttribute(reportId, dd);
			}

			DfxQuery dq = null;
			Object o3 = session.getAttribute(dataId);
			if (o3 != null && o3 instanceof DfxQuery) {
				dq = (DfxQuery)o3;
			}
			if (dq != null) dq.setPause(true);
			
			if("binary".equalsIgnoreCase(req.getParameter("dataFileType")) && !DataSphereServlet.binAuth){
				//验证集算器授权ip不可用
				throw new Exception("对不起，集算器授权不可用，不能使用bin格式缓存");
			}
			if (dq != null) dq.setPause(false);
		} catch (Exception e) {
			e.printStackTrace();
			String err = e.getMessage().replaceAll("\n\r", " ").replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("'", " ").replaceAll("\"", " ");
			//错误信息：{0}！ 要重新查询数据吗？
			writer.write("{error:'"+GuideMessage.get(req).getMessage("guide.requery2",err)+"',action:'reQuery'}");
			session.removeAttribute(reportId);
		}
		
		
		
		Object[] os = new Object[2];
		String jsId = "jsId" + System.currentTimeMillis();
		JobSpace space = JobSpaceManager.getSpace(jsId);
		try{
			String dfx = DataSphereServlet.DFX_REPORT;
			if (!"binary".equalsIgnoreCase(dataFileType)) dfx = DataSphereServlet.DFX_REPORT_TXT;
			//dfx = "D:\\data\\workspace\\guide\\web\\WEB-INF\\dfx\\report.dfx";
			PgmCellSet pcs = CellSetUtil.readPgmCellSet(DataSphereServlet.class.getResourceAsStream(dfx));
			Context ctx = pcs.getContext();
			//ParamList pl = ctx.getParamList();
			String calcs = "no";
			String filters = "no";
			String fields = "no";
			String aggrFieldFilters = null;
			Sequence s1 = getSeq(calcs);
			Sequence s2 = getSeq(filters);
			Sequence s3 = getSeq(fields);
			Sequence s4 = getSeq(aggrFieldFilters);
			int len = s1.length();
			if (len<s2.length()) len = s2.length();
			if (len<s3.length()) len = s3.length();
			for (int i=0; i<len-s1.length(); i++) s1.add("");
			for (int i=0; i<len-s2.length(); i++) s2.add("");
			for (int i=0; i<len-s3.length(); i++) s3.add("");
			ctx.setParamValue("calcs", s1);
			ctx.setParamValue("filters", s2);
			ctx.setParamValue("fields", s3);
			ctx.setParamValue("aggrFieldFilters", s4);
			ctx.setParamValue("resultExp", "new("+dqlField+":"+dqlField+")");
			ctx.setParamValue("dataFileType", dataFileType);
			ctx.setParamValue("dataFile", DataSphereServlet.getFilePath(dataId));
			String esProcTypes = "";
			ctx.setParamValue("types", esProcTypes);
			ctx.setJobSpace(space);
			Object o = pcs.execute();
			//执行dfx完毕
			Param p1 = ctx.getParam("guideResult");
			Param p2 = ctx.getParam("finish");
			os[0] = p1.getValue();
			os[1] = p2.getValue();
			Sequence seq = (Sequence)os[0];
			StringBuffer editorConfig = new StringBuffer();
			editorConfig.append("[");
			editorConfig.append("{\"v\":null,\"d\":\"\"}");
			for (int i=1; i<=seq.length(); i++) {
				if (seq.get(i) == null || "null".equals(seq.get(i)) || "".equals(seq.get(i))) continue;
				Record record = (Record) seq.get(i);
				String value = record.getFieldValue(0).toString();
				if(value != null) editorConfig.append(",{\"v\":\"").append(value).append("\",\"d\":\"").append(value).append("\"}");
			}
			editorConfig.append("]");
			writer.println(seq.length()+"|||"+editorConfig.toString());
		}finally{
			JobSpaceManager.closeSpace(jsId);
		}
	}
	
	private static Sequence getSeq(String s) {
		Sequence seq = new Sequence();
		if (s == null) return seq;
		String ss[] = s.split("<;>");
		for (int i=0; i<ss.length; i++) seq.add(ss[i]); 
		return seq;
	}
}
