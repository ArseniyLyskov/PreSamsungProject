package com.example.presamsungproject;

import com.example.presamsungproject.ConnectionObjects.MessagesReceiver;

public class ReceiverStorage {
    private static MessagesReceiver messagesReceiver;

    public static MessagesReceiver getMessagesReceiver() {
        return messagesReceiver;
    }

    public static void setMessagesReceiver(MessagesReceiver messagesReceiver) {
        ReceiverStorage.messagesReceiver = messagesReceiver;
    }
}
