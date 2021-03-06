package bezier;
import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.LineArray;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.RestrictedAccessException;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Sphere;


public class Scene {
	private BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
	private Locale myLocale;
	private GraphicsConfiguration gc;


	private Canvas3D canvas3D;
	public Canvas3D getCanvas3D(){return canvas3D;}
	
	private boolean renderScreen = true;
	private OffScreenCanvas3D offscreenCanvas3D;


	private class OffScreenCanvas3D extends Canvas3D { 
		private static final long serialVersionUID = 1L;

		BufferedImage visionImage; 
		private int imageWidth; 
		private int imageHeight;

		public OffScreenCanvas3D(GraphicsConfiguration gconfig,int width, int height) { 
			super(gconfig, true); 

			this.imageWidth = width; 
			this.imageHeight = height; 

			visionImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB); 

			ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGB, visionImage); 
			buffer.setCapability(ImageComponent.ALLOW_IMAGE_READ); 
			setOffScreenBuffer(buffer); 
		} 


		@Override 
		synchronized public void postSwap() { 
			// copy rendered image 
			BufferedImage bim = getOffScreenBuffer().getImage(); 
			visionImage.setData(bim.getData()); 
		} 

		synchronized void render() { 
			try { 
				renderOffScreenBuffer(); 
			} catch (RestrictedAccessException e) { } 
		} 
	} 	




	Scene(){

		VirtualUniverse myUniverse = new VirtualUniverse();
		myLocale = new Locale(myUniverse);

		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		template.setSceneAntialiasing(GraphicsConfigTemplate.REQUIRED);
		template.setDoubleBuffer(GraphicsConfigTemplate.PREFERRED);

		gc = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(template);
		canvas3D = new Canvas3D(gc);

		myLocale.addBranchGraph(createSceneGraph());
		
		if(renderScreen)
			myLocale.addBranchGraph(buildViewBranch(canvas3D));
		
	}

	public void update(){
		if (offscreenCanvas3D != null)
			offscreenCanvas3D.render();
	}





	/**
	 * Build the view branch of the scene graph
	 * 
	 * @return BranchGroup that is the root of the view branch
	 */
	private BranchGroup buildViewBranch(Canvas3D canvas) {
		BranchGroup viewBranch = new BranchGroup();
		
		Background background = new Background(new Color3f(1f,1f,1f));
		background.setApplicationBounds(bounds);
		viewBranch.addChild(background);

		TransformGroup viewTransformGroup = new TransformGroup();
		viewTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		viewTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D t3d = new Transform3D();
		t3d.setTranslation(new Vector3f(0.0f,-10.0f,7.5f));
		t3d.setRotation(new AxisAngle4f(new Vector3f(1f,0f,0f),1.3f));
		viewTransformGroup.setTransform(t3d);

		ViewPlatform viewPlatform = new ViewPlatform();
		viewPlatform.setViewAttachPolicy(View.NOMINAL_HEAD);
		viewPlatform.setActivationRadius(100);

		View view = new View();
		view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		view.setViewPolicy(View.SCREEN_VIEW);
		view.setVisibilityPolicy(View.VISIBILITY_DRAW_ALL);
		view.setFrontClipDistance(0.5);
		view.setSceneAntialiasingEnable(true);

		view.addCanvas3D(canvas);

		view.setPhysicalBody(new PhysicalBody());
		view.setPhysicalEnvironment(new PhysicalEnvironment());

		view.attachViewPlatform(viewPlatform);
		viewTransformGroup.addChild(viewPlatform);
		viewBranch.addChild(viewTransformGroup);
		return viewBranch;
	}





	private void addLights(BranchGroup b) {
		AmbientLight ambLight = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
		ambLight.setInfluencingBounds(bounds);

		DirectionalLight dirLight = new DirectionalLight( new Color3f(0.9f, 0.9f, 0.9f),  new Vector3f(1.0f, 1.0f, 1.0f));
		dirLight.setInfluencingBounds(bounds);
		
		b.addChild(ambLight);
		b.addChild(dirLight);
	}

	private BranchGroup createSceneGraph() {
		BranchGroup contentBranch = new BranchGroup();

		TransformGroup spinGroup = new TransformGroup();
		TransformGroup zoomGroup = new TransformGroup();
		TransformGroup moveGroup = new TransformGroup();

		spinGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		spinGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		zoomGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		zoomGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		moveGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		moveGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		
		MouseRotate mouseSpin = new MouseRotate();
		mouseSpin.setTransformGroup(spinGroup);
		contentBranch.addChild(mouseSpin);
		mouseSpin.setSchedulingBounds(bounds);
		
		MouseZoom mouseSize = new MouseZoom();
		mouseSize.setTransformGroup(zoomGroup);
		contentBranch.addChild(mouseSize);
		mouseSize.setSchedulingBounds(bounds);
		
		MouseTranslate mouseMove = new MouseTranslate();
		mouseMove.setTransformGroup(moveGroup);
		contentBranch.addChild(mouseMove);
		mouseMove.setSchedulingBounds(bounds);

		//Put it all together
		Sphere sphere = new Sphere(0.5f);

		spinGroup.addChild(sphere);
		//spinGroup.addChild(sthToAdd);

		moveGroup.addChild(spinGroup);
		zoomGroup.addChild(moveGroup);
		contentBranch.addChild(zoomGroup);
		addLights(contentBranch);
		return contentBranch;

	}




}
