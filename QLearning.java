import java.io.*;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;

// Created by Jacob Carse on 05/08/2016.
// An implementation of the Q Learning algorithm demonstrated within a Grid World environment.
public class QLearning {
    static int height;                                                                                                  // Height of the Grid World.
    static int width;                                                                                                   // width of the Grid World.
    static int[][] grid;                                                                                                // Matrix that represents the Grid World

    static int[][] actions;                                                                                             // Matrix with the list of actions for each state.

    static double[][] rewards;                                                                                          // Matrix with the reward for each action.
    static int goal;                                                                                                    // The Goal state.

    static double[][] qScores;                                                                                          // Matrix for each Q Score from each sate to another state.
    static double learningRate;                                                                                         // The rate the algorithm learns new information.
    static double discountRate;                                                                                         // The rate in which the algorithm ignores future actions.
    static int episodes;                                                                                                // The number of episodes that need to be ran.

    static int[] policy;                                                                                                // Array that states the best action for each state.

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        buildGrid(scanner);                                                                                             // Sets up the Grid World.
        System.out.println("Build " + height + "x" + width + " Grid World.\n");

        setActions();                                                                                                   // Sets the Actions for each state within the Grid World.
        System.out.println("Actions for each state have been set.\n");

        setRewards(scanner);                                                                                            // Sets the Rewards for each state within the Grid World.
        System.out.println("Rewards set with " + (goal + 1) + " as the goal state.\n");

        setQVariables(scanner);                                                                                         // Sets the variables for the Q Learning algorithm.
        System.out.println("Variables have been set.\n");

        qLeaning();                                                                                                     // Generates the Q Scores using the Q Learning algorithm and then saves to a text file.
        saveQScores();
        System.out.println("Q Scores have been calculated and saved to Q_Scores.txt\n");

        makePolicy();                                                                                                   // Generate the optimal policy based on the Q Scores and then saves to a text file.
        savePolicy();
        System.out.println("Policy has been created and saved to Policy.txt\n");

