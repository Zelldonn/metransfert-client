package com.metransfert.client.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class Gui {
    /**
     * Converts byte to string with appropriate unit  * @param byte
     * @return human readable string
     */
    static public String byte2Readable(double _byte){
        String s = "";
        float mebibyte = 1_048_576F;

        if(_byte < mebibyte / 1024F)
            s = (int)_byte + " B";
        else if(_byte < mebibyte)
            s = String.format("%.2f",_byte/1024F) + " kB";
        else if(_byte < mebibyte * 1024F){
            s = String.format("%.2f",_byte/mebibyte) + " MB";
        }else if(_byte < mebibyte * mebibyte){
            s = String.format("%.2f",_byte/(mebibyte * 1024F)) + " GB";
        }
        return s;
    }

    static public long calculateThroughput(long transferredBytes, long oldTransferredBytes, long time, long oldTime){
        return  0L;
    }

    static public long calculateTotalSize(File[] fileList) {
        long l = 0L;
        for (File file : fileList) {
            if(!file.isDirectory())
                l += file.length();
            else{
                l += calculateTotalSize(file.listFiles());
            }
        }
        return l;
    }

    static public int calculateTotalFiles(File[] fileList) {
        int size = 0;
        for (File file : fileList) {
            if(!file.isDirectory())
                size++;
            else{
                size += calculateTotalFiles(file.listFiles());
            }
        }
        return size;
    }
    static public String displayFileInfo(File[] fileList){
        int numberOfFile = calculateTotalFiles(fileList);
        long size = calculateTotalSize(fileList);
        return numberOfFile + " file(s) selected : "+ byte2Readable(size);
    }
    static public ArrayList<Path> pathListFromFileList(File[] fileList){
        ArrayList<Path> pathList = new ArrayList<Path>();
        for (File file : fileList) {
          pathList.add(file.toPath());
        }
        return pathList;
    }
}
