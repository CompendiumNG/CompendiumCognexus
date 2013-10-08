import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.sync.*;

// Derby Query - send query to derby, return results to stdout.

public class dq
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


		Connection derby_conn = getDerbyConnection(derby_url);

		ResultSet derby_rst = null;

		String sql = args[1];

		ResultSet rst = issueSelectQuery(derby_conn,sql);

		try
		{
			ResultSetMetaData rsmd = rst.getMetaData();

			//iterate thru the columns, produce key=value rows to stdout

			while (rst.next())
			{
				for (int i=1; i<=rsmd.getColumnCount(); i++)
				{
					System.out.print(rsmd.getColumnName(i) + "=" + rst.getString(i) + "\n");
				}
			}
		}
		catch (SQLException sex)
		{
			System.out.println("60 dq Derby SQLException: " + sex.getMessage());
			System.out.println("61 dq Derby SQLState: " + sex.getSQLState());
			System.out.println("62 dq Derby VendorError: " + sex.getErrorCode());
		}
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
			System.out.println("Derby SQLException: " + sex.getMessage());
			System.out.println("Derby SQLState: " + sex.getSQLState());
			System.out.println("Derby VendorError: " + sex.getErrorCode());
		}

		if (conn == null)
		{
			System.out.println("Derby connection is null.");
			System.exit(1);
		}

		return conn;
	}

	private static ResultSet issueSelectQuery (Connection conn, String s)
	{
		ResultSet rs = null;
		try
		{
			Statement stmt = conn.createStatement();
			System.out.print("s");
			System.out.println("\n" + s);
			rs = stmt.executeQuery(s);
		}
		catch (SQLException sex)
		{
			System.out.println("120 dq SQLException: " + sex.getMessage());
			System.out.println("121 dq SQLState: " + sex.getSQLState());
			System.out.println("122 dq VendorError: " + sex.getErrorCode());
			System.out.println("123 dq sql is " + s);
			System.exit(1);
		}

		return rs;
	}

}
