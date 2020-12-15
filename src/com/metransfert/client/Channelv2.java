package com.metransfert.client;

import com.metransfert.client.transaction.TransferListener;
import com.packeteer.network.PacketInputStream;
import com.packeteer.network.PacketOutputStream;

import java.net.Socket;
import java.nio.file.Path;

public class Channelv2 implements Runnable {
    final String ip;
    final int port;

    Socket soc = null;
    PacketInputStream pis = null;
    PacketOutputStream pos = null;

    public Channelv2(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void upload(Path file, TransferListener l){

    }

    @Override
    public void run() {

    }
}
