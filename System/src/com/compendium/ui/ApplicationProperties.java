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
 * stored in the Format.properties file.
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
public interface ApplicationProperties {

    /** The accessor for the label length at which the detail box should be automatically popped up.*/
    @PropertyDefault(value = "250")
    @PropertyAlias(name = "detailrolloverlength")
	int getDetailRolloverLength();

    /** The mutator for the label length at which the detail box should be automatically popped up.*/
    void setDetailRolloverLength(int detailRolloverLength);

    /** The accessor for the  default database to use when using the local Derby database.*/
    @PropertyAlias(name = "defaultdatabase")
    String getDefaultDatabase();

    /** The mutator for the default database to use when using the local Derby database.*/
    void setDefaultDatabase(String defaultDatabase);

    /** The accessor for current look and feel.*/
    @PropertyDefault(value = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
    @PropertyAlias(name = "LAF")
    String getCurrentLookAndFeel();

    /** The mutator for the current look and feel.*/
    void setCurrentLookAndFeel(String currentLookAndFeel);

    /** The accessor for the current Timed Refresh setting.*/
    @PropertyAlias(name = "TimedRefresh")
    String getCurrentTimedRefresh();

    /** The mutator for the current Timed Refresh setting.*/
    void setCurrentTimedRefresh(String currentTimedRefresh);

    /** The accessor for the skin set selected.*/
    @PropertyDefault(value = "default")
    String getSkin();

    /** The mutator for the skin set selected.*/
    void setSkin(String skin);

    /** The accessor for the setting whether audio should be on or not.*/
    @PropertyDefault(value = "false")
    boolean isAudioOn();

    /** The mutator for the setting whether audio should be on or not.*/
    void setAudioOn(boolean audioOn);

    /** The accessor for the setting whether dnd files should be copied to the Linked Files folder, and with ot without prompting (on/off/prompt)*/
    @PropertyDefault(value = "prompt")
    String getDndFiles();

    /** The mutator for the setting whether dnd files should be copied to the Linked Files folder, and with ot without prompting (on/off/prompt)*/
    void setDndFiles(String dndFiles);

    /** The accessor for the setting which indicates image enlargement on rollover. */
    @PropertyDefault(value = "false")
    boolean isImageRollover();

    /** The mutator for the setting which indicates image enlargement on  rollover.*/
    void setImageRollover(boolean imageRollover);

    /** The accessor for the setting whether images should be scaled on rollover to fit screen.*/
    @PropertyDefault(value = "false")
    boolean isScaleImageRollover();

    /** The mutator for the setting whether images should be scaled on rollover to fit screen.*/
    void setScaleImageRollover(boolean scaleImageRollover);

    /** The accessor for the setting process all text drops as plain text automatically.*/
    @PropertyDefault(value = "false")
    boolean isDndNoTextChoice();

    /** The mutator for the processing all text drops as plain text automatically.*/
    void setDndNoTextChoice(boolean dndNoTextChoice);

    /** The accessor for the setting whether auto search is on.*/
    @PropertyDefault(value = "false")
    boolean isAutoSearchLabel();

    /** The mutator for the setting whether auto search is on.*/
    void setAutoSearchLabel(boolean autoSearchLabel);

    /** The accessor for the setting whether the aerial view is on.*/
    @PropertyDefault(value = "false")
    boolean isAerialView();

    /** The mutator for the setting whether the aerial view is on.*/
    void setAerialView(boolean aerialView);

    /** The accessor for the default zoom level for maps.*/
    @PropertyDefault(value = "1.0")
    @PropertyAlias(name = "zoom")
    double getZoomLevel();

    /** The mutator for the default zoom level for maps.*/
    void setZoomLevel(double zoomLevel);

    /** The accessor for the setting whether the main menu should be at the top of the screen on the mac os.*/
    @PropertyDefault(value = "false")
    @PropertyAlias(name = "macmenubar")
    boolean isMacMenuBar();

    /** The mutator for the whether the main menu should be at the top of the screen on the mac os.*/
    void setMacMenuBar(boolean macMenuBar);

    /** The accessor for the setting whether the menu shortcut characters should be underlined on the mac os.*/
    @PropertyDefault(value = "true")
    @PropertyAlias(name = "macmenuunderline")
    boolean isMacMenuUnderline();

    /** The mutator for the setting whether the menu shortcut characters should be underlined on the mac os.*/
    void setMacMenuUnderline(boolean macMenuUnderline);

    /** The accessor for the last x position of the main application screen.*/
    @PropertyDefault(value = "0")
    int getLastScreenX();

    /** The mutator for the last x position of the main application screen.*/
    void setLastScreenX(int lastScreenX);

    /** The accessor for the last y position of the main application screen.*/
    @PropertyDefault(value = "0")
    int getLastScreenY();

    /** The mutator for the last y position of the main application screen.*/
    void setLastScreenY(int lastScreenY);

    /** The accessor for the last width of the main application screen.*/
    @PropertyDefault(value = "-1")
    int getLastScreenWidth();

    /** The mutator for the last width of the main application screen.*/
    void setLastScreenWidth(int lastScreenWidth);

    /** The accessor for the last height of the main application screen.*/
    @PropertyDefault(value = "-1")
    int getLastScreenHeight();

    /** The mutator for the last height of the main application screen.*/
    void setLastScreenHeight(int lastScreenHeight);

    /** The accessor for the database type to use */
    //@PropertyDefault(value = "0")  // Derby?
    @PropertyDefault(value = "1")  // MySQL?
    @PropertyAlias(name = "database")
    int getDatabaseType();

    /** The mutator for the database type to use */
    void setDatabaseType(int databaseType);

    /** The accessor for the MySQL database profile last used */
    @PropertyAlias(name = "databaseprofile")
    String getDatabaseProfile();

    /** The mutator for the database profile last used */
    void setDatabaseProfile(String databaseProfile);

    /** The accessor for the setting whether to display the full path of the current datasource of not, in the application title bar.*/
    @PropertyDefault(value = "false")
    boolean isDisplayFullPath();

    /** The mutator for the setting whether to display the full path of the current datasource of not, in the application title bar.*/
    void setDisplayFullPath(boolean displayFullPath);

    /** The accessor for the setting whether to display the status bar.*/
    @PropertyDefault(value = "true")
    boolean isDisplayStatusBar();

    /** The mutator for the setting whether to display the status bar.*/
    void setDisplayStatusBar(boolean displayStatusBar);

    /** The accessor for the setting whether to display the view history bar.*/
    @PropertyDefault(value = "false")
    boolean isDisplayViewHistoryBar();

    /** The mutator for the setting whether to display the view history bar.*/
    void setDisplayViewHistoryBar(boolean displayViewHistoryBar);

    /** The accessor for the setting whether to display the outline.*/
    @PropertyDefault(value = IUIConstants.DISPLAY_NONE)
    String getDisplayOutlineView();

    /** The mutator for the setting whether to display the outline.*/
    void setDisplayOutlineView(String displayOutlineView);

    /** The accessor for the setting whether to display the unread view. */
    @PropertyDefault(value = "false")
    boolean isDisplayUnreadView();

    /** The mutator for the setting whether to display the unread view. */
    void setDisplayUnreadView(boolean displayUnreadView);

    /** The accessor for the amount the cursor should be moved when using keyboard arrow keys.*/
    @PropertyDefault(value = "20")
    int getCursorMovementDistance();

    /** The mutator for the amount the cursor should be moved when using keyboard arrow keys.*/
    void setCursorMovementDistance(int cursorMovementDistance);

    /** The accessor for the vertical gap between nodes when doing a left-to-right arrange.*/
    @PropertyDefault(value = "20")
    int getArrangeLeftVerticalGap();

    /** The mutator for the vertical gap between nodes when doing a left-to-right arrange.*/
    void setArrangeLeftVerticalGap(int arrangeLeftVerticalGap);

    /** The accessor for the horizontal gap between nodes when doing a left-to-right arrange.*/
    @PropertyDefault(value = "30")
    int getArrangeLeftHorizontalGap();

    /** The mutator for the horizontal gap between nodes when doing a left-to-right arrange.*/
    void setArrangeLeftHorizontalGap(int arrangeLeftHorizontalGap);

    /** The accessor for the vertical gap between nodes when doing a top-down arrange.*/
    @PropertyDefault(value = "40")
    int getArrangeTopVerticalGap();

    /** The mutator for the vertical gap between nodes when doing a top-down arrange.*/
    void setArrangeTopVerticalGap(int arrangeTopVerticalGap);

    /** The accessor for the horizontal gap between nodes when doing a top-down arrange.*/
    @PropertyDefault(value = "20")
    int getArrangeTopHorizontalGap();

    /** The mutator for the horizontal gap between nodes when doing a top-down arrange.*/
    void setArrangeTopHorizontalGap(int arrangeTopHorizontalGap);

    /** The accessor for the setting which indicates whether the refresh timer was running.*/
    @PropertyDefault(value = "false")
    @PropertyAlias(name = "timerRunning")
    boolean isRefreshTimerRunning();

    /** The mutator for the setting which indicates whether the refresh timer was running.*/
    void setRefreshTimerRunning(boolean refreshTimerRunning);

    /** The accessor for the refresh time interval to run the timer at (in seconds).*/
    @PropertyDefault(value = "10")
    int getRefreshTime();

    /** The mutator for the refresh time interval to run the timer at (in seconds).*/
    void setRefreshTime(int refreshTime);

    /** The accessor for the setting which indicates whether to start the uDig Communucations manager and related services.*/
    @PropertyDefault(value = "false")
    @PropertyAlias(name = "udig")
    boolean isStartUDigCommunications();

    /** The mutator for the setting which indicates whether to start the uDig Communucations manager and related services.*/
    void setStartUDigCommunications(boolean startUDigCommunications);

    /** The accessor for the setting whether to display the tag view. */
    @PropertyDefault(value = "false")
    boolean isDisplayTagsView();

    /** The mutator for the setting whether to display the tag view. */
    void setDisplayTagsView(boolean displayTagsView);

    /** The accessor for the orientation to display the tags view.*/
    @PropertyDefault(value = "vertical")
    String getTagsViewOrientation();

    /** The mutator for the orientation to display the tags view.*/
    void setTagsViewOrientation(String tagsViewOrientation);

    /** The accessor for opening nodes with single click */
    @PropertyDefault(value = "false")
    boolean isSingleClick();

    /** The mutator for opening nodes with single click */
    void setSingleClick(boolean singleClick);

    /** The accessor for the setting whether import all subdirectories recursively or no. */
    @PropertyDefault(value = "false")
    boolean isDndAddDirRecursively();

    /** The mutator for importing all subdirectories recursively */
    void setDndAddDirRecursively(boolean dndAddDirRecursively);

    /** The accessor for using the kfmclient to open files or no. */
    @PropertyDefault(value = "false")
    @PropertyAlias(name = "kfmclient")
    boolean isUseKFMClient();

    /** The mutator for using the kfmclient to open files */
    void setUseKFMClient(boolean useKFMClient);

    /** The accessor for the setting whether you want to be emailed when an item goes in the inbox or no. */
    @PropertyDefault(value = "false")
    boolean isEmailInbox();

    /** The mutator for the setting whether you want to be emailed when an item goes in the inbox. */
    void setEmailInbox(boolean emailInbox);

    /** The accessor for the length limit of desktop mail to accommodate OS/mailer limitations */
    @PropertyDefault(value = "240")
    int getEmailLengthLimit();

    /** The mutator for the length limit of desktop mail to accommodate OS/mailer limitations */
    void setEmailLengthLimit(int emailLengthLimit);

    /** The accessor for the current outline format to use.*/
    @PropertyDefault(value = "Default")
    String getOutlineFormat();

    /** The mutator for the current outline format to use.*/
    void setOutlineFormat(String outlineFormat);

    /** The accessor for the user's last active codeGroup (tag group) selection */
    String getActiveCodeGroup();

    /** The mutator for the user's last active codeGroup (tag group) selection */
    void setActiveCodeGroup(String activeCodeGroup);
}
