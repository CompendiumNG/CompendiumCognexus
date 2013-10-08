/**
 *
 */
package com.compendium.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.compendium.core.db.management.DBConnection;

/**
 * @author jamesl
 *
 */
//TODO: Work on locking functions has been frozen. Finish or delete.
public final class DBNodeLock {

	/** SQL statement to create a new Lock Record into the Lock table.*/
	public final static String INSERT_NODE_LOCK_QUERY =
		"INSERT INTO NodeLock (OwnerID, NodeID) VALUES (?, ?) ";

	/** SQL statement to check whether a Lock record exists for a particular node */
	public final static String GET_NODE_LOCK_BY_NODE_QUERY =
		"SELECT LockID FROM NodeLock WHERE NodeID = ? ";

	public final static String DELETE_NODE_LOCK_QUERY =
		"DELETE FROM NodeLock WHERE LockID = ?";


	/**
	 * @param DBConnection dbcon
	 * @param String sOwnerID
	 * @param String sNodeID
	 * @throws SQLException
	 * @returns int nRowCount
	 */
	public static int insert(DBConnection dbcon, String sOwnerID, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		PreparedStatement pstmt = con.prepareStatement(INSERT_NODE_LOCK_QUERY);
		pstmt.setString(1, sOwnerID);
		pstmt.setString(2, sNodeID);

		int nRowCount = 0;

		try {

			nRowCount = pstmt.executeUpdate();

		} catch (Exception e){

			e.printStackTrace();

		}

		return nRowCount;

	}

	/**
	 *
	 * @param dbcon
	 * @param sNodeID
	 * @return
	 * @throws SQLException
	 */
	public static int check(DBConnection dbcon, String sNodeID) throws SQLException {

		int iLockID = 0;
		try
		{
			Connection con = dbcon.getConnection();
			PreparedStatement pstmt = con.prepareStatement(GET_NODE_LOCK_BY_NODE_QUERY);
			pstmt.setString(1, sNodeID);

			ResultSet oResults = null;

			oResults = pstmt.executeQuery();

			if (oResults.next()){

				iLockID = oResults.getInt("LockID");

			}

		}
		catch (SQLException sex)
		{
			System.out.println("91 DBNodeLock caught SQLException " + sex.getMessage());
		}
		catch (Exception e)
		{

			e.printStackTrace();

		}

		return iLockID;

	}

	/**
	 *
	 * @param dbcon
	 * @param sLockID
	 * @return
	 * @throws SQLException
	 */
	public static int delete(DBConnection dbcon, int sLockID) throws SQLException {

		Connection con = dbcon.getConnection();
		PreparedStatement pstmt = con.prepareStatement(DELETE_NODE_LOCK_QUERY);
		pstmt.setInt(1, sLockID);

		int nRowCount = 0;

		try {

			nRowCount = pstmt.executeUpdate();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return nRowCount;

	}

}
