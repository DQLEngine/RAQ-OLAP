package com.raqsoft.guide.web.model;

import java.util.HashMap;
import java.util.Map;

public class SqlDataset {
	
	public static Map<String,SqlDataset> DATASETS = new HashMap<String,SqlDataset>();
	
	private String sqlId;
	public String getSqlId() {
		return sqlId;
	}
	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	private String sql;
	private String dataSource;

}
