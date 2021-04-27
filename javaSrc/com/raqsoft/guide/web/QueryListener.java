package com.raqsoft.guide.web;

public interface QueryListener {
	public void executeFinished( String dbName, String user, String dql, long beginTime, long endTime );
}
