package com.example.presamsungproject.Activities.Game;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Models.Game;
import com.example.presamsungproject.Models.Resources;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private final Game game;

    class ThreadTimer extends CountDownTimer {

        public ThreadTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
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
            Resources.getInstance().getGUIUListener().showFPS(game.updateFPS());
        }

        @Override
        public void onFinish() {

        }
    }

    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);
        game = MessageManager.getCurrentGame();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(holder, game);
        drawThread.setRunning(true);
        drawThread.start();
        ThreadTimer threadTimer = new ThreadTimer(Integer.MAX_VALUE, (int) (1000f / Game.MAX_FPS));
        threadTimer.start();
        SecTimer secTimer = new SecTimer(Integer.MAX_VALUE, 1000);
        secTimer.start();
        Log.d("MyTag", "Drawing started");
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