        run(scanner);                                                                                                   // Navigates the Grid World from a starting position to the goal using the policy.
    }

    // Builds the Grid World bases on inputted dimensions.
    public static void buildGrid(Scanner scanner) {
        System.out.println("What is the height of the grid world?");                                                    // Gets the height and width of the Grid World from the user.
        height = scanner.nextInt();
        System.out.println("What is the width of the grid world?");
        width = scanner.nextInt();

        grid = new int[height][width];                                                                                  // Cycles through the Grid World setting a integer for each possible state.
        int counter = 0;
        for (int n = 0; n < height; n++) {
            for (int m = 0; m < width; m++) {
                grid[n][m] = counter;
                counter++;
            }
        }
    }

    // Finds all possible actions for each state within the Grid World.
    public static void setActions() {
        actions = new int[height*width][];
        for (int n = 0; n < height; n++) {                                                                              // Cycles through each of the states and finds each possible action.
            for (int m = 0; m < width; m++) {
                int[] action = new int[4];
                if (n != 0) {action[0] = (grid[n-1][m]);} else {action[0] = -1;}                                        // Up action
                if (m != 0) {action[1] = (grid[n][m-1]);} else {action[1] = -1;}                                        // Left action
                if (n != height-1) {action[2] = (grid[n+1][m]);} else {action[2] = -1;}                                 // Down action
                if (m != width-1) {action[3] = (grid[n][m+1]);} else {action[3] = -1;}                                  // Right action
                actions[grid[n][m]] = action;
            }
        }
    }

    // Sets the goal and reward matrix.
    public static void setRewards(Scanner scanner) {
        System.out.println("What is the X coordinate for the goal?");                                                   // Gets the goal state position and non-goal rewards from the user.
        int x = scanner.nextInt() - 1;
        System.out.println("What is the Y coordinate for the goal?");
        int y = scanner.nextInt() - 1;
        System.out.println("What reward for non-goal states would you like to set?");
        double reward = scanner.nextDouble();

        rewards = new double[height*width][height*width];
        goal = grid[y][x];                                                                                              // Sets the goal state from the Grid.

        if (x != 0) {rewards[goal - 1][goal] = 100;}                                                                    // Sets the rewards for the goal position.
        if (y != 0) {rewards[goal - width][goal] = 100;}
        if (x != width-1) {rewards[goal + 1][goal] = 100;}
        if (y != height-1) {rewards[goal + width][goal] = 100;}

        if (reward == 0) {                                                                                              // Sets all other rewards to user preference.
            for (int i = 0; i < rewards.length; i++) {
                for (int j = 0; j < rewards[i].length; j++) {
                    if (rewards[i][j] == 0)
                        rewards[i][j] = reward;
                }
            }
        }

        actions[goal] = new int[] {goal};                                                                               // Removes possible actions from goal.
    }

    // Sets the Q scores, learning rate, discount rate and number of episodes.
    public static void setQVariables(Scanner scanner) {
        qScores = new double[height*width][height*width];
        System.out.println("What Learning Rate should be used? (between 0 and 1)");                                     // Gets and then sets all variables from user preference.
        learningRate = scanner.nextDouble();
        System.out.println("What Discount Rate should be used? (between 0 and 1)");
        discountRate = scanner.nextDouble();
        System.out.println("How many episodes should be used?");
        episodes = scanner.nextInt();
    }

    // Runs the Q Learning algorithm to generate scores.
    public static void qLeaning() {
        Random random = new Random();

        for (int i = 0; i < episodes; i++) {                                                                            // Runs for each episode.
            int state = random.nextInt(height*width - 1);                                                               // Gets a random state.
            while (state != goal) {
                int[] stateActions = actions[state];                                                                    // Gets actions for the state.
                int action = stateActions[random.nextInt(stateActions.length)];                                         // Selects a random action.
                while (action == -1) {
                    action = stateActions[random.nextInt(stateActions.length)];
                }

                double q = qScores[state][action];                                                                      // Gets the previous Q Score for the action.
                double reward = rewards[state][action];                                                                 // Gets the reward for the action.
                double maxQ = maxQ(action);                                                                             // Finds the maximum Q Score from the future action.

                double score = q + learningRate * (reward + discountRate * maxQ - q);                                   // Calculates a new Q Score for the action.
                qScores[state][action] = score;                                                                         // Sets the new score.

                state = action;                                                                                         // Transitions to new state.
            }
        }
    }

    // Find maximum Q score for future state.
    public static double maxQ(int state) {
        double max = Double.MIN_VALUE;                                                                                  // Sets the max to minimum value.
        int[] futureActions = actions[state];                                                                           // Gets all the states actions.
        for (int i = 0; i < futureActions.length; i++) {                                                                // Cycles through each action.
            if (futureActions[i] != -1) {
                double value = qScores[state][futureActions[i]];                                                        // Gets the Q score from the action.
                if (value > max)                                                                                        // Compares the q score to the max.
                    max = value;
            }
        }
        return max;
    }

    // Saves the Q Scores to a text file.
    public static void saveQScores() {
        DecimalFormat decimal = new DecimalFormat("0.00");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("Q_Scores.txt"));                                    // Opens a File Writer.
            for (int i = 0; i < qScores.length; i++) {                                                                  // For each state.
                out.write((i + 1) + ": ");
                if (i != goal) {
                    for (int j = 0; j < qScores[i].length; j++) {                                                       // For each action.
                        out.write(Double.parseDouble(decimal.format(qScores[i][j])) + ", ");                            // Write the Q Score.
                    }
                    out.newLine();
                }
                else {
                    out.write("GOAL!");
                    out.newLine();
                }
            }
            out.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    // Creates the policy based on Q scores.
    public static void makePolicy() {
        policy = new int[height*width];
        for (int i = 0; i < qScores.length; i++) {                                                                      // For each state.
            double max = Double.MIN_VALUE;
            int index = -1;
            for (int j = 0; j < qScores[i].length; j++) {                                                               // For each action.
                if (qScores[i][j] > max) {                                                                              // Compare Q score against max
                    index = j;                                                                                          // Sets the index.
                    max = qScores[i][j];                                                                                // Sets new max.
                }
            }
            policy[i] = index;                                                                                          // Sets the policy for the state.
        }
    }

    // Saves the policy to a text file.
    public static void savePolicy() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("Policy.txt"));                                      // Sets up File Writer
            for (int i = 0; i < policy.length; i++) {                                                                   // For each state.
                if (i != goal)
                    out.write((i + 1) + " -> " + (policy[i] + 1));                                                      // Write the optimal action.
                else
                    out.write((i + 1) + " ->" + " Goal!");
                out.newLine();
            }
            out.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    // Uses the policy to navigate from a given start point to a goal point.
    public static void run(Scanner scanner) {
        System.out.println("What is the X coordinate for the start?");                                                  // Gets the starting position from the user.
        int x = scanner.nextInt() - 1;
        System.out.println("What is the Y coordinate for the start?");
        int y = scanner.nextInt() - 1;

        int state = grid[y][x];                                                                                         // Gets the state from the starting position.
        while (state != goal) {
            System.out.print("From: " + (state + 1));
            state = policy[state];                                                                                      // Performs optimal action to next state.
            System.out.print(" To: " + (state + 1) + "\n");
        }
    }
}