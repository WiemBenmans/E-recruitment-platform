package com.enit.Erecruitement;

import org.apache.commons.io.input.ObservableInputStream;

import java.util.Observable;
import java.util.Observer;

public class NotificationReceiver implements Observer {
    public void update(Observable obj, Object arg) {
        if (obj instanceof NotificationSender) {
            NotificationSender sender = (NotificationSender) obj;
            System.out.println("Received notification: " + sender.getMessage());
        }
    }
}