package com.raqsoft.guide.web.dql;

import java.util.ArrayList;
import java.util.List;

import com.raqsoft.common.RQException;
import com.raqsoft.logic.metadata.Field;
import com.raqsoft.logic.metadata.FieldList;
import com.raqsoft.logic.metadata.ForeignKey;
import com.raqsoft.logic.metadata.ForeignKeyList;
import com.raqsoft.logic.metadata.Level;
import com.raqsoft.logic.metadata.LevelList;
import com.raqsoft.logic.metadata.LogicMetaData;
import com.raqsoft.logic.metadata.Table;
import com.raqsoft.logic.metadata.TableList;
import com.raqsoft.logic.util.IOUtil;

public class LmdService {
	
	private LogicMetaData lmd;
	
	public LmdService(LogicMetaData lmd) {
		this.lmd = lmd;
	}

//	list table	�г����б�
//	of field GF,��	�г������ֶ�GF�������ڵı�
//	of dim D,��	�г�ά��D��ά��
	public String listTable(String fields, String Dims) {
		return null;
	}

//	list dim table	�г�����ά��û�з��ؿ�
//	of field GF,��	�������GFָ���ά��
//	of table T	��T��ͬά��
	public String listDimTable(String fields, String table) {
		return null;
	}

//	list sub table 
//	of table T	�г���T���ӱ�
	public String listSubTable(String table) {
		return null;
	}

//	list pseudo table	�г����мٱ�

//	list layer
//	  of dim D	�г�ά��D�Ĳ㺯��
	public String listLayer(String dim) {
		return null;
	}
	
	
//	list dim	�г�����ά��
//	of field GF,��	�г������ֶ�GF��ά������ֶη��ؿ�
//	of table T,��	�г�ά��T��ά��
	public String listDim(String fields, String tables) {
		return null;
	}
	
//	list type
//	of field GF,��	�г������ֶ�GF����������
	public String listType(String fields) {
		return null;
	}
	
	
//	list field from T	�г���T�Ĺ����ֶ�
//	depth d	Ѱ�ҹ����ֶε���ȣ�ȱʡΪ1�����������Ϊ0����ͬά��
//	primay key	�г������ֶ�
//	foreign key FK	���FK���ֶΣ�FKʡ���г���T���������
	//	dim D	ֻ�г���Dͬά�����
	// fieldType : 1ȫ��  2����  3���������ʱdepth>1ʱǿ�Ƹĳ�1
	public ArrayList<String> listField(String table, int depth, int fieldType, String foreign, String dim) {
		if (fieldType == 2 && depth>1) depth = 1;
		
		ArrayList<String> rs = new ArrayList<String>();
		Table t = lmd.getTable(table);
		if (t == null) throw new RQException("table["+table+"] not exist"); 
		nextDeep("",table,null,1,depth,fieldType,foreign,dim,rs);
		return rs;
	}
	
