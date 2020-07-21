
public class Board {
	static byte[][] board = new byte[8][8];
	boolean[][] checkedFiveHor = new boolean[8][8];
	boolean[][] checkedFiveCorner = new boolean[8][8];
	boolean[][] checkedFour = new boolean[8][8];
	boolean[][] checkedThree = new boolean[8][8];
	
	public Board(byte[][] b){
		board = b;
	}
	
	public static int get(int r, int c){
		if(r < 8 && c < 8)
			return board[r][c];
		return -1;
	}
	
	public boolean checkPair(int r, int c, int r2, int c2){
		if(r < 8 && c < 8 && r2 < 8 && c2 < 8)
			if(r >= 0 && c >= 0 && r2 >= 0 && c2 >= 0)
				return board[r][c] == board[r2][c2];
		return false;	// In the case of invalid coordinates, the caller response to "not equal" is equivalent to "do not check".
	}
	
//	public boolean checkedFiveHor(int r, int c){
//		if(r < 8 && c < 8)
//			return checkedFiveHor[r][c];
//		return true;	// In the case of invalid coordinates, the caller response to "already checked" is equivalent to "do not check".
//	}
}
