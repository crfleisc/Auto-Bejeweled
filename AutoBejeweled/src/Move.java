
public class Move {
	public static int NORTH = 0;
	public static int EAST = 1;
	public static int SOUTH = 2;
	public static int WEST = 3;
	public int x;
	public int y;
	public int dir;
	public int score;
	public int ruleNum;
	public String colour;
	
	public Move(){
		x = -1;
		y = -1;
		dir = -1;
		score = -1;
		ruleNum = -1;
	}
	
	public Move(int X, int Y, int DIR, int SCORE, int rule){
		x = X;
		y = Y;
		dir = DIR;
		score = SCORE;
		ruleNum = rule;
		colour = Main.colourNames[Main.board.get(X, Y)];
	}
	
	public String toString(){
		String direct = "ILLEGAL DIRECTION";
		if(dir == 0)
			direct = "NORTH";
		else if(dir == 1)
			direct = "EAST";
		else if(dir == 2)
			direct = "SOUTH";
		else if(dir == 3)
			direct = "WEST";
		
		return "X: " + x + "\nY: " + y + "\nD: " + direct + "\nV: " + score + "\nRULE " + ruleNum + "\n" + colour;
	}
	
	
}
