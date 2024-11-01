package dicegame;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

// Dice class to handle dice rolling
class Dice {
    private static final Random random = new Random();

    // Method to roll a die and get a random number between 1 and 6
    public int roll() {
        return random.nextInt(6) + 1;
    }
}

// Player class to store each player's scores and total score
class Player {
    private final int[] scores = new int[11]; // Array to hold scores for each column (2-12)
    private int totalScore = 0; // Total score for the player

    public Player() {
        // Initialize all scores to zero 
        for (int i = 0; i < scores.length; i++) {
            scores[i] = 0;
        }
    }

    // Get the score in a specific column
    public int getScore(int column) {
        return scores[column];
    }

    // Set the score in a specific column
    public void setScore(int column, int score) {
        scores[column] = score;
    }

    // Get the total score for the player
    public int getTotalScore() {
        return totalScore;
    }

    // Add points to the player's total score
    public void addPoints(int points) {
        totalScore += points;
    }
}

// Manage game state, including the game table, chosen columns, and completed columns
class GameTable {
    private static final int COLUMNS = 11; // Number of columns (for values 2 to 12)
    private static final int PLAYERS = 3; // Number of players

    private final Player[] players = new Player[PLAYERS]; // Array to store players
    private final boolean[][] chosenColumn = new boolean[PLAYERS][COLUMNS]; // Track columns chosen by each player
    private final boolean[] columnCompleted = new boolean[COLUMNS]; // Track if columns are completed
    private final String[] gameAnalysis = new String[COLUMNS]; // Array to store game analysis messages

    public GameTable() {
        // Init players
        for (int i = 0; i < PLAYERS; i++) {
            players[i] = new Player();
        }
    }

    // Get a player by index
    public Player getPlayer(int index) {
        return players[index];
    }

    // Check if a column is completed
    public boolean isColumnCompleted(int column) {
        return columnCompleted[column];
    }

    // Check if a player has chosen a specific column
    public boolean hasPlayerChosenColumn(int playerIndex, int column) {
        return chosenColumn[playerIndex][column];
    }

    // Mark a column as chosen by a player
    public void markColumnAsChosen(int playerIndex, int column) {
        chosenColumn[playerIndex][column] = true;
    }

    // Mark a column as completed
    public void markColumnAsCompleted(int column) {
        columnCompleted[column] = true;
    }

    // Store game analysis information for a column
    public void setGameAnalysis(int column, String analysis) {
        gameAnalysis[column] = analysis;
    }

    // Display the game analysis for all columns
    public void displayGameAnalysis() {
        System.out.println("\n****** Game Analysis ******");
        for (String analysis : gameAnalysis) {
            if (analysis != null) {
                System.out.println(analysis);
            }
        }
    }

    // Display the current game table, including scores and the highest scores in each column
    public void displayGameTable() {
        System.out.println();
        System.out.println(" |   ROLL!  |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  9 | 10 | 11 | 12 | Scores |");
        System.out.println("  ---------------------------------------------------------------------------");

        int[] highestInEachColumn = findHighestInColumns();

        for (int playerIndex = 0; playerIndex < PLAYERS; playerIndex++) {
            System.out.print(" | Player " + (playerIndex + 1) + " |");

            for (int col = 0; col < COLUMNS; col++) {
                int score = players[playerIndex].getScore(col);
                if (score == 0) {
                    System.out.print("    |");
                } else if (score < highestInEachColumn[col]) {
                    System.out.print("  * |"); // Mark scores that are not the highest
                } else {
                    System.out.printf(" %2d |", score);
                }
            }

            System.out.printf("   %3d  |\n", players[playerIndex].getTotalScore());
            System.out.println("  ---------------------------------------------------------------------------");
        }
    }

    // Find the highest score in each column
    private int[] findHighestInColumns() {
        int[] highestInEachColumn = new int[COLUMNS];
        for (int col = 0; col < COLUMNS; col++) {
            int highest = Integer.MIN_VALUE;
            for (Player player : players) {
                if (player.getScore(col) > highest) {
                    highest = player.getScore(col);
                }
            }
            highestInEachColumn[col] = highest;
        }
        return highestInEachColumn;
    }
}

