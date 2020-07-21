import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


//import org.opencv.core.Core.*;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

@SuppressWarnings("unused")
public class Main {
	static String[] images = new String[]{"positive_images/red/red2.png", "positive_images/blue/blue2.png",
				"positive_images/green/green2.png", "positive_images/yellow/yellow2.png", 
				"positive_images/purple/purple2.png", "positive_images/orange/orange2.png", 
				"positive_images/white/white2.png" };
	
	static String[] colourNames = new String[]{"Red", "Blue", "Green", "Yellow", "Purple", "Orange", "White"};
	static Mat[] colours = new Mat[8];
	
	static int[] hues = new int[12];
	static final int RESIZE = 3;	// 16 solved normal_windowed perfectly, increasing size doesn't decrease solve rate
	static Mat[] pieces = new Mat[images.length];
	static int height = 104;
	static int width = height;
	static int firstX = 680;
	static int firstY = 110;
	static Board board;
	
 static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
 public static void main(String args[]) throws InterruptedException, AWTException, IOException {	 

	 initializeKnownPieces();
	 
	 Scanner reader = new Scanner(System.in);
	 String ready = "";

	 Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	 Robot bot = new Robot();
	 Mat image = null;
     BufferedImage capture = null;
     
     List<Move> moves;
     Move theMove;
     


//     testScreen("normal_windowed.png");
    
     
     for(;;){
    	 capture = bot.createScreenCapture(screenRect);
    	 ImageIO.write(capture, "png", new File("temp.png"));
    	 image = Imgcodecs.imread("temp.png");
    	 board = new Board(capturePieces(image));
    	 moves = determineMoves(board);
    	 theMove = chooseMove(moves);
    	 makeMove(theMove);
//    	 ready = reader.nextLine();
     }
	 

//	 createGUI(pieces); // TODO!
     
//     orientScreen();
//	 practiceMoving();
//     testFeatureComparison();
//     testSpectrum();
//     testCvOpen();
//	   pressingFastOnlineGame();
//     screenCaptureAndDisplay();
//	   trackMousePosition();
 }
 
 
 	private static void testScreen(String testImageName) throws IOException, AWTException {
 		Board board;
 		Mat image;
 		List<Move> moves;
 		Move theMove;
		Scanner reader = new Scanner(System.in);
 		String ready = "";
 		orientScreen();
 		 for(;;){
 	    	 image = Imgcodecs.imread(testImageName);
 	    	 board = new Board(capturePieces(image));
 	    	 moves = determineMoves(board);
 	    	 theMove = chooseMove(moves);
 	    	 makeMove(theMove);
 	    	 ready = reader.nextLine();
 	     }
 	}


	private static void orientScreen() throws IOException, AWTException {
		 BufferedImage capture;
		 Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		 Robot bot = new Robot();
		 Scanner reader = new Scanner(System.in);
		 String ready = "";
		 Mat image = Imgcodecs.imread("hint_windowed.png"); 
		 displayImage(image, "original");
		 System.out.println("ORIENT GAME USING SCREENSHOT, PRESS ANY KEY WHEN YOU BELIEVE IT'S READY");
	     
		 while(!(ready.equals("Y") || ready.equals("y"))){
	    	 System.out.println("Veryify board. Type 'Y' when ready");
	    	 sleep(2000);
	    	 capture = bot.createScreenCapture(screenRect);
	    	 ImageIO.write(capture, "png", new File("temp.png"));
	    	 image = Imgcodecs.imread("temp.png");
	    	 alignBoardPieces(image);
	    	 ready = reader.nextLine();
	     }
	     reader.close();	 
	     
	     alignBoardPieces(image);
 	}


