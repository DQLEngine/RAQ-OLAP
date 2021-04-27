package com.raqsoft.guide.web.querys;

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.raqsoft.cellset.datamodel.PgmCellSet;
import com.raqsoft.common.DBSession;
import com.raqsoft.common.ISessionFactory;
import com.raqsoft.common.Logger;
import com.raqsoft.common.StringUtils;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.JobSpace;
import com.raqsoft.dm.JobSpaceManager;
import com.raqsoft.dm.ParamList;
import com.raqsoft.guide.web.DataSphereServlet;
import com.raqsoft.guide.web.dl.ActionResultPage;
import com.raqsoft.util.CellSetUtil;

public class QueryTask implements ITask, Externalizable {
	private static final long serialVersionUID = 0x1;

	private int taskType;//1:查询任务; 2：生成中间表任务; 3:数据来源txt的中间表任务
	//共有属性
	private String dql;
	private ISessionFactory isf;
	private DBSession dbs;
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	private long startTime = 0;
	private long endTime = 0;//查询任务完成后
	private String dbName;
	private String user;
	private Worker w = null;
	private String taskID = "";
	private ArrayList columns = new ArrayList();
	private ArrayList colTypes = new ArrayList();
	//private long backTime = 0;

	//查询任务属性
	private int rsCursor = 0;//结果集指针
	private ArrayList cache = new ArrayList();//最前面的数据行
	private String attrs;
	
	//中间表任务属性,txt中间表任务属性
	private FileInputStream fisTsk = null;
	private ObjectInputStream oisTsk = null;
	private FileOutputStream fosTsk = null;
	private ObjectOutputStream oosTsk = null;
	private String tskFile;
	private String tableName;//不可修改，user_当前时间毫秒数
	private String name;//用户看到的表名
	private String errorInfo;
	private String qyx;
	private long execTime=0; //用户定义的开始执行时间，0表示立即执行的。
	
	//txt中间表任务属性
	private String dataFile;//txt
	private FileInputStream fisDat = null;
	private ObjectInputStream oisDat = null;
	private int allCount = 0;
	private int succCount = 0;
	
