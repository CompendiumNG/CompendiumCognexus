package com.compendium.sync;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.*;

public class LinkSqlGenerator extends SqlGenerator
{
	public LinkSqlGenerator ()
	{
		table_name = "Link";
		key_column_names.add("LinkID");

		has_deletes = true;
	}

	public String getInsertSQL (ResultSet rst, boolean derby_dialect)
	{
		try
		{
			return "insert into Link (" +
				"LinkID," +
				"Author," +
				"CreationDate," +
				"ModificationDate," +
				"LinkType," +
				"OriginalID," +
				"FromNode," +
				"ToNode," +
				"ViewID," +
				"Label," +
				"Arrow," +
				"CurrentStatus" +
				") values (" +
				"'" + rst.getString("LinkID") + "'," +
				"'" + rst.getString("Author") + "'," +
				rst.getString("CreationDate") + "," +
				(derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"'" + rst.getString("LinkType") + "'," +
				"'" + rst.getString("OriginalID") + "'," +
				"'" + rst.getString("FromNode") + "'," +
				"'" + rst.getString("ToNode") + "'," +
				"'" + rst.getString("ViewID") + "'," +
				"'" + rst.getString("Label") + "'," +
				rst.getString("Arrow") + "," +
				rst.getString("CurrentStatus") +
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

	public String getUpdateSQL (ResultSet rst, String link_id, String not_used, boolean derby_dialect)
	{
		try
		{
			return "update Link set " +
				"Author=" + "'" + rst.getString("Author") + "'," +
				"CREATIONDATE=" + rst.getString("CreationDate") + "," +
				"MODIFICATIONDATE=" + (derby_dialect ? rst.getString("ModificationDate") : now) + "," +
				"LinkType=" + "'" + rst.getString("LinkType") + "'," +
				"OriginalId=" + "'" + rst.getString("OriginalId") + "'," +
				"FromNode=" + "'" + rst.getString("FromNode") + "'," +
				"ToNode=" + "'" + rst.getString("ToNode") + "'," +
				"ViewID=" + "'" + rst.getString("ViewID") + "'," +
				"Label=" + "'" + rst.getString("Label") + "'," +
				"Arrow=" + rst.getString("Arrow") + "," +
				"CurrentStatus=" + rst.getString("CurrentStatus") + " " +
				getWhereStatement(link_id, null);
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

	protected void getDeletes (Synchronizer sync, Connection src, long last_sync_time, Vector<String> tgt)
	{
		ResultSet rst = sync.issueSelectQuery(src, "select * from Audit where Category = 'Link:Delete'");

		try
		{
			while (rst.next())
			{
				tgt.add("delete from Link where LinkID = '" + rst.getString("ItemID") + "'");
			}
		}
		catch (SQLException sex)
		{
			System.out.println("Link deletes SQLException: " + sex.getMessage());
			System.out.println("Link deletes SQLState: " + sex.getSQLState());
			System.out.println("Link deletes VendorError: " + sex.getErrorCode());
			System.out.flush();
		}
	}
}