package com.metransfert.client.controller.transaction;

import com.metransfert.client.transactionhandlers.TransferListener;

import java.io.*;
import java.util.ArrayList;

abstract public class Transfer extends Transaction {

    public ArrayList<TransferListener> transferListeners = new ArrayList<>();

    public void addTransferListeners(TransferListener newListener){
        transferListeners.add(newListener);
    }

    private static final int BUFFER_SIZE = 16*1024;

    protected byte[] data, buffer;

    protected long expectedBytes;

    public long getTransferredBytes() {
        return transferredBytes;
    }
    public void setTransferredBytes(long bytes){
        transferredBytes = bytes;
    }

    protected long transferredBytes;

    protected int expectedFiles;

    protected boolean success = false;

    public Transfer() {
        this.buffer = new byte[BUFFER_SIZE];

        this.transferredBytes = 0L;
    }

    public int getFileNumber(){
        return expectedFiles;
    }
    public long getExpectedBytes(){
        return expectedBytes;
    }


    public void writeAndFlushTransactionInfo() throws IOException {
        dos.writeInt(expectedFiles);
        dos.writeLong(expectedBytes);
        dos.flush();
    }

}
