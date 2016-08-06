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
        rewards = new int[height][width];
        qScores = new double[height][width];

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
        int x = scanner.nextInt();
        System.out.println("What is the Y coordinate for the goal?");
        int y = scanner.nextInt();
        rewards[x][y] = 100;                // Sets the reward for the goal state.
        goal = grid[x][y];                  // Gets the state from the grid world.
        actions[goal] = new int[] {goal};   // Removes all possible actions from goal.
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
            int state = random.nextInt(height*width);
            while (state != goal) {
                int[] stateActions = actions[state];
                int action = stateActions[random.nextInt(stateActions.length)];
                while(action == 0) {
                    action = stateActions[random.nextInt(stateActions.length)];
                }
                int next = action;

                double q = qScores[state][action];
            }
        }
    }
}
