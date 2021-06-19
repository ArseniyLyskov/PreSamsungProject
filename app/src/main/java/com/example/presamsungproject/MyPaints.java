package com.example.presamsungproject;

import android.graphics.Color;
import android.graphics.Paint;

public class MyPaints {
    private static Paint allyNickPaint;
    private static Paint enemyNickPaint;
    private static Paint hitboxPaint;
    private static Paint tanksightPaint;

    public static void paintsInit() {
        hitboxPaint = new Paint();
        hitboxPaint.setColor(Color.CYAN);
        hitboxPaint.setStrokeWidth(3);
        hitboxPaint.setStyle(Paint.Style.STROKE);

        tanksightPaint = new Paint();
        tanksightPaint.setColor(Color.RED);
        tanksightPaint.setStrokeWidth(3);

        enemyNickPaint = new Paint();
        enemyNickPaint.setTextSize(30);
        enemyNickPaint.setColor(Color.RED);

        allyNickPaint = new Paint();
        allyNickPaint.setTextSize(30);
        allyNickPaint.setColor(Color.GREEN);
    }

    public static Paint getHitBoxPaint() {
        return hitboxPaint;
    }

    public static Paint getTankSightPaint() {
        return tanksightPaint;
    }

    public static Paint getAllyNickPaint() {
        return allyNickPaint;
    }

    public static Paint getEnemyNickPaint() {
        return enemyNickPaint;
    }
}
