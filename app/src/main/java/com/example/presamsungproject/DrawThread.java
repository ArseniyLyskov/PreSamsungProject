package com.example.presamsungproject;

import android.content.Context;
import android.graphics.*;
import android.view.SurfaceHolder;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class DrawThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private int delay = 0;
    private Game game;

    private volatile boolean running = false; //флаг для остановки потока

    public DrawThread(SurfaceHolder surfaceHolder, Game game) {
        this.surfaceHolder = surfaceHolder;
        this.game = game;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    private Paint backgroundPaint = new Paint();

    {
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    public void requestStop() {
        running = false;
    }

    public void updateMyTankCoordinates() {
        if (game.lJstrength > 0)
            game.myTank.angleH = -game.lJangle + 90;
        game.myTank.current_speed = game.myTank.speed * game.lJstrength / 100;

        if (game.rJstrength > 0)
            game.myTank.angleT = -game.rJangle + 90 - game.myTank.angleH;

        game.myTank.x += game.myTank.current_speed * Math.cos(Math.toRadians(90 - game.myTank.angleH));
        game.myTank.y -= game.myTank.current_speed * Math.sin(Math.toRadians(90 - game.myTank.angleH));

    }

    @Override
    public void run() {
        while (running) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
                    //
                    canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);

                    game.drawAll(canvas, backgroundPaint);

                    updateMyTankCoordinates();

                    Thread.sleep(delay);
                    //
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
