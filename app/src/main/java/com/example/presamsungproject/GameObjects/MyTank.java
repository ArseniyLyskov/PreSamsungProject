package com.example.presamsungproject.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import com.example.presamsungproject.Game;
import com.example.presamsungproject.HitBox;

import java.util.Arrays;
import java.util.HashSet;

public class MyTank extends Tank {
    private double speed, current_speed;
    private Game game;
    private HitBox updatedHhb, updatedThb;
    private double[] updatedHullIndents;
    private HashSet<HitBox> hitBoxes;
    private Bitmap bmp_Dhull, bmp_Dtower;

    {
        updatedHullIndents = Arrays.copyOf(hullIndents, hullIndents.length);
        updatedHullIndents[2] = 13 / 50f;
        updatedHhb = new HitBox(x, y, 0, 1, 1, hullIndents);
        updatedThb = new HitBox(x, y, 0, 1, 1, towerIndents);
    }

    public MyTank(double hp, double x, double y, double angleH, double angleT, String playerName,
                  TankSight tankSight, Bitmap bmp_hull, Bitmap bmp_tower, Paint myPaint,
                  HashSet<Bullet> bullets, long id,
                  double speed, Bitmap bmp_Dhull, Bitmap bmp_Dtower, Game game) {

        super(hp, x, y, angleH, angleT, playerName, tankSight, bmp_hull, bmp_tower, myPaint, bullets, id);
        this.speed = speed;
        this.game = game;
        this.bmp_Dhull = bmp_Dhull;
        this.bmp_Dtower = bmp_Dtower;
        hullHitBox = new HitBox(x, y, angleH, bmp_hull.getWidth(), bmp_hull.getHeight(), hullIndents);
        towerHitBox = new HitBox(x, y, angleH + angleT, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);
        nameWidth = (int) myPaint.measureText(playerName);
    }

    public void updateMyTankProperties() {
        double koeff = 1 / (double) (Game.MAX_FPS);

        if(hp <= 0) {
            if(tankSight.isSighting())
                tankSight.setSighting(false);
            updateBulletsCoordinates(koeff);
            return;
        }

        for (Bullet b :
                game.getBullets()) {
            if (HitBox.isPointInHitBox((int) b.getX(), (int) b.getY(), hullHitBox)
                    || HitBox.isPointInHitBox((int) b.getX(), (int) b.getY(), towerHitBox)) {
                hp--;
            }
        }

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

        hitBoxes = game.getAllHitBoxes();

        updatedHhb = new HitBox(x + xChange, y - yChange, 90 - hAngleChange, bmp_hull.getWidth(), bmp_hull.getHeight(), updatedHullIndents);
        updatedThb = new HitBox(x + xChange, y - yChange, 90 - tAngleChange, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);

        HitBox updatedXHhb = new HitBox(x + xChange, y, 90 - hAngleChange, bmp_hull.getWidth(), bmp_hull.getHeight(), updatedHullIndents);
        HitBox updatedXThb = new HitBox(x + xChange, y, 90 - tAngleChange, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);
        HitBox updatedYHhb = new HitBox(x, y - yChange, 90 - hAngleChange, bmp_hull.getWidth(), bmp_hull.getHeight(), updatedHullIndents);
        HitBox updatedYThb = new HitBox(x, y - yChange, 90 - tAngleChange, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);

        boolean isHullMoveAble = true;
        boolean isTowerMoveAble = true;

        boolean isXHullMoveAble = true;
        boolean isXTowerMoveAble = true;
        boolean isYHullMoveAble = true;
        boolean isYTowerMoveAble = true;

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

        if(!isHullMoveAble) {
            for (HitBox hb : hitBoxes) {
                if (HitBox.isHitBoxesIntersect(hb, updatedXHhb)) {
                    isXHullMoveAble = false;
                    break;
                }
            }
            for (HitBox hb : hitBoxes) {
                if (HitBox.isHitBoxesIntersect(hb, updatedYHhb)) {
                    isYHullMoveAble = false;
                    break;
                }
            }
        }
        if(!isHullMoveAble) {
            for (HitBox hb : hitBoxes) {
                if (HitBox.isHitBoxesIntersect(hb, updatedXThb)) {
                    isXTowerMoveAble = false;
                    isXHullMoveAble = false;
                    break;
                }
            }
            for (HitBox hb : hitBoxes) {
                if (HitBox.isHitBoxesIntersect(hb, updatedYThb)) {
                    isYTowerMoveAble = false;
                    isYHullMoveAble = false;
                    break;
                }
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
        else if(isXHullMoveAble) {
            x += xChange;
            if(game.getlJstrength() > 0) {
                angleH = -game.getlJangle() + 90;
                current_speed = speed * game.getlJstrength() / 100;
            }
            else
                current_speed = 0;
        }
        else if(isYHullMoveAble) {
            y -= yChange;
            if(game.getlJstrength() > 0) {
                angleH = -game.getlJangle() + 90;
                current_speed = speed * game.getlJstrength() / 100;
            }
            else
                current_speed = 0;
        }

        if((isTowerMoveAble || isXTowerMoveAble || isYTowerMoveAble) && game.getrJstrength() > 0)
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
            double newX = b.getX() + b.getSpeed() * Math.cos(Math.toRadians(90 - b.getAngle())) * koeff;
            double newY = b.getY() - b.getSpeed() * Math.sin(Math.toRadians(90 - b.getAngle())) * koeff;
            boolean changeCoordinates = true;
            for (HitBox hb : game.getWallsHitBoxes()) {
                if (HitBox.isPointInSquareHitBox((int) newX, (int) newY, hb)) {
                    if(HitBox.isHorizontalWallIntersection((int) b.getX(), (int) b.getY(), (int) newX, (int) newY, hb))
                        b.setAngle(180 - b.getAngle());
                    else
                        b.setAngle(- b.getAngle());
                    b.setRicochets(b.getRicochets() - 1);
                    changeCoordinates = false;
                    break;
                }
            }
            if(changeCoordinates) {
                b.setX(newX);
                b.setY(newY);
            }
            //Log.d("MyTag", "" + b.getRicochets());
            if (b.getRicochets() < 0)
                bullets.remove(b);

            for (HitBox hb : game.getTankHitBoxes()) {
                if (HitBox.isPointInHitBox((int) newX, (int) newY, hb)) {
                    b.setRicochets(-1);
                    break;
                }
            }
        }
    }

    private void createBullet() {
        bullets.add(new Bullet(
                (int) (x + bmp_tower.getWidth() / 2 + bmp_tower.getWidth() / 2 * Math.cos(Math.toRadians(90 - (angleH + angleT)))),
                (int) (y + bmp_tower.getHeight() / 2 - bmp_tower.getHeight() / 2 * Math.sin(Math.toRadians(90 - (angleH + angleT)))),
                angleH + angleT));
    }


    public void draw(Canvas canvas, Paint paint, Bitmap bmp_bullets) {
        if(hp > 0)
            super.draw(canvas, paint, bmp_hull, bmp_tower, bmp_bullets);
        else
            super.draw(canvas, paint, bmp_Dhull, bmp_Dtower, bmp_bullets);
        if(tankSight.isSighting())
            tankSight.update(x + bmp_tower.getWidth() / 2f + bmp_tower.getWidth() * towerIndents[0] * Math.cos(Math.toRadians(90 - angleH - angleT)),
                    y + bmp_tower.getHeight() / 2f - bmp_tower.getHeight() * towerIndents[0] * Math.sin(Math.toRadians(90 - angleH - angleT)), angleH + angleT, hitBoxes);
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
