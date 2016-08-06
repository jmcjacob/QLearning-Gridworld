import java.util.*;
/**
 * Created by Jacob Carse on 05/08/2016.
 * An implementation of the Q Learning algorithm demonstrated within a Grid World environment.
 */
public class QLearningGridWorld {

    // Main method to be run.
    public static void main(String[] args) {
        /**
         *  VARIABLES
         */
        int height; // Height of Grid world.
        int width; // Width of Grid world.
        int[][] grid; // Grid world with labeled states

        int[][] actions; // For each state a list of all possible actions.
        int[][] rewards; // Grid world with the rewards.
        int goal; // The end goal the the Gird world.

        int episodes; // The number of episodes to run.
        double[][] qScores;

        double learningRate; // The rate which the algorithm learns new information. 0 = learns nothing, 1 = only learns from most recent action.
        double discountRate; // Used to avoid Infinite Horizon by making actions in the future cost more.

        Scanner scanner = new Scanner(System.in);

        /**
         *  INITIALISATION and SET UP
         */
        // Sets the Grid World
        System.out.println("What is the height of the grid world?");
        height = scanner.nextInt();
        System.out.println("What is the width of the grid world?");
        width = scanner.nextInt();
        System.out.println("Height: " + height + "\nWidth: " + width);

        // Initialises the Grid, actions and rewards.
        grid = new int[height][width];
        actions = new int[height*width][];
        rewards = new int[height*width][height*width];
        qScores = new double[height*width][height*width];

        // Numbers the states within the grid.
        int counter = 1;
        for(int n = 0; n < height; n++) {
            for(int m = 0; m < width; m++) {
                grid[n][m] = counter;
                counter++;
            }
        }

        // Finds all possible actions for each state.
        for(int n = 0; n < height; n++) {
            for(int m = 0; m < width; m++) {
                int[] action = new int[4];
                if (n != 0)
                    action[0] = (grid[n-1][m]);
                if (m != 0)
                    action[1] = (grid[n][m-1]);
                if (n != height-1)
                    action[2] = (grid[n+1][m]);
                if (m != width-1)
                    action[3] = (grid[n][m+1]);
                actions[grid[n][m]-1] = action;
                //System.out.println(grid[n][m] + ": " + action[0] + " " + action[1] + " " + action[2] + " " + action[3] + " ");
            }
        }

        // Sets the Learning Rate and Discount Rate based on user preference.
        System.out.println("What is the X coordinate for the goal?");
        int x = scanner.nextInt() - 1;
        System.out.println("What is the Y coordinate for the goal?");
        int y = scanner.nextInt() - 1;

        goal = grid[x][y] - 1;                       // Gets the state from the grid world.
        if (x != 0)                                 // Sets the rewards to 100 for moving into the goal.
            rewards[goal-1][goal] = 100;
        if (y != 0)
            rewards[goal-width][goal] = 100;
        if (x != height-1)
            rewards[goal+1][goal] = 100;
        if (y != width-1)
            rewards[goal+width][goal] = 100;
        actions[goal-1] = new int[] {goal};   // Removes all possible actions from goal.
        goal++;
        System.out.println("Goal State: " + goal);

        // Sets the Learning Rate and Discount Rate based on user preference.
        System.out.println("What Learning Rate should be used?");
        learningRate = scanner.nextDouble();
        System.out.println("What Discount Rate should be used?");
        discountRate = scanner.nextDouble();
        System.out.println("How many episodes should be used?");
        episodes = scanner.nextInt();
        System.out.println("Learning Rate: " + learningRate + "\nDiscount Rate: " + discountRate + "\nEpisodes: " + episodes);

        /**
         *  Q-LEARNING ALGORITHM
         */
        Random random = new Random();
        for(int i = 0; i < episodes; i++) {
            int state = random.nextInt(height*width - 1);
            while (state != goal) {
                int[] stateActions = actions[state-1];
                int action = stateActions[random.nextInt(stateActions.length)];
                while(action == 0) {
                    action = stateActions[random.nextInt(stateActions.length)];
                }

                double q = qScores[state - 1][action - 1];
                int reward = rewards[state - 1][action - 1];
                double max = Double.MIN_VALUE;
                for(int j = 0; j < stateActions.length; j++) {
                    if (stateActions[j] != 0) {
                        double value = qScores[state - 1][stateActions[j] - 1];

                        if (value > max)
                            max = value;
                    }
                }

                double score = q + learningRate * (reward + discountRate * max - q);
                qScores[state - 1][action - 1] = score;

                state = action;
            }
        }
        for(int i = 0; i < (height*width); i++) {
            System.out.print((i+1) + ": ");
            for(int j = 0; j < qScores[i].length; j++) {
                System.out.print(qScores[i][j] + " ");
            }
        }
    }
}