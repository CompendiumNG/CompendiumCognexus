import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.datatransfer.*;

//import statements
//Check if window closes automatically. Otherwise add suitable code
public class SyncGui extends JFrame implements ActionListener, ClipboardOwner{

	java.io.File last_dir = new java.io.File(".");

	// props line
	JButton btn_props = new JButton("Properties File...");
	JTextField txt_props = new JTextField("", 20);
	JButton btn_load_props = new JButton("Load It");
	JButton btn_write_props = new JButton("Write It");
	String last_sync_time;

	// mysql line
	JButton btn_connect = new JButton("List");
	JTextField txt_mysql_server = new JTextField("", 10);
	JTextField txt_mysql_db = new JTextField("", 25);
	JTextField txt_mysql_user = new JTextField("", 10);
	JTextField txt_mysql_pass = new JTextField("", 5);
	JButton btn_mod_dates = new JButton("Add Mod Dates");
	JButton btn_orphans = new JButton("Cleanup Orphans");

	//derby line
	JTextField txt_derby_dir = new JTextField("", 35);
	JTextField txt_derby_db = new JTextField("", 20);
	JButton btn_set_derby_db_dir = new JButton("Set");
	JButton btn_set_derby_dir = new JButton("Set");
	JButton btn_send = new JButton("Send");

	// c user line
	JTextField txt_c_user = new JTextField("", 5);
	JTextField txt_sync_log_dir = new JTextField("", 30);
	JButton btn_set_set_snyc_log_dir = new JButton("Set");
	JTextField txt_last_sync_time = new JTextField("", 11);
	JButton btn_now = new JButton("Set NOW");

	JTextField txt_sync_jar = new JTextField("", 35);
	JButton btn_set_sync_jar = new JButton("Set");

	JTextField txt_mysql_jar = new JTextField("", 35);
	JButton btn_set_mysql_jar = new JButton("Set");

	JTextField txt_derby_jar = new JTextField("", 35);
	JButton btn_set_derby_jar = new JButton("Set");

	JButton btn_run_sync = new JButton("Run Sync");

