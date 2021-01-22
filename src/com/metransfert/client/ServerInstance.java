package com.metransfert.client;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerInstance implements Runnable{

    private static ServerInstance instance;
    public static ServerInstance instance(){
        return instance;
    }

    ArrayList<InstanceListener> listeners = new ArrayList<>();

    public void addServerInstanceListener(InstanceListener l){
        listeners.add(l);
    }

    private final ServerSocket serverSocket;

    public ServerInstance() throws IOException {
        this.serverSocket = new ServerSocket(43210);
        instance = this;
    }

    @Override
    public void run() {
        while(true){
            try {
                final Socket s = this.serverSocket.accept();
                System.out.println("new instance detected");
                NewInstanceProcessor p = new NewInstanceProcessor(s);

                p.addArgumentsListener(new ArgumentsListener() {
                    @Override
                    public void onArgsRetrieved(String[] args) {
                        for(InstanceListener l : listeners){
                            l.onNewInstance(args);
                        }
                    }
                });
                Thread t = new Thread(p);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
