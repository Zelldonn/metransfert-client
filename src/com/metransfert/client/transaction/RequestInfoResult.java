package com.metransfert.client.transaction;

import com.metransfert.common.ErrorTypes;
import com.metransfert.common.PacketTypes;
import com.packeteer.network.Packet;
import com.packeteer.network.PacketUtils;

import java.nio.ByteBuffer;

public class RequestInfoResult extends TransactionResult {
    public boolean id_exists = true;
    public String fileName;
    public int fileSize;

    public RequestInfoResult(Packet p) {
        super(p);
        ByteBuffer bf = p.getPayloadBuffer();

        //Check header type
        byte type = p.getType();
        System.out.println(type);
        if(type == PacketTypes.ERROR){
            byte errorType = bf.get();
            if(errorType == ErrorTypes.FILE_DOES_NOT_EXIST){
                id_exists = false;
            }
        }else{
            fileSize = bf.getInt();
            fileName = PacketUtils.readNetworkString(bf);
        }
    }
}
