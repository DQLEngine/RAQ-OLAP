package com.raqsoft.guide.web.dl;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspWriter;

import com.datalogic.jdbc.DQLFieldsAttr;
import com.datalogic.jdbc.FieldAttr;
import com.raqsoft.cellset.datacalc.CalcCellSet;
import com.raqsoft.common.DBSession;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.dm.DataStruct;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.Table;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.querys.QueryTask;
import com.raqsoft.ide.gex.GMGex;
import com.raqsoft.report.usermodel.Context;
import com.raqsoft.util.CellSetUtil;

public class ResultPage {
	public static boolean COUNT_TOTAL_FIRST = false;
	public static int CACHE_NUM = 5;//缓存几页
	
//	private ResultSet rs;
	private int pageCount = 50;//每页行数
	private int currPage = 0;//当前页数
	private boolean reachEnd = false;
	private boolean title;
	private QueryTask tsk;
	private DQLFieldsAttr dqlfa;
	private DBSession dbs;
	private Connection con;
	private String initAttrs = null; 
	public ResultPage(){
//			Logger.debug(dqlfa.toString());
	}

	private ResultSet rs = null;
	public ResultPage(String dbName, String dql) throws Exception{
		ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(dbName);

		Logger.debug(new String("do new query!"));
		dbs = isf.getSession();
		con = (Connection)dbs.getSession();
		Logger.debug(new String("3.." + con));
		rs = con.createStatement().executeQuery(dql);
	}
	public ResultSet getResultSet() {
		return rs;
	}

//
//	public void cancel() throws SQLException {
//		tsk.cancel();
//	}


	public String status = "info:正在查询......";
	public String status() {
		if (status.startsWith("error:")) return status;
		if (tsk != null) {
			if (tsk.getEndTime() == 0) {
				if (tsk.getStartTime() == 0) {
					status = "info_back:等待执行......";
				} else {
					status = "info:正在查询......";
				}
			} else {
				if (tsk.getErrorInfo() != null && tsk.getErrorInfo().length() > 0) status = "error:" + tsk.getErrorInfo();
			}
		}
		return status;
	}


	private Object getDispValue(Object v, FieldAttr fa) {
		if (v == null) return null;
		if (fa == null || !fa.isUseDisp()) return v;
		Map dt = fa.getDispTable();
		if (dt == null) return v;
		Object disp = dt.get(v.toString());
		if (disp == null) return v;
		else return disp;
	}
	public void gex(String file) throws Exception {
//		GMGex.table2Gex(arg0, arg1)
		int rowCount = 0;
		int max = Integer.MAX_VALUE;

		String title[] = new String[tsk.getColumns().size()];
		for (int i=0; i<tsk.getColumns().size(); i++) {
			String si = tsk.getColumns().get(i).toString();
			title[i] = si;
		}
		DataStruct ds = new DataStruct(title);
		Table t = new Table(ds);
		//Sequence s = new Sequence();
		//s.dataStruct()
		boolean over = true;
		for (int i=0; i<Integer.MAX_VALUE; i++) {
			List row = tsk.getRowData(i, false);
			if (row == null || row.size() != tsk.getColumns().size()) {
				reachEnd = true;
				break;
			}
			this.currPage = i / pageCount + 1;
			if (max == 0) {
				over = false;
				status = "info:导出权限使用完，数据未完全导出！";
				break;
			}
			if (rowCount%1000 == 0) status = "处理到第" + rowCount + "条数据";
			String rowi[] = new String[tsk.getColumns().size()];
			//Sequence rowi = new Sequence();
			for (int j=0; j<this.tsk.getColumns().size(); j++) {
				if (dqlfa == null) generateHeader(null);
				FieldAttr fa = dqlfa.getFieldAttr(j+1);
				if (fa == null) fa = new FieldAttr();
				String sj = (j>0?"":"") + ConfigUtil.format(getDispValue(row.get(j), fa), fa.getFormat());
				//rowi.add(sj);
				rowi[j] = sj;
				//if (pw != null) pw.print(sj);
				//if (fos != null) fos.write(sj.getBytes(DataSphereServlet.qyxCharset));
			}
			t.insert(0, rowi);
			//if (pw != null) pw.print("\r\n");
			//if (fos != null) fos.write("\r\n".getBytes(DataSphereServlet.qyxCharset));
			
			rowCount++;
			max--;
		}
		
		this.closeResultSet();

		if (over) status = "success";
		
		CalcCellSet ccs = GMGex.table2Gex(t, title);
		CellSetUtil.writeCalcCellSet(file, ccs);
	}
	