	private static void practiceMoving() {
		  Robot bot = null;
		  try {
			  bot = new Robot();
		  } catch (Exception e) {
			  System.err.println("Failed instantiating Robot: " + e);
		  }
		  int mask = InputEvent.BUTTON1_DOWN_MASK;
		  sleep(2000);
		  int x = (int) (firstX + (width*0.5));
		  int y = (int) (firstY + (height*0.7));
		  sleep(2000);
		  for(int i=0; i<8; i++){
			  for(int j=0; j<8; j++){
				  bot.mouseMove(x + (width*j), y);
				  sleep(100);
			  }
			  x = (int) (firstX + (width*0.5));
			  y += height;
		  }
 	}


	private static void makeMove(Move m) {
// 		static int height = 104;
// 		static int width = height;
// 		static int x = 680;
// 		static int y = 110;
 		  Robot bot = null;
 		  try {
 			  bot = new Robot();
 		  } catch (Exception e) {
 			  System.err.println("Failed instantiating Robot: " + e);
 		  }
 		  int mask = InputEvent.BUTTON1_DOWN_MASK;
 		  
 		  bot.mouseMove(680 + 52, 110 + 52);
// 		  sleep(5000);
 		  
// 		  int distance = 100;
 		  int firstCenterX = (int) (firstX + (width*.5));
 		  int firstCenterY = (int) (firstY + (height*.8));
 		  
 		  int x1 = (int) (m.x*width + firstCenterX);
 		  int y1 = (int) (m.y*height + firstCenterY);
 		  int x2 = x1;
 		  int y2 = y1;
 		  if(m.dir == 0)	   // North
 			  y2 = y1 - height;
 		  else if(m.dir == 1) // East
 			  x2 = x1 + width;
 		  else if(m.dir == 2) // South
 			  y2 = y1 + height;
 		  else if(m.dir == 3) // West
 			  x2 = x1 - width;
 		  
 		  bot.mouseMove(x1, y1);
 		  bot.mousePress(mask);
// 		  sleep(2000);
 		  bot.mouseMove(x2, y2);
 		  bot.mouseRelease(mask);
 		  bot.mouseMove(0, 0);
 		  
 		  System.out.println(m.toString());
 		  System.out.println(x1 + " " + y1);
 		  System.out.println(x2 + " " + y2 + "\n");
 	}


	private static Move chooseMove(List<Move> moves) {
 		//TODO tie-break between best scoring moves. Choose one that preserves the other for next turn, or random if both?
 		Move best = new Move();
 		int m = moves.size();
 		m = (int) (Math.random()*m)-1;
 		if( m < 0)
 			m = 0;
 		best = moves.get(m);
 		return best;
// 		for(Move m : moves){
// 			if(m.score > best.score)
// 				best = m;
// 		}
// 		return best;
 	}


	private static List<Move> determineMoves(Board b) {
 		List<Move> moves = new ArrayList<Move>();
 		
 		for(int r=0; r<8; r++){
 			for(int c=0; c<8; c++){
 				if(b.checkPair(r, c, r, c+1)){
 					moves.add(checkFiveHor(b, r, c, r, c+1));
 					moves.add(checkFiveCornerHor(b, r, c, r, c+1));
 					moves.add(checkFourHor(b, r, c, r, c+1));
 					moves.add(checkThreeHor(b, r, c, r, c+1));
 				}
 				if(b.checkPair(r,  c,  r+1,  c)){
 					moves.add(checkFiveVer(b, r, c, r+1, c));
 					moves.add(checkFiveCornerVer(b, r, c, r+1, c));
 					moves.add(checkFourVer(b, r, c, r+1, c));
 					moves.add(checkThreeVer(b, r, c, r+1, c));
 				}
 			}
 		}
 		List<Move> validMoves = new ArrayList<Move>(0);
		for(int i=0; i<moves.size(); i++){
			if(moves.get(i).score != -1){
				//TODO sort moves by score here?
				validMoves.add(moves.get(i));
			}
		}
 		System.out.println("DONE");
 		return validMoves;
 	}

 	// 0 sb W
 	// 1 sb S
 	// 2 sb E
 	// 3 sb N
 	
