package com.example.presamsungproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class MyTank extends Tank{
    double speed, current_speed;
    private Bitmap hull, tower;
    private Bitmap buf_hull, buf_tower;

    public MyTank(double hp, double x, double y, double scaleUp, double angleH, double angleT,
                  long id, double speed, Bitmap hull, Bitmap tower) {
        super(hp, x, y, scaleUp, angleH, angleT, "Me", id);
        this.speed = speed;
        this.hull = hull;
        this.tower = tower;
        setScaleUp(scaleUp);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.rotate((float) angleH, (float) (x + buf_hull.getWidth() / 2),
                (float) (y + buf_hull.getHeight() / 2));
        canvas.drawBitmap(buf_hull, (int) x, (int) y, paint);
        canvas.rotate((float) -angleH, (float) (x + buf_hull.getWidth() / 2),
                (float) (y + buf_hull.getHeight() / 2));

        canvas.rotate((float) (angleH + angleT), (float) (x + buf_tower.getWidth() / 2),
                (float) (y + buf_tower.getHeight() / 2));
        canvas.drawBitmap(buf_tower, (int) x, (int) y, paint);
        canvas.rotate((float) -(angleH + angleT), (float) (x + buf_tower.getWidth() / 2),
                (float) (y + buf_tower.getHeight() / 2));
    }

    public void setScaleUp(double scaleUp) {
        super.scaleUp = scaleUp;
        buf_hull = Bitmap.createScaledBitmap(this.hull, (int) (this.hull.getWidth() * this.scaleUp),
                (int) (this.hull.getHeight() * this.scaleUp), false);
        buf_tower = Bitmap.createScaledBitmap(this.tower, (int) (this.tower.getWidth() * this.scaleUp),
                (int) (this.tower.getHeight() * this.scaleUp), false);
    }

}
