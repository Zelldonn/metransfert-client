package com.metransfert.client.controller.transaction;

import com.metransfert.client.transactionhandlers.TransferListener;


import java.io.*;

public class Upload extends Transfer{

    private FileInputStream fis;

    private boolean isRunning = true;

    public void changeRunningState(){
        isRunning = !isRunning;
        System.out.println(isRunning);
    }

    public Upload(File[] fileList) {
        super(fileList);
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
                dos.write(0x1);
                dos.writeLong(file.length());
                dos.flush();

                fis = new FileInputStream(file);
                //System.out.print(" named: " + file.toString());

                int count;
                while((count = fis.read(buffer)) > 0){
                    bos.write(buffer, 0, count);
                    setTransferredBytes(getTransferredBytes()+count);
                    for(TransferListener listener : transferListeners){
                        listener.onTransferUpdate(new TransferListener.Info(getExpectedBytes(), getTransferredBytes(), 0L));
                    }
                }
                bos.flush();
            }else{
                //System.out.print(" and it is a directory");
                dos.writeByte(0x1);

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
            if(next != 0x1){
                //System.out.println("Server has not sended NEXTFILE ACK");
            }else if(next == 0x1){
                //System.out.println("Server received my file");
            }
        }
    }

    @Override
    public void run(){
        try {
            //Sending header to notify multiple files upload
            writeAndFlush((byte) 0x1);

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

    /*private boolean answerIsValidate(Packet answer) throws IOException {


        //trigger if invalid file name or
        //TODO handle errors with flags
        byte answerType = answer.getType();
        if(answerType == PacketTypes.ERROR){
            byte errorType = answer.getPayloadBuffer().get();
            if(errorType == ErrorTypes.SERVER_ERROR){

            }else if(errorType == ErrorTypes.INVALID_FILENAME){

            }else{
                //Does not follow standard error procedure in upload result context
            }
        }else if(answerType == PacketTypes.UPLOADRESULT){
            return true;
        }else{
            //Does not follow standard procedure at all
        }
        return false;
    }*/

    public boolean getRunningState() {
        return isRunning;
    }
}
