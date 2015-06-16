import java.awt.*;
import java.text.*;
import java.util.*;
import java.awt.event.*;

public class GameListener implements KeyListener , Runnable {
	private MPanel pan;
	private Window win;
	private Thread t;
	private Boolean UP;
	private Boolean DN;
	private Boolean L;
	private Boolean R;
	
	public void start () {
		if (t == null) {
			t = new Thread(this);
			t.start();
		} 
	}
	public void stop () { if (t != null) {t.stop(); t = null;}}
	public void run () {/*
		try {
			while(true) {
				this.t.sleep(20);
				this.processKey();
			}
		}
		catch (InterruptedException ie) {}
		*/
	}

	public GameListener (MPanel p, Window w) {
		this.pan = p;
		this.win = w;
		/*
		this.UP = new Boolean(false);
		this.DN = new Boolean(false);
		this.L = new Boolean(false);
		this.R = new Boolean(false);
		*/
	}

	public void keyPressed(KeyEvent e) {
		if (this.pan.getGameState() != null && this.pan.getGameState()) {
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				this.pan.moveUp();
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				this.pan.moveDown();
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				this.pan.moveLeft();
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				this.pan.moveRight();
			}
			if (e.getKeyCode() == KeyEvent.VK_A) {
				this.pan.rCCW();
			}
			if (e.getKeyCode() == KeyEvent.VK_D) {
				this.pan.rCW();
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {			//set piece in place
				this.pan.placeElectromino();
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				this.pan.setMenuState(new Boolean(true));
				this.pan.setGameState(new Boolean(false));
			}
		}
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {}



//another possible way of handling movement

/*
	public void keyPressed(KeyEvent e) {
		if (this.pan.getGameState() != null && this.pan.getGameState()) {
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				this.UP = new Boolean(true);
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				this.DN = new Boolean(true);
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				this.L = new Boolean(true);
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				this.R = new Boolean(true);
			}
			if (e.getKeyCode() == KeyEvent.VK_A) {}
			if (e.getKeyCode() == KeyEvent.VK_D) {}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {			//set piece in place
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				this.pan.setMenuState(new Boolean(true));
				this.pan.setGameState(new Boolean(false));
			}
		}
	}
	public void keyReleased(KeyEvent e) {
		if (this.pan.getGameState() != null && this.pan.getGameState()) {
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				this.UP = new Boolean(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				this.DN = new Boolean(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				this.L = new Boolean(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				this.R = new Boolean(false);
			}
		}
	}
	public void keyTyped(KeyEvent e) {}

	public void processKey () {
		if (this.pan != null) {
			if (this.UP) {
				this.pan.moveUp();
			}
			if (this.DN) {
				this.pan.moveDown();
			}
			if (this.L) {
				this.pan.moveLeft();
			}
			if (this.R) {
				this.pan.moveRight();
			}
		}
	}*/
}