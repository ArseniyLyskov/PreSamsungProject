package com.example.presamsungproject.GameObjects;

import android.graphics.Canvas;
import com.example.presamsungproject.HitBox;
import com.example.presamsungproject.MyPaints;

import java.io.Serializable;
import java.util.HashSet;

public class TankSight implements Serializable {
    private boolean isSighting;
    private double x1, y1, x2, y2;
    private static final long serialVersionUID = 3L;

    {
        isSighting = false;
    }

    public void draw(Canvas canvas) {
        canvas.drawLine((int) x1, (int) y1, (int) x2, (int) y2, MyPaints.getTankSightPaint());
    }

    public void scaleTo(double koeff) {
        x1 *= koeff;
        y1 *= koeff;
        x2 *= koeff;
        y2 *= koeff;
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

}