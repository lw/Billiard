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

public class Billiard extends JPanel {
	// Members
	public static final int BALLS = 2;
	public static Ball ball[];
	
	private double next_collision;
	private Ball first;
	private Ball second;
	
	private static boolean queued_collision_update = false;
	
	// Constructor
	public Billiard () {
		super ();
		
		setOpaque (true);
//		setBackground (new Color (66, 101, 17));
		setBackground (new Color (255, 255, 255));		
		
		ball = new Ball[BALLS];
		double start = 0.0;
		for (int i = 0; i < BALLS; i++) {
			ball[i] = new Ball (//(i + 0.5) * BilliardWindow.WIDTH / BALLS,
			                    start + 20+i*3 + 5,
			                    BilliardWindow.HEIGHT / 2.0 + i * 2,
			                    20+i*3,
			                    new Speed (4, 0));
			start += 2 * (20+i*3) + 5;
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
			collision_update ();	
		}
		next_collision += passed;
		next_collision -= 1.0;
		for (int i = 0; i < BALLS; i++) {
			ball[i].move (1.0 - passed);
			ball[i].paint (g2d);
		}
		
		/*
		g2d.setColor (new Color (0, 0, 0));
		if (next_collision < 1000)
			g2d.drawLine ((int)(first.getX() + first.getSpeed().getX() * next_collision),
			              (int)(first.getY() + first.getSpeed().getY() * next_collision),
			              (int)(second.getX() + second.getSpeed().getX() * next_collision),
			              (int)(second.getY() + second.getSpeed().getY() * next_collision));
		*/
		
		if (queued_collision_update)
			collision_update ();
	}
	
	// Update
	public static void queue_collision_update () {
		queued_collision_update = true;
	}
	
	public void collision_update () {
//		System.out.println ("\nStarting new collision calculation");
		next_collision = Double.POSITIVE_INFINITY;
		for (int i = 0; i < BALLS-1; i++) {
			for (int j = i+1; j < BALLS; j++) {
				double minimo = ball[i].next_collision (ball[j]);
//				System.out.println ("Between "+(i+1)+" and "+(j+1)+"	"+minimo);
				if (minimo < next_collision) {
					next_collision = minimo;
					first = ball[i];
					second = ball[j];
				}
			}
		}
		/*
		if (next_collision < Double.POSITIVE_INFINITY) {
			System.out.println ((int)next_collision+"	to collision between "+first.getRadius()+" and "+second.getRadius());
		}
		else {
			System.out.println ("No collision");
		}
		*/
		queued_collision_update = false;
	}
}
