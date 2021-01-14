package com.metransfert.client.gui;

public interface GUIListener {
    void onRefreshButtonClicked(String ip, String port);
    void onGuiClosed();
}
