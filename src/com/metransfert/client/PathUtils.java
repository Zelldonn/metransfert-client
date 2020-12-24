package com.metransfert.client;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class PathUtils {

    /**
     * This function return all paths which are or contain at least one file in a given path
     * If the given path is a file, then it return itself
     * @param p
     * @return
     */
    public static ArrayList<Path> listPath(Path p){
        ArrayList<Path> path = new ArrayList<>();
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(p);
            for (Path pa : directoryStream) {
                boolean emptyDirectory = directoryStream.iterator().hasNext();
                if(!emptyDirectory)
                    path.add(pa);
            }
        } catch (IOException ex) {
        }
        if(p.toFile().isFile())
            path.add(p);
        return path;
    }
}
