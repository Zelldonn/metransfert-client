package com.metransfert.client.gui.download.tab;

import com.metransfert.client.gui.download.panel.DownloadPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DownloadTab extends JPanel implements ActionListener{
    ArrayList<DownloadTabListener> listeners = new ArrayList<>();

    public void addDownloadTabListeners(DownloadTabListener l){
        listeners.add(l);
    }

    private JTextField pathTextField, idTextField;

    private JButton downloadButton, pathChooserButton;

    private JLabel fileInfoLabel;

    private boolean pathChooserClicked = false;

    private ArrayList<DownloadPanel> downloadPanels = new ArrayList<>();

    public DownloadTab(){
        initComponents();
    }

    public void initComponents(){
        String home = System.getProperty("user.home");
        Path path = Paths.get(home + "/Downloads/");

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
        idTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                for(DownloadTabListener l : listeners){
                    l.onIDTextFieldChanged();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                for(DownloadTabListener l : listeners){
                    l.onIDTextFieldChanged();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        pathTextField = new JTextField(path.toString(), 12);
        pathTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    Path p = Paths.get(pathTextField.getText());
                    if(p.toFile().isDirectory() && !pathChooserClicked){
                        for(DownloadTabListener l : listeners){
                            l.onPathTextFieldChanged(p);
                        }
                    }
                } catch (InvalidPathException ignored){
                    System.err.println("invalid path");
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    Path p = Paths.get(pathTextField.getText());
                    if(p.toFile().isDirectory() && !pathChooserClicked){
                        for(DownloadTabListener l : listeners){
                            l.onPathTextFieldChanged(p);
                        }
                    }
                } catch (InvalidPathException ignored){
                    System.err.println("invalid path");
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        pathChooserButton = new JButton("Set save directory");
        pathChooserButton.addActionListener(this);

        downloadButton = new JButton("Show");
        downloadButton.addActionListener(this);


        this.add(pathChooserButton);
        this.add(pathTextField);
        this.add(idTextField);
        this.add(downloadButton);
        this.add(fileInfoLabel);
    }

    public void refreshPanel(){
        revalidate();
        repaint();
    }

    public void setFileInfoLabel(String text){
        fileInfoLabel.setText(text);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == pathChooserButton){
            pathChooserClicked = true;
            for(DownloadTabListener l : listeners)
                l.onPathChooserClicked();
        }
        if(e.getSource() == downloadButton){
            for(DownloadTabListener l : listeners)
                l.onDownloadButtonClicked();
        }
    }

    public void setPathTextField(String text) {
        pathTextField.setText(text);
        pathChooserClicked = false;
    }
    public String getIdTextField() {
        return idTextField.getText();
    }

    public void setFileInfoLabelVisible(boolean b){
        fileInfoLabel.setVisible(b);
    }

    public void setDownloadButton(String text) {
        this.downloadButton.setText(text);
    }

    public void setIdTextField(String s) {
        idTextField.setText(s);
    }
}
