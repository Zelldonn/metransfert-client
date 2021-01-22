package com.metransfert.client.gui.upload.tab;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadTab extends JPanel implements ActionListener{

    private ArrayList<UploadTabListener> listeners = new ArrayList<UploadTabListener>();

    public void addUploadTabListener(UploadTabListener newListener){
        listeners.add(newListener);
    }

    private JButton pathChooserButton;

    public JButton getPathChooserButton() {
        return pathChooserButton;
    }

    public void setPathChooserButton(JButton pathChooserButton) {
        this.pathChooserButton = pathChooserButton;
    }

    public JTextField getPathTextField() {
        return pathTextField;
    }

    public void setPathTextField(String text) {
        this.pathTextField.setText(text);
    }

    public JLabel getFileInfoLabel() {
        return fileInfoLabel;
    }

    public void setFileInfoLabel(String text) {
        this.fileInfoLabel.setText(text);
    }

    private JTextField pathTextField;

    private JLabel fileInfoLabel;

    public JButton getUploadButton() {
        return uploadButton;
    }

    private JButton uploadButton;

    public UploadTab(){
        initComponents();
    }

    public void initComponents(){

        fileInfoLabel = new JLabel();
        fileInfoLabel.setVisible(false);

        pathChooserButton = new JButton("Choose file");
        pathChooserButton.addActionListener(this);

        pathTextField = new JTextField("", 20);
        pathTextField.setDropTarget(new DropTarget(){
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    File[] fileList = new File[droppedFiles.size()];
                    for (int i = 0; i < droppedFiles.size(); i++) {
                        fileList[i] = droppedFiles.get(i);
                    }
                    for(UploadTabListener listener :listeners) {
                        listener.onFilesDragged(fileList);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        uploadButton = new JButton("Upload");
        uploadButton.addActionListener(this);

        this.add(pathChooserButton);
        this.add(pathTextField);
        this.add(uploadButton);
        this.add(fileInfoLabel);
    }

    public void refreshPanel(){
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
        else if(e.getSource() == pathChooserButton){
            for(UploadTabListener listener : listeners) {
                listener.openPathChooserButtonClicked();
            }
        }
    }
}
