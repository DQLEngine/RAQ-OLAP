package com.raqsoft.guide.web.dl;

import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Record;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.Table;
import com.raqsoft.guide.web.DataSphereServlet;

public class DqlService {
	
	private String dataSource;
	private String metadata;
	private Table md = null;
	private Table tables = null;
	private Table dims = null;
	private Table annexTables = null;
	
	private String dict;
	
	public DqlService(String dataSource, String dictionary) throws Exception {
	}
	public DqlService(String dataSource) throws Exception {
		this.dataSource = dataSource;
		this.metadata = ConfigUtil.getMetaDataJson(dataSource);
		Context ctx = new Context();
		ctx.setParamValue("jsonStr", this.metadata);
		DfxUtils.execDfxFile(DataSphereServlet.class.getResourceAsStream("/com/raqsoft/guide/web/dfx/readJson.dfx"), ctx);
		Object o = ctx.getParam("jsonObj");
		if (o != null) {
			md = (Table)o;
			Record r = md.getRecord(1);
			tables = (Table)r.getFieldValue("tables");
			dims = (Table)r.getFieldValue("dims");
			annexTables = (Table)r.getFieldValue("annexTables");
		} throw new RQException("not found metadata Object");
	}
	
	public String getMetadata() {
		return this.metadata;
	}
	
	public Table getRelations(String table, String dim, int deep) {
		return null;
	}
	
	public Table getFields(String table, int deep) {
		Table t = new Table("".split(","));
		
		Record tObj = getTable(table);
		
		return null;
	}
	
	private Record getTable(String t) {
		for (int i=1; i<=tables.length(); i++) {
			Record ri = tables.getRecord(i);
			if (ri.getFieldValue("name").equals(t)) return ri;
		}
		return null;
	}
}
