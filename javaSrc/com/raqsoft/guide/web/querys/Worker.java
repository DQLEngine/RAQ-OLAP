package com.raqsoft.guide.web.querys;

import com.raqsoft.guide.web.DataSphereServlet;

public class Worker extends Thread {
	private TaskQueue queue;
	private ITask tsk;
	
	public Worker(TaskQueue queue, ITask tsk){
		this.queue = queue;
		this.tsk = tsk;
	}
	
	public void run(){
		QueryTask qt = (QueryTask)tsk;
//		UserInfo ui = DataSphereServlet.userManager.getUserInfo(t.getUser());
		if (qt.isCancelled()) {
//			synchronized(userCount){
//				userCount--;
//			}
			return;
		}
		//qt.setStartTime(System.currentTimeMillis());
		//qt.setWorker(this);
		qt.execute();
//		synchronized(userCount){
//			userCount--;
//		}
		
		//queue.remove(tsk);
	}

}
