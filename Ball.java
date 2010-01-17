/*  
 *  Copyright (C) 2010  Luca Wehrstedt
 *
 *  This file is released under the GPLv2
 *  Read the file 'COPYING' for more information
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

import java.awt.geom.AffineTransform;

public class Ball {
	private Path2D shape;
	
	private double radius;
	private double x;
	private double y;
	private Speed speed;
	
	private Billiard pool;
	private boolean active = false;
	private boolean light = true;
	
	public static final Color lighter[] = {new Color (239, 41, 41), new Color (114, 159, 207),
	                                       new Color (252, 234, 79), new Color (252, 175, 62),
	                                       new Color (138, 226, 52), new Color (173, 127, 168),
	                                       new Color (136, 138, 133), new Color (233, 185, 110)};
	public static final Color colors[] = {new Color (204, 0, 0), new Color (52, 101, 164),
	                                      new Color (237, 212, 0), new Color (245, 121, 0),
	                                      new Color (115, 210, 22), new Color (117, 80, 123),
	                                      new Color (85, 87, 83), new Color (193, 125, 17)};
	public static final Color darker[] = {new Color (164, 0, 0), new Color (32, 74, 135),
	                                      new Color (196, 160, 0), new Color (206, 92, 0),
	                                      new Color (78, 154, 6), new Color (92, 53, 102),
	                                      new Color (46, 52, 54), new Color (143, 89, 2)};
	
	private static final int COLOR_DELAY = 20;
	private static int color_count = 0;
	private int color = 0;
	private int count = 0;
	
	
	// Constructor
	public Ball (Billiard pool, double x, double y, double radius, Speed speed) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color_count ++ % 8;
		this.shape = new Path2D.Double (new Ellipse2D.Double (x - radius, y - radius, 2 * radius, 2 * radius));
		this.speed = speed;
		this.pool = pool;
	}
	
	// Getters and Setters
	public Speed getSpeed () {
		return speed;
	}
	
	public double getX () {
		return x;
	}
	
	public double getY () {
		return y;
	}
	
	public double getRadius () {
		return radius;
	}
	
	public int getColorId () {
		return color;
	}
	
	public void toggleActive () {
		active = !active;
	}
	
	// Move
	public void move (double time) {
		move (speed.getX () * time, speed.getY () * time);
	}
	
	public void move (double x, double y) {
		AffineTransform affine = new AffineTransform ();
		affine.translate (-this.x, -this.y);
		
		this.x += x;
		this.y += y;
		
		if (this.x < radius) {
			this.x = 2 * radius - this.x;
			speed.addX (-2 * speed.getX ());
			pool.queue_collision_update ();
		}
		
		if (this.x > BilliardWindow.WIDTH - radius) {
			this.x = 2 * (BilliardWindow.WIDTH - radius) - this.x;
			speed.addX (-2 * speed.getX ());
			pool.queue_collision_update ();
		}
		
		if (this.y < radius) {
			this.y = 2 * radius - this.y;
			speed.addY (-2 * speed.getY ());
			pool.queue_collision_update ();
		}
		
		if (this.y > BilliardWindow.HEIGHT - radius) {
			this.y = 2 * (BilliardWindow.HEIGHT - radius) - this.y;
			speed.addY (-2 * speed.getY ());
			pool.queue_collision_update ();
		}
		
		affine.translate (this.x, this.y);
		shape.transform (affine);
	}
	
	// Paint
	public void paint (Graphics2D g) {
		if (active && count == 0) {
			if (light)
				count ++;
			else
				count --;
		}
		
		if (count != 0) {
			Color base = colors[color];
			Color reference;
			
			if (count < 0)
				reference = darker[color];
			else
				reference = lighter[color];
			
			if (light) {
				count ++;
				if (count >= COLOR_DELAY)
					light = false;
			}
			else {
				count --;
				if (count <= -COLOR_DELAY)
					light = true;
			}
			
			int red = base.getRed() + Math.abs(count) * (reference.getRed() - base.getRed()) / COLOR_DELAY;
			int green = base.getGreen() + Math.abs(count) * (reference.getGreen() - base.getGreen()) / COLOR_DELAY;
			int blue = base.getBlue() + Math.abs(count) * (reference.getBlue() - base.getBlue()) / COLOR_DELAY;
			
			g.setColor (new Color (red, green, blue));
		}
		else {
			g.setColor (colors[color]);
		}

		g.fill (shape);
	}
	
	// Next collision
	public double next_collision (Ball next) {
		double pos_x = x - next.getX ();
		double pos_y = y - next.getY ();
		double vel_x = speed.getX () - next.getSpeed ().getX ();
		double vel_y = speed.getY () - next.getSpeed ().getY ();
		
		if (vel_x == 0 && vel_y == 0)
			return Double.POSITIVE_INFINITY;
//			return 1000.0;
		
		double a = vel_x * vel_x + vel_y * vel_y;
		double b = 2 * vel_x * pos_x + 2 * vel_y * pos_y;
		double c = pos_x * pos_x + pos_y * pos_y - (radius + next.getRadius ()) * (radius + next.getRadius ());
		double delta = b * b - 4 * a * c;
		
		if (delta < 0)
			return Double.POSITIVE_INFINITY;
//			return 2000.0;
		
		double inizio = (- b - Math.sqrt (delta)) / (2 * a);
		double fine = (- b + Math.sqrt (delta)) / (2 * a);

		if (fine < 0)
			return Double.POSITIVE_INFINITY;
//			return 3000.0;
		
//		System.out.println("\t\t\tinizio: "+inizio);
		
		if (inizio < (inizio - fine) / 2) // Large approximation, but it should work
			return Double.POSITIVE_INFINITY;
//			return 4000.0;
		
		if (inizio < 0)
			return 0.0;
		
		return inizio;
	}
	
	// Collide
	public void collide (Ball next, double time) {
		move (speed.getX () * time, speed.getY () * time);
		next.move (next.getSpeed ().getX () * time, next.getSpeed ().getY () * time);
		
		double theta = Math.atan2 (next.getY () - y, next.getX () - x);
		
		double first = speed.getComponent (theta);
		double second = next.getSpeed ().getComponent (theta);
		
		speed.addComponent (theta, -first + second);
		next.getSpeed ().addComponent (theta, -second + first);
		
//		move (speed.getX () * (1.0 - time), speed.getY () * (1.0 - time));
//		next.move (next.getSpeed ().getX () * (1.0 - time), next.getSpeed ().getY () * (1.0 - time));
	}
}
