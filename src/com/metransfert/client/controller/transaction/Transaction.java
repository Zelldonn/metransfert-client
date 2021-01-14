package com.metransfert.client.controller.transaction;
import com.metransfert.client.controller.exception.ConnectionFailedException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

abstract public class Transaction implements Runnable {

    Socket soc = null;

    protected BufferedInputStream bis;

    protected BufferedOutputStream bos;

    public void setSocket(Socket s) throws ConnectionFailedException {
        this.soc = s;
        if (soc == null)
            System.out.println("Aberrant : transaction's socket is null");
        try {
            this.bis = new BufferedInputStream(soc.getInputStream());

            this.bos = new BufferedOutputStream(soc.getOutputStream());
        } catch (IOException e) {
            throw new ConnectionFailedException(e);
        }
    }

    public void writeAndFlush(byte b) throws IOException {
        bos.write(b);
        bos.flush();
    }
}
