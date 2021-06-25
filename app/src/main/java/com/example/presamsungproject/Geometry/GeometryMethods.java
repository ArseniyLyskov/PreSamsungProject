package com.example.presamsungproject.Geometry;

import com.example.presamsungproject.HitBox;

public class GeometryMethods {
    public static boolean isHitBoxesIntersect(HitBox hb1, HitBox hb2) {
        return isSquaresIntersect(hb1.getSquare(), hb2.getSquare());
    }

    public static Point segmentHitBoxIntersection(Segment segment, HitBox hitBox) {
        return segmentSquareIntersection(segment, hitBox.getSquare());
    }

    public static double getBulletRicochetAngle(double current_angle, Segment segment, HitBox hitBox) {
        Square square = hitBox.getSquare();
        Segment segment1 = new Segment(square.getP1(), square.getP2());
        Segment segment2 = new Segment(square.getP2(), square.getP3());
        Segment segment3 = new Segment(square.getP3(), square.getP4());
        Segment segment4 = new Segment(square.getP4(), square.getP1());
        Point intersection1 = segmentsIntersection(segment, segment1);
        Point intersection2 = segmentsIntersection(segment, segment2);
        Point intersection3 = segmentsIntersection(segment, segment3);
        Point intersection4 = segmentsIntersection(segment, segment4);
        Point intersection = segmentSquareIntersection(segment, hitBox.getSquare());
        double intersectedSegmentAngle = 0;
        if (intersection1 != null) {
            if (intersection.equalsTo(intersection1)) {
                intersectedSegmentAngle = findIntersectedSegmentAngle(segment1);
            }
        }
        if (intersection2 != null) {
            if (intersection.equalsTo(intersection2)) {
                intersectedSegmentAngle = findIntersectedSegmentAngle(segment2);
            }
        }
        if (intersection3 != null) {
            if (intersection.equalsTo(intersection3)) {
                intersectedSegmentAngle = findIntersectedSegmentAngle(segment3);
            }
        }
        if (intersection4 != null) {
            if (intersection.equalsTo(intersection4)) {
                intersectedSegmentAngle = findIntersectedSegmentAngle(segment4);
            }
        }
        return intersectedSegmentAngle * 2 - current_angle;
    }

    private static double findIntersectedSegmentAngle(Segment segment) {
        double intersectedSegmentAngle;
        if (segment.getP1().getX() - segment.getP2().getX() == 0)
            intersectedSegmentAngle = 0;
        else
            intersectedSegmentAngle = 90 - Math.atan(
                    (double) (segment.getP1().getY() - segment.getP2().getY())
                            / (double) (segment.getP1().getX() - segment.getP2().getX()));
        return intersectedSegmentAngle;
    }

    private static boolean isPointInTriangle(Point point, Triangle triangle) {
        int x0 = point.getX(), y0 = point.getY();
        int x1 = triangle.getP1().getX(), y1 = triangle.getP1().getY();
        int x2 = triangle.getP2().getX(), y2 = triangle.getP2().getY();
        int x3 = triangle.getP3().getX(), y3 = triangle.getP3().getY();
        double expr1 = (x1 - x0) * (y2 - y1) - (x2 - x1) * (y1 - y0);
        double expr2 = (x2 - x0) * (y3 - y2) - (x3 - x2) * (y2 - y0);
        double expr3 = (x3 - x0) * (y1 - y3) - (x1 - x3) * (y3 - y0);
        if (expr1 >= 0 && expr2 >= 0 && expr3 >= 0)
            return true;
        return expr1 <= 0 && expr2 <= 0 && expr3 <= 0;
    }

    private static boolean isPointInSquare(Point point, Square square) {
        Triangle t1 = new Triangle(square.getP1(), square.getP2(), square.getP3());
        Triangle t2 = new Triangle(square.getP1(), square.getP4(), square.getP3());
        return isPointInTriangle(point, t1) ||
                isPointInTriangle(point, t2);
    }

    private static boolean isSquaresIntersect(Square square1, Square square2) {
        boolean intersection = isPointInSquare(square2.getP1(), square1) ||
                isPointInSquare(square2.getP2(), square1) ||
                isPointInSquare(square2.getP3(), square1) ||
                isPointInSquare(square2.getP4(), square1) ||
                isPointInSquare(square1.getP1(), square2) ||
                isPointInSquare(square1.getP2(), square2) ||
                isPointInSquare(square1.getP3(), square2) ||
                isPointInSquare(square1.getP4(), square2);
        if (intersection)
            return true;
        else {
            if (square1.maxX() <= square2.maxX() && square1.minX() >= square2.minX())
                if (square1.maxY() >= square2.maxY() && square1.minY() <= square2.minY())
                    return true;
            if (square1.maxY() <= square2.maxY() && square1.minY() >= square2.minY())
                return square1.maxX() >= square2.maxX() && square1.minX() <= square2.minX();
        }
        return false;
    }

