package com.example.presamsungproject.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.example.presamsungproject.Game;
import com.example.presamsungproject.HitBox;

import java.util.Arrays;
import java.util.HashSet;

public class MyTank extends Tank {
    private double speed, current_speed;
    private double hp;
    private Game game;
    private HitBox updatedHhb, updatedThb;
    private double[] updatedHullIndents;

    {
        updatedHullIndents = Arrays.copyOf(hullIndents, hullIndents.length);
        updatedHullIndents[2] = 13 / 50f;
        updatedHhb = new HitBox(x, y, 0, 1, 1, hullIndents);
        updatedThb = new HitBox(x, y, 0, 1, 1, towerIndents);
    }

    public MyTank(double x, double y, double angleH, double angleT, String playerName,
                  TankSight tankSight, Bitmap bmp_hull, Bitmap bmp_tower, Paint myPaint,
                  HashSet<Bullet> bullets, long id,
                  double speed, double hp, Game game) {

        super(x, y, angleH, angleT, playerName, tankSight, bmp_hull, bmp_tower, myPaint, bullets, id);
        this.speed = speed;
        this.hp = hp;
        this.game = game;
        hullHitBox = new HitBox(x, y, angleH, bmp_hull.getWidth(), bmp_hull.getHeight(), hullIndents);
        towerHitBox = new HitBox(x, y, angleH + angleT, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);
        nameWidth = (int) myPaint.measureText(playerName);
    }

    public void updateMyTankProperties() {
        double koeff = 1 / (double) (Game.MAX_FPS);

        double xChange = speed * game.getlJstrength() / 100 * Math.cos(Math.toRadians(game.getlJangle())) * koeff;
        double yChange = speed * game.getlJstrength() / 100 * Math.sin(Math.toRadians(game.getlJangle())) * koeff;
        double hAngleChange = game.getlJangle();
        double tAngleChange = game.getrJangle();

        if (game.getlJstrength() == 0) {
            xChange = 0;
            yChange = 0;
            hAngleChange = 90 - angleH;
        }

        if (game.getrJstrength() == 0) {
            tAngleChange = 90 - (90 - hAngleChange + angleT);
        }

        HashSet<HitBox> hitBoxes = game.getHitBoxes();

        updatedHhb = new HitBox(x + xChange, y - yChange, 90 - hAngleChange, bmp_hull.getWidth(), bmp_hull.getHeight(), updatedHullIndents);
        updatedThb = new HitBox(x + xChange, y - yChange, 90 - tAngleChange, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);

        boolean isHullMoveAble = true;
        boolean isTowerMoveAble = true;

        for (HitBox hb : hitBoxes) {
            if(HitBox.isHitBoxesIntersect(hb, updatedHhb)) {
                isHullMoveAble = false;
                break;
            }
        }

        for (HitBox hb : hitBoxes) {
            if(HitBox.isHitBoxesIntersect(hb, updatedThb)) {
                isTowerMoveAble = false;
                isHullMoveAble = false;
                break;
            }
        }

        if(isHullMoveAble) {
            x += xChange;
            y -= yChange;
            if(game.getlJstrength() > 0) {
                angleH = -game.getlJangle() + 90;
                current_speed = speed * game.getlJstrength() / 100;
            }
            else
                current_speed = 0;
        }

        if(isTowerMoveAble && game.getrJstrength() > 0)
            angleT = -game.getrJangle() + 90 - angleH;

        if (game.getrJstrength() > 0)
            tankSight.setSighting(true);
        else {
            if (tankSight.isSighting())
                createBullet();
            tankSight.setSighting(false);
        }

        hullHitBox.updateProperties(x, y, angleH);
        towerHitBox.updateProperties(x, y, angleH + angleT);

        updateBulletsCoordinates(koeff);
    }

    private void updateBulletsCoordinates(double koeff) {
        Bullet[] arr_bullets = new Bullet[bullets.size()];
        bullets.toArray(arr_bullets);
        for (Bullet b : arr_bullets) {
            b.setX(b.getX() + b.getSpeed() * Math.cos(Math.toRadians(90 - b.getAngle())) * koeff);
            b.setY(b.getY() - b.getSpeed() * Math.sin(Math.toRadians(90 - b.getAngle())) * koeff);
            if (b.getX() > game.getWidth() + 50 || b.getX() < -50 || b.getY() > game.getHeight() + 50 || b.getY() < -50 || b.getRicochets() == 0)
                bullets.remove(b);
        }
    }

    private void createBullet() {
        bullets.add(new Bullet(
                (int) (x + bmp_tower.getWidth() / 2 * Math.cos(Math.toRadians(90 - (angleH + angleT)))),
                (int) (y - bmp_tower.getHeight() / 2 * Math.sin(Math.toRadians(90 - (angleH + angleT)))),
                angleH + angleT));
    }


    public void draw(Canvas canvas, Paint paint, Bitmap bmp_bullets) {
        super.draw(canvas, paint, bmp_hull, bmp_tower, bmp_bullets);
    }

    public int getBulletsSize() {
        return bullets.size();
    }

    public HitBox getUpdatedHhb() {
        return updatedHhb;
    }

    public HitBox getUpdatedThb() {
        return updatedThb;
    }
}
