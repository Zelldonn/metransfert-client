package com.metransfert.client.gui;

public class GuiUtils {
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
}
