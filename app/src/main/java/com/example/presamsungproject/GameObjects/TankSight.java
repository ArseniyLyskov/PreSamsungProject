package com.example.presamsungproject.GameObjects;

import android.graphics.Canvas;
import com.example.presamsungproject.Geometry.GeometryMethods;
import com.example.presamsungproject.Geometry.Point;
import com.example.presamsungproject.Geometry.Segment;
import com.example.presamsungproject.Models.HitBox;
import com.example.presamsungproject.Models.Resources;

import java.io.Serializable;
import java.util.HashSet;

public class TankSight implements Serializable {
    private boolean isSighting;
    private Segment segment;

    public void draw(Canvas canvas) {
        canvas.drawLine(segment.getP1().getX(), segment.getP1().getY(),
                segment.getP2().getX(), segment.getP2().getY(),
                Resources.getInstance().getTankSightPaint());
    }

    public void scaleTo(double koeff) {
        segment.scaleTo(koeff);
    }

    public void update(double x, double y, double angle, HashSet<HitBox> hitBoxes) {
        int length = (int) Math.pow(2, 12);
        int segmentEndCoordinateX = (int) (x + length * Math.cos(Math.toRadians(90 - angle)));
        int segmentEndCoordinateY = (int) (y - length * Math.sin(Math.toRadians(90 - angle)));
        Point segmentStart = new Point((int) x, (int) y);
        Point segmentEnd = new Point(segmentEndCoordinateX, segmentEndCoordinateY);
        for (HitBox hb : hitBoxes) {
            Point temp = GeometryMethods.segmentHitBoxIntersection(new Segment(segmentStart, segmentEnd), hb);
            if (temp != null) {
                segmentEnd = temp;
            }
        }
        segment = new Segment(segmentStart, segmentEnd);
    }

    public TankSight() {
        isSighting = false;
        segment = new Segment(new Point(0, 0), new Point(0, 0));
    }

    public void setSighting(boolean isSighting) {
        this.isSighting = isSighting;
    }

    public boolean isSighting() {
        return isSighting;
    }

}