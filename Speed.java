/*  
 *  Copyright (C) 2010  Luca Wehrstedt
 *
 *  This file is released under the GPLv2
 *  Read the file 'COPYING' for more information
 */

class Speed {
	private double x = 0;
	private double y = 0;
	
	public Speed (double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX () {
		return x;
	}
	
	public double getY () {
		return y;
	}
	
	public double getComponent (double theta) {
		return Math.cos (theta) * x + Math.sin (theta) * y;
	}
	
	public void addX (double speed) {
		x += speed;
	}
	
	public void addY (double speed) {
		y += speed;
	}
	
	public void addComponent (double theta, double speed) {
		x += Math.cos (theta) * speed;
		y += Math.sin (theta) * speed;
	}
}
