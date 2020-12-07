package com.metransfert.client.transaction;

import com.packeteer.network.Packet;
import com.packeteer.network.PacketUtils;

import java.nio.ByteBuffer;

public class UploadInfoResult extends TransactionResult{
    //TODO: Incoming error type not yet implemented by Alexander
    public byte result;
    public String id;

    public UploadInfoResult(Packet p) {
        super(p);
        ByteBuffer bf = p.getPayloadBuffer();

        result = bf.get();
        id = PacketUtils.readNetworkString(bf);
    }
}
