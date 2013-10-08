package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class ViewLinkSqlGenerator extends SqlGenerator
{
	public ViewLinkSqlGenerator ()
	{
		table_name = "ViewLink";
		key_column_names.add("LinkID");
		key_column_names.add("ViewID");
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into ViewLink (" +
				"VIEWID," +
				"LinkID," +
				"CREATIONDATE," +
				"MODIFICATIONDATE," +
				"CURRENTSTATUS" +
				") values (" +
				"'" + rst.getString("ViewID") + "'," +
				"'" + rst.getString("LinkID") + "'," +
				rst.getString("CreationDate") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				rst.getString("CurrentStatus") +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("SQLException: " + sex.getMessage());
			System.out.println("SQLState: " + sex.getSQLState());
			System.out.println("VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in getNodeDetailInsertSQL - should not reach this point");
	}

	// key columns must be alphabetical
	public String getUpdateSQL (ResultSet rst, String link_id, String view_id, boolean derby_dialect)
	{
		try
		{
			return "update ViewLink set " +
				"CREATIONDATE=" + rst.getString("CreationDate") + "," +
				"MODIFICATIONDATE=" + (derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"CURRENTSTATUS=" + rst.getString("CurrentStatus") + " " +
				"where LinkId = '" + link_id + "' and ViewId = '" + view_id + "'";
		}
		catch (SQLException sex)
		{
			System.out.println("SQLException: " + sex.getMessage());
			System.out.println("SQLState: " + sex.getSQLState());
			System.out.println("VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in getNodeDetailInsertSQL - should not reach this point");
	}

}