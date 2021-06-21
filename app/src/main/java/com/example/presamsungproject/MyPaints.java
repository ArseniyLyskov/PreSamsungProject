package com.example.presamsungproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

public class MyPaints {
    private static Paint allyNickPaint;
    private static Paint enemyNickPaint;
    private static Paint hitboxPaint;
    private static Paint tanksightPaint;
    private static Paint defaultPaint;
    private static Bitmap paintedWallPaper;

    public static void paintsInit(Context context) {
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

        defaultPaint = new Paint();
        //TODO: вот здесь ты передаёшь контекст в функцию, которая делает с ним операции
        // и возможно записывает данные, связанные с конекстом в объект
        // Тут может быть утечка памяти.
        // Рекомендую подумать о том, чтобы заменить этот метод конструктором и передавать как объект
        paintedWallPaper = Map.getWallPaperMap(context);
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

    public static Paint getDefaultPaint() {
        return defaultPaint;
    }

    public static Bitmap getPaintedWallPaper() {
        return paintedWallPaper;
    }
}
