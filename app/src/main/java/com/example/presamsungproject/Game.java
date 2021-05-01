package com.example.presamsungproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.TextView;
import com.example.presamsungproject.GameObjects.Bullet;
import com.example.presamsungproject.GameObjects.MyTank;
import com.example.presamsungproject.GameObjects.Tank;

import java.util.HashSet;

public class Game {
    private Bitmap bmp_greenHp, bmp_greenTp;
    private Bitmap bmp_redHp, bmp_redTp;
    private Bitmap bmp_blueHp, bmp_blueTp;
    private Bitmap bmp_bullet;
    public static final int MAX_FPS = 35;

    private int width, height;
    private MyTank myTank;
    private HashSet<Tank> enemyTanks = new HashSet<>();
    private HashSet<Tank> allyTanks = new HashSet<>();
    private HashSet<Bullet> bullets = new HashSet<>();
    private double lJangle, lJstrength, rJangle, rJstrength;
    private int fps;
    private TextView fps_tv;

    {
        width = 1920;
        height = 1280;
    }

    public void start(Context context){
        bmp_greenHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_hp);
        bmp_greenTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_tp);
        bmp_redHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_hp);
        bmp_redTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_tp);
        bmp_blueHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_hp);
        bmp_blueTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_tp);
        bmp_bullet = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_p);

        /*for (int i = 0; i < 5; i++) {
            enemyTanks.add(new Tank(1,1000 + i*150,300,36,-87,"SecondGuy",1));
        }*/

        enemyTanks.add(new Tank(1,1000,300,36,-87,"SecondGuy",1));

        myTank = new MyTank(1,200,200,1,0,
                "Me",0,800, bmp_greenHp, bmp_greenTp, this);

    }

    public void drawAll(Canvas canvas, Paint paint) {
        myTank.draw(canvas, paint);

        for (Tank t : enemyTanks) {
            t.draw(canvas, paint, bmp_redHp, bmp_redTp);
        }

        for (Tank t : allyTanks) {
            t.draw(canvas, paint, bmp_blueHp, bmp_blueTp);
        }

        Bullet[] arr_bullets = new Bullet[bullets.size()];
        bullets.toArray(arr_bullets);
        for (Bullet b : arr_bullets) {
            canvas.drawBitmap(bmp_bullet, (int) b.getX(), (int) b.getY(), paint);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public MyTank getMyTank() {
        return myTank;
    }

    public HashSet<Bullet> getBullets() {
        return bullets;
    }

    public double getlJangle() {
        return lJangle;
    }

    public void setlJangle(double lJangle) {
        this.lJangle = lJangle;
    }

    public double getlJstrength() {
        return lJstrength;
    }

    public void setlJstrength(double lJstrength) {
        this.lJstrength = lJstrength;
    }

    public double getrJangle() {
        return rJangle;
    }

    public void setrJangle(double rJangle) {
        this.rJangle = rJangle;
    }

    public double getrJstrength() {
        return rJstrength;
    }

    public void setrJstrength(double rJstrength) {
        this.rJstrength = rJstrength;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public TextView getFps_tv() {
        return fps_tv;
    }

    public void setFps_tv(TextView fps_tv) {
        this.fps_tv = fps_tv;
    }
}