	public static void main(String args[]) {
		new SyncGui();
	}
	SyncGui() {

		JPanel main_vert = new JPanel();
		main_vert.setLayout(new BoxLayout(main_vert, BoxLayout.PAGE_AXIS));

		//line 1 properties file
		JPanel props_panel = new JPanel();

		props_panel.setLayout(new BoxLayout(props_panel, BoxLayout.LINE_AXIS));

		props_panel.setPreferredSize(new Dimension(30, 15));

		JLabel jlb_props = new JLabel("Properties File:  	");
		props_panel.add(jlb_props);

		props_panel.add(txt_props);

		btn_props.addActionListener(this);
		props_panel.add(btn_props);

		btn_load_props.addActionListener(this);
		props_panel.add(btn_load_props);

		btn_write_props.addActionListener(this);
		props_panel.add(btn_write_props);

		main_vert.add(props_panel);

		//line 2 mysql database connection
		JPanel mysql_panel = new JPanel();
		JLabel jlb_mysql = new JLabel("MySQL: ");
		mysql_panel.add(jlb_mysql);

		JLabel jlb_mysql_server = new JLabel("Server: ");
		mysql_panel.add(jlb_mysql_server);
		mysql_panel.add(txt_mysql_server);

		JLabel jlb_mysql_db = new JLabel("DB: ");
		mysql_panel.add(jlb_mysql_db);
		mysql_panel.add(txt_mysql_db);

		btn_connect.addActionListener(this);
		mysql_panel.add(btn_connect);

		JLabel jlb_mysql_user = new JLabel("User: ");
		mysql_panel.add(jlb_mysql_user);
		mysql_panel.add(txt_mysql_user);

		JLabel jlb_mysql_pass = new JLabel("Pass: ");
		mysql_panel.add(jlb_mysql_pass);
		mysql_panel.add(txt_mysql_pass);

		btn_mod_dates.addActionListener(this);
		mysql_panel.add(btn_mod_dates);

		btn_orphans.addActionListener(this);
		mysql_panel.add(btn_orphans);

		main_vert.add(mysql_panel);

		//line 3 derby database connection
		JPanel derby_panel = new JPanel();
		JLabel jlb_derby = new JLabel("Derby: ");
		derby_panel.add(jlb_derby);

		JLabel jlb_derby_dir = new JLabel("Dir: ");
		derby_panel.add(jlb_derby_dir);

		derby_panel.add(txt_derby_dir);
		derby_panel.add(btn_set_derby_db_dir);
		btn_set_derby_db_dir.addActionListener(this);

		JLabel jlb_derby_db = new JLabel("DB: ");
		derby_panel.add(jlb_derby_db);

		derby_panel.add(txt_derby_db);

		derby_panel.add(btn_set_derby_dir);
		btn_set_derby_dir.addActionListener(this);

		derby_panel.add(btn_send);
		btn_send.addActionListener(this);

		main_vert.add(derby_panel);

		//line 4 compendium user name
		JPanel cuser_panel = new JPanel();
		JLabel jlb_cuser = new JLabel("Compendium User Name: ");
		cuser_panel.add(jlb_cuser);
		cuser_panel.add(txt_c_user);

		JLabel jlb_sync_log_dir = new JLabel("Sync Log Dir: ");
		cuser_panel.add(jlb_sync_log_dir);
		cuser_panel.add(txt_sync_log_dir);

		cuser_panel.add(btn_set_set_snyc_log_dir);
		btn_set_set_snyc_log_dir.addActionListener(this);

		JLabel jlb_last_sync_time = new JLabel("Last Sync Time: ");
		cuser_panel.add(jlb_last_sync_time);
		cuser_panel.add(txt_last_sync_time);

		cuser_panel.add(btn_now);
		btn_now.addActionListener(this);

		main_vert.add(cuser_panel);

		//separator
		JPanel sep_panel = new JPanel();
		JLabel jlb_sep = new JLabel("------- above, properties file ------------ below, run sync -------------");
		sep_panel.add(jlb_sep);

		main_vert.add(sep_panel);

		//sync class file
		JPanel sync_jar_panel = new JPanel();
		JLabel jlb_sync_jar = new JLabel("sync2.jar: ");
		sync_jar_panel.add(jlb_sync_jar);
		sync_jar_panel.add(txt_sync_jar);
		sync_jar_panel.add(btn_set_sync_jar);
		btn_set_sync_jar.addActionListener(this);

		main_vert.add(sync_jar_panel);

		//mysql jar
		JPanel mysql_jar_panel = new JPanel();
		JLabel jlb_mysql_jar = new JLabel("mysql-something.jar file: ");
		mysql_jar_panel.add(jlb_mysql_jar);
		mysql_jar_panel.add(txt_mysql_jar);
		mysql_jar_panel.add(btn_set_mysql_jar);
		btn_set_mysql_jar.addActionListener(this);

		main_vert.add(mysql_jar_panel);

		//derby jar
		JPanel derby_jar_panel = new JPanel();
		JLabel jlb_derby_jar = new JLabel("derby.jar file: ");
		derby_jar_panel.add(jlb_derby_jar);
		derby_jar_panel.add(txt_derby_jar);
		derby_jar_panel.add(btn_set_derby_jar);
		btn_set_derby_jar.addActionListener(this);

		main_vert.add(derby_jar_panel);

		//run sync
		JPanel run_sync_panel = new JPanel();
		run_sync_panel.add(btn_run_sync);
		btn_run_sync.addActionListener(this);

		main_vert.add(run_sync_panel);

		// put main_vert in
		add(main_vert);
		setSize(1150, 350);
		setVisible(true);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

   public void actionPerformed(ActionEvent event)
   {
	 Object source = event.getSource();
		if (source == btn_props)
		{
		  System.out.println("props file Button Clicked");

		  JFileChooser fc = new JFileChooser();

		  fc.setCurrentDirectory(last_dir);

		  int returnVal = fc.showOpenDialog(this);

		  if (returnVal == JFileChooser.APPROVE_OPTION)
		  {
			  File f = fc.getSelectedFile();
			  //This is where a real application would open the file.
			  System.out.println("Opening: " + f.getAbsolutePath() + ".");
			  String ppath = f.getAbsolutePath().replace('\\','/');
			  String fname = f.getName();
			  System.out.println("fname is " + fname);
/*
			  StringTokenizer tok = new StringTokenizer(ppath, "/");

			  StringBuffer strpath = new StringBuffer();

			  while (tok.hasMoreElements())
			  {
				  String pp = (String)tok.nextElement();

				  if (pp.equals(fname))
				  {
					  strpath.append(fname);
			      }
			      else
			      {
					  System.out.println("pp is " + pp);
					  pp = pp.replace(" ", "");
					  System.out.println("pp is " + pp);
					  if (pp.length() > 8)
					  {
						  pp = pp.substring(0,6) + "~1";
					  }
					  System.out.println("pp is " + pp);

				  	  strpath.append(pp + "/");
				  }
			  }

			  txt_props.setText(strpath.toString());
			  */
			  txt_props.setText(ppath);
			  last_dir = fc.getCurrentDirectory();
		  }
		  else
		  {
			  System.out.println("Open command cancelled by user.");
		  }

		}
		else if (source == btn_load_props)
		{
			System.out.println("Load properties file");
			Properties props = new Properties();
			try {
				props.load(new FileInputStream(txt_props.getText()));

				String mysql_url = props.getProperty("mysql_url");
				if(mysql_url==null)
				{
					System.out.println("Failed to load mysql_url");
					System.exit(1);
				}
				System.out.println("mysql_url is " + mysql_url);

				//load fields
				// format jdbc:mysql://s/d?user=u&password=p

				StringTokenizer tok = new StringTokenizer(mysql_url, "/");

				String tok1 = (String)tok.nextElement(); // jdbc:mysql preamble
				String s = (String)tok.nextElement();
				txt_mysql_server.setText(s);

				String dup = (String)tok.nextElement();

				tok = new StringTokenizer(dup, "?");
				String d = (String)tok.nextElement();
				txt_mysql_db.setText(d);
				String up = (String)tok.nextElement();

				tok = new StringTokenizer(up, "&");
				String u = (String)tok.nextElement();
				String p = (String)tok.nextElement();

				tok = new StringTokenizer(u, "=");
				tok.nextElement();
				u = (String)tok.nextElement();
				txt_mysql_user.setText(u);

				tok = new StringTokenizer(p, "=");
				tok.nextElement();
				p = (String)tok.nextElement();
				txt_mysql_pass.setText(p);

				String derby_url = props.getProperty("derby_url");
				if(derby_url==null)
				{
					System.out.println("Failed to load derby_url");
					System.exit(1);
				}
				System.out.println("derby_url is " + derby_url);

				//format jdbc:derby:C:/Program Files/Compendium/System/resources/Databases/derby_test_01_1296666094746;create=false
				tok = new StringTokenizer(derby_url, ";");
				derby_url = (String)tok.nextElement();

				tok = new StringTokenizer(derby_url, ":");
				tok.nextElement();
				tok.nextElement();

				String pd = tok.nextElement() + ":" + tok.nextElement();

				StringBuffer pd_reversed = (new StringBuffer(pd)).reverse();

				tok = new StringTokenizer(pd_reversed.toString(), "/");
				d = (String)tok.nextElement();
				txt_derby_db.setText((new StringBuffer(d)).reverse().toString());

				StringBuffer p_reversed = new StringBuffer();
				while (tok.hasMoreElements())
				{
					p_reversed.append("/"+(String)tok.nextElement());
				}

				p = p_reversed.reverse().toString();
				txt_derby_dir.setText(p);

				last_sync_time = props.getProperty("last_sync_time");
				System.out.println("last_sync_time is " + last_sync_time);
				txt_last_sync_time.setText(last_sync_time);

				String sync_log_dir = props.getProperty("sync_log_dir");
				if (sync_log_dir == null)
				{
					//set default
					txt_sync_log_dir.setText("C:/Program Files/Compendium/System/resources/Logs/");
				}
				else
				{
					txt_sync_log_dir.setText(sync_log_dir);
				}
				System.out.println("sync_log_dir is " + sync_log_dir);

				String compendium_user_login = props.getProperty("compendium_user_login");
				if ((compendium_user_login == null) || (compendium_user_login == ""))
				{
					System.out.println("Failed to load compendium_user_login");
					System.exit(1);
				}
				System.out.println("compendium_user_login is " + compendium_user_login);

				txt_c_user.setText(compendium_user_login);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}

		}
		else if (source == btn_connect)
		{
			Connection conn;

			System.out.println("393 user name is " + txt_mysql_user.getText() + " indexOf returns " + txt_mysql_user.getText().indexOf('&'));
			if (txt_mysql_user.getText().indexOf('&') > 0)
				conn = getMySQLConnection2();
			else
				conn = getMySQLConnection();

			StringBuilder databases = new StringBuilder();

			ResultSet rs = null;
			try
			{
				Statement stmt = conn.createStatement();
				rs = stmt.executeQuery("show databases;");

				while (rs.next())
				{
					if (rs.getString(1).equals("mysql") || rs.getString(1).equals("information_schema"))
					{
						//ignore
					}
					else if (rs.getString(1).equals("pcna") || rs.getString(1).equals("redmine"))
					{
						//ignore
					}
					else
					{
						databases.append(rs.getString(1) + "\n");
					}
				}
			}
			catch (SQLException sex)
			{
				System.out.println("424 SQLException: " + sex.getMessage());
				System.out.println("425 SQLState: " + sex.getSQLState());
				System.out.println("426 VendorError: " + sex.getErrorCode());
				System.exit(1);
			}

			StringSelection stringSelection = new StringSelection(databases.toString());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents( stringSelection, this );

			//log into mysql, get list of databases
			JOptionPane.showMessageDialog(this,
			    "MySQL database names have been copied to system clipboard.");

		}
		else if (source == btn_mod_dates)
		{
			Connection conn;

			System.out.println("393 user name is " + txt_mysql_user.getText() + " indexOf returns " + txt_mysql_user.getText().indexOf('&'));
			if (txt_mysql_user.getText().indexOf('&') > 0)
				conn = getMySQLConnection2();
			else
				conn = getMySQLConnection();

			Vector<String> sql_statements = new Vector<String>();

			sql_statements.add("alter table Audit add column ModificationDate double");
			sql_statements.add("alter table Clone add column ModificationDate double");
			sql_statements.add("alter table Connections add column ModificationDate double");
			sql_statements.add("alter table ExtendedTypeCode add column ModificationDate double");
			sql_statements.add("alter table GroupUser add column ModificationDate double");
			sql_statements.add("alter table Meeting add column ModificationDate double");
			sql_statements.add("alter table NodeCode add column ModificationDate double");
			sql_statements.add("alter table NodeUserState add column ModificationDate double");
			sql_statements.add("alter table Permission add column ModificationDate double");
			sql_statements.add("alter table Preference add column ModificationDate double");
			sql_statements.add("alter table Properties add column ModificationDate double");
			sql_statements.add("alter table ReferenceNode add column ModificationDate double");
			sql_statements.add("alter table ShortCutNode add column ModificationDate double");
			sql_statements.add("alter table System add column ModificationDate double");
			sql_statements.add("alter table ViewLayer add column ModificationDate double");
			sql_statements.add("alter table ViewProperty add column ModificationDate double");
			sql_statements.add("alter table WorkspaceView add column ModificationDate double");

			StringBuffer sql_returns = new StringBuffer();

			for (int i = 0; i < sql_statements.size(); i++)
			{
				sql_returns.append("DDL " + sql_statements.get(i) + " returns: " + issueUpdateQuery(conn, sql_statements.get(i)) + "\n");
			}

			StringSelection stringSelection = new StringSelection(sql_returns.toString());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents( stringSelection, this );

			//log into mysql, get list of databases
			JOptionPane.showMessageDialog(this, "System output has been copied to clipboard.");

		}
		else if (source == btn_orphans)
		{
			Connection conn;

			System.out.println("393 user name is " + txt_mysql_user.getText() + " indexOf returns " + txt_mysql_user.getText().indexOf('&'));
			if (txt_mysql_user.getText().indexOf('&') > 0)
				conn = getMySQLConnection2();
			else
				conn = getMySQLConnection();

			Vector<String> sql_statements = new Vector<String>();

			sql_statements.add("delete from ExtendedTypeCode where CODEID not in (select CODEID from Code );");
			sql_statements.add("delete from GroupCode where CODEID not in (select CODEID from Code );");
			sql_statements.add("delete from NodeCode where CODEID not in (select CODEID from Code );");
			sql_statements.add("delete from GroupCode where CODEGROUPID not in (select CODEGROUPID from CodeGroup );");
			sql_statements.add("delete from ExtendedTypeCode where EXTENDEDNODETYPEID not in (select EXTENDEDNODETYPEID from ExtendedNodeType );");
			sql_statements.add("delete from ViewLink where LINKID not in (select LINKID from Link );");
			sql_statements.add("delete from MediaIndex where MEETINGID not in (select MEETINGID from Meeting );");
			sql_statements.add("delete from Clone where CHILDNODEID not in (select NODEID from Node );");
			sql_statements.add("delete from Favorite where NODEID not in (select NODEID from Node );");
			sql_statements.add("delete from Favorite where VIEWID not in (select NODEID from Node );");
			sql_statements.add("delete from Link where FROMNODE not in (select NODEID from Node );");
			sql_statements.add("delete from Link where TONODE not in (select NODEID from Node );");
			sql_statements.add("delete from MediaIndex where VIEWID not in (select NODEID from Node );");
			sql_statements.add("delete from MediaIndex where NODEID not in (select NODEID from Node );");
			sql_statements.add("delete from Meeting where MEETINGMAPID not in (select NODEID from Node );");
			sql_statements.add("delete from NodeCode where NODEID not in (select NODEID from Node );");
			sql_statements.add("delete from NodeDetail where NODEID not in (select NODEID from Node );");
			sql_statements.add("delete from NodeUserState where NODEID not in (select NODEID from Node );");
			sql_statements.add("delete from ReferenceNode where NODEID not in (select NODEID from Node );");
			sql_statements.add("delete from ReferenceNode where NODEID not in (select NODEID from Node );");
			sql_statements.add("delete from ShortCutNode where NODEID not in (select NODEID from Node );");
			sql_statements.add("delete from ViewLayer where VIEWID not in (select NODEID from Node );");
			sql_statements.add("delete from ViewLink where VIEWID not in (select NODEID from Node );");
			sql_statements.add("delete from ViewNode where VIEWID not in (select NODEID from Node );");
			sql_statements.add("delete from ViewNode where NODEID not in (select NODEID from Node );");
			sql_statements.add("delete from ViewProperty where VIEWID not in (select NODEID from Node );");
			sql_statements.add("delete from WorkspaceView where VIEWID not in (select NODEID from Node );");
			sql_statements.add("delete from GroupUser where GROUPID not in (select GROUPID from UserGroup );");
			sql_statements.add("delete from Permission where GROUPID not in (select GROUPID from UserGroup );");
			sql_statements.add("delete from Connections where USERID not in (select USERID from Users );");
			sql_statements.add("delete from Favorite where USERID not in (select USERID from Users );");
			sql_statements.add("delete from GroupUser where USERID not in (select USERID from Users );");
			sql_statements.add("delete from NodeUserState where USERID not in (select USERID from Users );");
			sql_statements.add("delete from Preference where USERID not in (select USERID from Users );");
			sql_statements.add("delete from ViewLayer where USERID not in (select USERID from Users );");
			sql_statements.add("delete from ViewProperty where USERID not in (select USERID from Users );");
			sql_statements.add("delete from Workspace where USERID not in (select USERID from Users );");
			sql_statements.add("delete from WorkspaceView where WORKSPACEID not in (select WORKSPACEID from Workspace );");

			StringBuffer sql_returns = new StringBuffer();

			for (int i = 0; i < sql_statements.size(); i++)
			{
				sql_returns.append("DDL " + sql_statements.get(i) + " returns: " + issueUpdateQuery(conn, sql_statements.get(i)) + "\n");
			}

			StringSelection stringSelection = new StringSelection(sql_returns.toString());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents( stringSelection, this );

			//log into mysql, get list of databases
			JOptionPane.showMessageDialog(this, "System output has been copied to clipboard.");

		}
		else if (source == btn_set_derby_dir)
		{
			//set a specific derby database
			//set the directory where the derby databases are
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File(txt_derby_dir.getText()));

			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			//
			// disable the "All files" option.
			//
			chooser.setAcceptAllFileFilterUsed(false);

			int returnVal = chooser.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File props_file = chooser.getSelectedFile();

				txt_derby_db.setText(props_file.getName());
			  } else {
				  System.out.println("Open command cancelled by user.");
			  }

		}
		else if (source == btn_set_derby_db_dir)
		{
			//set the directory where the derby databases are
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(last_dir);

			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			// disable the "All files" option.
			fc.setAcceptAllFileFilterUsed(false);

			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File f = fc.getSelectedFile();

				txt_derby_dir.setText(f.getAbsolutePath().replace('\\','/'));
				last_dir = fc.getCurrentDirectory();
			  }
			  else
			  {
				  System.out.println("Open command cancelled by user.");
			  }

		}
		else if (source == btn_write_props)
		{
			try
			{
				Properties props = new Properties();

				props.setProperty("last_sync_time", last_sync_time);

				// format jdbc:mysql://s/d?user=u&password=p
				props.setProperty("mysql_url", "jdbc:mysql://" + txt_mysql_server.getText() + "/" + txt_mysql_db.getText() + "?user=" + txt_mysql_user.getText() + "&password=" + txt_mysql_pass.getText());

				//format jdbc:derby:C:/Program Files/Compendium/System/resources/Databases/derby_test_01_1296666094746;create=false
				props.setProperty("derby_url", "jdbc:derby:" + txt_derby_dir.getText() + "/" + txt_derby_db.getText() + ";create=false");

				props.setProperty("compendium_user_login", txt_c_user.getText());

				props.setProperty("sync_log_dir", txt_sync_log_dir.getText());

				props.store(new FileOutputStream(txt_props.getText()), "# This file create by compendium sync, do not modify by hand");
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}

		}
		else if (source == btn_set_set_snyc_log_dir)
		{
			//set the directory where the sync logs live
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File(txt_sync_log_dir.getText()));

			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			// disable the "All files" option.
			fc.setAcceptAllFileFilterUsed(false);

			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File props_file = fc.getSelectedFile();

				txt_sync_log_dir.setText(props_file.getAbsolutePath().replace('\\','/'));
				last_dir = fc.getCurrentDirectory();
			  }
			  else
			  {
				  System.out.println("Open command cancelled by user.");
			  }

		}
		else if (source == btn_send)
		{
			//send to derby
			Connection conn = null;

			try
			{
				Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
				//System.out.println("Derby Class.forName() call good");
			}
			catch (ClassNotFoundException cnfe)
			{
				JOptionPane.showMessageDialog(this,"Derby Class not found, " + cnfe.getMessage());
			}
			catch (Exception ex)
			{
				System.out.println("Derby Failed Class.forName() call");
				System.out.println("Derby Exception: " + ex.getMessage());
			}

			try
			{
				//log("Derby Trying: " + derby_url);
				conn = DriverManager.getConnection("jdbc:derby:" + txt_derby_dir.getText() + "/" + txt_derby_db.getText() + ";create=false");
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
				JOptionPane.showMessageDialog(this,"error: Derby connection is null.");
				return;
			}

			JOptionPane.showMessageDialog(this,"Derby Connection OK.");

			JFileChooser fc = new JFileChooser();

			int returnVal = fc.showOpenDialog(this);

			String infile;

			 if (returnVal == JFileChooser.APPROVE_OPTION)
			 {
				  File sql_file = fc.getSelectedFile();
				  infile = sql_file.getAbsolutePath();
			} else {
				  System.out.println("Open command cancelled by user.");
				  return;
			}

			System.out.println("infile is " + infile);

			StringBuffer sql_statements = new StringBuffer();

			try
			{
				BufferedReader in = new BufferedReader(new FileReader(infile));
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

			StringTokenizer tok = new StringTokenizer(sql_statements.toString().trim(), ";");

			StringBuffer sql_returns = new StringBuffer();
			while (tok.hasMoreElements())
			{
				String sql = (String)tok.nextElement();

				sql = sql.trim();

				if (sql.trim() == "")
				{
					System.out.println("??: blank statement");
				}
				else
				{
					if (sql.startsWith("update") || sql.startsWith("Update") || sql.startsWith("UPDATE") ||
					    sql.startsWith("insert") || sql.startsWith("Insert") || sql.startsWith("INSERT") ||
					    sql.startsWith("delete") || sql.startsWith("Delete") || sql.startsWith("DELETE") ||
					    sql.startsWith("alter")  || sql.startsWith("Alter")  || sql.startsWith("ALTER")   )
					{
						sql_returns.append("DDL ret=" + issueUpdateQuery(conn, sql));
					}
					else if (sql.startsWith("select") || sql.startsWith("Select") || sql.startsWith("SELECT") )
					{
						ResultSet rs = issueSelectQuery(conn, sql);
						try
						{
							ResultSetMetaData rsmd = rs.getMetaData();
							//iterate the column headers
							for (int i=0; i<rsmd.getColumnCount(); i++)
							{
								sql_returns.append(rsmd.getColumnName(i) + "\t");
							}

							sql_returns.append("\n");

							//iterate the rows
							while (rs.next())
							{
								for (int i=0; i<rsmd.getColumnCount(); i++)
								{
									sql_returns.append(rs.getString(i) + "\t");
								}
								sql_returns.append("\n");
							}
						}
						catch (SQLException sex)
						{
							System.out.println("SQLException: " + sex.getMessage());
							System.out.println("SQLState: " + sex.getSQLState());
							System.out.println("VendorError: " + sex.getErrorCode());
							return;
						}
					}
					else
					{
						System.out.println("non sql?----------> " + sql);
					}
				}
				System.out.println("Success");
			}

			StringSelection stringSelection = new StringSelection(sql_returns.toString());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents( stringSelection, this );

			try
			{
				conn.close();
			}
			catch (SQLException sex)
			{
				System.out.println("Derby SQLException: " + sex.getMessage());
				System.out.println("Derby SQLState: " + sex.getSQLState());
				System.out.println("Derby VendorError: " + sex.getErrorCode());
			}

			//log into mysql, get list of databases
			JOptionPane.showMessageDialog(this,
			    "Derby database ou;tput has been copied to system clipboard.");
		}
		else if (source == btn_now)
		{
			System.out.println("btn now pushed");

			java.util.Date now = new java.util.Date();
			long l_now = now.getTime();

			Double d_now = new Double(new Long((new java.util.Date()).getTime()).doubleValue());

			txt_last_sync_time.setText( d_now.toString() );
		}
		else if (source == btn_set_sync_jar)
		{
		  JFileChooser fc = new JFileChooser();
		  fc.setCurrentDirectory(last_dir);

		  int returnVal = fc.showOpenDialog(this);

		  if (returnVal == JFileChooser.APPROVE_OPTION)
		  {
			  File f = fc.getSelectedFile();
			  txt_sync_jar.setText(f.getAbsolutePath().replace('\\','/'));
				last_dir = fc.getCurrentDirectory();
		  }

		}
		else if (source == btn_set_mysql_jar)
		{
		  JFileChooser fc = new JFileChooser();
		  fc.setCurrentDirectory(last_dir);

		  int returnVal = fc.showOpenDialog(this);

		  if (returnVal == JFileChooser.APPROVE_OPTION)
		  {
			  File f = fc.getSelectedFile();
			  txt_mysql_jar.setText(f.getAbsolutePath().replace('\\','/'));
				last_dir = fc.getCurrentDirectory();
		  }

		}
		else if (source == btn_set_derby_jar)
		{
		  JFileChooser fc = new JFileChooser(last_dir);
		  fc.setCurrentDirectory(last_dir);

		  int returnVal = fc.showOpenDialog(this);

		  if (returnVal == JFileChooser.APPROVE_OPTION)
		  {
			  File f = fc.getSelectedFile();
			  txt_derby_jar.setText(f.getAbsolutePath().replace('\\','/'));
				last_dir = fc.getCurrentDirectory();
		  }
		}
		else if (source == btn_run_sync)
		{
		  Process theProcess = null;
		  BufferedReader inStream = null;

		  try
		  {
			  StringBuffer str = new StringBuffer();
			  str.append(txt_sync_jar.getText() + ";");
			  str.append(txt_mysql_jar.getText() + ";");
			  str.append(txt_derby_jar.getText() + ";");

			  System.out.println("command line is " + "java -cp \"" + str.toString() + "\" com.compendium.Sync2 " + txt_props.getText());

			  //theProcess = Runtime.getRuntime().exec("java");
			  theProcess = Runtime.getRuntime().exec("java -cp \"" + str.toString() + "\" com.compendium.Sync2 " + txt_props.getText());
		  }
		  catch(IOException e)
		  {
			 System.err.println("Error on exec() method");
			 e.printStackTrace();
		  }

		  System.out.println("process is running");

		  // read from the called program's standard output stream
		  try
		  {
			 inStream = new BufferedReader(new InputStreamReader( theProcess.getInputStream() ));

			 StringBuffer output = new StringBuffer();
			 String line = "";

			 do
			 {
				 line = inStream.readLine();

				 if (line != null)
				 {
					System.out.println(line);
				 	//output.append(line + "\n");
				 }
				 else
				 {
					 output.append("sync program finished.");
				 }
			 } while (line != null);

			 //System.out.println(output);
		  }
		  catch(IOException e)
		  {
			 System.err.println("Error on inStream.readLine()");
			 e.printStackTrace();
		  }

		}
		else
		{
		  System.out.println("Other Button Clicked");
		}
   }

	public void lostOwnership(Clipboard clipboard, Transferable contents)
	{

	}

	private static ResultSet issueSelectQuery (Connection conn, String sql)
	{
		ResultSet rs = null;
		try
		{
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		}
		catch (SQLException sex)
		{
			System.out.println("SQLException: " + sex.getMessage());
			System.out.println("SQLState: " + sex.getSQLState());
			System.out.println("VendorError: " + sex.getErrorCode());
			System.out.println("sql is " + sql);
		}

		return rs;
	}

	private static int issueUpdateQuery (Connection conn, String sql)
	{
		try
		{
			Statement stmt = conn.createStatement();
			int ret = stmt.executeUpdate(sql);
			return ret;
		}
		catch (SQLException sex)
		{
			System.out.println("SQLException: " + sex.getMessage());
			System.out.println("SQLState: " + sex.getSQLState());
			System.out.println("VendorError: " + sex.getErrorCode());
			System.out.println("sql is '" + sql + "'");
		}

		return -1;
	}

	private Connection getMySQLConnection ()
	{
		Connection conn = null;

		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println("967 MySQL Class.forName() call good");
		}
		catch (ClassNotFoundException cnfe)
		{
			System.out.println("MySQL Class not found, " + cnfe.getMessage());
		}
		catch (Exception ex)
		{
			System.out.println("MySQL Failed Class.forName() call");
			System.out.println("MySQL Exception: " + ex.getMessage());
		}

		try
		{
			//jdbc:mysql://s/d?user=u&password=p
			conn = DriverManager.getConnection("jdbc:mysql://" + txt_mysql_server.getText() + "/" + txt_mysql_db.getText() + "?user=" + txt_mysql_user.getText() + "&password=" + txt_mysql_pass.getText());
			System.out.println("984 MySQL Connection --> OK");
		}
		catch (SQLException sex)
		{
			System.out.println("MySQL 988 SQLException: " + sex.getMessage());
			System.out.println("MySQL 989 SQLState: " + sex.getSQLState());
			System.out.println("MySQL 990 VendorError: " + sex.getErrorCode());
		}

		if (conn == null)
		{
			System.out.println("MySQL connection is null.");
			System.exit(1);
		}

		return conn;

	}

	private Connection getMySQLConnection2 ()
	{
		Connection conn = null;

		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println("1010 MySQL Class.forName() call good");
		}
		catch (ClassNotFoundException cnfe)
		{
			System.out.println("MySQL Class not found, " + cnfe.getMessage());
		}
		catch (Exception ex)
		{
			System.out.println("MySQL Failed Class.forName() call");
			System.out.println("MySQL Exception: " + ex.getMessage());
		}

		try
		{
			System.out.println("jdbc:mysql://" + txt_mysql_server.getText() + "/" + txt_mysql_db.getText()+","+txt_mysql_user.getText()+","+txt_mysql_pass.getText());
			conn = DriverManager.getConnection("jdbc:mysql://" + txt_mysql_server.getText() + "/" + txt_mysql_db.getText(),txt_mysql_user.getText(),txt_mysql_pass.getText());
			System.out.println("1027 MySQL Connection --> OK");
		}
		catch (SQLException sex)
		{
			System.out.println("MySQL 1031 SQLException: " + sex.getMessage());
			System.out.println("MySQL 1032 SQLState: " + sex.getSQLState());
			System.out.println("MySQL 1033 VendorError: " + sex.getErrorCode());
		}

		if (conn == null)
		{
			System.out.println("MySQL connection is null.");
			System.exit(1);
		}

		return conn;

	}


}