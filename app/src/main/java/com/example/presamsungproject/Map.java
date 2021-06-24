package com.example.presamsungproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import com.example.presamsungproject.GameObjects.Bullet;
import com.example.presamsungproject.GameObjects.Tank;
import com.example.presamsungproject.GameObjects.TankSight;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

public class Map implements Serializable {
    private transient Paint paint;
    private transient int cellWidth;
    private transient int cellHeight;
    private transient int wallWidth;
    private transient int playableBitmapWidth;
    private transient int playableBitmapHeight;
    private transient int fullBitmapWidth;
    private transient int fullBitmapHeight;

    private MapCell[][] mapCells;
    private static final long serialVersionUID = 1L;

    public Map(int min_width, int min_height, int max_width, int max_height) {
        int height_quantity = min_height + (int) (Math.random() * (max_height - min_height));
        int width_quantity = min_width + (int) (Math.random() * (max_width - min_width));

        mapCellsInit(width_quantity, height_quantity);

        fieldsInit();

        generateRandomCells();
        generateBorderWalls();
        generateRandomWalls();
        generateCornerWalls();
    }

    private void fieldsInit() {
        paint = MySingletons.getMyResources().getDefaultPaint();

        cellWidth = MySingletons.getMyResources().getBmp_mapCellBackground().getWidth();
        cellHeight = MySingletons.getMyResources().getBmp_mapCellBackground().getHeight();
        wallWidth = MySingletons.getMyResources().getBmp_wallC().getWidth();
        playableBitmapWidth = (mapCells[0].length - 1) * cellWidth + wallWidth;
        playableBitmapHeight = (mapCells.length - 1) * cellHeight + wallWidth;
        fullBitmapWidth = (mapCells[0].length + 1) * cellWidth;
        fullBitmapHeight = (mapCells.length + 1) * cellHeight;
    }

    public Bitmap getDrawnMap() {
        fieldsInit();

        Bitmap fullBitmap = getBackgroundMap();
        Bitmap playableBitmap = Bitmap.createBitmap(playableBitmapWidth, playableBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas playableBitmapCanvas = new Canvas(playableBitmap);
        drawCells(playableBitmapCanvas, paint);
        drawWalls(playableBitmapCanvas, paint);

        Canvas fullBitmapCanvas = new Canvas(fullBitmap);
        fullBitmapCanvas.drawBitmap(playableBitmap, cellWidth, cellHeight, paint);

        return fullBitmap;
    }

    private Bitmap getBackgroundMap() {
        Bitmap bitmapBackground = Bitmap.createBitmap(fullBitmapWidth, fullBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapBackground);

        for (int i = 0; i < mapCells.length + 1; i++) {
            for (int j = 0; j < mapCells[0].length + 1; j++) {
                canvas.drawBitmap(MySingletons.getMyResources().getBmp_mapCellBackground(),
                        cellWidth * j, cellHeight * i, paint);
            }
        }

        return bitmapBackground;
    }

    public static Bitmap getWallpaperMap(Context context) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        Map map = null;
        double display_koeff = displaymetrics.widthPixels / (double) displaymetrics.heightPixels;
        double best_scale_koeff = 1;
        for (int i = 9; i < 24; i++) {
            for (int j = 14; j < 29; j++) {
                if (Math.abs((j + 1) / (double) (i + 1) - display_koeff) < Math.abs(best_scale_koeff - display_koeff))
                    best_scale_koeff = (j + 1) / (double) (i + 1);
            }
        }
        for (int i = 9; i < 24; i++) {
            for (int j = 14; j < 29; j++) {
                if ((j + 1) / (double) (i + 1) == best_scale_koeff) {
                    map = new Map(j, i, j, i);
                }
            }
        }
        if (map == null) {
            Log.d("MyTag", "Wallpaper creating error");
            return null;
        }
        Bitmap map_bitmap = map.getDrawnMap();
        Bitmap bitmap = Bitmap.createBitmap(map_bitmap.getWidth(), map_bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(map_bitmap, 0, 0, map.paint);
        wallpaperDrawSomething(map, canvas);
        return bitmap;
    }

    private static void wallpaperDrawSomething(Map map, Canvas canvas) {
        Bitmap map_content = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas_content = new Canvas(map_content);

        HashSet<HitBox> hitBoxes = map.getWallsHitBox();
        HashSet<Tank> tanks = new HashSet<>();

        int bmp_bulletWidth = MySingletons.getMyResources().getBmp_bullet().getWidth();
        int bmp_bulletHeight = MySingletons.getMyResources().getBmp_bullet().getHeight();
        int bmp_tankWidth = MySingletons.getMyResources().getBmp_greenHp().getWidth();
        int bmp_tankHeight = MySingletons.getMyResources().getBmp_greenHp().getHeight();

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
                if (map.mapCells[i][j].isTexture) {
                    int rand = (int) (Math.random() * 100);
                    int x = j * map.cellWidth + map.wallWidth;
                    int y = i * map.cellHeight + map.wallWidth;
                    if (rand < 20) {
                        continue;
                    } else if (rand < 80) {
                        for (int k = 1; k < (int) (Math.random() * 4); k++) {
                            double bX = x + Math.random() * map.cellWidth - map.wallWidth;
                            double bY = y + Math.random() * map.cellHeight - map.wallWidth;
                            Bullet b = new Bullet(bX, bY);
                            canvas_content.drawBitmap(MySingletons.getMyResources().getBmp_bullet(),
                                    (int) (b.getPoint().getX() - bmp_bulletWidth / 2f),
                                    (int) (b.getPoint().getY() - bmp_bulletHeight / 2f), map.paint);
                        }
                    } else {
                        double tankX = x + Math.random() * (map.cellWidth - bmp_tankWidth - map.wallWidth);
                        double tankY = y + Math.random() * (map.cellHeight - bmp_tankHeight - map.wallWidth);
                        double tankAngleH = Math.random() * 360;
                        double tankAngleT = Math.random() * 360;
                        Tank tank = new Tank(1, 1, tankX, tankY, tankAngleH, tankAngleT,
                                "", new TankSight(), new HashSet<Bullet>());
                        hitBoxes.add(new HitBox(tankX, tankY, tankAngleH, bmp_tankWidth, bmp_tankHeight, hullIndents));
                        hitBoxes.add(new HitBox(tankX, tankY, tankAngleH + tankAngleT, bmp_tankWidth, bmp_tankHeight, towerIndents));
                        Bitmap hull = null;
                        Bitmap tower = null;
                        switch ((int) (Math.random() * 4)) {
                            case 0: {
                                hull = MySingletons.getMyResources().getBmp_blueHp();
                                tower = MySingletons.getMyResources().getBmp_blueTp();
                                tanks.add(tank);
                                break;
                            }
                            case 1: {
                                hull = MySingletons.getMyResources().getBmp_blueDHp();
                                tower = MySingletons.getMyResources().getBmp_blueDTp();
                                break;
                            }
                            case 2: {
                                hull = MySingletons.getMyResources().getBmp_redHp();
                                tower = MySingletons.getMyResources().getBmp_redTp();
                                tanks.add(tank);
                                break;
                            }
                            case 3: {
                                hull = MySingletons.getMyResources().getBmp_redDHp();
                                tower = MySingletons.getMyResources().getBmp_redDTp();
                                break;
                            }
                        }
                        if (hull == null || tower == null) {
                            Log.d("MyTag", "Creating wallpaper tank error");
                            return;
                        }
                        tank.draw(canvas_content, map.paint, hull, tower);
                    }
                }
            }
        }

