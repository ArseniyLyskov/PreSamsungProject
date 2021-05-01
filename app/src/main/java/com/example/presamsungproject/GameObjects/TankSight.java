package com.example.presamsungproject.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class TankSight {
    private boolean isSighting;
    private final Paint paint;

    {
        paint = new Paint();
        isSighting = false;
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
    }

    public void draw(Canvas canvas, int x1, int y1) {
        canvas.drawLine(x1, y1, x1, y1 - canvas.getHeight() * 2, paint);
    }

    public void setSighting(boolean isSighting) {
        this.isSighting = isSighting;
    }

    public boolean isSighting() {
        return isSighting;
    }
}
