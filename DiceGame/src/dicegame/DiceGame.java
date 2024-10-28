package dicegame;

import java.util.InputMismatchException; //Handles invalid inputs 
import java.util.Random; // Random for dice rolls
import java.util.Scanner; // Reads inputs

public class DiceGame { //Main Class

    private static final int NUM_PLAYERS = 3; // Players
    private static final int NUM_ROUNDS = 11; // Rounds
    private static final int NUM_COLUMNS = 11; // Columns

    private static final int[][] scoreTable = new int[NUM_PLAYERS][NUM_COLUMNS]; //Stores player scores for each column in an array
    private static final boolean[][] selectedColumns = new boolean[NUM_PLAYERS][NUM_COLUMNS]; // Tracks selected columns to see if a player has chosen it already
    private static final boolean[] columnAwarded = new boolean[NUM_COLUMNS]; // Tracks when a column has been completed (scored or tie)
    private static final int[] totalScores = new int[NUM_PLAYERS]; // Total of each players scores from won columns
    private static final String[] columnSummary = new String[NUM_COLUMNS]; // Stores endgame summary
    private static final Random random = new Random(); 
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        playGame(); // Runs main game loop
        displayEndGameSummary(); // Display the summary after the game ends
    }

    private static void playGame() {
        System.out.println("Ready to roll?"); // Text formatting
        for (int round = 0; round < NUM_ROUNDS; round++) { // Iterates through rounds
            System.out.println("--------------------------"); 
            System.out.println("****** Round " + (round + 1) + " ******");
            System.out.println("--------------------------");
            displayScoreTable(); // Display table

            for (int player = 0; player < NUM_PLAYERS; player++) { // Iterates through players per round
                System.out.println("\nPlayer " + (player + 1) + "'s turn:");
                waitForThrow(); // Input for throwing dice

                // Dice Calculation 
                int dice1 = rollDice(); 
                int dice2 = rollDice();
                int total = dice1 + dice2;
                System.out.printf("[%d][%d] (Total: %d)\n", dice1, dice2, total);

                displayScoreTable(); 

                // Choose Column 
                int columnChoice = chooseColumn(player, round);
                scoreTable[player][columnChoice] = total;
                System.out.println("Player " + (player + 1) + " placed score in column " + (columnChoice + 2));

                displayScoreTable(); // Show table after placing the score
                checkCompletedColumns(); // Check and award column if needed
            }
        }
        System.out.println("\nFinal Score Table:");
        displayScoreTable(); // Show final table
        chooseWinner(); // Choose and announce the winner
    }

    // Waits for user to input 't' to throw
    private static void waitForThrow() {
        System.out.print("Press 't' to throw the dice: ");
        while (!scanner.next().equalsIgnoreCase("t")) {
            System.out.print("Invalid input. Press 't' to throw the dice: ");
        }
    }

    // Rolls by returning random number between 1 and 6
    private static int rollDice() {
        return random.nextInt(6) + 1; 
    }

    // Remove the 'total' parameter if not needed
