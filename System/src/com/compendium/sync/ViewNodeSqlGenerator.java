package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.*;

public class ViewNodeSqlGenerator extends SqlGenerator
{
	public ViewNodeSqlGenerator ()
	{
		table_name = "ViewNode";
		key_column_names.add("NodeID");
		key_column_names.add("ViewID");
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into ViewNode (" +
				"VIEWID," +
				"NODEID," +
				"XPOS," +
				"YPOS," +
				"CREATIONDATE," +
				"MODIFICATIONDATE," +
				"CURRENTSTATUS," +
				"SHOWTAGS," +
				"SHOWTEXT," +
				"SHOWTRANS," +
				"SHOWWEIGHT," +
				"SMALLICON," +
				"HIDEICON," +
				"LABELWRAPWIDTH," +
				"FONTSIZE," +
				"FONTFACE," +
				"FONTSTYLE," +
				"FOREGROUND," +
				"BACKGROUND" +
				") values (" +
				"'" + rst.getString("ViewID") + "'," +
				"'" + rst.getString("NodeId") + "'," +
				rst.getString("XPos") + "," +
				rst.getString("YPos") + "," +
				rst.getString("CreationDate") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				rst.getString("CurrentStatus") + "," +
				"'" + rst.getString("ShowTags") + "'," +
				"'" + rst.getString("ShowText") + "'," +
				"'" + rst.getString("ShowTrans") + "'," +
				"'" + rst.getString("ShowWeight") + "'," +
				"'" + rst.getString("SmallIcon") + "'," +
				"'" + rst.getString("HideIcon") + "'," +
				rst.getString("LabelWrapWidth") + "," +
				rst.getString("FontSize") + "," +
				"'" + rst.getString("FontFace") + "'," +
				rst.getString("FontStyle") + "," +
				rst.getString("Foreground") + "," +
				rst.getString("Background") + "" +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("SQLException: " + sex.getMessage());
			System.out.println("SQLState: " + sex.getSQLState());
			System.out.println("VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in getNodeDetailInsertSQL - should not reach this point");
	}

	public String getUpdateSQL (ResultSet rst, String node_id, String view_id, boolean derby_dialect)
	{
		try
		{
			return "update ViewNode set " +
				"XPOS=" + rst.getString("XPos") + "," +
				"YPOS=" + rst.getString("YPos") + "," +
				"CREATIONDATE=" + rst.getString("CreationDate") + "," +
				"MODIFICATIONDATE=" + (derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"CURRENTSTATUS=" + rst.getString("CurrentStatus") + "," +
				"SHOWTAGS=" + "'" + rst.getString("ShowTags") + "'," +
				"SHOWTEXT=" + "'" + rst.getString("ShowText") + "'," +
				"SHOWTRANS=" + "'" + rst.getString("ShowTrans") + "'," +
				"SHOWWEIGHT=" + "'" + rst.getString("ShowWeight") + "'," +
				"SMALLICON=" + "'" + rst.getString("SmallIcon") + "'," +
				"HIDEICON=" + "'" + rst.getString("HideIcon") + "'," +
				"LABELWRAPWIDTH=" + rst.getString("LabelWrapWidth") + "," +
				"FONTSIZE=" + rst.getString("FontSize") + "," +
				"FONTFACE=" + "'" + rst.getString("FontFace") + "'," +
				"FONTSTYLE=" + rst.getString("FontStyle") + "," +
				"FOREGROUND=" + rst.getString("Foreground") + "," +
				"BACKGROUND=" + rst.getString("Background") + " " +
				getWhereStatement(node_id, view_id);
		}
		catch (SQLException sex)
		{
			System.out.println("SQLException: " + sex.getMessage());
			System.out.println("SQLState: " + sex.getSQLState());
			System.out.println("VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in getNodeDetailInsertSQL - should not reach this point");
	}

	public String getConflictExpanded (ResultSet rst)
	{
		StringBuffer str = new StringBuffer(super.getConflictExpanded(rst));

		try
		{
			str.append("\nCompendium URI is comp://" + rst.getString("ViewID") + "/" + rst.getString("NodeID") + "\n\n");
		}
		catch (SQLException sex)
		{
			System.out.println("SQLException: " + sex.getMessage());
			System.out.println("SQLState: " + sex.getSQLState());
			System.out.println("VendorError: " + sex.getErrorCode());
		}
		return str.toString();

	}
}