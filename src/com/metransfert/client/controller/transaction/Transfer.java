package com.metransfert.client.controller.transaction;

import com.metransfert.client.controller.exception.ConnectionFailedException;
import com.metransfert.client.transactionhandlers.TransferListener;
import com.metransfert.client.utils.Gui;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

abstract public class Transfer extends Transaction {

    public ArrayList<TransferListener> transferListeners = new ArrayList<>();

    public void addTransferListeners(TransferListener newListener){
        transferListeners.add(newListener);
    }

    private static final int BUFFER_SIZE = 16*1024;

    protected DataInputStream dis;

    protected DataOutputStream dos;

    protected byte[] data, buffer;

    protected final File[] fileList;

    protected long expectedBytes;

    public long getTransferredBytes() {
        return transferredBytes;
    }
    public void setTransferredBytes(long bytes){
        transferredBytes = bytes;
    }

    protected long transferredBytes;

    protected int numberOfFile;

    protected boolean finished = false;

    public Transfer(File[] fileList) {
        this.fileList = fileList;

        buffer = new byte[BUFFER_SIZE];

        this.numberOfFile = Gui.calculateTotalFiles(fileList);
        this.expectedBytes = Gui.calculateTotalSize(fileList);
        transferredBytes = 0L;
    }

    public int getFileNumber(){
        return numberOfFile;
    }
    public long getExpectedBytes(){
        return expectedBytes;
    }

    @Override
    public void setSocket(Socket s) throws ConnectionFailedException {
        super.setSocket(s);
        dis = new DataInputStream(bis);
        dos = new DataOutputStream(bos);
    }

    public void writeAndFlushTransactionInfo() throws IOException {
        dos.writeInt(numberOfFile);
        dos.writeLong(expectedBytes);
        dos.flush();
    }

}
