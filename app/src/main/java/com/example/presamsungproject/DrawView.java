package com.example.presamsungproject;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private Game game;
    public static final int MAX_FPS = 50;

    public void updateGameProperties() {
        double koeff = 1 / (double) (MAX_FPS);

        if (game.lJstrength > 0)
            game.myTank.angleH = -game.lJangle + 90;
        game.myTank.current_speed = game.myTank.speed * game.lJstrength / 100;

        if (game.rJstrength > 0)
            game.myTank.angleT = -game.rJangle + 90 - game.myTank.angleH;

        game.myTank.x += game.myTank.current_speed * Math.cos(Math.toRadians(90 - game.myTank.angleH)) * koeff;
        game.myTank.y -= game.myTank.current_speed * Math.sin(Math.toRadians(90 - game.myTank.angleH)) * koeff;

        if (game.rJstrength > 0)
            game.myTank.tankSight.isSighting = true;
        else {
            if (game.myTank.tankSight.isSighting)
                createBullet();
            game.myTank.tankSight.isSighting = false;
        }

        Bullet[] arr_bullets = new Bullet[game.bullets.size()];
        game.bullets.toArray(arr_bullets);
        for (Bullet b : arr_bullets) {
            b.x += b.speed * Math.cos(Math.toRadians(90 - b.angle)) * koeff;
            b.y -= b.speed * Math.sin(Math.toRadians(90 - b.angle)) * koeff;
            if(b.x > game.width + 50 || b.x < -50 || b.y > game.height + 50 || b.y < -50 || b.ricochets == 0)
                game.bullets.remove(b);
        }

    }

    public void createBullet() {
        game.bullets.add(new Bullet((int) (game.myTank.x + game.myTank.tower.getWidth() / 2 * Math.cos(Math.toRadians(90 - (game.myTank.angleH + game.myTank.angleT)))),
                (int) (game.myTank.y - game.myTank.tower.getHeight() / 2 * Math.sin(Math.toRadians(90 - (game.myTank.angleH + game.myTank.angleT)))),
                game.myTank.angleH + game.myTank.angleT));
    }

    class ThreadTimer extends CountDownTimer {

        public ThreadTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            updateGameProperties();
            drawThread.isTimeToUpdate = true;
        }

        @Override
        public void onFinish() {

        }
    }

    class SecTimer extends CountDownTimer {

        public SecTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            game.fps_tv.setText("FPS: " + game.fps + "\nBullets: "+ game.bullets.size());
            game.fps = 0;
        }

        @Override
        public void onFinish() {

        }
    }

    public DrawView(Context context, Game game) {
        super(context);
        getHolder().addCallback(this);
        this.game = game;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(holder, game);
        drawThread.setRunning(true);
        drawThread.start();
        ThreadTimer threadTimer = new ThreadTimer(Integer.MAX_VALUE, (int) (1000 / MAX_FPS));
        threadTimer.start();
        SecTimer secTimer = new SecTimer(Integer.MAX_VALUE, 1000);
        secTimer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //
            }
        }
    }
}
