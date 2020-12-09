package com.metransfert.client.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MyDocumentListener implements DocumentListener {
    Gui.RequestInfoButton button;
    public MyDocumentListener(Gui.RequestInfoButton _button){
        button = _button;
    }
    @Override
    public void insertUpdate(DocumentEvent e) {
        button.setDownload(false);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        button.setDownload(false);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
