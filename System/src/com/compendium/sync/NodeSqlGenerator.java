package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.*;

public class NodeSqlGenerator extends SqlGenerator
{
	public NodeSqlGenerator ()
	{
		table_name = "Node";
		key_column_names.add("NodeID");

		check_back_data = false;
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into Node (" +
				"NODEID," +
				"AUTHOR," +
				"CREATIONDATE," +
				"MODIFICATIONDATE," +
				"NODETYPE," +
				"ORIGINALID," +
				"EXTENDEDNODETYPE," +
				"LABEL," +
				"DETAIL," +
				"CURRENTSTATUS," +
				"LASTMODAUTHOR" +
				") values (" +
				"'" + rst.getString("NodeID") + "'," +
				"'" + rst.getString("Author") + "'," +
				rst.getString("CreationDate") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				rst.getString("NodeType") + "," +
				"'" + rst.getString("OriginalID") + "'," +
				"'" + rst.getString("ExtendedNodeType") + "'," +
				"'" + escapeData(rst.getString("Label"), derby_dialect) + "'," +
				"'" + escapeData(rst.getString("Detail"), derby_dialect) + "'," +
				rst.getString("CurrentStatus") + "," +
				"'" + rst.getString("LastModAuthor") + "'" +
				")";
		}
		catch (SQLException sex)
		{
			System.out.println("Node Insert SQLException: " + sex.getMessage());
			System.out.println("Node Insert SQLState: " + sex.getSQLState());
			System.out.println("Node Insert VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in getNodeInsertSQL - should not reach this point");
	}

	public String getUpdateSQL (ResultSet rst, String node_id, String not_used, boolean derby_dialect)
	{
		try
		{
			return "update Node set " +
				"AUTHOR='" + rst.getString("Author") + "'," +
				"CREATIONDATE=" + rst.getString("CreationDate") + "," +
				"MODIFICATIONDATE=" + (derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"NODETYPE=" + rst.getString("NodeType") + "," +
				"ORIGINALID='" + rst.getString("OriginalID") + "'," +
				"EXTENDEDNODETYPE='" + rst.getString("ExtendedNodeType") + "'," +
				"LABEL='" + escapeData(rst.getString("Label"), derby_dialect) + "'," +
				"DETAIL='" + escapeData(rst.getString("Detail"), derby_dialect) + "'," +
				"CURRENTSTATUS=" + rst.getString("CurrentStatus") + "," +
				"LASTMODAUTHOR='" + rst.getString("LastModAuthor") + "' " +
				getWhereStatement(node_id, null);
		}
		catch (SQLException sex)
		{
			System.out.println("Node Update SQLException: " + sex.getMessage());
			System.out.println("Node Update SQLState: " + sex.getSQLState());
			System.out.println("Node Update VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

		throw new RuntimeException("Exception in getNodeInsertSQL - should not reach this point");
	}

	public String getConflictExpanded (ResultSet rst)
	{
		return "Node Conflict detail: " + super.getConflictExpanded(rst);
	}

	protected void appendConflictReport (Synchronizer sync, ResultSet rst)
	{
		try
		{
			sync.appendConflictReport("\nNode Label:" + rst.getString("Label") + " (NodeID " + rst.getString("NodeID") + ")");
		}
		catch (SQLException sex)
		{
			System.out.println("Node Update SQLException: " + sex.getMessage());
			System.out.println("Node Update SQLState: " + sex.getSQLState());
			System.out.println("Node Update VendorError: " + sex.getErrorCode());
			System.out.flush();
		}

	}
}