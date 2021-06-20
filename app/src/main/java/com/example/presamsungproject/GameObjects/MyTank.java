package com.example.presamsungproject.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Game;
import com.example.presamsungproject.HitBox;

import java.util.Arrays;
import java.util.HashSet;

public class MyTank extends Tank {
    private double speed, current_speed;
    private Game game;
    private HitBox updatedHhb, updatedThb;
    private Bitmap bmp_hull, bmp_tower;
    private double[] updatedHullIndents;
    private HashSet<HitBox> hitBoxes;

    {
        updatedHullIndents = Arrays.copyOf(hullIndents, hullIndents.length);
        updatedHullIndents[2] = 13 / 50f;
        updatedHhb = new HitBox(x, y, 0, 1, 1, hullIndents);
        updatedThb = new HitBox(x, y, 0, 1, 1, towerIndents);
    }

    public MyTank(double hp, int team, double x, double y, double angleH, double angleT, String playerName,
                  TankSight tankSight, HashSet<Bullet> bullets, Bitmap bmp_hull, Bitmap bmp_tower, double speed, Game game) {

        super(hp, team, x, y, angleH, angleT, playerName, tankSight, bullets);
        this.speed = speed;
        this.game = game;
        this.bmp_hull = bmp_hull;
        this.bmp_tower = bmp_tower;
        hullHitBox = new HitBox(x, y, angleH, bmp_hull.getWidth(), bmp_hull.getHeight(), hullIndents);
        towerHitBox = new HitBox(x, y, angleH + angleT, bmp_tower.getWidth(), bmp_tower.getHeight(), towerIndents);
        scale = game.getScale();
    }

    public Tank getTankToSerialize() {
        return new Tank(hp, team, x, y, angleH, angleT, playerName, tankSight, bullets, hullHitBox, towerHitBox, MessageManager.EXTERNAL_ADDRESS, scale);
    }

    public void updateMyTankProperties() {
        double speed_koeff = 1 / (double) (Game.MAX_FPS);

        double oldX = x;
        double oldY = y;
        double oldAngleT = angleT;
        double oldAngleH = angleH;
        boolean oldSighting = tankSight.isSighting();
        double oldHP = hp;

        if (bmp_hull == null || bmp_tower == null)
            return;

        if (hp <= 0) {
            if (tankSight.isSighting())
                tankSight.setSighting(false);
            updateBulletsCoordinates(speed_koeff);
            return;
        }

        double xChange = speed * game.getlJstrength() / 100 * Math.cos(Math.toRadians(game.getlJangle())) * speed_koeff;
        double yChange = speed * game.getlJstrength() / 100 * Math.sin(Math.toRadians(game.getlJangle())) * speed_koeff;
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
            if (hb == null)
                return;
        }

        for (HitBox hb : hitBoxes) {
            if (HitBox.isHitBoxesIntersect(hb, updatedHhb)) {
                isHullMoveAble = false;
                break;
            }
        }

        for (HitBox hb : hitBoxes) {
            if (HitBox.isHitBoxesIntersect(hb, updatedThb)) {
                isTowerMoveAble = false;
                isHullMoveAble = false;
                break;
            }
        }

        if (!isHullMoveAble) {
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
        if (!isHullMoveAble) {
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

        if (isHullMoveAble) {
            x += xChange;
            y -= yChange;
            if (game.getlJstrength() > 0) {
                angleH = -game.getlJangle() + 90;
                current_speed = speed * game.getlJstrength() / 100;
            } else
                current_speed = 0;
        } else if (isXHullMoveAble) {
            x += xChange;
            if (game.getlJstrength() > 0) {
                angleH = -game.getlJangle() + 90;
                current_speed = speed * game.getlJstrength() / 100;
            } else
                current_speed = 0;
        } else if (isYHullMoveAble) {
            y -= yChange;
            if (game.getlJstrength() > 0) {
                angleH = -game.getlJangle() + 90;
                current_speed = speed * game.getlJstrength() / 100;
            } else
                current_speed = 0;
        }

        if ((isTowerMoveAble || isXTowerMoveAble || isYTowerMoveAble) && game.getrJstrength() > 0)
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

        updateBulletsCoordinates(speed_koeff);

        if (oldX != x || oldY != y || oldHP != hp || oldAngleT != angleT
                || oldAngleH != angleH || oldSighting != tankSight.isSighting() || bullets.size() > 0) {
            if (game.isEverybodyReady) {
                MessageManager.sendMyTank();
            }
        }
    }

    private void updateBulletsCoordinates(double speed_koeff) {
        Bullet[] arr_bullets = new Bullet[bullets.size()];
        bullets.toArray(arr_bullets);
        for (Bullet b : arr_bullets) {
            double newX = b.getX() + b.getSpeed() * Math.cos(Math.toRadians(90 - b.getAngle())) * speed_koeff;
            double newY = b.getY() - b.getSpeed() * Math.sin(Math.toRadians(90 - b.getAngle())) * speed_koeff;
            boolean changeCoordinates = true;
            for (HitBox hb : game.getWallsHitBoxes()) {
                if (HitBox.isPointInSquareHitBox((int) newX, (int) newY, hb)) {
                    if (HitBox.isHorizontalWallIntersection((int) b.getX(), (int) b.getY(), (int) newX, (int) newY, hb))
                        b.setAngle(180 - b.getAngle());
                    else
                        b.setAngle(-b.getAngle());
                    b.setRicochets(b.getRicochets() - 1);
                    changeCoordinates = false;
                    break;
                }
            }
            if (changeCoordinates) {
                b.setX(newX);
                b.setY(newY);
            }
            //Log.d("MyTag", "" + b.getRicochets());
            if (b.getRicochets() < 0) {
                bullets.remove(b);
                MessageManager.sendMyTank();
            }


            for (Tank tank : game.otherTanks.values()) {
                if (HitBox.isPointInHitBox((int) b.getX(), (int) b.getY(), tank.hullHitBox)
                        || HitBox.isPointInHitBox((int) b.getX(), (int) b.getY(), tank.towerHitBox)) {
                    if (game.isLobby) {
                        Server.specificMessage(tank.address, MessageManager.hitMessage(tank.address));
                    } else {
                        Client.sendMessage(MessageManager.hitMessage(tank.address));
                    }
                    bullets.remove(b);
                    MessageManager.sendMyTank();
                }
                if (HitBox.isPointInHitBox((int) b.getX(), (int) b.getY(), hullHitBox)
                        || HitBox.isPointInHitBox((int) b.getX(), (int) b.getY(), towerHitBox)) {
                    hp--;
                    bullets.remove(b);
                    MessageManager.sendMyTank();
                }
            }

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


    public void draw(Canvas canvas, Paint paint, Bitmap bmp_hull, Bitmap bmp_tower, Bitmap bmp_bullets) {
        this.bmp_hull = bmp_hull;
        this.bmp_tower = bmp_tower;

        super.draw(canvas, paint, bmp_hull, bmp_tower, bmp_bullets);
        if (tankSight.isSighting())
            tankSight.update(x + bmp_tower.getWidth() / 2f + bmp_tower.getWidth() * towerIndents[0] * Math.cos(Math.toRadians(90 - angleH - angleT)),
                    y + bmp_tower.getHeight() / 2f - bmp_tower.getHeight() * towerIndents[0] * Math.sin(Math.toRadians(90 - angleH - angleT)), angleH + angleT, hitBoxes);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
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