package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class ViewLayerSqlGenerator extends SqlGenerator
{
	public ViewLayerSqlGenerator ()
	{
		table_name = "ViewLayer";
		key_column_names.add("UserID");
		key_column_names.add("ViewID");

		check_back_data = false;
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into ViewLayer (" +
				"UserID," +
				"ViewID," +
				"Scribble," +
				"Background," +
				"Grid," +
				"Shapes," +
				"ModificationDate" +
				") values (" +
				"'" + rst.getString("UserID") + "'," +
				"'" + rst.getString("ViewID") + "'," +
				"'" + escapeData(rst.getString("Scribble"),derby_dialect) + "'," +
				"'" + escapeData(rst.getString("Background"),derby_dialect) + "'," +
				"'" + escapeData(rst.getString("Grid"),derby_dialect) + "'," +
				"'" + escapeData(rst.getString("Shapes"),derby_dialect) + "'," +
				(derby_dialect ? rst.getString("ModificationDate") : now) +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("ViewLayer insert SQLException: " + sex.getMessage());
			System.out.println("ViewLayer insert SQLState: " + sex.getSQLState());
			System.out.println("ViewLayer insert VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in ViewLayer insert - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String user_id, String view_id, boolean derby_dialect)
	{
		try
		{
			return "update ViewLayer set " +
				"Scribble='" + escapeData(rst.getString("Scribble"),derby_dialect) + "'," +
				"Background='" + escapeData(rst.getString("Background"),derby_dialect) + "'," +
				"Grid='" + escapeData(rst.getString("Grid"),derby_dialect) + "'," +
				"Shapes='" + escapeData(rst.getString("Shapes"),derby_dialect) + "'," +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) + " " +
				getWhereStatement(user_id, view_id);
		}
		catch (SQLException sex)
		{
			System.out.println("ViewLayer update SQLException: " + sex.getMessage());
			System.out.println("ViewLayer update SQLState: " + sex.getSQLState());
			System.out.println("ViewLayer update VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in ViewLayer update - should not reach this point");
	}

}