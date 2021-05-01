package com.example.presamsungproject.GameObjects;

public class Bullet {
    private double x, y;
    private double speed;
    private double angle;
    private int ricochets;

    {
        ricochets = 1;
        speed = 1200;
    }

    public Bullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
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
