package com.metransfert.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class UploadPanel extends JPanel {
    private JProgressBar progressBar;

    private JTextField uploadIDTextField;

    private JButton clipboardButton;

    private JLabel fileNameLabel;

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

        Icon icon = new ImageIcon("img/clipboard.png");
        Image img_ = ((ImageIcon) icon).getImage() ;
        Image new_img = img_.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH) ;
        icon = new ImageIcon( new_img );
        clipboardButton = new JButton(icon);
        clipboardButton.setVisible(false);
        clipboardButton.addActionListener(e ->{
            copyToClipBoard(uploadIDTextField.getText());
        });

        fileNameLabel = new JLabel();
        fileNameLabel.setVisible(false);

        this.add(fileNameLabel);
        this.add(progressBar);
        this.add(uploadIDTextField);
        this.add(clipboardButton);
    }

    public void setProgressBarValue(int n){
        progressBar.setValue(n);
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

    public JLabel getFileNameLabel() {
        return fileNameLabel;
    }
    public static void copyToClipBoard(String text){
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
