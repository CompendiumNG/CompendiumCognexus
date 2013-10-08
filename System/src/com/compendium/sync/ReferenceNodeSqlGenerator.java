package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class ReferenceNodeSqlGenerator extends SqlGenerator
{
	public ReferenceNodeSqlGenerator ()
	{
		table_name = "ReferenceNode";
		key_column_names.add("NodeID");
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into ReferenceNode (" +
				"NodeID," +
				"Source," +
				"ImageSource," +
				"ImageWidth," +
				"ImageHeight," +
				"ModificationDate" +
				") values (" +
				"'" + rst.getString("NodeID") + "'," +
				"'" + escapeData(rst.getString("Source"),derby_dialect) + "'," +
				"'" + escapeData(rst.getString("ImageSource"),derby_dialect) + "'," +
				rst.getString("ImageWidth") + "," +
				rst.getString("ImageHeight") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("ReferenceNodeSqlGenerator insert SQLException: " + sex.getMessage());
			System.out.println("ReferenceNodeSqlGenerator insert SQLState: " + sex.getSQLState());
			System.out.println("ReferenceNodeSqlGenerator insert VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in getNodeDetailInsertSQL - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String node_id, String not_used, boolean derby_dialect)
	{
		try
		{
			return "update ReferenceNode set " +
				"Source='" + escapeData(rst.getString("Source"),derby_dialect) + "' " +
				"ImageSource='" + escapeData(rst.getString("ImageSource"),derby_dialect) + "'," +
				"ImageWidth=" + rst.getString("ImageWidth") + "," +
				"ImageHeight=" + rst.getString("ImageHeight") + ", " +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) + " " +
				getWhereStatement(node_id, null);
		}
		catch (SQLException sex)
		{
			System.out.println("ReferenceNodeSqlGenerator update SQLException: " + sex.getMessage());
			System.out.println("ReferenceNodeSqlGenerator update SQLState: " + sex.getSQLState());
			System.out.println("ReferenceNodeSqlGenerator update VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in getNodeInsertSQL - should not reach this point");
	}
}