package com.example.presamsungproject.GameObjects;

import android.graphics.Canvas;
import com.example.presamsungproject.Geometry.GeometryMethods;
import com.example.presamsungproject.Geometry.Point;
import com.example.presamsungproject.Geometry.Segment;
import com.example.presamsungproject.HitBox;
import com.example.presamsungproject.MySingletons;

import java.io.Serializable;
import java.util.HashSet;

public class TankSight implements Serializable {
    private boolean isSighting;
    private Segment segment;
    private static final long serialVersionUID = 3L;

    {
        isSighting = false;
    }

    public void draw(Canvas canvas) {
        canvas.drawLine(segment.getP1().getX(), segment.getP1().getY(),
                segment.getP2().getX(), segment.getP2().getY(),
                MySingletons.getMyResources().getTankSightPaint());
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

    public TankSight(boolean isSighting, Segment segment) {
        this.isSighting = isSighting;
        this.segment = segment;
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