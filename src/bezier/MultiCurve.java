package bezier;

import java.util.ArrayList;

public class MultiCurve {
	private ArrayList<BezierCurve> curves = new ArrayList<BezierCurve>();
	public ArrayList<BezierCurve> getCurves(){return curves;}
	
	private int smoothLv = -1;
	
	public MultiCurve(BezierCurve bc){
		curves.add(bc);
	}
	
	public void addCurve(BezierCurve bc){
		curves.add(bc);
	}

	public void recalculate() {
		for(BezierCurve bc:curves){
			bc.recalculate();
		}
		
	}
	

	
}
