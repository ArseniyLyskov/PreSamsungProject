package com.example.presamsungproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class MyTank extends Tank{
    double speed, current_speed;
    Bitmap hull, tower;
    Game game;

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

        if (game.lJstrength > 0)
            angleH = -game.lJangle + 90;
        current_speed = speed * game.lJstrength / 100;

        if (game.rJstrength > 0)
            angleT = -game.rJangle + 90 - angleH;

        x += current_speed * Math.cos(Math.toRadians(90 - angleH)) * koeff;
        y -= current_speed * Math.sin(Math.toRadians(90 - angleH)) * koeff;

        if (game.rJstrength > 0)
            tankSight.isSighting = true;
        else {
            if (tankSight.isSighting)
                createBullet();
            tankSight.isSighting = false;
        }

        updateBulletsCoordinates(koeff);
    }

    private void updateBulletsCoordinates(double koeff) {
        Bullet[] arr_bullets = new Bullet[game.bullets.size()];
        game.bullets.toArray(arr_bullets);
        for (Bullet b : arr_bullets) {
            b.x += b.speed * Math.cos(Math.toRadians(90 - b.angle)) * koeff;
            b.y -= b.speed * Math.sin(Math.toRadians(90 - b.angle)) * koeff;
            if(b.x > game.width + 50 || b.x < -50 || b.y > game.height + 50 || b.y < -50 || b.ricochets == 0)
                game.bullets.remove(b);
        }
    }

    private void createBullet() {
        game.bullets.add(new Bullet(
                (int) (x + tower.getWidth() / 2 * Math.cos(Math.toRadians(90 - (angleH + angleT)))),
                (int) (y - tower.getHeight() / 2 * Math.sin(Math.toRadians(90 - (angleH + angleT)))),
                angleH + angleT));
    }


    public void draw(Canvas canvas, Paint paint){
        super.draw(canvas, paint, hull, tower);
    }
}
