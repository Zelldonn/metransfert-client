package com.metransfert.client.gui.upload;

import com.metransfert.client.gui.TransferPanel;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class UploadPanel extends TransferPanel {

    private JTextField uploadIDTextField;

    private JButton clipboardButton;

    private JLabel fileInfoLabel;

    public JLabel getFileInfoLabel() {
        return fileInfoLabel;
    }

    public void setUploadIDTextField(String text) {
        this.uploadIDTextField.setText(text);
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
        img_ = icon.getImage() ;
        scaledImage = img_.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH) ;
        icon = new ImageIcon(scaledImage);
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

    public void changeRunningStateIcon(boolean isRunning){
        if(isRunning)
            pauseButton.setIcon(pauseIcon);
        else pauseButton.setIcon(playIcon);
    }

    public JTextField getUploadIDTextField() {
        return uploadIDTextField;
    }

    public JButton getClipboardButton() {
        return clipboardButton;
    }
}
