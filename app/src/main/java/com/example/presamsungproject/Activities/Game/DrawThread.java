package com.example.presamsungproject.Activities.Game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import com.example.presamsungproject.Models.Game;

public class DrawThread extends Thread {
    private final SurfaceHolder surfaceHolder;
    private final Game game;

    public volatile boolean isTimeToUpdate = false;
    private volatile boolean running = false;

    private final Paint backgroundPaint = new Paint();

    {
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    public DrawThread(SurfaceHolder surfaceHolder, Game game) {
        this.surfaceHolder = surfaceHolder;
        this.game = game;
        Canvas canvas = surfaceHolder.lockCanvas();
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    public void setRunning(boolean run) {
        running = run;
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
                        game.increaseFPS();

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