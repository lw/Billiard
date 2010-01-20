/*  
 *  Copyright (C) 2010  Luca Wehrstedt
 *
 *  This file is released under the GPLv2
 *  Read the file 'COPYING' for more information
 */

import java.util.Calendar;

import java.io.File;
import java.io.FileInputStream;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Path2D;
import java.awt.geom.AffineTransform;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

public class Overlay extends JPanel implements MouseListener, MouseMotionListener {
	private Path2D shapes[];
	private Path2D selector;
	private Font font;
	
	private int active_ball = 0;
	
	private boolean is_dragged = false;
	private int drag_start_x;
	private int drag_start_y;
	
	private int second = -1;
	private int count = 0;
	private int fps = 0;
	
	private double angle = 0;
	
	// Utility
	private void drawStringFromCenter (Graphics2D g, String str, double x, double y) {
		Rectangle2D bounds = g.getFont ().getStringBounds (str, g.getFontRenderContext ());
		g.drawString (str, (int)(x - bounds.getWidth() / 2), (int)(y + bounds.getHeight() / 2));
	}
	
	private void createFont () {
		try {
			File file = new File ("dejavu.ttf");
			FileInputStream fis = new FileInputStream (file);
			font = Font.createFont (Font.TRUETYPE_FONT, fis).deriveFont (12f);
		}
		catch (java.io.IOException e) {
			System.out.println ("I/O error");
			font = new Font (Font.SANS_SERIF, Font.PLAIN, 12);
		}
		catch (java.awt.FontFormatException e) {
			System.out.println ("File \"dejavu.ttf\" not valid");
			font = new Font (Font.SANS_SERIF, Font.PLAIN, 12);
		}
	}
	
