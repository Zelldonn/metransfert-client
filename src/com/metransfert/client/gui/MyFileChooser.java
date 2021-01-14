package com.metransfert.client.gui;

import com.metransfert.client.utils.Path;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class MyFileChooser {
    JFileChooser fileChooser;


    File directorySelected;

    public File getSelectedDirectory() {
        return directorySelected;
    }

    public File[] getSelectedFiles() {
        return selectedFiles;
    }

    private File[] selectedFiles;

    public MyFileChooser(){
        selectedFiles = null;
        directorySelected = null;
    }

    public boolean openFileChooser(java.nio.file.Path p, Component c, int selectionMode){
        fileChooser = new JFileChooser();

        if(Path.isValidPath(p)){
            File dir = p.toFile();
            fileChooser.setCurrentDirectory(dir);
            fileChooser.setFileSelectionMode(selectionMode);
            if(selectionMode == JFileChooser.FILES_AND_DIRECTORIES)
                fileChooser.setMultiSelectionEnabled(true);

            int i = 0;

            fileChooser.showDialog(c,"Select");
            if(i == JFileChooser.APPROVE_OPTION){
                selectedFiles = fileChooser.getSelectedFiles();
                boolean oneFileIsSelected = selectedFiles.length == 1;
                boolean isDirectory = oneFileIsSelected && selectedFiles[0].isDirectory();
                if(oneFileIsSelected && isDirectory){
                    directorySelected = selectedFiles[0];
                }else
                    directorySelected = fileChooser.getCurrentDirectory();
                return true;
            }else
                return false;

        }else return false;
    }
}
