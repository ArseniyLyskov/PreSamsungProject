package com.example.presamsungproject.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Geometry.GeometryMethods;
import com.example.presamsungproject.Models.Game;
import com.example.presamsungproject.Models.HitBox;
import com.example.presamsungproject.Models.MySingletons;
import com.example.presamsungproject.Models.MySoundEffects;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class MyTank extends Tank {
    private final double speed;
    private final Game game;
    private double current_speed;
    private HitBox updatedHhb, updatedThb;
    private Bitmap bmp_hull, bmp_tower;
    private final double[] updatedHullIndents;
    private final boolean ricochetAble;

    {
        speed = 800;
        updatedHullIndents = Arrays.copyOf(hullIndents, hullIndents.length);
        updatedHullIndents[2] = 13 / 50f;
        updatedHhb = new HitBox(x, y, 0, 1, 1, hullIndents);
        updatedThb = new HitBox(x, y, 0, 1, 1, towerIndents);
    }

    public MyTank(int hp, int team, double x, double y, String playerName, boolean ricochetAble, Game game) {

        super(hp, team, x, y, 0, 0, playerName, new TankSight(), new HashSet<>());
        this.ricochetAble = ricochetAble;
        this.game = game;
        bmp_hull = MySingletons.getMyResources().getBmp_greenHp();
        bmp_tower = MySingletons.getMyResources().getBmp_greenTp();
        hullHitBox = new HitBox(x, y, angleH, bmp_hull.getWidth(), bmp_hull.getHeight(), hullIndents);
        towerHitBox = new HitBox(x, y, angleH + angleT, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);
        scale = game.getScale();
    }

    public Tank getSimpleVersion() {
        return new Tank(hp, team, x, y, angleH, angleT, playerName, tankSight, bullets, hullHitBox, towerHitBox, MessageManager.EXTERNAL_ADDRESS, scale);
    }

    public void updateMyTankProperties() {
        double speed_koeff = 1f / game.getPreviousPFS();

        if (!checkIsAlive(speed_koeff))
            return;

        double newX = x;
        double newY = y;
        double newAngleT = angleT;
        double newAngleH = angleH;
        double newSpeed = current_speed;
        boolean newSighting;

        double xChange = speed * game.getlJstrength() / 100 * Math.cos(Math.toRadians(game.getlJangle())) * speed_koeff;
        double yChange = speed * game.getlJstrength() / 100 * Math.sin(Math.toRadians(game.getlJangle())) * speed_koeff;
        double hAngleChange = 90 - game.getlJangle();
        double tAngleChange = 90 - game.getrJangle();

        double maxChange = Math.max(Math.abs(xChange), Math.abs(yChange));
        double tankWidth = bmp_hull.getWidth() * (hullIndents[1] + hullIndents[3]);
        if (maxChange > tankWidth) {
            double normalize_speed_koeff = tankWidth / maxChange;
            xChange *= normalize_speed_koeff;
            yChange *= normalize_speed_koeff;
        }

        if (game.getlJstrength() == 0) {
            xChange = 0;
            yChange = 0;
            hAngleChange = angleH;
        }

        if (game.getrJstrength() == 0) {
            tAngleChange = hAngleChange;
        }

        HashSet<HitBox> hitBoxes = game.getAllHitBoxes();

        updatedHhb = new HitBox(x + xChange, y - yChange, hAngleChange, bmp_hull.getWidth(), bmp_hull.getHeight(), updatedHullIndents);
        updatedThb = new HitBox(x + xChange, y - yChange, tAngleChange, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);

        HitBox updatedXHhb = new HitBox(x + xChange, y, hAngleChange, bmp_hull.getWidth(), bmp_hull.getHeight(), updatedHullIndents);
        HitBox updatedXThb = new HitBox(x + xChange, y, tAngleChange, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);
        HitBox updatedYHhb = new HitBox(x, y - yChange, hAngleChange, bmp_hull.getWidth(), bmp_hull.getHeight(), updatedHullIndents);
        HitBox updatedYThb = new HitBox(x, y - yChange, tAngleChange, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);

        boolean isHullMoveAble = true;
        boolean isTowerMoveAble = true;

        boolean isXHullMoveAble = true;
        boolean isXTowerMoveAble = true;
        boolean isYHullMoveAble = true;
        boolean isYTowerMoveAble = true;

        for (HitBox hb : hitBoxes) {
            if (GeometryMethods.isHitBoxesIntersect(hb, updatedHhb)) {
                isHullMoveAble = false;
                break;
            }
        }

        for (HitBox hb : hitBoxes) {
            if (GeometryMethods.isHitBoxesIntersect(hb, updatedThb)) {
                isTowerMoveAble = false;
                isHullMoveAble = false;
                break;
            }
        }

        if (!isHullMoveAble) {
            for (HitBox hb : hitBoxes) {
                if (GeometryMethods.isHitBoxesIntersect(hb, updatedXHhb)) {
                    isXHullMoveAble = false;
                    break;
                }
            }
            for (HitBox hb : hitBoxes) {
                if (GeometryMethods.isHitBoxesIntersect(hb, updatedYHhb)) {
                    isYHullMoveAble = false;
                    break;
                }
            }
        }
        if (!isHullMoveAble) {
            for (HitBox hb : hitBoxes) {
                if (GeometryMethods.isHitBoxesIntersect(hb, updatedXThb)) {
                    isXTowerMoveAble = false;
                    isXHullMoveAble = false;
                    break;
                }
            }
            for (HitBox hb : hitBoxes) {
                if (GeometryMethods.isHitBoxesIntersect(hb, updatedYThb)) {
                    isYTowerMoveAble = false;
                    isYHullMoveAble = false;
                    break;
                }
            }
        }


        if (isHullMoveAble || isXHullMoveAble || isYHullMoveAble) {
            newAngleH = hAngleChange;

            if (isHullMoveAble) {
                newX = x + xChange;
                newY = y - yChange;
            } else if (isXHullMoveAble) {
                newX = x + xChange;
            } else {
                newY = y - yChange;
            }

            if (game.getlJstrength() > 0) {
                newSpeed = speed * game.getlJstrength() / 100;
            } else
                newSpeed = 0;
        }

        if (isTowerMoveAble || isXTowerMoveAble || isYTowerMoveAble) {
            newAngleT = tAngleChange;
        } else {
            newAngleT += newAngleH;
        }

        if (game.getrJstrength() > 0)
            newSighting = true;
        else {
            if (tankSight.isSighting())
                createBullet();
            newSighting = false;
        }

        if (newX != x || newY != y || newAngleT != angleT || newAngleH != angleH
                || newSighting != tankSight.isSighting() || bullets.size() > 0) {
            x = newX;
            y = newY;
            angleT = newAngleT - newAngleH;
            angleH = newAngleH;
            current_speed = newSpeed;
            tankSight.setSighting(newSighting);
            MessageManager.sendMyTank();
        }

        hullHitBox.updateProperties(x, y, angleH);
        towerHitBox.updateProperties(x, y, angleH + angleT);

        updateBulletsCoordinates(speed_koeff);
        if (tankSight.isSighting())
            tankSight.update(x + bmp_tower.getWidth() / 2f + bmp_tower.getWidth() * towerIndents[0] * Math.cos(Math.toRadians(90 - angleH - angleT)),
                    y + bmp_tower.getHeight() / 2f - bmp_tower.getHeight() * towerIndents[0] * Math.sin(Math.toRadians(90 - angleH - angleT)),
                    angleH + angleT, hitBoxes);
    }

    private void updateBulletsCoordinates(double speed_koeff) {
        HashSet<Bullet> temp = new HashSet<>(bullets);
        HashSet<HitBox> walls = game.getWallsHitBoxes();
        Collection<Tank> otherTanks = game.getOtherTanks().values();
        for (Bullet b : temp) {
            b.update(this, speed_koeff, walls, otherTanks);
            if (b.getRicochets() < 0) {
                bullets.remove(b);
                MessageManager.sendMyTank();
            }
        }
    }

    private boolean checkIsAlive(double speed_koeff) {
        boolean isAlive = hp > 0;
        if (!isAlive) {
            if (tankSight.isSighting()) {
                tankSight.setSighting(false);
                MessageManager.sendMyTank();
            }
            updateBulletsCoordinates(speed_koeff);
        }
        return isAlive;
    }

    private void createBullet() {
        MessageManager.sendSFX(MySoundEffects.SHOOT);
        bullets.add(new Bullet(
                (int) (x + bmp_tower.getWidth() / 2 + bmp_tower.getWidth() / 2 * Math.cos(Math.toRadians(90 - (angleH + angleT)))),
                (int) (y + bmp_tower.getHeight() / 2 - bmp_tower.getHeight() / 2 * Math.sin(Math.toRadians(90 - (angleH + angleT)))),
                angleH + angleT, ricochetAble));
    }


    public void draw(Canvas canvas, Paint paint, Bitmap bmp_hull, Bitmap bmp_tower) {
        this.bmp_hull = bmp_hull;
        this.bmp_tower = bmp_tower;

        updateMyTankProperties();
        super.draw(canvas, paint, bmp_hull, bmp_tower);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public HitBox getUpdatedHhb() {
        return updatedHhb;
    }

    public HitBox getUpdatedThb() {
        return updatedThb;
    }

    public void minusHealth() {
        hp--;
        if (hp > 0) {
            MessageManager.sendSFX(MySoundEffects.HIT);
            MySingletons.getMyResources().getGUIUListener().vibrate(300);
        } else {
            MessageManager.sendSFX(MySoundEffects.EXPLOSION);
            if (hp > -1)
                MySingletons.getMyResources().getGUIUListener().vibrate(1000);
        }
    }
}