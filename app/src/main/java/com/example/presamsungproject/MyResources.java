package com.example.presamsungproject;

import android.content.Context;
import android.graphics.*;
import com.example.presamsungproject.MyInterfaces.ClientUpdatableUI;
import com.example.presamsungproject.MyInterfaces.GameUpdatableUI;
import com.example.presamsungproject.MyInterfaces.ServerUpdatableUI;

public class MyResources {
    private final Bitmap bmp_greenHp, bmp_greenTp, bmp_redHp, bmp_redTp, bmp_blueHp, bmp_blueTp;
    private final Bitmap bmp_greenDHp, bmp_greenDTp, bmp_redDHp, bmp_redDTp, bmp_blueDHp, bmp_blueDTp;
    private final Bitmap bmp_mapCell1, bmp_mapCell2, bmp_mapCellBackground;
    private final Bitmap bmp_wallV, bmp_wallH, bmp_wallC;
    private final Bitmap bmp_bullet;
    private final Paint allyNickPaint;
    private final Paint enemyNickPaint;
    private final Paint hitBoxPaint;
    private final Paint tankSightPaint;
    private final Paint defaultPaint;
    private Bitmap paintedWallpaper;
    private ServerUpdatableUI serverUpdatableUI;
    private ClientUpdatableUI clientUpdatableUI;
    private GameUpdatableUI gameUpdatableUI;

    public MyResources(Context context) {
        hitBoxPaint = new Paint();
        hitBoxPaint.setColor(Color.CYAN);
        hitBoxPaint.setStrokeWidth(3);
        hitBoxPaint.setStyle(Paint.Style.STROKE);

        tankSightPaint = new Paint();
        tankSightPaint.setColor(Color.RED);
        tankSightPaint.setStrokeWidth(3);

        enemyNickPaint = new Paint();
        enemyNickPaint.setTextSize(30);
        enemyNickPaint.setColor(Color.RED);

        allyNickPaint = new Paint();
        allyNickPaint.setTextSize(30);
        allyNickPaint.setColor(Color.GREEN);

        defaultPaint = new Paint();

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

        bmp_mapCell1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_map);
        bmp_mapCell2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_map2);
        bmp_mapCellBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_background_map);
        bmp_wallV = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        bmp_wallH = Bitmap.createBitmap(bmp_wallV.getHeight(), bmp_wallV.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp_wallH);
        canvas.rotate(-90, bmp_wallV.getWidth() / 2f, bmp_wallV.getWidth() / 2f);
        canvas.drawBitmap(bmp_wallV, 0, 0, defaultPaint);
        bmp_wallC = Bitmap.createBitmap(bmp_wallV, 0, 0, bmp_wallV.getWidth(), bmp_wallV.getWidth());
    }

    public void createWallpaper(Context context) {
        paintedWallpaper = Map.getWallpaperMap(context);
    }

    public Paint getHitBoxPaint() {
        return hitBoxPaint;
    }

    public Paint getTankSightPaint() {
        return tankSightPaint;
    }

    public Paint getAllyNickPaint() {
        return allyNickPaint;
    }

    public Paint getEnemyNickPaint() {
        return enemyNickPaint;
    }

    public Paint getDefaultPaint() {
        return defaultPaint;
    }

    public Bitmap getPaintedWallPaper() {
        return paintedWallpaper;
    }

    public Bitmap getBmp_greenHp() {
        return bmp_greenHp;
    }

    public Bitmap getBmp_greenTp() {
        return bmp_greenTp;
    }

    public Bitmap getBmp_redHp() {
        return bmp_redHp;
    }

    public Bitmap getBmp_redTp() {
        return bmp_redTp;
    }

    public Bitmap getBmp_blueHp() {
        return bmp_blueHp;
    }

    public Bitmap getBmp_blueTp() {
        return bmp_blueTp;
    }

    public Bitmap getBmp_greenDHp() {
        return bmp_greenDHp;
    }

    public Bitmap getBmp_greenDTp() {
        return bmp_greenDTp;
    }

    public Bitmap getBmp_redDHp() {
        return bmp_redDHp;
    }

    public Bitmap getBmp_redDTp() {
        return bmp_redDTp;
    }

    public Bitmap getBmp_blueDHp() {
        return bmp_blueDHp;
    }

    public Bitmap getBmp_blueDTp() {
        return bmp_blueDTp;
    }

    public Bitmap getBmp_mapCell1() {
        return bmp_mapCell1;
    }

    public Bitmap getBmp_mapCell2() {
        return bmp_mapCell2;
    }

    public Bitmap getBmp_mapCellBackground() {
        return bmp_mapCellBackground;
    }

    public Bitmap getBmp_wallV() {
        return bmp_wallV;
    }

    public Bitmap getBmp_wallH() {
        return bmp_wallH;
    }

    public Bitmap getBmp_wallC() {
        return bmp_wallC;
    }

    public Bitmap getBmp_bullet() {
        return bmp_bullet;
    }

    public ServerUpdatableUI getServerUpdatableUI() {
        return serverUpdatableUI;
    }

    public void setServerUpdatableUI(ServerUpdatableUI serverUpdatableUI) {
        this.serverUpdatableUI = serverUpdatableUI;
    }

    public ClientUpdatableUI getClientUpdatableUI() {
        return clientUpdatableUI;
    }

    public void setClientUpdatableUI(ClientUpdatableUI clientUpdatableUI) {
        this.clientUpdatableUI = clientUpdatableUI;
    }

    public GameUpdatableUI getGameUpdatableUI() {
        return gameUpdatableUI;
    }

    public void setGameUpdatableUI(GameUpdatableUI gameUpdatableUI) {
        this.gameUpdatableUI = gameUpdatableUI;
    }
}
