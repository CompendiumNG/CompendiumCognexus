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

package com.compendium.io.html;

import static com.compendium.ProjectCompendium.*;

import java.util.*;
import java.util.zip.*;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.awt.geom.*;

import javax.imageio.*;
import javax.imageio.stream.*;

import java.awt.image.*;

import com.sun.image.codec.jpeg.*;

import javax.swing.*;

import java.text.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.CoreUtilities;

import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;

/**
 * This class handles the creation of HTML Views exports which include image maps for each map view exported.
 *
 * @author ? / Michelle Bachler
 */
public class HTMLViews implements IUIConstants {

	/** Holds the data format used for detail page dates.*/
	private static SimpleDateFormat sdf = new SimpleDateFormat("d MMM, yyyy");

	/** The path used for the current nodes detail file.*/
	private	String				detailPath1 		=	"";

	/** A List of all views being exported.*/
	private Vector          	vtSelectedViews;

	/** A List of the View items to put on the menu panel*/
	private Vector 				vtMenuItems = new Vector(10);

	/** The <code>JOptionPane</code> object associated with the progress bar panel.*/
	private JOptionPane			oOptionPane 		= null;

	/** The dialog which holds the progress bar.*/
	private UIProgressDialog	oProgressDialog 	= null;

	/** The progress bar showing the progress of this export.*/
	private JProgressBar		oProgressBar 		= null;

	/** The thread class which starts the progress bar dialog.*/
	private ProgressThread		oThread 			= null;

	/** Used by the progress bar.*/
	private int					nCount 				= 0;

	/** The width for the popup node detail pabe to open at.*/
	private	int 				detailBoxWidth 		= 550;

	/** Has the user tried to cancel this export.*/
	private boolean 			exportCancelled 	= false;

	/** Is this export including external reference files ?*/
	private boolean				bIncludeReferences	= true;

	/** Is this export being written to a zip file ?*/
	private boolean				bZipUp				= true;

	/** Should the side menu map titles be sorted alphabetically.*/
	private boolean				bSortMenu			= false;

	/** Indicates of external files should be opened in a new window or not.*/
	private boolean				bOpenNew			= true;

	/** Whether to exclude detail popups when there is only the anchor if in them.*/
	private boolean				bNoDetailPopup	= false;

	/** Whether to exclude detail popups all together.*/
	private boolean				bNoDetailPopupAtAll	= false;

	/** The directory being exported to.*/
	private String 				sDirectory 			= "";

	/** The title for the main HTML file being created.*/
	private String 				sUserTitle 			= "";

	/** The name of the main file being exported to, (html or zip).*/
	private String 				sMainFileName 		= "";

	/** The stub of the backup file name without extension, used when exporting to zip file*/
	private String				sBackupName			= "";

	/** Holds the menu page HTML data.*/
	private StringBuffer 		sMenuPage 			= null;

	/** Used when calculating image map areas of a node. It holds the current node's transclusions indicator area.*/
	private	Rectangle			transRectangle 		= null;

	/** Used when calculating image map areas of a node. It holds the current node's text indicator area.*/
	private Rectangle			textRectangle 		= null;

	/** Used when calculating image map areas of a node. It holds the current node's tag indicator area.*/
	private	Rectangle			codeRectangle 		= null;

	/** Used when calculating image map areas of a node. It holds the current node's weight indicator area.*/
	private	Rectangle			weightRectangle 	= null;

	/** Used when calculating image map areas of a node. It holds the current node's icon area.*/
	private	Rectangle			iconRectangle 		= null;

	/** Used when calculating image map areas of a node. It holds the current node's label area.*/
	private	Rectangle			labelRectangle 		= null;


	/** Used when calculating image map areas of a node, does the current node have a transclusions indicator?.*/
	private boolean				hasTrans 			= false;

	/** Used when calculating image map areas of a node, does the current node have a text indicator?.*/
	private	boolean				hasText 			= false;

	/** Used when calculating image map areas of a node, does the current node have a Tags indicator?.*/
	private	boolean				hasCodes 			= false;

	/** Used when calculating image map areas of a node, does the current node have a weight indicator?.*/
	private	boolean				hasWeight 			= false;

	/** Used when calculating image map areas of a node, does the current node have a visible Icon?.*/
	private	boolean				hasIcon 			= false;

	/** Holds all the currentl open view frames, when opening and closing views to create jpgs for image maps.*/
	private Vector 				allFrames 			= null;

	/** Holds a list of all the home views in the database.*/
	private Hashtable 			oHomeViews 			= null;

	/** A list of the reference and images files to export, when exporting to a zip file.*/
	private Hashtable			htExportFiles 		= new Hashtable();

	/** A list of the HTML file path and data created during this export, when exporting to a zip file*/
	private Hashtable			htCreatedFiles		= new Hashtable();

	/** A list of the image map files created for this export.*/
	private Hashtable			htMapFiles			= new Hashtable();

	/** A counter used by the HTML menu page creation code.*/
	private int 				counter 			= 2;

	/** The background color for the rollover image map hint boxes.*/
	private String				hintcolor			= "#FFFED9";

	/** The current model, used to access the database as required.*/
	private IModel 				model 				= null;

	/** The current session id, used when accessing the database.*/
	private PCSession 			session 			= null;

	/** A ViewService instanced used to access the view information in the database.*/
	private IViewService 		vs 					= null;

	/** The author name of the current user */
	private String 				sAuthor 				= "";

	/** Holds messages about missing reference files.*/
	private Vector 				vtMessages				= new Vector();


	/**
	 * Constructor.
	 *
	 * @param directory java.lang.String, the directory to export to.
	 * @param fileName java.lang.String, the name of the file to export to.
	 * @param userTitle java.lang.String, the title for the main export file.
	 * @param bIncludeReferences boolean, export any associated reference and image files.
	 * @param bToZip boolean, export all files to a zip file.
	 * @param openInNew indicates whether external urls file or images should be opened in a new window ot not.
	 * @param bNoDetailPopup whether to exclude the detail popups for nodes with only anchor id info in them.
	 * @param bNoDetailPopupAtAll whether to exclude the detail popups totally from this export.
	 */
  	public HTMLViews(String directory, String fileName, String userTitle, boolean bIncludeReferences,
  			boolean bToZip, boolean bSortMenu, boolean openInNew, boolean bNoDetailPopup,
  			boolean bNoDetailPopupAtAll) {

		this.sDirectory = directory;
		this.sMainFileName = fileName;
		this.sUserTitle = userTitle;
		this.bIncludeReferences = bIncludeReferences;
		this.bZipUp = bToZip;
		this.bSortMenu = bSortMenu;
		this.bOpenNew = openInNew;
		this.bNoDetailPopup = bNoDetailPopup;
		this.bNoDetailPopupAtAll = bNoDetailPopupAtAll;

		String name = new File(fileName).getName();
		int ind = name.lastIndexOf(".");
		if (ind != -1) {
			sBackupName = name.substring(0, ind);
		}

		allFrames = ProjectCompendium.APP.getAllFrames();

		sMenuPage = new StringBuffer(2000);

		model = ProjectCompendium.APP.getModel();
		session = ProjectCompendium.APP.getModel().getSession();
		vs = model.getViewService();

		try {
			oHomeViews = model.getUserService().getHomeViews(session);
		}
		catch(Exception ex) {
			System.out.println("Error: (HTMLView - getHomeViews) "+ex.getMessage());
		}

 		oProgressBar = new JProgressBar();
  		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		oThread = new ProgressThread();
		oThread.start();
	}

	/**
	 * This small class starts up the progress bar for the export.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread() {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,"Export Progress..", "Export completed");
	  		oProgressDialog.showDialog(oProgressBar);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
		}
	}

	/**
	 * Checks if the user has requested to cancel the export.
	 * @return boolean, true if the progress has been cancelled, else false.
	 */
	private boolean checkProgress() {

	  	if (!exportCancelled && oProgressDialog.isCancelled()) {

			int result = JOptionPane.showConfirmDialog(oProgressDialog,
							"Do you want to Cancel the export?",
							"Cancel Export",
							JOptionPane.YES_NO_OPTION);

			if (result == JOptionPane.YES_OPTION) {
				exportCancelled = true;
				oProgressDialog.setVisible(false);
				return true;
			}
	  		else {
				oProgressDialog.setCancelled(false);
			  return false;
	  		}
		}
		return false;
	}

	/**
	 * Create the filename for the given view.
	 * @param view the view to create the filename for.
	 * @return
	 */
	private String createFileName(View view) {
		String sLabel = view.getLabel();
		if (sLabel.length()> 20) {
			sLabel = sLabel.substring(0, 20);
		}
		String sViewFileName = CoreUtilities.cleanFileName(sLabel)+"_"+view.getId()+".html";
		return sViewFileName;
	}

	/**
	 * This method sets up the progress bar and then loops through the given list of views
	 * creating the relevant HTML pages and addint each view the the side menu.
	 * Finally it write out the main file, or exports to zip fi required.
	 *
	 * @param selectedViews the list of views to export.
	 * @param bOpenAfter Open the html page after completion.
	 */
	public void processViews(Vector selectedViews, boolean bOpenAfter)
	{
		showTrace("Entered HTMLViews processViews");

		if (selectedViews == null || selectedViews.size() == 0)
			return;

		vtSelectedViews = new Vector();
		if (selectedViews.size() > 0) {
			for(Enumeration e = selectedViews.elements(); e.hasMoreElements();){
				View tview = (View)e.nextElement();
				vtSelectedViews.add( tview );
			}
		}

		int totalCount = selectedViews.size();

		if (bZipUp)
			oProgressBar.setMaximum(totalCount+3);
		else
			oProgressBar.setMaximum(totalCount);

		String sFileName = "";
		for(int i = 0; i < selectedViews.size(); i ++){

			if (checkProgress()) {
				ProjectCompendium.APP.displayMessage("Some files may already have been created", "Cancelling Export");
				return;
			}

			View view = (View)selectedViews.elementAt(i);
			view.initialize(session, model);
			sFileName = createFileName(view);
			createHTML(view, sFileName);
			vtMenuItems.addElement((View) view);

			nCount += 1;
			oProgressBar.setValue(nCount);
			oProgressDialog.setStatus(nCount);
		}

		writeMenuPage();

		if (bZipUp) {
			oProgressDialog.setMessage("About to create Zip file");
			zipUpExport(sDirectory+ProjectCompendium.sFS+sMainFileName);
		}

		oProgressDialog.setVisible(false);
		oProgressDialog.dispose();

		// Display Messages

		if (vtMessages.size() > 0) {
			UITextArea txtLabel = new UITextArea(800, 800);
			int count = vtMessages.size();
			for (int i=0; i<count; i++) {
				txtLabel.append((String)vtMessages.elementAt(i));
				txtLabel.append("\n\n");
			}
			//txtLabel.setAutoscrolls(true);
			txtLabel.setEditable(false);
			JScrollPane scrollpane = new JScrollPane(txtLabel);
			scrollpane.setPreferredSize(new Dimension(600,300));
			JOptionPane.showMessageDialog(ProjectCompendium.APP,
					scrollpane,
                    "Export Problems Encountered",
                    JOptionPane.WARNING_MESSAGE);
		}

  		if ( sMainFileName != null && !(sMainFileName.equals("")) ) {
			if (!bOpenAfter) {
	    		ProjectCompendium.APP.displayMessage("Finished exporting into " + sDirectory + ProjectCompendium.sFS+ sMainFileName, "Export Finished");
			}
  		}

		ProjectCompendium.APP.setDefaultCursor();
		ProjectCompendium.APP.setStatus("");

		showTrace("Exiting HTMLViews processViews");

	}

