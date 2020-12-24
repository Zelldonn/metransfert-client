package com.metransfert.client.gui;

import com.metransfert.client.Channel;
import com.metransfert.client.PathUtils;
import com.metransfert.client.controller.ClientController;
import com.metransfert.client.transactionhandlers.TransactionResult;
import com.metransfert.client.transactionhandlers.TransferListener;
import com.metransfert.client.transactionhandlers.UploadInfoResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadTab extends JPanel implements ActionListener{

    public ArrayList<UploadTabListener> listeners = new ArrayList<UploadTabListener>();

    public void addUploadTabListener(UploadTabListener newListener){
        listeners.add(newListener);
    }

    private JButton openPathChooserButton;

    private JButton newOpenPathChooserButton;

    private JTextField pathTextField;

    private JLabel fileInfoLabel;

    private JButton uploadButton;

    private JPanel panel;

    private Path directoryPath, filePath;

    public UploadTab(){
        String home = System.getProperty("user.home");
        directoryPath = Paths.get(home + "/Downloads/");
        filePath = null;
        initComponents();
    }

    public void initComponents(){
        panel = this;

        fileInfoLabel = new JLabel();
        fileInfoLabel.setVisible(false);

        openPathChooserButton = new JButton("Choose file");
        openPathChooserButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();

            File dir = directoryPath.toFile();
            if(dir != null)
                fc.setCurrentDirectory(dir);


            int i = fc.showOpenDialog(panel);
            if(i == JFileChooser.APPROVE_OPTION){
                filePath = fc.getSelectedFile().toPath();

                pathTextField.setText(filePath.toString());
                fileInfoLabel.setVisible(true);

                fileInfoLabel.setText(filePath.getFileName() +" | Size : " + GuiUtils.byte2Readable(filePath.toFile().length()));
                fileInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

                directoryPath = filePath.getParent();
            }

        });

        newOpenPathChooserButton = new JButton("Choose file (No limit)");
        newOpenPathChooserButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();

            File dir = directoryPath.toFile();
            if(dir != null)
                fc.setCurrentDirectory(dir);

            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int i = fc.showOpenDialog(panel);
            if(i == JFileChooser.APPROVE_OPTION){
                filePath = fc.getSelectedFile().toPath();

                pathTextField.setText(filePath.toString());
                fileInfoLabel.setVisible(true);

                fileInfoLabel.setText(filePath.getFileName() +" | Size : " + GuiUtils.byte2Readable(filePath.toFile().length()));
                fileInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

                directoryPath = filePath.getParent();
            }
        });

        pathTextField = new JTextField("", 20);
        pathTextField.setDragEnabled(true);
        pathTextField.setText(directoryPath.toString());

        uploadButton = new JButton("Generate ID");
        uploadButton.addActionListener(this);

        this.add(openPathChooserButton);
        this.add(newOpenPathChooserButton);
        this.add(pathTextField);
        this.add(uploadButton);
        this.add(fileInfoLabel);
    }

    private void refreshPanel(){
        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadButton) {
            for(UploadTabListener listener : listeners) {
                listener.onUploadButtonClicked();
            }
        }
    }
}
