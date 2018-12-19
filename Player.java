package Checkers;

public class Player {
	private int pieces = 0;
	private PlayerType side;

	public Player(PlayerType type) {
		side = type;
		pieces = 12;
	}

	public int piecesLeft() {
		return pieces;
	}

	public void pieceEaten() {
		if(side == PlayerType.RED){
			System.out.println("Red piece eaten");
		}else if (side == PlayerType.BLACK){
			System.out.println("Black piece eaten");
		}
		pieces--;
		if(pieces == 0){
			lost();
		}
	}

	public void lost() {	//Resignation
		pieces = 0;
	}

}
