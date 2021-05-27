package com.example.presamsungproject.GameObjects;

import android.graphics.*;
import com.example.presamsungproject.HitBox;

import java.util.HashSet;

public class Tank {
    final long id;
    double hp;
    double x, y;
    double angleH, angleT;
    String playerName;
    TankSight tankSight;
    Bitmap bmp_hull, bmp_tower;
    Paint myPaint;
    int nameWidth;
    public HashSet<Bullet> bullets;
    public HitBox hullHitBox, towerHitBox;
    double[] hullIndents, towerIndents;

    {
        hullIndents = new double[4];
        hullIndents[0] = 18 / 50f;
        hullIndents[1] = 15 / 50f;
        hullIndents[2] = 19 / 50f;
        hullIndents[3] = 15 / 50f;
        towerIndents = new double[4];
        towerIndents[0] = 25 / 50f;
        towerIndents[1] = 3 / 50f;
        towerIndents[2] = 1 / 50f;
        towerIndents[3] = 4 / 50f;
    }

    public void draw(Canvas canvas, Paint paint, Bitmap bmp_hull, Bitmap bmp_tower, Bitmap bmp_bullet) {
        double hWidth = bmp_hull.getWidth();
        double hHeight = bmp_hull.getHeight();
        double tWidth = bmp_tower.getWidth();
        double tHeight = bmp_tower.getHeight();
        /*
        // Иной способ отрисовки танка
        //canvas.save();
        canvas.rotate((float) angleH, (float) (x + hWidth / 2),
                (float) (y + hHeight / 2));
        canvas.drawBitmap(hull, (int) x, (int) y, paint);
        canvas.rotate((float) -angleH, (float) (x + hWidth / 2),
                (float) (y + hHeight / 2));
        //canvas.restore();


        //canvas.save();
        canvas.rotate((float) (angleH + angleT), (float) (x + tWidth / 2),
                (float) (y + tHeight / 2));
        if(tankSight.isSighting)
            tankSight.draw(canvas, (int) (x + tWidth / 2), (int) (y + tHeight / 2));
        canvas.drawBitmap(tower, (int) x, (int) y, paint);
        canvas.rotate((float) -(angleH + angleT), (float) (x + tWidth / 2),
                (float) (y + tHeight / 2));
        //canvas.restore();
        */

        canvas.save();
        canvas.rotate((float) angleH, (float) (x + hWidth / 2), (float) (y + hHeight / 2));
        canvas.drawBitmap(bmp_hull, (int) x, (int) y, paint);
        canvas.rotate((float) angleT, (float) (x + tWidth / 2), (float) (y + tHeight / 2));
        canvas.drawBitmap(bmp_tower, (int) x, (int) y, paint);
        canvas.restore();

        if(tankSight.isSighting())
            tankSight.draw(canvas);

        canvas.drawText(playerName, (int) (x + hWidth / 2 - nameWidth / 2), (int) (y - 10), myPaint);

        Bullet[] arr_bullets = new Bullet[bullets.size()];
        bullets.toArray(arr_bullets);
        for (Bullet b : arr_bullets) {
            canvas.drawBitmap(bmp_bullet, (int) (b.getX() - bmp_bullet.getWidth() / 2), (int) (b.getY() - bmp_bullet.getHeight() / 2), paint);
            //canvas.drawRect((int) b.getX() - 5, (int) b.getY() - 5, (int) b.getX() + 5, (int) b.getY() + 5, paint);
        }
    }

    public Tank(long id, double hp, double x, double y, double angleH, double angleT, String playerName, TankSight tankSight,
                Bitmap bmp_hull, Bitmap bmp_tower, Paint myPaint,
                HashSet<Bullet> bullets) {

        this.hp = hp;
        this.x = x;
        this.y = y;
        this.angleH = angleH;
        this.angleT = angleT;
        this.playerName = playerName;
        this.tankSight = tankSight;
        this.bmp_hull = bmp_hull;
        this.bmp_tower = bmp_tower;
        this.myPaint = myPaint;
        this.bullets = bullets;
        this.id = id;
        hullHitBox = new HitBox(x, y, angleH, bmp_hull.getWidth(), bmp_hull.getHeight(), hullIndents);
        towerHitBox = new HitBox(x, y, angleH + angleT, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);
        nameWidth = (int) myPaint.measureText(playerName);
    }

    public HitBox getHullHitBox() {
        return hullHitBox;
    }

    public HitBox getTowerHitBox() {
        return towerHitBox;
    }

    public HashSet<Bullet> getBullets() {
        return bullets;
    }
}
