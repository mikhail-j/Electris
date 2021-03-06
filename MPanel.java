import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.text.*;
import java.util.*;
import java.io.*;

public class MPanel extends Panel implements Runnable{
	private BufferedImage can;
	private Graphics gfx;
	private Graphics2D gfx2D;
	private Thread t;
	private Boolean isUpdating;
	private Boolean isMenu;
	private Boolean isGame;
	private Boolean isOver;
	private Frame ctx;
	private Rectangle bounds;
	private Font font;
	private Font end_font;
	private MWindow win;
	private String TITLE_PLAY_NOW = "PRESS START";
	private String TITLE = "Eletris";
	private SQ[][] GRID;
	private Integer SQ_HEIGHT;
	private Integer PN;				//piece number
	private Electromino current;
	private Point cp;				//x and y position of the current electromino piece
	private long frame_count;		//used to calculate fps
	private long SYSTEM_TIME;		//last recorded system nanotime
	private Boolean[] movable;		//has the electromino been moved in a certain direction this frame?
	private Queue<Electromino> nes;		//queue of the following electrominos

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
				this.t.sleep(20);
			}
		}
		catch (InterruptedException ie) {}
	}

	public MPanel (Frame window, MWindow win) {
		
		this.SYSTEM_TIME = System.nanoTime();
		this.isUpdating = true;
		this.isMenu = true;
		this.isGame = false;		//game hasn't started yet
		this.isOver = false;
		this.win = win;
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("PressStart2P-Regular.ttf")));
			this.font = (Font.createFont(Font.TRUETYPE_FONT, new File("PressStart2P-Regular.ttf"))).deriveFont(20f);
			this.end_font = (Font.createFont(Font.TRUETYPE_FONT, new File("PressStart2P-Regular.ttf"))).deriveFont(72f);
		}
		catch (IOException e) {
			this.font = new Font("Arial Monospaced", Font.PLAIN, 20);
			this.end_font = new Font("Arial Monospaced", Font.PLAIN, 72);
			System.out.println("fail");
		}
		catch (FontFormatException e) {
			this.font = new Font("Arial Monospaced", Font.PLAIN, 20);
			this.end_font = new Font("Arial Monospaced", Font.PLAIN, 72);
			System.out.println("fail");
		}
		//this.font = new Font("Arial Monospaced", Font.PLAIN, 20);
		//this.end_font = new Font("Arial Monospaced", Font.PLAIN, 72);
		
		this.ctx = window;
		this.bounds = this.ctx.getBounds();
		super.setSize(this.bounds.width,this.bounds.height);
		this.can = (new BufferedImage(this.bounds.width,this.bounds.height, BufferedImage.TYPE_INT_ARGB_PRE));
		this.gfx = this.can.createGraphics();
		this.gfx2D = (Graphics2D)this.gfx;
		this.gfx2D.setFont(this.font);
		this.initialize();

		this.movable = new Boolean [4];
		this.movable[0] = new Boolean(false);
		this.movable[1] = new Boolean(false);
		this.movable[2] = new Boolean(false);
		this.movable[3] = new Boolean(false);
		super.validate();
	}

	public void initialize () {
		this.GRID = new SQ [10][20];
		this.SQ_HEIGHT = 20;
		this.PN = new Integer(0);
		this.current = new Electromino ((int)(Math.floor(7 * Math.random())));
		this.nes = new LinkedList<Electromino>();
		this.nes.add(new Electromino ((int)(Math.floor(7 * Math.random()))));
		this.nes.add(new Electromino ((int)(Math.floor(7 * Math.random()))));
		System.out.println("current piece size - width: " + this.current.getPiece().length + " height: " + this.current.getPiece()[0].length);
		this.cp = new Point (3, 17);

		this.cp.setLocation(this.cp.getX(),20);
		boolean not_low = true;
		boolean still_looking = true;
		int ydiff = 0;
		while (not_low) {
			for (int i = 0; i < this.current.getPiece().length && still_looking; i++) {
				for (int j = 0; j < this.current.getPiece()[0].length && still_looking; j++) {
					if ((int)this.cp.getY() + j + ydiff >= 0 && (int)this.cp.getY() + j + ydiff < 20) {
						if (this.current.getPiece()[i][j] != null && this.GRID[(int)this.cp.getX() + i][(int)this.cp.getY() + j + ydiff] != null) {
							still_looking = false;
							not_low = false;
						}
					}
					else if ((int)this.cp.getY() + j + ydiff < 0 &&this.current.getPiece()[i][j] != null){//rock bottom
						still_looking = false;
						not_low = false;
					}
				}
			}
			if (still_looking) {
				--ydiff;
			}
			else {
				++ydiff;
			}
		}
		this.cp.translate(0,ydiff);
	}

	@Override
	public void update (Graphics g) {
		++frame_count;
		this.movable[0] = new Boolean(false);
		this.movable[1] = new Boolean(false);
		this.movable[2] = new Boolean(false);
		this.movable[3] = new Boolean(false);
		this.cleanGFX();
		if (this.isMenu) {
			this.drawTitle();
		}
		else if (isGame) {
			this.drawBGD();
			this.drawCurrent();
			this.drawNPS();
			this.drawPN();
			this.clearRow();
		}
		else if (isOver) {
			this.showFin();
		}
		this.drawFPS();
		this.paint(g);
		//this.isUpdating = false;
	}

	public synchronized void moveLeft () {
		boolean chk = true;			//move the point, check if any pieces are out of bounds
		//if (this.cp.getX() > 0 && !this.movable[3]) {
		int x = (int)this.cp.getX() - 1;
		int y = (int)this.cp.getY();
		for (int a = 0; a < this.current.getPiece().length && chk; a++) {
			for (int b = 0; b < this.current.getPiece()[0].length && chk; b++) {
				if (this.current.getPiece()[a][b] != null && a + x < 0) {
					chk = false;
				}
				/*
				else if (this.current.getPiece()[a][b] != null && a + x >= 0 && this.GRID[a + x][b + y] != null) {
					chk = false;
				}
				*/
			}
		}
		if (chk && !this.movable[3]) {
			this.cp.translate(-1,0);
			this.movable[3] = !this.movable[3];
			boolean not_low = true;
			boolean still_looking = true;
			int ydiff = 0;
			while (not_low) {
				for (int i = 0; i < this.current.getPiece().length && still_looking; i++) {
					for (int j = 0; j < this.current.getPiece()[0].length && still_looking; j++) {
						if ((int)this.cp.getY() + j + ydiff >= 0 && (int)this.cp.getY() + j + ydiff < 20) {
							if (this.current.getPiece()[i][j] != null && this.GRID[(int)this.cp.getX() + i][(int)this.cp.getY() + j + ydiff] != null) {
								still_looking = false;
								not_low = false;
							}
						}
						else if ((int)this.cp.getY() + j + ydiff < 0 &&this.current.getPiece()[i][j] != null){//rock bottom
							still_looking = false;
							not_low = false;
						}
					}
				}
				if (still_looking) {
					--ydiff;
				}
				else {
					++ydiff;
				}
			}
			this.cp.translate(0,ydiff);
		}
	}
	
	public synchronized void moveRight () {
		boolean chk = true;
		//if (this.cp.getX() + this.current.getPiece().length < 10 && !this.movable[2]) {
		int x = (int)this.cp.getX() + 1;
		int y = (int)this.cp.getY();
		for (int a = 0; a < this.current.getPiece().length && chk; a++) {
			for (int b = 0; b < this.current.getPiece()[0].length && chk; b++) {
				if (this.current.getPiece()[a][b] != null && a + x > 9) {
					chk = false;
				}
				/*
				else if (this.current.getPiece()[a][b] != null && a + x < 20 && this.GRID[a + x][b + y] != null) {
					chk = false;
				}
				*/
			}
		}
		if (chk && !this.movable[2]) {
			this.cp.translate(1,0);
			this.movable[2] = !this.movable[2];
			boolean not_low = true;
			boolean still_looking = true;
			int ydiff = 0;
			while (not_low) {
				for (int i = 0; i < this.current.getPiece().length && still_looking; i++) {
					for (int j = 0; j < this.current.getPiece()[0].length && still_looking; j++) {
						if ((int)this.cp.getY() + j + ydiff >= 0 && (int)this.cp.getY() + j + ydiff < 20) {
							if (this.current.getPiece()[i][j] != null && this.GRID[(int)this.cp.getX() + i][(int)this.cp.getY() + j + ydiff] != null) {
								still_looking = false;
								not_low = false;
							}
						}
						else if ((int)this.cp.getY() + j + ydiff < 0 &&this.current.getPiece()[i][j] != null){//rock bottom
							still_looking = false;
							not_low = false;
						}
					}
				}
				if (still_looking) {
					--ydiff;
				}
				else {
					++ydiff;
				}
			}
			this.cp.translate(0,ydiff);
		}
	}
	/*
	public synchronized void moveDown () {
		boolean chk = true;
		//if (this.cp.getY() > 0 && !this.movable[1]) {
		int x = (int)this.cp.getX();
		int y = (int)this.cp.getY() - 1;
		for (int a = 0; a < this.current.getPiece().length && chk; a++) {
			for (int b = 0; b < this.current.getPiece()[0].length && chk; b++) {
				if (this.current.getPiece()[a][b] != null && b + y < 0) {
					chk = false;
				}
			}
		}
		if (chk && !this.movable[1]) {
			this.cp.translate(0,-1);
			this.movable[1] = !this.movable[1];
		}
	}
	public synchronized void moveUp () {			//this game should not support moving up
		if (this.cp.getY() + this.current.getPiece()[0].length < 20 && !this.movable[0]) {
			this.cp.translate(0,1);
			this.movable[0] = !this.movable[0];
		}
	}
*/
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

	public Boolean getGameState() {
		return this.isGame;
	}

	public void rCW () {
		if (this.current.getC() != 6) {				//no point in rotating the sqaure
			int chk = 0;
			int x = (int)this.cp.getX();
			int y = (int)this.cp.getY();
			Electromino n = this.current.getNext();
			for (int a = 0; a < n.getPiece().length && chk == 0; a++) {
				for (int b = 0; b < n.getPiece()[0].length && chk == 0; b++) {
					if (n.getPiece()[a][b] != null && a + x > 9) {
						chk = 1;
					}
					if (n.getPiece()[a][b] != null && a + x < 0) {
						chk = -1;
					}
				}
			}
			if (chk != 0) {
				if (chk == 1) {
					int x1 = x - 1;
					boolean c1 = true;
					for (int a = 0; a < n.getPiece().length && c1; a++) {
						for (int b = 0; b < n.getPiece()[0].length && c1; b++) {
							if (n.getPiece()[a][b] != null && a + x1 > 9) {
								c1 = false;
							}
						}
					}
					if (c1) {
						this.current = n;
						this.cp.translate(-1,0);
					}
					else {
						c1 = true;
						x1 = x - 2;
						for (int a = 0; a < n.getPiece().length && c1; a++) {
							for (int b = 0; b < n.getPiece()[0].length && c1; b++) {
								if (n.getPiece()[a][b] != null && a + x1 > 9) {
									c1 = false;
								}
							}
						}
						if (c1) {
							this.current = n;
							this.cp.translate(-2, 0);
						}
					}
				}
				else if (chk == -1) {
					int x1 = x + 1;
					boolean c1 = true;
					for (int a = 0; a < n.getPiece().length && c1; a++) {
						for (int b = 0; b < n.getPiece()[0].length && c1; b++) {
							if (n.getPiece()[a][b] != null && a + x1 < 0) {
								c1 = false;
							}
						}
					}
					if (c1) {
						this.current = n;
						this.cp.translate(1,0);
					}
					else {
						c1 = true;
						x1 = x + 2;
						for (int a = 0; a < n.getPiece().length && c1; a++) {
							for (int b = 0; b < n.getPiece()[0].length && c1; b++) {
								if (n.getPiece()[a][b] != null && a + x1 < 0) {
									c1 = false;
								}
							}
						}
						if (c1) {
							this.current = n;
							this.cp.translate(2, 0);
						}
						else {
							c1 = true;
							x1 = x + 3;
							for (int a = 0; a < n.getPiece().length && c1; a++) {
								for (int b = 0; b < n.getPiece()[0].length && c1; b++) {
									if (n.getPiece()[a][b] != null && a + x1 < 0) {
										c1 = false;
									}
								}
							}
							if (c1) {
								this.current = n;
								this.cp.translate(3, 0);
							}
						}
					}
				}
			}
			else if (chk == 0) {
				this.current = this.current.getNext();
			}
		}
		this.cp.setLocation(this.cp.getX(),20);
		boolean not_low = true;
		boolean still_looking = true;
		int ydiff = 0;
		while (not_low) {
			for (int i = 0; i < this.current.getPiece().length && still_looking; i++) {
				for (int j = 0; j < this.current.getPiece()[0].length && still_looking; j++) {
					if ((int)this.cp.getY() + j + ydiff >= 0 && (int)this.cp.getY() + j + ydiff < 20) {
						if (this.current.getPiece()[i][j] != null && this.GRID[(int)this.cp.getX() + i][(int)this.cp.getY() + j + ydiff] != null) {
							still_looking = false;
							not_low = false;
						}
					}
					else if ((int)this.cp.getY() + j + ydiff < 0 &&this.current.getPiece()[i][j] != null){//rock bottom
						still_looking = false;
						not_low = false;
					}
				}
			}
			if (still_looking) {
				--ydiff;
			}
			else {
				++ydiff;
			}
		}
		this.cp.translate(0,ydiff);
	}

	public void rCCW () {
		if (this.current.getC() != 6) {
			int chk = 0;
			int x = (int)this.cp.getX();
			int y = (int)this.cp.getY();
			Electromino n = this.current.getPrev();
			for (int a = 0; a < n.getPiece().length && chk == 0; a++) {
				for (int b = 0; b < n.getPiece()[0].length && chk == 0; b++) {
					if (n.getPiece()[a][b] != null && a + x > 9) {
						chk = 1;
					}
					if (n.getPiece()[a][b] != null && a + x < 0) {
						chk = -1;
					}
				}
			}
			if (chk != 0) {
				if (chk == 1) {
					int x1 = x - 1;
					boolean c1 = true;
					for (int a = 0; a < n.getPiece().length && c1; a++) {
						for (int b = 0; b < n.getPiece()[0].length && c1; b++) {
							if (n.getPiece()[a][b] != null && a + x1 > 9) {
								c1 = false;
							}
						}
					}
					if (c1) {
						this.current = n;
						this.cp.translate(-1,0);
					}
					else {
						c1 = true;
						x1 = x1 - 1;
						for (int a = 0; a < n.getPiece().length && c1; a++) {
							for (int b = 0; b < n.getPiece()[0].length && c1; b++) {
								if (n.getPiece()[a][b] != null && a + x1 > 9) {
									c1 = false;
								}
							}
						}
						if (c1) {
							this.current = n;
							this.cp.translate(-2, 0);
						}
					}
				}
				else if (chk == -1) {
					int x1 = x + 1;
					boolean c1 = true;
					for (int a = 0; a < n.getPiece().length && c1; a++) {
						for (int b = 0; b < n.getPiece()[0].length && c1; b++) {
							if (n.getPiece()[a][b] != null && a + x1 < 0) {
								c1 = false;
							}
						}
					}
					if (c1) {
						this.current = n;
						this.cp.translate(1,0);
					}
					else {
						c1 = true;
						x1 = x1 + 1;
						for (int a = 0; a < n.getPiece().length && c1; a++) {
							for (int b = 0; b < n.getPiece()[0].length && c1; b++) {
								if (n.getPiece()[a][b] != null && a + x1 < 0) {
									c1 = false;
								}
							}
						}
						if (c1) {
							this.current = n;
							this.cp.translate(2, 0);
						}
					}
				}
			}
			else {
				this.current = this.current.getPrev();
			}
		}
		this.cp.setLocation(this.cp.getX(),20);
		boolean not_low = true;
		boolean still_looking = true;
		int ydiff = 0;
		while (not_low) {
			for (int i = 0; i < this.current.getPiece().length && still_looking; i++) {
				for (int j = 0; j < this.current.getPiece()[0].length && still_looking; j++) {
					if ((int)this.cp.getY() + j + ydiff >= 0 && (int)this.cp.getY() + j + ydiff < 20) {
						if (this.current.getPiece()[i][j] != null && this.GRID[(int)this.cp.getX() + i][(int)this.cp.getY() + j + ydiff] != null) {
							still_looking = false;
							not_low = false;
						}
					}
					else if ((int)this.cp.getY() + j + ydiff < 0 &&this.current.getPiece()[i][j] != null){//rock bottom
						still_looking = false;
						not_low = false;
					}
				}
			}
			if (still_looking) {
				--ydiff;
			}
			else {
				++ydiff;
			}
		}
		this.cp.translate(0,ydiff);
	}

	public void setGameState(Boolean b) {
		this.isGame = b;
	}

	public void drawTitle() {
		this.gfx2D.setColor(Color.black);
		this.gfx2D.setFont(this.end_font);
		this.gfx2D.drawString(TITLE, (this.bounds.width/2) - (33 * (TITLE.length())), (int)(this.bounds.height * .35) - 36 + 100);
		this.gfx2D.setFont(this.font);
		this.gfx2D.drawString(TITLE_PLAY_NOW, (this.bounds.width/2) - (8 * (TITLE_PLAY_NOW.length())), (this.bounds.height/2) - 10 + 100);
	}

	public void drawBGD() {
		this.gfx2D.setColor(Color.gray);
		this.gfx2D.fillRect(0,0,this.bounds.width,this.bounds.height);
		this.gfx2D.setColor(Color.white);
		this.gfx2D.fillRect((int)(this.bounds.width * .25) - (5 * this.SQ_HEIGHT), (int)(this.bounds.height * .55) - (10 * this.SQ_HEIGHT), (10 * this.SQ_HEIGHT), (20 * this.SQ_HEIGHT));
	}

	public void placeElectromino() {
		for (int i = 0; i < this.current.getPiece().length; i++) {
			for (int j = 0; j < this.current.getPiece()[0].length; j++) {
				if (this.current.getPiece()[i][j] != null) {
					this.GRID[i + (int)this.cp.getX()][j + (int)this.cp.getY()] = this.current.getPiece()[i][j];
				}
			}
		}
		this.cp.setLocation(3,20);
		this.current = this.nes.remove();
		this.PN = ++this.PN;
		this.nes.add(new Electromino((int)(Math.floor(7 * Math.random()))));
		this.cp.setLocation(this.cp.getX(),20);
		boolean not_low = true;
		boolean still_looking = true;
		int ydiff = 0;
		while (not_low) {
			for (int i = 0; i < this.current.getPiece().length && still_looking; i++) {
				for (int j = 0; j < this.current.getPiece()[0].length && still_looking; j++) {
					if ((int)this.cp.getY() + j + ydiff >= 0 && (int)this.cp.getY() + j + ydiff < 20) {
						if (this.current.getPiece()[i][j] != null && this.GRID[(int)this.cp.getX() + i][(int)this.cp.getY() + j + ydiff] != null) {
							still_looking = false;
							not_low = false;
						}
					}
					else if ((int)this.cp.getY() + j + ydiff < 0 &&this.current.getPiece()[i][j] != null){//rock bottom
						still_looking = false;
						not_low = false;
					}
				}
			}
			if (still_looking) {
				--ydiff;
			}
			else {
				++ydiff;
			}
		}
		this.cp.translate(0,ydiff);
	}

	public void drawNPS() {		//render following pieces
		Queue<Electromino> tmp = new LinkedList<Electromino>();
		int xi = (int)(this.bounds.width * .15);
		int yi = (int)(this.bounds.height * .17);		//from bottom upwards
		int n = 0;

		while (this.nes.size() > 0) {
			Electromino h = this.nes.remove();
        	
			int b = 0;
			if (h.getC() != 6 && h.getC() != 0) {
				b = 1;
			}
			else {
				b = 0;
			}
			for (int i = 0; i < h.getPiece().length; i++) {
				for (int j = 0; j < h.getPiece()[0].length; j++) {
					if (h.getPiece()[i][j] != null) {
						if (h.getC() == 6) {
							this.gfx2D.setColor(Color.yellow);
						}
						else if (h.getC() == 5) {
							this.gfx2D.setColor(Color.green);
						}
						else if (h.getC() == 4) {
							this.gfx2D.setColor(Color.cyan);
						}
						else if (h.getC() == 3) {
							this.gfx2D.setColor(Color.magenta);
						}
						else if (h.getC() == 2) {
							this.gfx2D.setColor(Color.blue);
						}
						else if (h.getC() == 1) {
							this.gfx2D.setColor(Color.orange);
						}
						else if (h.getC() == 0) {
							this.gfx2D.setColor(Color.red);
						}
						this.gfx2D.fillRect(xi + (i * this.SQ_HEIGHT) + (n * this.SQ_HEIGHT), yi - ((j + b) * this.SQ_HEIGHT), this.SQ_HEIGHT, this.SQ_HEIGHT);
						this.gfx2D.setColor(Color.black);
					}
				}
			}
			//n = n + h.getPiece().length + 1;
			n = n + 5;
			tmp.add(h);
		}
		//if (tmp.peek().getC() != 6) {
			this.gfx2D.drawRect(xi - (this.SQ_HEIGHT/2), yi - (3 * this.SQ_HEIGHT), 5 * this.SQ_HEIGHT , 4 * this.SQ_HEIGHT);
			//}
		//else {
		//	this.gfx2D.drawRect(xi + (this.SQ_HEIGHT/2), yi - (int)((-1.5 + tmp.peek().getPiece().length) * this.SQ_HEIGHT), (tmp.peek().getPiece().length - 1) * this.SQ_HEIGHT , (tmp.peek().getPiece().length - 1) * this.SQ_HEIGHT);
		//}
		this.nes = tmp;
	}

	public void drawFPS() {
		String fps = new String("FPS: " + (new DecimalFormat("#.###")).format((frame_count/((double)(System.nanoTime() - this.SYSTEM_TIME) * .000000001))));
		frame_count = 0;
		if (this.isOver) {
			this.gfx.setColor(Color.white);
		}
		else {
			this.gfx2D.setColor(Color.black);
		}
		this.gfx2D.drawString(fps, (int)(this.bounds.width * .75), (int)(this.bounds.height * .1) - 10);
		this.SYSTEM_TIME = System.nanoTime();
	}

	public void drawPN() {			//total pieces dropped
		String num = new String("Piece: " + (new DecimalFormat("####")).format(this.PN));
		this.gfx2D.setColor(Color.black);
		this.gfx2D.drawString(num, (int)(this.bounds.width * .75), (int)(this.bounds.height * .75) - 10);
	}

	public void drawCurrent() {
		int xi = (int)(this.bounds.width * .25) - (5 * this.SQ_HEIGHT);
		int yi = (int)(this.bounds.height * .55) + (10 * this.SQ_HEIGHT) - this.SQ_HEIGHT;		//from bottom upwards and we start drawing from the top right
		for (int i = 0; i < this.GRID.length; i++) {
			for (int j = 0; j < this.GRID[0].length; j++) {
				if (this.GRID[i][j] != null) {
					if (this.GRID[i][j].getC() == 6) {
						this.gfx2D.setColor(Color.yellow);
					}
					else if (this.GRID[i][j].getC() == 5) {
						this.gfx2D.setColor(Color.green);
					}
					else if (this.GRID[i][j].getC() == 4) {
						this.gfx2D.setColor(Color.cyan);
					}
					else if (this.GRID[i][j].getC() == 3) {
						this.gfx2D.setColor(Color.magenta);
					}
					else if (this.GRID[i][j].getC() == 2) {
						this.gfx2D.setColor(Color.blue);
					}
					else if (this.GRID[i][j].getC() == 1) {
						this.gfx2D.setColor(Color.orange);
					}
					else if (this.GRID[i][j].getC() == 0) {
						this.gfx2D.setColor(Color.red);
					}
					this.gfx2D.fillRect(xi + (i * this.SQ_HEIGHT), yi - (j * this.SQ_HEIGHT), this.SQ_HEIGHT, this.SQ_HEIGHT);
				}
			}
		}
		
		//System.out.println("ydiff: " + ydiff + " x: " + this.cp.getX() + " y: " + this.cp.getY());
		int counter = 0;
		for (int i = 0; i < this.current.getPiece().length; i++) {
			for (int j = 0; j < this.current.getPiece()[0].length; j++) {
				if (this.current.getPiece()[i][j] != null) {
					if (this.current.getC() == 6) {
						this.gfx2D.setColor(Color.yellow);
					}
					else if (this.current.getC() == 5) {
						this.gfx2D.setColor(Color.green);
					}
					else if (this.current.getC() == 4) {
						this.gfx2D.setColor(Color.cyan);
					}
					else if (this.current.getC() == 3) {
						this.gfx2D.setColor(Color.magenta);
					}
					else if (this.current.getC() == 2) {
						this.gfx2D.setColor(Color.blue);
					}
					else if (this.current.getC() == 1) {
						this.gfx2D.setColor(Color.orange);
					}
					else if (this.current.getC() == 0) {
						this.gfx2D.setColor(Color.red);
					}
					this.gfx2D.fillRect(xi + ((i + (int)this.cp.getX()) * this.SQ_HEIGHT), yi - ((j + (int)this.cp.getY()) * this.SQ_HEIGHT), this.SQ_HEIGHT, this.SQ_HEIGHT);
					//this.gfx2D.setColor(Color.black);
					//this.gfx2D.drawString("" + ++counter, xi + ((i + (int)this.cp.getX()) * this.SQ_HEIGHT), yi - ((j + (int)this.cp.getY() - 1) * this.SQ_HEIGHT));
					if (j + this.cp.getY() >= 20) {
						this.isOver = true;
						this.isGame = false;
					}
				}
			}
		}
	}

	public void clearRow() {
		for (int i = 0; i < this.GRID[0].length; i++) {
			boolean full = true;
			for (int j = 0; j < this.GRID.length; j++) {
				if (this.GRID[j][i] == null) {
					full = false;
				}
			}
			if (full) {
				SQ[][] tmp = new SQ[10][20];
				for (int x = 0; x < tmp[0].length; x++) {
					for (int y = 0; y < tmp.length; y++) {
						if (x < i) {
							if (this.GRID[y][x] != null) {
								tmp[y][x] = this.GRID[y][x];
							}
						}
						else if (x > i) {				//skip the cleared row
							if (this.GRID[y][x] != null) {
								tmp[y][x - 1] = this.GRID[y][x];
							}
						}
					}
				}
				this.GRID = tmp;
			}
		}
	}

	public void cleanGFX() {
		this.gfx.setColor(Color.white);
		this.gfx.fillRect(0,0,this.bounds.width,this.bounds.height);
		this.gfx.setColor(Color.black);
	}


	public void showFin() {
		this.gfx.setColor(Color.black);
		this.gfx.fillRect(0,0,this.bounds.width,this.bounds.height);
		this.gfx.setColor(Color.white);
		this.gfx2D.setFont(this.end_font);
		this.gfx2D.drawString("GAME OVER", (this.bounds.width/2) - (33 * ("GAME OVER".length())), (this.bounds.height/2) - 36);
		this.gfx2D.setFont(this.font);
	}
}