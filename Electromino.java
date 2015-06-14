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

	public Electromino (int i) {
		this.type = new Integer(i);
		if (i == 6) {
			this.piece = new SQ[4][4];
			for (int x = 1; x < 3; x++) {
				for (int y = 1; y < 3; y++) {
					this.piece[x][y] = new SQ(i);
				}
			}
		}
		else if (i == 0) {
			this.piece = new SQ[4][4];
			this.piece[0][2] = new SQ(i);
			this.piece[1][2] = new SQ(i);
			this.piece[2][2] = new SQ(i);
			this.piece[3][2] = new SQ(i);
		}
	}

	public Integer getC () {
		return type;
	}

	public SQ[][] getPiece () {
		return piece;
	}
}