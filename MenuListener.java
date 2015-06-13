import java.awt.*;
import java.text.*;
import java.util.*;
import java.awt.event.*;

public class MenuListener implements KeyListener {//, Runnable {
	private MPanel pan;
	private Window win;
	/*private Thread t;

	public void start () {
		if (t == null) {
			t = new Thread(this);
			t.start();
		} 
	}
	public void stop () { if (t != null) {t.stop(); t = null;}}
	public void run () {
		try {
			this.t.sleep(50);
		}
		catch (InterruptedException ie) {}
	}
*/
	public MenuListener (MPanel p, Window w) {
		this.pan = p;
		this.win = w;
	}

	public void keyPressed(KeyEvent e) {
		if (this.pan.getMenuState() != null) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER && this.pan.getMenuState() != false) {
				this.pan.setMenuState(new Boolean(false));
				System.out.println("play! " + this.pan.getMenuState());
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				System.exit(0);		//quit to desktop
			}
		}
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
}