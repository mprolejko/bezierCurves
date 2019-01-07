package bezier;

import javax.vecmath.*;
import java.util.ArrayList;

public class BezierCurve{
	private ArrayList<Point2d> points = new ArrayList<Point2d>();
	public ArrayList<Point2d> getPoints(){ return points;}
	
	private int precision = 200;
	private Point2d[] curve = new Point2d[precision+1];
	private Point2d[] diff = new Point2d[precision];
	public Point2d[] getCurve(){return curve;}
	public Point2d[] getDiff(){return diff;}
	
	private double[] paramVec = new double[precision+1];
	private double[] bounds= {0.0,0.0,0.0,0.0};
	public double[] getCurveBounds(){return bounds;}

	public static BezierCurve getDifferencial(BezierCurve bc){
		BezierCurve dbc = new BezierCurve();
		ArrayList<Point2d> points = bc.getPoints();
		int N = points.size()-1;
		for(int i=0;i<N;i++){
			Point2d p1 = points.get(i);
			Point2d p2 = points.get(i+1);
			dbc.addPoint(new Point2d((p2.x-p1.x)*N,(p2.y-p1.y)*N));
		}
		return dbc;
	}
	
	public BezierCurve(){
		for(int i=0; i<=precision;i++){
			paramVec[i] = ((double)i)/precision;
		}
	}
	public BezierCurve(ArrayList<Point2d> points){
		this();
		this.points = points;
		recalculate();
		setBounds();
	}
	
	public void addPoint(Point2d p){
		points.add(p);
		recalculate();
		setBounds();
	}
	public void recalculate(){
		calculateCurve();
		calculate1st();
	}
	private void calculateCurve(){
		int N = points.size()-1;
		int i=0;
		
		for(double t:paramVec){
			double x=0,	y=0;
			for(int n=0;n<=N;n++){
				double w = newton(N,n)*Math.pow(t, n)*Math.pow(1-t, N-n);
				x += points.get(n).x*w;
				y += points.get(n).y*w;
			}
			curve[i++] = new Point2d(x,y);
		}
	}
	private void calculate1st(){
		for(int i=0;i<precision;i++){
			diff[i] = new Point2d(curve[i].x-curve[i+1].x,curve[i].y-curve[i+1].y);
		}
	}

	
	private void setBounds(){
		Point2d max = new Point2d(0,0), min=new Point2d(0,0);
		
		for(Point2d p: points){
			max.x = p.x > max.x? p.x : max.x;
			max.y = p.y > max.y? p.y : max.y;
			min.x = p.x < min.x? p.x : min.x;
			min.y = p.y < min.y? p.y : min.y;
		}
		bounds = new double[]{min.x, min.y, max.x, max.y};

	}
	
	private static long newton( int n, int k )
	{
		long wynik=1;
		for(int i=1; i<=k; i++){ wynik = wynik*(n-i+1)/i;}
		return wynik;
	}
}
