package com.example.presamsungproject.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.presamsungproject.Game;

public class MyTank extends Tank {
    private double speed, current_speed;
    private Bitmap hull, tower;
    private Game game;

    {
        myPaint.setColor(Color.GREEN);
    }

    public MyTank(double hp, double x, double y, double angleH, double angleT, String playerName,
                  long id, double speed, Bitmap hull, Bitmap tower, Game game) {
        super(hp, x, y, angleH, angleT, playerName, id);
        this.speed = speed;
        this.hull = hull;
        this.tower = tower;
        this.game = game;
        nameWidth = (int) myPaint.measureText(playerName);
    }

    public void updateMyTankProperties() {
        double koeff = 1 / (double) (Game.MAX_FPS);

        if (game.getlJstrength() > 0)
            angleH = -game.getlJangle() + 90;
        current_speed = speed * game.getlJstrength() / 100;

        if (game.getrJstrength() > 0)
            angleT = -game.getrJangle() + 90 - angleH;

        x += current_speed * Math.cos(Math.toRadians(90 - angleH)) * koeff;
        y -= current_speed * Math.sin(Math.toRadians(90 - angleH)) * koeff;

        if (game.getrJstrength() > 0)
            tankSight.setSighting(true);
        else {
            if (tankSight.isSighting())
                createBullet();
            tankSight.setSighting(false);
        }

        updateBulletsCoordinates(koeff);
    }

    private void updateBulletsCoordinates(double koeff) {
        Bullet[] arr_bullets = new Bullet[game.getBullets().size()];
        game.getBullets().toArray(arr_bullets);
        for (Bullet b : arr_bullets) {
            b.setX(b.getX() + b.getSpeed() * Math.cos(Math.toRadians(90 - b.getAngle())) * koeff);
            b.setY(b.getY() - b.getSpeed() * Math.sin(Math.toRadians(90 - b.getAngle())) * koeff);
            if(b.getX() > game.getWidth() + 50 || b.getX() < -50 || b.getY() > game.getHeight() + 50 || b.getY() < -50 || b.getRicochets() == 0)
                game.getBullets().remove(b);
        }
    }

    private void createBullet() {
        game.getBullets().add(new Bullet(
                (int) (x + tower.getWidth() / 2 * Math.cos(Math.toRadians(90 - (angleH + angleT)))),
                (int) (y - tower.getHeight() / 2 * Math.sin(Math.toRadians(90 - (angleH + angleT)))),
                angleH + angleT));
    }


    public void draw(Canvas canvas, Paint paint){
        super.draw(canvas, paint, hull, tower);
    }
}
