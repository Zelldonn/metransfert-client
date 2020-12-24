package com.metransfert.client.transactionhandlers;

import com.packeteer.network.Packet;
import com.packeteer.network.PacketUtils;

import java.nio.ByteBuffer;

public class UploadInfoResult extends TransactionResult{
    public String id;

    public UploadInfoResult(Packet p) {
        super(p);
        ByteBuffer bf = p.getPayloadBuffer();

        id = PacketUtils.readNetworkString(bf);
    }
}
