package com.example.presamsungproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import com.example.presamsungproject.GameObjects.Bullet;
import com.example.presamsungproject.GameObjects.Tank;
import com.example.presamsungproject.GameObjects.TankSight;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

public class Map implements Serializable {
    private transient Bitmap bmp_texture_map, bmp_texture_map2, bmp_background_map;
    private transient Bitmap bmp_wall_v, bmp_wall_h, bmp_wall_c;
    private transient Bitmap bitMap;
    private transient Paint paint;

    private MapCell[][] mapCells;
    private static final long serialVersionUID = 1L;

    public Map(int min_width, int min_height, int max_width, int max_height) {
        int height_quantity = min_height + (int) (Math.random() * (max_height - min_height));
        int width_quantity = min_width + (int) (Math.random() * (max_width - min_width));

        mapCellsInit(width_quantity, height_quantity);

        generateRandomCells();
        generateBorderWalls();
        generateRandomWalls();
        generateCornerWalls();
    }

    public Bitmap getDrawnMap(Context context) {
        sourceInit(context);

        Bitmap backgroundBitmap = getBackgroundMap();

        bitMap = Bitmap.createBitmap((mapCells[0].length - 1) * bmp_texture_map.getWidth() + bmp_wall_v.getWidth(),
                (mapCells.length - 1) * bmp_texture_map.getHeight() + bmp_wall_h.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitMap);

        drawCells(canvas, paint);
        drawWalls(canvas, paint);

        Canvas backgroundCanvas = new Canvas(backgroundBitmap);
        backgroundCanvas.drawBitmap(bitMap, bmp_background_map.getWidth(), bmp_background_map.getHeight(), paint);

        return backgroundBitmap;
    }

    private Bitmap getBackgroundMap() {
        Bitmap bitMapBackground = Bitmap.createBitmap((mapCells[0].length + 1) * bmp_texture_map.getWidth(),
                (mapCells.length + 1) * bmp_texture_map.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitMapBackground);

        for (int i = 0; i < mapCells.length + 1; i++) {
            for (int j = 0; j < mapCells[0].length + 1; j++) {
                canvas.drawBitmap(bmp_background_map, bmp_background_map.getWidth() * j,
                        bmp_background_map.getHeight() * i, paint);
            }
        }

        return bitMapBackground;
    }

    public static Bitmap getWallPaperMap(Context context) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        Map map = null;
        double display_koeff = displaymetrics.widthPixels / (double) displaymetrics.heightPixels;
        double best_scale_koeff = 1;
        for (int i = 9; i < 24; i++) {
            for (int j = 14; j < 29; j++) {
                if(Math.abs((j + 1) / (double) (i + 1) - display_koeff) < Math.abs(best_scale_koeff - display_koeff))
                    best_scale_koeff = (j + 1) / (double) (i + 1);
            }
        }
        for (int i = 9; i < 24; i++) {
            for (int j = 14; j < 29; j++) {
                if((j + 1) / (double) (i + 1) == best_scale_koeff) {
                    map = new Map(j, i, j, i);
                }
            }
        }
        Bitmap map_bitmap = map.getDrawnMap(context);
        Bitmap bitmap = Bitmap.createBitmap(map_bitmap.getWidth(), map_bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(map_bitmap, 0, 0, MyPaints.getDefaultPaint());
        wallpaperDrawSomething(map, canvas, context);
        return bitmap;
    }

