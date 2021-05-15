package com.example.presamsungproject.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.presamsungproject.Game;

public class TankSight {
    private boolean isSighting;
    private final Paint paint;
    private Game game;

    {
        paint = new Paint();
        isSighting = false;
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
    }

    public TankSight(Game game) {
        this.game = game;
    }

    public TankSight() {

    }

    public void draw(Canvas canvas, double x1, double y1, double angle) {
        int length = canvas.getWidth() * 2;
        double x2 = x1 + length * Math.cos(Math.toRadians(90 - angle));
        double y2 = y1 - length * Math.sin(Math.toRadians(90 - angle));
        for (length = 0; length < canvas.getWidth() * 2; length += 10) {
            for (HitBox hb : game.getHitBoxes()) {
                int[] temp = HitBox.sightHitBoxIntersection(new int[]{(int) x1, (int) x2}, new int[]{(int) y1, (int) y2}, hb);
                if(temp[0] != -1) {
                    x2 = temp[0];
                    y2 = temp[1];
                }
            }
        }
        canvas.drawLine((int) x1, (int) y1, (int) x2, (int) y2, paint);
    }

    public void setSighting(boolean isSighting) {
        this.isSighting = isSighting;
    }

    public boolean isSighting() {
        return isSighting;
    }
}