	private static Move checkThreeVer(Board b, int r, int c, int i, int c2) {
		if(b.checkPair(r, c, r-1, c-1))
			return new Move(r-1, c-1, Move.SOUTH, 3, 1);
		if(b.checkPair(r, c, r-2, c))
			return new Move(r-2, c, Move.EAST, 3, 2);
		if(b.checkPair(r, c, r-1, c+1))
			return new Move(r-1, c+1, Move.NORTH, 3, 3);
		if(b.checkPair(r, c, r+2, c-1))
			return new Move(r+2, c-1, Move.SOUTH, 3, 4);
		if(b.checkPair(r, c, r+3, c))
			return new Move(r+3, c, Move.WEST, 3, 5);
		if(b.checkPair(r, c, r+2, c+1))
			return new Move(r+2, c+1, Move.NORTH, 3, 6);
		return new Move();
	}


	private static Move checkFourVer(Board b, int r, int c, int i, int c2) {
		if(b.checkPair(r, c, r-2, c)){			
			if(b.checkPair(r, c, r-2, c-1))
				return new Move(r-2, c-1, Move.SOUTH, 4, 7);
			if(b.checkPair(r, c, r-2, c+1))
				return new Move(r-2, c+1, Move.NORTH, 4, 8);
		}
		if(b.checkPair(r, c, r-3, c)){
			if(b.checkPair(r, c, r-2, c-1))
				return new Move(r-1, c-1, Move.SOUTH, 4, 9);
			if(b.checkPair(r, c, r-2, c+1))
				return new Move(r-2, c+1, Move.NORTH, 4, 10);
		}
		
		return new Move();
	}

	/*
	 * 	--O--
	 * 	--O--
	 * 	-1-OO
	 *	--2--
	 *
	 * 	--2--
	 * 	-1-OO
	 *	--O--
	 *	--O--
	 */
	private static Move checkFiveCornerVer(Board b, int r, int c, int i, int c2) {
		if(b.checkPair(r, c, r-2, c+1) && (b.checkPair(r-2, c+1, r-2, c+2))){
			if(b.checkPair(r, c, r-2, c-1))
				return new Move(r-2, c-1, Move.SOUTH, 5, 11);
			if(b.checkPair(r, c, r-3, c))
				return new Move(r-3, c, Move.WEST, 5, 12);
		}
		if(b.checkPair(r, c, r-1, c+1) && (b.checkPair(r-1, c+1, r-1, c+2))){
			if(b.checkPair(r, c, r-2, c))
				return new Move(r-2, c, Move.EAST, 5, 13);
			if(b.checkPair(r, c, r-1, c-1))
				return new Move(r-1, c-1, Move.SOUTH, 5, 14);
		}
		return new Move();
	}


	private static Move checkFiveVer(Board b, int r, int c, int i, int c2) {
		if(b.checkPair(r, c, r-3, c) && b.checkPair(r-3, c, r-4, c)){
			if(b.checkPair(r, c, r-2, c-1))
				return new Move(r-2, c-1, Move.SOUTH, 6, 15);
			if(b.checkPair(r, c, r-2, c+1))
				return new Move(r-2, c+1, Move.NORTH, 6, 16);
		}
		return new Move();
	}

	/*
	 * 	--1--4--
	 * 	-2-OO-5-
	 *	--3--6--
	 */
	private static Move checkThreeHor(Board b, int r, int c, int r2, int c2) {
		if(b.checkPair(r, c, r-1, c-1))
			return new Move(r-1, c-1, Move.EAST, 3, 17);
		if(b.checkPair(r, c, r, c-2))
			return new Move(r, c-2, Move.SOUTH, 3, 18);
		if(b.checkPair(r, c, r+1, c-1))
			return new Move(r+1, c-1, Move.WEST, 3, 19);
		if(b.checkPair(r, c, r-1, c+2))
			return new Move(r-1, c+2, Move.EAST, 3, 20);
		if(b.checkPair(r, c, r, c+3))
			return new Move(r, c+3, Move.NORTH, 3, 21);
		if(b.checkPair(r, c, r+1, c+2))
			return new Move(r+1, c+2, Move.WEST, 3, 22);
		return new Move();
	}

