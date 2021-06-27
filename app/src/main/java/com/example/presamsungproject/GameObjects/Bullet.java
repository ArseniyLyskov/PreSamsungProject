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

    private Point point;
    private static final long serialVersionUID = 4L;

    {
        ricochets = 3;
        speed = 1200;
    }

    public void scaleTo(double koeff) {
        point.scaleTo(koeff);
    }

    public Bullet(double x, double y, double angle) {
        point = new Point((int) x, (int) y);
        this.angle = angle;
    }

    public Bullet(double x, double y) {
        point = new Point((int) x, (int) y);
    }

    public void update(MyTank myTank, double speed_koeff, HashSet<HitBox> walls, Collection<Tank> otherTanks) {
        int newX = (int) (point.getX() + speed * Math.cos(Math.toRadians(90 - angle)) * speed_koeff);
        int newY = (int) (point.getY() - speed * Math.sin(Math.toRadians(90 - angle)) * speed_koeff);
        for (Tank tank : otherTanks) {
            if (GeometryMethods.segmentHitBoxIntersection(new Segment(point, new Point(newX, newY)), tank.hullHitBox) != null
                    || GeometryMethods.segmentHitBoxIntersection(new Segment(point, new Point(newX, newY)), tank.towerHitBox) != null) {
                if (MySingletons.isLobby()) {
                    MySingletons.getServer().specificMessage(tank.address, MessageManager.hitMessage(tank.address));
                } else {
                    MySingletons.getClient().sendMessage(MessageManager.hitMessage(tank.address));
                }
                ricochets = -1;
                if(tank.getHp() > 1)
                    MessageManager.sendSFX(MySoundEffects.HIT);
                else
                    MessageManager.sendSFX(MySoundEffects.EXPLOSION);
                MessageManager.sendMyTank();
                return;
            }
        }
        if (ricochets != 3)
            if (GeometryMethods.segmentHitBoxIntersection(new Segment(point, new Point(newX, newY)), myTank.hullHitBox) != null
                    || GeometryMethods.segmentHitBoxIntersection(new Segment(point, new Point(newX, newY)), myTank.towerHitBox) != null) {
                ricochets = -1;
                if(myTank.getHp() > 1)
                    MessageManager.sendSFX(MySoundEffects.HIT);
                else
                    MessageManager.sendSFX(MySoundEffects.EXPLOSION);
                myTank.minusHealth();
                MessageManager.sendMyTank();
                return;
            }
        boolean changeCoordinates = true;
        for (HitBox hb : walls) {
            if (GeometryMethods.segmentHitBoxIntersection(new Segment(point, new Point(newX, newY)), hb) != null) {
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