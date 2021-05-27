package com.example.presamsungproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Arrays;
import java.util.HashSet;

public class Map {
    private Bitmap bmp_texture_map;
    private Bitmap bmp_texture_map2;
    private Bitmap bmp_wall_v, bmp_wall_h, bmp_wall_c;
    private Bitmap map;
    private boolean[][] isTextureAtCell;
    private boolean[][] isVWallAtCell;
    private boolean[][] isHWallAtCell;
    private boolean[][] isCWallAtCell;
    private int width_quantity;
    private int height_quantity;

    public Map(Context context) {
        Paint paint = new Paint();
        bmp_texture_map = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_map);
        bmp_texture_map2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_map2);
        bmp_wall_v = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        bmp_wall_h = Bitmap.createBitmap(bmp_wall_v.getHeight(), bmp_wall_v.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp_wall_h);
        canvas.rotate(-90, bmp_wall_v.getWidth() / 2f, bmp_wall_v.getWidth() / 2f);
        canvas.drawBitmap(bmp_wall_v, 0, 0, paint);
        bmp_wall_c = Bitmap.createBitmap(bmp_wall_v, 0, 0, bmp_wall_v.getWidth(), bmp_wall_v.getWidth());

        height_quantity = 3 + (int) (Math.random() * 2);
        width_quantity = height_quantity + 2 + (int) (Math.random() * 2);

        isTextureAtCell = new boolean[height_quantity][width_quantity];
        fillBooleanArrayTrue(isTextureAtCell);
        isVWallAtCell = new boolean[height_quantity + 1][width_quantity + 1];
        fillBooleanArrayFalse(isVWallAtCell);
        isHWallAtCell = new boolean[height_quantity + 1][width_quantity + 1];
        fillBooleanArrayFalse(isHWallAtCell);
        isCWallAtCell = new boolean[height_quantity + 1][width_quantity + 1];
        fillBooleanArrayFalse(isCWallAtCell);

        map = Bitmap.createBitmap(width_quantity * bmp_texture_map.getWidth() + bmp_wall_v.getWidth(),
                height_quantity * bmp_texture_map.getHeight() + bmp_wall_h.getHeight(),
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(map);

        for (int j = 0; j < height_quantity; j++) {
            for (int i = 0; i < width_quantity; i++) {
                double random = Math.random() * 100;
                if (random <= 50) {
                    canvas.drawBitmap(bmp_texture_map, bmp_texture_map.getWidth() * i, bmp_texture_map.getHeight() * j, paint);
                } else if (random <= 80) {
                    canvas.drawBitmap(bmp_texture_map2, bmp_texture_map.getWidth() * i, bmp_texture_map.getHeight() * j, paint);
                } else {
                    boolean[][] check = new boolean[isTextureAtCell.length][isTextureAtCell[0].length];
                    copyBooleanArray(isTextureAtCell, check);
                    check[j][i] = false;
                    if (isMapAvailable(check, isHWallAtCell, isVWallAtCell)) {
                        isTextureAtCell[j][i] = false;
                    } else {
                        canvas.drawBitmap(bmp_texture_map, bmp_texture_map.getWidth() * i, bmp_texture_map.getHeight() * j, paint);
                        //Log.d("MyTag", "Returned cell: " + (i + 1) + " " + (j + 1));
                    }
                }
            }
        }
        for (int j = 0; j < height_quantity; j++) {
            for (int i = 0; i < width_quantity; i++) {
                if (!isTextureAtCell[j][i]) {
                    if (i > 0)
                        if (isTextureAtCell[j][i - 1])
                            isVWallAtCell[j][i] = true;
                    if (i < width_quantity - 1)
                        if (isTextureAtCell[j][i + 1])
                            isVWallAtCell[j][i + 1] = true;
                    if (j > 0)
                        if (isTextureAtCell[j - 1][i])
                            isHWallAtCell[j][i] = true;
                    if (j < height_quantity - 1)
                        if (isTextureAtCell[j + 1][i])
                            isHWallAtCell[j + 1][i] = true;
                }
            }
        }
        for (int i = 0; i < height_quantity; i++) {
            if(isTextureAtCell[i][0])
                isVWallAtCell[i][0] = true;
            if(isTextureAtCell[i][width_quantity - 1])
                isVWallAtCell[i][width_quantity] = true;
        }
        for (int i = 0; i < width_quantity; i++) {
            if(isTextureAtCell[0][i])
                isHWallAtCell[0][i] = true;
            if(isTextureAtCell[height_quantity - 1][i])
                isHWallAtCell[height_quantity][i] = true;
        }
        for (int j = 1; j < height_quantity; j++) {
            for (int i = 1; i < width_quantity; i++) {
                if(!isVWallAtCell[j][i] && isTextureAtCell[j][i]) {
                    double random = Math.random() * 100;
                    if(random <= 40) {
                        boolean[][] checkV = new boolean[isVWallAtCell.length][isVWallAtCell[0].length];
                        copyBooleanArray(isVWallAtCell, checkV);
                        checkV[j][i] = true;
                        if (isMapAvailable(isTextureAtCell, isHWallAtCell, checkV))
                            isVWallAtCell[j][i] = true;
                        /*else
                            Log.d("MyTag", "Returned wallV: " + (i + 1) + " " + (j + 1));*/
                    }
                }

                if(!isHWallAtCell[j][i] && isTextureAtCell[j][i]) {
                    double random = Math.random() * 100;
                    if (random <= 40) {
                        boolean[][] checkH = new boolean[isHWallAtCell.length][isHWallAtCell[0].length];
                        copyBooleanArray(isHWallAtCell, checkH);
                        checkH[j][i] = true;
                        if (isMapAvailable(isTextureAtCell, checkH, isVWallAtCell))
                            isHWallAtCell[j][i] = true;
                        /*else
                            Log.d("MyTag", "Returned wallH: " + (i + 1) + " " + (j + 1));*/
                    }
                }
            }
        }
        for (int j = 0; j <= height_quantity; j++) {
            for (int i = 0; i <= width_quantity; i++) {
                if (isHWallAtCell[j][i])
                    canvas.drawBitmap(bmp_wall_h, i * bmp_texture_map.getWidth(), j * bmp_texture_map.getHeight(), paint);
                if (isVWallAtCell[j][i])
                    canvas.drawBitmap(bmp_wall_v, i * bmp_texture_map.getWidth(), j * bmp_texture_map.getHeight(), paint);
            }
        }
        for (int j = 1; j <= height_quantity; j++) {
            for (int i = 1; i <= width_quantity; i++) {
                if((!isHWallAtCell[j][i]) && (!isVWallAtCell[j][i]) && isHWallAtCell[j][i-1] && isVWallAtCell[j-1][i]) {
                    isCWallAtCell[j][i] = true;
                    canvas.drawBitmap(bmp_wall_c, i * bmp_texture_map.getWidth(), j * bmp_texture_map.getHeight(), paint);
                }
            }
        }
    }

    private boolean isMapAvailable(boolean[][] check, boolean[][] isHWallAtCell, boolean[][] isVWallAtCell) {
        int unavailableCells = 0;
        int x = -1, y = -1;
        for (int j = 0; j < check.length; j++) {
            for (int i = 0; i < check[j].length; i++) {
                if (!check[j][i])
                    unavailableCells++;
                else {
                    x = i;
                    y = j;
                }
            }
        }
        if (unavailableCells == check.length * check[0].length)
            return false;
        boolean[][] availableCells = new boolean[check.length][check[0].length];
        fillBooleanArrayFalse(availableCells);
        checkingCellsConnections(availableCells, check, y, x, isHWallAtCell, isVWallAtCell);
        int count = 0;
        for (int i = 0; i < availableCells.length; i++) {
            for (int j = 0; j < availableCells[0].length; j++) {
                if (availableCells[i][j] == true)
                    count++;
            }
        }
        if (count != check.length * check[0].length - unavailableCells)
            return false;
        return true;
    }

    private void copyBooleanArray(boolean[][] prev, boolean[][] ne) {
        for (int i = 0; i < prev.length; i++) {
            for (int j = 0; j < prev[0].length; j++) {
                ne[i][j] = prev[i][j];
            }
        }
    }

    private void checkingCellsConnections(boolean[][] availableCells, boolean[][] isTextureAtCell, int y, int x, boolean[][] isHWallAtCell, boolean[][] isVWallAtCell) {
        availableCells[y][x] = true;
        if (y > 0)
            if (!availableCells[y - 1][x] && !isHWallAtCell[y][x] && isTextureAtCell[y - 1][x])
                checkingCellsConnections(availableCells, isTextureAtCell, y - 1, x, isHWallAtCell, isVWallAtCell);
        if (x < isTextureAtCell[0].length - 1)
            if (!availableCells[y][x + 1] && !isVWallAtCell[y][x + 1] && isTextureAtCell[y][x + 1])
                checkingCellsConnections(availableCells, isTextureAtCell, y, x + 1, isHWallAtCell, isVWallAtCell);
        if (y < isTextureAtCell.length - 1)
            if (!availableCells[y + 1][x] && !isHWallAtCell[y + 1][x] && isTextureAtCell[y + 1][x])
                checkingCellsConnections(availableCells, isTextureAtCell, y + 1, x, isHWallAtCell, isVWallAtCell);
        if (x > 0)
            if (!availableCells[y][x - 1] && !isVWallAtCell[y][x] && isTextureAtCell[y][x - 1])
                checkingCellsConnections(availableCells, isTextureAtCell, y, x - 1, isHWallAtCell, isVWallAtCell);
    }

    private void fillBooleanArrayFalse(boolean[][] booleanArray) {
        for (boolean[] booleans : booleanArray) {
            Arrays.fill(booleans, false);
        }
    }

    private void fillBooleanArrayTrue(boolean[][] booleanArray) {
        for (boolean[] booleans : booleanArray) {
            Arrays.fill(booleans, true);
        }
    }

    public HashSet<HitBox> getWallsHitBox() {
        HashSet<HitBox> wallsHitBox = new HashSet<>();
        for (int j = 0; j <= height_quantity; j++) {
            for (int i = 0; i <= width_quantity; i++) {
                if(isHWallAtCell[j][i]) {
                    int length = 1;
                    int width_piece = 0;
                    for (int k = i + 1; k <= width_quantity; k++) {
                        if(isHWallAtCell[j][k])
                            length++;
                        else {
                            if(isVWallAtCell[j][k] || isCWallAtCell[j][k])
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

        for (int i = 0; i <= width_quantity; i++) {
            for (int j = 0; j <= height_quantity; j++) {
                if (isVWallAtCell[j][i]) {
                    int length = 1;
                    int height_piece = 0;
                    for (int k = j + 1; k <= height_quantity; k++) {
                        if (isVWallAtCell[k][i])
                            length++;
                        else {
                            if (isHWallAtCell[k][i] || isCWallAtCell[k][i])
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

    public Bitmap getBitmap() {
        return map;
    }

    public int[] startCoordinates() {
        int[] coordinates = new int[4];
        coordinates[2] = bmp_texture_map.getWidth();
        coordinates[3] = bmp_texture_map.getHeight();
        do {
            coordinates[0] = (int) (Math.random() * width_quantity);
            coordinates[1] = (int) (Math.random() * height_quantity);
        } while (!isTextureAtCell[coordinates[1]][coordinates[0]]);
        coordinates[0] *= coordinates[2];
        coordinates[1] *= coordinates[3];
        return coordinates;
    }
}
