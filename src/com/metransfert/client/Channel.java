package com.metransfert.client;

import com.metransfert.client.transaction.RequestInfoResult;
import com.metransfert.client.transaction.TransactionListener;
import com.metransfert.client.transaction.TransferListener;
import com.metransfert.common.PacketTypes;
import com.packeteer.network.Packet;
import com.packeteer.network.PacketBuilder;
import com.packeteer.network.PacketInputStream;
import com.packeteer.network.PacketOutputStream;

import java.io.BufferedInputStream;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;

import java.util.ArrayList;


public class Channel {

    //TODO : make it not hard coded
    String ip = "dkkp.ddns.net";
    int port = 7999;

    Socket soc = null;
    PacketInputStream pis = null;
    PacketOutputStream pos = null;

    boolean inTransaction = false;


    private void connect(){

        try {
            soc = new Socket(ip, port);
            pis =  new PacketInputStream(new BufferedInputStream(soc.getInputStream()));
            pos =  new PacketOutputStream(soc.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ping(){

    }

    public Channel(){

    }

    public void upload(Path file, TransferListener l) {
        //TODO : check if l is not null, want to enable an upload without callback  ?

        connect();
        //TODO : Integrate Async code here
        AsyncUpload au = new AsyncUpload(pis, pos, file);

        au.addTransferListeners(l);

        au.start();
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

    //--------------Getters & Setters----------------\\
    public void setAddress(String ip, int port){
        setIp(ip);
        setPort(port);
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        if(ip == null)
            throw new IllegalArgumentException("IP address cannot be null");
        this.ip = ip;
    }

    public void setPort(int port) {
        if(port > 0 && port < 65536)
            this.port = port;
        else throw new IllegalArgumentException("Port number must in the range [1 - 65535]");
    }
}
