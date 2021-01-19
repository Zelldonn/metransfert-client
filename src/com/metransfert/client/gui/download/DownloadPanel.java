package com.metransfert.client.gui.download;

import com.metransfert.client.gui.TransferPanel;
import com.metransfert.client.gui.upload.PanelListener;
import com.metransfert.client.transactionhandlers.TransferInfo;
import com.metransfert.client.utils.Gui;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.Path;
import java.util.ArrayList;

public class DownloadPanel extends TransferPanel {
    ArrayList<DownloadPanelListener> listeners = new ArrayList<>();

    public void addDownloadPanelListeners(DownloadPanelListener l){
        listeners.add(l);
    }

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
        openDownloadPathButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                for(DownloadPanelListener l : listeners){
                    l.onOpenDownloadPathClicked();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        this.add(fileInfoLabel);
        fileInfoLabel.setVisible(false);
        this.add(progressBar);

        this.add(pauseButton);
        this.add(stopButton);
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