	private void nextDeep(String parent, String table, String fkField, int currDeep, int depth
			, int fieldType, String foreign, String dim, ArrayList<String> fs) {
		String t = table;
		Table tObj = lmd.getTable(table);
		ForeignKey fkObj = null;
		Field fObj = null;
		Field dimObj = null;
		LevelList ll = null;
		int nextDeep = currDeep+1;
		
		System.out.println("nextDeep : " + table + " , " + fkField + " , " + currDeep);
		
		if (fkField != null) {
			tObj = lmd.getTable(table);
			fkObj = tObj.getForeignKey(fkField);
			if (fkObj == null) {
				fObj = tObj.getField(fkField);
				if (fObj == null) throw new RQException("field ["+table+"] not exist");
				else {
					dimObj = fObj.getDim();
					if (dimObj == null) return;
					tObj = dimObj.getTable();
					ll = dimObj.getLevelList();
				}
			} else {
				tObj = fkObj.getRefTable();
			}
		}
		
		TableList tl = null;
		
		if (depth > 0) {
			tl = tObj.getAnnexTableList();
		} else {
			tl = new TableList();
			tl.add(tObj);
		}
		
		if (ll != null && fieldType == 1) {
			for (int i=0; i<ll.size(); i++) {
				Level li = ll.get(i);
				String fi = parent+"#"+li.getName();
				if (fs.indexOf(fi)==-1) fs.add(fi);
				if (nextDeep<=depth) {
					nextDeep(fi,li.getDestDim().getTable().getName(),null,nextDeep,depth,fieldType,foreign,dim,fs);	
				}
			}
		}
		
		FieldList pkfl = tObj.getPKFieldList();
		
		if (currDeep == 2 && pkfl != null && pkfl.size()==1 && pkfl.get(0) == fObj) return;
		
		for (int i=0; i<tl.size(); i++) {
			Table ti = tl.get(i);
			if (fieldType == 2) {
				List li = ti.getPK();
				if (li != null) {
					for (int j=0; j<li.size(); i++) {
						String fj = (parent.length()>0?parent+".":"")+li.get(j);
						if (fs.indexOf(fj)==-1) fs.add(fj);
						
					}
				}
			} else if (fieldType == 3) {
				if (foreign != null) {
					ForeignKey fk = ti.getForeignKey(foreign);
					if (fk == null) throw new RQException("can not find foreign key ["+foreign+"]");
					List fkl = fk.getFieldNameList();
					if (fkl != null) {
						for (int j=0; j<fkl.size(); j++) {
							String fj = (parent.length()>0?parent+".":"")+fkl.get(j);
							if (fs.indexOf(fj)==-1) fs.add(fj);
						}
					}
				} else {
					ForeignKeyList fkl = ti.getForeignKeyList();
					if (fkl != null) {
						for (int j=0; j<fkl.size(); j++) {
							ForeignKey fkj = fkl.get(j);
							List names = fkj.getFieldNameList();
							String fj = null;
							if (names.size()==1) {
								fj = (parent.length()>0?parent+".":"")+fkj.getFieldList().get(0).getName();
							} else {
								fj = (parent.length()>0?parent+".":"")+fkj.getName();
							}
							if ((dim == null || fkj.getDim().getName().equals(dim)) && fs.indexOf(fj)==-1) fs.add(fj);
							if (nextDeep<=depth) {
								nextDeep(fj,ti.getName(),fkj.getName(),nextDeep,depth,fieldType,foreign,dim,fs);	
							}
						}
					}
				}
			} else {
				FieldList fl = ti.getFieldList();
				if (fl != null) {
					for (int j=0; j<fl.size(); j++) {
						Field fj = fl.get(j);
						String fName = (parent.length()>0?parent+".":"")+fj.getName();
						Field fdj = fj.getDim();
						//System.out.println(fj.getName()+"---" + fdj);
						if (fj.isPKField()) {
							if (currDeep > 1 && tObj.getPK().size()==1) continue;
							if (tObj.getPK().size()==1) fdj = null;
						}
						if (fs.indexOf(fName)==-1) fs.add(fName);
						if (fdj != null && nextDeep<=depth) {
							nextDeep(fName,ti.getName(),fj.getName(),nextDeep,depth,fieldType,foreign,dim,fs);	
						}
					}
				}

				ForeignKeyList fkl = ti.getForeignKeyList();
				if (fkl != null) {
					for (int j=0; j<fkl.size(); j++) {
						ForeignKey fkj = fkl.get(j);
						List names = fkj.getFieldNameList();
						if (names.size()==1) continue;
						String fName = (parent.length()>0?parent+".":"")+fkj.getName();
						if (fs.indexOf(fName)==-1) fs.add(fName);
						if (nextDeep<=depth) {
							nextDeep(fName,ti.getName(),fkj.getName(),nextDeep,depth,fieldType,foreign,dim,fs);	
						}
					}
				}
			}

		}
	}
	
	public static void main(String args[]) throws Exception {
		LogicMetaData lmd = IOUtil.readLogicMetaData("D:/data/workspace/datalogic2/services/datalogic/conf/","demo.lmd");
		lmd.prepare();
		LmdService ls = new LmdService(lmd);
		ArrayList al = ls.listField("�ͻ�", 3, 1, null, null);
		for (int i=0; i<al.size(); i++) {
			System.out.println(al.get(i));
		}

//		FieldList fl = lmd.getTable("��Ա").getFieldList();
//		for (int i=0; i<fl.size(); i++) {
//			System.out.println(fl.get(i).getName() + fl.get(i).getDim());
//		}
	}

}
