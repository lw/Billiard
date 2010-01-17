/*  
 *  Copyright (C) 2010  Luca Wehrstedt
 *
 *  This file is released under the GPLv2
 *  Read the file 'COPYING' for more information
 */

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;

public class BilliardWindow extends JFrame {
	// Members
	private Billiard content;
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	// Constructor
	public BilliardWindow () {
		super ();
		
		setTitle ("Billiard");
		setResizable (false);
		pack ();
		
		setSize (WIDTH + getInsets ().left + getInsets ().right,
		         HEIGHT + getInsets ().top + getInsets ().bottom);
		
		content = new Billiard ();
		getContentPane ().add (content);

//		getContentPane ().addMouseListener (content);
//		getContentPane ().addMouseMotionListener (content);
		
		Timer timer = new Timer (10, content);
		timer.start ();
	}
}
