package h02;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * Main entry point in executing the program.
 */
public class Main {
    // Delay between each action in FopBot-World (world), for example:
    // Waits 1000ms between each .move() call
    public static final int DELAY = 50;

    // Generates random int between 4 (inclusive) and 10 (exclusive)
    public static int getRandomWorldSize() {
        return 4 + ThreadLocalRandom.current().nextInt(6);
    }

    // Name of file for patterns
    public static final String FILENAME = "ExamplePattern.txt";

    public static void main(String[] args) {
        // Get number of columns from method
        int numberOfColumns = getRandomWorldSize();

        // Get number of rows from method
        int numberOfRows = getRandomWorldSize();

        // Initialize World with specified number of columns and rows
        World.setSize(numberOfColumns, numberOfRows);

        // Set the internal delay of the world
        World.setDelay(DELAY);

        // Set the world visible
        World.setVisible(true);

        // Print out size of the world to the command line
        System.out.println("Size of world: " + numberOfColumns + "x" + numberOfRows);

        // Initialize new Main-object to call methods
        Main main = new Main();

        // Initialize a pattern provider for the .txt-file in resources
        PatternProvider patternProvider;
        try {
            patternProvider = new PatternProvider(FILENAME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Get the pattern from the .txt file
        boolean[][] testPattern = patternProvider.getPattern();

        // Call initializeRobotsPattern
        Robot[] allRobots = main.initializeRobotsPattern(testPattern, numberOfColumns, numberOfRows);

        main.letRobotsMarch(allRobots);

        // TODO: H2.2 - Put your code here:

    }

    /**
     * Counts the number of robots in a pattern, given a specified world size.
     *
     * @param pattern           The pattern for the robots.
     * @param numberOfColumns   Number of columns in the world.
     * @param numberOfRows      Number of rows in the world.
     * @return                  Number of robots in the world.
     */
    public int countRobotsInPattern(boolean[][] pattern, int numberOfColumns, int numberOfRows) {
        int numberOfRobots = 0;
        for (int x = 0; x < pattern.length; x++) {
            for (int y = 0; y < pattern[x].length; y++) {
                if (x < numberOfColumns && y < numberOfRows && pattern[x][y]) {
                    numberOfRobots++;
                }
            }
        }

        return numberOfRobots;
    }

    /**
     * Initialize allRobots array for given pattern and world size.
     *
     * @param pattern           The pattern for the robots.
     * @param numberOfColumns   Number of columns in world.
     * @param numberOfRows      Number of rows in world.
     * @return                  Correctly initialized allRobots array.
     */
    public Robot[] initializeRobotsPattern(boolean[][] pattern, int numberOfColumns, int numberOfRows) {
        Robot[] allRobots = new Robot[countRobotsInPattern(pattern, numberOfColumns, numberOfRows)];
        int numberOfRobots = 0;
        for (int x = 0; x < numberOfColumns; x++) {
            for (int y = 0; y < numberOfRows; y++) {
                if (x < pattern.length && y < pattern[x].length && pattern[x][y]) {
                    allRobots[numberOfRobots++] = new Robot(x, y, Direction.RIGHT, numberOfColumns - x);
                }
            }
        }
        return allRobots;
    }

    /**
     * Returns how many of the components of the given robot-array are null.
     *
     * @param allRobots   The Robot-array.
     * @return            True, if array contains robot.
     */
    public int numberOfNullRobots(Robot[] allRobots) {
        int numberOfNullRobots = 0;
        for (Robot allRobot : allRobots) {
            if (allRobot == null) {
                numberOfNullRobots++;
            }
        }
        return numberOfNullRobots;
    }

    /**
     * Creates an array containing three (pseudo-) random int values from 0 (inclusive) to given parameter (exclusive).
     *
     * @param bound   The upper bound for the int values.
     * @return        The array.
     */
    public int[] generateThreeDistinctRandomIndices(int bound) {
        int i0 = ThreadLocalRandom.current().nextInt(bound);
        int i1 = ThreadLocalRandom.current().nextInt(bound);
        int i2 = ThreadLocalRandom.current().nextInt(bound);

        while (i0 == i1 || i0 == i2 || i1 == i2) {
            i0 = ThreadLocalRandom.current().nextInt(bound);
            i1 = ThreadLocalRandom.current().nextInt(bound);
            i2 = ThreadLocalRandom.current().nextInt(bound);
        }

        return new int[] {i0, i1, i2};
    }

    /**
     * Sorts the given 3 valued array from lowest to highest.
     *
     * @param array   The array to be sorted.
     */
    public void sortArray(int[] array) {
        if (array[1] < array[0]) {
            int tmp = array[1];
            array[1] = array[0];
            array[0] = tmp;
        }

        if (array[2] < array[0]) {
            int tmp = array[2];
            array[2] = array[0];
            array[0] = tmp;
        }

        if (array[2] < array[1]) {
            int tmp = array[2];
            array[2] = array[1];
            array[1] = tmp;
        }

        // Oder Alternativ Lösung -> Arrays.sort(array);

    }

    /**
     * Swaps three robots in given robot array.
     * Robot at index i will later be at index j.
     * Robot at index j will later be at index k.
     * Robot at index k will later be at index i.
     *
     * @param indices       Array containing indices i, j and k.
     * @param allRobots     Array containing the robots.
     */
    public void swapRobots(int[] indices, Robot[] allRobots) {
        int i = indices[0];
        int j = indices[1];
        int k = indices[2];

        Robot tmp = allRobots[k];
        allRobots[k] = allRobots[j];
        allRobots[j] = allRobots[i];
        allRobots[i] = tmp;
    }

    /**
     * Reduces the given robot array by the set amount and only keeps non-null components.
     *
     * @param robots    The array to be reduced.
     * @param reduceBy  The number of indices that are reduced.
     * @return          The reduced array.
     */
    public Robot[] reduceRobotArray(Robot[] robots, int reduceBy) {
        int arraySize = robots.length - reduceBy;
        Robot[] reducedArray = new Robot[arraySize];
        int counter = 0;
        for (Robot robot : robots) {
            if (robot != null) {
                reducedArray[counter++] = robot;
            }
        }
        return reducedArray;
    }

    /**
     * Lets all robots in the given array walk to the right while also putting down coins.
     * If robots leave the world they are set to null.
     * After the steps are made, if more than three robots exist, three of them change their index.
     * If 3 or more components of the array are null, the array is reduced by the amount of null components.
     *
     * @param allRobots   Array containing all the robots.
     */
    public void letRobotsMarch(Robot[] allRobots) {
        while (numberOfNullRobots(allRobots) != allRobots.length) {
            for (int i = 0; i < allRobots.length; i++) {
                if (allRobots[i] != null) {
                    allRobots[i].putCoin();
                    if (ifCanMove(allRobots[i])) {
                        allRobots[i].move();
                    } else {
                        allRobots[i] = null;
                    }
                }
            }
            if (allRobots.length >= 3) {
                int[] indices = generateThreeDistinctRandomIndices(allRobots.length);
                sortArray(indices);
                swapRobots(indices, allRobots);
            }
            int numberOfNullRobots = numberOfNullRobots(allRobots);
            if (numberOfNullRobots >= 3) {
                allRobots = reduceRobotArray(allRobots, numberOfNullRobots);
            }
        }
    }

    private boolean ifCanMove(Robot robot) {
        switch (robot.getDirection()) {
            case UP -> {
                return robot.getY() != World.getWidth() - 1;
            }
            case DOWN -> {
                return robot.getY() != 0;
            }
            case LEFT -> {
                return robot.getX() != 0;
            }
            case RIGHT -> {
                return robot.getX() != World.getWidth() - 1;
            }
            default -> throw new IllegalArgumentException("Ups...Something went Wrong!");
        }
    }
}
