import java.sql.*;
import java.util.*;
import java.io.*;

import com.compendium.Synchronizer;
//import com.compendium.sync.*;

public class Sync2
{

	public static void main (String[] args)
	{
		long last_sync_time = -1;

		String compendium_user_login = "";

		String this_user_id;  //different from user_id in data

 		Synchronizer sync = new Synchronizer();

		//System.out.println("Date Info:");
		java.util.Date now = new java.util.Date();

		System.out.println("Load Properties File...");
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(args[0]));

			sync.setMySQLUrl(props.getProperty("mysql_url"));
			sync.setMySQLConnection();
			if(sync.getMySQLConnection()==null)
			{
				System.out.println("33 Sync2 Failed to load mysql_url as database Connection");
				System.exit(1);
			}

			sync.setDerbyUrl(props.getProperty("derby_url"));
			sync.setDerbyConnection();
			if(sync.getDerbyConnection()==null)
			{
				System.out.println("40 Sync2 Failed to load derby_url as database Connection");
				System.exit(1);
			}

			last_sync_time = Long.parseLong(props.getProperty("last_sync_time"));
			//TODO: what if last sync time not specified in properties file?  error message?s
			//if no last sync time supplied, check the command line
			if (args.length >= 2)
			{
				System.out.println("args 1 is " + args[1]);
				last_sync_time = Long.parseLong(args[1]);
				System.out.println("last_sync_time is " + last_sync_time);

				if (last_sync_time == -1)
				{
					System.out.println("55 Sync2 Failed to load last_sync_time - not in property file, command line incorrect");
					System.exit(1);
				}
			}

			sync.setLogDir(props.getProperty("sync_log_dir"));
			if (sync.getLogDir() == null)
			{
				//set default
				sync.setLogDir("C:/Program Files/Compendium/System/resources/Logs/");
			}

			compendium_user_login = props.getProperty("compendium_user_login");
			if ((compendium_user_login == null) || (compendium_user_login == ""))
			{
				System.out.println("Non critical failure: not compendium_user_login");
				//System.exit(1);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		long lnow = now.getTime();
		sync.log("Now C time is " + lnow);

		//user_id is required
		String sql = "select UserID from Users where Login = '" + compendium_user_login + "'";
		this_user_id = sync.getColumnValue(sync.issueSelectQuery(sync.getMySQLConnection(), sql), "UserID");
		System.out.println("UserID is " + this_user_id);

		sync.setVerbose(false);

		sync.generateStatements(last_sync_time, null, null);
		sync.send(null, null);

		sync.log("writing properties file");

		try
		{
			props.setProperty("last_sync_time", String.valueOf(lnow));

			//if a new "now" was passed in on the command line, use it instead
			// this is primarily for testing purposes, the definition of 'now' needs to be repeatable

			if (args.length >= 3)
			{
				long new_now = Long.parseLong(args[2]);
				props.setProperty("last_sync_time", String.valueOf(new_now));
			}

			props.store(new FileOutputStream(args[0]), "# This file create by compendium sync, do not modify by hand");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}


		sync.log("Program Exit.\n");

	}
}
