package com.metransfert.client.controller;

import com.metransfert.client.controller.exception.ConnectionFailedException;
import com.metransfert.client.controller.transaction.Transaction;
import com.metransfert.client.gui.Status;
import com.metransfert.client.transactionhandlers.PingListener;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Channel implements Runnable{

    private Address address;

    private Socket soc;

    private Transaction transaction;

    public Channel(Address address){
        this.address = address;
    }

    private boolean isConnected = true;

    //TODO : this method is not asynchronous, make it async
    public Status startTransaction(Transaction t) throws ConnectionFailedException {
        if(transaction != null){
            //Already a transaction a this moment
            return Status.ALREADY_CONNECTED;
        }
        transaction = t;
        try {
            connect();
            Thread t1 = new Thread(() -> {
                Thread thread = new Thread(transaction);
                thread.start();
            });
            t1.start();
        } catch (IOException e) {
            System.err.println("Cannot connect to : "+address.getIp()+":"+address.getPort());
            return Status.DISCONNECTED;
        }


        //TODO : set transaction to null when "job" is finished, implements listeners ...
        return Status.CONNECTED;
    }

    private void connect() throws IOException, ConnectionFailedException {
        soc =  new Socket(address.getIp(), address.getPort());
        transaction.setSocket(soc);
    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void run() {

    }
}
