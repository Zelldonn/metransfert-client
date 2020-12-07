package com.metransfert.client;

enum Status{
    TRYING,
    CONNECTED,
    DISCONNECTED,
    TIMEOUT
}

public interface StatusChangeListener {
    void onStatusChange(Status status);
}
