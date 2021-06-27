package com.example.presamsungproject.Models;

import android.graphics.Canvas;
import android.graphics.Path;
import com.example.presamsungproject.Geometry.Point;
import com.example.presamsungproject.Geometry.Quadrangle;

import java.io.Serializable;

public class HitBox implements Serializable {
    private transient double x, y;
    private transient final double angle1, angle2, angle3, angle4;
    private transient final int bmp_width, bmp_height;
    private transient final double r1, r2, r3, r4;

    private Quadrangle quadrangle;
    private static final long serialVersionUID = 5L;

    public HitBox(double x, double y, double angle, int bmp_width, int bmp_height,
                  double[] indents) {
        this.bmp_width = bmp_width;
        this.bmp_height = bmp_height;
        this.x = x;
        this.y = y;

        double upIndent;
        double rightIndent;
        double downIndent;
        double leftIndent;

        if (indents != null) {
            upIndent = indents[0];
            rightIndent = indents[1];
            downIndent = indents[2];
            leftIndent = indents[3];
        } else {
            upIndent = 1 / 2f;
            rightIndent = 1 / 2f;
            downIndent = 1 / 2f;
            leftIndent = 1 / 2f;
        }

        angle1 = 180 - Math.toDegrees(Math.atan((upIndent * bmp_height) / (leftIndent * bmp_width)));
        angle2 = Math.toDegrees(Math.atan((upIndent * bmp_height) / (rightIndent * bmp_width)));
        angle3 = -Math.toDegrees(Math.atan((downIndent * bmp_height) / (rightIndent * bmp_width)));
        angle4 = Math.toDegrees(Math.atan((downIndent * bmp_height) / (leftIndent * bmp_width))) - 180;

        r1 = Math.sqrt(Math.pow(bmp_width * leftIndent, 2) + Math.pow(bmp_height * upIndent, 2));
        r2 = Math.sqrt(Math.pow(bmp_width * rightIndent, 2) + Math.pow(bmp_height * upIndent, 2));
        r3 = Math.sqrt(Math.pow(bmp_width * rightIndent, 2) + Math.pow(bmp_height * downIndent, 2));
        r4 = Math.sqrt(Math.pow(bmp_width * leftIndent, 2) + Math.pow(bmp_height * downIndent, 2));

        updateProperties(x, y, angle);
    }

    public void scaleTo(double koeff) {
        quadrangle.scaleTo(koeff);
    }

    public void draw(Canvas canvas) {
        Path path = new Path();
        path.moveTo(quadrangle.getP1().getX(), quadrangle.getP1().getY());
        path.lineTo(quadrangle.getP2().getX(), quadrangle.getP2().getY());
        path.lineTo(quadrangle.getP3().getX(), quadrangle.getP3().getY());
        path.lineTo(quadrangle.getP4().getX(), quadrangle.getP4().getY());
        path.lineTo(quadrangle.getP1().getX(), quadrangle.getP1().getY());
        canvas.drawPath(path, MySingletons.getMyResources().getHitBoxPaint());
    }

    public Quadrangle getSquare() {
        return quadrangle;
    }

    public void updateProperties(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        double xc = this.x + bmp_width / 2f, yc = this.y + bmp_height / 2f;
        double curAngle1 = angle1 - angle;
        double curAngle2 = angle2 - angle;
        double curAngle3 = angle3 - angle;
        double curAngle4 = angle4 - angle;
        int x1 = (int) (xc + r1 * Math.cos(Math.toRadians(curAngle1)));
        int x2 = (int) (xc + r2 * Math.cos(Math.toRadians(curAngle2)));
        int x3 = (int) (xc + r3 * Math.cos(Math.toRadians(curAngle3)));
        int x4 = (int) (xc + r4 * Math.cos(Math.toRadians(curAngle4)));
        int y1 = (int) (yc - r1 * Math.sin(Math.toRadians(curAngle1)));
        int y2 = (int) (yc - r2 * Math.sin(Math.toRadians(curAngle2)));
        int y3 = (int) (yc - r3 * Math.sin(Math.toRadians(curAngle3)));
        int y4 = (int) (yc - r4 * Math.sin(Math.toRadians(curAngle4)));
        quadrangle = new Quadrangle(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3), new Point(x4, y4));
    }
}