	/*
	 * 	--1-
	 * 	OO-O
	 *	--2-
	 *
	 *	-1--
	 * 	O-OO
	 *	-2--
	 */
	private static Move checkFourHor(Board b, int r, int c, int r2, int i) {
		if(b.checkPair(r, c, r, c+3)){
			if(b.checkPair(r, c, r-1, c+2))
				return new Move(r-1, c+2, Move.EAST, 4, 23);
			if(b.checkPair(r, c, r+1, c+2))
				return new Move(r+1, c+2, Move.WEST, 4, 24);
		}
		if(b.checkPair(r, c, r, c-2)){
			if(b.checkPair(r, c, r-1, c-1))
				return new Move(r-1, c-1, Move.EAST, 4, 25);
			if(b.checkPair(r, c, r+1, c-1))
				return new Move(r+1, c-1, Move.WEST, 4, 26);
		}
			
		return new Move();
	}

	/*
	 * 	--O--
	 * 	--O--
	 * 	OO-2-
	 *	--1--
	 *
	 * 	--1--
	 * 	OO-2-
	 *	--O--
	 *	--O--
	 */
	private static Move checkFiveCornerHor(Board b, int r, int c, int r2, int c2) {
		if(b.checkPair(r, c, r2-1, c2+1) && b.checkPair(r2-1, c2+1, r2-2, c2+1)){
			if(b.checkPair(r, c, r, c+3))
				return new Move(r, c+3, Move.NORTH, 5, 27);
			if(b.checkPair(r, c, r+1, c+2))
				return new Move(r+1, c+2, Move.WEST, 5, 28);
		}
		if(b.checkPair(r, c, r+1, c+2) && b.checkPair(r+1, c+2, r+2, c+2)){
			if(b.checkPair(r, c, r-1, c+2))
				return new Move(r-1, c+2, Move.EAST, 5, 29);
			if(b.checkPair(r, c, r, c+3))
				return new Move(r, c+3, Move.NORTH, 5, 30);
		}
		return new Move();
	}

	/*
	 * 	--1--
	 * 	OO-OO
	 *	--2--
	 */
	private static Move checkFiveHor(Board b, int r, int c, int r2, int c2) {
//		if(b.checkedFiveHor(r, c) || b.checkedFiveHor(r2, c2))
//			return new Move();
		if(b.checkPair(r, c, r, c+3) && b.checkPair(r, c+3, r, c+4)){
			if(b.checkPair(r, c, r+2, c+1))
				return new Move(r+2, c+1, Move.EAST, 6, 31);
			if(b.checkPair(r,  c,  r+2,  c-1))
				return new Move(r+2, c-1, Move.WEST, 6, 32);
		}
		return new Move();
	}


	private static void createGUI(byte[][] pieces) {
 		Mat show = new Mat(8*width, 8*height, 0);
// 		Mat show = new Mat();
// 		show.reshape(8*width, 8*height);
 		Mat temp;
 		for(int i=0; i<8; i++){
 			for(int j=0; j<8; j++){
 				
// 				displayImage(colours[pieces[i][j]], i + " " + j);
// 				show.adjustROI(0+i*width, 0+j*height, width, height);
 				
 				
 				
 				
 				
// 				colours[pieces[i][j]].copyTo(show.submat(i*width,i*width+width, j*height, j*height+height));
 				temp = colours[pieces[i][j]];//.copyTo(show.submat(new Rect(0+i*width, 0+j*height, width, height)));
 				temp.copyTo(show.submat(new Rect(0+i*width, 0+j*height, width, height)));
 				
 				
 				
 				
// 				show.adjustROI(0, 0, 8*width, 8*height);
 				System.out.println((0+i*width) + " " +  (0+j*height));
 				System.out.print("");
// 				temp = colours[pieces[i][j]];
 			}
 			displayImage(show, "ALL!");
 		}
 	}


