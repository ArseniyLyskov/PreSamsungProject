package com.example.presamsungproject;

import android.graphics.*;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {
    private final SurfaceHolder surfaceHolder;
    private final Game game;

    public volatile boolean isTimeToUpdate = false;
    private volatile boolean running = false;

    public DrawThread(SurfaceHolder surfaceHolder, Game game) {
        this.surfaceHolder = surfaceHolder;
        this.game = game;
        Canvas canvas = surfaceHolder.lockCanvas();
        game.setFrameWidth(canvas.getWidth());
        game.setFrameHeight(canvas.getHeight());
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    public void setRunning(boolean run) {
        running = run;
    }

    private final Paint backgroundPaint = new Paint();

    {
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void run() {
        while (running) {
            if (isTimeToUpdate) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    try {
                        //
                        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);

                        game.drawAll(canvas);
                        game.setFps(game.getFps() + 1);

                        //
                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        isTimeToUpdate = false;
                    }
                }
            }
        }
    }
}