package com.metransfert.client.gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.metransfert.client.Channel;
import com.metransfert.client.Status;
import com.metransfert.client.transaction.PingListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Gui extends JFrame implements ActionListener{

    private static Gui instance;

    private Channel pingChannel;

    private JMenuItem changeAddressMenuItem;

    private JLabel connectionStatusLabel;

    private String ip = "localhost";

    private int port = 8565;

    private ArrayList<ServerAddress> serverAddresses = new ArrayList<>();

    private static int MAX_SAVED_ADDRESS = 5;

    public Gui(){
        if(instance == null)
            instance = this;
        else throw new RuntimeException("There cannot be more than one instance of MeTransfer");

        //---------Define Frame properties---------\\
        this.setTitle("MeTransfer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(550,350);
        ImageIcon img = new ImageIcon("img/logo.png");
        setIconImage(img.getImage());

        initComponents();

        setAddress(ip, port);

        pingChannel.ping();
    }

    private void initComponents(){

        //Move focus to the next item (Avoid first button to be focused)
        setFocusable(true);

        //-------Creating the MenuBar and adding components------\\
        JMenuBar menuBar = new JMenuBar();
        JMenu serverMenu = new JMenu("Server");
        JMenu clientMenu = new JMenu("Client");
        changeAddressMenuItem = new JMenuItem("Change address");
        JMenu selectServerMenu = new JMenu("Select server");
        JMenuItem editServerMenuItem = new JMenuItem("Edit server addresses");
        connectionStatusLabel = new JLabel("Not Connected");
        JMenu UI_themeMenu = new JMenu("Change theme");
        JMenuItem UI_LightThemeMenuItem = new JMenuItem("Light");
        JMenuItem UI_DarkThemeMenuItem = new JMenuItem("Dark");

        ServerAddress s1 = new ServerAddress("dkkp", "dkkp.ddns.net", 7999);
        ServerAddress s2 = new ServerAddress("localhost", "localhost", 8565);
        ServerAddress s3 = new ServerAddress("alex", "89.170.187.156", 8565);
        serverAddresses.add(s1);
        serverAddresses.add(s2);
        serverAddresses.add(s3);

        for(ServerAddress server : serverAddresses){
            JMenuItem m = new JMenuItem(server.name);
            m.setToolTipText(server.name+":"+server.port);
            m.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    setAddress(server.ip, server.port);
                    pingChannel.ping();
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            selectServerMenu.add(m);
        }

        changeAddressMenuItem.addActionListener(this);
        changeAddressMenuItem.setAccelerator(KeyStroke.getKeyStroke(78, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        changeAddressMenuItem.setMnemonic('N');

        connectionStatusLabel.setForeground(Color.GRAY);
        connectionStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        ImageIcon icon = new ImageIcon("img/refresh.png");
        Image img_ = icon.getImage() ;
        Image new_img = img_.getScaledInstance( 14, 14,  java.awt.Image.SCALE_SMOOTH) ;
        icon = new ImageIcon( new_img );
        JButton pingButton = new JButton(icon);

        pingButton.setFocusable(false);
        setButtonBorder(pingButton, false);
        pingButton.addActionListener(e -> {
            setAddress(ip, port);
            pingChannel.ping();
        });
        pingButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setButtonBorder(pingButton, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setButtonBorder(pingButton, false);
            }
        });

        UI_DarkThemeMenuItem.addActionListener(e -> {
            try {
                UIManager.setLookAndFeel( new FlatDarculaLaf());
                SwingUtilities.updateComponentTreeUI(this);
                this.pack();
            } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                unsupportedLookAndFeelException.printStackTrace();
            }
        });
        UI_DarkThemeMenuItem.setAccelerator(KeyStroke.getKeyStroke(81, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        UI_DarkThemeMenuItem.setMnemonic('Q');
        UI_LightThemeMenuItem.addActionListener(e ->{
            try {
                UIManager.setLookAndFeel( new FlatIntelliJLaf());
                SwingUtilities.updateComponentTreeUI(this);
                this.pack();
            } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                unsupportedLookAndFeelException.printStackTrace();
            }
        });
        UI_LightThemeMenuItem.setAccelerator(KeyStroke.getKeyStroke(65, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        UI_LightThemeMenuItem.setMnemonic('A');

        editServerMenuItem.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                EditAddressPopUp editPopUp = new EditAddressPopUp(serverAddresses);
                JOptionPane.showOptionDialog(null , editPopUp, "Edit server addresses", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

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
        tabbedPane.addTab("Upload", new UploadTab());
        tabbedPane.addTab("Download", new DownloadTab());
        tabbedPane.setPreferredSize(this.getSize());
        tabbedPane.addMouseListener(new MouseAdapter() {
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

        setContentPane(tabbedPane);
    }

    private void updateStatusLabel(Status status){
        String address = pingChannel.getIp() + ":" + pingChannel.getPort();
        connectionStatusLabel.setText(status + " : " + address);
        connectionStatusLabel.setForeground(new Color(112, 116, 113));
    }

    public void setButtonBorder(JButton c, Boolean state){
        c.setBorderPainted(state);
        c.setContentAreaFilled(state);
        c.setFocusPainted(state);
        c.setOpaque(state);
    }

    public class ServerAddress{
        String name, ip;

        int port;

        public ServerAddress(String name, String ip, int port){
            this.name = name;
            this.ip = ip;
            this.port = port;
        }
    }

    public static Gui getInstance() {
        return instance;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == changeAddressMenuItem){
            ChangeAddressPopUp p = new ChangeAddressPopUp();
            boolean addressIsNull = p.ip.isEmpty() && p.port == 0;
            if(!addressIsNull && hasAddressChanged(p.ip, p.port)){
                setAddress(p.ip, p.port);
                updateStatusLabel(Status.DISCONNECTED);
                pingChannel.ping();
            }
        }
    }

    public boolean hasAddressChanged(String ip, int port){
        return (!this.ip.equals(ip) && this.port != port);
    }
    public void setAddress(String ip, int port){
        this.ip = ip;
        this.port = port;

        pingChannel = new Channel(ip, port);
        pingChannel.addPingListeners(new PingListener() {
            @Override
            public void onStatusChanged(Status status) {
                updateStatusLabel(status);
            }
        });
        updateStatusLabel(Status.DISCONNECTED);
    }

    private void saveAddress(String name, String ip, int port){
        if(serverAddresses.size() < MAX_SAVED_ADDRESS)
            serverAddresses.add(new ServerAddress(name, ip, port));
    }

    static class ChangeAddressPopUp extends JOptionPane{
        int port = getInstance().port;
        String ip = getInstance().ip;

        public ChangeAddressPopUp(){

            JTextField _ip = new JTextField(ip);
            JTextField _port = new JTextField(Integer.toString(port));
            Object[] message = {
                    "IP: ", _ip,
                    "Port: ", _port
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Change address", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                if(!_ip.getText().isEmpty())
                    ip = _ip.getText();
                if(!_port.getText().isEmpty())
                    port = Integer.parseInt(_port.getText());
            }
        }

    }
    static class EditAddressPopUp extends JPanel{
        ArrayList<ServerAddress> serverAddresses = new ArrayList<>();

        ArrayList<JPanel> editServerPanel = new ArrayList<>();
        ArrayList<JTextField> infoTextField =  new ArrayList<>();

        public EditAddressPopUp(ArrayList<ServerAddress> sa){

            serverAddresses = sa;

            for(int i = 0; i < serverAddresses.size();i++){
                editServerPanel.add(new JPanel());
                int index = (3*i);
                infoTextField.add(new JTextField(serverAddresses.get(i).name, 5));
                infoTextField.add(new JTextField(serverAddresses.get(i).ip,10));
                infoTextField.add(new JTextField(Integer.toString(serverAddresses.get(i).port),5));
                editServerPanel.get(i).add(infoTextField.get(index));
                editServerPanel.get(i).add(infoTextField.get(index + 1));
                editServerPanel.get(i).add(infoTextField.get(index + 2));
                this.add(editServerPanel.get(i));
            }
        }
    }
}