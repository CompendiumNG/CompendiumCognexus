/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.core.datamodel;

/**
 * The Node object represents a hyperlinkable node that
 * has as its minimum properties a type, label and detailed description.
 *
 * @author	rema and sajid / Michelle Bachler
 */
public class PCSession implements IPCSession, java.io.Serializable {

	/** The session unique identifier */
	protected String sSessionID = "";

	/** The name of the database project model for this session*/
	protected String sModelName = "";

	/** The user id for this session*/
	protected String sUserID = "";

	/**
	 * Constructor set the session id, model name and user id for this session.
	 *
	 * @param String sSessionID, the session id for this session.
	 * @param String sModelName, the model name for this session.
	 * @param String sUserID, the user id for this session.
	 */
	public PCSession(String sSessionID, String sModelName, String sUserID) {
		this.sSessionID = sSessionID;
		this.sModelName = sModelName;
		this.sUserID = sUserID;
	}

	/**
	 * Set the session id for this session.
	 *
	 * @param String sSessionID, the session id for this session.
	 */
	public void setSessionID(String sSessionID) {
		this.sSessionID = sSessionID;
	}

	/**
	 * Get the session id for this session.
	 *
	 * @return String, the session id for this session.
	 */
	public String getSessionID() {
		return sSessionID;
	}

	/**
	 * Set the model name for this session.
	 *
	 * @param String sModelName, the model name for this session.
	 */
	public void setModelName(String modelName) {
		sModelName = modelName;
	}

	/**
	 * Get the model name for this session.
	 *
	 * @return String, the model name for this session.
	 */
	public String getModelName() {
		return sModelName;
	}

	/**
	 * Set the user id for this session.
	 *
	 * @param String sUserID, the user id for this session.
	 */
	public void setUserID(String id) {
		sUserID = id;
	}

	/**
	 * Get the user id for this session.
	 *
	 * @return String, the user id for this session.
	 */
	public String getUserID() {
		return sUserID;
	}
}
