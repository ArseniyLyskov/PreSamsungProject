package com.example.presamsungproject.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.example.presamsungproject.Models.HitBox;
import com.example.presamsungproject.Models.MySingletons;

import java.io.Serializable;
import java.util.HashSet;

public class Tank implements Serializable {
    int hp;
    int team;
    int scale;
    String address;
    double x, y;
    double angleH, angleT;
    String playerName;
    TankSight tankSight;
    HashSet<Bullet> bullets;
    HitBox hullHitBox, towerHitBox;
    double[] hullIndents, towerIndents;
    private static final long serialVersionUID = 2L;

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

        hullHitBox = new HitBox(0, 0, 0, 1, 1, hullIndents);
        towerHitBox = new HitBox(0, 0, 0, 1, 1, towerIndents);
    }

    public void draw(Canvas canvas, Paint paint, Bitmap bmp_hull, Bitmap bmp_tower) {
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

        if (tankSight.isSighting())
            tankSight.draw(canvas);

        canvas.drawText(playerName, (int) (x + hWidth / 2 - (int) paint.measureText(playerName) / 2), (int) (y - 10), paint);

        HashSet<Bullet> temp = new HashSet<>(bullets);
        Bitmap bmp_bullet = MySingletons.getMyResources().getBmp_bullet();
        for (Bullet b : temp) {
            canvas.drawBitmap(bmp_bullet,
                    (int) (b.getPoint().getX() - bmp_bullet.getWidth() / 2),
                    (int) (b.getPoint().getY() - bmp_bullet.getHeight() / 2), paint);
        }
    }

    public Tank(int hp, int team, double x, double y, double angleH, double angleT,
                String playerName, TankSight tankSight, HashSet<Bullet> bullets) {

        this.hp = hp;
        this.team = team;
        this.x = x;
        this.y = y;
        this.angleH = angleH;
        this.angleT = angleT;
        this.playerName = playerName;
        this.tankSight = tankSight;
        this.bullets = bullets;
    }

    public void scaleTo(double koeff) {
        this.x *= koeff;
        this.y *= koeff;
        tankSight.scaleTo(koeff);
        Bullet[] arr_bullets = new Bullet[bullets.size()];
        bullets.toArray(arr_bullets);
        for (Bullet b : arr_bullets) {
            b.scaleTo(koeff);
        }
        hullHitBox.scaleTo(koeff);
        towerHitBox.scaleTo(koeff);
    }

    public Tank(int hp, int team, double x, double y, double angleH, double angleT, String playerName,
                TankSight tankSight, HashSet<Bullet> bullets, HitBox hullHitBox, HitBox towerHitBox, String address, int scale) {

        this.hp = hp;
        this.team = team;
        this.x = x;
        this.y = y;
        this.angleH = angleH;
        this.angleT = angleT;
        this.playerName = playerName;
        this.tankSight = tankSight;
        this.bullets = bullets;
        this.hullHitBox = hullHitBox;
        this.towerHitBox = towerHitBox;
        this.address = address;
        this.scale = scale;
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngleH() {
        return angleH;
    }

    public double getAngleT() {
        return angleT;
    }

    public double getScale() {
        return scale;
    }

    public double getHp() {
        return hp;
    }

    public int getTeam() {
        return team;
    }
}