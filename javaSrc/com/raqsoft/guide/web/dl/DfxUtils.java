package com.raqsoft.guide.web.dl;

import java.io.InputStream;

import com.raqsoft.cellset.datamodel.PgmCellSet;
import com.raqsoft.common.Logger;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.FileObject;
import com.raqsoft.dm.JobSpace;
import com.raqsoft.dm.JobSpaceManager;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.util.CellSetUtil;
import com.raqsoft.util.Variant;

public class DfxUtils {

	
	public static String execUserDfxScript(String script, Context ctx) throws Exception {
		return execUserDfxScript(script, ctx, true);
	}
	public static String execUserDfxScript(String script, Context ctx, boolean closeJobSpace) throws Exception {
		String jsId = "jsId" + System.currentTimeMillis();
		JobSpace space = JobSpaceManager.getSpace(jsId);
		try{
			PgmCellSet pcs = CellSetUtil.toPgmCellSet(script);
			pcs.setContext(ctx);
			ctx.setJobSpace(space);
			Object o = pcs.execute();
			ctx.setParamValue("_returnValue_", o);
		}catch(Exception e){
			Logger.warn(e);
			throw e;
		}finally{
			if (closeJobSpace) JobSpaceManager.closeSpace(jsId);
		}
		return jsId;
	}
	
	public static String execDfxFile(String file, Context ctx) throws Exception {
		if (file.startsWith("/com/raqsoft")) return execDfxFile(DataSphereServlet.class.getResourceAsStream(file),ctx);
		return execDfxFile(file, ctx, true);
	}
	
	public static String execDfxFile(String file, Context ctx, boolean closeJobSpace) throws Exception {
		com.raqsoft.app.config.ConfigUtil.checkEsprocExpiration();
		if (file.startsWith("/com/raqsoft")) return execDfxFile(DataSphereServlet.class.getResourceAsStream(file),ctx,closeJobSpace);
		String jsId = "jsId" + System.currentTimeMillis();
		JobSpace space = JobSpaceManager.getSpace(jsId);
		try{
			FileObject fo = new FileObject(file, Env.getDefaultCharsetName(), "s", ctx);
			PgmCellSet pcs = fo.readPgmCellSet();
			pcs.setContext(ctx);
			ctx.setJobSpace(space);
			Object o = pcs.execute();
			ctx.setParamValue("_returnValue_", o);
		}catch(Exception e){
			Logger.warn(e);
			throw e;
		}finally{
			if (closeJobSpace) JobSpaceManager.closeSpace(jsId);
		}
		return jsId;
	}

	
	public static String execDfxFile(InputStream file, Context ctx) throws Exception {
		return execDfxFile(file, ctx, true);
	}
	public static String execDfxFile(InputStream file, Context ctx, boolean closeJobSpace) throws Exception {
		String jsId = "jsId" + System.currentTimeMillis();
		JobSpace space = JobSpaceManager.getSpace(jsId);
		try{
			PgmCellSet pcs = CellSetUtil.readPgmCellSet(file);
			
			pcs.setContext(ctx);
			ctx.setJobSpace(space);
			Object o = pcs.execute();
			ctx.setParamValue("_returnValue_", o);
		}catch(Exception e){
			Logger.warn(e);
			throw e;
		}finally{
			if (closeJobSpace) JobSpaceManager.closeSpace(jsId);
		}
		return jsId;
	}

	public static String toString(Object o) {
		if (o == null) return null;
		if (o instanceof java.sql.Time 
				|| o instanceof java.sql.Timestamp 
				|| o instanceof java.sql.Date 
				|| o instanceof String 
				|| o instanceof java.util.Date) {
			return "\""+Variant.toString(o)+"\"";
		} else return Variant.toString(o);
	}
}
