package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class CodeGroupSqlGenerator extends SqlGenerator
{
	public CodeGroupSqlGenerator ()
	{
		table_name = "CodeGroup";
		key_column_names.add("CodeGroupID");

		check_back_data = true;
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into CodeGroup (" +
				"CodeGroupID," +
				"Author," +
				"Name," +
				"Description," +
				"CreationDate," +
				"ModificationDate" +
				") values (" +
				"'" + rst.getString("CodeGroupID") + "'," +
				"'" + rst.getString("Author") + "'," +
				"'" + rst.getString("Name") + "'," +
				"'" + rst.getString("Description") + "'," +
				rst.getString("CreationDate") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("CodeGroup insert SQLException: " + sex.getMessage());
			System.out.println("CodeGroup insert SQLState: " + sex.getSQLState());
			System.out.println("CodeGroup insert VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in CodeGroupSqlGenerator..getInsertSQL - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String code_group_id, String not_used, boolean derby_dialect)
	{
		try
		{
			return "update CodeGroup set " +
				"Author='" + rst.getString("Author") + "'," +
				"Name='" + rst.getString("Name") + "'," +
				"Description='" + rst.getString("Description") + "'," +
				"CreationDate="+ rst.getString("CreationDate") + "," +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) +
				getWhereStatement(code_group_id, null);
		}
		catch (SQLException sex)
		{
			System.out.println("CodeGroup insert SQLException: " + sex.getMessage());
			System.out.println("CodeGroup insert SQLState: " + sex.getSQLState());
			System.out.println("CodeGroup insert VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in CodeGroupSqlGenerator.getUpdateInsertSQL - should not reach this point");
	}

}