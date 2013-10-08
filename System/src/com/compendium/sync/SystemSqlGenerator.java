package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class SystemSqlGenerator extends SqlGenerator
{
	public SystemSqlGenerator ()
	{
		table_name = "System";
		key_column_names.add("Property");

		check_back_data = false;
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into System (" +
				"Property," +
				"Contents," +
				"ModificationDate" +
				") values (" +
				"'" + escapeData(rst.getString("Property"),derby_dialect) + "'," +
				"'" + escapeData(rst.getString("Contents"),derby_dialect) + "'," +
				(derby_dialect ? rst.getString("ModificationDate") : now) +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("System insert SQLException: " + sex.getMessage());
			System.out.println("System insert SQLState: " + sex.getSQLState());
			System.out.println("System insert VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in System insert - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String property, String not_used, boolean derby_dialect)
	{
		try
		{
			return "update System set " +
				"Contents='" + escapeData(rst.getString("Contents"),derby_dialect) + "'," +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) + " " +
				getWhereStatement(property, null);
		}
		catch (SQLException sex)
		{
			System.out.println("System update SQLException: " + sex.getMessage());
			System.out.println("System update SQLState: " + sex.getSQLState());
			System.out.println("System update VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in System update - should not reach this point");
	}

	public String getSimpleModQuery (long last_sync_time)
	{
		return "select * from " + table_name + " where ModificationDate >= " + last_sync_time + " and Property <> 'defaultuser' " +
			" and Property <> 'last_sync_time'";
	}

}