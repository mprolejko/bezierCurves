package bezier;


import java.awt.Graphics;
import java.awt.Rectangle;
import java.text.DecimalFormat;

import javax.vecmath.Point2d;

public class PointHandler{
	DecimalFormat df = new DecimalFormat("0.00");
	
	private Point2d point;
	public Point2d getPoint(){return point;}
	public void setPoint(double x, double y){
		point.x = x; 
		point.y = y;
	}
	Rectangle area;
	
	public PointHandler(Point2d p){
		point = p;
	}

	public void draw(Graphics g,int x,int y) {
		g.drawString("["+df.format(point.x)+","+df.format(point.y)+"]", x+6, y+6);
		g.drawOval(x, y, 4, 4);
		area = new Rectangle(x-10, y-10, 20, 20);
	}
	
	public boolean isInArea(int x, int y){
		return area.contains(x, y);
	}
	
}
