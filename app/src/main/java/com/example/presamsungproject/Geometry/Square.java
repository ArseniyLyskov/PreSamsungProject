package com.example.presamsungproject.Geometry;

import java.io.Serializable;

public class Square implements Serializable {
    private final Point p1;
    private final Point p2;
    private final Point p3;
    private final Point p4;

    public Square(Point p1, Point p2, Point p3, Point p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    public void scaleTo(double koeff) {
        p1.scaleTo(koeff);
        p2.scaleTo(koeff);
        p3.scaleTo(koeff);
        p4.scaleTo(koeff);
    }

    public int maxX() {
        return Math.max(Math.max(p1.getX(), p2.getX()), Math.max(p3.getX(), p4.getX()));
    }

    public int minX() {
        return Math.min(Math.min(p1.getX(), p2.getX()), Math.min(p3.getX(), p4.getX()));
    }

    public int maxY() {
        return Math.max(Math.max(p1.getY(), p2.getY()), Math.max(p3.getY(), p4.getY()));
    }

    public int minY() {
        return Math.min(Math.min(p1.getY(), p2.getY()), Math.min(p3.getY(), p4.getY()));
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public Point getP3() {
        return p3;
    }

    public Point getP4() {
        return p4;
    }
}

