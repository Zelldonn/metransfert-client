package com.metransfert.client;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.metransfert.client.gui.Gui;

import javax.swing.*;
import java.awt.*;
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

        Gui gui = new Gui();

        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
        gui.pack();

    }
}
