package com.metransfert.client.transaction;

import com.metransfert.client.PathUtils;
import com.metransfert.client.transactionhandlers.TransferListener;
import com.metransfert.client.transactionhandlers.UploadInfoResult;
import com.metransfert.common.ErrorTypes;
import com.metransfert.common.PacketTypes;
import com.packeteer.network.Packet;
import com.packeteer.network.PacketInputStream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;

public class UploadPaths extends AsyncTransferPaths{
    private final int BLOCK_SIZE = 16*1024;

    private ArrayList<Path> uploadPaths;

    protected FileInputStream fis;

    public UploadPaths(ArrayList<Path> p, Socket soc) throws IOException {
        super(soc);
        if(p == null)	throw new NullPointerException("argument Path cannot be null");

        this.uploadPaths = p;
    }

    private void uploadPaths(ArrayList<Path> pathToUpload){
        for(TransferListener listener : transferListeners){
            listener.onTransactionStart();
        }
        int pathsNumber = pathToUpload.size();
        System.out.println("Sending " + pathsNumber + " paths");
        try {
            dos.writeInt(pathsNumber);
            dos.flush();

            int currentPathIndex = 0;
            for(Path path : pathToUpload){
                data = path.getFileName().toString().getBytes();
                dos.writeLong(data.length);
                dos.flush();
                out.write(data);
                out.flush();

                boolean isPathFile = path.toFile().isFile();
                if(isPathFile){
                    dos.write(PacketTypes.FILE);
                    dos.writeLong(path.toFile().length());
                    dos.flush();//Use full to do this here ?

                    fis = new FileInputStream(path.toFile());

                    int count;
                    byte[] buffer = new byte[BLOCK_SIZE];
                    while((count = fis.read(buffer)) > 0){
                        out.write(buffer, 0, count);
                    }
                    out.flush();
                }else{
                    dos.writeByte(PacketTypes.DIRECTORY);

                    ArrayList<Path> pathsInFolder;
                    pathsInFolder = PathUtils.listPath(path);

                    //Making sure it is a directory ...
                    if(path.toFile().isDirectory()){
                        data = path.getFileName().toString().getBytes();
                        dos.writeLong(data.length);
                        dos.flush();
                        out.write(data);
                        out.flush();

                        uploadPaths(pathsInFolder);
                    }else throw new RuntimeException("Not a directory !");
                }
                for(TransferListener listener : transferListeners){
                    listener.onTransferUpdate(new TransferListener.Info((long)pathsNumber,(long)currentPathIndex, 0L));
                }
                dos.flush();
                dis.readByte();//Wait the server
                currentPathIndex++;
            }

            this.finished = true;

            PacketInputStream pis = new PacketInputStream(new BufferedInputStream(in));
            Packet answer = pis.readPacket();
            //trigger if invalid file name or

            byte answerType = answer.getType();

            //TODO handle errors with flags
            if(answerType == PacketTypes.ERROR){
                byte errorType = answer.getPayloadBuffer().get();
                if(errorType == ErrorTypes.SERVER_ERROR){

                }else if(errorType == ErrorTypes.INVALID_FILENAME){

                }else{
                    //Does not follow standard error procedure in upload result context
                }
            }else if(answerType == PacketTypes.UPLOADRESULT){
                for(TransferListener listener : transferListeners){
                    listener.onTransactionFinish(new UploadInfoResult(answer));
                }
            }else{
                //Does not follow standard procedure at all
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run(){
        uploadPaths(uploadPaths);
        uploadPaths.clear();
    }
}
