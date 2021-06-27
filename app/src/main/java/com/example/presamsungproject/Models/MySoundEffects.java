package com.example.presamsungproject.Models;

import android.content.Context;
import android.media.MediaPlayer;
import com.example.presamsungproject.MyInterfaces.SFXInterface;
import com.example.presamsungproject.R;

public class MySoundEffects implements SFXInterface {
    public static final int MAIN_THEME = 11;
    public static final int TRACK = 12;
    public static final int CLICK = 21;
    public static final int SHOOT = 31;
    public static final int RICOCHET = 32;
    public static final int HIT = 33;
    public static final int EXPLOSION = 34;
    private final MediaPlayer mainTheme, track1, track2;
    private final MediaPlayer click;
    private final MediaPlayer shoot, ricochet, hit, explosion;

    public MySoundEffects(Context context) {
        mainTheme = MediaPlayer.create(context, R.raw.ost_tankz_main_theme);
        mainTheme.setLooping(true);
        mainTheme.setVolume(0.5f, 0.5f);
        track1 = MediaPlayer.create(context, R.raw.ost_tankz_track1);
        track1.setLooping(true);
        track1.setVolume(0.5f, 0.5f);
        track2 = MediaPlayer.create(context, R.raw.ost_tankz_track2);
        track2.setLooping(true);
        track2.setVolume(0.5f, 0.5f);
        click = MediaPlayer.create(context, R.raw.sfx_click);
        click.setLooping(false);
        click.setVolume(1, 1);
        shoot = MediaPlayer.create(context, R.raw.sfx_shoot);
        shoot.setLooping(false);
        shoot.setVolume(1, 1);
        ricochet = MediaPlayer.create(context, R.raw.sfx_ricochet);
        ricochet.setLooping(false);
        ricochet.setVolume(0.5f, 0.5f);
        hit = MediaPlayer.create(context, R.raw.sfx_hit);
        hit.setLooping(false);
        hit.setVolume(1, 1);
        explosion = MediaPlayer.create(context, R.raw.sfx_explosion);
        explosion.setLooping(false);
        explosion.setVolume(1, 1);
    }

    public SFXInterface getInterface() {
        return this;
    }

    @Override
    public void executeEffect(int effect) {
        switch (effect) {
            case MAIN_THEME: {
                mainTheme.start();
                break;
            }
            case TRACK: {
                int random = (int) (Math.random() * 2);
                switch (random) {
                    case 0: {
                        track1.start();
                        break;
                    }
                    case 1: {
                        track2.start();
                        break;
                    }
                }
            }
            case CLICK: {
                if (click.isPlaying())
                    click.seekTo(0);
                click.start();
                break;
            }
            case SHOOT: {
                if (shoot.isPlaying())
                    shoot.seekTo(0);
                shoot.start();
                break;
            }
            case RICOCHET: {
                if (ricochet.isPlaying())
                    ricochet.seekTo(0);
                ricochet.start();
                break;
            }
            case HIT: {
                if (hit.isPlaying())
                    hit.seekTo(0);
                hit.start();
                break;
            }
            case EXPLOSION: {
                if (explosion.isPlaying())
                    explosion.seekTo(0);
                explosion.start();
                break;
            }
        }
    }

    @Override
    public void stopEffect(int effect) {
        switch (effect) {
            case MAIN_THEME: {
                mainTheme.pause();
                mainTheme.seekTo(0);
                break;
            }
            case TRACK: {
                track1.pause();
                track1.seekTo(0);
                track2.pause();
                track2.seekTo(0);
                break;
            }
        }
    }

    @Override
    public void resumeEffects() {
        if (mainTheme.getCurrentPosition() != 0)
            mainTheme.start();
        if (track1.getCurrentPosition() != 0)
            track1.start();
        if (track2.getCurrentPosition() != 0)
            track2.start();
    }

    @Override
    public void pauseEffects() {
        if (mainTheme.isPlaying())
            mainTheme.pause();
        if (track1.isPlaying())
            track1.pause();
        if (track2.isPlaying())
            track2.pause();
    }
}
