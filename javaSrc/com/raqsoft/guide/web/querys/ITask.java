package com.raqsoft.guide.web.querys;

public interface ITask {
	public String getUser();					
	public void setUser(String user);
	
	public String getName();					
	public void setName(String name);
	
	public long getBackTime();				
	public void setBack();
	
	public long getStartTime();				
	public void setStartTime(long st);

	public long getEndTime();					
	public void setEndTime(long ft);

	public void execute();						
	public boolean cancel();							
	
	public boolean isCancelled();			
	public boolean isDone();					

	public Object get();							
	public Object get(long timeout);	

	public Worker getWorker();				
	public void setWorker(Worker w);			
	
	public String getDBName();
	public void setDBName(String db);

	public String getID();
	public void setID(String id);

}
