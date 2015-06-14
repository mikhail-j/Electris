import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.text.*;
import java.util.*;

public class MPanel extends Panel implements Runnable{
	private BufferedImage can;
	private Graphics gfx;
	private Graphics2D gfx2D;
	private Thread t;
	private Boolean isUpdating;
	private Boolean isMenu;
	private Frame ctx;
	private Rectangle bounds;
	private Font font;
	private MWindow win;
	private String TITLE_PLAY_NOW = "PRESS START";
	private SQ[][] GRID;
	private Integer SQ_HEIGHT;
	private Electromino current;
	private Point cp;

	public void start () {
		if (t == null) {
			t = new Thread(this);
			t.start();
		} 
	}
	public void stop () { if (t != null) {t.stop(); t = null;}}
	public void run () {
		try {
			while (this.isUpdating) {
				super.repaint();
				this.t.sleep(50);
			}
		}
		catch (InterruptedException ie) {}
	}

	public MPanel (Frame window, MWindow win) {
		this.isUpdating = true;
		this.isMenu = true;
		this.win = win;
		this.font = new Font("Arial Monospaced", Font.PLAIN, 20);
		this.ctx = window;
		this.bounds = this.ctx.getBounds();
		super.setSize(this.bounds.width,this.bounds.height);
		this.can = (new BufferedImage(this.bounds.width,this.bounds.height, BufferedImage.TYPE_INT_ARGB_PRE));
		this.gfx = this.can.createGraphics();
		this.gfx2D = (Graphics2D)this.gfx;
		this.gfx2D.setFont(this.font);
		this.GRID = new SQ [10][20];
		this.SQ_HEIGHT = 20;
		this.current = new Electromino (0);
		System.out.println("current piece size - width: " + this.current.getPiece().length + " height: " + this.current.getPiece()[0].length);
		this.cp = new Point (3, 0);
		super.validate();
	}
	
	@Override
	public void update (Graphics g) {
		this.cleanGFX();
		if (this.isMenu) {
			this.drawTitle();
		}
		else {
			this.drawBGD();
			this.drawCurrent();
		}
		this.paint(g);
		//this.isUpdating = false;
	}

	@Override
	public void paint (Graphics g) {
		super.paint(g);
		g.drawImage(this.can,0,0,this);
	}

	public Boolean getMenuState() {
		return this.isMenu;
	}

	public void setMenuState(Boolean b) {
		this.isMenu = b;
	}

	public void drawTitle() {
		this.gfx2D.setColor(Color.black);
		this.gfx2D.drawString(TITLE_PLAY_NOW, (this.bounds.width/2) - (6 * (TITLE_PLAY_NOW.length())), (this.bounds.height/2) - 10);
	}

	public void drawBGD() {
		this.gfx2D.setColor(Color.gray);
		this.gfx2D.fillRect(0,0,this.bounds.width,this.bounds.height);
		this.gfx2D.setColor(Color.white);
		this.gfx2D.fillRect((int)(this.bounds.width * .25) - (5 * this.SQ_HEIGHT), (int)(this.bounds.height * .5) - (10 * this.SQ_HEIGHT), (10 * this.SQ_HEIGHT), (20 * this.SQ_HEIGHT));
	}
	
	public void drawCurrent() {
		this.gfx2D.setColor(Color.yellow);
		int xi = (int)(this.bounds.width * .25) - (5 * this.SQ_HEIGHT);
		int yi = (int)(this.bounds.height * .5) + (10 * this.SQ_HEIGHT) - this.SQ_HEIGHT;		//from bottom upwards and we start drawing from the top right
		for (int i = 0; i < this.GRID.length; i++) {
			for (int j = 0; j < this.GRID[0].length; j++) {
				if (this.GRID[i][j] != null) {
					this.gfx2D.fillRect(xi + (i * this.SQ_HEIGHT), yi - (j * this.SQ_HEIGHT), this.SQ_HEIGHT, this.SQ_HEIGHT);
				}
			}
		}
		int counter = 0;
		for (int i = 0; i < this.current.getPiece().length; i++) {
			for (int j = 0; j < this.current.getPiece()[0].length; j++) {
				if (this.current.getPiece()[i][j] != null) {
					this.gfx2D.setColor(Color.yellow);
					this.gfx2D.fillRect(xi + ((i + (int)this.cp.getX()) * this.SQ_HEIGHT), yi - ((j + (int)this.cp.getY()) * this.SQ_HEIGHT), this.SQ_HEIGHT, this.SQ_HEIGHT);
					this.gfx2D.setColor(Color.black);
					this.gfx2D.drawString("" + ++counter, xi + ((i + (int)this.cp.getX()) * this.SQ_HEIGHT), yi - ((j + (int)this.cp.getY() - 1) * this.SQ_HEIGHT));
				}
			}
		}
	}

	public void cleanGFX() {
		this.gfx.setColor(Color.white);
		this.gfx.fillRect(0,0,this.bounds.width,this.bounds.height);
		this.gfx.setColor(Color.black);
	}
}