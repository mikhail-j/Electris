/*		I = 0 = Red
		L = 1 = Orange
		J = 2 = Blue
		S = 3 = Purple
		T = 4 = Cyan
		Z = 5 = Green
		O = 6 = Yellow
*/

public class Electromino {
	private SQ[][] piece;
	private Integer type;
	private Electromino next;
	private Electromino prev;

	public Electromino (int i, SQ[][] s) {
		this.type = new Integer(i);
		this.piece = s;
	}

	public Electromino (int i) {
		this.type = new Integer(i);
		if (i == 6) {					//a square shouldn't be rotated
			this.piece = new SQ[4][4];
			for (int x = 1; x < 3; x++) {
				for (int y = 1; y < 3; y++) {
					this.piece[x][y] = new SQ(i);
				}
			}
		}
		else if (i == 1) {
			this.piece = new SQ[3][3];
			this.piece[0][0] = new SQ(i);
			this.piece[0][1] = new SQ(i);
			this.piece[1][1] = new SQ(i);
			this.piece[2][1] = new SQ(i);
			SQ[][] tmp = new SQ [3][3];
			tmp[0][2] = new SQ (i);
			tmp[1][2] = new SQ (i);
			tmp[1][1] = new SQ (i);
			tmp[1][0] = new SQ (i);
			Electromino t1 = new Electromino (i, tmp);
			this.next = t1;
			t1.setPrev(this);
			tmp = new SQ [3][3];
			tmp[0][0] = new SQ(i);
			tmp[1][0] = new SQ(i);
			tmp[2][0] = new SQ(i);
			tmp[2][1] = new SQ(i);
			Electromino t2 = new Electromino (i, tmp);
			t1.setNext(t2);
			t2.setPrev(t1);
			tmp = new SQ[3][3];
			tmp[1][2] = new SQ(i);
			tmp[1][1] = new SQ(i);
			tmp[1][0] = new SQ(i);
			tmp[2][0] = new SQ(i);
			t1 = new Electromino (i, tmp);
			t2.setNext(t1);
			t1.setPrev(t2);
			this.prev = t1;
			t1.setNext(this);
		}
		else if (i == 0) {
			this.piece = new SQ[4][4];
			this.piece[0][2] = new SQ(i);
			this.piece[1][2] = new SQ(i);
			this.piece[2][2] = new SQ(i);
			this.piece[3][2] = new SQ(i);
			SQ[][] tmp = new SQ [4][4];
			tmp[2][0] = new SQ(i);
			tmp[2][1] = new SQ(i);
			tmp[2][2] = new SQ(i);
			tmp[2][3] = new SQ(i);
			Electromino te = new Electromino (i, tmp);
			this.next = te;
			this.prev = te;
			this.next.setPrev(this);
			this.next.setNext(this);
			this.prev.setNext(this);
			this.prev.setPrev(this);
		}
	}

	public Integer getC () {
		return type;
	}

	public SQ[][] getPiece () {
		return piece;
	}

	public Electromino getNext() {
		return this.next;
	}

	public Electromino getPrev() {
		return this.prev;
	}
	public void setNext(Electromino n) {
		this.next = n;
	}
	public void setPrev(Electromino p) {
		this.prev = p;
	}
}