	/**
	 * This method sets up the progress bar and then loops through the given list of views
	 * creating the relevant HTML pages and adding each view the the side menu.
	 * Finally it write out the main file, or exports to zip if required and includes the XML zip given.
	 *
	 * @param selectedViews the list of views to export.
	 * @param sXMLZipFile the file name of the xml zip to include.
	 */
	public void processViewsWithXML(Vector selectedViews, String sXMLZipFile) {

		if (selectedViews == null || selectedViews.size() == 0)
			return;

		vtSelectedViews = new Vector();
		if (selectedViews.size() > 0) {
			for(Enumeration e = selectedViews.elements(); e.hasMoreElements();){
				View tview = (View)e.nextElement();
				vtSelectedViews.add( tview );
			}
		}

		int totalCount = selectedViews.size();

		oProgressBar.setMaximum(totalCount+3);

		String sFileName = "";
		for(int i = 0; i < selectedViews.size(); i ++){

			if (checkProgress()) {
				ProjectCompendium.APP.displayMessage("Some files may already have been created", "Cancelling Export");
				return;
			}

			View view = (View)selectedViews.elementAt(i);
			view.initialize(session, model);
			sFileName = createFileName(view);
			createHTML(view, sFileName);
			vtMenuItems.addElement((View) view);

			nCount += 1;
			oProgressBar.setValue(nCount);
			oProgressDialog.setStatus(nCount);
		}

		writeMenuPage();

		oProgressDialog.setMessage("About to create Zip file");

		// ADD THE XML ZIP TO RESOURCES LOADED INTO ZIP
		htExportFiles.put(sXMLZipFile, (new File(sXMLZipFile)).getName());
		zipUpExport(sDirectory+ProjectCompendium.sFS+sMainFileName);
		CoreUtilities.deleteFile(new File(sXMLZipFile));

		oProgressDialog.setVisible(false);
		oProgressDialog.dispose();

		// Display Messages
		if (vtMessages.size() > 0) {
			UITextArea txtLabel = new UITextArea(800, 800);
			int count = vtMessages.size();
			for (int i=0; i<count; i++) {
				txtLabel.append((String)vtMessages.elementAt(i));
				txtLabel.append("\n\n");
			}
			//txtLabel.setAutoscrolls(true);
			txtLabel.setEditable(false);
			JScrollPane scrollpane = new JScrollPane(txtLabel);
			scrollpane.setPreferredSize(new Dimension(600,300));
			JOptionPane.showMessageDialog(ProjectCompendium.APP,
					scrollpane,
                    "Export Problems Encountered",
                    JOptionPane.WARNING_MESSAGE);
		}

		ProjectCompendium.APP.setDefaultCursor();
		ProjectCompendium.APP.setStatus("");
	}

	/**
	 * Calls the appropriate method to process the given view as a list of a map, depending on its node type.
	 *
	 * @param view the view to process.
	 * @param the filename for the main map file for this view.
	 * @see #processMap
	 * @see #processList
	 */
	public void createHTML(View view, String sFileName){

		if (view != null) {
			int viewtype = view.getType();
			if (viewtype == ICoreConstants.MAPVIEW)
				processMap(view, sFileName);
			else {
				processList(view, sFileName);
			}
  		}
	}

	/**
	 * Process the export of a map view and create the HTML text require for the page.
	 * This includes obtaining a jpg image of the map, and createing the Image Map HTML required.
	 *
	 * @param view the map view to process.
	 * @param the filename for the main map file for this view.
	 */
	@SuppressWarnings("deprecation")
	private void processMap(View view, String sFileName){

		StringBuffer htmlString = new StringBuffer(2000);
		StringBuffer divString = new StringBuffer(2000);

		if (!bZipUp) {
			File directory = new File(sDirectory + ProjectCompendium.sFS + "images" );
   		 	if (!directory.isDirectory()) {
				directory.mkdirs();
			}
		}

		String mapFileName = sDirectory + ProjectCompendium.sFS + "images"+ ProjectCompendium.sFS + view.getId()+"_map.jpg";
		String mapHTMLName = "images/" + view.getId()+"_map.jpg";

		// CREATE THE JPEG IMAGE
		String viewID = view.getId();

		boolean wasOpen = false;
		int count = allFrames.size();
		for (int i=0; i<count; i++) {
			UIViewFrame frame = (UIViewFrame)allFrames.elementAt(i);
			if (viewID.equals(frame.getView().getId())) {
				wasOpen = true;
				break;
			}
		}

		UIViewFrame frame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
		UIViewPane pane = ((UIMapViewFrame)frame).getViewPane();
		Dimension size = pane.calculateSize();

		//UIMapViewFrame mapFrame = new UIMapViewFrame(view, view.getLabel());
		//UIViewPane pane = mapFrame.getViewPane();
		//Dimension size = pane.calculateSize();

		//UIMapViewFrame mapFrame = new UIMapViewFrame(view, view.getLabel());
		//UIViewPane pane = mapFrame.getViewPane();
		//UIViewPane pane = new UIViewPane(view, null);
		//JFrame f = new JFrame("Show remain invisible");
        //f.setContentPane(pane);
        //f.pack();
		//Dimension size = pane.calculateSize();

		try {
			if (size.width > 0 && size.height > 0) {
				GraphicsConfiguration fig = pane.getGraphicsConfiguration();
				if (fig != null) {
					BufferedImage img = fig.createCompatibleImage(size.width, size.height, Transparency.OPAQUE);
					Graphics2D graphics = img.createGraphics();
					pane.paint(graphics);

					if (bZipUp) {
						mapFileName = sDirectory + ProjectCompendium.sFS + view.getId()+"_map.jpg";
						htMapFiles.put(mapFileName, "images/"+view.getId()+"_map.jpg");
					}

					if (ProjectCompendium.isLinux) {
						Iterator iter = ImageIO.getImageWritersByFormatName("JPG");
						if (iter.hasNext()) {
							ImageWriter writer = (ImageWriter)iter.next();
							ImageWriteParam iwp = writer.getDefaultWriteParam();
							iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
							iwp.setCompressionQuality(1);
							File outFile = new File(mapFileName);
							FileImageOutputStream output = new FileImageOutputStream(outFile);
							writer.setOutput(output);
							IIOImage image = new IIOImage(img, null, null);
							writer.write(null, image, iwp);
							output.close();
						}
					}
					else {
						BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(mapFileName));
						JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
						JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);

						param.setQuality(1.0f, false);
						encoder.setJPEGEncodeParam(param);
						encoder.encode(img);
						out.close();
					}
				} else {
					String sMessage = new String("Unable to create map jpg image for: "+view.getLabel()+"\n");
					if (!vtMessages.contains(sMessage)) {
						vtMessages.addElement(sMessage);
					}
				}
			} else {
				File file = new File(mapFileName);
				FileWriter writer = new FileWriter(file);
				writer.write(new String(view.getLabel()+" is an empty map").toCharArray() );
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception (HTMLViews.createHTML) \n\n"+ex.getMessage());
			System.out.flush();
			return;
		}

		htmlString.append(setupFileHeader(view));
		htmlString.append("<img Suppress=true src=\""+mapHTMLName);
		htmlString.append("\" width=\""+size.width+"\" height=\""+size.height+"\"");
		htmlString.append(" hspace=0 vspace=0 border=0 usemap=\"#"+viewID+"_map\">\n");
		htmlString.append("<map name=\""+viewID+"_map\">\n");