	private static void initializeKnownPieces(){
// 		static int redLow = 170;
// 		static int redHigh = 180;
// 		static int blueLow = 80;
// 		static int blueHigh = 130;
// 		static int greenLow = 50;
// 		static int greenHigh = 70;
// 		static int yellowLow = 30;		 // 10 if overlapping /w orange
// 		static int yellowHigh = 40;
// 		static int purpleLow = 140;
// 		static int purpleHigh = 160;
// 		static int orangeLow = 0;
// 		static int orangeHigh = 20; 		// 40 if overlapping /w yellow
 		hues[0] = 170;
 		hues[1] = 180;
 		hues[2] = 80;
 		hues[3] = 130;
 		hues[4] = 50;
 		hues[5] = 70;
 		hues[6] = 30;
 		hues[7] = 40;
 		hues[8] = 140;
 		hues[9] = 160;
 		hues[10] = 0;
 		hues[11] = 20;
 		
 		Mat newMat;
 		for(int i=0; i<images.length; i++){ 			
	 		newMat = Imgcodecs.imread(images[i]);
	 		colours[i] = newMat;
	 		newMat = resize(newMat);
			newMat = grey(newMat);
			pieces[i] = newMat;
 		}
 	}

 	private static void alignBoardPieces(Mat screenShot){
 		byte[][] board = new byte[8][8];

 		 Rect roi = new Rect(firstX, firstY, height, width);
 		 Mat testMat;
 		 int x = firstX;
 		 int y = firstY;
 		 for(int j=0; j<8; j++){
 			 for(int i=0; i<8; i++){
 				 roi = new Rect(x+(width*i), y, height, width);
 				 Mat newMat = new Mat(screenShot, roi);
 				 if(i == 0 || i == 7)
 					 displayImage(newMat,  "Piece at " + i + " " + j);
 			 }
 			 
 			 x = 680;
 			 y += height;
 		 }
 	}
	
 	private static byte[][] capturePieces(Mat screenShot){
 		byte[][] board = new byte[8][8];

 		 Rect roi = new Rect(firstX, firstY, height, width);
 		 
 		 int x = firstX;
 		 int y = firstY;
 		 for(int j=0; j<8; j++){
 			 for(int i=0; i<8; i++){
 				 roi = new Rect(x+(width*i), y, height, width);
 				 Mat newMat = new Mat(screenShot, roi);
 				 newMat = resize(newMat);
 				 newMat = grey(newMat);
// 				 newMat = averageColor(newMat);
 				 board[i][j] = identifyPiece(newMat);
 			 }
 			 x = 680;
 			 y += height;
 		 }
 		 return board;
 	}


 	private static Mat averageColor(Mat m) {
 		double total = 0;
 		double average = -1;
 		for(int i=0; i<m.rows(); i++){
 			for(int j=0; j<m.cols(); j++){
 				total += m.get(i, j)[0];
 			}
 		}

 		average = total / (m.rows()*m.cols());
 		
 		
 		double temp = 0;
 		for(int i=0; i<m.rows(); i++){
 			System.out.println(average);
 			for(int j=0; j<m.cols(); j++){
 				temp = m.get(i, j)[0];
 		 		System.out.println("Temp " + temp);

 				if(temp > average)
 					m.put(i, j, Short.MAX_VALUE);
 				else
 					m.put(i, j, 0);
 			}
 		}
 		
 		return m;
	}

	private static Mat grey(Mat big) {
 		Mat small = new Mat();
 		Imgproc.cvtColor(big, small, Imgproc.COLOR_BGR2GRAY);
 		return small;
	}

	private static void displayPieces(Mat[][] pieces){
 		 for(int j=0; j<8; j++){
 			 for(int i=0; i<8; i++){
// 				 String type = identifyPiece(pieces[i][j]);
// 				 displayImage(pieces[i][j], i + " " + j + " " + type);
 				 displayImage(pieces[i][j], i + " " + j);
 			 }
 		 }
 	}
 	
