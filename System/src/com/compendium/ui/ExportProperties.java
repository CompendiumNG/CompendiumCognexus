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

package com.compendium.ui;

/**
 * This interface provides an API to the core application properties which are
 * stored in the ExportOptions.properties file.
 * <p>
 * See <code>PropertyManager</code> class which allows to retrive an object
 * which implements this interface.
 * <p>
 * Each property has two corresponded method: accessor with 'get' or 'is' prefix
 * and mutator with prefix 'set'. Accessor and motator are similar to classic
 * getter and setter for a class property.
 * <p>
 * Accessor method may have <code>@PropertyDefault</code> and
 * <code>@PropertyAlias</code> annotations. The first one allows to define
 * default value for the property if it is not in the property file.
 * <code>@PropertyAlias</code> should be used when some property
 * is renamed and its old name is added into the alias annotation.     
 */
public interface ExportProperties {
    @PropertyDefault(value="false")
    @PropertyAlias(name="addmaptitles")
    boolean isAddMapTitles();
    
    void setAddMapTitles(boolean addMapTitles);
    
    /** The accessor for the path to an anchor image.*/
    @PropertyDefault(value="System/resources/Images/anchor0.gif")
    @PropertyAlias(name="anchorimage")
    String getAnchorImage();
    
    /** The mutator for the path to an anchor image.*/
    void setAnchorImage(String anchorImage);
    
    @PropertyDefault(value="true")
    @PropertyAlias(name="contentstitle")
    boolean isAddContentsTitle();
    
    void setAddContentsTitle(boolean addContentsTitle);
    
    /** The accessor for the depth chosen to export views to.*/
    @PropertyDefault(value="2")
    @PropertyAlias(name="depth")
    int getDepth();
    
    /** The mutator for the depth chosen to export views to.*/
    void setDepth(int depth);
    
    /** The accessor for the condition of diaplying node detail page dates in the export.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="displaydetaildates")
    boolean isDisplayDetailDates();
    
    /** The mutator for the condition of diaplying node detail page dates in the export.*/
    void setDisplayDetailDates(boolean displayDetailDates);
    
    /** The accessor for the condition of exporting exported views to separate pages.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="displayindifferentpages")
    boolean isDisplayInDifferentPages();
    
    /** The motator for the condition of exporting exported views to separate pages.*/
    void setDisplayInDifferentPages(boolean displayInDifferentPages);
    
    @PropertyDefault(value="")
    String getFeedbackEmail();
    
    void setFeedbackEmail(String feedbackEmail);
    
    @PropertyDefault(value="false")
    boolean isAddFeedback();
    
    void setAddFeedback(boolean addFeedback);
    
    /** The accessor for the date for filtering node detail pages.*/
    @PropertyDefault(value="0")
    @PropertyAlias(name="fromdate")
    long getFromDate();
    
    /** The mutator for the date for filtering node detail pages.*/
    void setFromDate(long fromDate);
    
    /** The accessor for the condition of not including node detail dates.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="hidenodenodate")
    boolean isHideNodeNoDate();
    
    /** The mutator for the condition of not including node detail dates.*/
    void setHideNodeNoDate(boolean hideNodeNoDate);
    
    /** The accessor for the condition of including node detail anchors in the export.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="includedetailanchors")
    boolean isIncludeDetailAnchors();
    
    /** The mutator for the condition of including node detail anchors in the export.*/
    void setIncludeDetailAnchors(boolean includeDetailAnchors);
    
    /** The accessor for the condition of including link label information in the export.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="includelinks")
    boolean isIncludeLinks();
    
    /** The mutator for the condition of including link label information in the export.*/
    void setIncludeLinks(boolean includeLinks);
    
    /** The accessor for the condition of including a navigation bar in the export.*/
    @PropertyDefault(value="true")
    @PropertyAlias(name="includenavigationbar")
    boolean isIncludeNavigationBar();
    
    /** The mutator for the condition of including a navigation bar in the export.*/
    void setIncludeNavigationBar(boolean includeNavigationBar);
    
    /** The accessor for the condition of including node anchors in the export.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="includenodeanchors")
    boolean isIncludeNodeAnchors();
    
    /** The mutator for the condition of including node anchors in the export.*/
    void setIncludeNodeAnchors(boolean includeNodeAnchors);
    
    @PropertyDefault(value="false")
    @PropertyAlias(name="includerefs")
    boolean isIncludeRefs();
    
    void setIncludeRefs(boolean includeNodeAnchors);
    
    /** The accessor for the condition of including tags in the export.*/
    @PropertyDefault(value="true")
    @PropertyAlias(name="includetags")
    boolean isIncludeTags();
    
