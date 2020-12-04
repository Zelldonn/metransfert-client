import com.packeteer.network.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        Gui client_gui =  new Gui();

        /*
        Socket soc = null;
        PacketInputStream pis = null;
        PacketOutputStream pos = null;
        try {
            soc = new Socket("192.168.1.40", 7999);
            pis =  new PacketInputStream(new BufferedInputStream(soc.getInputStream()));
            pos =  new PacketOutputStream(soc.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Packet p = PacketBuilder.newBuilder((byte)0x4).write("tFlYO").build();
        try {
            pos.writeAndFlush(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Packet answer = pis.readPacket();
        PacketUtils.hexDump(answer.getPayload());

        Path root = Paths.get(System.getProperty("user.dir"));
        Path file = root.resolve("server.properties");

        try {
            ServerConfiguration config = ServerConfiguration.loadFromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }
}
