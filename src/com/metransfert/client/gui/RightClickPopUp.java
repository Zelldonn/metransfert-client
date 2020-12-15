package com.metransfert.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class RightClickPopUp extends JPopupMenu {
    JMenuItem anItem;

    private String clipBoardContent;
    public RightClickPopUp() {
        System.out.println("test");
        anItem = new JMenuItem("Paste");
        anItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(e.getButton() == MouseEvent.BUTTON1)
                    clipBoardContent = getClipBoard();
                //TODO : fix paste error

            }
        });
        add(anItem);
    }
    public static String getClipBoard(){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String s = "";
        try {
            s =  clipboard.getData(DataFlavor.stringFlavor).toString();
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
