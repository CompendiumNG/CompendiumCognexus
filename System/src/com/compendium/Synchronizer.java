package com.compendium;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.sync.*;

import javax.swing.JFrame;
import javax.swing.JTextArea;


public class Synchronizer
{
	private String sync_log_dir = System.getenv("CompendiumUserPath") + "/System/resources/Logs/";

	private String sync_log_name;

	private String sync_log_file;

	private String mysql_url = null;
	private String derby_url = null;

	long last_sync_time = -1;

	NodeSqlGenerator     node_gen      = new NodeSqlGenerator();
	ViewNodeSqlGenerator view_node_gen = new ViewNodeSqlGenerator();
	ViewPropertySqlGenerator view_property_gen = new ViewPropertySqlGenerator();
	NodeDetailSqlGenerator node_detail_gen = new NodeDetailSqlGenerator();
	LinkSqlGenerator link_gen = new LinkSqlGenerator();
	ViewLinkSqlGenerator view_link_gen = new ViewLinkSqlGenerator();
	CodeSqlGenerator code_gen = new CodeSqlGenerator();
	NodeCodeSqlGenerator node_code_gen = new NodeCodeSqlGenerator();
	NodeUserStateSqlGenerator node_user_state_gen = new NodeUserStateSqlGenerator();
	CodeGroupSqlGenerator code_group_gen = new CodeGroupSqlGenerator();
	GroupCodeSqlGenerator group_code_gen = new GroupCodeSqlGenerator();
	UsersSqlGenerator users_gen = new UsersSqlGenerator();
	ReferenceNodeSqlGenerator reference_node_gen = new ReferenceNodeSqlGenerator();
	AuditSqlGenerator audit_gen = new AuditSqlGenerator();
	SystemSqlGenerator system_gen = new SystemSqlGenerator();
	ViewLayerSqlGenerator view_layer_gen = new ViewLayerSqlGenerator();
	FavoriteSqlGenerator favorite_gen = new FavoriteSqlGenerator();

	private Vector<String> derby_statements = new Vector<String>();  //statements to issue against the derby server
	private Vector<String> mysql_statements = new Vector<String>();

	public Connection mysql_conn;
	Connection derby_conn;

	boolean verbose = true;

	boolean set_log_only_to_system_out = false;

	private StringBuffer conflict_report = new StringBuffer();
	private int conflict_hits = 0;

	public void setMySQLUrl (String url)
	{
		mysql_url = url;
	}

	public String getMySQLUrl ()
	{
		return mysql_url;
	}

	public void setDerbyUrl (String url)
	{
		derby_url = url;
	}

	public String getDerbyUrl ()
	{
		return derby_url;
	}

	public void setLogDir (String s)
	{
		sync_log_dir = s;

		//set the file too
		java.util.Date now = new java.util.Date();

		sync_log_file = sync_log_dir + "sync_" + now.toString().replace(' ', '_').replace(':', '_') + ".log";
	}

	public String getLogDir ()
	{
		return sync_log_dir;
	}

	public void setMySQLConnection (String uname, String pass)
	{
		System.out.println("79 Synchronizer entered setMySQLConnection");
		Connection conn = null;
		mysql_conn = null;

		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println("MySQL Class.forName() call good");
		}
		catch (ClassNotFoundException cnfe)
		{
			System.out.println("89 Synchronizer MySQL Class not found, " + cnfe.getMessage());
			System.out.flush();
			System.exit(1);
		}
		catch (Exception ex)
		{
			System.out.println("94 Synchronizer MySQL Failed Class.forName() call");
			System.out.println("95 Synchronizer MySQL Exception: " + ex.getMessage());
			System.out.flush();
		}

		System.out.println("101 Synchronizer driver ok, going to get Connection");

