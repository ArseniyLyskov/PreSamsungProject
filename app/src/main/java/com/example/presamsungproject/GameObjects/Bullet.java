package com.example.presamsungproject.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Bullet {
    private double x, y;
    private double speed;
    private double angle;
    private int ricochets;
    private Paint paint;

    {
        paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);

        ricochets = 3;
        speed = 1200;
    }

    public Bullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void drawHitBox(Canvas canvas) {
        canvas.drawRect((int) x - 5, (int) y - 5, (int) x + 5, (int) y + 5, paint);
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
