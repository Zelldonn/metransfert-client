package com.metransfert.client;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.metransfert.client.controller.ClientConfiguration;
import com.metransfert.client.controller.ClientController;
import com.metransfert.client.gui.Gui;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {

        ClientConfiguration config = null;

        Path userDir = Paths.get(System.getProperty("user.dir"));

        try {
            config = ClientConfiguration.loadFromFile(userDir.resolve("client.properties"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not load configuration from file. Creating file...");
            createDefaultConfig();
            try {
                config = ClientConfiguration.loadFromFile(userDir.resolve("client.properties"));
            } catch (FileNotFoundException fileNotFoundException) {
                System.err.println("Cannot create file...EXITING");
                System.exit(1);
            }
        }

        try {
            if(config.getTheme().equals("DARK"))
                UIManager.setLookAndFeel( new FlatDarculaLaf());
            else
                UIManager.setLookAndFeel( new FlatIntelliJLaf());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        UIManager.put( "TextComponent.arc", 999 );
        UIManager.put( "Button.arc", 999 );
        UIManager.put( "TabbedPane.showTabSeparators", true );
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        Gui gui = new Gui();

        ClientController clientController = new ClientController(gui, config, args);
        //clientController.populateDefaultAddresses();
        clientController.initGUI();
        clientController.initUploadTab();
        clientController.initDownloadTab();
        clientController.refreshGUI();
    }

    private static void createDefaultConfig() {
        try {
            File myObj = new File("client.properties");
            if (myObj.createNewFile()) {
                System.out.println("Default client.properties file created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter("client.properties");
            myWriter.write("ADDRESS=\n"+
                    "THEME=\n"+
                    "UPLOAD_PATH=\n"+
                    "DOWNLOAD_PATH=\n");
            myWriter.close();
            System.out.println("Successfully wrote default properties.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to client.properties.");
            e.printStackTrace();
        }
    }
}
