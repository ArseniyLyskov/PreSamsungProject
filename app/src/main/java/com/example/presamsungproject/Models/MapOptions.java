package com.example.presamsungproject.Models;

public class MapOptions {
    private final int minCells;
    private final int min_width, min_height, max_width, max_height;
    private final int missingCellPercent, innerWallPercent;

    public MapOptions(int minCells, int min_width, int min_height, int max_width, int max_height, int missingCellPercent, int innerWallPercent) {
        this.minCells = minCells;
        this.min_width = min_width;
        this.min_height = min_height;
        this.max_width = max_width;
        this.max_height = max_height;
        this.missingCellPercent = missingCellPercent;
        this.innerWallPercent = innerWallPercent;
    }

    public int getMinCells() {
        return minCells;
    }

    public int getMin_width() {
        return min_width;
    }

    public int getMin_height() {
        return min_height;
    }

    public int getMax_width() {
        return max_width;
    }

    public int getMax_height() {
        return max_height;
    }

    public int getMissingCellPercent() {
        return missingCellPercent;
    }

    public int getInnerWallPercent() {
        return innerWallPercent;
    }
}