    private static Point segmentSquareIntersection(Segment segment, Square square) {
        Segment temp = new Segment(segment.getP1(), segment.getP2());
        Point point = null;
        Point intersection1 = segmentsIntersection(temp, new Segment(square.getP1(), square.getP2()));
        if (intersection1 != null) {
            point = intersection1;
            temp = new Segment(temp.getP1(), point);
        }
        Point intersection2 = segmentsIntersection(temp, new Segment(square.getP2(), square.getP3()));
        if (intersection2 != null) {
            point = intersection2;
            temp = new Segment(temp.getP1(), point);
        }
        Point intersection3 = segmentsIntersection(temp, new Segment(square.getP3(), square.getP4()));
        if (intersection3 != null) {
            point = intersection3;
            temp = new Segment(temp.getP1(), point);
        }
        Point intersection4 = segmentsIntersection(temp, new Segment(square.getP4(), square.getP1()));
        if (intersection4 != null) {
            point = intersection4;
        }
        return point;
    }

    private static Point segmentsIntersection(Segment segment1, Segment segment2) {
        int maxX1 = segment1.maxX();
        int maxY1 = segment1.maxY();
        int maxX2 = segment2.maxX();
        int maxY2 = segment2.maxY();
        int minX1 = segment1.minX();
        int minY1 = segment1.minY();
        int minX2 = segment2.minX();
        int minY2 = segment2.minY();

        if (maxX1 < minX2 || maxX2 < minX1 || maxY1 < minY2 || maxY2 < minY1) {
            return null;
        }

        if (segment1.getP1().getX() - segment1.getP2().getX() == 0
                && segment2.getP1().getX() - segment2.getP2().getX() == 0) {
            if (segment1.getP1().getY() > maxY2)
                return new Point(segment1.getP1().getX(), maxY2);
            if (segment1.getP1().getY() < minY2)
                return new Point(segment1.getP1().getX(), minY2);
            return segment1.getP1();
        }

        if (segment1.getP1().getX() - segment1.getP2().getX() == 0) {
            int x_lower;
            if (maxY2 == segment2.getP1().getY())
                x_lower = segment2.getP1().getX();
            else
                x_lower = segment2.getP2().getX();
            double tg = (double) (maxY2 - minY2) / (double) (maxX2 - minX2);
            double y = maxY2 - Math.abs(x_lower - segment1.getP1().getX()) * tg;

            if (y > minY1 && y < maxY1)
                return new Point(segment1.getP1().getX(), (int) y);
            else
                return null;
        }

        if (segment2.getP1().getX() - segment2.getP2().getX() == 0) {
            int x_lower;
            if (maxY1 == segment1.getP1().getY())
                x_lower = segment1.getP1().getX();
            else
                x_lower = segment1.getP2().getX();
            double tg = (double) (maxY1 - minY1) / (double) (maxX1 - minX1);
            double y = maxY1 - Math.abs(x_lower - segment2.getP1().getX()) * tg;

            if (y > minY2 && y < maxY2)
                return new Point(segment2.getP1().getX(), (int) y);
            else
                return null;
        }

        double k1 = (double) (segment1.getP1().getY() - segment1.getP2().getY())
                / (double) (segment1.getP1().getX() - segment1.getP2().getX());
        double k2 = (double) (segment2.getP1().getY() - segment2.getP2().getY())
                / (double) (segment2.getP1().getX() - segment2.getP2().getX());

        if (k1 == k2) {
            return null;
        }

        double b1 = segment1.getP1().getY() - segment1.getP1().getX() * k1;
        double b2 = segment2.getP1().getY() - segment2.getP1().getX() * k2;
        double temp_x = (b2 - b1) / (k1 - k2);
        double temp_y = k1 * temp_x + b1;
        int x = (int) temp_x;
        int y = (int) temp_y;

        if (x - Math.min(maxX1, maxX2) > 1 || Math.max(minX1, minX2) - x > 1 || y - Math.min(maxY1, maxY2) > 1 || Math.max(minY1, minY2) - y > 1) {
            return null;
        } else {
            return new Point(x, y);
        }
    }
}