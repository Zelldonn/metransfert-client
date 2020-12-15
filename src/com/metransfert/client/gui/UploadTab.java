package com.metransfert.client.gui;

import com.metransfert.client.Channel;
import com.metransfert.client.transaction.TransactionResult;
import com.metransfert.client.transaction.TransferListener;
import com.metransfert.client.transaction.UploadInfoResult;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
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

    private JButton openPathChooserButton;

    private JTextField pathTextField;

    private JLabel fileInfoLabel;

    private JButton uploadButton;

    private float oldTime;

    private JPanel panel;

    private Path directoryPath, filePath;

    private ArrayList<Channel> channels = new ArrayList<>();

    private ArrayList<UploadPanel> uploadPanels = new ArrayList<>();

    private HashMap<Channel, UploadPanel> maps = new HashMap<>();

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

        pathTextField = new JTextField("", 20);
        pathTextField.setDragEnabled(true);
        pathTextField.setText(directoryPath.toString());

        uploadButton = new JButton("Generate ID");
        uploadButton.addActionListener(this);

        this.add(openPathChooserButton);
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
            boolean isTextFieldAFile = Paths.get(pathTextField.getText()).toFile().isFile();
            if(filePath == null || !isTextFieldAFile){
                pathTextField.setText("Error: The file specified does not exist");
                fileInfoLabel.setVisible(false);
            }
            else {
                Gui g = Gui.getInstance();
                Path p = Paths.get(pathTextField.getText());
                oldTime = System.nanoTime();

                UploadPanel up = new UploadPanel();
                uploadPanels.add(up);
                panel.add(up);
                refreshPanel();

                Channel c = new Channel(g.getIp(), g.getPort());
                channels.add(c);

                maps.put(c, up);
                c.upload(p, new TransferListener() {
                    @Override
                    public void onTransactionStart() {
                        String fileName = Paths.get(pathTextField.getText()).getFileName().toString();
                        up.getFileNameLabel().setText("File : " +fileName +" | Size :" + GuiUtils.byte2Readable(filePath.toFile().length()));
                        up.getFileNameLabel().setVisible(true);
                        pathTextField.setText("Let's upload again !");
                        filePath = null;
                        fileInfoLabel.setVisible(false);
                    }

                    @Override
                    public void onTransactionFinish(TransactionResult result) {
                        UploadInfoResult uploadResult = (UploadInfoResult) result;

                        up.getUploadIDTextField().setVisible(true);
                        up.getClipboardButton().setVisible(true);
                        up.getProgressBar().setVisible(false);

                        String ID = uploadResult.id;
                        up.getUploadIDTextField().setText(ID);

                        up.getFileNameLabel().setVisible(true);

                        refreshPanel();
                    }

                    @Override
                    public void onTransferUpdate(Info info) {
                        up.update(info);
                    }
                });
            }
        }
    }
}
