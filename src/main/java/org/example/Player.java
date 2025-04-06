package org.example;

import java.util.*;
import java.io.*;
import java.math.*;

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


        List<int[][]> currentTurn = new ArrayList<>();
        List<int[][]> nextTurn = new ArrayList<>();
        int turnCount = 0;

        final List<Integer> solutions = new ArrayList<>();

        currentTurn.add(initialState);

        while(turnCount < depth && currentTurn.size() > 0) {
            final var state = currentTurn.get(0);
            currentTurn.remove(state);

            List<int[][]> nextStates = getChildren(state);

            if(nextStates.size() == 0) {
                solutions.add(toHash(state));
            }

            for(int[][] future : nextStates) {
                nextTurn.add(future);
            }

            if(currentTurn.size() == 0) {
                currentTurn = nextTurn;
                nextTurn = new ArrayList<>();

                System.err.println("Turn end : " + turnCount);
                turnCount++;
            }
        }

        System.err.println("Current " + currentTurn.size());
        System.err.println("next " + nextTurn.size());

        for(int[][] remaining: currentTurn) {
            solutions.add(toHash(remaining));
        }

        debugHashes(solutions);

        System.out.println(computeSolution(solutions));
        //System.out.println(1);

    }

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

    public static boolean isDice(int tile) {
        return tile != 0;
    }

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

    public static int combinationNumber(int k, int n) {
        return factorial(n) / (factorial(k) * factorial(n - k));
    }

    public static int factorial(int number) {
        if(number == 0) {
            return 1;
        }

        return number * factorial(number - 1);
    }

    public static boolean combinationIsValid(List<int[]> combination, int[][] grid) {
        int sum = 0;

        for (int[] coordinates : combination) {
            sum += grid[coordinates[0]][coordinates[1]];
        }

        return sum <= 6;
    }

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

    public static void debugGrid(int[][] grid) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                System.err.print(grid[col][row]);
            }
            System.err.println("");
        }
    }

    public static void debugHashes(List<Integer> hashes) {
        System.err.printf("%d solutions found:", hashes.size());
        for(var hash: hashes) {
            System.err.println(hash);
        }
    }

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

    public static final int MODULO = (int)Math.pow(2, 30);

    public static int computeSolution(List<Integer> hashes) {
        int sum = 0;

        for(int hash: hashes) {
            sum = (sum + hash) % MODULO;
        }

        return sum;
    }

    public static int[][] copyGrid(int[][] grid) {
        return Arrays.stream(grid)
                .map(int[]::clone)
                .toArray(int[][]::new);
    }

}