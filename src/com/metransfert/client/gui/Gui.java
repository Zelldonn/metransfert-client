package com.metransfert.client.gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.metransfert.client.gui.common.Status;
import com.metransfert.client.gui.download.tab.DownloadTab;
import com.metransfert.client.gui.upload.tab.UploadTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Gui extends JFrame{

    ArrayList<GUIListener> GUIListeners = new ArrayList<GUIListener>();

    public void addGuiListener(GUIListener l){
        GUIListeners.add(l);
    }

    private UploadTab uploadTab;

    private  DownloadTab downloadTab;

    private String currentTheme;

    JTabbedPane tabbedPane;

    private boolean isInTray = false;

    public void setConnectionStatusLabel(Status s, String ip, String port) {
        String status;
        if(s == Status.CONNECTED){
            status = "Connected";
            this.connectionStatusLabel.setForeground(new Color(35, 153, 82));
        }else if(s == Status.DISCONNECTED){
            status = "Disconnected";
            this.connectionStatusLabel.setForeground(new Color(194, 28, 28));
        }else if(s == Status.TRYING){
            status = "Trying";
            this.connectionStatusLabel.setForeground(new Color(121, 121, 112));
        }else{
            status = "Timeout";
            this.connectionStatusLabel.setForeground(new Color(255, 206, 31));
        }
        this.connectionStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        this.connectionStatusLabel.setText(status+"  ");
        ipLabel.setText(ip);
        portLabel.setText(port);
    }
    JLabel connectionStatusLabel;

    public JButton getRefreshConnectionButton() {
        return refreshConnectionButton;
    }
    JButton refreshConnectionButton;

    public JTextField getIpLabel() {
        return ipLabel;
    }

    public void setIpLabel(String text) {
        this.ipLabel.setText(text);
    }

    JTextField ipLabel;

    public JTextField getPortLabel() {
        return portLabel;
    }

    public void setPortLabel(String text) {
        this.portLabel.setText(text);
    }

    JTextField portLabel;

    public JMenu getSelectServerMenu() {
        return selectServerMenu;
    }

    public void setSelectServerMenu(JMenu selectServerMenu) {
        this.selectServerMenu = selectServerMenu;
    }

    JMenu selectServerMenu;

    JMenuItem UI_themeMenuItem;

    private TrayIcon trayIcon;
    private SystemTray tray;

    Image traySimple, trayGreen;

    public Gui(){
        //---------Define Frame properties---------\\
        this.setTitle("MeTransfer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(650,350);

        ImageIcon img = new ImageIcon("img/logo.png");
        setIconImage(img.getImage());

        initComponents();
    }

    private void initComponents(){

        //Move focus to the next item (Avoid first button to be focused)
        setFocusable(true);

        //Handling system tray
        this.setDefaultCloseOperation(JFrame.ICONIFIED);

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent windowEvent) {
                setExtendedState(JFrame.ICONIFIED);
            }
        });

        if(SystemTray.isSupported()){
            System.out.println("system tray supported");
            tray=SystemTray.getSystemTray();

            traySimple = Toolkit.getDefaultToolkit().getImage("img/logo.png");
            trayGreen = Toolkit.getDefaultToolkit().getImage("img/logo_green.png");
            ActionListener exitListener= e -> {
                for(GUIListener l : GUIListeners){
                    l.onGuiClosed();
                }
                System.exit(0);
            };
            PopupMenu popup=new PopupMenu();
            MenuItem defaultItem=new MenuItem("Exit");

            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);
            defaultItem=new MenuItem("Open");
            defaultItem.addActionListener(e -> {
                removeFromTray();
            });
            popup.add(defaultItem);
            trayIcon = new TrayIcon(traySimple, "MeTransfer", popup);
            trayIcon.setImageAutoSize(true);

            trayIcon.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                        removeFromTray();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
        }else{
            System.err.println("system tray not supported");
        }
        addWindowStateListener(e -> {
            if(e.getNewState()==ICONIFIED){
                try {
                    isInTray = true;
                    tray.add(trayIcon);
                    setVisible(false);
                    //System.out.println("added to SystemTray");
                } catch (AWTException ex) {
                    //System.err.println("unable to add to tray");
                }
            }
            if(e.getNewState()==7){
                try{
                    isInTray = true;
                    tray.add(trayIcon);
                    setVisible(false);
                    //System.out.println("added to SystemTray");
                }catch(AWTException ex){
                    //System.err.println("unable to add to system tray");
                }
            }
            if(e.getNewState()==MAXIMIZED_BOTH){
                tray.remove(trayIcon);
                setVisible(true);
                trayIcon.setImage(this.traySimple);
                //System.out.println("Tray icon removed");
            }
            if(e.getNewState()==NORMAL){
                tray.remove(trayIcon);
                setVisible(true);
                trayIcon.setImage(this.traySimple);
                //LSystem.out.println("Tray icon removed");
            }
        });

        //-------Creating the MenuBar and adding components------\\
        JMenuBar menuBar = new JMenuBar();
        JMenu serverMenu = new JMenu("Server");
        JMenu clientMenu = new JMenu("Client");
        selectServerMenu = new JMenu("Select server");
        connectionStatusLabel = new JLabel("Not Connected");
        JMenu UI_themeMenu = new JMenu("Change theme");
        UI_themeMenuItem = new JMenuItem("Dark");
        UI_themeMenuItem.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                switchTheme();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        connectionStatusLabel.setForeground(Color.GRAY);
        connectionStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        ipLabel = new JTextField("ip address", 10);
        ipLabel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(ipLabel.getText().equals("ip address"))
                    ipLabel.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        portLabel = new JTextField("port", 3);
        portLabel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(portLabel.getText().equals("port"))
                    portLabel.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        ImageIcon icon = new ImageIcon("img/refresh.png");
        Image img_ = icon.getImage() ;
        Image new_img = img_.getScaledInstance( 14, 14,  java.awt.Image.SCALE_SMOOTH) ;
        icon = new ImageIcon( new_img );
        refreshConnectionButton = new JButton(icon);

        refreshConnectionButton.setFocusable(false);
        setButtonBorder(refreshConnectionButton, false);
        refreshConnectionButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                for(GUIListener l : GUIListeners)
                    l.onRefreshButtonClicked(ipLabel.getText(), portLabel.getText());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setButtonBorder(refreshConnectionButton, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setButtonBorder(refreshConnectionButton, false);
            }
        });

        serverMenu.add(selectServerMenu);
        UI_themeMenu.add(UI_themeMenuItem);

        clientMenu.add(UI_themeMenu);

        menuBar.add(serverMenu);
        menuBar.add(clientMenu);
        menuBar.add(connectionStatusLabel);
        menuBar.add(ipLabel);
        menuBar.add(portLabel);
        menuBar.add(refreshConnectionButton);
        setJMenuBar(menuBar);

        //Drag n Drop box ?

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(1);

        uploadTab = new UploadTab();
        downloadTab = new DownloadTab();
        tabbedPane.addTab("Upload", uploadTab);
        tabbedPane.addTab("Download", downloadTab);

        tabbedPane.setPreferredSize(this.getSize());

        setContentPane(tabbedPane);
    }

    public void setNotificationToTray(){
        trayIcon.setImage(this.trayGreen);
    }

    public void setTrayIconText(String text){
        trayIcon.setToolTip(text);
    }

    private void switchTheme(){
        if(currentTheme.equals("LIGHT")){

            setTheme("DARK");
        }else{
            setTheme("LIGHT");
        }
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void setTheme(String theme) {
        this.currentTheme = theme;
        try {
            if(theme.equals("LIGHT")){
                UI_themeMenuItem.setText("Dark");
                UIManager.setLookAndFeel( new FlatIntelliJLaf());
            }
            if(theme.equals("DARK")){
                UIManager.setLookAndFeel( new FlatDarculaLaf());
                UI_themeMenuItem.setText("Light");
            }

            SwingUtilities.updateComponentTreeUI(this);
            this.revalidate();
            this.repaint();
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
    }

    public String getTheme(){
        return currentTheme;
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

    public DownloadTab getDownloadTab(){ return downloadTab; }

    public void removeFromTray(){
        if(isInTray){
            setVisible(true);
            setExtendedState(JFrame.NORMAL);
            isInTray = false;
            tray.remove(trayIcon);
            trayIcon.setImage(this.traySimple);
        }
    }
}