	private String dfxOutputFile = null;

	
	public QueryTask(String dql, String user, String dbName, String file) {
		Logger.debug("add query task 1;");
		try {
			this.taskType = 1;
			this.dql = dql;
			this.user = user;
			this.dbName = dbName;
			this.taskID = user + "_" + System.currentTimeMillis();
			dfxOutputFile = file;
			ActionResultPage.tsks.put(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public QueryTask(String dql, String user, String dbName, String name, String path, String qyx, String id, long execTime) {
		Logger.debug("add create task 2;");
		try {
			this.taskType = 2;
			this.dql = dql;
			this.user = user;
			this.dbName = dbName;
			this.name = name;
			if (id == null) this.taskID = user + "_" + System.currentTimeMillis();
			else this.taskID = id;
			this.tableName = this.taskID;
			this.tskFile = path + File.separator + tableName + ".tsk";
			this.qyx = qyx;
			this.execTime = execTime;
			ActionResultPage.tsks.put(this);
			saveTask();
			if (execTime > System.currentTimeMillis()) {
				Timer t = new Timer();
				t.schedule(new TimerTask(){
					public void run() {
						synchronized(ActionResultPage.tsks){
							ActionResultPage.tsks.notify();
						}
					}
				}, execTime - System.currentTimeMillis());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public QueryTask(String dql, String user, String dbName, String name, String path, String qyx, String id, String dataFile, long execTime) {
		Logger.debug("add create task 3;");
		try {
			this.taskType = 3;
			this.dql = dql;
			this.user = user;
			this.dbName = dbName;
			this.name = name;
			if (id == null) this.taskID = user + "_" + System.currentTimeMillis();
			else this.taskID = id;
			this.tableName = this.taskID;
			this.tskFile = path + File.separator + tableName + ".tsk";
			this.qyx = qyx;
			this.dataFile = dataFile;
			this.execTime = execTime;
			ActionResultPage.tsks.put(this);
			saveTask();
			if (execTime > System.currentTimeMillis()) {
				Timer t = new Timer();
				t.schedule(new TimerTask(){
					public void run() {
						synchronized(ActionResultPage.tsks){
							ActionResultPage.tsks.notify();
						}
					}
				}, execTime - System.currentTimeMillis());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public QueryTask(String tskFile) {
		Logger.debug("read middle table task;");
		lastAccess();
		this.tskFile = tskFile;
		try {
			File f = new File(tskFile);
			fisTsk = new FileInputStream(f);
			oisTsk = new ObjectInputStream(fisTsk);
			readExternal(oisTsk);
			ActionResultPage.tsks.put(this);
			if (execTime > System.currentTimeMillis()) {
				Timer t = new Timer();
				t.schedule(new TimerTask(){
					public void run() {
						synchronized(ActionResultPage.tsks){
							ActionResultPage.tsks.notify();
						}
					}
				}, execTime - System.currentTimeMillis());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {if (oisTsk != null) oisTsk.close();} catch(Exception e) {}
			try {if (fisTsk != null) fisTsk.close();} catch(Exception e) {}
		}
	}

//	private Timer timer = new Timer();
//	public static int TIMEOUT = 30;//已经完成的任务超时后，关闭文件，删除任务对象。单位分钟。
	private void lastAccess(){
//		lastAccessTime = System.currentTimeMillis();
//		timer.cancel();
//		timer =new Timer();
//		final QueryTask tsk = this;
//		timer.schedule(new TimerTask(){
//			public void run() {
//				if (tsk.getEndTime() > 0 && tsk.getBackTime() > 0) {
//					tsk.close();
//				}
//			}
//		}, new Date(lastAccessTime + TIMEOUT * 60 * 1000));
	}

	
	public boolean cancel() {
		//dropTable();
		if (!dropTable()) return false;;
		closeResultSet();
		closeFile();
		removeFile();
		//if (this.backTime > 0) removeFile();
		ActionResultPage.tsks.remove(this);
		return true;
	}
	
	private boolean dropTable() {
		return true;
	}
	public void close(){
		closeResultSet();
		closeFile();
		ActionResultPage.tsks.remove(this);
	}
	
	public void closeFile(){
		try {if (oisTsk != null) oisTsk.close();} catch(Exception e) {}
		try {if (fisTsk != null) fisTsk.close();} catch(Exception e) {}
		try {if (oosTsk != null) oosTsk.close();} catch(Exception e) {}
		try {if (fosTsk != null) fosTsk.close();} catch(Exception e) {}
		try {if (oisDat != null) oisDat.close();} catch(Exception e) {}
		try {if (fisDat != null) fisDat.close();} catch(Exception e) {}
	}
	
	private void removeFile(){
		try {
			Logger.debug("remove task [" + tskFile + "] : " + new File(tskFile).delete());
		} catch (RuntimeException e) {
		}
		try {
			Logger.debug("remove data [" + dataFile + "] : " + new File(dataFile).delete());
		} catch (RuntimeException e) {
		}
		try {
			Logger.debug("remove data [" + qyx + "] : " + new File(qyx).delete());
		} catch (RuntimeException e) {
		}
	}
	
	public void closeResultSet() {
		Logger.debug("task[" + taskID+ "], close");
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
		}
		rs = null;
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
		}
		stmt = null;
		try {
			if (con != null) con.close();
		} catch (SQLException e) {
		}
		dbs = null;
		try {
			if (dbs != null) dbs.close();
		} catch (Exception e) {
		}
		dbs = null;
	}

	private boolean hasAdd = false;
	public void execute() {
		try {
			if (startTime > 0) return;
			startTime = System.currentTimeMillis();
			lastAccess();
			Logger.debug("task[" + taskID+ "], execute");
			hasAdd = true;
//			Thread.currentThread().sleep(5000);
			isf = (ISessionFactory)Env.getDBSessionFactory(dbName);

//			Logger.debug("do new query!");
			dbs = isf.getSession();
			con = (Connection)dbs.getSession();
			Logger.debug("1.." + con + "---" + Thread.currentThread() );
//			Thread.currentThread().sleep(10*1000);
			String p = null;
			String sql = p==null?dql:(StringUtils.replace(StringUtils.replace(dql, "SELECT ", "SELECT "+p), "select ", "select "+p));
			if (this.taskType != 1) {
				stmt = con.createStatement();
				//sql = "SELECT 公司信息.成立日期 AS 成立日期1 FROM 公司信息 AS 公司信息";
				stmt.execute("CREATE TABLE " + this.tableName + " AS (" + sql + ") WITH DATA");
				con.commit();
				boolean success = true;
				ResultSet rs = null;
				//int type = 0;
				try {
					rs = stmt.executeQuery("SELECT * FROM " + tableName);
					//type = rs.getMetaData().getColumnType(1);
					if (columns.size() == 0) {
						java.sql.ResultSetMetaData rsmd = rs.getMetaData();
						for (int i=1; i<=rsmd.getColumnCount(); i++) {
							columns.add(rsmd.getColumnLabel(i));
							colTypes.add(new Integer(rsmd.getColumnType(i)));
						}
					}
				} catch (Exception e1) {
					success = false;
				} finally {
					try {rs.close();} catch(Exception ex) {}
				}
				if (!success) errorInfo = "创建中间表失败";
				else {
					if (taskType == 3) {
						try {if (oisDat != null) oisDat.close();} catch(Exception e) {}
						
						try {if (fisDat != null) fisDat.close();} catch(Exception e) {}
						fisDat = new FileInputStream(this.dataFile);
						BufferedReader br = new BufferedReader( new InputStreamReader( fisDat, com.raqsoft.report.usermodel.Context.getJspCharset() ) );
						String value = br.readLine();
						//TODO 单列值，trim，失败的忽略掉，以后可能要告诉用户成功、失败行数。
						PreparedStatement ps = con.prepareStatement("INSERT INTO " + tableName + " VALUES (?)");
						while (value != null) {
							value = value.trim();
							if (value.length() > 0) {
								allCount++;
								try {
									ps.setObject(1, com.raqsoft.common.Types.getProperData(com.raqsoft.common.Types.getTypeBySQLType(((Integer)colTypes.get(0)).intValue()), value));
									ps.execute();
									succCount++;
								} catch (Exception e) {
									Logger.warn(e.getMessage());
								} finally {
								}
							}
							value = br.readLine();
						}
						try{ps.close();}catch(Exception e){}
						con.commit();
						br.close();
					}
				}
				close();
			} else {
				stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				rs = stmt.executeQuery(sql);
//				Thread.currentThread().sleep(30000);
				if (rs == null) return;
				if (columns.size() == 0) {
					java.sql.ResultSetMetaData rsmd = rs.getMetaData();
					for (int i=1; i<=rsmd.getColumnCount(); i++) {
						columns.add(rsmd.getColumnLabel(i));
						colTypes.add(new Integer(rsmd.getColumnType(i)));
					}
				}
			}
			Logger.debug("task[" + taskID+ "], query success");
			if (dfxOutputFile != null) {
				String jsId = "jsId" + System.currentTimeMillis();
				JobSpace space = JobSpaceManager.getSpace(jsId);
				try{
					PgmCellSet pcs = CellSetUtil.readPgmCellSet(DataSphereServlet.class.getResourceAsStream(DataSphereServlet.DFX_SAVE));//(obj.dfx);
					Context ctx = pcs.getContext();
					ParamList pl = ctx.getParamList();
					space.setParamValue("@dataSource", this.dbName);
					space.setParamValue("@dql", this.dql);
					space.setParamValue("@src", this.dfxOutputFile);
					//space.setAppHome(svr.getAppPath(appId));  //设应用主目录
					pcs.setParamToContext();
					ctx.setJobSpace(space);
					pcs.calculateResult();
					while(pcs.hasNextResult()) {
						//if ()
						Object o = pcs.nextResult();
						
					}
					//Logger.debug("result : " + v);
				}finally{
					JobSpaceManager.closeSpace(jsId);
				}
			}
		} catch (Exception e) {
			errorInfo = e.getMessage();
			e.printStackTrace();
			close();
		}
		endTime = System.currentTimeMillis();
		if (DataSphereServlet.queryListener != null) DataSphereServlet.queryListener.executeFinished(dbName, user, dql, startTime, endTime);
		if (taskType != 1) saveTask();
	}
	
	//name.tsk
	private void saveTask() {
		try {
			File f = new File(tskFile);
			fosTsk = new FileOutputStream(f);
			oosTsk = new ObjectOutputStream(fosTsk);
			writeExternal(oosTsk);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {if (oosTsk != null) oosTsk.close();} catch(Exception e) {}
			try {if (fosTsk != null) fosTsk.close();} catch(Exception e) {}
		}
	}
	
	public Object get() {
		return null;
	}

	public Object get(long timeout) {
		return null;
	}

	public long getBackTime() {
		return 0;
	}
//
//	public String getBackTimeStr() {
//		if (backTime == 0) return "";
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return dateFormat.format(new Date(backTime));
//	}

	public long getEndTime() {
		return endTime;
	}

	public String getEndTimeStr() {
		if (endTime == 0) return "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date(endTime));
	}

	public String getStatusStr() {
		if (this.startTime == 0) return "未开始执行，等待中！";
		if (this.endTime == 0) {
			if (columns == null || columns.size() == 0) return "正在从数据库中查询数据！";
			else return "正在保存数据";
		}
		if (errorInfo != null) return "失败：" + errorInfo;
		if (allCount > 0) return "完成（导入成功/总共：" + succCount + "/" + allCount + "）";
		return "完成";
	}

	public String getName() {
		return name;
	}

	public long getStartTime() {
		return startTime;
	}

	public String getStartTimeStr() {
		if (startTime == 0) return "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date(startTime));
	}

	public String getUser() {
		return user;
	}

	public Worker getWorker() {
		return w;
	}

	public boolean isCancelled() {
		return false;
	}

	public boolean isDone() {
		return endTime > 0;
	}

	public boolean isSuccess() { //中间表是否创建成功
		return isDone() && errorInfo == null;
	}

	public void setEndTime(long ft) {
	}

	public void setName(String name) {
		this.name = name;
		if (taskType != 1) saveTask();
	}

	public void setStartTime(long st) {
	}

	public void setUser(String user) {
		this.user = user;

	}

	public String getDBName(){
		return dbName;
	}
	public void setDBName(String db){
		this.dbName = db;
	}
	
	public String getQuery(){
		return dql;
	}
	
	public void setQuery(String query){
		this.dql = query;
	}

	public void setWorker(Worker w) {
		this.w = w;
	}
	
	public String getAttrs() {
		return attrs;
	}
	public void setAttrs(String attrs) {
		lastAccess();
		this.attrs = attrs;
	}
	
//	//共有属性
//	private String dql;
//	private long startTime = 0;
//	private long endTime = 0;//查询任务完成后
//	private String dbName;
//	private String user;
//	
//	private String tskFile;
//	private String name;//用户看到的表名
//	private String tableName;//不可修改，user_当前时间毫秒数

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		taskType = in .readInt();
		dbName = in.readObject().toString();
		name = in.readObject().toString();
		tableName = in.readObject().toString();
		taskID = tableName;
		dql = in.readObject().toString();
		user = in.readObject().toString();
		startTime = in.readLong();
		endTime = in.readLong();
		Object o = in.readObject();
		if (o != null) attrs = o.toString();
		o = in.readObject();
		if (o != null) errorInfo = o.toString();
		o = in.readObject();
		if (o != null) dataFile = o.toString();
		
		if (version >= 2) {
			columns = IOUtils.readArrayList(in);
			colTypes = IOUtils.readArrayList(in);
		}

		if (version >= 3) {
			allCount = in.readInt();
			succCount = in.readInt();
		}
		
		if (version >= 4) {
			o = in.readObject();
			if (o != null) qyx = o.toString();
		}
		
		if (version >= 5) {
			execTime = in.readLong();
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(5);
		out.writeInt(this.taskType);
		out.writeObject(dbName);
		out.writeObject(name);
		out.writeObject(tableName);
		out.writeObject(dql);
		out.writeObject(user);
		out.writeLong(this.startTime);
		out.writeLong(this.endTime);
		out.writeObject(this.attrs);
		out.writeObject(this.errorInfo);
		out.writeObject(this.dataFile);
		
		//2
		IOUtils.writeArrayList(out, columns);
		IOUtils.writeArrayList(out, colTypes);
		
		//3
		out.writeInt(allCount);
		out.writeInt(succCount);
		
		//4
		out.writeObject(qyx);
		
		//5
		out.writeLong(execTime);

	}

	public List getColumns() {
		return columns;
	}

	public void setColumns(ArrayList columns) {
		this.columns = columns;
	}
	
	/**
	 * 
	 * @param row
	 * @param cache，为false时，取过的数据不缓存。
	 * @return
	 */
	public ArrayList getRowData(int row, boolean needCache) throws Exception {
		lastAccess();//TODO 每取一行更新一下最后访问时间，是否效率太低了。
		//System.out.println("getRow:" + columns.size());
		//synchronized(columns){
			if (row < cache.size()) {
				return (ArrayList)cache.get(row);
			}
			if (rs != null) {
				try {
					if (!rs.next()) {
						closeResultSet();
						return null;
					}
				} catch (Exception e) {
					closeResultSet();
					e.printStackTrace();
					return null;
				}
				rsCursor++;
				ArrayList l = new ArrayList();
				for (int k=1; k<=this.columns.size(); k++) {
					Object o = rs.getObject(k);
					l.add(o);
				}
				needCache = row < 100000; //360浏览器会发两次txt下载的请求，导致不cache就下载不全，暂定成十万条以内的记录都cache。2013/7/16
				if (needCache) cache.add(l);
				return l;
			}
			
		//}
		return null;
	}

	public static void main(String args[]) {
		Connection con = null;
		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			con = DriverManager.getConnection("jdbc:mysql://192.168.0.95:3306/weishijiao", "root", "123456");
//			con.setAutoCommit(true);
//			Statement stmt = con.createStatement();
//			System.out.println("con object = " + con);
//			//stmt.addBatch("select * from sjr_1367113792568");
//			stmt.addBatch("create table sjr_1367113792569 as (SELECT T_1.prov prov,T_1.city city FROM cust T_1)");
//			stmt.addBatch("commit");
//			stmt.executeBatch();

			Class.forName("com.ibm.db2.jcc.DB2Driver");
			con = DriverManager.getConnection("jdbc:db2://192.168.0.95:50000/demo", "db2admin", "root");
			con.setReadOnly(true);
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();
			//stmt.execute("create table xingjl_t1( id integer not null primary key,username varchar(200),password varchar(200))");
//			stmt.execute("insert into xingjl_t1 (id,username,password) values (1,'a','b')");
//			stmt.execute("insert into xingjl_t1 (id,username,password) values (2,'c','d')");
			//System.out.println(con);
			ResultSet rs = stmt.executeQuery("select * from xingjl_t1");
			while (rs.next()) {
				System.out.println("id : " + rs.getInt("id"));
			}
			
//			Statement stmt = con.createStatement();
//			System.out.println("con object = " + con);
//			con.setAutoCommit(false);
//			stmt.execute("drop table sjr_13687806658901");
////			stmt.execute("create table sjr_13687806658901 as (SELECT T_1.客户ID 客户ID FROM (SELECT * FROM 客户 WHERE 城市编码!=10101) T_1 WHERE 1=0) definition only;insert into sjr_13687806658901 SELECT T_1.客户ID 客户ID FROM (SELECT * FROM 客户 WHERE 城市编码!=10101) T_1 WHERE 1=0;commit;");
//			stmt.addBatch("create table sjr_13687806658901 as (SELECT T_1.客户ID 客户ID FROM (SELECT * FROM 客户 WHERE 城市编码!=10101) T_1 WHERE 1=0) definition only");
//			stmt.addBatch("insert into sjr_13687806658901 SELECT T_1.客户ID 客户ID FROM (SELECT * FROM 客户 WHERE 城市编码!=10101) T_1 WHERE 1=0");
			
			//stmt.execute("commit");
//			stmt.executeBatch();
			//con.commit();
//
//			Connection con1 = DriverManager.getConnection("jdbc:db2://192.168.0.232:50000/newdemo", "db2admin", "root");
//			PreparedStatement ps = con1.prepareStatement("INSERT INTO sjr_13687806658901 VALUES (?)");
//			ps.setInt(1, 1);
//			ps.execute();
//
//			ps = con1.prepareStatement("INSERT INTO sjr_13687806658901 VALUES (?)");
//			ps.setInt(1, 2);
//			ps.execute();
//
//			ps = con1.prepareStatement("INSERT INTO sjr_13687806658901 VALUES (?)");
//			ps.setInt(1, 3);
//			ps.execute();
//			
//			ResultSet rs = con1.createStatement().executeQuery("select * from sjr_13687806658901 where 客户ID in (select * from sjr_13687806658901)");
//			System.out.println(rs.getMetaData().getColumnLabel(1));
//			while (rs.next()) {
//				System.out.println(rs.getInt(1));
//			}
//			
////			stmt.execute("drop table sjr_13687806658902");
////			PreparedStatement ps = con.prepareStatement("create table sjr_13687806658902 as (select * from sjr_13687806658901) definition only");
////			
////			stmt.addBatch("create table sjr_13687806658902 as (select * from sjr_13687806658901) definition only");
//////			stmt.addBatch("commit");
////			stmt.addBatch("insert into sjr_13687806658902 select * from sjr_13687806658901");
////			stmt.addBatch("commit");
////			stmt.executeBatch();
////			
////			ResultSet rs = stmt.executeQuery("select * from sjr_13687806658902");
////			System.out.println(rs);
////			while (rs.next()) {
////				System.out.println(rs.getInt(1));
////			}

//			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//			con = DriverManager.getConnection("jdbc:sqlserver://192.168.0.232:1433;DatabaseName=dldemo", "sa", "root");
//			
//			Statement stmt = con.createStatement();
//			System.out.println("con object = " + con);
////			con.setAutoCommit(false);
//			
//			stmt.execute("drop table sjr_1369709762957");
			
//			Connection con1 = DriverManager.getConnection("jdbc:sqlserver://192.168.0.232:1433;DatabaseName=dldemo", "sa", "root");
//			
//			ResultSet rs = con1.createStatement().executeQuery("select * from sjr_1369709762957");
//			while (rs.next()) {
//				System.out.println(rs.getObject(1));
//			}

		} catch (SQLException e) {
			e.printStackTrace();
			//e.getNextException().printStackTrace(); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (true) return;
		try {
			FileOutputStream fos = new FileOutputStream("d:\\a.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(2);
			oos.flush();
			oos.writeShort(1);
			oos.writeObject("aaa");
			oos.writeShort(0);
			oos.flush();
			oos.close();
			fos.close();
			
			FileInputStream fis = new FileInputStream("d:\\a.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			//QueryTask qd = new QueryTask("d:\\a.dat");
			int version = ois.readInt();
			System.out.println(version);
			ArrayList datas = new ArrayList();
			for (int i = 0; i<1000; i++) {
				ArrayList row = IOUtils.readArrayList(ois);
				System.out.println(i + "--" + row);
				if (row == null) {
					break;
				}
				datas.add(row);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void setBack() {
		// TODO Auto-generated method stub
		
	}

	public String getID() {
		return taskID;
	}

	public void setID(String taskID) {
		this.taskID = taskID;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public ArrayList getColTypes() {
		return colTypes;
	}

	public String getColTypeStr() {
		String str = "";
		if (colTypes == null) return str;
		for (int i=0; i<colTypes.size(); i++) {
			if (i > 0) str += ",";
			str += colTypes.get(i).toString();
		}
		return str;
	}

	public String getColInfos() {
		String str = "";
		if (colTypes == null) return str;
		for (int i=0; i<colTypes.size(); i++) {
			if (i > 0) str += ";";
			str += columns.get(i).toString() + "," + colTypes.get(i).toString();
		}
		return str;
	}
//
//	public void setColTypes(ArrayList colTypes) {
//		this.colTypes = colTypes;
//	}

	public String getQyx() {
		return qyx;
	}

	public String getQyxFile() {
		if (qyx == null) return null;
		return new File(qyx).getName();
	}

	public void setQyx(String qyx) {
		this.qyx = qyx;
	}

	public long getExecTime() {
		return execTime;
	}

	public void setExecTime(long execTime) {
		this.execTime = execTime;
	}

}

