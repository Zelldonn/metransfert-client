package com.metransfert.client.gui;

import javax.swing.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DownloadTab extends JPanel {
    private JTextField idTextField;

    private JButton saveDirectoryButton;

    private JTextField saveDirectoryTextField;

    private JButton downloadButton;

    private JPanel panel;

    private Path savePathDirectory;

    private ArrayList<DownloadPanel> downloadPanels = new ArrayList<>();


    public DownloadTab(){
        panel = this;
        initComponents();
    }

    public void initComponents(){
        String home = System.getProperty("user.home");
        Path path = Paths.get(home + "/Downloads/");
        savePathDirectory = path;

        idTextField = new JTextField("Past ID here", 7);
        idTextField.setHorizontalAlignment(JTextField.CENTER);
        idTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                idTextField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        idTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(e.getButton() == MouseEvent.BUTTON3)
                    doPop(e);

            }
            private void doPop(MouseEvent e){
                RightClickPopUp menu = new RightClickPopUp();
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        saveDirectoryTextField = new JTextField(path.toString());

        saveDirectoryButton = new JButton("Set save location");
        saveDirectoryButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int i = fc.showOpenDialog(panel);
            if(i == JFileChooser.APPROVE_OPTION) {
                savePathDirectory = fc.getSelectedFile().toPath();

                saveDirectoryTextField.setText(savePathDirectory.toString());
            }
        });

        downloadButton = new JButton("Download");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO : check if directory is valid
                DownloadPanel p = new DownloadPanel();
                downloadPanels.add(p);
                panel.add(p);
                refreshPanel();
            }
        });

        panel.add(saveDirectoryButton);
        panel.add(saveDirectoryTextField);
        panel.add(idTextField);
        panel.add(downloadButton);
    }

    private void refreshPanel(){
        revalidate();
        repaint();
    }
}
