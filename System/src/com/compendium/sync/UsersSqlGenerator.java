package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.*;

public class UsersSqlGenerator extends SqlGenerator
{
	public UsersSqlGenerator ()
	{
		table_name = "Users";
		key_column_names.add("UserID");
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into Users (" +
				"UserID," +
				"Author," +
				"CreationDate," +
				"ModificationDate," +
				"Login," +
				"Name," +
				"Password," +
				"Description," +
				"HomeView," +
				"IsAdministrator," +
				"CurrentStatus," +
				"LinkView" +
				") values (" +
				"'" + rst.getString("UserID") + "'," +
				"'" + rst.getString("Author") + "'," +
				rst.getString("CreationDate") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"'" + rst.getString("Login") + "'," +
				"'" + rst.getString("Name") + "'," +
				"'" + rst.getString("Password") + "'," +
				"'" + rst.getString("Description") + "'," +
				"'" + rst.getString("HomeView") + "'," +
				"'" + rst.getString("IsAdministrator") + "'," +
				rst.getString("CurrentStatus") + "," +
				"'" + rst.getString("LinkView") + "'" +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("NodeCode insert SQLException: " + sex.getMessage());
			System.out.println("NodeCode insert SQLState: " + sex.getSQLState());
			System.out.println("NodeCode insert VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in CodeGroupSqlGenerator..getInsertSQL - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String user_id, String not_used, boolean derby_dialect)
	{
		try
		{
			return "update Users set " +
				"Author='" + rst.getString("Author") + "'," +
				"CreationDate="+ rst.getString("CreationDate") + "," +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"Login='" + rst.getString("Login") + "'," +
				"Name='" + rst.getString("Name") + "'," +
				"Password='" + rst.getString("Password") + "'," +
				"Description='" + rst.getString("Description") + "'," +
				"HomeView='" + rst.getString("HomeView") + "'," +
				"IsAdministrator='" + rst.getString("IsAdministrator") + "'," +
				"CurrentStatus=" + rst.getString("CurrentStatus") + "," +
				"LinkView='" + rst.getString("LinkView") + "'" +
				getWhereStatement(user_id, null);
		}
		catch (SQLException sex)
		{
			System.out.println("NodeCode insert SQLException: " + sex.getMessage());
			System.out.println("NodeCode insert SQLState: " + sex.getSQLState());
			System.out.println("NodeCode insert VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in CodeGroupSqlGenerator.getUpdateInsertSQL - should not reach this point");
	}

	protected void getDeletes (Synchronizer sync, Connection src, long last_sync_time, Vector<String> tgt)
	{
		ResultSet rst = sync.issueSelectQuery(src, "select * from Audit where Category = 'User:Delete'");

		try
		{
			while (rst.next())
			{
				tgt.add("delete from User where UserID = '" + rst.getString("ItemID") + "'");
			}
		}
		catch (SQLException sex)
		{
			System.out.println("User deletes SQLException: " + sex.getMessage());
			System.out.println("User deletes SQLState: " + sex.getSQLState());
			System.out.println("User deletes VendorError: " + sex.getErrorCode());
			System.out.flush();
		}
	}
}