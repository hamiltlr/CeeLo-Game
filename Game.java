// CHANGE LOG //
/*
 * 10/25/2013 10:30AM Lee/Josiah - Created Game.java
 * 10/25/2013 11:00PM Josiah - Added some status logic. 
 * 10/27/13 11:15PM Josiah - More status logic
 * 10/28/13 7:30PM Lee/Josiah -  Added file logging/cleanUp method. Moved game
 * 								 logic comment into Game.java from Gui.java
 * 10/29/13 11:30AM Lee - Added final variable output to log in cleanUp method
 * 10/29/13 7:00 Lee/Josiah - Added final comments
 */

package hamilton;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * The class that handles all of the game logic.
 * 
 * In this game, each round involves two or more players of equal status. Each
 * player then has to roll all three dice at once and must continue until a
 * recognized combination is rolled. Whichever player rolls the best combination
 * wins the round, and a new round begins. In cases where two or more players
 * tie for the best combination, they must have a shoot out to determine a
 * single winner. The combinations can be ranked from best to worst as:
 * 
 * 4-5-6 The highest possible roll. If you roll 4-5-6, you automatically win.
 * 
 * Trips Rolling three of the same number is known as rolling "trips". Higher
 * trips beat lower trips, so 4-4-4 is better than 3-3-3.
 * 
 * Point: Rolling a pair, and another number, establishes the singleton as a
 * "point". A higher point beats a lower point, so 2-2-6 is better than 5-5-2.
 * 
 * 1-2-3 The lowest possible roll. If you roll 1-2-3, you automatically lose.
 * 
 * Any other roll is a meaningless combination and must be rerolled until one of
 * the above combinations occurs.
 * 
 * @author Lee Hamilton, Josiah Hamilton
 * 
 */
public class Game {

	private int player1Wins = 0;
	private int player2Wins = 0;

	private boolean player1Turn = true;
	private boolean endOfRound = false;

	private int pseudoScoreP1 = 0;
	private int pseudoScoreP2 = 0;

	private int p1ScoreFlag = -1;
	private int p2ScoreFlag = -1;

	private Die[] dice = new Die[3];
	private int[] tops = new int[3];
	private String status;

	private PrintWriter out;