    private static void wallpaperDrawSomething(Map map, Canvas canvas, Context context) {
        Bitmap bmp_bullet = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_p);
        Bitmap bmp_mapCell = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_map);
        Bitmap bmp_wall_v = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        Bitmap bmp_redHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_hp);
        Bitmap bmp_redTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_tp);
        Bitmap bmp_blueHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_hp);
        Bitmap bmp_blueTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_tp);
        Bitmap bmp_redDHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_dhp);
        Bitmap bmp_redDTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_dtp);
        Bitmap bmp_blueDHp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_dhp);
        Bitmap bmp_blueDTp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_dtp);

        Bitmap map_content = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas_content = new Canvas(map_content);

        HashSet<HitBox> hitBoxes = map.getWallsHitBox();
        HashSet<Tank> tanks = new HashSet<>();

        double[] hullIndents, towerIndents;
        hullIndents = new double[4];
        hullIndents[0] = 18 / 50f;
        hullIndents[1] = 15 / 50f;
        hullIndents[2] = 19 / 50f;
        hullIndents[3] = 15 / 50f;
        towerIndents = new double[4];
        towerIndents[0] = 25 / 50f;
        towerIndents[1] = 3 / 50f;
        towerIndents[2] = 1 / 50f;
        towerIndents[3] = 4 / 50f;

        for (int i = 0; i < map.getHeightQuantity(); i++) {
            for (int j = 0; j < map.getWidthQuantity(); j++) {
                if(map.mapCells[i][j].isTexture) {
                    int rand = (int) (Math.random() * 100);
                    int x = j * bmp_mapCell.getWidth() + bmp_wall_v.getWidth();
                    int y = i * bmp_mapCell.getHeight() + bmp_wall_v.getWidth();
                    if(rand < 20)
                        continue;
                    else if(rand < 80) {
                        for (int k = 1; k < (int) (Math.random() * 4); k++) {
                            Bullet b = new Bullet(
                                    x + Math.random() * bmp_mapCell.getWidth() - bmp_wall_v.getWidth(),
                                    y + Math.random() * bmp_mapCell.getHeight() - bmp_wall_v.getWidth());
                            canvas_content.drawBitmap(bmp_bullet, (int) (b.getX() - bmp_bullet.getWidth() / 2),
                                    (int) (b.getY() - bmp_bullet.getHeight() / 2), MyPaints.getDefaultPaint());
                        }
                    } else {
                        double tankX = x + Math.random() * (bmp_mapCell.getWidth() - bmp_blueHp.getWidth() - bmp_wall_v.getWidth());
                        double tankY = y + Math.random() * (bmp_mapCell.getHeight() - bmp_blueHp.getHeight() - bmp_wall_v.getWidth());
                        double tankAngleH = Math.random() * 360;
                        double tankAngleT = Math.random() * 360;
                        Tank tank = new Tank(1, 1, tankX, tankY, tankAngleH, tankAngleT,
                                "", new TankSight(), new HashSet<Bullet>());
                        hitBoxes.add(new HitBox(tankX, tankY, tankAngleH, bmp_blueHp.getWidth(), bmp_blueHp.getHeight(), hullIndents));
                        hitBoxes.add(new HitBox(tankX, tankY, tankAngleH + tankAngleT, bmp_blueTp.getWidth(), bmp_blueTp.getHeight(), towerIndents));
                        switch ((int) (Math.random() * 3)) {
                            case 0: {
                                tank.draw(canvas_content, MyPaints.getDefaultPaint(), bmp_blueHp, bmp_blueTp, bmp_bullet);
                                tanks.add(tank);
                                break;
                            }
                            case 1: {
                                tank.draw(canvas_content, MyPaints.getDefaultPaint(), bmp_blueDHp, bmp_blueDTp, bmp_bullet);
                                break;
                            }
                            case 2: {
                                tank.draw(canvas_content, MyPaints.getDefaultPaint(), bmp_redHp, bmp_redTp, bmp_bullet);
                                tanks.add(tank);
                                break;
                            }
                            case 3: {
                                tank.draw(canvas_content, MyPaints.getDefaultPaint(), bmp_redDHp, bmp_redDTp, bmp_bullet);
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (Tank tank : tanks) {
            TankSight tankSight = new TankSight();
            tankSight.update(tank.getX() + bmp_blueTp.getWidth() / 2f + bmp_blueTp.getWidth() * towerIndents[0] * Math.cos(Math.toRadians(90 - tank.getAngleH() - tank.getAngleT())),
                    tank.getY() + bmp_blueTp.getHeight() / 2f - bmp_blueTp.getHeight() * towerIndents[0] * Math.sin(Math.toRadians(90 - tank.getAngleH() - tank.getAngleT())),
                    tank.getAngleH() + tank.getAngleT(), hitBoxes);
            tankSight.draw(canvas_content);
        }

        /*for (HitBox hb : hitBoxes) {
            hb.draw(canvas_content);
        }*/

        canvas.drawBitmap(map_content, bmp_mapCell.getWidth(), bmp_mapCell.getHeight(), MyPaints.getDefaultPaint());
    }

    private void sourceInit(Context context) {
        paint = new Paint();
        bmp_texture_map = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_map);
        bmp_texture_map2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_map2);
        bmp_background_map = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_background_map);
        bmp_wall_v = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        bmp_wall_h = Bitmap.createBitmap(bmp_wall_v.getHeight(), bmp_wall_v.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp_wall_h);
        canvas.rotate(-90, bmp_wall_v.getWidth() / 2f, bmp_wall_v.getWidth() / 2f);
        canvas.drawBitmap(bmp_wall_v, 0, 0, paint);
        bmp_wall_c = Bitmap.createBitmap(bmp_wall_v, 0, 0, bmp_wall_v.getWidth(), bmp_wall_v.getWidth());
    }

    private void mapCellsInit(int width_quantity, int height_quantity) {
        mapCells = new MapCell[height_quantity + 1][width_quantity + 1];
        fillMapCells(mapCells);
        for (int i = 0; i < mapCells.length; i++) {
            mapCells[i][width_quantity].isTexture = false;
        }
        for (int i = 0; i < mapCells[0].length; i++) {
            mapCells[height_quantity][i].isTexture = false;
        }
    }

    private void drawCells(Canvas canvas, Paint paint) {
        for (int j = 0; j + 1 < mapCells.length; j++) {
            for (int i = 0; i + 1 < mapCells[0].length; i++) {
                if (mapCells[j][i].isTextureA)
                    canvas.drawBitmap(bmp_texture_map, bmp_texture_map.getWidth() * i, bmp_texture_map.getHeight() * j, paint);
                if (mapCells[j][i].isTextureB)
                    canvas.drawBitmap(bmp_texture_map2, bmp_texture_map.getWidth() * i, bmp_texture_map.getHeight() * j, paint);
            }
        }
    }

    private void generateRandomCells() {
        for (int j = 0; j + 1 < mapCells.length; j++) {
            for (int i = 0; i + 1 < mapCells[0].length; i++) {
                double random = Math.random() * 100;
                if (random <= 50) {
                    mapCells[j][i].isTextureA = true;
                } else if (random <= 80) {
                    mapCells[j][i].isTextureB = true;
                } else {
                    mapCells[j][i].isTexture = false;
                    if (!isMapAvailable(mapCells)) {
                        mapCells[j][i].isTexture = true;
                        mapCells[j][i].isTextureA = true;
                        //Log.d("MyTag", "Returned cell: " + (i + 1) + " " + (j + 1));
                    }
                }
            }
        }
    }

    private void generateBorderWalls() {
        for (int j = 0; j < mapCells.length; j++) {
            for (int i = 0; i < mapCells[0].length; i++) {
                if (!mapCells[j][i].isTexture) {
                    if (i > 0)
                        if (mapCells[j][i - 1].isTexture)
                            mapCells[j][i].isVWall = true;
                    if (i < mapCells[0].length - 1)
                        if (mapCells[j][i + 1].isTexture)
                            mapCells[j][i + 1].isVWall = true;
                    if (j > 0)
                        if (mapCells[j - 1][i].isTexture)
                            mapCells[j][i].isHWall = true;
                    if (j < mapCells.length - 1)
                        if (mapCells[j + 1][i].isTexture)
                            mapCells[j + 1][i].isHWall = true;
                }
            }
        }
        for (int i = 0; i < mapCells.length; i++) {
            if (mapCells[i][0].isTexture)
                mapCells[i][0].isVWall = true;
            if (mapCells[i][mapCells[0].length - 1].isTexture)
                mapCells[i][mapCells[0].length].isVWall = true;
        }
        for (int i = 0; i < mapCells[0].length; i++) {
            if (mapCells[0][i].isTexture)
                mapCells[0][i].isHWall = true;
            if (mapCells[mapCells.length - 1][i].isTexture)
                mapCells[mapCells.length][i].isHWall = true;
        }
    }

    private void generateCornerWalls() {
        for (int j = 1; j < mapCells.length; j++) {
            for (int i = 1; i < mapCells[0].length; i++) {
                if ((!mapCells[j][i].isHWall) && (!mapCells[j][i].isVWall) && mapCells[j][i - 1].isHWall && mapCells[j - 1][i].isVWall) {
                    mapCells[j][i].isCWall = true;
                }
            }
        }
    }

    private void generateRandomWalls() {
        for (int j = 1; j + 1 < mapCells.length; j++) {
            for (int i = 1; i + 1 < mapCells[0].length; i++) {
                if (!mapCells[j][i].isVWall && mapCells[j][i].isTexture) {
                    double random = Math.random() * 100;
                    if (random <= 40) {
                        mapCells[j][i].isVWall = true;
                        if (!isMapAvailable(mapCells)) {
                            mapCells[j][i].isVWall = false;
                            //Log.d("MyTag", "Returned wallV: " + (i + 1) + " " + (j + 1));
                        }
                    }
                }

                if (!mapCells[j][i].isHWall && mapCells[j][i].isTexture) {
                    double random = Math.random() * 100;
                    if (random <= 40) {
                        mapCells[j][i].isHWall = true;
                        if (!isMapAvailable(mapCells)) {
                            mapCells[j][i].isHWall = false;
                            //Log.d("MyTag", "Returned wallH: " + (i + 1) + " " + (j + 1));
                        }
                    }
                }
            }
        }
    }

    private void drawWalls(Canvas canvas, Paint paint) {
        for (int j = 0; j < mapCells.length; j++) {
            for (int i = 0; i < mapCells[0].length; i++) {
                if (mapCells[j][i].isHWall)
                    canvas.drawBitmap(bmp_wall_h, i * bmp_texture_map.getWidth(), j * bmp_texture_map.getHeight(), paint);
                if (mapCells[j][i].isVWall)
                    canvas.drawBitmap(bmp_wall_v, i * bmp_texture_map.getWidth(), j * bmp_texture_map.getHeight(), paint);
                if (mapCells[j][i].isCWall)
                    canvas.drawBitmap(bmp_wall_c, i * bmp_texture_map.getWidth(), j * bmp_texture_map.getHeight(), paint);
            }
        }
    }

    private boolean isMapAvailable(MapCell[][] check) {
        int unavailableCells = 0;
        int x = -1, y = -1;
        for (int j = 0; j < check.length; j++) {
            for (int i = 0; i < check[j].length; i++) {
                if (!check[j][i].isTexture)
                    unavailableCells++;
                else {
                    x = i;
                    y = j;
                }
            }
        }
        if (unavailableCells == check.length * check[0].length || x == -1)
            return false;
        boolean[][] availableCells = new boolean[check.length][check[0].length];
        fillBooleanArrayWithValue(availableCells, false);
        checkingCellsConnections(availableCells, check, y, x);
        int count = 0;
        for (int i = 0; i < availableCells.length; i++) {
            for (int j = 0; j < availableCells[0].length; j++) {
                if (availableCells[i][j])
                    count++;
            }
        }
        if (count != check.length * check[0].length - unavailableCells)
            return false;
        return true;
    }

    private void copyMapCellsArray(MapCell[][] arrayCopyFrom, MapCell[][] arrayCopyTo) {
        for (int i = 0; i < arrayCopyFrom.length; i++) {
            for (int j = 0; j < arrayCopyFrom[0].length; j++) {
                arrayCopyTo[i][j] = arrayCopyFrom[i][j].getCopy();
            }
        }
    }

    private void checkingCellsConnections(boolean[][] availableCells, MapCell[][] check, int y, int x) {
        availableCells[y][x] = true;
        if (y > 0)
            if (!availableCells[y - 1][x] && !check[y][x].isHWall && check[y - 1][x].isTexture)
                checkingCellsConnections(availableCells, check, y - 1, x);
        if (x < check[0].length - 1)
            if (!availableCells[y][x + 1] && !check[y][x + 1].isVWall && check[y][x + 1].isTexture)
                checkingCellsConnections(availableCells, check, y, x + 1);
        if (y < check.length - 1)
            if (!availableCells[y + 1][x] && !check[y + 1][x].isHWall && check[y + 1][x].isTexture)
                checkingCellsConnections(availableCells, check, y + 1, x);
        if (x > 0)
            if (!availableCells[y][x - 1] && !check[y][x].isVWall && check[y][x - 1].isTexture)
                checkingCellsConnections(availableCells, check, y, x - 1);
    }

    private void fillBooleanArrayWithValue(boolean[][] booleanArray, boolean value) {
        for (boolean[] booleans : booleanArray) {
            Arrays.fill(booleans, value);
        }
    }

    private void fillMapCells(MapCell[][] mapCells) {
        for (int i = 0; i < mapCells.length; i++) {
            for (int j = 0; j < mapCells[0].length; j++) {
                mapCells[i][j] = new MapCell(j, i);
            }
        }
    }

    public HashSet<HitBox> getWallsHitBox() {
        HashSet<HitBox> wallsHitBox = new HashSet<>();
        for (int j = 0; j < mapCells.length; j++) {
            for (int i = 0; i < mapCells[0].length; i++) {
                if (mapCells[j][i].isHWall) {
                    int length = 1;
                    int width_piece = 0;
                    for (int k = i + 1; k < mapCells[0].length; k++) {
                        if (mapCells[j][k].isHWall)
                            length++;
                        else {
                            if (mapCells[j][k].isVWall || mapCells[j][k].isCWall)
                                width_piece = bmp_wall_c.getWidth();
                            break;
                        }
                    }
                    wallsHitBox.add(new HitBox(i * bmp_texture_map.getWidth(), j * bmp_texture_map.getHeight(), 0,
                            length * bmp_wall_h.getWidth() + width_piece, bmp_wall_h.getHeight(), null));
                    i += length;
                }
            }
        }

        for (int i = 0; i < mapCells[0].length; i++) {
            for (int j = 0; j < mapCells.length; j++) {
                if (mapCells[j][i].isVWall) {
                    int length = 1;
                    int height_piece = 0;
                    for (int k = j + 1; k < mapCells.length; k++) {
                        if (mapCells[k][i].isVWall)
                            length++;
                        else {
                            if (mapCells[k][i].isHWall || mapCells[k][i].isCWall)
                                height_piece = bmp_wall_c.getHeight();
                            break;
                        }
                    }
                    wallsHitBox.add(new HitBox(i * bmp_texture_map.getWidth(), j * bmp_texture_map.getHeight(), 0,
                            bmp_wall_v.getWidth(), length * bmp_wall_v.getHeight() + height_piece, null));
                    j += length;
                }
            }
        }
        return wallsHitBox;
    }

    public int[] startCoordinates() { //TODO: different coordinates
        int[] coordinates = new int[4];
        coordinates[2] = bmp_texture_map.getWidth();
        coordinates[3] = bmp_texture_map.getHeight();
        do {
            coordinates[0] = (int) (Math.random() * (mapCells[0].length - 1));
            coordinates[1] = (int) (Math.random() * (mapCells.length - 1));
        } while (!mapCells[coordinates[1]][coordinates[0]].isTextureA && !mapCells[coordinates[1]][coordinates[0]].isTextureB);
        coordinates[0] *= coordinates[2];
        coordinates[1] *= coordinates[3];
        return coordinates;
    }

    public int getBackgroundCellWidth() {
        return bmp_background_map.getWidth();
    }

    public int getBackgroundCellHeight() {
        return bmp_background_map.getHeight();
    }

    public int getWidthQuantity() {
        return mapCells[0].length;
    }

    public int getHeightQuantity() {
        return mapCells.length;
    }
}

class MapCell implements Serializable {
    private int x, y;
    public boolean isTexture;
    public boolean isTextureA, isTextureB;
    public boolean isVWall, isHWall;
    public boolean isCWall;

    public MapCell(int x, int y) {
        this.x = x;
        this.y = y;
        isTexture = true;
        isTextureA = false;
        isTextureB = false;
        isVWall = false;
        isHWall = false;
        isCWall = false;
    }

    public MapCell(int x, int y, boolean isTexture, boolean isTextureA, boolean isTextureB,
                   boolean isVWall, boolean isHWall, boolean isCWall) {
        this.x = x;
        this.y = y;
        this.isTexture = isTexture;
        this.isTextureA = isTextureA;
        this.isTextureB = isTextureB;
        this.isVWall = isVWall;
        this.isHWall = isHWall;
        this.isCWall = isCWall;
    }

    public MapCell getCopy() {
        return new MapCell(x, y, isTexture, isTextureA, isTextureB, isVWall, isHWall, isCWall);
    }
}