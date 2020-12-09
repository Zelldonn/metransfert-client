package com.metransfert.client.gui;

import com.metransfert.client.Channel;
import com.metransfert.client.transaction.TransactionResult;
import com.metransfert.client.transaction.TransferListener;
import com.metransfert.client.transaction.UploadInfoResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadTab extends JPanel implements ActionListener{
    private JButton openPathChooserButton;

    private JTextField pathTextField;

    private JButton uploadButton;

    private UploadPanel transaction;


    private float oldTime;

    private JPanel panel;

    private Channel channel;

    public UploadTab(){
        channel = new Channel();
        initComponents();
    }

    public void initComponents(){
        panel = this;

        //parent.setLayout(new GridBagLayout());
        openPathChooserButton = new JButton("Choose file");
        openPathChooserButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int i = fc.showOpenDialog(panel);
            if(i == JFileChooser.APPROVE_OPTION){
                Path filepath = fc.getSelectedFile().toPath();

                pathTextField.setText(filepath.toString());
            }
        });

        pathTextField = new JTextField("E:/Téléchargements/c.c", 20);
        pathTextField.setDragEnabled(true);

        uploadButton = new JButton("Generate ID");
        uploadButton.addActionListener(this);

        this.add(openPathChooserButton);
        this.add(pathTextField);
        this.add(uploadButton);
    }

    private void refreshPanel(){
        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadButton) {
            if (isFile(pathTextField.getText())) {
                transaction = new UploadPanel();
                panel.add(transaction);
                refreshPanel();

                Path p = Paths.get(pathTextField.getText());
                oldTime = System.nanoTime();
                channel.upload(p, new TransferListener() {
                    @Override
                    public void onTransactionStart() {
                        String fileName = Paths.get(pathTextField.getText()).getFileName().toString();
                        transaction.getFileNameLabel().setText("File : " +fileName);
                        pathTextField.setText("Let's upload again !");
                    }

                    @Override
                    public void onTransactionFinish(TransactionResult result) {
                        UploadInfoResult uploadResult = (UploadInfoResult) result;

                        transaction.getUploadIDTextField().setVisible(true);
                        transaction.getClipboardButton().setVisible(true);
                        transaction.getProgressBar().setVisible(false);

                        String ID = uploadResult.id;
                        transaction.getUploadIDTextField().setText(ID);

                        transaction.getFileNameLabel().setVisible(true);

                        refreshPanel();
                    }

                    @Override
                    public void onTransferUpdate(Info info) {
                        int percentage = (int) (((double) info.transferredBytes / (double) info.expectedBytes) * 100);
                        transaction.setProgressBarValue(percentage);
                        transaction.getProgressBar().setString("Uploading... " + percentage + " %");

                        //float throughput = getThroughput(info.transferredBytes, info.oldTransferredBytes);

                        //l_throughput.setText(byte2Readable(throughput) + "/s");
                    }
                });
            }
        }
    }
    private boolean isFile(String path){
        File file = new File(path);
        return file.isFile();
    }
}
