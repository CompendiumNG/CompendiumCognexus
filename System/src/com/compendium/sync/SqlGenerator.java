package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.*;

public abstract class SqlGenerator
{
	protected String table_name;

	protected boolean check_back_data = false;

	protected boolean has_deletes = false;

	//ALWAYS ALWAYS alphabetical!
	protected Vector<String> key_column_names = new Vector<String>();

	public abstract String getUpdateSQL(ResultSet rst, String key1, String key2, boolean derby_dialect);

	public abstract String getInsertSQL(ResultSet rst, boolean derby_dialect);

	protected long now = new java.util.Date().getTime();

	public String escapeData (String s, boolean derby_dialect)
	{
		if ((s == null) || s.trim().equals("") )
			return s;

		if (s.indexOf('\'') == -1 && s.indexOf('\\') == -1)
			return s;

		//System.out.println("22 SqlGenerator entered escapeData s is " + s);
		//System.out.flush();

		// 4 backslashes here equals a single one in the final output
		if (!derby_dialect)
		{
			s = s.replaceAll("\\\\", "\\\\\\\\");
		}

		//System.out.println("28 SqlGenerator s is now " + s);
		//System.out.flush();

		if (derby_dialect)
		{
			s = s.replaceAll("'", "''");
		}
		else // mysql dialect
		{
			s = s.replaceAll("'", "\\\\'");
		}

		//System.out.println("40 SqlGenerator s is now " + s);
		//System.out.flush();

		return s;
	}

	public String getTableName ()
	{
		return table_name;
	}

	public int getKeyColumnCount()
	{
		return key_column_names.size();
	}

	// send the keys in alphabetical by key column name
	public String getWhereModStatement(String key1, String key2, long last_sync_time)
	{
		return getWhereStatement(key1, key2) + " and ModificationDate >= " + last_sync_time;
	}

	public String getSimpleModQuery (long last_sync_time)
	{
		return "select * from " + table_name + " where ModificationDate >= " + last_sync_time;
	}

	//input HAS to be alphabetical by key column name!!
	public String getWhereStatement(String key0, String key1)
	{
		//there is always a key 1
		return " where " + key_column_names.get(0) + "='" + key0 + "'" + (key1 != null ? " and " + key_column_names.get(1) + "='" + key1 + "'": "");
	}

	public String getColumnValue(ResultSet rst, String column_name)
	{
		try
		{
			String val = rst.getString(column_name);
			return val;
		}
		catch (SQLException sex)
		{
			//do nothing
		}

		//maybe the ResultSet needs to be moved to the first row - could get the resultset metadata first to see if the col name is there....
		try
		{
			rst.next();
			String val = rst.getString(column_name);
			return val;
		}
		catch (SQLException sex)
		{
			//do nothing
		}
		return null;
	}

	public void generateStatements(Synchronizer sync, Vector<String> derby_statements, Vector<String> mysql_statements, long last_sync_time,
		Connection mysql_conn, Connection derby_conn)
	{
		sync.log(table_name + "--" + table_name + "--" + table_name);

		if (check_back_data)
			checkBackData(sync, mysql_conn, last_sync_time, derby_statements);

		if (has_deletes)
		{
			getDeletes(sync, mysql_conn, last_sync_time, derby_statements);
			getDeletes(sync, derby_conn, last_sync_time, mysql_statements);
		}

		//mysql to derby...
		generateOneSideStatements(mysql_conn, derby_conn, last_sync_time, derby_statements, sync, true);
		//derby to mysql...
		generateOneSideStatements(derby_conn, mysql_conn, last_sync_time, mysql_statements, sync, false);
	}

