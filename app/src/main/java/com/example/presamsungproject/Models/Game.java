package com.example.presamsungproject.Models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.GameObjects.Bullet;
import com.example.presamsungproject.GameObjects.MyTank;
import com.example.presamsungproject.GameObjects.Tank;
import com.example.presamsungproject.Geometry.Point;
import com.example.presamsungproject.MyInterfaces.GameUIUpdateListener;

import java.util.HashMap;
import java.util.HashSet;

public class Game {
    public static final int MAX_FPS = 80;
    public boolean isEverybodyReady = false;

    private final boolean DEBUG;
    private GameUIUpdateListener GUIUListener;
    private final HashMap<String, Tank> otherTanks = new HashMap<>();
    private final Bitmap map_bitmap;
    private final HashSet<HitBox> walls;
    private final MyTank myTank;
    private double lJangle, lJstrength, rJangle, rJstrength;
    private int fps = MAX_FPS, previousPFS = MAX_FPS;
    private double scaleTo = 1; //TODO: учесть в translate
    private int frameWidth, frameHeight;


    public Game(String name, GameOptions gameOptions) {
        Map map = gameOptions.getMap();
        int hp = gameOptions.getHp();
        int team = gameOptions.getTeam();
        boolean ricochetAble = gameOptions.isRicochetAble();
        DEBUG = gameOptions.isDEBUG();

        map_bitmap = map.getDrawnMap();
        walls = map.getWallsHitBox();

        double startCoordinatesScale = (double) gameOptions.getStartCoordinatesScale();
        Point startCoordinates = gameOptions.getStartCoordinates();
        startCoordinates.scaleTo(getScale() / startCoordinatesScale);
        myTank = new MyTank(hp, team, startCoordinates.getX(), startCoordinates.getY(), name, ricochetAble, this);
    }

    public void start() {
        if (!MySingletons.isLobby()) {
            try {
                String message = MessageManager.sendTankMessage(myTank.getSimpleVersion());
                MySingletons.getClient().sendMessage(message);
            } catch (Exception e) {
                Log.d("MyTag", "Sending tank error");
                e.printStackTrace();
            }
        } else {
            if (MySingletons.getServer().getConnectionsQuantity() == 0) {
                isEverybodyReady = true;
            }
        }
    }

    public void drawAll(Canvas canvas) {
        canvas.scale((float) scaleTo, (float) scaleTo, canvas.getWidth() / 2f, canvas.getHeight() / 2f);
        canvas.translate(getTranslateCanvasX(), getTranslateCanvasY());

        canvas.drawBitmap(map_bitmap,
                -MySingletons.getMyResources().getBmp_mapCellBackground().getWidth(),
                -MySingletons.getMyResources().getBmp_mapCellBackground().getWidth(),
                MySingletons.getMyResources().getAllyNickPaint());

        if (myTank.getHp() > 0)
            myTank.draw(canvas, MySingletons.getMyResources().getAllyNickPaint(),
                    MySingletons.getMyResources().getBmp_greenHp(),
                    MySingletons.getMyResources().getBmp_greenTp());
        else
            myTank.draw(canvas, MySingletons.getMyResources().getAllyNickPaint(),
                    MySingletons.getMyResources().getBmp_greenDHp(),
                    MySingletons.getMyResources().getBmp_greenDTp());

        for (Tank t : otherTanks.values()) {
            Bitmap hull;
            Bitmap tower;
            Paint paint;
            if (t.getTeam() != myTank.getTeam()) {
                if (t.getHp() > 0) {
                    hull = MySingletons.getMyResources().getBmp_redHp();
                    tower = MySingletons.getMyResources().getBmp_redTp();
                } else {
                    hull = MySingletons.getMyResources().getBmp_redDHp();
                    tower = MySingletons.getMyResources().getBmp_redDTp();
                }
                paint = MySingletons.getMyResources().getEnemyNickPaint();
            } else {
                if (t.getHp() > 0) {
                    hull = MySingletons.getMyResources().getBmp_blueHp();
                    tower = MySingletons.getMyResources().getBmp_blueTp();
                } else {
                    hull = MySingletons.getMyResources().getBmp_blueDHp();
                    tower = MySingletons.getMyResources().getBmp_blueDTp();
                }
                paint = MySingletons.getMyResources().getAllyNickPaint();
            }
            t.draw(canvas, paint, hull, tower);
        }

        if (DEBUG)
            drawAllHitBoxes(canvas);
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
            HashSet<Bullet> temp = new HashSet<>(t.getBullets());
            bullets.addAll(temp);
        }

        HashSet<Bullet> temp = new HashSet<>(myTank.getBullets());
        bullets.addAll(temp);
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
        int cellWidth = MySingletons.getMyResources().getBmp_mapCellBackground().getWidth();
        int tankWidth = MySingletons.getMyResources().getBmp_blueHp().getWidth();
        if (frameWidth >= map_bitmap.getWidth())
            return cellWidth;
        int translation = -(int) (myTank.getX() - frameWidth / 2f + tankWidth / 2f);
        if (translation > cellWidth)
            return cellWidth;
        return Math.max(translation, -(map_bitmap.getWidth() - cellWidth - frameWidth));
    }

    private int getTranslateCanvasY() {
        int cellHeight = MySingletons.getMyResources().getBmp_mapCellBackground().getHeight();
        int tankHeight = MySingletons.getMyResources().getBmp_blueHp().getHeight();
        if (frameHeight >= map_bitmap.getHeight())
            return cellHeight;
        int translation = -(int) (myTank.getY() - frameHeight / 2f + tankHeight / 2f);
        if (translation > cellHeight)
            return cellHeight;
        return Math.max(translation, -(map_bitmap.getHeight() - cellHeight - frameHeight));
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

    public int updateFPS() {
        int temp = fps;
        fps = 0;
        previousPFS = temp;
        return temp;
    }

    public void increaseFPS() {
        fps++;
    }

    public int getPreviousPFS() {
        return previousPFS;
    }

    public HashMap<String, Tank> getOtherTanks() {
        return otherTanks;
    }

    public void setGUIUListener(GameUIUpdateListener GUIUListener) {
        this.GUIUListener = GUIUListener;
    }
}