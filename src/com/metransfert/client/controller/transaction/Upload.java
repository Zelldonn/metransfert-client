package com.metransfert.client.controller.transaction;

import com.metransfer.common.TransactionType;
import com.metransfer.common.Ui;
import com.metransfert.client.transactionhandlers.TransferInfo;
import com.metransfert.client.transactionhandlers.TransferListener;

import java.io.*;

public class Upload extends Transfer{

    FileInputStream fis;

    private boolean isRunning = true;

    protected final File[] fileList;

    public void changeRunningState(){
        isRunning = !isRunning;
        System.out.println(isRunning);
    }

    public Upload(File[] fileList) {
        this.fileList = fileList;
        this.expectedFiles = Ui.calculateExpectedFiles(fileList);
        this.expectedBytes = Ui.calculateExpectedBytes(fileList);
    }

    public void sendFile(File[] fileList) throws IOException {
        int fileNumber = fileList.length;
        //System.out.println("Will send "+ fileList.length +"file");
        dos.writeInt(fileNumber);
        dos.flush();

        for(File file : fileList){
            data = file.getName().getBytes();
            dos.writeLong(data.length);
            dos.flush();
            bos.write(data);
            bos.flush();

            if(file.isFile()){
                //System.out.print(" and it is a file of size " + file.length());
                dos.write(TransactionType.FILE);
                dos.writeLong(file.length());
                dos.flush();

                try{
                    fis = new FileInputStream(file);
                }catch(IOException e){throw new IOException("File cannot be read"); }

                //System.out.print(" named: " + file.toString());

                int count;
                while((count = fis.read(buffer)) > 0){
                    bos.write(buffer, 0, count);
                    setTransferredBytes(getTransferredBytes()+count);
                    for(TransferListener listener : transferListeners){
                        listener.onTransferUpdate(new TransferInfo(getExpectedBytes(), getTransferredBytes(), 0L));
                    }
                }
                bos.flush();
            }else{
                //System.out.print(" and it is a directory");
                dos.writeByte(TransactionType.DIRECTORY);

                //Not sure this is working
                File[] filesInFolder = file.listFiles();

                if(file.isDirectory()){
                    data = file.toString().getBytes();
                    dos.writeLong(data.length);
                    dos.flush();
                    bos.write(data);
                    bos.flush();

                    sendFile(filesInFolder);
                }
            }
            byte next = dis.readByte();
            if(next != TransactionType.NEXT_FILE){
                System.out.println("Server has not sended NEXTFILE ACK");
            }else {
                //System.out.println("Server received my file");
            }
        }
    }

    @Override
    public void run(){
        try {
            //Sending header to notify multiple files upload
            writeAndFlush(TransactionType.UPLOAD);

            //SENDING information about the incoming files
            writeAndFlushTransactionInfo();

            //Doing upload
            for(TransferListener listener : transferListeners){
                listener.onTransactionStart();
            }
            sendFile(fileList);

            //Reading answer
            long fileNameLength = dis.readLong();

            String s = "";
            int index;
            while(s.length() < fileNameLength){
                index = bis.read();
                if(index == -1)
                    throw new EOFException("Input stream end reached unexpectedly before a string could be read");

                s += (char)index;
            }
            String id = s;

            for(TransferListener listener : transferListeners){
                listener.onTransactionFinish(id);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean getRunningState() {
        return isRunning;
    }
}
