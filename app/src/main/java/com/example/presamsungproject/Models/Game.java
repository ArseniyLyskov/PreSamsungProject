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
        double scaleKoeff = getScale() / startCoordinatesScale;
        startCoordinates.scaleTo(scaleKoeff);
        controlledTank = new ControlledTank(hp, team, startCoordinates.getX(), startCoordinates.getY(), name, ricochetAble, this);

        if (!InfoSingleton.getInstance().isLobby()) {
            String message = MessageManager.clientReadyMessage();
            Client.getInstance().sendMessage(message);
        }
    }

    public void drawAll(Canvas canvas) {
        canvas.save();

        float scale = (float) (2.625f / Resources.getInstance().getPixelsDensity());
        float scaleWidth = canvas.getWidth() * scale / 1920f;
        float scaleHeight = canvas.getHeight() * scale / 1080f;
        canvas.translate(getTranslateCanvasX(canvas.getWidth(), scaleWidth),
                getTranslateCanvasY(canvas.getHeight(), scaleHeight));
        canvas.scale(scaleWidth, scaleHeight,
                canvas.getWidth() / 2f, canvas.getHeight() / 2f);

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

        canvas.restore();
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

    private int getTranslateCanvasX(double canvasWidth, double scaleWidth) {
        int cellWidth = Resources.getInstance().getBmp_mapCellBackground().getWidth();
        int tankWidth = Resources.getInstance().getBmp_greenHp().getWidth();
        double rightMargin = map_bitmap.getWidth() - cellWidth - controlledTank.getX() - tankWidth;
        double leftMargin = map_bitmap.getWidth() - tankWidth - rightMargin;
        double minimumMargin = (canvasWidth - tankWidth) / 2f;
        double translation = -(controlledTank.getX() - (canvasWidth - tankWidth) / 2f);

        if (leftMargin * scaleWidth < minimumMargin) {
            double leftDeficit = minimumMargin - leftMargin * scaleWidth;
            return (int) (translation * scaleWidth - leftDeficit);
        } else {
            if (rightMargin * scaleWidth < minimumMargin) {
                double rightDeficit = minimumMargin - rightMargin * scaleWidth;
                return (int) (translation * scaleWidth + rightDeficit);
            } else {
                return (int) (translation * scaleWidth);
            }
        }
    }

    private int getTranslateCanvasY(double canvasHeight, double scaleHeight) {
        int cellHeight = Resources.getInstance().getBmp_mapCellBackground().getHeight();
        int tankHeight = Resources.getInstance().getBmp_greenHp().getHeight();
        double bottomMargin = map_bitmap.getHeight() - cellHeight - controlledTank.getY() - tankHeight;
        double topMargin = map_bitmap.getHeight() - tankHeight - bottomMargin;
        double minimumMargin = (canvasHeight - tankHeight) / 2f;
        double translation = -(controlledTank.getY() - (canvasHeight - tankHeight) / 2f);

        if (topMargin * scaleHeight < minimumMargin) {
            double topDeficit = minimumMargin - topMargin * scaleHeight;
            return (int) (translation * scaleHeight - topDeficit);
        } else {
            if (bottomMargin * scaleHeight < minimumMargin) {
                double bottomDeficit = minimumMargin - bottomMargin * scaleHeight;
                return (int) (translation * scaleHeight + bottomDeficit);
            } else {
                return (int) (translation * scaleHeight);
            }
        }
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