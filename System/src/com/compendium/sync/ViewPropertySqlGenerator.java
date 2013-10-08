package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

public class ViewPropertySqlGenerator extends SqlGenerator
{
	public ViewPropertySqlGenerator ()
	{
		table_name = "ViewProperty";
		key_column_names.add("UserID");
		key_column_names.add("ViewID");
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into ViewProperty (" +
				"UserID," +
				"ViewID," +
				"HorizontalScroll," +
				"VerticalScroll," +
				"Width," +
				"Height," +
				"XPosition," +
				"YPosition," +
				"IsIcon," +
				"IsMaximum," +
				"ShowTags," +
				"ShowText," +
				"ShowTrans," +
				"ShowWeight," +
				"SmallIcons," +
				"HideIcons," +
				"LabelLength," +
				"LabelWidth," +
				"FontSize," +
				"FontFace," +
				"FontStyle," +
				"ModificationDate" +
				") values (" +
				"'" + rst.getString("UserID") + "'," +
				"'" + rst.getString("ViewID") + "'," +
				rst.getString("HorizontalScroll") + "," +
				rst.getString("VerticalScroll") + "," +
				rst.getString("Width") + "," +
				rst.getString("Height") +"," +
				rst.getString("XPosition") +"," +
				rst.getString("YPosition") +"," +
				"'" + rst.getString("IsIcon") +"'," +
				"'" + rst.getString("IsMaximum") +"'," +
				"'" + rst.getString("ShowTags") +"'," +
				"'" + rst.getString("ShowText") +"'," +
				"'" + rst.getString("ShowTrans") +"'," +
				"'" + rst.getString("ShowWeight") +"'," +
				"'" + rst.getString("SmallIcons") +"'," +
				"'" + rst.getString("HideIcons") +"'," +
				rst.getString("LabelLength") +"," +
				rst.getString("LabelWidth") +"," +
				rst.getString("FontSize") +"," +
				"'" + rst.getString("FontFace") +"'," +
				rst.getString("FontStyle") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("ViewProperty insert SQLException: " + sex.getMessage());
			System.out.println("ViewProperty insert SQLState: " + sex.getSQLState());
			System.out.println("ViewProperty insert VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in getNodeDetailInsertSQL - should not reach this point");
	}


	public String getUpdateSQL (ResultSet rst, String user_id, String view_id, boolean derby_dialect)
	{
		try
		{
			return "update ViewProperty set " +
				"HorizontalScroll=" + rst.getString("HorizontalScroll") + "," +
				"VerticalScroll=" + rst.getString("VerticalScroll") + "," +
				"Width=" + rst.getString("Width") + ", " +
				"Height=" + rst.getString("Height") + "," +
				"XPosition=" + rst.getString("XPosition") + "," +
				"YPosition=" + rst.getString("YPosition") + ", " +
				"IsIcon='" + rst.getString("IsIcon") + "', " +
				"IsMaximum='" + rst.getString("IsMaximum") + "', " +
				"ShowTags='" + rst.getString("ShowTags") + "', " +
				"ShowText='" + rst.getString("ShowText") + "', " +
				"ShowTrans='" + rst.getString("ShowTrans") + "', " +
				"ShowWeight='" + rst.getString("ShowWeight") + "', " +
				"SmallIcons='" + rst.getString("SmallIcons") + "', " +
				"HideIcons='" + rst.getString("HideIcons") + "', " +
				"LabelLength=" + rst.getString("LabelLength") + "," +
				"LabelWidth=" + rst.getString("LabelWidth") + "," +
				"FontSize=" + rst.getString("FontSize") + ", " +
				"FontFace='" + rst.getString("FontFace") + "'," +
				"FontStyle=" + rst.getString("FontStyle") + "," +
				"ModificationDate=" + (derby_dialect ? rst.getString("ModificationDate") : now) + " " +
				getWhereStatement(user_id, view_id);
		}
		catch (SQLException sex)
		{
			System.out.println("ViewProperty update SQLException: " + sex.getMessage());
			System.out.println("ViewProperty update SQLState: " + sex.getSQLState());
			System.out.println("ViewProperty update VendorError: " + sex.getErrorCode());
		}

		throw new RuntimeException("Exception in getNodeInsertSQL - should not reach this point");
	}

}