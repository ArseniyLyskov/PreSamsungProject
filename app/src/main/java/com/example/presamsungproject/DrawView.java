package com.example.presamsungproject;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private Game game;
    private static final int MAX_FPS = 30;

    public void updateMyTankCoordinates() {
        double koeff = 1 / (double) (MAX_FPS);

        if (game.lJstrength > 0)
            game.myTank.angleH = -game.lJangle + 90;
        game.myTank.current_speed = game.myTank.speed * game.lJstrength / 100;

        if (game.rJstrength > 0)
            game.myTank.angleT = -game.rJangle + 90 - game.myTank.angleH;

        game.myTank.x += game.myTank.current_speed * Math.cos(Math.toRadians(90 - game.myTank.angleH)) * koeff;
        game.myTank.y -= game.myTank.current_speed * Math.sin(Math.toRadians(90 - game.myTank.angleH)) * koeff;

    }

    class ThreadTimer extends CountDownTimer {

        public ThreadTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            updateMyTankCoordinates();
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
            game.fps_tv.setText("FPS:" + game.fps);
            game.fps = 0;
        }

        @Override
        public void onFinish() {

        }
    }

    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public DrawView(Context context, Game game) {
        super(context);
        getHolder().addCallback(this);
        this.game = game;
    }

    /*public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    } // конструктор для обращения activity_main к DrawView*/

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
