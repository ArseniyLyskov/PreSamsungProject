package com.example.presamsungproject.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class HitBox {
    private double x, y;
    private double angle1, angle2, angle3, angle4;
    private int bmp_width, bmp_height;
    private double r1, r2, r3, r4;
    private int x1, y1, x2, y2, x3, y3, x4, y4;
    private Paint paint;

    {
        paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
    }

    public HitBox(double x, double y, double angle, int bmp_width, int bmp_height,
                  double[] indents) {
        this.bmp_width = bmp_width;
        this.bmp_height = bmp_height;
        this.x = x;
        this.y = y;

        double upIndent = indents[0];
        double rightIndent = indents[1];
        double downIndent = indents[2];
        double leftIndent = indents[3];

        angle1 = 180 - Math.toDegrees(Math.atan(upIndent / leftIndent));
        angle2 = Math.toDegrees(Math.atan(upIndent / rightIndent));
        angle3 = -Math.toDegrees(Math.atan(downIndent / rightIndent));
        angle4 = Math.toDegrees(Math.atan(downIndent / leftIndent)) - 180;

        r1 = Math.sqrt(Math.pow(bmp_width * leftIndent, 2) + Math.pow(bmp_height * upIndent, 2));
        r2 = Math.sqrt(Math.pow(bmp_width * rightIndent, 2) + Math.pow(bmp_height * upIndent, 2));
        r3 = Math.sqrt(Math.pow(bmp_width * rightIndent, 2) + Math.pow(bmp_height * downIndent, 2));
        r4 = Math.sqrt(Math.pow(bmp_width * leftIndent, 2) + Math.pow(bmp_height * downIndent, 2));

        updateProperties(x, y, angle);
    }

    public void draw(Canvas canvas) {
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.lineTo(x1, y1);
        canvas.drawPath(path, paint);
    }

    private int[] getXpoints() {
        int[] points = new int[4];
        points[0] = x1;
        points[1] = x2;
        points[2] = x3;
        points[3] = x4;
        return points;
    }

    private int[] getYpoints() {
        int[] points = new int[4];
        points[0] = y1;
        points[1] = y2;
        points[2] = y3;
        points[3] = y4;
        return points;
    }

    public void updateProperties(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        double xc = this.x + bmp_width / 2, yc = this.y + bmp_height / 2;
        double curAngle1 = angle1 - angle;
        double curAngle2 = angle2 - angle;
        double curAngle3 = angle3 - angle;
        double curAngle4 = angle4 - angle;
        x1 = (int) (xc + r1 * Math.cos(Math.toRadians(curAngle1)));
        x2 = (int) (xc + r2 * Math.cos(Math.toRadians(curAngle2)));
        x3 = (int) (xc + r3 * Math.cos(Math.toRadians(curAngle3)));
        x4 = (int) (xc + r4 * Math.cos(Math.toRadians(curAngle4)));
        y1 = (int) (yc - r1 * Math.sin(Math.toRadians(curAngle1)));
        y2 = (int) (yc - r2 * Math.sin(Math.toRadians(curAngle2)));
        y3 = (int) (yc - r3 * Math.sin(Math.toRadians(curAngle3)));
        y4 = (int) (yc - r4 * Math.sin(Math.toRadians(curAngle4)));
    }

    private static boolean isPointInTriangle(int x0, int y0,
                                             int x1, int y1, int x2, int y2, int x3, int y3) {
        double expr1 = (x1 - x0) * (y2 - y1) - (x2 - x1) * (y1 - y0);
        double expr2 = (x2 - x0) * (y3 - y2) - (x3 - x2) * (y2 - y0);
        double expr3 = (x3 - x0) * (y1 - y3) - (x1 - x3) * (y3 - y0);
        if (expr1 >= 0 && expr2 >= 0 && expr3 >= 0)
            return true;
        if (expr1 <= 0 && expr2 <= 0 && expr3 <= 0)
            return true;
        return false;
    }

    private static boolean isPointInSquare(int x0,  int y0,
                                           int[] x, int[] y) {
        return isPointInTriangle(x0, y0, x[0], y[0], x[1], y[1], x[2], y[2]) ||
                isPointInTriangle(x0, y0, x[0], y[0], x[3], y[3], x[2], y[2]);
    }

    private static boolean isSquaresIntersect(int[] x1, int[] y1,
                                              int[] x2, int[] y2) {
        return isPointInSquare(x2[0], y2[0], x1, y1) ||
                isPointInSquare(x2[1], y2[1], x1, y1) ||
                isPointInSquare(x2[2], y2[2], x1, y1) ||
                isPointInSquare(x2[3], y2[3], x1, y1) ||
                isPointInSquare(x1[0], y1[0], x2, y2) ||
                isPointInSquare(x1[1], y1[1], x2, y2) ||
                isPointInSquare(x1[2], y1[2], x2, y2) ||
                isPointInSquare(x1[3], y1[3], x2, y2);

    }

    public static boolean isHitBoxesIntersect(HitBox hb1, HitBox hb2) {
        return isSquaresIntersect(hb1.getXpoints(), hb1.getYpoints(), hb2.getXpoints(), hb2.getYpoints());
    }

    public static int[] sightHitBoxIntersection(int[] x, int[] y, HitBox hitBox) {
        int[] hbXpoints = hitBox.getXpoints();
        int[] hbYpoints = hitBox.getYpoints();
        int result_x = -1, result_y = -1;
        for (int i = 0; i < 3; i++) {
            int[] temp = sightSegmentIntersection(x, y, new int[]{hbXpoints[i], hbXpoints[i+1]}, new int[]{hbYpoints[i], hbYpoints[i+1]});
            if(temp[0] != -1 && (result_x == -1 || Math.abs(x[0] - temp[0]) < Math.abs(x[0] - result_x))) {
                result_x = temp[0];
                result_y = temp[1];
            }
        }
        return new int[]{result_x, result_y};
    }

    private static int[] sightSegmentIntersection(int[] x1, int[] y1,
                                             int[] x2, int[] y2) {

        int maxX1 = Math.max(x1[0], x1[1]);
        int maxY1 = Math.max(y1[0], y1[1]);
        int maxX2 = Math.max(x2[0], x2[1]);
        int maxY2 = Math.max(y2[0], y2[1]);
        int minX1 = Math.min(x1[0], x1[1]);
        int minY1 = Math.min(y1[0], y1[1]);
        int minX2 = Math.min(x2[0], x2[1]);
        int minY2 = Math.min(y2[0], y2[1]);

        if(maxX1 < minX2 || maxX2 < minX1 || maxY1 < minY2 || maxY2 < minY1)
            return new int[]{-1, -1};

        if((x1[0] - x1[1] == 0) && (x2[0] - x2[1] == 0)) {
            if(y1[0] > maxY2)
                return new int[]{x1[0], maxY2};
            if(y1[0] < minY2)
                return new int[]{x1[0], minY2};
            return new int[]{x1[0], y1[0]};
        }

        if(x1[0] - x1[1] == 0) {
            int x_nizhn;
            if(maxY2 == y2[0])
                x_nizhn = x2[0];
            else
                x_nizhn = x2[1];
            double tg = (double) (maxY2 - minY2) / (double) (maxX2 - minX2);
            double y = maxY2 - Math.abs(x_nizhn - x1[0]) * tg;

            if(y < maxY1 && y > minY1)
                return new int[]{x1[0], (int) y};
            else
                return new int[]{-1, -1};

        }

        if(x2[0] - x2[1] == 0) {
            int x_nizhn;
            if(maxY1 == y1[0])
                x_nizhn = x1[0];
            else
                x_nizhn = x1[1];
            double tg = (double) (maxY1 - minY1) / (double) (maxX1 - minX1);
            double y = maxY1 - Math.abs(x_nizhn - x2[0]) * tg;

            if(y < maxY2 && y > minY2)
                return new int[]{x2[0], (int) y};
            else
                return new int[]{-1, -1};

        }

        double k1 = (double) (y1[0] - y1[1]) / (double) (x1[0] - x1[1]);
        double k2 = (double) (y2[0] - y2[1]) / (double) (x2[0] - x2[1]);

        if (k1 == k2) {
            return new int[]{-1, -1};
        }

        double b1 = y1[0] - x1[0] * k1;
        double b2 = y2[0] - x2[0] * k2;
        double x = (b2 - b1) / (k1 - k2);
        double y = k1 * x + b1;

        if(x < maxX1 && x > minX1 && x < maxX2 && x > minX2) {
            return new int[]{(int) x, (int) y};
        } else {
            return new int[]{-1, -1};
        }

    }
}
