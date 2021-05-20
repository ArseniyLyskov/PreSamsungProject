package com.example.presamsungproject;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.widget.TextView;
import com.example.presamsungproject.GameObjects.*;

import java.util.HashSet;

public class Game {
    private Bitmap bmp_greenHp, bmp_greenTp;
    private Bitmap bmp_redHp, bmp_redTp;
    private Bitmap bmp_blueHp, bmp_blueTp;
    private Bitmap bmp_bullet;
    public static final int MAX_FPS = 60;

    private int width, height;
    private MyTank myTank;
    private HashSet<Tank> enemyTanks;
    private HashSet<Tank> allyTanks;
    private double lJangle, lJstrength, rJangle, rJstrength;
    private int fps;
    private int[] startCoordinates;
    private TextView fps_tv;
    private double scale;
    private Bitmap map_bitmap;
    private HashSet<HitBox> walls;

    {
        enemyTanks = new HashSet<>();
        allyTanks = new HashSet<>();
        width = 1920;
        height = 1280;
    }

    public Game(Map map) {
        this.map_bitmap = map.getBitmap();
        walls = map.getWallsHitBox();
        startCoordinates = map.startCoordinates();
    }

    public void start(Context context){
        bmp_greenHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_hp);
        bmp_greenTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_tp);
        bmp_redHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_hp);
        bmp_redTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_tp);
        bmp_blueHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_hp);
        bmp_blueTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_tp);
        bmp_bullet = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_p);

        Paint paint1 = new Paint();
        paint1.setTextSize(35);
        paint1.setColor(Color.RED);

        /*for (int i = 0; i < 5; i++) {
            enemyTanks.add(new Tank(500 + i * 150, 300 + i * 100, 36, -87,
                    "SecondGuy", new TankSight(), bmp_redHp, bmp_redTp,
                    paint1, new HashSet<Bullet>(), 1));
        }*/

        /*enemyTanks.add(new Tank(1000, 300, 36, -87,
                "SecondGuy", new TankSight(), bmp_redHp, bmp_redTp,
                paint1, new HashSet<Bullet>(), 1));*/

        Paint paint2 = new Paint();
        paint2.setTextSize(35);
        paint2.setColor(Color.GREEN);

        myTank = new MyTank(startCoordinates[0] + (startCoordinates[2] - bmp_greenHp.getWidth()) / 2f,
                startCoordinates[1] + (startCoordinates[3] - bmp_greenHp.getHeight()) / 2f,0,0,
                "Me", new TankSight(this), bmp_greenHp, bmp_greenTp,
                paint2, new HashSet<Bullet>(), 0, 800, 1, this);

    }

    public void drawAll(Canvas canvas, Paint paint) {
        canvas.drawBitmap(map_bitmap, 0, 0, paint);
        myTank.draw(canvas, paint, bmp_bullet);

        for (Tank t : enemyTanks) {
            t.draw(canvas, paint, bmp_redHp, bmp_redTp, bmp_bullet);
        }

        for (Tank t : allyTanks) {
            t.draw(canvas, paint, bmp_blueHp, bmp_blueTp, bmp_bullet);
        }

        //drawAllHitBoxes(canvas);
    }

    private void drawAllHitBoxes(Canvas canvas) {
        for (HitBox hb : getHitBoxes()) {
            hb.draw(canvas);
        }
        myTank.getHullHitBox().draw(canvas);
        myTank.getTowerHitBox().draw(canvas);
        myTank.getUpdatedHhb().draw(canvas);
        myTank.getUpdatedThb().draw(canvas);
    }

    public HashSet<HitBox> getHitBoxes() {
        HashSet<HitBox> hitBoxes = new HashSet<>();
        Tank[] arr_enemyTanksHB = new Tank[enemyTanks.size()];
        enemyTanks.toArray(arr_enemyTanksHB);
        Tank[] arr_allyTanksHB = new Tank[allyTanks.size()];
        allyTanks.toArray(arr_allyTanksHB);
        for (Tank tank: arr_enemyTanksHB) {
            hitBoxes.add(tank.getHullHitBox());
            hitBoxes.add(tank.getTowerHitBox());
        }
        for (Tank tank: arr_allyTanksHB) {
            hitBoxes.add(tank.getHullHitBox());
            hitBoxes.add(tank.getTowerHitBox());
        }
        hitBoxes.addAll(walls);
        return hitBoxes;
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
