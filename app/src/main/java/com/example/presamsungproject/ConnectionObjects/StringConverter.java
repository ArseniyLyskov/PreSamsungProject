package com.example.presamsungproject.ConnectionObjects;

import java.util.TreeSet;

public class StringConverter {
    public static String SEND_READY_TO_BATTLE = "SEND_READY_TO_BATTLE";
    public static String SEND_PARTICIPANTS_LIST = "SEND_PARTICIPANTS_LIST";

    public static String sendReadyToBattle(String name) {
        return SEND_READY_TO_BATTLE + " " + name;
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

}
