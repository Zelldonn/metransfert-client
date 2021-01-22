package com.metransfert.client;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.metransfer.common.NetworkInputOutput;
import com.metransfert.client.controller.ClientConfiguration;
import com.metransfert.client.controller.ClientController;
import com.metransfert.client.gui.Gui;

import javax.swing.*;
import java.io.*;
import java.net.BindException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    ArrayList<InstanceListener> listeners = new ArrayList<>();
    public void addInstanceListener(InstanceListener l){
        listeners.add(l);
    }
    private static Main instance;
    public static Main instance(){
        return instance;
    }

    public static void main(String[] args) throws IOException {

        try{
            ServerInstance serverInstance = new ServerInstance();
            serverInstance.addServerInstanceListener(new InstanceListener() {
                @Override
                public void onNewInstance(String[] args) {
                    System.out.println("New instance triggered");
                    for(String a : args)
                        System.out.println(a);
                }
            });
            Thread t = new Thread(serverInstance);
            t.start();
        }catch(BindException e){
            if(args.length > 0) {
                Socket soc = new Socket("localhost", 43210);

                NetworkInputOutput network = new NetworkInputOutput(soc);
                network.sendInt(args.length);
                for (String s : args) {
                    System.out.println("Sending this arg: "+s);
                    network.sendString(s);
                }
            }
            System.out.println("second instance exited");
            System.exit(1);
        }

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
            myWriter.write("ADDRESS=Official;metransfer.ddns.net\\:7999\n"+
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
