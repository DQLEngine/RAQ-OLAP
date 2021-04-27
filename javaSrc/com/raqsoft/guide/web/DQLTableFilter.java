package com.raqsoft.guide.web;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.raqsoft.common.RQException;
import com.raqsoft.guide.util.DqlUtil;
import com.raqsoft.logic.metadata.MacroList;
import com.raqsoft.logic.metadata.MacroManager;
import com.raqsoft.logic.metadata.UserMacro;
import com.raqsoft.logic.metadata.UserMacroList;

public class DQLTableFilter {
	
	private String ID;
	private String dataSource;
	private Map<String,String> filters = new HashMap<String,String>();
	private Map<String,String> paramValues = new HashMap<String,String>();
	
	public String getID() {
		return ID;
	}


	public void setID(String iD) {
		ID = iD;
	}


	public String getDataSource() {
		return dataSource;
	}


	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}


	public Map<String, String> getFilters() {
		return filters;
	}


	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}


	public Map<String, String> getParamValues() {
		return paramValues;
	}


	public void setParamValues(Map<String, String> paramValues) {
		this.paramValues = paramValues;
	}

	
	
	public DQLTableFilter(String ID, String dataSource) {
		this.ID = ID;
		this.dataSource = dataSource;
	}
	
	public void setVsb(String vsbFile) throws Exception {
		DqlUtil.getVsbConditions(vsbFile,filters);
	}
	
//	public void setMacroValue(String macroFile,String user) throws Exception{
//		
//	}
	
	
	public void setMacro(String macroFile,String user) throws Exception{
		macroFile = DataSphereServlet.getFilePath(macroFile);
		if (!new File(macroFile).exists()) throw new RQException("no macro file!");
		MacroManager macroManager = new MacroManager();
		macroManager.readFrom(macroFile);
		UserMacroList list = macroManager.userMacroList;
		if (list == null) throw new RQException("has no user in macro file!");
		for (int i=0; i<list.size(); i++) {
			UserMacro um = list.get(0);
			if (user != null) um = list.getByName(user);
			if (um == null) throw new RQException("has no user in macro file!");
			MacroList ml = um.getMacroList();
			if (ml == null) throw new RQException("has no user in macro file!");
			for (int j=0; j<ml.size(); j++) {
				this.paramValues.put(ml.get(j).getName(), ml.get(j).getValue());
			}
		}
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
