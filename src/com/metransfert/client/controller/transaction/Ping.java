package com.metransfert.client.controller.transaction;

import com.metransfert.client.gui.Status;
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

    @Override
    public void run(){
       for(PingListener l : pingListeners){
            l.onStatusChanged(Status.TRYING);
        }
        try {
            bos.write(0X1);
            bos.flush();
            byte pong = (byte) bis.read();
            if(pong == 0X1){
                for(PingListener l : pingListeners){
                    l.onStatusChanged(Status.CONNECTED);
                }
            }else{
                for(PingListener l : pingListeners){
                    l.onStatusChanged(Status.DISCONNECTED);
                }
            }
        } catch (IOException e) {
            for(PingListener l : pingListeners){
                l.onStatusChanged(Status.TIMEOUT);
            }
            e.printStackTrace();
        }
    }
}
