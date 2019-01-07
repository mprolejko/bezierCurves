package bezier;

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.vecmath.*;

public class DrawingPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MultiCurve curves;
	private ArrayList<PointHandler> handlers = new ArrayList<PointHandler>();
	
	private double scaleFactor=1.0;
	private double[] canvaBounds= {0.0,0.0,0.0,0.0};
	private int[] canvaSize= new int[2];

	
	public DrawingPanel(Dimension d, MultiCurve bc){
		curves = bc;
		for(BezierCurve curve:curves.getCurves()){
			for(Point2d p:curve.getPoints()){
				handlers.add(new PointHandler(p));
			}
		}
		this.setPreferredSize(d);
		this.setLayout(new FlowLayout());
		this.setBackground(Color.white);
		canvaSize[0] = d.width - 50;
		canvaSize[1] = d.height - 50;
		setBounds();
		
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				for(PointHandler ph :handlers){
					if(ph.isInArea(x,y)){
						ph.setPoint(xd(x),yd(y));
						curves.recalculate();
						repaint();
					}
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			

		});
	}
	

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		float col = 0.0f;
		
		for(BezierCurve curve:curves.getCurves()){
		
			Point2d[] cur = curve.getCurve();
			Point2d[] dif = curve.getDiff();
			int precision = cur.length;
			
			for(int i=0;i<precision-1;i++){
				Point2d s = cur[i];
				Point2d d = dif[i];
	
				g.setColor(Color.getHSBColor(col, 0.9f, 0.7f)); 
				g.drawLine(dx(s.x), dy(s.y), dx(s.x+d.y*3), dy(s.y-d.x*3));
				
				g.setColor(Color.black); 
				g.drawOval( dx(s.x), dy(s.y), 1, 1);
			}
			
			for(PointHandler ph :handlers){
				ph.draw(g,dx(ph.getPoint().x),dy(ph.getPoint().y));
			}
			col+=0.2;
		}
		
	}
	
	private int dx(double a){
		return (int)(25-canvaBounds[0] + a*scaleFactor);
	}
	private int dy(double a){
		return (int)(25-canvaBounds[1] +a*scaleFactor);
	}
	private double xd(int a){
		return ((double)(-25+canvaBounds[0] + a))/scaleFactor;
	}
	private double yd(int a){
		return ((double)(-25+canvaBounds[1] +a))/scaleFactor;
	}
	
	private void setBounds(){
		for(BezierCurve curve:curves.getCurves()){
			double[] bounds= curve.getCurveBounds();
			double w = bounds[2]-bounds[0];
			double h = bounds[3]-bounds[1];
			
			scaleFactor = Math.min(canvaSize[0]/w, canvaSize[1]/h);
			
			canvaBounds = new double[]{ 
					Math.min(canvaBounds[0],scaleFactor*bounds[0]),
					Math.min(canvaBounds[1],scaleFactor*bounds[1]),
					Math.max(canvaBounds[2],scaleFactor*bounds[2]),
					Math.max(canvaBounds[3],scaleFactor*bounds[3])};
		}
	}

}