	/**
	 * Initializes the logger and dice.
	 */
	public Game() {
		try {
			out = new PrintWriter("log.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 3; i++) {
			dice[i] = new Die();
			tops[i] = dice[i].roll();
		}
	}

	/**
	 * Rolls the dice and sets the status based on the outcome.
	 */
	public void rollDice() {
		for (int i = 0; i < 3; i++) {
			tops[i] = dice[i].roll();
		}

		Arrays.sort(tops);

		if (tops[0] == 4 && tops[1] == 5 && tops[2] == 6) {
			/*
			 * According to our scale, 50 is the highest pseudo score possible.
			 * A scoreFlag of 3 means that the player rolled a 4-5-6. See the
			 * compareScores method to see how each of these values is used.
			 */
			if (player1Turn) {
				pseudoScoreP1 = 50;
				p1ScoreFlag = 3;

				status = "4-5-6, how fortunate! Player 2's turn...";
				out.println(status);
			} else {
				pseudoScoreP2 = 50;
				p2ScoreFlag = 3;

				compareScores();
			}

			changePlayer();

		} else if (tops[0] == tops[1] && tops[0] == tops[2]) {
			/*
			 * This creates a trips. The pseudoscore for a trips is higher than
			 * the pseudoscore for a point value, so by multiplying the top by
			 * 7, we ensure that the trips pseudoscore is higher than the point
			 * pseudoscore (trips beats points). A scoreFlag of 2 means that the
			 * player rolled a trips. See the compareScores method to see how
			 * each of these values is used.
			 */
			if (player1Turn) {
				pseudoScoreP1 = tops[0] * 7;
				p1ScoreFlag = 2;

				status = "Player 1's trips is " + tops[0]
						+ "! Player 2's turn...";
				out.println(status);

			} else {
				pseudoScoreP2 = tops[0] * 7;
				p2ScoreFlag = 2;

				compareScores();
			}

			changePlayer();

		} else if (tops[0] == tops[1]) {
			/*
			 * Since the array is sorted, if either the first two dice are the
			 * same or the last two are the same (keeping in mind that the three
			 * dice being the same is already handled by the trips condition),
			 * our pseudoscore will be the same as the established point value,
			 * ensuring that the point pseudoscore is less than the trips
			 * pseudoscore. This condition handles the case in which the first
			 * two dice have the same top face. A scoreFlag of 1 means that the
			 * player rolled a point. See the compareScores method to see how
			 * each of these values is used.
			 */

			if (player1Turn) {
				pseudoScoreP1 = tops[2];
				p1ScoreFlag = 1;

				status = "Player 1's point is " + pseudoScoreP1
						+ ". Player 2's turn...";
				out.println(status);
			} else {
				pseudoScoreP2 = tops[2];
				p2ScoreFlag = 1;

				compareScores();
			}

			changePlayer();

		} else if (tops[1] == tops[2]) {
			/*
			 * See previous comment. This condition handles the case in which
			 * the last two dice have the same top face.
			 */
			if (player1Turn) {
				pseudoScoreP1 = tops[0];
				p1ScoreFlag = 1;

				status = "Player 1's point is " + pseudoScoreP1
						+ ". Player 2's turn...";
				out.println(status);
			} else {
				pseudoScoreP2 = tops[0];
				p2ScoreFlag = 1;

				compareScores();
			}

			changePlayer();
			
		} else if (tops[0] == 1 && tops[1] == 2 && tops[2] == 3) {
			/*
			 * According to our scale, -1 is the lowest pseudo score possible. A
			 * scoreFlag of 0 means that the player rolled a 1-2-3. See the
			 * compareScores method to see how each of these values is used.
			 */
			if (player1Turn) {
				pseudoScoreP1 = -1;
				p1ScoreFlag = 0;

				status = "1-2-3, how unfortunate. Player 2's turn...";
				out.println(status);
			} else {
				pseudoScoreP2 = -1;
				p2ScoreFlag = 0;

				compareScores();
			}

			changePlayer();

		} else {
			//If the pattern 
			if (player1Turn) {
				status = "Keep rolling, Player 1.";
				out.println(status);
			} else {
				status = "Keep rolling, Player 2";
				out.println(status);
			}
		}
	}

	/**
	 * Sets the status based on the outcome of both rolls, and adjusts the
	 * winning scores appropriately
	 */
	public void compareScores() {
		if (pseudoScoreP1 > pseudoScoreP2) {
			// Player1 wins
			if (p1ScoreFlag == 3) {
				// Player 1 won with a 4-5-6
				status = "Player 1 wins this round with a 4-5-6";

			} else if (p1ScoreFlag == 2) {
				// Player 1 won with a trips
				if (p2ScoreFlag == 2)
					//Player 1 got a higher trips than Player 2
					status = "Player 1 wins: Trips of " + pseudoScoreP1 / 7
							+ " beats Trips of " + pseudoScoreP2 / 7;

				else if (p2ScoreFlag == 1)
					//Player 1 got a trips, Player 2 got a point
					status = "Player 1 wins: Trips of " + pseudoScoreP1 / 7
							+ " beats point of " + pseudoScoreP2;

				else if (p2ScoreFlag == 0)
					//Player 1 got trips, Player 2 got 1-2-3
					status = "Player 1 wins: Trips of " + pseudoScoreP1 / 7
							+ " beats 1-2-3";

			} else if (p1ScoreFlag == 1) {
				// Player 1 won with a point
				if (p2ScoreFlag == 1)
					//Player 1 got a higher point than Player 2
					status = "Player 1 wins: Point of " + pseudoScoreP1
							+ " beats Point of " + pseudoScoreP2;

				else if (p2ScoreFlag == 0)
					//Player 1 got a point, Player 2 got a 1-2-3
					status = "Player 1 wins: Point of " + pseudoScoreP1
							+ " beats 1-2-3";
			}

			player1Wins++;
			endOfRound = true;

		} else if (pseudoScoreP1 < pseudoScoreP2) {
			// Player 2 wins
			if (p2ScoreFlag == 3) {
				// Player 2 won with a 4-5-6
				status = "Player 2 wins this round with a 4-5-6";

			} else if (p2ScoreFlag == 2) {
				// Player 2 won with a trips
				if (p1ScoreFlag == 2)
					//Player 2 got a higher trips than Player 1
					status = "Player 2 wins: Trips of " + pseudoScoreP2 / 7
							+ " beats Trips of " + pseudoScoreP1 / 7;

				else if (p1ScoreFlag == 1)
					//Player 2 got a trips, Player 1 got a point
					status = "Player 2 wins: Trips of " + pseudoScoreP2 / 7
							+ " beats point of " + pseudoScoreP1;

				else if (p1ScoreFlag == 0)
					//Player 2 got a trips, Player 1 got a 1-2-3
					status = "Player 2 wins: Trips of " + pseudoScoreP2 / 7
							+ " beats 1-2-3";

			} else if (p2ScoreFlag == 1) {
				// Player 2 won with a point
				if (p1ScoreFlag == 1)
					//Player 2 got a higher point than Player 1
					status = "Player 2 wins: Point of " + pseudoScoreP2
							+ " beats Point of " + pseudoScoreP1;

				else if (p1ScoreFlag == 0)
					//Player 2 got a point, Player 1 got a 1-2-3
					status = "Player 2 wins: Point of " + pseudoScoreP2
							+ " beats 1-2-3";
			}

			player2Wins++;
			endOfRound = true;

		} else {
			//If the pseudoScores are equal
			status = "It's a tie! Tiebreaker to determine winner";
			endOfRound = true;
		}

		out.println(status);

	}

	/**
	 * Changes which player is currently rolling.
	 */
	private void changePlayer() {
		player1Turn = !player1Turn;
	}

	/**
	 * Starts a new round.
	 */
	public void newRound() {
		pseudoScoreP1 = 0;
		pseudoScoreP2 = 0;
		endOfRound = false;
	}

	/**
	 * Returns the number of wins for player 1.
	 * 
	 * @return the number of wins for player 1.
	 */
	public int getPlayer1Wins() {
		return player1Wins;
	}

	/**
	 * Returns the number of wins for player 2.
	 * 
	 * @return the number of wins for player 2.
	 */
	public int getPlayer2Wins() {
		return player2Wins;
	}

	/**
	 * Returns true if it's player 1's turn.
	 * 
	 * @return true if it's player 1's turn.
	 */
	public boolean isPlayer1Turn() {
		return player1Turn;
	}

	/**
	 * Returns true it's the end of the round.
	 * 
	 * @return true it's the end of the round.
	 */
	public boolean isEndOfRound() {
		return endOfRound;
	}

	/**
	 * Returns the pseudo score of player 1.
	 * 
	 * @return the pseudo score of player 1.
	 */
	public int getPseudoScoreP1() {
		return pseudoScoreP1;
	}

	/**
	 * Returns the pseudo score of player 2.
	 * 
	 * @return the pseudo score of player 2.
	 */
	public int getPseudoScoreP2() {
		return pseudoScoreP2;
	}

	/**
	 * Returns the three die tops.
	 * 
	 * @return the three die tops.
	 */
	public int[] getTops() {
		return tops;
	}

	/**
	 * Returns the current status.
	 * 
	 * @return the current status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Writes the last values to the log and closes the file.
	 */
	public void cleanUp() {
		out.println();
		out.println("Final values: ");

		out.println("P1:");
		out.println("\tplayer1Wins = " + player1Wins);
		out.println("\tpseudoScoreP1 = " + pseudoScoreP1);

		out.println("P2:");
		out.println("\tplayer2Wins = " + player2Wins);
		out.println("\tpseudoScoreP2 = " + pseudoScoreP2);

		out.close();
	}
}
