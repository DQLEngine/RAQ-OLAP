package com.raqsoft.guide.web;

import java.util.Set;

public interface IReportConfigManager {
	public ReportConfigModel getReportConfigModel();
	public String getInitParameter(String name);
	public Set getInitParameters();
	public JDBCDsConfigModel getJDBCDsConfigModel(String dsName);
	public JNDIDsConfigModel getJNDIDsConfigModel(String dsName);
	public String[] listDcModelkeys();
	public void setInputStream(java.io.InputStream inputStream);
	public void setParameterValue(Object obj, Object obj1);
}
