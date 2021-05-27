package com.example.presamsungproject.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.presamsungproject.HitBox;

import java.util.HashSet;

public class TankSight {
    private boolean isSighting;
    private final Paint paint;
    private double x1, y1, x2, y2;

    {
        paint = new Paint();
        isSighting = false;
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
    }

    public void draw(Canvas canvas) {
        canvas.drawLine((int) x1, (int) y1, (int) x2, (int) y2, paint);
    }

    public void update(double x1, double y1, double angle, HashSet<HitBox> hitBoxes) {
        int length = (int) Math.pow(2, 12);
        this.x1 = x1;
        this.y1 = y1;
        x2 = x1 + length * Math.cos(Math.toRadians(90 - angle));
        y2 = y1 - length * Math.sin(Math.toRadians(90 - angle));
        for (HitBox hb : hitBoxes) {
            int[] temp = HitBox.sightHitBoxIntersection(new int[]{(int) x1, (int) x2}, new int[]{(int) y1, (int) y2}, hb);
            if (temp[0] != -1) {
                x2 = temp[0];
                y2 = temp[1];
            }
        }
    }

    public TankSight(boolean isSighting, double x1, double y1, double x2, double y2) {
        this.isSighting = isSighting;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public TankSight() {

    }

    public void setSighting(boolean isSighting) {
        this.isSighting = isSighting;
    }

    public boolean isSighting() {
        return isSighting;
    }

    public String getString() {
        String result = "";
        result += "" + isSighting + " ";
        result += "" + x1 + " ";
        result += "" + y1 + " ";
        result += "" + x2 + " ";
        result += "" + y2 + " ";
        return result;
    }
}
