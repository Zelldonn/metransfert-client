package com.metransfert.client.gui;

import com.metransfert.client.gui.upload.PanelListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class TransferPanel extends JPanel implements ActionListener {

    protected ArrayList<PanelListener> listeners = new ArrayList<PanelListener>();

    public void addPanelListener(PanelListener newListener){
        listeners.add(newListener);
    }

    protected JProgressBar progressBar;

    protected ImageIcon playIcon, pauseIcon, stopIcon;

    protected Image img_, scaledImage;

    protected JButton pauseButton, stopButton, closeButton;

    public TransferPanel(){
        pauseIcon = new ImageIcon("img/pause.png");
        img_ = pauseIcon.getImage() ;
        scaledImage = img_.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH) ;
        pauseIcon = new ImageIcon(scaledImage);

        playIcon = new ImageIcon("img/play.png");
        img_ = playIcon.getImage() ;
        scaledImage = img_.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH) ;
        playIcon = new ImageIcon(scaledImage);

        pauseButton = new JButton(pauseIcon);
        pauseButton.addActionListener(this);
        stopButton = new JButton("s");
        stopButton.addActionListener(this);

        stopIcon = new ImageIcon("img/close.png");
        img_ = stopIcon.getImage() ;
        scaledImage = img_.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH) ;
        stopIcon = new ImageIcon(scaledImage);
        closeButton = new JButton(stopIcon);
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
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public void setButtonBorder(JButton c, Boolean state){
        c.setBorderPainted(state);
        c.setContentAreaFilled(state);
        c.setFocusPainted(state);
        c.setOpaque(state);
    }

    public void setProgressBarValue(int n){
        progressBar.setValue(n);
    }

    public void setProgressBarText(String text) {
        progressBar.setString(text);
    }

    public JProgressBar getProgressBar(){
        return progressBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            for(PanelListener listener : listeners) {
                listener.onClosedButtonClicked();
            }
        }
        else if(e.getSource() == pauseButton){
            for(PanelListener listener : listeners) {
                listener.onPauseButtonClicked();
            }
        }else if(e.getSource() == stopButton){
            for(PanelListener listener : listeners) {
                listener.onStopButtonClicked();
            }
        }
    }
}
