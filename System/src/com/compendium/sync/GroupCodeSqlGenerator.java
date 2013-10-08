package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class GroupCodeSqlGenerator extends SqlGenerator
{
	public GroupCodeSqlGenerator ()
	{
		table_name = "GroupCode";
		key_column_names.add("CodeGroupID");
		key_column_names.add("CodeID");
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into " + table_name + " (" +
				"CodeID," +
				"CodeGroupID," +
				"Author," +
				"CreationDate," +
				"ModificationDate" +
				") values (" +
				"'" + rst.getString("CodeID") + "'," +
				"'" + rst.getString("CodeGroupID") + "'," +
				"'" + rst.getString("Author") + "'," +
				rst.getString("CreationDate") + "," +
				( derby_dialect ? rst.getString("ModificationDate") : now ) +
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


	public String getUpdateSQL (ResultSet rst, String code_group_id, String code_id, boolean derby_dialect)
	{
		try
		{
			return "update " + table_name + " set " +
				"Author='" + rst.getString("Author") + "'," +
				"CreationDate="+ rst.getString("CreationDate") + "," +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) +
				getWhereStatement(code_group_id, code_id);
		}
		catch (SQLException sex)
		{
			System.out.println("NodeCode insert SQLException: " + sex.getMessage());
			System.out.println("NodeCode insert SQLState: " + sex.getSQLState());
			System.out.println("NodeCode insert VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in CodeGroupSqlGenerator.getUpdateInsertSQL - should not reach this point");
	}

}