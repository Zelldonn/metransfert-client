package com.metransfert.client.gui;

import com.metransfert.client.transaction.TransferListener;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class UploadPanel extends JPanel {
    private JProgressBar progressBar;

    private JTextField uploadIDTextField;

    private JButton clipboardButton;

    private JLabel fileNameLabel;

    private long throughput, lastNano;

    private JButton stopButton;

    public UploadPanel(){
        lastNano = System.nanoTime();
        throughput = 0L;
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
        clipboardButton.addActionListener(e ->{
            copyToClipBoard(uploadIDTextField.getText());
        });
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

        fileNameLabel = new JLabel();
        fileNameLabel.setVisible(false);

        /*stopButton = new JButton("STOP");
        stopButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //Cancel download
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });*/

        this.add(fileNameLabel);
        this.add(progressBar);
        //this.add(stopButton);
        this.add(uploadIDTextField);
        this.add(clipboardButton);
    }

    public void update(TransferListener.Info info){
        throughput = getThroughput(info.transferredBytes, info.oldTransferredBytes);

        String throughputString = GuiUtils.byte2Readable(throughput) + "/s";

        int percentage = (int)(((double)info.transferredBytes/(double)info.expectedBytes)*100);
        progressBar.setValue(percentage);
        progressBar.setString("Uploading... " +percentage+" % " + "("+throughputString+")");
    }

    private long getThroughput(int currentBytes, int lastBytes){
        long currentNano = System.nanoTime();
        long dB = (long)currentBytes - (long)lastBytes;
        long dt = currentNano - lastNano;
        long dt_s = dt/1_000_000_000L;
        if(dB != 0 && dt_s != 0)
            throughput = dB/dt_s;

        lastNano = currentNano;

        return throughput;
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