        for (Tank tank : tanks) {
            TankSight tankSight = new TankSight();
            tankSight.update(tank.getX() + bmp_tankWidth / 2f + 1.05 * bmp_tankWidth * towerIndents[0] * Math.cos(Math.toRadians(90 - tank.getAngleH() - tank.getAngleT())),
                    tank.getY() + bmp_tankHeight / 2f - 1.05 * bmp_tankHeight * towerIndents[0] * Math.sin(Math.toRadians(90 - tank.getAngleH() - tank.getAngleT())),
                    tank.getAngleH() + tank.getAngleT(), hitBoxes);
            tankSight.draw(canvas_content);
        }

        /*for (HitBox hb : hitBoxes) {
            hb.draw(canvas_content);
        }*/

        canvas.drawBitmap(map_content, map.cellWidth, map.cellHeight, map.paint);
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
                    canvas.drawBitmap(MySingletons.getMyResources().getBmp_mapCell1(),
                            cellWidth * i, cellHeight * j, paint);
                if (mapCells[j][i].isTextureB)
                    canvas.drawBitmap(MySingletons.getMyResources().getBmp_mapCell2(),
                            cellWidth * i, cellHeight * j, paint);
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
                    canvas.drawBitmap(MySingletons.getMyResources().getBmp_wallH(),
                            i * cellWidth, j * cellHeight, paint);
                if (mapCells[j][i].isVWall)
                    canvas.drawBitmap(MySingletons.getMyResources().getBmp_wallV(),
                            i * cellWidth, j * cellHeight, paint);
                if (mapCells[j][i].isCWall)
                    canvas.drawBitmap(MySingletons.getMyResources().getBmp_wallC(),
                            i * cellWidth, j * cellHeight, paint);
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
                                width_piece = wallWidth;
                            break;
                        }
                    }
                    wallsHitBox.add(new HitBox(i * cellWidth, j * cellHeight, 0,
                            length * cellWidth + width_piece, wallWidth, null));
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
                                height_piece = wallWidth;
                            break;
                        }
                    }
                    wallsHitBox.add(new HitBox(i * cellWidth, j * cellHeight, 0,
                            wallWidth, length * cellHeight + height_piece, null));
                    j += length;
                }
            }
        }
        return wallsHitBox;
    }

    public int[] getStartCoordinates() { //TODO: different coordinates
        int[] coordinates = new int[4];
        coordinates[2] = cellWidth;
        coordinates[3] = cellHeight;
        do {
            coordinates[0] = (int) (Math.random() * (mapCells[0].length - 1));
            coordinates[1] = (int) (Math.random() * (mapCells.length - 1));
        } while (!mapCells[coordinates[1]][coordinates[0]].isTextureA && !mapCells[coordinates[1]][coordinates[0]].isTextureB);
        coordinates[0] *= coordinates[2];
        coordinates[1] *= coordinates[3];
        return coordinates;
    }

    public int getWidthQuantity() {
        return mapCells[0].length;
    }

    public int getHeightQuantity() {
        return mapCells.length;
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
}