package com.example.presamsungproject;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private Game game;

    class ThreadTimer extends CountDownTimer {

        public ThreadTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            game.myTank.updateMyTankProperties();
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
        ThreadTimer threadTimer = new ThreadTimer(Integer.MAX_VALUE, (int) (1000 / Game.MAX_FPS));
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
