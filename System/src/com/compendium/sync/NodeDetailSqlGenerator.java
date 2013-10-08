package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.*;

public class NodeDetailSqlGenerator extends SqlGenerator
{
	public NodeDetailSqlGenerator ()
	{
		table_name = "NodeDetail";
		key_column_names.add("NodeID");
		key_column_names.add("PageNo");
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into NodeDetail (" +
				"NODEID," +
				"AUTHOR," +
				"PAGENO," +
				"CREATIONDATE," +
				"MODIFICATIONDATE," +
				"DETAIL" +
				") values (" +
				"'" + rst.getString("NodeID") + "'," +
				"'" + rst.getString("Author") + "'," +
				rst.getString("PageNo") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				rst.getString("CreationDate") + "," +
				"'" + escapeData(rst.getString("Detail"), derby_dialect) + "'" +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("NodeDetail insert SQLException: " + sex.getMessage());
			System.out.println("NodeDetail insert SQLState: " + sex.getSQLState());
			System.out.println("NodeDetail insert VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in NodeDetailSqlGenerator getInsertSQL - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String node_id, String page_no, boolean derby_dialect)
	{
		try
		{
			return "update NodeDetail set " +
				"Author='" + rst.getString("Author") + "'," +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"CreationDate=" + rst.getString("CreationDate") + "," +
				"Detail ='" + escapeData(rst.getString("Detail"), derby_dialect) + "' " +
				getWhereStatement(node_id, page_no);
		}
		catch (SQLException sex)
		{
			System.out.println("NodeDetail update SQLException: " + sex.getMessage());
			System.out.println("NodeDetail update SQLState: " + sex.getSQLState());
			System.out.println("NodeDetail update VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in NodeDetailSqlGenerator getUpdateSQL - should not reach this point");
	}

	//WHY:  Override here because second key is INT type
	public String getWhereStatement(String key0, String key1)
	{
		//second key is int type
		return " where " + key_column_names.get(0) + "='" + key0 + "' and " + key_column_names.get(1) + "=" + key1;
	}

/*
	protected void appendConflictReport (Synchronizer sync, ResultSet rst)
	{
		try
		{
			sync.appendConflictReport("\nNodeDetail Page: " + rst.getString("PAGENO") + " Detail:" + rst.getString("DETAIL") + " (NodeID " + rst.getString("NodeID") + ")");
		}
		catch (SQLException sex)
		{
			System.out.println("NodeDetail Update SQLException: " + sex.getMessage());
			System.out.println("NodeDetail Update SQLState: " + sex.getSQLState());
			System.out.println("NodeDetail Update VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

	}
*/

}