package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.*;

public class FavoriteSqlGenerator extends SqlGenerator
{
	public FavoriteSqlGenerator ()
	{
		table_name = "Favorite";
		key_column_names.add("NodeID");
		key_column_names.add("UserID");
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into Favorite(" +
				"USERID," +
				"NODEID," +
				"LABEL," +
				"NODETYPE," +
				"CREATIONDATE," +
				"MODIFICATIONDATE," +
				"VIEWID" +
				") values (" +
				"'" + rst.getString("UserId") + "'," +
				"'" + rst.getString("NodeId") + "'," +
				"'" + rst.getString("Label") + "'," +
				rst.getString("NodeType") + "," +
				rst.getString("CreationDate") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"'" + rst.getString("ViewID") + "'" +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("SQLException: " + sex.getMessage());
			System.out.println("SQLState: " + sex.getSQLState());
			System.out.println("VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in " + table_name + " getInsertSQL - should not reach this point");
	}

	public String getUpdateSQL (ResultSet rst, String node_id, String user_id, boolean derby_dialect)
	{
		try
		{
			return "update ViewNode set " +
				"LABEL='" + rst.getString("Label") + "'," +
				"NODETYPE='" + rst.getString("NodeType") + "'," +
				"CREATIONDATE=" + rst.getString("CreationDate") + "," +
				"MODIFICATIONDATE=" + (derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"VIEWID='" + rst.getString("ViewID") + "' " +
				getWhereStatement(node_id, user_id);
		}
		catch (SQLException sex)
		{
			System.out.println("SQLException: " + sex.getMessage());
			System.out.println("SQLState: " + sex.getSQLState());
			System.out.println("VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in " + table_name + " getUpdateSQL - should not reach this point");
	}

}