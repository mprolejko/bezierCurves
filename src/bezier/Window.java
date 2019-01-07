package bezier;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.*;
import javax.vecmath.Point2d;

public class Window extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel canvaLeft;
	private JPanel canvaRight;
	//private JPanel userInterface;
	
	private MultiCurve mc;
	
	public Window(String title, int width, int height, MultiCurve mc){
		super(title);
		this.setSize(width, height);
		
		this.mc = mc;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0,0,2*width,height);
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top,BoxLayout.LINE_AXIS));
		
		getContentPane().add(top);
		
		canvaLeft = new JPanel();
		canvaLeft.setPreferredSize(new Dimension(width, height));
//		canvaRight = new JPanel();
//		canvaRight.setPreferredSize(new Dimension(width, height));

		top.add(canvaLeft);
		//top.add(canvaRight);

		prepareGUI(width, height);
		
		pack();
		setVisible(true);
	}
	
	private void prepareGUI(int width, int height){
		
		canvaLeft.add(new DrawingPanel(new Dimension(width,height), mc));
		Scene sc = new Scene();
		//canvaRight.add(sc.getCanvas3D());
	}

	
}
