package com.raqsoft.guide.web;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.raqsoft.common.Logger;
import com.raqsoft.common.RQException;

/**
 * web  ��д�����ļ�
 */
public class ReportConfigManagerImpl implements IReportConfigManager {
	private ReportConfigModel rcm = null;
	private Map configMap = new HashMap();
	private Document doc = null;
	private Map analyzeExps = new HashMap();
	private Map analyzeExps4Aggre = new HashMap();

	public void setInputStream(InputStream inputStream) {
		rcm = new ReportConfigModel();
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (Throwable e) {
			Logger.error("",e);
			e.printStackTrace();
		}
		docBuilder.setEntityResolver(new MyEntityResolver());
		try {
			doc = docBuilder.parse(inputStream);
		} catch (Throwable e) {
			throw new RQException( "xml File Error: " + e.getMessage(), e);
		}
		Element root = doc.getDocumentElement();
		NodeList listIterator = root.getChildNodes();
		for (int i = 0, iCount = listIterator.getLength(); i < iCount; i++) {
			Node el = listIterator.item(i);
			if ("config".equals(el.getNodeName())) {
				NodeList lI = el.getChildNodes();
				ConfigModel cm = new ConfigModel();
				for (int j = 0; j < lI.getLength(); j++) {
					Node ccl = lI.item(j);
					if (ccl.getNodeName().equalsIgnoreCase("name")) {
						if (ccl.getFirstChild() != null) {
							if (ccl.getNodeName().equalsIgnoreCase("name")) {
								cm.setName(ccl.getFirstChild().getNodeValue());
							}
						}
					}
					if (ccl.getNodeName().equalsIgnoreCase("value")) {
						if (ccl.getFirstChild() != null) {
							cm.setValue(ccl.getFirstChild().getNodeValue());
						} else {
							cm.setValue("");
						}
					}
				}
				rcm.addConfig(cm);
			}
			else if ("olapanalyzes".equals(el.getNodeName())) {
				NodeList exps = el.getChildNodes();
				for (int j=0; j<exps.getLength(); j++) {
					Node exp = exps.item(j);
					if (exp.getNodeType() == Node.ELEMENT_NODE) {
						if ("2".equals(exp.getAttributes().getNamedItem("type").getNodeValue())) {
							analyzeExps4Aggre.put(exp.getAttributes().getNamedItem("name").getNodeValue(), exp.getAttributes().getNamedItem("value").getNodeValue());
						} else {
							analyzeExps.put(exp.getAttributes().getNamedItem("name").getNodeValue(), exp.getAttributes().getNamedItem("value").getNodeValue());
						}
					}
				}
			}
			else if ("jdbc-ds-configs".equals(el.getNodeName())) {
				NodeList lI = el.getChildNodes();
				for (int j = 0; j < lI.getLength(); j++) {
					Node ccl = lI.item(j);
					if ("jdbc-ds-config".equals(ccl.getNodeName())) {
						NodeList lI_l = ccl.getChildNodes();
						JDBCDsConfigModel jcm = new JDBCDsConfigModel();
						for (int k = 0; k < lI_l.getLength(); k++) {
							Node ccll = lI_l.item( k );
							String nodeName = ccll.getNodeName();
							if ( nodeName.equalsIgnoreCase( "name" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.name = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "dbType" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.dbType = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "url" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.url = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "driver" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.driver = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "userName" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									else tmp = "";
									jcm.userName = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "password" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									else tmp = "";
									jcm.password = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "dbCharset" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.dbCharset = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "clientCharset" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.clientCharset = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "useSchema" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.useSchema = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "caseSentence" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.caseSentence = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "needTranContent" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.needTranContent = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "needTranSentence" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.needTranSentence = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "addTilde" ) ) { //TODO JNDI��ʽҲ������Ҫ�Ƿ���Ҫ��ŵ����á�
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.addTilde = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "maxQuery" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.maxQuery = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "parallel" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.parallel = tmp;
								}
							}
						}
						this.rcm.addDS(jcm);
					}
				}
			}
			else if ("jndi-ds-configs".equals(el.getNodeName())) {
				NodeList lI = el.getChildNodes();
				String jndiPrefix = "";
				for (int j = 0; j < lI.getLength(); j++) {
					Node ccl = lI.item(j);
					if ("jndi-prefix".equals(ccl.getNodeName())) {
						if ( ccl.getFirstChild() != null ) {
							String tmp = ccl.getFirstChild().getNodeValue();
							if ( tmp != null ) jndiPrefix = tmp.trim();
						}
					}
				}
				if ( jndiPrefix.length() > 0 && !jndiPrefix.endsWith( "/" ) ) {
					jndiPrefix = jndiPrefix + "/";
				}
				for (int j = 0; j < lI.getLength(); j++) {
					Node ccl = lI.item(j);
					if ("jndi-ds-config".equals(ccl.getNodeName())) {
						NodeList lI_l = ccl.getChildNodes();
						JNDIDsConfigModel jcm = new JNDIDsConfigModel();
						jcm.jndiPrefix = jndiPrefix;
						for (int k = 0; k < lI_l.getLength(); k++) {
							Node ccll = lI_l.item( k );
							String nodeName = ccll.getNodeName();
							if ( nodeName.equalsIgnoreCase( "name" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.name = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "dbType" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.dbType = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "dbCharset" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.dbCharset = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "clientCharset" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.clientCharset = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "needTranContent" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.needTranContent = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "needTranSentence" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.needTranSentence = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "maxQuery" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.maxQuery = tmp;
								}
							}
							else if ( nodeName.equalsIgnoreCase( "parallel" ) ) {
								if ( ccll.getFirstChild() != null ) {
									String tmp = ccll.getFirstChild().getNodeValue();
									if ( tmp != null ) tmp = tmp.trim();
									jcm.parallel = tmp;
								}
							}
						}
						this.rcm.addJndiDS(jcm);
					}
				}
			}
		}
		List configList = rcm.getServletConfigModelList();
		for (int i = 0; i < configList.size(); i++) {
			ConfigModel cm1 = (ConfigModel) configList.get(i);
			String name = cm1.getName();
			String value = cm1.getValue();
			if (name != null && !"".equals(name)) {
				configMap.put(name, value);
			}
		}
	}



	public String getInitParameter(String name) {
		Object obj = configMap.get(name);
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}


	public Set getInitParameters() {
		return configMap.keySet();
	}

	public void setParameterValue(Object key, Object value) {
		configMap.put(key, value);
	}

	public ReportConfigModel getReportConfigModel(){
		return this.rcm;
	}

	public JDBCDsConfigModel getJDBCDsConfigModel(String dsName){
		if (rcm.listDsModelKeys().length > 0) {
			JDBCDsConfigModel jcm = (JDBCDsConfigModel) rcm.getDsValue(dsName);
			if (jcm == null) {
				jcm = this.rcm.getFirstDsModel();
			}
			return jcm;
		}
		//Logger.info("Not Has JDBC Config Please Check ReportConfig.xml !!");
		return null;
	}

	public String[] listDcModelkeys(){
		return this.rcm.listDsModelKeys();
	}

	/*
	 *
	 */
	private static class MyEntityResolver implements EntityResolver {
		public InputSource resolveEntity(String publicId, String systemId) {
			return new InputSource(new StringReader(""));
		}
	};

	/*
	 *
	 */
	public static IReportConfigManager getInstance() {
		IReportConfigManager rcm = new ReportConfigManagerImpl();
		return rcm;
	}

	public JNDIDsConfigModel getJNDIDsConfigModel( String dsName ) {
		if( rcm.listJndiDsModelKeys().length > 0 ) {
			JNDIDsConfigModel jcm = (JNDIDsConfigModel) rcm.getJndiDsValue(dsName);
			return jcm;
		}
		//Logger.info("Not Has JNDI Config Please Check ReportConfig.xml !!");
		return null;
	}



	public Map getAnalyzeExps() {
		return analyzeExps;
	}



	public Map getAnalyzeExps4Aggre() {
		return analyzeExps4Aggre;
	}
}
