package com.metransfert.client.gui;

import javax.swing.*;
import java.awt.*;

public class DownloadPanel extends JPanel{
    private JProgressBar progressBar;

    private JLabel DownloadResult;


    public DownloadPanel(){
        initComponent();
    }

    public void initComponent(){
        progressBar = new JProgressBar();
        progressBar.setVisible(true);
        progressBar.setStringPainted(true);

        this.add(progressBar);

    }

}
