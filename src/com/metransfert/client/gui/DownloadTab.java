package com.metransfert.client.gui;

import com.metransfert.client.Channel;
import com.metransfert.client.controller.ClientController;
import com.metransfert.client.transactionhandlers.RequestInfoResult;
import com.metransfert.client.transactionhandlers.TransactionListener;
import com.metransfert.client.transactionhandlers.TransactionResult;
import com.metransfert.client.transactionhandlers.TransferListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DownloadTab extends JPanel {
    private JTextField idTextField;

    private JTextField saveDirectoryTextField;

    private DownloadButton downloadButton;

    private final JPanel panel;

    private Path savePathDirectory;

    private JLabel fileInfoLabel;

    private ArrayList<Channel> channels = new ArrayList<>();

    private ArrayList<DownloadPanel> downloadPanels = new ArrayList<>();

    public DownloadTab(){
        panel = this;
        initComponents();
    }

    public void initComponents(){
        String home = System.getProperty("user.home");
        Path path = Paths.get(home + "/Downloads/");
        savePathDirectory = path;

        fileInfoLabel = new JLabel("");
        fileInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        idTextField = new JTextField(">>ID<<", 5);
        idTextField.setHorizontalAlignment(JTextField.CENTER);
        idTextField.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        idTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(idTextField.getText().contains(">>ID<<"))
                    idTextField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(idTextField.getText().isEmpty())
                    idTextField.setText(">>ID<<");
            }
        });

        saveDirectoryTextField = new JTextField(path.toString(), 12);
        saveDirectoryTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                savePathDirectory =  Paths.get(saveDirectoryTextField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                savePathDirectory =  Paths.get(saveDirectoryTextField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        JButton saveDirectoryButton = new JButton("Set save location");
        saveDirectoryButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int i = fc.showOpenDialog(panel);
            if(i == JFileChooser.APPROVE_OPTION) {
                savePathDirectory = fc.getSelectedFile().toPath();

                saveDirectoryTextField.setText(savePathDirectory.toString());
            }
        });

        downloadButton = new DownloadButton("Show");
        downloadButton.addActionListener(e -> {
            ClientController g = ClientController.getInstance();
            Channel c = new Channel(g.getIp(), g.getPort());
            channels.add(c);

            if(!downloadButton.canDownload){
                String id = idTextField.getText();

                c.requestInfo(id, new TransactionListener() {
                    @Override
                    public void onTransactionStart() {

                    }

                    @Override
                    public void onTransactionFinish(TransactionResult result) {
                        RequestInfoResult infoResult = (RequestInfoResult)result;

                        String display;
                        if(!infoResult.id_exists)
                            display = "File does not exist";
                        else{
                            display = infoResult.fileName + " | " + GuiUtils.byte2Readable(infoResult.fileSize);
                            downloadButton.setDownload(true);
                            downloadButton.setFileName(infoResult.fileName);
                        }
                        fileInfoLabel.setText(display);
                    }
                });
            }else{
                if(!isDirectory(savePathDirectory)){
                    saveDirectoryTextField.setText("Not a directory !");
                }else{
                    String temp = savePathDirectory.toString() + "/" + downloadButton.fileName;
                    Path downloadPath = Paths.get(temp);

                    DownloadPanel p = new DownloadPanel(downloadPath);
                    downloadPanels.add(p);
                    panel.add(p);
                    refreshPanel();

                    String id = idTextField.getText();
                    idTextField.setText("");

                    try {
                        c.download(id, downloadPath, new TransferListener() {
                            @Override
                            public void onTransferUpdate(Info info) {
                                p.update(info);
                            }

                            @Override
                            public void onTransactionStart() {
                                downloadButton.setDownload(false);
                                p.getFileInfoLabel().setText(fileInfoLabel.getText());
                                p.getFileInfoLabel().setVisible(true);
                                fileInfoLabel.setText("");
                            }

                            @Override
                            public void onTransactionFinish(TransactionResult result) {
                                p.getProgressBar().setVisible(false);
                                p.getFileInfoLabel().setVisible(false);
                                p.getDownloadResultLabel().setText("File saved in : " + p.getFileLocationPath().toString());
                            }
                        });
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }


            }

        });

        panel.add(saveDirectoryButton);
        panel.add(saveDirectoryTextField);
        panel.add(idTextField);
        panel.add(downloadButton);
        panel.add(fileInfoLabel);
    }

    private void refreshPanel(){
        revalidate();
        repaint();
    }

    private boolean isDirectory(Path path){
        File file = new File(path.toString());
        return file.isDirectory();
    }

}
