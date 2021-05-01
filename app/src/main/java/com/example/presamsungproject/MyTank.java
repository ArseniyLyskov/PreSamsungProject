package com.example.presamsungproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class MyTank extends Tank{
    double speed, current_speed;
    Bitmap hull, tower;

    {
        myPaint.setColor(Color.GREEN);
    }

    public MyTank(double hp, double x, double y, double angleH, double angleT, String playerName,
                  long id, double speed, Bitmap hull, Bitmap tower) {
        super(hp, x, y, angleH, angleT, playerName, id);
        this.speed = speed;
        this.hull = hull;
        this.tower = tower;
        nameWidth = (int) myPaint.measureText(playerName);
    }

    public void draw(Canvas canvas, Paint paint){
        super.draw(canvas, paint, hull, tower);
    }
}
