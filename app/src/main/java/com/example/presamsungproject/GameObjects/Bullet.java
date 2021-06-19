package com.example.presamsungproject.GameObjects;

import android.graphics.Canvas;
import com.example.presamsungproject.MyPaints;

import java.io.Serializable;

public class Bullet implements Serializable {
    private transient double speed;
    private transient int ricochets;
    private transient double angle;

    private double x, y;
    private static final long serialVersionUID = 4L;

    {
        ricochets = 3;
        speed = 1200;
    }

    public void scaleTo(double koeff) {
        x *= koeff;
        y *= koeff;
    }

    public Bullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public Bullet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void drawHitBox(Canvas canvas) {
        canvas.drawRect((int) x - 5, (int) y - 5, (int) x + 5, (int) y + 5, MyPaints.getHitBoxPaint());
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getRicochets() {
        return ricochets;
    }

    public void setRicochets(int ricochets) {
        this.ricochets = ricochets;
    }

    public double getSpeed() {
        return speed;
    }
}