	protected void generateOneSideStatements (Connection src, Connection tgt, long last_sync_time, Vector<String> statements, Synchronizer sync, boolean mysql_to_derby)
	{
		try
		{
			//get data keys by mod time from source system
			ResultSet rst = sync.issueSelectQuery(src, getSimpleModQuery(last_sync_time));
			while (rst.next())
			{
				String key0 = getColumnValue(rst,key_column_names.get(0));
				String key1 = (key_column_names.size() > 1 ?  getColumnValue(rst,key_column_names.get(1)) : null);

				//check if the data key exists on the target system
				if (sync.getRowCount(table_name, tgt, getWhereStatement(key0, key1)) == 0)
				{
					//data does not exist on target system, insert
					statements.add(getInsertSQL(rst, mysql_to_derby));
				}
				else
				{
					//does the data exist on the target system and is the ModificationDate greater than last_sync_time?
					if (sync.getRowCount(table_name, tgt, getWhereModStatement(key0,key1,last_sync_time)) == 1)
					{
						//The data DOES exist and mod time is after last sync, this indicates a conflict
						sync.logConflict(table_name + " " + getWhereStatement(key0, key1) + " " + getConflictExpanded(rst));
						appendConflictReport(sync, rst);

						if (mysql_to_derby)
						{
							sync.log("156 SqlGenerator, conflict - getting update SQL");
							sync.log("update sql is " + getUpdateSQL(rst, key0, key1, mysql_to_derby));

							statements.add(getUpdateSQL(rst, key0, key1, mysql_to_derby));
						}
					}
					else
					{
						//The data exists but mod date has not been touched, just a one way update
						statements.add(getUpdateSQL(rst, key0, key1, mysql_to_derby));
					}
				}
			}
		}
		catch (SQLException sex)
		{
			System.out.println("174 SqlGenerator generateOneSideStatements SQLException: " + sex.getMessage());
			System.out.println("175 SqlGenerator generateOneSideStatements SQLState: " + sex.getSQLState());
			System.out.println("176 SqlGenerator generateOneSideStatements VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

	}

	protected String getConflictExpanded (ResultSet rst)
	{
		StringBuffer str = new StringBuffer("\nDetail row data for conflict:\n");

		try
		{

			ResultSetMetaData rsmd = rst.getMetaData();

			for (int i=1; i<=rsmd.getColumnCount(); i++)
			{
				String col_name = rsmd.getColumnName(i);
				String col_valu = rst.getString(i);
				//System.out.println("\t" + col_name + ": " + col_valu + "\n");
				//System.out.flush();
				str.append("\t" + col_name + ": " + col_valu + "\n");
			}
		}
		catch (SQLException sex)
		{
			System.out.println("202 SqlGenerator getConflictExpanded SQLException: " + sex.getMessage());
			System.out.println("203 SqlGenerator getConflictExpanded SQLState: " + sex.getSQLState());
			System.out.println("204 SqlGenerator getConflictExpanded VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		return str.toString();
	}

	protected void checkBackData (Synchronizer sync, Connection mysql_conn, long last_sync_time, Vector<String> derby_statements)
	{

		//scan mysql for rows not in derby - how and why is this happening?  check those not already checked by Mod Date.
		ResultSet rst = sync.issueSelectQuery(mysql_conn, "select * from " + table_name + " where ModificationDate < " + last_sync_time);
		try
		{
			while (rst.next())
			{
				//String node_id = rst.getString("NodeID");
				if (sync.getRowCount(table_name, mysql_conn, getWhereStatement(rst.getString(key_column_names.get(0)), ( key_column_names.size() == 2 ?  rst.getString(key_column_names.get(1)) : null)) ) == 0)
				{
					System.out.println("103 " + table_name + " found row that exists on MySQL not derby with older Mod Date");
					System.out.flush();

					derby_statements.add(getInsertSQL(rst, true));
				}
			}
		}
		catch (SQLException sex)
		{
			System.out.println("232 SqlGenerator Update SQLException: " + sex.getMessage());
			System.out.println("233 SqlGenerator Update SQLState: " + sex.getSQLState());
			System.out.println("234 SqlGenerator Update VendorError: " + sex.getErrorCode());
			System.out.flush();
			//System.exit(1);
		}
	}

	protected void getDeletes (Synchronizer sync, Connection src, long last_sync_time, Vector<String> tgt)
	{

	}

	protected void appendConflictReport (Synchronizer sync, ResultSet rst)
	{

	}
}
