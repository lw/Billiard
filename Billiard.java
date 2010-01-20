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
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	public static final int BALLS = 11;
	public static Ball ball[] = new Ball[BALLS];
	
	private double next_collision;
	private Ball first;
	private Ball second;
	
	private static boolean paused = true;
	private static boolean queued_collision_update = false;
	
	// Constructor
	public Billiard () {
		super ();
		
		setOpaque (true);
		setBackground (new Color (255, 255, 255));		
		
		double init_radius = 25;
		double radius_step = 2;
		double init_mass = 5;
		double mass_step = 4;
		
		int rows = (int)Math.sqrt (BALLS);
		int columns = (int)Math.ceil ((double)BALLS / rows);
		int first_row = BALLS - (rows - 1) * columns;
		int count = 0;
		
		for (int i = 0; i < rows; i++) {
			int j = 0;
			if (i == 0)
				j = columns - first_row;
			
			for (; j < columns; j++) {
				ball[count++] = new Ball ((j+0.5) * WIDTH / columns,
				                          (i+0.5) * HEIGHT / rows,
				                          init_radius,
				                          init_mass,
				                          new Speed (Math.random()*8-4, Math.random()*8-4));
				init_radius += radius_step;
				init_mass += mass_step;
			}
		}
	}
	
	// Draw
	public void paintComponent (Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint (RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2d.setRenderingHint (RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		super.paintComponent (g);
		
		if (!paused) {
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
			}
		}
		
		for (int i = 0; i < BALLS; i++) {
			ball[i].paint (g2d);
		}
		
		/*
		g2d.setColor (new Color (0, 0, 0));
		if (next_collision < 1000 && first != null && second != null)
			g2d.drawLine ((int)(first.getX() + first.getSpeed().getX() * next_collision),
			              (int)(first.getY() + first.getSpeed().getY() * next_collision),
			              (int)(second.getX() + second.getSpeed().getX() * next_collision),
			              (int)(second.getY() + second.getSpeed().getY() * next_collision));
		*/
		
		if (queued_collision_update)
			collision_update ();
	}
	
	// Pause
	public static void pause () {
		paused = true;
	}
	
	public static void play () {
		paused = false;
	}
	
	public static void toggle_play_pause () {
		paused = !paused;
	}
	
	public static boolean is_paused () {
		return paused;
	}
	
	// Update
	public static void queue_collision_update () {
		queued_collision_update = true;
	}
	
	public void collision_update () {
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
		
		queued_collision_update = false;
	}
}
