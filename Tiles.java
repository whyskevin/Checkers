import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class Tiles extends JPanel {
	final private int WIDTH = 75;
	final private int HEIGHT = 75;
	private Pieces piece;
	private Board board;
	private boolean PieceAdded = false;
	private int row;
	private int col;
	private boolean select;

	public Tiles(int row, int col, Board b) {
		this.row = row;
		this.col = col;
		this.board = b;
	}

	public Board getBoard() {
		return this.board;
	}

	public void coord() {
		System.out.println("This is tile (" + row + "," + col + ").");
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public void addPiece(Pieces p) {
		piece = p;
		PieceAdded = true;
	}

	public boolean isOccupied() {
		if (piece != null) {
			return true;
		} else
			return false;
	}

	public void selected(boolean bool) {
		this.select = bool;
		if (this.select == false) { // If the destination is a non-piece holding tile, it will set the destination coordinates
			board.movePieces(row, col); // Gives board destination coordinates
		}
		this.select = false;		//Refreshes the function
	}

	public Pieces getPiece() {
		return this.piece;
	}

	public void delete() {
		this.piece = null;
	}

	protected void paintComponent(Graphics g) { // Paints tiles. If piece is
												// present, paints a circle
												// within the tile.
		Graphics2D g2 = (Graphics2D) g;

		if ((row + col) % 2 == 0) {
			g2.setColor(Color.WHITE);
		} else {
			g2.setColor(Color.GRAY);
		}
		g2.fillRect(0, 0, WIDTH, HEIGHT);

		if (piece != null) {
			if (piece.getType() == PieceType.BLACK
					|| piece.getType() == PieceType.BLACK_KING) {
				g2.setColor(Color.BLACK);
				if (piece.getType() == PieceType.BLACK_KING) {
					g2.setColor(Color.LIGHT_GRAY);					//Black kings become light gray
				}
			} else if (piece.getType() == PieceType.RED
					|| piece.getType() == PieceType.RED_KING) {
				g2.setColor(Color.RED);
				if (piece.getType() == PieceType.RED_KING) {
					g2.setColor(Color.MAGENTA);						//Red kings become bright magenta
				}
			}
			g2.fillOval(5, 5, 65, 65); // Creates "checker" look
		}
		repaint();		//Continuously repaint to make sure the pieces appear
	}
}
