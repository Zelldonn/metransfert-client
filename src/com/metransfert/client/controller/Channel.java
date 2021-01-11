package com.metransfert.client.controller;

import com.metransfert.client.controller.exception.ConnectionFailedException;
import com.metransfert.client.controller.transaction.Transaction;

import java.io.IOException;
import java.net.Socket;

public class Channel {
    private Address address;

    private Socket soc;

    private Transaction transaction;

    public Channel(Address address){
        this.address = address;
    }

    public boolean startTransaction(Transaction t) throws ConnectionFailedException {
        if(transaction != null){
            //Already a transaction a this moment
            return false;
        }
        transaction = t;
        try {
            connect();
        } catch (IOException e) {
            throw new ConnectionFailedException(e);
        }
        //TODO : set transaction to null when "job" is finished, implements listeners ...
        transaction.start();
        return true;
    }

    private void connect() throws IOException, ConnectionFailedException {
        soc =  new Socket(address.getIp(), address.getPort());
        transaction.setSocket(soc);
    }
}
