package org.example;

import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int depth = in.nextInt();

        int[][] initialState = new int[3][3];

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int value = in.nextInt();
                initialState[col][row] = (int)value;
            }
        }

        System.err.println("Initial state");
        debugGrid(initialState);

        System.err.println("Steps to run : " + depth);

        Map<Integer, Map<Integer, Integer>> cumulatedHashesPerTurn = new HashMap<>();

        int solution = computeSolution(toHash(initialState), 0, depth, cumulatedHashesPerTurn);

        System.out.println(solution);
    }

    /**
     * Compute children states for a current turn's game state
     * @param state game state
     * @return A list containing the next turn states
     */
    public static List<int[][]> getChildren(int[][] state) {
        List<int[][]> children = new ArrayList<>();

        for (int row = 0; row < state[0].length; row++) {
            for (int col = 0; col < state.length; col++) {
                int tile = state[col][row];

                if(tile != 0) {
                    continue;
                }

                List<int[]> adjacentDice = getAdjacentDice(col, row, state);

                if(adjacentDice.size() <= 1) {
                    int[][] stateNew = copyGrid(state);
                    stateNew[col][row] = 1;
                    children.add(stateNew);
                } else {
                    final List<List<int[]>> diceCombinations = getDiceCombinations(adjacentDice, state);

                    if(diceCombinations.isEmpty()) {
                        int[][] stateNew = copyGrid(state);
                        stateNew[col][row] = 1;
                        children.add(stateNew);
                    } else {
                        for(List<int[]> combination: diceCombinations) {
                            children.add(createNewState(state, col, row, combination));
                        }
                    }
                }

            }
        }

        return children;
    }

    /**
     * Get adjacent dice positions
     * @param col column index
     * @param row row index
     * @param grid game state
     * @return a list containing a tile adjacent dices if they exist
     */
    public static List<int[]> getAdjacentDice(int col, int row, int[][] grid) {
        List<int[]> dice = new ArrayList<>();

        if(col > 0 && isDice(grid[col-1][row])) { // left
            dice.add(new int[] { col-1, row });
        }
        if(col < grid.length -1 && isDice(grid[col+1][row])) { // right
            dice.add(new int[] { col+1, row });
        }
        if(row > 0 && isDice(grid[col][row-1])) { // top
            dice.add(new int[] { col, row-1 });
        }
        if(row < grid[0].length -1 && isDice(grid[col][row+1])) { // bottom
            dice.add(new int[] { col, row+1 });
        }

        return dice;
    }

    /**
     * Indicates if a tile is a dice
     * @param tile tile value
     */
    public static boolean isDice(int tile) {
        return tile != 0;
    }

    /**
     * Determine all distinct dice combinations from a list of dice
     * @param dice dice position list
     * @param grid game state
     */
    public static List<List<int[]>> getDiceCombinations(List<int[]> dice, int[][] grid) {
        List<List<int[]>> combinations = new ArrayList<>();

        // all combinations for k elements in n
        for(int k = 2; k <= dice.size() && k <= 4; k++) {

            int combNumber = combinationNumber(k, dice.size());
            int combStart = 0;
            int step = 1;

            // create all combinations
            for(int combIndex = 0;  combIndex < combNumber; combIndex++) {
                List<int[]> combination = new ArrayList<>();

                int diceIndex = combStart;
                while(combination.size() < k) {
                    combination.add(dice.get(diceIndex));

                    diceIndex = diceIndex + step >= dice.size() ?
                            step - 1
                            : diceIndex + step;
                }

                if(combinationIsValid(combination, grid)) {
                    combinations.add(combination);
                }

                combStart++;

                if(combStart == dice.size()) {
                    combStart = 0;
                    step++;
                }
            }

        }

        return combinations;
    }

    /**
     * Computes the number of distinct combinations of k elements from a collection of n
     * @param k number of combination elements
     * @param n number of elements in a pool
     */
    public static int combinationNumber(int k, int n) {
        return factorial(n) / (factorial(k) * factorial(n - k));
    }

    /**
     * Computes the factorial of a given number
     */
    public static int factorial(int number) {
        if(number == 0) {
            return 1;
        }

        return number * factorial(number - 1);
    }

    /**
     * Determines if a combination is valid based on if the sum of all dice is smaller than 6
     * @param combination List of dice
     * @param grid game state
     */
    public static boolean combinationIsValid(List<int[]> combination, int[][] grid) {
        int sum = 0;

        for (int[] coordinates : combination) {
            sum += grid[coordinates[0]][coordinates[1]];
        }

        return sum <= 6;
    }

    /**
     * Create a new game state by putting a dice at a given position
     * and absorbing dice listed in combination
     * @param currentState game state
     * @param x column for new dice position
     * @param y row for new dice position
     * @param combination dice combination to create a new dice
     */
    public static int[][] createNewState(int[][] currentState, int x, int y, List<int[]> combination) {
        int[][] stateNew = copyGrid(currentState);

        int sum = 0;
        int col;
        int row;

        for(int[] diceCoordinates: combination) {
            col = diceCoordinates[0];
            row = diceCoordinates[1];

            sum += currentState[col][row];
            stateNew[col][row] = 0;
        }

        stateNew[x][y] = sum;

        return stateNew;
    }

    /**
     * Print grid
     * @param grid game state
     */
    public static void debugGrid(int[][] grid) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                System.err.print(grid[col][row]);
            }
            System.err.println("");
        }
    }

    /**
     * Converts a game state into an integer hash.
     * Hash is created as follows :
     * 1 2 3
     * 4 5 6 --> 123456789
     * 7 8 9
     * @param grid game state
     */
    public static int toHash(int[][] grid) {
        int hash = 0;

        int tenExponent = 8;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                hash += grid[col][row] * Math.pow(10, tenExponent);
                tenExponent--;
            }
        }

        return hash;
    }

    /**
     * Converts a hash into a game state grid
     * @param hash integer hash
     * @see Player#toHash(int[][])
     */
    public static int[][] fromHash(int hash) {
        String literal = String.valueOf(hash);

        if(literal.length() < 9) {
            literal = "0".repeat(9 - literal.length()) + literal;
        }

        int[][] grid = new int[3][3];

        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 3; col++) {
                grid[col][row] = Integer.parseInt(String.valueOf(literal.charAt(row * 3 + col)));
            }
        }

        return grid;
    }

    public static final int MODULO = (int)Math.pow(2, 30);

    /**
     * Compute recursively the cumulated hashes of all final game states for a maximum of maxTurn turns.
     * This method is a depth-first algorithm which tries to compute game states and save in memory
     * all cumulated hashes values for each game state for a given turn.
     * @param hash current game state
     * @param turn current turn
     * @param maxTurn maximum turn to which the algorithm has to dig
     * @param cumulatedHashesPerTurn Map containing cumulated hashes for each already encountered game states
     * @return The cumulated hashes of all final game states
     */
    public static int computeSolution(int hash, int turn, int maxTurn, Map<Integer, Map<Integer, Integer>> cumulatedHashesPerTurn) {

        if(!cumulatedHashesPerTurn.containsKey(turn)) {
            cumulatedHashesPerTurn.put(turn, new HashMap<Integer, Integer>());
        }

        Map<Integer, Integer> cumulatedHashes = cumulatedHashesPerTurn.get(turn);
        Integer cumulated = cumulatedHashes.get(hash);

        if(cumulated == null) {
            if(turn == maxTurn) {
                cumulatedHashes.put(hash, hash);
                return hash;
            }

            var children = getChildren(fromHash(hash));

            if(children.isEmpty()) {
                cumulatedHashes.put(hash, hash);
                return hash;
            }

            cumulated = 0;
            int futureHash;
            for(var child: children) {
                futureHash = computeSolution(toHash(child), turn + 1, maxTurn, cumulatedHashesPerTurn);
                cumulated = (cumulated + futureHash) % MODULO;
            }

            cumulatedHashes.put(hash, cumulated);

            return cumulated;
        } else {
            return cumulated;
        }

    }

    /**
     * Make a deep copy of a game state
     * @param grid game state
     */
    public static int[][] copyGrid(int[][] grid) {
        return Arrays.stream(grid)
                .map(int[]::clone)
                .toArray(int[][]::new);
    }

}