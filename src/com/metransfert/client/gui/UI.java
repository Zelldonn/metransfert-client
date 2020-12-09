package com.metransfert.client.gui;

import javax.swing.*;
import java.awt.*;

public class UI {
    private JTabbedPane tab = new JTabbedPane();
    public JPanel UIpanel;

    public UI(){
        tab.addTab("Dowload",null, new DownloadPanel());

        JMenuBar m = new JMenuBar();
        UIpanel.add(tab);
        JMenuBar mb = new JMenuBar();
        JLabel label_address = new JLabel("Not Connected");
        label_address.setForeground(Color.GRAY);
        JMenu m1 = new JMenu("Server");
        JMenu m2 = new JMenu("Client");


        mb.add(m1);
        mb.add(m2);

        mb.add(label_address);
        UIpanel.add(mb);
    }
}
