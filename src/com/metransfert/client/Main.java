package com.metransfert.client;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.metransfert.client.controller.ClientController;
import com.metransfert.client.gui.GUI;

import javax.swing.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel( new FlatIntelliJLaf());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        UIManager.put( "TextComponent.arc", 999 );
        UIManager.put( "Button.arc", 999 );
        UIManager.put( "TabbedPane.showTabSeparators", true );
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        GUI gui = new GUI();

        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
        gui.pack();

        Channel c = new Channel("metransfer.ddns.net", 7999);

        ClientController clientController = new ClientController(gui, c);
        clientController.populateDefaultAddresses();
    }
}
