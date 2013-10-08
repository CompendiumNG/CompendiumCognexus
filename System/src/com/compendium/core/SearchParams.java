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

package com.compendium.core;

import java.util.Date;
import java.util.Vector;

/**
 * The SearchParams class serves to keep search paramerers.
 */

public class SearchParams {

	/** Context of the search (just the given view, all views, deleted views). */
	private String contextCondition;

	/** The id of the curernt view. */
	private String viewId;

	/** A vector of the selected node type to run the search against. */
	private Vector selectedNodeTypes;

	/** A vector of author to run the search against. */
	private Vector selectedAuthors;

	/** A vector of codes to run the search against. */
	private Vector selectedCodes;

	private int matchCodesCondition;

	/** A vector of keywords to run the search against. */
	private Vector keywords;

	/** Match all or any keywords. */
	private int matchKeywordCondition;

	/** Match the keywords in the label, detail or both as specified. */
	private Vector attrib;

	/** The end creation date to run the search against. */
	private Date beforeCreationDate;

	/** The start creation date to run the search against. */
	private Date afterCreationDate;

	/** The end modification date to run the search against. */
	private Date beforeModificationDate;

	/** The start modification date to run the search against. */
	private Date afterModificationDate;

	/** Match whole words. */
	private boolean matchWholeWords;

	/**
	 * @return the contextCondition
	 */
	public String getContextCondition() {
		return contextCondition;
	}

	/**
	 * @param contextCondition the contextCondition to set
	 */
	public void setContextCondition(String contextCondition) {
		this.contextCondition = contextCondition;
	}

	/**
	 * @return the viewId
	 */
	public String getViewId() {
		return viewId;
	}

	/**
	 * @param viewId the viewId to set
	 */
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	/**
	 * @return the selectedNodeTypes
	 */
	public Vector getSelectedNodeTypes() {
		return selectedNodeTypes;
	}

	/**
	 * @param selectedNodeTypes the selectedNodeTypes to set
	 */
	public void setSelectedNodeTypes(Vector selectedNodeTypes) {
		this.selectedNodeTypes = selectedNodeTypes;
	}

	/**
	 * @return the selectedAuthors
	 */
	public Vector getSelectedAuthors() {
		return selectedAuthors;
	}

	/**
	 * @param selectedAuthors the selectedAuthors to set
	 */
	public void setSelectedAuthors(Vector selectedAuthors) {
		System.out.println("130 SearchParams selectedAuthors in is: " + selectedAuthors);
		this.selectedAuthors = selectedAuthors;
	}

	/**
	 * @return the selectedCodes
	 */
	public Vector getSelectedCodes() {
		return selectedCodes;
	}

	/**
	 * @param selectedCodes the selectedCodes to set
	 */
	public void setSelectedCodes(Vector selectedCodes) {
		this.selectedCodes = selectedCodes;
	}

	/**
	 * @return the matchCodesCondition
	 */
	public int getMatchCodesCondition() {
		return matchCodesCondition;
	}

	/**
	 * @param matchCodesCondition the matchCodesCondition to set
	 */
	public void setMatchCodesCondition(int matchCodesCondition) {
		this.matchCodesCondition = matchCodesCondition;
	}

	/**
	 * @return the keywords
	 */
	public Vector getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(Vector keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the matchKeywordCondition
	 */
	public int getMatchKeywordCondition() {
		return matchKeywordCondition;
	}

	/**
	 * @param matchKeywordCondition the matchKeywordCondition to set
	 */
	public void setMatchKeywordCondition(int matchKeywordCondition) {
		this.matchKeywordCondition = matchKeywordCondition;
	}

	/**
	 * @return the attrib
	 */
	public Vector getAttrib() {
		return attrib;
	}

	/**
	 * @param attrib the attrib to set
	 */
	public void setAttrib(Vector attrib) {
		this.attrib = attrib;
	}

	/**
	 * @return the beforeCreationDate
	 */
	public Date getBeforeCreationDate() {
		return beforeCreationDate;
	}

	/**
	 * @param beforeCreationDate the beforeCreationDate to set
	 */
	public void setBeforeCreationDate(Date beforeCreationDate) {
		this.beforeCreationDate = beforeCreationDate;
	}

	/**
	 * @return the afterCreationDate
	 */
	public Date getAfterCreationDate() {
		return afterCreationDate;
	}

	/**
	 * @param afterCreationDate the afterCreationDate to set
	 */
	public void setAfterCreationDate(Date afterCreationDate) {
		this.afterCreationDate = afterCreationDate;
	}

	/**
	 * @return the beforeModificationDate
	 */
	public Date getBeforeModificationDate() {
		return beforeModificationDate;
	}

	/**
	 * @param beforeModificationDate the beforeModificationDate to set
	 */
	public void setBeforeModificationDate(Date beforeModificationDate) {
		this.beforeModificationDate = beforeModificationDate;
	}

	/**
	 * @return the afterModificationDate
	 */
	public Date getAfterModificationDate() {
		return afterModificationDate;
	}

	/**
	 * @param afterModificationDate the afterModificationDate to set
	 */
	public void setAfterModificationDate(Date afterModificationDate) {
		this.afterModificationDate = afterModificationDate;
	}

	/**
	 * @return the matchWholeWords
	 */
	public boolean isMatchWholeWords() {
		return matchWholeWords;
	}

	/**
	 * @param matchWholeWords the matchWholeWords to set
	 */
	public void setMatchWholeWords(boolean matchWholeWords) {
		this.matchWholeWords = matchWholeWords;
	}




}
