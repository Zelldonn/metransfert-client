package com.metransfert.client;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.metransfert.client.controller.Address;
import com.metransfert.client.controller.ClientController;
import com.metransfert.client.gui.GUI;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        String home = System.getProperty("user.home");
        Path uploadPath = Paths.get(home + "/Documents/");
        Path downloadPath = Paths.get(home + "/Downloads/");

        ClientController clientController = new ClientController(gui, new Address(null, "localhost", 8888), uploadPath, downloadPath);
        clientController.populateDefaultAddresses();
        clientController.initGUI();
        clientController.updateGUI();
        clientController.initMainChannel();
    }
}
