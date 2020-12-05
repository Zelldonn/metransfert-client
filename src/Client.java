import com.metransfert.common.MeTransfertPacketTypes;
import com.packeteer.network.Packet;
import com.packeteer.network.PacketBuilder;
import com.packeteer.network.PacketInputStream;
import com.packeteer.network.PacketOutputStream;

import java.io.BufferedInputStream;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;

import java.util.ArrayList;


public class Client {

    private ArrayList<StatusChangeListener> statusChangeListeners =  new ArrayList<>();

    public void addStatusChangeListeners(StatusChangeListener newListener) {
        statusChangeListeners.add(newListener);
    }

    //TODO : make it not hard coded
    String ip = "dkkp.ddns.net";
    int port = 7999;

    Socket soc = null;
    PacketInputStream pis = null;
    PacketOutputStream pos = null;

    boolean inTransaction = false;

    //TODO make it private
    public void connect(){
        for (StatusChangeListener listener : statusChangeListeners) {
            listener.onStatusChange(Status.TRYING);
        }
        try {
            soc = new Socket(ip, port);
            pis =  new PacketInputStream(new BufferedInputStream(soc.getInputStream()));
            pos =  new PacketOutputStream(soc.getOutputStream());
            for (StatusChangeListener listener : statusChangeListeners) {
                listener.onStatusChange(Status.CONNECTED);
            }
        } catch (IOException e) {
            for (StatusChangeListener listener : statusChangeListeners) {
                listener.onStatusChange(Status.TIMEOUT);
            }
            e.printStackTrace();
        }
    }

    public Client(){

    }
    //TODO : Integrate Async code here
    public void upload(Path file, TransferListener l) throws InterruptedException {
        //TODO : check if l is not null, want to enable an upload without callback  ?
        AsyncUpload au = new AsyncUpload(pis,pos, file);

        au.addTransferUpdateListeners(l);

        au.start();
    }

    public void requestInfo(String ID, TransactionListener l){
        //TODO : check if l is not null, want to enable an upload without callback  ?
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    l.onTransactionStart();
                    Packet p = PacketBuilder.newBuilder(MeTransfertPacketTypes.REQINFO).write(ID).build();
                    pos.writeAndFlush(p);

                    Packet answer = pis.readPacket();
                    l.onTransactionFinish(new TransactionListener.TransactionResult(answer));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    //--------------Getters & Setters----------------\\
    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
