package tictactoe;

import tictactoe.StdDraw;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

/**
 * This class (with the help of the StdDraw class,) allows a user
 * to play a game of Tic-Tac-Toe using Swing GUI. (exclusively a player
 * vs. computer game).
 * 
 * Note: The real-life player is the "X" character and the Computer is the "O" character.
 * 
 * References: Slides from Data Structures module on Moodle & Classes.
 * 
 * @author Adam Buckley (Student I.D: 20062910).
 * @version 1.
 * @date 25/01/2016.
 */

public class TicTacToe{

	// This flag is to control the level of debug messages generated
	final static boolean DEBUG = true;

	final static int EMPTY = 0;
	final static int X_SHAPE = 1;
	final static int O_SHAPE = -1;
	final static Random random = new Random();

	public static void main(String[] args) 
	{
		StartOrNotPopUp();
		overallGameActions();
	}

	/**
	 * This contains the execution of the actual game itself.
	 * This method is always called in the main method and if
	 * a user decides to play again then this method is called again
	 * via the gameEndOptions() method).
	 */
	public static void overallGameActions()
	{
		// Allocate identifiers to represent game state
		// Using an array of int so that summing along a row, a column or a
		// diagonal is a easy test for a win
		//Frame frame = new JFrame("Game Of Tic-Tac-Toe");

		int[][] board = new int[3][3];

		// Initializing variables within the main method.
		int row = 0;
		int col = 0;
		int move = 0;
		boolean boardFull;
		int playerWon;

		// Setup graphics and draw empty board.
		StdDraw.setPenRadius(0.04);							// draws thicker lines.
		StdDraw.line(0, 0.33, 1, 0.33);
		StdDraw.line(0, 0.66, 1, 0.66);
		StdDraw.line(0.33, 0, 0.33, 1.0);
		StdDraw.line(0.66, 0, 0.66, 1.0);

		StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 64)); // Font Size: Large.

		do {
			if(move % 2 ==0)
			{
				System.out.println("\tHuman move ...");

				displayUserMovePrompt();

				// use mouse position to get slot
				boolean mousePressed = false;
				do {
					if (StdDraw.mousePressed()) {

						col = (int) (StdDraw.mouseX() * 3);	

						row = (int) (StdDraw.mouseY() * 3);

						mousePressed = true;
					}
				}while(!mousePressed || board[row][col] != EMPTY);
				board[row][col] = X_SHAPE;   // valid move (empty slot)
				StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 20)); // Font SIZE!
				//StdDraw.clear(0.5, 0.1, StdDraw.LIGHT_GRAY);
				StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 64)); // Font SIZE!
			}
			else
			{
				System.out.println("\tComputer move ...");

				displayCompMovePrompt();

				int[] ComputerToWinPositions = computerToWin(board);

				if (ComputerToWinPositions != null)
				{
					row = ComputerToWinPositions[0];
					col = ComputerToWinPositions[1];
				}
				else if (ComputerToWinPositions == null)
				{	
					int[] ComputerToBlockPositions = computerToBlock(board);
					if (ComputerToBlockPositions != null)
					{
						row = ComputerToBlockPositions[0];
						col = ComputerToBlockPositions[1];
					}
					else
					{
						int[] ComputerRandomPositions = getEmptySquareRandom(board);
						row = ComputerRandomPositions[0];
						col = ComputerRandomPositions[1];
					}
				}

				StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 20)); // Font SIZE!
				StdDraw.text(0.5, 0.1, "         ");
				StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 64)); // Font SIZE!
				board[row][col] = O_SHAPE;   // valid move (empty slot)
				//keep a time of 650ms for the computer to make a move
				//(helps the player experience feel more authentic).
				try {
					Thread.sleep(650);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 20)); // Font SIZE!
				StdDraw.text(0.5, 0.1, "           ");
				StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 64)); // Font SIZE!
			}

			// Below: Update screen to reflect board change
			// move is the move number, col is the current move's column, row 
			//is the current move's row

			double x = col * .33 + 0.15;
			double y = row * .33 + 0.15;

			//Directly below: board reflecting the change: if the move int value is an even
			//number then the user's X value position choice is reflected but it move is an
			//odd number the computer's (random) position choice is reflected on this update.

			StdDraw.text(x, y, (move % 2 == 0 ? "X" : "O"));
			move++;
			System.out.println(move);
			boardFull = isBoardFull(board);
			playerWon = hasSomeoneWon(board);
			// Below: While the board isn't full and neither the user or the computer have won:
		}while (!boardFull && playerWon == 0);

		switch (playerWon) 
		{
		case EMPTY:
			//JOptionPane.showMessageDialog(null, "The Game is Over: It is a draw");
			gameEndOptions(playerWon, board);
			break;
		case X_SHAPE:
			gameEndOptions(playerWon, board);
			break;
		case O_SHAPE:
			gameEndOptions(playerWon, board);
			break;
		}
	}

	/**
	 * This method clears all the board positions for the 
	 * start of a brand new game when a game was played 
	 * immediately before the brand new game.
	 * 
	 * @param int[][] - A two dimensional array called board is 
	 * passed into this method.  (this is board itself as it is currently).
	 */
	private static void clear(int[][] board)
	{
		for (int col = 0; col < 3; col++)
		{
			for (int row = 0; row < 3; row++)
			{
				double x = col * .33 + 0.15;
				double y = row * .33 + 0.15;

				if (board[row][col] == X_SHAPE || board[row][col] == O_SHAPE)
				{
					// Below: the clear each cell, pen colour is changed to white, a
					// filled white square is placed over the previous cells' areas.
					StdDraw.setPenColor(Color.WHITE);
					StdDraw.filledSquare(x, y, 0.08);
				}
				// Below: sets pen colour back to black.
				StdDraw.setPenColor();
			}
		}		
	}

	/**
	 * This method allows a pop up to appear right before the very first game
	 * is about to be played. This pop-up gives a user an option to play the game
	 * (yes button being pressed) or not play the game (no or cancel buttons being pressed).
	 */
	public static void StartOrNotPopUp()
	{
		final JFrame frame = new JFrame("JOptionPane Demo");
		int i = JOptionPane.showOptionDialog(frame,
				"Would You Like The Tic-Tac-Toe Game To begin?",
				"Tic-Tac-Toe: Video Game",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, // icon
				null,
				null,
				null);

		// Code directly below: if my i int is equals to 0 (user click user action) then the game 
		// does not terminate. However if i is not equals to 0 then the game does terminate.
		if (i != 0)
		{
			System.exit(0);
		}
	}

	/**
	 * This method allows a pop up to appear the end of each Tic-Tac-Toe game.
	 * The user is given an option to play again (yes button clicked) or not play again
	 * (either no or cancel buttons being clicked).
	 * 
	 * Note: A different pop-up window pops up based on whether the player won
	 * the game, lost the game or the game being a draw
	 * 
	 * @param int - An int value that showcases if the user won the game (1),
	 * lost the game (-1) or the game was a draw (0), is passed into the method.
	 * 
	 * @param int[][] - A two dimensional int array called
	 * board  is passed into this method as a parameter.
	 */
	public static void gameEndOptions(int playerWon, int[][] board)
	{

		final JFrame frame = new JFrame("JOptionPane Demo");
		if (playerWon == 0)
		{
			int i = JOptionPane.showOptionDialog(frame,
					"It's a Draw!.. Do You Want To Play The Game Again?",
					"Tic-Tac-Toe: Video Game",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE, // icon
					null,
					null,
					null);

			// Code directly below: if my i int is equals to 0 (user click user action) then the game 
			// does not terminate. However if i is not equals to 0 then the game does terminate.
			if (i == 0)
			{
				clear(board);
				overallGameActions();
			}
			else
			{
				System.exit(0);
			}
		}
		else if (playerWon == -1)
		{
			int i = JOptionPane.showOptionDialog(frame,
					"You Lost.. Do You Want To Try Again?",
					"Tic-Tac-Toe: Video Game",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE, // icon
					null,
					null,
					null);

			// Code directly below: if my i int is equals to 0 (user click user action) then the game 
			// does not terminate. However if i is not equals to 0 then the game does terminate.
			if (i == 0)
			{
				clear(board);
				overallGameActions();
			}
			else
			{
				System.exit(0);
			}
		}
		else if (playerWon == 1)
		{
			int i = JOptionPane.showOptionDialog(frame,
					"You Win! Do You Want To Play The Game Again?",
					"Tic-Tac-Toe: Video Game",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE, // icon
					null,
					null,
					null);

			// Code directly below: if my i int is equals to 0 (user click user action) then the game 
			// does not terminate. However if i is not equals to 0 then the game does terminate.
			if (i == 0)
			{
				clear(board);
				overallGameActions();
			}
			else
			{
				System.exit(0);
			}
		}
	}

	/**
	 * This method checks to see if the board is currently full.
	 * 
	 * @param board - a two dimensional array called board is
	 * passed into this method. (this is board itself as it is currently).
	 * 
	 * @return boolean - a boolean value: true or false is returned:
	 * true if the board is full and false is board has an empty cell.
	 */
	public static boolean isBoardFull(int[][] board)
	{

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if(board[row][col] == EMPTY)
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * This method returns an int based on if the game
	 * was won or not won.
	 * 
	 * @param int[][] - A two dimensional int array called
	 * board  is passed into this method as a parameter.
	 * (this is board itself as it is currently).
	 * 
	 * @return int - Returns an int - 1, 0 or -1.
	 * Returns 1 if the real-life user won, returns 0 if neither
	 * won and -1 if the computer won.
	 */
	public static int hasSomeoneWon(int[][] board)
	{
		for (int row = 0; row < 3; row++)
		{
			int sum = board[row][0] + board[row][1] + board[row][2];
			if (sum == 3)
			{
				return X_SHAPE;
			}
			if (sum == -3)
			{
				return O_SHAPE;
			}
		}

		for (int col = 0; col < 3; col++)
		{
			int sum = board[0][col] + board[1][col] + board[2][col];
			if (sum == 3)
			{
				return X_SHAPE;
			}
			if (sum == -3)
			{
				return O_SHAPE;
			}
		}

		int sum = board[0][0] + board[1][1] + board[2][2];
		int sum2 = board[0][2] + board[1][1] + board[2][0];

		if (sum == 3 || sum2 == 3)
		{
			//1 is returned as X_SHAPE is 1 (see global variables).
			return X_SHAPE;
		}
		if (sum == -3 || sum2 == -3)
		{
			//-1 is returned as O_SHAPE is 0 (see global variables).
			return O_SHAPE;
		}
		//below the variable 0 is returned if nobody has won the game.
		return 0;
	}

	/**
	 * This method simply returns a random empty position on
	 * the board.
	 * 
	 * @param int[][] - A two dimensional int array called
	 * board  is passed into this method as a parameter.
	 * (this is board itself as it is currently).
	 * 
	 * @return int[] - Returns an int array, position
	 * 0 gets the row value and position 1 gets the column value.
	 */
	public static int[] getEmptySquareRandom(int[][] board)
	{
		int row = 0;
		int col = 0;

		do {
			//below:random which is an object of type Random will generate a random
			//number between 0 and 2 (completely inclusive) for the eventually chosen
			//row (horizontal) and column (vertial).
			row =random.nextInt(3);
			col =random.nextInt(3);
			//below: while that specific square is empty, an int array is returned (result)
			//that contains the row in question (position 0 in int array) and the column 
			//(position 1 in int array).
		}while (board[row][col] != EMPTY);
		int[] chosenEmptySquare = {row, col};
		return chosenEmptySquare;
	}

	/**
	 * This method places the text on the bottom of the board saying that 
	 * it is the user's turn when it is the user's turn.
	 */
	public static void displayUserMovePrompt()
	{
		/*StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 20)); // Font SIZE!
		StdDraw.text(0.5, 0.03, "User Move");
		StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 64)); // Font SIZE!*/

		StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 20)); // Font SIZE!
		StdDraw.setPenColor(Color.WHITE);
		//StdDraw.rectangle(0.5, 0.03, 0.2, 0.2);
		//StdDraw.text(0.5, 0.03, "Comp Move");
		StdDraw.text(0.5, 0.03, "Comp Move");
		StdDraw.setPenColor();
		StdDraw.text(0.5, 0.03, "User Move");
		StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 64)); // Font SIZE!
	}

	/**
	 * This method places the text on the bottom of the board saying that 
	 * it is the Computer's turn when it is the Computer's turn.
	 */
	public static void displayCompMovePrompt()
	{
		/*StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 20)); // Font SIZE!
		StdDraw.text(0.5, 0.03, "Comp Move");
		StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 64)); // Font SIZE!*/

		StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 20)); // Font SIZE!
		StdDraw.setPenColor(Color.WHITE);
		//StdDraw.rectangle(0.5, 0.03, 0.2, 0.2);
		//StdDraw.text(0.5, 0.03, "User Move");
		StdDraw.text(0.5, 0.03, "User Move");
		StdDraw.setPenColor();
		StdDraw.text(0.5, 0.03, "Comp Move");
		StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 64)); // Font SIZE!
	}

	/**
	 * This method returns a position for a square
	 * for the computer to win if a potential win
	 * is already in place somewhere on the board for computer.
	 * 
	 * @param int[][] - A two dimensional int array called
	 * board is passed into this method as a parameter.
	 * (this is board itself as it is currently).
	 * 
	 * @return int[] int array - Returns an int array, position
	 * 0 gets the row value and position 1 gets the column value.
	 */
	public static int[] computerToWin(int board[][])
	{
		{
			for (int row = 0; row < 3; row++)
			{
				int sum = board[row][0] + board[row][1] + board[row][2];
				if (sum == -2)
				{
					for (int col = 0; col < 3; col++)
					{
						while (board[row][col] == EMPTY)
						{
							int[] itelligentChosenEmptySquare = {row, col};
							return itelligentChosenEmptySquare;
						}		
					}
				}
			}

			for (int col = 0; col < 3; col++)
			{
				int sum = board[0][col] + board[1][col] + board[2][col];
				if (sum == -2)
				{
					for (int row = 0; row < 3; row++)
					{
						while (board[row][col] == EMPTY)
						{
							int[] itelligentChosenEmptySquare = {row, col};
							return itelligentChosenEmptySquare;
						}		
					}
				}
			}
		}

		int sum = board[0][0] + board[1][1] + board[2][2];
		int sum2 = board[0][2] + board[1][1] + board[2][0];

		if (sum == -2)
		{
			if (board[0][0] == EMPTY || board[1][1] == EMPTY || board [2][2] == EMPTY)
			{
				for (int i = 0; i < 3; i++)
					if (board[i][i] == EMPTY)
					{
						int[] itelligentChosenEmptySquare = {i, i};
						return itelligentChosenEmptySquare;
					}
			}

			if (sum2 == -2)
			{
				if (board[0][2] == EMPTY || board[1][1] == EMPTY || board [2][0] == EMPTY)
				{
					for (int j = 0; j < 3; j++)
						for (int k = 0; k < 3; k++)
						{
							while (j+k == 2)
							{
								if (board[j][k] == EMPTY)
								{
									int[] itelligentChosenEmptySquare = {j, k};
									return itelligentChosenEmptySquare;
								}
							}
						}
				}
			}
		}
		return null;
	}

	/**
	 * This method returns a position for a square
	 * for the computer to block a real-life user win.
	 * this method only executes if a potential win does not
	 * exist for computer.
	 * This method only executes if real-life user has a potential win.
	 * 
	 * @param int[][] - A two dimensional int array called
	 * board  is passed into this method as a parameter.
	 * (this is board itself as it is currently).
	 * 
	 * @return int[] int array - Returns an int array, position
	 * 0 gets the row value and position 1 gets the column value.
	 */
	public static int[] computerToBlock(int board[][])
	{
		for (int row = 0; row < 3; row++)
		{
			int sum = board[row][0] + board[row][1] + board[row][2];
			if (sum == 2)
			{
				for (int col = 0; col < 3; col++)
				{
					while (board[row][col] == EMPTY)
					{
						int[] itelligentChosenEmptySquare = {row, col};
						return itelligentChosenEmptySquare;
					}		
				}
			}
		}

		for (int col = 0; col < 3; col++)
		{
			int sum = board[0][col] + board[1][col] + board[2][col];
			if (sum == 2)
			{
				for (int row = 0; row < 3; row++)
				{
					while (board[row][col] == EMPTY)
					{
						int[] itelligentChosenEmptySquare = {row, col};
						return itelligentChosenEmptySquare;
					}		
				}
			}
		}

		int sum = board[0][0] + board[1][1] + board[2][2];
		int sum2 = board[0][2] + board[1][1] + board[2][0];

		if (sum == 2)
		{
			if (board[0][0] == EMPTY || board[1][1] == EMPTY || board [2][2] == EMPTY)
			{
				for (int i = 0; i < 3; i++)
					if (board[i][i] == EMPTY)
					{
						int[] itelligentChosenEmptySquare = {i, i};
						return itelligentChosenEmptySquare;
					}
			}
		}

		if (sum2 == 2)
		{
			if (board[0][2] == EMPTY || board[1][1] == EMPTY || board [2][0] == EMPTY)
			{
				for (int j = 0; j < 3; j++)
					for (int k = 0; k < 3; k++)
					{
						if (j+k == 2)
						{
							if (DEBUG)
							{
								System.out.println(j);
								System.out.println(k);
							}
							if (board[j][k] == EMPTY)
							{
								int[] itelligentChosenEmptySquare = {j, k};
								return itelligentChosenEmptySquare;
							}
						}
					}
			}
		}
		return null;
	}
}