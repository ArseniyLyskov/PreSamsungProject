package com.example.presamsungproject.Models;

import com.example.presamsungproject.Geometry.Point;

import java.io.Serializable;
import java.util.Arrays;

public class GameOptions implements Serializable {
    private transient final int[] teams;

    private final Map map;
    private final Point startCoordinates;
    private final int startCoordinatesScale;
    private final int team;
    private final int hp;
    private final boolean ricochetAble;
    private final boolean DEBUG;

    public GameOptions(Map map, int team_quantity, int hp, boolean ricochetAble, boolean DEBUG) {
        this.map = map;
        this.hp = hp;
        this.ricochetAble = ricochetAble;
        this.DEBUG = DEBUG;
        startCoordinates = map.getStartCoordinates();
        startCoordinatesScale = map.getDrawnMap().getWidth();
        teams = new int[team_quantity];
        Arrays.fill(teams, 0);
        team = generateTeam();
    }

    private GameOptions(GameOptions gameOptions, int team) {
        this.map = gameOptions.getMap();
        this.hp = gameOptions.getHp();
        this.ricochetAble = gameOptions.isRicochetAble();
        this.DEBUG = gameOptions.isDEBUG();
        this.startCoordinatesScale = gameOptions.getStartCoordinatesScale();
        this.team = team;
        this.teams = null;
        startCoordinates = map.getStartCoordinates();
    }

    public GameOptions getVersionForAnotherPlayer() {
        return new GameOptions(this, generateTeam());
    }

    private int generateTeam() {
        int min_quantity = teams[0];
        for (int i = 1; i < teams.length; i++) {
            if (teams[i] < min_quantity)
                min_quantity = teams[i];
        }
        for (int i = 0; i < teams.length; i++) {
            if (teams[i] == min_quantity) {
                teams[i]++;
                return i;
            }
        }
        return -1;
    }

    public Map getMap() {
        return map;
    }

    public int getTeam() {
        return team;
    }

    public Point getStartCoordinates() {
        return startCoordinates;
    }

    public int getStartCoordinatesScale() {
        return startCoordinatesScale;
    }

    public int getHp() {
        return hp;
    }

    public boolean isRicochetAble() {
        return ricochetAble;
    }

    public boolean isDEBUG() {
        return DEBUG;
    }
}
