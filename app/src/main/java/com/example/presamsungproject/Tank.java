package com.example.presamsungproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Tank {

    double hp;
    double x, y;
    double scaleUp;
    double angleH, angleT;
    String playerName;
    final long id;

    public void draw(Canvas canvas, Paint paint, Bitmap hull, Bitmap tower) {
        canvas.rotate((float) angleH, (float) (x + hull.getWidth() * scaleUp / 2),
                (float) (y + hull.getHeight() * scaleUp / 2));
        canvas.drawBitmap(Bitmap.createScaledBitmap(hull, (int) (hull.getWidth() * scaleUp), (int) (hull.getHeight() * scaleUp), false), (int) x, (int) y, paint);
        canvas.rotate((float) -angleH, (float) (x + hull.getWidth() * scaleUp / 2),
                (float) (y + hull.getHeight() * scaleUp / 2));

        canvas.rotate((float) (angleH + angleT), (float) (x + tower.getWidth() * scaleUp / 2),
                (float) (y + tower.getHeight() * scaleUp / 2));
        canvas.drawBitmap(Bitmap.createScaledBitmap(tower, (int) (tower.getWidth() * scaleUp), (int) (tower.getHeight() * scaleUp), false), (int) x, (int) y, paint);
        canvas.rotate((float) -(angleH + angleT), (float) (x + tower.getWidth() * scaleUp / 2),
                (float) (y + tower.getHeight() * scaleUp / 2));
    }

    public Tank(double hp, double x, double y, double scaleUp,
                double angleH, double angleT, String playerName, long id) {
        this.hp = hp;
        this.x = x;
        this.y = y;
        this.scaleUp = scaleUp;
        this.angleH = angleH;
        this.angleT = angleT;
        this.playerName = playerName;
        this.id = id;
    }
}
