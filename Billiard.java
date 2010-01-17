/*  
 *  Copyright (C) 2010  Luca Wehrstedt
 *
 *  This file is released under the GPLv2
 *  Read the file 'COPYING' for more information
 */

import java.util.Iterator;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Billiard extends JPanel implements ActionListener {
	// Members
	private static final int BALLS = 4;
	private Ball ball[];
	
	private double next_collision;
	private Ball first;
	private Ball second;
	
	// Constructor
	public Billiard () {
		super ();
		
		setOpaque (true);
		setBackground (new Color (66, 101, 17));
		
		
		ball = new Ball[BALLS];
		for (int i = 0; i < BALLS; i++) {
			
			ball[i] = new Ball ((i + 0.5) * BilliardWindow.WIDTH / BALLS, BilliardWindow.HEIGHT / 2.0 + i*15, 30+i*15, new Color (255, 0, 0), this);
		}
	}
	
	// Draw
	public void paintComponent (Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint (RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2d.setRenderingHint (RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		super.paintComponent (g);
		
		double passed = 0.0;
		while (passed + next_collision < 1.0) {
			for (int i = 0; i < BALLS; i++) {
				if (ball[i] == first) {
					ball[i].collide (second, next_collision);
				}
				else if (ball[i] != second) {
					ball[i].move (next_collision);
				}
			}
			passed += next_collision;
			update_collision ();
		}
		next_collision += passed;
		next_collision -= 1.0;
		for (int i = 0; i < BALLS; i++) {
			ball[i].move (1.0 - passed);
			ball[i].paint (g2d);
		}
		
		
		g2d.setColor (new Color (0, 0, 0));
		if (next_collision < 1000)
			g2d.drawLine ((int)(first.getX() + first.getSpeed().getX() * next_collision),
			              (int)(first.getY() + first.getSpeed().getY() * next_collision),
			              (int)(second.getX() + second.getSpeed().getX() * next_collision),
			              (int)(second.getY() + second.getSpeed().getY() * next_collision));
		
	}
	
	// Update
	public void update_collision () {
		next_collision = Double.POSITIVE_INFINITY;
		for (int i = 0; i < BALLS-1; i++) {
			for (int j = i+1; j < BALLS; j++) {
				double minimo = ball[i].next_collision (ball[j]);
				if (minimo < next_collision) {
					next_collision = minimo;
					first = ball[i];
					second = ball[j];
				}
			}
		}
	}
	
	// New frame
	public void actionPerformed (ActionEvent e) {
		repaint ();
	}
}
