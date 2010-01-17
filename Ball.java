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
	private Color color;
	
	private double radius;
	private double x;
	private double y;
	private Speed speed;
	
	private Billiard pool;
	
	private static int color_count = 0;
	private static Color colors[] = {Color.RED};
	
	// Constructor
	public Ball (Billiard pool, double x, double y, double radius, Speed speed) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = colors[color_count++%1];
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
	private int count = 0;
	private boolean active = true;
	private boolean normalizing = true;
	
	public void paint (Graphics2D g) {
		if (active || normalizing) {
			if (!normalizing) {
				count ++;
				if (count >= 40)
					normalizing = true;
			}
			else {
				count --;
				if (count <= 0)
					normalizing = false;
			}
		}
		g.setColor (new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
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
