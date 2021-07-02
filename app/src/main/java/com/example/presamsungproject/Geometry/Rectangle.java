package com.example.presamsungproject.Geometry;

import java.io.Serializable;

public class Rectangle implements Serializable {
    private final Point p1;
    private final Point p2;
    private final Point p3;
    private final Point p4;
    private static final long serialVersionUID = 7L;

    public Rectangle(Point p1, Point p2, Point p3, Point p4) {
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

    public Point maxXPoint() {
        Point max = p1;
        if (p2.getX() > max.getX())
            max = p2;
        if (p3.getX() > max.getX())
            max = p3;
        if (p4.getX() > max.getX())
            max = p4;
        return max;
    }

    public Point minXPoint() {
        Point min = p1;
        if (p2.getX() < min.getX())
            min = p2;
        if (p3.getX() < min.getX())
            min = p3;
        if (p4.getX() < min.getX())
            min = p4;
        return min;
    }

    public Point maxYPoint() {
        Point max = p1;
        if (p2.getY() > max.getY())
            max = p2;
        if (p3.getY() > max.getY())
            max = p3;
        if (p4.getY() > max.getY())
            max = p4;
        return max;
    }

    public Point minYPoint() {
        Point min = p1;
        if (p2.getY() < min.getY())
            min = p2;
        if (p3.getY() < min.getY())
            min = p3;
        if (p4.getY() < min.getY())
            min = p4;
        return min;
    }

    public Point getDiagonalPoint(Point point) {
        if (point.equalsTo(p1))
            return p3;
        if (point.equalsTo(p2))
            return p4;
        if (point.equalsTo(p3))
            return p1;
        if (point.equalsTo(p4))
            return p2;
        return null;
    }

    public Point getNotDiagonalPoint(Point point) {
        if (point.equalsTo(p1))
            return p2;
        if (point.equalsTo(p2))
            return p3;
        if (point.equalsTo(p3))
            return p4;
        if (point.equalsTo(p4))
            return p1;
        return null;
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

