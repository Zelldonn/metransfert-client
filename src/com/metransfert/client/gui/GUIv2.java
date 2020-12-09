package com.metransfert.client.gui;

import javax.swing.*;
import java.awt.*;

public class GUIv2 extends JFrame {
    JTabbedPane tabbedPane;
    public GUIv2(){

        initComponents();
        setTitle("MeTransfer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon img = new ImageIcon("img/logo.png");

        setIconImage(img.getImage());
    }

    private void initComponents(){
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu();
        JMenuItem changeAddressMenuItem = new JMenuItem();

        fileMenu.setText("Server");
        changeAddressMenuItem.setText("Change server address");
        changeAddressMenuItem.setAccelerator(KeyStroke.getKeyStroke(78, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        changeAddressMenuItem.setMnemonic('N');
        fileMenu.add(changeAddressMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        UploadPanel uploadPanel = new UploadPanel();

        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setTabLayoutPolicy(1);
        this.tabbedPane.addTab("Upload",uploadPanel);
        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(new FlowLayout());

        contentPanel.add(this.tabbedPane, "cell 0 0");

        setContentPane(contentPanel);

    }
}
