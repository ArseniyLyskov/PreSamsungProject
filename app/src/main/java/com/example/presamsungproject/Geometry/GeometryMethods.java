package com.example.presamsungproject.Geometry;

import com.example.presamsungproject.Models.HitBox;

public class GeometryMethods {
    public static boolean isHitBoxesIntersect(HitBox hb1, HitBox hb2) {
        return isRectanglesIntersect(hb1.getRectangle(), hb2.getRectangle());
    }

    public static boolean isSegmentIntersectHitBox(Segment segment, HitBox hitBox) {
        return segmentRectangleIntersection(segment, hitBox.getRectangle()) != null;
    }

    public static Point segmentHitBoxIntersection(Segment segment, HitBox hitBox) {
        return segmentRectangleIntersection(segment, hitBox.getRectangle());
    }

    public static double getBulletRicochetAngle(double current_angle, Segment segment, HitBox hitBox) {
        Rectangle rectangle = hitBox.getRectangle();
        Segment segment1 = new Segment(rectangle.getP1(), rectangle.getP2());
        Segment segment2 = new Segment(rectangle.getP2(), rectangle.getP3());
        Segment segment3 = new Segment(rectangle.getP3(), rectangle.getP4());
        Segment segment4 = new Segment(rectangle.getP4(), rectangle.getP1());
        Point intersection1 = segmentsIntersection(segment, segment1);
        Point intersection2 = segmentsIntersection(segment, segment2);
        Point intersection3 = segmentsIntersection(segment, segment3);
        Point intersection4 = segmentsIntersection(segment, segment4);
        Point intersection = segmentRectangleIntersection(segment, hitBox.getRectangle());
        double intersectedSegmentAngle = 0;
        if (intersection1 != null) {
            if (intersection.equalsTo(intersection1)) {
                intersectedSegmentAngle = findSegmentAngle(segment1);
            }
        }
        if (intersection2 != null) {
            if (intersection.equalsTo(intersection2)) {
                intersectedSegmentAngle = findSegmentAngle(segment2);
            }
        }
        if (intersection3 != null) {
            if (intersection.equalsTo(intersection3)) {
                intersectedSegmentAngle = findSegmentAngle(segment3);
            }
        }
        if (intersection4 != null) {
            if (intersection.equalsTo(intersection4)) {
                intersectedSegmentAngle = findSegmentAngle(segment4);
            }
        }
        return intersectedSegmentAngle * 2 - current_angle;
    }

    private static double findSegmentAngle(Segment segment) {
        double segmentAngle;
        if (segment.getP1().getX() - segment.getP2().getX() == 0)
            segmentAngle = 0;
        else
            segmentAngle = 90 - Math.atan(
                    (double) (segment.getP1().getY() - segment.getP2().getY())
                            / (double) (segment.getP1().getX() - segment.getP2().getX()));
        return segmentAngle;
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

    private static boolean isPointInRectangle(Point point, Rectangle rectangle) {
        Triangle t1 = new Triangle(rectangle.getP1(), rectangle.getP2(), rectangle.getP3());
        Triangle t2 = new Triangle(rectangle.getP1(), rectangle.getP4(), rectangle.getP3());
        return isPointInTriangle(point, t1) ||
                isPointInTriangle(point, t2);
    }

    private static boolean isRectanglesIntersect(Rectangle rectangle1, Rectangle rectangle2) {
        if (rectangle1.maxXPoint().getX() < rectangle2.minXPoint().getX()
                || rectangle2.maxXPoint().getX() < rectangle1.minXPoint().getX()
                || rectangle1.maxYPoint().getY() < rectangle2.minYPoint().getY()
                || rectangle2.maxYPoint().getY() < rectangle1.minYPoint().getY())
            return false;
        boolean intersection =
                isPointInRectangle(rectangle2.getP1(), rectangle1) ||
                        isPointInRectangle(rectangle2.getP2(), rectangle1) ||
                        isPointInRectangle(rectangle2.getP3(), rectangle1) ||
                        isPointInRectangle(rectangle2.getP4(), rectangle1) ||
                        isPointInRectangle(rectangle1.getP1(), rectangle2) ||
                        isPointInRectangle(rectangle1.getP2(), rectangle2) ||
                        isPointInRectangle(rectangle1.getP3(), rectangle2) ||
                        isPointInRectangle(rectangle1.getP4(), rectangle2);
        if (intersection)
            return true;
        intersection = isSegmentIntersectRectangle(new Segment(rectangle1.getP1(), rectangle1.getP2()), rectangle2) ||
                isSegmentIntersectRectangle(new Segment(rectangle1.getP2(), rectangle1.getP3()), rectangle2) ||
                isSegmentIntersectRectangle(new Segment(rectangle1.getP3(), rectangle1.getP4()), rectangle2) ||
                isSegmentIntersectRectangle(new Segment(rectangle1.getP4(), rectangle1.getP1()), rectangle2);
        return intersection;
    }

    private static Point segmentRectangleIntersection(Segment segment, Rectangle rectangle) {
        Segment temp = new Segment(segment.getP1(), segment.getP2());
        Point point = null;
        Point intersection1 = segmentsIntersection(temp, new Segment(rectangle.getP1(), rectangle.getP2()));
        if (intersection1 != null) {
            point = intersection1;
            temp = new Segment(temp.getP1(), point);
        }
        Point intersection2 = segmentsIntersection(temp, new Segment(rectangle.getP2(), rectangle.getP3()));
        if (intersection2 != null) {
            point = intersection2;
            temp = new Segment(temp.getP1(), point);
        }
        Point intersection3 = segmentsIntersection(temp, new Segment(rectangle.getP3(), rectangle.getP4()));
        if (intersection3 != null) {
            point = intersection3;
            temp = new Segment(temp.getP1(), point);
        }
        Point intersection4 = segmentsIntersection(temp, new Segment(rectangle.getP4(), rectangle.getP1()));
        if (intersection4 != null) {
            point = intersection4;
        }
        return point;
    }

    private static boolean isSegmentsIntersect(Segment segment1, Segment segment2) {
        return segmentsIntersection(segment1, segment2) != null;
    }

    private static boolean isSegmentIntersectRectangle(Segment segment, Rectangle rectangle) {
        return segmentRectangleIntersection(segment, rectangle) != null;
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