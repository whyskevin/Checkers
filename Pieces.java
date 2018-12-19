package Checkers;
import javax.swing.JComponent;

public class Pieces extends JComponent {

	private PieceType type;
	private PlayerType side;
	private int thisRow = 0;
	private int thisCol = 0;
	private boolean potentialLocation;
	private boolean clicked = false;

	public Pieces(int row, int column, PieceType type) {
		if (type == PieceType.RED || type == PieceType.RED_KING) {
			side = PlayerType.RED;
		} else if (type == PieceType.BLACK || type == PieceType.BLACK_KING) {
			side = PlayerType.BLACK;
		}
		thisRow = row;
		thisCol = column;
		this.type = type;
	}

	public PieceType getType() {
		return this.type;
	}

	public void crowned() {
		if (type == PieceType.RED) {
			type = PieceType.RED_KING;
			System.out.println("Red crowned");
		} else if (type == PieceType.BLACK) {
			type = PieceType.BLACK_KING;
			System.out.println("Black crowned");
		}
	}

	public PlayerType getSide() {
		return this.side;
	}

	public int getRow() {
		return this.thisRow;
	}

	public int getCol() {
		return this.thisCol;
	}
	
	public void talk(){
		System.out.println("Piece is at " + thisRow + "," + thisCol);
	}

	public void moved(int row, int col) { // Changes coordinates after being
											// moved
		System.out.println("Moved to " + row + "," + col);
		thisRow = row;
		thisCol = col;
	}

	public boolean potentialMove() {
		potentialLocation = true;
		return potentialLocation;
	}
}
