package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class NodeUserStateSqlGenerator extends SqlGenerator
{

	public NodeUserStateSqlGenerator ()
	{
		table_name = "NodeUserState";
		key_column_names.add("NodeID");
		key_column_names.add("UserID");
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into NodeUserState (" +
				"NodeID," +
				"UserID," +
				"State," +
				"ModificationDate" +
				") values (" +
				"'" + rst.getString("NodeID") + "'," +
				"'" + rst.getString("UserID") + "'," +
				rst.getString("State") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("NodeUserState insert SQLException: " + sex.getMessage());
			System.out.println("NodeUserState insert SQLState: " + sex.getSQLState());
			System.out.println("NodeUserState insert VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in getNodeDetailInsertSQL - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String node_id, String user_id, boolean derby_dialect)
	{
		try
		{
			return "update NodeUserState set " +
				"State=" + rst.getString("State") + "," +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) + " " +
				getWhereStatement(node_id, user_id);
		}
		catch (SQLException sex)
		{
			System.out.println("NodeUserState update SQLException: " + sex.getMessage());
			System.out.println("NodeUserState update SQLState: " + sex.getSQLState());
			System.out.println("NodeUserState update VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in getNodeInsertSQL - should not reach this point");
	}


}