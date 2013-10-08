package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class AuditSqlGenerator extends SqlGenerator
{
	public AuditSqlGenerator ()
	{
		table_name = "Audit";
		key_column_names.add("AuditID");

		check_back_data = false;
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into Audit (" +
				"AuditID," +
				"Author," +
				"ItemID," +
				"AuditDate," +
				"Category," +
				"Action," +
				"Data," +
				"ModificationDate" +
				") values (" +
				"'" + rst.getString("AuditID") + "'," +
				"'" + rst.getString("Author") + "'," +
				"'" + rst.getString("ItemID") + "'," +
				rst.getString("AuditDate") + "," +
				"'" + rst.getString("Category") + "'," +
				rst.getString("Action") + "," +
				"'" + rst.getString("Data") + "'," +
				(derby_dialect ? rst.getString("ModificationDate") : now) +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("Audit insert SQLException: " + sex.getMessage());
			System.out.println("Audit insert SQLState: " + sex.getSQLState());
			System.out.println("Audit insert VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in AuditSqlGenerator getInsertSQL - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String audit_id, String not_used, boolean derby_dialect)
	{
		try
		{
			return "update Code set " +
				"Author='" + rst.getString("Author") + "'," +
				"ItemID='" + rst.getString("ItemID") + "'," +
				"AuditDate=" + rst.getString("AuditDate") + "," +
				"Category='" + rst.getString("Category") + "'," +
				"Action=" + rst.getString("Action") + "," +
				"Data='" + rst.getString("Data") + "'," +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) + " " +
				getWhereStatement(audit_id, null);
		}
		catch (SQLException sex)
		{
			System.out.println("Audit update SQLException: " + sex.getMessage());
			System.out.println("Audit update SQLState: " + sex.getSQLState());
			System.out.println("Audit update VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in AuditSqlGenerator getUpdateSQL - should not reach this point");
	}
}