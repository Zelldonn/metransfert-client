package com.metransfert.client.controller.transaction;

import com.metransfer.common.TransactionType;
import com.metransfert.client.transactionhandlers.DownloadListener;

import com.metransfert.client.transactionhandlers.TransferInfo;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Download extends Transfer{

    public ArrayList<DownloadListener> downloadListeners = new ArrayList<>();

    public void addDownloadListeners(DownloadListener newListener){
        downloadListeners.add(newListener);
    }


    FileOutputStream fos;

    Path downloadPath;

    String requestedID;

    public Download(Path p, String id) {
        this.downloadPath = p;
        this.requestedID = id;
    }

    private void receiveFile(Path downloadDirectory) throws IOException {
        int n = dis.readInt();
        //System.out.println("Transferring "+ n +" file(s)");
        for(int i = 0; i < n; i++) {

            long fileNameLength = dis.readLong();

            String s = "";
            int index;
            while(s.length() < fileNameLength){
                index = bis.read();
                if(index == -1)
                    throw new EOFException("Input stream end reached unexpectedly before a string could be read");

                s += (char)index;
            }
            String pathName = s;
            byte pathType = dis.readByte();

            if(pathType == TransactionType.FILE){
                Path p = Paths.get(downloadDirectory.toString() + "/" + pathName);
                //System.out.print("Will create this file : " + p);

                long fileSize ;
                fileSize = dis.readLong();

                //System.out.println( " of size : "+ fileSize);

                try {
                    fos = new FileOutputStream(p.toFile());
                } catch (FileNotFoundException ex) {
                    System.out.println("File not found. ");
                }

                int count = 0;
                long read = 0;
                while (read < fileSize) {
                    count = bis.read(buffer);
                    read += count;
                    transferredBytes += count;
                    fos.write(buffer, 0, count);
                    for(DownloadListener l :downloadListeners)
                        l.onTransferUpdate(new TransferInfo(expectedBytes, transferredBytes, 0L));
                }
            }else if(pathType == TransactionType.DIRECTORY){
                //System.out.println("Directory received");
                long DirectoryNameLength = dis.readLong();

                String dir = "";
                int z;
                while(dir.length() < DirectoryNameLength){
                    z = bis.read();
                    if(z == -1)
                        throw new EOFException("Input stream end reached unexpectedly before a string could be read");

                    dir += (char)z;
                }
                String DirectoryPathName = s;
                //System.out.println("path N° " +(i+1) + " :" + DirectoryPathName );
                Path newFolder = Paths.get(downloadDirectory.toString() +"/"+ DirectoryPathName);

                try {
                    Files.createDirectories(newFolder);
                } catch (IOException e) {
                    System.err.println("Failed to create directory!" + e.getMessage());
                }
                receiveFile(newFolder);
            }
            writeAndFlush(TransactionType.NEXT_FILE);
        }
    }

    @Override
    public void run() {
        try {
            writeAndFlush(TransactionType.DOWNLOAD);

            data = requestedID.getBytes();

            dos.writeLong(data.length);
            dos.flush();

            bos.write(data);
            bos.flush();

            expectedFiles = dis.readInt();
            expectedBytes = dis.readLong();

            //on transaction start
            for(DownloadListener l : downloadListeners){
                l.onTransactionStart();
            }

            receiveFile(downloadPath);

        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        }finally{
            try {
                if(fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(transferredBytes == expectedBytes){
            success = true;
            for(DownloadListener l : downloadListeners){
                l.onTransferFinish();
            }
        } else{
            System.err.println("Not all files have been transferred");
            //May be tell the server ?
        }
    }
}