private static int chooseColumn(int player, int round) {
    int columnChoice;
    while (true) {
        columnChoice = getInput("Choose a column (2-12) to place your total: ", 2, 12) - 2;

        // Check if the column is already taken by another player in round 1 or if a player has already placed a score into it
        if (round == 0 && isColumnTaken(columnChoice, player)) {
            System.out.println("That column is already taken by another player in this round. Please choose a different column.");
        } else if (!selectedColumns[player][columnChoice]) {
            selectedColumns[player][columnChoice] = true;
            return columnChoice;
        } else {
            System.out.println("Invalid choice or column already selected by you. Try again.");
        }
    }
}

    // Function for checking of column is taken. Returns true if it is taken
    private static boolean isColumnTaken(int columnChoice, int currentPlayer) {
        for (int otherPlayer = 0; otherPlayer < NUM_PLAYERS; otherPlayer++) {
            if (otherPlayer != currentPlayer && selectedColumns[otherPlayer][columnChoice]) {
                return true;
            }
        }
        return false;
    }

    // Ensures column choice input is correct. Loops until it is.
    private static int getInput(String prompt, int min, int max) {
        int input = -1;
        while (true) {
            System.out.print(prompt);
            try {
                input = scanner.nextInt();
                if (input >= min && input <= max) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // Clear invalid input
            }
        }
        return input;
    }

    // Checks if a column is completed in order to award points
    private static void checkCompletedColumns() {
        for (int col = 0; col < NUM_COLUMNS; col++) {
            // Loop through and check if the column has already been awarded
            if (columnAwarded[col]) {
                continue;
            }

            int nonZeroPlayers = 0;

            // Check if all players have selected this column by iterating through and incrementing players that have subbmited a score (non-zero in that column) 
            for (int player = 0; player < NUM_PLAYERS; player++) {
                if (scoreTable[player][col] != 0) {
                    nonZeroPlayers++;
                }
            }

            // Process the column only if all players have played into it
            if (nonZeroPlayers == NUM_PLAYERS) {
                checkCompletedColumn(col);
            }
        }
    }

    // Check which player has won the column 
    private static void checkCompletedColumn(int col) {
        int highestScore = Integer.MIN_VALUE; 
        int winner = -1;
        boolean tie = false;

        // Check for the highest score and if there's a tie, increment through players and update highest score with the highest in the column (once all players have been checked, the winner is decided)
        for (int player = 0; player < NUM_PLAYERS; player++) {
            if (scoreTable[player][col] > highestScore) {
                highestScore = scoreTable[player][col];
                winner = player;
                tie = false;
                // If two players have an equal score, its a tie
            } else if (scoreTable[player][col] == highestScore) {
                tie = true;
            }
        }

        // If there's a tie, no points are awarded
        if (tie) {
            System.out.println("Column " + (col + 2) + " has been drawn. No points awarded.");
            columnSummary[col] = "Column " + (col + 2) + " drawn"; // Record the tie result
        } else if (winner != -1) {
            // Award the column value to the winner
            int columnValue = col + 2;
            totalScores[winner] += columnValue;
            System.out.println("Player " + (winner + 1) + " wins column " + columnValue + " and is awarded " + columnValue + " points!");
            columnSummary[col] = "Player " + (winner + 1) + " won column " + columnValue; // Record the win
        }
        columnAwarded[col] = true; // Mark the column as awarded or drawn
    }

    // Display score table in text format
    private static void displayScoreTable() {
        System.out.println();
        System.out.println(" |   ROLL!  |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  9 | 10 | 11 | 12 | Scores |");
        System.out.println("  ---------------------------------------------------------------------------");

        int[] highestInEachColumn = findHighestInColumns(); // Compute highest scores once

        // Print each player's row
        for (int player = 0; player < NUM_PLAYERS; player++) {
            System.out.print(" | Player " + (player + 1) + " |");

            // Loop through each column, check if a score exists, and if not print a blank space.
            for (int col = 0; col < NUM_COLUMNS; col++) {
                if (scoreTable[player][col] == 0) {
                    System.out.print("    |");
                    // If the player's score is less than the highest in that column, print a *
                } else if (scoreTable[player][col] < highestInEachColumn[col]) {
                    System.out.print("  * |");  
                    // Otherwise display the players score
                } else {
                    System.out.printf(" %2d |", scoreTable[player][col]);
                }
            }

            System.out.printf("   %3d  |\n", totalScores[player]);
            System.out.println("  ---------------------------------------------------------------------------");
        }
    }

    // Find the highest number in that column, iterate through and check each score in the column. Update highest if it is higher than the current highest
    private static int[] findHighestInColumns() {
        int[] highestInEachColumn = new int[NUM_COLUMNS];
        for (int col = 0; col < NUM_COLUMNS; col++) {
            int highest = Integer.MIN_VALUE;
            for (int player = 0; player < NUM_PLAYERS; player++) {
                if (scoreTable[player][col] > highest) {
                    highest = scoreTable[player][col];
                }
            }
            highestInEachColumn[col] = highest;
        }
        return highestInEachColumn;
    }

    // Display endgame summary by displaying the columnSummary string
    private static void displayEndGameSummary() {
        System.out.println("\n****** Endgame Summary ******");
        for (int i = 0; i < NUM_COLUMNS; i++) {
            if (columnSummary[i] != null) {
                System.out.println(columnSummary[i]);
            }
        }
    }

    // Decide the winner of the game based on their total score. Call a win or a tie
    private static void chooseWinner() {
        int maxScore = Integer.MIN_VALUE;
        int winner = -1;
        boolean tie = false;

        // Loop through each player and check their scores, highest score wins, drawn scores tie.
        for (int player = 0; player < NUM_PLAYERS; player++) {
            if (totalScores[player] > maxScore) {
                maxScore = totalScores[player];
                winner = player;
                tie = false;
            } else if (totalScores[player] == maxScore) {
                tie = true;
            }
        }

        if (tie) {
            System.out.println("It's a tie! No winner.");
        } else {
            System.out.println("Player " + (winner + 1) + " wins with a score of " + maxScore + "!");
        }
    }
}