    /** The mutator for the condition of including tags in the export.*/
    void setIncludeTags(boolean includeTags);
    
    /** The accessor for the condition of including parent views in the export.*/
    @PropertyDefault(value="true")
    @PropertyAlias(name="includeviews")
    boolean isIncludeViews();
    
    /** The mutator for the condition of including parent views in the export.*/
    void setIncludeViews(boolean includeViews);
    
    /** The accessor for the condition of displaying the parent view information in the main text body.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="inlineview")
    boolean isInlineView();
    
    /** The mutator for the condition of displaying the parent view information in the main text body.*/
    void setInlineView(boolean inlineView);
    
    /** The accessor for the condition of exporting parent view data to separate pages.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="newview")
    boolean isNewView();
    
    /** The mutator for the condition of exporting parent view data to separate pages.*/
    void setNewView(boolean newView);
    
    /** The accessor for the condition of including node author information in the export.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="nodeauthor")
    boolean isAddNodeAuthor();
    
    /** The mutator for the condition of including node author information in the export.*/
    void setAddNodeAuthor(boolean addNodeAuthor);
    
    /** The accessor for the condition of filtering node detail pages on certain dates.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="nodedetaildate")
    boolean isAddNodeDetailDate();
    
    /** The mutator for the condition of filtering node detail pages on certain dates.*/
    void setAddNodeDetailDate(boolean addNodeDetailDate);
    
    /** The accessor for the condition of including node detail pages in the export.*/
    @PropertyDefault(value="true")
    @PropertyAlias(name="nodedetail")
    boolean isAddNodeDetail();
    
    /** The mutator for the condition of including node detail pages in the export.*/
    void setAddNodeDetail(boolean addNodeDetail);
    
    /** The accessor for the condition of including node images in the export.*/
    @PropertyDefault(value="true")
    @PropertyAlias(name="nodeimage")
    boolean isAddNodeImage();
    
    /** The mutator for the condition of including node images in the export.*/
    void setAddNodeImage(boolean addNodeImage);
    
    @PropertyDefault(value="false")
    @PropertyAlias(name="nodetailpopupatall")
    boolean isNoDetailPopupAtAll();
    
    void setNoDetailPopupAtAll(boolean noDetailPopupAtAll);
    
    @PropertyDefault(value="false")
    @PropertyAlias(name="nodetailpopup")
    boolean isNoDetailPopup();
    
    void setNoDetailPopup(boolean noDetailPopup);
    
    /** The accessor for the condition of opening the export file after completion (only if not zipped). */
    @PropertyDefault(value="false")
    @PropertyAlias(name="openafter")
    boolean isOpenAfter();
    
    /** The mutator for the condition of opening the export file after completion (only if not zipped). */
    void setOpenAfter(boolean openAfter);
    
    @PropertyDefault(value="true")
    @PropertyAlias(name="openinnew")
    boolean isOpenInNew();
    
    void setOpenInNew(boolean openInNew);
    
    /** The accessor for the condition of including the heading tags (it doesn't mean optimising for Word).*/
    @PropertyDefault(value="true")
    @PropertyAlias(name="optimizeforword")
    boolean isOptimizeForWord();
    
    /** The mutator for the condition of including the heading tags (it doesn't mean optimising for Word).*/
    void setOptimizeForWord(boolean optimizeForWord);
    
    /** The accessor for the condition of exporting views selected from the views dialog.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="otherviews")
    boolean isOtherViews();
    
    /** The mutator for the condition of exporting views selected from the views dialog.*/
    void setOtherViews(boolean otherViews);
    
    /** The accessor for the condition of the exporting selected views only.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="selectedviewsonly")
    boolean isSelectedViewsOnly();
    
    /** The mutator for the condition of the exporting selected views only.*/
    void setSelectedViewsOnly(boolean selectedViewsOnly);
    
    @PropertyDefault(value="false")
    @PropertyAlias(name="sortmenu")
    boolean isSortMenu();
    
    void setSortMenu(boolean sortMenu);
    
    /** The accessor for the date for filtering node detail pages.*/
    @PropertyDefault(value="0")
    @PropertyAlias(name="todate")
    long getToDate();
    
    /** The mutator for the date for filtering node detail pages.*/
    void setToDate(long toDate);
    
    /** The accessor for the condition to use purple numbers for the anchors.*/
    @PropertyDefault(value="false")
    @PropertyAlias(name="useanchornumbers")
    boolean isUseAnchorNumbers();
    
    /** The mutator for the condition to use purple numbers for the anchors.*/
    void setUseAnchorNumbers(boolean useAnchorNumbers);
    
    @PropertyDefault(value="false")
    @PropertyAlias(name="zip")
    boolean isZip();
    
    void setZip(boolean zip);
}