	public void txt(PrintWriter pw, FileOutputStream fos) throws Exception {
		while (tsk.getEndTime() == 0) {
			Thread.currentThread().sleep(1000);
		}
		status = "开始查询数据......";
		//StringBuffer sb = new StringBuffer();
//		UserDataRows edr = ui.getExportDataRows();
//		long perTime = ui.getExportMaxRowsPerTime();
//		long perDay = ui.getExportMaxRowsPerDay();
//		long perMonth = ui.getExportMaxRowsPerMonth();
		//Logger.debug("row = " + perTime + "--" + perDay + "--" + perMonth + "--" + edr.getDayRows() + "--" + edr.getMonthRows());
		long max = Integer.MAX_VALUE;
//		if (perTime > 0) {
//			max = perTime;
//		}
//		if (perDay > 0) {
//			long dayLeft = perDay - edr.getDayRows();
//			if (dayLeft <= 0) throw new Exception("Exceed everyday's max row count");
//			if (max > dayLeft) max = dayLeft;
//		}
//		if (perMonth > 0) {
//			long monthLeft = perMonth - edr.getMonthRows();
//			if (monthLeft <= 0) throw new Exception("Exceed per month's max row count");
//			if (max > monthLeft) max = monthLeft;
//		}
		
		int rowCount = 0;
//		try {
//			rs.last();
//			rowCount = rs.getRow();
//			rs.beforeFirst();
//		} catch (SQLException e) {
//		}
//		Logger.debug("data row = " + rowCount);
//		if (perTime > 0 && rowCount > perTime) throw new Exception("Result Set too large!");
//		if (perDay > 0 && edr.getDayRows() + rowCount > perDay) throw new Exception("Exceed everyday's max row count");
//		if (perMonth > 0 && edr.getMonthRows() + rowCount > perMonth) throw new Exception("Exceed per month's max row count");
		for (int i=0; i<tsk.getColumns().size(); i++) {
			String si = tsk.getColumns().get(i).toString() + "\t";
			if (pw != null) pw.print(si);
			if (fos != null) fos.write(si.getBytes(Context.getJspCharset()));
		}
		if (pw != null) pw.print("\r\n");
		if (fos != null) fos.write("\r\n".getBytes(Context.getJspCharset()));
		boolean over = true;
		for (int i=0; i<Integer.MAX_VALUE; i++) {
			List row = tsk.getRowData(i, false);
			if (row == null || row.size() != tsk.getColumns().size()) {
				reachEnd = true;
				break;
			}
			this.currPage = i / pageCount + 1;
			if (max == 0) {
				over = false;
				status = "info:导出权限使用完，数据未完全导出！";
				break;
			}
			if (rowCount%1000 == 0) status = "处理到第" + rowCount + "条数据";
			for (int j=0; j<this.tsk.getColumns().size(); j++) {
				if (dqlfa == null) generateHeader(null);
				FieldAttr fa = dqlfa.getFieldAttr(j+1);
				if (fa == null) fa = new FieldAttr();
				String sj = (j>0?"\t":"") + ConfigUtil.format(getDispValue(row.get(j), fa), fa.getFormat());
				if (pw != null) pw.print(sj);
				if (fos != null) fos.write(sj.getBytes(Context.getJspCharset()));
			}
			if (pw != null) pw.print("\r\n");
			if (fos != null) fos.write("\r\n".getBytes(Context.getJspCharset()));
			rowCount++;
			max--;
		}
		
		this.closeResultSet();

//		Statement stat = null;
//		try {
//			stat = rs.getStatement();
//		} catch (Exception e) {}
//		Connection con = null;
//		try {
//			con = stat.getConnection();
//		} catch (Exception e) {}
//		try {
//			rs.close();
//		} catch (Exception e) {}
//		try {
//			stat.close();
//		} catch (Exception e) {}
//		try {
//			con.close();
//		} catch (Exception e) {}

//		edr.add(rowCount);
//		ui.writeExportDataRows(edr);
		if (over) status = "success";
		//return sb.toString();
	}

