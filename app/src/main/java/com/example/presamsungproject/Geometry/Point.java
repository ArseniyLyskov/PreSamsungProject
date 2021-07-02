package com.example.presamsungproject.Geometry;

import java.io.Serializable;

public class Point implements Serializable {
    private int x;
    private int y;
    private static final long serialVersionUID = 6L;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void scaleTo(double koeff) {
        x *= koeff;
        y *= koeff;
    }

    public boolean equalsTo(Point point) {
        return x == point.getX() && y == point.getY();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
