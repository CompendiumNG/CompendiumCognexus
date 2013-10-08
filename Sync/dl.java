import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.sync.*;


// Derby loader - issue insert and update statements against a derby database

public class dl
{

	public static void main (String[] args)
	{
		System.out.println("Load Properties File...");

		String derby_url = null;

		Properties props = new Properties();
		try {
			props.load(new FileInputStream(args[0]));

			derby_url = props.getProperty("derby_url");
			if(derby_url==null)
			{
				System.out.println("Failed to load derby_url");
				System.exit(1);
			}

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("Properties file loaded");

		Connection derby_conn = getDerbyConnection(derby_url);

		ResultSet derby_rst = null;

		String infilename = args[1];

		StringBuffer sql_statements = new StringBuffer();

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(infilename));
			String str;

			while ((str = in.readLine()) != null)
			{
				sql_statements.append(str + " ");
			}
			in.close();
		}
		catch (IOException e)
		{
			System.out.println("Derby loader, IOE infilename - reading sql statements file <<<<<<<<<<<<<<<< error");
			System.out.println(e.getMessage());
			System.exit(1);
		}

		issueUpdateQuery(derby_conn, sql_statements.toString());

	}


	private static Connection getDerbyConnection (String derby_url)
	{
		Connection conn = null;

		try
		{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			//System.out.println("Derby Class.forName() call good");
		}
		catch (ClassNotFoundException cnfe)
		{
			System.out.println("Deby Class not found, " + cnfe.getMessage());
		}
		catch (Exception ex)
		{
			System.out.println("Derby Failed Class.forName() call");
			System.out.println("Derby Exception: " + ex.getMessage());
		}

		try
		{
			//log("Derby Trying: " + derby_url);
			conn = DriverManager.getConnection(derby_url);
			//log("Derby Connection --> OK");
		}
		catch (SQLException sex)
		{
			System.out.println("96 dl Derby SQLException: " + sex.getMessage());
			System.out.println("97 dl Derby SQLState: " + sex.getSQLState());
			System.out.println("98 dl Derby VendorError: " + sex.getErrorCode());
		}

		if (conn == null)
		{
			System.out.println("Derby connection is null.");
			System.exit(1);
		}

		return conn;
	}

	private static ResultSet issueUpdateQuery (Connection conn, String s)
	{
		ResultSet rs = null;
		String sql = null;

		try
		{
			StringTokenizer tok = new StringTokenizer(s.trim(), ";");


			while (tok.hasMoreElements())
			{
				sql = (String)tok.nextElement();

				Statement stmt = conn.createStatement();
				//System.out.println("u:" + sql);
				if (sql.trim() == "")
				{
					System.out.println("??: blank statement");
				}
				else
				{
					stmt.executeUpdate(sql);
				}
				System.out.println("Success");
			}
		}
		catch (SQLException sex)
		{
			System.out.println("139 dl SQLException: " + sex.getMessage());
			System.out.println("140 dl SQLState: " + sex.getSQLState());
			System.out.println("141 dl VendorError: " + sex.getErrorCode());
			System.out.println("142 dl sql is '" + sql + "'");
			System.exit(1);
		}

		return rs;
	}

}
