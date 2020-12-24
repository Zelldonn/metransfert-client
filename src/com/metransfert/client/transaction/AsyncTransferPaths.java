package com.metransfert.client.transaction;

import com.metransfert.client.transactionhandlers.TransferListener;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class AsyncTransferPaths  extends Thread{

    public ArrayList<TransferListener> transferListeners = new ArrayList<>();

    public void addTransferListeners(TransferListener newListener){
        transferListeners.add(newListener);
    }

    protected InputStream in;
    protected OutputStream out;
    protected DataInputStream dis;
    protected DataOutputStream dos;
    protected boolean finished = false;

    byte[] data;

    protected AsyncTransferPaths(Socket soc) throws IOException {
        in = soc.getInputStream();
        out = soc.getOutputStream();
        dos = new DataOutputStream(out);
        dis = new DataInputStream(in);
    }
}
