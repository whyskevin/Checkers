import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class Mouse implements MouseListener {
	final int Range = 75;
	private int selectedRow;
	private int selectedCol;
	private Tiles tile;
	private Pieces piece;
	private Board board;

	@Override
	public void mousePressed(MouseEvent e) {		//The Mouse Listener handles most of the events that occur in this program
												
		tile = (Tiles) e.getSource();	
		tile.setBorder((BorderFactory.createLoweredBevelBorder()));
		if (tile == null) {
			return; // Should never happen
		}
		this.board = tile.getBoard();
		if (tile.isOccupied()) { //Determines if the tile holds a piece
			this.piece = tile.getPiece(); // Gets the piece at the tile.

			if (piece.getSide() == board.turn()) { //If the turn corresponds with Red/Black player's turn
				System.out.println("---------------------");	//Output for organized debugging
				board.clearPotentialMoves();
				this.selectedRow = this.piece.getRow(); // Get selected piece coordinates
				this.selectedCol = this.piece.getCol();
				tile.selected(true);					//Passed as selected!	
				this.board.getRootRowCol(this.selectedRow, this.selectedCol);	//Gives the board the coordinates of the clicked tile/piece
				System.out.println("Piece occupies: " + selectedRow + ","
						+ selectedCol);
			} else {
				System.err.println("It's not your turn");
				return;
			}
		} else if (!tile.isOccupied()) {		//If tile has no pieces within it...
			tile.coord();						
			tile.selected(false);				//Head to board functions
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	tile.setBorder(BorderFactory.createEmptyBorder());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
