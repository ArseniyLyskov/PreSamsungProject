package com.example.presamsungproject.Models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.GameObjects.Bullet;
import com.example.presamsungproject.GameObjects.ControlledTank;
import com.example.presamsungproject.GameObjects.Tank;
import com.example.presamsungproject.Geometry.Point;

import java.util.HashMap;
import java.util.HashSet;

public class Game {
    public static final int MAX_FPS = 80;

    private final boolean DEBUG;
    private final HashMap<String, Tank> otherTanks = new HashMap<>();
    private final Bitmap map_bitmap;
    private final HashSet<HitBox> walls;
    private final ControlledTank controlledTank;
    private double lJangle, lJstrength, rJangle, rJstrength;
    private int fps = MAX_FPS, previousPFS = MAX_FPS;
    private int frameWidth, frameHeight;
    private final double scaleKoeff;


    public Game(String name, GameOptions gameOptions) {
        Map map = gameOptions.getMap();
        int hp = gameOptions.getHp();
        int team = gameOptions.getTeam();
        boolean ricochetAble = gameOptions.isRicochetAble();
        DEBUG = gameOptions.isDEBUG();

        map_bitmap = map.getDrawnMap();
        walls = map.getWallsHitBox();

        double startCoordinatesScale = gameOptions.getStartCoordinatesScale();
        Point startCoordinates = gameOptions.getStartCoordinates();
        scaleKoeff = getScale() / startCoordinatesScale;
        startCoordinates.scaleTo(scaleKoeff);
        controlledTank = new ControlledTank(hp, team, startCoordinates.getX(), startCoordinates.getY(), name, ricochetAble, this);

        if (!InfoSingleton.getInstance().isLobby()) {
            String message = MessageManager.clientReadyMessage();
            Client.getInstance().sendMessage(message);
        }
    }

    public void drawAll(Canvas canvas) {
        frameWidth = canvas.getWidth();
        frameHeight = canvas.getHeight();
        canvas.scale(1 / (float) scaleKoeff, 1 / (float) scaleKoeff, frameWidth / 2f, frameHeight / 2f);
        canvas.translate(getTranslateCanvasX(), getTranslateCanvasY()); //TODO: учесть scale

        canvas.drawBitmap(map_bitmap,
                -Resources.getInstance().getBmp_mapCellBackground().getWidth(),
                -Resources.getInstance().getBmp_mapCellBackground().getWidth(),
                Resources.getInstance().getAllyNickPaint());

        if (controlledTank.getHp() > 0)
            controlledTank.draw(canvas, Resources.getInstance().getAllyNickPaint(),
                    Resources.getInstance().getBmp_greenHp(),
                    Resources.getInstance().getBmp_greenTp());
        else
            controlledTank.draw(canvas, Resources.getInstance().getAllyNickPaint(),
                    Resources.getInstance().getBmp_greenDHp(),
                    Resources.getInstance().getBmp_greenDTp());

        for (Tank t : otherTanks.values()) {
            Bitmap hull;
            Bitmap tower;
            Paint paint;
            if (t.getTeam() != controlledTank.getTeam()) {
                if (t.getHp() > 0) {
                    hull = Resources.getInstance().getBmp_redHp();
                    tower = Resources.getInstance().getBmp_redTp();
                } else {
                    hull = Resources.getInstance().getBmp_redDHp();
                    tower = Resources.getInstance().getBmp_redDTp();
                }
                paint = Resources.getInstance().getEnemyNickPaint();
            } else {
                if (t.getHp() > 0) {
                    hull = Resources.getInstance().getBmp_blueHp();
                    tower = Resources.getInstance().getBmp_blueTp();
                } else {
                    hull = Resources.getInstance().getBmp_blueDHp();
                    tower = Resources.getInstance().getBmp_blueDTp();
                }
                paint = Resources.getInstance().getAllyNickPaint();
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
        controlledTank.getHullHitBox().draw(canvas);
        controlledTank.getTowerHitBox().draw(canvas);
        controlledTank.getUpdatedHhb().draw(canvas);
        controlledTank.getUpdatedThb().draw(canvas);
    }

    private HashSet<Bullet> getBullets() {
        HashSet<Bullet> bullets = new HashSet<>();

        for (Tank t : otherTanks.values()) {
            HashSet<Bullet> temp = new HashSet<>(t.getBullets());
            bullets.addAll(temp);
        }

        HashSet<Bullet> temp = new HashSet<>(controlledTank.getBullets());
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

    private int getTranslateCanvasX() {
        int cellWidth = Resources.getInstance().getBmp_mapCellBackground().getWidth();
        int tankWidth = Resources.getInstance().getBmp_blueHp().getWidth();
        if (frameWidth >= map_bitmap.getWidth())
            return cellWidth;
        int translation = -(int) (controlledTank.getX() - frameWidth / 2f + tankWidth / 2f);
        if (translation > cellWidth)
            return cellWidth;
        return Math.max(translation, -(map_bitmap.getWidth() - cellWidth - frameWidth));
    }

    private int getTranslateCanvasY() {
        int cellHeight = Resources.getInstance().getBmp_mapCellBackground().getHeight();
        int tankHeight = Resources.getInstance().getBmp_blueHp().getHeight();
        if (frameHeight >= map_bitmap.getHeight())
            return cellHeight;
        int translation = -(int) (controlledTank.getY() - frameHeight / 2f + tankHeight / 2f);
        if (translation > cellHeight)
            return cellHeight;
        return Math.max(translation, -(map_bitmap.getHeight() - cellHeight - frameHeight));
    }

    public int getScale() {
        return map_bitmap.getWidth();
    }

    public HashSet<HitBox> getWallsHitBoxes() {
        return walls;
    }

    public ControlledTank getControlledTank() {
        return controlledTank;
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
}