public class DiceGame {
    private static final int ROUNDS = 11; // Total rounds in the game
    private static final int PLAYERS = 3; // Number of players
    private static final Dice dice = new Dice();
    private static final GameTable gameTable = new GameTable();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        playGame(); // Run the game
        determineWinner(); // Determine the winner after all rounds are completed
        gameTable.displayGameAnalysis(); // Display game analysis for each column
    }

    // Main game loop to play each round and handle each player's turn
    private static void playGame() {
        System.out.println("Ready to rock and roll? ... Or maybe just roll?");
        for (int round = 0; round < ROUNDS; round++) {
            System.out.println("--------------------------");
            System.out.println("****** Round " + (round + 1) + " ******");
            System.out.println("--------------------------");
            gameTable.displayGameTable(); // Display the game table at the start of each round

            for (int playerIndex = 0; playerIndex < PLAYERS; playerIndex++) {
                System.out.println("\nPlayer " + (playerIndex + 1) + "'s turn:");
                waitForThrow(); // Wait for the player to press 't' to roll

                // Roll dice and calculate the total score
                int dice1 = dice.roll();
                int dice2 = dice.roll();
                int total = dice1 + dice2;
                System.out.printf("[%d][%d]" + "\nPlayer " + (playerIndex + 1) + " scored %d \n", dice1, dice2, total);

                // Player chooses a column to place their score
                int columnChoice = chooseColumn(playerIndex, round);
                gameTable.getPlayer(playerIndex).setScore(columnChoice, total);
                gameTable.markColumnAsChosen(playerIndex, columnChoice);
                System.out.println("Player " + (playerIndex + 1) + " chose column " + (columnChoice + 2));

                gameTable.displayGameTable(); // Display updated game table

                // Check and complete the column immediately after each player chooses it
                if (!gameTable.isColumnCompleted(columnChoice)) {
                    evaluateColumn(columnChoice); // Evaluate the column for completion and scoring
                }
            }
        }

        System.out.println("\nFinal Score Table:");
        gameTable.displayGameTable(); // Display final game table after all rounds
    }

    // Wait for player to press 't' to roll the dice
    private static void waitForThrow() {
        System.out.print("Enter 't' to take your throw: ");
        while (!scanner.next().equalsIgnoreCase("t")) {
            System.out.print("Invalid input. Enter 't' to take your throw: ");
        }
    }

    // Allow player to choose a column, ensuring valid input and checking for any constraints
    private static int chooseColumn(int playerIndex, int round) {
        int columnChoice;
        while (true) {
            columnChoice = getInput("Enter the column in which you wish to insert your score: (2 to 12) ", 2, 12) - 2;
            if (round == 0 && isColumnTaken(columnChoice, playerIndex)) {
                System.out.println("For round 1, you must select a different column to other players.");
            } else if (!gameTable.hasPlayerChosenColumn(playerIndex, columnChoice)) {
                return columnChoice;
            } else {
                System.out.println("That position is already assigned for Player " + (playerIndex + 1) + " ... try again.");
            }
        }
    }

    // Handle user input with validation
    private static int getInput(String prompt, int min, int max) {
        int input;
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
                scanner.next();
            }
        }
        return input;
    }

    // Check if the chosen column has already been selected by another player in the same round
    private static boolean isColumnTaken(int columnChoice, int currentPlayer) {
        for (int otherPlayer = 0; otherPlayer < PLAYERS; otherPlayer++) {
            if (otherPlayer != currentPlayer && gameTable.hasPlayerChosenColumn(otherPlayer, columnChoice)) {
                return true;
            }
        }
        return false;
    }

    // Evaluate and complete a column when all players have selected it
    private static void evaluateColumn(int col) {
        int highestScore = Integer.MIN_VALUE;
        int winner = -1;
        boolean tie = false;
        int playerCountInColumn = 0;

        // Check all players' scores in this column to find the highest score and check for ties
        for (int playerIndex = 0; playerIndex < PLAYERS; playerIndex++) {
            int score = gameTable.getPlayer(playerIndex).getScore(col);
            if (score != 0) { // Only consider non-zero scores
                playerCountInColumn++;
                if (score > highestScore) {
                    highestScore = score;
                    winner = playerIndex;
                    tie = false;
                } else if (score == highestScore) {
                    tie = true;
                }
            }
        }

        // Mark the column as complete and apply scoring if all players have entered scores
        if (playerCountInColumn == PLAYERS) {
            if (tie) {
                System.out.println("Column " + (col + 2) + " is now complete.");
                System.out.println("However, there is a tie, so nothing is added to any player's score.");
                gameTable.setGameAnalysis(col, "Column " + (col + 2) + " was tied.");
            } else if (winner != -1) { // Only award points if there is a non-zero highest score
                int columnValue = col + 2;
                gameTable.getPlayer(winner).addPoints(columnValue);
                System.out.println("Column value " + columnValue + " is now complete.");
                System.out.println("Player " + (winner + 1) + " has the highest value so earns " + columnValue + " points, added to their score.");
                gameTable.setGameAnalysis(col, "Column " + columnValue + " won by Player " + (winner + 1) + ".");
            }
            gameTable.markColumnAsCompleted(col); // Mark the column as completed
        }
    }

    // Determine the winner after all rounds are completed
    private static void determineWinner() {
        int maxScore = Integer.MIN_VALUE;
        int winner = -1;
        boolean tie = false;

        // Find the player with the highest total score
        for (int playerIndex = 0; playerIndex < PLAYERS; playerIndex++) {
            int score = gameTable.getPlayer(playerIndex).getTotalScore();
            if (score > maxScore) {
                maxScore = score;
                winner = playerIndex;
                tie = false;
            } else if (score == maxScore) {
                tie = true;
            }
        }

        System.out.println("The game is now completed.");
        if (tie) {
            System.out.println("It's a tie! No winner.");
        } else {
            System.out.println("Player " + (winner + 1) + " wins with a score of " + maxScore + "!");
        }
    }
}
