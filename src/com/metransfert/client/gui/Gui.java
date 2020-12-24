package com.metransfert.client.gui;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame{

    private UploadTab uploadTab;

    public GUI(){

        //---------Define Frame properties---------\\
        this.setTitle("MeTransfer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(550,350);
        ImageIcon img = new ImageIcon("img/logo.png");
        setIconImage(img.getImage());

        initComponents();
    }

    private void initComponents(){

        //Move focus to the next item (Avoid first button to be focused)
        setFocusable(true);

        //-------Creating the MenuBar and adding components------\\
        JMenuBar menuBar = new JMenuBar();
        JMenu serverMenu = new JMenu("Server");
        JMenu clientMenu = new JMenu("Client");
        JMenuItem changeAddressMenuItem = new JMenuItem("Change address");
        JMenu selectServerMenu = new JMenu("Select server");
        JMenuItem editServerMenuItem = new JMenuItem("Edit server addresses");
        JLabel connectionStatusLabel = new JLabel("Not Connected");
        JMenu UI_themeMenu = new JMenu("Change theme");
        JMenuItem UI_LightThemeMenuItem = new JMenuItem("Light");
        JMenuItem UI_DarkThemeMenuItem = new JMenuItem("Dark");

        connectionStatusLabel.setForeground(Color.GRAY);
        connectionStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        ImageIcon icon = new ImageIcon("img/refresh.png");
        Image img_ = icon.getImage() ;
        Image new_img = img_.getScaledInstance( 14, 14,  java.awt.Image.SCALE_SMOOTH) ;
        icon = new ImageIcon( new_img );
        JButton pingButton = new JButton(icon);

        pingButton.setFocusable(false);
        setButtonBorder(pingButton, false);

        serverMenu.add(changeAddressMenuItem);
        serverMenu.add(selectServerMenu);
        UI_themeMenu.add(UI_DarkThemeMenuItem);
        UI_themeMenu.add(UI_LightThemeMenuItem);
        clientMenu.add(UI_themeMenu);
        clientMenu.add(editServerMenuItem);

        menuBar.add(serverMenu);
        menuBar.add(clientMenu);
        menuBar.add(connectionStatusLabel);
        menuBar.add(pingButton);
        setJMenuBar(menuBar);

        //Drag n Drop box ?

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(1);
        uploadTab = new UploadTab();
        tabbedPane.addTab("Upload", uploadTab);
        tabbedPane.addTab("Download", new DownloadTab());
        tabbedPane.setPreferredSize(this.getSize());

        setContentPane(tabbedPane);
    }

    public void setButtonBorder(JButton c, Boolean state){
        c.setBorderPainted(state);
        c.setContentAreaFilled(state);
        c.setFocusPainted(state);
        c.setOpaque(state);
    }

    public UploadTab getUploadTab() {
        return uploadTab;
    }
}