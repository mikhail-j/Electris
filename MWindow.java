import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.image.*;

public class MWindow {
	private Frame wframe;
	private Window fwindow;
	private GraphicsConfiguration gc;
	private BufferedImage buff;
	private Rectangle bounds;
	private MPanel pan;

	public MWindow () {
		this.wframe = new Frame("Electris");
		this.fwindow = new Window(this.wframe);
		this.gc = this.fwindow.getGraphicsConfiguration();
		this.buff = this.gc.createCompatibleImage(1280,720);
		this.wframe.setResizable(false);
		this.wframe.setSize(640,480);
		this.bounds = this.wframe.getMaximizedBounds();
		System.out.println("height: " + this.wframe.getBounds().height + " width: " + this.wframe.getBounds().width);
 		this.wframe.setLocation(100,100);
 		this.wframe.setUndecorated(false);
		this.wframe.setLayout(null);
 		this.wframe.addWindowListener(new WindowListener () {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
	
			public void windowOpened(WindowEvent e) {}
	        public void windowActivated(WindowEvent e) {}
	        public void windowIconified(WindowEvent e) {}
	        public void windowDeiconified(WindowEvent e) {}
	        public void windowDeactivated(WindowEvent e) {}
	        public void windowClosed(WindowEvent e) {}
		});
		this.pan = new MPanel(this.wframe, this);
		this.wframe.add(this.pan);
		this.wframe.validate();
 		this.wframe.setVisible(true);
	}
}