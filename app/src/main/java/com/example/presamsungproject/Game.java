package com.example.presamsungproject;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.widget.TextView;
import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.GameObjects.*;

import java.util.HashMap;
import java.util.HashSet;

public class Game {
    private Bitmap bmp_greenHp, bmp_greenTp, bmp_redHp, bmp_redTp, bmp_blueHp, bmp_blueTp;
    private Bitmap bmp_greenDHp, bmp_greenDTp, bmp_redDHp, bmp_redDTp, bmp_blueDHp, bmp_blueDTp;
    private Bitmap bmp_bullet;

    public static final int MAX_FPS = 60;
    public final String addrress;
    public final String name;
    public final boolean isLobby;
    public boolean isEverybodyReady = false;
    public HashMap<String, Tank> otherTanks = new HashMap<>();

    private Context context;
    private Map map;
    private MyTank myTank;
    private double lJangle, lJstrength, rJangle, rJstrength;
    private int fps;
    private int frameWidth, frameHeight;
    private int[] startCoordinates;
    private TextView fps_tv;
    private Bitmap map_bitmap;
    private HashSet<HitBox> walls;
    private int team;


    public Game(Map map, String name, int team, boolean isLobby, Context context) {
        this.map = map;
        this.name = name;
        this.team = team;
        this.isLobby = isLobby;
        this.context = context;
        addrress = MessageManager.EXTERNAL_ADDRESS;
        map_bitmap = map.getDrawnMap(context);
        walls = map.getWallsHitBox();
        startCoordinates = map.startCoordinates();
    }

    public void start() {
        bmp_greenHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_hp);
        bmp_greenTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_tp);
        bmp_redHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_hp);
        bmp_redTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_tp);
        bmp_blueHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_hp);
        bmp_blueTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_tp);
        bmp_greenDHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_dhp);
        bmp_greenDTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_dtp);
        bmp_redDHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_dhp);
        bmp_redDTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_dtp);
        bmp_blueDHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_dhp);
        bmp_blueDTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_dtp);
        bmp_bullet = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_p);

        myTank = new MyTank(3, team, startCoordinates[0] + (startCoordinates[2] - bmp_greenHp.getWidth()) / 2f,
                startCoordinates[1] + (startCoordinates[3] - bmp_greenHp.getHeight()) / 2f, 0, 0,
                name, new TankSight(), new HashSet<Bullet>(), bmp_greenHp, bmp_greenTp, 800, this);


        if (!isLobby) {
            try {
                String message = MessageManager.sendTankMessage(myTank.getTankToSerialize());
                Client.sendMessage(message);
            } catch (Exception e) {
                Log.d("MyTag", "Sending tank error");
                e.printStackTrace();
            }
        } else {
            if (Server.getConnectionsQuantity() == 0) {
                isEverybodyReady = true;
            }
        }

    }

    public void drawAll(Canvas canvas, Paint paint) {
        canvas.translate(getTranslateCanvasX(), getTranslateCanvasY());

        canvas.drawBitmap(map_bitmap, -map.getBackgroundCellWidth(), -map.getBackgroundCellHeight(), paint);
        if (myTank.hp > 0)
            myTank.draw(canvas, MyPaints.getAllyNickPaint(), bmp_greenHp, bmp_greenTp, bmp_bullet);
        else
            myTank.draw(canvas, MyPaints.getAllyNickPaint(), bmp_greenDHp, bmp_greenDTp, bmp_bullet);

        for (Tank t : otherTanks.values()) {
            if (t.team != team) {
                if (t.hp > 0)
                    t.draw(canvas, MyPaints.getEnemyNickPaint(), bmp_redHp, bmp_redTp, bmp_bullet);
                else
                    t.draw(canvas, MyPaints.getEnemyNickPaint(), bmp_redDHp, bmp_redDTp, bmp_bullet);
            } else {
                if (t.hp > 0)
                    t.draw(canvas, MyPaints.getAllyNickPaint(), bmp_blueHp, bmp_blueTp, bmp_bullet);
                else
                    t.draw(canvas, MyPaints.getAllyNickPaint(), bmp_blueDHp, bmp_blueDTp, bmp_bullet);
            }
        }

        //drawAllHitBoxes(canvas);
    }

    private void drawAllHitBoxes(Canvas canvas) {
        for (HitBox hb : getAllHitBoxes()) {
            hb.draw(canvas);
        }
        for (Bullet b : getBullets()) {
            b.drawHitBox(canvas);
        }
        myTank.getHullHitBox().draw(canvas);
        myTank.getTowerHitBox().draw(canvas);
        myTank.getUpdatedHhb().draw(canvas);
        myTank.getUpdatedThb().draw(canvas);
    }

    private HashSet<Bullet> getBullets() {
        HashSet<Bullet> bullets = new HashSet<>();

        for (Tank t : otherTanks.values()) {
            Bullet[] arr_bullets = new Bullet[t.getBullets().size()];
            t.getBullets().toArray(arr_bullets);
            for (Bullet b : arr_bullets) {
                bullets.add(b);
            }
        }

        Bullet[] arr_bullets = new Bullet[myTank.getBullets().size()];
        myTank.getBullets().toArray(arr_bullets);
        for (Bullet b : arr_bullets) {
            bullets.add(b);
        }
        return bullets;
    }

    public HashSet<HitBox> getAllHitBoxes() {
        HashSet<HitBox> hitBoxes = new HashSet<>();
        for (Tank tank : otherTanks.values()) {
            hitBoxes.add(tank.getHullHitBox());
            hitBoxes.add(tank.getTowerHitBox());
        }
        hitBoxes.addAll(walls);
        return hitBoxes;
    }

    public HashSet<HitBox> getTankHitBoxes() {
        HashSet<HitBox> hitBoxes = new HashSet<>();
        for (Tank tank : otherTanks.values()) {
            hitBoxes.add(tank.getHullHitBox());
            hitBoxes.add(tank.getTowerHitBox());
        }
        hitBoxes.add(myTank.getTowerHitBox());
        hitBoxes.add(myTank.getHullHitBox());
        return hitBoxes;
    }

    private int getTranslateCanvasX() {
        int translation = -(int) (myTank.getX() - frameWidth / 2f + bmp_greenHp.getWidth() / 2f);
        if(translation > map.getBackgroundCellWidth())
            return map.getBackgroundCellWidth();
        if(translation < -(map_bitmap.getWidth() - map.getBackgroundCellWidth() - frameWidth))
            return -(map_bitmap.getWidth() - map.getBackgroundCellWidth() - frameWidth);
        return translation;
    }

    private int getTranslateCanvasY() {
        int translation = -(int) (myTank.getY() - frameHeight / 2f + bmp_greenHp.getHeight() / 2f);
        if(translation > map.getBackgroundCellHeight())
            return map.getBackgroundCellHeight();
        if(translation < -(map_bitmap.getHeight() - map.getBackgroundCellHeight() - frameHeight))
            return -(map_bitmap.getHeight() - map.getBackgroundCellHeight() - frameHeight);
        return translation;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    public int getScale() {
        return map_bitmap.getWidth();
    }

    public HashSet<HitBox> getWallsHitBoxes() {
        return walls;
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