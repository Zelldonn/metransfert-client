package com.metransfert.client.controller.transaction;
import com.metransfert.client.controller.exception.ConnectionFailedException;

import java.io.*;
import java.net.Socket;

abstract public class Transaction implements Runnable {

    Socket soc = null;

    protected BufferedInputStream bis;

    protected BufferedOutputStream bos;

    protected DataInputStream dis;

    protected DataOutputStream dos;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    protected boolean isRunning = false;


    public void setSocket(Socket s) throws ConnectionFailedException {
        this.soc = s;
        if (soc == null)
            System.out.println("Aberrant : transaction's socket is null");
        try {
            this.bis = new BufferedInputStream(soc.getInputStream());

            this.bos = new BufferedOutputStream(soc.getOutputStream());

            this.dis = new DataInputStream(bis);

            this.dos = new DataOutputStream(bos);

            this.isRunning = true;
        } catch (IOException e) {
            throw new ConnectionFailedException(e);
        }
    }

    public void endTransaction() throws IOException {
        this.soc.close();
    }

    public void writeAndFlush(byte b) throws IOException {
        bos.write(b);
        bos.flush();
    }
}
