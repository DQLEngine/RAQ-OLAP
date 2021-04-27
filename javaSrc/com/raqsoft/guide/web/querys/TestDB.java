package com.raqsoft.guide.web.querys;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.raqsoft.common.Logger;

public class TestDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection con = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(
					"jdbc:sqlserver://192.168.0.232:1433;DatabaseName=dldemo",
					"sa", "root");
			Statement stmt = con.createStatement();
			Logger.debug("con object = " + con);
//			stmt.execute("create table sjr_1369709762957(id int)");
//			stmt.execute("insert into sjr_1369709762957 values (1)");
//			stmt.execute("insert into sjr_1369709762957 values (2)");
//			stmt.execute("insert into sjr_1369709762957 values (3)");
			con.setAutoCommit(false);
			Logger.debug(stmt.execute("drop table sjr_1369709762957"));
//														sjr_1369709762957
			//stmt.execute("select * from sjr");
			stmt.close();
			con.close();
//			Thread.currentThread().sleep(30 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}		
		
	}

}
