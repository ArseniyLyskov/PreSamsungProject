package com.example.presamsungproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

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

    public Map() {
        int height_quantity = 15 + (int) (Math.random() * 2);
        int width_quantity = height_quantity + 10 + (int) (Math.random() * 2);

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