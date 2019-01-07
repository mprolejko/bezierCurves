package bezier;

import javax.vecmath.Point2d;

public class Application {
	public static void main(String[] args){
		
		BezierCurve bc = new BezierCurve();
		bc.addPoint(new Point2d(0,0));
		bc.addPoint(new Point2d(0,1));
		bc.addPoint(new Point2d(1,1));
		bc.addPoint(new Point2d(1,0));
		
		BezierCurve bc2 = new BezierCurve();
		bc2.addPoint(new Point2d(.5,1.5));
		bc2.addPoint(new Point2d(.5,2.5));
		bc2.addPoint(new Point2d(1.5,2.5));
		bc2.addPoint(new Point2d(2.5,.5));
		bc2.addPoint(new Point2d(1.5,.5));
		
		MultiCurve mc = new MultiCurve(bc);
		mc.addCurve(bc2);
			
		MultiCurve mc2 = new MultiCurve(BezierCurve.getDifferencial(bc));
		mc2.addCurve(BezierCurve.getDifferencial(bc2));
		
		new Window("Krzywe Beziera",500,500, mc);
	}
}
