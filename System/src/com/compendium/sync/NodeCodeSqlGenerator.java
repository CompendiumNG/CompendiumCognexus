package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class NodeCodeSqlGenerator extends SqlGenerator
{
	public NodeCodeSqlGenerator ()
	{
		table_name = "NodeCode";
		key_column_names.add("CodeID");
		key_column_names.add("NodeID");
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into NodeCode (" +
				"NodeID," +
				"CodeID," +
				"ModificationDate" +
				") values (" +
				"'" + rst.getString("NodeID") + "'," +
				"'" + rst.getString("CodeID") + "'," +
				(derby_dialect ? rst.getString("ModificationDate") : now) +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("NodeCode insert SQLException: " + sex.getMessage());
			System.out.println("NodeCode insert SQLState: " + sex.getSQLState());
			System.out.println("NodeCode insert VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in NodeCodeSqlGenerator..getInsertSQL - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String code_id, String node_id, boolean derby_dialect)
	{
		try
		{
			return "update NodeCode set " +
				"ModificationDate = " + (derby_dialect ? rst.getString("ModificationDate") : now) +
				getWhereStatement(code_id, node_id);
		}
		catch (SQLException sex)
		{
			System.out.println("NodeCode insert SQLException: " + sex.getMessage());
			System.out.println("NodeCode insert SQLState: " + sex.getSQLState());
			System.out.println("NodeCode insert VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in NodeCodeSqlGenerator.getUpdateInsertSQL - should not reach this point");
	}

}