package com.raqsoft.guide.web.querys;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.raqsoft.guide.Consts;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.common.Logger;

public class TaskQueue {
	
	public synchronized ITask getTask(String id) {
		Object o = allTsks.get(id);
		if (o != null) return (ITask)o;
		return null;
	}

	public synchronized ITask getTaskByName(String user, String name)  throws Exception {
		List tsks = getTaskList(user, true);
		for (int i=0; i<tsks.size(); i++) {
			if (((QueryTask)tsks.get(i)).getName().equals(name)) return (QueryTask)tsks.get(i); 
		}
		return null;
	}

	private Map tsks = new HashMap();
	private Map allTsks = new HashMap();

	public synchronized void put(ITask tsk) throws Exception{
		Logger.debug(new String("Task Queue put task : " + tsk.getID()));
		allTsks.put(tsk.getID(), tsk);
		if (tsk.getEndTime() == 0) {
			tsks.put(tsk.getID(), tsk);
		}
		this.notify();
	}
	//�Ƴ����񣬿�ʼִ��
	public synchronized ITask take(){
		try {
			Iterator iter = tsks.keySet().iterator();
			while (iter.hasNext()) {
				ITask t = (ITask)tsks.get(iter.next());
				if (check(t)) {
					tsks.remove(t.getID());
					return t;
				}
			}
		} catch (Exception e) {
			Logger.error("",e);
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean check(ITask t) throws Exception {
		if (t.getStartTime() > 0) return false;
		else return true;
//		DBInfo dbi = DataSphereServlet.dbManager.getDBInfo(t.getDBName());
//		UserInfo ui = DataSphereServlet.userManager.getUserInfo(t.getUser());
//		UserParallel up = DataSphereServlet.dbManager.getUser(t.getUser());
//		int userMax = ui.getParallelCount();
//		if (userMax<=0) userMax = Integer.MAX_VALUE;
//		int userCurr = up.getCurrQuery();
//		int dbMax = dbi.getMaxQuery();//
//		int dbCurr = dbi.getCurrQuery();//
//		if (userCurr < userMax && dbCurr < dbMax) {
////			dbi.addQuery();
////			synchronized(userCount){
////				userCount++;
////			}
//			if (((QueryTask)t).getExecTime() > System.currentTimeMillis()) return false;
//			return true;
//		} else return false;
	}
	
	public synchronized boolean cancel(String id) {
		Object o = allTsks.get(id);
		if (o != null) {
			return ((ITask)o).cancel(); 
		}
		return true;
	}
	
	public synchronized void remove(ITask tsk) {
		allTsks.remove(tsk.getID());
		if (tsks.get(tsk.getID()) != null) {
			tsks.remove(tsk.getID());
		}
		this.notify();
		Logger.debug(new String("remove task [" + tsk.getID() + "]"));
	}

	public synchronized List getTaskList(String user, boolean countFailed) throws Exception {
		File f = new File(Consts.getAbsPath(user, ""));
//		UserInfo ui = DataSphereServlet.userManager.getUserInfo(user);
//		int maxCount = ui.getZjbCount()
		List list = new ArrayList();
		if (f.exists()) {
			File[] fs = f.listFiles();
			if (fs != null) {
				for (int i=0; i<fs.length; i++) {
					try {
						File fi = fs[i];
						if (fi.getName().toLowerCase().endsWith(".tsk")) {
							String id = fi.getName().replaceAll(".tsk", "");
							QueryTask qt = null;
							ITask it = getTask(id);
							if (it != null) qt = (QueryTask)it;
							else qt = new QueryTask(fi.getPath());
							if (qt.getEndTime()>0 && qt.getErrorInfo()!= null && !countFailed) continue;
							list.add(qt);
						}
					} catch (Exception e) {
					}
				}
			}
		}
		return list;
	}
	
	public synchronized String getTaskNames(String user) throws Exception {
		List tsks = getTaskList(user, true);
		String names = ",";
		for (int i=0; i<tsks.size(); i++) {
			names += ((QueryTask)tsks.get(i)).getName() + ",";
		}
		return names;
	}

	public synchronized boolean canAdd(String user) throws Exception {
//		UserInfo ui = DataSphereServlet.userManager.getUserInfo(user);
//		int maxCount = ui.getZjbCount();
//		return getTaskList(user, false).size() < maxCount;
		return true;
	}

}
