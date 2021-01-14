package com.metransfert.client.gui.upload;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class UploadPanel extends JPanel implements ActionListener {

    private ArrayList<UploadPanelListener> listeners = new ArrayList<UploadPanelListener>();

    public void addUploadPanelListener(UploadPanelListener newListener){
        listeners.add(newListener);
    }

    private JProgressBar progressBar;

    public void setUploadIDTextField(String text) {
        this.uploadIDTextField.setText(text);
    }

    private JTextField uploadIDTextField;

    private JButton clipboardButton;

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    ImageIcon playIcon, pauseIcon;

    private JButton pauseButton;
    private JButton stopButton;
    private JButton closeButton;

    private JLabel fileInfoLabel;

    public JLabel getFileInfoLabel() {
        return fileInfoLabel;
    }

    public void setFileInfoLabel(String text) {
        this.fileInfoLabel.setText(text);
    }

    public UploadPanel(){
        initComponent();
    }

    public void initComponent(){
        progressBar = new JProgressBar();
        progressBar.setVisible(true);
        progressBar.setStringPainted(true);
        progressBar.setValue(0);

        uploadIDTextField = new JTextField("", 4);
        uploadIDTextField.setHorizontalAlignment(JTextField.CENTER);
        uploadIDTextField.setEditable(false);
        uploadIDTextField.setVisible(false);

        ImageIcon icon = new ImageIcon("img/clipboard.png");
        Image img_ = icon.getImage() ;
        Image new_img = img_.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH) ;
        icon = new ImageIcon( new_img );
        clipboardButton = new JButton(icon);
        clipboardButton.setVisible(false);
        clipboardButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setButtonBorder(clipboardButton, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setButtonBorder(clipboardButton, false);
            }
        });
        setButtonBorder(clipboardButton, false);

        pauseIcon = new ImageIcon("img/pause.png");
        img_ = pauseIcon.getImage() ;
        new_img = img_.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH) ;
        pauseIcon = new ImageIcon( new_img );

        playIcon = new ImageIcon("img/play.png");
        img_ = playIcon.getImage() ;
        new_img = img_.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH) ;
        playIcon = new ImageIcon( new_img );

        pauseButton = new JButton(pauseIcon);
        pauseButton.addActionListener(this);
        stopButton = new JButton("s");
        stopButton.addActionListener(this);

        icon = new ImageIcon("img/close.png");
        img_ = icon.getImage() ;
        new_img = img_.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH) ;
        icon = new ImageIcon( new_img );
        closeButton = new JButton(icon);
        closeButton.addActionListener(this);
        closeButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setButtonBorder(closeButton, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setButtonBorder(closeButton, false);
            }
        });

        fileInfoLabel = new JLabel();
        fileInfoLabel.setVisible(false);

        this.add(fileInfoLabel);
        this.add(progressBar);
        this.add(uploadIDTextField);
        this.add(clipboardButton);
        this.add(pauseButton);
        this.add(stopButton);
        this.add(closeButton);
    }

    public void setButtonBorder(JButton c, Boolean state){
        c.setBorderPainted(state);
        c.setContentAreaFilled(state);
        c.setFocusPainted(state);
        c.setOpaque(state);
    }

    public void changeRunningStateIcon(boolean isRunning){
        if(isRunning)
            pauseButton.setIcon(pauseIcon);
        else pauseButton.setIcon(playIcon);
    }

    public void setProgressBarValue(int n){
        progressBar.setValue(n);
    }
    public void setProgressBarText(String text){
        progressBar.setString(text);
    }

    public JProgressBar getProgressBar(){
        return progressBar;
    }

    public JTextField getUploadIDTextField() {
        return uploadIDTextField;
    }

    public JButton getClipboardButton() {
        return clipboardButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            for(UploadPanelListener listener : listeners) {
                listener.onClosedButtonClicked();
            }
        }
        else if(e.getSource() == pauseButton){
            for(UploadPanelListener listener : listeners) {
                listener.onPauseButtonClicked();
            }
        }else if(e.getSource() == stopButton){
            for(UploadPanelListener listener : listeners) {
                listener.onStopButtonClicked();
            }
        }
    }

}