	private static byte identifyPiece(Mat found) {
		Mat orig;
		int diff;
		int[] differences = new int[7];
		for(int piece=0; piece<pieces.length; piece++){
			orig = pieces[piece];
			diff = 0;
			for(int i=0; i<orig.height(); i++){
				for(int j=0; j<orig.width(); j++){
					if(found.get(i, j)[0] - orig.get(i, j)[0] != 0)
						diff += 1;
				}
			}
			differences[piece] = diff;
		}
		int smallest = differences[0];
		byte index = 0;
		for(byte i=1; i<differences.length; i++){
			if(smallest > differences[i]){
				smallest = differences[i];	
				index = i;
			}
//			System.out.println("Differences: " + i + " " + differences[i]);
		}
		return index;
		
//		int[] similarity = new int[7];
//		int similar = 0;
//		int index = 0;
//	    Mat testColor = new Mat();
//	    
//	    for(int i=0; i<hues.length; i+=2){
//	    	Core.inRange(found, new Scalar(hues[i], 100, 100), new Scalar(hues[i+1], 255, 255), testColor);
//	    	displayImage(testColor, images[i/2] + "?");
//	    	for(int j=0; j<testColor.rows(); j++){
//	    		for(int k=0; k<testColor.cols(); k++){
//	    			double[] temp = testColor.get(j, k);
//	    			similar += testColor.get(j, k)[0];
//	    		}
//	    	}
//	    	similarity[index] = similar;
//	    	similar = 0;
//	    	index++;
//	    }
//	    similar = similarity[0];
//	    for(int i=0; i<similarity.length; i++){
//	    	if(similarity[i] > similar){
//	    		similar = similarity[i];
//	    		index = i;
//	    	}
//	    }
//	    return images[index];
	}


	private static void testFeatureComparison() {
		 Mat img = Imgcodecs.imread("cat.jpg");
	     MatOfKeyPoint keyPoints = new MatOfKeyPoint();
	     FeatureDetector detect = FeatureDetector.create(1);
	     detect.detect(img, keyPoints);
	     KeyPoint[] keys = keyPoints.toArray(); // sort by keyPoint.response?
	     System.out.println("Num key points: " + keys.length + " in feature " +  1);
	     
	     DescriptorExtractor extract = DescriptorExtractor.create(5);
	     Mat descriptors = new Mat();
	     extract.compute(img, keyPoints, descriptors);
	     System.out.println("Found " + descriptors.rows() + " descriptors, each with dimension " + descriptors.cols());
		 
	     Mat original = Imgcodecs.imread("hidden_cat.png");
	     MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();
	     detect.detect(original, keyPoints2);
	     Mat descriptors2 = new Mat();
	     extract.compute(original, keyPoints2, descriptors2);
	     System.out.println("Found " + descriptors2.rows() + " descriptors, each with dimension " + descriptors2.cols());
	
	     DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
	     MatOfDMatch dmatches = new MatOfDMatch();
	     matcher.match(descriptors, descriptors2, dmatches);
	     
	     Mat correspondence = new Mat();
	     Features2d.drawMatches(img, keyPoints, original, keyPoints2, dmatches, correspondence);
	     displayImage(correspondence, "Correspondence");
	     displayImage(original, "Original");
	}

private static Mat resize(Mat m){
	Mat ret = new Mat();
	Imgproc.resize(m, ret, new Size(RESIZE, RESIZE));
	return ret;
}

private static void testSpectrum() {

	 int low;
	 int high;
	 Mat mat = new Mat();
	for(int i=0; i<7; i++){
		Imgproc.cvtColor(Imgcodecs.imread(images[i]), mat, Imgproc.COLOR_BGR2HSV);
		for(int j=0; j<18; j++){
			low = j*10;
			high = (j+1) * 10;
			testColor(mat, low, high, images[i]+" "+low+" "+high);
		}
	}
}

private static Mat testColor(Mat mat, int lower, int upper, String name){
     Mat testColor = new Mat();
     Core.inRange(mat, new Scalar(lower, 100, 100), new Scalar(upper, 255, 255), testColor);
     displayImage(testColor, name);
     return testColor;
 }
 
