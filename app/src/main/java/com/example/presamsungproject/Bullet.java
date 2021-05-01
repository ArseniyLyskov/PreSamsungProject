package com.example.presamsungproject;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Bullet {
    double x, y;
    double speed;
    double angle;
    int ricochets;

    {
        ricochets = 1;
        speed = 1200;
    }

    public Bullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

}