	// Draw selector
	private void drawSelector (Graphics2D g) {
		Stroke old_stroke = g.getStroke ();
		
		g.setStroke (new BasicStroke (4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		int color_id = Billiard.ball[active_ball].getColorId();
		int red = (int)((Ball.lighter[color_id].getRed() + Ball.darker[color_id].getRed()) / 2 +
		                Math.sin (angle * 2) * (Ball.lighter[color_id].getRed() - Ball.darker[color_id].getRed()) / 2);
		int green = (int)((Ball.lighter[color_id].getGreen() + Ball.darker[color_id].getGreen()) / 2 +
		                  Math.sin (angle * 2) * (Ball.lighter[color_id].getGreen() - Ball.darker[color_id].getGreen()) / 2);
		int blue = (int)((Ball.lighter[color_id].getBlue() + Ball.darker[color_id].getBlue()) / 2 +
		                 Math.sin (angle * 2) * (Ball.lighter[color_id].getBlue() - Ball.darker[color_id].getBlue()) / 2);
		g.setColor (new Color (red, green, blue));
		
		double x = Billiard.ball[active_ball].getX ();
		double y = Billiard.ball[active_ball].getY ();
		double radius = Billiard.ball[active_ball].getRadius ();
		
		double start = radius + 20 + 10 * Math.sin (angle * 2);
		double end = radius + 40 + 10 * Math.sin (angle * 2);
		
		angle += Math.PI / 150;
		
		g.draw (new Line2D.Double (x + Math.cos (angle) * start, y + Math.sin (angle) * start,
		                           x + Math.cos (angle) * end, y + Math.sin (angle) * end));
		g.draw (new Line2D.Double (x + Math.cos (angle + Math.PI / 2) * start, y + Math.sin (angle + Math.PI / 2) * start,
		                           x + Math.cos (angle + Math.PI / 2) * end, y + Math.sin (angle + Math.PI / 2) * end));
		g.draw (new Line2D.Double (x + Math.cos (angle + Math.PI) * start, y + Math.sin (angle + Math.PI) * start,
		                           x + Math.cos (angle + Math.PI) * end, y + Math.sin (angle + Math.PI) * end));
		g.draw (new Line2D.Double (x + Math.cos (angle + 3 * Math.PI / 2) * start, y + Math.sin (angle + 3 * Math.PI / 2) * start,
		                           x + Math.cos (angle + 3 * Math.PI / 2) * end, y + Math.sin (angle + 3 * Math.PI / 2) * end));
		
		g.setStroke (old_stroke);
	}
	
	// Constructor
	public Overlay () {
		super ();
		
		setOpaque (false);
		setBackground (new Color (0, 0, 0, 0));
		
		createFont ();
		
		shapes = new Path2D.Double [17];
		
		shapes[0] = new Path2D.Double (new RoundRectangle2D.Double (10, 10, 160, 160, 10, 10));
		
		shapes[1] = new Path2D.Double (new RoundRectangle2D.Double (20, 20, 45, 15, 7.5, 7.5));
		shapes[2] = new Path2D.Double (new RoundRectangle2D.Double (115, 20, 45, 15, 7.5, 7.5));
		
		shapes[3] = new Path2D.Double (new Rectangle2D.Double (45, 60, 90, 20));
		
		shapes[4] = new Path2D.Double ();
		shapes[4].moveTo (30, 60);
		shapes[4].lineTo (20, 70);
		shapes[4].lineTo (30, 80);
		shapes[4].closePath ();
		
		shapes[5] = new Path2D.Double ();
		shapes[5].moveTo (150, 60);
		shapes[5].lineTo (160, 70);
		shapes[5].lineTo (150, 80);
		shapes[5].closePath ();
		
		shapes[6] = new Path2D.Double ();
		shapes[6].moveTo (30, 100);
		shapes[6].lineTo (20, 110);
		shapes[6].lineTo (30, 120);
		shapes[6].closePath ();
		
		shapes[7] = new Path2D.Double ();
		shapes[7].moveTo (75, 100);
		shapes[7].lineTo (85, 110);
		shapes[7].lineTo (75, 120);
		shapes[7].closePath ();
		
		shapes[8] = new Path2D.Double ();
		shapes[8].moveTo (105, 100);
		shapes[8].lineTo (95, 110);
		shapes[8].lineTo (105, 120);
		shapes[8].closePath ();
		
		shapes[9] = new Path2D.Double ();
		shapes[9].moveTo (150, 100);
		shapes[9].lineTo (160, 110);
		shapes[9].lineTo (150, 120);
		shapes[9].closePath ();
		
		shapes[10] = new Path2D.Double ();
		shapes[10].moveTo (30, 140);
		shapes[10].lineTo (20, 150);
		shapes[10].lineTo (30, 160);
		shapes[10].closePath ();
		
		shapes[11] = new Path2D.Double ();
		shapes[11].moveTo (75, 140);
		shapes[11].lineTo (85, 150);
		shapes[11].lineTo (75, 160);
		shapes[11].closePath ();
		
		shapes[12] = new Path2D.Double ();
		shapes[12].moveTo (105, 140);
		shapes[12].lineTo (95, 150);
		shapes[12].lineTo (105, 160);
		shapes[12].closePath ();
		
		shapes[13] = new Path2D.Double ();
		shapes[13].moveTo (150, 140);
		shapes[13].lineTo (160, 150);
		shapes[13].lineTo (150, 160);
		shapes[13].closePath ();
		
		shapes[14] = new Path2D.Double (new RoundRectangle2D.Double (75, 20, 30, 15, 7.5, 7.5));
		
		shapes[15] = new Path2D.Double (new Rectangle2D.Double (85, 22.5, 4, 10));
		shapes[15].append (new Rectangle2D.Double (91, 22.5, 4, 10), false);
		
		shapes[16] = new Path2D.Double ();
		shapes[16].moveTo (86, 22.5);
		shapes[16].lineTo (95, 27.5);
		shapes[16].lineTo (86, 32.5);
		shapes[16].closePath ();
		
		addMouseListener (this);
		addMouseMotionListener (this);
	}
	
	public void paintComponent (Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint (RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2d.setRenderingHint (RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		super.paintComponent (g);
		
		g2d.setFont (font);
		
		// Draw the selector
		drawSelector (g2d);
		
		// FPS
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.SECOND) != second) {
			second = now.get(Calendar.SECOND);
			fps = count;
			count = 0;
		}
		
		count ++;
		
		g2d.setColor (new Color (0, 0, 0));
		g2d.drawString (fps+" FPS", 5, Billiard.HEIGHT - 5);
		
		// Draw widget
		g2d.setColor (new Color (0, 0, 0, 150));
		g2d.fill (shapes[0]);
		
		g2d.setColor (new Color (255, 255, 255, 200));
		g2d.fill (shapes[1]);
		g2d.fill (shapes[2]);
		g2d.fill (shapes[4]);
		g2d.fill (shapes[5]);
		g2d.fill (shapes[6]);
		g2d.fill (shapes[7]);
		g2d.fill (shapes[8]);
		g2d.fill (shapes[9]);
		g2d.fill (shapes[10]);
		g2d.fill (shapes[11]);
		g2d.fill (shapes[12]);
		g2d.fill (shapes[13]);
		
		g2d.setColor (new Color (255, 0, 0));
		g2d.fill (shapes[3]);
		
		g2d.setColor (new Color (255, 255, 255));
		g2d.draw (shapes[3]);
		
		g2d.drawString ("Color", (int)shapes[4].getBounds2D().getMinX(), (int)shapes[4].getBounds2D().getMinY() - 5);
		g2d.drawString ("Speed (X, Y)", (int)shapes[6].getBounds2D().getMinX(), (int)shapes[6].getBounds2D().getMinY() - 5);
		g2d.drawString ("Mass", (int)shapes[10].getBounds2D().getMinX(), (int)shapes[10].getBounds2D().getMinY() - 5);
		g2d.drawString ("Radius", (int)shapes[12].getBounds2D().getMinX(), (int)shapes[12].getBounds2D().getMinY() - 5);
		
		g2d.setColor (new Color (0, 0, 0));
		drawStringFromCenter (g2d, "Prev", shapes[1].getBounds2D().getCenterX(), shapes[1].getBounds2D().getCenterY() - 2);
		drawStringFromCenter (g2d, "Next", shapes[2].getBounds2D().getCenterX(), shapes[2].getBounds2D().getCenterY() - 2);
		
		// Play/pause
		if (Billiard.is_paused ()) {
			g2d.setColor (new Color (138, 226, 52));
			g2d.fill (shapes[14]);
			g2d.setColor (Color.WHITE);
			g2d.fill (shapes[16]);
		}
		else {
			g2d.setColor (new Color (239, 41, 41));
			g2d.fill (shapes[14]);
			g2d.setColor (Color.WHITE);
			g2d.fill (shapes[15]);
		}
		
		// Show data
		g2d.setColor (new Color (255, 255, 255));
		drawStringFromCenter (g2d, (int)(Billiard.ball[active_ball].getSpeed().getX()*100)/100.0+"",
		                      shapes[6].getBounds2D().getMaxX() + 22.5, shapes[6].getBounds2D().getCenterY() - 2);
		drawStringFromCenter (g2d, (int)(Billiard.ball[active_ball].getSpeed().getY()*-100)/100.0+"",
		                      shapes[8].getBounds2D().getMaxX() + 22.5, shapes[8].getBounds2D().getCenterY() - 2);
		drawStringFromCenter (g2d, (int)(Billiard.ball[active_ball].getMass()*100)/100.0+"",
		                      shapes[10].getBounds2D().getMaxX() + 22.5, shapes[10].getBounds2D().getCenterY() - 2);
		drawStringFromCenter (g2d, (int)(Billiard.ball[active_ball].getRadius()*100)/100.0+"",
		                      shapes[12].getBounds2D().getMaxX() + 22.5, shapes[12].getBounds2D().getCenterY() - 2);
		
		g2d.setColor (Ball.colors[Billiard.ball[active_ball].getColorId()]);
		g2d.fill (shapes[3]);
	}
	
	public void mousePressed (MouseEvent e) {
		if (shapes[1].contains (e.getX(), e.getY())) {
			active_ball += Billiard.BALLS - 1;
			active_ball %= Billiard.BALLS;
		}
		if (shapes[2].contains (e.getX(), e.getY())) {
			active_ball ++;
			active_ball %= Billiard.BALLS;
		}
		
		if (shapes[4].contains (e.getX(), e.getY())) {
			Billiard.ball[active_ball].setColorId (Billiard.ball[active_ball].getColorId() - 1);
		}
		if (shapes[5].contains (e.getX(), e.getY())) {
			Billiard.ball[active_ball].setColorId (Billiard.ball[active_ball].getColorId() + 1);
		}
		
		if (shapes[6].contains (e.getX(), e.getY())) {
			Billiard.ball[active_ball].getSpeed ().addX (-1.0);
			Billiard.queue_collision_update ();
		}
		if (shapes[7].contains (e.getX(), e.getY())) {
			Billiard.ball[active_ball].getSpeed ().addX (1.0);
			Billiard.queue_collision_update ();
		}
		
		if (shapes[8].contains (e.getX(), e.getY())) {
			Billiard.ball[active_ball].getSpeed ().addY (1.0);
			Billiard.queue_collision_update ();
		}
		if (shapes[9].contains (e.getX(), e.getY())) {
			Billiard.ball[active_ball].getSpeed ().addY (-1.0);
			Billiard.queue_collision_update ();
		}
		
		if (shapes[10].contains (e.getX(), e.getY())) {
			Billiard.ball[active_ball].setMass (Billiard.ball[active_ball].getMass() - 1.0);
		}
		if (shapes[11].contains (e.getX(), e.getY())) {
			Billiard.ball[active_ball].setMass (Billiard.ball[active_ball].getMass() + 1.0);
		}
		
		if (shapes[12].contains (e.getX(), e.getY())) {
			Billiard.ball[active_ball].setRadius (Billiard.ball[active_ball].getRadius() - 1.0);
			Billiard.queue_collision_update ();
		}
		if (shapes[13].contains (e.getX(), e.getY())) {
			Billiard.ball[active_ball].setRadius (Billiard.ball[active_ball].getRadius() + 1.0);
			Billiard.queue_collision_update ();
		}
		
		if (shapes[14].contains (e.getX(), e.getY())) {
			Billiard.toggle_play_pause ();
		}
		
		if (shapes[0].contains (e.getX(), e.getY())) {
			is_dragged = true;
			drag_start_x = e.getX();
			drag_start_y = e.getY();
		}
	}
	
	public void mouseReleased (MouseEvent e) {
		is_dragged = false;
	}
	
	public void mouseClicked (MouseEvent e) {}
	
	public void mouseEntered (MouseEvent e) {}
	
	public void mouseExited (MouseEvent e) {}
	
	public void mouseMoved (MouseEvent e) {}
	
	public void mouseDragged (MouseEvent e) {
		int delta_x = e.getX() - drag_start_x;
		int delta_y = e.getY() - drag_start_y;
		
		if (shapes[0].getBounds2D().getMaxX() + delta_x > Billiard.WIDTH) {
			delta_x = Billiard.WIDTH - (int)shapes[0].getBounds2D().getMaxX();
		}
		
		if (shapes[0].getBounds2D().getMinX() + delta_x < 0) {
			delta_x = 0 - (int)shapes[0].getBounds2D().getMinX();
		}
		
		if (shapes[0].getBounds2D().getMaxY() + delta_y > Billiard.HEIGHT) {
			delta_y = Billiard.HEIGHT - (int)shapes[0].getBounds2D().getMaxY();
		}
		
		if (shapes[0].getBounds2D().getMinY() + delta_y < 0) {
			delta_y = 0 - (int)shapes[0].getBounds2D().getMinY();
		}
		
		drag_start_x += delta_x;
		drag_start_y += delta_y;		
		
		AffineTransform af = new AffineTransform ();
		af.translate (delta_x, delta_y);
				
		for (int i = 0; i < 17; i++) {
			shapes[i].transform (af);
		}
	}
}
