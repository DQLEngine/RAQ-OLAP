package com.raqsoft.guide.web.querys;

import com.raqsoft.common.Logger;


public class TaskMonitor extends Thread {
	private TaskQueue queue;
	
	public TaskMonitor(TaskQueue queue){
		this.queue = queue;
	}
	public void run(){
		try {
			while (true) {
				synchronized(queue){//
					ITask tsk = queue.take();
					while ( tsk != null ) {
						new Worker(queue, tsk).start();
						tsk = queue.take();
					}
					queue.wait();
				}
			}
		} catch (InterruptedException e) {
			Logger.error("",e);
			e.printStackTrace();
		}
	}
}
