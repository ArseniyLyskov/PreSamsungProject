package com.example.presamsungproject.Geometry;

import java.io.Serializable;

public class Segment implements Serializable {
    private final Point p1;
    private final Point p2;
    private static final long serialVersionUID = 8L;

    public Segment(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public void scaleTo(double koeff) {
        p1.scaleTo(koeff);
        p2.scaleTo(koeff);
    }

    public int maxX() {
        return Math.max(p1.getX(), p2.getX());
    }

    public int minX() {
        return Math.min(p1.getX(), p2.getX());
    }

    public int maxY() {
        return Math.max(p1.getY(), p2.getY());
    }

    public int minY() {
        return Math.min(p1.getY(), p2.getY());
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }
}
