package com.metransfert.client.gui.download.panel;

import com.metransfert.client.gui.common.TransferPanel;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.Path;


public class DownloadPanel extends TransferPanel  {

    private JLabel downloadResultLabel;

    private final Path fileLocationPath;

    private JLabel fileInfoLabel;

    public JButton getOpenDownloadPathButton() {
        return openDownloadPathButton;
    }

    private JButton openDownloadPathButton;

    public DownloadPanel(Path p){
        this.fileLocationPath = p;
        initComponent();
    }

    public void initComponent(){
        downloadResultLabel = new JLabel();
        progressBar = new JProgressBar();
        progressBar.setVisible(true);
        progressBar.setStringPainted(true);

        fileInfoLabel =  new JLabel("");

        openDownloadPathButton = new JButton("Open");
        openDownloadPathButton.setVisible(false);

        this.add(fileInfoLabel);
        fileInfoLabel.setVisible(false);
        this.add(progressBar);

        //this.add(pauseButton);
        //this.add(stopButton);
        this.add(closeButton);

        this.add(downloadResultLabel);

        this.add(openDownloadPathButton);
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getDownloadResultLabel() {
        return downloadResultLabel;
    }

    public Path getFileLocationPath(){
        return fileLocationPath;
    }

    public JLabel getFileInfoLabel(){
        return fileInfoLabel;
    }

}
