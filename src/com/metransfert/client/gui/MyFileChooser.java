package com.metransfert.client.gui;

import com.metransfert.client.utils.PathUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MyFileChooser {
    JFileChooser fileChooser;

    public File getDirectorySelected() {
        return directorySelected;
    }

    File directorySelected;

    public File[] getSelectedFiles() {
        return selectedFiles;
    }

    private File[] selectedFiles;

    public MyFileChooser(){
        selectedFiles = null;
        directorySelected = null;
    }

    public boolean openFileChooser(Path p, Component c, int selectionMode){
        fileChooser = new JFileChooser();

        if(PathUtils.isValidPath(p)){
            File dir = p.toFile();
            fileChooser.setCurrentDirectory(dir);
            fileChooser.setFileSelectionMode(selectionMode);
            if(selectionMode == JFileChooser.FILES_AND_DIRECTORIES)
                fileChooser.setMultiSelectionEnabled(true);

            int i = 0;
            fileChooser.showDialog(c,"Select");
            if(i == JFileChooser.APPROVE_OPTION){
                selectedFiles = fileChooser.getSelectedFiles();
                directorySelected = fileChooser.getCurrentDirectory();
                return true;
            }else
                return false;

        }else return false;
    }
}