	//页面关闭、TODO(session失效)
	public void closeResultSet() throws SQLException {
		try {
			Logger.debug(new String("con close begin 1"));
			con.close();
			Logger.debug(new String("con close end 1"));
		} catch (Exception e) {}
		try {
			dbs.close();
		} catch (Exception e) {}
		if (rs != null) {
			Statement stat = null;
			try {
				stat = rs.getStatement();
			} catch (Exception e) {}
			Connection con = null;
			try {
				con = stat.getConnection();
			} catch (Exception e) {}
			try {
				rs.close();
			} catch (Exception e) {}
			try {
				stat.close();
			} catch (Exception e) {}
			try {
				Logger.debug(new String("con close begin 2"));
				con.close();
				Logger.debug(new String("con close end 2"));
			} catch (Exception e) {}
		}
		if (tsk != null) {
			tsk.cancel();
		}
	}
//
//	//页面关闭、TODO(session失效)
//	public void cancel() throws SQLException {
//		try {con.close();} catch (Exception e) {}
//		if (rs != null) {
//			Statement stat = null;
//			try {
//				stat = rs.getStatement();
//			} catch (Exception e) {}
//			Connection con = null;
//			try {
//				con = stat.getConnection();
//			} catch (Exception e) {}
//			try {
//				rs.close();
//			} catch (Exception e) {}
//			try {
//				stat.close();
//			} catch (Exception e) {}
//			try {
//				con.close();
//			} catch (Exception e) {}
//		}
//		if (tsk != null) {
//			tsk.cancel();
//		}
//	}

