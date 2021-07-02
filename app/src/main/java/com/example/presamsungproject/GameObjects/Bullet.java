package com.example.presamsungproject.GameObjects;

import android.graphics.Canvas;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Geometry.GeometryMethods;
import com.example.presamsungproject.Geometry.Point;
import com.example.presamsungproject.Geometry.Segment;
import com.example.presamsungproject.Models.HitBox;
import com.example.presamsungproject.Models.MySingletons;
import com.example.presamsungproject.Models.MySoundEffects;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

public class Bullet implements Serializable {
    private final transient double speed;
    private transient int ricochets;
    private transient double angle;
    private transient boolean ricochetAble;

    private Point point;
    private static final long serialVersionUID = 4L;

    {
        ricochets = 3;
        speed = 1200;
    }

    public void scaleTo(double koeff) {
        point.scaleTo(koeff);
    }

    public Bullet(double x, double y, double angle, boolean ricochetAble) {
        point = new Point((int) x, (int) y);
        this.angle = angle;
        this.ricochetAble = ricochetAble;
        if (!ricochetAble)
            ricochets = 0;
    }

    public Bullet(double x, double y) {
        point = new Point((int) x, (int) y);
    }

    public void update(MyTank myTank, double speed_koeff, HashSet<HitBox> walls, Collection<Tank> otherTanks) {
        int newX = (int) (point.getX() + speed * Math.cos(Math.toRadians(90 - angle)) * speed_koeff);
        int newY = (int) (point.getY() - speed * Math.sin(Math.toRadians(90 - angle)) * speed_koeff);
        for (Tank tank : otherTanks) {
            if (GeometryMethods.isSegmentIntersectHitBox(new Segment(point, new Point(newX, newY)), tank.hullHitBox)
                    || GeometryMethods.isSegmentIntersectHitBox(new Segment(point, new Point(newX, newY)), tank.towerHitBox)) {
                if (MySingletons.isLobby()) {
                    MySingletons.getServer().specificMessage(tank.address, MessageManager.hitMessage(tank.address));
                } else {
                    MySingletons.getClient().sendMessage(MessageManager.hitMessage(tank.address));
                }
                ricochets = -1;
                MessageManager.sendMyTank();
                return;
            }
        }
        if (ricochets != 3 && ricochetAble)
            if (GeometryMethods.isSegmentIntersectHitBox(new Segment(point, new Point(newX, newY)), myTank.hullHitBox)
                    || GeometryMethods.isSegmentIntersectHitBox(new Segment(point, new Point(newX, newY)), myTank.towerHitBox)) {
                ricochets = -1;
                myTank.minusHealth();
                MessageManager.sendMyTank();
                return;
            }
        boolean changeCoordinates = true;
        for (HitBox hb : walls) {
            if (GeometryMethods.isSegmentIntersectHitBox(new Segment(point, new Point(newX, newY)), hb)) {
                if (ricochets > 0)
                    MessageManager.sendSFX(MySoundEffects.RICOCHET);
                angle = GeometryMethods.getBulletRicochetAngle(angle, new Segment(point, new Point(newX, newY)), hb);
                ricochets--;
                changeCoordinates = false;
                break;
            }
        }
        if (changeCoordinates) {
            point = new Point(newX, newY);
        }
    }

    public void drawHitBox(Canvas canvas) {
        canvas.drawRect(point.getX() - 5, point.getY() - 5, point.getX() + 5, point.getY() + 5,
                MySingletons.getMyResources().getHitBoxPaint());
    }

    public Point getPoint() {
        return point;
    }

    public int getRicochets() {
        return ricochets;
    }
}