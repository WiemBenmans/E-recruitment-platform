package com.enit.Erecruitement;

import java.util.Observable;

public class NotificationSender extends Observable {
    private String message;

    public void setMessage(String message) {
        this.message = message;
        setChanged();
        notifyObservers();
    }

    public String getMessage() {
        return message;
    }
}
