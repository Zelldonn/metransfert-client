package com.metransfert.client.controller.transaction;

import com.metransfer.common.TransactionType;
import com.metransfert.client.gui.common.Status;
import com.metransfert.client.transactionhandlers.PingListener;

import java.io.IOException;
import java.util.ArrayList;

public class Ping extends Transaction {

    public ArrayList<PingListener> pingListeners = new ArrayList<>();

    public void addPingListeners(PingListener newListener){
        pingListeners.add(newListener);
    }

    public Ping() {
    }

    public void setStatus(Status status){
        for(PingListener l : pingListeners){
            l.onStatusChanged(status);
        }
    }

    @Override
    public void run(){
       setStatus(Status.TRYING);
        try {
            bos.write(TransactionType.PING);
            bos.flush();
            byte pong = (byte) bis.read();
            //TODO : will never received answer if server does not answer
            if(pong == TransactionType.PONG){
                setStatus(Status.CONNECTED);
            }else{
                setStatus(Status.DISCONNECTED);
            }
        } catch (IOException e) {
            setStatus(Status.TIMEOUT);
            e.printStackTrace();
        }
    }
}
