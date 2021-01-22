package com.metransfert.client;

import com.metransfer.common.NetworkInputOutput;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class NewInstanceProcessor extends NetworkInputOutput implements Runnable{

    ArrayList<ArgumentsListener> listeners = new ArrayList<>();
    void addArgumentsListener(ArgumentsListener l){
        listeners.add(l);
    }

    public NewInstanceProcessor(Socket soc) throws IOException {
        super(soc);
    }

    @Override
    public void run() {
        try {
            //System.out.println("reding int" );
            int argsNumber = dis.readInt();
            String [] args = new String [argsNumber];
            //System.out.println("Expecting args number" + args.length );
            for(int i = 0; i < argsNumber; i++){
                args[i] = readString();
                //System.out.println("args retrivied : " + args[i]);
            }

            for(ArgumentsListener l : listeners){
                l.onArgsRetrieved(args);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
