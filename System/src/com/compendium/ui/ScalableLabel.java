package com.compendium.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ScalableLabel extends JLabel {
	
	/**
	 *   Class to draw scaled images.
	 */
	private static final long serialVersionUID = 1L;
	private double m_zoom=0;
	private ImageIcon image;
	public void setZoom (double zoom){
		if (m_zoom != zoom){
		  m_zoom = zoom;
		  this.revalidate();
		  this.repaint();
		}
	}
	public void setIcon(Icon icon){
		super.setIcon(icon);
		image =  (ImageIcon) icon;
	}
	 /** 
     * This method is overriden to draw the image 
     * and scale the graphics accordingly 
     */ 
    public void paintComponent(Graphics grp)  
    {  
    	 Graphics2D g = (Graphics2D) grp;
        if (m_zoom != 1){
            //scale the graphics to get the zoom effect 
          AffineTransform trans = AffineTransform.getScaleInstance(m_zoom, m_zoom);
          g.drawImage(image.getImage(), trans, null) ;
        } else {
          super.paintComponent(grp);
        }
    } 
      
}
