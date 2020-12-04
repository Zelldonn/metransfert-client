import com.metransfert.common.AsyncUpload;
import com.metransfert.common.FileTransfert;
import com.metransfert.common.MeTransfertPacketTypes;
import com.packeteer.network.Packet;
import com.packeteer.network.PacketBuilder;
import com.packeteer.network.PacketInputStream;
import com.packeteer.network.PacketOutputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;

public class Client{

    private ArrayList<StatusChangeListener> statusChangeListeners =  new ArrayList<>();
    private ArrayList<TransactionFinishListener> requestInfoFinishListeners =  new ArrayList<>();

    public void addStatusChangeListeners(StatusChangeListener newListener){
        statusChangeListeners.add(newListener);
    }
    public void addRequestInfoFinishListeners(TransactionFinishListener newListener){
        requestInfoFinishListeners.add(newListener);
    }

    public Boolean status = false;
    public String address = "192.168.1.40";
    public int port = 7999;

    Socket soc = null;
    PacketInputStream pis = null;
    PacketOutputStream pos = null;

    public void connect(){
        for (StatusChangeListener listener : statusChangeListeners) {
            listener.onStatusChange(false);
        }
        try {

            soc = new Socket(address, port);
            pis =  new PacketInputStream(new BufferedInputStream(soc.getInputStream()));
            pos =  new PacketOutputStream(soc.getOutputStream());
            for (StatusChangeListener listener : statusChangeListeners) {
                listener.onStatusChange(true);
            }
        } catch (IOException e) {
            for (StatusChangeListener listener : statusChangeListeners) {
                //TODO:Connexion timeout
                listener.onStatusChange(false);
            }
            e.printStackTrace();
        }
    }

    public Client(){

    }


    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Boolean isConnected() {
        return status;
    }

    public void upload(Path file){
        FileTransfert f = new FileTransfert(pis, pos);
        try {
            f.upload(new File(file.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestInfo(String ID){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Packet p = PacketBuilder.newBuilder(MeTransfertPacketTypes.REQINFO).write(ID).build();
                    pos.writeAndFlush(p);

                    Packet answer = pis.readPacket();

                    for (TransactionFinishListener t: requestInfoFinishListeners) {
                        t.onFinish(answer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
