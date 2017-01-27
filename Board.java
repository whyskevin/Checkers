import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Board extends JComponent {
	// Numerous private variables with different characteristics
	final private int COL = 8;
	final private int ROW = 8;
	private Tiles theBoard[][] = new Tiles[COL][ROW]; // The container for the
														// checkerboard
	private Tiles[][] tile = new Tiles[COL][ROW]; // Container for the tiles
	private JFrame frame;
	private JPanel panel;
	private ArrayList<Pieces> redPieces = new ArrayList<>(); // Holds all the
																// pieces.
	private ArrayList<Pieces> blackPieces = new ArrayList<>(); // Used in
																// construction
																// of the board
	private JMenuBar menuBar;
	private JMenu menu; // Menu
	private JMenuItem help;
	private JMenuItem resign; // Resign option
	private int redCounter = 0;
	private int blackCounter = 0;
	private int destRow = 0;
	private int destCol = 0;
	private int currentRow = 0;
	private int currentCol = 0;
	private int preyRow = 0; // Coordinates of piece being eaten
	private int preyCol = 0;
	private int turnCounter = 0;
	private Pieces lastPieceMoved; // Proposed for a path-finding function...
	private Player RED; // Players of the game
	private Player BLACK;
	private ArrayList<Pieces> nextPiece = new ArrayList<>();
	private String loser; // Prints losing side

	public Board() {
		createComponents();
		addingTiles();
		makePieces();
		createMenu();
		BLACK = new Player(PlayerType.BLACK);
		RED = new Player(PlayerType.RED);
		frame.add(panel);
		frame.setVisible(true);
	}

	public void checkWin() { // If one side has no pieces left, the other side
								// wins. No draw game functionality
		if (RED.piecesLeft() == 0) {
			loser = "Red";
			displayDialog();
			frame.dispose();
		} else if (BLACK.piecesLeft() == 0) {
			loser = "Black";
			displayDialog();
			frame.dispose();
		}
	}

	public void switchTurns() { // Once a move is made, switch/increment turns
		turnCounter++;
	}

	public void clearPotentialMoves() {
		nextPiece.clear();
	}

	public PlayerType turn() { // Makes sure turns are alternating. Black goes
								// first.
		if (turnCounter % 2 == 1) {
			return PlayerType.RED;
		} else {
			return PlayerType.BLACK;
		}
	}

	public void getRootRowCol(int row, int col) { // Passes in clicked
													// piece/tile coordinates
		currentRow = row;
		currentCol = col;
	}

	public void createComponents() { // Creation of JComponents
		frame = new JFrame();
		frame.setSize(new Dimension(600, 623));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new JPanel(new GridLayout(ROW, COL));
	}

	public void addingTiles() { // Creation and storage of Tiles. Tiles extends
								// JPanel
		Mouse m = new Mouse();
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				tile[i][j] = new Tiles(i, j, this); // Tile(row, column, Board
													// this);
				theBoard[i][j] = tile[i][j];
				theBoard[i][j].addMouseListener(m);
				panel.add(theBoard[i][j]); // i is row, j is column
			}
		}
	}

	public void makePieces() { // Creation and storage of Pieces
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				if ((i + j) % 2 == 1) {
					if (i < 3) { // Creates red pieces up to row 3
						redPieces.add(new Pieces(i, j, PieceType.RED)); // (row,
																		// column,
																		// type)
						theBoard[i][j].addPiece(redPieces.get(redCounter));
						redCounter++;
					} else if (i > 4 && i < 8) { // Creates black pieces up from
													// row 5
						blackPieces.add(new Pieces(i, j, PieceType.BLACK));
						theBoard[i][j].addPiece(blackPieces.get(blackCounter));
						blackCounter++;
					}
				}
			}
		}
	}

	public void checkingTheCrown(Pieces p, int destRow, int destCol) { // Checks
																		// if a
																		// piece
																		// has
																		// qualified
																		// to
																		// become
																		// a
																		// King
		if (p.getType() == PieceType.RED && destRow == 7) {
			p.crowned();
		} else if (p.getType() == PieceType.BLACK && destRow == 0) {
			p.crowned();
		}
	}

	public Tiles getTile(int xCoord, int yCoord) {
		if ((xCoord >= 0 && xCoord <= 7) && (yCoord >= 0 && yCoord <= 7)) {
			return theBoard[xCoord][yCoord];
		} else
			return null;
	}

	public int returnTurns() {
		return turnCounter;
	}

	public boolean checkJump(Pieces jumper) { // Checks if a piece may jump.
												// Jumper is the clicked piece.
												// For very direct movements
		currentRow = jumper.getRow();
		currentCol = jumper.getCol();
		System.out.println("Jumper begins at " + currentRow + "," + currentCol);
		int rowDistance = (destRow - jumper.getRow());
		int colDistance = (destCol - jumper.getCol());
		preyRow = jumper.getRow() + (rowDistance / 2); // Location of "prey"
														// piece
		preyCol = jumper.getCol() + (colDistance / 2);

		if (!theBoard[destRow][destCol].isOccupied() // If the destination is
														// not occupied and the
														// "prey" location is
				&& (theBoard[preyRow][preyCol].isOccupied())) {
			return true;
		} else {
			System.err.println("Cannot jump to " + destRow + "," + destCol);
			switchTurns();
			return false;
		}
	}

	public boolean jumpAvailable(Pieces jumper) { // Checks numerous potential
													// destinations
		int switchCase = 0, RowMovement = 0, jumperRow = 0, jumperCol = 0;
		if (jumper.getType() == PieceType.RED) { // Depending on the piece type,
													// switch case checks
													// different areas
			switchCase = 1;
			RowMovement = 2; // Red pieces may only move South
		} else if (jumper.getType() == PieceType.BLACK) {
			switchCase = 2; // Black pieces may only move North
			RowMovement = -2;
		} else if (jumper.getType() == PieceType.RED_KING // Kings move in all 4
															// directions
				|| jumper.getType() == PieceType.BLACK_KING) {
			switchCase = 3;
		}

		jumperRow = jumper.getRow();
		jumperCol = jumper.getCol();

		switch (switchCase) {
		case 1: { // Red pieces
			if ((jumperRow > -1 && jumperRow < 8)
					&& (jumperCol > -1 && jumperCol < 8)) { // Checks if within
															// board bounds
				if ((jumperRow + RowMovement) <= 7) { // Checks if row is <= 7
					if (jumperCol != 7 && jumperCol < 6 && jumperCol != 0
							&& jumperCol > 1) { // If the selected piece is not
												// near any edges
						if (!theBoard[jumperRow + RowMovement][jumperCol + 2] // Check
																				// right
																				// location
								.isOccupied()
								&& theBoard[jumperRow + 1][jumperCol + 1]
										.isOccupied()) {
							return true;
						}
						if (!theBoard[jumperRow + RowMovement][jumperCol - 2] // Check
																				// left
																				// location
								.isOccupied()
								&& theBoard[jumperRow + 1][jumperCol - 1]
										.isOccupied()) {
							return true;
						}
						return false;
					}

					if (jumperCol >= 6) { // if jumper is close to 7 cols, check
											// movement toward 0 col
						if (!theBoard[jumperRow + RowMovement][jumperCol - 2]
								.isOccupied()
								&& theBoard[jumperRow + 1][jumperCol - 1]
										.isOccupied()) {
							return true;
						}
					}
					if (jumperCol <= 1) {
						if (!theBoard[jumperRow + RowMovement][jumperCol + 2]
								.isOccupied()
								&& theBoard[jumperRow + 1][jumperCol + 1]
										.isOccupied()) {
							return true;
						}
					}
					return false;
				} else
					return false;
			}
		}
			break;
		case 2: { // Black pieces
			if ((jumperRow > -1 && jumperRow < 8)
					&& (jumperCol > -1 && jumperCol < 8)) { // Checks if within
															// board bounds
				if ((jumperRow + RowMovement) > -1) { // if row within bounds

					if (jumperCol != 7 && jumperCol < 6 && jumperCol != 0
							&& jumperCol > 1) { // if column less than 6 and
												// greater than 1. check both
												// sides

						if (!theBoard[jumperRow + RowMovement][jumperCol + 2]
								.isOccupied()
								&& theBoard[jumperRow - 1][jumperCol + 1]
										.isOccupied()) {
							return true;
						}
						if (!theBoard[jumperRow + RowMovement][jumperCol - 2]
								.isOccupied()
								&& theBoard[jumperRow - 1][jumperCol - 1]
										.isOccupied()) {
							return true;
						}
						return false;
					}
					if (jumperCol >= 6) { // if jumper is close to 7 cols, check
											// movement toward 0 col
						if (!theBoard[jumperRow + RowMovement][jumperCol - 2]
								.isOccupied()
								&& theBoard[jumperRow - 1][jumperCol - 1]
										.isOccupied()) {
							return true;
						}
					}
					if (jumperCol <= 1) {
						if (!theBoard[jumperRow + RowMovement][jumperCol + 2]
								.isOccupied()
								&& theBoard[jumperRow - 1][jumperCol + 1]
										.isOccupied()) {
							return true;
						}
					}
					return false;
				} else
					return false;
			}
		}
			break;
		case 3: { // King availability
			int KingNorth = jumperRow - 2;
			int KingEast = jumperCol + 2;
			int KingSouth = jumperRow + 2;
			int KingWest = jumperCol - 2;
			if (KingSouth <= 7 && KingEast <= 7 && KingNorth >= 0
					&& KingWest >= 0) { // If destination is within bounds
				System.out.println(KingSouth + " " + KingEast + " " + KingNorth
						+ " " + KingWest);
				if (!theBoard[KingNorth][KingEast].isOccupied()
						&& theBoard[jumperRow - 1][jumperCol + 1].isOccupied()) {
					System.out.println("NorthEast open");
					return true;
				}
				if (!theBoard[KingNorth][KingWest].isOccupied()
						&& theBoard[jumperRow - 1][jumperCol - 1].isOccupied()) {
					System.out.println("NorthWest open");
					return true;
				}
				if (!theBoard[KingSouth][KingEast].isOccupied()
						&& theBoard[jumperRow + 1][jumperCol + 1].isOccupied()) {
					System.out.println("SouthEast open");
					return true;
				}
				if (!theBoard[KingSouth][KingWest].isOccupied()
						&& theBoard[jumperRow + 1][jumperCol - 1].isOccupied()) {
					System.out.println("SouthWest open");
					return true;
				}
			}
			if ((jumperRow == 0 || jumperRow == 1)
					&& ((KingEast <= 7) && (KingWest >= 0))) { // Column to
				// far north
				if (!theBoard[KingSouth][KingEast].isOccupied()
						&& theBoard[jumperRow + 1][jumperCol + 1].isOccupied()) {
					return true;
				}
				if (!theBoard[KingSouth][KingWest].isOccupied()
						&& theBoard[jumperRow + 1][jumperCol - 1].isOccupied()) {
					return true;
				}
			}
			if ((jumperRow == 7 || jumperRow == 6)
					&& ((KingEast <= 7) && (KingWest >= 0))) { // Column to
				// far south
				if (!theBoard[KingNorth][KingEast].isOccupied()
						&& theBoard[jumperRow - 1][jumperCol + 1].isOccupied()) {
					return true;
				}
				if (!theBoard[KingNorth][KingWest].isOccupied()
						&& theBoard[jumperRow - 1][jumperCol - 1].isOccupied()) {
					return true;
				}
			}
			if ((jumperRow == 7 || jumperRow == 6)
					&& ((KingNorth >= 0) && (KingSouth <= 7))) { // Column to
				// far right
				if (!theBoard[KingNorth][KingWest].isOccupied()
						&& theBoard[jumperRow - 1][jumperCol - 1].isOccupied()) {
					return true;
				}
				if (!theBoard[KingSouth][KingWest].isOccupied()
						&& theBoard[jumperRow + 1][jumperCol - 1].isOccupied()) {
					return true;
				}
			}
			if ((jumperRow == 0 || jumperRow == 1)
					&& ((KingNorth >= 0) && (KingSouth <= 7))) { // Column to
				// far left
				if (!theBoard[KingNorth][KingEast].isOccupied()
						&& theBoard[jumperRow - 1][jumperCol + 1].isOccupied()) {
					return true;
				}
				if (!theBoard[KingSouth][KingEast].isOccupied()
						&& theBoard[jumperRow + 1][jumperCol + 1].isOccupied()) {
					return true;
				}
			}
			return false;
		}
		default:
			System.err.println("Default case");
			break;
		}
		return false;
	}

	public void jumpPieces(Pieces jumper) {

		Pieces prey = theBoard[preyRow][preyCol].getPiece();
		Tiles t = theBoard[currentRow][currentCol];
		jumper = t.getPiece();
		if (checkJump(jumper)) {
			if (jumper.getType() == prey.getType()) {
				jumper.talk();
				prey.talk();
				System.err.println("Cannot eat same side piece");
				return;
			}
			theBoard[destRow][destCol].addPiece(jumper);
			theBoard[currentRow][currentCol].delete();
			jumper.moved(destRow, destCol);
			lastPieceMoved = jumper;
			checkingTheCrown(jumper, destRow, destCol);
			theBoard[preyRow][preyCol].delete();
			if (prey.getType() == PieceType.RED
					|| prey.getType() == PieceType.RED_KING) {
				RED.pieceEaten();
			} else if (prey.getType() == PieceType.BLACK
					|| prey.getType() == PieceType.BLACK_KING) {
				BLACK.pieceEaten();
			}
			checkWin();
		}
		if (jumpAvailable(lastPieceMoved)) {
			if (checkJump(lastPieceMoved)) {
				jumpPieces(lastPieceMoved);
			} else
				return;
		} else
			return;
	}

	public void movePieces(int dRow, int dCol) {
		destRow = dRow;
		destCol = dCol;
		System.out.println(currentRow + "," + currentCol
				+ " would like to go to " + destRow + "," + destCol);
		if ((theBoard[currentRow][currentCol].isOccupied())
				&& ((destRow + destCol) % 2 == 1)) { // Gray tiles
			Pieces root = theBoard[currentRow][currentCol].getPiece();
			if (jumpAvailable(root) == false) {
				if (root.getType() == PieceType.BLACK_KING
						|| root.getType() == PieceType.RED_KING) {
					if ((Math.abs(destRow - currentRow) == 1)
							|| (Math.abs(destCol - currentCol) == 1)) {

						if (theBoard[destRow][destCol].isOccupied() == false) {
							theBoard[destRow][destCol].addPiece(root);
							theBoard[currentRow][currentCol].delete();
							root.moved(destRow, destCol);
							lastPieceMoved = root;
							System.out.println("Root piece moved to " + destRow
									+ "," + destCol);
							switchTurns();
						}
					}

					// Normal piece movement
				} else if ((root.getType() == PieceType.BLACK || root.getType() == PieceType.RED)) {

					if ((root.getType() == PieceType.RED && (destRow > currentRow))
							|| (root.getType() == PieceType.BLACK && (destRow < currentRow))) {
						if ((Math.abs(destRow - currentRow) == 1)
								|| (Math.abs(destCol - currentCol) == 1)) {

							if (theBoard[destRow][destCol].isOccupied() == false) {
								theBoard[destRow][destCol].addPiece(root);
								theBoard[currentRow][currentCol].delete();
								root.moved(destRow, destCol);
								lastPieceMoved = root;
								// System.out.println("Last piece moved "
								// + lastPieceMoved.getRow() + ","
								// + lastPieceMoved.getCol());
								switchTurns();
								System.out.println("Root piece moved to "
										+ destRow + "," + destCol);
								checkingTheCrown(root, destRow, destCol);
							}
						}

					} else {
						System.err
								.println("Normal pieces can't move backwards");
						return;
					}
				}
			} else {
				if (checkJump(root)) {
					jumpPieces(root);
					switchTurns();
				}
			}
		} else {
			System.err.println("Cannot move onto white tile bounds");
			return;
		}
	}

	public void displayDialog() {
		JOptionPane.showMessageDialog(frame, loser
				+ "-side player lost! Well played!");
	}

	public void createMenu() {
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		resign = new JMenuItem("Resign");
		help = new JMenuItem("Help");
		help.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								frame,
								"Checkers/Draughts is a board game designed to be played by two players.\n"
								+ "\nThe objective is to \"eat\" all the pieces of the other side. This game is played only on the darker tiles of the board."
								+ "\nNormal pieces may only move diagonally forward one space at a time, if a same-side piece is present, they are not able to move."
								+ "\nPieces may only eat other-side pieces if there is another piece diagonal to them, and the tile behind that piece is open."
								+ "\nIf the opportunity to eat a piece is present, the player must eat the piece.\n"
								+ "\nNormal pieces that reach the other end of the board from their side are crowned king. Kings may move diagonally forwards and backwards.\n"
								+ "\nThe first move is made by the black player side. Good luck and have fun!");
			}

		});
		resign.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (turnCounter % 2 == 1) {
					RED.lost();
					loser = "Red";
				} else {
					BLACK.lost();
					loser = "Black";
				}
				JOptionPane.showMessageDialog(frame, loser
						+ "-side player resigned! Good game.");
				frame.dispose();
			}
		});
		menuBar.add(menu);
		menu.add(help);
		menu.add(resign);
		frame.setJMenuBar(menuBar);
	}

	public static void main(String[] args) { // Main method
		Board checkerBoard = new Board();
	}
}