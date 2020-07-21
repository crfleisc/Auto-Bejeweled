# Auto-Bejeweled
Uses OpenCV-3.0.0 to identify board pieces and the Java Robot class to manipulate the mouse and make moves.

- Pet project done during my "spare time" in university. Needs serious housekeeping and refactoring.
- Bejeweled must be played in windowed mode for the Robot class to be able to interact with it.
- Uses a greedy algorithm to choose which piece to move: the more gems connected the better, with ties
  broken by whichever it saw first (closest to the top of the board).

