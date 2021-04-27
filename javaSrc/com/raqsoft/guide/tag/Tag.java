package com.raqsoft.guide.tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.raqsoft.common.Logger;

public class Tag  extends TagSupport{
	
	protected String getSqlLang(String sqlType, ServletContext application) {
		if(sqlType == null) sqlType = "default";
		StringBuffer sb = new StringBuffer();
		String fileName = "/raqsoft/guide/asset/"+sqlType+".properties";
		InputStream is = application.getResourceAsStream(fileName);
		if(is == null) {
			Logger.debug("没有找到文件："+fileName);
		}
		ResourceBundle resource = null;
		try {
			if(is != null) {
				resource = new PropertyResourceBundle(is);
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String _isnull = "_x_ is null";
		String _isnotnull = "_x_ is not null";
		String _like_prefix = "_x_ like '%";
		String _like_suffix = "%'";
		String _notlike_prefix = "_x_ not like '%";
		String _notlike_suffix = "%'";
		String _startfrom_prefix = "_x_ not like '";
		String _startfrom_suffix = "%'";
		String _endat_prefix = "_x_ like '%";
		String _endat_suffix = "'";
		String _date_prefix = "_x_=date('";
		String _date_suffix = "')";
		String _timestamp_prefix = "";
		String _timestamp_suffix = "";
		if( resource == null ) {
			Logger.debug("没有找到文件："+fileName);
		}else{
			try{
				_isnull = resource.getString("_isnull");
			}catch(java.util.MissingResourceException e){}
			try{
				_isnotnull = resource.getString("_isnotnull");
			}catch(java.util.MissingResourceException e){}
			try{
				_like_prefix = resource.getString("_like").split("_v_")[0];
				_like_suffix = resource.getString("_like").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
			try{
				_notlike_prefix = resource.getString("_notlike").split("_v_")[0];
				_notlike_suffix = resource.getString("_notlike").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
			try{
				_startfrom_prefix = resource.getString("_startfrom").split("_v_")[0];
				_startfrom_suffix = resource.getString("_startfrom").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
			try{
				_endat_prefix = resource.getString("_endat").split("_v_")[0];
				_endat_suffix = resource.getString("_endat").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
			try{
				_date_prefix = resource.getString("_date").split("_v_")[0];
				_date_suffix = resource.getString("_date").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
			try{
				_timestamp_prefix = resource.getString("_timestamp").split("_v_")[0];
				_timestamp_suffix = resource.getString("_timestamp").split("_v_")[1];
			}catch(java.util.MissingResourceException e){}
		}
		sb.append("{\n");
		sb.append("\t_isnull:\"").append(_isnull).append("\",\n");
		sb.append("\t_isnotnull:\"").append(_isnotnull).append("\",\n");
		sb.append("\t_like_prefix:\"").append(_like_prefix).append("\",\n");
		sb.append("\t_like_suffix:\"").append(_like_suffix).append("\",\n");
		sb.append("\t_notlike_prefix:\"").append(_notlike_prefix).append("\",\n");
		sb.append("\t_notlike_suffix:\"").append(_notlike_suffix).append("\",\n");
		sb.append("\t_startfrom_prefix:\"").append(_startfrom_prefix).append("\",\n");
		sb.append("\t_startfrom_suffix:\"").append(_startfrom_suffix).append("\",\n");
		sb.append("\t_endat_prefix:\"").append(_endat_prefix).append("\",\n");
		sb.append("\t_endat_suffix:\"").append(_endat_suffix).append("\",\n");
		sb.append("\t_date_prefix:\"").append(_date_prefix).append("\",\n");
		sb.append("\t_date_suffix:\"").append(_date_suffix).append("\",\n");
		sb.append("\t_timestamp_prefix:\"").append(_timestamp_prefix).append("\",\n");
		sb.append("\t_timestamp_suffix:\"").append(_timestamp_suffix).append("\",\n");
		sb.append("}\n");
		return sb.toString();
	}
}
