package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void toHash() {
        int[][] grid = new int[][] {
                new int[] { 0, 3, 6 },
                new int[] { 1, 4, 7 },
                new int[] { 2, 5, 8 },
        };

        assertEquals(12345678, Player.toHash(grid));
    }

    @Test
    void getChildren() {
        int[][] grid = new int[][] {
                new int[] { 6, 1, 6 },
                new int[] { 1, 0, 1 },
                new int[] { 6, 1, 6 },
        };

        var children = Player.getChildren(grid);

        for(var state: children) {
            //Main.debugGrid(state);
        }
    }

    @Test
    void getAdjacentDice() {
        int[][] grid = new int[][] {
                new int[] { 6, 1, 6 },
                new int[] { 1, 0, 1 },
                new int[] { 6, 1, 6 },
        };

        var adjacent = Player.getAdjacentDice(1, 1, grid);

        for(var dice: adjacent) {
            System.out.println(grid[dice[0]][dice[1]]);
        }
    }

    @Test
    void isDice() {
        assertTrue(Player.isDice(1));
        assertTrue(Player.isDice(2));
        assertTrue(Player.isDice(3));
        assertTrue(Player.isDice(4));
        assertTrue(Player.isDice(5));
        assertTrue(Player.isDice(6));

        assertFalse(Player.isDice(0));
    }

    @Test
    void getDiceCombinations() {
    }

    @Test
    void copyGrid() {
        int[][] grid = new int[][] {
                new int[] { 6, 1, 6 },
                new int[] { 1, 0, 1 },
                new int[] { 6, 1, 6 },
        };

        var copy = Player.copyGrid(grid);

        copy[0][0] = 9;

        assertEquals(6, grid[0][0]);
        assertEquals(9, copy[0][0]);
    }
}