		try
		{
			log("MySQL Trying: " + mysql_url);
			conn = DriverManager.getConnection(mysql_url, uname, pass);
			log("MySQL Connection --> OK");
		}
		catch (SQLException sex)
		{
			System.out.println("118 Synchronizer MySQL SQLException: " + sex.getMessage());
			System.out.println("119 Synchronizer MySQL SQLState: " + sex.getSQLState());
			System.out.println("120 Synchronizer MySQL VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		if (conn == null)
		{
			System.out.println("109 Synchronizer MySQL connection is null.");
			System.out.flush();
		}

		System.out.println("120 Synchronizer exiting setMySQLConnection");
		mysql_conn = conn;

	}

	public void setMySQLConnection ()
	{
		System.out.println("79 Synchronizer entered setMySQLConnection");
		Connection conn = null;
		mysql_conn = null;

		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println("MySQL Class.forName() call good");
		}
		catch (ClassNotFoundException cnfe)
		{
			System.out.println("89 Synchronizer MySQL Class not found, " + cnfe.getMessage());
			System.out.flush();
			System.exit(1);
		}
		catch (Exception ex)
		{
			System.out.println("94 Synchronizer MySQL Failed Class.forName() call");
			System.out.println("95 Synchronizer MySQL Exception: " + ex.getMessage());
			System.out.flush();
		}

		System.out.println("101 Synchronizer driver ok, going to get Connection");

		try
		{
			log("MySQL Trying: " + mysql_url);
			conn = DriverManager.getConnection(mysql_url);
			log("MySQL Connection --> OK");
		}
		catch (SQLException sex)
		{
			System.out.println("168 Synchronizer MySQL SQLException: " + sex.getMessage());
			System.out.println("169 Synchronizer MySQL SQLState: " + sex.getSQLState());
			System.out.println("170 Synchronizer MySQL VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		if (conn == null)
		{
			System.out.println("109 Synchronizer MySQL connection is null.");
			System.out.flush();
		}

		System.out.println("120 Synchronizer exiting setMySQLConnection");
		mysql_conn = conn;
	}

	public Connection getMySQLConnection ()
	{
		return mysql_conn;
	}

	public void setDerbyConnection ()
	{
		Connection conn = null;

		try
		{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			//System.out.println("Derby Class.forName() call good");
		}
		catch (ClassNotFoundException cnfe)
		{
			System.out.println("Deby Class not found, " + cnfe.getMessage());
		}
		catch (Exception ex)
		{
			System.out.println("Derby Failed Class.forName() call");
			System.out.println("Derby Exception: " + ex.getMessage());
			System.out.flush();
		}

		try
		{
			log("Derby Trying: " + derby_url);
			conn = DriverManager.getConnection(derby_url);
			log("Derby Connection --> OK");
		}
		catch (SQLException sex)
		{
			System.out.println("217 Synchronizer Derby SQLException: " + sex.getMessage());
			System.out.println("218 Synchronizer Derby SQLState: " + sex.getSQLState());
			System.out.println("219 Synchronizer Derby VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		if (conn == null)
		{
			System.out.println("155 Synchronizer Derby connection is null.");
			System.out.flush();
			//System.exit(1);
		}

		derby_conn = conn;
	}

	public Connection getDerbyConnection ()
	{
		return derby_conn;
	}

	public void setLogOnlyToSystemOut (boolean b)
	{
		//System.out.println("171 ProjectCompendiumFrame setting log only to system out to " + b);
		set_log_only_to_system_out = b;
	}

	public void setVerbose (boolean b)
	{
		verbose = b;
	}

	public ResultSet issueSelectQuery (Connection conn, String s)
	{
		ResultSet rs = null;
		try
		{
			Statement stmt = conn.createStatement();
			if (verbose)
			{
				log("s:");
				log("\n" + s);
			}
			rs = stmt.executeQuery(s);
		}
		catch (SQLException sex)
		{
			System.out.println("264 Synchronizer SQLException: " + sex.getMessage());
			System.out.println("265 Synchronizer SQLState: " + sex.getSQLState());
			System.out.println("266 Synchronizer VendorError: " + sex.getErrorCode());
			System.out.println("267 Synchronizer sql is " + s);
			System.out.flush();
			//System.exit(1);
		}

		return rs;
	}

	public void issueUpdateQuery (Connection conn, String s)
	{
		System.out.println("276 Synchronizer issueUpdateQuery sql in is " + s);
		System.out.flush();

		//ResultSet rs = null;
		try
		{
			Statement stmt = conn.createStatement();
			//if (verbose)
			//{
			//	log("u:");
			//	log("\n" + s);
			//}
			stmt.executeUpdate(s);
		}
		catch (SQLException sex)
		{
			System.out.println("290 Synchronizer issueUpdateQuery SQLException: " + sex.getMessage());
			System.out.println("293 Synchronizer issueUpdateQuery sql is " + s);
			System.out.println("291 Synchronizer issueUpdateQuery SQLState: " + sex.getSQLState());
			System.out.println("292 Synchronizer issueUpdateQuery VendorError: " + sex.getErrorCode());
			System.out.flush();
			//System.exit(1);
		}

		//return rs;
	}

	public int getRowCount(String table_name, Connection conn, String where_clause)
	{
		String sql = "select count(*) as row_count from " + table_name + " " + where_clause;

		ResultSet rst = issueSelectQuery(conn, sql);
		try
		{
			rst.next();
			return rst.getInt("row_count");
		}
		catch (SQLException sex)
		{
			System.out.println("313 Synchronizer SQLException: " + sex.getMessage());
			System.out.println("314 Synchronizer SQLState: " + sex.getSQLState());
			System.out.println("315 Synchronizer VendorError: " + sex.getErrorCode());
			System.out.flush();
			//System.exit(1);
		}

		throw new RuntimeException("Exception in getRowCount - should not reach this point");
	}

	public String getColumnValue(ResultSet rst, String column_name)
	{
		String val = "initialized";

		try
		{
			val = rst.getString(column_name);
			return val;
		}
		catch (SQLException sex)
		{
			//expected - ignore
			//System.out.println("getColumnValue SQLException: " + sex.getMessage());
			//System.out.println("getColumnValue SQLState: " + sex.getSQLState());
			//System.out.println("getColumnValue VendorError: " + sex.getErrorCode());

		}

		//maybe the rst is not moved to the first row

		try
		{
			rst.next();
			val = rst.getString(column_name);
			return val;
		}
		catch (SQLException sex)
		{
			//expected - ignore
			//System.out.println("getColumnValue SQLException: " + sex.getMessage());
			//System.out.println("getColumnValue SQLState: " + sex.getSQLState());
			//System.out.println("getColumnValue VendorError: " + sex.getErrorCode());

		}


		val = "## nothing returned for column " + column_name + "##";

		return val;
	}

	public void logConflict(String msg)
	{
		log("\n\nSyncConflict ------------------------------------: \n" + msg + "\n-----------------------------------\n\n");
	}

	public void log (String msg)
	{
		System.out.println("378 Synchronizer LOG: " + msg);
		System.out.flush();
/*
		if (set_log_only_to_system_out)
			return;

		try
		{
			FileWriter fstream = new FileWriter(sync_log_file,true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(msg + "\n");
			out.close();
		}
		catch (Exception e)
		{
			System.err.println("393 Synchronizer Error: " + e.getMessage());
		}
*/
	}

	public void generateStatements(long last_sync_time, JFrame f, JTextArea t)
	{
		boolean graphic = ( f == null ? false : true);

		if (graphic) repaint(f, t, "Node table...");
		node_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "ViewNode table...");
		view_node_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "ViewProperty table...");
		view_property_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "NodeDetail table...");
		node_detail_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "Link table...");
		link_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "ViewLink table...");
		view_link_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "Code table...");
		code_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "NodeCode table...");
		node_code_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "NodeUserState table...");
		node_user_state_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "CodeGroup table...");
		code_group_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "GroupCode table...");
		group_code_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "Users table...");
		users_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "ReferenceNode table...");
		reference_node_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "Audit table...");
		audit_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "System table...");
		system_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "ViewLayer table...");
		view_layer_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		if (graphic) repaint(f, t, "Favorite table...");
		favorite_gen.generateStatements(this, derby_statements, mysql_statements, last_sync_time, mysql_conn, derby_conn);

		System.out.flush();
	}

	public void repaint(JFrame f, JTextArea _resultArea, String message)
	{
		_resultArea.setText(message);
		f.paint(f.getGraphics());
	}

	public void send (JFrame f, JTextArea _resultArea)
	{
		// ----------- process statements mysql down to derby -----------------------

		if ((f != null) && (_resultArea != null))
		{
			_resultArea.setText("Sending data from mysql to derby...");
			f.paint(f.getGraphics());
		}

		for (int i=0; i<derby_statements.size(); i++)
		{
			log("processing to derby " + derby_statements.get(i));
			issueUpdateQuery(derby_conn, derby_statements.get(i));
		}

		// ----------- process statements derby up to mysql -------------------------
		if ((f != null) && (_resultArea != null))
		{
			_resultArea.setText("Sending data from derby to mysql...");
			f.paint(f.getGraphics());
		}

		for (int i=0; i<mysql_statements.size(); i++)
		{
			log("processing to mysql" + mysql_statements.get(i));
			issueUpdateQuery(mysql_conn, mysql_statements.get(i));
		}

	}

	public void appendConflictReport (String s)
	{
		conflict_hits++;
		conflict_report.append(s);
	}

	public String getConflictReport ()
	{
		if (conflict_report.toString().trim().equals(""))
			return " No Conflicts.";

		return "\nConflict Report:\n" + (conflict_hits/2) + " possible Node Conflict(s)\n" +
				"(A single NodeID may trigger multiple conflict indications.)\n" +
				conflict_report.toString();
	}
}