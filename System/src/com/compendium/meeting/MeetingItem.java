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

package com.compendium.meeting;

import java.util.*;

/**
 * MeetingItem is the superclass of all loaded Meeting data items.
 * It has a meeting id, item name and item original id (triplestore uri)
 * that are properties common to all meeting data items.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class MeetingItem {

	/** The id for the meeting this agenda item is associated with.*/
	protected String sMeetingID		= "";

	/** The agenda item name/label text.*/
	protected String sName			= "";

	/** The triplestore uri for this item.*/
	protected String sOriginalID 	= "";


	/**
	 * Constructor for a meeting item item object.
	 */
	public MeetingItem() {}

	/**
	 * Constructor for a meeting item item object.
	 *
	 * @param sMeetingID, the id of the meeting this event belongs to.
	 * @param sName, the name/label for the agenda item.
	 */
	public MeetingItem(String sMeetingID, String sName) {
		this.sMeetingID = sMeetingID;
		this.sName = sName;
	}

	/**
	 * Return the id of the meeting this is an agenda item for.
	 */
	public String getMeetingID() {
		return this.sMeetingID;
	}

	/**
	 * Set label for this agenda item.
	 *
	 * @param String sName, label for this agenda item.
	 */
	public void setName(String sName) {
		this.sName = sName;
	}

	/**
	 * Return the label for this agenda item.
	 */
	public String getName() {
		return this.sName;
	}

	/**
	 * Set the original id (triplestore uri) for this agenda item.
	 *
	 * @param String sOriginalID, the original id (triplestore uri) for this agenda item.
	 */
	public void setOriginalID(String sOriginalID) {
		this.sOriginalID = sOriginalID;
	}

	/**
	 * Return the original id (triplestore uri) for this agenda item.
	 */
	public String getOriginalID() {
		return this.sOriginalID;
	}
}
