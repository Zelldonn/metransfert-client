package com.metransfert.client.gui.download;

import javax.swing.*;

public class DownloadButton extends JButton {
    Boolean canDownload;
    String fileName;

    DownloadButton(String title){
        this.setText(title);
        setDownload(false);
        //Don't forget to add actionListener
    }

    public void setDownload(Boolean canDownload) {
        this.canDownload = canDownload;
        if(canDownload){
            this.setText("Download");
        }else{
            this.setText("Show");
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