	    try {
			Vector vtTemp = vs.getNodePositions(session, view.getId());
			Vector nodeList = new Vector();

			// nodeList will be a vector of NodePositions...
			for(int i = 0; i< vtTemp.size(); i++){
    			nodeList.addElement( ((NodePosition)vtTemp.elementAt(i)).getNode());
			}

			int xpos = 0;
			int ypos = 0;

			int detailBoxHeight = 530;

			String nodeID = "";
			NodeSummary node = null;

			boolean hasExternalImage = false;
			boolean hasReference = false;
			boolean isInternalReference = false;
			String sInternalView = "";
			String sInternalNode = "";
			String image = "";
			String source = "";
			String inner = "";

			// now, for each node found in the view...
			for(int i = 0; i<vtTemp.size(); i++){

				// RESET VARIABLES
				detailBoxHeight = 530;
				hasReference = false;
				hasExternalImage = false;
				isInternalReference = false;
				sInternalView = "";
				sInternalNode = "";
				image = "";
				source = "";
				inner = "";

				NodePosition oPos = (NodePosition)vtTemp.elementAt(i);
				UINode cn = new UINode( oPos, sAuthor);
				node = (NodeSummary)nodeList.elementAt(i);

				nodeID = node.getId();
				node.initialize(session, model);

				UINode uinode = (UINode)pane.get(nodeID);
				calculateCoords(uinode);

				Point nodeloc = uinode.getLocation();
				Dimension nodesize = uinode.getSize();

				// CREATE NODE DETAIL VARIABLES AND HTML PAGE
				String detailLoc = "details"+ProjectCompendium.sFS+nodeID+"_"+viewID+".html";

				if (!bZipUp && !bNoDetailPopupAtAll) {
					File directory = new File(sDirectory+ProjectCompendium.sFS+"details");
					if (!directory.isDirectory()) {
						directory.mkdirs();
					}
				}

				File detailFile = new File(detailLoc);
				String detailName = detailFile.getName();
				String htmlDetailPath = "details/"+detailName;
				detailPath1 = sDirectory + ProjectCompendium.sFS + detailLoc;

				int detailItemCount = 1; // 1 = just label and anchor
				if (!bNoDetailPopupAtAll) {
					detailItemCount = createDetailFile(detailPath1, htmlDetailPath, node, view);
				}

				boolean includeDetail =
				    (!bNoDetailPopupAtAll && (detailItemCount > 1 || !bNoDetailPopup));
				if (includeDetail
				        && (node.isReference() || node.isReferenceShortcut())
				        && node.getDetail().isEmpty())
				{
				    includeDetail = false;
				}

				if (detailItemCount == 3 ) {
					detailBoxHeight = detailBoxHeight + 30;
				}
				else if (detailItemCount == 4 ) {
					detailBoxHeight = detailBoxHeight + 80;
				}
				else if (detailItemCount > 4) {
					detailBoxHeight = detailBoxHeight + 130;
				}

				int type = node.getType();
				String path = UIImages.getPath(type, uinode.getNodePosition().getShowSmallIcon());

				String nodeName = node.getLabel();

				String newPath = null;

				// SPECIAL CASES:
				if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
					image = node.getImage();
					source = node.getSource();
					if (source != null && !source.equals("")) {
						if (source.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
							inner = source.substring(ICoreConstants.sINTERNAL_REFERENCE.length());
							int ind = inner.indexOf("/");
							if (ind != -1) {
								sInternalView = inner.substring(0, ind);
								sInternalNode = inner.substring(ind+1);
								isInternalReference = true;
							}
						}
						if ( !UIImages.isImage(source) ) {
							hasReference = true;
						}
					}

					if (image != null && !image.equals("")) {
						if (image.startsWith("www.")) {
							image = "http://"+image;
						}
						path = image;
						hasExternalImage = true;
					}
					else if(source != null && source.equals("")) //if no ref, leave ref icon (path)
						path = path;
					else{
						if (source != null) {
							if (source.startsWith("www.")) {
								source = "http://"+source;
							}
							if ( UIImages.isImage(source) ) {
								hasExternalImage = true;
								path = source;
							}
							else {
								path = UIImages.getReferencePath(source, path, oPos.getShowSmallIcon());
							}
						}
					}
				}
				else if(type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT ||
						type == ICoreConstants.LISTVIEW || type == ICoreConstants.LIST_SHORTCUT) {
					image = node.getImage();
					if (image != null && !image.equals("")) {
						if (image.startsWith("www.")) {
							image = "http://"+image;
						}
						path = image;
						hasExternalImage = true;
					}
				}

				// NODE LABEL IMAGE MAPPING FOR OPENING DETAILS WINDOW
				int labelX = nodeloc.x+labelRectangle.x;
				int labelY = nodeloc.y+labelRectangle.y;
				int labelWidth = labelX+labelRectangle.width;
				int labelHeight = labelY+labelRectangle.height;

				if (path != null && hasIcon) {
					String htmlImagePath = path;

					boolean isImageURL = false;
					String sLowerCasePath = path.toLowerCase();
					if (sLowerCasePath.startsWith("http:") || sLowerCasePath.startsWith("https:")) {
						isImageURL = true;
					}
					boolean isURL = false;
					String sLowerCaseSource = source.toLowerCase();
					if (sLowerCaseSource.startsWith("http:") || sLowerCaseSource.startsWith("https:")) {
						isURL = true;
					}

					File imageFile = new File(path);
					String imageName = "";
					if (imageFile.exists() && !isImageURL && (bIncludeReferences || !hasExternalImage)) {
						imageName = imageFile.getName();
						htmlImagePath = "images/"+imageName;
						newPath = sDirectory + ProjectCompendium.sFS + "images" +  ProjectCompendium.sFS + imageName;
						createImageFile(path, newPath);
					} else if (!imageFile.exists() && hasExternalImage && !isImageURL ) {
						htmlImagePath = "";
					}

					int iconX = nodeloc.x+iconRectangle.x;
					int iconY = nodeloc.y+iconRectangle.y;
					int iconWidth = iconX+iconRectangle.width;
					int iconHeight = iconY+iconRectangle.height;

					// NODE ICON MAPPING
					if(type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT ||
						type == ICoreConstants.LISTVIEW || type == ICoreConstants.LIST_SHORTCUT) {
						View match = mapOkay(nodeID);
						if( match != null ) {
							String sViewFileName = createFileName((View) node);

							htmlString.append("<area id=\"nid"+nodeID+"_"+viewID+"\" shape=\"rect\" coords=\""+labelX+","+labelY+","+labelWidth+","+labelHeight+"\"");
							if (includeDetail) {
								htmlString.append(" href=\"\" onClick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+node.getId()+"',");
								htmlString.append("'width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes');\"");
								htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"labelhint'); return false;\"/>\n");
							} else {
								htmlString.append(" href=\"\"");
								htmlString.append("/>\n");
							}

							htmlString.append("<area shape=\"rect\" coords=\""+iconX+","+iconY+","+iconWidth+","+iconHeight+"\"");
							htmlString.append(" href=\"javascript:loadFile('"+sViewFileName+"','"+nodeID+"')\" onClick=\"javascript:loadFile('"+sViewFileName+"','"+nodeID+"')\"");
							htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/>\n");

							if (includeDetail) {
								if (type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT) {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view map contents<br>Click label to view this node's details"));
									divString.append(createHintDiv(nodeID+"labelhint", "Click icon to view map contents<br>Click label to view this node's details"));
								} else {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view list contents<br>Click label to view this node's details"));
									divString.append(createHintDiv(nodeID+"labelhint", "Click icon to view list contents<br>Click label to view this node's details"));
								}
							} else {
								if (type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT) {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view map contents"));
								} else {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view list contents"));
								}
							}
						}
						else {
							htmlString.append("<area id=\"nid"+nodeID+"_"+viewID+"\" shape=\"poly\" coords=\""+iconX+","+iconY+","+(iconX+iconRectangle.width)+","+iconY+", ");
							htmlString.append((iconX+iconRectangle.width)+","+labelY+","+(labelX+labelRectangle.width)+","+labelY+", ");
							htmlString.append((labelX+labelRectangle.width)+","+(labelY+labelRectangle.height)+","+labelX+","+(labelY+labelRectangle.height)+", ");
							htmlString.append(labelX+","+labelY+","+iconX+","+labelY+"\" ");

							if (includeDetail) {
								htmlString.append(" href=\"\" onclick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+nodeID+"',");
	 							htmlString.append("'width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes');\"");
								htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/>\n");
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon or label to view this node's details"));
							} else {
								htmlString.append("/>\n");
							}
						}
					}
					else {
						if (isInternalReference) {
							View match = mapOkay(sInternalView);
							if( match != null ) {
								String sViewFileName = createFileName(match);
								sViewFileName += "#nid"+sInternalView+"_"+sInternalNode;
								htmlString.append("<area id=\"nid"+nodeID+"_"+viewID+"\" shape=\"rect\" coords=\""+labelX+","+labelY+","+labelWidth+","+labelHeight+"\"");

								if (includeDetail) {
									htmlString.append(" href=\"\" onClick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+nodeID+"',");
									htmlString.append("'width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes');\"");
									htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"labelhint'); return false;\"/>\n");
								} else {
									htmlString.append("/>\n");
								}
								htmlString.append("<area shape=\"rect\" coords=\""+iconX+","+iconY+","+iconWidth+","+iconHeight+"\"");
								htmlString.append(" href=\"javascript:loadFile('"+sViewFileName+"','"+nodeID+"')\" onclick=\"javascript:loadFile('"+sViewFileName+"','"+nodeID+"')\"");
								htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/>\n");

								if (includeDetail) {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to GOTO referenced view<br>Click label to view this node's details"));
									divString.append(createHintDiv(nodeID+"labelhint", "Click icon to GOTO referenced view<br>Click label to view this node's details"));
								} else {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to GOTO referenced view"));
								}
							}
							else {
								htmlString.append("<area id=\"nid"+nodeID+"_"+viewID+"\" shape=\"poly\" coords=\""+iconX+","+iconY+","+(iconX+iconRectangle.width)+","+iconY+", ");
								htmlString.append((iconX+iconRectangle.width)+","+labelY+","+(labelX+labelRectangle.width)+","+labelY+", ");
								htmlString.append((labelX+labelRectangle.width)+","+(labelY+labelRectangle.height)+","+labelX+","+(labelY+labelRectangle.height)+", ");
								htmlString.append(labelX+","+labelY+","+iconX+","+labelY+"\" ");

								if (includeDetail) {
									htmlString.append(" href=\"\" onclick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+nodeID+"',");
		 							htmlString.append("'width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes');\"");
									htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/>\n");
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon or label to view this node's details"));
								} else {
									htmlString.append("/>\n");
								}
							}
						} else if (hasReference && (bIncludeReferences || isURL)) {

							File refFile = new File(source);
							String refName = refFile.getName();
							String newSourcePath = "";

							// If the source a local file or URL
							// If URL, leave original source.
							if (CoreUtilities.isFile(source)) {
								newSourcePath = "references/"+refName;
							}
							else
								newSourcePath = source;

							htmlString.append("<area id=\"nid"+nodeID+"_"+viewID+"\" shape=\"rect\" Coords=\""+labelX+","+labelY+","+labelWidth+","+labelHeight+"\"");

							if (includeDetail) {
								htmlString.append(" href=\"\" onClick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+node.getId()+"',");
								htmlString.append("'width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes');\"");
								htmlString.append(" onMouseOut=\"hideHints(1); return false;\" onMouseOver=\"showHint(event,'"+nodeID+"labelhint'); return false;\"/>\n");
							} else {
								htmlString.append("/>\n");
							}

							htmlString.append("<area shape=\"rect\" coords=\""+iconX+","+iconY+","+iconWidth+","+iconHeight+"\"");

							if (bOpenNew) {
								htmlString.append(" href=\""+newSourcePath+"\" target=\"_blank\" onClick=\"openFile('"+newSourcePath+"', '"+nodeID+"')\"");
							} else {
								htmlString.append(" href=\""+newSourcePath+"\" onClick=\"loadFile('"+newSourcePath+"', '"+nodeID+"')\"");
							}
							htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/>\n");

							if (includeDetail) {
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon to follow hyperlink<br>Click label to view this node's details"));
								divString.append(createHintDiv(nodeID+"labelhint", "Click icon to follow hyperlink<br>Click label to view this node's details"));
							} else {
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon to follow hyperlink"));
							}

						} else if (hasExternalImage && bIncludeReferences && !includeDetail) {
							htmlString.append("<area id=\"nid"+nodeID+"_"+viewID+"\" shape=\"rect\" coords=\""+labelX+","+labelY+","+labelWidth+","+labelHeight+"\"");
							//TODO: This "else if" branch has "!includeDetail" condition
							//      so the the next "if" has become redundant
							if (includeDetail) {
								htmlString.append(" href=\"\" onClick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+nodeID+"',");
								htmlString.append("'width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes');\"");
								htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"labelhint'); return false;\"/>\n");
							} else {
								htmlString.append("/>\n");
							}

							htmlString.append("<area shape=\"rect\" coords=\""+iconX+","+iconY+","+iconWidth+","+iconHeight+"\"");

							if (bOpenNew) {
								htmlString.append(" href=\""+htmlImagePath+"\" target=\"_blank\" onClick=\"openFile('"+htmlImagePath+"', '"+nodeID+"')\"");
							} else {
								htmlString.append(" href=\""+htmlImagePath+"\" onClick=\"loadFile('"+htmlImagePath+"', '"+nodeID+"')\"");
							}
							htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/>\n");

                            //TODO: This "else if" branch has "!includeDetail" condition
                            //      so the the next "if" has become redundant.
							if (includeDetail) {
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view full size image<br>Click label to view this node's details"));
								divString.append(createHintDiv(nodeID+"labelhint", "Click icon to view full size image<br>Click label to view this node's details"));
							} else {
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view full size image"));
							}
						} else {
							if (includeDetail) {
								htmlString.append("<area id=\"nid"+nodeID+"_"+viewID+"\" shape=\"poly\" coords=\""+iconX+","+iconY+","+(iconX+iconRectangle.width)+","+iconY+", ");
								htmlString.append((iconX+iconRectangle.width)+","+labelY+","+(labelX+labelRectangle.width)+","+labelY+", ");
								htmlString.append((labelX+labelRectangle.width)+","+(labelY+labelRectangle.height)+","+labelX+","+(labelY+labelRectangle.height)+", ");
								htmlString.append(labelX+","+labelY+","+iconX+","+labelY+"\" ");
								htmlString.append(" href=\"\" onClick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+nodeID+"',");
	 							htmlString.append("'width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes');\"");
								htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/>\n");
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon or label to view this node's details"));
							}
						}
					}

					// ADD HIGHLIGHTER DIV
					int xHigh = nodeloc.x;
					int yHigh = nodeloc.y;
					if (xHigh < 0) {
						xHigh = 0;
					}
					if (yHigh < 0) {
						yHigh = 0;
					}
					int widthHigh = nodesize.width;
					int heightHigh = nodesize.height;

					divString.append("\n<div id=\"nid"+nodeID+"_"+viewID+"highlight\" style=\"position:absolute; z-index:15; visibility:hidden; left:"+xHigh+"px; top:"+yHigh+"px; width:"+widthHigh+"px; height:"+heightHigh+"px; border: 2px solid yellow\"></div>\n");

					// ICON TRANSCLUSION INDICATOR MAPPING
					if (hasTrans) {
						int transX = nodeloc.x+transRectangle.x;
						int transY = nodeloc.y+transRectangle.y;
						int transWidth = transX+transRectangle.width;
						int transHeight = transY+transRectangle.height;

						htmlString.append("<area shape=\"rect\" coords=\""+transX+","+transY+","+transWidth+","+transHeight+"\"");
						htmlString.append(" onmouseover=\"showHint(event,'"+nodeID+"views'); return false;\"/>\n");

						divString.append("<div id=\""+nodeID+"views\" style=\"position: absolute; z-index: 20; visibility: hidden; left: 0; top: 0;\">\n");
						divString.append("\t<table border=1 cellpadding=1 cellspacing=0 bgcolor=\""+hintcolor+"\">\n");

						Vector views = node.getMultipleViews();
						views = CoreUtilities.sortList(views);

						int countj = views.size();
						String sMainFileName = "";
						String sViewID = "";
						String sLabel="";
						for(int j=0; j<countj; j++) {
							View tmpView = (View)views.elementAt(j);
							//if (!tmpView.getId().equals(view.getId())) {
							if (tmpView != null) {
								sMainFileName = createFileName(tmpView);
								sViewID = tmpView.getId();
								sLabel = sMainFileName;
								if (oHomeViews.containsKey(tmpView.getId())) {
									sLabel = sLabel + " - " + oHomeViews.get(tmpView.getId());
								}

								if (sLabel.length() > 100)
									sLabel = sLabel.substring(0, 97)+"...";

								divString.append("\t\t<tr>\n");
								divString.append("\t\t\t<td nowrap style=\"hint\">");
								View match = mapOkay(sViewID);
								if (match != null && !sViewID.equals(view.getId())) {
		       						divString.append("\t\t\t\t<a href=\""+sMainFileName+"#nid"+nodeID+"_"+sViewID+"\" onClick=\"loadFile('"+sMainFileName+"#nid"+nodeID+"_"+sViewID+"', '"+sViewID+"'); return false;\">"+sLabel+"</a>\n");
								}
								else {
		       						divString.append("\t\t\t\t"+sLabel);
								}
								divString.append("\t\t\t</td>\n\t\t</tr>\n");
							}
						}
						divString.append("\t</table>\n</div>\n");
					}

					// ICON WEIGHT INDICATOR MAPPING - NOT USED AT THE MOMENT
					//if (hasWeight) {
					//	int weightX = nodeloc.x+weightRectangle.x;
					//	int weightY = nodeloc.y+weightRectangle.y;
					//	int weightWidth = weightX+weightRectangle.width;
					//	int weightHeight = weightY+weightRectangle.height;
					//	htmlString.append("<AREA Shape=\"rect\" Coords=\""+weightX+","+weightY+","+weightWidth+","+weightHeight+"\"");
					//	htmlString.append(" HREF=\""+htmlDetailPath+"\" target=\"_blank\"/>\n");
					//}

					// ICON TEXT INDICATOR MAPPING
					if (hasText) {
						int textX = nodeloc.x+textRectangle.x;
						int textY = nodeloc.y+textRectangle.y;
						int textWidth = textX+textRectangle.width;
						int textHeight = textY+textRectangle.height;

						if (includeDetail) {
							htmlString.append("<area shape=\"rect\" coords=\""+textX+","+textY+","+textWidth+","+textHeight+"\"");
							htmlString.append(" onClick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+nodeID+"','width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes')\" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"text'); return false;\"/>\n");
						} else {
							htmlString.append("<area shape=\"rect\" coords=\""+textX+","+textY+","+textWidth+","+textHeight+"\"");
							htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"text'); return false;\"/>\n");
						}

						divString.append("<div id=\""+nodeID+"text\" style=\"position: absolute; z-index: 20; visibility: hidden; left: 0; top: 0;\">\n");
						divString.append("\t<table width=\"250\" border=1 cellpadding=1 cellspacing=0 bgcolor=\""+hintcolor+"\">\n");
						divString.append("\t\t<tr width=\"250\">\n");
						divString.append("\t\t\t<td width=\"250\" style=\"hint\"><p>\n");

						String detail = node.getDetail();
						detail = detail.trim();

						if (detail.length() > APP_PROPERTIES.getDetailRolloverLength()) {
							detail = detail.substring(0, APP_PROPERTIES.getDetailRolloverLength())+"...";
						}

						divString.append("\t\t\t\t"+detail+"\n");
						divString.append("\t\t\t</p></td>\n\t\t</tr>\n\t</table>\n</div>\n");
					}

					// ICON TAG INDICATOR MAPPING
					if (hasCodes) {
						int codeX = nodeloc.x+codeRectangle.x;
						int codeY = nodeloc.y+codeRectangle.y;
						int codeWidth = codeX+codeRectangle.width;
						int codeHeight = codeY+codeRectangle.height;

						// ADD DIV FOR ROLLOVER
						int nCodeCount = node.getCodeCount();
						String tag = "";
						if (nCodeCount > 0) {
							divString.append("<div id=\""+nodeID+"tags\" style=\"position: absolute; z-index: 20; visibility: hidden; left: 0; top: 0;\">\n");
							divString.append("\t<table border=1 cellpadding=1 cellspacing=0 bgcolor=\""+hintcolor+"\">\n");
							divString.append("\t\t<tr>\n");
							divString.append("\t\t\t<td nowrap style=\"hint\">\n");
							boolean isFirst = true;
							for(Enumeration e = node.getCodes(); e.hasMoreElements();) {
								Code code = (Code)e.nextElement();
								tag = code.getName();
								if (isFirst) {
		        					divString.append("\t\t\t\t"+tag);
									isFirst = false;
								}
								else {
		        					divString.append("<br/>\n\t\t\t\t"+tag);
								}
	      					}
							divString.append("\n\t\t\t</td>\n\t\t</tr>\n\t</table>\n</div>\n");

							htmlString.append("<area shape=\"rect\" coords=\""+codeX+","+codeY+","+codeWidth+","+codeHeight+"\"");
							htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"tags'); return false;\"/>\n");
						}
					}
				} else { // IF NO ICON
					if (includeDetail) {
						divString.append(createHintDiv(nodeID+"labelhint", "Click label to view this node's details"));
					}
					htmlString.append("<area id=\"nid"+nodeID+"_"+viewID+"\" alt=\""+node.getLabel()+"\" Shape=\"rect\" Coords=\""+labelX+","+labelY+","+labelWidth+","+labelHeight+"\"");
					if (includeDetail) {
						htmlString.append(" href=\"\" onClick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+node.getId()+"',");
						htmlString.append("'width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes');\"");
						htmlString.append(" onmouseout=\"hideHints(1); return false;\" onmouseover=\"showHint(event,'"+nodeID+"labelhint'); return false;\"/></a>\n");
					} else {
						htmlString.append(" href=\"\"");
						htmlString.append("/>\n");
					}
				}
   			}

			htmlString.append("</map></div>\n");
			htmlString.append(divString.toString());
			htmlString.append("</body></html>\n");

			if (!bZipUp) {
				try {
					FileWriter fw = new FileWriter(sDirectory +ProjectCompendium.sFS+ sFileName);
					fw.write(htmlString.toString());
					fw.close();
	  			}
				catch(IOException e) {
					System.out.println("Some sort of io problem while creating HTML");
				}
			}
			else {
				htCreatedFiles.put(sFileName, htmlString.toString());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("caught a hanger");
		}

		if (!wasOpen) {
			try {frame.setClosed(true);}
			catch(Exception io){
				System.out.println("error closing open View "+frame.getView().getLabel());
			}
		}
 	}

	/**
	 * Process the export of a list view and create the HTML text require for the page.
	 *
	 * @param view com.compendium.datamodel.View, the list view to process.
	 * @param the filename for the main map file for this view.
	 */
	private void processList(View view, String sFileName){

		View currView = view;
		//String htmlString = "";
		StringBuffer htmlString = new StringBuffer(2000);
		StringBuffer divString = new StringBuffer(2000);
		String viewID = view.getId();

	    try{
			Vector vtTemp = vs.getNodePositions(session, view.getId());
			Vector nodeList = new Vector();

			for(int i = 0; i< vtTemp.size(); i++){
    			nodeList.addElement( ((NodePosition)vtTemp.elementAt(i)).getNode() );
			}

			htmlString.append(setupFileHeader(view));
			htmlString.append("<table width='100%' style='margin-left: 10px; margin-top: 10px;'>\n");

			int xpos = 0;
			int ypos = 0;
			int yDivPos = 10;

			int detailBoxHeight = 450;

			String nodeID = "";
			boolean hasReference = false;
			boolean hasExternalImage = false;
			boolean isInternalReference = false;
			String sInternalView = "";
			String sInternalNode = "";
			String image = "";
			String source = "";
			String inner = "";

			// now, for each node found in the view...
			for(int i = 0; i<vtTemp.size(); i++){

				// RESET VARIABLES
				detailBoxHeight = 450;
				hasReference = false;
				hasExternalImage = false;
				isInternalReference = false;
				sInternalView = "";
				sInternalNode = "";
				image = "";
				source = "";
				inner = "";

				UINode cn = new UINode( (NodePosition)vtTemp.elementAt(i), sAuthor);
				NodeSummary node = (NodeSummary)nodeList.elementAt(i);
				nodeID = node.getId();
				node.initialize(session, model);

				// we get the type of node (ie: list, map, etc)
				int type = node.getType();
				String path = UIImages.getPath(type, false);

				// and we'll use this to create a details file, listing details for clickable labels
				String detailLoc = "details"+ProjectCompendium.sFS+node.getId()+"_"+viewID+".html";
				String nodeName = (String)node.getLabel();

				//then create a directory instance, and see if the directory exists.
				if (!bZipUp && !bNoDetailPopupAtAll) {
					File directory = new File(sDirectory+ProjectCompendium.sFS+"details");
					if (!directory.isDirectory()) {
						directory.mkdirs();
					}
				}

				if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
					image = node.getImage();
					source = node.getSource();

					if (source != null && !source.equals("")) {
						if (source.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
							inner = source.substring(ICoreConstants.sINTERNAL_REFERENCE.length());
							int ind = inner.indexOf("/");
							if (ind != -1) {
								sInternalView = inner.substring(0, ind);
								sInternalNode = inner.substring(ind+1);
								isInternalReference = true;
							}
						}
						if ( !UIImages.isImage(source) ) {
							hasReference = true;
						}
					}
					if (image != null && !image.equals("")) {
						if ((image.toLowerCase()).startsWith("www.")) {
							image = "http://"+image;
						}
						path = image;
						hasExternalImage = true;
					}
					else if(source != null && source.equals("")) //if no ref, leave ref icon (path)
						path = path;
					else{
						if (source != null) {
							if ((source.toLowerCase()).startsWith("www.")) {
								source = "http://"+source;
							}
							if ( UIImages.isImage(source) ) {
								hasExternalImage = true;
								path = source;
							}
							else
								path = UIImages.getReferencePath(source, path, false);
						}
					}
				}
				else if(type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT ||
						type == ICoreConstants.LISTVIEW || type == ICoreConstants.LIST_SHORTCUT) {
					image = node.getImage();
					if (image != null && !image.equals("")) {
						if ((image.toLowerCase()).startsWith("www.")) {
							image = "http://"+image;
						}
						path = image;
						hasExternalImage = true;
					}
				}

				if (path != null) {
					int imageWidth = -1;
					int imageHeight = -1;
					Dimension refDim = null;

					// GET THE SCALED WIDTH AND HEIGHT OR REFERENCE NODE IMAGES
					if (hasExternalImage) {
						refDim = UIImages.thumbnailImage(path, imageWidth, imageHeight);
						imageWidth = refDim.width;
						imageHeight = refDim.height;
					}
					else {
						refDim = UIImages.getImageSize(path);
						imageWidth = refDim.width;
						imageHeight = refDim.height;
					}

					// PREPARE ICON IMAGE PATH AND IF THE IMAGE IS A LOCAL FILE COPY FOR EXPORT
					String htmlImagePath = path;

					boolean isImageURL = false;
					String sLowerCasePath = path.toLowerCase();
					if (sLowerCasePath.startsWith("http:") || sLowerCasePath.startsWith("https:")) {
						isImageURL = true;
					}
					boolean isURL = false;
					String sLowerCaseSource = source.toLowerCase();
					if (sLowerCaseSource.startsWith("http:") || sLowerCaseSource.startsWith("https:")) {
						isURL = true;
					}

					File imageFile = new File(path);
					if (imageFile.exists() && !isImageURL && (bIncludeReferences || !hasExternalImage)) {
						String imageName = imageFile.getName();
						htmlImagePath = "images/"+imageName;
						String newPath = sDirectory + ProjectCompendium.sFS + "images" +  ProjectCompendium.sFS + imageName;
						createImageFile(path, newPath);
					} else if (!imageFile.exists() && hasExternalImage && !isImageURL ) {
						htmlImagePath = "";
					}

					Vector extraLinkInfo = new Vector(2);
					extraLinkInfo.add(refDim);

					File detailFile = new File(detailLoc);
					String detailName = detailFile.getName();
					String htmlDetailPath = "details/"+detailName;
					detailPath1 = sDirectory + ProjectCompendium.sFS + detailLoc;

					int detailItemCount = 1; // 1 = just label and anchor
					if (!bNoDetailPopupAtAll) {
						detailItemCount = createDetailFile(detailPath1, htmlDetailPath, node, view);
					}

					boolean includeDetail = (!bNoDetailPopupAtAll && (detailItemCount > 1 || !bNoDetailPopup));
	                if (includeDetail
	                        && (node.isReference() || node.isReferenceShortcut())
	                        && node.getDetail().isEmpty())
	                {
	                    includeDetail = false;
	                }

					if (detailItemCount == 3 ) {
						detailBoxHeight = 480;
					}
					else if (detailItemCount == 4 ) {
						detailBoxHeight = 530;
					}
					else if (detailItemCount > 4) {
						detailBoxHeight = 580;
					}

					// NEED THIS IN NEXT IF/ELSE STATEMENTS FOR TEXT POSITIONING CALCULATIONS
					FontMetrics fm = cn.getFontMetrics(new Font("Serif", Font.PLAIN, 12));
					String sViewFileName="";
					if(type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT ||
							type == ICoreConstants.LISTVIEW || type == ICoreConstants.LIST_SHORTCUT) {

						View match = mapOkay(nodeID);
						if( match != null ) {
							sViewFileName = this.createFileName((View) node);
							htmlString.append("<tr><td width=\"50\"><a href=\"javascript:loadFile('"+sViewFileName+"','"+nodeID+"')\" onClick=\"javascript:loadFile('"+sViewFileName+"','"+nodeID+"')\"");
							htmlString.append(" onMouseOut=\"hideHints(1); return false;\" onMouseOver=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/>\n");

							htmlString.append("<img border='0' style='width:"+imageWidth+"px;height:"+imageHeight+"px;' ");
							htmlString.append("src='" + htmlImagePath + "'/></a></td>");

							if (type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT) {
								if (includeDetail) {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view map contents<br>Click label to view this node's details"));
									divString.append(createHintDiv(nodeID+"labelhint", "Click icon to view map contents<br>Click label to view this node's details"));
								} else {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view map contents"));
								}
							}
							else {
								if (includeDetail) {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view list contents<br>Click label to view this node's details"));
									divString.append(createHintDiv(nodeID+"labelhint", "Click icon to view list contents<br>Click label to view this node's details"));
								} else {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view list contents"));
								}
							}
						} else {
							if (includeDetail) {
								htmlString.append("<tr><td width=\"50\"><img border='0' style='width:"+imageWidth+"px;height:"+imageHeight+"px;' ");
								htmlString.append("src='" + htmlImagePath + "' onMouseOut=\"hideHints(1); return false;\" onMouseOver=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/></td>");
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon or label to view this node's details"));
								divString.append(createHintDiv(nodeID+"labelhint", "Click icon or label to view this node's details"));
							} else {
								htmlString.append("<tr><td width=\"50\"><img border='0' style='width:"+imageWidth+"px;height:"+imageHeight+"px;' ");
								htmlString.append("src='" + htmlImagePath + "'/></td>");
							}
						}
					} else {
						if (isInternalReference) {
							View match = mapOkay(sInternalView);
							System.out.println("view="+view);
							if( match != null ) {
								sViewFileName = this.createFileName(match);
								sViewFileName += "#nid"+sInternalView+"_"+sInternalNode;
								htmlString.append("<tr><td width=\"50\"><a href=\"javascript:loadFile('"+sViewFileName+"','"+nodeID+"')\" onClick=\"javascript:loadFile('"+sViewFileName+"','"+nodeID+"')\"");
								htmlString.append(" onMouseOut=\"hideHints(1); return false;\" onMouseOver=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/>\n");

								htmlString.append("<img border='0' style='width:"+imageWidth+"px;height:"+imageHeight+"px;' ");
								htmlString.append("src='" + htmlImagePath + "'/></a></td>");
								if (includeDetail) {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to GOTO referenced view<br>Click label to view this node's details"));
									divString.append(createHintDiv(nodeID+"labelhint", "Click icon to GOTO referenced view<br>Click label to view this node's details"));
								} else {
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon to GOTO referenced view"));
								}
							} else {
								if (includeDetail) {
									htmlString.append("<tr><td width=\"50\"><img border='0' style='width:"+imageWidth+"px;height:"+imageHeight+"px;' ");
									htmlString.append("src='" + htmlImagePath + "' onMouseOut=\"hideHints(1); return false;\" onMouseOver=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/></td>");
									divString.append(createHintDiv(nodeID+"imagehint", "Click icon or label to view this node's details"));
									divString.append(createHintDiv(nodeID+"labelhint", "Click icon or label to view this node's details"));
								} else {
									htmlString.append("<tr><td width=\"50\"><img border='0' style='width:"+imageWidth+"px;height:"+imageHeight+"px;' ");
									htmlString.append("src='" + htmlImagePath + "'/></td>");
								}
							}
						} else if (hasReference && (bIncludeReferences || isURL)) {
							File refFile = new File(source);
							String refName = refFile.getName();
							String newSourcePath = "";

							// If the source a local file or URL
							// If URL, leave original source.
							if (CoreUtilities.isFile(source)) {
								newSourcePath = "references/"+refName;
							}
							else
								newSourcePath = source;

							if (bOpenNew) {
								htmlString.append("<tr><td width=\"50\"><a href=\""+newSourcePath+"\" target=\"_blank\" onClick=\"openFile('"+newSourcePath+"', '"+nodeID+"')\"");
							} else {
								htmlString.append("<tr><td width=\"50\"><a href=\""+newSourcePath+"\" onClick=\"loadFile('"+newSourcePath+"', '"+nodeID+"')\"");
							}
							htmlString.append(" onMouseOut=\"hideHints(1); return false;\" onMouseOver=\"showHint(event,'"+nodeID+"imagehint'); return false;\">\n");
							htmlString.append("<img border='0' style='width:"+imageWidth+"px;height:"+imageHeight+"px;' ");
							htmlString.append("src='" + htmlImagePath + "'></a></td>");

							if (includeDetail) {
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon to follow hyperlink<br>Click label to view this node's details"));
								divString.append(createHintDiv(nodeID+"labelhint", "Click icon to follow hyperlink<br>Click label to view this node's details"));
							} else {
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon to follow hyperlink"));
							}
						} else if (hasExternalImage && bIncludeReferences && !includeDetail) {

							if (bOpenNew) {
								htmlString.append("<tr><td width=\"50\"><a href=\""+htmlImagePath+"\" target=\"_blank\" onClick=\"openFile('"+htmlImagePath+"', '"+nodeID+"')\"");
							} else {
								htmlString.append("<tr><td width=\"50\"><a href=\""+htmlImagePath+"\" onClick=\"loadFile('"+htmlImagePath+"', '"+nodeID+"')\"");
							}
							htmlString.append(" onMouseOut=\"hideHints(1); return false;\" onMouseOver=\"showHint(event,'"+nodeID+"imagehint'); return false;\">\n");
							htmlString.append("<img border='0' style='width:"+imageWidth+"px;height:"+imageHeight+"px;' ");
							htmlString.append("src='" + htmlImagePath + "'></a></td>");

                            //TODO: This "else if" branch has "!includeDetail" condition
                            //      so the the next "if" has become redundant.
							if (includeDetail) {
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view full size image<br>Click label to view this node's details"));
								divString.append(createHintDiv(nodeID+"labelhint", "Click icon to view full size image<br>Click label to view this node's details"));
							} else {
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon to view full size image"));
							}
						} else {
							htmlString.append("<tr><td width=\"50\">");

							if (includeDetail) {
								htmlString.append("<a href=\"\" onClick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+nodeID+"',");
								htmlString.append("'width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes');\"");
								htmlString.append(" onMouseOut=\"hideHints(1); return false;\" onMouseOver=\"showHint(event,'"+nodeID+"labelhint'); return false;\"/>\n");
								htmlString.append("<img border='0' style='width:"+imageWidth+"px;height:"+imageHeight+"px;' ");
								htmlString.append("src='" + htmlImagePath + "' onMouseOut=\"hideHints(1); return false;\" onMouseOver=\"showHint(event,'"+nodeID+"imagehint'); return false;\"/></a></td>");
								divString.append(createHintDiv(nodeID+"imagehint", "Click icon or label to view this node's details"));
								divString.append(createHintDiv(nodeID+"labelhint", "Click icon or label to view this node's details"));
							} else {
								htmlString.append("<img border='0' style='width:"+imageWidth+"px;height:"+imageHeight+"px;' ");
								htmlString.append("src='" + htmlImagePath + "'/></td>");
							}
						}
					}

					int textYPos = (ypos + 30 + (imageHeight / 2)) - (fm.getDescent()+4);

					htmlString.append("<td nowrap><font face='Arial, Helvetica'>");
					if (includeDetail) {
						htmlString.append("<a name=\"nid"+nodeID+"_"+viewID+"\" href=\"\" onClick=\"javascript:openNewWindow('"+ htmlDetailPath +"','"+nodeID+"',");
						htmlString.append("'width="+detailBoxWidth+", height="+detailBoxHeight+", scrollbars=yes, resizable=yes');\"");
						htmlString.append(" onMouseOut=\"hideHints(1); return false;\" onMouseOver=\"showHint(event,'"+nodeID+"labelhint'); return false;\"/>"+nodeName+"</a>\n");
					} else {
						htmlString.append(nodeName);
					}
					htmlString.append("</font></td></tr>\n");

					// ADD HIGHLIGHTER DIV
					int xHigh = 10;
					int yHigh = yDivPos;
					yDivPos += 36;
					int widthHigh = 34;
					int heightHigh = 34;
					divString.append("\n<div id=\"nid"+nodeID+"_"+viewID+"highlight\" style=\"position:absolute; z-index:15; visibility:hidden; left:"+xHigh+"px; top:"+yHigh+"px; width:"+widthHigh+"px; height:"+heightHigh+"px; border: 2px solid yellow\"></div>\n");

					ypos += imageHeight+10;
				}
				else {
					System.out.println("not creating detail file for "+nodeName);
				}
 			}

			htmlString.append("</table>\n\n");
			htmlString.append(divString.toString());
			htmlString.append("</body></html>");

			if (!bZipUp) {
				try {
					FileWriter fw = new FileWriter(sDirectory +ProjectCompendium.sFS+ sFileName);
					fw.write(htmlString.toString());
					fw.close();
	  			}
				catch(IOException e) {
					System.out.println("Some sort of io problem while creating HTML");
				}
			}
			else {
				htCreatedFiles.put(sFileName, htmlString.toString());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("caught a hanger");
		}
 	}

	/**
	 * Create a div for a rollover hint message.
	 * @param sID the div id.
	 * @param sMessage the message to display.
	 * @return the final div html string.
	 */
	private String createHintDiv(String sID, String sMessage) {
		StringBuffer divString = new StringBuffer(300);

		divString.append("<div id=\""+sID+"\" style=\"POSITION: absolute; Z-INDEX: 20; VISIBILITY: hidden; left: 0; top: 0;\">\n");
		divString.append("\t<table width=\"200\" border=1 cellpadding=1 cellspacing=0 bgcolor=\""+hintcolor+"\">\n");
		divString.append("\t\t<tr width=\"200\">\n");
		divString.append("\t\t\t<td width=\"200\" style=\"hint\"><p>\n");
		divString.append("\t\t\t\t"+sMessage+"\n");
		divString.append("\t\t\t</p></td>\n\t\t</tr>\n\t</table>\n</div>\n");

		return divString.toString();
	}

	/**
	 * Create the header html text for the Image Map file.
	 * This includes an style information and javascript functions.
	 *
 	 * @param sTitle java.lang.String, the title of the the map.
	 * @return java.lang.String, the created HTML text.
	 */
	private String setupFileHeader(View view) {
	    StringBuffer sf = new StringBuffer(1000);

		sf.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">\n");
		sf.append("<title>"+view.getLabel()+"</title>\n");

		sf.append("<style>\n");
		sf.append("td { font-family:Arial, Helvetica; font-size:8.0pt; font-weight:normal; }\n");
		sf.append("</style>\n");

		// popBox Script
		sf.append("<script Language=\"JavaScript1.2\">\n");
		sf.append("<!--\n");

		// Scroll to node specified by anchor on current url
		sf.append("var highlightDiv = null;\n");
		sf.append("window.onload = function(){\n");
		sf.append("\tvar specificNodeID = unescape(parent.document.location.hash.substring(1));\n");
		sf.append("\tscrollAndHighlight(specificNodeID);\n");
		sf.append("}\n\n");

		sf.append("var IE = 0; var IE5 = 0; var NS = 0; var GECKO = 0;\n");
	    sf.append("var openpopups = new Array();\n");
    	sf.append("if (document.all) {     // Internet Explorer Detected\n");
		sf.append("\tOS = navigator.platform;\n");
		sf.append("\tVER = new String(navigator.appVersion);\n");
		sf.append("\tVER = VER.substr(VER.indexOf(\"MSIE\")+5, VER.indexOf(\" \"));\n");
		sf.append("\tif ((VER <= 5) && (OS == \"Win32\")) {\n");
	    sf.append("\t\tIE5 = true;\n");
		sf.append("\t} else {\n");
 	   	sf.append("\t\tIE = true;\n");
	   	sf.append("\t}\n");
    	sf.append("}\n");
    	sf.append("else if (document.layers) {   // Netscape Navigator Detected\n");
	   	sf.append("\tNS = true;\n");
    	sf.append("}\n");
    	sf.append("else if (document.getElementById) { // Netscape 6 Detected\n");
       	sf.append("\tGECKO = true;\n");
    	sf.append("}\n");
    	sf.append("else {\n");
		sf.append("\talert(\"Unrecognized Browser Detected. Sorry, your browser is not compatible.\");\n");
    	sf.append("}\n\n");

    	sf.append("function handleResize() {\n");
	    sf.append("\tlocation.reload();\n");
		sf.append("\treturn false;\n");
    	sf.append("}\n\n");

    	sf.append("if ((NS) && (navigator.platform == \"MacPPC\")) {\n");
		sf.append("\twindow.captureEvents(Event.RESIZE);\n");
		sf.append("\twindow.onresize = handleResize;\n");
    	sf.append("}\n\n");

		sf.append("function scrollAndHighlight(specificNodeID) {\n");
		sf.append("\tif (specificNodeID != null && specificNodeID != \"\") {\n");
		sf.append("\t\thighlightDiv = document.getElementById(specificNodeID+'highlight');\n");
		sf.append("\t\tif (highlightDiv) {\n");
		sf.append("\t\t\thighlightDiv.style.visibility = 'visible';\n");
		sf.append("\t\t\tx = highlightDiv.style.left;\n");
		sf.append("\t\t\ty = highlightDiv.style.top;\n");
		sf.append("\t\t\tyb = highlightDiv.style.height;\n");
		sf.append("\t\t\tx = (x.substring(0, x.length-2))*1;\n");
		sf.append("\t\t\ty = (y.substring(0, y.length-2))*1;\n");
		sf.append("\t\t\tyb = (yb.substring(0, yb.length-2))*1;\n");
		sf.append("\t\t\ty = y+yb;\n");
		sf.append("\t\t\twindow.scrollTo(x,y);\n");
		sf.append("\t\t\t// This didn't work!\n");
		sf.append("\t\t\t//node = highlightDiv = document.getElementById(specificNodeID);\n");
		sf.append("\t\t\t//node.scrollIntoView(true);\n");
		sf.append("\t\t}\n");
		sf.append("\t}\n");
		sf.append("}\n\n");

		sf.append("function openNewWindow(url, name, features){\n");
      	sf.append("\thideHints(1);\n");
		sf.append("\tvar popBox = window.open(url, name, features);\n");
		sf.append("\tpopBox.focus();\n");
		sf.append("return;\n");
		sf.append("}\n\n");

		sf.append("function loadFile(url, name) {\n");
      	sf.append("\thideHints(1);\n");
		sf.append("\twindow.location.href = url;\n");
		sf.append("return;\n");
		sf.append("}\n\n");

		sf.append("function openFile(url, name) {\n");
      	sf.append("\thideHints(1);\n");
		sf.append("\tvar popBox = window.open(url, name);\n");
		sf.append("\tpopBox.focus();\n");
		sf.append("return;\n");
		sf.append("}\n\n");

	    sf.append("function showHint(event, popupName) {\n");
		sf.append("\thideHints(1);\n");
		sf.append("\tif (GECKO) {\n");
		sf.append("\t\tdocument.getElementById(popupName).style.left = event.layerX+7 + document.body.scrollLeft;\n");
		sf.append("\t\tdocument.getElementById(popupName).style.top = event.layerY-5 + document.body.scrollTop;\n");
		sf.append("\t\tdocument.getElementById(popupName).style.background = \""+hintcolor+"\";\n");
		sf.append("\t\tdocument.getElementById(popupName).style.visibility = \"visible\";\n");
		sf.append("\t\topenpopups.push(popupName);\n");
		sf.append("\t}\n");
		sf.append("\telse if (NS) {\n");
		sf.append("\t\tdocument.layers[popupName].moveTo(event.pageX+7 + document.body.scrollLeft, event.pageY-5 + document.body.scrollTop);\n");
		sf.append("\t\tdocument.layers[popupName].bgColor = \""+hintcolor+"\";\n");
		sf.append("\t\tdocument.layers[popupName].visibility = \"show\";\n");
		sf.append("\t\topenpopups.push(popupName);\n");
		sf.append("\t}\n");
		sf.append("\telse if (IE || IE5) {\n");
		sf.append("\t\twindow.event.cancelBubble = true;\n");
		sf.append("\t\tdocument.all[popupName].style.left = window.event.clientX+7 + document.body.scrollLeft;\n");
		sf.append("\t\tdocument.all[popupName].style.top = window.event.clientY-5 + document.body.scrollTop;\n");
		sf.append("\t\tdocument.all[popupName].style.visibility = \"visible\";\n");
		sf.append("\t\topenpopups[openpopups.length] = popupName;\n");
		sf.append("\t}\n");
		sf.append("\treturn false;\n");
	    sf.append("}\n\n");

	    sf.append("function hideHighlight() {\n");
	    sf.append("\tif (highlightDiv != null) {\n");
	    sf.append("\t\thighlightDiv.style.visibility = 'hidden';\n");
	    sf.append("\t}\n");
	    sf.append("}\n\n");

	    sf.append("function hideHints(fromBody) {\n");
		sf.append("\tvar popupname;\n");
		sf.append("\tfor (var i = 0; i < openpopups.length; i++) {\n");
		sf.append("\t\tpopupname = new String (openpopups[i]);\n");
		sf.append("\t\tif ( IE || (GECKO && fromBody == 1)) {\n");
		sf.append("\t\t\tdocument.getElementById(popupname).style.visibility = \"hidden\";\n");
		sf.append("\t\t}\n");
		sf.append("\t\telse if (NS) {\n");
		sf.append("\t\t\tdocument.layers[popupname].visibility = \"hide\";\n");
	    sf.append("\t\t}\n");
	    sf.append("\t\telse if (IE5) {\n");
		sf.append("\t\t\tdocument.all[popupname].style.visibility = \"hidden\";\n");
	    sf.append("\t\t}\n");
		sf.append("\t}\n");
		sf.append("\topenpopups = new Array();\n");
		sf.append("\treturn;\n");
    	sf.append("}\n\n");

		sf.append("//-->\n</script>\n</head>\n\n<body onClick=\"hideHints(0); hideHighlight(); return false;\"  style=\"margin:0px; padding: 0px;\">\n");

		if (EXPORT_PROPERTIES.isAddMapTitles() || EXPORT_PROPERTIES.isAddFeedback()) {
    		sf.append("<table width=\"100%\"><tr><td valign=\"top\">");
            if (EXPORT_PROPERTIES.isAddMapTitles()) {
                sf.append("<h2>"+view.getLabel()+"</h2>");
            } else {
                sf.append("&nbsp;");
            }
            if (EXPORT_PROPERTIES.isAddFeedback()) {
                sf.append("</td>");
                sf.append("<td align=\"right\"><FORM>");
                sf.append("<INPUT TYPE=\"button\" VALUE=\"Send feedback\" onClick=\"parent.location=");
                sf.append("'mailto:");
                sf.append(encode(EXPORT_PROPERTIES.getFeedbackEmail()));
                sf.append("?subject=Feedback%20on%20Compendium%20webmap:%20"
                    + encode(view.getLabel()) + "&body=%0A%0A%0A");
                sf.append("====%20CONTEXT%20(DO%20NOT%20MODIFY%20BELOW%20THIS%20LINE)%20====%0A");
                sf.append("Project:%20" + encode(ProjectCompendium.APP.sFriendlyName) + "%0A");
                sf.append("View:%20" + encode(view.getLabel()) + "%0A");

                sf.append("Node:%20%0A");
                sf.append("ID:%20comp://" + encode(view.getId()) + "/" + encode("0")+ "%0A");
                sf.append("'\"></FORM></td></tr></table>");
	        } else {
                sf.append("&nbsp;");
	        }
		}

		return sf.toString();
	}

	private static String encode(String text) {
	    String result = text;
	    try {
            result = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported encoding exception.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
	}

	/**
	 * This method takes the name of the map and checks that it is on the selected maps list.
	 *
	 * @param nodeLabel the id of the node to check.
	 * @return boolean true if it is on the list of selected nodes, else false.
	 */
	private View mapOkay(String nodeID) {
	    View match = null;

		try{
		    View view = null;
			int count = vtSelectedViews.size();
		    for (int i = 0; i < count; i++) {
		      	view = (View)vtSelectedViews.elementAt(i);
		    	String sID = view.getId();
		        if( sID.equals(nodeID) ) {
		        	match = view;
		        	break;
		        }
		    }
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return match;
	}

	/**
	 * Calculates all the positions of the node UI elements for the node on the IMAGE map.
	 * This includes: The icon dimension, the label dimension, the '*' (more detail) rollover dimension,
	 * the 'T' (has tags) rollover dimension, the parent view indicator rollover dimension.
	 *
	 * @param node com.compendium.ui.UINode, the node to make these caluclations for.
	 */
	private void calculateCoords(UINode node) {

		transRectangle = null;
		textRectangle = null;
		codeRectangle = null;
		weightRectangle = null;
		labelRectangle = null;

		hasTrans = false;
		hasText = false;
		hasCodes = false;
		hasWeight = false;

		String text = node.getText();

		NodeSummary nodeSumm = node.getNode();
		NodePosition nodePos = node.getNodePosition();

		Insets insets = node.getInsets();
		int dx = insets.left + insets.right;
		int dy = insets.top + insets.bottom;

		Font font = node.getFont();
		FontMetrics fm = node.getFontMetrics(font);

		Rectangle iconR = new Rectangle();
		Rectangle textR = new Rectangle();
		Rectangle viewR = new Rectangle(node.getSize());
		int imageHeight = 0;
		int imageWidth = 0;

		viewR.x = insets.left;
		viewR.y = insets.top;

		Icon icon = node.getIcon();
		if (nodePos.getHideIcon() || icon == null) {
			iconR.width = 0;
			iconR.height = 0;
			iconR.x = viewR.width/2;
			iconR.y = viewR.y+1;
			textR.y = iconR.y  + iconR.height + fm.getAscent();
		}
		else {
			hasIcon = true;
			if ((icon == null) && (text == null))
				return;

			imageHeight = icon.getIconHeight();
			imageWidth = icon.getIconWidth();
			int extraIconWidth = 0;

			iconR.width = imageWidth+1;
			iconR.height = imageHeight+1;

			iconR.x = (viewR.width - imageWidth)/2;
			iconR.y = viewR.y+1;

			hasIcon = true;

			// FOR EXTRA BIT ON SIDE
			String detail = nodeSumm.getDetail();
			detail = detail.trim();
			int type = node.getType();

			AffineTransform trans=new AffineTransform();
			trans.setToScale(node.getScale(), node.getScale());

			Point p1 = new Point(10, 10);
			try { p1 = (Point)trans.transform(p1, new Point(0, 0));}
			catch(Exception e) {System.out.println("can't convert font size (NodeUI.paint 1) \n\n"+e.getMessage()); }
			Font newFont = new Font("Dialog" , Font.BOLD, p1.x);

			FontMetrics sfm = node.getFontMetrics(newFont);

			// DRAW * IF HAS DETAILS
			detail = detail.trim();
			if (nodePos.getShowText() && (type != ICoreConstants.TRASHBIN && !detail.equals("") && !detail.equals(ICoreConstants.NODETAIL_STRING))) {

				hasText = true;
				Point p2 = new Point(18, 18);
				try { p2 = (Point)trans.transform(p2, new Point(0, 0));}
				catch(Exception e) {System.out.println("can't convert font size (NodeUI.paint 2) \n\n"+e.getMessage());}

				Font tFont = new Font("Dialog", Font.BOLD, p2.x);
				FontMetrics rfm = node.getFontMetrics(tFont);
				int twidth = rfm.stringWidth("*")+3;

				int pos = 13;
				int height = 16;
				if (nodePos.getShowSmallIcon()) {
					pos = 11;
					height = 13;
				}

				textRectangle = new Rectangle(iconR.x+iconR.width, (iconR.y-5), twidth, height);
			}

			// DRAW TRANCLUSION NUMBER
			if (nodePos.getShowTrans() &&  (nodeSumm.isInMultipleViews()) && (nodeSumm.getViewCount() > 1)) {
				hasTrans = true;
				int ncount = nodeSumm.getViewCount();
				if (ncount > 1) {

					String count = String.valueOf(ncount);
					int nwidth = sfm.stringWidth(count)+2;

					int extra = 2;
					int back = 8;
					int theight = 14;
					if (nodePos.getShowSmallIcon()) {
						theight = 11;
						extra =4;
						back = 5;
					}

					transRectangle = new Rectangle(iconR.x+iconR.width, iconR.y+(iconR.height-back), nwidth, theight);
				}
			}

			// DRAW VIEW WEIGHT COUNT IF REQUESTED
			/*if  (ProjectCompendium.APP.showWeight && (type == ICoreConstants.MAPVIEW || type == ICoreConstants.LISTVIEW)) {
				hasWeight = true;

				View view  = (View)node.getNode();
				String sCount = "";
				try { sCount = String.valueOf(view.getNodeCount()); }
				catch(Exception ex) { System.out.println("Error: (NodeUI.paint)\n\n"+ex.getMessage());}

				int w = sfm.stringWidth(sCount);
				int h = sfm.getAscent();

				int extra = 2;
				int back = 8;
				if (ProjectCompendium.APP.smallIcons)  {
					extra = 4;
					back = 6;
				}

				weightRectangle = new Rectangle(iconR.x-(w+2), iconR.y+(iconR.height-back), w, h);
			}
			*/

			// DRAW 'T', if has Tags
			try {
				if (nodePos.getShowTags() && node.getNode().getCodeCount() > 0) {
					hasCodes = true;

					int twidth = sfm.stringWidth("T")+2;
					int pos = sfm.getAscent()-3;

					int theight = 14;
					if (nodePos.getShowSmallIcon()) {
						pos = 6;
						theight = 11;
					}

					codeRectangle = new Rectangle(iconR.x-(twidth+2), (iconR.y-3), twidth, theight);
				}
			}
			catch(Exception ex) {
				System.out.println("Error: (NodeUI.calculateDimension) \n\n"+ex.getMessage());
			}

			if (hasText || hasTrans || hasWeight || hasCodes)
				imageWidth += extraIconWidth+1;
		}

		textR.y = iconR.y + iconR.height + node.getIconTextGap();
		iconR.width = imageWidth;

		int wrapWidth = nodePos.getLabelWrapWidth();
		if (wrapWidth <= 0) {
			wrapWidth = ((Model)ProjectCompendium.APP.getModel()).labelWrapWidth;
		}
		wrapWidth = wrapWidth+1; // Needs this for some reason.
		int textWidth = text.length();

		textR.width = textWidth;
		int widestLine = 0;

		textR.height = 0;
		textR.x = dx;

		if (textWidth > wrapWidth) {
			int loop = -1;
			String textLeft = text;

			while ( textLeft.length() > 0 ) {
				loop ++;
				int textLen = textLeft.length();
				int curLen = wrapWidth;
				if (textLen < wrapWidth ) {
					curLen = textLen;
				}
				String nextText = textLeft.substring(0, curLen);
				if (curLen < textLen) {
					int lastSpace = nextText.lastIndexOf(" ");
					if (lastSpace != -1 && lastSpace != textLen) {
						curLen = lastSpace+1;
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}
					else {
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}

					textR.height += fm.getAscent()+fm.getDescent();
				}
				else {
					if (!textLeft.equals("")) {
						textR.height += fm.getAscent()+fm.getDescent();
					}
					nextText = textLeft;
					textLeft = "";
				}

				int thisWidth = fm.stringWidth( nextText );
				if ( thisWidth > widestLine) {
					widestLine = thisWidth;
				}
			}
		}
		else {
			widestLine = fm.stringWidth( text );
			textR.height += fm.getAscent()+fm.getDescent();
		}

		textR.width = widestLine;
		if (iconR.width > textR.width) {
			textR.width = iconR.width;
			textR.x = iconR.x;
		}

		iconRectangle = iconR;
		labelRectangle = textR;
	}

	/**
	 * Creates the HTML for the popup file with the given node detail pages, label, codes, and references.
	 * If not zipping up, writes the HTML to a file, else stores the data for later.
	 *
	 * @param detPath java.lang.String, the path to write the detail file to.
	 * @param htmlPath java.lang.String, the path to put in the HTML - NOT CURRENTLY USED.
	 * @param node com.compendium.datamodel.NodeSummary, the node to create the detail page for.
	 * @param sViewID the view id of the view this node is in.
	 */
	private int createDetailFile(String detPath, String htmlPath, NodeSummary node, View view) {

		// THE NUMBER OF ELEMENTS ADDED TO THE DETAILS BOX
		// USED FOR A ROUGH IDEA OF HOW LARGE TO DRAW THE BOX
		// AND WHETHER TO DRAW THE DETAIL AT ALL
		int itemCount = 1; //label - default

		// create the file
		File newDetailFile = new File(detPath);

		// if the file does not exist, then we must check the sDirectory and then create the file
		if (!newDetailFile.exists()) {

			try {
				StringBuffer detailInfo = new StringBuffer(500);
				detailInfo.append("<html>\n");
				detailInfo.append("<title>details for "+node.getLabel()+"</title>\n");
				detailInfo.append("<style>\n");
				detailInfo.append(".detail { font-family:Arial, Helvetica; font-size:10.0pt; font-weight:normal; }\n");
				detailInfo.append("</style>\n");

				detailInfo.append("<body><A Name='top'></A>");
				detailInfo.append("<a name=\"label\"><span class=\"detail\"><b>Label:</b></span></a><br/>");
				detailInfo.append("<textarea class=\"detail\" readonly name=\"textlabel\" cols=\"70\" rows=\"4\">"+node.getLabel()+"</textarea>");
				detailInfo.append("<br/><br/>");

				// Write Details
				String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
				Vector details = node.getDetailPages(sAuthor);

				detailInfo.append("<a name=\"details\"><span class=\"detail\"><b>Details:</b></span></span></a><br/>");

				int countDetails = details.size();
				boolean bPageAdded = false;
				if (countDetails > 0) {
					detailInfo.append("<textarea class=\"detail\" readonly name=\"textlabel\" cols=\"70\" rows=\"12\">");
				}
				String sNodeDetail = "";
				for (int det=0; det<countDetails; det++) {

					NodeDetailPage page = (NodeDetailPage)details.elementAt(det);
					sNodeDetail = page.getText();
					if (sNodeDetail == null || (sNodeDetail != null && sNodeDetail.equals(ICoreConstants.NODETAIL_STRING) ))
						sNodeDetail = "";

					// get the size of the detail
					int nDetailLength = sNodeDetail.length();
					if(nDetailLength > 0) {
						bPageAdded = true;
						Date creation = page.getCreationDate();
						Date modified = page.getModificationDate();

						//	Node Detail work here
						sNodeDetail = sNodeDetail; // now in <textarea> so dont' need to replace '/n' with '<br>. So code removed.
						sNodeDetail = sNodeDetail.trim();

						//detailInfo.append("<p>");
						detailInfo.append("Page: "+(det+1)+"&nbsp;&nbsp;Entered: "+sdf.format(creation).toString()+"&nbsp;&nbsp;Modified: "+sdf.format(modified).toString()+"\n\n");
						detailInfo.append(sNodeDetail);
						//detailInfo.append("</textarea>");
						detailInfo.append("\n\n");
						//detailInfo.append("<hr/></p>");
					}
				}

				if (countDetails > 0) {
					detailInfo.append("</textarea><br><br>");
					if (bPageAdded) {
						itemCount++;
					}
				}

				// IF THIS IS A REFERENCE AND THE EXPORT IS INCLUDING REFERENCES - ADD ANY SOURCE AND IMAGE REFERENCES
				int type = node.getType();
				String image = "";
				String source = "";

				if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
					image = node.getImage();
					source = node.getSource();
				}
				else if(type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT ||
						type == ICoreConstants.LISTVIEW || type == ICoreConstants.LIST_SHORTCUT) {

					image = node.getImage();
				}

				String sLowercaseSource = source.toLowerCase();
				if (sLowercaseSource.startsWith("www.")) {
					source = "http://"+source;
				}
				String sLowercaseImage = image.toLowerCase();
				if (sLowercaseImage.startsWith("www.")) {
					image = "http://"+image;
				}

				boolean isURLSource = false;
				if (sLowercaseSource.startsWith("http:") || sLowercaseSource.startsWith("https:")) {
					isURLSource = true;
				}
				boolean isURLImage = false;
				if (sLowercaseImage.startsWith("http:") || sLowercaseImage.startsWith("https:")) {
					isURLImage = true;
				}

				if(source != null && UIImages.isImage(source)) {
					File refFile = new File(source);
					if (refFile.exists() && !isURLSource && bIncludeReferences) {
						String imageName = refFile.getName();

						if (bZipUp) {
							htExportFiles.put(refFile.getAbsolutePath(), "images/"+imageName);
							source = "../images/"+imageName;
						}
						else if (!bZipUp) {
							source = "../images/"+imageName;
							File newFile = new File(sDirectory + ProjectCompendium.sFS +"images" + ProjectCompendium.sFS + imageName);
							if (!newFile.exists()) {
								File directory = new File(sDirectory+ ProjectCompendium.sFS+ "images");
								if (!directory.isDirectory()) {
									directory.mkdirs();
								}
								try {
									FileInputStream fis = new FileInputStream(refFile.getAbsolutePath());
									FileOutputStream fos = new FileOutputStream(newFile.getAbsolutePath());
									byte[] data = new byte[fis.available()];
									fis.read(data);
									fos.write(data);
								}
								catch (Exception e) {
									String sMessage = new String("Unable to copy reference file:" + e.getMessage());
									if (!vtMessages.contains(sMessage)) {
										vtMessages.addElement(sMessage);
									}
								}
							}
						}
						detailInfo.append("<span class=\"detail\"><b>Reference:</b><br/>");
						detailInfo.append("<a href=\""+source+"\" target='blank'>"+source+"</a></span><br/><br/>");
					} else if (isURLSource) {
						detailInfo.append("<span class=\"detail\"><b>Reference:</b><br/>");
						detailInfo.append("<a href=\""+source+"\" target='blank'>"+source+"</a></span><br/><br/>");
					}

					itemCount ++;
				} else if(source != null && !source.equals("")) {
					File refFile = new File(source);
					if (refFile.exists() && !isURLSource && bIncludeReferences) {
						String refName = refFile.getName();
						if (bZipUp) {
							htExportFiles.put(refFile.getAbsolutePath(), "references/"+refName);
							source = "../references/"+refName;
						}
						else if (!bZipUp) {
							File newFile = new File(sDirectory + ProjectCompendium.sFS +"references" + ProjectCompendium.sFS + refName);
							source = "../references/" + refName;
							if (!newFile.exists()) {
								File directory = new File(sDirectory+ ProjectCompendium.sFS+ "references");
								if (!directory.isDirectory()) {
									directory.mkdirs();
								}
								try {
									FileInputStream fis = new FileInputStream(refFile.getAbsolutePath());
									FileOutputStream fos = new FileOutputStream(newFile.getAbsolutePath());
									byte[] data = new byte[fis.available()];
									fis.read(data);
									fos.write(data);
								}
								catch (Exception e) {
									String sMessage = new String("Unable to copy reference file:" + e.getMessage());
									if (!vtMessages.contains(sMessage)) {
										vtMessages.addElement(sMessage);
									}
								}
							}
						}
						detailInfo.append("<span class=\"detail\"><b>Reference:</b><br/>");
						detailInfo.append("<a href=\""+source+"\" target='blank'>"+source+"</a></span><br/><br/>");
					} else if (isURLSource) {
						detailInfo.append("<span class=\"detail\"><b>Reference:</b><br/>");
						detailInfo.append("<a href=\""+source+"\" target='blank'>"+source+"</a></span><br/><br/>");
					}

					itemCount ++;
				}

				if (image != null && !image.equals("")) {
					String path = image;
					File imageFile = new File(image);
					if (imageFile.exists() && !isURLImage && bIncludeReferences) {
						String imageName = imageFile.getName();

						if (bZipUp) {
							htExportFiles.put(image, "images/"+imageName);
							image = "../images/"+imageName;
						}
						else if (!bZipUp) {
							image = "../images/"+imageName;
							File newImageFile = new File(image);
							if (!newImageFile.exists()) {
								//then create a directory instance, and see if the directory exists.
								File directory = new File(sDirectory +ProjectCompendium.sFS+ "images");
								if (!directory.isDirectory()) {
									directory.mkdirs();
								}
								try {
									FileInputStream fis = new FileInputStream(path);
									FileOutputStream fos = new FileOutputStream("images"+ProjectCompendium.sFS+imageName);
									byte[] data = new byte[fis.available()];
									fis.read(data);
									fos.write(data);
								}
								catch (Exception e) {
									String sMessage = new String("Unable to create image:" + e.getMessage());
									if (!vtMessages.contains(sMessage)) {
										vtMessages.addElement(sMessage);
									}
								}
							}
						}
						detailInfo.append("<span class=\"detail\"><b>Image:</b></span><br/>");
						detailInfo.append("<a href=\""+image+"\" target='blank'>"+image+"</a><br/><br/>");
					} else if (isURLImage) {
						detailInfo.append("<span class=\"detail\"><b>Image:</b></span><br/>");
						detailInfo.append("<a href=\""+image+"\" target='blank'>"+image+"</a><br/><br/>");
					}

					itemCount ++;
				}

				// Write Codes, if avail for node.
				// Here, we create the enumeration list of codes, via n.getCodes().  If there
				// are no codes the list should be empty.  O/w traverse through this list as long
				// as there are codes...
				boolean first = true;
				for(Enumeration e = node.getCodes(); e.hasMoreElements();) {
					if(first) {
						itemCount ++;
						detailInfo.append("<span class=\"detail\"><a name=\"codes\"><b>Tags:</b></a></span><br/>");
					}
					first = false;
					Code code = (Code)e.nextElement();
	        		detailInfo.append("<span class=\"detail\">- " + code.getName() + "</span><br/>");
	      		}

                if (EXPORT_PROPERTIES.isAddFeedback()) {
                    detailInfo.append("<FORM>");
                    detailInfo.append("<INPUT TYPE=\"button\" VALUE=\"Send feedback\" onClick=\"parent.location=");
                    detailInfo.append("'mailto:");
                    detailInfo.append(encode(EXPORT_PROPERTIES.getFeedbackEmail()));
                    detailInfo.append("?subject=Feedback%20on%20Compendium%20webmap:%20"
                        + encode(view.getLabel()) + "&body=%0A%0A%0A");
                    detailInfo.append("====%20CONTEXT%20(DO%20NOT%20MODIFY%20BELOW%20THIS%20LINE)%20====%0A");
                    detailInfo.append("Project:%20" + encode(ProjectCompendium.APP.sFriendlyName) + "%0A");
                    detailInfo.append("View:%20" + encode(view.getLabel()) + "%0A");

                    detailInfo.append("Node:%20" + encode(node.getLabel()) + "%0A");
                    detailInfo.append("ID:%20comp://" + encode(view.getId()) + "/" + encode(node.getId())+ "%0A");
                    detailInfo.append("'\"></FORM>");
                }

	      		detailInfo.append("</body></html>");

	      		// Don't create a details file if it only contains the node label.
	      		if (itemCount > 1 || !bNoDetailPopup) {
					if (!bZipUp) {
						FileWriter fw = new FileWriter(detailPath1);
			      		fw.write(detailInfo.toString());
			      		fw.close();
					}
					else {
						File file = new File(detailPath1);
						htCreatedFiles.put("details/"+file.getName(), detailInfo.toString());
					}
				}

			}
			catch (Exception e) {
				e.printStackTrace();
				String sMessage = new String("Unable to create detail file:" + e.getMessage());
				if (!vtMessages.contains(sMessage)) {
					vtMessages.addElement(sMessage);
				}
				//ProjectCompendium.APP.displayError("Unable to create detail file:" + e.getMessage());
			}
		}

		return itemCount;
	}

	/**
	 * Close headers and FileWrite the menu page out to file if not zipping up.
	 * If zipping up store the file data for later.
	 *
	 * @return boolean, currently not used, always returns true.
	 */
	public boolean writeMenuPage() {

		sMenuPage.append("<html><head>\n");
		sMenuPage.append("<title>Compendium Web Map Table of Contents</title>\n");
		sMenuPage.append("<style>\n");
		sMenuPage.append(".offme{font-family:Arial, Helvetica; font-size:8.0pt; color: navy;}\n");
		sMenuPage.append("</style>\n");
		sMenuPage.append("</head>\n");
		sMenuPage.append("<body>\n");
		sMenuPage.append("<table>\n");

		if (bSortMenu) {
			vtMenuItems = CoreUtilities.sortList(vtMenuItems);
		}

		View oStartView = null;
		int count = vtMenuItems.size();
		String sFileName="";
		for (int i=0; i<count; i++) {
			View view = (View)vtMenuItems.elementAt(i);
			if (i == 0) {
				oStartView = view;
			}
		    int type = view.getType();
			sFileName = this.createFileName(view);
		    String szIcon = UIImages.getSmallPath(type);
			File tempFile = new File(szIcon);
			String szIconName = tempFile.getName();
		    createImageFile(szIcon, sDirectory + ProjectCompendium.sFS+"images"+ProjectCompendium.sFS+szIconName);
		    sMenuPage.append("<tr><td align=\"left\" nowrap>");
		    sMenuPage.append("<a id=\""+view.getId()+"\" href=\""+sFileName+"\" target=\"mapFrame\" class=\"offme\" style=\"cursor: hand\"><img border=\"0\" src='images/" + szIconName+"'>&nbsp;"+view.getLabel()+"</a><br>");
		    sMenuPage.append("</td></tr>\n");
		}

		sMenuPage.append("</table>\n");
		sMenuPage.append("</body></html>\n");

		if (!bZipUp) {
			try{
				FileWriter fw = new FileWriter(sDirectory+ProjectCompendium.sFS+"TOC.html");
				fw.write(sMenuPage.toString());
				fw.close();
			}
			catch(IOException e){System.out.println("there was a problem writing TOC");}
		}
		else {
			htCreatedFiles.put("TOC.html", sMenuPage.toString());
		}

		String sStartPage = "";
		if (oStartView == null) {
			String szStartPage = "<html><head><title>StartPage</title></head><body>";
			szStartPage += "<font size='6'>Links to views are provided in the lefthand frame.</font>";
			szStartPage += "</body></html>";

			if (!bZipUp) {
				try{
					FileWriter fw = new FileWriter(sDirectory+ProjectCompendium.sFS+"StartPage.html");
					fw.write(szStartPage);
					fw.close();
				}
				catch(IOException e){System.out.println("there was a problem writing Start Page");}
			}
			else {
				htCreatedFiles.put("StartPage.html", szStartPage);
			}
			sStartPage = "StartPage.html";
		} else {
			sStartPage = this.createFileName(oStartView);
		}

		String szFinalPage = "";
		szFinalPage += "<html>\n";
		szFinalPage += "<head>\n";
		szFinalPage += "<title>"+sBackupName+"</title>\n\n";
		szFinalPage += "<script Language=\"JavaScript1.2\">\n";
		szFinalPage += "<!--\n";
		szFinalPage += "window.onload = function(){\n";
		szFinalPage += "\tvar specificNodeID = unescape(parent.document.location.hash.substring(1));	\n";
		szFinalPage += "\t//if required, locate map and open\n";
		szFinalPage += "\tif (specificNodeID != null && specificNodeID !=\"\" ){\n";
		szFinalPage += "\t\tvar index = specificNodeID.lastIndexOf('_');\n";
		szFinalPage += "\t\tif (index != -1) {\n";
		szFinalPage += "\t\t\tvar mapID = specificNodeID.substring(index+1, specificNodeID.length);\n";
		szFinalPage += "\t\t\tvar mapAnchor = parent.menuFrame.document.getElementById(mapID);\n";
		szFinalPage += "\t\t\tif (mapAnchor != null) {\n";
		szFinalPage += "\t\t\t\tparent.mapFrame.location = mapAnchor.href;\n";
		szFinalPage += "\t\t\t}\n";
		szFinalPage += "\t\t}\n";
		szFinalPage += "\t}\n";
		szFinalPage += "}\n";
		szFinalPage += "//-->\n";
		szFinalPage += "</script>\n\n";
		szFinalPage += "</head>\n";
		szFinalPage += "<frameset cols='1%,*'>\n";
		szFinalPage += "<frame name=\"menuFrame\" id=\"menuFrame\" src='TOC.html'>\n";
		szFinalPage += "<frame name=\"mapFrame\" id=\"mapFrame\" src=\""+sStartPage+"\">\n";
		szFinalPage += "</frameset>\n";
		szFinalPage += "</html>\n";

		if (!bZipUp) {
			try{
				FileWriter fw = new FileWriter(sDirectory + ProjectCompendium.sFS+sBackupName+".html");
				fw.write(szFinalPage);
				fw.close();
			}
			catch(Exception e){
				System.out.println("Problem printing file page");
			}
		}
		else {
			htCreatedFiles.put(sBackupName+".html", szFinalPage);
		}

		return true;
	}

	/**
	 * Move the passed image with the given 'path' to the export 'nPath'.
	 * Unless zipping up when it is stored for later.
	 *
	 * @param path the current image path.
	 * @param nPath the path to move the image to.
	 */
	private void createImageFile(String path, String nPath) {

	  	File newImageFile = new File(nPath);

		if (bZipUp) {
			if (!newImageFile.exists()) {
				File file = new File(nPath);
				htExportFiles.put(path, "images/"+file.getName());
			}
		}
		else {

			File directory = new File(sDirectory +ProjectCompendium.sFS+ "images");
			if (!directory.isDirectory()) {
				directory.mkdirs();
			}

			if (!newImageFile.exists()) {
		    	try {
					if ( (new File(path)).exists() ) {
			      		FileInputStream fis = new FileInputStream(path);
			      		FileOutputStream fos = new FileOutputStream(nPath);
		    	  		byte[] data = new byte[fis.available()];
		      			fis.read(data);
		      			fos.write(data);
						fis.close();
						fos.close();
					}
					else {
						String sMessage = new String("File does not exist: " + path);
						if (!vtMessages.contains(sMessage)) {
							vtMessages.addElement(sMessage);
						}
					}
	   		 	}
				catch (Exception e) {
	      			System.out.println(e.getMessage());
	    		}
			}
	  	}
	}

	/**
	 * Zip up the export and its associated directories.
	 * @param fileName the name of the zip file to export to.
	 */
	private void zipUpExport(String fileName) {

		try {
			int BUFFER = 2048;
			BufferedInputStream origin = null;
			FileInputStream fi = null;

			ZipOutputStream out = null;

			File exportFile = new File(fileName);
			if (exportFile.exists()) {
				out = addFilesFromExistingZip(exportFile);
			} else {
				FileOutputStream dest = new FileOutputStream(exportFile.getAbsolutePath());
				out = new ZipOutputStream(new BufferedOutputStream(dest));
				out.setMethod(ZipOutputStream.DEFLATED);
			}

			byte data2[] = new byte[BUFFER];
			ZipEntry entry = null;
			int count = 0;

			// ADD CREATED FILES
			for (Enumeration e = htCreatedFiles.keys(); e.hasMoreElements() ;) {
				String sFilePath = (String)e.nextElement();
				String sData = (String)htCreatedFiles.get(sFilePath);

				try {
					entry = new ZipEntry(sFilePath);
					out.putNextEntry(entry);
					int len = sData.length();
					byte data3[] = sData.getBytes();
					out.write(data3, 0, len);
				}
				catch (Exception ex) {
					System.out.println("Unable to zip up html export: \n\n"+sFilePath+"\n\n"+ex.getMessage());
				}
			}

			nCount += 1;
			oProgressBar.setValue(nCount);
			oProgressDialog.setStatus(nCount);

			// ADD RESOURCES
			count = 0;
			for (Enumeration e = htExportFiles.keys(); e.hasMoreElements() ;) {
				String sOldFilePath = (String)e.nextElement();
				String sNewFilePath = (String)htExportFiles.get(sOldFilePath);

				try {
					fi = new FileInputStream(sOldFilePath);
					origin = new BufferedInputStream(fi, BUFFER);

					entry = new ZipEntry(sNewFilePath);
					out.putNextEntry(entry);

					while((count = origin.read(data2, 0, BUFFER)) != -1) {
						out.write(data2, 0, count);
					}
					origin.close();
				}
				catch (Exception ex) {
					System.out.println("Unable to zip up html export: \n\n"+sOldFilePath+"\n\n"+ex.getMessage());
				}
			}

			nCount += 1;
			oProgressBar.setValue(nCount);
			oProgressDialog.setStatus(nCount);

			// ADD ANY IMAGE MAPS
			count = 0;
			for (Enumeration e = htMapFiles.keys(); e.hasMoreElements() ;) {
				String sOldFilePath = (String)e.nextElement();
				String sNewFilePath = (String)htMapFiles.get(sOldFilePath);

				try {
					fi = new FileInputStream(sOldFilePath);
					origin = new BufferedInputStream(fi, BUFFER);

					entry = new ZipEntry(sNewFilePath);
					out.putNextEntry(entry);

					while((count = origin.read(data2, 0, BUFFER)) != -1) {
						out.write(data2, 0, count);
					}
					origin.close();
					File file = new File(sOldFilePath);
					file.delete();
				}
				catch (Exception ex) {
					System.out.println("Unable to zip up html export: \n\n"+sOldFilePath+"\n\n"+ex.getMessage());
				}
			}

			nCount += 1;
			oProgressBar.setValue(nCount);
			oProgressDialog.setStatus(nCount);

			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * For the Export with Outline, Maps and XML all in one, where the zip file will already exist from Outline export.
	 * Create a temp file and add the existing zipped files to the new zip.
	 * @param zipFile the name of the existing zip file.
	 * @throws IOException
	 * @return a reference to the new zip still open to finish adding to.
	 */
	public ZipOutputStream addFilesFromExistingZip(File zipFile) throws IOException {

		File tempFile = File.createTempFile(zipFile.getName(), null);

        // DELETE IT OR YOU CAN'T RENAME EXISTING FILE TO IT.
		tempFile.delete();
		boolean renameOk=zipFile.renameTo(tempFile);
		if (!renameOk) {
			throw new RuntimeException("could not rename the file "+zipFile.getAbsolutePath()+" to "+tempFile.getAbsolutePath());
		}

		byte[] BUFFER = new byte[2048];
		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		FileOutputStream dest = new FileOutputStream(zipFile.getAbsolutePath());
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		out.setMethod(ZipOutputStream.DEFLATED);

		// ADD ONLY THOSE FILES FROM THE EXISTING ZIP WHICH ARE NOT IN THE LIST OF NEW RESOURCE FILES TO ADD
		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			boolean notInFiles = true;

			for (Enumeration e = htExportFiles.keys(); e.hasMoreElements() ;) {
				String sOldFilePath = (String)e.nextElement();
				String sNewFilePath = (String)htExportFiles.get(sOldFilePath);
				if (new File(sNewFilePath).getName().equals(name)) {
					notInFiles = false;
					break;
				}
			}

			if (notInFiles) {
				out.putNextEntry(new ZipEntry(name));
				int len;
				while ((len = zin.read(BUFFER)) > 0) {
					out.write(BUFFER, 0, len);
				}
			}
			entry = zin.getNextEntry();
		}
		zin.close();
		tempFile.delete();

		return out;
	}

	public static void showTrace(String msg)
	{
	  //if (msg.length() > 0) System.out.println(msg);
	  System.out.println(
			   new Throwable().getStackTrace()[1].getLineNumber() +
			   " " + new Throwable().getStackTrace()[1].getFileName() +
			   " " + new Throwable().getStackTrace()[1].getMethodName() +
				   " " + msg);
	}
}