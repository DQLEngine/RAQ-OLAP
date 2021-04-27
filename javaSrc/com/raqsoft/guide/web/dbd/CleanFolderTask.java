package com.raqsoft.guide.web.dbd;

import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.dl.FileUtils;

public class CleanFolderTask implements Runnable{

	public void run() {
		while(true){
			FileUtils.cleanFolder(DataSphereServlet.dbd_olapFileTempdir);
			try {
				Thread.sleep(30*60*1000);//10分钟清理一次
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