 private static void displayImage(Image img){
	 Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
     ImageIcon icon = new ImageIcon(img);
     JFrame frame = new JFrame();
     frame.setLayout(new FlowLayout());        
     frame.setSize(img.getWidth(null)+100, img.getHeight(null)+100);     
     if(frame.getWidth() < 500)
    	 frame.setSize(500, img.getHeight(null)+100);
     JLabel lbl = new JLabel();
     lbl.setIcon(icon);
     frame.add(lbl);
     frame.setTitle("TEST");
     frame.setVisible(true);
 }
 
 private static void displayImage(Image img, String name){
	 Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
     ImageIcon icon = new ImageIcon(img);
     JFrame frame = new JFrame();
     frame.setLayout(new FlowLayout());        
     frame.setSize(img.getWidth(null)+100, img.getHeight(null)+100);     
     if(frame.getWidth() < 500)
    	 frame.setSize(500, img.getHeight(null)+100);
     JLabel lbl = new JLabel();
     lbl.setIcon(icon);
     frame.add(lbl);
     frame.setTitle(name);
     frame.setVisible(true);
//     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

 }
 
 private static void displayImage(Mat m, String name){
	 displayImage(toBufferedImage(m), name);
 }
 
 private static void testCvOpen() {
   System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
   Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1 );
   System.out.println( "mat = " + mat.dump() );
}

private static void pressingFastOnlineGame() {
     //http://www.urban75.com/Mag/java7.html
	  Robot bot = null;
	  try {
		  bot = new Robot();
	  } catch (Exception failed) {
		  System.err.println("Failed instantiating Robot: " + failed);
	  }
	  int mask = InputEvent.BUTTON1_DOWN_MASK;	  
	  sleep(3000);
	  bot.mouseMove(911, 614);
	  bot.mousePress(mask);
	  sleep(10);
	  bot.mouseRelease(mask);
	  bot.mouseMove(965, 617);
	  for(int i=0; i<6000; i++){
		  bot.mousePress(mask);
		  bot.mouseRelease(mask);
	  }
}

private static void testScreenCaptureAndDisplay() throws AWTException {
	 Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
     BufferedImage capture = new Robot().createScreenCapture(screenRect);
     ImageIcon icon = new ImageIcon(capture);
     JFrame frame = new JFrame();
     frame.setLayout(new FlowLayout());        
     frame.setSize(capture.getWidth(null)/2, capture.getHeight(null)/2);     
//     frame.setSize(500, 500);     
     JLabel lbl = new JLabel();
     lbl.setIcon(icon);
     frame.add(lbl);
     frame.setVisible(true);
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

     for(;;){
    	 capture = new Robot().createScreenCapture(screenRect);
         icon = new ImageIcon(capture);
         frame.remove(lbl);
         lbl.setIcon(icon);
         frame.add(lbl);
         frame.repaint();
         frame.setVisible(true);
     }
}

	private static Image toBufferedImage(Mat m){
	     int type = BufferedImage.TYPE_BYTE_GRAY;
	     if ( m.channels() > 1 ) {
	         type = BufferedImage.TYPE_3BYTE_BGR;
	     }
	     int bufferSize = m.channels()*m.cols()*m.rows();
	     byte [] b = new byte[bufferSize];
	     m.get(0,0,b); // get all the pixels
	     BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
	     final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	     System.arraycopy(b, 0, targetPixels, 0, b.length);  
	     return image;
	 }

	private static void sleep(int t){
 		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
 	}
 
	private static void trackMousePosition() {
		Point p;
		for(;;){
			p = MouseInfo.getPointerInfo().getLocation();
			System.out.println("X: " + p.getX());
			System.out.println("Y: " + p.getY() + "\n");
			sleep(5000);
		}
	}
}