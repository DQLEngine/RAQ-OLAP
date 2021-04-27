package com.raqsoft.guide.util;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.raqsoft.common.Logger;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.dl.SaveUtil;
import com.raqsoft.report.usermodel.Context;

public class OlapFileUtils {
	String olapStr = null;
	JSONObject olapJson = null;
	/**
	 * 解析olap文件成json
	 * olapPath 文件相对路径 相对于项目路径
	 */
	public void parseOlap(String olapPath){
		if (olapPath != null && olapPath.length() > 0) { 
			File f = new File(olapPath);
			Context.setJspCharset("utf-8");
			try {
				if (f.exists()) {
					olapStr = SaveUtil.readFile(f);
				} else {
					f = new File(DataSphereServlet.getFilePath(olapPath));
					if (f.exists()) {
						olapStr = SaveUtil.readFile(f);
					} else {
						Logger.info(olapPath+" not exist");
						return;
					}
				}
				if(olapStr != null) this.toJson(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void toJson(String olapStr) {
		if(olapStr == null) olapStr = this.olapStr;
		if(olapStr == null) return;
		if(olapStr.length() > 0) {
			olapStr = olapStr.replaceAll("\r", "").replaceAll("\n", "").replace("<d_q>","\"");
			try {
				this.olapJson = new org.json.JSONObject(olapStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<String> getRpxNames(){
		try {
			ArrayList<String> rpxNames = new ArrayList<String>();
			JSONArray rpxs =  olapJson.getJSONArray("rpxs");
			for(int i = 0; i < rpxs.length(); i++){
				JSONObject rpxobj = (JSONObject) rpxs.get(i);
				rpxNames.add(rpxobj.getString("name"));
			}
			return rpxNames;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<String> getDataSetNames(){
		try {
			ArrayList<String> dataSetNames = new ArrayList<String>();
			JSONArray rpxs =  olapJson.getJSONArray("dataSets");
			for(int i = 0; i < rpxs.length(); i++){
				JSONObject rpxobj = (JSONObject) rpxs.get(i);
				dataSetNames.add(rpxobj.getString("name"));
			}
			return dataSetNames;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	public JSONArray get(String objName){
		try {
			return olapJson.getJSONArray(objName);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString(){
		if(olapJson != null) return olapJson.toString();
		else if(olapStr != null) return olapStr;
		return "none";
	}
}