	public static void main(String args[]) throws Exception {
//		Context ctx = new Context();
//		LogicMetaData lmd = LogicMetaData.readLogicMetaData("D:\\workspace\\datalogic\\web\\WEB-INF\\classes\\newdemo120611.xml");
//		lmd.prepare();
//		ctx.setLogicMetaData(lmd);
////		ctx.setDBType(DBTypes.SQLSVR);
//		Translator trans = new Translator("select 员工表.count(姓名) from 员工表 by 年龄", ctx);
//
//		Logger.debug(trans.toNativeSQL());

	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public boolean isReachEnd() {
		return reachEnd;
	}

	public int getCurrPage() {
		return currPage;
	}
	
	private void generateHeader(JspWriter pw) throws Exception {
		if (title) {
			int count = tsk.getColumns().size();
			int tw = 0;
			if (dqlfa == null) {
				dqlfa = new DQLFieldsAttr(initAttrs, con);
				for (int i=0; i<count; i++) {
					FieldAttr fa = dqlfa.getFieldAttr(i + 1);
					if (fa == null) {
						fa = new FieldAttr();
						dqlfa.setFieldAttr(i + 1, fa);
					}
					fa.setName(tsk.getColumns().get(i).toString());
					tw += dqlfa.getFieldAttr(i+1).getWidth();
				}
			}
			dqlfa.mergeAttr(tsk.getAttrs());
			tsk.setAttrs(dqlfa.toString());
			if (pw == null) return;
			pw.print("<style>.resultTableClass td{border:1px solid #BFC2C6;overflow:hidden;}</style><table class='resultTableClass' style='border-collapse:collapse;font-size:12px;table-layout:fixed;width:" + tw + "px;'><tr id='titleTr'>");
			for (int i=0; i<count; i++) {
				FieldAttr fa = dqlfa.getFieldAttr(i+1);
				String useDisp = "";
				if (fa.getSql() != null && fa.getSql().length() > 0) useDisp = " useDisp=" + (fa.isUseDisp()?"1":"0");
				pw.print("<th format='" + fa.getFormat() + "'" + useDisp + " style='border:1px solid #BFC2C6;width:" + fa.getWidth() + "px'>" + tsk.getColumns().get(i) + "</th>");
			}
			pw.print("</tr>");
		}
	}
	
	public void prev(JspWriter pw, String attrs) throws Exception {
		while (tsk.getEndTime() == 0) {
			Thread.currentThread().sleep(1000);
		}
		status = "info:开始查询数据......";
		tsk.setAttrs(attrs);
		try {
			if (currPage == 1) {
				pw.print("error:已经是最前页");
				status = "error:已经是最前页";
				return;
			}
			reachEnd = false;
			currPage--;
			if (title) generateHeader(pw); 
			for (int i=(currPage-1) * pageCount; i<currPage * pageCount; i++) {
				if (i%1000 == 0) status = "info:处理到第" + i + "条数据";
				List row = tsk.getRowData(i, true);
				if (row == null) {
					currPage++;
					pw.print("error:已经是最前页");
					status = "error:已经是最前页";
					return;
				}
				reachEnd = (row == null);
				if (reachEnd) break;
				pw.print("<tr>");
				for (int k=1; k<=row.size(); k++) {
					FieldAttr fa = dqlfa.getFieldAttr(k);
					if (fa == null) fa = new FieldAttr();
					String str = ConfigUtil.format(getDispValue(row.get(k-1), fa), fa.getFormat());
					pw.print("<td>" + str + "</td>");
				}
				pw.print("</tr>");
			}
			if (title) pw.print("</table>");
			pw.print("<input type='hidden' id='pageNum' value='" + currPage + "'><input type='hidden' id='reachEnd' value='" + (reachEnd?"1":"0") + "'>");
			status = "success";
		} catch (SQLException e) {
			Logger.error("",e);
			e.printStackTrace();
			status = "error:" + e.getMessage();
			pw.print(status);
		}
	}

	public void refresh(JspWriter pw, String attrs) throws Exception {
		while (tsk.getEndTime() == 0) {
			Thread.currentThread().sleep(1000);
		}
		status = "info:开始查询数据......";
		tsk.setAttrs(attrs);
		try {
			if (title) generateHeader(pw); 
			for (int i=(currPage-1) * pageCount; i<currPage * pageCount; i++) {
				if (i%1000 == 0) status = "info:处理到第" + i + "条数据";
				List row = tsk.getRowData(i, true);
				reachEnd = (row == null);
				if (reachEnd) break;
				pw.print("<tr>");
				for (int k=1; k<=row.size(); k++) {
					FieldAttr fa = dqlfa.getFieldAttr(k);
					if (fa == null) fa = new FieldAttr();
					String str = ConfigUtil.format(getDispValue(row.get(k-1), fa), fa.getFormat());
					pw.print("<td>" + str + "</td>");
				}
				pw.print("</tr>");
			}
			if (!reachEnd) reachEnd = (tsk.getRowData(currPage * pageCount, true) == null);//多取一条。验证是不是到了最后页。
			if (title) pw.print("</table>");
			pw.print("<input type='hidden' id='pageNum' value='" + currPage + "'><input type='hidden' id='reachEnd' value='" + (reachEnd?"1":"0") + "'>");
			status = "success";
		} catch (SQLException e) {
			Logger.error("",e);
			e.printStackTrace();
			status = "error:" + e.getMessage();
			pw.print(status);
		}
	}
	
	public void next(JspWriter pw, String attrs) throws Exception {
		while (tsk.getEndTime() == 0) {
			Thread.currentThread().sleep(1000);
		}
		status = "info:开始查询数据......";
		tsk.setAttrs(attrs);
		try {
			//Logger.debug("--" + reachEnd);
			if (reachEnd) {
				pw.print("error:已经达到最后页");
				status = "error:已经达到最后页";
				return;
			}
			currPage++;
			if (title) generateHeader(pw); 
			for (int i=(currPage-1) * pageCount; i<currPage * pageCount; i++) {
				//Thread.currentThread().sleep(500);
				if (i%1000 == 0) 
					status = "info:处理到第" + i + "条数据";
				List row = tsk.getRowData(i, true);
				reachEnd = (row == null);
				if (reachEnd) break;
				pw.print("<tr>");
				for (int k=1; k<=row.size(); k++) {
					FieldAttr fa = dqlfa.getFieldAttr(k);
					if (fa == null) fa = new FieldAttr();
					String str = ConfigUtil.format(getDispValue(row.get(k-1), fa), fa.getFormat());
					//Logger.debug(row.get(k-1) + "--" + str);
					pw.print("<td>" + str + "</td>");
				}
				pw.print("</tr>");
			}
			if (!reachEnd) reachEnd = (tsk.getRowData(currPage * pageCount, true) == null);//多取一条。验证是不是到了最后页。
			if (title) pw.print("</table>"); 
			pw.print("<input type='hidden' id='pageNum' value='" + currPage + "'><input type='hidden' id='reachEnd' value='" + (reachEnd?"1":"0") + "'>");
			status = "success";
		} catch (SQLException e) {
			Logger.error("",e);
			e.printStackTrace();
			status = "error:" + e.getMessage();
			pw.print(status);
		}
	}
	public QueryTask getTsk() {
		return tsk;
	}
	public void setTsk(QueryTask tsk) throws Exception {
		this.tsk = tsk;
		this.title = true;
		Logger.debug(new String("Logic Sql:[" + tsk.getQuery() + "]"));
		ISessionFactory isf = (ISessionFactory)Env.getDBSessionFactory(tsk.getDBName());
		dbs = isf.getSession();
		con = (Connection)dbs.getSession();
		if (con.getClass().toString().indexOf("LogicDriver") >= 0) {
			Logger.debug(new String("4.." + con));
			ResultSet set = con.createStatement().executeQuery("result " + tsk.getQuery());
			while (set.next()) {
				initAttrs = set.getString(1);
				Logger.debug(new String("initAttrs:[" + initAttrs + "]"));
			}
			set.close();
		}
	}
	
	
}
