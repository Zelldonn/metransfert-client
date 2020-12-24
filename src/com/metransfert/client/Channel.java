package com.metransfert.client;

import com.metransfert.client.transaction.AsyncDownload;
import com.metransfert.client.transaction.AsyncUpload;
import com.metransfert.client.transaction.UploadPaths;
import com.metransfert.client.transactionhandlers.*;
import com.metransfert.common.PacketTypes;
import com.packeteer.network.*;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;

public class Channel {

    public ArrayList<PingListener> pingListeners = new ArrayList<>();

    public void addPingListeners(PingListener newListener){
        pingListeners.add(newListener);
    }

    final String ip;
    final int port ;

    Socket soc = null;
    PacketInputStream pis = null;
    PacketOutputStream pos = null;

    public Channel(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    private void connect(){
        try {
            soc = new Socket(ip, port);
            pis =  new PacketInputStream(new BufferedInputStream(soc.getInputStream()));
            pos =  new PacketOutputStream(new BufferedOutputStream(soc.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Cannot create new socket");
        }
    }

    public void ping(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for(PingListener l : pingListeners){
                    l.onStatusChanged(Status.TRYING);
                }
                connect();
                if(pos == null){
                    System.out.println("Ping is not possible : Packet output stream is null");
                    for(PingListener l : pingListeners){
                        l.onStatusChanged(Status.DISCONNECTED);
                    }
                }
                else{
                    try {
                        Packet p = PacketBuilder.newBuilder(PacketTypes.PING).build();
                        pos.writeAndFlush(p);
                        Packet pong = pis.readPacket();
                        if(pong.getType() == PacketTypes.PONG){
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
        });
        t.start();
    }

    public void upload(Path file, TransferListener l) {
        //TODO : check if l is not null, want to enable an upload without callback  ?
        connect();
        //TODO : Integrate Async code here
        AsyncUpload au = new AsyncUpload(pis, pos, file);

        au.addTransferListeners(l);

        au.start();
    }

    public void pathUpload(ArrayList path, TransferListener l) throws IOException {
        connect();
        Packet p = PacketBuilder.newBuilder(PacketTypes.PATHUPLOAD).build();
        pos.writeAndFlush(p);

        UploadPaths up = new UploadPaths(path, soc);

        up.addTransferListeners(l);

        up.start();
    }

    public void download(String ID, Path downloadLocation, TransferListener l) throws IOException {
        connect();

        Packet p = PacketBuilder.newBuilder(PacketTypes.REQFILE).write(ID).build();
        pos.writeAndFlush(p);

        AsyncDownload ad = new AsyncDownload(pis, pos, downloadLocation);

        ad.addTransferListeners(l);

        ad.start();
    }

    public void requestInfo(String ID, TransactionListener l){
        //TODO : check if l is not null, want to enable an upload without callback  ?
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
                try {
                    l.onTransactionStart();
                    Packet p = PacketBuilder.newBuilder(PacketTypes.REQINFO).write(ID).build();
                    pos.writeAndFlush(p);

                    //TODO  : Check answer's integrity/validity
                    Packet answer = pis.readPacket();
                    l.onTransactionFinish(new RequestInfoResult(answer));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
