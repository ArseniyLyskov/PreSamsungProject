package com.example.presamsungproject.ConnectionObjects;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import com.example.presamsungproject.Game;
import com.example.presamsungproject.GameObjects.Bullet;
import com.example.presamsungproject.GameObjects.Tank;
import com.example.presamsungproject.GameObjects.TankSight;
import com.example.presamsungproject.R;

import java.util.HashSet;
import java.util.TreeSet;

public class StringConverter {
    public static String SEND_READY_TO_BATTLE = "SEND_READY_TO_BATTLE";
    public static String SEND_PARTICIPANTS_LIST = "SEND_PARTICIPANTS_LIST";

    public static String sendReadyToBattle(String name, int port) {
        return SEND_READY_TO_BATTLE + " " + name + " " + port;
    }

    public static String createParticipantsList(TreeSet<String> participantsList) {
        String result = SEND_PARTICIPANTS_LIST + " ";
        String[] temp = new String[participantsList.size()];
        participantsList.toArray(temp);
        for (String s : temp) {
            result += s + " ";
        }
        return result;
    }

    public static String getParticipantsList(String participantsList) {
        String result = "";
        String[] temp = participantsList.split(" ");
        for (int i = 1; i < temp.length; i++) {
            result += temp[i] + "\n";
        }
        return result;
    }

    public static Tank getTank(String received) {
        String[] split = received.split(" ");
        HashSet<Bullet> bullets = new HashSet<>();
        for (int i = 0; i < (split.length - 11) / 2 - 1; i++) {
            Bullet b = new Bullet(Double.parseDouble(split[12 + i]), Double.parseDouble(split[13 +i]));
            bullets.add(b);
        }
        Tank tank = new Tank(Long.parseLong(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3]),
                Double.parseDouble(split[4]),
                Double.parseDouble(split[5]),
                split[6],
                new TankSight(Boolean.parseBoolean(split[7]),
                        Double.parseDouble(split[8]),
                        Double.parseDouble(split[9]),
                        Double.parseDouble(split[10]),
                        Double.parseDouble(split[11])),
                BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.red_hp),
                BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.red_tp),
                new Paint(),
                bullets);
        //TankSight tankSight = new TankSight(received);
        return tank;
    }

}
