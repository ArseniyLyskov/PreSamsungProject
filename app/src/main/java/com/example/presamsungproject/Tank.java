package com.example.presamsungproject;

import android.graphics.*;

public class Tank {
    double hp;
    double x, y;
    double angleH, angleT;
    String playerName;
    TankSight tankSight;
    final long id;
    Paint myPaint;
    int nameWidth;

    {
        myPaint = new Paint();
        myPaint.setTextSize(30);
        myPaint.setColor(Color.RED);
        tankSight = new TankSight();
    }

    public void draw(Canvas canvas, Paint paint, Bitmap hull, Bitmap tower) {
        double hWidth = hull.getWidth();
        double hHeight = hull.getHeight();
        double tWidth = tower.getWidth();
        double tHeight = tower.getHeight();
        canvas.rotate((float) angleH, (float) (x + hWidth / 2),
                (float) (y + hHeight / 2));
        canvas.drawBitmap(hull, (int) x, (int) y, paint);
        canvas.rotate((float) -angleH, (float) (x + hWidth / 2),
                (float) (y + hHeight / 2));

        canvas.rotate((float) (angleH + angleT), (float) (x + tWidth / 2),
                (float) (y + tHeight / 2));
        if(tankSight.isSighting)
            tankSight.draw(canvas, (int) (x + tWidth / 2), (int) (y + tHeight / 2));
        canvas.drawBitmap(tower, (int) x, (int) y, paint);
        canvas.rotate((float) -(angleH + angleT), (float) (x + tWidth / 2),
                (float) (y + tHeight / 2));

        canvas.drawText(playerName, (int) (x + hWidth / 2 - nameWidth / 2), (int) (y - 10), myPaint);
    }

    public Tank(double hp, double x, double y, double angleH, double angleT, String playerName, long id) {
        this.hp = hp;
        this.x = x;
        this.y = y;
        this.angleH = angleH;
        this.angleT = angleT;
        this.playerName = playerName;
        this.id = id;
        nameWidth = (int) myPaint.measureText(playerName);
    }
}
