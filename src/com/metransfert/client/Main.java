package com.metransfert.client;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.metransfert.client.gui.GUIv2;
import com.metransfert.client.gui.Gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        try {
            UIManager.setLookAndFeel( new FlatDarculaLaf() );
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        Gui test = new Gui();
        test.setLocationRelativeTo(null);
        test.setVisible(true);

        /*GUIv2 client_gui =  new GUIv2();
        client_gui.setPreferredSize(new Dimension(375, 250));
        client_gui.setMinimumSize(client_gui.getSize());
        client_gui.pack();
        client_gui.setLocationRelativeTo(null);
        client_gui.setVisible(true);*/
    }
}
