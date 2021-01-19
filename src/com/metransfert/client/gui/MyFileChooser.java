package com.metransfert.client.gui;

import com.metransfert.client.utils.Path;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MyFileChooser {

    JFileChooser fileChooser;

    java.nio.file.Path selectedDirectory;


    public java.nio.file.Path getCurrentDir() {
        return currentDirectory;
    }

    java.nio.file.Path currentDirectory;

    private File[] selectedFiles;

    public java.nio.file.Path getSelectedDirectory() {
        return selectedDirectory;
    }

    public File[] getSelectedFiles() {
        return selectedFiles;
    }

    public MyFileChooser(){
        selectedFiles = null;
        selectedDirectory = null;
    }

    public boolean openFileChooser(java.nio.file.Path p, Component c, int selectionMode){
        fileChooser = new JFileChooser();

        if(!Path.isValidPath(p))
            return false;

        File dir = p.toFile();
        fileChooser.setCurrentDirectory(dir);
        fileChooser.setFileSelectionMode(selectionMode);
        if(selectionMode == JFileChooser.FILES_AND_DIRECTORIES)
            fileChooser.setMultiSelectionEnabled(true);

        int i = fileChooser.showDialog(c,"Select");

        //TODO : if selected directory and current is equal then something wrong append
        if(i == JFileChooser.APPROVE_OPTION){
            currentDirectory = fileChooser.getCurrentDirectory().toPath();

            if(selectionMode == JFileChooser.FILES_AND_DIRECTORIES){
                selectedFiles = fileChooser.getSelectedFiles();
                boolean oneFileIsSelected = selectedFiles.length == 1;
                boolean isDirectory = oneFileIsSelected && selectedFiles[0].isDirectory();
                if(oneFileIsSelected && isDirectory){
                    selectedDirectory = selectedFiles[0].toPath();
                }else
                    selectedDirectory = fileChooser.getCurrentDirectory().toPath();
            }else{
                selectedDirectory = fileChooser.getSelectedFile().toPath();
            }
            return true;
        }

        return false;
    }
}
