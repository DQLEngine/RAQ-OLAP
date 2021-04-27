package com.raqsoft.guide.web.dl;

import java.util.HashMap;

import com.raqsoft.common.Logger;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.report.cache.CacheManager;
import com.raqsoft.report.model.ReportDefine;
import com.raqsoft.report.usermodel.IReport;

public class ReportConf {
	private String qyx;
	private String dql;//原始dql
	private String dbName;
	private String reportId;
	private HashMap rss = new HashMap(); //含有修改后的“dql”
	private IReport iReport;
	private ReportDefine reportDefine;
	private IReport graphReport;
	private ReportDefine graphReportDefine;
	private boolean showTotal = true;
	private String reportType = "grid"; // group,olap
	private String reportDetail;
	private String style = "style1";
	
	private String txts;
	private String dimName;
	private String txtFields;
	
	public ReportConf(String dbName, String dql, String rid) {
		
	}
	
	public ReportConf() {
		
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getQyx() {
		return qyx;
	}
	public void setQyx(String qyx) {
		this.qyx = qyx;
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public IReport getIReport() {
		return iReport;
	}
	public void setIReport(IReport report) {
		if (iReport != null) {
			iReport = null;
			CacheManager manager = CacheManager.getInstance();
			manager.deleteReportEntry(this.getReportId());
//			Iterator iter = manager.getReportEntries().keySet().iterator();
//			while (iter.hasNext()) {
//				Logger.debug("----" + iter.next());
//			}
		}
		//Logger.debug("----setIReport");
		iReport = report;
	}
	public HashMap getRss() {
		return rss;
	}
	public void setRss(HashMap rss) {
		this.rss = rss;
	}

	public boolean isShowTotal() {
		return showTotal;
	}

	public void setShowTotal(boolean showTotal) {
		this.showTotal = showTotal;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getReportDetail() {
		return reportDetail;
	}

	public void setReportDetail(String reportDetail) {
		this.reportDetail = reportDetail;
	}

	public String getDql() {
		return dql;
	}

	public void setDql(String dql) {
		this.dql = dql;
	}

	public IReport getGraphReport() {
		return graphReport;
	}

	public void setGraphReport(IReport graphReport) {
		this.graphReport = graphReport;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public ReportDefine getGraphReportDefine() {
		return graphReportDefine;
	}

	public void setGraphReportDefine(ReportDefine graphReportDefine) {
		this.graphReportDefine = graphReportDefine;
	}

	public ReportDefine getReportDefine() {
		return reportDefine;
	}

	public void setReportDefine(ReportDefine reportDefine) {
		this.reportDefine = reportDefine;
	}

	public String getTxts() {
		return txts;
	}
//	
//	public String getTxtFulls() {
//		String[] arr = txts.split(",");
//		String f = "";
//		for (int i=0; i<arr.length; i++) {
//			if (i>0) f += ",";
//			f += DataSphereServlet.ROOT_PATH + "/WEB-INF/txts/" + arr[i];
//		}
//		return f;
//	}
//	
//	public String[] getTxtsArr() {
//		String[] arr = txts.split(",");
//		for (int i=0; i<arr.length; i++) {
//			arr[i] = DataSphereServlet.ROOT_PATH + "/WEB-INF/txts/" + arr[i];
//		}
//		return arr;
//	}
	
	

	public void setTxts(String txts) {
		this.txts = txts;
	}

	public String getDimName() {
		return dimName;
	}

	public void setDimName(String dimName) {
		this.dimName = dimName;
	}

	public String getTxtFields() {
		return txtFields;
	}

	public void setTxtFields(String txtFields) {
		this.txtFields = txtFields;
	}
}
