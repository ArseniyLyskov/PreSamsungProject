package com.example.presamsungproject.Models;

import android.content.Context;
import android.graphics.*;
import android.util.TypedValue;
import androidx.core.content.res.ResourcesCompat;
import com.example.presamsungproject.MyInterfaces.GameUIUpdateListener;
import com.example.presamsungproject.MyInterfaces.SFXInterface;
import com.example.presamsungproject.MyInterfaces.StartActivityMessageListener;
import com.example.presamsungproject.R;

public class MyResources {
    private final Bitmap bmp_greenHp, bmp_greenTp, bmp_redHp, bmp_redTp, bmp_blueHp, bmp_blueTp;
    private final Bitmap bmp_greenDHp, bmp_greenDTp, bmp_redDHp, bmp_redDTp, bmp_blueDHp, bmp_blueDTp;
    private final Bitmap bmp_mapCell1, bmp_mapCell2, bmp_mapCellBackground;
    private final Bitmap bmp_wallV, bmp_wallH, bmp_wallC;
    private final Bitmap bmp_bullet;
    private final Bitmap panel_350_200, panel_350_300;
    private final Paint allyNickPaint;
    private final Paint enemyNickPaint;
    private final Paint hitBoxPaint;
    private final Paint tankSightPaint;
    private final Paint defaultPaint;
    private Bitmap paintedWallpaper;
    private final SFXInterface sfxInterface;
    private StartActivityMessageListener SAMListener;
    private GameUIUpdateListener GUIUListener;

    public MyResources(Context context) {
        hitBoxPaint = new Paint();
        hitBoxPaint.setColor(Color.CYAN);
        hitBoxPaint.setStrokeWidth(3);
        hitBoxPaint.setStyle(Paint.Style.STROKE);

        tankSightPaint = new Paint();
        tankSightPaint.setColor(Color.RED);
        tankSightPaint.setStrokeWidth(3);

        int nicksTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                13, context.getResources().getDisplayMetrics());
        int defaultTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                20, context.getResources().getDisplayMetrics());

        enemyNickPaint = new Paint();
        enemyNickPaint.setTextSize(nicksTextSize);
        enemyNickPaint.setColor(Color.RED);
        enemyNickPaint.setTypeface(ResourcesCompat.getFont(context, R.font.minecraft_font));

        allyNickPaint = new Paint();
        allyNickPaint.setTextSize(nicksTextSize);
        allyNickPaint.setColor(Color.GREEN);
        allyNickPaint.setTypeface(ResourcesCompat.getFont(context, R.font.minecraft_font));

        defaultPaint = new Paint();
        defaultPaint.setTextSize(defaultTextSize);
        defaultPaint.setColor(Color.BLACK);
        defaultPaint.setTypeface(ResourcesCompat.getFont(context, R.font.minecraft_font));

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

        panel_350_200 = BitmapFactory.decodeResource(context.getResources(), R.drawable.panel_350_200);
        panel_350_300 = BitmapFactory.decodeResource(context.getResources(), R.drawable.panel_350_300);

        bmp_mapCell1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_map);
        bmp_mapCell2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_map2);
        bmp_mapCellBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_background_map);
        bmp_wallV = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        bmp_wallH = Bitmap.createBitmap(bmp_wallV.getHeight(), bmp_wallV.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp_wallH);
        canvas.rotate(-90, bmp_wallV.getWidth() / 2f, bmp_wallV.getWidth() / 2f);
        canvas.drawBitmap(bmp_wallV, 0, 0, defaultPaint);
        bmp_wallC = Bitmap.createBitmap(bmp_wallV, 0, 0, bmp_wallV.getWidth(), bmp_wallV.getWidth());

        sfxInterface = new MySoundEffects(context).getInterface();
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

    public Bitmap getPanel_350_200() {
        return panel_350_200;
    }

    public Bitmap getPanel_350_300() {
        return panel_350_300;
    }

    public GameUIUpdateListener getGUIUListener() {
        return GUIUListener;
    }

    public void setGUIUListener(GameUIUpdateListener GUIUListener) {
        this.GUIUListener = GUIUListener;
    }

    public StartActivityMessageListener getSAMListener() {
        return SAMListener;
    }

    public void setSAMListener(StartActivityMessageListener SAMListener) {
        this.SAMListener = SAMListener;
    }

    public SFXInterface getSFXInterface() {
        return sfxInterface;
    }
}
