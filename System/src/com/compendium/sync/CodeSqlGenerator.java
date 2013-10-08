package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class CodeSqlGenerator extends SqlGenerator
{
	public CodeSqlGenerator ()
	{
		table_name = "Code";
		key_column_names.add("CodeID");

		check_back_data = false;
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into Code (" +
				"CodeID," +
				"Author," +
				"CreationDate," +
				"ModificationDate," +
				"Name," +
				"Description," +
				"Behavior" +
				") values (" +
				"'" + rst.getString("CodeID") + "'," +
				"'" + rst.getString("Author") + "'," +
				rst.getString("CreationDate") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"'" + rst.getString("Name") + "'," +
				"'" + rst.getString("Description") + "'," +
				"'" + rst.getString("Behavior") + "'" +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("NodeCode insert SQLException: " + sex.getMessage());
			System.out.println("NodeCode insert SQLState: " + sex.getSQLState());
			System.out.println("NodeCode insert VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in getNodeDetailInsertSQL - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String code_id, String not_used, boolean derby_dialect)
	{
		try
		{
			return "update Code set " +
				"Author='" + rst.getString("Author") + "'," +
				"CreationDate=" + rst.getString("CreationDate") + "," +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"Name='" + rst.getString("Name") + "'," +
				"Description='" + rst.getString("Description") + "'," +
				"Behavior='" + rst.getString("Behavior") + "' " +
				getWhereStatement(code_id, null);
		}
		catch (SQLException sex)
		{
			System.out.println("NodeCode update SQLException: " + sex.getMessage());
			System.out.println("NodeCode update SQLState: " + sex.getSQLState());
			System.out.println("NodeCode update VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in getNodeInsertSQL - should not reach this point");
	}
}